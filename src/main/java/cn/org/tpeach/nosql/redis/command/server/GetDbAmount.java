package cn.org.tpeach.nosql.redis.command.server;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import cn.org.tpeach.nosql.tools.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author tyz
 * @Title: DbSize
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-02 13:57
 * @since 1.0.0
 */
public class GetDbAmount extends JedisCommand<Integer> {
    final static Logger logger = LoggerFactory.getLogger(GetDbAmount.class);

    public GetDbAmount(String id) {
        super(id);
    }

    @Override
    public Integer concreteCommand(RedisLarkContext redisLarkContext) {
        try{
            redisLarkContext.getDbAmount();
        }catch (Exception e){
//            for()
        }
        return redisLarkContext.getDbAmount();
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_2_0;
    }
}
