package io.github.hefrankeleyn.hefsharding.strategy;

import java.util.List;
import java.util.Map;

public interface HefShardingStrategy {

    List<String> getShardingColumns();

    String doSharding(List<String> availableTargetNames, String logicTableName, Map<String, Object> shardingParams);
}
