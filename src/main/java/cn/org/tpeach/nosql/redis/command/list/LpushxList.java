package cn.org.tpeach.nosql.redis.command.list;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tyz
 * @Title: LpushList
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-03 0:36
 * @since 1.0.0
 */
public class LpushxList extends JedisDbCommand<Long> {
    private static final Logger logger = LoggerFactory.getLogger(LpushxList.class);
    private String key;
    private String[] strings;

    /**
     * 命令：LPUSHX key value
     *
     * @param id
     * @param db
     * @param key
     * @param strings
     */
    public LpushxList(String id, int db, String key, String[] strings) {
        super(id, db);
        this.key = key;
        this.strings = strings;
    }

    /**
     * 将值value插入到列表key的表头，当且仅当key存在并且是一个列表。
     * 和LPUSH命令相反，当key不存在时，LPUSHX命令什么也不做。
     * 时间复杂度：O(1)
     *
     * @param redisLarkContext
     * @return LPUSHX命令执行之后，表的长度。
     */
    @Override
    public Long concreteCommand(RedisLarkContext redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        logger.info("[runCommand] LPUSHX {} {}", key, strings);
        final Long response = redisLarkContext.lpushx(key, strings);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }
}