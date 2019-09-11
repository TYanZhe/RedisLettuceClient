package cn.org.tpeach.nosql.enums;

import cn.org.tpeach.nosql.tools.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author tyz
 * @Title: RedisVersion1
 * @ProjectName RedisLark
 * @Description: redis版本
 * @date 2019-06-23 17:42
 */
public enum RedisVersion {
	/**REDIS版本1.0.* */
	REDIS_1_0(10,"1.0"),
	/**REDIS版本1.2.* */
	REDIS_1_2(12,"1.2"),
	/**REDIS版本2.0.* */
	REDIS_2_0(20,"2.0"),
	/**REDIS版本2.2.* */
	REDIS_2_2(22,"2.2"),
	/**REDIS版本2.4.* */
	REDIS_2_4(24,"2.4"),
	/**REDIS版本2.6.* */
	REDIS_2_6(26,"2.6"),
	/**REDIS版本2.8.* */
	REDIS_2_8(28,"2.8"),
	/**REDIS版本3.0.* */
	REDIS_3_0(30,"3.0"),
	/**REDIS版本3.2.* */
	REDIS_3_2(32,"3.2"),
	/**更高版本 */
	REDIS_HIGH(32,"*"),
	/**未知版本 */
	UNKNOWN(0,"unknown");
	private int code;
	private String version;
	
	public String getVersion() {
		return version;
	}
	public int getCode() {
		return code;
	}
	RedisVersion(int code,String version){
		this.version = version;
		this.code = code;
	}

	/**
	 *
	 * @param version
	 * @return
	 */
	public static RedisVersion getRedisVersion(String version) {
		if(StringUtils.isNotEmpty(version)) {
			Double v = Double.valueOf(Arrays.stream(version.split("\\.")).limit(2).collect(Collectors.joining("")));
			if(v > RedisVersion.REDIS_HIGH.code){
				return RedisVersion.REDIS_HIGH;
			}else if (version.startsWith(RedisVersion.REDIS_3_2.getVersion())){
				return RedisVersion.REDIS_3_2;
			}else if (version.startsWith(RedisVersion.REDIS_3_0.getVersion())){
				return RedisVersion.REDIS_3_0;
			}else if (version.startsWith(RedisVersion.REDIS_2_8.getVersion())){
				return RedisVersion.REDIS_2_8;
			}else if (version.startsWith(RedisVersion.REDIS_2_6.getVersion())){
				return RedisVersion.REDIS_2_6;
			}else if (version.startsWith(RedisVersion.REDIS_2_4.getVersion())){
				return RedisVersion.REDIS_2_4;
			}else if (version.startsWith(RedisVersion.REDIS_2_2.getVersion())){
				return RedisVersion.REDIS_2_2;
			}else if (version.startsWith(RedisVersion.REDIS_2_0.getVersion())){
				return RedisVersion.REDIS_2_0;
			}else if (version.startsWith(RedisVersion.REDIS_1_0.getVersion())){
				return RedisVersion.REDIS_1_0;
			}
		}
		return RedisVersion.UNKNOWN;
		
	}
}
