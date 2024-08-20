package io.github.hefrankeleyn.hefsharding.config;

import io.github.hefrankeleyn.hefsharding.core.HefShardingContext;
import io.github.hefrankeleyn.hefsharding.core.HefShardingResult;
import io.github.hefrankeleyn.hefsharding.demo.model.User;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.mapper.MapperFactoryBean;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @Date 2024/8/16
 * @Author lifei
 */
public class HefShardingMapperFactoryBean<T> extends MapperFactoryBean<T> {

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
//            Object parameterObject = boundSql.getParameterObject();
            Object parameterObject = args[0];
            if (parameterObject instanceof User user) {
                Integer id = user.getId();
                String lookupKey = id % 2 == 0 ? "ds0" : "ds1";
                HefShardingContext.setHefShardingResult(new HefShardingResult(lookupKey));
            } else if (parameterObject instanceof Integer id) {
                String lookupKey = id % 2 == 0 ? "ds0" : "ds1";
                HefShardingContext.setHefShardingResult(new HefShardingResult(lookupKey));
            }
            System.out.println(parameterObject);
            return method.invoke(originProxy, args);
        });
        return (T)result;

    }
}
