package cn.org.tpeach.nosql.redis.command.server;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.bean.SlowLogBo;
import cn.org.tpeach.nosql.redis.command.JedisCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;

import java.util.List;

public class SlowlogGetCommand extends JedisCommand<List<SlowLogBo>> {

    private Integer count;

    public SlowlogGetCommand(String id) {
        super(id);
    }
    public SlowlogGetCommand(String id,Integer count) {
        super(id);
        this.count = count;
    }
    @Override
    public String sendCommand() {
        if(count == null){
            return "SLOWLOG GET";
        }else{
            return "SLOWLOG GET "+count;
        }

    }

    @Override
    public List<SlowLogBo> concreteCommand(RedisLarkContext<byte[], byte[]> redisLarkContext) {
        return redisLarkContext.slowlogGet(count);
    }

    @Override
    public RedisVersion getSupportVersion() {
        return RedisVersion.REDIS_2_2;
    }
}
