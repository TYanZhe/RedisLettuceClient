package cn.org.tpeach.nosql.lettuce;

import cn.org.tpeach.nosql.ApplicationTest;
import cn.org.tpeach.nosql.enums.RedisStructure;
import cn.org.tpeach.nosql.exception.ServiceException;
import cn.org.tpeach.nosql.framework.LarkFrame;
import cn.org.tpeach.nosql.redis.bean.RedisConnectInfo;
import cn.org.tpeach.nosql.redis.command.connection.PingCommand;
import cn.org.tpeach.nosql.redis.command.connection.SelectCommand;
import cn.org.tpeach.nosql.redis.command.hash.HmSetHash;
import cn.org.tpeach.nosql.redis.command.hash.HsetHash;
import cn.org.tpeach.nosql.redis.command.list.RpushList;
import cn.org.tpeach.nosql.redis.command.set.SAddSet;
import cn.org.tpeach.nosql.redis.command.string.SetnxString;
import cn.org.tpeach.nosql.redis.command.zset.ZmAddSet;
import cn.org.tpeach.nosql.redis.connection.RedisLarkPool;
import cn.org.tpeach.nosql.tools.StringUtils;
import io.lettuce.core.ScoredValue;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RedisTest {
    static String id = "0e5f3c61-488e-4d3c-9df0-5f130416ac93";

    public static LinkedList<String> ids = new LinkedList<>();
    static {
        ids.add(id);
        for (int i = 1; i < 50; i++) {
            ids.add(StringUtils.getUUID());
        }
    }
    /**
     *
     */
    @Before
    public void connectSingle(){
        LarkFrame.run(ApplicationTest.class);
        for (String s : ids) {

            //构建连接信息
            RedisConnectInfo conn = new RedisConnectInfo();
            conn.setId(s);
            conn.setStructure(RedisStructure.SINGLE.getCode());
            conn.setHost("127.0.0.1");
            conn.setPort(6379);
            RedisLarkPool.addOrUpdateConnectInfo(conn);
            String ping = new PingCommand(id).execute();
            if(!"PONG".equals(ping)){
                throw new ServiceException("Ping命令执行失败");
            }
            System.out.println("连接成功");
        }

    }
    @Test
    public void testList()   {
        try {
            int index = 0;
            byte[][] list = new byte[100000][];
            for (int i = 0; i < 100000; i++) {
                list[i] = ("测试" + index).getBytes("UTF-8");
                index++;
                try {
//				TimeUnit.MICROSECONDS.sleep(100);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            new RpushList(id, 0, "list".getBytes("UTF-8"), list).execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Test
    public void testString(){
        for (int i = 0; i < 10000; i++) {

//                    System.out.println("测试bigger_" +i);

            try {
//				TimeUnit.MICROSECONDS.sleep(100);
                SetnxString setnxString = new SetnxString(id, 6, ("测试" + i).getBytes("UTF-8"), (i + "").getBytes("UTF-8"));
                setnxString.setPrintLog(false);
                setnxString.execute();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }
    @Test
    public void testBatchString(){
        int num = 10000 /50;
        ExecutorService executorService = Executors.newCachedThreadPool();
        CountDownLatch countDownLatch = new CountDownLatch(50);
        for(int x = 0;x<50;x++){
            int finalX = x;
            String remove = ids.removeFirst();
            executorService.execute(()->{
                try {
                    for (int i = finalX * num; i < (finalX + 1) * num; i++) {

//                    System.out.println("测试bigger_" +i);

                        try {
//				TimeUnit.MICROSECONDS.sleep(100);
                            SetnxString setnxString = new SetnxString(remove, 7, ("测试" + i).getBytes("UTF-8"), (i + "").getBytes("UTF-8"));
                            setnxString.setPrintLog(false);
                            setnxString.execute();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }finally {
                    countDownLatch.countDown();
                }
            });

        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    @Test
    public void testSet(){
        try {
            byte[][] set = new byte[100000][];
            int index = 0;
            for (int i = 0; i < 100000; i++) {
                set[i] = ("测试set" +index).getBytes("UTF-8");
                index++;
            }
            new SAddSet(id,0,"set".getBytes("UTF-8"),set).execute();
        } catch (  Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @Test
    public void testHash(){
        try {
            Map<byte[], byte[]> hash = new HashMap<>();
            int index = 0;
            for (int i = 0; i < 100000; i++) {
                hash.put(("hash" + index).getBytes("UTF-8"), ("测试hash" + index).getBytes("UTF-8"));
                index++;
            }

            new HmSetHash(id, 0, "hash".getBytes("UTF-8"), hash).execute();
        } catch (  Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @Test
    public void testZset(){
        try {
            ScoredValue<byte[]>[] scoreValues = new ScoredValue[100000];
            int index = 0;
            for (int i = 0; i < 100000; i++) {
                scoreValues[i] = ScoredValue.fromNullable(index, ("测试zset" + index).getBytes("UTF-8"));
                index++;
            }


            new ZmAddSet(id, 0, "zset".getBytes("UTF-8"), scoreValues).execute();
        } catch (  Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
