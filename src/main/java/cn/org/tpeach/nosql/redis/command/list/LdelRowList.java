/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.org.tpeach.nosql.redis.command.list;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;

/**
 * 先设置再搜索删除
 * @author taoyz
 */
public class LdelRowList extends JedisDbCommand{

    private int index;
    private String key;
    public LdelRowList(String id, int db,String key,int index) {
        super(id, db);
        this.index = index;
        this.key = key;
    }
    @Override
    public Object concreteCommand(RedisLarkContext redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        redisLarkContext.ldelRow(key, index);
        
        return null;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }
    
}
