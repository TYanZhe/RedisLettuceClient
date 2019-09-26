package cn.org.tpeach.nosql.redis.connection.impl;

import cn.org.tpeach.nosql.enums.RedisStructure;
import cn.org.tpeach.nosql.exception.ServiceException;
import cn.org.tpeach.nosql.redis.connection.RedisLark;
import cn.org.tpeach.nosql.tools.StringUtils;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import io.lettuce.core.codec.RedisCodec;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
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
            auth = auth + "@";
        }
        // client
        this.client = RedisClient.create(String.format(redisUrlFormat, auth, host, port));
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
}
