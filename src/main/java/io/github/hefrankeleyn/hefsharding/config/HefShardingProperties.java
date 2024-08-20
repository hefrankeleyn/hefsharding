package io.github.hefrankeleyn.hefsharding.config;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.Properties;

/**
 * @Date 2024/8/15
 * @Author lifei
 */
@ConfigurationProperties(prefix = "spring.sharding")
public class HefShardingProperties {
    private Map<String, Properties> datasources = Maps.newHashMap();


    public Map<String, Properties> getDatasources() {
        return datasources;
    }

    public void setDatasources(Map<String, Properties> datasources) {
        this.datasources = datasources;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(HefShardingProperties.class)
                .add("datasources", datasources)
                .toString();
    }
}
