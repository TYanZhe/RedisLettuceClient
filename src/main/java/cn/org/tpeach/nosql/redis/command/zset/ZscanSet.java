/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.org.tpeach.nosql.redis.command.zset;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.AbstractScanCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.Tuple;

/**
 *
 * @author Administrator
 */
public class ZscanSet extends AbstractScanCommand<ScanResult<Tuple>> {


    public ZscanSet(String id, int db, String key, String cursor, Integer count) {
        super(id, db, key, cursor, count);
    }


    @Override
    public ScanResult<Tuple> concreteCommand(RedisLarkContext redisLarkContext) {
         super.concreteCommand(redisLarkContext);
        ScanResult<Tuple> response = redisLarkContext.zscan(key, cursor, scanParams);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }
    
}
