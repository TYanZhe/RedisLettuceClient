package cn.org.tpeach.nosql.redis.command.key;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author tyz
 * @Title: ExpireCommand
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-03 0:26
 * @since 1.0.0
 */
@Slf4j
public class ExpireCommand extends JedisDbCommand<Boolean> {
    private String key;
    private int seconds;
    /**
     * 命令：EXPIRE key seconds
     * @param id
     */
    public ExpireCommand(String id, int db,String key,int seconds) {
        super(id,db);
        this.key = key;
        this.seconds = seconds;
    }

    @Override
    public String sendCommand() {
        return "EXPIRE "+key+" "+seconds;
    }

    /**
     * 为给定key设置生存时间。
     * 当key过期时，它会被自动删除。
     * 在Redis中，带有生存时间的key被称作“易失的”(volatile)。
     * 在低于2.1.3版本的Redis中，已存在的生存时间不可覆盖
     * 从2.1.3版本开始，key的生存时间可以被更新，也可以被PERSIST命令移除。(详情参见 http://redis.io/topics/expire)。
     * @param redisLarkContext
     * @return 设置成功返回1。当key不存在或者不能为key设置生存时间时(比如在低于2.1.3中你尝试更新key的生存时间)，返回0。
     */
    @Override
    public Boolean concreteCommand(RedisLarkContext redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        final Boolean response = redisLarkContext.expire(key, seconds);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }
}
