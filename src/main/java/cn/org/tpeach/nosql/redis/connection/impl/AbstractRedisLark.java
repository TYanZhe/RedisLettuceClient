package cn.org.tpeach.nosql.redis.connection.impl;

import cn.org.tpeach.nosql.enums.RedisStructure;
import cn.org.tpeach.nosql.exception.ServiceException;
import cn.org.tpeach.nosql.redis.bean.RedisConnectInfo;
import cn.org.tpeach.nosql.redis.connection.RedisLark;
import cn.org.tpeach.nosql.tools.StringUtils;
import io.lettuce.core.*;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.output.*;
import io.lettuce.core.protocol.CommandArgs;
import io.lettuce.core.protocol.CommandType;
import io.lettuce.core.protocol.ProtocolKeyword;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author tyz
 * @Title: AbstractRedisLark
 * @ProjectName redisLark-Github
 * @Description: TODO
 * @date 2019-09-26 23:05
 * @since 1.0.0
 */
public abstract  class AbstractRedisLark<K,V> implements RedisLark<K,V> {
    @Getter
    @Setter
    protected String id;
    String redisUrlFormat = "redis://%s%s:%s";
    protected RedisClient client;
    protected RedisClusterClient cluster;
    protected StatefulRedisConnection<K,V> clentConnection;
    protected StatefulRedisClusterConnection<K,V> clusterConnection;

    protected RedisCodec<K, V> redisCodec;

    public AbstractRedisLark(RedisConnectInfo connectInfo, RedisCodec<K, V> redisCodec) {
        this.redisCodec = redisCodec;
        this.id = connectInfo.getId();
        String auth;
        RedisStructure redisStructure = RedisStructure.getRedisStructure(connectInfo.getStructure());
        if (redisStructure == RedisStructure.SINGLE) {
            if (StringUtils.isBlank(connectInfo.getAuth())) {
                auth = "";
            } else{
                auth = connectInfo.getAuth();
            }
            RedisURI.Builder builder = RedisURI.Builder.redis(connectInfo.getHost(), connectInfo.getPort()).withPassword(auth).withDatabase(0);
            final RedisURI redisURI = builder.build();
            if(connectInfo.getTimeout() != null){
                redisURI.setTimeout(Duration.ofSeconds(connectInfo.getTimeout()));
            }
            this.client = RedisClient.create(redisURI);
            this.clentConnection = client.connect(redisCodec);
        }else if(redisStructure == RedisStructure.CLUSTER) {
            auth = connectInfo.getAuth();
            if (StringUtils.isBlank(auth)) {
                auth = "";
            } else {
                auth = auth + "@";
            }
            ArrayList<RedisURI> list = new ArrayList<>();
            String[] hosts = connectInfo.getHost().split(",");
            for (String hostport : hosts) {
                String[] ipport = hostport.replaceAll(";", ",").split(":");
                String ip = ipport[0];
                int port = Integer.parseInt(ipport[1]);
                RedisURI redisURI = RedisURI.create(String.format(redisUrlFormat, auth, ip, port));
                if(connectInfo.getTimeout() != null){
                    redisURI.setTimeout(Duration.ofSeconds(connectInfo.getTimeout()));
                }
                list.add(redisURI);
            }
            this.cluster = RedisClusterClient.create(list);
            this.clusterConnection = cluster.connect(redisCodec);
        }else{
            throw new ServiceException("配置文件出错,不支持的RedisStructure");
        }

    }
    /**
     * 单机
     * @param id
     * @param host
     * @param port
     * @param auth
     */
    public AbstractRedisLark(final String id, final String host, final int port, String auth,RedisCodec<K, V> redisCodec) {
        this.redisCodec = redisCodec;
        this.id = id;
        if (StringUtils.isBlank(auth)) {
            auth = "";
        } else {
//            auth = auth + "@";
        }
        final RedisURI build = RedisURI.Builder.redis(host, port).withPassword(auth).withDatabase(0).build();
        this.client = RedisClient.create(build);
//        this.client = RedisClient.create(String.format(redisUrlFormat, auth, host, port));

        this.clentConnection = client.connect(redisCodec);
    }


    /**
     * 集群
     * @param id
     * @param hostAndPort
     * @param auth
     */
    public AbstractRedisLark(final String id, final String hostAndPort, String auth,RedisCodec<K, V> redisCodec) {
        this.redisCodec = redisCodec;
        this.id = id;
        if (StringUtils.isBlank(auth)) {
            auth = "";
        } else {
            auth = auth + "@";
        }
        ArrayList<RedisURI> list = new ArrayList<>();
        String[] hosts = hostAndPort.split(",");
        for (String hostport : hosts) {
            String[] ipport = hostport.replaceAll(";", ",").split(":");
            String ip = ipport[0];
            int port = Integer.parseInt(ipport[1]);
            list.add(RedisURI.create(String.format(redisUrlFormat, auth, ip, port)));
        }
        this.cluster = RedisClusterClient.create(list);
        this.clusterConnection = cluster.connect(redisCodec);
    }
    /**
     *
     * @param clientFun
     * @param clusterFun
     * @return
     */
    protected <R> R executeCommand(Function<RedisCommands<K,V>, R> clientFun,
                                 Function<RedisAdvancedClusterCommands<K,V>, R> clusterFun) {
        R apply = null;
        if (clentConnection != null) {
            RedisCommands<K,V> commands = clentConnection.sync();
            apply = clientFun.apply(commands);
        } else if (clusterConnection != null) {
            RedisAdvancedClusterCommands<K,V> commands = clusterConnection.sync();
            apply = clusterFun.apply(commands);
        } else {
            throw new ServiceException("RedisLarkLettuce实例化失败");
        }

        return apply;
    }
    /**
     *
     * @param clientConsumer
     * @param clusterConsumer
     * @return
     */
    protected void executeCommandConsumer(Consumer<RedisCommands<K,V>> clientConsumer,
                                        Consumer<RedisAdvancedClusterCommands<K,V>> clusterConsumer) {

        if (client != null) {
            StatefulRedisConnection<K,V> connection = client.connect(redisCodec);
            RedisCommands<K,V> commands = connection.sync();
            clientConsumer.accept(commands);
        } else if (cluster != null) {
            StatefulRedisClusterConnection<K,V> connection = cluster.connect(redisCodec);
            RedisAdvancedClusterCommands<K,V> commands = connection.sync();
            clusterConsumer.accept(commands);
        } else {
            throw new ServiceException("RedisLarkLettuce实例化失败");
        }

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
    public ScanIterator<K> scanIterator(int count, String pattren){
        return executeCommand(c ->ScanIterator.scan(c, ScanArgs.Builder.limit(count).match(pattren)),
                u -> ScanIterator.scan(u, ScanArgs.Builder.limit(count).match(pattren)));
    }
    @Override
    public ScanIterator<KeyValue<K, V>> hscanIterator(K key, int count, String pattren){
//		List<KeyValue<String, String>> keys = scan.stream().collect(Collectors.toList());
        return executeCommand(c ->ScanIterator.hscan(c, key,ScanArgs.Builder.limit(count).match(pattren)),
                u -> ScanIterator.hscan(u, key,ScanArgs.Builder.limit(count).match(pattren)));
    }
    @Override
    public ScanIterator<V> sscanIterator(K key, int count, String pattren){
        return executeCommand(c ->ScanIterator.sscan(c,key, ScanArgs.Builder.limit(count).match(pattren)),
                u -> ScanIterator.sscan(u,key, ScanArgs.Builder.limit(count).match(pattren)));
    }
    @Override
    public ScanIterator<ScoredValue<V>> zscanIterator(K key, int count, String pattren){
        return executeCommand(c ->ScanIterator.zscan(c,key, ScanArgs.Builder.limit(count).match(pattren)),
                u -> ScanIterator.zscan(u,key, ScanArgs.Builder.limit(count).match(pattren)));
    }
    //------------------------------------------------------BaseRedisCommands-----------------------------------------------------
    @Override
    public Long publish(K channel, V message) {
        return executeCommand(c -> c.publish(channel, message), u -> u.publish(channel, message));
    }

    @Override
    public List<K> pubsubChannels() {
        return executeCommand(c -> c.pubsubChannels(), u -> u.pubsubChannels());
    }

    @Override
    public List<K> pubsubChannels(K channel) {
        return executeCommand(c -> c.pubsubChannels(channel), u -> u.pubsubChannels(channel));
    }

    @Override
    public Map<K, Long> pubsubNumsub(K... channels) {
        return executeCommand(c -> c.pubsubNumsub(channels), u -> u.pubsubNumsub(channels));
    }

    @Override
    public Long pubsubNumpat() {
        return executeCommand(c -> c.pubsubNumpat(), u -> u.pubsubNumpat());
    }

    @Override
    public V echo(V msg) {
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
    public <T> T dispatch(ProtocolKeyword protocolKeyword, CommandOutput<K, V, T> commandOutput) {
        return executeCommand(c -> c.dispatch(protocolKeyword, commandOutput), u -> u.dispatch(protocolKeyword, commandOutput));
    }

    @Override
    public <T> T dispatch(ProtocolKeyword protocolKeyword, CommandOutput<K, V, T> commandOutput, CommandArgs<K, V> commandArgs) {
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
    public Long append(K key, V value) {
        return executeCommand(c -> c.append(key, value), u -> u.append(key, value));
    }

    @Override
    public Long bitcount(K key) {
        return executeCommand(c -> c.bitcount(key), u -> u.bitcount(key));
    }

    @Override
    public Long bitcount(K key, long start, long end) {
        return executeCommand(c -> c.bitcount(key, start, end), u -> u.bitcount(key, start, end));
    }

    @Override
    public List<Long> bitfield(K key, BitFieldArgs bitFieldArgs) {
        return executeCommand(c -> c.bitfield(key, bitFieldArgs), u -> u.bitfield(key, bitFieldArgs));
    }

    @Override
    public Long bitpos(K key, boolean state) {
        return executeCommand(c -> c.bitpos(key, state), u -> u.bitpos(key, state));
    }

    @Override
    public Long bitpos(K key, boolean state, long start) {
        return executeCommand(c -> c.bitpos(key, state, start), u -> u.bitpos(key, state, start));
    }

    @Override
    public Long bitpos(K key, boolean state, long start, long end) {
        return executeCommand(c -> c.bitpos(key, state, start, end), u -> u.bitpos(key, state, start, end));
    }

    @Override
    public Long bitopAnd(K destination, K... keys) {
        return executeCommand(c -> c.bitopAnd(destination, keys), u -> u.bitopAnd(destination, keys));
    }

    @Override
    public Long bitopNot(K destination, K source) {
        return executeCommand(c -> c.bitopNot(destination, source), u -> u.bitopNot(destination, source));
    }

    @Override
    public Long bitopOr(K destination, K... keys) {
        return executeCommand(c -> c.bitopOr(destination, keys), u -> u.bitopOr(destination, keys));
    }

    @Override
    public Long bitopXor(K destination, K... keys) {
        return executeCommand(c -> c.bitopXor(destination, keys), u -> u.bitopXor(destination, keys));
    }

    @Override
    public Long decr(K key) {
        return executeCommand(c -> c.decr(key), u -> u.decr(key));
    }

    @Override
    public Long decrby(K key, long amount) {
        return executeCommand(c -> c.decrby(key, amount), u -> u.decrby(key, amount));
    }

    @Override
    public V get(K key) {
        return executeCommand(c -> c.get(key), u -> u.get(key));
    }

    @Override
    public Long getbit(K key, long offset) {
        return executeCommand(c -> c.getbit(key, offset), u -> u.getbit(key, offset));
    }

    @Override
    public V getrange(K key, long start, long end) {
        return executeCommand(c -> c.getrange(key, start, end), u -> u.getrange(key, start, end));
    }

    @Override
    public V getset(K key, V value) {
        return executeCommand(c -> c.getset(key, value), u -> u.getset(key, value));
    }

    @Override
    public Long incr(K key) {
        return executeCommand(c -> c.incr(key), u -> u.incr(key));
    }

    @Override
    public Long incrby(K key, long amount) {
        return executeCommand(c -> c.incrby(key, amount), u -> u.incrby(key, amount));
    }

    @Override
    public Double incrbyfloat(K key, double amount) {
        return executeCommand(c -> c.incrbyfloat(key, amount), u -> u.incrbyfloat(key, amount));
    }

    @Override
    public List<KeyValue<K, V>> mget(K... keys) {
        return executeCommand(c -> c.mget(keys), u -> u.mget(keys));
    }

    @Override
    public Long mget(KeyValueStreamingChannel<K, V> channel, K... keys) {
        return executeCommand(c -> c.mget(channel, keys), u -> u.mget(channel, keys));
    }

    @Override
    public String mset(Map<K, V> map) {
        return executeCommand(c -> c.mset(map), u -> u.mset(map));
    }

    @Override
    public Boolean msetnx(Map<K, V> map) {
        return executeCommand(c -> c.msetnx(map), u -> u.msetnx(map));
    }

    @Override
    public String set(K key, V value) {
        return executeCommand(c -> c.set(key, value), u -> u.set(key, value));
    }

    @Override
    public String set(K key, V value, SetArgs setArgs) {
        return executeCommand(c -> c.set(key, value, setArgs), u -> u.set(key, value,setArgs));
    }

    @Override
    public Long setbit(K key, long offset, int value) {
        return executeCommand(c -> c.setbit(key, offset, value), u -> u.setbit(key, offset, value));
    }

    @Override
    public String setex(K key, long seconds, V value) {
        return executeCommand(c -> c.setex(key, seconds, value), u -> u.setex(key, seconds, value));
    }

    @Override
    public String psetex(K key, long milliseconds, V value) {
        return executeCommand(c -> c.psetex(key, milliseconds, value), u -> u.psetex(key, milliseconds, value));
    }

    @Override
    public Boolean setnx(K key, V value) {
        return executeCommand(c -> c.setnx(key, value), u -> u.setnx(key, value));
    }

    @Override
    public Long setrange(K key, long offset, V value) {
        return executeCommand(c -> c.setrange(key, offset, value), u -> u.setrange(key, offset, value));
    }
    @Override
    public Long strlen(K key) {
        return executeCommand(c -> c.strlen(key), u -> u.strlen(key));
    }
    //------------------------------------------------------RedisListCommands-----------------------------------------------------

    @Override
    public KeyValue<K, V> blpop(long timeout, K... keys) {
        return executeCommand(c -> c.blpop(timeout, keys), u -> u.blpop(timeout, keys));
    }

    @Override
    public KeyValue<K, V> brpop(long timeout, K... keys) {
        return executeCommand(c -> c.brpop(timeout, keys), u -> u.brpop(timeout, keys));
    }

    @Override
    public V brpoplpush(long timeout, K source, K destination) {
        return executeCommand(c -> c.brpoplpush(timeout, source, destination), u -> u.brpoplpush(timeout, source, destination));
    }

    @Override
    public V lindex(K key, long index) {
        return executeCommand(c -> c.lindex(key, index), u -> u.lindex(key,index));
    }

    @Override
    public Long linsert(K key, boolean before, V pivot, V value) {
        return executeCommand(c -> c.linsert(key, before, pivot, value), u -> u.linsert(key, before, pivot, value));
    }

    @Override
    public Long llen(K key) {
        return executeCommand(c -> c.llen(key), u -> u.llen(key));
    }

    @Override
    public V lpop(K key) {
        return executeCommand(c -> c.lpop(key), u -> u.lpop(key));
    }

    @Override
    public Long lpush(K key, V... values) {
        return executeCommand(c -> c.lpush(key, values), u -> u.lpush(key, values));
    }

    @Override
    public Long lpushx(K key, V... values) {
        return executeCommand(c -> c.lpushx(key, values), u -> u.lpushx(key, values));
    }

    @Override
    public List<V> lrange(K key, long start, long stop) {
        return executeCommand(c -> c.lrange(key, start, stop), u -> u.lrange(key, start, stop));
    }

    @Override
    public Long lrange(ValueStreamingChannel<V> channel, K key, long start, long stop) {
        return executeCommand(c -> c.lrange(channel, key, start, stop), u -> u.lrange(channel, key, start, stop));
    }

    @Override
    public Long lrem(K key, long count, V value) {
        return executeCommand(c -> c.lrem(key, count, value), u -> u.lrem(key, count, value));
    }

    @Override
    public String lset(K key, long index, V value) {
        return executeCommand(c -> c.lset(key, index, value), u -> u.lset(key, index, value));
    }

    @Override
    public String ltrim(K key, long start, long stop) {
        return executeCommand(c -> c.ltrim(key, start, stop), u -> u.ltrim(key, start, stop));
    }

    @Override
    public V rpop(K key) {
        return executeCommand(c -> c.rpop(key), u -> u.rpop(key));
    }

    @Override
    public V rpoplpush(K source, K destination) {
        return executeCommand(c -> c.rpoplpush(source, destination), u -> u.rpoplpush(source, destination));
    }

    @Override
    public Long rpush(K key, V... values) {
        return executeCommand(c -> c.rpush(key, values), u -> u.rpush(key, values));
    }


    @Override
    public Long rpushx(K key, V... values) {
        return executeCommand(c -> c.rpushx(key, values), u -> u.rpushx(key, values));
    }
    //------------------------------------------------------RedisHashCommands-----------------------------------------------------
    @Override
    public Long hdel(K key, K... fields) {
        return executeCommand(c -> c.hdel(key, fields), u -> u.hdel(key, fields));
    }

    @Override
    public Boolean hexists(K key, K field) {
        return executeCommand(c -> c.hexists(key, field), u -> u.hexists(key, field));
    }

    @Override
    public V hget(K key, K field) {
        return executeCommand(c -> c.hget(key, field), u -> u.hget(key, field));
    }

    @Override
    public Long hincrby(K key, K field, long amount) {
        return executeCommand(c -> c.hincrby(key, field, amount), u -> u.hincrby(key, field, amount));
    }

    @Override
    public Double hincrbyfloat(K key, K field, double amount) {
        return executeCommand(c -> c.hincrbyfloat(key, field, amount), u -> u.hincrbyfloat(key, field, amount));
    }

    @Override
    public Map<K, V> hgetall(K key) {
        return executeCommand(c -> c.hgetall(key), u -> u.hgetall(key));
    }

    @Override
    public Long hgetall(KeyValueStreamingChannel<K, V> channel, K key) {
        return executeCommand(c -> c.hgetall(channel, key), u -> u.hgetall(channel, key));
    }

    @Override
    public List<K> hkeys(K key) {
        return executeCommand(c -> c.hkeys(key), u -> u.hkeys(key));
    }

    @Override
    public Long hkeys(KeyStreamingChannel<K> channel, K key) {
        return executeCommand(c -> c.hkeys(channel, key), u -> u.hkeys(channel, key));
    }

    @Override
    public Long hlen(K key) {
        return executeCommand(c -> c.hlen(key), u -> u.hlen(key));
    }

    @Override
    public List<KeyValue<K, V>> hmget(K key, K... fields) {
        return executeCommand(c -> c.hmget(key, fields), u -> u.hmget(key, fields));
    }

    @Override
    public Long hmget(KeyValueStreamingChannel<K, V> channel, K key, K... fields) {
        return executeCommand(c -> c.hmget(channel, key, fields), u -> u.hmget(channel, key, fields));
    }

    @Override
    public String hmset(K key, Map<K, V> map) {
        return executeCommand(c -> c.hmset(key, map), u -> u.hmset(key, map));
    }

    @Override
    public MapScanCursor<K, V> hscan(K key) {
        return executeCommand(c -> c.hscan(key), u -> u.hscan(key));
    }

    @Override
    public MapScanCursor<K, V> hscan(K key, ScanArgs scanArgs) {
        return executeCommand(c -> c.hscan(key, scanArgs), u -> u.hscan(key, scanArgs));
    }

    @Override
    public MapScanCursor<K, V> hscan(K key, ScanCursor scanCursor, ScanArgs scanArgs) {
        return executeCommand(c -> c.hscan(key, scanCursor, scanArgs), u -> u.hscan(key, scanCursor, scanArgs));
    }

    @Override
    public MapScanCursor<K, V> hscan(K key, ScanCursor scanCursor) {
        return executeCommand(c -> c.hscan(key, scanCursor), u -> u.hscan(key, scanCursor));
    }

    @Override
    public StreamScanCursor hscan(KeyValueStreamingChannel<K, V> channel, K key) {
        return executeCommand(c -> c.hscan(channel, key), u -> u.hscan(channel, key));
    }

    @Override
    public StreamScanCursor hscan(KeyValueStreamingChannel<K, V> channel, K key, ScanArgs scanArgs) {
        return executeCommand(c -> c.hscan(channel, key, scanArgs), u -> u.hscan(channel, key, scanArgs));
    }

    @Override
    public StreamScanCursor hscan(KeyValueStreamingChannel<K, V> channel, K key, ScanCursor scanCursor, ScanArgs scanArgs) {
        return executeCommand(c -> c.hscan(channel, key, scanCursor, scanArgs), u -> u.hscan(channel, key, scanCursor, scanArgs));
    }

    @Override
    public StreamScanCursor hscan(KeyValueStreamingChannel<K, V> channel, K key, ScanCursor scanCursor) {
        return executeCommand(c -> c.hscan(channel, key, scanCursor), u -> u.hscan(channel, key, scanCursor));
    }

    @Override
    public Boolean hset(K key, K field, V value) {
        return executeCommand(c -> c.hset(key, field, value), u -> u.hset(key, field, value));
    }

    @Override
    public Boolean hsetnx(K key, K field, V value) {
        return executeCommand(c -> c.hsetnx(key, field, value), u -> u.hsetnx(key, field, value));
    }

    @Override
    public Long hstrlen(K key, K field) {
        return executeCommand(c -> c.hstrlen(key, field), u -> u.hstrlen(key, field));
    }

    @Override
    public List<V> hvals(K key) {
        return executeCommand(c -> c.hvals(key), u -> u.hvals(key));
    }

    @Override
    public Long hvals(ValueStreamingChannel<V> channel, K key) {
        return executeCommand(c -> c.hvals(channel, key), u -> u.hvals(channel, key));
    }
    //------------------------------------------------------RedisSetCommands-----------------------------------------------------

    @Override
    public Long sadd(K key, V... members) {
        return executeCommand(c -> c.sadd(key, members), u -> u.sadd(key, members));
    }

    @Override
    public Long scard(K key) {
        return executeCommand(c -> c.scard(key), u -> u.scard(key));
    }

    @Override
    public Set<V> sdiff(K... keys) {
        return executeCommand(c -> c.sdiff(keys), u -> u.sdiff(keys));
    }

    @Override
    public Long sdiff(ValueStreamingChannel<V> channel, K... keys) {
        return executeCommand(c -> c.sdiff(channel, keys), u -> u.sdiff(channel,keys));
    }

    @Override
    public Long sdiffstore(K destination, K... keys) {
        return executeCommand(c -> c.sdiffstore(destination, keys), u -> u.sdiffstore(destination, keys));
    }

    @Override
    public Set<V> sinter(K... keys) {
        return executeCommand(c -> c.sinter(keys), u -> u.sinter(keys));
    }

    @Override
    public Long sinter(ValueStreamingChannel<V> channel, K... keys) {
        return executeCommand(c -> c.sinter(channel, keys), u -> u.sinter(channel, keys));
    }

    @Override
    public Long sinterstore(K destination, K... keys) {
        return executeCommand(c -> c.sinterstore(destination, keys), u -> u.sinterstore(destination, keys));
    }

    @Override
    public Boolean sismember(K key, V member) {
        return executeCommand(c -> c.sismember(key, member), u -> u.sismember(key, member));
    }

    @Override
    public Boolean smove(K source, K destination, V member) {
        return executeCommand(c -> c.smove(source, destination, member), u -> u.smove(source, destination, member));
    }

    @Override
    public Set<V> smembers(K key) {
        return executeCommand(c -> c.smembers(key), u -> u.smembers(key));
    }

    @Override
    public Long smembers(ValueStreamingChannel<V> channel, K key) {
        return executeCommand(c -> c.smembers(channel, key), u -> u.smembers(channel, key));
    }

    @Override
    public V spop(K key) {
        return executeCommand(c -> c.spop(key), u -> u.spop(key));
    }

    @Override
    public Set<V> spop(K key, long count) {
        return executeCommand(c -> c.spop(key, count), u -> u.spop(key, count));
    }

    @Override
    public V srandmember(K key) {
        return executeCommand(c -> c.srandmember(key), u -> u.srandmember(key));
    }

    @Override
    public List<V> srandmember(K key, long count) {
        return executeCommand(c -> c.srandmember(key, count), u -> u.srandmember(key, count));
    }

    @Override
    public Long srandmember(ValueStreamingChannel<V> channel, K key, long count) {
        return executeCommand(c -> c.srandmember(channel, key, count), u -> u.srandmember(channel, key, count));
    }

    @Override
    public Long srem(K key, V... members) {
        return executeCommand(c -> c.srem(key, members), u -> u.srem(key, members));
    }

    @Override
    public Set<V> sunion(K... keys) {
        return executeCommand(c -> c.sunion(keys), u -> u.sunion(keys));
    }

    @Override
    public Long sunion(ValueStreamingChannel<V> channel, K... keys) {
        return executeCommand(c -> c.sunion(channel, keys), u -> u.sunion(channel, keys));
    }

    @Override
    public Long sunionstore(K destination, K... keys) {
        return executeCommand(c -> c.sunionstore(destination, keys), u -> u.sunionstore(destination, keys));
    }

    @Override
    public ValueScanCursor<V> sscan(K key) {
        return executeCommand(c -> c.sscan(key), u -> u.sscan(key));
    }

    @Override
    public ValueScanCursor<V> sscan(K key, ScanArgs scanArgs) {
        return executeCommand(c -> c.sscan(key, scanArgs), u -> u.sscan(key, scanArgs));
    }

    @Override
    public ValueScanCursor<V> sscan(K key, ScanCursor scanCursor, ScanArgs scanArgs) {
        return executeCommand(c -> c.sscan(key, scanCursor, scanArgs), u -> u.sscan(key, scanCursor, scanArgs));
    }

    @Override
    public ValueScanCursor<V> sscan(K key, ScanCursor scanCursor) {
        return executeCommand(c -> c.sscan(key, scanCursor), u -> u.sscan(key, scanCursor));
    }

    @Override
    public StreamScanCursor sscan(ValueStreamingChannel<V> channel, K key) {
        return executeCommand(c -> c.sscan(channel, key), u -> u.sscan(channel, key));
    }

    @Override
    public StreamScanCursor sscan(ValueStreamingChannel<V> channel, K key, ScanArgs scanArgs) {
        return executeCommand(c -> c.sscan(channel, key, scanArgs), u -> u.sscan(channel, key, scanArgs));
    }

    @Override
    public StreamScanCursor sscan(ValueStreamingChannel<V> channel, K key, ScanCursor scanCursor, ScanArgs scanArgs) {
        return executeCommand(c -> c.sscan(channel, key, scanCursor, scanArgs), u -> u.sscan(channel, key, scanCursor, scanArgs));
    }

    @Override
    public StreamScanCursor sscan(ValueStreamingChannel<V> channel, K key, ScanCursor scanCursor) {
        return executeCommand(c -> c.sscan(channel, key, scanCursor), u -> u.sscan(channel, key, scanCursor));
    }
    //------------------------------------------------------RedisSortedSetCommands-----------------------------------------------------
    @Override
    public KeyValue<K, ScoredValue<V>> bzpopmin(long timeout, K... keys) {
        return executeCommand(c -> c.bzpopmin(timeout, keys), u -> u.bzpopmin(timeout, keys));
    }

    @Override
    public KeyValue<K, ScoredValue<V>> bzpopmax(long timeout, K... keys) {
        return executeCommand(c -> c.bzpopmax(timeout, keys), u -> u.bzpopmax(timeout, keys));
    }

    @Override
    public Long zadd(K key, double score, V member) {
        return executeCommand(c -> c.zadd(key, score, member), u -> u.zadd(key, score, member));
    }

    @Override
    public Long zadd(K key, Object... scoresAndValues) {
        return executeCommand(c -> c.zadd(key, scoresAndValues), u -> u.zadd(key, scoresAndValues));
    }

    @Override
    public Long zadd(K key, ScoredValue<V>... scoredValues) {
        return executeCommand(c -> c.zadd(key, scoredValues), u -> u.zadd(key, scoredValues));
    }

    @Override
    public Long zadd(K key, ZAddArgs zAddArgs, double score, V member) {
        return executeCommand(c -> c.zadd(key, zAddArgs, score, member), u -> u.zadd(key, zAddArgs, score, member));
    }

    @Override
    public Long zadd(K key, ZAddArgs zAddArgs, Object... scoresAndValues) {
        return executeCommand(c -> c.zadd(key, zAddArgs, scoresAndValues), u -> u.zadd(key, zAddArgs, scoresAndValues));
    }

    @Override
    public Long zadd(K key, ZAddArgs zAddArgs, ScoredValue<V>... scoredValues) {
        return executeCommand(c -> c.zadd(key, zAddArgs, scoredValues), u -> u.zadd(key, zAddArgs, scoredValues));
    }

    @Override
    public Double zaddincr(K key, double score, V member) {
        return executeCommand(c -> c.zaddincr(key, score, member), u -> u.zaddincr(key, score, member));
    }

    @Override
    public Double zaddincr(K key, ZAddArgs zAddArgs, double score, V member) {
        return executeCommand(c -> c.zaddincr(key, zAddArgs, score, member), u -> u.zaddincr(key, zAddArgs, score, member));
    }

    @Override
    public Long zcard(K key) {
        return executeCommand(c -> c.zcard(key), u -> u.zcard(key));
    }

    @Override
    public Long zcount(K key, double min, double max) {
        return executeCommand(c -> c.zcount(key, min, max), u -> u.zcount(key, min, max));
    }

    @Override
    public Long zcount(K key, String min, String max) {
        return executeCommand(c -> c.zcount(key, min, max), u -> u.zcount(key, min, max));
    }

    @Override
    public Long zcount(K key, Range<? extends Number> range) {
        return executeCommand(c -> c.zcount(key, range), u -> u.zcount(key, range));
    }

    @Override
    public Double zincrby(K key, double amount,V member) {
        return executeCommand(c -> c.zincrby(key, amount, member), u -> u.zincrby(key, amount, member));
    }

    @Override
    public Long zinterstore(K destination, K... keys) {
        return executeCommand(c -> c.zinterstore(destination, keys), u -> u.zinterstore(destination, keys));
    }

    @Override
    public Long zinterstore(K destination, ZStoreArgs storeArgs, K... keys) {
        return executeCommand(c -> c.zinterstore(destination, storeArgs, keys), u -> u.zinterstore(destination, storeArgs, keys));
    }

    @Override
    public Long zlexcount(K key, String min, String max) {
        return executeCommand(c -> c.zlexcount(key, min, max), u -> u.zlexcount(key, min, max));
    }

    @Override
    public Long zlexcount(K key, Range<? extends V> range) {
        return executeCommand(c -> c.zlexcount(key, range), u -> u.zlexcount(key, range));
    }

    @Override
    public ScoredValue<V> zpopmin(K key) {
        return executeCommand(c -> c.zpopmin(key), u -> u.zpopmin(key));
    }

    @Override
    public List<ScoredValue<V>> zpopmin(K key, long count) {
        return executeCommand(c -> c.zpopmin(key, count), u -> u.zpopmin(key, count));
    }

    @Override
    public ScoredValue<V> zpopmax(K key) {
        return executeCommand(c -> c.zpopmax(key), u -> u.zpopmax(key));
    }

    @Override
    public List<ScoredValue<V>> zpopmax(K key, long count) {
        return executeCommand(c -> c.zpopmax(key, count), u -> u.zpopmax(key, count));
    }

    @Override
    public List<V> zrange(K key, long start, long stop) {
        return executeCommand(c -> c.zrange(key, start, stop), u -> u.zrange(key, start, stop));
    }

    @Override
    public Long zrange(ValueStreamingChannel<V> channel,K key, long start, long stop) {
        return executeCommand(c -> c.zrange(channel,key, start, stop), u -> u.zrange(channel,key, start, stop));
    }

    @Override
    public List<ScoredValue<V>> zrangeWithScores(K key, long start, long stop) {
        return executeCommand(c -> c.zrangeWithScores(key, start, stop), u -> u.zrangeWithScores(key, start, stop));
    }

    @Override
    public Long zrangeWithScores(ScoredValueStreamingChannel<V> channel, K key, long start, long stop) {
        return executeCommand(c -> c.zrangeWithScores(channel, key, start, stop), u -> u.zrangeWithScores(channel, key, start, stop));
    }

    @Override
    public List<V> zrangebylex(K key, String min, String max) {
        return executeCommand(c -> c.zrangebylex(key, min, max), u -> u.zrangebylex(key, min, max));
    }

    @Override
    public List<V> zrangebylex(K key, Range<? extends V> range) {
        return executeCommand(c -> c.zrangebylex(key, range), u -> u.zrangebylex(key, range));
    }

    @Override
    public List<V> zrangebylex(K key, String min, String max, long offset, long count) {
        return executeCommand(c -> c.zrangebylex(key, min, max, offset, count), u -> u.zrangebylex(key, min, max, offset, count));
    }

    @Override
    public List<V> zrangebylex(K key, Range<? extends V> range, Limit limit) {
        return executeCommand(c -> c.zrangebylex(key, range, limit), u -> u.zrangebylex(key, range, limit));
    }

    @Override
    public List<V> zrangebyscore(K key, double min, double max) {
        return executeCommand(c -> c.zrangebyscore(key, min, max), u -> u.zrangebyscore(key, min, max));
    }

    @Override
    public List<V> zrangebyscore(K key, String min, String max) {
        return executeCommand(c -> c.zrangebyscore(key, min, max), u -> u.zrangebyscore(key, min, max));
    }

    @Override
    public List<V> zrangebyscore(K key, Range<? extends Number> range) {
        return executeCommand(c -> c.zrangebyscore(key, range), u -> u.zrangebyscore(key, range));
    }

    @Override
    public List<V> zrangebyscore(K key, double min, double max, long offset, long count) {
        return executeCommand(c -> c.zrangebyscore(key, min, max, offset, count), u -> u.zrangebyscore(key, min, max, offset, count));
    }

    @Override
    public List<V> zrangebyscore(K key, String min, String max, long offset, long count) {
        return executeCommand(c -> c.zrangebyscore(key, min, max, offset, count), u -> u.zrangebyscore(key, min, max, offset, count));
    }

    @Override
    public List<V> zrangebyscore(K key, Range<? extends Number> range, Limit limit) {
        return executeCommand(c -> c.zrangebyscore(key, range, limit), u -> u.zrangebyscore(key, range, limit));
    }

    @Override
    public Long zrangebyscore(ValueStreamingChannel<V> channel, K key, double min, double max) {
        return executeCommand(c -> c.zrangebyscore(channel, key, min, max), u -> u.zrangebyscore(channel, key, min, max));
    }

    @Override
    public Long zrangebyscore(ValueStreamingChannel<V> channel, K key, String min, String max) {
        return executeCommand(c -> c.zrangebyscore(channel, key, min, max), u -> u.zrangebyscore(channel, key, min, max));
    }

    @Override
    public Long zrangebyscore(ValueStreamingChannel<V> channel, K key, Range<? extends Number> range) {
        return executeCommand(c -> c.zrangebyscore(channel, key, range), u -> u.zrangebyscore(channel, key, range));
    }

    @Override
    public Long zrangebyscore(ValueStreamingChannel<V> channel, K key, double min, double max, long offset, long count) {
        return executeCommand(c -> c.zrangebyscore(channel, key, min, max, offset, count), u -> u.zrangebyscore(channel, key, min, max, offset, count));
    }

    @Override
    public Long zrangebyscore(ValueStreamingChannel<V> channel, K key, String min, String max, long offset, long count) {
        return executeCommand(c -> c.zrangebyscore(channel, key, min, max, offset, count), u -> u.zrangebyscore(channel, key, min, max, offset, count));
    }

    @Override
    public Long zrangebyscore(ValueStreamingChannel<V> channel, K key, Range<? extends Number> range, Limit limit) {
        return executeCommand(c -> c.zrangebyscore(channel, key, range, limit), u -> u.zrangebyscore(channel, key, range, limit));
    }

    @Override
    public List<ScoredValue<V>> zrangebyscoreWithScores(K key, double min, double max) {
        return executeCommand(c -> c.zrangebyscoreWithScores(key, min, max), u -> u.zrangebyscoreWithScores(key, min, max));
    }

    @Override
    public List<ScoredValue<V>> zrangebyscoreWithScores(K key, String min, String max) {
        return executeCommand(c -> c.zrangebyscoreWithScores(key, min, max), u -> u.zrangebyscoreWithScores(key, min, max));
    }

    @Override
    public List<ScoredValue<V>> zrangebyscoreWithScores(K key, Range<? extends Number> range) {
        return executeCommand(c -> c.zrangebyscoreWithScores(key,range), u -> u.zrangebyscoreWithScores(key,range));
    }

    @Override
    public List<ScoredValue<V>> zrangebyscoreWithScores(K key, double min, double max, long offset, long count) {
        return executeCommand(c -> c.zrangebyscoreWithScores(key, min, max, offset, count), u -> u.zrangebyscoreWithScores(key, min, max, offset, count));
    }

    @Override
    public List<ScoredValue<V>> zrangebyscoreWithScores(K key, String min, String max, long offset, long count) {
        return executeCommand(c -> c.zrangebyscoreWithScores(key, min, max, offset, count), u -> u.zrangebyscoreWithScores(key, min, max, offset, count));
    }

    @Override
    public List<ScoredValue<V>> zrangebyscoreWithScores(K key, Range<? extends Number> range, Limit limit) {
        return executeCommand(c -> c.zrangebyscoreWithScores(key, range, limit), u -> u.zrangebyscoreWithScores(key, range, limit));
    }

    @Override
    public Long zrangebyscoreWithScores(ScoredValueStreamingChannel<V> channel, K key, double min, double max) {
        return executeCommand(c -> c.zrangebyscoreWithScores(channel, key, min, max), u -> u.zrangebyscoreWithScores(channel, key, min, max));
    }

    @Override
    public Long zrangebyscoreWithScores(ScoredValueStreamingChannel<V> channel, K key, String min, String max) {
        return executeCommand(c -> c.zrangebyscoreWithScores(channel, key, min, max), u -> u.zrangebyscoreWithScores(channel, key, min, max));
    }

    @Override
    public Long zrangebyscoreWithScores(ScoredValueStreamingChannel<V> channel, K key, Range<? extends Number> range) {
        return executeCommand(c -> c.zrangebyscoreWithScores(channel, key, range), u -> u.zrangebyscoreWithScores(channel, key, range));
    }

    @Override
    public Long zrangebyscoreWithScores(ScoredValueStreamingChannel<V> channel, K key, double min, double max, long offset, long count) {
        return executeCommand(c -> c.zrangebyscoreWithScores(channel, key, min, max, offset, count), u -> u.zrangebyscoreWithScores(channel, key, min, max, offset, count));
    }

    @Override
    public Long zrangebyscoreWithScores(ScoredValueStreamingChannel<V> channel, K key, String min, String max, long offset, long count) {
        return executeCommand(c -> c.zrangebyscoreWithScores(channel, key, min, max, offset, count), u -> u.zrangebyscoreWithScores(channel, key, min, max, offset, count));
    }

    @Override
    public Long zrangebyscoreWithScores(ScoredValueStreamingChannel<V> channel, K key, Range<? extends Number> range, Limit limit) {
        return executeCommand(c -> c.zrangebyscoreWithScores(channel, key, range, limit), u -> u.zrangebyscoreWithScores(channel, key, range, limit));
    }

    @Override
    public Long zrank(K key, V member) {
        return executeCommand(c -> c.zrank(key, member), u -> u.zrank(key, member));
    }

    @Override
    public Long zrem(K key, V... members) {
        return executeCommand(c -> c.zrem(key, members), u -> u.zrem(key, members));
    }

    @Override
    public Long zremrangebylex(K key, String min, String max) {
        return executeCommand(c -> c.zremrangebylex(key, min, max), u -> u.zremrangebylex(key, min, max));
    }

    @Override
    public Long zremrangebylex(K key, Range<? extends V> range) {
        return executeCommand(c -> c.zremrangebylex(key, range), u -> u.zremrangebylex(key, range));
    }

    @Override
    public Long zremrangebyrank(K key, long start, long stop) {
        return executeCommand(c -> c.zremrangebyrank(key, start, stop), u -> u.zremrangebyrank(key, start, stop));
    }

    @Override
    public Long zremrangebyscore(K key, double min, double max) {
        return executeCommand(c -> c.zremrangebyscore(key, min, max), u -> u.zremrangebyscore(key, min, max));
    }

    @Override
    public Long zremrangebyscore(K key, String min, String max) {
        return executeCommand(c -> c.zremrangebyscore(key, min, max), u -> u.zremrangebyscore(key, min, max));
    }

    @Override
    public Long zremrangebyscore(K key, Range<? extends Number> range) {
        return executeCommand(c -> c.zremrangebyscore(key, range), u -> u.zremrangebyscore(key, range));
    }

    @Override
    public List<V> zrevrange(K key, long start, long stop) {
        return executeCommand(c -> c.zrevrange(key, start, stop), u -> u.zrevrange(key, start, stop));
    }

    @Override
    public Long zrevrange(ValueStreamingChannel<V> channel, K key, long start, long stop) {
        return executeCommand(c -> c.zrevrange(channel, key, start, stop), u -> u.zrevrange(channel, key, start, stop));
    }

    @Override
    public List<ScoredValue<V>> zrevrangeWithScores(K key, long start, long stop) {
        return executeCommand(c -> c.zrevrangeWithScores(key, start, stop), u -> u.zrevrangeWithScores(key, start, stop));
    }

    @Override
    public Long zrevrangeWithScores(ScoredValueStreamingChannel<V> channel, K key, long start, long stop) {
        return executeCommand(c -> c.zrevrangeWithScores(channel, key, start, stop), u -> u.zrevrangeWithScores(channel, key, start, stop));
    }

    @Override
    public List<V> zrevrangebylex(K key, Range<? extends V> range) {
        return executeCommand(c -> c.zrevrangebylex(key, range), u -> u.zrevrangebylex(key, range));
    }

    @Override
    public List<V> zrevrangebylex(K key, Range<? extends V> range, Limit limit) {
        return executeCommand(c -> c.zrevrangebylex(key, range, limit), u -> u.zrevrangebylex(key, range, limit));
    }

    @Override
    public List<V> zrevrangebyscore(K key, double max, double min) {
        return executeCommand(c -> c.zrevrangebyscore(key, max, min), u -> u.zrevrangebyscore(key, max, min));
    }

    @Override
    public List<V> zrevrangebyscore(K key, String max, String min) {
        return executeCommand(c -> c.zrevrangebyscore(key, max, min), u -> u.zrevrangebyscore(key, max, min));
    }

    @Override
    public List<V> zrevrangebyscore(K key, Range<? extends Number> range) {
        return executeCommand(c -> c.zrevrangebyscore(key, range), u -> u.zrevrangebyscore(key, range));
    }

    @Override
    public List<V> zrevrangebyscore(K key, double max, double min, long offset, long count) {
        return executeCommand(c -> c.zrevrangebyscore(key, max, min, offset, count), u -> u.zrevrangebyscore(key, max, min, offset, count));
    }

    @Override
    public List<V> zrevrangebyscore(K key, String max, String min, long offset, long count) {
        return executeCommand(c -> c.zrevrangebyscore(key, max, min, offset, count), u -> u.zrevrangebyscore(key, max, min, offset, count));
    }

    @Override
    public List<V> zrevrangebyscore(K key, Range<? extends Number> range, Limit limit) {
        return executeCommand(c -> c.zrevrangebyscore(key, range, limit), u -> u.zrevrangebyscore(key, range, limit));
    }

    @Override
    public Long zrevrangebyscore(ValueStreamingChannel<V> channel, K key, double max, double min) {
        return executeCommand(c -> c.zrevrangebyscore(channel, key, max, min), u -> u.zrevrangebyscore(channel, key, max, min));
    }

    @Override
    public Long zrevrangebyscore(ValueStreamingChannel<V> channel, K key, String max, String min) {
        return executeCommand(c -> c.zrevrangebyscore(channel, key, max, min), u -> u.zrevrangebyscore(channel, key, max, min));
    }

    @Override
    public Long zrevrangebyscore(ValueStreamingChannel<V> channel, K key, Range<? extends Number> range) {
        return executeCommand(c -> c.zrevrangebyscore(channel, key, range), u -> u.zrevrangebyscore(channel, key, range));
    }

    @Override
    public Long zrevrangebyscore(ValueStreamingChannel<V> channel, K key, double max, double min, long offset, long count) {
        return executeCommand(c -> c.zrevrangebyscore(channel, key, max, min, offset, count), u -> u.zrevrangebyscore(channel, key, max, min, offset, count));
    }

    @Override
    public Long zrevrangebyscore(ValueStreamingChannel<V> channel, K key, String max, String min, long offset, long count) {
        return executeCommand(c -> c.zrevrangebyscore(channel, key, max, min, offset, count), u -> u.zrevrangebyscore(channel, key, max, min, offset, count));
    }

    @Override
    public Long zrevrangebyscore(ValueStreamingChannel<V> channel, K key, Range<? extends Number> range, Limit limit) {
        return executeCommand(c -> c.zrevrangebyscore(channel, key, range, limit), u -> u.zrevrangebyscore(channel, key, range, limit));
    }

    @Override
    public List<ScoredValue<V>> zrevrangebyscoreWithScores(K key, double max, double min) {
        return executeCommand(c -> c.zrevrangebyscoreWithScores(key, max, min), u -> u.zrevrangebyscoreWithScores(key, max, min));
    }

    @Override
    public List<ScoredValue<V>> zrevrangebyscoreWithScores(K key, String max, String min) {
        return executeCommand(c -> c.zrevrangebyscoreWithScores(key, max, min), u -> u.zrevrangebyscoreWithScores(key, max, min));
    }

    @Override
    public List<ScoredValue<V>> zrevrangebyscoreWithScores(K key, Range<? extends Number> range) {
        return executeCommand(c -> c.zrevrangebyscoreWithScores(key, range), u -> u.zrevrangebyscoreWithScores(key, range));
    }

    @Override
    @Deprecated
    public List<ScoredValue<V>> zrevrangebyscoreWithScores(K key, double max, double min, long offset, long count) {
        return executeCommand(c -> c.zrevrangebyscoreWithScores(key, max, min, offset, count), u -> u.zrevrangebyscoreWithScores(key, max, min, offset, count));
    }

    @Override
    @Deprecated
    public List<ScoredValue<V>> zrevrangebyscoreWithScores(K key, String max, String min, long offset, long count) {
        return executeCommand(c -> c.zrevrangebyscoreWithScores(key, max, max, offset, count), u -> u.zrevrangebyscoreWithScores(key, max, min, offset, count));
    }

    @Override
    public List<ScoredValue<V>> zrevrangebyscoreWithScores(K key, Range<? extends Number> range, Limit limit) {
        return executeCommand(c -> c.zrevrangebyscoreWithScores(key, range, limit), u -> u.zrevrangebyscoreWithScores(key, range, limit));
    }

    @Override
    @Deprecated
    public Long zrevrangebyscoreWithScores(ScoredValueStreamingChannel<V> channel, K key, double max, double min) {
        return executeCommand(c -> c.zrevrangebyscoreWithScores(channel, key, max, min), u -> u.zrevrangebyscoreWithScores(channel, key, max, min));
    }

    @Override
    @Deprecated
    public Long zrevrangebyscoreWithScores(ScoredValueStreamingChannel<V> channel, K key, String max, String min) {
        return executeCommand(c -> c.zrevrangebyscoreWithScores(channel, key, max, min), u -> u.zrevrangebyscoreWithScores(channel, key, max, min));
    }

    @Override
    @Deprecated
    public Long zrevrangebyscoreWithScores(ScoredValueStreamingChannel<V> channel, K key, Range<? extends Number> range) {
        return executeCommand(c -> c.zrevrangebyscoreWithScores(channel, key, range), u -> u.zrevrangebyscoreWithScores(channel, key, range));
    }

    @Override
    @Deprecated
    public Long zrevrangebyscoreWithScores(ScoredValueStreamingChannel<V> channel, K key, double max, double min, long offset, long count) {
        return executeCommand(c -> c.zrevrangebyscoreWithScores(channel,key, max, min, offset, count), u -> u.zrevrangebyscoreWithScores(channel,key, max, min, offset, count));
    }


    @Override
    @Deprecated
    public Long zrevrangebyscoreWithScores(ScoredValueStreamingChannel<V> channel, K key, String max, String min, long offset, long count) {
        return executeCommand(c -> c.zrevrangebyscoreWithScores(channel,key, max, min, offset, count), u -> u.zrevrangebyscoreWithScores(channel,key, max, min, offset, count));
    }

    @Override
    public Long zrevrangebyscoreWithScores(ScoredValueStreamingChannel<V> channel, K key, Range<? extends Number> range, Limit limit) {
        return executeCommand(c -> c.zrevrangebyscoreWithScores(channel, key, range, limit), u -> u.zrevrangebyscoreWithScores(channel, key, range, limit));
    }

    @Override
    public Long zrevrank(K key, V member) {
        return executeCommand(c -> c.zrevrank(key, member), u -> u.zrevrank(key, member));
    }

    @Override
    public ScoredValueScanCursor<V> zscan(K key) {
        return executeCommand(c -> c.zscan(key), u -> u.zscan(key));
    }

    @Override
    public ScoredValueScanCursor<V> zscan(K key, ScanArgs scanArgs) {
        return executeCommand(c -> c.zscan(key, scanArgs), u -> u.zscan(key, scanArgs));
    }

    @Override
    public ScoredValueScanCursor<V> zscan(K key, ScanCursor scanCursor, ScanArgs scanArgs) {
        return executeCommand(c -> c.zscan(key, scanCursor, scanArgs), u -> u.zscan(key, scanCursor, scanArgs));
    }

    @Override
    public ScoredValueScanCursor<V> zscan(K key, ScanCursor scanCursor) {
        return executeCommand(c -> c.zscan(key, scanCursor), u -> u.zscan(key, scanCursor));
    }

    @Override
    public StreamScanCursor zscan(ScoredValueStreamingChannel<V> channel, K key) {
        return executeCommand(c -> c.zscan(channel, key), u -> u.zscan(channel, key));
    }

    @Override
    public StreamScanCursor zscan(ScoredValueStreamingChannel<V> channel, K key, ScanArgs scanArgs) {
        return executeCommand(c -> c.zscan(channel, key, scanArgs), u -> u.zscan(channel, key, scanArgs));
    }

    @Override
    public StreamScanCursor zscan(ScoredValueStreamingChannel<V> channel, K key, ScanCursor scanCursor, ScanArgs scanArgs) {
        return executeCommand(c -> c.zscan(channel, key, scanCursor, scanArgs), u -> u.zscan(channel, key, scanCursor, scanArgs));
    }

    @Override
    public StreamScanCursor zscan(ScoredValueStreamingChannel<V> channel, K key, ScanCursor scanCursor) {
        return executeCommand(c -> c.zscan(channel, key, scanCursor), u -> u.zscan(channel, key, scanCursor));
    }

    @Override
    public Double zscore(K key, V member) {
        return executeCommand(c -> c.zscore(key, member), u -> u.zscore(key, member));
    }

    @Override
    public Long zunionstore(K destination, K... keys) {
        return executeCommand(c -> c.zunionstore(destination, keys), u -> u.zunionstore(destination, keys));
    }

    @Override
    public Long zunionstore(K destination, ZStoreArgs storeArgs, K... keys) {
        return executeCommand(c -> c.zunionstore(destination, storeArgs, keys), u -> u.zunionstore(destination, storeArgs, keys));
    }
    //------------------------------------------------------RedisKeyCommands-----------------------------------------------------
    @Override
    public Long del(K... keys) {
        return executeCommand(c -> c.del(keys), u -> u.del(keys));
    }

    @Override
    public Long unlink(K... keys) {
        return executeCommand(c -> c.unlink(keys), u -> u.unlink(keys));
    }

    @Override
    public byte[] dump(K key) {
        return executeCommand(c -> c.dump(key), u -> u.dump(key));
    }

    @Override
    public Long exists(K... keys) {
        return executeCommand(c -> c.exists(keys), u -> u.exists(keys));
    }

    @Override
    public Boolean expire(K key, long seconds) {
        return executeCommand(c -> c.expire(key, seconds), u -> u.expire(key, seconds));
    }

    @Override
    public Boolean expireat(K key, Date timestamp) {
        return executeCommand(c -> c.expireat(key, timestamp), u -> u.expireat(key, timestamp));
    }

    @Override
    public Boolean expireat(K key, long timestamp) {
        return executeCommand(c -> c.expireat(key, timestamp), u -> u.expireat(key, timestamp));
    }

    @Override
    public List<K> keys(K pattern) {
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
    public Long keys(KeyStreamingChannel<K> channel, K pattern) {
        return executeCommand(c -> c.keys(channel, pattern), u -> u.keys(channel, pattern));
    }

    @Override
    public String migrate(String host, int port, K key, int db, long timeout) {
        return executeCommand(c -> c.migrate(host, port, key, db, timeout), u -> u.migrate(host, port, key, db, timeout));
    }

    @Override
    public String migrate(String host, int port, int db, long timeout, MigrateArgs<K> migrateArgs) {
        return executeCommand(c -> c.migrate(host, port, db, timeout, migrateArgs), u -> u.migrate(host, port, db, timeout, migrateArgs));
    }

    @Override
    public Boolean move(K key, int db) {
        return executeCommand(c -> c.move(key, db), u -> u.move(key, db));
    }

    @Override
    public String objectEncoding(K key) {
        return executeCommand(c -> c.objectEncoding(key), u -> u.objectEncoding(key));
    }

    @Override
    public Long objectIdletime(K key) {
        return executeCommand(c -> c.objectIdletime(key), u -> u.objectIdletime(key));
    }

    @Override
    public Long objectRefcount(K key) {
        return executeCommand(c -> c.objectRefcount(key), u -> u.objectRefcount(key));
    }

    @Override
    public Boolean persist(K key) {
        return executeCommand(c -> c.persist(key), u -> u.persist(key));
    }

    @Override
    public Boolean pexpire(K key, long milliseconds) {
        return executeCommand(c -> c.pexpire(key, milliseconds), u -> u.pexpire(key, milliseconds));
    }

    @Override
    public Boolean pexpireat(K key, Date timestamp) {
        return executeCommand(c -> c.pexpireat(key, timestamp), u -> u.pexpireat(key, timestamp));
    }

    @Override
    public Boolean pexpireat(K key, long timestamp) {
        return executeCommand(c -> c.pexpireat(key, timestamp), u -> u.pexpireat(key, timestamp));
    }

    @Override
    public Long pttl(K key) {
        return executeCommand(c -> c.pttl(key), u -> u.pttl(key));
    }

    @Override
    public V randomkey() {
        return executeCommand(c -> c.randomkey(), u -> u.randomkey());
    }

    @Override
    public String rename(K key, K newKey) {
        return executeCommand(c -> c.rename(key, newKey), u -> u.rename(key, newKey));

    }

    @Override
    public Boolean renamenx(K key, K newKey) {
        return executeCommand(c -> c.renamenx(key, newKey), u -> {
//			return u.renamenx(key, newKey);
            if(key.equals(newKey)){
                return true;
            }
            if(get(newKey) == null){
                V temp = get(key);
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
    public String restore(K key, long ttl, byte[] value) {
        return executeCommand(c -> c.restore(key, ttl, value), u -> u.restore(key, ttl, value));
    }

    @Override
    public String restore(K key, byte[] value, RestoreArgs args) {
        return executeCommand(c -> c.restore(key, value, args), u -> u.restore(key, value, args));
    }

    @Override
    public List<V> sort(K key) {
        return executeCommand(c -> c.sort(key), u -> u.sort(key));
    }

    @Override
    public Long sort(ValueStreamingChannel<V> channel, K key) {
        return executeCommand(c -> c.sort(channel, key), u -> u.sort(channel, key));
    }

    @Override
    public List<V> sort(K key, SortArgs sortArgs) {
        return executeCommand(c -> c.sort(key, sortArgs), u -> u.sort(key, sortArgs));
    }

    @Override
    public Long sort(ValueStreamingChannel<V> channel, K key, SortArgs sortArgs) {
        return executeCommand(c -> c.sort(channel, key, sortArgs), u -> u.sort(channel, key, sortArgs));
    }

    @Override
    public Long sortStore(K key, SortArgs sortArgs, K destination) {
        return executeCommand(c -> c.sortStore(key, sortArgs, destination), u -> u.sortStore(key, sortArgs, destination));
    }

    @Override
    public Long touch(K... keys) {
        return executeCommand(c -> c.touch(keys), u -> u.touch(keys));
    }

    @Override
    public Long ttl(K key) {
        return executeCommand(c -> c.ttl(key), u -> u.ttl(key));
    }

    @Override
    public String type(K key) {
        return executeCommand(c -> c.type(key), u -> u.type(key));
    }

    @Override
    public KeyScanCursor<K> scan() {
        return executeCommand(c -> c.scan(), u -> u.scan());
    }

    @Override
    public KeyScanCursor<K> scan(ScanArgs scanArgs) {
        return executeCommand(c -> c.scan(scanArgs), u -> u.scan(scanArgs));
    }

    @Override
    public KeyScanCursor<K> scan(ScanCursor scanCursor, ScanArgs scanArgs) {
        return executeCommand(c -> c.scan(scanCursor,scanArgs), u -> u.scan(scanCursor,scanArgs));
    }

    @Override
    public KeyScanCursor<K> scan(ScanCursor scanCursor) {
        return executeCommand(c -> c.scan(scanCursor), u -> u.scan(scanCursor));
    }

    @Override
    public StreamScanCursor scan(KeyStreamingChannel<K> channel) {
        return executeCommand(c -> c.scan(channel), u -> u.scan(channel));
    }

    @Override
    public StreamScanCursor scan(KeyStreamingChannel<K> channel, ScanArgs scanArgs) {
        return executeCommand(c -> c.scan(channel,scanArgs), u -> u.scan(channel,scanArgs));
    }

    @Override
    public StreamScanCursor scan(KeyStreamingChannel<K> channel, ScanCursor scanCursor, ScanArgs scanArgs) {
        return executeCommand(c -> c.scan(channel,scanCursor,scanArgs), u -> u.scan(channel,scanCursor,scanArgs));
    }

    @Override
    public StreamScanCursor scan(KeyStreamingChannel<K> channel, ScanCursor scanCursor) {
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
    public K clientGetname() {
        return executeCommand(c -> c.clientGetname(), u -> u.clientGetname());
    }

    @Override
    public String clientSetname(K name) {
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
    public String debugObject(K key) {
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
    public String debugSdslen(K key) {
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
    public List<V> time() {
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
    public String watch(K... keys) {
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
	public TransactionResult execMulti(Consumer<RedisCommands<K, V>> statement ) throws UnsupportedOperationException{
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
