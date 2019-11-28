package cn.org.tpeach.nosql.redis.command.pubsub;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;

import java.util.Arrays;
import java.util.stream.Collectors;

public class PunsubscribeCommand extends JedisCommand<Void> {
    private byte[][] pattern;

    /**
     * 命令：PUNSUBSCRIBE [pattern [pattern ...]]
     * @param id
     * @param pattern
     */
    public PunsubscribeCommand(String id, byte[]... pattern) {
        super(id);
        this.pattern = pattern;
    }
    @Override
    public String sendCommand() {
        return "PUNSUBSCRIBE "+  Arrays.stream(byteArrToStr(pattern)).collect(Collectors.joining(" "));
    }

    /**
     * 指示客户端退订所有给定模式。
     * 如果没有模式被指定，也即是，一个无参数的 PUNSUBSCRIBE 调用被执行，那么客户端使用 PSUBSCRIBE 命令订阅的所有模式都会被退订。
     * 在这种情况下，命令会返回一个信息，告知客户端所有被退订的模式。
     * 时间复杂度：
     *     O(N+M) ，其中 N 是客户端已订阅的模式的数量， M 则是系统中所有客户端订阅的模式的数量。
     * @param redisLarkContext
     * @return 这个命令在不同的客户端中有不同的表现。
     */
    @Override
    public Void concreteCommand(RedisLarkContext<byte[], byte[]> redisLarkContext) {
        redisLarkContext.punsubscribe(pattern);
        return null;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_2_0;
    }
}
