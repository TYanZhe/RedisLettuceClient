package cn.org.tpeach.nosql.redis.command;

public abstract class  RedisClusterPubSubClusterAdapt<K,V> extends AbstractLarkRedisPubSubListener<K,V> {
    @Override
    public void message(K channel, V message) {

    }

    @Override
    public void message(K pattern, K channel, V message) {

    }

    @Override
    public void subscribed(K channel, long count) {

    }

    @Override
    public void psubscribed(K pattern, long count) {

    }

    @Override
    public void unsubscribed(K channel, long count) {

    }

    @Override
    public void punsubscribed(K pattern, long count) {

    }
}
