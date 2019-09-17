package cn.org.tpeach.nosql.redis.connection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.org.tpeach.nosql.annotation.Component;
import cn.org.tpeach.nosql.enums.RedisStructure;
import cn.org.tpeach.nosql.exception.ServiceException;
import cn.org.tpeach.nosql.framework.BeanContext;
import cn.org.tpeach.nosql.redis.bean.RedisConnectInfo;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import cn.org.tpeach.nosql.redis.service.IRedisConfigService;

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
	public RedisLarkContext connectRedis(String id) {
		RedisStructure redisStructure = null;
		try{
			// 获取连接信息
			RedisConnectInfo conn = RedisLarkPool.getConnectInfo(id);
			boolean refresh = false;
			if (conn == null) {
				conn= redisConfigService.getRedisConfigById(id);
				refresh = true;
				RedisLarkPool.addOrUpdateConnectInfo(conn);
			}
			//获取上下文
			RedisLark redisLark;
			RedisLarkContext redisLarkContext = RedisLarkPool.getRedisLarkContext(conn.getId());
			if (redisLarkContext == null || refresh ) {
				redisStructure = RedisStructure.getRedisStructure(conn.getStructure());
				// 根据Structure生成对应的连接服务，并缓存到pool
				if (redisStructure == RedisStructure.UNKNOW) {
					throw new ServiceException("配置文件出错");
				}

				// 根据RedisStructure 配置和RedisConnectInfo 构造方法动态生成RedisLark
				Class<?> clz = Class.forName(redisStructure.getService());
				Constructor<?> constructor = clz.getConstructor(redisStructure.getParameterTypes());
				redisLark = (RedisLark) constructor.newInstance(redisStructure.getInitargs(conn));
				if(redisLarkContext == null) {
					redisLarkContext = new RedisLarkContext(redisLark);
				}else {
					redisLarkContext.setRedisLark(redisLark);
				}
				RedisLarkPool.addRedisLarkContext(conn.getId(), redisLarkContext);
			}
			return redisLarkContext;

		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			Throwable ex = e.getCause();

//			logger.error("反射加载"+redisStructure.getService()+"失败",ex);
//			while (ex != null) {
//				Throwable tex = ex.getCause();
//				if (tex == null) {
//					throw new ServiceException( ex.getMessage());
//				} else {
//					ex = tex;
//				}
//			}
			throw new ServiceException( ex.getMessage());
		}catch (Exception e){
			if(e instanceof ServiceException){
				throw e;
			}
			logger.error("获取连接失败",e);
			throw new ServiceException("连接失败:"+e.getMessage());
		}

	}


}
