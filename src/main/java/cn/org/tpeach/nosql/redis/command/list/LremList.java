package cn.org.tpeach.nosql.redis.command.list;

import cn.org.tpeach.nosql.enums.RedisVersion;
import cn.org.tpeach.nosql.redis.command.JedisDbCommand;
import cn.org.tpeach.nosql.redis.command.RedisLarkContext;

/**
 * @author tyz
 * @Title: LremList
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-09-21 10:39
 * @since 1.0.0
 */
public class LremList extends JedisDbCommand<Long>{
	private byte[] key;
	private byte[] value;
	private long count;

	/**
	 * LREM key count value
	 * @param id
	 * @param db
	 * @param key
	 * @param count
	 * @param value
	 */
	public LremList(String id, int db, byte[] key, long count, byte[] value) {
		super(id, db);
		this.key = key;
		this.count = count;
		this.value = value;
	}



	@Override
	public String sendCommand() {
		return "LREM "+ byteToStr(key) +" "+ count + " "+byteToStr(value);
	}

	/**
	 * 根据参数count的值，移除列表中与参数value相等的元素。
	 *
	 * count的值可以是以下几种：
	 * count > 0: 从表头开始向表尾搜索，移除与value相等的元素，数量为count。
	 * count < 0: 从表尾开始向表头搜索，移除与value相等的元素，数量为count的绝对值。
	 * count = 0: 移除表中所有与value相等的值。
	 * 时间复杂度：
	 * O(N)，N为列表的长度。
	 * @param redisLarkContext
	 * @return 被移除元素的数量。 因为不存在的key被视作空表(empty list)，所以当key不存在时，LREM命令总是返回0。
	 */
	@Override
	public Long concreteCommand(RedisLarkContext<byte[], byte[]> redisLarkContext) {
		super.concreteCommand(redisLarkContext);
		Long response = redisLarkContext.lrem(key, count, value);
		return response;
	}


	@Override
	public RedisVersion getSupportVersion() {
		return RedisVersion.REDIS_1_0;
	}
}
