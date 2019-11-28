package cn.org.tpeach.nosql.redis.command.pubsub;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;

public class PublishCommand extends JedisCommand<Long> {
    private byte[] channel;
    private byte[] message;

    /**
     * 命令：PUBLISH channel message
     * @param id
     * @param channel
     * @param message
     */
    public PublishCommand(String id, byte[] channel, byte[] message) {
        super(id);
        this.channel = channel;
        this.message = message;
    }

    @Override
    public String sendCommand() {
        return "PUBLISH " + byteToStr(channel) + " "+byteToStr(message);
    }

    /**
     * 时间复杂度：
     *     O(N+M)，其中 N 是频道 channel 的订阅者数量，而 M 则是使用模式订阅(subscribed patterns)的客户端的数量。
     * @param redisLarkContext
     * @return 接收到信息 message 的订阅者数量。
     */
    @Override
    public Long concreteCommand(RedisLarkContext<byte[], byte[]> redisLarkContext) {
        Long publish = redisLarkContext.publish(channel, message);
        return publish;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_2_0;
    }
}
