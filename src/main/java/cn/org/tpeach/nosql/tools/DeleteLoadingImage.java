package cn.org.tpeach.nosql.tools;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import java.io.File;

/**
 * 自定义Ant任务必须继承Task,重写execute
 *
 * @author taoyz
 */
@Getter
@Setter
@Slf4j
public class DeleteLoadingImage extends Task {
    private int loadingMaxIndex = 0;
    private int loadingMaxNum = 0;
    private String buildDirectory;

    @Override
    public void execute() throws BuildException {
        String path = "/classes/image/base/loading_g%s.gif";
        for (int i = loadingMaxIndex+1; i <= loadingMaxNum; i++) {
            try {
                File file = new File(buildDirectory + String.format(path, i));
                file.delete();
                log.info("delete {}", file.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        new File(buildDirectory+"/classes/cn/org/tpeach/nosql/tools/DeleteLoadingImage.class").delete();


    }

}
