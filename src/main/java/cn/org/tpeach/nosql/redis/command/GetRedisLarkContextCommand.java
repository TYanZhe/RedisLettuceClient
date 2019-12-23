package cn.org.tpeach.nosql.redis.command;

import cn.org.tpeach.nosql.enums.RedisVersion;

public class GetRedisLarkContextCommand extends JedisCommand<RedisLarkContext<byte[],byte[]>> {
    public GetRedisLarkContextCommand(String id) {
        super(id);
    }

    @Override
    public String sendCommand() {
        return null;
    }

    @Override
    public RedisLarkContext<byte[], byte[]> concreteCommand(RedisLarkContext<byte[], byte[]> redisLarkContext) {
        return redisLarkContext;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }
}
