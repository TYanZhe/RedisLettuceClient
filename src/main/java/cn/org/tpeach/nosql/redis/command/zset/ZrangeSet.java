package cn.org.tpeach.nosql.redis.command.zset;

import java.util.List;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;

/**
 * @author tyz
 * @Title: ZrangeSet
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-09 10:43
 * @since 1.0.0
 */
public class ZrangeSet extends JedisDbCommand<List<byte[]>> {
    private byte[] key;
    private long start;
    private long stop;
    /**
     * 命令：ZRANGE key start stop [WITHSCORES]
     *
     * @param id
     * @param db
     * @param key
     */
    public ZrangeSet(String id, int db, byte[] key, long start, long stop) {
        super(id, db);
        this.key = key;
        this.start = start;
        this.stop = stop;
    }
    @Override
    public String sendCommand() {
        return "ZRANGE "+byteToStr(key)+" "+start+" "+stop;
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
    public List<byte[]> concreteCommand(RedisLarkContext<byte[], byte[]> redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        final List<byte[]> response = redisLarkContext.zrange(key,start,stop);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }


}