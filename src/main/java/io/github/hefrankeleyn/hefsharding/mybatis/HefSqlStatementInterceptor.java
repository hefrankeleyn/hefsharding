package io.github.hefrankeleyn.hefsharding.mybatis;

import com.google.common.base.Strings;
import io.github.hefrankeleyn.hefsharding.core.HefShardingContext;
import io.github.hefrankeleyn.hefsharding.core.HefShardingResult;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.objenesis.instantiator.util.UnsafeUtils;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.util.Objects;

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
        HefShardingResult hefShardingResult = HefShardingContext.getHefShardingResult();
        if (Objects.nonNull(hefShardingResult) && !Strings.isNullOrEmpty(hefShardingResult.getTargetSqlStatement())) {
            String targetSqlStatement = hefShardingResult.getTargetSqlStatement();
            StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
            BoundSql boundSql = statementHandler.getBoundSql();
            String originalSQL = boundSql.getSql();
            log.debug("====> originalSQL: {}", originalSQL);
            log.debug("===> targetSqlStatement: {}", targetSqlStatement);
            // 修改sql
            updateTargetSQL(boundSql, targetSqlStatement);
        }
        return invocation.proceed();
    }

    private static void updateTargetSQL(BoundSql boundSql, String targetSqlStatement) {
        try {
            Field declaredField = boundSql.getClass().getDeclaredField("sql");
//            declaredField.setAccessible(true);
//            declaredField.set(boundSql, targetSqlStatement);
            Unsafe unsafe = UnsafeUtils.getUnsafe();
            long fieldOffset = unsafe.objectFieldOffset(declaredField);
            unsafe.putObject(boundSql, fieldOffset, targetSqlStatement);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
