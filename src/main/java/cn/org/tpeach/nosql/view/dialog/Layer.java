package cn.org.tpeach.nosql.view.dialog;

import cn.org.tpeach.nosql.constant.PublicConstant;
import cn.org.tpeach.nosql.framework.LarkFrame;
import cn.org.tpeach.nosql.tools.StringUtils;
import cn.org.tpeach.nosql.tools.SwingTools;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Layer {
    private static ConcurrentLinkedDeque<LoadingDialog> loadingDeque = new ConcurrentLinkedDeque();

    public static void showDialogLoading(JDialog jDialog, boolean isload, Supplier<Boolean> doInBackground, boolean timeout) {
        jDialog.setVisible(false);
        showLoading(isload, doInBackground, b -> {
            if (b) {
                jDialog.setVisible(true);
            }
        }, timeout);
    }

    public static void showLoading(boolean isload, Runnable doInBackground, boolean timeout) {
        showLoading(isload, () -> {
            doInBackground.run();
            return true;
        }, null, timeout);
    }

    public static void showLoading(boolean isload, Runnable doInBackground) {
        showLoading(isload, doInBackground, true);
    }

    public static void showLoading(boolean isload, Supplier<Boolean> doInBackground, Consumer<Boolean> hidden, boolean timeout) {
//        System.out.println("loadingDeque>>>>>>>>>>>>>>>>:" + loadingDeque.size());
        while (loadingDeque.size() > 10) {
            loadingDeque.pop();
        }
        LoadingDialog loadingDialog = null;
        try{
            loadingDialog = loadingDeque.pop();
        }catch (Exception e){}
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog.newLoadingDialog();
        }
        loadingDialog.setLocationRelativeTo(LarkFrame.frame);
        LoadingDialog finalLoadingDialog = loadingDialog;
        loadingDialog.showLoading(isload, doInBackground, hidden, timeout, () -> {
            loadingDeque.add(finalLoadingDialog);
        });
    }

    public static void resizeDialog(int width, int height) {
        LoadingDialog.resizeDialog(width, height);
    }
}


/**
 * @author tyz
 * @Title: LoadingDialog
 * @ProjectName redisLark-Github
 * @Description: TODO
 * @date 2019-10-11 23:30
 * @since 1.0.0
 */
class LoadingDialog extends JDialog {
    //    public static LoadingDialog getInstance() {
//        Inner.instance.setLocationRelativeTo(LarkFrame.frame);
//        return Inner.instance;
//    }
//
//
//
//    private static class Inner {
//        private static final LoadingDialog instance = new LoadingDialog();
//    }
    @Getter
    private int initWidth = 300;
    @Getter
    private int initHeight = 200;

    private JPanel contextPanel = new JPanel();

    private static int frameWidth = 300;
    @Getter
    private static int frameHeight = 200;

    protected static LoadingDialog newLoadingDialog() {
        LoadingDialog loadingDialog = new LoadingDialog();
        loadingDialog.setSize(frameWidth, frameHeight);
        Container container = loadingDialog.getContentPane();
        container.setPreferredSize(new Dimension(frameWidth, frameHeight));
        container.setMaximumSize(new Dimension(frameWidth, frameHeight));
        container.setMinimumSize(new Dimension(frameWidth, frameHeight));
        return loadingDialog;
    }

    private LoadingDialog() {
        super(LarkFrame.frame, false);
        init();
    }

    public static void resizeDialog(int width, int height) {
        frameWidth = width;
        frameHeight = height;
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
        this.setBackground(new Color(0, 0, 0, 5));
        this.setOpacity(0.8f);
        this.getRootPane().setOpaque(false);
        contextPanel.setOpaque(false);
        contextPanel.setBackground(Color.RED);
//        com.sun.awt.AWTUtilities.setWindowOpacity(this, 0.5F);// 设置整个窗体的不透明度为0.5
    }


    void showLoading(boolean isload, Supplier<Boolean> doInBackground, Consumer<Boolean> hidden, boolean timeout, Runnable hiddenLister) {
        if (isload) {
            String uuid = null;
            CountDownLatch countDownLatch = new CountDownLatch(1);
            CountDownLatch countDownLatch2 = new CountDownLatch(1);
            AtomicBoolean atomicBoolean = new AtomicBoolean(true);
            SwingTools.swingWorkerExec(() -> {
                Boolean needVisible = true;
                try {
                    //请求超过300毫秒才显示loading
                    LarkFrame.executorService.schedule(() -> {
                        countDownLatch.countDown();
                    }, 100, TimeUnit.MILLISECONDS);
                    //超时隐藏loading
                    if (timeout) {
                        LarkFrame.executorService.execute(() -> {
                            try {
                                AtomicBoolean longTime = new AtomicBoolean(true);
                                Thread thread = Thread.currentThread();
                                LarkFrame.executorService.execute(() -> {
                                    try {
                                        countDownLatch2.await();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    if (longTime.get()) {
                                        thread.interrupt();
                                    }
                                });
                                TimeUnit.SECONDS.sleep(65);
                                longTime.set(false);
                            } catch (InterruptedException e) {

                            }
                            if (atomicBoolean.get()) {
                                SwingTools.showMessageErrorDialog(null, "请求超时");
                                hiddenLoading(hiddenLister);
                            }
                        });
                    }
                    try {
                        needVisible = doInBackground.get();
                        atomicBoolean.set(false);
                    } finally {
                        countDownLatch2.countDown();
                    }
                } finally {
                    hiddenLoading(hiddenLister);
                    if (hidden != null) {
                        hidden.accept(needVisible);
                    }

                }
                return null;
            });
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (atomicBoolean.get()) {
                this.setVisible(true);
            }
        } else {
            doInBackground.get();
        }
    }

    private void hiddenLoading(Runnable hiddenLister) {
        SwingTools.swingWorkerExec(() -> {
            this.setVisible(false);
            if (hiddenLister != null) {
                hiddenLister.run();
            }
            return null;
        });
    }
}
