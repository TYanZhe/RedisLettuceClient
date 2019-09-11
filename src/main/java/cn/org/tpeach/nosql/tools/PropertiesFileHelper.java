package cn.org.tpeach.nosql.tools;

import java.io.*;
import java.util.Properties;

/**
 * @author tyz
 * @Title: PropertiesFileHelper
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-06-26 22:38
 * @since 1.0.0
 */
public class PropertiesFileHelper {
    /**
     * 获取Properties配置文件
     * @return
     */
    public Properties getProperties(String properiesName) throws IOException {
        File file = IOUtil.getFile(properiesName);
        try(InputStream in = new BufferedInputStream(new FileInputStream(file))){
            Properties p = new Properties();
            p.load(in);
            return p;
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("配置文件路径不正确");
        }

    }
}
