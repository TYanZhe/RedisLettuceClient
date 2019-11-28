package cn.org.tpeach.nosql.redis.command.pubsub;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;

import java.util.Arrays;
import java.util.stream.Collectors;

public class PsubscribeCommand extends JedisCommand<Void> {
    private byte[][] pattern;

    /**
     * 命令：PSUBSCRIBE pattern [pattern ...]
     * @param id
     * @param pattern
     */
    public PsubscribeCommand(String id, byte[]... pattern) {
        super(id);
        this.pattern = pattern;
    }
    @Override
    public String sendCommand() {
        return "PSUBSCRIBE "+  Arrays.stream(byteArrToStr(pattern)).collect(Collectors.joining(" "));
    }

    /**
     * 订阅一个或多个符合给定模式的频道。
     * 每个模式以 * 作为匹配符，比如 it* 匹配所有以 it 开头的频道( it.news 、 it.blog 、 it.tweets 等等)，
     * news.* 匹配所有以 news. 开头的频道( news.it 、 news.global.today 等等)，诸如此类
     * 时间复杂度：
     *     O(N)， N 是订阅的模式的数量。
     * @param redisLarkContext
     * @return 接收到的信息
     * 1) "psubscribe"                  # 返回值的类型：显示订阅成功
     * 2) "news.*"                      # 订阅的模式
     * 3) (integer) 1                   # 目前已订阅的模式的数量。
     */
    @Override
    public Void concreteCommand(RedisLarkContext<byte[], byte[]> redisLarkContext) {
        redisLarkContext.psubscribe(pattern);
        return null;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_2_0;
    }
}
