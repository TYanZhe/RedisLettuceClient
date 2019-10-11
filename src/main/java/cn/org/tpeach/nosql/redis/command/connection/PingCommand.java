package cn.org.tpeach.nosql.redis.command.connection;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;

/**
 * @author tyz
 * @Title: PingCommand
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-03 0:26
 * @since 1.0.0
 */
public class PingCommand extends JedisCommand<String> {
    /**
     * 命令：PING
     * @param id
     */
    public PingCommand(String id) {
        super(id);
    }

    @Override
    public String sendCommand() {
        return "PING";
    }

    /**
     * 客户端向服务器发送一个 PING ，然后服务器返回客户端一个 PONG 。
     *
     * 通常用于测试与服务器的连接是否仍然生效，或者用于测量延迟值。
     * 时间复杂度：O(1)
     * @param redisLarkContext
     * @return PONG
     */
    @Override
    public String concreteCommand(RedisLarkContext<byte[], byte[]> redisLarkContext) {
        return redisLarkContext.ping();
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }
}
