package cn.org.tpeach.nosql.redis.command.list;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author tyz
 * @Title: LpushList
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-03 0:36
 * @since 1.0.0
 */
public class RpushxList  extends JedisDbCommand<Long> {
    private static final Logger logger = LoggerFactory.getLogger(RpushxList.class);
    private String key;
    private String[] values;

    /**
     * 命令：RPUSHX key value
     *
     * @param id
     * @param db
     * @param key
     * @param values
     */
    public RpushxList(String id, int db, String key, String[] values) {
        super(id, db);
        this.key = key;
        this.values = values;
    }
    @Override
    public String sendCommand() {
        return "RPUSHX "+ key +" "+ Arrays.stream(values).collect(Collectors.joining(" "));
    }
    /**
     * 将值value插入到列表key的表尾，当且仅当key存在并且是一个列表。
     * 和RPUSH命令相反，当key不存在时，RPUSHX命令什么也不做。
     * 时间复杂度：O(1)
     *
     * @param redisLarkContext
     * @return 执行RPUSHX操作后，表的长度。
     */
    @Override
    public Long concreteCommand(RedisLarkContext redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        final Long response = redisLarkContext.rpushx(key, values);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }
}