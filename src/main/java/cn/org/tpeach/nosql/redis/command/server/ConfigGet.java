package cn.org.tpeach.nosql.redis.command.server;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import cn.org.tpeach.nosql.redis.command.string.GetString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author tyz
 * @Title: ConfigGet
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-03 08:48
 * @since 1.0.0
 */
public class ConfigGet extends JedisCommand<Map<String, String>> {
	final static Logger logger = LoggerFactory.getLogger(GetString.class);
	private String pattern;
	/**
	 * 命令: CONFIG GET parameter
	 * @param id
	 * @param pattern
	 */
	public ConfigGet(String id,  String pattern) {
		super(id);
		this.pattern = pattern;

	}

	@Override
	public String sendCommand() {
		return "CONFIG GET "+pattern;
	}

	/**
	 * CONFIG GET 命令用于取得运行中的 Redis 服务器的配置参数(configuration parameters)，
	 * 不过并非所有配置参数都被 CONFIG GET 命令所支持
	 * CONFIG GET 接受单个参数 parameter 作为搜索关键字，查找所有匹配的配置参数，
	 * 其中参数和值以“键-值对”(key-value pairs)的方式排列。
	 * 比如执行 CONFIG GET s* 命令，服务器就会返回所有以 s 开头的配置参数及参数的值
	 * @param redisLarkContext
	 * @return 给定配置参数的值。
	 */
	@Override
	public Map<String, String> concreteCommand(RedisLarkContext<byte[], byte[]> redisLarkContext) {
		final Map<String, String> response = redisLarkContext.configGet(pattern);
		return response;
	}

	@Override
	public RedisVersion getSupportVersion() {
		return RedisVersion.REDIS_2_0;
	}
}
