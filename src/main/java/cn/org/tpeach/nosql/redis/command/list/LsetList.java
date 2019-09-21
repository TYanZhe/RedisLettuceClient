package cn.org.tpeach.nosql.redis.command.list;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;

/**
 * @author tyz
 * @Title: LsetList
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-09-06 10:39
 * @since 1.0.0
 */
public class LsetList  extends JedisDbCommand<String>{
	private String key;
	private String value;
	private int index;
	/**
	 * LSET key index value
	 * @param id
	 * @param db
	 */
	public LsetList(String id, int db,String key,int index,String value) {
		super(id, db);
		this.key = key;
		this.index = index;
		this.value = value;
	}



	@Override
	public String sendCommand() {
		return "LSET "+ key +" "+ index + " "+value;
	}

	@Override
	public String concreteCommand(RedisLarkContext redisLarkContext) {
		super.concreteCommand(redisLarkContext);
		String response = redisLarkContext.lset(key, index, value);
		return response;
	}


	@Override
	public RedisVersion getSupportVersion() {
		return RedisVersion.REDIS_1_0;
	}
}
