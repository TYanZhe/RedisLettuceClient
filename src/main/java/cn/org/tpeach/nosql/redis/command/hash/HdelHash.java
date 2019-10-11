package cn.org.tpeach.nosql.redis.command.hash;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author tyz
 * @Title: HdelHash
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-02 22:34
 * @since 1.0.0
 */
public class HdelHash extends JedisDbCommand<Long> {
    private byte[] key;
    private byte[][] fields;

    /**
     * 命令：HDEL key field [field ...]
     * @param id
     * @param db
     * @param key
     * @param fields
     */
//    public HdelHash(String id, int db, String key, String... fields) {
//        super(id,db);
//        this.key = strToByte(key);
//        this.fields = strArrToByte(fields);
//    }
    public HdelHash(String id, int db, byte[] key, byte[]... fields) {
        super(id,db);
        this.key = key;
        this.fields = fields;
    }
    @Override
    public String sendCommand() {
        return "HDEL "+byteToStr(key) +" "+ Arrays.stream(byteArrToStr(fields)).collect(Collectors.joining(" "));
    }

    /**
     * 删除哈希表key中的一个或多个指定域，不存在的域将被忽略。
     * 在Redis2.4以下的版本里，HDEL每次只能删除单个域，如果你需要在一个原子时间内删除多个域，请将命令包含在MULTI/ EXEC块内。
     * 时间复杂度：O(N)，N为要删除的域的数量。
     * @param redisLarkContext
     * @return  被成功移除的域的数量，不包括被忽略的域。
     */
    @Override
    public Long concreteCommand(RedisLarkContext<byte[], byte[]> redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        final Long response = redisLarkContext.hdel(key,fields);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_2_0;
    }
}
