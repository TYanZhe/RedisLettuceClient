package cn.org.tpeach.nosql.tools;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class MapUtils {


    public static <T> T mapToObject(Map<String, Object> map, Class<T> beanClass) throws Exception {
        if (isEmpty(map)) {
            return null;
        }


        T obj = beanClass.newInstance();

        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            int mod = field.getModifiers();
            if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                continue;
            }

            field.setAccessible(true);
            field.set(obj, map.get(field.getName()));
        }

        return obj;
    }

    public static Map<String, Object> objectToMap(Object obj) throws Exception {
        if (obj == null) {
            return null;
        }

        Map<String, Object> map = new HashMap<String, Object>();

        Field[] declaredFields = obj.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            map.put(field.getName(), field.get(obj));
        }

        return map;
    }

    /**
     * 判断map是否为空
     *
     * @param map map为空 true 不为空 false
     * @return boolean
     */
    public static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }

    /**
     * 判断map是否为空
     *
     * @param map map为空 false 不为空 true
     * @return
     */
    public static boolean isNotEmpty(Map map) {
        return !isEmpty(map);
    }
}
