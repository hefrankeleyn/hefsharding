package io.github.hefrankeleyn.hefsharding.config;

import io.github.hefrankeleyn.hefsharding.datasource.HefShardingDataSource;
import io.github.hefrankeleyn.hefsharding.engine.HefShardingEngine;
import io.github.hefrankeleyn.hefsharding.engine.impl.HefStandardShardingEngine;
import io.github.hefrankeleyn.hefsharding.mybatis.HefSqlStatementInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @Date 2024/8/15
 * @Author lifei
 */
@Configuration
@EnableConfigurationProperties({HefShardingProperties.class})
public class HefShardingAutoConfiguration {

    @Bean
    public DataSource dataSource(HefShardingProperties hefShardingProperties) {
        return new HefShardingDataSource(hefShardingProperties);
    }

    @Bean
    public Interceptor statementHandlerInterceptor() {
        return new HefSqlStatementInterceptor();
    }

    @Bean
    public HefShardingEngine hefShardingEngine(HefShardingProperties hefShardingProperties) {
        return new HefStandardShardingEngine(hefShardingProperties);
    }
}
