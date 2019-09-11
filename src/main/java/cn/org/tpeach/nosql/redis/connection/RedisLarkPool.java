package cn.org.tpeach.nosql.redis.connection;

import java.util.HashMap;
import java.util.Map;

import cn.org.tpeach.nosql.redis.bean.RedisConnectInfo;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import cn.org.tpeach.nosql.tools.StringUtils;

/**
 * @author tyz
 * @Title: RedisLarkPool
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-06-23 20:17
 * @since 1.0.0
 */
public class RedisLarkPool {
	private static Map<String, RedisLarkContext> pool = new HashMap<>();
	private static Map<String, RedisConnectInfo> redisConnectInfo = new HashMap<>();

	public static RedisLarkContext getRedisLarkContext(String id) {
		return pool.get(id);
	}

	public static void destory(String id) {
		final RedisLarkContext remove = pool.remove(id);
		if(remove != null){
			remove.close();
		}

	}

	public static boolean addRedisLarkContext(String id, RedisLarkContext redisLark) {
		if (redisLark == null || StringUtils.isBlank(id)) {
			throw new RuntimeException("redisLark或者id不能为空");
		}
		pool.put(id, redisLark);
		return true;
	}

	public static void addOrUpdateConnectInfo(RedisConnectInfo connectInfo) {
		if (connectInfo == null || StringUtils.isBlank(connectInfo.getId())) {
			throw new RuntimeException("connectInfo不能为空");
		}
		redisConnectInfo.put(connectInfo.getId(), connectInfo);
	}

	public static RedisConnectInfo getConnectInfo(String id) {
		return redisConnectInfo.get(id);
	}
	
	public static RedisConnectInfo deleteConnectInfo(String id) {
		return redisConnectInfo.remove(id);
	}
}
