package cn.org.tpeach.nosql.redis.command.key;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;

import java.util.List;

/**
 * @author tyz
 * @Title: KeysCommand
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-03 0:26
 * @since 1.0.0
 */
public class KeysCommand extends JedisDbCommand<List<byte[]>> {
    private byte[] pattern;
    /**
     * 命令：KEYS pattern
     * @param id
     */
    public KeysCommand(String id, int db,byte[] pattern) {
        super(id,db);
        this.pattern = pattern;
    }

    @Override
    public String sendCommand() {
        return "KEYS "+byteToStr(pattern);
    }

    /**
     * 查找符合给定模式的key。
     * KEYS *命中数据库中所有key。
     * 时间复杂度：O(N)，N为数据库中key的数量。
     * @param redisLarkContext
     * @return 符合给定模式的key列表。
     */
    @Override
    public List<byte[]> concreteCommand(RedisLarkContext<byte[], byte[]> redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        final List<byte[]> response = redisLarkContext.keys(pattern);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }
}
