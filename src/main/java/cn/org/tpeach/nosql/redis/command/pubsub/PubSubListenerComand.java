package cn.org.tpeach.nosql.redis.command.pubsub;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.AbstractLarkRedisPubSubListener;
import cn.org.tpeach.nosql.redis.command.JedisCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;

public class PubSubListenerComand extends JedisCommand<Void> {
    private  AbstractLarkRedisPubSubListener<byte[], byte[]> listener;
    private boolean remove;
    public PubSubListenerComand(String id, AbstractLarkRedisPubSubListener<byte[], byte[]> listener) {
       this(id,listener,false);
    }
    public PubSubListenerComand(String id, AbstractLarkRedisPubSubListener<byte[], byte[]> listener,boolean remove) {
        super(id);
        this.listener = listener;
        this.remove = remove;
    }
    public void updateLister(AbstractLarkRedisPubSubListener<byte[], byte[]> listener){
        this.listener = listener;
    }

    @Override
    public String sendCommand() {
        return null;
    }

    @Override
    public Void concreteCommand(RedisLarkContext<byte[], byte[]> redisLarkContext) {
        if(remove){
            redisLarkContext.removeListener(listener);
        }else{
            redisLarkContext.addListener(listener);
        }
        return null;
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_2_0;
    }
}
