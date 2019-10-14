package cn.org.tpeach.nosql.view.dialog;

import cn.org.tpeach.nosql.constant.PublicConstant;
import cn.org.tpeach.nosql.framework.LarkFrame;
import cn.org.tpeach.nosql.tools.StringUtils;
import cn.org.tpeach.nosql.tools.SwingTools;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.time.Clock;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author tyz
 * @Title: LoadingDialog
 * @ProjectName redisLark-Github
 * @Description: TODO
 * @date 2019-10-11 23:30
 * @since 1.0.0
 */
public class LoadingDialog extends JDialog {
    public static LoadingDialog getInstance() {
        Inner.instance.setLocationRelativeTo(LarkFrame.frame);
        return Inner.instance;
    }



    private static class Inner {
        private static final LoadingDialog instance = new LoadingDialog();
    }

    @Getter
    private int initWidth = 300;
    @Getter
    private int initHeight = 200;
    private static ConcurrentSkipListSet<String> loadingSet = new ConcurrentSkipListSet<>();
    private JPanel contextPanel = new JPanel();

    private LoadingDialog() {
        super(LarkFrame.frame, false);
        init();
    }

    public static void resizeDialog(int width, int height) {
        getInstance().setSize(width, height);
        Container container = getInstance().getContentPane();
        container.setPreferredSize(new Dimension(width, height));
        container.setMaximumSize(new Dimension(width, height));
        container.setMinimumSize(new Dimension(width, height));
    }

    private void init() {
        Container container = this.getContentPane();
        this.setSize(initWidth, initHeight);
        container.setLayout(new BorderLayout());
        container.add(contextPanel);

        container.setPreferredSize(new Dimension(initWidth, initHeight));
        container.setMaximumSize(new Dimension(initWidth, initHeight));
        container.setMinimumSize(new Dimension(initWidth, initHeight));
        contextPanel.setLayout(new BorderLayout());

        JLabel loadingLabel = new JLabel(PublicConstant.Image.loading_g);
        contextPanel.add(loadingLabel);
        //透明
        this.setUndecorated(true);
//        container.setBackground (new Color (0, 0, 0, 0));
//        this.setBackground(new Color (0, 0, 0, 0));
//        this.getRootPane().setOpaque(false);
//        contextPanel.setOpaque(false);
//        contextPanel.setBackground(Color.RED);
        com.sun.awt.AWTUtilities.setWindowOpacity(this, 0.5F);// 设置整个窗体的不透明度为0.5
    }
    public static void showDialogLoading(JDialog jDialog, boolean isload, Supplier<Boolean> doInBackground) {
        jDialog.setVisible(false);
        showLoading(isload,doInBackground,b -> {
            if(b){
                jDialog.setVisible(true);
            }
        });
    }
    public static void showLoading(boolean isload, Runnable doInBackground){
        showLoading(isload,()->{doInBackground.run();return true;},null);
    }
    public static   void showLoading(boolean isload, Supplier<Boolean> doInBackground, Consumer<Boolean> hidden) {
        if (isload) {
            String uuid = StringUtils.getUUID();
            loadingSet.add(uuid);
            CountDownLatch countDownLatch = new CountDownLatch(1);
            AtomicBoolean atomicBoolean = new AtomicBoolean(true);
            LarkFrame.executorService.execute(()->{
                Boolean needVisible = true;
                try {

                    LarkFrame.executorService.schedule(()->{
                        countDownLatch.countDown();
                    },300,TimeUnit.MILLISECONDS);
                    needVisible = doInBackground.get();
                    atomicBoolean.set(false);
                } finally {
                    hiddenLoading(uuid);
                    if(hidden != null){
                        hidden.accept(needVisible);
                    }

                }
            });
            SwingTools.swingWorkerExec(() -> {
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(atomicBoolean.get()){
                    LoadingDialog.getInstance().setVisible(true);
                }
                return null;
            });

        } else {
            doInBackground.get();
        }
    }

    private static void hiddenLoading(String loadId) {
        if (StringUtils.isNotBlank(loadId)) {
            if (loadingSet.contains(loadId)) {
                loadingSet.remove(loadId);
                if (loadingSet.isEmpty()) {
                    SwingTools.swingWorkerExec(() -> {
                        try {
//                TimeUnit.SECONDS.sleep(5);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }, () -> LoadingDialog.getInstance().setVisible(false));
                }
            }
        }
    }
}
