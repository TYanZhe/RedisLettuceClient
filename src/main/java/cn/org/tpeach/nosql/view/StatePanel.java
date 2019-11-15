package cn.org.tpeach.nosql.view;

import cn.org.tpeach.nosql.constant.PublicConstant;
import cn.org.tpeach.nosql.constant.RedisInfoKeyConstant;
import cn.org.tpeach.nosql.framework.LarkFrame;
import cn.org.tpeach.nosql.redis.bean.RedisTreeItem;
import cn.org.tpeach.nosql.redis.service.IRedisConnectService;
import cn.org.tpeach.nosql.service.ServiceProxy;
import cn.org.tpeach.nosql.tools.MapUtils;
import cn.org.tpeach.nosql.tools.MathUtils;
import cn.org.tpeach.nosql.tools.StringUtils;
import cn.org.tpeach.nosql.tools.SwingTools;
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
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
@Setter
@Slf4j
public class StatePanel extends JPanel {
    private  JLabel connectStateLabel;
    private JLabel redisServerVersionLabel;
    private JLabel clientCountLabel,loadingLabel;
    private RedisTreeItem currentRedisItem;
    private JPanel loadingGlassPane;
    @Getter
    private MonitorDialog monitorDialog;
    IRedisConnectService redisConnectService = ServiceProxy.getBeanProxy("redisConnectService", IRedisConnectService.class);
    private static DecimalFormat df = new DecimalFormat("0.00");
    @Getter
    @Setter
    private static class LoadingData{
        public static final String defaultTime = "0.00s";
        Long startTime;
        private String loadId;
        private Thread timeouThread;
        private Thread requestThread;
        private boolean isNeedGlassPanl;
        public Double getTime(){
            if(startTime == null){
                return 0D;
            }
            return Double.valueOf(MathUtils.divide(2,(System.currentTimeMillis() - startTime)+"","1000"));
        }
    }
    private static ConcurrentLinkedDeque<String> hiddenDeque = new ConcurrentLinkedDeque();
    private static ConcurrentLinkedDeque<LoadingData> loadingDataDeque = new ConcurrentLinkedDeque();
    private static Vector<Future> loadingTextVector = new Vector(1);
    private static ConcurrentLinkedDeque<String> showGlassQueue =  new ConcurrentLinkedDeque();
    public StatePanel(MonitorDialog monitorDialog) {
        this.monitorDialog = monitorDialog;
        this.connectStateLabel = getLable();
        this.redisServerVersionLabel = getLable();
        this.clientCountLabel = getLable();

        this.connectStateLabel.setForeground(new java.awt.Color(255, 0, 51));
        this.connectStateLabel.setText("未连接到服务");
        this.loadingLabel = new JLabel();
        this.loadingLabel.setIcon(PublicConstant.Image.loading_o);
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
        LarkFrame.executorService.scheduleAtFixedRate(()->{
            userMemoryLabel.setText(StringUtils.getLengthHummanText(bean.getHeapMemoryUsage().getUsed()));
        },1,1,TimeUnit.SECONDS);
        this.add(userMemoryLabel);
        this.add(Box.createHorizontalStrut(5));


        loadingGlassPane = getLoadingGlassPane();
    }
    private void addStrut(Component comp){
        this.add(Box.createHorizontalStrut(5));
        this.add(comp);

    }
    private JPanel getLoadingGlassPane(){
        JPanel loadingGlassPane = new JPanel();
        RButton button = new RButton();
        button.setText("Loading data, please wait...");
        button.setIcon(PublicConstant.Image.loading_g);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setFocusPainted(false);
        button.setOpaque(false);
//        button.setFont(new Font("新宋体", Font.ITALIC, 16));
        button.setForeground(new Color(80,223,240));
        loadingGlassPane.setLayout(new BorderLayout());

        loadingGlassPane.add(button);
// Transparent
        loadingGlassPane.setOpaque(false);
        return loadingGlassPane;
    }
    public static synchronized void showLoading(Runnable doInBackground){
        showLoading(doInBackground,true,false,-1);
    }
    public static synchronized void showLoading(Runnable doInBackground,boolean isNeedGlassPanl,boolean rightNow ){
        showLoading(doInBackground,isNeedGlassPanl,rightNow,-1);
    }
    public static synchronized void showLoading(Runnable doInBackground,boolean isNeedGlassPanl,boolean rightNow,long delayHiddenMs){
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
            CountDownLatch countDownLatch = new CountDownLatch(1);
            hiddenDeque.add(loadId);
            loadingDataDeque.add(loadingData);
            LarkFrame.executorService.execute(()->{
                try{
                    loadingData.setTimeouThread(Thread.currentThread());
                    countDownLatch.countDown();
                    TimeUnit.MINUTES.sleep(5);
                    Optional.ofNullable(loadingData.getRequestThread()).ifPresent(t->t.interrupt());
                }catch (Exception e){}finally {
                    loadingData.setTimeouThread(null);
                }
            });
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(!release){
                log.debug("显示loading:{}",loadId);
            }
            if(loadingTextVector.isEmpty()){
                Future future = LarkFrame.executorService.scheduleAtFixedRate(()->{
                    try {
                        if(!release){
                            log.debug("耗时统计线程:{},{}",Thread.currentThread(),statePanel.loadingLabel.isVisible());
                        }
                        String text = statePanel.loadingLabel.getText();
                        if (StringUtils.isNotBlank(text)) {
                            try {
                                Double s = Double.valueOf(text.replace("s", ""));
                                if (s > loadingData.getTime()) {
                                    return;
                                }
                            } catch (Exception e) {
                                return;
                            }
                        }
                        Double time = 0.00;
                        Iterator<LoadingData> iterator = loadingDataDeque.iterator();
                        while (iterator.hasNext()) {
                            LoadingData next = iterator.next();
                            if (next.getTime() > time) {
                                time = next.getTime();
                            }
                        }

                        statePanel.loadingLabel.setText(df.format(time) + "s");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                },300,300,TimeUnit.MILLISECONDS);
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {

                }
                loadingTextVector.add(future);
            }

            if(statePanel != null){
                AtomicBoolean isFinish = new AtomicBoolean(false);
                SwingTools.swingWorkerExec(() -> {
                    loadingData.setRequestThread(Thread.currentThread());
                    if(rightNow){
                        if(isNeedGlassPanl){
                            showGlassQueue.add(loadId);
                            statePanel.loadingGlassPane.setVisible(true);
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
                    isFinish.set(true);
                    hiddenDeque.remove(loadId);
                    loadingDataDeque.remove(loadingData);
                    Optional.ofNullable(loadingData.getTimeouThread()).ifPresent(t->t.interrupt());
                    showGlassQueue.remove(loadId);
                    if(hiddenDeque.isEmpty()){
                        Iterator<Future> iterator = loadingTextVector.iterator();
                        while (iterator.hasNext()){
                            iterator.next().cancel(true);
                            iterator.remove();
                        }
                        statePanel.loadingLabel.setVisible(false);
                        statePanel.loadingLabel.setText(LoadingData.defaultTime);
                        if(!release){
                            log.debug("隐藏loading:{}",loadId);
                        }
                    }
                    if(showGlassQueue.isEmpty() && statePanel.loadingGlassPane.isVisible()){
   /*                     if(delayHiddenMs > 0){
                            try {
                                TimeUnit.MILLISECONDS.sleep(delayHiddenMs);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }*/
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
