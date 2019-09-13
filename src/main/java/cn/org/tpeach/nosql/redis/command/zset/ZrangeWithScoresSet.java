package cn.org.tpeach.nosql.redis.command.zset;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import io.lettuce.core.ScoredValue;

/**
 * @author tyz
 * @Title: ZrangeWithScoresSet
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-09 10:43
 * @since 1.0.0
 */
public class ZrangeWithScoresSet extends JedisDbCommand<List<ScoredValue<String>>> {
    private static final Logger logger = LoggerFactory.getLogger(ZrangeWithScoresSet.class);
    private String key;
    private long start;
    private long stop;
    /**
     * 命令：ZRANGE key start stop [WITHSCORES]
     *
     * @param id
     * @param db
     * @param key
     */
    public ZrangeWithScoresSet(String id, int db, String key, long start, long stop) {
        super(id, db);
        this.key = key;
        this.start = start;
        this.stop = stop;
    }

    /**
     * 返回有序集key中，指定区间内的成员。
     * 其中成员的位置按score值递减(从大到小)来排列。
     * 具有相同score值的成员按字典序的反序(reverse lexicographical order)排列。
     * 除了成员按score值递减的次序排列这一点外，ZREVRANGE命令的其他方面和ZRANGE命令一样。
     * 时间复杂度: O(log(N)+M)，N为有序集的基数，而M为结果集的基数。
     * @param redisLarkContext
     * @return 指定区间内，带有score值(可选)的有序集成员的列表。
     */
    @Override
    public List<ScoredValue<String>> concreteCommand(RedisLarkContext redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        logger.info("[runCommand] ZRANGE  {} {} {}", key,start,stop);
        final List<ScoredValue<String>> response = redisLarkContext.zrangeWithScores(key,start,stop);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }


}