package cn.org.tpeach.nosql.redis.service;

import cn.org.tpeach.nosql.redis.command.ICommand;

public abstract class BaseRedisService {

    protected  <T> T executeJedisCommand(ICommand<T> command){
        return command.execute();
    }
}
