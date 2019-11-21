package cn.org.tpeach.nosql.tools;

import java.io.*;
import java.util.Properties;

public class PropertiesUtils {

    /**
     * 获取配置文件
     * @param filePath
     * @return
     */
    public static Properties getProperties(String filePath) throws IOException {
        Properties prop = new Properties();
        InputStream in = null;
        try {
            File file = new File(filePath);
//          直接读取文件
            if (file.canRead()) {
                in = new BufferedInputStream(new FileInputStream(file));
//          从当前路径中获取文件流
            } else {
                in = PropertiesUtils.class.getClassLoader().getResourceAsStream(filePath);
            }
            if (in != null) {
                prop.load(in);
            }else{
                throw new IOException(filePath+" not exists");
            }
        } finally {
            IOUtil.close(in);
        }
        return prop;
    }

    public static void writeProperties(String filePath, String parameterName, String parameterValue) {
        Properties prop = new Properties();
        try {
            InputStream fis = new FileInputStream(filePath);
            //从输入流中读取属性列表（键和元素对）
            prop.load(fis);
            //调用 Hashtable 的方法 put。使用 getProperty 方法提供并行性。
            //强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。
            OutputStream fos = new FileOutputStream(filePath);
            prop.setProperty(parameterName, parameterValue);
            //以适合使用 load 方法加载到 Properties 表中的格式，
            //将此 Properties 表中的属性列表（键和元素对）写入输出流
            prop.store(fos, "Update '" + parameterName + "' value");
        } catch (IOException e) {
            System.err.println("Visit " + filePath + " for updating " + parameterName + " value error");
        }
    }

}
