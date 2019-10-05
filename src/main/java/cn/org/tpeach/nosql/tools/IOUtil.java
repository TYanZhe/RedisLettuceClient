package cn.org.tpeach.nosql.tools;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.io.*;

/**
 * @author tyz
 * @Title: IOUtil
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-06-26 22:45
 * @since 1.0.0
 */
@Slf4j
public class IOUtil {
	private static final int BUFFER_SIZE = 2 * 1024;
	public static final int EOF = -1;

	public static byte[] getByteArray(String path) throws IOException {
		try (InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
				ByteArrayOutputStream swapStream = new ByteArrayOutputStream();) {
			if (resourceAsStream == null) {
				throw new FileNotFoundException(path + "不存在");
			}
			copy(resourceAsStream, swapStream);
			return swapStream.toByteArray();
		}

	}

	public static ImageIcon getImageIcon(String path,int width,int height) {
		return setImageLength(getImageIcon(path),width, height);
	}
	public static ImageIcon setImageLength(ImageIcon ii,int width,int height) {
		ii.setImage(ii.getImage().getScaledInstance(width, height,Image.SCALE_DEFAULT));
		return ii;
	}
	public static ImageIcon getImageIcon(String path) {
		try {
			ImageIcon ii = new ImageIcon(IOUtil.class.getClassLoader().getResource(path));
			return ii;	
		}catch(Exception e) {
			return null;
		}
	
	}
	public static File getFile(String strPath) throws IOException {
		File file = new File(strPath);
		if (!file.exists()) {
			File fileParent = file.getParentFile();
			if (!fileParent.exists()) {
				fileParent.mkdirs();
			}
			file.createNewFile();
			file = new File(strPath);
		}
		return file;

	}

	public static long copy(final InputStream in, final OutputStream out) throws IOException {
		if (in == null || out == null) {
			return -1;
		}
		int len;
		long count = 0;
		byte[] buffer = new byte[BUFFER_SIZE];
		while ((len = in.read(buffer)) != EOF) {
			out.write(buffer, 0, len);
			count += len;
		}
		return count;
	}
	/**
	 * 覆盖原内容
	 * @param inputStream
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	public static void writeConfigFile(InputStream inputStream,File file) throws IOException{
		byte[] bytes = new byte[BUFFER_SIZE];
		int index;
		try(FileOutputStream fileOutputStream = new FileOutputStream(file)){
			 while ((index = inputStream.read(bytes)) != -1){
				 fileOutputStream.write(bytes, 0, index);
				 fileOutputStream.flush();
			 }
		}


	}
    /**
     * 覆盖原内容
     * @param content
     * @param fileName
     * @return
     * @throws IOException 
     * @throws Exception
     */
    public static void writeConfigFile(String content,File file) throws IOException{
		try(FileOutputStream fileOutputStream = new FileOutputStream(file);){
			fileOutputStream.write(content.getBytes("UTF-8"));
		}
    }

    /**
     * 追加写入
     * @param filePath
     * @param content
     * @throws IOException 
     */
    public static void fileAppendFW(File file, String content) throws IOException {
        //构造函数中的第二个参数true表示以追加形式写文件
        FileWriter fw = new FileWriter(file,true);
        fw.write(content);
        fw.close();
    
    }


	public static void close(Closeable... closeableList) {
		try {
			for (Closeable closeable : closeableList) {
				if (closeable != null) {
					closeable.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
