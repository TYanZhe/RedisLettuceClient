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
import io.lettuce.core.ScoredValue;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RedisTest {
    String id = "0e5f3c61-488e-4d3c-9df0-5f130416ac93";
    /**
     *
     */
    @Before
    public void connectSingle(){
        LarkFrame.run(ApplicationTest.class);
        //构建连接信息
        RedisConnectInfo conn = new RedisConnectInfo();
        conn.setId(id);
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
    @Test
    public void testList(){
        String[] list = new String[100000];
        int index = 0;
        for (int i = 0; i < 100000; i++) {
            list[i] = "测试" +index;
            index++;
            try {
//				TimeUnit.MICROSECONDS.sleep(100);
			} catch ( Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        new RpushList(id,0,"list",list).execute();
    }
    
    @Test
    public void testString(){
 
 
        new SelectCommand(id, 7).execute();
        for (int i = 0; i < 100000; i++) {
 
            System.out.println("测试" +i);
 
            try {
				TimeUnit.MICROSECONDS.sleep(100);
				new SetnxString(id, 7, "测试" +i, i+"").execute();
			} catch (  Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
    }
    @Test
    public void testSet(){
        String[] set = new String[100000];
        int index = 0;
        for (int i = 0; i < 100000; i++) {
            set[i] = "测试set" +index;
            index++;
        }
        new SAddSet(id,0,"set",set).execute();
    }
    @Test
    public void testHash(){
        Map<String, String> hash = new HashMap<>();
        int index = 0;
        for (int i = 0; i < 100000; i++) {
            hash.put("hash"+index,"测试hash" +index);
            index++;
        }

        new HmSetHash(id,0,"hash",hash).execute();
    }
    @Test
    public void testZset(){
        ScoredValue<String>[] scoreValues = new ScoredValue[100000];
        int index = 0;
        for (int i = 0; i < 100000; i++) {
            scoreValues[i] =  ScoredValue.fromNullable(index,"测试zset" +index);
            index++;
        }


        new ZmAddSet(id,0,"zset",scoreValues).execute();
    }
}
