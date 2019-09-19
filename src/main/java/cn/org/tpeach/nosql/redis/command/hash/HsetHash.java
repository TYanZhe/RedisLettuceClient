package cn.org.tpeach.nosql.redis.command.hash;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
/**
 * @author tyz
 * @Title: HsetHash
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-02 22:12
 * @since 1.0.0
 */
public class HsetHash extends JedisDbCommand<Boolean> {
    private String key;
    private String field;
    private String value;

    /**
     * 命令：HSET key field
     * @param id
     * @param db
     * @param key
     * @param field
     * @param value
     */
    public HsetHash(String id, int db, String key, String field, String value) {
        super(id,db);
        this.key = key;
        this.field = field;
        this.value = value;
    }

    /**
     * 将哈希表key中的域field的值设为value。时间复杂度：O(1)
     * @param redisLarkContext
     * @return 如果field是哈希表中的一个新建域，并且值设置成功，返回1。如果哈希表中域field已经存在且旧值已被新值覆盖，返回0
     */
    @Override
    public Boolean concreteCommand(RedisLarkContext redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        final Boolean response = redisLarkContext.hset(key, field, value);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_2_0;
    }
}
