package cn.org.tpeach.nosql.redis.command.server;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tyz
 * @Title: FlushDbCommand
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-08-27 08:48
 * @since 1.0.0
 */
public class FlushDbCommand extends JedisDbCommand<String> {
	final static Logger logger = LoggerFactory.getLogger(FlushDbCommand.class);
	/**
	 * 命令: FLUSHDB 
	 * @param id
	 * @param db
	 */
	public FlushDbCommand(String id, int db) {
		super(id, db);
	}

	@Override
	public String sendCommand() {
		return "FLUSHDB";
	}

	/**
	 * Redis Flushdb 命令用于清空当前数据库中的所有 key。
	 * @param redisLarkContext
	 * @return 给定配置参数的值。
	 */
	@Override
	public String concreteCommand(RedisLarkContext redisLarkContext) {
		super.concreteCommand(redisLarkContext);
		final String response = redisLarkContext.flushDB();
		return response;
	}

	@Override
	public RedisVersion getSupportVersion() {
		return RedisVersion.REDIS_1_0;
	}
}
