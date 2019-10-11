package cn.org.tpeach.nosql.redis.command.string;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tyz
 * @Title: GetString
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-06-25 23:48
 * @since 1.0.0
 */
public class GetSetString extends JedisDbCommand<byte[]> {
	final static Logger logger = LoggerFactory.getLogger(GetSetString.class);
	private byte[] key;
	private byte[] value;
	/**
	 * 命令: GETSET key value
	 * @param id
	 * @param db
	 * @param key
	 * @param value
	 */
	public GetSetString(String id, int db, byte[] key,byte[] value) {
		super(id,db);
		this.key = key;
		this.value = value;
	}

	@Override
	public String sendCommand() {
		return "GETSET "+byteToStr(key) +" "+byteToStr(value);
	}

	/**
	 * 将给定key的值设为value，并返回key的旧值。
	 * 当key存在但不是字符串类型时，返回一个错误。
	 * 时间复杂度：O(1)
	 * @param redisLarkContext
	 * @return 返回给定key的旧值(old value)。当key没有旧值时，返回nil。
	 */
	@Override
	public byte[] concreteCommand(RedisLarkContext<byte[], byte[]> redisLarkContext) {
		super.concreteCommand(redisLarkContext);
		final byte[] response = redisLarkContext.getSet(key,value);
		return response;
	}

	@Override
	public RedisVersion getSupportVersion() {
		return RedisVersion.REDIS_1_0;
	}
}
