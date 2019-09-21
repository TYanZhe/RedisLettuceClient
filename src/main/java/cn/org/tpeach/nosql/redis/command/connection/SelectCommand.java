package cn.org.tpeach.nosql.redis.command.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;

/**
 * @author tyz
 * @Title: SelectCommand
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-03 0:26
 * @since 1.0.0
 */
public class SelectCommand extends JedisCommand<String> {
    private int db;
    public int getDb() {
        return db;
    }

    public void setDb(int db) {
        this.db = db;
    }

    /**
     * 命令：SELECT index
     *
     * @param id
     * @param db
     */
    public SelectCommand(String id, int db) {
        super(id);
        this.db = db;
    }

    @Override
    public String sendCommand() {
        return "SELECT "+db;
    }

    /**
     *切换到指定的数据库，数据库索引号用数字值指定，以 0 作为起始索引值。
     *
     *新的链接总是使用 0 号数据库。
     *
     * @param redisLarkContext
     * @return OK
     */
    @Override
    public String concreteCommand(RedisLarkContext redisLarkContext) {
        return redisLarkContext.select(db);
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_1_0;
    }
}
