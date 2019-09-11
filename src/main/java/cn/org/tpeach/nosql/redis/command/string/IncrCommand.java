package cn.org.tpeach.nosql.redis.command.string;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;

/**
 * @author tyz
 * @Title: IncrCommand
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-03 0:21
 * @since 1.0.0
 */
public class IncrCommand extends JedisDbCommand<Long> {
    private String key;

    /**
     * 命令：INCR key
     * @param id
     * @param db
     * @param key
     */
    public IncrCommand(String id, int db, String key) {
        super(id,db);
        this.key = key;
    }

    /**
     * 将key中储存的数字值增一。
     * 如果key不存在，以0为key的初始值，然后执行INCR操作。
     * 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。
     * 本操作的值限制在64位(bit)有符号数字表示之内。
     * 这是一个针对字符串的操作，因为Redis没有专用的整数类型，所以key内储存的字符串被解释为十进制64位有符号整数来执行INCR操作。
     * 时间复杂度：O(1)
     *
     * @param redisLarkContext
     * @return 执行INCR命令之后key的值。
     */
    @Override
    public Long concreteCommand(RedisLarkContext redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        final Long response = redisLarkContext.incr(key);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }
}
