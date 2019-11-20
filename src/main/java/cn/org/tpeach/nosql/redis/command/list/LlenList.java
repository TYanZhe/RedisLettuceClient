package cn.org.tpeach.nosql.redis.command.list;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;

/**
 * @author tyz
 * @Title: LpopList
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-03 0:37
 * @since 1.0.0
 */
public class LlenList extends JedisDbCommand<Long> {
    private byte[] key;

    /**
     * 命令：LLEN key
     *
     * @param id
     * @param db
     * @param key
     */
    public LlenList(String id, int db, byte[] key) {
        super(id, db);
        this.key = key;
    }

    @Override
    public String sendCommand() {
        return "LLEN "+byteToStr(key);
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
    public Long concreteCommand(RedisLarkContext<byte[], byte[]> redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        final Long response = redisLarkContext.llen(key);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }
}
