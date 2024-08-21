package io.github.hefrankeleyn.hefsharding.core;

import com.google.common.base.MoreObjects;

/**
 * @Date 2024/8/15
 * @Author lifei
 */
public class HefShardingResult {

    private String targetDataSourceName;
    private String targetSqlStatement;

    public HefShardingResult() {
    }

    public HefShardingResult(String targetDataSourceName, String targetSqlStatement) {
        this.targetDataSourceName = targetDataSourceName;
        this.targetSqlStatement = targetSqlStatement;
    }

    public String getTargetDataSourceName() {
        return targetDataSourceName;
    }

    public void setTargetDataSourceName(String targetDataSourceName) {
        this.targetDataSourceName = targetDataSourceName;
    }

    public String getTargetSqlStatement() {
        return targetSqlStatement;
    }

    public void setTargetSqlStatement(String targetSqlStatement) {
        this.targetSqlStatement = targetSqlStatement;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(HefShardingResult.class)
                .add("targetDataSourceName", targetDataSourceName)
                .add("targetSqlStatement", targetSqlStatement)
                .toString();
    }
}
