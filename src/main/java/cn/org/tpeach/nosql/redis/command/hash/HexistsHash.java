package cn.org.tpeach.nosql.redis.command.hash;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;

/**
 * @author tyz
 * @Title: HexistsHash
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-02 23:30
 * @since 1.0.0
 */
public class HexistsHash extends JedisDbCommand<Boolean> {
    private String key;
    private String field;
    /**
     * 命令：HEXISTS key field
     * @param id
     * @param db
     * @param key
     * @param field
     */
    public HexistsHash(String id, int db, String key,String field) {
        super(id,db);
        this.db = db;
        this.key = key;
        this.field = field;
    }

    /**
     * 查看哈希表key中，给定域field是否存在
     * 时间复杂度：O(1)
     * @param redisLarkContext
     * @return 如果哈希表含有给定域，返回1。如果哈希表不含有给定域，或key不存在，返回0。
     */
    @Override
    public Boolean concreteCommand(RedisLarkContext redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        final Boolean response = redisLarkContext.hexists(key,field);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_2_0;
    }
}
