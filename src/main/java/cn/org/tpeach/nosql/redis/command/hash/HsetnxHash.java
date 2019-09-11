package cn.org.tpeach.nosql.redis.command.hash;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;

/**
 * @author tyz
 * @Title: HsetnxHash
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-02 21:51
 * @since 1.0.0
 */
public class HsetnxHash extends JedisDbCommand<Long> {
    private String key;
    private String field;
    private String value;

    /**
     * 命令：HSETNX key field value
     * @param id
     * @param db
     * @param key
     * @param field
     * @param value
     */
    public HsetnxHash(String id, int db, String key, String field, String value) {
        super(id,db);
        this.key = key;
        this.field = field;
        this.value = value;
    }

    /**
     * 将哈希表key中的域field的值设置为value，当且仅当域field不存在。<br/>
     * 如果项不存在则赋值，存在时什么都不做。<br/>
     * 如果key不存在，一个新哈希表被创建并执行HSETNX命令<br/>
     * 时间复杂度：O(1)
     * @param redisLarkContext
     * @return 设置成功，返回1。如果给定域已经存在且没有操作被执行，返回0
     */
    @Override
    public Long concreteCommand(RedisLarkContext redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        final Long response = redisLarkContext.hsetnx(key, field, value);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_2_0;
    }
}
