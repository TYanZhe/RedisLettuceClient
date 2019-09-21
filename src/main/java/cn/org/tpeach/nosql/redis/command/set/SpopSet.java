package cn.org.tpeach.nosql.redis.command.set;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;

/**
 * @author tyz
 * @Title: SpopSet
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-09-21 0:41
 * @since 1.0.0
 */
public class SpopSet extends JedisDbCommand<String> {
    private String key;

    /**
     * 命令：SPOP key
     * @param id
     * @param db
     * @param key
     */
    public SpopSet(String id, int db, String key) {
        super(id, db);
        this.key = key;

    }

    @Override
    public String sendCommand() {
        return "SPOP "+key ;
    }

    /**
     * 移除并返回集合中的一个随机元素
     * 时间复杂度:O(1)
     * @param redisLarkContext
     * @return 被移除的随机元素。当key不存在或key是空集时，返回nil
     */
    @Override
    public String concreteCommand(RedisLarkContext redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        final String response = redisLarkContext.spop(key);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }

}
