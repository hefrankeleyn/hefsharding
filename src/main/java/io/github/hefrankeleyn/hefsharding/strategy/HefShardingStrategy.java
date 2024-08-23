package io.github.hefrankeleyn.hefsharding.strategy;

import java.util.List;
import java.util.Map;

public interface HefShardingStrategy {

    List<String> getShardingColumns();

    String doSharding(Map<String, Object> shardingParams);
}
