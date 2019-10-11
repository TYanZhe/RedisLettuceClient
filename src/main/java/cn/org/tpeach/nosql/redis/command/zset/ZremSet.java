/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.org.tpeach.nosql.redis.command.zset;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author tyz
 * @Title: SaddSet
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-09-04 14:40
 * @since 1.0.0
 */
@Slf4j
public class ZremSet extends JedisDbCommand<Long>{
    private byte[] key;
    private byte[][] members;
       /**
     * 命令：ZREM key member [member ...]
     * @param id
     * @param db
     * @param key
     * @param members
     */
    public ZremSet(String id, int db,byte[] key,byte[]... members) {
        super(id, db);
        this.key = key;
        this.members = members;
    }
    @Override
    public String sendCommand() {
        return "ZREM "+byteToStr(key)+" "+ Arrays.stream(byteArrToStr(members)).collect(Collectors.joining(" "));
    }
    /**
     * 移除有序集key中的一个或多个成员，不存在的成员将被忽略。
     * 当key存在但不是有序集类型时，返回一个错误。
     * 时间复杂度:O(M*log(N))，N为有序集的基数，M为被成功移除的成员的数量。
     * @param redisLarkContext
     * @return 被成功移除的成员的数量，不包括被忽略的成员。
     */
    @Override
    public Long concreteCommand(RedisLarkContext<byte[], byte[]> redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        final Long response = redisLarkContext.zrem(key, members);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
         return RedisVersion.REDIS_1_0;
    }
    
}
