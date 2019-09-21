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
public class ScanIteratorCommand extends JedisCommand<ScanIterator<String>> {
    private int count;
    private String pattern;

    public ScanIteratorCommand(String id, int count, String pattern) {
        super(id);
        this.count = count;
        this.pattern = pattern;
    }

    @Override
    public String sendCommand() {
        return "SCAN ...";
    }

    @Override
    public ScanIterator<String> concreteCommand(RedisLarkContext redisLarkContext) {
        final ScanIterator<String> response = redisLarkContext.scanIterator(count, pattern);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }
}
