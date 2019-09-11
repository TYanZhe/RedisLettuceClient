/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.org.tpeach.nosql.redis.command.set;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.AbstractScanCommand;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import redis.clients.jedis.ScanResult;

/**
 *
 * @author Administrator
 */
public class SscanSet extends AbstractScanCommand<ScanResult<String>> {


    public SscanSet(String id, int db, String key, String cursor, Integer count) {
        super(id, db, key, cursor, count);
    }


    @Override
    public ScanResult<String> concreteCommand(RedisLarkContext redisLarkContext) {
         super.concreteCommand(redisLarkContext);
        ScanResult<String> response = redisLarkContext.sscan(key, cursor, scanParams);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }
    
}
