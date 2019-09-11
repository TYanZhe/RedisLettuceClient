package cn.org.tpeach.nosql.redis.command.list;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tyz
 * @Title: LpopList
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-03 0:37
 * @since 1.0.0
 */
public class LlenList extends JedisDbCommand<Long> {
    private static final Logger logger = LoggerFactory.getLogger(LlenList.class);
    private String key;

    /**
     * 命令：LLEN key
     *
     * @param id
     * @param db
     * @param key
     */
    public LlenList(String id, int db, String key) {
        super(id, db);
        this.key = key;
    }

    /**
     * 返回列表key的长度。
     * 如果key不存在，则key被解释为一个空列表，返回0.
     * 如果key不是列表类型，返回一个错误。
     * 时间复杂度：O(1)
     *
     * @param redisLarkContext
     * @return 列表key的长度。
     */
    @Override
    public Long concreteCommand(RedisLarkContext redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        logger.info("[runCommand] LLEN {}", key);
        final Long response = redisLarkContext.llen(key);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }
}
