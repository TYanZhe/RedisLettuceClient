package cn.org.tpeach.nosql.view;

import cn.org.tpeach.nosql.constant.I18nKey;
import cn.org.tpeach.nosql.constant.PublicConstant;
import cn.org.tpeach.nosql.enums.RedisStructure;
import cn.org.tpeach.nosql.exception.ServiceException;
import cn.org.tpeach.nosql.framework.LarkFrame;
import cn.org.tpeach.nosql.redis.bean.RedisConnectInfo;
import cn.org.tpeach.nosql.redis.command.connection.PingCommand;
import cn.org.tpeach.nosql.redis.command.string.SetnxString;
import cn.org.tpeach.nosql.redis.connection.RedisLarkPool;
import cn.org.tpeach.nosql.redis.service.IRedisConfigService;
import cn.org.tpeach.nosql.service.ServiceProxy;
import cn.org.tpeach.nosql.tools.StringUtils;
import cn.org.tpeach.nosql.tools.SwingTools;
import cn.org.tpeach.nosql.view.component.RButton;
import cn.org.tpeach.nosql.view.component.RTabbedPane;
import cn.org.tpeach.nosql.view.dialog.AboutDialog;
import cn.org.tpeach.nosql.view.dialog.MonitorDialog;
import cn.org.tpeach.nosql.view.dialog.SettingDialog;
import cn.org.tpeach.nosql.view.menu.MenuManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RToolBar extends JToolBar {

    private int jToolBarHeight;
    private AboutDialog aboutDialog;
    private SettingDialog settingDialog;
    private StatePanel statePanel;
    private RTabbedPane tabbedPane;
    private JTree redisTree;
    private List<RButton> rButtonList = new ArrayList<>();
    private MonitorDialog monitorDialog;
    private RButton selectButton;
    IRedisConfigService redisConfigService = ServiceProxy.getBeanProxy("redisConfigService", IRedisConfigService.class);

    public RToolBar(int jToolBarHeight, JTree redisTree, StatePanel statePanel, RTabbedPane tabbedPane, MonitorDialog monitorDialog) {
        this.jToolBarHeight = jToolBarHeight;
        this.redisTree = redisTree;
        this.statePanel = statePanel;
        this.tabbedPane = tabbedPane;
        this.monitorDialog = monitorDialog;
        intToolBar();
    }



    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        final Graphics2D graphics = (Graphics2D) g;
        //graphics.setBackground(new Color(255,255,255));
//                graphics.setColor(PublicConstant.RColor.toolBarGridBackground);
        graphics.setColor(Color.white);
        graphics.fillRect(0, 0, this.getWidth(), jToolBarHeight);

    }

    @Override
    protected JButton createActionComponent(Action a) {
        JButton jb = super.createActionComponent(a);
        jb.setOpaque(false);
        return jb;
    }

    private RButton getToolBarButton(String text, Icon icon) {
        RButton button = new RButton() {
            @Override
            public void init() {
                if(selectButton != this){
                    setBackground(Color.WHITE);
                }

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(new Color(229, 243, 255));

            }
        };
//        button.addActionListener(e->{
//            selectButton = button;
//            if(CollectionUtils.isNotEmpty(rButtonList)){
//                for (RButton rButton : rButtonList) {
//                    if(rButton != button){
//                        rButton.setBackground(Color.WHITE);
//                    }
//                }
//            }
//            button.setBackground(new Color(229, 243, 255));
//
//        });
        button.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        button.setPreferredSize(new Dimension(58, 50));
        button.setMinimumSize(new Dimension(58, 50));
        button.setMaximumSize(new Dimension(58, 50));
        button.setText(text);
        button.setIcon(icon);
//        button.setFont(new Font("comic sans MS",Font.PLAIN,14));
        button.setFont(new Font("黑体", Font.PLAIN, 14));
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setFocusPainted(false);
        return button;
    }

    private void intToolBar() {
        //是否需要绘制边框
        this.setBorderPainted(true);
        //// 设置工具栏边缘和其内部工具组件之间的边距（内边距）
        this.setMargin(new Insets(0, 0, 0, 0));
//        jToolBar.add(new JLabel(" Font: "));
        RButton newButton = getToolBarButton(LarkFrame.getI18nText(I18nKey.RedisResource.NEW), PublicConstant.Image.atom);
        RButton serverButton = getToolBarButton(LarkFrame.getI18nText(I18nKey.RedisResource.SERVER), PublicConstant.Image.server);
//        RButton configButton = getToolBarButton(LarkFrame.getI18nText(I18nKey.RedisResource.CONFIG), PublicConstant.Image.config);
        RButton monitorButton = getToolBarButton(LarkFrame.getI18nText(I18nKey.RedisResource.MONITOR), PublicConstant.Image.monitor);
        RButton settingsButton = getToolBarButton(LarkFrame.getI18nText(I18nKey.RedisResource.SETTING), PublicConstant.Image.settings);
        RButton helpButton = getToolBarButton(LarkFrame.getI18nText(I18nKey.RedisResource.HELP), PublicConstant.Image.help);
        RButton aboutButton = getToolBarButton(LarkFrame.getI18nText(I18nKey.RedisResource.ABOUT), PublicConstant.Image.about);
        RButton testBatch = getToolBarButton("TestBatch", PublicConstant.Image.refresh);
        rButtonList.add(newButton);
        rButtonList.add(serverButton);
        rButtonList.add(monitorButton);
        rButtonList.add(settingsButton);
        rButtonList.add(aboutButton);
        if(redisTree != null){
            newButton.addActionListener(e -> MenuManager.getInstance().newConnectConfig(redisTree));
        }

        aboutButton.addActionListener(e->{
            if(aboutDialog == null){
                aboutDialog = new AboutDialog();
            }
            aboutDialog.open();
        });
        serverButton.addActionListener(e->{
            if(statePanel.getCurrentRedisItem() == null){
                SwingTools.showMessageInfoDialog(null,"请选择一个服务","提示");
                return;
            }
            MenuManager.getInstance().addServerInfoToTab(tabbedPane,statePanel);

        });
        settingsButton.addActionListener(e->{
            if(settingDialog == null){
                settingDialog = new SettingDialog();
            }
            settingDialog.open();
        });
        monitorButton.addActionListener(e->{
            if(!monitorDialog.isVisible() && !monitorDialog.isOutCLose()){
                if(statePanel.getCurrentRedisItem() == null){
                    SwingTools.showMessageInfoDialog(null,"请选择一个服务","提示");
                    return;
                }
                if(monitorDialog.isInit()){
                    monitorDialog.setRedisTreeItem(statePanel.getCurrentRedisItem());
                    monitorDialog.setVisible(true);
                }else{
                    monitorDialog.monitorInit(statePanel.getCurrentRedisItem());
                    if(monitorDialog.isInit()){
                        monitorDialog.setVisible(true);
                    }
                }

            }
        });
        aboutButton.addActionListener(e->{
            if(aboutDialog == null){
                aboutDialog = new AboutDialog();
            }
            aboutDialog.open();
        });
        testBatch.addActionListener(e->{
            StatePanel.showLoading(()->testBatchString());
        });
        this.add(newButton);
        this.add(serverButton);
//        this.add(configButton);
        this.add(monitorButton);
        this.add(settingsButton);
//        jToolBar.add(helpButton);

        if(PublicConstant.ProjectEnvironment.TEST.equals(LarkFrame.getProjectEnv())){
            this.add(testBatch);
        }else if(PublicConstant.ProjectEnvironment.RELEASE.equals(LarkFrame.getProjectEnv())|| PublicConstant.ProjectEnvironment.BETA.equals(LarkFrame.getProjectEnv())){
            this.add(aboutButton);
        }
        this.putClientProperty("JToolBar.isRollover", Boolean.TRUE);
    }

    public void testBatchString(){
        LinkedList<String> ids = new LinkedList<>();
        for (int i = 0; i < 50; i++) {
            ids.add(StringUtils.getUUID());
        }
        for (String s : ids) {
            //构建连接信息
            RedisConnectInfo conn = new RedisConnectInfo();
            conn.setId(s);
//            conn.setStructure(RedisStructure.CLUSTER.getCode());
//            conn.setHost("127.0.0.1:7000,127.0.0.1:7001,127.0.0.1:7002,127.0.0.1:7003,127.0.0.1:7004,127.0.0.1:7005");
            conn.setStructure(RedisStructure.SINGLE.getCode());
            conn.setHost("127.0.0.1");
            conn.setPort(6379);
            RedisLarkPool.addOrUpdateConnectInfo(conn);
            String ping = new PingCommand(s).execute();
            if(!"PONG".equals(ping)){
                throw new ServiceException("Ping命令执行失败");
            }
        }
        int num = 1000000 /50;
        ExecutorService executorService = Executors.newCachedThreadPool();
        CountDownLatch countDownLatch = new CountDownLatch(50);
        for(int x = 0;x<50;x++){
            int finalX = x;
            String remove = ids.removeFirst();
            executorService.execute(()->{
                try {
                    for (int i = finalX * num; i < (finalX + 1) * num; i++) {
                        try {
                            SetnxString setnxString = new SetnxString(remove, 5, ("测试很珍惜" + i).getBytes("UTF-8"), (i + "").getBytes("UTF-8"));
                            setnxString.setPrintLog(false);
                            setnxString.execute();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    LarkFrame.larkLog.info(LocalDateTime.now(),remove+"执行完毕");
                }finally {
                    countDownLatch.countDown();
                }
            });

        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
