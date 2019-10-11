package cn.org.tpeach.nosql.redis.command.string;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tyz
 * @Title: SetnxString
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-03 00:00
 * @since 1.0.0
 */
public class SetexString extends JedisDbCommand<String> {
	final static Logger logger = LoggerFactory.getLogger(SetexString.class);
	private byte[] key;
	private int seconds;
	private byte[] value;

    /**
     * 命令: SETEX key seconds value
	 * @param id
     * @param db
     * @param key
	 * @param seconds
     * @param value
	 */
	public SetexString(String id, int db, byte[] key,int seconds, byte[] value) {
		super(id,db);
		this.key = key;
		this.seconds = seconds;
		this.value = value;
	}
	@Override
	public String sendCommand() {
		return "SETEX "+byteToStr(key) +" "+seconds + " "+ byteToStr(value);
	}
    /**
	 * 将值value关联到key，并将key的生存时间设为seconds(以秒为单位)。
	 * 如果key 已经存在，SETEX命令将覆写旧值。
	 * 时间复杂度：O(1)
	 * @param redisLarkContext
     * @return 设置成功时返回OK。 当seconds参数不合法时，返回一个错误。
	 */
	@Override
	public String concreteCommand(RedisLarkContext<byte[], byte[]> redisLarkContext) {
		super.concreteCommand(redisLarkContext);
		final String response = redisLarkContext.setex(key, seconds,value);
		return response;

	}

	@Override
	public RedisVersion getSupportVersion() {
		return RedisVersion.REDIS_1_0;
	}
}
