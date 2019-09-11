package cn.org.tpeach.nosql.tools;

import cn.org.tpeach.nosql.redis.bean.RedisConnectInfo;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;

/**
 * @author tyz
 * @Title: ReflectUtil
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-06-25 21:56
 * @since 1.0.0
 */
public class ReflectUtil {
    /**
     * Map with primitive wrapper type as key and corresponding primitive
     * type as value, for example: Integer.class -> int.class.
     */
    private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new IdentityHashMap<Class<?>, Class<?>>(8);

    /**
     * Map with primitive type as key and corresponding wrapper
     * type as value, for example: int.class -> Integer.class.
     */
    private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap = new IdentityHashMap<Class<?>, Class<?>>(8);

    static {
        primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
        primitiveWrapperTypeMap.put(Byte.class, byte.class);
        primitiveWrapperTypeMap.put(Character.class, char.class);
        primitiveWrapperTypeMap.put(Double.class, double.class);
        primitiveWrapperTypeMap.put(Float.class, float.class);
        primitiveWrapperTypeMap.put(Integer.class, int.class);
        primitiveWrapperTypeMap.put(Long.class, long.class);
        primitiveWrapperTypeMap.put(Short.class, short.class);

        for (Map.Entry<Class<?>, Class<?>> entry : primitiveWrapperTypeMap.entrySet()) {
            primitiveTypeToWrapperMap.put(entry.getValue(), entry.getKey());
        }
    }

    /**
     * 拼接某属性的 get方法 不支持boolean
     *
     * @param fieldName
     * @return String
     */
    private static String parName(String fieldName, String prefix) {
        if (null == fieldName || "".equals(fieldName)) {
            return null;
        }
        int startIndex = 0;
        //第二个单词大写时,第一个单词不变
        String first = null;
        if (fieldName.length() > 1 && Character.isUpperCase(fieldName.charAt(1))) {
            first = fieldName.substring(startIndex, startIndex + 1);
        } else {
            first = fieldName.substring(startIndex, startIndex + 1).toUpperCase();
        }

        return "get" + first + fieldName.substring(startIndex + 1);
    }

    public static String parGetName(String fieldName) {
        return parName(fieldName, "get");
    }

    public static String parSetName(String fieldName) {
        return parName(fieldName, "set");
    }

    public static Object getValue(String field, Object obj) {
        String methodName = parGetName(field);
        try {
            Class<?> c = obj.getClass();
            Method method = c.getMethod(methodName);
            return method.invoke(obj);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void copyProperties(Object source, Object target) {
        //需要设置的数据
        Class<?> actualEditable = target.getClass();
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(actualEditable);
            //        PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
            PropertyDescriptor[] targetPds = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor targetPd : targetPds) {
                Method writeMethod = targetPd.getWriteMethod();
                if (writeMethod != null) {
                    PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
                    if (sourcePd != null) {
                        Method readMethod = sourcePd.getReadMethod();
                        if (readMethod != null && isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())) {
                            try {
                                if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                                    readMethod.setAccessible(true);
                                }
                                Object value = readMethod.invoke(source);
                                if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                    writeMethod.setAccessible(true);
                                }
                                writeMethod.invoke(target, value);
                            } catch (Throwable ex) {
                                throw new RuntimeException(
                                        "Could not copy property '" + targetPd.getName() + "' from source to target", ex);
                            }
                        }
                    }
                }
            }
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }

    }

    public static boolean isAssignable(Class<?> lhsType, Class<?> rhsType) {
        if (lhsType.isAssignableFrom(rhsType)) {
            return true;
        }
        if (lhsType.isPrimitive()) {
            Class<?> resolvedPrimitive = primitiveWrapperTypeMap.get(rhsType);
            if (lhsType == resolvedPrimitive) {
                return true;
            }
        } else {
            Class<?> resolvedWrapper = primitiveTypeToWrapperMap.get(rhsType);
            if (resolvedWrapper != null && lhsType.isAssignableFrom(resolvedWrapper)) {
                return true;
            }
        }
        return false;
    }

    private static PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();

        List<PropertyDescriptor> list = new ArrayList<>(fields.length);
        for (int i = 0; i < fields.length; i++) {
            try {
                list.add(new PropertyDescriptor(fields[i].getName(), clazz));
            } catch (IntrospectionException e) {
//                e.printStackTrace();
            }
        }
        PropertyDescriptor[] prop = new PropertyDescriptor[list.size()];
        return list.toArray(prop);
    }

    private static PropertyDescriptor getPropertyDescriptor(Class<?> clazz, String propertyName) {
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            if (propertyName.equals(fields[i].getName())) {
                try {
                    return new PropertyDescriptor(fields[i].getName(), clazz);
                } catch (IntrospectionException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    // 设置bean的某个属性值
    public static void setProperty(Object obj, String fieldName, String value) throws Exception {
        // 获取bean的某个属性的描述符
        PropertyDescriptor propDesc = new PropertyDescriptor(fieldName, obj.getClass());
        // 获得用于写入属性值的方法
        Method methodSetUserName = propDesc.getWriteMethod();
        // 写入属性值
        methodSetUserName.invoke(obj, value);

    }

    // 获取bean的某个属性值
    public static Object getProperty(Object obj, String fieldName) throws Exception {
        // 获取Bean的某个属性的描述符
        PropertyDescriptor proDescriptor = new PropertyDescriptor(fieldName, obj.getClass());
        // 获得用于读取属性值的方法
        Method methodGetUserName = proDescriptor.getReadMethod();
        // 读取属性值
        Object value = methodGetUserName.invoke(obj);
        return value;
    }

    public static <T> T mapToObject(Map<String, Object> map, Class<T> clazz) throws Exception {
        if (MapUtils.isEmpty(map)){
            return null;
        }

        T obj = clazz.newInstance();
        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : propertyDescriptors) {
            Method setter = property.getWriteMethod();
            if (setter != null) {
                setter.invoke(obj, map.get(property.getName()));
            }
        }

        return obj;
    }

    public static Map<String, Object> objectToMap(Object obj) throws Exception {
        if (obj == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : propertyDescriptors) {
            String key = property.getName();
            if (key.compareToIgnoreCase("class") == 0) {
                continue;
            }
            Method getter = property.getReadMethod();
            Object value = getter != null ? getter.invoke(obj) : null;
            map.put(key, value);
        }

        return map;
    }

    public static void main(String[] args) {
        RedisConnectInfo source = new RedisConnectInfo();

        source.setHost("1213,m24324");
        source.setId(StringUtils.getUUID());
        RedisConnectInfo targe = new RedisConnectInfo();
        System.out.println(source);
        System.out.println(targe);
        System.out.println(">>>>>>>>>>>>>>>>>");
        targe.setHost("127.0.0.1");
        ReflectUtil.copyProperties(source, targe);
        targe.setId("242423432");
        System.out.println(source);
        System.out.println(targe);
    }

}
