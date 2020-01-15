package cn.org.tpeach.nosql.view;

import cn.org.tpeach.nosql.constant.ConfigConstant;
import cn.org.tpeach.nosql.constant.PublicConstant;
import cn.org.tpeach.nosql.constant.RedisInfoKeyConstant;
import cn.org.tpeach.nosql.framework.LarkFrame;
import cn.org.tpeach.nosql.redis.bean.RedisTreeItem;
import cn.org.tpeach.nosql.redis.service.IRedisConnectService;
import cn.org.tpeach.nosql.service.ServiceProxy;
import cn.org.tpeach.nosql.tools.*;
import cn.org.tpeach.nosql.view.component.PrefixTextLabel;
import cn.org.tpeach.nosql.view.component.RButton;
import cn.org.tpeach.nosql.view.dialog.MonitorDialog;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.text.DecimalFormat;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

@Getter
@Setter
@Slf4j
public class StatePanel extends JPanel {
 
	private static final long serialVersionUID = -962162242158879466L;
	private  JLabel connectStateLabel;
    private JLabel redisServerVersionLabel;
    private JLabel clientCountLabel,loadingLabel;
    private RedisTreeItem currentRedisItem;
    private JPanel loadingGlassPane;
    private RButton loadingGlassbutton;
    @Getter
    private MonitorDialog monitorDialog;
    IRedisConnectService redisConnectService = ServiceProxy.getBeanProxy("redisConnectService", IRedisConnectService.class);
    private static DecimalFormat df = new DecimalFormat("0.00");
    private List<Integer> loadingQueue = new Vector<>(5);
    //五分鐘
    public static final int DEFAULTTIMEOUT = 300000;
    @Getter
    @Setter
    private static class LoadingData{
        public static final String defaultTime = "0.00s";
        Long startTime;
        private String loadId;
        private Thread timeouThread;
        private Thread requestThread;
        private boolean isNeedGlassPanl;
        public Double getSecondTime(){
            if(startTime == null){
                return 0D;
            }
            return Double.valueOf(MathUtils.divide(2,(System.currentTimeMillis() - startTime)+"","1000"));
        }
    }
    private static ConcurrentLinkedDeque<String> hiddenDeque = new ConcurrentLinkedDeque<>();
    private static ConcurrentLinkedDeque<LoadingData> loadingDataDeque = new ConcurrentLinkedDeque<>();
	private static Vector<Future<?>> loadingTextVector = new Vector<>(1);
    private static ConcurrentLinkedDeque<String> showGlassQueue =  new ConcurrentLinkedDeque<>();
    public StatePanel(MonitorDialog monitorDialog) {
        this.monitorDialog = monitorDialog;
        this.connectStateLabel = getLable();
        this.redisServerVersionLabel = getLable();
        this.clientCountLabel = getLable();

        this.connectStateLabel.setForeground(new java.awt.Color(255, 0, 51));
        this.connectStateLabel.setText("未连接到服务");
        this.loadingLabel = new JLabel();
        this.loadingLabel.setIcon(PublicConstant.Image.getImageIcon(PublicConstant.Image.loading_o,16,16));
        loadingLabel.setVisible(false);
        loadingLabel.setText(LoadingData.defaultTime);

        this.addStrut(connectStateLabel);
        this.addStrut(clientCountLabel);
        //loading
        this.add(Box.createHorizontalGlue());
        this.add(loadingLabel);
        this.add(Box.createHorizontalGlue());
        //版本信息
        this.add(redisServerVersionLabel);
        this.add(Box.createHorizontalStrut(5));
        //占用内存
        MemoryMXBean bean = ManagementFactory.getMemoryMXBean();
        PrefixTextLabel userMemoryLabel = new PrefixTextLabel("内存占用:");
        userMemoryLabel.setFont(redisServerVersionLabel.getFont());
        userMemoryLabel.setForeground(Color.GREEN.darker().darker() );
        userMemoryLabel.setText(StringUtils.getLengthHummanText(bean.getHeapMemoryUsage().getUsed()));
        long period = ConfigParser.getInstance().getLong(ConfigConstant.Section.EXPERIMENT, ConfigConstant.MEMORY_FIXEDRATE_PERIOD, 5);
        LarkFrame.executorService.scheduleAtFixedRate(()->{
            long used = bean.getHeapMemoryUsage().getUsed();
            userMemoryLabel.setText(StringUtils.getLengthHummanText(used));
            if(used > 200 * 1024 * 1024){
                System.gc();
            }
        },period,period,TimeUnit.SECONDS);
        this.add(userMemoryLabel);
        this.add(Box.createHorizontalStrut(5));


        loadingGlassPane = buildLoadingGlassPane();
    }
    private void addStrut(Component comp){
        this.add(Box.createHorizontalStrut(5));
        this.add(comp);

    }

    /**
     * 图片来源
     * https://blog.csdn.net/weixin_42735261/article/details/99844390
     * http://www.woshipm.com/pd/2141507.html
     * https://mp.weixin.qq.com/s?__biz=MjM5MDMxOTE5NA==&mid=402703079&idx=2&sn=2fcc6746a866dcc003c68ead9b68e595&scene=2&srcid=0302A7p723KK8E5gSzLKb2ZL&from=timeline&isappinstalled=0#wechat_redirect
     * @return
     */
    private ImageIcon getLoadingIcon(){
        int index;
        if(!loadingQueue.isEmpty()){
            index = loadingQueue.remove(loadingQueue.size() - 1);
        }else{
            String loadingGifIndex = LarkFrame.APPLICATION_VALUE.getProperty("loading.gif.max.index");
            index = (int) (Math.random() * Integer.valueOf(loadingGifIndex));
            IntStream.range(0,5).forEach(i->loadingQueue.add(index));
        }
        ImageIcon imageIcon ;
        String path = String.format(PublicConstant.Image.loading_g, index);
        if(PublicConstant.Image.catchContainsKey(path)){
            imageIcon = PublicConstant.Image.getImageIcon(path);
        }else{
            imageIcon = PublicConstant.Image.getImageIcon(path);
            int width = imageIcon.getIconWidth()/2;
            int height = imageIcon.getIconHeight()/2;
            imageIcon = PublicConstant.Image.getImageIcon(path,width,height);
            PublicConstant.Image.removeImageCatch(path,width,height);
            PublicConstant.Image.replaceImageCatch(path,imageIcon);
        }
        return imageIcon;
    }

    private JPanel buildLoadingGlassPane(){
        JPanel loadingGlassPane = new JPanel();
        loadingGlassbutton = new RButton();
//        button.setText("Loading data, please wait...");

        loadingGlassbutton.setIcon( getLoadingIcon());
        loadingGlassbutton.setVerticalTextPosition(SwingConstants.BOTTOM);
        loadingGlassbutton.setHorizontalTextPosition(SwingConstants.CENTER);
        loadingGlassbutton.setFocusPainted(false);
        loadingGlassbutton.setOpaque(false);
//        button.setFont(new Font("新宋体", Font.ITALIC, 16));
        loadingGlassbutton.setForeground(new Color(94,70,116));
        loadingGlassPane.setLayout(new BorderLayout());

        loadingGlassPane.add(loadingGlassbutton);
// Transparent
        loadingGlassPane.setOpaque(false);
        return loadingGlassPane;
    }
    public static synchronized void showLoading(Runnable doInBackground){
        showLoading(true,doInBackground);
    }
    public static synchronized void showLoading(boolean isLoad,Runnable doInBackground){
        showLoading(isLoad,doInBackground,true,false );
    }
    public static synchronized void showLoading(Runnable doInBackground,boolean isNeedGlassPanl,boolean rightNow ){
        showLoading(true,doInBackground,isNeedGlassPanl,rightNow);
    }
    public static synchronized void showLoading(boolean isLoad,Runnable doInBackground,boolean isNeedGlassPanl,boolean rightNow ){
        showLoading(isLoad,doInBackground,isNeedGlassPanl,rightNow,DEFAULTTIMEOUT,null);
    }
    public static synchronized void showLoading(boolean isLoad, Runnable doInBackground, boolean isNeedGlassPanl, boolean rightNow, int timeOut){
        showLoading(isLoad,doInBackground,isNeedGlassPanl,rightNow,timeOut,null);
    }

    public static synchronized void showLoading(boolean isLoad, Runnable doInBackground, boolean isNeedGlassPanl, boolean rightNow, int timeOut, BiConsumer<String,Double> hiddenLister){
        if(!isLoad){
            doInBackground.run();
            return;
        }
        boolean release = PublicConstant.ProjectEnvironment.RELEASE.equals(LarkFrame.getProjectEnv());
        Optional<RedisMainWindow> frame = Optional.ofNullable((RedisMainWindow)LarkFrame.frame) ;
        if(frame.isPresent()){
            RedisMainWindow redisMainWindow = frame.get();
            String loadId = StringUtils.getUUID();
            StatePanel statePanel = redisMainWindow.getStatePanel();
            redisMainWindow.setGlassPane(statePanel.loadingGlassPane);
            LoadingData loadingData = new LoadingData();
            loadingData.setLoadId(loadId);
            loadingData.setStartTime(System.currentTimeMillis());
            loadingData.setNeedGlassPanl(isNeedGlassPanl);
            hiddenDeque.add(loadId);
            loadingDataDeque.add(loadingData);
            String globalTimeOutFlag = ConfigParser.getInstance().getString(ConfigConstant.Section.EXPERIMENT, ConfigConstant.LOADING_GLOBEL_TIMEOGT_ENABLED, "0");
            // 超时自动取消
            if(timeOut > 0 && !"1".equals(globalTimeOutFlag)) {
                CountDownLatch countDownLatch = new CountDownLatch(1);
                LarkFrame.executorService.execute(() -> {
                    try {
                        loadingData.setTimeouThread(Thread.currentThread());
                    } finally {
                        countDownLatch.countDown();
                    }
                    try {
                        TimeUnit.MILLISECONDS.sleep(timeOut);
                        Optional.ofNullable(loadingData.getRequestThread()).ifPresent(t -> t.interrupt());
                    } catch (InterruptedException e) {
//                        e.printStackTrace();
                    }finally {
                        loadingData.setTimeouThread(null);
                    }
                });
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if(!release){
                log.debug("显示loading:{}",loadId);
            }
            //耗時統計
            if(loadingTextVector.isEmpty()){
                Future<?> future = LarkFrame.executorService.scheduleAtFixedRate(()->{
                    try {
                        if(!release){
                            log.debug("耗时统计线程:{},{}",Thread.currentThread(),statePanel.loadingLabel.isVisible());
                        }
                        //状态栏显示耗时
                        String text = statePanel.loadingLabel.getText();
                        if (StringUtils.isNotBlank(text)) {
                            try {
                                Double s = Double.valueOf(text.replace("s", ""));
                                if (s > loadingData.getSecondTime()) {
                                    return;
                                }
                            } catch (Exception e) {
                                return;
                            }
                        }
                        Double time = maxLoadingTime();

                        statePanel.loadingLabel.setText(df.format(time) + "s");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                },300,300,TimeUnit.MILLISECONDS);
                loadingTextVector.add(future);
            }
            //開始請求
            if(statePanel != null){
                AtomicBoolean isFinish = new AtomicBoolean(false);
                SwingTools.swingWorkerExec(() -> {
                    loadingData.setRequestThread(Thread.currentThread());
                    if(rightNow){
                        if(isNeedGlassPanl){
                            showGlassQueue.add(loadId);
                            statePanel.loadingGlassPane.setVisible(true);
                            statePanel.loadingGlassbutton.setIcon( statePanel.getLoadingIcon());
                            statePanel.loadingGlassPane.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                        }
                        statePanel.loadingLabel.setVisible(true);
                    }else{
                        SwingTools.swingWorkerExec(()->{
                            try {
                                TimeUnit.MILLISECONDS.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if(!isFinish.get()){
                                if(isNeedGlassPanl){
                                    showGlassQueue.add(loadId);
                                    statePanel.loadingGlassPane.setVisible(true);
                                    statePanel.loadingGlassPane.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                                }
                                statePanel.loadingLabel.setVisible(true);
                            }
                        });
                    }

                    doInBackground.run();
                    return "OK";
                },()->{
                    Optional.ofNullable(loadingData.getTimeouThread()).ifPresent(t->t.interrupt());
                    isFinish.set(true);
                    Double time =  maxLoadingTime();
                    hiddenDeque.remove(loadId);
                    loadingDataDeque.remove(loadingData);
                    showGlassQueue.remove(loadId);
                    if(hiddenLister != null){
                        hiddenLister.accept(loadId,loadingData.getSecondTime());
                    }
                    if(hiddenDeque.isEmpty()){
                        Iterator<Future<?>> iterator = loadingTextVector.iterator();
                        while (iterator.hasNext()){
                            iterator.next().cancel(true);
                            iterator.remove();
                        }
                        statePanel.loadingLabel.setVisible(false);
                        statePanel.loadingLabel.setText(LoadingData.defaultTime);
                        if(!release){
                            log.info("隐藏loading:{},耗時：{}s",loadId,df.format(time));
                        }
                    }
                    if(showGlassQueue.isEmpty() && statePanel.loadingGlassPane.isVisible()){
                        statePanel.loadingGlassPane.setVisible(false);
                        statePanel.loadingGlassPane.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        if(!release){
                            log.debug("隐藏glass:{}",loadId);
                        }
                    }
                });
            }

        }else{
            doInBackground.run();
        }
    }

    private static Double maxLoadingTime() {
        Double time = 0.00;
        Iterator<LoadingData> iterator = loadingDataDeque.iterator();
        while (iterator.hasNext()) {
            LoadingData next = iterator.next();
            if (next.getSecondTime() > time) {
                time = next.getSecondTime();
            }
        }
        return time;
    }

    private JLabel getLable(){
        JLabel label = new JLabel();
        label.setFont(new Font("黑体",Font.PLAIN,14));
        label.setForeground(Color.BLUE.brighter().brighter());
        return label;
    }

    public synchronized void doUpdateStatus(RedisTreeItem redisTreeItem){
        Map<String, String> connectInfo = null;
        if(redisTreeItem != null){
           connectInfo = redisConnectService.getConnectInfo(redisTreeItem.getId(),false);
        }
       if(MapUtils.isNotEmpty(connectInfo)){
            currentRedisItem = redisTreeItem ;
            //设置连接
            this.connectStateLabel.setForeground(Color.GREEN.darker().darker());
            this.connectStateLabel.setText("已成功连接到:"+redisTreeItem.getParentName());
            //版本信息
            String version = connectInfo.get(RedisInfoKeyConstant.redisVersion);
            if(StringUtils.isNotBlank(version)){
                this.redisServerVersionLabel.setText("Redis版本:"+version);
            }
            String connectedClients = connectInfo.get(RedisInfoKeyConstant.connectedClients);
            if(StringUtils.isNotBlank(version)){
                this.clientCountLabel.setText("客户端数量:"+connectedClients);
            }
        }else{
            currentRedisItem = null ;
            this.connectStateLabel.setForeground(new java.awt.Color(255, 0, 51));
            this.connectStateLabel.setText("未连接到服务");
            this.clientCountLabel.setText("");
            this.redisServerVersionLabel.setText("");
        }
    }
}
