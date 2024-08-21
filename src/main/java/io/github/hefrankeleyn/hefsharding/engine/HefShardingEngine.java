package io.github.hefrankeleyn.hefsharding.engine;

import io.github.hefrankeleyn.hefsharding.core.HefShardingResult;

public interface HefShardingEngine {

    HefShardingResult sharding(String originSql, Object[] args);
}
