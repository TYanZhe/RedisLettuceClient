package cn.org.tpeach.nosql.view;

import cn.org.tpeach.nosql.constant.I18nKey;
import cn.org.tpeach.nosql.constant.PublicConstant;
import cn.org.tpeach.nosql.framework.LarkFrame;
import cn.org.tpeach.nosql.redis.service.IRedisConfigService;
import cn.org.tpeach.nosql.service.ServiceProxy;
import cn.org.tpeach.nosql.tools.CollectionUtils;
import cn.org.tpeach.nosql.tools.SwingTools;
import cn.org.tpeach.nosql.view.component.RButton;
import cn.org.tpeach.nosql.view.component.RTabbedPane;
import cn.org.tpeach.nosql.view.dialog.AboutDialog;
import cn.org.tpeach.nosql.view.dialog.MonitorDialog;
import cn.org.tpeach.nosql.view.menu.MenuManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class RToolBar extends JToolBar {

    private int jToolBarHeight;
    private AboutDialog aboutDialog;
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
                aboutDialog = new AboutDialog(null, null);
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
        monitorButton.addActionListener(e->{
            if(!monitorDialog.isVisible() && !monitorDialog.isOutCLose()){
                monitorDialog.setVisible(true);
            }
        });
        this.add(newButton);
        this.add(serverButton);
//        this.add(configButton);
        this.add(monitorButton);
        this.add(settingsButton);
//        jToolBar.add(helpButton);
        this.add(aboutButton);
        this.putClientProperty("JToolBar.isRollover", Boolean.TRUE);
    }

}
