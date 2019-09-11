package cn.org.tpeach.nosql.redis.command.key;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisCommand;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;

import java.util.Set;

/**
 * @author tyz
 * @Title: TTLCommand
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-03 0:26
 * @since 1.0.0
 */
public class TTLCommand extends JedisDbCommand<Long> {

    private String key;
    /**
     * 命令：TTL key
     * @param id
     *  @param db
     * @param key
     */
    public TTLCommand(String id, int db,String key) {
        super(id,db);
        this.key = key;
    }

    /**
     * 返回给定key的剩余生存时间(time to live)(以秒为单位)。
     * 时间复杂度：O(1)。
     * @param redisLarkContext
     * @return key的剩余生存时间(以秒为单位)。当key不存在或没有设置生存时间时，返回-1 。
     */
    @Override
    public Long concreteCommand(RedisLarkContext redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        final Long response = redisLarkContext.ttl(key);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }
}
