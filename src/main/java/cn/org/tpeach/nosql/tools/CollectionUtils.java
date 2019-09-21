package cn.org.tpeach.nosql.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

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

    /**
     * list去重
     * @param lists
     * @param <T>
     * @return
     */
    private <T> List<T> removeDuplicateList(List<T>... lists) {
        int resultCount = 0;
        if(lists != null && lists.length >0){
            for (List<T> list : lists) {
                if(CollectionUtils.isNotEmpty(list)){
                    resultCount+=list.size();
                }
            }
            if(resultCount == 0){
                return new ArrayList<>(0);
            }
            LinkedHashSet<T> set = new LinkedHashSet<>(resultCount);
            List<T> resultList = new ArrayList<>(resultCount);
            for (List<T> list : lists) {
                if(CollectionUtils.isNotEmpty(list)){
                    set.addAll(list);
                }
            }
            resultList.addAll(set);
            return resultList;
        }else{
            return new ArrayList<>(0);
        }
    }
}
