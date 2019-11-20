/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.org.tpeach.nosql.redis.command.set;

import java.util.Arrays;
import java.util.stream.Collectors;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;

/**
 * @author tyz
 * @Title: SaddSet
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-09-04 14:40
 * @since 1.0.0
 */

public class SremSet extends JedisDbCommand<Long>{
    private byte[] key;
    private byte[][] members;
       /**
     * 命令：SREM key member [member ...]
     * @param id
     * @param db
     * @param key
     * @param members
     */
    public SremSet(String id, int db,byte[] key,byte[]... members) {
        super(id, db);
        this.key = key;
        this.members = members;
    }
    @Override
    public String sendCommand() {
        return "SREM "+byteToStr(key)+" "+ Arrays.stream(byteArrToStr(members)).collect(Collectors.joining(" "));
    }
    /**
     * 移除集合key中的一个或多个member元素，不存在的member元素会被忽略。
     * 当key不是集合类型，返回一个错误。
     * 时间复杂度:O(N)，N为给定member元素的数量。
     * @param redisLarkContext
     * @return 被成功移除的元素的数量，不包括被忽略的元素。
     */
    @Override
    public Long concreteCommand(RedisLarkContext<byte[], byte[]> redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        final Long response = redisLarkContext.srem(key, members);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
         return RedisVersion.REDIS_1_0;
    }
    
}
