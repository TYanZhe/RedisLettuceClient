package cn.org.tpeach.nosql.tools;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GsonUtil {


    private static Gson gson = null;

    static {
        if (gson == null) {
            //属性为空的时候输出来的json字符串是有键值key,显示形式是"key":null
            gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").setPrettyPrinting().create();
        }
    }


    private GsonUtil() {
    }


    /**
     * 将object对象转成json字符串
     *
     * @param object
     * @return
     */
    public static String gson2String(Object object) {
        String jsonString = null;
        if (gson != null) {
            jsonString = gson.toJson(object);
        }
        return jsonString;
    }


    /**
     * 将json字符串转成泛型bean
     *
     * @param jsonString
     * @param cls
     * @return
     */
    public static <T> T GsonToBean(String jsonString, Class<T> cls) {
        T t = null;
        if (gson != null) {
            t = gson.fromJson(jsonString, cls);
        }
        return t;
    }


    /**
     * 转成list
     * 泛型在编译期类型被擦除导致报错
     * @param gsonString
     * @param cls
     * @return
     */
//    public static <T> List<T> GsonToList(String gsonString, Class<T> cls) {
//        List<T> list = null;
//        if (gson != null) {
    //根据泛型返回解析指定的类型,TypeToken<List<T>>{}.getType()获取返回类型
//            list = gson.fromJson(gsonString, new TypeToken<List<T>>() {
//            }.getType());
//        }
//        return list;
//    }

    /**
     * 转成list
     * 解决泛型在编译期类型被擦除导致报错
     *
     * @param json
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> List<T> jsonToList(String json, Class<T> cls) {
        Gson gson = new Gson();
        List<T> list = new ArrayList<T>();
        JsonArray array = new JsonParser().parse(json).getAsJsonArray();
        for (final JsonElement elem : array) {
            list.add(gson.fromJson(elem, cls));
        }
        return list;
    }


    /**
     * 转成list中有map的
     *
     * @param gsonString
     * @return
     */
    public static <T> List<Map<String, T>> gsonToListMaps(String gsonString) {
        List<Map<String, T>> list = null;
        if (gson != null) {
            list = gson.fromJson(gsonString, new TypeToken<List<Map<String, T>>>() {}.getType());
        }
        return list;
    }


    /**
     * 转成map的
     *
     * @param gsonString
     * @return
     */
    public static <T> Map<String, T> gsonToMaps(String gsonString) {
        Map<String, T> map = null;
        if (gson != null) {
            map = gson.fromJson(gsonString, new TypeToken<Map<String, T>>() {
            }.getType());
        }
        return map;
    }

    /**
     * 格式化输出JSON字符串
     * @return 格式化后的JSON字符串
     */
    public static String toPrettyFormat(String json) {
        try{
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
            return gson.toJson(jsonObject);
        }catch (Exception e){
            return json;
        }
    }

}