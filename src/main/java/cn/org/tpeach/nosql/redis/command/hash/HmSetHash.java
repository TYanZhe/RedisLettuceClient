package cn.org.tpeach.nosql.redis.command.hash;

import cn.org.tpeach.nosql.enums.RedisType;
import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.exception.ServiceException;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;

import java.util.Map;
import java.util.Map.Entry;
/**
 * @author tyz
 * @Title: HmSetHash
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-02 22:12
 * @since 1.0.0
 */
public class HmSetHash extends JedisDbCommand<String> {
    private byte[] key;
    private  Map<byte[], byte[]> hash;

    /**
     * 命令：HMSET key field value [field value ...]
     * @param id
     * @param db
     * @param key
     * @param hash
     */
    public HmSetHash(String id, int db, byte[] key, Map<byte[], byte[]> hash) {
        super(id,db);
        this.key = key;
        this.hash = hash;
    }

    @Override
    public String sendCommand() {
        StringBuffer sb = new StringBuffer();
        for (Entry<byte[], byte[]> entry : hash.entrySet()) {
            sb.append(" ");
            sb.append(byteToStr(entry.getKey()));
            sb.append(" ");
            sb.append(byteToStr(entry.getValue()));
        }
        return "HMSET "+byteToStr(key)+sb.toString();
    }

    /**
     * 同时将多个field - value(域-值)对设置到哈希表key中。
     * 此命令会覆盖哈希表中已存在的域。
     * 如果key不存在，一个空哈希表被创建并执行HMSET操作。
     * 时间复杂度：O(N)，N为field - value对的数量。
     * @param redisLarkContext
     * @return 如果命令执行成功，返回OK。当key不是哈希表(hash)类型时，返回一个错误。[(error) ERR Operation against a key holding the wrong kind of value)]
     */
    @Override
    public String concreteCommand(RedisLarkContext<byte[], byte[]> redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        if(redisLarkContext.exists(key) > 0){
            final String type = redisLarkContext.type(key);
            if(RedisType.HASH != RedisType.getRedisType(type)){
                throw new ServiceException("Key exist, and type is not hash");
            }
        }
        final String response = redisLarkContext.hmset(key, hash);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_2_0;
    }
}
