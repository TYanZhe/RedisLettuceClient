package cn.org.tpeach.nosql.redis.command;

import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.org.tpeach.nosql.enums.RedisStructure;
import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.connection.RedisLark;
import cn.org.tpeach.nosql.tools.StringUtils;
import io.lettuce.core.*;
import lombok.extern.slf4j.Slf4j;


/**
 * @author tyz
 * @Title: RedisLarkContext
 * @ProjectName RedisLark
 * @Description: RedisLark策略上下文
 * @date 2019-06-23 20:11
 * @since 1.0.0
 */
@Slf4j
public class RedisLarkContext {

    // 抽象策略算法
    RedisLark<String,String> redisLark;

    public RedisLarkContext(RedisLark<String,String> redisLark) {
        this.redisLark = redisLark;
    }

    public RedisLark<String,String> getRedisLark() {
        return redisLark;
    }

    public void setRedisLark(RedisLark redisLark) {
        this.redisLark = redisLark;
    }

    public String info() {
        return redisLark.info();
    }

    public Map<String, String> getInfo() {
        return getInfo();
    }

    public RedisVersion getRedisVersion() {
        return redisLark.getVersion();
    }

    public RedisStructure getRedisStructure() {
        return redisLark.getRedisStructure();
    }

    public String select(int db) {
        return redisLark.select(db);
    }

    /**
     * 迭代当前数据库中的数据库键
     * @param scanCursor
     * @param scanArgs
     */
    public KeyScanCursor<String>  scan(ScanCursor scanCursor, ScanArgs scanArgs){
        if(scanArgs == null){
            return redisLark.scan(scanCursor);
        }
        return redisLark.scan( scanCursor, scanArgs);
    }

    // string
    /**
     * 设置指定 key 的值
     *
     * @param key
     * @param value
     * @return
     */
    public String set(String key, String value) {
        return redisLark.set(key, value);
    }

    public Boolean setnx(final String key, final String value) {
        return redisLark.setnx(key, value);
    }

    public String setex(final String key, final int seconds, final String value) {
        return redisLark.setex(key, seconds, value);
    }

    public Long setrange(final String key, final long offset, final String value) {
        return redisLark.setrange(key, offset, value);
    }

    /**
     * 同时设置一个或多个 key-value 对。(对比MGET)
     *
     * @param map
     * @return
     */
    public String mset(Map<String, String> map) {
        return redisLark.mset(map);
    }

    /**
     * 获取指定 key 的值
     *
     * @param key
     * @return
     */
    public String get(String key) {
        return redisLark.get(key);
    }

    /**
     * 返回 key 中字符串值的子字符串 （这里的start和end即起始子字符的索引，例如字符串abcde,取1至3，即拿到bcd，索引从0开始计数）
     *
     * @param key
     * @param startOffset
     * @param endOffset
     * @return
     */
    public String getrange(final String key, final long startOffset, final long endOffset) {
        return redisLark.getrange(key, startOffset, endOffset);
    }

    /**
     * 将给定 key 的值设为 value ，并返回 key 的旧值
     *
     * @param key
     * @param value
     * @return
     */
    public String getSet(final String key, final String value) {
        return redisLark.getset(key, value);
    }

    /**
     * 获取所有(一个或多个)给定 key 的值。
     *
     * @param keys
     * @return
     */
    public List<KeyValue<String, String>> mget(final String... keys) {
        return redisLark.mget(keys);
    }

    // hash------------------------------------
    public Boolean hset(final String key, final String field, final String value) {
        return redisLark.hset(key, field, value);
    }

    public Boolean hsetnx(final String key, final String field, final String value) {
        return redisLark.hsetnx(key, field, value);
    }

    public String hmset(final String key, final Map<String, String> hash) {
        return redisLark.hmset(key, hash);
    }

    public String hget(final String key, final String field) {
        return redisLark.hget(key, field);
    }

    public List<KeyValue<String, String>> hmget(final String key, final String... fields) {
        return redisLark.hmget(key, fields);
    }

    public Map<String, String> hgetAll(final String key) {
        return redisLark.hgetall(key);
    }

    public Long hdel(final String key, final String... fields) {
        return redisLark.hdel(key, fields);
    }

    public Long hlen(final String key) {
        return redisLark.hlen(key);
    }

    public Boolean hexists(final String key, final String field) {
        return redisLark.hexists(key, field);
    }

    public Long hincrBy(final String key, final String field, final long value) {
        return redisLark.hincrby(key, field, value);
    }

    public List<String> hkeys(final String key) {
        return redisLark.hkeys(key);
    }

    public List<String> hvals(final String key) {
        return redisLark.hvals(key);
    }

    public MapScanCursor<String, String> hscan(String key, ScanCursor scanCursor, ScanArgs scanArgs){
        if(scanArgs == null){
            return redisLark.hscan(key,scanCursor);
        }
        return redisLark.hscan(key,scanCursor,scanArgs);
    }

    //-------------list-------------

    public Long rpush(final String key, final String... strings) {
        return redisLark.rpush(key, strings);
    }

    public Long lpush(final String key, final String... strings) {
        return redisLark.lpush(key, strings);
    }

    public Long rpushx(final String key, final String... strings) {
        return redisLark.rpushx(key, strings);
    }

    public Long lpushx(final String key, final String... strings) {
        return redisLark.lpushx(key, strings);
    }

    public String rpop(final String key) {
    	
        return redisLark.rpop(key);
    }

    public String lpop(final String key) {
        return redisLark.lpop(key);
    }

    public Long llen(final String key) {
        return redisLark.llen(key);
    }

    public List<String> lrange(final String key, final long start, final long stop) {

        return redisLark.lrange(key, start, stop);
    }

    public void rrange(final String key, final long start, final long stop) {
        redisLark.lrange(key, start, stop);
    }
    public String lset(final String key, final long index, final String value) {
    	return redisLark.lset(key, index, value);
    }
    public void ldelRow(final String key, final int index) {
        String uuid = StringUtils.getUUID();

        try {
			redisLark.execMulti(c->{
              c.lset(key, index, uuid);
//            count > 0: 从表头开始向表尾搜索，移除与value相等的元素，数量为count。
//            count < 0: 从表尾开始向表头搜索，移除与value相等的元素，数量为count的绝对值。
//            count = 0: 移除表中所有与value相等的值。
              c.lrem(key, 0, uuid);
			}, r ->{
				
			});
		} catch (UnsupportedOperationException e1) {
	        redisLark.lset(key, index, uuid);
            redisLark.lrem(key, 0, uuid);
		}

    }
    //--------------list end------------
    //--------------set start------------

    public Long sadd(String key, String... members) {
        return redisLark.sadd(key, members);
    }

    public Long scard(final String key) {
        return redisLark.scard(key);
    }

    public Set<String> smembers(final String key) {
        return redisLark.smembers(key);
    }

    public Long srem(String key, String... members) {
        return redisLark.srem(key, members);
    }
    public ValueScanCursor<String> sscan(String key, ScanCursor scanCursor, ScanArgs scanArgs) {
        if(scanArgs == null){
            return redisLark.sscan(key,scanCursor);
        }
        return redisLark.sscan(key,scanCursor,scanArgs);
    }
    //--------------set end------------
    //--------------zset start------------
    public Long zadd(String key, double score, String member) {
        return redisLark.zadd(key, score, member);
    }
    public Long zadd(String key, ScoredValue<String>... scoredValues) {
        return redisLark.zadd(key, scoredValues);
    }
    public List<String> zrange(final String key, final long start, final long stop) {
        return redisLark.zrange(key, start, stop);
    }

    public  List<ScoredValue<String>> zrangeWithScores(final String key, final long start, final long stop) {
        return redisLark.zrangeWithScores(key, start, stop);
    }

    public Long zcard(final String key) {
        return redisLark.zcard(key);
    }

    public Long zrem(String key, String... members) {
        return redisLark.zrem(key, members);
    }

    public ScoredValueScanCursor<String> zscan(String key, ScanCursor scanCursor, ScanArgs scanArgs){
        if(scanArgs == null){
            return redisLark.zscan(key,scanCursor);
        }
        return redisLark.zscan(key,scanCursor,scanArgs);
    }
    //--------------zset end------------
    //---------------key start-------------------


    public  String rename(final String oldkey, final String newkey) {
        return redisLark.rename( oldkey, newkey);
    }
    public Boolean renamenx(String oldkey, String newkey) {
        return redisLark.renamenx( oldkey, newkey);
    }
    //---------------key end-------------------

    public Map<String, String> configGet(final String pattern) {
        return redisLark.configGet(pattern);
    }

    public String ping() {
        return redisLark.ping();
    }

    public Long ttl(final String key) {
        Long ttl = redisLark.ttl(key);
        return ttl;
    }

    public Boolean persist(final String key) {
        return redisLark.persist(key);
    }

    public Boolean pexpire(final String key, final long milliseconds) {
        return redisLark.pexpire(key, milliseconds);
    }

    public Boolean expire(final String key, final int seconds) {
        return redisLark.expire(key, seconds);
    }

    public Long exists(final String key) {
        return redisLark.exists(key);
    }

    public String type(final String key) {
        return redisLark.type(key);
    }

    public String objectEncoding(final String key) {
        return redisLark.objectEncoding(key);
    }

    public Long objectIdletime(final String key) {
        return redisLark.objectIdletime(key);
    }

    public Long objectRefcount(final String key) {
        return redisLark.objectRefcount(key);
    }

    public Long incr(final String key) {
        return redisLark.incr(key);
    }



    public List<String> keys(String pattern) {
        return redisLark.keys(pattern);
    }

    public Long dbSize() {
        return redisLark.dbsize();
    }

    public Long del(String... keys) {
        return redisLark.del(keys);
    }

    /**
     * Delete all the keys of the currently selected DB. This command never
     * fails.
     *
     * @return Status code reply
     */
    public String flushDB() {
        return redisLark.flushdb();
    }
    public void close() {
         redisLark.close();
    }


    public ScanIterator<String> scanIterator(int count, String pattren){
        return redisLark.scanIterator(count,pattren);
    }

    public ScanIterator<KeyValue<String, String>> hscanIterator(String key, int count, String pattren){
        return redisLark.hscanIterator(key,count,pattren);
    }

    public ScanIterator<String> sscanIterator(String key, int count, String pattren){
        return redisLark.sscanIterator(key,count,pattren);
    }

    public ScanIterator<ScoredValue<String>> zscanIterator(String key, int count, String pattren){
        return redisLark.zscanIterator(key,count,pattren);
    }


}
