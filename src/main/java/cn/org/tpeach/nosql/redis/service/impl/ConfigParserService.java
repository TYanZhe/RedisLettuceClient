/**
 * 
 */
package cn.org.tpeach.nosql.redis.service.impl;

import cn.org.tpeach.nosql.annotation.Component;
import cn.org.tpeach.nosql.constant.ConfigConstant;
import cn.org.tpeach.nosql.redis.bean.RedisConnectInfo;
import cn.org.tpeach.nosql.redis.connection.RedisLarkPool;
import cn.org.tpeach.nosql.redis.service.IRedisConfigService;
import cn.org.tpeach.nosql.tools.*;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;



/**
 * <p>
 * Title: ConfigParserService.java
 * </p>
 * @author taoyz @date 2019年9月7日 @version 1.0
 */
@Component(value = "redisConfigService", scope = Component.BeanScope.SCOPE_SINGLETON, lazy = false)
public class ConfigParserService implements IRedisConfigService {
	private ConfigParser configParser = ConfigParser.getInstance();
	@Override
	public List<RedisConnectInfo> getRedisConfigAllList() {
		List<RedisConnectInfo> resultList = new LinkedList<>();
		List<Map<String, Map<String, ConfigMapper>>> listBySectionList = configParser.getListBySectionList(ConfigConstant.Section.CONNECTLIST);
		if(listBySectionList == null) {
			listBySectionList = new ArrayList<Map<String,Map<String,ConfigMapper>>>();
			configParser.getEntries().put(ConfigConstant.Section.CONNECTLIST, listBySectionList);
			Map<String, Object> newMapper = new LinkedHashMap<>();
			newMapper.put(ConfigConstant.Section.CONNECTLIST, new ArrayList<Map<String, Map<String, ConfigMapper>>>());
			try {
				IOUtil.fileAppendFW(configParser.getFile(), configParser.parseWrite(newMapper));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else if(!listBySectionList.isEmpty()){
			for (Map<String, Map<String, ConfigMapper>> map : listBySectionList) {
				for (Entry<String, Map<String, ConfigMapper>> entry : map.entrySet()) {
					resultList.add(configMapperToRedisConnectInfo(entry.getValue()));
				}
			}
		}else {

		}
		
		return resultList;
	}

	
	private RedisConnectInfo configMapperToRedisConnectInfo( Map<String, ConfigMapper> configMapper) {
		RedisConnectInfo redisConnectInfo = new RedisConnectInfo();
		redisConnectInfo.setId(configParser.getString(configMapper, ConfigConstant.Section.SERVER, "id", null));
		redisConnectInfo.setAuth(AESUtil.decrypt(configParser.getString(configMapper, ConfigConstant.Section.SERVER, "auth", "")));
		redisConnectInfo.setConnType(configParser.getShort(configMapper, ConfigConstant.Section.SERVER, "connType", (short) 0));
		redisConnectInfo.setHost(configParser.getString(configMapper, ConfigConstant.Section.SERVER, "host", null));
		redisConnectInfo.setName(configParser.getString(configMapper, ConfigConstant.Section.SERVER, "name", null));
		redisConnectInfo.setPort(configParser.getInt(configMapper, ConfigConstant.Section.SERVER, "port", 0));
		redisConnectInfo.setStructure(configParser.getInt(configMapper, ConfigConstant.Section.SERVER, "structure", 0));
		return redisConnectInfo;
	}
	

	/**
	 * @param conn
	 * @return
	 */
	private Map<String, ConfigMapper> redisConnectInfoToConfigMapper(RedisConnectInfo conn) {
		Map<String, ConfigMapper> connConfig = new LinkedHashMap<>();
		connConfig.put("id", ConfigMapper.builder().value(conn.getId()).build());
		connConfig.put("name", ConfigMapper.builder().value(StringUtils.defaultEmptyToString(conn.getName())).build());
		connConfig.put("auth", ConfigMapper.builder().value(AESUtil.encrypt(StringUtils.defaultEmptyToString(conn.getAuth()))).build());
		connConfig.put("connType", ConfigMapper.builder().value(StringUtils.defaultEmptyToString(conn.getConnType())).build());
		connConfig.put("host", ConfigMapper.builder().value(StringUtils.defaultEmptyToString(conn.getHost())).build());
		connConfig.put("port", ConfigMapper.builder().value(StringUtils.defaultEmptyToString(conn.getPort())).build());
		connConfig.put("structure", ConfigMapper.builder().value(StringUtils.defaultEmptyToString(conn.getStructure())).build());
		return connConfig;
	}

	@Override
	public RedisConnectInfo addRedisConfig(RedisConnectInfo conn) {
		conn.setId(StringUtils.getUUID());

		List<Map<String, Map<String, ConfigMapper>>> listBySectionList = configParser.getListBySectionList(ConfigConstant.Section.CONNECTLIST);
		if(CollectionUtils.isEmpty(listBySectionList)) {
			listBySectionList = new LinkedList<>();
		}
		Map<String, Map<String, ConfigMapper>> newMappper = new HashMap<>(1);
		Map<String, ConfigMapper> connConfig = redisConnectInfoToConfigMapper(conn);
		newMappper.put(ConfigConstant.Section.SERVER, connConfig);
		listBySectionList.add(newMappper);
		configParser.getEntries().put(ConfigConstant.Section.CONNECTLIST, listBySectionList);
		try {
			configParser.writhConfigFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return conn;
	}




	@Override
	public RedisConnectInfo deleteRedisConfigById(String id) {
		List<Map<String, Map<String, ConfigMapper>>> listBySectionList = configParser.getListBySectionList(ConfigConstant.Section.CONNECTLIST);
		if(CollectionUtils.isNotEmpty(listBySectionList)) {
			Iterator<Map<String, Map<String, ConfigMapper>>> iterator = listBySectionList.iterator();
			while (iterator.hasNext()) {
				Map<String, Map<String, ConfigMapper>> map = iterator.next();
				for (Entry<String, Map<String, ConfigMapper>> entry : map.entrySet()) {
					String configId = configParser.getString(entry.getValue(), ConfigConstant.Section.CONNECTLIST, "id", "");
					if(configId.equals(id)) {
						iterator.remove();
						try {
							configParser.writhConfigFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
						RedisLarkPool.deleteConnectInfo(id);
						RedisLarkPool.destory(id);
						return configMapperToRedisConnectInfo(entry.getValue());
					}
				}
			}
		}

		return null;
	}


	@Override
	public RedisConnectInfo getRedisConfigById(String id) {
		List<Map<String, Map<String, ConfigMapper>>> listBySectionList = configParser.getListBySectionList(ConfigConstant.Section.CONNECTLIST);
		if(CollectionUtils.isNotEmpty(listBySectionList)) {
			for (Map<String, Map<String, ConfigMapper>> map : listBySectionList) {
				for (Entry<String, Map<String, ConfigMapper>> entry : map.entrySet()) {
					String configId = configParser.getString(entry.getValue(), ConfigConstant.Section.SERVER, "id", "");
					if(configId.equals(id)) {
						return configMapperToRedisConnectInfo(entry.getValue());
					}
				}
			}
		}
		return null;
	}


	@Override
	public RedisConnectInfo updateRedisConfig(RedisConnectInfo conn) {
		List<Map<String, Map<String, ConfigMapper>>> listBySectionList = configParser.getListBySectionList(ConfigConstant.Section.CONNECTLIST);
		if(CollectionUtils.isNotEmpty(listBySectionList)) {
			for (Map<String, Map<String, ConfigMapper>> map : listBySectionList) {
				for (Entry<String, Map<String, ConfigMapper>> entry : map.entrySet()) {
					Map<String, ConfigMapper> value = entry.getValue();
					String configId = configParser.getString(value, ConfigConstant.Section.SERVER, "id", "");
					if(configId.equals(conn.getId())) {
						value.get("id").setValue(conn.getId());
						value.get("name").setValue(StringUtils.defaultEmptyToString(conn.getName()));
						value.get("auth").setValue(AESUtil.encrypt(StringUtils.defaultEmptyToString(conn.getAuth())));
						value.get("connType").setValue(StringUtils.defaultEmptyToString(conn.getConnType()));
						value.get("host").setValue(StringUtils.defaultEmptyToString(conn.getHost()));
						value.get("port").setValue(StringUtils.defaultEmptyToString(conn.getPort()));
						value.get("structure").setValue(StringUtils.defaultEmptyToString(conn.getStructure()));
						try {
							configParser.writhConfigFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
						RedisLarkPool.addOrUpdateConnectInfo(conn);
						return conn;
					}
				}
			}
		}
		return conn;
	}


	@Override
	public void resetConfig() {
		try {
			IOUtil.writeConfigFile("", configParser.getFile());
			configParser.parseReader();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
