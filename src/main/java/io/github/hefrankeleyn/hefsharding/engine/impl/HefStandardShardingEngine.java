package io.github.hefrankeleyn.hefsharding.engine.impl;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import io.github.hefrankeleyn.hefsharding.config.HefShardingProperties;
import io.github.hefrankeleyn.hefsharding.core.HefShardingResult;
import io.github.hefrankeleyn.hefsharding.demo.model.User;
import io.github.hefrankeleyn.hefsharding.engine.HefShardingEngine;
import io.github.hefrankeleyn.hefsharding.strategy.HefShardingStrategy;
import io.github.hefrankeleyn.hefsharding.strategy.impl.HefHashShardingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;

/**
 * @Date 2024/8/20
 * @Author lifei
 */
public class HefStandardShardingEngine implements HefShardingEngine {

    private static final Logger log = LoggerFactory.getLogger(HefStandardShardingEngine.class);

    // key: database， value: table
    private final MultiValueMap<String, String> actualDatabaseNames = new LinkedMultiValueMap<>();
    // key: table, value: database
    private final MultiValueMap<String, String> actualTableNames = new LinkedMultiValueMap<>();
    private final Map<String, HefShardingStrategy> databaseStrategies = Maps.newHashMap();
    private final Map<String, HefShardingStrategy> tableStrategies = Maps.newHashMap();

    public HefStandardShardingEngine(HefShardingProperties hefShardingProperties) {

        for (Map.Entry<String, HefShardingProperties.TableProperties> tableEntry : hefShardingProperties.getTables().entrySet()) {
            // 真实表名和库名的映射关系
            for (String actualDataNode : tableEntry.getValue().getActualDataNodes()) {
                String[] sp = actualDataNode.split("\\.");
                String dbName = sp[0], tableName = sp[1];
                actualDatabaseNames.add(dbName, tableName);
                actualTableNames.add(tableName, dbName);
            }
            // 存储分库分表的策略
            databaseStrategies.put(tableEntry.getKey(), new HefHashShardingStrategy(tableEntry.getValue().getDatabaseStrategy()));
            tableStrategies.put(tableEntry.getKey(), new HefHashShardingStrategy(tableEntry.getValue().getTableStrategy()));
        }

    }

    @Override
    public HefShardingResult sharding(String originSql, Object[] args) {

        SQLStatement sqlStatement = SQLUtils.parseSingleMysqlStatement(originSql);
        if (sqlStatement instanceof SQLInsertStatement sqlInsertStatement) {
            String tableName = sqlInsertStatement.getTableName().getSimpleName();
            // 获取分库的策略
            HefShardingStrategy databaseStrategy = databaseStrategies.get(tableName);
            Map<String, Object> shardingColumns = createShardingColumns(args, sqlInsertStatement);
            String targetDbName = databaseStrategy.doSharding(null, tableName, shardingColumns);
            // 获取分表的策略
            HefShardingStrategy tableStrategy = tableStrategies.get(tableName);
            String targetTableName = tableStrategy.doSharding(null, tableName, shardingColumns);
            log.debug("===> [sharding] targetDbName: {}, targetTableName: {}", targetDbName, targetTableName);
            System.out.println(Strings.lenientFormat("===> [sharding] targetDbName: %s, targetTableName: %s", targetDbName, targetTableName));
        }

        Object parameterObject = args[0];
        int id=0;
        if (parameterObject instanceof User user) {
            id = user.getId();
        } else if (parameterObject instanceof Integer uid) {
            id = uid;
        }
        String lookupKey = id % 2 == 0 ? "ds0" : "ds1";
        return new HefShardingResult(lookupKey, originSql);
    }

    private static Map<String, Object> createShardingColumns(Object[] args, SQLInsertStatement sqlInsertStatement) {
        Map<String, Object> shardingColumns = Maps.newHashMap();
        List<SQLExpr> columns = sqlInsertStatement.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            SQLExpr sqlExpr = columns.get(i);
            SQLIdentifierExpr sqlIdentifierExpr = (SQLIdentifierExpr) sqlExpr;
            String columnName = sqlIdentifierExpr.getSimpleName();
            Object columnValue = args[i];
            shardingColumns.put(columnName, columnValue);
        }
        return shardingColumns;
    }
}
