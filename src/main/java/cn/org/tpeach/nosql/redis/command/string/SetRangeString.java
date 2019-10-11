package cn.org.tpeach.nosql.redis.command.string;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tyz
 * @Title: SetRangeString
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-06-28 22:57
 * @since 1.0.0
 */
public class SetRangeString extends JedisDbCommand<Long> {
    final static Logger logger = LoggerFactory.getLogger(SetRangeString.class);
    private byte[] key;
    private long offset;
    private byte[] value;

    /**
     * 命令：SETRANGE key offset value
     * @param id
     * @param db
     * @param key
     * @param offset
     * @param value
     */
    public SetRangeString(String id, int db, byte[] key, long offset, byte[] value) {
        super(id,db);
        this.key = key;
        this.offset = offset;
        this.value = value;
    }
    @Override
    public String sendCommand() {
        return "SETRANGE "+byteToStr(key) +" "+offset+" "+byteToStr(value);
    }
    /**
     * 用value参数覆写(Overwrite)给定key所储存的字符串值，从偏移量offset开始。
     * 不存在的key当作空白字符串处理。
     * SETRANGE命令会确保字符串足够长以便将value设置在指定的偏移量上，
     * 如果给定key原来储存的字符串长度比偏移量小(比如字符串只有5个字符长，
     * 但你设置的offset是10)，那么原字符和偏移量之间的空白将用零比特(zerobytes,"\x00")来填充。
     * 注意你能使用的最大偏移量是2^29-1(536870911)，因为Redis的字符串被限制在512兆(megabytes)内。
     * 如果你需要使用比这更大的空间，你得使用多个key。
     * 时间复杂度：
     * 对小(small)的字符串，平摊复杂度O(1)。(关于什么字符串是”小”的，请参考APPEND命令)
     * 否则为O(M)，M为value参数的长度。
     * @param redisLarkContext
     * @return 被SETRANGE修改之后，字符串的长度。
     */
    @Override
    public Long concreteCommand(RedisLarkContext<byte[], byte[]> redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        final Long response = redisLarkContext.setrange(key,offset, value);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }
}
