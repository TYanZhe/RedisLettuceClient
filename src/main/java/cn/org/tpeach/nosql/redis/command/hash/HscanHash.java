package cn.org.tpeach.nosql.redis.command.hash;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.AbstractScanCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.ScanResult;

import java.util.Map;

@Slf4j
public class HscanHash extends AbstractScanCommand<ScanResult<Map.Entry<String, String>>> {
    /**
     * hscan key cursor match pattern COUNT count
     * @param id
     * @param db
     * @param key
     * @param cursor
     * @param count
     */
    public HscanHash(String id, int db, String key, String cursor, Integer count) {
        super(id, db, key, cursor, count);
        log.info("cursor"+cursor+",count"+count);
    }

    @Override
    public ScanResult<Map.Entry<String, String>> concreteCommand(RedisLarkContext redisLarkContext) {

        super.concreteCommand(redisLarkContext);
        // 游标初始值为0
//        String cursor = ScanParams.SCAN_POINTER_START;
        ScanResult<Map.Entry<String, String>> response = redisLarkContext.hscan(key, cursor, scanParams);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }
}
