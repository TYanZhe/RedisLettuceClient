package cn.org.tpeach.nosql.redis.command.key;

import java.util.List;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;

/**
 * @author tyz
 * @Title: KeysCommand
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-03 0:26
 * @since 1.0.0
 */
public class KeysCommand extends JedisDbCommand<List<String>> {
    private String pattern;
    /**
     * 命令：KEYS pattern
     * @param id
     */
    public KeysCommand(String id, int db,String pattern) {
        super(id,db);
        this.pattern = pattern;
    }

    @Override
    public String sendCommand() {
        return "KEYS "+pattern;
    }

    /**
     * 查找符合给定模式的key。
     * KEYS *命中数据库中所有key。
     * 时间复杂度：O(N)，N为数据库中key的数量。
     * @param redisLarkContext
     * @return 符合给定模式的key列表。
     */
    @Override
    public List<String> concreteCommand(RedisLarkContext redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        final List<String> response = redisLarkContext.keys(pattern);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }
}
