package cn.org.tpeach.nosql.redis.connection.impl;

import cn.org.tpeach.nosql.enums.RedisStructure;
import cn.org.tpeach.nosql.exception.ServiceException;
import io.lettuce.core.*;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.output.*;
import io.lettuce.core.protocol.CommandArgs;
import io.lettuce.core.protocol.CommandType;
import io.lettuce.core.protocol.ProtocolKeyword;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

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

	public RedisLarkLettuce(String id, String host, int port, String auth) {
		super(id, host, port, auth, StringCodec.UTF8);
	}

	public RedisLarkLettuce(String id, String hostAndPort, String auth) {
		super(id, hostAndPort, auth,  StringCodec.UTF8);
	}


}
