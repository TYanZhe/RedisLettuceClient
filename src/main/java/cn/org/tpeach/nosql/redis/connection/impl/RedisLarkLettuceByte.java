package cn.org.tpeach.nosql.redis.connection.impl;

import cn.org.tpeach.nosql.redis.connection.RedisLark;
import cn.org.tpeach.nosql.tools.StringUtils;
import io.lettuce.core.*;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
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
    public String select(int db) {
        return null;
    }

    @Override
    public TransactionResult execMulti(Consumer<RedisCommands<byte[], byte[]>> statement) throws UnsupportedOperationException {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public ScanIterator<byte[]> scanIterator(int count, String pattren) {
        return null;
    }

    @Override
    public ScanIterator<KeyValue<byte[], byte[]>> hscanIterator(byte[] key, int count, String pattren) {
        return null;
    }

    @Override
    public ScanIterator<byte[]> sscanIterator(byte[] key, int count, String pattren) {
        return null;
    }

    @Override
    public ScanIterator<ScoredValue<byte[]>> zscanIterator(byte[] key, int count, String pattren) {
        return null;
    }

    @Override
    public Long publish(byte[] channel, byte[] message) {
        return null;
    }

    @Override
    public List<byte[]> pubsubChannels() {
        return null;
    }

    @Override
    public List<byte[]> pubsubChannels(byte[] channel) {
        return null;
    }

    @Override
    public Map<byte[], Long> pubsubNumsub(byte[]... channels) {
        return null;
    }

    @Override
    public Long pubsubNumpat() {
        return null;
    }

    @Override
    public byte[] echo(byte[] msg) {
        return new byte[0];
    }

    @Override
    public List<Object> role() {
        return null;
    }

    @Override
    public String ping() {
        return null;
    }

    @Override
    public String readOnly() {
        return null;
    }

    @Override
    public String readWrite() {
        return null;
    }

    @Override
    public String quit() {
        return null;
    }

    @Override
    public Long waitForReplication(int replicas, long timeout) {
        return null;
    }

    @Override
    public <T> T dispatch(ProtocolKeyword type, CommandOutput<byte[], byte[], T> output) {
        return null;
    }

    @Override
    public <T> T dispatch(ProtocolKeyword type, CommandOutput<byte[], byte[], T> output, CommandArgs<byte[], byte[]> args) {
        return null;
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public Long hdel(byte[] key, byte[]... fields) {
        return null;
    }

    @Override
    public Boolean hexists(byte[] key, byte[] field) {
        return null;
    }

    @Override
    public byte[] hget(byte[] key, byte[] field) {
        return new byte[0];
    }

    @Override
    public Long hincrby(byte[] key, byte[] field, long amount) {
        return null;
    }

    @Override
    public Double hincrbyfloat(byte[] key, byte[] field, double amount) {
        return null;
    }

    @Override
    public Map<byte[], byte[]> hgetall(byte[] key) {
        return null;
    }

    @Override
    public Long hgetall(KeyValueStreamingChannel<byte[], byte[]> channel, byte[] key) {
        return null;
    }

    @Override
    public List<byte[]> hkeys(byte[] key) {
        return null;
    }

    @Override
    public Long hkeys(KeyStreamingChannel<byte[]> channel, byte[] key) {
        return null;
    }

    @Override
    public Long hlen(byte[] key) {
        return null;
    }

    @Override
    public List<KeyValue<byte[], byte[]>> hmget(byte[] key, byte[]... fields) {
        return null;
    }

    @Override
    public Long hmget(KeyValueStreamingChannel<byte[], byte[]> channel, byte[] key, byte[]... fields) {
        return null;
    }

    @Override
    public String hmset(byte[] key, Map<byte[], byte[]> map) {
        return null;
    }

    @Override
    public MapScanCursor<byte[], byte[]> hscan(byte[] key) {
        return null;
    }

    @Override
    public MapScanCursor<byte[], byte[]> hscan(byte[] key, ScanArgs scanArgs) {
        return null;
    }

    @Override
    public MapScanCursor<byte[], byte[]> hscan(byte[] key, ScanCursor scanCursor, ScanArgs scanArgs) {
        return null;
    }

    @Override
    public MapScanCursor<byte[], byte[]> hscan(byte[] key, ScanCursor scanCursor) {
        return null;
    }

    @Override
    public StreamScanCursor hscan(KeyValueStreamingChannel<byte[], byte[]> channel, byte[] key) {
        return null;
    }

    @Override
    public StreamScanCursor hscan(KeyValueStreamingChannel<byte[], byte[]> channel, byte[] key, ScanArgs scanArgs) {
        return null;
    }

    @Override
    public StreamScanCursor hscan(KeyValueStreamingChannel<byte[], byte[]> channel, byte[] key, ScanCursor scanCursor, ScanArgs scanArgs) {
        return null;
    }

    @Override
    public StreamScanCursor hscan(KeyValueStreamingChannel<byte[], byte[]> channel, byte[] key, ScanCursor scanCursor) {
        return null;
    }

    @Override
    public Boolean hset(byte[] key, byte[] field, byte[] value) {
        return null;
    }

    @Override
    public Boolean hsetnx(byte[] key, byte[] field, byte[] value) {
        return null;
    }

    @Override
    public Long hstrlen(byte[] key, byte[] field) {
        return null;
    }

    @Override
    public List<byte[]> hvals(byte[] key) {
        return null;
    }

    @Override
    public Long hvals(ValueStreamingChannel<byte[]> channel, byte[] key) {
        return null;
    }

    @Override
    public Long del(byte[]... keys) {
        return null;
    }

    @Override
    public Long unlink(byte[]... keys) {
        return null;
    }

    @Override
    public byte[] dump(byte[] key) {
        return new byte[0];
    }

    @Override
    public Long exists(byte[]... keys) {
        return null;
    }

    @Override
    public Boolean expire(byte[] key, long seconds) {
        return null;
    }

    @Override
    public Boolean expireat(byte[] key, Date timestamp) {
        return null;
    }

    @Override
    public Boolean expireat(byte[] key, long timestamp) {
        return null;
    }

    @Override
    public List<byte[]> keys(byte[] pattern) {
        return null;
    }

    @Override
    public Long keys(KeyStreamingChannel<byte[]> channel, byte[] pattern) {
        return null;
    }

    @Override
    public String migrate(String host, int port, byte[] key, int db, long timeout) {
        return null;
    }

    @Override
    public String migrate(String host, int port, int db, long timeout, MigrateArgs<byte[]> migrateArgs) {
        return null;
    }

    @Override
    public Boolean move(byte[] key, int db) {
        return null;
    }

    @Override
    public String objectEncoding(byte[] key) {
        return null;
    }

    @Override
    public Long objectIdletime(byte[] key) {
        return null;
    }

    @Override
    public Long objectRefcount(byte[] key) {
        return null;
    }

    @Override
    public Boolean persist(byte[] key) {
        return null;
    }

    @Override
    public Boolean pexpire(byte[] key, long milliseconds) {
        return null;
    }

    @Override
    public Boolean pexpireat(byte[] key, Date timestamp) {
        return null;
    }

    @Override
    public Boolean pexpireat(byte[] key, long timestamp) {
        return null;
    }

    @Override
    public Long pttl(byte[] key) {
        return null;
    }

    @Override
    public byte[] randomkey() {
        return new byte[0];
    }

    @Override
    public String rename(byte[] key, byte[] newKey) {
        return null;
    }

    @Override
    public Boolean renamenx(byte[] key, byte[] newKey) {
        return null;
    }

    @Override
    public String restore(byte[] key, long ttl, byte[] value) {
        return null;
    }

    @Override
    public String restore(byte[] key, byte[] value, RestoreArgs args) {
        return null;
    }

    @Override
    public List<byte[]> sort(byte[] key) {
        return null;
    }

    @Override
    public Long sort(ValueStreamingChannel<byte[]> channel, byte[] key) {
        return null;
    }

    @Override
    public List<byte[]> sort(byte[] key, SortArgs sortArgs) {
        return null;
    }

    @Override
    public Long sort(ValueStreamingChannel<byte[]> channel, byte[] key, SortArgs sortArgs) {
        return null;
    }

    @Override
    public Long sortStore(byte[] key, SortArgs sortArgs, byte[] destination) {
        return null;
    }

    @Override
    public Long touch(byte[]... keys) {
        return null;
    }

    @Override
    public Long ttl(byte[] key) {
        return null;
    }

    @Override
    public String type(byte[] key) {
        return null;
    }

    @Override
    public KeyScanCursor<byte[]> scan() {
        return null;
    }

    @Override
    public KeyScanCursor<byte[]> scan(ScanArgs scanArgs) {
        return null;
    }

    @Override
    public KeyScanCursor<byte[]> scan(ScanCursor scanCursor, ScanArgs scanArgs) {
        return null;
    }

    @Override
    public KeyScanCursor<byte[]> scan(ScanCursor scanCursor) {
        return null;
    }

    @Override
    public StreamScanCursor scan(KeyStreamingChannel<byte[]> channel) {
        return null;
    }

    @Override
    public StreamScanCursor scan(KeyStreamingChannel<byte[]> channel, ScanArgs scanArgs) {
        return null;
    }

    @Override
    public StreamScanCursor scan(KeyStreamingChannel<byte[]> channel, ScanCursor scanCursor, ScanArgs scanArgs) {
        return null;
    }

    @Override
    public StreamScanCursor scan(KeyStreamingChannel<byte[]> channel, ScanCursor scanCursor) {
        return null;
    }

    @Override
    public KeyValue<byte[], byte[]> blpop(long timeout, byte[]... keys) {
        return null;
    }

    @Override
    public KeyValue<byte[], byte[]> brpop(long timeout, byte[]... keys) {
        return null;
    }

    @Override
    public byte[] brpoplpush(long timeout, byte[] source, byte[] destination) {
        return new byte[0];
    }

    @Override
    public byte[] lindex(byte[] key, long index) {
        return new byte[0];
    }

    @Override
    public Long linsert(byte[] key, boolean before, byte[] pivot, byte[] value) {
        return null;
    }

    @Override
    public Long llen(byte[] key) {
        return null;
    }

    @Override
    public byte[] lpop(byte[] key) {
        return new byte[0];
    }

    @Override
    public Long lpush(byte[] key, byte[]... values) {
        return null;
    }

    @Override
    public Long lpushx(byte[] key, byte[]... values) {
        return null;
    }

    @Override
    public List<byte[]> lrange(byte[] key, long start, long stop) {
        return null;
    }

    @Override
    public Long lrange(ValueStreamingChannel<byte[]> channel, byte[] key, long start, long stop) {
        return null;
    }

    @Override
    public Long lrem(byte[] key, long count, byte[] value) {
        return null;
    }

    @Override
    public String lset(byte[] key, long index, byte[] value) {
        return null;
    }

    @Override
    public String ltrim(byte[] key, long start, long stop) {
        return null;
    }

    @Override
    public byte[] rpop(byte[] key) {
        return new byte[0];
    }

    @Override
    public byte[] rpoplpush(byte[] source, byte[] destination) {
        return new byte[0];
    }

    @Override
    public Long rpush(byte[] key, byte[]... values) {
        return null;
    }

    @Override
    public Long rpushx(byte[] key, byte[]... values) {
        return null;
    }

    @Override
    public String bgrewriteaof() {
        return null;
    }

    @Override
    public String bgsave() {
        return null;
    }

    @Override
    public byte[] clientGetname() {
        return new byte[0];
    }

    @Override
    public String clientSetname(byte[] name) {
        return null;
    }

    @Override
    public String clientKill(String addr) {
        return null;
    }

    @Override
    public Long clientKill(KillArgs killArgs) {
        return null;
    }

    @Override
    public Long clientUnblock(long id, UnblockType type) {
        return null;
    }

    @Override
    public String clientPause(long timeout) {
        return null;
    }

    @Override
    public String clientList() {
        return null;
    }

    @Override
    public List<Object> command() {
        return null;
    }

    @Override
    public List<Object> commandInfo(String... commands) {
        return null;
    }

    @Override
    public List<Object> commandInfo(CommandType... commands) {
        return null;
    }

    @Override
    public Long commandCount() {
        return null;
    }

    @Override
    public Map<String, String> configGet(String parameter) {
        return null;
    }

    @Override
    public String configResetstat() {
        return null;
    }

    @Override
    public String configRewrite() {
        return null;
    }

    @Override
    public String configSet(String parameter, String value) {
        return null;
    }

    @Override
    public Long dbsize() {
        return null;
    }

    @Override
    public String debugCrashAndRecover(Long delay) {
        return null;
    }

    @Override
    public String debugHtstats(int db) {
        return null;
    }

    @Override
    public String debugObject(byte[] key) {
        return null;
    }

    @Override
    public void debugOom() {

    }

    @Override
    public void debugSegfault() {

    }

    @Override
    public String debugReload() {
        return null;
    }

    @Override
    public String debugRestart(Long delay) {
        return null;
    }

    @Override
    public String debugSdslen(byte[] key) {
        return null;
    }

    @Override
    public String flushall() {
        return null;
    }

    @Override
    public String flushallAsync() {
        return null;
    }

    @Override
    public String flushdb() {
        return null;
    }

    @Override
    public String flushdbAsync() {
        return null;
    }

    @Override
    public String info() {
        return null;
    }

    @Override
    public String info(String section) {
        return null;
    }

    @Override
    public Date lastsave() {
        return null;
    }

    @Override
    public String save() {
        return null;
    }

    @Override
    public void shutdown(boolean save) {

    }

    @Override
    public String slaveof(String host, int port) {
        return null;
    }

    @Override
    public String slaveofNoOne() {
        return null;
    }

    @Override
    public List<Object> slowlogGet() {
        return null;
    }

    @Override
    public List<Object> slowlogGet(int count) {
        return null;
    }

    @Override
    public Long slowlogLen() {
        return null;
    }

    @Override
    public String slowlogReset() {
        return null;
    }

    @Override
    public List<byte[]> time() {
        return null;
    }

    @Override
    public Long sadd(byte[] key, byte[]... members) {
        return null;
    }

    @Override
    public Long scard(byte[] key) {
        return null;
    }

    @Override
    public Set<byte[]> sdiff(byte[]... keys) {
        return null;
    }

    @Override
    public Long sdiff(ValueStreamingChannel<byte[]> channel, byte[]... keys) {
        return null;
    }

    @Override
    public Long sdiffstore(byte[] destination, byte[]... keys) {
        return null;
    }

    @Override
    public Set<byte[]> sinter(byte[]... keys) {
        return null;
    }

    @Override
    public Long sinter(ValueStreamingChannel<byte[]> channel, byte[]... keys) {
        return null;
    }

    @Override
    public Long sinterstore(byte[] destination, byte[]... keys) {
        return null;
    }

    @Override
    public Boolean sismember(byte[] key, byte[] member) {
        return null;
    }

    @Override
    public Boolean smove(byte[] source, byte[] destination, byte[] member) {
        return null;
    }

    @Override
    public Set<byte[]> smembers(byte[] key) {
        return null;
    }

    @Override
    public Long smembers(ValueStreamingChannel<byte[]> channel, byte[] key) {
        return null;
    }

    @Override
    public byte[] spop(byte[] key) {
        return new byte[0];
    }

    @Override
    public Set<byte[]> spop(byte[] key, long count) {
        return null;
    }

    @Override
    public byte[] srandmember(byte[] key) {
        return new byte[0];
    }

    @Override
    public List<byte[]> srandmember(byte[] key, long count) {
        return null;
    }

    @Override
    public Long srandmember(ValueStreamingChannel<byte[]> channel, byte[] key, long count) {
        return null;
    }

    @Override
    public Long srem(byte[] key, byte[]... members) {
        return null;
    }

    @Override
    public Set<byte[]> sunion(byte[]... keys) {
        return null;
    }

    @Override
    public Long sunion(ValueStreamingChannel<byte[]> channel, byte[]... keys) {
        return null;
    }

    @Override
    public Long sunionstore(byte[] destination, byte[]... keys) {
        return null;
    }

    @Override
    public ValueScanCursor<byte[]> sscan(byte[] key) {
        return null;
    }

    @Override
    public ValueScanCursor<byte[]> sscan(byte[] key, ScanArgs scanArgs) {
        return null;
    }

    @Override
    public ValueScanCursor<byte[]> sscan(byte[] key, ScanCursor scanCursor, ScanArgs scanArgs) {
        return null;
    }

    @Override
    public ValueScanCursor<byte[]> sscan(byte[] key, ScanCursor scanCursor) {
        return null;
    }

    @Override
    public StreamScanCursor sscan(ValueStreamingChannel<byte[]> channel, byte[] key) {
        return null;
    }

    @Override
    public StreamScanCursor sscan(ValueStreamingChannel<byte[]> channel, byte[] key, ScanArgs scanArgs) {
        return null;
    }

    @Override
    public StreamScanCursor sscan(ValueStreamingChannel<byte[]> channel, byte[] key, ScanCursor scanCursor, ScanArgs scanArgs) {
        return null;
    }

    @Override
    public StreamScanCursor sscan(ValueStreamingChannel<byte[]> channel, byte[] key, ScanCursor scanCursor) {
        return null;
    }

    @Override
    public KeyValue<byte[], ScoredValue<byte[]>> bzpopmin(long timeout, byte[]... keys) {
        return null;
    }

    @Override
    public KeyValue<byte[], ScoredValue<byte[]>> bzpopmax(long timeout, byte[]... keys) {
        return null;
    }

    @Override
    public Long zadd(byte[] key, double score, byte[] member) {
        return null;
    }

    @Override
    public Long zadd(byte[] key, Object... scoresAndValues) {
        return null;
    }

    @Override
    public Long zadd(byte[] key, ScoredValue<byte[]>... scoredValues) {
        return null;
    }

    @Override
    public Long zadd(byte[] key, ZAddArgs zAddArgs, double score, byte[] member) {
        return null;
    }

    @Override
    public Long zadd(byte[] key, ZAddArgs zAddArgs, Object... scoresAndValues) {
        return null;
    }

    @Override
    public Long zadd(byte[] key, ZAddArgs zAddArgs, ScoredValue<byte[]>... scoredValues) {
        return null;
    }

    @Override
    public Double zaddincr(byte[] key, double score, byte[] member) {
        return null;
    }

    @Override
    public Double zaddincr(byte[] key, ZAddArgs zAddArgs, double score, byte[] member) {
        return null;
    }

    @Override
    public Long zcard(byte[] key) {
        return null;
    }

    @Override
    public Long zcount(byte[] key, double min, double max) {
        return null;
    }

    @Override
    public Long zcount(byte[] key, String min, String max) {
        return null;
    }

    @Override
    public Long zcount(byte[] key, Range<? extends Number> range) {
        return null;
    }

    @Override
    public Double zincrby(byte[] key, double amount, byte[] member) {
        return null;
    }

    @Override
    public Long zinterstore(byte[] destination, byte[]... keys) {
        return null;
    }

    @Override
    public Long zinterstore(byte[] destination, ZStoreArgs storeArgs, byte[]... keys) {
        return null;
    }

    @Override
    public Long zlexcount(byte[] key, String min, String max) {
        return null;
    }

    @Override
    public Long zlexcount(byte[] key, Range<? extends byte[]> range) {
        return null;
    }

    @Override
    public ScoredValue<byte[]> zpopmin(byte[] key) {
        return null;
    }

    @Override
    public List<ScoredValue<byte[]>> zpopmin(byte[] key, long count) {
        return null;
    }

    @Override
    public ScoredValue<byte[]> zpopmax(byte[] key) {
        return null;
    }

    @Override
    public List<ScoredValue<byte[]>> zpopmax(byte[] key, long count) {
        return null;
    }

    @Override
    public List<byte[]> zrange(byte[] key, long start, long stop) {
        return null;
    }

    @Override
    public Long zrange(ValueStreamingChannel<byte[]> channel, byte[] key, long start, long stop) {
        return null;
    }

    @Override
    public List<ScoredValue<byte[]>> zrangeWithScores(byte[] key, long start, long stop) {
        return null;
    }

    @Override
    public Long zrangeWithScores(ScoredValueStreamingChannel<byte[]> channel, byte[] key, long start, long stop) {
        return null;
    }

    @Override
    public List<byte[]> zrangebylex(byte[] key, String min, String max) {
        return null;
    }

    @Override
    public List<byte[]> zrangebylex(byte[] key, Range<? extends byte[]> range) {
        return null;
    }

    @Override
    public List<byte[]> zrangebylex(byte[] key, String min, String max, long offset, long count) {
        return null;
    }

    @Override
    public List<byte[]> zrangebylex(byte[] key, Range<? extends byte[]> range, Limit limit) {
        return null;
    }

    @Override
    public List<byte[]> zrangebyscore(byte[] key, double min, double max) {
        return null;
    }

    @Override
    public List<byte[]> zrangebyscore(byte[] key, String min, String max) {
        return null;
    }

    @Override
    public List<byte[]> zrangebyscore(byte[] key, Range<? extends Number> range) {
        return null;
    }

    @Override
    public List<byte[]> zrangebyscore(byte[] key, double min, double max, long offset, long count) {
        return null;
    }

    @Override
    public List<byte[]> zrangebyscore(byte[] key, String min, String max, long offset, long count) {
        return null;
    }

    @Override
    public List<byte[]> zrangebyscore(byte[] key, Range<? extends Number> range, Limit limit) {
        return null;
    }

    @Override
    public Long zrangebyscore(ValueStreamingChannel<byte[]> channel, byte[] key, double min, double max) {
        return null;
    }

    @Override
    public Long zrangebyscore(ValueStreamingChannel<byte[]> channel, byte[] key, String min, String max) {
        return null;
    }

    @Override
    public Long zrangebyscore(ValueStreamingChannel<byte[]> channel, byte[] key, Range<? extends Number> range) {
        return null;
    }

    @Override
    public Long zrangebyscore(ValueStreamingChannel<byte[]> channel, byte[] key, double min, double max, long offset, long count) {
        return null;
    }

    @Override
    public Long zrangebyscore(ValueStreamingChannel<byte[]> channel, byte[] key, String min, String max, long offset, long count) {
        return null;
    }

    @Override
    public Long zrangebyscore(ValueStreamingChannel<byte[]> channel, byte[] key, Range<? extends Number> range, Limit limit) {
        return null;
    }

    @Override
    public List<ScoredValue<byte[]>> zrangebyscoreWithScores(byte[] key, double min, double max) {
        return null;
    }

    @Override
    public List<ScoredValue<byte[]>> zrangebyscoreWithScores(byte[] key, String min, String max) {
        return null;
    }

    @Override
    public List<ScoredValue<byte[]>> zrangebyscoreWithScores(byte[] key, Range<? extends Number> range) {
        return null;
    }

    @Override
    public List<ScoredValue<byte[]>> zrangebyscoreWithScores(byte[] key, double min, double max, long offset, long count) {
        return null;
    }

    @Override
    public List<ScoredValue<byte[]>> zrangebyscoreWithScores(byte[] key, String min, String max, long offset, long count) {
        return null;
    }

    @Override
    public List<ScoredValue<byte[]>> zrangebyscoreWithScores(byte[] key, Range<? extends Number> range, Limit limit) {
        return null;
    }

    @Override
    public Long zrangebyscoreWithScores(ScoredValueStreamingChannel<byte[]> channel, byte[] key, double min, double max) {
        return null;
    }

    @Override
    public Long zrangebyscoreWithScores(ScoredValueStreamingChannel<byte[]> channel, byte[] key, String min, String max) {
        return null;
    }

    @Override
    public Long zrangebyscoreWithScores(ScoredValueStreamingChannel<byte[]> channel, byte[] key, Range<? extends Number> range) {
        return null;
    }

    @Override
    public Long zrangebyscoreWithScores(ScoredValueStreamingChannel<byte[]> channel, byte[] key, double min, double max, long offset, long count) {
        return null;
    }

    @Override
    public Long zrangebyscoreWithScores(ScoredValueStreamingChannel<byte[]> channel, byte[] key, String min, String max, long offset, long count) {
        return null;
    }

    @Override
    public Long zrangebyscoreWithScores(ScoredValueStreamingChannel<byte[]> channel, byte[] key, Range<? extends Number> range, Limit limit) {
        return null;
    }

    @Override
    public Long zrank(byte[] key, byte[] member) {
        return null;
    }

    @Override
    public Long zrem(byte[] key, byte[]... members) {
        return null;
    }

    @Override
    public Long zremrangebylex(byte[] key, String min, String max) {
        return null;
    }

    @Override
    public Long zremrangebylex(byte[] key, Range<? extends byte[]> range) {
        return null;
    }

    @Override
    public Long zremrangebyrank(byte[] key, long start, long stop) {
        return null;
    }

    @Override
    public Long zremrangebyscore(byte[] key, double min, double max) {
        return null;
    }

    @Override
    public Long zremrangebyscore(byte[] key, String min, String max) {
        return null;
    }

    @Override
    public Long zremrangebyscore(byte[] key, Range<? extends Number> range) {
        return null;
    }

    @Override
    public List<byte[]> zrevrange(byte[] key, long start, long stop) {
        return null;
    }

    @Override
    public Long zrevrange(ValueStreamingChannel<byte[]> channel, byte[] key, long start, long stop) {
        return null;
    }

    @Override
    public List<ScoredValue<byte[]>> zrevrangeWithScores(byte[] key, long start, long stop) {
        return null;
    }

    @Override
    public Long zrevrangeWithScores(ScoredValueStreamingChannel<byte[]> channel, byte[] key, long start, long stop) {
        return null;
    }

    @Override
    public List<byte[]> zrevrangebylex(byte[] key, Range<? extends byte[]> range) {
        return null;
    }

    @Override
    public List<byte[]> zrevrangebylex(byte[] key, Range<? extends byte[]> range, Limit limit) {
        return null;
    }

    @Override
    public List<byte[]> zrevrangebyscore(byte[] key, double max, double min) {
        return null;
    }

    @Override
    public List<byte[]> zrevrangebyscore(byte[] key, String max, String min) {
        return null;
    }

    @Override
    public List<byte[]> zrevrangebyscore(byte[] key, Range<? extends Number> range) {
        return null;
    }

    @Override
    public List<byte[]> zrevrangebyscore(byte[] key, double max, double min, long offset, long count) {
        return null;
    }

    @Override
    public List<byte[]> zrevrangebyscore(byte[] key, String max, String min, long offset, long count) {
        return null;
    }

    @Override
    public List<byte[]> zrevrangebyscore(byte[] key, Range<? extends Number> range, Limit limit) {
        return null;
    }

    @Override
    public Long zrevrangebyscore(ValueStreamingChannel<byte[]> channel, byte[] key, double max, double min) {
        return null;
    }

    @Override
    public Long zrevrangebyscore(ValueStreamingChannel<byte[]> channel, byte[] key, String max, String min) {
        return null;
    }

    @Override
    public Long zrevrangebyscore(ValueStreamingChannel<byte[]> channel, byte[] key, Range<? extends Number> range) {
        return null;
    }

    @Override
    public Long zrevrangebyscore(ValueStreamingChannel<byte[]> channel, byte[] key, double max, double min, long offset, long count) {
        return null;
    }

    @Override
    public Long zrevrangebyscore(ValueStreamingChannel<byte[]> channel, byte[] key, String max, String min, long offset, long count) {
        return null;
    }

    @Override
    public Long zrevrangebyscore(ValueStreamingChannel<byte[]> channel, byte[] key, Range<? extends Number> range, Limit limit) {
        return null;
    }

    @Override
    public List<ScoredValue<byte[]>> zrevrangebyscoreWithScores(byte[] key, double max, double min) {
        return null;
    }

    @Override
    public List<ScoredValue<byte[]>> zrevrangebyscoreWithScores(byte[] key, String max, String min) {
        return null;
    }

    @Override
    public List<ScoredValue<byte[]>> zrevrangebyscoreWithScores(byte[] key, Range<? extends Number> range) {
        return null;
    }

    @Override
    public List<ScoredValue<byte[]>> zrevrangebyscoreWithScores(byte[] key, double max, double min, long offset, long count) {
        return null;
    }

    @Override
    public List<ScoredValue<byte[]>> zrevrangebyscoreWithScores(byte[] key, String max, String min, long offset, long count) {
        return null;
    }

    @Override
    public List<ScoredValue<byte[]>> zrevrangebyscoreWithScores(byte[] key, Range<? extends Number> range, Limit limit) {
        return null;
    }

    @Override
    public Long zrevrangebyscoreWithScores(ScoredValueStreamingChannel<byte[]> channel, byte[] key, double max, double min) {
        return null;
    }

    @Override
    public Long zrevrangebyscoreWithScores(ScoredValueStreamingChannel<byte[]> channel, byte[] key, String max, String min) {
        return null;
    }

    @Override
    public Long zrevrangebyscoreWithScores(ScoredValueStreamingChannel<byte[]> channel, byte[] key, Range<? extends Number> range) {
        return null;
    }

    @Override
    public Long zrevrangebyscoreWithScores(ScoredValueStreamingChannel<byte[]> channel, byte[] key, double max, double min, long offset, long count) {
        return null;
    }

    @Override
    public Long zrevrangebyscoreWithScores(ScoredValueStreamingChannel<byte[]> channel, byte[] key, String max, String min, long offset, long count) {
        return null;
    }

    @Override
    public Long zrevrangebyscoreWithScores(ScoredValueStreamingChannel<byte[]> channel, byte[] key, Range<? extends Number> range, Limit limit) {
        return null;
    }

    @Override
    public Long zrevrank(byte[] key, byte[] member) {
        return null;
    }

    @Override
    public ScoredValueScanCursor<byte[]> zscan(byte[] key) {
        return null;
    }

    @Override
    public ScoredValueScanCursor<byte[]> zscan(byte[] key, ScanArgs scanArgs) {
        return null;
    }

    @Override
    public ScoredValueScanCursor<byte[]> zscan(byte[] key, ScanCursor scanCursor, ScanArgs scanArgs) {
        return null;
    }

    @Override
    public ScoredValueScanCursor<byte[]> zscan(byte[] key, ScanCursor scanCursor) {
        return null;
    }

    @Override
    public StreamScanCursor zscan(ScoredValueStreamingChannel<byte[]> channel, byte[] key) {
        return null;
    }

    @Override
    public StreamScanCursor zscan(ScoredValueStreamingChannel<byte[]> channel, byte[] key, ScanArgs scanArgs) {
        return null;
    }

    @Override
    public StreamScanCursor zscan(ScoredValueStreamingChannel<byte[]> channel, byte[] key, ScanCursor scanCursor, ScanArgs scanArgs) {
        return null;
    }

    @Override
    public StreamScanCursor zscan(ScoredValueStreamingChannel<byte[]> channel, byte[] key, ScanCursor scanCursor) {
        return null;
    }

    @Override
    public Double zscore(byte[] key, byte[] member) {
        return null;
    }

    @Override
    public Long zunionstore(byte[] destination, byte[]... keys) {
        return null;
    }

    @Override
    public Long zunionstore(byte[] destination, ZStoreArgs storeArgs, byte[]... keys) {
        return null;
    }

    @Override
    public Long append(byte[] key, byte[] value) {
        return null;
    }

    @Override
    public Long bitcount(byte[] key) {
        return null;
    }

    @Override
    public Long bitcount(byte[] key, long start, long end) {
        return null;
    }

    @Override
    public List<Long> bitfield(byte[] key, BitFieldArgs bitFieldArgs) {
        return null;
    }

    @Override
    public Long bitpos(byte[] key, boolean state) {
        return null;
    }

    @Override
    public Long bitpos(byte[] key, boolean state, long start) {
        return null;
    }

    @Override
    public Long bitpos(byte[] key, boolean state, long start, long end) {
        return null;
    }

    @Override
    public Long bitopAnd(byte[] destination, byte[]... keys) {
        return null;
    }

    @Override
    public Long bitopNot(byte[] destination, byte[] source) {
        return null;
    }

    @Override
    public Long bitopOr(byte[] destination, byte[]... keys) {
        return null;
    }

    @Override
    public Long bitopXor(byte[] destination, byte[]... keys) {
        return null;
    }

    @Override
    public Long decr(byte[] key) {
        return null;
    }

    @Override
    public Long decrby(byte[] key, long amount) {
        return null;
    }

    @Override
    public byte[] get(byte[] key) {
        return new byte[0];
    }

    @Override
    public Long getbit(byte[] key, long offset) {
        return null;
    }

    @Override
    public byte[] getrange(byte[] key, long start, long end) {
        return new byte[0];
    }

    @Override
    public byte[] getset(byte[] key, byte[] value) {
        return new byte[0];
    }

    @Override
    public Long incr(byte[] key) {
        return null;
    }

    @Override
    public Long incrby(byte[] key, long amount) {
        return null;
    }

    @Override
    public Double incrbyfloat(byte[] key, double amount) {
        return null;
    }

    @Override
    public List<KeyValue<byte[], byte[]>> mget(byte[]... keys) {
        return null;
    }

    @Override
    public Long mget(KeyValueStreamingChannel<byte[], byte[]> channel, byte[]... keys) {
        return null;
    }

    @Override
    public String mset(Map<byte[], byte[]> map) {
        return null;
    }

    @Override
    public Boolean msetnx(Map<byte[], byte[]> map) {
        return null;
    }

    @Override
    public String set(byte[] key, byte[] value) {
        return null;
    }

    @Override
    public String set(byte[] key, byte[] value, SetArgs setArgs) {
        return null;
    }

    @Override
    public Long setbit(byte[] key, long offset, int value) {
        return null;
    }

    @Override
    public String setex(byte[] key, long seconds, byte[] value) {
        return null;
    }

    @Override
    public String psetex(byte[] key, long milliseconds, byte[] value) {
        return null;
    }

    @Override
    public Boolean setnx(byte[] key, byte[] value) {
        return null;
    }

    @Override
    public Long setrange(byte[] key, long offset, byte[] value) {
        return null;
    }

    @Override
    public Long strlen(byte[] key) {
        return null;
    }

    @Override
    public String discard() {
        return null;
    }

    @Override
    public TransactionResult exec() {
        return null;
    }

    @Override
    public String multi() {
        return null;
    }

    @Override
    public String watch(byte[]... keys) {
        return null;
    }

    @Override
    public String unwatch() {
        return null;
    }
}
