package cn.org.tpeach.nosql.redis.connection.impl;

import cn.org.tpeach.nosql.enums.RedisStructure;
import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.exception.ServiceException;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import cn.org.tpeach.nosql.redis.connection.RedisLark;
import cn.org.tpeach.nosql.tools.ArraysUtil;
import cn.org.tpeach.nosql.tools.CollectionUtils;
import cn.org.tpeach.nosql.tools.MapUtils;
import cn.org.tpeach.nosql.tools.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author tyz
 * @Title: RedisLarkSingle
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-06-23 20:47
 * @since 1.0.0
 */
public class RedisLarkSingle extends Jedis implements RedisLark{
	final static Logger logger = LoggerFactory.getLogger(RedisLarkSingle.class);
	private String id;
	private final static int timeout = 60000;
    private RedisVersion version;
	private Map<String,String> redisInfo;
	@Override
	public Map<String,String> getInfo(){
		if(redisInfo != null){
			return redisInfo;
		}
		String info = info();
		System.out.println(info);
		if(StringUtils.isBlank(info)){
			redisInfo = new HashMap<>(0);
		}else{
			redisInfo = new HashMap<>();
			try(BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(info.getBytes(Charset.forName("utf8"))), Charset.forName("utf8")))){
				String line;
				while ( (line = br.readLine()) != null ) {
					if(StringUtils.isNotBlank(line)){
						line = line.trim();
						if(line.startsWith("#")){
							continue;
						}
						String[] split = line.split(":");
						if(!ArraysUtil.isEmpty(split) && split.length == 2){
							redisInfo.put(split[0].trim(),split[1]);
						}
					}
				}
			} catch (IOException e) {
				logger.error("获取redis信息失败",e);
			}
		}
		return redisInfo;
	}
	public RedisLarkSingle(final String id,final String host, final int port,final String auth) {
		super(host,port,timeout);
		this.id = id;
		if(StringUtils.isNotEmpty(auth)) {
			super.auth(auth);
			super.ping();
		}
	}
    @Override
    public RedisVersion getVersion() {
    	if(this.version == null) {
			Map<String, String> info1 = getInfo();
			if(MapUtils.isNotEmpty(info1)){
				this.version = RedisVersion.getRedisVersion(info1.get("redis_version"));
			}
    	}

    	
        return version;
    }

    @Override
    public RedisStructure getRedisStructure() {
        return RedisStructure.SINGLE;
    }

	@Override
	@Deprecated
	public int getDbAmount() {
		try{
			List<String> dbs = super.configGet("databases");
			if(CollectionUtils.isNotEmpty(dbs)){
				return Integer.valueOf(dbs.get(1));
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return 16;
	}


}
