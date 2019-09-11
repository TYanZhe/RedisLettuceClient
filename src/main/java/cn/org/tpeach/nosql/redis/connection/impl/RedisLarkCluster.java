package cn.org.tpeach.nosql.redis.connection.impl;

import cn.org.tpeach.nosql.enums.RedisStructure;
import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.exception.ServiceException;
import cn.org.tpeach.nosql.redis.connection.RedisLark;
import cn.org.tpeach.nosql.tools.ArraysUtil;
import cn.org.tpeach.nosql.tools.CollectionUtils;
import cn.org.tpeach.nosql.tools.StringUtils;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.params.*;
import redis.clients.jedis.util.Slowlog;

import java.util.*;
import java.util.Map.Entry;

/**
 * @author tyz
 * @Title: RedisLarkCluster
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-06-23 20:46
 * @since 1.0.0
 */
@Slf4j
public class RedisLarkCluster implements RedisLark {
	protected static final int DEFAULT_MAX_ATTEMPTS = 5;
	protected static final int DEFAULT_TIMEOUT = 10000;

	private JedisCluster jedisCluster;
	private String id;
	public RedisLarkCluster(final String id,final String host, final String auth) {
		this.id=id;
		// 节点、连接超时时间、读取数据超时时间、 最大尝试次数
		if (StringUtils.isNotEmpty(auth)) {
			jedisCluster = new JedisCluster(this.getHostAndPort(host), DEFAULT_TIMEOUT, DEFAULT_TIMEOUT,DEFAULT_MAX_ATTEMPTS, auth, this.getPoolConfig());
		} else {
			jedisCluster = new JedisCluster(this.getHostAndPort(host), DEFAULT_TIMEOUT, this.getPoolConfig());
		}
		
	}

	private Set<HostAndPort> getHostAndPort(String host) {
		// 集群的服务节点Set集合
		Set<HostAndPort> nodes = new HashSet<HostAndPort>();
		String[] hosts = host.split(",");
		for (String hostport : hosts) {
			String[] ipport = hostport.replaceAll(";", ",").split(":");
			String ip = ipport[0];
			int port = Integer.parseInt(ipport[1]);
			nodes.add(new HostAndPort(ip, port));
		}
		return nodes;
	}

	private JedisPoolConfig getPoolConfig() {
		// Jedis连接池配置
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		// 最大连接数, 默认8个
		poolConfig.setMaxTotal(200);
		// 最大空闲连接数, 默认8个
		poolConfig.setMaxIdle(50);
		// 最小空闲连接数, 默认0
		poolConfig.setMinIdle(0);
		// 获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间, 默认-1
		// Could not get a resource from the pool
		poolConfig.setMaxWaitMillis(1000 * 100);
		// 对拿到的connection进行validateObject校验
		poolConfig.setTestOnBorrow(false);
		return poolConfig;
	}

	@Override
	public RedisVersion getVersion() {
		return RedisVersion.REDIS_3_2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.org.tpeach.nosql.redis.connection.RedisLark#getRedisStructure()
	 */
	@Override
	public RedisStructure getRedisStructure() {
		return RedisStructure.CLUSTER;
	}

	// -----------------------------------JedisCLuster支持---------------------------------------
	@Override
	public String set(String key, String value) {
		return jedisCluster.set(key, value);
	}

	@Override
	public String set(String key, String value, SetParams params) {
		return jedisCluster.set(key, value, params);
	}

	@Override
	public String get(String key) {
		return jedisCluster.get(key);
	}

	@Override
	public Long exists(String... keys) {

		return jedisCluster.exists(keys);
	}

	@Override
	public Boolean exists(String key) {
		return jedisCluster.exists(key);
	}

	@Override
	public Long del(String... keys) {
		//修复 No way to dispatch this command to Redis Cluster because keys have different slots
//		return jedisCluster.del(keys);
		Long l = 0L;
		if(!ArraysUtil.isEmpty(keys)){
			for (String key : keys) {
				try{
					l+=del(key);
				}catch (Exception e){
					log.error("Clust del keys error",e);
				}
			}

		}
		return l;
	}

	@Override
	public Long del(String key) {

		return jedisCluster.del(key);
	}

	@Override
	public Long unlink(String... keys) {

		return jedisCluster.unlink(keys);
	}

	@Override
	public Long unlink(String key) {

		return jedisCluster.unlink(key);
	}

	@Override
	public String type(String key) {

		return jedisCluster.type(key);
	}

	/**
	 * 搜索集群内匹配的Key
	 *
	 * @param pattern
	 *            匹配规则,该参数和Jedis keys(pattern)方法相同。
	 *            eg:搜索Node_status_开头的所有key，该参数填Node_status_*
	 * @return 集群内所有符合规则的Key列表
	 */
	@Override
	public Set<String> keys(String pattern) {
		final TreeSet<String> keys = new TreeSet<>();
		// 获取Redis集群内所有节点
		Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
		clusterNodes.forEach((key, jedisPool) -> {
			Jedis jedis = jedisPool.getResource();
			// 判断非从节点(因为若主从复制，从节点会跟随主节点的变化而变化)
			if (!jedis.info("replication").contains("role:slave")) {
				// 搜索单个节点内匹配的Key
				Set<String> keysNode = jedis.keys(pattern);
//				System.out.println("key：" + key + ">>>>>>>>>>>" + keysNode.size());
				// 合并搜索结果
				keys.addAll(keysNode);
			}
			jedis.close();
		});
		return keys;
	}

	@Override
	public String rename(String oldKey, String newKey) {
		//jedisCluster.rename(oldKey, newKey);
//		String temp = get(oldKey);
//		set(newKey, temp);
//		del(oldKey);
//		return temp;
		throw new ServiceException("该版本集群暂未支持重命名");

	}

	@Override
	public Long renamenx(String oldkey, String newkey) {
		throw new ServiceException("该版本集群暂未支持重命名");
//		if(!exists(oldkey)){
//			return 0L;
//		}
//		String temp = get(oldkey);
//		set(newkey, temp);
//		del(oldkey);
//		return 1L;
//		return jedisCluster.renamenx(oldkey, newkey);
	}

	@Override
	public Long expire(String key, int seconds) {

		return jedisCluster.expire(key, seconds);
	}

	@Override
	public Long expireAt(String key, long unixTime) {

		return jedisCluster.expireAt(key, unixTime);
	}

	@Override
	public Long ttl(String key) {

		return jedisCluster.ttl(key);
	}

	@Override
	public Long touch(String... keys) {

		return jedisCluster.touch(keys);
	}

	@Override
	public Long touch(String key) {

		return jedisCluster.touch(key);
	}

	@Override
	public String getSet(String key, String value) {

		return jedisCluster.getSet(key, value);
	}

	@Override
	public List<String> mget(String... keys) {

		return jedisCluster.mget(keys);
	}

	@Override
	public Long setnx(String key, String value) {

		return jedisCluster.setnx(key, value);
	}

	@Override
	public String setex(String key, int seconds, String value) {

		return jedisCluster.setex(key, seconds, value);
	}

	@Override
	public String mset(String... keysvalues) {

		return jedisCluster.mset(keysvalues);
	}

	@Override
	public Long msetnx(String... keysvalues) {

		return jedisCluster.msetnx(keysvalues);
	}

	@Override
	public Long decrBy(String key, long decrement) {

		return jedisCluster.decrBy(key, decrement);
	}

	@Override
	public Long decr(String key) {

		return jedisCluster.decr(key);
	}

	@Override
	public Long incrBy(String key, long increment) {

		return jedisCluster.incrBy(key, increment);
	}

	@Override
	public Double incrByFloat(String key, double increment) {

		return jedisCluster.incrByFloat(key, increment);
	}

	@Override
	public Long incr(String key) {

		return jedisCluster.incr(key);
	}

	@Override
	public Long append(String key, String value) {

		return jedisCluster.append(key, value);
	}

	@Override
	public String substr(String key, int start, int end) {

		return jedisCluster.substr(key, start, end);
	}

	@Override
	public Long hset(String key, String field, String value) {

		return jedisCluster.hset(key, field, value);
	}

	@Override
	public Long hset(String key, Map<String, String> hash) {

		return jedisCluster.hset(key, hash);
	}

	@Override
	public String hget(String key, String field) {

		return jedisCluster.hget(key, field);
	}

	@Override
	public Long hsetnx(String key, String field, String value) {

		return jedisCluster.hsetnx(key, field, value);
	}

	@Override
	public String hmset(String key, Map<String, String> hash) {

		return jedisCluster.hmset(key, hash);
	}

	@Override
	public List<String> hmget(String key, String... fields) {

		return jedisCluster.hmget(key, fields);
	}

	@Override
	public Long hincrBy(String key, String field, long value) {

		return jedisCluster.hincrBy(key, field, value);
	}

	@Override
	public Boolean hexists(String key, String field) {

		return jedisCluster.hexists(key, field);
	}

	@Override
	public Long hdel(String key, String... fields) {

		return jedisCluster.hdel(key, fields);
	}

	@Override
	public Long hlen(String key) {

		return jedisCluster.hlen(key);
	}

	@Override
	public Set<String> hkeys(String key) {

		return jedisCluster.hkeys(key);
	}

	@Override
	public List<String> hvals(String key) {

		return jedisCluster.hvals(key);
	}

	@Override
	public Map<String, String> hgetAll(String key) {

		return jedisCluster.hgetAll(key);
	}

	@Override
	public Long rpush(String key, String... strings) {

		return jedisCluster.rpush(key, strings);
	}

	@Override
	public Long lpush(String key, String... strings) {

		return jedisCluster.lpush(key, strings);
	}

	@Override
	public Long llen(String key) {

		return jedisCluster.llen(key);
	}

	@Override
	public List<String> lrange(String key, long start, long stop) {

		return jedisCluster.lrange(key, start, stop);
	}

	@Override
	public String ltrim(String key, long start, long stop) {

		return jedisCluster.ltrim(key, start, stop);
	}

	@Override
	public String lindex(String key, long index) {

		return jedisCluster.lindex(key, index);
	}

	@Override
	public String lset(String key, long index, String value) {

		return jedisCluster.lset(key, index, value);
	}

	@Override
	public Long lrem(String key, long count, String value) {

		return jedisCluster.lrem(key, count, value);
	}

	@Override
	public String lpop(String key) {

		return jedisCluster.lpop(key);
	}

	@Override
	public String rpop(String key) {

		return jedisCluster.rpop(key);
	}

	@Override
	public String rpoplpush(String srckey, String dstkey) {

		return jedisCluster.rpoplpush(srckey, dstkey);
	}

	@Override
	public Long sadd(String key, String... members) {

		return jedisCluster.sadd(key, members);
	}

	@Override
	public Set<String> smembers(String key) {

		return jedisCluster.smembers(key);
	}

	@Override
	public Long srem(String key, String... members) {

		return jedisCluster.srem(key, members);
	}

	@Override
	public String spop(String key) {

		return jedisCluster.spop(key);
	}

	@Override
	public Set<String> spop(String key, long count) {

		return jedisCluster.spop(key, count);
	}

	@Override
	public Long smove(String srckey, String dstkey, String member) {

		return jedisCluster.smove(srckey, dstkey, member);
	}

	@Override
	public Long scard(String key) {

		return jedisCluster.scard(key);
	}

	@Override
	public Boolean sismember(String key, String member) {

		return jedisCluster.sismember(key, member);
	}

	@Override
	public Set<String> sinter(String... keys) {

		return jedisCluster.sinter(keys);
	}

	@Override
	public Long sinterstore(String dstkey, String... keys) {

		return jedisCluster.sinterstore(dstkey, keys);
	}

	@Override
	public Set<String> sunion(String... keys) {

		return jedisCluster.sunion(keys);
	}

	@Override
	public Long sunionstore(String dstkey, String... keys) {

		return jedisCluster.sunionstore(dstkey, keys);
	}

	@Override
	public Set<String> sdiff(String... keys) {

		return jedisCluster.sdiff(keys);
	}

	@Override
	public Long sdiffstore(String dstkey, String... keys) {

		return jedisCluster.sdiffstore(dstkey, keys);
	}

	@Override
	public String srandmember(String key) {

		return jedisCluster.srandmember(key);
	}

	@Override
	public List<String> srandmember(String key, int count) {

		return jedisCluster.srandmember(key, count);
	}

	@Override
	public Long zadd(String key, double score, String member) {

		return jedisCluster.zadd(key, score, member);
	}

	@Override
	public Long zadd(String key, double score, String member, ZAddParams params) {

		return jedisCluster.zadd(key, score, member, params);
	}

	@Override
	public Long zadd(String key, Map<String, Double> scoreMembers) {

		return jedisCluster.zadd(key, scoreMembers);
	}

	@Override
	public Long zadd(String key, Map<String, Double> scoreMembers, ZAddParams params) {

		return jedisCluster.zadd(key, scoreMembers, params);
	}

	@Override
	public Set<String> zrange(String key, long start, long stop) {

		return jedisCluster.zrange(key, start, stop);
	}

	@Override
	public Long zrem(String key, String... members) {

		return jedisCluster.zrem(key, members);
	}

	@Override
	public Double zincrby(String key, double increment, String member) {

		return jedisCluster.zincrby(key, increment, member);
	}

	@Override
	public Double zincrby(String key, double increment, String member, ZIncrByParams params) {

		return jedisCluster.zincrby(key, increment, member, params);
	}

	@Override
	public Long zrank(String key, String member) {

		return jedisCluster.zrank(key, member);
	}

	@Override
	public Long zrevrank(String key, String member) {

		return jedisCluster.zrevrank(key, member);
	}

	@Override
	public Set<String> zrevrange(String key, long start, long stop) {

		return jedisCluster.zrange(key, start, stop);
	}

	@Override
	public Set<Tuple> zrangeWithScores(String key, long start, long stop) {

		return jedisCluster.zrangeWithScores(key, start, stop);
	}

	@Override
	public Set<Tuple> zrevrangeWithScores(String key, long start, long stop) {

		return jedisCluster.zrevrangeWithScores(key, start, stop);
	}

	@Override
	public Long zcard(String key) {

		return jedisCluster.zcard(key);
	}

	@Override
	public Double zscore(String key, String member) {

		return jedisCluster.zscore(key, member);
	}

	@Override
	public List<String> sort(String key) {

		return jedisCluster.sort(key);
	}

	@Override
	public List<String> sort(String key, SortingParams sortingParameters) {

		return jedisCluster.sort(key, sortingParameters);
	}

	@Override
	public List<String> blpop(int timeout, String... keys) {

		return jedisCluster.blpop(timeout, keys);
	}

	@Override
	public Long sort(String key, SortingParams sortingParameters, String dstkey) {

		return jedisCluster.sort(key, sortingParameters, dstkey);
	}

	@Override
	public Long sort(String key, String dstkey) {

		return jedisCluster.sort(key, dstkey);
	}

	@Override
	public List<String> brpop(int timeout, String... keys) {

		return jedisCluster.brpop(timeout, keys);
	}

	@Override
	public Long zcount(String key, double min, double max) {

		return jedisCluster.zcount(key, min, max);
	}

	@Override
	public Long zcount(String key, String min, String max) {

		return jedisCluster.zcount(key, min, max);
	}

	@Override
	public Set<String> zrangeByScore(String key, double min, double max) {
		return jedisCluster.zrangeByScore(key, min, max);
	}

	@Override
	public Set<String> zrangeByScore(String key, String min, String max) {

		return jedisCluster.zrangeByScore(key, min, max);
	}

	@Override
	public Set<String> zrangeByScore(String key, double min, double max, int offset, int count) {

		return jedisCluster.zrangeByScore(key, min, max, offset, count);
	}

	@Override
	public Set<String> zrangeByScore(String key, String min, String max, int offset, int count) {
		return jedisCluster.zrangeByScore(key, min, max, offset, count);
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max) {

		return jedisCluster.zrangeByScoreWithScores(key, min, max);
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max) {
		return jedisCluster.zrangeByScoreWithScores(key, min, max);
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max, int offset, int count) {
		return jedisCluster.zrangeByScoreWithScores(key, min, max, offset, count);
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max, int offset, int count) {

		return jedisCluster.zrevrangeByScoreWithScores(key, max, min, offset, count);
	}

	@Override
	public Set<String> zrevrangeByScore(String key, double max, double min) {

		return jedisCluster.zrevrangeByScore(key, max, min);
	}

	@Override
	public Set<String> zrevrangeByScore(String key, String max, String min) {

		return jedisCluster.zrevrangeByScore(key, max, min);
	}

	@Override
	public Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count) {

		return jedisCluster.zrevrangeByScore(key, max, min, offset, count);
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min) {

		return jedisCluster.zrevrangeByScoreWithScores(key, max, min);
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min, int offset, int count) {

		return jedisCluster.zrevrangeByScoreWithScores(key, max, min, offset, count);
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min, int offset, int count) {

		return jedisCluster.zrevrangeByScoreWithScores(key, max, min, offset, count);
	}

	@Override
	public Set<String> zrevrangeByScore(String key, String max, String min, int offset, int count) {

		return jedisCluster.zrevrangeByScore(key, max, min, offset, count);
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min) {

		return jedisCluster.zrevrangeByScoreWithScores(key, max, min);
	}

	@Override
	public Long zremrangeByRank(String key, long start, long stop) {

		return jedisCluster.zremrangeByRank(key, start, stop);
	}

	@Override
	public Long zremrangeByScore(String key, double min, double max) {

		return jedisCluster.zremrangeByScore(key, min, max);
	}

	@Override
	public Long zremrangeByScore(String key, String min, String max) {

		return jedisCluster.zremrangeByScore(key, min, max);
	}

	@Override
	public Long zunionstore(String dstkey, String... sets) {

		return jedisCluster.zunionstore(dstkey, sets);
	}

	@Override
	public Long zunionstore(String dstkey, ZParams params, String... sets) {

		return jedisCluster.zunionstore(dstkey, params, sets);
	}

	@Override
	public Long zinterstore(String dstkey, String... sets) {

		return jedisCluster.zinterstore(dstkey, sets);
	}

	@Override
	public Long zinterstore(String dstkey, ZParams params, String... sets) {

		return jedisCluster.zinterstore(dstkey, params, sets);
	}

	@Override
	public Long zlexcount(String key, String min, String max) {

		return jedisCluster.zlexcount(key, min, max);
	}

	@Override
	public Set<String> zrangeByLex(String key, String min, String max) {

		return jedisCluster.zrangeByLex(key, min, max);
	}

	@Override
	public Set<String> zrangeByLex(String key, String min, String max, int offset, int count) {

		return jedisCluster.zrangeByLex(key, min, max, offset, count);
	}

	@Override
	public Set<String> zrevrangeByLex(String key, String max, String min) {

		return jedisCluster.zrevrangeByLex(key, max, min);
	}

	@Override
	public Set<String> zrevrangeByLex(String key, String max, String min, int offset, int count) {

		return jedisCluster.zrevrangeByLex(key, max, min, offset, count);
	}

	@Override
	public Long zremrangeByLex(String key, String min, String max) {

		return jedisCluster.zremrangeByLex(key, min, max);
	}

	@Override
	public Long strlen(String key) {

		return jedisCluster.strlen(key);
	}

	@Override
	public Long lpushx(String key, String... string) {

		return jedisCluster.lpushx(key, string);
	}

	@Override
	public Long persist(String key) {

		return jedisCluster.persist(key);
	}

	@Override
	public Long rpushx(String key, String... string) {

		return jedisCluster.rpushx(key, string);
	}

	@Override
	public String echo(String string) {

		return jedisCluster.echo(string);
	}

	@Override
	public Long linsert(String key, ListPosition where, String pivot, String value) {

		return jedisCluster.linsert(key, where, pivot, value);
	}

	@Override
	public String brpoplpush(String source, String destination, int timeout) {

		return jedisCluster.brpoplpush(source, destination, timeout);
	}

	@Override
	public Boolean setbit(String key, long offset, boolean value) {

		return jedisCluster.setbit(key, offset, value);
	}

	@Override
	public Boolean setbit(String key, long offset, String value) {

		return jedisCluster.setbit(key, offset, value);
	}

	@Override
	public Boolean getbit(String key, long offset) {

		return jedisCluster.getbit(key, offset);
	}

	@Override
	public Long setrange(String key, long offset, String value) {

		return jedisCluster.setrange(key, offset, value);
	}

	@Override
	public String getrange(String key, long startOffset, long endOffset) {

		return jedisCluster.getrange(key, startOffset, endOffset);
	}

	@Override
	public Object eval(String script, int keyCount, String... params) {

		return jedisCluster.eval(script, keyCount, params);
	}

	@Override
	public void subscribe(JedisPubSub jedisPubSub, String... channels) {

		jedisCluster.subscribe(jedisPubSub, channels);
	}

	@Override
	public Long publish(String channel, String message) {

		return jedisCluster.publish(channel, message);
	}

	@Override
	public void psubscribe(JedisPubSub jedisPubSub, String... patterns) {

		jedisCluster.psubscribe(jedisPubSub, patterns);
	}

	@Override
	public Object eval(String script, List<String> keys, List<String> args) {

		return jedisCluster.eval(script, keys, args);
	}

	@Override
	public Object evalsha(String sha1, List<String> keys, List<String> args) {

		return jedisCluster.evalsha(sha1, keys, args);
	}

	@Override
	public Object evalsha(String sha1, int keyCount, String... params) {

		return jedisCluster.evalsha(sha1, keyCount, params);
	}

	@Override
	public Long bitcount(String key) {

		return jedisCluster.bitcount(key);
	}

	@Override
	public Long bitcount(String key, long start, long end) {

		return jedisCluster.bitcount(key);
	}

	@Override
	public Long bitop(BitOP op, String destKey, String... srcKeys) {

		return jedisCluster.bitop(op, destKey, srcKeys);
	}

	@Override
	public byte[] dump(String key) {

		return jedisCluster.dump(key);
	}

	@Override
	public String restore(String key, int ttl, byte[] serializedValue) {

		return jedisCluster.restore(key, ttl, serializedValue);
	}

	@Override
	public Long pexpire(String key, long milliseconds) {

		return jedisCluster.pexpire(key, milliseconds);
	}

	@Override
	public Long pexpireAt(String key, long millisecondsTimestamp) {

		return jedisCluster.pexpireAt(key, millisecondsTimestamp);
	}

	@Override
	public Long pttl(String key) {

		return jedisCluster.pttl(key);
	}

	@Override
	public String psetex(String key, long milliseconds, String value) {

		return jedisCluster.psetex(key, milliseconds, value);
	}

	@Override
	public ScanResult<String> scan(String cursor, ScanParams params) {
		// 获取Redis集群内所有节点

		List<String> keyList = new ArrayList<>();
		Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
		clusterNodes.forEach((key, jedisPool) -> {

			Jedis jedis = jedisPool.getResource();
			// 判断非从节点(因为若主从复制，从节点会跟随主节点的变化而变化)
			if (!jedis.info("replication").contains("role:slave")) {
				String scanCursor = ScanParams.SCAN_POINTER_START;
				List<String> scanResult = null;
				do {

					ScanResult<String> scan = jedis.scan(scanCursor, params);
					scanCursor = scan.getCursor();
					scanResult = scan.getResult();
					if(CollectionUtils.isNotEmpty(scanResult)){
						keyList.addAll(scanResult);
					}

				}while  (!scanCursor.equals("0") && CollectionUtils.isNotEmpty(scanResult));
			}
			jedis.close();
		});
		return new ScanResult<String>(ScanParams.SCAN_POINTER_START,keyList);
//		return jedisCluster.scan(cursor, params);
	}


	@Override
	public ScanResult<Entry<String, String>> hscan(String key, String cursor) {

		return jedisCluster.hscan(key, cursor);
	}

	@Override
	public ScanResult<String> sscan(String key, String cursor) {

		return jedisCluster.sscan(key, cursor);
	}

	@Override
	public ScanResult<Tuple> zscan(String key, String cursor) {

		return jedisCluster.zscan(key, cursor);
	}

	@Override
	public void close() {
		jedisCluster.close();

	}

	@Override
	public Long pfadd(String key, String... elements) {

		return jedisCluster.pfadd(key, elements);
	}

	@Override
	public long pfcount(String key) {

		return jedisCluster.pfcount(key);
	}

	@Override
	public long pfcount(String... keys) {

		return jedisCluster.pfcount(keys);
	}

	@Override
	public String pfmerge(String destkey, String... sourcekeys) {

		return jedisCluster.pfmerge(destkey, sourcekeys);
	}

	@Override
	public List<String> blpop(int timeout, String key) {

		return jedisCluster.blpop(timeout, key);
	}

	@Override
	public List<String> brpop(int timeout, String key) {

		return jedisCluster.brpop(timeout, key);
	}

	@Override
	public Long geoadd(String key, double longitude, double latitude, String member) {

		return jedisCluster.geoadd(key, longitude, latitude, member);
	}

	@Override
	public Long geoadd(String key, Map<String, GeoCoordinate> memberCoordinateMap) {

		return jedisCluster.geoadd(key, memberCoordinateMap);
	}

	@Override
	public Double geodist(String key, String member1, String member2) {

		return jedisCluster.geodist(key, member1, member2);
	}

	@Override
	public Double geodist(String key, String member1, String member2, GeoUnit unit) {

		return jedisCluster.geodist(key, member1, member2);
	}

	@Override
	public List<String> geohash(String key, String... members) {

		return jedisCluster.geohash(key, members);
	}

	@Override
	public List<GeoCoordinate> geopos(String key, String... members) {

		return jedisCluster.geopos(key, members);
	}

	@Override
	public List<GeoRadiusResponse> georadius(String key, double longitude, double latitude, double radius,
			GeoUnit unit) {

		return jedisCluster.georadius(key, longitude, latitude, radius, unit);
	}

	@Override
	public List<GeoRadiusResponse> georadiusReadonly(String key, double longitude, double latitude, double radius,
			GeoUnit unit) {

		return jedisCluster.georadiusReadonly(key, longitude, latitude, radius, unit);
	}

	@Override
	public List<GeoRadiusResponse> georadius(String key, double longitude, double latitude, double radius, GeoUnit unit,
			GeoRadiusParam param) {

		return jedisCluster.georadius(key, longitude, latitude, radius, unit, param);
	}

	@Override
	public List<GeoRadiusResponse> georadiusReadonly(String key, double longitude, double latitude, double radius,
			GeoUnit unit, GeoRadiusParam param) {

		return jedisCluster.georadiusReadonly(key, longitude, latitude, radius, unit, param);
	}

	@Override
	public List<GeoRadiusResponse> georadiusByMember(String key, String member, double radius, GeoUnit unit) {

		return jedisCluster.georadiusByMember(key, member, radius, unit);
	}

	@Override
	public List<GeoRadiusResponse> georadiusByMemberReadonly(String key, String member, double radius, GeoUnit unit) {

		return jedisCluster.georadiusByMemberReadonly(key, member, radius, unit);
	}

	@Override
	public List<GeoRadiusResponse> georadiusByMember(String key, String member, double radius, GeoUnit unit,
			GeoRadiusParam param) {

		return jedisCluster.georadiusByMember(key, member, radius, unit, param);
	}

	@Override
	public List<GeoRadiusResponse> georadiusByMemberReadonly(String key, String member, double radius, GeoUnit unit,
			GeoRadiusParam param) {

		return jedisCluster.georadiusByMemberReadonly(key, member, radius, unit, param);
	}

	@Override
	public List<Long> bitfield(String key, String... arguments) {

		return jedisCluster.bitfield(key, arguments);
	}

	@Override
	public Long hstrlen(String key, String field) {

		return jedisCluster.hstrlen(key, field);
	}

	// -----------------------------------JedisCluster---------------------------------------
	@Override
	public String ping(String message) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}
	@Override
	public String ping() {
		// TODO Auto-generated method stub

//		throw new ServiceException("该版本暂未支持");
//		Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
//		for (String keySet : clusterNodes.keySet()) {
////			try {
//				JedisPool jedisPool = clusterNodes.get(keySet);
//				Jedis jedisConn = jedisPool.getResource();
//				if (!jedisConn.info("replication").contains("role:slave")) {
//
//					jedisConn.ping();
//				}
////			} catch (Exception e) {
////				continue;
////			}
//		}
		return "PONG";

	}
	@Override
	public String randomKey() {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public Long move(String key, int dbIndex) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public Double hincrByFloat(String key, String field, double value) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public String watch(String... keys) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public List<String> blpop(String... args) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public List<String> brpop(String... args) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public Long bitpos(String key, boolean value) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public Long bitpos(String key, boolean value, BitPosParams params) {

		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public List<String> configGet(String pattern) {
		// TODO Auto-generated method stub
		return Arrays.asList("databases", "1");
	}

	@Override
	public String configSet(String parameter, String value) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public Object eval(String script) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public Object evalsha(String sha1) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public Boolean scriptExists(String sha1) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public List<Boolean> scriptExists(String... sha1) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public String scriptLoad(String script) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public List<Slowlog> slowlogGet() {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public List<Slowlog> slowlogGet(long entries) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public Long objectRefcount(String key) {
		// TODO Auto-generated method stub
		Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
		for (String keySet : clusterNodes.keySet()) {
			try {
				JedisPool jedisPool = clusterNodes.get(keySet);
				Jedis jedisConn = jedisPool.getResource();
				return jedisConn.objectRefcount(key);
			} catch (Exception e) {
				continue;
			}
		}
		return 0L;
	}

	@Override
	public String objectEncoding(String key) {
		// TODO Auto-generated method stub
		Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
		for (String keySet : clusterNodes.keySet()) {
			try {
				JedisPool jedisPool = clusterNodes.get(keySet);
				Jedis jedisConn = jedisPool.getResource();
				return jedisConn.objectEncoding(key);
			} catch (Exception e) {
				continue;
			}
		}
		return null;
	}

	@Override
	public Long objectIdletime(String key) {
		// TODO Auto-generated method stub
		Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
		for (String keySet : clusterNodes.keySet()) {
			try {
				JedisPool jedisPool = clusterNodes.get(keySet);
				Jedis jedisConn = jedisPool.getResource();
				return jedisConn.objectIdletime(key);
			} catch (Exception e) {
				continue;
			}
		}
		return 0L;
	}

	@Override
	public List<Map<String, String>> sentinelMasters() {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public List<String> sentinelGetMasterAddrByName(String masterName) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public Long sentinelReset(String pattern) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public List<Map<String, String>> sentinelSlaves(String masterName) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public String sentinelFailover(String masterName) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public String sentinelMonitor(String masterName, String ip, int port, int quorum) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public String sentinelRemove(String masterName) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public String sentinelSet(String masterName, Map<String, String> parameterMap) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public String restoreReplace(String key, int ttl, byte[] serializedValue) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public String clientKill(String ipPort) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public String clientGetname() {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public String clientList() {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public String clientSetname(String name) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public String migrate(String host, int port, String key, int destinationDb, int timeout) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public String migrate(String host, int port, int destinationDB, int timeout, MigrateParams params, String... keys) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public ScanResult<String> scan(String cursor) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public ScanResult<Entry<String, String>> hscan(String key, String cursor, ScanParams params) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public ScanResult<String> sscan(String key, String cursor, ScanParams params) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public ScanResult<Tuple> zscan(String key, String cursor, ScanParams params) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public String clusterNodes() {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public String readonly() {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public String clusterMeet(String ip, int port) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public String clusterReset(ClusterReset resetType) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public String clusterAddSlots(int... slots) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public String clusterDelSlots(int... slots) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public String clusterInfo() {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public List<String> clusterGetKeysInSlot(int slot, int count) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public String clusterSetSlotNode(int slot, String nodeId) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public String clusterSetSlotMigrating(int slot, String nodeId) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public String clusterSetSlotImporting(int slot, String nodeId) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public String clusterSetSlotStable(int slot) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public String clusterForget(String nodeId) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public String clusterFlushSlots() {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public Long clusterKeySlot(String key) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public Long clusterCountKeysInSlot(int slot) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public String clusterSaveConfig() {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public String clusterReplicate(String nodeId) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public List<String> clusterSlaves(String nodeId) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public String clusterFailover() {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public List<Object> clusterSlots() {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public String asking() {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public List<String> pubsubChannels(String pattern) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public Long pubsubNumPat() {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public Map<String, String> pubsubNumSub(String... channels) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public void setDataSource(JedisPoolAbstract jedisPool) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public String moduleLoad(String path) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public String moduleUnload(String name) {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public List<Module> moduleList() {
		// TODO Auto-generated method stub
		throw new ServiceException("该版本暂未支持");
	}

	@Override
	public String info() {
		Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
		for (String key : clusterNodes.keySet()) {
			try {
				JedisPool jedisPool = clusterNodes.get(key);
				Jedis jedisConn = jedisPool.getResource();
				return jedisConn.info();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public String select(int db) {
		if(db == 0){
			return "OK";
		}
		throw new JedisDataException("-ERR invalid DB index");
	}

	@Override
	public int getDbAmount() {
		return 0;
	}

	@Override
	public Long dbSize() {
		Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
		Long dbSize = 0L;
//		for (JedisPool jedisPool : clusterNodes.values()) {
//			Jedis jedis = jedisPool.getResource();
//			// 判断非从节点(因为若主从复制，从节点会跟随主节点的变化而变化)
//			if (!jedis.info("replication").contains("role:slave")) {
//				dbSize+=jedis.dbSize();
//			}
//			jedis.close();
//		}
		return dbSize;
	}

	@Override
	public Map<String, String> getInfo() {
		return null;
	}

	/* (non-Javadoc)
	 * @see cn.org.tpeach.nosql.redis.connection.RedisLark#flushDB()
	 */
	@Override
	public String flushDB() {
		// TODO Auto-generated method stub
//		throw new ServiceException("该版本暂未支持flushDB");
		// 获取Redis集群内所有节点
		Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
		clusterNodes.forEach((key, jedisPool) -> {
			Jedis jedis = jedisPool.getResource();
			// 判断非从节点(因为若主从复制，从节点会跟随主节点的变化而变化)
			if (!jedis.info("replication").contains("role:slave")) {
				jedis.flushDB();
			}
			jedis.close();
		});
		return "OK";
	}


    @Override
    public Transaction multi() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
	

}
