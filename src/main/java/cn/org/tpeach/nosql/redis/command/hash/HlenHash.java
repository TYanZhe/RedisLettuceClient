package cn.org.tpeach.nosql.redis.command.hash;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;

/**
 * @author tyz
 * @Title: HlenHash
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-02 23:30
 * @since 1.0.0
 */
public class HlenHash extends JedisDbCommand<Long > {
    private byte[] key;

    /**
     * 命令：HLEN key
     * @param id
     * @param db
     * @param key
     */
    public HlenHash(String id, int db, byte[] key) {
        super(id,db);
        this.key = key;
    }

    @Override
    public String sendCommand() {
        return "HLEN "+ byteToStr(key);
    }

    /**
     * 返回哈希表key中域的数量。
     * 时间复杂度：O(1)
     * @param redisLarkContext
     * @return 哈希表中域的数量。当key不存在时，返回0。
     */
    @Override
    public Long concreteCommand(RedisLarkContext<byte[], byte[]> redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        final Long response = redisLarkContext.hlen(key);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_2_0;
    }
}
