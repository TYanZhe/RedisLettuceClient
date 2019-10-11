package cn.org.tpeach.nosql.tools;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.org.tpeach.nosql.constant.ConfigConstant;
import cn.org.tpeach.nosql.constant.PublicConstant;
import lombok.extern.slf4j.Slf4j;

import static java.util.regex.Pattern.compile;

import java.io.UnsupportedEncodingException;

/**
 * 提取org.apache.commons.lang.StringUtils常用方法
 * 
 * @author taoyz
 *
 */
@Slf4j
public class StringUtils {
	public static final String EMPTY = "";
	public static final String BLANK = " ";
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

	/**
	 *  判断是文本还是字符串
	 * 1、如果字符串含有空字符（‘\0’），则认为是二进制格式
	 * 2、文本的合法字符为ascii码从32到126的字符，加上'\n','\r','\t','\b'
	 * 3、超过30%的字符串高位时1（ascii大于126）或其它奇怪字符，则认为是二进制格式
	 * @param s
	 * @return
	 */
	public static boolean isText(String s){
		if(StringUtils.isNotBlank(s)){
			char[] chars = s.toCharArray();
			int textCount = 0;
			int length = chars.length;
			for (char c : chars) {
				Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
				if(0 == c){
					return false;
				}else if((c >= 32 && c <= 126) || (c==8 || c==9 || c==10 || c==13) || (c >= 0x4E00 &&  c <= 0x9FA5)
						|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
						|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
						|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
						|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
						|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
						|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS){
					textCount++;
				}
			}
			int percent = textCount / length;
			if(percent < 0.7){
				return false;
			}

		}
		return true;
	}
	public static boolean isText(byte[] bytes){
		return isText(byteToStr(bytes));
	}
	public static String showHexStringValue(String value) {
		char[] chars = value.toCharArray();
		StringBuffer sb = new StringBuffer();
		for (char c : chars) {
			Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
			if((c >= 32 && c <= 126) || (c==8 || c==9 || c==10 || c==13) || (c >= 0x4E00 &&  c <= 0x9FA5)
					|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
					|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
					|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
					|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
					|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
					|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS){
				sb.append(c);
			}else{
				int intNum = c;
				String s = "\\x" + StringUtils.intToHexStringSmall(intNum);
				sb.append(s);
			}
		}
		return sb.toString();
	}

	public static String showHexStringValue(byte[] value) {
		return showHexStringValue(byteToStr(value));
	}
	/**
	 * byte——>hexString
	 *
	 * @param bytes
	 * @param isBlankSpace 是否按空格分开
	 * @return
	 */
	public static String bytesToHexString(byte[] bytes, boolean isBlankSpace) {
		if (bytes == null || bytes.length <= 0) {
			return null;
		}
		StringBuffer stringBuffer = new StringBuffer();
		boolean flag = false;
		for (byte b : bytes) {
			int v = b & 0xFF;
			String hex = Integer.toHexString(v);
			if (flag && isBlankSpace) {
				stringBuffer.append(BLANK);
			} else {
				flag = true;
			}
			if (v < 0x10) {
				stringBuffer.append('0' + hex);
			} else {
				stringBuffer.append(hex);
			}

		}
		return stringBuffer.toString().toUpperCase();
	}
	/**
	 * int——>hexString
	 *
	 * @return
	 */
	public static String intToHexString(int b) {
		if(b >= 0x00 && b <= 0xFF){
			return numToHex8(b);
		}else if(b > 0xFF && b <= 0xFFFF){
			return numToHex16( b);
		}else {
			return numToHex32( b);
		}

	}
	/**
	 * 去除前面多余的零
	 * @param b
	 * @return
	 */
	public static String intToHexStringSmall(int b) {
		String hexString = intToHexString(b);
		Pattern pattern = compile("0*(\\S*)");
		Matcher matcher = pattern.matcher(hexString);
		if(matcher.find()){
			String group = matcher.group(1);
			return group.length() % 2 == 0 ? group : "0"+group;
		}
		return hexString;
	}
	//使用1字节就可以表示b
	public static String numToHex8(int b) {
		return String.format("%02x", b).toUpperCase();
	}
	//需要使用2字节表示b
	public static String numToHex16(int b) {
		return String.format("%04x", b).toUpperCase();
	}
	//需要使用4字节表示b
	public static String numToHex32(int b) {
		return String.format("%08x", b).toUpperCase();
	}

	public static String repeat(String origin,int repeatCount){
		if(origin == null){
			return null;
		}else if(EMPTY.equals(origin) || repeatCount == 1){
			return origin;
		}
		if(repeatCount<=0){
			return EMPTY;
		}

		StringBuilder buf = new StringBuilder();
		for(int i=0;i<repeatCount;i++){
			buf.append(origin);
		}
		return buf.toString();
	}
	public static String repeat(char ch, int repeat) {
		char[] buf = new char[repeat];
		for (int i = repeat - 1; i >= 0; i--) {
			buf[i] = ch;
		}
		return new String(buf);
	}
	
	
    public static String byteToStr(byte[] b){
        String character = ConfigParser.getInstance().getString(ConfigConstant.Section.CHARACTER_ENCODING, ConfigConstant.CHARACTER, PublicConstant.CharacterEncoding.UTF_8);
        try {
            return new String(b,character);
        } catch (UnsupportedEncodingException e) {
        	log.error("不支持的编码格式",e);
        }
        return null;
    }

    public static byte[] strToByte(String s){
        if(StringUtils.isNotBlank(s)){
            String character = ConfigParser.getInstance().getString(ConfigConstant.Section.CHARACTER_ENCODING, ConfigConstant.CHARACTER, PublicConstant.CharacterEncoding.UTF_8);
            try {
                return s.getBytes(character);
            } catch (UnsupportedEncodingException e) {
                log.error("不支持的编码格式",e);
            }
        }
        return null;
    }
    
    public static byte[][] strArrToByte(String[] arr){
    	if(!ArraysUtil.isEmpty(arr)) {
    		byte[][] bytes = new byte[arr.length][];
            for (int i = 0; i < arr.length; i++) {
                bytes[i] = strToByte(arr[i]);
            }
            return bytes;
    	}
    	return null;
    }
    public static String[] byteArrToStr(byte[][] arr){
        if(!ArraysUtil.isEmpty(arr)) {
            String[] s = new String[arr.length];
            for (int i = 0; i < arr.length; i++) {
                s[i] = byteToStr(arr[i]);
            }
            return s;
        }
        return null;
    }
}