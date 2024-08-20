package io.github.hefrankeleyn.hefsharding.mybatis;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;

/**
 * @Date 2024/8/15
 * @Author lifei
 */
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare",
                args = {Connection.class, Integer.class})
})
public class HefSqlStatementInterceptor implements Interceptor {

    private static final Logger log = LoggerFactory.getLogger(HefSqlStatementInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws InvocationTargetException, IllegalAccessException {
        StatementHandler statementHandler = (StatementHandler)invocation.getTarget();
        BoundSql boundSql = statementHandler.getBoundSql();
        String originalSQL = boundSql.getSql();
        log.debug("====> originalSQL: {}", originalSQL);
        return invocation.proceed();
    }
}
