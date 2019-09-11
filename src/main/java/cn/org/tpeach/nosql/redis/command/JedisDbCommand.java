package cn.org.tpeach.nosql.redis.command;

import cn.org.tpeach.nosql.redis.command.connection.SelectCommand;
import com.sun.org.apache.bcel.internal.generic.Select;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	public T concreteCommand(RedisLarkContext redisLarkContext) {

		new SelectCommand(id,db).execute();

		return null;
	}
}
