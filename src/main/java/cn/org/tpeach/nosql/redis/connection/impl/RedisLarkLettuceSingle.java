package cn.org.tpeach.nosql.redis.connection.impl;

import cn.org.tpeach.nosql.enums.RedisStructure;
import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.connection.RedisLark;
import cn.org.tpeach.nosql.tools.StringUtils;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.support.ConnectionPoolSupport;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.*;
import redis.clients.jedis.params.*;
import redis.clients.jedis.util.Slowlog;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class RedisLarkLettuceSingle implements RedisLark {

    private String id ;
    String redisUrlFormat = "redis://%s%s:%s";

    GenericObjectPool<StatefulRedisConnection<String, String>> pool;
    RedisClient client;
    public RedisLarkLettuceSingle(final String id,final String host, final int port, String auth) {
        this.id = id;
        if(StringUtils.isBlank(auth)){
            auth = "";
        }else{
            auth = auth+"@";
        }

        // client
        this.client = RedisClient.create(String.format(redisUrlFormat,auth,host,port));

        // connection, 线程安全的长连接，连接丢失时会自动重连，直到调用 close 关闭连接。
//        StatefulRedisConnection<String, String> connection = client.connect();
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxIdle(2);
        this.pool = ConnectionPoolSupport.createGenericObjectPool(() -> client.connect(), poolConfig);

    }


    @Override
    public RedisVersion getVersion() {
        return RedisVersion.REDIS_3_2;
    }

    @Override
    public RedisStructure getRedisStructure() {
        return null;
    }

    @Override
    public String ping(String message) {
        return null;
    }

    @Override
    public String ping() {
        try {
            StatefulRedisConnection<String, String> connection = pool.borrowObject();
            RedisCommands<String, String> sync = connection.sync();
            String ping = sync.ping();
            connection.close();
            return ping;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String set(String key, String value) {
        return null;
    }

    @Override
    public String set(String key, String value, SetParams params) {
        return null;
    }

    @Override
    public String get(String key) {
        return null;
    }

    @Override
    public Long exists(String... keys) {
        return null;
    }

    @Override
    public Boolean exists(String key) {
        return null;
    }

    @Override
    public Long del(String... keys) {
        return null;
    }

    @Override
    public Long del(String key) {
        return null;
    }

    @Override
    public Long unlink(String... keys) {
        return null;
    }

    @Override
    public Long unlink(String key) {
        return null;
    }

    @Override
    public String type(String key) {
        return null;
    }

    @Override
    public Set<String> keys(String pattern) {
        return null;
    }

    @Override
    public String randomKey() {
        return null;
    }

    @Override
    public String rename(String oldkey, String newkey) {
        return null;
    }

    @Override
    public Long renamenx(String oldkey, String newkey) {
        return null;
    }

    @Override
    public Long expire(String key, int seconds) {
        return null;
    }

    @Override
    public Long expireAt(String key, long unixTime) {
        return null;
    }

    @Override
    public Long ttl(String key) {
        return null;
    }

    @Override
    public Long touch(String... keys) {
        return null;
    }

    @Override
    public Long touch(String key) {
        return null;
    }

    @Override
    public Long move(String key, int dbIndex) {
        return null;
    }

    @Override
    public String getSet(String key, String value) {
        return null;
    }

    @Override
    public List<String> mget(String... keys) {
        return null;
    }

    @Override
    public Long setnx(String key, String value) {
        return null;
    }

    @Override
    public String setex(String key, int seconds, String value) {
        return null;
    }

    @Override
    public String mset(String... keysvalues) {
        return null;
    }

    @Override
    public Long msetnx(String... keysvalues) {
        return null;
    }

    @Override
    public Long decrBy(String key, long decrement) {
        return null;
    }

    @Override
    public Long decr(String key) {
        return null;
    }

    @Override
    public Long incrBy(String key, long increment) {
        return null;
    }

    @Override
    public Double incrByFloat(String key, double increment) {
        return null;
    }

    @Override
    public Long incr(String key) {
        return null;
    }

    @Override
    public Long append(String key, String value) {
        return null;
    }

    @Override
    public String substr(String key, int start, int end) {
        return null;
    }

    @Override
    public Long hset(String key, String field, String value) {
        return null;
    }

    @Override
    public Long hset(String key, Map<String, String> hash) {
        return null;
    }

    @Override
    public String hget(String key, String field) {
        return null;
    }

    @Override
    public Long hsetnx(String key, String field, String value) {
        return null;
    }

    @Override
    public String hmset(String key, Map<String, String> hash) {
        return null;
    }

    @Override
    public List<String> hmget(String key, String... fields) {
        return null;
    }

    @Override
    public Long hincrBy(String key, String field, long value) {
        return null;
    }

    @Override
    public Double hincrByFloat(String key, String field, double value) {
        return null;
    }

    @Override
    public Boolean hexists(String key, String field) {
        return null;
    }

    @Override
    public Long hdel(String key, String... fields) {
        return null;
    }

    @Override
    public Long hlen(String key) {
        return null;
    }

    @Override
    public Set<String> hkeys(String key) {
        return null;
    }

    @Override
    public List<String> hvals(String key) {
        return null;
    }

    @Override
    public Map<String, String> hgetAll(String key) {
        return null;
    }

    @Override
    public Long rpush(String key, String... strings) {
        return null;
    }

    @Override
    public Long lpush(String key, String... strings) {
        return null;
    }

    @Override
    public Long llen(String key) {
        return null;
    }

    @Override
    public List<String> lrange(String key, long start, long stop) {
        return null;
    }

    @Override
    public String ltrim(String key, long start, long stop) {
        return null;
    }

    @Override
    public String lindex(String key, long index) {
        return null;
    }

    @Override
    public String lset(String key, long index, String value) {
        return null;
    }

    @Override
    public Long lrem(String key, long count, String value) {
        return null;
    }

    @Override
    public String lpop(String key) {
        return null;
    }

    @Override
    public String rpop(String key) {
        return null;
    }

    @Override
    public String rpoplpush(String srckey, String dstkey) {
        return null;
    }

    @Override
    public Long sadd(String key, String... members) {
        return null;
    }

    @Override
    public Set<String> smembers(String key) {
        return null;
    }

    @Override
    public Long srem(String key, String... members) {
        return null;
    }

    @Override
    public String spop(String key) {
        return null;
    }

    @Override
    public Set<String> spop(String key, long count) {
        return null;
    }

    @Override
    public Long smove(String srckey, String dstkey, String member) {
        return null;
    }

    @Override
    public Long scard(String key) {
        return null;
    }

    @Override
    public Boolean sismember(String key, String member) {
        return null;
    }

    @Override
    public Set<String> sinter(String... keys) {
        return null;
    }

    @Override
    public Long sinterstore(String dstkey, String... keys) {
        return null;
    }

    @Override
    public Set<String> sunion(String... keys) {
        return null;
    }

    @Override
    public Long sunionstore(String dstkey, String... keys) {
        return null;
    }

    @Override
    public Set<String> sdiff(String... keys) {
        return null;
    }

    @Override
    public Long sdiffstore(String dstkey, String... keys) {
        return null;
    }

    @Override
    public String srandmember(String key) {
        return null;
    }

    @Override
    public List<String> srandmember(String key, int count) {
        return null;
    }

    @Override
    public Long zadd(String key, double score, String member) {
        return null;
    }

    @Override
    public Long zadd(String key, double score, String member, ZAddParams params) {
        return null;
    }

    @Override
    public Long zadd(String key, Map<String, Double> scoreMembers) {
        return null;
    }

    @Override
    public Long zadd(String key, Map<String, Double> scoreMembers, ZAddParams params) {
        return null;
    }

    @Override
    public Set<String> zrange(String key, long start, long stop) {
        return null;
    }

    @Override
    public Long zrem(String key, String... members) {
        return null;
    }

    @Override
    public Double zincrby(String key, double increment, String member) {
        return null;
    }

    @Override
    public Double zincrby(String key, double increment, String member, ZIncrByParams params) {
        return null;
    }

    @Override
    public Long zrank(String key, String member) {
        return null;
    }

    @Override
    public Long zrevrank(String key, String member) {
        return null;
    }

    @Override
    public Set<String> zrevrange(String key, long start, long stop) {
        return null;
    }

    @Override
    public Set<Tuple> zrangeWithScores(String key, long start, long stop) {
        return null;
    }

    @Override
    public Set<Tuple> zrevrangeWithScores(String key, long start, long stop) {
        return null;
    }

    @Override
    public Long zcard(String key) {
        return null;
    }

    @Override
    public Double zscore(String key, String member) {
        return null;
    }

    @Override
    public String watch(String... keys) {
        return null;
    }

    @Override
    public List<String> sort(String key) {
        return null;
    }

    @Override
    public List<String> sort(String key, SortingParams sortingParameters) {
        return null;
    }

    @Override
    public List<String> blpop(int timeout, String... keys) {
        return null;
    }

    @Override
    public List<String> blpop(String... args) {
        return null;
    }

    @Override
    public List<String> brpop(String... args) {
        return null;
    }

    @Override
    public Long sort(String key, SortingParams sortingParameters, String dstkey) {
        return null;
    }

    @Override
    public Long sort(String key, String dstkey) {
        return null;
    }

    @Override
    public List<String> brpop(int timeout, String... keys) {
        return null;
    }

    @Override
    public Long zcount(String key, double min, double max) {
        return null;
    }

    @Override
    public Long zcount(String key, String min, String max) {
        return null;
    }

    @Override
    public Set<String> zrangeByScore(String key, double min, double max) {
        return null;
    }

    @Override
    public Set<String> zrangeByScore(String key, String min, String max) {
        return null;
    }

    @Override
    public Set<String> zrangeByScore(String key, double min, double max, int offset, int count) {
        return null;
    }

    @Override
    public Set<String> zrangeByScore(String key, String min, String max, int offset, int count) {
        return null;
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max) {
        return null;
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max) {
        return null;
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max, int offset, int count) {
        return null;
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max, int offset, int count) {
        return null;
    }

    @Override
    public Set<String> zrevrangeByScore(String key, double max, double min) {
        return null;
    }

    @Override
    public Set<String> zrevrangeByScore(String key, String max, String min) {
        return null;
    }

    @Override
    public Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count) {
        return null;
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min) {
        return null;
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min, int offset, int count) {
        return null;
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min, int offset, int count) {
        return null;
    }

    @Override
    public Set<String> zrevrangeByScore(String key, String max, String min, int offset, int count) {
        return null;
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min) {
        return null;
    }

    @Override
    public Long zremrangeByRank(String key, long start, long stop) {
        return null;
    }

    @Override
    public Long zremrangeByScore(String key, double min, double max) {
        return null;
    }

    @Override
    public Long zremrangeByScore(String key, String min, String max) {
        return null;
    }

    @Override
    public Long zunionstore(String dstkey, String... sets) {
        return null;
    }

    @Override
    public Long zunionstore(String dstkey, ZParams params, String... sets) {
        return null;
    }

    @Override
    public Long zinterstore(String dstkey, String... sets) {
        return null;
    }

    @Override
    public Long zinterstore(String dstkey, ZParams params, String... sets) {
        return null;
    }

    @Override
    public Long zlexcount(String key, String min, String max) {
        return null;
    }

    @Override
    public Set<String> zrangeByLex(String key, String min, String max) {
        return null;
    }

    @Override
    public Set<String> zrangeByLex(String key, String min, String max, int offset, int count) {
        return null;
    }

    @Override
    public Set<String> zrevrangeByLex(String key, String max, String min) {
        return null;
    }

    @Override
    public Set<String> zrevrangeByLex(String key, String max, String min, int offset, int count) {
        return null;
    }

    @Override
    public Long zremrangeByLex(String key, String min, String max) {
        return null;
    }

    @Override
    public Long strlen(String key) {
        return null;
    }

    @Override
    public Long lpushx(String key, String... string) {
        return null;
    }

    @Override
    public Long persist(String key) {
        return null;
    }

    @Override
    public Long rpushx(String key, String... string) {
        return null;
    }

    @Override
    public String echo(String string) {
        return null;
    }

    @Override
    public Long linsert(String key, ListPosition where, String pivot, String value) {
        return null;
    }

    @Override
    public String brpoplpush(String source, String destination, int timeout) {
        return null;
    }

    @Override
    public Boolean setbit(String key, long offset, boolean value) {
        return null;
    }

    @Override
    public Boolean setbit(String key, long offset, String value) {
        return null;
    }

    @Override
    public Boolean getbit(String key, long offset) {
        return null;
    }

    @Override
    public Long setrange(String key, long offset, String value) {
        return null;
    }

    @Override
    public String getrange(String key, long startOffset, long endOffset) {
        return null;
    }

    @Override
    public Long bitpos(String key, boolean value) {
        return null;
    }

    @Override
    public Long bitpos(String key, boolean value, BitPosParams params) {
        return null;
    }

    @Override
    public List<String> configGet(String pattern) {
        return null;
    }

    @Override
    public String configSet(String parameter, String value) {
        return null;
    }

    @Override
    public Object eval(String script, int keyCount, String... params) {
        return null;
    }

    @Override
    public void subscribe(JedisPubSub jedisPubSub, String... channels) {

    }

    @Override
    public Long publish(String channel, String message) {
        return null;
    }

    @Override
    public void psubscribe(JedisPubSub jedisPubSub, String... patterns) {

    }

    @Override
    public Object eval(String script, List<String> keys, List<String> args) {
        return null;
    }

    @Override
    public Object eval(String script) {
        return null;
    }

    @Override
    public Object evalsha(String sha1) {
        return null;
    }

    @Override
    public Object evalsha(String sha1, List<String> keys, List<String> args) {
        return null;
    }

    @Override
    public Object evalsha(String sha1, int keyCount, String... params) {
        return null;
    }

    @Override
    public Boolean scriptExists(String sha1) {
        return null;
    }

    @Override
    public List<Boolean> scriptExists(String... sha1) {
        return null;
    }

    @Override
    public String scriptLoad(String script) {
        return null;
    }

    @Override
    public List<Slowlog> slowlogGet() {
        return null;
    }

    @Override
    public List<Slowlog> slowlogGet(long entries) {
        return null;
    }

    @Override
    public Long objectRefcount(String key) {
        return null;
    }

    @Override
    public String objectEncoding(String key) {
        return null;
    }

    @Override
    public Long objectIdletime(String key) {
        return null;
    }

    @Override
    public Long bitcount(String key) {
        return null;
    }

    @Override
    public Long bitcount(String key, long start, long end) {
        return null;
    }

    @Override
    public Long bitop(BitOP op, String destKey, String... srcKeys) {
        return null;
    }

    @Override
    public List<Map<String, String>> sentinelMasters() {
        return null;
    }

    @Override
    public List<String> sentinelGetMasterAddrByName(String masterName) {
        return null;
    }

    @Override
    public Long sentinelReset(String pattern) {
        return null;
    }

    @Override
    public List<Map<String, String>> sentinelSlaves(String masterName) {
        return null;
    }

    @Override
    public String sentinelFailover(String masterName) {
        return null;
    }

    @Override
    public String sentinelMonitor(String masterName, String ip, int port, int quorum) {
        return null;
    }

    @Override
    public String sentinelRemove(String masterName) {
        return null;
    }

    @Override
    public String sentinelSet(String masterName, Map<String, String> parameterMap) {
        return null;
    }

    @Override
    public byte[] dump(String key) {
        return new byte[0];
    }

    @Override
    public String restore(String key, int ttl, byte[] serializedValue) {
        return null;
    }

    @Override
    public String restoreReplace(String key, int ttl, byte[] serializedValue) {
        return null;
    }

    @Override
    public Long pexpire(String key, long milliseconds) {
        return null;
    }

    @Override
    public Long pexpireAt(String key, long millisecondsTimestamp) {
        return null;
    }

    @Override
    public Long pttl(String key) {
        return null;
    }

    @Override
    public String psetex(String key, long milliseconds, String value) {
        return null;
    }

    @Override
    public String clientKill(String ipPort) {
        return null;
    }

    @Override
    public String clientGetname() {
        return null;
    }

    @Override
    public String clientList() {
        return null;
    }

    @Override
    public String clientSetname(String name) {
        return null;
    }

    @Override
    public String migrate(String host, int port, String key, int destinationDb, int timeout) {
        return null;
    }

    @Override
    public String migrate(String host, int port, int destinationDB, int timeout, MigrateParams params, String... keys) {
        return null;
    }

    @Override
    public ScanResult<String> scan(String cursor) {
        return null;
    }

    @Override
    public ScanResult<String> scan(String cursor, ScanParams params) {
        return null;
    }

    @Override
    public ScanResult<Map.Entry<String, String>> hscan(String key, String cursor) {
        return null;
    }

    @Override
    public ScanResult<Map.Entry<String, String>> hscan(String key, String cursor, ScanParams params) {
        return null;
    }

    @Override
    public ScanResult<String> sscan(String key, String cursor) {
        return null;
    }

    @Override
    public ScanResult<String> sscan(String key, String cursor, ScanParams params) {
        return null;
    }

    @Override
    public ScanResult<Tuple> zscan(String key, String cursor) {
        return null;
    }

    @Override
    public ScanResult<Tuple> zscan(String key, String cursor, ScanParams params) {
        return null;
    }

    @Override
    public String clusterNodes() {
        return null;
    }

    @Override
    public String readonly() {
        return null;
    }

    @Override
    public String clusterMeet(String ip, int port) {
        return null;
    }

    @Override
    public String clusterReset(ClusterReset resetType) {
        return null;
    }

    @Override
    public String clusterAddSlots(int... slots) {
        return null;
    }

    @Override
    public String clusterDelSlots(int... slots) {
        return null;
    }

    @Override
    public String clusterInfo() {
        return null;
    }

    @Override
    public List<String> clusterGetKeysInSlot(int slot, int count) {
        return null;
    }

    @Override
    public String clusterSetSlotNode(int slot, String nodeId) {
        return null;
    }

    @Override
    public String clusterSetSlotMigrating(int slot, String nodeId) {
        return null;
    }

    @Override
    public String clusterSetSlotImporting(int slot, String nodeId) {
        return null;
    }

    @Override
    public String clusterSetSlotStable(int slot) {
        return null;
    }

    @Override
    public String clusterForget(String nodeId) {
        return null;
    }

    @Override
    public String clusterFlushSlots() {
        return null;
    }

    @Override
    public Long clusterKeySlot(String key) {
        return null;
    }

    @Override
    public Long clusterCountKeysInSlot(int slot) {
        return null;
    }

    @Override
    public String clusterSaveConfig() {
        return null;
    }

    @Override
    public String clusterReplicate(String nodeId) {
        return null;
    }

    @Override
    public List<String> clusterSlaves(String nodeId) {
        return null;
    }

    @Override
    public String clusterFailover() {
        return null;
    }

    @Override
    public List<Object> clusterSlots() {
        return null;
    }

    @Override
    public String asking() {
        return null;
    }

    @Override
    public List<String> pubsubChannels(String pattern) {
        return null;
    }

    @Override
    public Long pubsubNumPat() {
        return null;
    }

    @Override
    public Map<String, String> pubsubNumSub(String... channels) {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public void setDataSource(JedisPoolAbstract jedisPool) {

    }

    @Override
    public Long pfadd(String key, String... elements) {
        return null;
    }

    @Override
    public long pfcount(String key) {
        return 0;
    }

    @Override
    public long pfcount(String... keys) {
        return 0;
    }

    @Override
    public String pfmerge(String destkey, String... sourcekeys) {
        return null;
    }

    @Override
    public List<String> blpop(int timeout, String key) {
        return null;
    }

    @Override
    public List<String> brpop(int timeout, String key) {
        return null;
    }

    @Override
    public Long geoadd(String key, double longitude, double latitude, String member) {
        return null;
    }

    @Override
    public Long geoadd(String key, Map<String, GeoCoordinate> memberCoordinateMap) {
        return null;
    }

    @Override
    public Double geodist(String key, String member1, String member2) {
        return null;
    }

    @Override
    public Double geodist(String key, String member1, String member2, GeoUnit unit) {
        return null;
    }

    @Override
    public List<String> geohash(String key, String... members) {
        return null;
    }

    @Override
    public List<GeoCoordinate> geopos(String key, String... members) {
        return null;
    }

    @Override
    public List<GeoRadiusResponse> georadius(String key, double longitude, double latitude, double radius, GeoUnit unit) {
        return null;
    }

    @Override
    public List<GeoRadiusResponse> georadiusReadonly(String key, double longitude, double latitude, double radius, GeoUnit unit) {
        return null;
    }

    @Override
    public List<GeoRadiusResponse> georadius(String key, double longitude, double latitude, double radius, GeoUnit unit, GeoRadiusParam param) {
        return null;
    }

    @Override
    public List<GeoRadiusResponse> georadiusReadonly(String key, double longitude, double latitude, double radius, GeoUnit unit, GeoRadiusParam param) {
        return null;
    }

    @Override
    public List<GeoRadiusResponse> georadiusByMember(String key, String member, double radius, GeoUnit unit) {
        return null;
    }

    @Override
    public List<GeoRadiusResponse> georadiusByMemberReadonly(String key, String member, double radius, GeoUnit unit) {
        return null;
    }

    @Override
    public List<GeoRadiusResponse> georadiusByMember(String key, String member, double radius, GeoUnit unit, GeoRadiusParam param) {
        return null;
    }

    @Override
    public List<GeoRadiusResponse> georadiusByMemberReadonly(String key, String member, double radius, GeoUnit unit, GeoRadiusParam param) {
        return null;
    }

    @Override
    public String moduleLoad(String path) {
        return null;
    }

    @Override
    public String moduleUnload(String name) {
        return null;
    }

    @Override
    public List<Module> moduleList() {
        return null;
    }

    @Override
    public List<Long> bitfield(String key, String... arguments) {
        return null;
    }

    @Override
    public Long hstrlen(String key, String field) {
        return null;
    }

    @Override
    public String info() {
        return null;
    }

    @Override
    public String select(int db) {
        return null;
    }

    @Override
    public int getDbAmount() {
        return 0;
    }

    @Override
    public Long dbSize() {
        return null;
    }

    @Override
    public Map<String, String> getInfo() {
        return null;
    }

    @Override
    public String flushDB() {
        return null;
    }

    @Override
    public Transaction multi() {
        return null;
    }
}
