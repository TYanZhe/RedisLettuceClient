package cn.org.tpeach.nosql.document;


import cn.org.tpeach.nosql.tools.StringUtils;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * 响应数据
 */
@Data
public class RetMsg {

    /**
     * 响应业务状态
     */
    @ResponseBody(desc = "状态码",demo = "200")
    private int ret;

    /**
     * 响应消息
     */
    @ResponseBody(desc = "信息",demo = "success")
    private String msg;
    /**
     * 堆栈信息
     */
    @ResponseBody(desc = "堆栈信息")
    private String stack;

    /**
     * 响应中的数据
     */
    @ResponseBody(desc = "响应数据" )
    private Object data;

    private RetMsg(int ret, String msg, Object data) {
        this.ret = ret;
        this.msg = msg;
        this.data = data;
    }

    private static RetMsg buildMsg(int ret, String msg, Object data){
        return new RetMsg(ret, msg, data);
    }

    public static RetMsg buildSucess(String msg, Object data){
        if(StringUtils.isBlank(msg)){
            msg = "SUCESS";
        }
        return buildMsg(200,msg,data);
    }
    public static RetMsg buildSucess(){
        return buildSucess(null,null);
    }
    public static RetMsg buildSucess(String msg){
        return buildSucess(msg,null);
    }


    public static RetMsg buildError(String msg, Object data){
        if(StringUtils.isBlank(msg)){
            msg = "Fail";
        }
        return buildMsg(400,msg,data);
    }

    /**
     * 初始化data
     * @param initalCapacity
     * @return
     */
    public  RetMsg initMapData(int initalCapacity){
        if(this.data == null){
            if(initalCapacity < 0 ){
                initalCapacity = 4;
            }
            this.data = new LinkedHashMap<>(initalCapacity);
        }
        return this;
    }
    /**
     * 设置数据项 data为空时自动创建，data必须为Map
     * @param key 不能为空 否则无效
     * @param value
     * @return
     * @author taoyz
     * @date 2019-05-23
     */
    public RetMsg putData(String key,Object value){
        initMapData(4);
        if(this.data instanceof Map && StringUtils.isNotBlank(key)){
            ((Map) this.data).put(key,value);
        }
        return this;
    }


}
