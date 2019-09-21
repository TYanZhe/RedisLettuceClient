package cn.org.tpeach.nosql.redis.command.set;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * @author tyz
 * @Title: ScardSet
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-03 0:41
 * @since 1.0.0
 */
public class ScardSet extends JedisDbCommand<Long> {
    private static final Logger logger = LoggerFactory.getLogger(ScardSet.class);
    private String key;

    /**
     * 命令：SCARD key
     *
     * @param id
     * @param db
     * @param key
     */
    public ScardSet(String id, int db, String key) {
        super(id, db);
        this.key = key;
    }
    @Override
    public String sendCommand() {
        return "SCARD "+key;
    }
    /**
     * 返回集合key的基数(集合中元素的数量)。
     * @param redisLarkContext
     * @return 集合的基数。 当key不存在时，返回0。
     */
    @Override
    public Long concreteCommand(RedisLarkContext redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        final Long response = redisLarkContext.scard(key);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }


}
