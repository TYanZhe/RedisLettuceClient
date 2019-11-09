package cn.org.tpeach.nosql.document;

import cn.org.tpeach.nosql.tools.CollectionUtils;
import cn.org.tpeach.nosql.tools.GsonUtil;
import cn.org.tpeach.nosql.tools.StringUtils;
import lombok.Getter;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
class DataWrapper{
    @Getter
    private int level;
    @Getter
    private String key;
    @Getter
    private Object data;
    public static DataWrapper build(Object data,int level){
        DataWrapper dataWrapper = new DataWrapper();
        dataWrapper.data = data;
        dataWrapper.level = level;
        return dataWrapper;
    }
    public DataWrapper key(String key){
        this.key = key;
        return this;
    }
    public String getPrefix(){
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < level; i++) {
            sb.append("-");
        }
        sb.append(" ");
        return sb.toString();
    }
}
public class InterferfaceCondition {
    public  static  String[] types = {"java.lang.Integer",
            "java.lang.Double",
            "java.lang.Float",
            "java.lang.Long",
            "java.lang.Short",
            "int","double","long","short","float"};
    public InterferfaceCondition build(){
        Class<? > aClass = this.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field field : declaredFields) {
            try {
                field.setAccessible(true);
                ResponseBody annotation = field.getAnnotation(ResponseBody.class);
                if (annotation != null && StringUtils.isNotBlank(annotation.demo())) {
                    Object o = null;
                    o = field.get(this);
                    if (o == null) {
                        Class<?> type = field.getType();
                        if(type == String.class) {
                            field.set(this,annotation.demo());
                        }else if ("java.lang.Integer".equalsIgnoreCase(type.getName()) || "int".equalsIgnoreCase(type.getName())) {
                            field.set(this, Integer.valueOf(annotation.demo()));
                        } else if ("java.lang.Float".equalsIgnoreCase(type.getName()) || "float".equalsIgnoreCase(type.getName())) {
                            field.set(this, Float.valueOf(annotation.demo()));
                        } else if ("java.lang.Long".equalsIgnoreCase(type.getName()) || "long".equalsIgnoreCase(type.getName())) {
                            field.set(this, Long.valueOf(annotation.demo()));
                        } else if ("java.lang.Double".equalsIgnoreCase(type.getName()) || "double".equalsIgnoreCase(type.getName())) {
                            field.set(this, Double.valueOf(annotation.demo()));
                        } else if ("java.lang.Short".equalsIgnoreCase(type.getName()) || "short".equalsIgnoreCase(type.getName())) {
                            field.set(this, Short.valueOf(annotation.demo()));
                        }else if(type == Date.class){
                            field.set(this,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(annotation.demo()));
                        }
                    }

                }
                RequestCondition annotation1 = field.getAnnotation(RequestCondition.class);
                if (annotation1 != null && StringUtils.isNotBlank(annotation1.demo())) {
                    Object  o = field.get(this);
                    if (o == null) {
                        Class<?> type = field.getType();
                        if(type == String.class) {
                            field.set(this,annotation1.demo());
                        }else if ("java.lang.Integer".equalsIgnoreCase(type.getName()) || "int".equalsIgnoreCase(type.getName())) {
                            field.set(this, Integer.valueOf(annotation1.demo()));
                        } else if ("java.lang.Float".equalsIgnoreCase(type.getName()) || "float".equalsIgnoreCase(type.getName())) {
                            field.set(this, Float.valueOf(annotation1.demo()));
                        } else if ("java.lang.Long".equalsIgnoreCase(type.getName()) || "long".equalsIgnoreCase(type.getName())) {
                            field.set(this, Long.valueOf(annotation1.demo()));
                        } else if ("java.lang.Double".equalsIgnoreCase(type.getName()) || "double".equalsIgnoreCase(type.getName())) {
                            field.set(this, Double.valueOf(annotation1.demo()));
                        } else if ("java.lang.Short".equalsIgnoreCase(type.getName()) || "short".equalsIgnoreCase(type.getName())) {
                            field.set(this, Short.valueOf(annotation1.demo()));
                        }else if(type == Date.class){
                            field.set(this,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(annotation.demo()));
                        }
                    }


                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return this;

    }

    public static void create(String catalogPrefix,String name,InterfaceDesc interfaceDesc,InterferfaceCondition condition,RetMsg retMsg) throws IllegalAccessException {
        StringBuffer sb = new StringBuffer();
        String tableFormat = "| %s  | %s   | %s | %s | %s   |\n";
        sb.append("### "+catalogPrefix);
        sb.append(name);
        sb.append("\n\n");
        sb.append("#### "+catalogPrefix+".1 功能描述\n\n");
        if(interfaceDesc.getDesc() != null){
            sb.append(interfaceDesc.getDesc());
        }
        sb.append("\r\n\r\n");
        sb.append("#### "+catalogPrefix+".2 请求说明\n\n");
        sb.append("> 请求方式："+interfaceDesc.getRequestMethod()+"<br/>\n");
        sb.append(">\n");
        sb.append("> Content-Type : "+interfaceDesc.getContentType()+"<br/>\n");
        sb.append(">\n");
        sb.append("> 请求URL ："+interfaceDesc.getUrl()+"\n");
        sb.append("\n");
        sb.append("#### "+catalogPrefix+".3 请求参数\n\n");
        sb.append("| 参数名称  | 类型   | 出现要求 | 示例 | 描述     |\n");
        sb.append("| --------- | ------ | -------- | ---- | -------- |\n");
        if(condition == null) {
            sb.append(String.format(tableFormat, "", "", "", "", ""));
        }else{
            Field[] oFields = condition.getClass().getDeclaredFields( );
            boolean flag = true;
            for ( Field field : oFields ) {
                field.setAccessible(true);
                RequestCondition annotation = field.getAnnotation(RequestCondition.class);
                String require = "";
                String demo = "";
                String desc = "";
                if(annotation != null) {
                    require = annotation.require();
                    demo = annotation.demo();
                    desc = annotation.desc();
                    sb.append(String.format(tableFormat, field.getName(), getTypeName(field.getType()), require, demo, desc));
                    flag = false;
                }
            }
            if(flag){
                sb.append(String.format(tableFormat, "", "", "", "", ""));
            }
        }



        sb.append("\n#### "+catalogPrefix+".4 返回结果\n\n");
        sb.append("| 参数名称  | 类型   | 出现要求 | 示例 | 描述     |\n");
        sb.append("| --------- | ------ | -------- | ---- | -------- |\n");
        Class<? extends RetMsg> clazz = retMsg.getClass();
        Field[ ] fields = clazz.getDeclaredFields( );
        Object data = null;
        String dataString=null;
        for ( Field field : fields ){
            field.setAccessible( true );
            ResponseBody annotation = field.getAnnotation(ResponseBody.class);
            String require = "";
            String demo = "";
            String desc = "";
            if(annotation != null){
                require = annotation.require();
                demo = annotation.demo();
                desc = annotation.desc();
            }
            if("data".equalsIgnoreCase(field.getName())){
                try {
                    data = field.get(retMsg);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                dataString = String.format(tableFormat,field.getName(),getTypeName(field.getType()),require,demo,desc);
            }else{
                sb.append(String.format(tableFormat,field.getName(),getTypeName(field.getType()),require,demo,desc));
            }

        }
        if(data != null){

            LinkedList<DataWrapper> linkedList = new LinkedList();
            if(data instanceof Map){
                sb.append(dataString.replace("data","-data"));
                Map<String,Object> map = (Map) data;
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    Class c = null;
                    String demo = "";
                    if(entry.getValue() != null){
                        c = entry.getValue().getClass();
                        demo = getValueString(entry.getValue());
                    }
                    if(entry.getValue() instanceof Map || entry.getValue() instanceof Collection || entry.getValue() instanceof InterferfaceCondition){
                        linkedList.add(DataWrapper.build(entry.getValue(),2).key(entry.getKey()));
                    }else{
                        sb.append(String.format(tableFormat,entry.getKey(),getTypeName(c),"",demo,""));
                    }
                }
            }else if(data instanceof Collection){
                sb.append(dataString.replace("data","-data").replace("object","array"));
                Collection collection = (Collection) data;
                if(CollectionUtils.isNotEmpty(collection)){
                    Iterator iterator = collection.iterator();
                    while (iterator.hasNext()){
                        Object next = iterator.next();
                        if(next instanceof Map || next instanceof Collection || next instanceof InterferfaceCondition){
                            linkedList.add(DataWrapper.build(next,2).key(null));
                        }else{
                            sb.append(String.format(tableFormat,"",getTypeName(next.getClass()),"","",""));
                        }
                        break;
                    }
                }


            }else if(data instanceof InterferfaceCondition){
                sb.append(dataString.replace("data","-data"));
                InterferfaceCondition condition1 = (InterferfaceCondition) data;
                Class<?> oClass = condition1.getClass();
                Field[] oFields = oClass.getDeclaredFields( );
                for ( Field field : oFields ) {
                    field.setAccessible(true);
                    ResponseBody annotation = field.getAnnotation(ResponseBody.class);
                    String require = "";
                    String demo = "";
                    String desc = "";
                    if(annotation != null){
                        require = annotation.require();
                        demo = annotation.demo();
                        desc = annotation.desc();
                        sb.append(String.format(tableFormat,field.getName(),getTypeName(field.getType()),require,demo,desc));
                        if(Map.class.isAssignableFrom(field.getType()) || Collection.class.isAssignableFrom(field.getType()) || InterferfaceCondition.class.isAssignableFrom(field.getType())){
                            linkedList.add(DataWrapper.build(field.get(condition1),2).key(field.getName()));
                        }
                    }

                }
            }

            while (!linkedList.isEmpty()){
                DataWrapper dataWrapper = linkedList.removeFirst();
                Object o = dataWrapper.getData();
                if(o instanceof Map){
                    Map<String,Object> map = (Map) o;
                    if(dataWrapper.getKey() != null){
                        sb.append(String.format(tableFormat,dataWrapper.getPrefix()+dataWrapper.getKey(),getTypeName(Map.class),"","",""));
                    }

                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        Class c = null;
                        String demo = "";
                        if(entry.getValue() != null){
                            c = entry.getValue().getClass();
                            demo = entry.getValue().toString();
                        }
                        if(entry.getValue() instanceof Map || entry.getValue() instanceof Collection || entry.getValue() instanceof InterferfaceCondition){
                            linkedList.add(DataWrapper.build(entry.getValue(),dataWrapper.getLevel()+1).key(entry.getKey()));
                        }else{
                            sb.append(String.format(tableFormat,entry.getKey(),getTypeName(c),"",demo,""));
                        }

                    }
                }else if(o instanceof Collection){
                    Collection collection = (Collection) o;
                    if(dataWrapper.getKey() == null){
                        sb.append(String.format(tableFormat,dataWrapper.getPrefix()+"**ARRAY**",getTypeName(collection.getClass()),"","",""));
                    }else{
                        sb.append(String.format(tableFormat,dataWrapper.getPrefix()+dataWrapper.getKey(),getTypeName(collection.getClass()),"","",""));
                    }

                    if(CollectionUtils.isNotEmpty(collection)){
                        Iterator iterator = collection.iterator();
                        while (iterator.hasNext()){
                            Object next = iterator.next();
                            if(next instanceof Map || next instanceof Collection || next instanceof InterferfaceCondition){
                                linkedList.add(DataWrapper.build(next,dataWrapper.getLevel()+1).key(null));
                            }else{
                                sb.append(String.format(tableFormat,"",getTypeName(next.getClass()),"","",""));
                            }
                            break;
                        }
                    }
                }else if(o instanceof InterferfaceCondition){
                    Class<?> oClass = o.getClass();
                    Field[] oFields = oClass.getDeclaredFields( );
                    for ( Field field : oFields ) {
                        field.setAccessible(true);
                        ResponseBody annotation = field.getAnnotation(ResponseBody.class);
                        String require = "";
                        String demo = "";
                        String desc = "";
                        if(annotation != null){
                            require = annotation.require();
                            demo = annotation.demo();
                            desc = annotation.desc();
                            sb.append(String.format(tableFormat,field.getName(),getTypeName(field.getType()),require,demo,desc));
                            if(Map.class.isAssignableFrom(field.getType()) || Collection.class.isAssignableFrom(field.getType()) || InterferfaceCondition.class.isAssignableFrom(field.getType())){
                                linkedList.add(DataWrapper.build(field.get(o),dataWrapper.getLevel()+1).key(field.getName()));
                            }
                        }

                    }
                }
            }
        }else{
            sb.append(dataString);
        }

        sb.append("\n");
        sb.append("示例\n\n");
        sb.append("```json\n");
        sb.append(GsonUtil.gson2String(retMsg));
        sb.append("\n```\n");
        System.out.println(sb.toString());
    }

    private static String getTypeName(Class<?> type){
        if(type == null){
            return "";
        }
        if(type.isArray() || Collection.class.isAssignableFrom(type)){
            return "array";
        }else if(type == String.class){
            return "string";
        }
        for(String str : InterferfaceCondition.types) {
            if(str.equalsIgnoreCase(type.getName())){
                return "number";
            }
        }
        return "object";
    }

    private static String getValueString(Object o){
        if(o instanceof Date){
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(o);
        }else{
            return o.toString();
        }
    }
}
