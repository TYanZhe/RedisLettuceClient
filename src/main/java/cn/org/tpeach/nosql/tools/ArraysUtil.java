package cn.org.tpeach.nosql.tools;

import java.util.Arrays;
import java.util.Collection;

import cn.org.tpeach.nosql.function.Each;

/**
 * @author tyz
 * @Title: ArraysUtil
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-06-25 21:14
 * @since 1.0.0
 */
public class ArraysUtil {

	public static <T> void each(T[] t, Each<T> each) {
		if (!ArraysUtil.isEmpty(t)) {
			for (int i = 0; i < t.length; i++) {
				each.foreach(i, t[i]);
			}
		}

	}

	/**
	 * 判断是否为空
	 * 
	 * @param array
	 * @return
	 */
	public static boolean isEmpty(Object[] array) {
		return array == null || array.length == 0;
	}

	public static boolean isArray(Object object) {
		if (object == null) {
			return false;
		}
		return object.getClass().isArray();
	}

	public static boolean isCollection(Object object) {
		return object instanceof Collection;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] objectToArray(Object obj) {
		if (obj == null) {
			return (T[]) new Object[0];
		}
		if (isArray(obj)) {
			return (T[]) obj;
		} else if (isCollection(obj)) {
			return (T[]) ((Collection<T>) obj).toArray();
		}
		return (T[]) new Object[] { obj };
	}

	public static <T> boolean contains(T[] array, Object obj) {
		return Arrays.asList(array).contains(obj);
	}
}
