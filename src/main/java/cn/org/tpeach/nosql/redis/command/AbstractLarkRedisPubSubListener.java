package cn.org.tpeach.nosql.redis.command;

import cn.org.tpeach.nosql.tools.StringUtils;
import io.lettuce.core.cluster.pubsub.RedisClusterPubSubListener;
import io.lettuce.core.pubsub.RedisPubSubListener;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public abstract class AbstractLarkRedisPubSubListener<K, V> implements RedisPubSubListener<K,V>, RedisClusterPubSubListener<K,V> {
    private String id;
    private RedisClusterPubSubListener redisClusterPubSubListener;

    public AbstractLarkRedisPubSubListener() {
        this.id = StringUtils.getUUID();
    }


}
