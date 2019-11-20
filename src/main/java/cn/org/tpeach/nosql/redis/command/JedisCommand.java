package cn.org.tpeach.nosql.redis.command;

import cn.org.tpeach.nosql.enums.RedisStructure;
import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.framework.BeanContext;
import cn.org.tpeach.nosql.framework.LarkFrame;
import cn.org.tpeach.nosql.redis.bean.RedisConnectInfo;
import cn.org.tpeach.nosql.redis.connection.RedisLarkFactory;
import cn.org.tpeach.nosql.redis.connection.RedisLarkPool;
import cn.org.tpeach.nosql.tools.StringUtils;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Collection;

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
    private boolean printLog = true;
    public JedisCommand(String id) {
    	super();
        this.id = id;
    }

    public JedisCommand<T> setPrintLog(boolean printLog) {
        this.printLog = printLog;
        return this;
    }

    @Override
    public T execute() {
        RedisLarkFactory conn = BeanContext.getBean("redisLarkFactory", RedisLarkFactory.class);
        @SuppressWarnings("unchecked")
		final RedisLarkContext<byte[],byte[]> redisLarkContext = conn.connectRedis(id);
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
        LocalDateTime now = LocalDateTime.now();
        T t;
        try{
             t = concreteCommand(redisLarkContext);
        }catch (Exception e){
            LarkFrame.larkLog.receivedError(redisConnectInfo.getName(),"",e);
            throw e;
        }
//        LarkFrame.larkLog.info("[Server %s] Response Received : %s ", Color.BLUE.darker(),redisConnectInfo.getName(),t);
        if(command != null && printLog){
            String response = null;
            if(t != null){
                if(t instanceof byte[]){
                    response = StringUtils.showHexStringValue((byte[]) t);
                }else if( Collection.class.isAssignableFrom(t.getClass())){
                    response = "Array";
                }else{
                    response = t.toString();
                }

                if(!isFullResponseLog() && response.length() > responseLogLength()){
                    response = "Bulk";
                }
            }

            LarkFrame.larkLog.sendInfo(now,redisConnectInfo.getName(),"%s",sendCommand());
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
    protected  void excuteBefore(RedisLarkContext<byte[],byte[]> redisLarkContext){

    }

    public abstract String sendCommand();
    /**
     * 其他的版本要求 默认判断集群3.0
     * @param redisLarkContext
     * @return true 支持 false 不支持
     */
     public boolean extraSupportVersion(RedisLarkContext<byte[],byte[]> redisLarkContext){
        //集群需3.0以上
        final RedisVersion redisVersion = redisLarkContext.getRedisVersion();
        return redisLarkContext.getRedisStructure() != RedisStructure.CLUSTER || redisVersion.getCode() >= RedisVersion.REDIS_3_0.getCode();
    }
    /**
     * 实际执行的命令
     */
    public abstract T concreteCommand(RedisLarkContext<byte[],byte[]> redisLarkContext);

    public abstract RedisVersion getSupportVersion();

    public  String byteToStr(byte[] b){
        return StringUtils.showHexStringValue(b);
    }

    public  byte[] strToByte(String s){
    	return StringUtils.strToByte(s);
    }
    
    public  byte[][] strArrToByte(String[] arr){
    	return StringUtils.strArrToByte(arr);
    }
    public  String[] byteArrToStr(byte[][] arr){
    	return StringUtils.byteArrToStr(arr);
    }
}
