package cn.org.tpeach.nosql.redis.command;

/**
 * @author tyz
 * @Title: ICommand
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-06-23 19:45
 * @since 1.0.0
 */
public interface ICommand<T> {
    T execute();
}