package io.github.hefrankeleyn.hefsharding.datasource;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import io.github.hefrankeleyn.hefsharding.config.HefShardingProperties;
import io.github.hefrankeleyn.hefsharding.core.HefShardingContext;
import io.github.hefrankeleyn.hefsharding.core.HefShardingResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Date 2024/8/15
 * @Author lifei
 */
public class HefShardingDataSource extends AbstractRoutingDataSource {

    private static final Logger log = LoggerFactory.getLogger(HefShardingDataSource.class);

    public HefShardingDataSource(HefShardingProperties hefShardingProperties) {
        try {
            Map<Object, Object> targetDataSources = hefShardingProperties.getDatasources().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry-> {
                        try {
                            return DruidDataSourceFactory.createDataSource(entry.getValue());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }));
            // 设置数据源列表
            setTargetDataSources(targetDataSources);
            // 指定默认数据源
            setDefaultTargetDataSource(targetDataSources.values().stream().findFirst().get());
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    protected Object determineCurrentLookupKey() {
        HefShardingResult hefShardingResult = HefShardingContext.getHefShardingResult();
        log.debug("===> determineCurrentLookupKey: {}", hefShardingResult);
        if (Objects.isNull(hefShardingResult)) {
            return null;
        }
        return hefShardingResult.getTargetDataSourceName();
    }
}
