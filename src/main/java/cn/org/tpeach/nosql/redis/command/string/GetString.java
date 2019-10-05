package cn.org.tpeach.nosql.redis.command.string;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tyz
 * @Title: GetString
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-06-25 23:48
 * @since 1.0.0
 */
public class GetString extends JedisDbCommand<String> {
	final static Logger logger = LoggerFactory.getLogger(GetString.class);
	private String key;
	/**
	 * 命令: GET key
	 * @param id
	 * @param db
	 * @param key
	 */
	public GetString(String id, int db, String key) {
		super(id,db);
		this.key = key;

	}
	@Override
	public String sendCommand() {
		return "GET "+key  ;
	}
	/**
	 * 返回key所关联的字符串值。
	 * 如果key不存在则返回特殊值nil。
	 * 假如key储存的值不是字符串类型，返回一个错误，因为GET只能用于处理字符串值。
	 * 时间复杂度：O(1)
	 * @param redisLarkContext
	 * @return key的值。如果key不存在，返回nil。
	 */
	@Override
	public String concreteCommand(RedisLarkContext redisLarkContext) {
		super.concreteCommand(redisLarkContext);
		final String response = redisLarkContext.get(key);
		return response;
	}

	@Override
	public RedisVersion getSupportVersion() {
		return RedisVersion.REDIS_1_0;
	}
}
