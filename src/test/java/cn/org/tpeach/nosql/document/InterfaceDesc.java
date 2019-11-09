package cn.org.tpeach.nosql.document;

import lombok.Data;

@Data
public class InterfaceDesc {
    public final static class REQUEST_METHOD{
        public final static String POST = "POST";
        public final static String GET = "GET";
    }
    public final static class CONTENT_TYPE{
        public final static String FORM = "application/x-www-form-urlencoded";
        public final static String FILE = "multipart/form-data";
        public final static String TEXT = "text/xml";
        public final static String JSON = "application/json";
    }
    private String desc;
    private String requestMethod;
    private String url;
    private String contentType;

    private InterfaceDesc(){
    }

    public InterfaceDesc desc(String desc){
        this.desc = desc;
        return this;
    }

    public static InterfaceDesc buildGet_Form(String url){
        InterfaceDesc interfaceDesc = new InterfaceDesc();
        interfaceDesc.contentType = CONTENT_TYPE.FORM;
        interfaceDesc.requestMethod = REQUEST_METHOD.GET;
        interfaceDesc.url = url;
        return interfaceDesc;
    }
    public static InterfaceDesc buildGet_File(String url){
        InterfaceDesc interfaceDesc = new InterfaceDesc();
        interfaceDesc.contentType = CONTENT_TYPE.FILE;
        interfaceDesc.requestMethod = REQUEST_METHOD.GET;
        interfaceDesc.url = url;
        return interfaceDesc;
    }
    public static InterfaceDesc buildGet_Text(String url){
        InterfaceDesc interfaceDesc = new InterfaceDesc();
        interfaceDesc.contentType = CONTENT_TYPE.TEXT;
        interfaceDesc.requestMethod = REQUEST_METHOD.GET;
        interfaceDesc.url = url;
        return interfaceDesc;
    }
    public static InterfaceDesc buildGet_Json(String url){
        InterfaceDesc interfaceDesc = new InterfaceDesc();
        interfaceDesc.contentType = CONTENT_TYPE.JSON;
        interfaceDesc.requestMethod = REQUEST_METHOD.GET;
        interfaceDesc.url = url;
        return interfaceDesc;
    }
    public static InterfaceDesc buildPost_Form(String url){
        InterfaceDesc interfaceDesc = new InterfaceDesc();
        interfaceDesc.contentType = CONTENT_TYPE.FORM;
        interfaceDesc.requestMethod = REQUEST_METHOD.POST;
        interfaceDesc.url = url;
        return interfaceDesc;
    }
    public static InterfaceDesc buildPost_File(String url){
        InterfaceDesc interfaceDesc = new InterfaceDesc();
        interfaceDesc.contentType = CONTENT_TYPE.FILE;
        interfaceDesc.requestMethod = REQUEST_METHOD.POST;
        interfaceDesc.url = url;
        return interfaceDesc;
    }
    public static InterfaceDesc buildPost_Text(String url){
        InterfaceDesc interfaceDesc = new InterfaceDesc();
        interfaceDesc.contentType = CONTENT_TYPE.TEXT;
        interfaceDesc.requestMethod = REQUEST_METHOD.POST;
        interfaceDesc.url = url;
        return interfaceDesc;
    }
    public static InterfaceDesc buildPost_Json(String url){
        InterfaceDesc interfaceDesc = new InterfaceDesc();
        interfaceDesc.contentType = CONTENT_TYPE.JSON;
        interfaceDesc.requestMethod = REQUEST_METHOD.POST;
        interfaceDesc.url = url;
        return interfaceDesc;
    }
}
