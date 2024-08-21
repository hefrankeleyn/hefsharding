package io.github.hefrankeleyn.hefsharding.strategy.impl;

import groovy.lang.Closure;
import io.github.hefrankeleyn.hefsharding.strategy.HefShardingStrategy;
import io.github.hefrankeleyn.hefsharding.strategy.InlineExpressionParser;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @Date 2024/8/20
 * @Author lifei
 */
public class HefHashShardingStrategy implements HefShardingStrategy {

    private final String shardingColumn;
    private final String algorithmExpression;

    public HefHashShardingStrategy(Properties properties) {
        this.shardingColumn = properties.getProperty("shardingColumn");
        this.algorithmExpression = properties.getProperty("algorithmExpression");
    }

    @Override
    public List<String> getShardingColumns() {
        return List.of(this.shardingColumn);
    }

    @Override
    public String doSharding(List<String> availableTargetNames, String logicTableName, Map<String, Object> shardingParams) {
        // 处理表达式，纠正写法, 因为spring表达式的写法和groovy 写法，冲突
        String expression = InlineExpressionParser.handlePlaceHolder(algorithmExpression);
        InlineExpressionParser parser = new InlineExpressionParser(expression);
        // 计算必包的值
        Closure<?> closure = parser.evaluateClosure();
        // 可以把闭包看作函数，接受参数
        closure.setProperty(shardingColumn, shardingParams.get(shardingColumn));
        return closure.call().toString();
    }

    public String getShardingColumn() {
        return shardingColumn;
    }

    public String getAlgorithmExpression() {
        return algorithmExpression;
    }
}
