package cn.org.tpeach.nosql.tools;

import java.util.UUID;

/**
 * 提取org.apache.commons.lang.StringUtils常用方法
 * 
 * @author taoyz
 *
 */
public class StringUtils {
	public static final String EMPTY = "";

	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	public static boolean isNotEmpty(String str) {
		return !StringUtils.isEmpty(str);
	}

	public static boolean isBlank(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if ((Character.isWhitespace(str.charAt(i)) == false)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNotBlank(String str) {
		return !StringUtils.isBlank(str);
	}
	/**
	 * 为空时使用默认
	 * 
	 * @param obj
	 * @param defaultStr 默认字符串
	 * @return
	 */
	public static String defaultToString(Object obj, String defaultStr) {
		return obj == null ? defaultStr : obj.toString();
	}
	/**
	 * obj为null时返回 null 不返回"null"字符串</br>
	 * 不为null时使用toString
	 * 
	 * @param obj
	 * @return String
	 */
	public static String defaultNullToString(Object obj) {
		return defaultToString(obj, null);
	}

	/**
	 * obj为null时返回 "" 不返回"null"字符串</br>
	 * 不为null时使用toString
	 * 
	 * @param obj
	 * @return StringIOUtil
	 */
	public static String defaultEmptyToString(Object obj) {
		return defaultToString(obj, EMPTY);
	}


	/**
	 *
	 * Description:
	 * <p>
	 * 对象转为字符串。去除左右空白。null返回""
	 * </p>
	 * 
	 * @param obj 对象
	 * @return 字符串
	 *
	 */
	public static String getTrimString(Object obj) {
		return defaultEmptyToString(obj).trim();
	}

	/**
	 * @param obj
	 * @description 把空字符转为null返回，主要用于数据表的插入
	 */
	public static String getEmptyStrToNull(Object obj) {
		if (obj == null || EMPTY.equals(defaultNullToString(obj))) {
			return null;
		}
		return obj.toString();
	}

	/**
	 * 判断字符串是否相等
	 * 
	 * @param cs1
	 * @param cs2
	 * @return {@code true} if the CharSequences are equal, case sensitive, or both
	 *         {@code null}
	 */
	public static boolean equals(CharSequence cs1, CharSequence cs2) {
		return cs1 == null ? cs2 == null : cs1.equals(cs2);
	}

	public static String getUUID(){
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
}