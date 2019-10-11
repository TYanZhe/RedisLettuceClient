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
 * @date 2019-07-02 23:48
 * @since 1.0.0
 */
public class SetnxString extends JedisDbCommand<Boolean> {
	final static Logger logger = LoggerFactory.getLogger(SetnxString.class);
	private byte[] key;
	private byte[] value;

    /**
     * 命令: SETNX key value
	 * @param id
     * @param db
     * @param key
     * @param value
	 */
	public SetnxString(String id, int db, byte[] key, byte[] value) {
		super(id,db);
		this.key = key;
		this.value = value;
	}
	@Override
	public String sendCommand() {
		return "SETNX "+byteToStr(key) +" "+byteToStr(value) ;
	}
    /**
	 * 将key的值设为value，当且仅当key不存在。
	 * 若给定的key已经存在，则SETNX不做任何动作。
	 * SETNX是”SET if Not eXists”(如果不存在，则SET)的简写。
	 * 时间复杂度：O(1)
	 * @param redisLarkContext
     * @return 设置成功，返回1 true。设置失败，返回0 false。
	 */
	@Override
	public Boolean concreteCommand(RedisLarkContext<byte[], byte[]> redisLarkContext) {
		super.concreteCommand(redisLarkContext);
		final Boolean response = redisLarkContext.setnx(key, value);
		return response;

	}

	@Override
	public RedisVersion getSupportVersion() {
		return RedisVersion.REDIS_1_0;
	}
}
