package cn.org.tpeach.nosql.tools;

import java.util.Collection;

/**
 * @author tyz
 * @Title: CollectionUtils
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-06-24 1:38
 * @since 1.0.0
 */
public class CollectionUtils {

    public static boolean isEmpty(Collection coll) {
        return coll == null || coll.isEmpty();
    }
    public static boolean isNotEmpty(Collection coll) {
        return !isEmpty(coll);
    }


    public static boolean isEmptyArray(Object[] array){
    	 return array == null || array.length < 1;
    }
}
