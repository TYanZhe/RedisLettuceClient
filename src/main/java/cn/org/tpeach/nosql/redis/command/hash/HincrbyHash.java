package cn.org.tpeach.nosql.redis.command.hash;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;

/**
 * @author tyz
 * @Title: HincrbyHash
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-02 23:30
 * @since 1.0.0
 */
public class HincrbyHash extends JedisDbCommand<Long> {
    private String key;
    private String field;
    private long increment;
    /**
     * 命令：HINCRBY key field increment
     * @param id
     * @param db
     * @param key
     * @param field
     */
    public HincrbyHash(String id, int db, String key, String field,long increment) {
        super(id,db);
        this.key = key;
        this.field = field;
        this.increment = increment;
    }

    @Override
    public String sendCommand() {
        return "HINCRBY "+key +" "+field+" "+increment;
    }

    /**
     * 为哈希表key中的域field的值加上增量increment。
     * 增量也可以为负数，相当于对给定域进行减法操作。
     * 如果key不存在，一个新的哈希表被创建并执行HINCRBY命令。
     * 如果域field不存在，那么在执行命令前，域的值被初始化为0。
     * 对一个储存字符串值的域field执行HINCRBY命令将造成一个错误。
     * 本操作的值限制在64位(bit)有符号数字表示之内。
     * 时间复杂度：O(1)
     * @param redisLarkContext
     * @return 执行HINCRBY命令之后，哈希表key中域field的值。
     */
    @Override
    public Long  concreteCommand(RedisLarkContext redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        final Long response = redisLarkContext.hincrBy(key,field,increment);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_2_0;
    }
}
