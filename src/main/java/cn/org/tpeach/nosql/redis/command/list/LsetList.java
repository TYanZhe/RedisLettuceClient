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
	private byte[] key;
	private byte[] value;
	private int index;
	/**
	 * LSET key index value
	 * @param id
	 * @param db
	 */
	public LsetList(String id, int db,byte[] key,int index,byte[] value) {
		super(id, db);
		this.key = key;
		this.index = index;
		this.value = value;
	}



	@Override
	public String sendCommand() {
		return "LSET "+ byteToStr(key) +" "+ index + " "+byteToStr(value);
	}

	/**
	 * 将列表key下标为index的元素的值甚至为value。
	 * 时间复杂度：
	 * 对头元素或尾元素进行LSET操作，复杂度为O(1)。
	 * 其他情况下，为O(N)，N为列表的长度。
	 * @param redisLarkContext
	 * @return 操作成功返回ok，否则返回错误信息。
	 */
	@Override
	public String concreteCommand(RedisLarkContext<byte[], byte[]> redisLarkContext) {
		super.concreteCommand(redisLarkContext);
		String response = redisLarkContext.lset(key, index, value);
		return response;
	}


	@Override
	public RedisVersion getSupportVersion() {
		return RedisVersion.REDIS_1_0;
	}
}
