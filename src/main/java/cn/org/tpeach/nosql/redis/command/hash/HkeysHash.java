package cn.org.tpeach.nosql.redis.command.hash;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisCommand;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;

import java.util.Set;

/**
 * @author tyz
 * @Title: HkeysHash
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-02 22:12
 * @since 1.0.0
 */
public class HkeysHash extends JedisDbCommand<Set<String>> {

    private String key;


    /**
     * 命令：HKEYS key
     * @param id
     * @param db
     * @param key
     */
    public HkeysHash(String id, int db, String key) {
        super(id,db);
        this.key = key;
    }

    /**
     * 返回哈希表key中的所有域。
     * 时间复杂度：O(N)，N为哈希表的大小。
     * @param redisLarkContext
     * @return 一个包含哈希表中所有域的表。当key不存在时，返回一个空表。
     */
    @Override
    public Set<String> concreteCommand(RedisLarkContext redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        final Set<String> response = redisLarkContext.hkeys(key);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_2_0;
    }
}
