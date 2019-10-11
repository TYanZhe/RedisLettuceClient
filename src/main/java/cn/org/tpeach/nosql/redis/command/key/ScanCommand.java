package cn.org.tpeach.nosql.redis.command.key;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.AbstractScanCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import io.lettuce.core.KeyScanCursor;
import io.lettuce.core.ScanCursor;

public class ScanCommand extends AbstractScanCommand<KeyScanCursor<byte[]>> {

    public ScanCommand(String id, int db, ScanCursor scanCursor, Integer count) {
        super(id, db, scanCursor, count);
    }

    @Override
    public String sendCommand() {
        return "SCAN ...";
    }

    @Override
    public KeyScanCursor<byte[]> concreteCommand(RedisLarkContext<byte[], byte[]> redisLarkContext) {
         super.concreteCommand(redisLarkContext);
         KeyScanCursor<byte[]> response = redisLarkContext.scan(scanCursor, scanArgs);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }
}
