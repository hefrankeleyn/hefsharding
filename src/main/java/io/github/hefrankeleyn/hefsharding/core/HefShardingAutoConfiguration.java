package io.github.hefrankeleyn.hefsharding.core;

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
}
