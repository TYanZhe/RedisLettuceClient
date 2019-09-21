package cn.org.tpeach.nosql.redis.command.hash;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisCommand;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;

import java.util.List;
import java.util.Map;

/**
 * @author tyz
 * @Title: HgetHash
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-02 23:10
 * @since 1.0.0
 */
public class HmGetAllHash extends JedisDbCommand<Map<String, String>> {
    private String key;

    /**
     * 命令：HGETALL key
     * @param id
     * @param db
     */
    public HmGetAllHash(String id, int db, String key) {
        super(id,db);
        this.key = key;
    }


    @Override
    public String sendCommand() {
        return "HGETALL "+key;
    }

    /**
     * 返回哈希表key中，所有的域和值。
     * 在返回值里，紧跟每个域名(field name)之后是域的值(value)，所以返回值的长度是哈希表大小的两倍。
     * 时间复杂度：O(N)，N为哈希表的大小。
     * @param redisLarkContext
     * @return 以列表形式返回哈希表的域和域的值。 若key不存在，返回空列表。
     */
    @Override
    public Map<String, String> concreteCommand(RedisLarkContext redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        final Map<String, String> response = redisLarkContext.hgetAll(key);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_2_0;
    }
}
