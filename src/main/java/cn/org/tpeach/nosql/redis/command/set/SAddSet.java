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
 * @date 2019-07-03 0:40
 * @since 1.0.0
 */
public class SAddSet extends JedisDbCommand<Long> {
    private byte[] key;
    private byte[][] members;

    /**
     * 命令：SADD key member [member ...]
     * @param id
     * @param db
     * @param key
     * @param members
     */
    public SAddSet(String id, int db, byte[] key, byte[]... members) {
        super(id, db);
        this.key = key;
        this.members = members;
    }

    @Override
    public String sendCommand() {
        return "SADD "+byteToStr(key)+" "+ Arrays.stream(byteArrToStr(members)).collect(Collectors.joining(" "));
    }

    /**
     * 将一个或多个member元素加入到集合key当中，已经存在于集合的member元素将被忽略。
     * 假如key不存在，则创建一个只包含member元素作成员的集合。
     * 当key不是集合类型时，返回一个错误。
     * 时间复杂度：O(N)，N是被添加的元素的数量。
     *
     * @param redisLarkContext
     * @return 被添加到集合中的新元素的数量，不包括被忽略的元素。
     */
    @Override
    public Long concreteCommand(RedisLarkContext<byte[], byte[]> redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        final Long response = redisLarkContext.sadd(key, members);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }


}
