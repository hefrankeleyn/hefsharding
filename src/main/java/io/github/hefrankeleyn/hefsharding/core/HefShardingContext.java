package io.github.hefrankeleyn.hefsharding.core;

/**
 * @Date 2024/8/15
 * @Author lifei
 */
public class HefShardingContext {
    private static final ThreadLocal<HefShardingResult> LOCAL = new ThreadLocal<>();

    public static HefShardingResult getHefShardingResult() {
        return LOCAL.get();
    }

    public static void setHefShardingResult(HefShardingResult hefShardingResult) {
        LOCAL.set(hefShardingResult);
    }
}
