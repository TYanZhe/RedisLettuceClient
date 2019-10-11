package cn.org.tpeach.nosql.redis.command.list;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tyz
 * @Title: LpopList
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-03 0:37
 * @since 1.0.0
 */
public class RpopList extends JedisDbCommand<byte[]> {
    private static final Logger logger = LoggerFactory.getLogger(RpopList.class);
    private byte[] key;

    /**
     * 命令：RPOP key
     *
     * @param id
     * @param db
     * @param key
     */
    public RpopList(String id, int db, byte[] key) {
        super(id, db);
        this.key = key;
    }

    @Override
    public String sendCommand() {
        return "RPOP "+ byteToStr(key)  ;
    }
    /**
     * 移除并返回列表key的尾元素。
     * 时间复杂度：O(1)
     *
     * @param redisLarkContext
     * @return 列表的尾元素。当key不存在时，返回nil。
     */
    @Override
    public byte[] concreteCommand(RedisLarkContext<byte[], byte[]> redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        final byte[] response = redisLarkContext.rpop(key);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }
}
