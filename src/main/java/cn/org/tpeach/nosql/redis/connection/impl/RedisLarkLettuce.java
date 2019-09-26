package cn.org.tpeach.nosql.redis.connection.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import cn.org.tpeach.nosql.enums.RedisStructure;
import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.exception.ServiceException;
import cn.org.tpeach.nosql.redis.connection.RedisLark;
import cn.org.tpeach.nosql.tools.ArraysUtil;
import cn.org.tpeach.nosql.tools.MapUtils;
import cn.org.tpeach.nosql.tools.StringUtils;
import io.lettuce.core.*;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.sync.NodeSelection;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.output.CommandOutput;
import io.lettuce.core.output.KeyStreamingChannel;
import io.lettuce.core.output.KeyValueStreamingChannel;
import io.lettuce.core.output.ScoredValueStreamingChannel;
import io.lettuce.core.output.ValueStreamingChannel;
import io.lettuce.core.protocol.CommandArgs;
import io.lettuce.core.protocol.CommandType;
import io.lettuce.core.protocol.ProtocolKeyword;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author tyz
 * @Title: RedisLarkLettuce
 * @ProjectName redisLark
 * @Description: TODO
 * @date 2019-09-13 10:11
 * @since 1.0.0
 */
@Slf4j
public class RedisLarkLettuce extends AbstractRedisLark<String,String> {

	public RedisLarkLettuce(String id, String host, int port, String auth) {
		super(id, host, port, auth, StringCodec.UTF8);
	}

	public RedisLarkLettuce(String id, String hostAndPort, String auth) {
		super(id, hostAndPort, auth,  StringCodec.UTF8);
	}
    @Override
    public RedisStructure getRedisStructure() {
        if(client != null){
            return RedisStructure.SINGLE;
        }else if(cluster != null){
            return RedisStructure.CLUSTER;
        }
        return null;
    }


    @Override
    public String select(int db) {
        return (String) executeCommand(c->c.select(db), u-> {
            if(db == 0){
                return "OK";
            }
            throw new ServiceException("-ERR invalid DB index");
        });
    }
    @Override
    public void close() {

		executeCommandConsumer(c->{
			c.getStatefulConnection().close();
			client.shutdownAsync();
		},u->{
			u.getStatefulConnection().close();
			cluster.shutdownAsync();
		});
    }

    @Override
	public ScanIterator<String> scanIterator(int count, String pattren){
		return executeCommand(c ->ScanIterator.scan(c, ScanArgs.Builder.limit(count).match(pattren)),
				u -> ScanIterator.scan(u, ScanArgs.Builder.limit(count).match(pattren)));
	}
	@Override
	public ScanIterator<KeyValue<String, String>> hscanIterator(String key, int count, String pattren){
//		List<KeyValue<String, String>> keys = scan.stream().collect(Collectors.toList());
		return executeCommand(c ->ScanIterator.hscan(c, key,ScanArgs.Builder.limit(count).match(pattren)),
				u -> ScanIterator.hscan(u, key,ScanArgs.Builder.limit(count).match(pattren)));
	}
	@Override
	public ScanIterator<String> sscanIterator(String key, int count, String pattren){
		return executeCommand(c ->ScanIterator.sscan(c,key, ScanArgs.Builder.limit(count).match(pattren)),
				u -> ScanIterator.sscan(u,key, ScanArgs.Builder.limit(count).match(pattren)));
	}
	@Override
	public ScanIterator<ScoredValue<String>> zscanIterator(String key, int count, String pattren){
		return executeCommand(c ->ScanIterator.zscan(c,key, ScanArgs.Builder.limit(count).match(pattren)),
				u -> ScanIterator.zscan(u,key, ScanArgs.Builder.limit(count).match(pattren)));
	}
	//------------------------------------------------------BaseRedisCommands-----------------------------------------------------
	@Override
	public Long publish(String channel, String message) {
		return executeCommand(c -> c.publish(channel, message), u -> u.publish(channel, message));
	}

	@Override
	public List<String> pubsubChannels() {
		return executeCommand(c -> c.pubsubChannels(), u -> u.pubsubChannels());
	}

	@Override
	public List<String> pubsubChannels(String channel) {
		return executeCommand(c -> c.pubsubChannels(channel), u -> u.pubsubChannels(channel));
	}

	@Override
	public Map<String, Long> pubsubNumsub(String... channels) {
		return executeCommand(c -> c.pubsubNumsub(channels), u -> u.pubsubNumsub(channels));
	}

	@Override
	public Long pubsubNumpat() {
		return executeCommand(c -> c.pubsubNumpat(), u -> u.pubsubNumpat());
	}

	@Override
	public String echo(String msg) {
		return executeCommand(c -> c.echo(msg), u -> u.echo(msg));
	}

	@Override
	public List<Object> role() {
		return executeCommand(c -> c.role(), u -> u.role());
	}

	@Override
	public String ping() {
		return executeCommand(c -> c.ping(), u -> u.ping());
	}

	@Override
	public String readOnly() {
		return executeCommand(c -> c.readOnly(), u -> u.readOnly());
	}

	@Override
	public String readWrite() {
		return executeCommand(c -> c.readWrite(), u -> u.readWrite());
	}

	@Override
	public String quit() {
		return executeCommand(c -> c.quit(), u -> u.quit());
	}

	@Override
	public Long waitForReplication(int replicas, long timeout) {
		return executeCommand(c -> c.waitForReplication(replicas, timeout), u -> u.waitForReplication(replicas, timeout));
	}

	@Override
	public <T> T dispatch(ProtocolKeyword protocolKeyword, CommandOutput<String, String, T> commandOutput) {
		return executeCommand(c -> c.dispatch(protocolKeyword, commandOutput), u -> u.dispatch(protocolKeyword, commandOutput));
	}

	@Override
	public <T> T dispatch(ProtocolKeyword protocolKeyword, CommandOutput<String, String, T> commandOutput, CommandArgs<String, String> commandArgs) {
		return executeCommand(c -> c.dispatch(protocolKeyword, commandOutput,commandArgs), u -> u.dispatch(protocolKeyword, commandOutput,commandArgs));
	}

	@Override
	public boolean isOpen() {
		return executeCommand(c -> c.isOpen(), u -> u.isOpen());
	}
	
	@Override
	public void reset() {
		executeCommandConsumer(c -> c.reset(), u -> u.reset());
	}
	//------------------------------------------------------RedisStringCommands-----------------------------------------------------

	@Override
	public Long append(String key, String value) {
		return executeCommand(c -> c.append(key, value), u -> u.append(key, value));
	}

	@Override
	public Long bitcount(String key) {
		return executeCommand(c -> c.bitcount(key), u -> u.bitcount(key));
	}

	@Override
	public Long bitcount(String key, long start, long end) {
		return executeCommand(c -> c.bitcount(key, start, end), u -> u.bitcount(key, start, end));
	}

	@Override
	public List<Long> bitfield(String key, BitFieldArgs bitFieldArgs) {
		return executeCommand(c -> c.bitfield(key, bitFieldArgs), u -> u.bitfield(key, bitFieldArgs));
	}

	@Override
	public Long bitpos(String key, boolean state) {
		return executeCommand(c -> c.bitpos(key, state), u -> u.bitpos(key, state));
	}

	@Override
	public Long bitpos(String key, boolean state, long start) {
		return executeCommand(c -> c.bitpos(key, state, start), u -> u.bitpos(key, state, start));
	}

	@Override
	public Long bitpos(String key, boolean state, long start, long end) {
		return executeCommand(c -> c.bitpos(key, state, start, end), u -> u.bitpos(key, state, start, end));
	}

	@Override
	public Long bitopAnd(String destination, String... keys) {
		return executeCommand(c -> c.bitopAnd(destination, keys), u -> u.bitopAnd(destination, keys));
	}

	@Override
	public Long bitopNot(String destination, String source) {
		return executeCommand(c -> c.bitopNot(destination, source), u -> u.bitopNot(destination, source));
	}

	@Override
	public Long bitopOr(String destination, String... keys) {
		return executeCommand(c -> c.bitopOr(destination, keys), u -> u.bitopOr(destination, keys));
	}

	@Override
	public Long bitopXor(String destination, String... keys) {
		return executeCommand(c -> c.bitopXor(destination, keys), u -> u.bitopXor(destination, keys));
	}

	@Override
	public Long decr(String key) {
		return executeCommand(c -> c.decr(key), u -> u.decr(key));
	}

	@Override
	public Long decrby(String key, long amount) {
		return executeCommand(c -> c.decrby(key, amount), u -> u.decrby(key, amount));
	}

	@Override
	public String get(String key) {
		return executeCommand(c -> c.get(key), u -> u.get(key));
	}

	@Override
	public Long getbit(String key, long offset) {
		return executeCommand(c -> c.getbit(key, offset), u -> u.getbit(key, offset));
	}

	@Override
	public String getrange(String key, long start, long end) {
		return executeCommand(c -> c.getrange(key, start, end), u -> u.getrange(key, start, end));
	}

	@Override
	public String getset(String key, String value) {
		return executeCommand(c -> c.getset(key, value), u -> u.getset(key, value));
	}

	@Override
	public Long incr(String key) {
		return executeCommand(c -> c.incr(key), u -> u.incr(key));
	}

	@Override
	public Long incrby(String key, long amount) {
		return executeCommand(c -> c.incrby(key, amount), u -> u.incrby(key, amount));
	}

	@Override
	public Double incrbyfloat(String key, double amount) {
		return executeCommand(c -> c.incrbyfloat(key, amount), u -> u.incrbyfloat(key, amount));
	}

	@Override
	public List<KeyValue<String, String>> mget(String... keys) {
		return executeCommand(c -> c.mget(keys), u -> u.mget(keys));
	}

	@Override
	public Long mget(KeyValueStreamingChannel<String, String> channel, String... keys) {
		return executeCommand(c -> c.mget(channel, keys), u -> u.mget(channel, keys));
	}

	@Override
	public String mset(Map<String, String> map) {
		return executeCommand(c -> c.mset(map), u -> u.mset(map));
	}

	@Override
	public Boolean msetnx(Map<String, String> map) {
		return executeCommand(c -> c.msetnx(map), u -> u.msetnx(map));
	}

	@Override
	public String set(String key, String value) {
		return executeCommand(c -> c.set(key, value), u -> u.set(key, value));
	}

	@Override
	public String set(String key, String value, SetArgs setArgs) {
		return executeCommand(c -> c.set(key, value, setArgs), u -> u.set(key, value,setArgs));
	}

	@Override
	public Long setbit(String key, long offset, int value) {
		return executeCommand(c -> c.setbit(key, offset, value), u -> u.setbit(key, offset, value));
	}

	@Override
	public String setex(String key, long seconds, String value) {
		return executeCommand(c -> c.setex(key, seconds, value), u -> u.setex(key, seconds, value));
	}

	@Override
	public String psetex(String key, long milliseconds, String value) {
		return executeCommand(c -> c.psetex(key, milliseconds, value), u -> u.psetex(key, milliseconds, value));
	}

	@Override
	public Boolean setnx(String key, String value) {
		return executeCommand(c -> c.setnx(key, value), u -> u.setnx(key, value));
	}

	@Override
	public Long setrange(String key, long offset, String value) {
		return executeCommand(c -> c.setrange(key, offset, value), u -> u.setrange(key, offset, value));
	}
	@Override
	public Long strlen(String key) {
		return executeCommand(c -> c.strlen(key), u -> u.strlen(key));
	}
	//------------------------------------------------------RedisListCommands-----------------------------------------------------

	@Override
	public KeyValue<String, String> blpop(long timeout, String... keys) {
		return executeCommand(c -> c.blpop(timeout, keys), u -> u.blpop(timeout, keys));
	}

	@Override
	public KeyValue<String, String> brpop(long timeout, String... keys) {
		return executeCommand(c -> c.brpop(timeout, keys), u -> u.brpop(timeout, keys));
	}

	@Override
	public String brpoplpush(long timeout, String source, String destination) {
		return executeCommand(c -> c.brpoplpush(timeout, source, destination), u -> u.brpoplpush(timeout, source, destination));
	}

	@Override
	public String lindex(String key, long index) {
		return executeCommand(c -> c.lindex(key, index), u -> u.lindex(key,index));
	}

	@Override
	public Long linsert(String key, boolean before, String pivot, String value) {
		return executeCommand(c -> c.linsert(key, before, pivot, value), u -> u.linsert(key, before, pivot, value));
	}

	@Override
	public Long llen(String key) {
		return executeCommand(c -> c.llen(key), u -> u.llen(key));
	}

	@Override
	public String lpop(String key) {
		return executeCommand(c -> c.lpop(key), u -> u.lpop(key));
	}

	@Override
	public Long lpush(String key, String... values) {
		return executeCommand(c -> c.lpush(key, values), u -> u.lpush(key, values));
	}

	@Override
	public Long lpushx(String key, String... values) {
		return executeCommand(c -> c.lpushx(key, values), u -> u.lpushx(key, values));
	}

	@Override
	public List<String> lrange(String key, long start, long stop) {
		return executeCommand(c -> c.lrange(key, start, stop), u -> u.lrange(key, start, stop));
	}

	@Override
	public Long lrange(ValueStreamingChannel<String> channel, String key, long start, long stop) {
		return executeCommand(c -> c.lrange(channel, key, start, stop), u -> u.lrange(channel, key, start, stop));
	}

	@Override
	public Long lrem(String key, long count, String value) {
		return executeCommand(c -> c.lrem(key, count, value), u -> u.lrem(key, count, value));
	}

	@Override
	public String lset(String key, long index, String value) {
		return executeCommand(c -> c.lset(key, index, value), u -> u.lset(key, index, value));
	}

	@Override
	public String ltrim(String key, long start, long stop) {
		return executeCommand(c -> c.ltrim(key, start, stop), u -> u.ltrim(key, start, stop));
	}

	@Override
	public String rpop(String key) {
		return executeCommand(c -> c.rpop(key), u -> u.rpop(key));
	}

	@Override
	public String rpoplpush(String source, String destination) {
		return executeCommand(c -> c.rpoplpush(source, destination), u -> u.rpoplpush(source, destination));
	}

	@Override
	public Long rpush(String key, String... values) {
		return executeCommand(c -> c.rpush(key, values), u -> u.rpush(key, values));
	}


	@Override
	public Long rpushx(String key, String... values) {
		return executeCommand(c -> c.rpushx(key, values), u -> u.rpushx(key, values));
	}
	//------------------------------------------------------RedisHashCommands-----------------------------------------------------
	@Override
	public Long hdel(String key, String... fields) {
		return executeCommand(c -> c.hdel(key, fields), u -> u.hdel(key, fields));
	}

	@Override
	public Boolean hexists(String key, String field) {
		return executeCommand(c -> c.hexists(key, field), u -> u.hexists(key, field));
	}

	@Override
	public String hget(String key, String field) {
		return executeCommand(c -> c.hget(key, field), u -> u.hget(key, field));
	}

	@Override
	public Long hincrby(String key, String field, long amount) {
		return executeCommand(c -> c.hincrby(key, field, amount), u -> u.hincrby(key, field, amount));
	}

	@Override
	public Double hincrbyfloat(String key, String field, double amount) {
		return executeCommand(c -> c.hincrbyfloat(key, field, amount), u -> u.hincrbyfloat(key, field, amount));
	}

	@Override
	public Map<String, String> hgetall(String key) {
		return executeCommand(c -> c.hgetall(key), u -> u.hgetall(key));
	}

	@Override
	public Long hgetall(KeyValueStreamingChannel<String, String> channel, String key) {
		return executeCommand(c -> c.hgetall(channel, key), u -> u.hgetall(channel, key));
	}

	@Override
	public List<String> hkeys(String key) {
		return executeCommand(c -> c.hkeys(key), u -> u.hkeys(key));
	}

	@Override
	public Long hkeys(KeyStreamingChannel<String> channel, String key) {
		return executeCommand(c -> c.hkeys(channel, key), u -> u.hkeys(channel, key));
	}

	@Override
	public Long hlen(String key) {
		return executeCommand(c -> c.hlen(key), u -> u.hlen(key));
	}

	@Override
	public List<KeyValue<String, String>> hmget(String key, String... fields) {
		return executeCommand(c -> c.hmget(key, fields), u -> u.hmget(key, fields));
	}

	@Override
	public Long hmget(KeyValueStreamingChannel<String, String> channel, String key, String... fields) {
		return executeCommand(c -> c.hmget(channel, key, fields), u -> u.hmget(channel, key, fields));
	}

	@Override
	public String hmset(String key, Map<String, String> map) {
		return executeCommand(c -> c.hmset(key, map), u -> u.hmset(key, map));
	}

	@Override
	public MapScanCursor<String, String> hscan(String key) {
		return executeCommand(c -> c.hscan(key), u -> u.hscan(key));
	}

	@Override
	public MapScanCursor<String, String> hscan(String key, ScanArgs scanArgs) {
		return executeCommand(c -> c.hscan(key, scanArgs), u -> u.hscan(key, scanArgs));
	}

	@Override
	public MapScanCursor<String, String> hscan(String key, ScanCursor scanCursor, ScanArgs scanArgs) {
		return executeCommand(c -> c.hscan(key, scanCursor, scanArgs), u -> u.hscan(key, scanCursor, scanArgs));
	}

	@Override
	public MapScanCursor<String, String> hscan(String key, ScanCursor scanCursor) {
		return executeCommand(c -> c.hscan(key, scanCursor), u -> u.hscan(key, scanCursor));
	}

	@Override
	public StreamScanCursor hscan(KeyValueStreamingChannel<String, String> channel, String key) {
		return executeCommand(c -> c.hscan(channel, key), u -> u.hscan(channel, key));
	}

	@Override
	public StreamScanCursor hscan(KeyValueStreamingChannel<String, String> channel, String key, ScanArgs scanArgs) {
		return executeCommand(c -> c.hscan(channel, key, scanArgs), u -> u.hscan(channel, key, scanArgs));
	}

	@Override
	public StreamScanCursor hscan(KeyValueStreamingChannel<String, String> channel, String key, ScanCursor scanCursor, ScanArgs scanArgs) {
		return executeCommand(c -> c.hscan(channel, key, scanCursor, scanArgs), u -> u.hscan(channel, key, scanCursor, scanArgs));
	}

	@Override
	public StreamScanCursor hscan(KeyValueStreamingChannel<String, String> channel, String key, ScanCursor scanCursor) {
		return executeCommand(c -> c.hscan(channel, key, scanCursor), u -> u.hscan(channel, key, scanCursor));
	}

	@Override
	public Boolean hset(String key, String field, String value) {
		return executeCommand(c -> c.hset(key, field, value), u -> u.hset(key, field, value));
	}

	@Override
	public Boolean hsetnx(String key, String field, String value) {
		return executeCommand(c -> c.hsetnx(key, field, value), u -> u.hsetnx(key, field, value));
	}

	@Override
	public Long hstrlen(String key, String field) {
		return executeCommand(c -> c.hstrlen(key, field), u -> u.hstrlen(key, field));
	}

	@Override
	public List<String> hvals(String key) {
		return executeCommand(c -> c.hvals(key), u -> u.hvals(key));
	}

	@Override
	public Long hvals(ValueStreamingChannel<String> channel, String key) {
		return executeCommand(c -> c.hvals(channel, key), u -> u.hvals(channel, key));
	}
	//------------------------------------------------------RedisSetCommands-----------------------------------------------------

	@Override
	public Long sadd(String key, String... members) {
		return executeCommand(c -> c.sadd(key, members), u -> u.sadd(key, members));
	}

	@Override
	public Long scard(String key) {
		return executeCommand(c -> c.scard(key), u -> u.scard(key));
	}

	@Override
	public Set<String> sdiff(String... keys) {
		return executeCommand(c -> c.sdiff(keys), u -> u.sdiff(keys));
	}

	@Override
	public Long sdiff(ValueStreamingChannel<String> channel, String... keys) {
		return executeCommand(c -> c.sdiff(channel, keys), u -> u.sdiff(channel,keys));
	}

	@Override
	public Long sdiffstore(String destination, String... keys) {
		return executeCommand(c -> c.sdiffstore(destination, keys), u -> u.sdiffstore(destination, keys));
	}

	@Override
	public Set<String> sinter(String... keys) {
		return executeCommand(c -> c.sinter(keys), u -> u.sinter(keys));
	}

	@Override
	public Long sinter(ValueStreamingChannel<String> channel, String... keys) {
		return executeCommand(c -> c.sinter(channel, keys), u -> u.sinter(channel, keys));
	}

	@Override
	public Long sinterstore(String destination, String... keys) {
		return executeCommand(c -> c.sinterstore(destination, keys), u -> u.sinterstore(destination, keys));
	}

	@Override
	public Boolean sismember(String key, String member) {
		return executeCommand(c -> c.sismember(key, member), u -> u.sismember(key, member));
	}

	@Override
	public Boolean smove(String source, String destination, String member) {
		return executeCommand(c -> c.smove(source, destination, member), u -> u.smove(source, destination, member));
	}

	@Override
	public Set<String> smembers(String key) {
		return executeCommand(c -> c.smembers(key), u -> u.smembers(key));
	}

	@Override
	public Long smembers(ValueStreamingChannel<String> channel, String key) {
		return executeCommand(c -> c.smembers(channel, key), u -> u.smembers(channel, key));
	}

	@Override
	public String spop(String key) {
		return executeCommand(c -> c.spop(key), u -> u.spop(key));
	}

	@Override
	public Set<String> spop(String key, long count) {
		return executeCommand(c -> c.spop(key, count), u -> u.spop(key, count));
	}

	@Override
	public String srandmember(String key) {
		return executeCommand(c -> c.srandmember(key), u -> u.srandmember(key));
	}

	@Override
	public List<String> srandmember(String key, long count) {
		return executeCommand(c -> c.srandmember(key, count), u -> u.srandmember(key, count));
	}

	@Override
	public Long srandmember(ValueStreamingChannel<String> channel, String key, long count) {
		return executeCommand(c -> c.srandmember(channel, key, count), u -> u.srandmember(channel, key, count));
	}

	@Override
	public Long srem(String key, String... members) {
		return executeCommand(c -> c.srem(key, members), u -> u.srem(key, members));
	}

	@Override
	public Set<String> sunion(String... keys) {
		return executeCommand(c -> c.sunion(keys), u -> u.sunion(keys));
	}

	@Override
	public Long sunion(ValueStreamingChannel<String> channel, String... keys) {
		return executeCommand(c -> c.sunion(channel, keys), u -> u.sunion(channel, keys));
	}

	@Override
	public Long sunionstore(String destination, String... keys) {
		return executeCommand(c -> c.sunionstore(destination, keys), u -> u.sunionstore(destination, keys));
	}

	@Override
	public ValueScanCursor<String> sscan(String key) {
		return executeCommand(c -> c.sscan(key), u -> u.sscan(key));
	}

	@Override
	public ValueScanCursor<String> sscan(String key, ScanArgs scanArgs) {
		return executeCommand(c -> c.sscan(key, scanArgs), u -> u.sscan(key, scanArgs));
	}

	@Override
	public ValueScanCursor<String> sscan(String key, ScanCursor scanCursor, ScanArgs scanArgs) {
		return executeCommand(c -> c.sscan(key, scanCursor, scanArgs), u -> u.sscan(key, scanCursor, scanArgs));
	}

	@Override
	public ValueScanCursor<String> sscan(String key, ScanCursor scanCursor) {
		return executeCommand(c -> c.sscan(key, scanCursor), u -> u.sscan(key, scanCursor));
	}

	@Override
	public StreamScanCursor sscan(ValueStreamingChannel<String> channel, String key) {
		return executeCommand(c -> c.sscan(channel, key), u -> u.sscan(channel, key));
	}

	@Override
	public StreamScanCursor sscan(ValueStreamingChannel<String> channel, String key, ScanArgs scanArgs) {
		return executeCommand(c -> c.sscan(channel, key, scanArgs), u -> u.sscan(channel, key, scanArgs));
	}

	@Override
	public StreamScanCursor sscan(ValueStreamingChannel<String> channel, String key, ScanCursor scanCursor, ScanArgs scanArgs) {
		return executeCommand(c -> c.sscan(channel, key, scanCursor, scanArgs), u -> u.sscan(channel, key, scanCursor, scanArgs));
	}
	
	@Override
	public StreamScanCursor sscan(ValueStreamingChannel<String> channel, String key, ScanCursor scanCursor) {
		return executeCommand(c -> c.sscan(channel, key, scanCursor), u -> u.sscan(channel, key, scanCursor));
	}
	//------------------------------------------------------RedisSortedSetCommands-----------------------------------------------------
	@Override
	public KeyValue<String, ScoredValue<String>> bzpopmin(long timeout, String... keys) {
		return executeCommand(c -> c.bzpopmin(timeout, keys), u -> u.bzpopmin(timeout, keys));
	}

	@Override
	public KeyValue<String, ScoredValue<String>> bzpopmax(long timeout, String... keys) {
		return executeCommand(c -> c.bzpopmax(timeout, keys), u -> u.bzpopmax(timeout, keys));
	}

	@Override
	public Long zadd(String key, double score, String member) {
		return executeCommand(c -> c.zadd(key, score, member), u -> u.zadd(key, score, member));
	}

	@Override
	public Long zadd(String key, Object... scoresAndValues) {
		return executeCommand(c -> c.zadd(key, scoresAndValues), u -> u.zadd(key, scoresAndValues));
	}

	@Override
	public Long zadd(String key, ScoredValue<String>... scoredValues) {
		return executeCommand(c -> c.zadd(key, scoredValues), u -> u.zadd(key, scoredValues));
	}

	@Override
	public Long zadd(String key, ZAddArgs zAddArgs, double score, String member) {
		return executeCommand(c -> c.zadd(key, zAddArgs, score, member), u -> u.zadd(key, zAddArgs, score, member));
	}

	@Override
	public Long zadd(String key, ZAddArgs zAddArgs, Object... scoresAndValues) {
		return executeCommand(c -> c.zadd(key, zAddArgs, scoresAndValues), u -> u.zadd(key, zAddArgs, scoresAndValues));
	}

	@Override
	public Long zadd(String key, ZAddArgs zAddArgs, ScoredValue<String>... scoredValues) {
		return executeCommand(c -> c.zadd(key, zAddArgs, scoredValues), u -> u.zadd(key, zAddArgs, scoredValues));
	}

	@Override
	public Double zaddincr(String key, double score, String member) {
		return executeCommand(c -> c.zaddincr(key, score, member), u -> u.zaddincr(key, score, member));
	}

	@Override
	public Double zaddincr(String key, ZAddArgs zAddArgs, double score, String member) {
		return executeCommand(c -> c.zaddincr(key, zAddArgs, score, member), u -> u.zaddincr(key, zAddArgs, score, member));
	}

	@Override
	public Long zcard(String key) {
		return executeCommand(c -> c.zcard(key), u -> u.zcard(key));
	}

	@Override
	public Long zcount(String key, double min, double max) {
		return executeCommand(c -> c.zcount(key, min, max), u -> u.zcount(key, min, max));
	}

	@Override
	public Long zcount(String key, String min, String max) {
		return executeCommand(c -> c.zcount(key, min, max), u -> u.zcount(key, min, max));
	}

	@Override
	public Long zcount(String key, Range<? extends Number> range) {
		return executeCommand(c -> c.zcount(key, range), u -> u.zcount(key, range));
	}

	@Override
	public Double zincrby(String key, double amount, String member) {
		return executeCommand(c -> c.zincrby(key, amount, member), u -> u.zincrby(key, amount, member));
	}

	@Override
	public Long zinterstore(String destination, String... keys) {
		return executeCommand(c -> c.zinterstore(destination, keys), u -> u.zinterstore(destination, keys));
	}

	@Override
	public Long zinterstore(String destination, ZStoreArgs storeArgs, String... keys) {
		return executeCommand(c -> c.zinterstore(destination, storeArgs, keys), u -> u.zinterstore(destination, storeArgs, keys));
	}

	@Override
	public Long zlexcount(String key, String min, String max) {
		return executeCommand(c -> c.zlexcount(key, min, max), u -> u.zlexcount(key, min, max));
	}

	@Override
	public Long zlexcount(String key, Range<? extends String> range) {
		return executeCommand(c -> c.zlexcount(key, range), u -> u.zlexcount(key, range));
	}

	@Override
	public ScoredValue<String> zpopmin(String key) {
		return executeCommand(c -> c.zpopmin(key), u -> u.zpopmin(key));
	}

	@Override
	public List<ScoredValue<String>> zpopmin(String key, long count) {
		return executeCommand(c -> c.zpopmin(key, count), u -> u.zpopmin(key, count));
	}

	@Override
	public ScoredValue<String> zpopmax(String key) {
		return executeCommand(c -> c.zpopmax(key), u -> u.zpopmax(key));
	}

	@Override
	public List<ScoredValue<String>> zpopmax(String key, long count) {
		return executeCommand(c -> c.zpopmax(key, count), u -> u.zpopmax(key, count));
	}

	@Override
	public List<String> zrange(String key, long start, long stop) {
		return executeCommand(c -> c.zrange(key, start, stop), u -> u.zrange(key, start, stop));
	}

	@Override
	public Long zrange(ValueStreamingChannel<String> channel,String key, long start, long stop) {
		return executeCommand(c -> c.zrange(channel,key, start, stop), u -> u.zrange(channel,key, start, stop));
	}

	@Override
	public List<ScoredValue<String>> zrangeWithScores(String key, long start, long stop) {
		return executeCommand(c -> c.zrangeWithScores(key, start, stop), u -> u.zrangeWithScores(key, start, stop));
	}

	@Override
	public Long zrangeWithScores(ScoredValueStreamingChannel<String> channel, String key, long start, long stop) {
		return executeCommand(c -> c.zrangeWithScores(channel, key, start, stop), u -> u.zrangeWithScores(channel, key, start, stop));
	}

	@Override
	public List<String> zrangebylex(String key, String min, String max) {
		return executeCommand(c -> c.zrangebylex(key, min, max), u -> u.zrangebylex(key, min, max));
	}

	@Override
	public List<String> zrangebylex(String key, Range<? extends String> range) {
		return executeCommand(c -> c.zrangebylex(key, range), u -> u.zrangebylex(key, range));
	}

	@Override
	public List<String> zrangebylex(String key, String min, String max, long offset, long count) {
		return executeCommand(c -> c.zrangebylex(key, min, max, offset, count), u -> u.zrangebylex(key, min, max, offset, count));
	}

	@Override
	public List<String> zrangebylex(String key, Range<? extends String> range, Limit limit) {
		return executeCommand(c -> c.zrangebylex(key, range, limit), u -> u.zrangebylex(key, range, limit));
	}

	@Override
	public List<String> zrangebyscore(String key, double min, double max) {
		return executeCommand(c -> c.zrangebyscore(key, min, max), u -> u.zrangebyscore(key, min, max));
	}

	@Override
	public List<String> zrangebyscore(String key, String min, String max) {
		return executeCommand(c -> c.zrangebyscore(key, min, max), u -> u.zrangebyscore(key, min, max));
	}

	@Override
	public List<String> zrangebyscore(String key, Range<? extends Number> range) {
		return executeCommand(c -> c.zrangebyscore(key, range), u -> u.zrangebyscore(key, range));
	}

	@Override
	public List<String> zrangebyscore(String key, double min, double max, long offset, long count) {
		return executeCommand(c -> c.zrangebyscore(key, min, max, offset, count), u -> u.zrangebyscore(key, min, max, offset, count));
	}

	@Override
	public List<String> zrangebyscore(String key, String min, String max, long offset, long count) {
		return executeCommand(c -> c.zrangebyscore(key, min, max, offset, count), u -> u.zrangebyscore(key, min, max, offset, count));
	}

	@Override
	public List<String> zrangebyscore(String key, Range<? extends Number> range, Limit limit) {
		return executeCommand(c -> c.zrangebyscore(key, range, limit), u -> u.zrangebyscore(key, range, limit));
	}

	@Override
	public Long zrangebyscore(ValueStreamingChannel<String> channel, String key, double min, double max) {
		return executeCommand(c -> c.zrangebyscore(channel, key, min, max), u -> u.zrangebyscore(channel, key, min, max));
	}

	@Override
	public Long zrangebyscore(ValueStreamingChannel<String> channel, String key, String min, String max) {
		return executeCommand(c -> c.zrangebyscore(channel, key, min, max), u -> u.zrangebyscore(channel, key, min, max));
	}

	@Override
	public Long zrangebyscore(ValueStreamingChannel<String> channel, String key, Range<? extends Number> range) {
		return executeCommand(c -> c.zrangebyscore(channel, key, range), u -> u.zrangebyscore(channel, key, range));
	}

	@Override
	public Long zrangebyscore(ValueStreamingChannel<String> channel, String key, double min, double max, long offset, long count) {
		return executeCommand(c -> c.zrangebyscore(channel, key, min, max, offset, count), u -> u.zrangebyscore(channel, key, min, max, offset, count));
	}

	@Override
	public Long zrangebyscore(ValueStreamingChannel<String> channel, String key, String min, String max, long offset, long count) {
		return executeCommand(c -> c.zrangebyscore(channel, key, min, max, offset, count), u -> u.zrangebyscore(channel, key, min, max, offset, count));
	}

	@Override
	public Long zrangebyscore(ValueStreamingChannel<String> channel, String key, Range<? extends Number> range, Limit limit) {
		return executeCommand(c -> c.zrangebyscore(channel, key, range, limit), u -> u.zrangebyscore(channel, key, range, limit));
	}

	@Override
	public List<ScoredValue<String>> zrangebyscoreWithScores(String key, double min, double max) {
		return executeCommand(c -> c.zrangebyscoreWithScores(key, min, max), u -> u.zrangebyscoreWithScores(key, min, max));
	}

	@Override
	public List<ScoredValue<String>> zrangebyscoreWithScores(String key, String min, String max) {
		return executeCommand(c -> c.zrangebyscoreWithScores(key, min, max), u -> u.zrangebyscoreWithScores(key, min, max));
	}

	@Override
	public List<ScoredValue<String>> zrangebyscoreWithScores(String key, Range<? extends Number> range) {
		return executeCommand(c -> c.zrangebyscoreWithScores(key,range), u -> u.zrangebyscoreWithScores(key,range));
	}

	@Override
	public List<ScoredValue<String>> zrangebyscoreWithScores(String key, double min, double max, long offset, long count) {
		return executeCommand(c -> c.zrangebyscoreWithScores(key, min, max, offset, count), u -> u.zrangebyscoreWithScores(key, min, max, offset, count));
	}

	@Override
	public List<ScoredValue<String>> zrangebyscoreWithScores(String key, String min, String max, long offset, long count) {
		return executeCommand(c -> c.zrangebyscoreWithScores(key, min, max, offset, count), u -> u.zrangebyscoreWithScores(key, min, max, offset, count));
	}

	@Override
	public List<ScoredValue<String>> zrangebyscoreWithScores(String key, Range<? extends Number> range, Limit limit) {
		return executeCommand(c -> c.zrangebyscoreWithScores(key, range, limit), u -> u.zrangebyscoreWithScores(key, range, limit));
	}

	@Override
	public Long zrangebyscoreWithScores(ScoredValueStreamingChannel<String> channel, String key, double min, double max) {
		return executeCommand(c -> c.zrangebyscoreWithScores(channel, key, min, max), u -> u.zrangebyscoreWithScores(channel, key, min, max));
	}

	@Override
	public Long zrangebyscoreWithScores(ScoredValueStreamingChannel<String> channel, String key, String min, String max) {
		return executeCommand(c -> c.zrangebyscoreWithScores(channel, key, min, max), u -> u.zrangebyscoreWithScores(channel, key, min, max));
	}

	@Override
	public Long zrangebyscoreWithScores(ScoredValueStreamingChannel<String> channel, String key, Range<? extends Number> range) {
		return executeCommand(c -> c.zrangebyscoreWithScores(channel, key, range), u -> u.zrangebyscoreWithScores(channel, key, range));
	}

	@Override
	public Long zrangebyscoreWithScores(ScoredValueStreamingChannel<String> channel, String key, double min, double max, long offset, long count) {
		return executeCommand(c -> c.zrangebyscoreWithScores(channel, key, min, max, offset, count), u -> u.zrangebyscoreWithScores(channel, key, min, max, offset, count));
	}

	@Override
	public Long zrangebyscoreWithScores(ScoredValueStreamingChannel<String> channel, String key, String min, String max, long offset, long count) {
		return executeCommand(c -> c.zrangebyscoreWithScores(channel, key, min, max, offset, count), u -> u.zrangebyscoreWithScores(channel, key, min, max, offset, count));
	}

	@Override
	public Long zrangebyscoreWithScores(ScoredValueStreamingChannel<String> channel, String key, Range<? extends Number> range, Limit limit) {
		return executeCommand(c -> c.zrangebyscoreWithScores(channel, key, range, limit), u -> u.zrangebyscoreWithScores(channel, key, range, limit));
	}

	@Override
	public Long zrank(String key, String member) {
		return executeCommand(c -> c.zrank(key, member), u -> u.zrank(key, member));
	}

	@Override
	public Long zrem(String key, String... members) {
		return executeCommand(c -> c.zrem(key, members), u -> u.zrem(key, members));
	}

	@Override
	public Long zremrangebylex(String key, String min, String max) {
		return executeCommand(c -> c.zremrangebylex(key, min, max), u -> u.zremrangebylex(key, min, max));
	}

	@Override
	public Long zremrangebylex(String key, Range<? extends String> range) {
		return executeCommand(c -> c.zremrangebylex(key, range), u -> u.zremrangebylex(key, range));
	}

	@Override
	public Long zremrangebyrank(String key, long start, long stop) {
		return executeCommand(c -> c.zremrangebyrank(key, start, stop), u -> u.zremrangebyrank(key, start, stop));
	}

	@Override
	public Long zremrangebyscore(String key, double min, double max) {
		return executeCommand(c -> c.zremrangebyscore(key, min, max), u -> u.zremrangebyscore(key, min, max));
	}

	@Override
	public Long zremrangebyscore(String key, String min, String max) {
		return executeCommand(c -> c.zremrangebyscore(key, min, max), u -> u.zremrangebyscore(key, min, max));
	}

	@Override
	public Long zremrangebyscore(String key, Range<? extends Number> range) {
		return executeCommand(c -> c.zremrangebyscore(key, range), u -> u.zremrangebyscore(key, range));
	}

	@Override
	public List<String> zrevrange(String key, long start, long stop) {
		return executeCommand(c -> c.zrevrange(key, start, stop), u -> u.zrevrange(key, start, stop));
	}

	@Override
	public Long zrevrange(ValueStreamingChannel<String> channel, String key, long start, long stop) {
		return executeCommand(c -> c.zrevrange(channel, key, start, stop), u -> u.zrevrange(channel, key, start, stop));
	}

	@Override
	public List<ScoredValue<String>> zrevrangeWithScores(String key, long start, long stop) {
		return executeCommand(c -> c.zrevrangeWithScores(key, start, stop), u -> u.zrevrangeWithScores(key, start, stop));
	}

	@Override
	public Long zrevrangeWithScores(ScoredValueStreamingChannel<String> channel, String key, long start, long stop) {
		return executeCommand(c -> c.zrevrangeWithScores(channel, key, start, stop), u -> u.zrevrangeWithScores(channel, key, start, stop));
	}

	@Override
	public List<String> zrevrangebylex(String key, Range<? extends String> range) {
		return executeCommand(c -> c.zrevrangebylex(key, range), u -> u.zrevrangebylex(key, range));
	}

	@Override
	public List<String> zrevrangebylex(String key, Range<? extends String> range, Limit limit) {
		return executeCommand(c -> c.zrevrangebylex(key, range, limit), u -> u.zrevrangebylex(key, range, limit));
	}

	@Override
	public List<String> zrevrangebyscore(String key, double max, double min) {
		return executeCommand(c -> c.zrevrangebyscore(key, max, min), u -> u.zrevrangebyscore(key, max, min));
	}

	@Override
	public List<String> zrevrangebyscore(String key, String max, String min) {
		return executeCommand(c -> c.zrevrangebyscore(key, max, min), u -> u.zrevrangebyscore(key, max, min));
	}

	@Override
	public List<String> zrevrangebyscore(String key, Range<? extends Number> range) {
		return executeCommand(c -> c.zrevrangebyscore(key, range), u -> u.zrevrangebyscore(key, range));
	}

	@Override
	public List<String> zrevrangebyscore(String key, double max, double min, long offset, long count) {
		return executeCommand(c -> c.zrevrangebyscore(key, max, min, offset, count), u -> u.zrevrangebyscore(key, max, min, offset, count));
	}

	@Override
	public List<String> zrevrangebyscore(String key, String max, String min, long offset, long count) {
		return executeCommand(c -> c.zrevrangebyscore(key, max, min, offset, count), u -> u.zrevrangebyscore(key, max, min, offset, count));
	}

	@Override
	public List<String> zrevrangebyscore(String key, Range<? extends Number> range, Limit limit) {
		return executeCommand(c -> c.zrevrangebyscore(key, range, limit), u -> u.zrevrangebyscore(key, range, limit));
	}

	@Override
	public Long zrevrangebyscore(ValueStreamingChannel<String> channel, String key, double max, double min) {
		return executeCommand(c -> c.zrevrangebyscore(channel, key, max, min), u -> u.zrevrangebyscore(channel, key, max, min));
	}

	@Override
	public Long zrevrangebyscore(ValueStreamingChannel<String> channel, String key, String max, String min) {
		return executeCommand(c -> c.zrevrangebyscore(channel, key, max, min), u -> u.zrevrangebyscore(channel, key, max, min));
	}

	@Override
	public Long zrevrangebyscore(ValueStreamingChannel<String> channel, String key, Range<? extends Number> range) {
		return executeCommand(c -> c.zrevrangebyscore(channel, key, range), u -> u.zrevrangebyscore(channel, key, range));
	}

	@Override
	public Long zrevrangebyscore(ValueStreamingChannel<String> channel, String key, double max, double min, long offset, long count) {
		return executeCommand(c -> c.zrevrangebyscore(channel, key, max, min, offset, count), u -> u.zrevrangebyscore(channel, key, max, min, offset, count));
	}

	@Override
	public Long zrevrangebyscore(ValueStreamingChannel<String> channel, String key, String max, String min, long offset, long count) {
		return executeCommand(c -> c.zrevrangebyscore(channel, key, max, min, offset, count), u -> u.zrevrangebyscore(channel, key, max, min, offset, count));
	}

	@Override
	public Long zrevrangebyscore(ValueStreamingChannel<String> channel, String key, Range<? extends Number> range, Limit limit) {
		return executeCommand(c -> c.zrevrangebyscore(channel, key, range, limit), u -> u.zrevrangebyscore(channel, key, range, limit));
	}

	@Override
	public List<ScoredValue<String>> zrevrangebyscoreWithScores(String key, double max, double min) {
		return executeCommand(c -> c.zrevrangebyscoreWithScores(key, max, min), u -> u.zrevrangebyscoreWithScores(key, max, min));
	}

	@Override
	public List<ScoredValue<String>> zrevrangebyscoreWithScores(String key, String max, String min) {
		return executeCommand(c -> c.zrevrangebyscoreWithScores(key, max, min), u -> u.zrevrangebyscoreWithScores(key, max, min));
	}

	@Override
	public List<ScoredValue<String>> zrevrangebyscoreWithScores(String key, Range<? extends Number> range) {
		return executeCommand(c -> c.zrevrangebyscoreWithScores(key, range), u -> u.zrevrangebyscoreWithScores(key, range));
	}

	@Override
	public List<ScoredValue<String>> zrevrangebyscoreWithScores(String key, double max, double min, long offset, long count) {
		return executeCommand(c -> c.zrevrangebyscoreWithScores(key, max, min, offset, count), u -> u.zrevrangebyscoreWithScores(key, max, min, offset, count));
	}

	@Override
	public List<ScoredValue<String>> zrevrangebyscoreWithScores(String key, String max, String min, long offset, long count) {
		return executeCommand(c -> c.zrevrangebyscoreWithScores(key, max, max, offset, count), u -> u.zrevrangebyscoreWithScores(key, max, min, offset, count));
	}

	@Override
	public List<ScoredValue<String>> zrevrangebyscoreWithScores(String key, Range<? extends Number> range, Limit limit) {
		return executeCommand(c -> c.zrevrangebyscoreWithScores(key, range, limit), u -> u.zrevrangebyscoreWithScores(key, range, limit));
	}

	@Override
	public Long zrevrangebyscoreWithScores(ScoredValueStreamingChannel<String> channel, String key, double max, double min) {
		return executeCommand(c -> c.zrevrangebyscoreWithScores(channel, key, max, min), u -> u.zrevrangebyscoreWithScores(channel, key, max, min));
	}

	@Override
	public Long zrevrangebyscoreWithScores(ScoredValueStreamingChannel<String> channel, String key, String max, String min) {
		return executeCommand(c -> c.zrevrangebyscoreWithScores(channel, key, max, min), u -> u.zrevrangebyscoreWithScores(channel, key, max, min));
	}

	@Override
	public Long zrevrangebyscoreWithScores(ScoredValueStreamingChannel<String> channel, String key, Range<? extends Number> range) {
		return executeCommand(c -> c.zrevrangebyscoreWithScores(channel, key, range), u -> u.zrevrangebyscoreWithScores(channel, key, range));
	}

	@Override
	public Long zrevrangebyscoreWithScores(ScoredValueStreamingChannel<String> channel, String key, double max, double min, long offset, long count) {
		return executeCommand(c -> c.zrevrangebyscoreWithScores(channel,key, max, min, offset, count), u -> u.zrevrangebyscoreWithScores(channel,key, max, min, offset, count));
	}

	@Override
	public Long zrevrangebyscoreWithScores(ScoredValueStreamingChannel<String> channel, String key, String max, String min, long offset, long count) {
		return executeCommand(c -> c.zrevrangebyscoreWithScores(channel,key, max, min, offset, count), u -> u.zrevrangebyscoreWithScores(channel,key, max, min, offset, count));
	}

	@Override
	public Long zrevrangebyscoreWithScores(ScoredValueStreamingChannel<String> channel, String key, Range<? extends Number> range, Limit limit) {
		return executeCommand(c -> c.zrevrangebyscoreWithScores(channel, key, range, limit), u -> u.zrevrangebyscoreWithScores(channel, key, range, limit));
	}

	@Override
	public Long zrevrank(String key, String member) {
		return executeCommand(c -> c.zrevrank(key, member), u -> u.zrevrank(key, member));
	}

	@Override
	public ScoredValueScanCursor<String> zscan(String key) {
		return executeCommand(c -> c.zscan(key), u -> u.zscan(key));
	}

	@Override
	public ScoredValueScanCursor<String> zscan(String key, ScanArgs scanArgs) {
		return executeCommand(c -> c.zscan(key, scanArgs), u -> u.zscan(key, scanArgs));
	}

	@Override
	public ScoredValueScanCursor<String> zscan(String key, ScanCursor scanCursor, ScanArgs scanArgs) {
		return executeCommand(c -> c.zscan(key, scanCursor, scanArgs), u -> u.zscan(key, scanCursor, scanArgs));
	}

	@Override
	public ScoredValueScanCursor<String> zscan(String key, ScanCursor scanCursor) {
		return executeCommand(c -> c.zscan(key, scanCursor), u -> u.zscan(key, scanCursor));
	}

	@Override
	public StreamScanCursor zscan(ScoredValueStreamingChannel<String> channel, String key) {
		return executeCommand(c -> c.zscan(channel, key), u -> u.zscan(channel, key));
	}

	@Override
	public StreamScanCursor zscan(ScoredValueStreamingChannel<String> channel, String key, ScanArgs scanArgs) {
		return executeCommand(c -> c.zscan(channel, key, scanArgs), u -> u.zscan(channel, key, scanArgs));
	}

	@Override
	public StreamScanCursor zscan(ScoredValueStreamingChannel<String> channel, String key, ScanCursor scanCursor, ScanArgs scanArgs) {
		return executeCommand(c -> c.zscan(channel, key, scanCursor, scanArgs), u -> u.zscan(channel, key, scanCursor, scanArgs));
	}

	@Override
	public StreamScanCursor zscan(ScoredValueStreamingChannel<String> channel, String key, ScanCursor scanCursor) {
		return executeCommand(c -> c.zscan(channel, key, scanCursor), u -> u.zscan(channel, key, scanCursor));
	}

	@Override
	public Double zscore(String key, String member) {
		return executeCommand(c -> c.zscore(key, member), u -> u.zscore(key, member));
	}

	@Override
	public Long zunionstore(String destination, String... keys) {
		return executeCommand(c -> c.zunionstore(destination, keys), u -> u.zunionstore(destination, keys));
	}

	@Override
	public Long zunionstore(String destination, ZStoreArgs storeArgs, String... keys) {
		return executeCommand(c -> c.zunionstore(destination, storeArgs, keys), u -> u.zunionstore(destination, storeArgs, keys));
	}
	//------------------------------------------------------RedisKeyCommands-----------------------------------------------------
	@Override
	public Long del(String... keys) {
		return executeCommand(c -> c.del(keys), u -> u.del(keys));
	}

	@Override
	public Long unlink(String... keys) {
		return executeCommand(c -> c.unlink(keys), u -> u.unlink(keys));
	}

	@Override
	public byte[] dump(String key) {
		return executeCommand(c -> c.dump(key), u -> u.dump(key));
	}

	@Override
	public Long exists(String... keys) {
		return executeCommand(c -> c.exists(keys), u -> u.exists(keys));
	}

	@Override
	public Boolean expire(String key, long seconds) {
		return executeCommand(c -> c.expire(key, seconds), u -> u.expire(key, seconds));
	}

	@Override
	public Boolean expireat(String key, Date timestamp) {
		return executeCommand(c -> c.expireat(key, timestamp), u -> u.expireat(key, timestamp));
	}

	@Override
	public Boolean expireat(String key, long timestamp) {
		return executeCommand(c -> c.expireat(key, timestamp), u -> u.expireat(key, timestamp));
	}

	@Override
	public List<String> keys(String pattern) {
		return executeCommand(c -> c.keys(pattern), u ->{
//			NodeSelection<String, String> masters = u.masters();
//			int size = masters.size();
//			NodeSelection<String, String> slaves = u.slaves();
//			int size1 = slaves.size();
//			NodeSelection<String, String> all = u.all();
//			int size2 = all.size();
			return u.keys(pattern);
			
		} );
	}

	@Override
	public Long keys(KeyStreamingChannel<String> channel, String pattern) {
		return executeCommand(c -> c.keys(channel, pattern), u -> u.keys(channel, pattern));
	}

	@Override
	public String migrate(String host, int port, String key, int db, long timeout) {
		return executeCommand(c -> c.migrate(host, port, key, db, timeout), u -> u.migrate(host, port, key, db, timeout));
	}

	@Override
	public String migrate(String host, int port, int db, long timeout, MigrateArgs<String> migrateArgs) {
		return executeCommand(c -> c.migrate(host, port, db, timeout, migrateArgs), u -> u.migrate(host, port, db, timeout, migrateArgs));
	}

	@Override
	public Boolean move(String key, int db) {
		return executeCommand(c -> c.move(key, db), u -> u.move(key, db));
	}

	@Override
	public String objectEncoding(String key) {
		return executeCommand(c -> c.objectEncoding(key), u -> u.objectEncoding(key));
	}

	@Override
	public Long objectIdletime(String key) {
		return executeCommand(c -> c.objectIdletime(key), u -> u.objectIdletime(key));
	}

	@Override
	public Long objectRefcount(String key) {
		return executeCommand(c -> c.objectRefcount(key), u -> u.objectRefcount(key));
	}

	@Override
	public Boolean persist(String key) {
		return executeCommand(c -> c.persist(key), u -> u.persist(key));
	}

	@Override
	public Boolean pexpire(String key, long milliseconds) {
		return executeCommand(c -> c.pexpire(key, milliseconds), u -> u.pexpire(key, milliseconds));
	}

	@Override
	public Boolean pexpireat(String key, Date timestamp) {
		return executeCommand(c -> c.pexpireat(key, timestamp), u -> u.pexpireat(key, timestamp));
	}

	@Override
	public Boolean pexpireat(String key, long timestamp) {
		return executeCommand(c -> c.pexpireat(key, timestamp), u -> u.pexpireat(key, timestamp));
	}

	@Override
	public Long pttl(String key) {
		return executeCommand(c -> c.pttl(key), u -> u.pttl(key));
	}

	@Override
	public String randomkey() {
		return executeCommand(c -> c.randomkey(), u -> u.randomkey());
	}

	@Override
	public String rename(String key, String newKey) {
		return executeCommand(c -> c.rename(key, newKey), u -> u.rename(key, newKey));

	}

	@Override
	public Boolean renamenx(String key, String newKey)  {
		return executeCommand(c -> c.renamenx(key, newKey), u -> {
//			return u.renamenx(key, newKey);
				if(key.equals(newKey)){
					return true;
				}
				if(get(newKey) == null){
					String temp = get(key);
					set(newKey, temp);
					del(key);
					return true;
				}else{
//					log.warn("renamenx:{} exists",newKey);
					throw new ServiceException(newKey+"已存在");
				}
		});
	}

	@Override
	public String restore(String key, long ttl, byte[] value) {
		return executeCommand(c -> c.restore(key, ttl, value), u -> u.restore(key, ttl, value));
	}

	@Override
	public String restore(String key, byte[] value, RestoreArgs args) {
		return executeCommand(c -> c.restore(key, value, args), u -> u.restore(key, value, args));
	}

	@Override
	public List<String> sort(String key) {
		return executeCommand(c -> c.sort(key), u -> u.sort(key));
	}

	@Override
	public Long sort(ValueStreamingChannel<String> channel, String key) {
		return executeCommand(c -> c.sort(channel, key), u -> u.sort(channel, key));
	}

	@Override
	public List<String> sort(String key, SortArgs sortArgs) {
		return executeCommand(c -> c.sort(key, sortArgs), u -> u.sort(key, sortArgs));
	}

	@Override
	public Long sort(ValueStreamingChannel<String> channel, String key, SortArgs sortArgs) {
		return executeCommand(c -> c.sort(channel, key, sortArgs), u -> u.sort(channel, key, sortArgs));
	}

	@Override
	public Long sortStore(String key, SortArgs sortArgs, String destination) {
		return executeCommand(c -> c.sortStore(key, sortArgs, destination), u -> u.sortStore(key, sortArgs, destination));
	}

	@Override
	public Long touch(String... keys) {
		return executeCommand(c -> c.touch(keys), u -> u.touch(keys));
	}

	@Override
	public Long ttl(String key) {
		return executeCommand(c -> c.ttl(key), u -> u.ttl(key));
	}

	@Override
	public String type(String key) {
		return executeCommand(c -> c.type(key), u -> u.type(key));
	}

	@Override
	public KeyScanCursor<String> scan() {
		return executeCommand(c -> c.scan(), u -> u.scan());
	}

	@Override
	public KeyScanCursor<String> scan(ScanArgs scanArgs) {
		return executeCommand(c -> c.scan(scanArgs), u -> u.scan(scanArgs));
	}

	@Override
	public KeyScanCursor<String> scan(ScanCursor scanCursor, ScanArgs scanArgs) {
		return executeCommand(c -> c.scan(scanCursor,scanArgs), u -> u.scan(scanCursor,scanArgs));
	}

	@Override
	public KeyScanCursor<String> scan(ScanCursor scanCursor) {
		return executeCommand(c -> c.scan(scanCursor), u -> u.scan(scanCursor));
	}

	@Override
	public StreamScanCursor scan(KeyStreamingChannel<String> channel) {
		return executeCommand(c -> c.scan(channel), u -> u.scan(channel));
	}

	@Override
	public StreamScanCursor scan(KeyStreamingChannel<String> channel, ScanArgs scanArgs) {
		return executeCommand(c -> c.scan(channel,scanArgs), u -> u.scan(channel,scanArgs));
	}

	@Override
	public StreamScanCursor scan(KeyStreamingChannel<String> channel, ScanCursor scanCursor, ScanArgs scanArgs) {
		return executeCommand(c -> c.scan(channel,scanCursor,scanArgs), u -> u.scan(channel,scanCursor,scanArgs));
	}
	
	@Override
	public StreamScanCursor scan(KeyStreamingChannel<String> channel, ScanCursor scanCursor) {
		return executeCommand(c -> c.scan(channel,scanCursor), u -> u.scan(channel,scanCursor));
	}
	//------------------------------------------------------RedisServerCommands-----------------------------------------------------
	@Override
	public String bgrewriteaof() {
		return executeCommand(c -> c.bgrewriteaof(), u -> u.bgrewriteaof());
	}

	@Override
	public String bgsave() {
		return executeCommand(c -> c.bgsave(), u -> u.bgsave());
	}

	@Override
	public String clientGetname() {
		return executeCommand(c -> c.clientGetname(), u -> u.clientGetname());
	}

	@Override
	public String clientSetname(String name) {
		return executeCommand(c -> c.clientSetname(name), u -> u.clientSetname(name));
	}

	@Override
	public String clientKill(String addr) {
		return executeCommand(c -> c.clientKill(addr), u -> u.clientKill(addr));
	}

	@Override
	public Long clientKill(KillArgs killArgs) {
		return executeCommand(c -> c.clientKill(killArgs), u -> u.clientKill(killArgs));
	}

	@Override
	public Long clientUnblock(long id, UnblockType type) {
		return executeCommand(c -> c.clientUnblock(id, type), u -> u.clientUnblock(id, type));
	}

	@Override
	public String clientPause(long timeout) {
		return executeCommand(c -> c.clientPause(timeout), u -> u.clientPause(timeout));
	}

	@Override
	public String clientList() {
		return executeCommand(c -> c.clientList(), u -> u.clientList());
	}

	@Override
	public List<Object> command() {
		return executeCommand(c -> c.command(), u -> u.command());
	}

	@Override
	public List<Object> commandInfo(String... commands) {
		return executeCommand(c -> c.commandInfo(commands), u -> u.commandInfo(commands));
	}

	@Override
	public List<Object> commandInfo(CommandType... commandTypes) {
		return executeCommand(c -> c.commandInfo(commandTypes), u -> u.commandInfo(commandTypes));
	}

	@Override
	public Long commandCount() {
		return executeCommand(c -> c.commandCount(), u -> u.commandCount());
	}

	@Override
	public Map<String, String> configGet(String parameter) {
		return executeCommand(c -> c.configGet(parameter), u -> u.configGet(parameter));
	}

	@Override
	public String configResetstat() {
		return executeCommand(c -> c.configResetstat(), u -> u.configResetstat());
	}

	@Override
	public String configRewrite() {
		return executeCommand(c -> c.configRewrite(), u -> u.configRewrite());
	}

	@Override
	public String configSet(String parameter, String value) {
		return executeCommand(c -> c.configSet(parameter, value), u -> u.configSet(parameter, value));
	}

	@Override
	public Long dbsize() {
		return executeCommand(c -> c.dbsize(), u -> u.dbsize());
	}

	@Override
	public String debugCrashAndRecover(Long delay) {
		return executeCommand(c -> c.debugCrashAndRecover(delay), u -> u.debugCrashAndRecover(delay));
	}

	@Override
	public String debugHtstats(int db) {
		return executeCommand(c -> c.debugHtstats(db), u -> u.debugHtstats(db));
	}

	@Override
	public String debugObject(String key) {
		return executeCommand(c -> c.debugObject(key), u -> u.debugObject(key));
	}

	@Override
	public void debugOom() {
		executeCommandConsumer(c -> c.debugOom(), u -> u.debugOom());
	}

	@Override
	public void debugSegfault() {
		executeCommandConsumer(c -> c.debugSegfault(), u -> u.debugSegfault());
	}

	@Override
	public String debugReload() {
		return executeCommand(c -> c.debugReload(), u -> u.debugReload());
	}

	@Override
	public String debugRestart(Long delay) {
		return executeCommand(c -> c.debugRestart(delay), u -> u.debugRestart(delay));
	}

	@Override
	public String debugSdslen(String key) {
		return executeCommand(c -> c.debugSdslen(key), u -> u.debugSdslen(key));
	}

	@Override
	public String flushall() {
		return executeCommand(c -> c.flushall(), u -> u.flushall());
	}

	@Override
	public String flushallAsync() {
		return executeCommand(c -> c.flushallAsync(), u -> u.flushallAsync());
	}

	@Override
	public String flushdb() {
		return executeCommand(c -> c.flushdb(), u -> u.flushdb());
	}

	@Override
	public String flushdbAsync() {
		return executeCommand(c -> c.flushdbAsync(), u -> u.flushdbAsync());
	}

	@Override
	public String info() {
		return executeCommand(c -> c.info(), u -> u.info());
	}

	@Override
	public String info(String section) {
		return executeCommand(c -> c.info(section), u -> u.info(section));
	}

	@Override
	public Date lastsave() {
		return executeCommand(c -> c.lastsave(), u -> u.lastsave());
	}

	@Override
	public String save() {
		return executeCommand(c -> c.save(), u -> u.save());
	}

	@Override
	public void shutdown(boolean save) {
		executeCommandConsumer(c -> c.shutdown(save), u -> u.shutdown(save));
	}

	@Override
	public String slaveof(String host, int port) {
		return executeCommand(c -> c.slaveof(host, port), u -> u.slaveof(host, port));
	}

	@Override
	public String slaveofNoOne() {
		return executeCommand(c -> c.slaveofNoOne(), u -> u.slaveofNoOne());
	}

	@Override
	public List<Object> slowlogGet() {
		return executeCommand(c -> c.slowlogGet(), u -> u.slowlogGet());
	}

	@Override
	public List<Object> slowlogGet(int count) {
		return executeCommand(c -> c.slowlogGet(count), u -> u.slowlogGet(count));
	}

	@Override
	public Long slowlogLen() {
		return executeCommand(c -> c.slowlogLen(), u -> u.slowlogLen());
	}

	@Override
	public String slowlogReset() {
		return executeCommand(c -> c.slowlogReset(), u -> u.slowlogReset());
	}

	@Override
	public List<String> time() {
		return executeCommand(c -> c.time(), u -> u.time());
	}

	//------------------------------------------------------RedisTransactionalCommands-----------------------------------------------------

	@Override
	public String discard() {
		return executeCommand(c -> c.discard(), u ->  {
			throw new UnsupportedOperationException("Not supported yet.");
		});
	}

	@Override
	public TransactionResult exec() {

		return executeCommand(c -> c.exec(), u ->  {
			throw new UnsupportedOperationException("Not supported yet.");
		});
	}


	@Override
	public String multi() {
		return executeCommand(c -> c.multi(), u -> {
			throw new UnsupportedOperationException("Not supported yet.");
		});
	}

	@Override
	public String watch(String... keys) {
		return executeCommand(c -> c.watch(keys),  u ->  {
			throw new UnsupportedOperationException("Not supported yet.");
		});
	}

	@Override
	public String unwatch() {
		return executeCommand(c -> c.unwatch(), u ->  {
			throw new UnsupportedOperationException("Not supported yet.");
		});
	}
	
	
	@Override
	public TransactionResult execMulti(Consumer<RedisCommands<String, String>> statement ) throws UnsupportedOperationException{
		return executeCommand(c -> {
			String multi = c.multi();
			statement.accept(c);
			TransactionResult exec = c.exec();
			return exec;
		}, u ->  {
			throw new UnsupportedOperationException("Not supported yet.");
		});
	}
	
}
