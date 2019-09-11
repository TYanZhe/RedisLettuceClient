/**
 * 
 */
package cn.org.tpeach.nosql.tools;

import java.io.IOException;
import java.io.RandomAccessFile;

public class MyRandomAccessFile {
/**
     *文件修改
     * @param fileName 文件名称
     * @param oldstr   要修改的字符串
     * @param newStr   新的字符串
     * @return
     */
    //  文件修改 文件内容出现换行就会有错误
    private static boolean modifyFileContent(String fileName, String oldstr, String newStr) {
        ///定义一个随机访问文件类的对象
        RandomAccessFile raf = null;
        try {
            //初始化对象,以"rw"(读写方式)访问文件
            raf = new RandomAccessFile(fileName, "rw");
            //临时变量,存放每次读出来的文件内容
            String line = null;
            // 记住上一次的偏移量
            long lastPoint = 0;
            //循环读出文件内容
            while ((line = raf.readLine()) != null) {
                // 文件当前偏移量 返回文件记录指针的当前位置
                final long point = raf.getFilePointer();
                // 查找要替换的内容
                if (line.contains(oldstr)) {
                    //修改内容,line读出整行数据
                    String str = line.replace(oldstr, newStr);
                    //文件节点移动到文件开始
                    System.out.println(str);
                    raf.seek(lastPoint);
                    raf.writeBytes(str);
                }
                lastPoint = point;//如果文件出现换行,则修改后的节点需要移动到下一行的开头,所以此处会出现错误
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
     public static void main(String[]args){
        boolean b=modifyFileContent("D:\\temp\\临时文件.txt","123","水电费第三方见识到了");
        System.out.println(b);
    }
}
