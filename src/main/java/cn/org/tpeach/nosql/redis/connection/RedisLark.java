package cn.org.tpeach.nosql.redis.connection;

import cn.org.tpeach.nosql.enums.RedisStructure;
import cn.org.tpeach.nosql.redis.command.AbstractLarkRedisPubSubListener;
import io.lettuce.core.KeyValue;
import io.lettuce.core.ScanIterator;
import io.lettuce.core.ScoredValue;
import io.lettuce.core.TransactionResult;
import io.lettuce.core.api.StatefulConnection;
import io.lettuce.core.api.sync.*;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Consumer;

/**
 * @author tyz
 * @Title: RedisLark1
 * @ProjectName redisLark
 * @Description: TODO
 * @date 2019-09-13 10:11
 * @since 1.0.0
 */

public interface RedisLark<K, V> extends BaseRedisCommands<K, V>, RedisStringCommands<K, V>, RedisListCommands<K, V>,
        RedisHashCommands<K, V>, RedisSetCommands<K, V>, RedisSortedSetCommands<K, V>,
        RedisKeyCommands<K, V>, RedisServerCommands<K, V>, RedisTransactionalCommands<K, V> {

    @SuppressWarnings("unchecked")
	default Class<K> getKeyTpye(){
        Type type = getClass().getGenericSuperclass();
        if( type instanceof ParameterizedType){
            ParameterizedType pType = (ParameterizedType)type;
            Type claz = pType.getActualTypeArguments()[0];
            if( claz instanceof Class ){
                return  (Class<K>) claz;
            }
        }
        return null;
    }
    StatefulConnection getStatefulConnection();


    /**
     * Change the selected database for the current Commands.
     *
     * @param db the database number
     * @return String simple-string-reply
     */
    String select(int db);

    /**
     * @return
     */
    RedisStructure getRedisStructure();

    TransactionResult execMulti(Consumer<RedisCommands<K, V>> statement ) throws UnsupportedOperationException;

    void close();

    ScanIterator<K> scanIterator(int count, String pattren);

    ScanIterator<KeyValue<K, V>> hscanIterator(K key, int count, String pattren);

    ScanIterator<V> sscanIterator(K key, int count, String pattren);

    ScanIterator<ScoredValue<V>> zscanIterator(K key, int count, String pattren);

    void addListener(AbstractLarkRedisPubSubListener<K, V> listener);


    void removeListener(AbstractLarkRedisPubSubListener<K, V> listener);


    //------------------异步--------



    void psubscribe(K... patterns);


    void  punsubscribe(K... patterns);


    void subscribe(K... channels);


    void  unsubscribe(K... channels);

    void closePubSub();

}
