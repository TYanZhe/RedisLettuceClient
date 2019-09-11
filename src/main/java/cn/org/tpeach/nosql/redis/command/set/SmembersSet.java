package cn.org.tpeach.nosql.redis.command.set;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * @author tyz
 * @Title: SmembersSet
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-09 10:43
 * @since 1.0.0
 */
public class SmembersSet extends JedisDbCommand<Set<String>> {
    private static final Logger logger = LoggerFactory.getLogger(SmembersSet.class);
    private String key;

    /**
     * 命令：SMEMBERS key
     *
     * @param id
     * @param db
     * @param key
     */
    public SmembersSet(String id, int db, String key) {
        super(id, db);
        this.key = key;
    }

    /**
     * 返回集合key中的所有成员。
     * 时间复杂度 O(N)，N为集合的基数。
     * @param redisLarkContext
     * @return 集合中的所有成员。
     */
    @Override
    public Set<String> concreteCommand(RedisLarkContext redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        logger.info("[runCommand] SMEMBERS  {}", key);
        final Set<String> response = redisLarkContext.smembers(key);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }


}