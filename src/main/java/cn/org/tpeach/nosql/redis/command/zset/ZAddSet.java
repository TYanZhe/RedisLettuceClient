package cn.org.tpeach.nosql.redis.command.zset;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import cn.org.tpeach.nosql.redis.command.set.SAddSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tyz
 * @Title: ZAddSet
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-03 0:44
 * @since 1.0.0
 */
public class ZAddSet extends JedisDbCommand<Long> {
    private static final Logger logger = LoggerFactory.getLogger(ZAddSet.class);
    private String key;
    private double score;
    private String members;

    /**
     * 命令：ZADD key score member [[score member] [score member] ...]
     * @param id
     * @param db
     * @param key
     * @param score
     * @param members
     */
    public ZAddSet(String id, int db, String key, double score, String members) {
        super(id, db);
        this.key = key;
        this.score = score;
        this.members = members;
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
    public Long concreteCommand(RedisLarkContext redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        logger.info("[runCommand] ZADD {} {} {}", key,score, members);
        final Long response = redisLarkContext.zadd(key,score, members);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }


}
