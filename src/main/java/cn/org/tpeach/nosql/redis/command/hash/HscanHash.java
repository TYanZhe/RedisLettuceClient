package cn.org.tpeach.nosql.redis.command.hash;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.AbstractScanCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import io.lettuce.core.MapScanCursor;
import io.lettuce.core.ScanCursor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HscanHash extends AbstractScanCommand<MapScanCursor<byte[], byte[]>> {
    /**
     * hscan key cursor match pattern COUNT count
     * @param id
     * @param db
     * @param key
     * @param scanCursor
     * @param count
     */
    public HscanHash(String id, int db, byte[] key, ScanCursor scanCursor, Integer count) {
        super(id, db, key, scanCursor, count);
    }

    @Override
    public String sendCommand() {
        return "hscan "+ byteToStr(key);
    }

    @Override
    public MapScanCursor<byte[], byte[]> concreteCommand(RedisLarkContext<byte[], byte[]> redisLarkContext) {

        super.concreteCommand(redisLarkContext);
        // 游标初始值为0
//        String cursor = ScanParams.SCAN_POINTER_START;
        MapScanCursor<byte[], byte[]> response = redisLarkContext.hscan(key, scanCursor, scanArgs);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }
}
