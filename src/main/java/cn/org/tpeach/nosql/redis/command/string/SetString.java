package cn.org.tpeach.nosql.redis.command.string;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tyz
 * @Title: SetString
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-06-25 23:48
 * @since 1.0.0
 */
public class SetString extends JedisDbCommand<String> {
	final static Logger logger = LoggerFactory.getLogger(SetString.class);
	private byte[] key;
	private byte[] value;

	/**
	 * 命令: SET key value
	 * @param id
	 * @param db
	 * @param key
	 * @param value
	 */
	public SetString(String id, int db, byte[] key, byte[] value) {
		super(id,db);
		this.key = key;
		this.value = value;
	}
	@Override
	public String sendCommand() {
		return "SET "+byteToStr(key) +" "+byteToStr(value) ;
	}
	/**
	 * 将字符串值value关联到key。
	 * 如果key已经持有其他值，SET就覆写旧值，无视类型。
	 * 时间复杂度：O(1)
	 * @param redisLarkContext
	 * @return 总是返回OK，因为SET不可能失败
	 */
	@Override
	public String concreteCommand(RedisLarkContext<byte[], byte[]> redisLarkContext) {
		super.concreteCommand(redisLarkContext);
		final String response = redisLarkContext.set(key, value);
		return response;

	}

	@Override
	public RedisVersion getSupportVersion() {
		return RedisVersion.REDIS_1_0;
	}
}
