package cn.org.tpeach.nosql.redis.command.server;

import cn.org.tpeach.nosql.enums.RedisStructure;
import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;

/**
 * @author tyz
 * @Title: RedisStructureCommand
 * @ProjectName redisLark-Github
 * @Description: TODO
 * @date 2019-09-13 23:55
 * @since 1.0.0
 */
public class RedisStructureCommand extends JedisCommand<RedisStructure> {
    public RedisStructureCommand(String id) {
        super(id);
    }

    @Override
    public RedisStructure concreteCommand(RedisLarkContext redisLarkContext) {
        return redisLarkContext.getRedisStructure();
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }
}
