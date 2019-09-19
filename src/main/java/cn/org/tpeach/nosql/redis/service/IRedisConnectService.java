package cn.org.tpeach.nosql.redis.service;

import java.util.Collection;

import cn.org.tpeach.nosql.bean.PageBean;
import cn.org.tpeach.nosql.enums.RedisType;
import cn.org.tpeach.nosql.redis.bean.RedisConnectInfo;
import cn.org.tpeach.nosql.redis.bean.RedisKeyInfo;
import io.lettuce.core.ScanCursor;

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

    public String readString(String id, int db, String key);

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

    Long getDbKeySize(String id, int db);

    Collection<String> getKeys(String id, int db);

    Collection<String> getKeys(String id, int db, String pattern);

    Long deleteKeys(String id, int db, String... keys);

    RedisKeyInfo addSingleKeyInfo(RedisKeyInfo keyInfo);

    RedisKeyInfo getRedisKeyInfo(String id, int db, String key, ScanCursor cursor,PageBean pageBean);

    String flushDb(String id, int db);

    /**
     * @param id
     * @param db
     * @param key
     * @param seconds
     * @return
     */
    Boolean expireKey(String id, int db, String key, int seconds);

    RedisKeyInfo updateKeyInfo(RedisKeyInfo newKeyInfo, RedisKeyInfo oldKeyInfo);

    RedisKeyInfo addRowKeyInfo(RedisKeyInfo keyInfo,boolean isLeftList);
    
    Long deleteRowKeyInfo(String id,int db,String key,String valueOrField,int index,RedisType type);

    Boolean remamenx(String id, int db,final String oldkey, final String newkey);
}
