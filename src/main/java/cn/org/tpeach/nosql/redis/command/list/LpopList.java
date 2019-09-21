package cn.org.tpeach.nosql.redis.command.list;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tyz
 * @Title: LpopList
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-03 0:37
 * @since 1.0.0
 */
public class LpopList extends JedisDbCommand<String> {
    private static final Logger logger = LoggerFactory.getLogger(LpopList.class);
    private String key;

    /**
     * 命令：LPOP  key
     *
     * @param id
     * @param db
     * @param key
     */
    public LpopList(String id, int db, String key) {
        super(id, db);
        this.key = key;
    }

    @Override
    public String sendCommand() {
        return "LPOP "+key;
    }

    /**
     * 移除并返回列表key的头元素。
     * 时间复杂度：O(1)
     *
     * @param redisLarkContext
     * @return 列表的尾元素。当key不存在时，返回nil。
     */
    @Override
    public String concreteCommand(RedisLarkContext redisLarkContext) {
        super.concreteCommand(redisLarkContext);
        final String response = redisLarkContext.lpop(key);
        return response;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }
}
