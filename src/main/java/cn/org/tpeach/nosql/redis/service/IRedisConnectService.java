package cn.org.tpeach.nosql.redis.service;

import cn.org.tpeach.nosql.bean.PageBean;
import cn.org.tpeach.nosql.enums.RedisType;
import cn.org.tpeach.nosql.redis.bean.RedisClientBo;
import cn.org.tpeach.nosql.redis.bean.RedisConnectInfo;
import cn.org.tpeach.nosql.redis.bean.RedisKeyInfo;
import cn.org.tpeach.nosql.redis.bean.SlowLogBo;
import cn.org.tpeach.nosql.redis.command.AbstractLarkRedisPubSubListener;
import io.lettuce.core.KeyScanCursor;
import io.lettuce.core.ScanCursor;

import java.util.List;
import java.util.Map;

/**
 * @author tyz
 * @Title: RedisConnectService
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-06-27 08:47
 * @since 1.0.0
 */
public interface IRedisConnectService {
	
	
	public boolean connectTest(RedisConnectInfo connectInfo) ;

    public byte[] readString(String id, int db, byte[] key);

    /**
     * 获取数据库数量
     *
     * @param id
     * @return
     */
//    public Integer getDbAmount(String id);

    /**
     * 获取数据库数量组装称 db+下标+(key数量) 如 db0(5000)
     *
     * @param id
     * @return
     */
    String[] getDbAmountAndSize(String id);
    Long getDbKeySize(String id, int db,boolean printLog);
    Long getDbKeySize(String id, int db);

    /**
     *
     * @param id
     * @param db
     * @param totalPattren
     * @return
     */
    KeyScanCursor<byte[]> getKeys(String id, int db ,boolean totalPattren);
    /**
     *
     * @param id
     * @param db
     * @param totalPattren 是否需要匹配的总数量 效率会有影响
     * @return
     */
    KeyScanCursor<byte[]> getKeys(String id, int db, String pattern,boolean totalPattren );

    /**
     * cursor 为空时从最开始匹配
     * @param id
     * @param db
     * @param pattern
     * @param cursor
     * @return
     */
    KeyScanCursor<byte[]> getKeysForCursor(String id, int db, String pattern,String cursor);


    Long deleteKeys(String id, int db, byte[]... keys);

    Long deleteKeys(String id, int db, String pattern,Integer totalCount);

    RedisKeyInfo addSingleKeyInfo(RedisKeyInfo keyInfo);

    RedisKeyInfo getRedisKeyInfo(String id, int db, byte[] key, ScanCursor cursor,String pattern,PageBean pageBean,RedisKeyInfo srcKeyInfo);

    String flushDb(String id, int db);

    /**
     * @param id
     * @param db
     * @param key
     * @param seconds
     * @return
     */
    Boolean expireKey(String id, int db, byte[] key, int seconds);

    RedisKeyInfo updateKeyInfo(RedisKeyInfo newKeyInfo, RedisKeyInfo oldKeyInfo);

    Long addRowKeyInfo(RedisKeyInfo keyInfo,boolean isLeftList);
    
    Integer deleteRowKeyInfo(String id, int db, byte[] key, RedisType type, List<RedisKeyInfo> keyInfoList);

    Boolean remamenx(String id, int db,final byte[] oldkey, final byte[] newkey);


    Map<String,String> getConnectInfo(String id,boolean isFresh,boolean printLog);


    Map<String,String> getConnectInfo(String id,boolean isFresh );

    List<RedisClientBo> clientList(String id);

    List<SlowLogBo>  slowlogGet(String id);

    List<RedisClientBo> clientList(String id, boolean printLog);

    List<SlowLogBo> slowlogGet(String id, boolean printLog);

    void addListener(String id,AbstractLarkRedisPubSubListener listener);

    void removeListener(String id,AbstractLarkRedisPubSubListener listener);

    Long publish(String id,byte[]  channel, byte[]  message);
    void psubscribe(String id,byte[] ... patterns);
    void  punsubscribe(String id,byte[] ... patterns);

    String ping(String id);

}
