package cn.org.tpeach.nosql.redis.connection;

import cn.org.tpeach.nosql.annotation.Component;
import cn.org.tpeach.nosql.enums.RedisStructure;
import cn.org.tpeach.nosql.exception.ServiceException;
import cn.org.tpeach.nosql.framework.BeanContext;
import cn.org.tpeach.nosql.framework.LarkFrame;
import cn.org.tpeach.nosql.redis.bean.RedisConnectInfo;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import cn.org.tpeach.nosql.redis.service.IRedisConfigService;
import io.lettuce.core.RedisConnectionException;
import io.lettuce.core.RedisException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;

/**
 * @author tyz
 * @Title: RedisLarkFactory
 * @ProjectName RedisLark
 * @Description: RedisLark上下文创建工厂
 * @date 2019-06-23 21:11
 * @since 1.0.0
 */
@Component("redisLarkFactory")
public class RedisLarkFactory {
	final static Logger logger = LoggerFactory.getLogger(RedisLarkFactory.class);
	private IRedisConfigService redisConfigService = BeanContext.getBean("redisConfigService",IRedisConfigService.class);
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <K, V> RedisLarkContext  connectRedis(String id) {
		RedisStructure redisStructure = null;
		RedisConnectInfo conn = null;
		LocalDateTime now = null;
		try{
			// 获取连接信息
			conn = RedisLarkPool.getConnectInfo(id);
			boolean refresh = false;
			if (conn == null) {
				conn= redisConfigService.getRedisConfigById(id);
				refresh = true;
				RedisLarkPool.addOrUpdateConnectInfo(conn);
			}
			//获取上下文
			RedisLark redisLark;
			RedisLarkContext<K,V> redisLarkContext = RedisLarkPool.getRedisLarkContext(conn.getId());
			if (redisLarkContext == null || refresh ) {
				redisStructure = RedisStructure.getRedisStructure(conn.getStructure());
				// 根据Structure生成对应的连接服务，并缓存到pool
				if (redisStructure == RedisStructure.UNKNOW) {
					throw new ServiceException("配置文件出错");
				}

				// 根据RedisStructure 配置和RedisConnectInfo 构造方法动态生成RedisLark
				Class<?> clz = Class.forName(redisStructure.getService());
//				Constructor<?> constructor = clz.getConstructor(redisStructure.getParameterTypes());
				Constructor<?> constructor = clz.getConstructor(new Class<?>[]{conn.getClass()} );
				 now = LocalDateTime.now();
//				redisLark = (RedisLark) constructor.newInstance(redisStructure.getInitargs(conn));
				redisLark = (RedisLark) constructor.newInstance(new Object[]{conn});
				if(redisLarkContext == null) {
					redisLarkContext = new RedisLarkContext<K,V>(redisLark,conn);
					try {
						redisLark.ping();
					}catch (RedisException e){
						LarkFrame.larkLog.receivedError(conn.getName(),"",e);
						throw e;
					}
				}else {
					redisLarkContext.setRedisLark(redisLark);
				}
				RedisLarkPool.addRedisLarkContext(conn.getId(), redisLarkContext);
				LarkFrame.larkLog.sendInfo(now,conn.getName(),"AUTH");
				LarkFrame.larkLog.receivedInfo(conn.getName(), "connected");
			}
			return redisLarkContext;

		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			Throwable ex = e.getCause();
			if(conn !=null){
				LarkFrame.larkLog.sendInfo(now == null? LocalDateTime.now():now,conn.getName(),"AUTH");
				LarkFrame.larkLog.receivedError(conn.getName(),"connect fail",ex);
			}
			if(ex instanceof RedisConnectionException){
				throw (RedisConnectionException)ex;
			}
			throw  new RedisConnectionException(ex.getMessage());
		}

	}


}
