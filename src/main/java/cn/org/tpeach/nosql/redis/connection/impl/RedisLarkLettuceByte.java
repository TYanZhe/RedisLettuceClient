package cn.org.tpeach.nosql.redis.connection.impl;

import cn.org.tpeach.nosql.redis.bean.RedisConnectInfo;
import io.lettuce.core.codec.ByteArrayCodec;

/**
 * @author tyz
 * @Title: RedisLarkLettuceByte
 * @ProjectName redisLark-Github
 * @Description: TODO
 * @date 2019-09-26 23:04
 * @since 1.0.0
 */
public class RedisLarkLettuceByte extends AbstractRedisLark<byte[], byte[]> {

    public RedisLarkLettuceByte(RedisConnectInfo connectInfo ) {
        super(connectInfo, ByteArrayCodec.INSTANCE);
    }

    public RedisLarkLettuceByte(String id, String host, int port, String auth) {
        super(id, host, port, auth, ByteArrayCodec.INSTANCE);
    }

    public RedisLarkLettuceByte(String id, String hostAndPort, String auth) {
        super(id, hostAndPort, auth,  ByteArrayCodec.INSTANCE);
    }


}
