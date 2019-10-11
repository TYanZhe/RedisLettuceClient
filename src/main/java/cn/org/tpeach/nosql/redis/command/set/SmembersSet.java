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
public class SmembersSet extends JedisDbCommand<Set<byte[]>> {
    private static final Logger logger = LoggerFactory.getLogger(SmembersSet.class);
    private byte[] key;

    /**
     * 命令：SMEMBERS key
     *
     * @param id
     * @param db
     * @param key
     */
    public SmembersSet(String id, int db, byte[] key) {
        super(id, db);
        this.key = key;
    }
    @Override
    public String sendCommand() {
        return "SMEMBERS "+byteToStr(key);
    }
    /**
     * 返回集合key中的所有成员。
     * 时间复杂度 O(N)，N为集合的基数。
     * @param redisLarkContext
     * @return 集合中的所有成员。
     */
    @Override
    public Set<byte[]> concreteCommand(RedisLarkContext<byte[], byte[]> redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        logger.info("[runCommand] SMEMBERS  {}", key);
        final Set<byte[]> response = redisLarkContext.smembers(key);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }


}