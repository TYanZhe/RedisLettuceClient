package cn.org.tpeach.nosql.redis.command.list;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author tyz
 * @Title: LrangeList
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-08 18:37
 * @since 1.0.0
 */
public class LrangeList extends JedisDbCommand<List<byte[]>> {
    @SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(LrangeList.class);
    private byte[] key;
    private long start;
    private long stop;

    /**
     * 命令：LRANGE key start stop
     * @param id
     * @param db
     * @param key
     * @param start
     * @param stop
     */
    public LrangeList(String id, int db, byte[] key, long start, long stop) {
        super(id, db);
        this.key = key;
        this.start = start;
        this.stop = stop;
    }

    @Override
    public String sendCommand() {
        return "LRANGE "+ byteToStr(key) +" "+ start + " "+stop;
    }
    /**
     * 返回列表key中指定区间内的元素，区间以偏移量start和stop指定。
     * 下标(index)参数start和stop都以0为底，也就是说，以0表示列表的第一个元素，以1表示列表的第二个元素，以此类推。
     * 你也可以使用负数下标，以-1表示列表的最后一个元素，-2表示列表的倒数第二个元素，以此类推。
     * 超出范围的下标值不会引起错误
     * 如果start下标比列表的最大下标end(LLEN list减去1)还要大，或者start > stop，LRANGE返回一个空列表。
     * 如果stop下标比end下标还要大，Redis将stop的值设置为end。
     * 时间复杂度：O(S+N)，S为偏移量start，N为指定区间内元素的数量。
     *
     * @param redisLarkContext
     * @return 一个列表，包含指定区间内的元素。
     */
    @Override
    public List<byte[]> concreteCommand(RedisLarkContext<byte[], byte[]> redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        final List<byte[]> response = redisLarkContext.lrange(key ,start, stop);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }
}
