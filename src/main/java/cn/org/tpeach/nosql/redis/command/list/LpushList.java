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
public class LpushList extends JedisDbCommand<Long> {
    private static final Logger logger = LoggerFactory.getLogger(LpushList.class);
    private String key;
    private String[] values;

    /**
     * 命令：LPUSH key value [value ...]
     *
     * @param id
     * @param db
     * @param key
     * @param values
     */
    public LpushList(String id, int db, String key, String... values) {
        super(id, db);
        this.key = key;
        this.values = values;
    }

    @Override
    public String sendCommand() {
        return "LPUSH "+ key +" "+ Arrays.stream(values).collect(Collectors.joining(" "));
    }

    /**
     * 将一个或多个值value插入到列表key的表头。
     * 如果有多个value值，那么各个value值按从左到右的顺序依次插入到表头：
     * 比如对一个空列表(mylist)执行LPUSH mylist a b c，则结果列表为c b a，等同于执行执行命令LPUSH mylist a、LPUSH mylist b、LPUSH mylist c。
     * 如果key不存在，一个空列表会被创建并执行LPUSH操作。
     * 当key存在但不是列表类型时，返回一个错误。
     * 时间复杂度：O(1)
     *
     * @param redisLarkContext
     * @return 执行LPUSH命令后，列表的长度。
     */
    @Override
    public Long concreteCommand(RedisLarkContext redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        final Long response = redisLarkContext.lpush(key, values);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }
}
