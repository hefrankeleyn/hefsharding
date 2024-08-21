package io.github.hefrankeleyn.hefsharding.config;

import io.github.hefrankeleyn.hefsharding.core.HefShardingContext;
import io.github.hefrankeleyn.hefsharding.core.HefShardingResult;
import io.github.hefrankeleyn.hefsharding.demo.model.User;
import io.github.hefrankeleyn.hefsharding.engine.HefShardingEngine;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * @Date 2024/8/16
 * @Author lifei
 */
public class HefShardingMapperFactoryBean<T> extends MapperFactoryBean<T> {

    // 为其添加 setter方法后， spring 发现有这个类型的对象，会自动设置该属性
    private HefShardingEngine hefShardingEngine;

    public HefShardingMapperFactoryBean() {
    }

    public HefShardingMapperFactoryBean(Class<T> mapperInterface) {
        super(mapperInterface);
    }

    @Override
    public T getObject() throws Exception {
        T originProxy = super.getObject();
        Class<T> clazz = getMapperInterface();
        SqlSession sqlSession = getSqlSession();
        Configuration configuration = sqlSession.getConfiguration();
        Object result = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, (Object proxy, Method method, Object[] args) -> {
            String mappedId = clazz.getName() + "." + method.getName();
            MappedStatement mappedStatement = configuration.getMappedStatement(mappedId);
            BoundSql boundSql = mappedStatement.getBoundSql(args);
            String originalSql = boundSql.getSql();
//            Object parameterObject = boundSql.getParameterObject();
            Object[] params = createParams(boundSql, args);
            HefShardingResult hefShardingResult = hefShardingEngine.sharding(originalSql, params);
            HefShardingContext.setHefShardingResult(hefShardingResult);
            return method.invoke(originProxy, args);
        });
        return (T)result;

    }

    private Object[] createParams(BoundSql boundSql, Object[] args) {
        Object[] result = args;
        if (args.length==1 && !ClassUtils.isPrimitiveOrWrapper(args[0].getClass())) {
            Object param = args[0];
            result = boundSql.getParameterMappings().stream().map(ParameterMapping::getProperty)
                    .map(fieldName->getFieldValue(param, fieldName))
                    .toArray(Object[]::new);
        }
        return result;
    }

    private Object getFieldValue(Object target, String fieldName) {
        try {
            Field declaredField = target.getClass().getDeclaredField(fieldName);
            declaredField.setAccessible(true);
            return declaredField.get(target);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public HefShardingEngine getHefShardingEngine() {
        return hefShardingEngine;
    }

    public void setHefShardingEngine(HefShardingEngine hefShardingEngine) {
        this.hefShardingEngine = hefShardingEngine;
    }
}
