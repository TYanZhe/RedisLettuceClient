package cn.org.tpeach.nosql.view.dialog;

import cn.org.tpeach.nosql.constant.PublicConstant;
import cn.org.tpeach.nosql.framework.LarkFrame;
import cn.org.tpeach.nosql.service.ServiceProxy;
import cn.org.tpeach.nosql.tools.SwingTools;
import cn.org.tpeach.nosql.view.RedisMainWindow;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;
@Slf4j
public class Layer {
    private static ConcurrentLinkedDeque<LoadingDialog> loadingDeque = new ConcurrentLinkedDeque();
    @Deprecated
    public static void showDialogLoading(JDialog jDialog, boolean isload, Supplier<Boolean> doInBackground, boolean timeout) {
        jDialog.setVisible(false);
        showLoading(isload, doInBackground, b -> {
            if (b) {
                jDialog.setVisible(true);
            }
        }, timeout);
    }
    @Deprecated
    public static void showLoading(boolean isload, Runnable doInBackground, boolean timeout) {
        showLoading(isload, doInBackground, timeout,false);
    }
    @Deprecated
    public static void showLoading(boolean isload, Runnable doInBackground) {
        showLoading(isload, doInBackground, true);
    }
    @Deprecated
    public static void showLoading(boolean isload, Runnable doInBackground,boolean timeout,boolean rightNow) {
        showLoading(isload, doInBackground, timeout,rightNow,false,0);
    }
    @Deprecated
    public static void showLoading(boolean isload, Runnable doInBackground,boolean timeout,boolean rightNow,boolean delayHidden,int delayMils) {
        showLoading(isload,  () -> {
            doInBackground.run();
            return true;
        }, null,  timeout,rightNow,delayHidden,delayMils);
    }
    @Deprecated
    public static void showLoading(boolean isload, Supplier<Boolean> doInBackground, Consumer<Boolean> hidden, boolean timeout){
        showLoading(isload,  doInBackground, hidden,  timeout,false,false,0);
    }
    @Deprecated
    public static void showLoading(boolean isload, Supplier<Boolean> doInBackground, Consumer<Boolean> hidden, boolean timeout,boolean rightNow,boolean delayHidden,int delayMils) {
        while (loadingDeque.size() > 10) {
            loadingDeque.pop();
        }
        LoadingDialog loadingDialog = null;
        try{
            loadingDialog = loadingDeque.pop();
        }catch (Exception e){}
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog.newLoadingDialog();
        }else{
            loadingDialog.setSize(LoadingDialog.getFrameWidth(), LoadingDialog.getFrameHeight());
        }
        loadingDialog.setLocationRelativeTo(LarkFrame.frame);
        LoadingDialog finalLoadingDialog = loadingDialog;
        loadingDialog.showLoading(isload, doInBackground, hidden, timeout,rightNow,delayHidden,delayMils, () -> {
            loadingDeque.add(finalLoadingDialog);
        });
    }

    public static void resizeDialog(int width, int height) {
        LoadingDialog.resizeDialog(width, height);
    }
    /**
     *
     * @param rightNow false 请求超过300毫秒才显示loading
     * @param timeout 是否65秒超时关闭
     * @param doInBackground
     */
    public static synchronized void showLoading_v2(boolean rightNow, boolean timeout,Runnable doInBackground) {
        showLoading_v2(true,rightNow,timeout,doInBackground);
    }
    /**
     *
     * @param rightNow false 请求超过300毫秒才显示loading
     * @param timeout 是否65秒超时关闭
     * @param doInBackground
     */
    public static synchronized void showLoading_v2(boolean isloading,boolean rightNow, boolean timeout,Runnable doInBackground) {
        if(isloading) {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            AtomicBoolean isFinish = new AtomicBoolean(false);
            SwingTools.swingWorkerExec(() -> {
                try {
                    //超时隐藏loading
                    final Thread requestThread = Thread.currentThread();
                    if (timeout) {
                        LarkFrame.executorService.execute(() -> {
                            try {
                                AtomicBoolean longTime = new AtomicBoolean(true);
                                Thread thread = Thread.currentThread();
                                LarkFrame.executorService.execute(() -> {
                                    try {
                                        countDownLatch.await();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    if (longTime.get()) {
                                        thread.interrupt();
                                    }
                                });
                                TimeUnit.SECONDS.sleep(65);
                                longTime.set(false);
                                if (!isFinish.get()) {
                                    requestThread.interrupt();
                                    SwingTools.showMessageErrorDialog(null, "请求超时");
                                }
                            } catch (InterruptedException e) {

                            }

                        });
                    }
                    //开始请求
                    doInBackground.run();
                } catch (Exception e) {
                    log.error("loding异常", e);
                    SwingTools.showMessageErrorDialog(null, ServiceProxy.getStackTrace(e));
                } finally {

                    isFinish.set(true);
                    countDownLatch.countDown();
                    hiddenLoading();
                }

                return true;
            });
            if (rightNow) {
                showLoadingPanel();
            } else {
                LarkFrame.executorService.schedule(() -> {
                    if (!isFinish.get()) {
                        showLoadingPanel();
                    }
                }, 300, TimeUnit.MILLISECONDS);
            }
        }
    }

    /**
     * 请求超过300毫秒才显示loading  65秒超时关闭
     * @param doInBackground
     */
    public static synchronized void showLoading_v2( Runnable doInBackground) {
        showLoading_v2(false,true, doInBackground);
    }
    private static void showLoadingPanel(){
        RedisMainWindow.loadingGlassPane.setVisible(true);
        RedisMainWindow.loadingGlassPane.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    }
    private static void hiddenLoading(){
        RedisMainWindow.loadingGlassPane.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        RedisMainWindow.loadingGlassPane.setVisible(false);
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
@Slf4j
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

    private static int frameHeight = 200;
    public static int getFrameWidth(){
        return frameWidth;
    }
    public static int getFrameHeight(){
        return frameHeight;
    }
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
        ImageIcon imageIcon = new ImageIcon();
        imageIcon.setImage(PublicConstant.Image.loading_g.getImage());
        JLabel loadingLabel = new JLabel(imageIcon);
        contextPanel.add(loadingLabel);
        //透明
        this.setUndecorated(true);
//        container.setBackground (new Color (0, 0, 0, 0));
        this.setBackground(new Color(0, 0, 0, 5));
        //某些平台 不支持
        try {
            this.setOpacity(0.8f);
        }catch (UnsupportedOperationException e){

        }
        this.getRootPane().setOpaque(false);
        contextPanel.setOpaque(false);
        contextPanel.setBackground(Color.RED);
//        com.sun.awt.AWTUtilities.setWindowOpacity(this, 0.5F);// 设置整个窗体的不透明度为0.5
    }


    void showLoading(boolean isload, Supplier<Boolean> doInBackground, Consumer<Boolean> hidden, boolean timeout,boolean rightNow, boolean delayHidden,int delayMiils,Runnable hiddenLister) {
        if (isload) {
            String uuid = null;
            CountDownLatch countDownLatch = new CountDownLatch(1);
            CountDownLatch countDownLatch2 = new CountDownLatch(1);
            AtomicBoolean atomicBoolean = new AtomicBoolean(true);

            SwingTools.swingWorkerExec(() -> {
                Boolean needVisible = true;
                try {
                    //请求超过300毫秒才显示loading
                    if(rightNow){
                        countDownLatch.countDown();
                    }else{
                        LarkFrame.executorService.schedule(() -> {
                            countDownLatch.countDown();
                        }, 300, TimeUnit.MILLISECONDS);
                    }

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
                                if (atomicBoolean.get()) {
                                    SwingTools.showMessageErrorDialog(null, "请求超时");
                                    hiddenLoading(hiddenLister);
                                }
                            } catch (InterruptedException e) {

                            }

                        });
                    }
                    needVisible = doInBackground.get();
                    atomicBoolean.set(false);
                } catch (Exception e){
                    log.error("loding异常",e);
                    SwingTools.showMessageErrorDialog(null,  ServiceProxy.getStackTrace(e));
                }finally {
                    countDownLatch2.countDown();
                    if (hidden != null) {
                        hidden.accept(needVisible);
                    }
                    if(delayHidden){
                        try {
                            TimeUnit.MILLISECONDS.sleep(delayMiils);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    hiddenLoading(hiddenLister);


                }
                return null;
            });
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (atomicBoolean.get() && countDownLatch2.getCount() > 0) {
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
