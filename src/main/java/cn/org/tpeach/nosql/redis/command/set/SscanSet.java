/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.org.tpeach.nosql.redis.command.set;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.AbstractScanCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import io.lettuce.core.ScanCursor;
import io.lettuce.core.ValueScanCursor;

/**
 *
 * @author Administrator
 */
public class SscanSet extends AbstractScanCommand<ValueScanCursor<byte[]>> {


    public SscanSet(String id, int db, byte[] key, ScanCursor scanCursor, Integer count) {
        super(id, db, key, scanCursor, count);
    }


    @Override
    public String sendCommand() {
        return null;
    }

    @Override
    public ValueScanCursor<byte[]> concreteCommand(RedisLarkContext<byte[], byte[]> redisLarkContext) {
         super.concreteCommand(redisLarkContext);
         ValueScanCursor<byte[]> response = redisLarkContext.sscan(key, scanCursor, scanArgs);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }
    
}
