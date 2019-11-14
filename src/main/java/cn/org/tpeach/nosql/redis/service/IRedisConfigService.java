package cn.org.tpeach.nosql.redis.service;

import cn.org.tpeach.nosql.redis.bean.RedisConnectInfo;

import java.util.List;

/**
 * @author tyz
 * @Title: RedisConfigService
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-06-27 08:47
 * @since 1.0.0
 */
public interface IRedisConfigService {

    /**
     * 获取所有配置文件
     *
     * @return : List<RedisConnectInfo>
     * @author : taoyz
     * @date : 2019/6/27 12:49
     */
    List<RedisConnectInfo> getRedisConfigAllList();

    RedisConnectInfo addRedisConfig(RedisConnectInfo conn);

    RedisConnectInfo deleteRedisConfigById(String id);
    
    RedisConnectInfo getRedisConfigById(String id);

    RedisConnectInfo updateRedisConfig(RedisConnectInfo conn);

    void resetConfig();

    void moveRedisconfig(RedisConnectInfo conn,RedisConnectInfo targetConn);

    Integer getIndex(RedisConnectInfo conn);
    Integer getIndexById(String id);
}
