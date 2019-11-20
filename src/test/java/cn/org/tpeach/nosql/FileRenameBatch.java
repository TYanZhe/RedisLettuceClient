package cn.org.tpeach.nosql;

import cn.org.tpeach.nosql.tools.IOUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * @author tyz
 * @Title: FileRenameBatch
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-11-17 8:40
 * @since 1.0.0
 */
public class FileRenameBatch {

    public static void main(String[] args) throws Exception{
        String fileDir = "D:\\home\\temp_dir";
        File file = new File(fileDir);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            FileInputStream fileInputStream = new FileInputStream(files[i]);
            FileOutputStream fileOutputStream =null;
//                    new FileOutputStream("D:\\WorkSpace\\Git\\GitHub\\RedisLark\\src\\main\\resources\\image\\base\\loading_g"+(i+2)+".gif");
            IOUtil.copy(fileInputStream,fileOutputStream);
            fileOutputStream.flush();
            fileInputStream.close();
            fileOutputStream.close();
        }

    }
}
