package cn.org.tpeach.nosql.redis.command.zset;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import io.lettuce.core.ScoredValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
    private long min;
    private long max;
    /**
     * 命令：ZRANGE key start stop [WITHSCORES]
     *
     * @param id
     * @param db
     * @param key
     */
    public ZrangeWithScoresSet(String id, int db, String key, long min , long max) {
        super(id, db);
        this.key = key;
        this.min = min;
        this.max = max;
    }
    @Override
    public String sendCommand() {
        return "ZRANGEBYSCORE "+key+" "+min+" "+max;
    }
    /**
     返回有序集key中，所有score值介于min和max之间(包括等于min或max)的成员。有序集成员按score值递增(从小到大)次序排列。

     具有相同score值的成员按字典序(lexicographical order)来排列(该属性是有序集提供的，不需要额外的计算)。

     可选的LIMIT参数指定返回结果的数量及区间(就像SQL中的SELECT LIMIT offset, count)，注意当offset很大时，定位offset的操作可能需要遍历整个有序集，此过程最坏复杂度为O(N)时间。

     可选的WITHSCORES参数决定结果集是单单返回有序集的成员，还是将有序集成员及其score值一起返回。
     该选项自Redis 2.0版本起可用。
     时间复杂度: O(log(N)+M)，N为有序集的基数，M为被结果集的基数。

     * @param redisLarkContext
     * @return 指定区间内，带有score值(可选)的有序集成员的列表。
     */
    @Override
    public List<ScoredValue<String>> concreteCommand(RedisLarkContext redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        final List<ScoredValue<String>> response = redisLarkContext.zrangeWithScores(key,min,max);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_2_0;
    }


}