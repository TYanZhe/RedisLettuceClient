package cn.org.tpeach.nosql.redis.command;

import cn.org.tpeach.nosql.enums.RedisStructure;
import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.framework.BeanContext;
import cn.org.tpeach.nosql.framework.LarkFrame;
import cn.org.tpeach.nosql.redis.bean.RedisConnectInfo;
import cn.org.tpeach.nosql.redis.command.string.GetString;
import cn.org.tpeach.nosql.redis.connection.RedisLarkFactory;
import cn.org.tpeach.nosql.redis.connection.RedisLarkPool;
import cn.org.tpeach.nosql.view.RedisMainWindow;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

/**
 * @author tyz
 * @Title: JedisCommand
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-06-25 20:36
 * @since 1.0.0
 */
public abstract class JedisCommand<T> implements ICommand<T> {
    final static Logger logger = LoggerFactory.getLogger(JedisCommand.class);
    protected String id;
    @Getter
    @Setter
    private boolean printLog = true;
    public JedisCommand(String id) {
    	super();
        this.id = id;
    }

    @Override
    public T execute() {
        RedisLarkFactory conn = BeanContext.getBean("redisLarkFactory", RedisLarkFactory.class);
        final RedisLarkContext redisLarkContext = conn.connectRedis(id);
        final RedisVersion redisVersion = redisLarkContext.getRedisVersion();
        final RedisVersion supportVersion = getSupportVersion();
        if(redisVersion == null) {
        	throw new RuntimeException("获取版本号失败");
        }
        if(supportVersion == null) {
        	throw new RuntimeException("获取支持supportVersion版本号失败");
        }

        //redis版本小于支持的版本
        if(redisVersion.getCode() < supportVersion.getCode() || !extraSupportVersion(redisLarkContext)){
            throw new RuntimeException("不支持的版本");
        }
        RedisConnectInfo redisConnectInfo = RedisLarkPool.getConnectInfo(id);
        String command = sendCommand();
        excuteBefore(redisLarkContext);
        if(command != null && printLog){
            LarkFrame.larkLog.sendInfo(redisConnectInfo.getName(),"%s",sendCommand());
        }

        T t;
        try{
             t = concreteCommand(redisLarkContext);
        }catch (Exception e){
            LarkFrame.larkLog.error("",e);
            throw e;
        }
//        LarkFrame.larkLog.info("[Server %s] Response Received : %s ", Color.BLUE.darker(),redisConnectInfo.getName(),t);
        if(command != null && printLog){
            String response = null;
            if(t != null){
                response = t.toString();
                if(!isFullResponseLog() && response.length() > responseLogLength()){
                    response = "Bulk";
                }
            }


            LarkFrame.larkLog.receivedInfo(redisConnectInfo.getName(),"%s",response);
        }
        return t;
    }

    protected int responseLogLength(){
        return 30;
    }
    protected boolean isFullResponseLog(){
        return false;
    }
    /**
     * 执行之前的操作
     * @param redisLarkContext
     */
    protected  void excuteBefore(RedisLarkContext redisLarkContext){

    }

    public abstract String sendCommand();
    /**
     * 其他的版本要求 默认判断集群3.0
     * @param redisLarkContext
     * @return true 支持 false 不支持
     */
     public boolean extraSupportVersion(RedisLarkContext redisLarkContext){
        //集群需3.0以上
        final RedisVersion redisVersion = redisLarkContext.getRedisVersion();
        return redisLarkContext.getRedisStructure() != RedisStructure.CLUSTER || redisVersion.getCode() >= RedisVersion.REDIS_3_0.getCode();
    }
    /**
     * 实际执行的命令
     */
    public abstract T concreteCommand(RedisLarkContext redisLarkContext);

    public abstract RedisVersion getSupportVersion();

}
