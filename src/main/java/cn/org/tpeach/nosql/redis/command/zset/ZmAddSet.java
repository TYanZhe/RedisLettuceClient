package cn.org.tpeach.nosql.redis.command.zset;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import cn.org.tpeach.nosql.tools.ArraysUtil;
import io.lettuce.core.ScoredValue;

/**
 * @author tyz
 * @Title: ZmAddSet
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-09-19 13:52
 * @since 1.0.1
 */
public class ZmAddSet extends JedisDbCommand<Long> {
    private byte[] key;
    private  ScoredValue<byte[]>[] scoredValues;

    /**
     * 命令：ZADD key score member [[score member] [score member] ...]
     * @param id
     * @param db
     * @param key
     * @param scoredValues
     */
    @SuppressWarnings("unchecked")
	public ZmAddSet(String id, int db, byte[] key, ScoredValue<byte[]> ... scoredValues) {
        super(id, db);
        this.key = key;
        this.scoredValues = scoredValues;
    }

    @Override
    public String sendCommand() {
        StringBuffer sb = new StringBuffer();
        if(!ArraysUtil.isEmpty(scoredValues)){
            for (ScoredValue<byte[]> scoredValue : scoredValues) {
                sb.append(" ");
                sb.append(scoredValue.getScore());
                sb.append(" ");
                sb.append(byteToStr(scoredValue.getValue()));
            }
        }
        return "ZADD "+ key  + sb.toString();
    }

    /**
     * 将一个或多个member元素及其score值加入到有序集key当中。
     * 如果某个member已经是有序集的成员，那么更新这个member的score值，并通过重新插入这个member元素，来保证该member在正确的位置上。
     * score值可以是整数值或双精度浮点数。
     * 如果key不存在，则创建一个空的有序集并执行ZADD操作。
     * 当key存在但不是有序集类型时，返回一个错误
     * 时间复杂度：O(M*log(N))，N是有序集的基数，M为成功添加的新成员的数量。
     * 在Redis2.4版本以前，ZADD每次只能添加一个元素。
     * @param redisLarkContext
     * @return 被成功添加的新成员的数量，不包括那些被更新的、已经存在的成员。
     */
    @Override
    public Long concreteCommand(RedisLarkContext<byte[], byte[]> redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        final Long response = redisLarkContext.zadd(key,scoredValues);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_2_4;
    }


}
