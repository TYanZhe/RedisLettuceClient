package cn.org.tpeach.nosql.redis.command.key;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisCommand;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;

/**
 * @author tyz
 * @Title: TypeCommand
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-03 0:26
 * @since 1.0.0
 */
public class TypeCommand extends JedisDbCommand<String> {
    private String key;
    /**
     * 命令：TYPE key
     * @param id
     */
    public TypeCommand(String id,int db,String key) {
        super(id,db);
        this.key = key;
    }

    /**
     * 返回key所储存的值的类型。
     * 时间复杂度：O(1)
     * @param redisLarkContext
     * @return none(key不存在) string(字符串) list(列表) set(集合) zset(有序集) hash(哈希表)
     */
    @Override
    public String concreteCommand(RedisLarkContext redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        final String response = redisLarkContext.type(key);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }
}
