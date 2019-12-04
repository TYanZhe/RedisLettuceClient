package cn.org.tpeach.nosql.view;

import cn.org.tpeach.nosql.constant.I18nKey;
import cn.org.tpeach.nosql.constant.PublicConstant;
import cn.org.tpeach.nosql.enums.RedisStructure;
import cn.org.tpeach.nosql.exception.ServiceException;
import cn.org.tpeach.nosql.framework.LarkFrame;
import cn.org.tpeach.nosql.redis.bean.RedisConnectInfo;
import cn.org.tpeach.nosql.redis.command.connection.PingCommand;
import cn.org.tpeach.nosql.redis.command.hash.HmSetHash;
import cn.org.tpeach.nosql.redis.command.list.LpushList;
import cn.org.tpeach.nosql.redis.command.set.SAddSet;
import cn.org.tpeach.nosql.redis.command.string.SetnxString;
import cn.org.tpeach.nosql.redis.command.zset.ZmAddSet;
import cn.org.tpeach.nosql.redis.connection.RedisLarkPool;
import cn.org.tpeach.nosql.redis.service.IRedisConfigService;
import cn.org.tpeach.nosql.redis.service.IRedisConnectService;
import cn.org.tpeach.nosql.service.ServiceProxy;
import cn.org.tpeach.nosql.tools.DateUtils;
import cn.org.tpeach.nosql.tools.StringUtils;
import cn.org.tpeach.nosql.tools.SwingTools;
import cn.org.tpeach.nosql.view.component.RButton;
import cn.org.tpeach.nosql.view.component.RTabbedPane;
import cn.org.tpeach.nosql.view.dialog.AboutDialog;
import cn.org.tpeach.nosql.view.dialog.MonitorDialog;
import cn.org.tpeach.nosql.view.dialog.SettingDialog;
import cn.org.tpeach.nosql.view.menu.JRedisPopupMenu;
import cn.org.tpeach.nosql.view.menu.MenuManager;
import io.lettuce.core.ScoredValue;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.util.List;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.function.BiConsumer;

/**
 * @author tyz
 */
public class RToolBar extends JToolBar {
	private static final long serialVersionUID = -8860399993054494603L;
	private int jToolBarHeight;
    private AboutDialog aboutDialog;
    private SettingDialog settingDialog;
    private StatePanel statePanel;
    private RTabbedPane tabbedPane;
    private JTree redisTree;
    private List<RButton> rButtonList = new ArrayList<>();
    private MonitorDialog monitorDialog;
    @Getter
    private RButton toolButton,selectButton;
    @Getter
    @Setter
    private boolean testMenuShow;

    MenuManager menuManager = MenuManager.getInstance();
    IRedisConfigService redisConfigService = ServiceProxy.getBeanProxy("redisConfigService", IRedisConfigService.class);
    IRedisConnectService redisConnectService = ServiceProxy.getBeanProxy("redisConnectService", IRedisConnectService.class);
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
        @SuppressWarnings("serial")
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

    @SuppressWarnings("unused")
	private void intToolBar() {
        //是否需要绘制边框
        this.setBorderPainted(true);
        //// 设置工具栏边缘和其内部工具组件之间的边距（内边距）
        this.setMargin(new Insets(0, 0, 0, 0));
//        jToolBar.add(new JLabel(" Font: "));
        RButton newButton = getToolBarButton(LarkFrame.getI18nText(I18nKey.RedisResource.NEW), PublicConstant.Image.getImageIcon(PublicConstant.Image.atom));
        RButton serverButton = getToolBarButton(LarkFrame.getI18nText(I18nKey.RedisResource.SERVER), PublicConstant.Image.getImageIcon(PublicConstant.Image.server));
//        RButton configButton = getToolBarButton(LarkFrame.getI18nText(I18nKey.RedisResource.CONFIG), PublicConstant.Image.config);
        RButton monitorButton = getToolBarButton(LarkFrame.getI18nText(I18nKey.RedisResource.MONITOR), PublicConstant.Image.getImageIcon(PublicConstant.Image.monitor));
        RButton settingsButton = getToolBarButton(LarkFrame.getI18nText(I18nKey.RedisResource.SETTING), PublicConstant.Image.getImageIcon(PublicConstant.Image.settings));
        RButton helpButton = getToolBarButton(LarkFrame.getI18nText(I18nKey.RedisResource.HELP), PublicConstant.Image.getImageIcon(PublicConstant.Image.help));
        RButton aboutButton = getToolBarButton(LarkFrame.getI18nText(I18nKey.RedisResource.ABOUT), PublicConstant.Image.getImageIcon(PublicConstant.Image.about));
        toolButton = getToolBarButton("工具", PublicConstant.Image.getImageIcon(PublicConstant.Image.tool));
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
        SwingTools.addMouseClickedListener(toolButton,e->tooleMenu(e));
        this.add(newButton);
        this.add(serverButton);
//        this.add(configButton);
        this.add(monitorButton);
        this.add(settingsButton);
//        jToolBar.add(helpButton);
        this.add(toolButton);
        boolean textToolBarShow = true;
        switch (LarkFrame.getProjectEnv()){
            case PublicConstant.ProjectEnvironment.BETA:
//                textToolBarShow = false;
                break;
            case PublicConstant.ProjectEnvironment.TEST:
                this.setTestMenuShow(true);
                break;
            case PublicConstant.ProjectEnvironment.RELEASE:
            case PublicConstant.ProjectEnvironment.DEV:
                this.setTestMenuShow(true);
                this.add(aboutButton);
                break;
            default:
                break;

        }
        toolButton.setVisible(textToolBarShow);
        this.putClientProperty("JToolBar.isRollover", Boolean.TRUE);
    }
    private void tooleMenu(MouseEvent event){
        JPopupMenu popMenu = new JRedisPopupMenu();
        if(testMenuShow){
            JMenuItem batchItem = menuManager.getJMenuItem("  批量插入  ", PublicConstant.Image.getImageIcon(PublicConstant.Image.batchImport));
            batchItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.CTRL_MASK));
            batchItem.addActionListener(e -> testBatchString());
            popMenu.add(batchItem);
        }
        if(!LarkFrame.builtInJre){
            JMenuItem toolItem = menuManager.getJMenuItem("  常用工具集合  ", PublicConstant.Image.getImageIcon(PublicConstant.Image.tool_web));
            toolItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_MASK));
            toolItem.addActionListener(e -> addJBrowerPanelToTab(tabbedPane));
            popMenu.add(toolItem);
        }
        popMenu.show((Component) event.getSource(),event.getX()+10,event.getY() );
    }
    public void addJBrowerPanelToTab(RTabbedPane tabbedPane) {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            Component componect = tabbedPane.getComponentAt(i);
            if (componect instanceof JBrowerPanel) {
                tabbedPane.setSelectedIndex(i);
                return;
            }
        }

        tabbedPane.addTab("工具 " ,  PublicConstant.Image.getImageIcon(PublicConstant.Image.tool_web),  JBrowerPanel.getInstance() , false);
    }
    @SuppressWarnings("unchecked")
	private void testBatchString(){
        try {
            String string = SwingTools.showInputDialog(null, "请输入批量操作参数(类型[string,zset,set,hash,list],key(可不填,string忽略),db,数量,线程数,Host,Password)",
                    "批量插入测试数据", "string,,0,1000,1,127.0.0.1:6379,");
            if (string == null) {
                return;
            }
            String[] split = string.split(",");
            if (!("string".equals(split[0]) || "zset".equals(split[0]) || "hash".equals(split[0]) || "set".equals(split[0]) || "list".equals(split[0]))) {
                throw new ServiceException(string);
            }
            String keyInput = split[1] ;
            int db = Integer.valueOf(split[2]);
            if(db < 0){
                throw new ServiceException("数据库下标不正确");
            }
            int totalNum = Integer.valueOf(split[3]);
            int threadCount = Integer.valueOf(split[4]);
            String[] split1 = split[5].split(":");
            String host =  split1[0];
            String password =  null;
            if(split.length >= 7){
                password = split[6];
            }
            if(StringUtils.isBlank(host)){
                throw new ServiceException(string);
            }
            int port = Integer.valueOf(split1[1]);
            RedisConnectInfo redisConnectInfo = new RedisConnectInfo();
            //            conn.setStructure(RedisStructure.CLUSTER.getCode());
            //            conn.setHost("127.0.0.1:7000,127.0.0.1:7001,127.0.0.1:7002,127.0.0.1:7003,127.0.0.1:7004,127.0.0.1:7005");
            redisConnectInfo.setStructure(RedisStructure.SINGLE.getCode());
            redisConnectInfo.setHost(host);
            redisConnectInfo.setPort(port);
            redisConnectInfo.setAuth(password);
            boolean b = redisConnectService.connectTest(redisConnectInfo);
            if(!b){
                throw new ServiceException("连接失败");
            }

            //校验通过
            CountDownLatch countDownLatch = new CountDownLatch(threadCount);
            StatePanel.showLoading(true,() -> {
                //连接
                LinkedList<String> ids = new LinkedList<>();
                for (int i = 0; i < threadCount; i++) {
                    ids.add(StringUtils.getUUID());
                }
                for (String s : ids) {
                    RedisConnectInfo conn = new RedisConnectInfo();
                    conn.setId(s);
                    //            conn.setStructure(RedisStructure.CLUSTER.getCode());
                    //            conn.setHost("127.0.0.1:7000,127.0.0.1:7001,127.0.0.1:7002,127.0.0.1:7003,127.0.0.1:7004,127.0.0.1:7005");
                    conn.setStructure(RedisStructure.SINGLE.getCode());
                    conn.setHost(host);
                    conn.setPort(port);
                    RedisLarkPool.addOrUpdateConnectInfo(conn);
                    String ping = new PingCommand(s).execute();
                    if (!"PONG".equals(ping)) {
                        throw new ServiceException("Ping命令执行失败");
                    }
                }


                String format = DateUtils.format(new Date(), "yyyyMMddHHmmss");
                byte[] key ;
                switch (split[0]) {
                    case "string":
                        excuteBatch(countDownLatch, threadCount,ids, totalNum, (id, indexs) -> {
                            try {
                                indexs.forEach(index->{
                                    SetnxString setnxString = new SetnxString(id, db, StringUtils.strToByte("Test_String_" + index + "_" + format),StringUtils.strToByte(StringUtils.getUUID()));
                                    setnxString.setPrintLog(false);
                                    setnxString.execute();
                                });
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        });
                        break;
                    case "zset":
                        key = StringUtils.isNotBlank(keyInput) ? StringUtils.strToByte(keyInput) : StringUtils.strToByte("Test_Zset_" + format);
                        excuteBatch(countDownLatch,threadCount, ids, totalNum, (id, indexs) -> {
                            try {
                                ScoredValue<byte[]>[] scoreValues = new ScoredValue[indexs.size()];
                                for (int i = 0; i < indexs.size(); i++) {
                                    scoreValues[i] = ScoredValue.fromNullable(indexs.get(i), StringUtils.strToByte("测试zset_" + indexs.get(i) + "_" + format));
                                }
                                new ZmAddSet(id, db, key, scoreValues).setPrintLog(false).execute();
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        });
                        break;
                    case "hash":
                        key = StringUtils.isNotBlank(keyInput) ? StringUtils.strToByte(keyInput) :StringUtils.strToByte("Test_Hash_" + format);
                        excuteBatch(countDownLatch, threadCount,ids, totalNum, (id, indexs) -> {
                            try {
                                Map<byte[], byte[]> hash = new HashMap<>();
                                indexs.forEach(index->{
                                    hash.put(StringUtils.strToByte("测试Hash_" + index + "_" + format), StringUtils.strToByte(StringUtils.getUUID()));
                                });
                                new HmSetHash(id, db, key, hash).setPrintLog(false).execute();
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        });
                        break;
                    case "list":
                        key = StringUtils.isNotBlank(keyInput) ? StringUtils.strToByte(keyInput) :StringUtils.strToByte("Test_List_" + format);
                        excuteBatch(countDownLatch, threadCount,ids, totalNum, (id, indexs) -> {
                            try {
                                byte[][] value = new byte[indexs.size()][];
                                indexs.forEach(index->{
                                    value[index] = StringUtils.strToByte("测试List_" +index +"_"+ format);
                                });
                                new LpushList(id, db, key, value).setPrintLog(false).execute();
                            } catch (Exception e) {

                                e.printStackTrace();
                            }
                        });
                        break;
                    case "set":
                        key = StringUtils.isNotBlank(keyInput) ? StringUtils.strToByte(keyInput) :StringUtils.strToByte("Test_Set_" + format);
                        excuteBatch(countDownLatch,threadCount, ids, totalNum, (id, indexs) -> {
                            try {
                                byte[][] value = new byte[indexs.size()][];
                                indexs.forEach(index->{
                                    value[index] = StringUtils.strToByte("测试List_" +index +"_"+ format);
                                });
                                new SAddSet(id, db, key, value).setPrintLog(false).execute();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        });
                        break;
                    default:

                        break;
                }
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            },false,true,-1,(s,t)->{
                SwingTools.showMessageMessageDialog(LarkFrame.frame,"批量插入成功,耗时："+t+"s","提示");
            });
        }catch (Exception e){
            SwingTools.showMessageErrorDialog(null, "参数有误，未执行任何操作:\n"+ServiceProxy.getStackTrace(e));
        }
    }


    private void excuteBatch(CountDownLatch countDownLatch, int threadCount,LinkedList<String> connectIds, int totalNum, BiConsumer<String,List<Integer>> consumer ) {

        int num = totalNum /threadCount;
        for (int x = 0; x < threadCount; x++) {
            int finalX = x;
            String id = connectIds.removeFirst();
            LarkFrame.executorService.execute(() -> {
                try {
                    List<Integer> list = new ArrayList<>();
                    for (int i = finalX * num; i < (finalX + 1) * num; i++) {
                        list.add(i);
                        if(list.size()> 1000){
                            consumer.accept(id,new ArrayList<>(list));
                            list.clear();
                        }
                    }
                    if(list.size() >0){
                        consumer.accept(id,list);
                        list.clear();
                    }
                    LarkFrame.larkLog.info(LocalDateTime.now(), id + "执行完毕:"+countDownLatch.getCount());

                } finally {
                    RedisLarkPool.destory(id);
                    RedisLarkPool.deleteConnectInfo(id);
                    countDownLatch.countDown();
                }
            });
        }
    }

}
