package cn.org.tpeach.nosql.redis.command.key;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;

/**
 * @author tyz
 * @Title: ObjectIdletime
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-03 0:26
 * @since 1.0.0
 */
public class ObjectIdletime extends JedisDbCommand<Long> {
    private byte[] key;
    /**
     * 命令：OBJECT subcommand [arguments [arguments]]
     * @param id
     */
    public ObjectIdletime(String id, int db, byte[] key) {
        super(id,db);
        this.key = key;
    }

    @Override
    public String sendCommand() {
        return "OBJECT IDLETIME "+byteToStr(key);
    }

    /**
     * OBJECT命令允许从内部察看给定key的Redis对象。
     * 它通常用在除错(debugging)或者了解为了节省空间而对key使用特殊编码的情况。
     * 当将Redis用作缓存程序时，你也可以通过OBJECT命令中的信息，决定key的驱逐策略(eviction policies)。
     *
     * OBJECT IDLETIME <key>返回给定key自储存以来的空转时间(idle， 没有被读取也没有被写入)，以秒为单位。
     * 时间复杂度：O(1)
     * @param redisLarkContext
     * @return REFCOUNT和IDLETIME返回数字。ENCODING返回相应的编码类型。
     */
    @Override
    public Long concreteCommand(RedisLarkContext<byte[], byte[]> redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        final Long response = redisLarkContext.objectIdletime(key);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }
}
