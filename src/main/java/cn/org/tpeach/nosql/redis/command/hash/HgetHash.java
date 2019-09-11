package cn.org.tpeach.nosql.redis.command.hash;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;

/**
 * @author tyz
 * @Title: HgetHash
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-02 22:12
 * @since 1.0.0
 */
public class HgetHash extends JedisDbCommand<String> {

    private String key;
    private String field;


    /**
     * 命令：HGET key field
     * @param id
     * @param db
     * @param key
     * @param field
     */
    public HgetHash(String id, int db, String key, String field) {
        super(id,db);
        this.key = key;
        this.field = field;
    }

    /**
     * 返回哈希表key中给定域field的值。。<br/>
     * 时间复杂度：O(1)
     * @param redisLarkContext
     * @return 给定域的值。当给定域不存在或是给定key不存在时，返回nil。
     */
    @Override
    public String concreteCommand(RedisLarkContext redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        final String response = redisLarkContext.hget(key,field);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_2_0;
    }
}