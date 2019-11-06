package cn.org.tpeach.nosql.redis.command.key;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import io.lettuce.core.ScanIterator;
import lombok.extern.slf4j.Slf4j;

/**
 * @author tyz
 * @Title: ScanIteratorCommand
 * @ProjectName redisLark-Github
 * @Description: TODO
 * @date 2019-09-13 23:38
 * @since 1.0.0
 */
@Slf4j
public class ScanIteratorCommand extends JedisCommand<ScanIterator<byte[]>> {
    private int count;
    private String pattern;

    public ScanIteratorCommand(String id, int count, String pattern) {
        super(id);
        this.count = count;
        this.pattern = pattern;
    }

    @Override
    public String sendCommand() {
        return "SCAN ... MATCH "+pattern+" COUNT "+count;
    }

    @Override
    public ScanIterator<byte[]> concreteCommand(RedisLarkContext<byte[], byte[]> redisLarkContext) {
        final ScanIterator<byte[]> response = redisLarkContext.scanIterator(count, pattern);
        return response;
    }

    public void match(String pattern){
        this.pattern = pattern;
    }

    public void count(Integer count){
        this.count = count;
    }
    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }
}
