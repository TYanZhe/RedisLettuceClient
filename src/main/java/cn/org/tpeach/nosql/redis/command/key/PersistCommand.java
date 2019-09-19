package cn.org.tpeach.nosql.redis.command.key;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;

/**
 * @author tyz
 * @Title: ExpireCommand
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-03 0:26
 * @since 1.0.0
 */
public class PersistCommand extends JedisDbCommand<Boolean> {
    private String key;
    /**
     * 命令：PERSIST key
     * @param id
     */
    public PersistCommand(String id, int db,String key) {
        super(id,db);
        this.key = key;

    }

    /**
     * 移除给定key的生存时间。
     * @param redisLarkContext
     * @return 当生存时间移除成功时，返回1.如果key不存在或key没有设置生存时间，返回0。
     */
    @Override
    public Boolean concreteCommand(RedisLarkContext redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        final Boolean response = redisLarkContext.persist(key);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }
}
