package cn.org.tpeach.nosql.redis.connection;

import java.util.Map;
import java.util.function.Consumer;

import cn.org.tpeach.nosql.enums.RedisStructure;
import cn.org.tpeach.nosql.enums.RedisVersion;
import io.lettuce.core.*;
import io.lettuce.core.api.sync.BaseRedisCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.api.sync.RedisHashCommands;
import io.lettuce.core.api.sync.RedisKeyCommands;
import io.lettuce.core.api.sync.RedisListCommands;
import io.lettuce.core.api.sync.RedisServerCommands;
import io.lettuce.core.api.sync.RedisSetCommands;
import io.lettuce.core.api.sync.RedisSortedSetCommands;
import io.lettuce.core.api.sync.RedisStringCommands;
import io.lettuce.core.api.sync.RedisTransactionalCommands;

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
    Map<String, String> getInfo();

    /**
     * @return
     */
    RedisVersion getVersion();

    /**
     * @return
     */
    RedisStructure getRedisStructure();

    void execMulti(Consumer<RedisCommands<String, String>> statement, Consumer<TransactionResult> result) throws UnsupportedOperationException;

    void close();

    ScanIterator<String> scanIterator(int count, String pattren);

    ScanIterator<KeyValue<String, String>> hscanIterator(String key, int count, String pattren);

    ScanIterator<String> sscanIterator(String key, int count, String pattren);

    ScanIterator<ScoredValue<String>> zscanIterator(String key, int count, String pattren);

}
