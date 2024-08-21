package io.github.hefrankeleyn.hefsharding.config;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @Date 2024/8/15
 * @Author lifei
 */
@ConfigurationProperties(prefix = "spring.sharding")
public class HefShardingProperties {
    private Map<String, Properties> datasources = Maps.newHashMap();
    private Map<String, TableProperties> tables = Maps.newHashMap();

    public static class TableProperties {
        private List<String> actualDataNodes;
        private Properties databaseStrategy;
        private Properties tableStrategy;

        public List<String> getActualDataNodes() {
            return actualDataNodes;
        }

        public void setActualDataNodes(List<String> actualDataNodes) {
            this.actualDataNodes = actualDataNodes;
        }

        public Properties getDatabaseStrategy() {
            return databaseStrategy;
        }

        public void setDatabaseStrategy(Properties databaseStrategy) {
            this.databaseStrategy = databaseStrategy;
        }

        public Properties getTableStrategy() {
            return tableStrategy;
        }

        public void setTableStrategy(Properties tableStrategy) {
            this.tableStrategy = tableStrategy;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(TableProperties.class)
                    .add("actualDataNodes", actualDataNodes)
                    .add("databaseStrategy", databaseStrategy)
                    .add("tableStrategy", tableStrategy)
                    .toString();
        }
    }


    public Map<String, Properties> getDatasources() {
        return datasources;
    }

    public void setDatasources(Map<String, Properties> datasources) {
        this.datasources = datasources;
    }

    public Map<String, TableProperties> getTables() {
        return tables;
    }

    public void setTables(Map<String, TableProperties> tables) {
        this.tables = tables;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(HefShardingProperties.class)
                .add("datasources", datasources)
                .add("tables", tables)
                .toString();
    }
}
