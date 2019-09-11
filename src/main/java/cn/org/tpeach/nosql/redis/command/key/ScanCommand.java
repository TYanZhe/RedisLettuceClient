package cn.org.tpeach.nosql.redis.command.key;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.AbstractScanCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import redis.clients.jedis.ScanResult;

public class ScanCommand extends AbstractScanCommand<ScanResult<String>> {

    public ScanCommand(String id, int db, String cursor, Integer count) {
        super(id, db, cursor, count);
    }
    @Override
    public ScanResult<String> concreteCommand(RedisLarkContext redisLarkContext) {
         super.concreteCommand(redisLarkContext);
        ScanResult<String> response = redisLarkContext.scan(cursor, scanParams);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }
}
