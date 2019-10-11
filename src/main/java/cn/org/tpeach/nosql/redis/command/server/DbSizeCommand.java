package cn.org.tpeach.nosql.redis.command.server;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tyz
 * @Title: DbSizeCommand
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-03 08:48
 * @since 1.0.0
 */
public class DbSizeCommand extends JedisDbCommand<Long> {
	final static Logger logger = LoggerFactory.getLogger(DbSizeCommand.class);
	/**
	 * 命令: DBSIZE
	 * @param id
	 */
	public DbSizeCommand(String id, int db) {
		super(id,db);
	}

	@Override
	public String sendCommand() {
		return "DBSIZE";
	}

	/**
	 * 返回当前数据库的 key 的数量。
	 * 时间复杂度：O(1)
	 * @return 当前数据库的 key 的数量。
	 */
	@Override
	public Long concreteCommand(RedisLarkContext<byte[], byte[]> redisLarkContext) {
		super.concreteCommand(redisLarkContext);
		final Long response = redisLarkContext.dbSize();
		return response;
	}

	@Override
	public RedisVersion getSupportVersion() {
		return RedisVersion.REDIS_2_0;
	}
}
