package cn.org.tpeach.nosql.redis.connection.impl;

import cn.org.tpeach.nosql.redis.bean.RedisConnectInfo;
import io.lettuce.core.codec.StringCodec;
import lombok.extern.slf4j.Slf4j;

/**
 * @author tyz
 * @Title: RedisLarkLettuce
 * @ProjectName redisLark
 * @Description: TODO
 * @date 2019-09-13 10:11
 * @since 1.0.0
 */
@Slf4j
public class RedisLarkLettuce extends AbstractRedisLark<String,String> {

	public RedisLarkLettuce(RedisConnectInfo connectInfo ) {
		super(connectInfo, StringCodec.UTF8);
	}
	public RedisLarkLettuce(String id, String host, int port, String auth) {
		super(id, host, port, auth, StringCodec.UTF8);
	}

	public RedisLarkLettuce(String id, String hostAndPort, String auth) {
		super(id, hostAndPort, auth,  StringCodec.UTF8);
	}


}
