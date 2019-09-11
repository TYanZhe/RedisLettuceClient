package cn.org.tpeach.nosql.redis.command.server;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import cn.org.tpeach.nosql.redis.command.string.GetString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author tyz
 * @Title: DbSizeCommand
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-03 08:48
 * @since 1.0.0
 */
public class DbSizeCommand extends JedisCommand<Long> {
	final static Logger logger = LoggerFactory.getLogger(DbSizeCommand.class);
	private int db;
	/**
	 * 命令: CONFIG GET parameter
	 * @param id
	 * @param db
	 */
	public DbSizeCommand(String id, int db) {
		super(id);
		this.db = db;

	}
	/**
	 * 返回当前数据库的 key 的数量。
	 * 时间复杂度：O(1)
	 * @return 当前数据库的 key 的数量。
	 */
	@Override
	public Long concreteCommand(RedisLarkContext redisLarkContext) {
		redisLarkContext.select(db);
		final Long response = redisLarkContext.dbSize();
		return response;
	}

	@Override
	public RedisVersion getSupportVersion() {
		return RedisVersion.REDIS_2_0;
	}
}
