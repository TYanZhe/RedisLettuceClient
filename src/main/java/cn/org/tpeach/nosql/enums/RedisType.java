/**
 * 
 */
package cn.org.tpeach.nosql.enums;

/**
　 * <p>Title: RedisType.java</p> 
　 * @author taoyz 
　 * @date 2019年7月1日 
　 * @version 1.0 
 */
public enum RedisType {
	/**字符串类型*/
	SERVER("server"),
	DATABASE("database"),
	KEY("key"),
	KEY_NAMESPACE("key_namespace"),
	STRING("string"),
	LIST("list"),
	SET("set"),
	ZSET("zset"),
	HASH("hash"),
	ROOT("root"),
	
	
	
	UNKNOWN("unknown");
	
	private String type;
	private RedisType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public static RedisType getRedisType(String type){
		if(RedisType.STRING.type.equals(type)){
			return RedisType.STRING;
		}else if(RedisType.LIST.type.equals(type)){
			return RedisType.LIST;
		}else if(RedisType.SET.type.equals(type)){
			return RedisType.SET;
		}else if(RedisType.HASH.type.equals(type)){
			return RedisType.HASH;
		}else if(RedisType.ZSET.type.equals(type)){
			return RedisType.ZSET;
		}else if(RedisType.ROOT.type.equals(type)){
			return RedisType.ROOT;
		}else if(RedisType.DATABASE.type.equals(type)){
			return RedisType.DATABASE;
		}else if(RedisType.SERVER.type.equals(type)){
			return RedisType.SERVER;
		}else if(RedisType.KEY.type.equals(type)){
			return RedisType.KEY;
		}
		return RedisType.UNKNOWN;
	}
}
