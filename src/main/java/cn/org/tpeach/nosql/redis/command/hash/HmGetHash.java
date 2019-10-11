package cn.org.tpeach.nosql.redis.command.hash;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import io.lettuce.core.KeyValue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
/**
 * @author tyz
 * @Title: HgetHash
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-02 22:12
 * @since 1.0.0
 */
public class HmGetHash extends JedisDbCommand<List<KeyValue<byte[], byte[]>>> {

    private byte[] key;
    private byte[][] fields;
    /**
     * 命令：HMGET key field [field ...]
     * @param id
     * @param db
     * @param key
     * @param fields
     */
    public HmGetHash(String id, int db, byte[] key,byte[]... fields) {
        super(id,db);
        this.key = key;
        this.fields = fields;
    }


    @Override
    public String sendCommand() {
        return "HMGET "+ byteToStr(key)  + " "+ Arrays.stream(byteArrToStr(fields)).collect(Collectors.joining(" "));
    }

    /**
     * 返回哈希表key中，一个或多个给定域的值。
     * 如果给定的域不存在于哈希表，那么返回一个nil值。
     * 因为不存在的key被当作一个空哈希表来处理，所以对一个不存在的key进行HMGET操作将返回一个只带有nil值的表。
     * 时间复杂度：O(N)，N为给定域的数量。
     * @param redisLarkContext
     * @return 一个包含多个给定域的关联值的表，表值的排列顺序和给定域参数的请求顺序一样。
     */
    @Override
    public List<KeyValue<byte[], byte[]>> concreteCommand(RedisLarkContext<byte[], byte[]> redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        final List<KeyValue<byte[], byte[]>> response = redisLarkContext.hmget(key, fields);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_2_0;
    }
}
