package cn.org.tpeach.nosql.redis.connection.impl;

import cn.org.tpeach.nosql.exception.ServiceException;
import io.lettuce.core.*;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.output.*;
import io.lettuce.core.protocol.CommandArgs;
import io.lettuce.core.protocol.CommandType;
import io.lettuce.core.protocol.ProtocolKeyword;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author tyz
 * @Title: RedisLarkLettuceByte
 * @ProjectName redisLark-Github
 * @Description: TODO
 * @date 2019-09-26 23:04
 * @since 1.0.0
 */
public class RedisLarkLettuceByte extends AbstractRedisLark<byte[], byte[]> {

    public RedisLarkLettuceByte(String id, String host, int port, String auth) {
        super(id, host, port, auth, ByteArrayCodec.INSTANCE);
    }

    public RedisLarkLettuceByte(String id, String hostAndPort, String auth) {
        super(id, hostAndPort, auth,  ByteArrayCodec.INSTANCE);
    }

    @Override
    public Boolean renamenx(byte[] key, byte[] newKey) {
        return executeCommand(c -> c.renamenx(key, newKey), u -> {
//			return u.renamenx(key, newKey);
            if(Arrays.equals(key,newKey)){
                return true;
            }
            if(get(newKey) == null){
                byte[] temp = get(key);
                set(newKey, temp);
                del(key);
                return true;
            }else{
//					log.warn("renamenx:{} exists",newKey);
                throw new ServiceException(newKey+"已存在");
            }
        });
    }
}
