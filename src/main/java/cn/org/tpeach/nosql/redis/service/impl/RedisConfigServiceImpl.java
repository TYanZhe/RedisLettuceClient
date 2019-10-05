package cn.org.tpeach.nosql.redis.service.impl;

import cn.org.tpeach.nosql.constant.PublicConstant;
import cn.org.tpeach.nosql.redis.bean.RedisConnectInfo;
import cn.org.tpeach.nosql.redis.connection.RedisLarkPool;
import cn.org.tpeach.nosql.redis.service.IRedisConfigService;
import cn.org.tpeach.nosql.tools.CollectionUtils;
import cn.org.tpeach.nosql.tools.IOUtil;
import cn.org.tpeach.nosql.tools.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tyz
 * @Title: RedisConfigServiceImpl
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-06-27 08:47
 * @deprecated replace by {@link cn.org.tpeach.nosql.redis.service.impl.ConfigParserService}
 * @since 1.0.0
 */
//@Component(value = "redisConfigService", scope = Component.BeanScope.SCOPE_SINGLETON, lazy = false)
@Deprecated
public class RedisConfigServiceImpl implements IRedisConfigService {
	final static Logger logger = LoggerFactory.getLogger(RedisConfigServiceImpl.class);
	private List<RedisConnectInfo> configList;

	/**
	 * 获取所有配置文件
	 *
	 * @return : List<RedisConnectInfo>
	 * @author : taoyz
	 * @date : 2019/6/27 0027 12:49
	 */
	@Override
	public List<RedisConnectInfo> getRedisConfigAllList() {
		try {
			System.out.println(PublicConstant.REDIS_CONFIG_PATH);
			File file = IOUtil.getFile(PublicConstant.REDIS_CONFIG_PATH);
			if (file.length() == 0) {
				writeConfigInfo(configList = new LinkedList<>());
			} else {
				if (configList == null) {
					try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
						Object o = ois.readObject();
						if (o instanceof List) {
							configList = (List<RedisConnectInfo>) o;
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return configList;

	}

	private void writeConfigInfo(List<RedisConnectInfo> redisConfinList) throws IOException {
		try (ObjectOutputStream out = new ObjectOutputStream(
				new FileOutputStream(IOUtil.getFile(PublicConstant.REDIS_CONFIG_PATH)))) {
			logger.info("写入配置文件>>>" + redisConfinList);
			out.writeObject(redisConfinList);
		}
	}

	@Override
	public RedisConnectInfo addRedisConfig(RedisConnectInfo conn) {
		configList = this.getRedisConfigAllList();
		if (CollectionUtils.isEmpty(configList)) {
			configList = new LinkedList<>();
		}
		conn.setId(StringUtils.getUUID());
		configList.add(conn);
		RedisLarkPool.addOrUpdateConnectInfo(conn);
		try {
			this.writeConfigInfo(configList);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return conn;
	}

	@Override
	public RedisConnectInfo deleteRedisConfigById(String id) {
		RedisConnectInfo connectInfo = null;
		configList = this.getRedisConfigAllList();
		if (CollectionUtils.isEmpty(configList)) {
			logger.info("不存在改配置文件:" + id);
			configList = new LinkedList<>();
		} else {
			Iterator<RedisConnectInfo> iterator = configList.iterator();
			boolean isExist = false;
			while (iterator.hasNext()) {
				RedisConnectInfo next = iterator.next();
				if (next.getId().equals(id)) {
					connectInfo = next;
					iterator.remove();
					RedisLarkPool.deleteConnectInfo(next.getId());
					isExist = true;
					break;
				}

			}
			if (isExist) {
				try {
					this.writeConfigInfo(configList);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				logger.info("不存在改配置文件>>>>>>" + id);
			}
		}
		return connectInfo;
	}

	@Override
	public RedisConnectInfo updateRedisConfig(RedisConnectInfo conn) {
		configList = this.getRedisConfigAllList();
		if (CollectionUtils.isEmpty(configList)) {
			configList = new LinkedList<>();
		}
		RedisConnectInfo oldConn = null;
		int index = -1;
		for (int i = 0; i < configList.size(); i++) {
			RedisConnectInfo redisConnectInfo = configList.get(i);
			if (redisConnectInfo.getId().equals(conn.getId())) {
				oldConn = redisConnectInfo;
				index = i;
				break;
			}
		}
		try {
			if (oldConn == null) {
				logger.info("更新失败，不存在改配置文件:" + conn);
			}
			if (!oldConn.equals(conn)) {
				configList.set(index, conn);
				this.writeConfigInfo(configList);
				RedisLarkPool.addOrUpdateConnectInfo(conn);
			} else {
				logger.info("更新失败，未修改任何信息:" + conn);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return conn;
	}

	@Override
	public void resetConfig() {
		try {
			writeConfigInfo(new LinkedList<>());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public RedisConnectInfo getRedisConfigById(String id) {
		configList = this.getRedisConfigAllList();
		if (CollectionUtils.isEmpty(configList)) {
			logger.info("不存在改配置文件:" + id);
			configList = new LinkedList<>();
		} else {
			List<RedisConnectInfo> list = configList.stream().filter(c -> id.equals(c.getId()))
					.collect(Collectors.toList());
			if (CollectionUtils.isNotEmpty(list)) {
				if (list.size() == 1) {
					return list.get(0);
				}

			} else {
				logger.info("不存在改配置文件>>>>>>" + id);
			}
		}
		return null;

	}
}
