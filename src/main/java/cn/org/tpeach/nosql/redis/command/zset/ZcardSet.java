package cn.org.tpeach.nosql.redis.command.zset;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tyz
 * @Title: ScardSet
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-03 0:41
 * @since 1.0.0
 */
public class ZcardSet extends JedisDbCommand<Long> {
    private static final Logger logger = LoggerFactory.getLogger(ZcardSet.class);
    private String key;

    /**
     * 命令：ZCARD  key
     *
     * @param id
     * @param db
     * @param key
     */
    public ZcardSet(String id, int db, String key) {
        super(id, db);
        this.key = key;
    }
    @Override
    public String sendCommand() {
        return "ZCARD "+key ;
    }
    /**
     * 返回有序集key的基数。
     * @param redisLarkContext
     * @return 当key存在且是有序集类型时，返回有序集的基数。当key不存在时，返回0。
     */
    @Override
    public Long concreteCommand(RedisLarkContext redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        final Long response = redisLarkContext.zcard(key);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }


}
