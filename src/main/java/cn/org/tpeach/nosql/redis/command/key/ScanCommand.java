package cn.org.tpeach.nosql.redis.command.key;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.AbstractScanCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import io.lettuce.core.KeyScanCursor;
import io.lettuce.core.ScanCursor;

public class ScanCommand extends AbstractScanCommand<KeyScanCursor<String>> {

    public ScanCommand(String id, int db, ScanCursor scanCursor, Integer count) {
        super(id, db, scanCursor, count);
    }
    @Override
    public KeyScanCursor<String> concreteCommand(RedisLarkContext redisLarkContext) {
         super.concreteCommand(redisLarkContext);
         KeyScanCursor<String> response = redisLarkContext.scan(scanCursor, scanArgs);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }
}
