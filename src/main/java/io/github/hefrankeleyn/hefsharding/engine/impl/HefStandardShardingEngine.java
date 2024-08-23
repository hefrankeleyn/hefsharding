package io.github.hefrankeleyn.hefsharding.engine.impl;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import static com.google.common.base.Preconditions.*;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import io.github.hefrankeleyn.hefsharding.config.HefShardingProperties;
import io.github.hefrankeleyn.hefsharding.core.HefShardingResult;
import io.github.hefrankeleyn.hefsharding.engine.HefShardingEngine;
import io.github.hefrankeleyn.hefsharding.strategy.HefShardingStrategy;
import io.github.hefrankeleyn.hefsharding.strategy.impl.HefHashShardingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        String originalTableName;
        Map<String, Object> shardingColumns;
        if (sqlStatement instanceof SQLInsertStatement sqlInsertStatement) {
            originalTableName = sqlInsertStatement.getTableName().getSimpleName();
            shardingColumns = createShardingColumns(args, sqlInsertStatement);
        } else {
            MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
            visitor.setParameters(List.of(args));
            sqlStatement.accept(visitor);
            List<String> tableNameList = visitor.getOriginalTables().stream().map(SQLName::getSimpleName).distinct().toList();
            checkState(tableNameList.size()==1, "只支持单表的分片");
            originalTableName = tableNameList.get(0);
            shardingColumns = visitor.getConditions().stream()
                    .collect(Collectors.toMap(c->c.getColumn().getName(), c->c.getValues().get(0)));
        }
        // 获取分库的策略
        HefShardingStrategy databaseStrategy = databaseStrategies.get(originalTableName);
        String targetDataSource = databaseStrategy.doSharding(shardingColumns);
        // 获取分表的策略
        HefShardingStrategy tableStrategy = tableStrategies.get(originalTableName);
        String targetTableName = tableStrategy.doSharding(shardingColumns);
        log.debug("===> [sharding] targetDataSource: {}, targetTableName: {}", targetDataSource, targetTableName);
        System.out.println(Strings.lenientFormat("===> [sharding] targetDataSource: %s, targetTableName: %s", targetDataSource, targetTableName));
        return new HefShardingResult(targetDataSource, originSql.replace(originalTableName, targetTableName));
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
