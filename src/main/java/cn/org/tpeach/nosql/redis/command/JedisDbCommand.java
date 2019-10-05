package cn.org.tpeach.nosql.redis.command;

import cn.org.tpeach.nosql.redis.command.connection.SelectCommand;
import lombok.extern.slf4j.Slf4j;

/**
 * @author tyz
 * @Title: JedisCommand
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-06 20:36
 * @since 1.0.0
 */
@Slf4j
public abstract class JedisDbCommand<T> extends JedisCommand<T> {
	protected int db;
	public JedisDbCommand(String id,int db) {
		super(id);
		this.db = db;

	}

	@Override
	protected void excuteBefore(RedisLarkContext redisLarkContext) {
		if(redisLarkContext.getDb() == null || !redisLarkContext.getDb().equals(db)){
			new SelectCommand(id,db).execute();
		}
	}

	@Override
	public T concreteCommand(RedisLarkContext redisLarkContext) {



		return null;
	}
}
