package io.github.hefrankeleyn.hefsharding.core;

import com.google.common.base.MoreObjects;

/**
 * @Date 2024/8/15
 * @Author lifei
 */
public class HefShardingResult {

    private String targetDataSourceName;

    public HefShardingResult() {
    }

    public HefShardingResult(String targetDataSourceName) {
        this.targetDataSourceName = targetDataSourceName;
    }

    public String getTargetDataSourceName() {
        return targetDataSourceName;
    }

    public void setTargetDataSourceName(String targetDataSourceName) {
        this.targetDataSourceName = targetDataSourceName;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(HefShardingResult.class)
                .add("targetDataSourceName", targetDataSourceName)
                .toString();
    }
}
