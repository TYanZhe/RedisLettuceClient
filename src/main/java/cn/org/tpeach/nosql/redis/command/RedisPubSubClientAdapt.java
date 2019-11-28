package cn.org.tpeach.nosql.redis.command;

import io.lettuce.core.cluster.models.partitions.RedisClusterNode;

public abstract class  RedisPubSubClientAdapt<K,V> extends AbstractLarkRedisPubSubListener<K,V> {
    @Override
    public void message(RedisClusterNode node, K channel, V message) {

    }

    @Override
    public void message(RedisClusterNode node, K pattern, K channel, V message) {

    }

    @Override
    public void subscribed(RedisClusterNode node, K channel, long count) {

    }

    @Override
    public void psubscribed(RedisClusterNode node, K pattern, long count) {

    }

    @Override
    public void unsubscribed(RedisClusterNode node, K channel, long count) {

    }

    @Override
    public void punsubscribed(RedisClusterNode node, K pattern, long count) {

    }
}
