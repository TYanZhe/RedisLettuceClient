package cn.org.tpeach.nosql.lettuce;

import io.lettuce.core.*;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.api.sync.RedisStringCommands;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import io.lettuce.core.support.ConnectionPoolSupport;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.ArrayList;

public class RedisLettuce {
    public static void main(String[] args) throws Exception {
        single();
    }
    public static void cluster(){
        ArrayList<RedisURI> list = new ArrayList<>();
        list.add(RedisURI.create("redis://longshine@172.18.125.68:7000"));
        list.add(RedisURI.create("redis://longshine@172.18.125.68:7001"));
        list.add(RedisURI.create("redis://longshine@172.18.125.68:7002"));
        list.add(RedisURI.create("redis://longshine@172.18.125.68:7003"));
        list.add(RedisURI.create("redis://longshine@172.18.125.68:7004"));
        list.add(RedisURI.create("redis://longshine@172.18.125.68:7005"));

        RedisClusterClient client = RedisClusterClient.create(list);
        //RedisClusterClient client = RedisClusterClient.create("redis://192.168.37.128:7000");
        StatefulRedisClusterConnection<String, String> connect = client.connect();

        /* 同步执行的命令 */
        RedisAdvancedClusterCommands<String, String> commands = connect.sync();
        commands.dbsize();
        String str = commands.get("test2");
        ScanCursor cursor = ScanCursor.INITIAL;
        ScanArgs scanArgs = new ScanArgs();
        scanArgs.match("*");
        scanArgs.limit(1000);
        KeyScanCursor<String> stringKeyScanCursor = commands.scan(cursor, scanArgs);
        stringKeyScanCursor.getKeys().forEach(System.err::println);
        String ping = commands.ping();
        System.out.println(ping);
        System.out.println(str);

        /*  异步执行的命令  */
//      RedisAdvancedClusterAsyncCommands<String, String> commands= connect.async();
//      RedisFuture<String> future = commands.get("test2");
//      try {
//          String str = future.get();
//          System.out.println(str);
//      } catch (InterruptedException e) {
//          e.printStackTrace();
//      } catch (ExecutionException e) {
//          e.printStackTrace();
//      }

        connect.close();
        client.shutdown();
    }

    public static void single() throws Exception {
        // client
        RedisClient client = RedisClient.create("redis://localhost:6379");

//        // connection, 线程安全的长连接，连接丢失时会自动重连，直到调用 close 关闭连接。
//        StatefulRedisConnection<String, String> connection = client.connect();
//
//        // sync, 默认超时时间为 60s.
//        RedisStringCommands<String, String> sync = connection.sync();
//        KeyScanCursor<String> scan = ((RedisCommands<String, String>) sync).scan();
//
//        sync.set("host", "note.abeffect.com");
//        String value = sync.get("host");
//        System.out.println(value);
//
//        // close connection
//        connection.close();

        // shutdown
//        client.shutdown();

        GenericObjectPool<StatefulRedisConnection<String, String>> pool = ConnectionPoolSupport.createGenericObjectPool(
                () -> client.connect(), new GenericObjectPoolConfig(), false);

//        for (int i = 0; i < 10; i++) {
//            StatefulRedisConnection<String, String> connection = pool.borrowObject();
//            RedisCommands<String, String> sync1 = connection.sync();
//            sync1.ping();
//            pool.returnObject(connection);
//        }
        StatefulRedisConnection<String, String> connection = pool.borrowObject();
        RedisCommands<String, String> sync1 = connection.sync();
        final ScanCursor initial = ScanCursor.INITIAL;
        final ScanArgs scanArgs = ScanArgs.Builder.limit(10000);
        scanArgs.match("*");
        final KeyScanCursor<String> scan = sync1.scan(ScanCursor.INITIAL,scanArgs);

        System.out.println();

    }
}
