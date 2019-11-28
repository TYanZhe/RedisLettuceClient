package cn.org.tpeach.nosql.redis.command;

import cn.org.tpeach.nosql.constant.RedisInfoKeyConstant;
import cn.org.tpeach.nosql.enums.RedisStructure;
import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.framework.LarkFrame;
import cn.org.tpeach.nosql.redis.bean.RedisConnectInfo;
import cn.org.tpeach.nosql.redis.bean.SlowLogBo;
import cn.org.tpeach.nosql.redis.connection.RedisLark;
import cn.org.tpeach.nosql.tools.ArraysUtil;
import cn.org.tpeach.nosql.tools.CollectionUtils;
import cn.org.tpeach.nosql.tools.MapUtils;
import cn.org.tpeach.nosql.tools.StringUtils;
import io.lettuce.core.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author tyz
 * @Title: RedisLarkContext
 * @ProjectName RedisLark
 * @Description: RedisLark策略上下文 支持byte[] 和String
 * @date 2019-06-23 20:11
 * @since 1.0.0
 */
@Slf4j
public class RedisLarkContext<K,V> {
    @Getter
    private Integer db;
    @Getter
    private Map<String, String> redisInfo;
    private RedisVersion version;
    // 抽象策略算法
    @Getter
    @Setter
    private RedisLark<K,V> redisLark;
    private RedisConnectInfo redisConnectInfo;

    public RedisLarkContext(RedisLark<K,V> redisLark,RedisConnectInfo redisConnectInfo) {
        if(redisLark == null || redisConnectInfo == null){
            throw new IllegalArgumentException("redisLark or redisConnectInfo can not be null");
        }
        this.redisLark = redisLark;
        this.redisConnectInfo = redisConnectInfo;
    }

    @SuppressWarnings("unchecked")
	protected Class<V> getValueTpye(){
        Type type = getClass().getGenericSuperclass();
        if( type instanceof ParameterizedType){
            ParameterizedType pType = (ParameterizedType)type;
            Type claz = pType.getActualTypeArguments()[1];
            if( claz instanceof Class ){
                return  (Class<V>) claz;
            }
        }
        return null;
    }

    public String info() {
        return redisLark.info();
    }

    public Map<String, String> getInfo(boolean printLog) {
        if(MapUtils.isNotEmpty(redisInfo)){
            return redisInfo;
        }
        LocalDateTime now = LocalDateTime.now();
        String info = this.info();
        if(printLog){
            LarkFrame.larkLog.sendInfo(now,redisConnectInfo.getName(),"%s","INFO");
            LarkFrame.larkLog.receivedInfo(redisConnectInfo.getName(),"%s",info);
        }

        if(StringUtils.isBlank(info)){
            redisInfo = new HashMap<>(0);
        }else{
            redisInfo = new HashMap<>();
            try(BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(info.getBytes(Charset.forName("utf8"))), Charset.forName("utf8")))){
                String line;
                while ( (line = br.readLine()) != null ) {
                    if(StringUtils.isNotBlank(line)){
                        line = line.trim();
                        if(line.startsWith("#")){
                            continue;
                        }
                        String[] split = line.split(":");
                        if(!ArraysUtil.isEmpty(split) && split.length == 2){
                            redisInfo.put(split[0].trim(),split[1]);
                        }
                    }
                }
            } catch (IOException e) {
                log.error("获取redis信息失败",e);
            }
        }
        return redisInfo;
    }

    public RedisVersion getRedisVersion() {
        if(this.version == null) {
            Map<String, String> infoMap = getInfo(!redisConnectInfo.isTest());
            if(MapUtils.isNotEmpty(infoMap)){
                this.version = RedisVersion.getRedisVersion(infoMap.get(RedisInfoKeyConstant.redisVersion));
            }
        }
        return version;
    }

    public RedisStructure getRedisStructure() {
        return redisLark.getRedisStructure();
    }

    public String select(int db) {
        String res = redisLark.select(db);
        if("OK".equals(res)){
            this.db = db;
        }
        return res;

    }

    /**
     * 迭代当前数据库中的数据库键
     * @param scanCursor
     * @param scanArgs
     */
    public KeyScanCursor<K> scan(ScanCursor scanCursor, ScanArgs scanArgs){
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
    public String set(K key, V value) {
        return redisLark.set(key, value);
    }

    public Boolean setnx(final K key, final V value) {
        return redisLark.setnx(key, value);
    }

    public String setex(final K key, final int seconds, final V value) {
        return redisLark.setex(key, seconds, value);
    }

    public Long setrange(final K key, final long offset, final V value) {
        return redisLark.setrange(key, offset, value);
    }

    /**
     * 同时设置一个或多个 key-value 对。(对比MGET)
     *
     * @param map
     * @return
     */
    public String mset(Map<K, V> map) {
        return redisLark.mset(map);
    }

    /**
     * 获取指定 key 的值
     *
     * @param key
     * @return
     */
    public V get(K key) {
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
    public V getrange(final K key, final long startOffset, final long endOffset) {
        return redisLark.getrange(key, startOffset, endOffset);
    }

    /**
     * 将给定 key 的值设为 value ，并返回 key 的旧值
     *
     * @param key
     * @param value
     * @return
     */
    public V getSet(final K key, final V value) {
        return redisLark.getset(key, value);
    }

    /**
     * 获取所有(一个或多个)给定 key 的值。
     *
     * @param keys
     * @return
     */
    public List<KeyValue<K, V>> mget(@SuppressWarnings("unchecked") final K... keys) {
        return redisLark.mget(keys);
    }

    // hash------------------------------------
    public Boolean hset(final K key, final K field, final V value) {
        return redisLark.hset(key, field, value);
    }

    public Boolean hsetnx(final K key, final K field, final V value) {
        return redisLark.hsetnx(key, field, value);
    }

    public String hmset(final K key, final Map<K, V> hash) {
        return redisLark.hmset(key, hash);
    }

    public V hget(final K key, final K field) {
        return redisLark.hget(key, field);
    }

    public List<KeyValue<K, V>> hmget(final K key, @SuppressWarnings("unchecked") final K... fields) {
        return redisLark.hmget(key, fields);
    }

    public Map<K, V> hgetAll(final K key) {
        return redisLark.hgetall(key);
    }

    public Long hdel(final K key, @SuppressWarnings("unchecked") final K... fields) {
        return redisLark.hdel(key, fields);
    }

    public Long hlen(final K key) {
        return redisLark.hlen(key);
    }

    public Boolean hexists(final K key, final K field) {
        return redisLark.hexists(key, field);
    }

    public Long hincrBy(final K key, final K field, final long value) {
        return redisLark.hincrby(key, field, value);
    }

    public List<K> hkeys(final K key) {
        return redisLark.hkeys(key);
    }

    public List<V> hvals(final K key) {
        return redisLark.hvals(key);
    }

    public MapScanCursor<K, V> hscan(K key, ScanCursor scanCursor, ScanArgs scanArgs){
        if(scanArgs == null){
            return redisLark.hscan(key,scanCursor);
        }
        return redisLark.hscan(key,scanCursor,scanArgs);
    }

    //-------------list-------------

    @SuppressWarnings("unchecked")
	public Long rpush(final K key, final V... strings) {
        return redisLark.rpush(key, strings);
    }

    @SuppressWarnings("unchecked")
	public Long lpush(final K key, final V... strings) {
        return redisLark.lpush(key, strings);
    }

    @SuppressWarnings("unchecked")
	public Long rpushx(final K key, final V... strings) {
        return redisLark.rpushx(key, strings);
    }

    @SuppressWarnings("unchecked")
	public Long lpushx(final K key, final V... strings) {
        return redisLark.lpushx(key, strings);
    }

    public V rpop(final K key) {
    	
        return redisLark.rpop(key);
    }

    public V lpop(final K key) {
        return redisLark.lpop(key);
    }

    public Long llen(final K key) {
        return redisLark.llen(key);
    }

    public List<V> lrange(final K key, final long start, final long stop) {

        return redisLark.lrange(key, start, stop);
    }

    public void rrange(final K key, final long start, final long stop) {
        redisLark.lrange(key, start, stop);
    }
    public String lset(final K key, final long index, final V value) {
    	return redisLark.lset(key, index, value);
    }

    @SuppressWarnings("unchecked")
	public void ldelRow(final K key, final int index,boolean pringLog) {
        String s = "$"+StringUtils.getUUID()+"&"+StringUtils.getUUID()+"$";
        V uuid ;
        if(key instanceof byte[]){
            uuid = (V) s.getBytes();
        }else{
            uuid = (V) s;
        }

        if(pringLog){
            if(key instanceof byte[]){
                LarkFrame.larkLog.sendInfo(redisConnectInfo.getName(), "LSET %s %s %s", StringUtils.showHexStringValue((byte[]) key), index, StringUtils.showHexStringValue((byte[]) uuid));
            }else{
                LarkFrame.larkLog.sendInfo(redisConnectInfo.getName(), "LSET %s %s %s", StringUtils.showHexStringValue((String) key), index, StringUtils.showHexStringValue((String)uuid));
            }
        }
        try {
            V finalUuid = uuid;
            TransactionResult execMulti = redisLark.execMulti(c -> {

                c.lset(key, index, finalUuid);

//            count > 0: 从表头开始向表尾搜索，移除与value相等的元素，数量为count。
//            count < 0: 从表尾开始向表头搜索，移除与value相等的元素，数量为count的绝对值。
//            count = 0: 移除表中所有与value相等的值。

                c.lrem(key, 0, finalUuid);

            });
            if(!execMulti.isEmpty()){
                String lset = execMulti.get(0);
                if(pringLog) {
                    LarkFrame.larkLog.receivedInfo(redisConnectInfo.getName(), "%s", lset);
                    if(key instanceof byte[]){
                        LarkFrame.larkLog.sendInfo(redisConnectInfo.getName(), "LREM %s %s %s", StringUtils.showHexStringValue((byte[]) key), 0, StringUtils.showHexStringValue((byte[]) uuid));
                    }else{
                        LarkFrame.larkLog.sendInfo(redisConnectInfo.getName(), "LREM %s %s %s", StringUtils.showHexStringValue((String) key), 0, StringUtils.showHexStringValue((String)uuid));
                    }
                    Long lrem = execMulti.get(1);
                    LarkFrame.larkLog.receivedInfo(redisConnectInfo.getName(), "%s", lrem);
                }
            }
        
        } catch (UnsupportedOperationException e1) {
            String lset = redisLark.lset(key, index,  uuid);
            if(pringLog) {
                LarkFrame.larkLog.receivedInfo(redisConnectInfo.getName(), "%s", lset);
                if(key instanceof byte[]){
                    LarkFrame.larkLog.sendInfo(redisConnectInfo.getName(), "LREM %s %s %s", StringUtils.showHexStringValue((byte[]) key), 0, StringUtils.showHexStringValue((byte[]) uuid));
                }else{
                    LarkFrame.larkLog.sendInfo(redisConnectInfo.getName(), "LREM %s %s %s", StringUtils.showHexStringValue((String) key), 0, StringUtils.showHexStringValue((String)uuid));
                }
                Long lrem = redisLark.lrem(key, 0,  uuid);
                LarkFrame.larkLog.receivedInfo(redisConnectInfo.getName(), "%s", lrem);
            }
            
        }



    }
    public Long lrem(K key, long count, V value){
        return redisLark.lrem(key,count,value);
    }

    //--------------list end------------
    //--------------set start------------

    @SuppressWarnings("unchecked")
	public Long sadd(K key, V... members) {
        return redisLark.sadd(key, members);
    }

    public Long scard(final K key) {
        return redisLark.scard(key);
    }

    public Set<V> smembers(final K key) {
        return redisLark.smembers(key);
    }

    @SuppressWarnings("unchecked")
	public Long srem(K key, V... members) {
        return redisLark.srem(key, members);
    }
    public ValueScanCursor<V> sscan(K key, ScanCursor scanCursor, ScanArgs scanArgs) {
        if(scanArgs == null){
            return redisLark.sscan(key,scanCursor);
        }
        return redisLark.sscan(key,scanCursor,scanArgs);
    }
    public V spop(final K key) {
        return redisLark.spop(key);
    }
    public Set<V> spop(final K key,long count) {
        return redisLark.spop(key,count);
    }

    //--------------set end------------
    //--------------zset start------------
    public Long zadd(K key, double score, V member) {
        return redisLark.zadd(key, score, member);
    }
    @SuppressWarnings("unchecked")
	public Long zadd(K key, ScoredValue<V>... scoredValues) {
        return redisLark.zadd(key, scoredValues);
    }
    public List<V> zrange(final K key, final long start, final long stop) {
        return redisLark.zrange(key, start, stop);
    }

    public  List<ScoredValue<V>> zrangeWithScores(final K key, final long start, final long stop) {
        return redisLark.zrangeWithScores(key, start, stop);
    }

    public Long zcard(final K key) {
        return redisLark.zcard(key);
    }

    @SuppressWarnings("unchecked")
	public Long zrem(K key, V... members) {
        return redisLark.zrem(key, members);
    }

    public ScoredValueScanCursor<V> zscan(K key, ScanCursor scanCursor, ScanArgs scanArgs){
        if(scanArgs == null){
            return redisLark.zscan(key,scanCursor);
        }
        return redisLark.zscan(key,scanCursor,scanArgs);
    }
    //--------------zset end------------
    //---------------key start-------------------


    public  String rename(final K oldkey, final K newkey) {
        return redisLark.rename( oldkey, newkey);
    }
    public Boolean renamenx(K oldkey, K newkey) {
        return redisLark.renamenx( oldkey, newkey);
    }
    //---------------key end-------------------

    public Map<String, String> configGet(final String pattern) {
        return redisLark.configGet(pattern);
    }

    public String ping() {
        return redisLark.ping();
    }

    public Long ttl(final K key) {
        Long ttl = redisLark.ttl(key);
        return ttl;
    }

    public Boolean persist(final K key) {
        return redisLark.persist(key);
    }

    public Boolean pexpire(final K key, final long milliseconds) {
        return redisLark.pexpire(key, milliseconds);
    }

    public Boolean expire(final K key, final int seconds) {
        return redisLark.expire(key, seconds);
    }

    @SuppressWarnings("unchecked")
	public Long exists(final K key) {
        return redisLark.exists(key);
    }

    public String type(final K key) {
        return redisLark.type(key);
    }

    public String objectEncoding(final K key) {
        return redisLark.objectEncoding(key);
    }

    public Long objectIdletime(final K key) {
        return redisLark.objectIdletime(key);
    }

    public Long objectRefcount(final K key) {
        return redisLark.objectRefcount(key);
    }

    public Long incr(final K key) {
        return redisLark.incr(key);
    }



    public List<K> keys(K pattern) {
        return redisLark.keys(pattern);
    }

    public Long dbSize() {
        return redisLark.dbsize();
    }

    @SuppressWarnings("unchecked")
	public Long del(K... keys) {
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
    public void closePubSub() {
        redisLark.closePubSub();
    }

    public ScanIterator<K> scanIterator(int count, String pattren){
        return redisLark.scanIterator(count,pattren);
    }

    public ScanIterator<KeyValue<K, V>> hscanIterator(K key, int count, String pattren){
        return redisLark.hscanIterator(key,count,pattren);
    }

    public ScanIterator<V> sscanIterator(K key, int count, String pattren){
        return redisLark.sscanIterator(key,count,pattren);
    }

    public ScanIterator<ScoredValue<V>> zscanIterator(K key, int count, String pattren){
        return redisLark.zscanIterator(key,count,pattren);
    }

    public String clientList(){
        return redisLark.clientList();
    }
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public List<SlowLogBo> slowlogGet(Integer count){
        List<Object> list;
        if(count != null){
            list = redisLark.slowlogGet(count);
        }else{
            list = redisLark.slowlogGet();
        }
        List<SlowLogBo> resultList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(list)){
            for (Object o : list) {
                if(o instanceof List){
                    List context = (List) o;
                    SlowLogBo slowLogBo = new SlowLogBo();
                    slowLogBo.setId((Long) context.get(0));
                    slowLogBo.setOperTime((Long) context.get(1));
                    slowLogBo.setExcuteTime((Long) context.get(2));
                    List<byte[]> cmd = (List) context.get(3);
                    String collect = cmd.stream().map(StringUtils::showHexStringValue).collect(Collectors.joining(" "));
                    slowLogBo.setCmd(collect);
                    resultList.add(slowLogBo);
                }
            }
        }
        Collections.sort(resultList, (o1,o2)-> o1.getId() - o2.getId() > 0 ? 1 : -1);
        return resultList;
    }
    //---------------------------发布订阅--------------------------------

    public void psubscribe(K... patterns) {
        redisLark.psubscribe(patterns);
    }


    public void  punsubscribe(K... patterns) {
        redisLark.punsubscribe(patterns);
    }


    public void subscribe(K... channels) {
        redisLark.subscribe(channels);
    }
    public void  unsubscribe(K... channels) {
        redisLark.unsubscribe(channels);
    }

    public Long publish(K channel, V message){
        return redisLark.publish(channel,message);
    }

    public void addListener(AbstractLarkRedisPubSubListener<K, V> listener){
        redisLark.addListener(listener);
    }


    public void removeListener(AbstractLarkRedisPubSubListener<K, V> listener){
        redisLark.removeListener(listener);
    }
}
