package cn.org.tpeach.nosql.enums;

import cn.org.tpeach.nosql.redis.bean.RedisConnectInfo;
import cn.org.tpeach.nosql.tools.ArraysUtil;
import cn.org.tpeach.nosql.tools.ReflectUtil;
import cn.org.tpeach.nosql.tools.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: RedisStructure.java</p> 
 * @author taoyz 
 * @date 2019年6月24日 
 * @version 1.0 
*/
public enum RedisStructure {
	//	SINGLE(0, "cn.org.tpeach.nosql.redis.connection.impl.RedisLarkSingle","java.lang.String,java.lang.String,int,java.lang.String","id,host,port,auth"),
//	CLUSTER(1, "cn.org.tpeach.nosql.redis.connection.impl.RedisLarkCluster","java.lang.String,java.lang.String,java.lang.String","id,host,auth"),
	SINGLE(0, "cn.org.tpeach.nosql.redis.connection.impl.RedisLarkLettuce","java.lang.String,java.lang.String,int,java.lang.String","id,host,port,auth"),
	CLUSTER(1, "cn.org.tpeach.nosql.redis.connection.impl.RedisLarkLettuce","java.lang.String,java.lang.String,java.lang.String","id,host,auth"),
	UNKNOW(-1, "unknow","","");
	private int code;
	private String service;
	private String parameterTypes;
	private String redisConnectInfoField;
	
	private static Map<String,Class<?>> baseType = new HashMap<>();
	
	static {
		baseType.put("byte",Byte.TYPE);
		baseType.put("short",Short.TYPE);
		baseType.put("int",Integer.TYPE);
		baseType.put("long",Long.TYPE);
		baseType.put("float",Float.TYPE);
		baseType.put("double",Double.TYPE);
	}
	
	private RedisStructure(int code, String service,String parameterTypes,String redisConnectInfoField) {
		this.code = code;
		this.service = service;
		this.parameterTypes = parameterTypes;
		this.redisConnectInfoField = redisConnectInfoField;
	}



	public int getCode() {
		return code;
	}


	public String getService() {
		return service;
	}
	

	public static RedisStructure getRedisStructure(int code) {
		if (code == RedisStructure.SINGLE.code) {
			return RedisStructure.SINGLE;
		} else if (code == RedisStructure.CLUSTER.code) {
			return RedisStructure.CLUSTER;
		}
		return RedisStructure.UNKNOW;
	}
	
	public Class<?>[] getParameterTypes(){
		if(StringUtils.isBlank(this.parameterTypes)) {
			return null;
		}
		String[] split = this.parameterTypes.split(",");
		Class<?>[] list = new Class<?>[split.length];
		ArraysUtil.each(split, (index,item)->{
			if(baseType.containsKey((item))) {
				list[index] =baseType.get(item);
				return;
			}
			try {
				list[index] = Class.forName(item);
			} catch (ClassNotFoundException e) {
				System.out.println("RedisStructure:"+service+"配置不正确");
				e.printStackTrace();
			}
			
		});
		return list;
	}
	
	public Object[] getInitargs(RedisConnectInfo conn){
		if(StringUtils.isBlank(this.redisConnectInfoField)) {
			return null;
		}
		String[] split = this.redisConnectInfoField.split(",");
		Object[] obj = new Object[split.length];
		Class<RedisConnectInfo> c = RedisConnectInfo.class;
		ArraysUtil.each(split, (index,item)->{
			if(StringUtils.isBlank(item)) {
				obj[index] = null;
			}else {
				obj[index] = ReflectUtil.getValue(item, conn);
			}
			
		});
		return obj;
	}
}
