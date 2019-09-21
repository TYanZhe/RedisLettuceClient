package cn.org.tpeach.nosql.redis.command.key;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisCommand;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import cn.org.tpeach.nosql.view.RedisMainWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author tyz
 * @Title: DelKeysCommand
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-06 0:26
 * @since 1.0.0
 */
public class DelKeysCommand extends JedisDbCommand<Long> {
    private static final Logger logger = LoggerFactory.getLogger(DelKeysCommand.class);
    private String[] keys;
    /**
     * DEL key [key ...]
     * @param id
     */
    public DelKeysCommand(String id, int db, String... keys) {
        super(id,db);
        this.keys = keys;
    }

    @Override
    public String sendCommand() {
        return "DEL "+ Arrays.stream(keys).collect(Collectors.joining(" "));
    }

    /**
     *移除给定的一个或多个key。
     * 如果key不存在，则忽略该命令。
     * 时间复杂度：O(N)，N为要移除的key的数量。
     * 移除单个字符串类型的key，时间复杂度为O(1)。移除单个列表、集合、有序集合或哈希表类型的key，时间复杂度为O(M)，M为以上数据结构内的元素数量。
     * @param redisLarkContext
     * @return 被移除key的数量。
     */
    @Override
    public Long concreteCommand(RedisLarkContext redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        final Long response = redisLarkContext.del(keys);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }
}
