package cn.org.tpeach.nosql.view.dialog;

import cn.org.tpeach.nosql.constant.I18nKey;
import cn.org.tpeach.nosql.constant.PublicConstant;
import cn.org.tpeach.nosql.controller.BaseController;
import cn.org.tpeach.nosql.controller.ResultRes;
import cn.org.tpeach.nosql.exception.ServiceException;
import cn.org.tpeach.nosql.framework.LarkFrame;
import cn.org.tpeach.nosql.redis.bean.RedisConnectInfo;
import cn.org.tpeach.nosql.redis.service.IRedisConfigService;
import cn.org.tpeach.nosql.redis.service.IRedisConnectService;
import cn.org.tpeach.nosql.service.ServiceProxy;
import cn.org.tpeach.nosql.tools.StringUtils;
import cn.org.tpeach.nosql.tools.SwingTools;
import cn.org.tpeach.nosql.view.component.PlaceHolderPasswordField;
import cn.org.tpeach.nosql.view.component.PlaceholderTextField;
import cn.org.tpeach.nosql.view.component.RButton;
import cn.org.tpeach.nosql.view.ui.ServerTabbedPaneUI;
import io.lettuce.core.RedisURI;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.TimeUnit;

/**
 * @author tyz
 * @Title: AddRedisServerDialog
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-05 21:11
 * @since 1.0.0
 */
public class AddRedisServerDialog extends AbstractRowDialog<RedisConnectInfo, RedisConnectInfo> {
    /**
     *
     */
    private static final long serialVersionUID = -500667826077225469L;
    private boolean isEdit = false;

    //连接地址在panel中的小标
    private int hostIndex = 2;
    private boolean isCluster = false;
    //组件
    private JPanel panel;
    private JCheckBox structurecheckBox, showPassCheckBox;
    private JLabel structureLable, nameLable, hostLable, authLable;
    private PlaceholderTextField nameField, hostField, portField, timeOutField, dbAmountField, nameSpaceSepartorField;
    private PlaceHolderPasswordField authField;
    private RedisConnectInfo connectInfo;

    IRedisConfigService redisConfigService = ServiceProxy.getBeanProxy("redisConfigService", IRedisConfigService.class);
    IRedisConnectService redisConnectService = ServiceProxy.getBeanProxy("redisConnectService", IRedisConnectService.class);


    public AddRedisServerDialog(JFrame parent, Image icon, RedisConnectInfo connectInfo) {
        super(parent, icon, connectInfo);
        initDialog(connectInfo);
    }

    public AddRedisServerDialog(JFrame parent, RedisConnectInfo connectInfo) {
        super(parent, connectInfo);
        initDialog(connectInfo);
    }


    @Override
    public void initDialog(RedisConnectInfo connectInfo) {
        if (connectInfo != null) {
            if (StringUtils.isBlank(connectInfo.getId())) {
                this.isError = true;
                SwingTools.showMessageErrorDialog(this, LarkFrame.getI18nFirstUpText(I18nKey.RedisResource.UN_KNOW, I18nKey.RedisResource.EXCEPTION));
            }
            this.isCluster = connectInfo.getStructure() == 0 ? false : true;
        }
        this.connectInfo = connectInfo;
        this.isEdit = connectInfo == null ? false : true;
        this.setTitle(this.isEdit ? LarkFrame.getI18nFirstUpAllText(I18nKey.RedisResource.MENU_EDIT, I18nKey.RedisResource.CONNECT) : LarkFrame.getI18nFirstUpAllText(I18nKey.RedisResource.ADD, I18nKey.RedisResource.CONNECT));
        this.setRowHeight(28);
    }


    @Override
    protected void contextUiImpl(JPanel contextPanel, JPanel btnPanel) {
//    	this.rowHeight = LarkFrame.fm.getHeight() > 35 ? LarkFrame.fm.getHeight() : rowHeight;
//    	this.setMinimumSize(new Dimension(1000, 50));
        super.contextUiImpl(contextPanel, btnPanel);
        //https://www.ibm.com/developerworks/cn/java/j-lo-boxlayout/
        // 创建选项卡面板
        final JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setUI(new ServerTabbedPaneUI("#FFFFFF", "#000000"));
        panel = new JPanel();
        panel.setBackground(getPanelBgColor());
        structureLable = new JLabel(LarkFrame.getI18nFirstUpAllText(I18nKey.RedisResource.CLUSTER) + " :", JLabel.RIGHT);
        nameLable = new JLabel(LarkFrame.getI18nFirstUpAllText(I18nKey.RedisResource.CONNECT, I18nKey.RedisResource.NAME) + " :", JLabel.RIGHT);
        hostLable = new JLabel(LarkFrame.getI18nFirstUpAllText(I18nKey.RedisResource.CONNECT, I18nKey.RedisResource.ADDRESS) + " :", JLabel.RIGHT);
        authLable = new JLabel(LarkFrame.getI18nFirstUpAllText(I18nKey.RedisResource.CONNECT, I18nKey.RedisResource.PASSWORD) + " :", JLabel.RIGHT);

        structurecheckBox = new JCheckBox(LarkFrame.getI18nFirstUpAllText(I18nKey.RedisResource.ENABLED), isCluster);
        structurecheckBox.setBackground(getPanelBgColor());
        nameField = new PlaceholderTextField(20);
        nameField.setPlaceholder(LarkFrame.getI18nFirstUpAllText(I18nKey.RedisResource.CONNECT, I18nKey.RedisResource.NAME));
        nameField.resetHeightSize(LarkFrame.fm.getHeight() - 5);
        hostField = new PlaceholderTextField(null);
        hostField.resetHeightSize(LarkFrame.fm.getHeight() - 5);
        portField = new PlaceholderTextField("6379", 4);
        portField.resetHeightSize(LarkFrame.fm.getHeight() - 5);
        portField.setPlaceholder(LarkFrame.getI18nFirstUpAllText(I18nKey.RedisResource.CONNECT, I18nKey.RedisResource.PORT));
        authField = new PlaceHolderPasswordField(20);
        authField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_C && e.isControlDown()){
                    char echoChar = authField.getEchoChar();
                    if('•' != echoChar){
                        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
                        Transferable tText = new StringSelection(authField.getSelectedText());
                        clip.setContents(tText, null);
                    }
                }
            }
        });
        authField.setPlaceholder(LarkFrame.getI18nFirstUpAllText(I18nKey.RedisResource.CONNECT, I18nKey.RedisResource.PASSWORD));
        authField.resetHeightSize(LarkFrame.fm.getHeight() - 5);

        timeOutField = new PlaceholderTextField(20);
        dbAmountField = new PlaceholderTextField(20);
        nameSpaceSepartorField = new PlaceholderTextField(20);


        showPassCheckBox = new JCheckBox("show password");
        showPassCheckBox.setBackground(getPanelBgColor());
        showPassCheckBox.setPreferredSize(new Dimension(28, rowHeight));
        showPassCheckBox.setMaximumSize(new Dimension(28, rowHeight));
        showPassCheckBox.setMinimumSize(new Dimension(28, rowHeight));


        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(Box.createVerticalStrut(15));
        panel.add(createRow(panel, structureLable, structurecheckBox, rowHeight, 0.25));
        panel.add(Box.createVerticalStrut(15));
        panel.add(createRow(panel, nameLable, nameField, rowHeight, 0.25));
        panel.add(Box.createVerticalStrut(15));
        panel.add(createRow(panel, hostLable, createCompoundRow(hostField, portField, 0.9), rowHeight, 0.25));
        panel.add(Box.createVerticalStrut(15));
        panel.add(createRow(panel, authLable, createCompoundRow(authField, showPassCheckBox, 0.25), rowHeight, 0.25));
//        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1,2,2,2,Color.GRAY),BorderFactory.createEmptyBorder(5,5,5,5)));
//        tabbedPane.addTab("Server", null, panel, false);

        JPanel otherInfoPanel = new JPanel();
        otherInfoPanel.setBackground(getPanelBgColor());
        otherInfoPanel.setLayout(new BoxLayout(otherInfoPanel, BoxLayout.Y_AXIS));
        otherInfoPanel.add(Box.createVerticalStrut(15));
        otherInfoPanel.add(createRow(otherInfoPanel, new JLabel("NameSpace Separator : ", JLabel.RIGHT), nameSpaceSepartorField, rowHeight, 0.36));
        otherInfoPanel.add(Box.createVerticalStrut(15));
        otherInfoPanel.add(createRow(otherInfoPanel, new JLabel("Database Discovery Limit :", JLabel.RIGHT), dbAmountField, rowHeight, 0.36));
        otherInfoPanel.add(Box.createVerticalStrut(15));
        otherInfoPanel.add(createRow(otherInfoPanel, new JLabel("Execution Timeout(Sec) : ", JLabel.RIGHT), timeOutField, rowHeight, 0.36));

        tabbedPane.addTab("Server", panel);
        tabbedPane.addTab("Advanced Setting", otherInfoPanel);
        contextPanel.setLayout(new BorderLayout());
        contextPanel.add(tabbedPane, BorderLayout.CENTER);
        if (connectInfo != null) {
            nameField.setText(connectInfo.getName());
            hostField.setText(connectInfo.getHost());
            portField.setText(connectInfo.getPort() + "");
            authField.setText(connectInfo.getAuth());
            timeOutField.setText(connectInfo.getTimeout() + "");
            dbAmountField.setText(connectInfo.getDbAmount() + "");
            nameSpaceSepartorField.setText(connectInfo.getNameSpaceSepartor());
        } else {
            timeOutField.setText(RedisURI.DEFAULT_TIMEOUT + "");
            dbAmountField.setText(RedisConnectInfo.DEFAULT_DBAMOUNT + "");
            nameSpaceSepartorField.setText(PublicConstant.NAMESPACE_SPLIT);
        }
        //监听事件
        judgCluster(structurecheckBox);
        structurecheckBox.addItemListener(e -> judgCluster((JCheckBox) e.getSource()));
        showPassCheckBox.addItemListener(e -> {
            final boolean selected = showPassCheckBox.isSelected();
            if (selected) {
                authField.setEchoChar('\0');
            } else {
                authField.setEchoChar('•');
            }

        });
    }


    /**
     * 是否是集群
     *
     * @param chectBox
     */
    private void judgCluster(JCheckBox chectBox) {
        final boolean selected = chectBox.isSelected();
        //集群
        if (selected) {
            portField.setVisible(false);
            hostField.setPlaceholder("ip1:port1,ip2:port2,ip3:port3...");
        } else {
            portField.setVisible(true);
            hostField.setPlaceholder(LarkFrame.getI18nFirstUpAllText(I18nKey.RedisResource.CONNECT, I18nKey.RedisResource.ADDRESS));
        }
        panel.updateUI();


    }


    @Override
    public Box addBtnToBtnPanel(JPanel btnPanel) {
        String testText = LarkFrame.getI18nUpText(I18nKey.RedisResource.TEST);
        JButton testBtn = new RButton(testText);
        testBtn.setBackground(new Color(108, 117, 125));
        testBtn.setForeground(Color.WHITE);
        btnbox.add(testBtn);
        btnbox.add(btnHorizontalStrut());
        testBtn.addActionListener(e -> {

            testBtn.setEnabled(false);
            testBtn.setText(LarkFrame.getI18nUpText(I18nKey.RedisResource.CONNECTING));
            LarkFrame.executorService.execute(() -> {
                try {
                    RedisConnectInfo item = validForm();
                    ResultRes<Boolean> dispatcher = BaseController.dispatcher(() -> redisConnectService.connectTest(item));
                    if (dispatcher.isRet()) {
                        testBtn.setBackground(new Color(132, 181, 71));
                        testBtn.setText(LarkFrame.getI18nUpText(I18nKey.RedisResource.SUCCESS));
                    } else {
                        testBtn.setBackground(Color.RED);
                        testBtn.setText(LarkFrame.getI18nUpText(I18nKey.RedisResource.FAIL));
                        LarkFrame.executorService.schedule(() -> testBtn.setText(LarkFrame.getI18nUpText(I18nKey.RedisResource.RETRY)), 2, TimeUnit.SECONDS);
                        SwingTools.showMessageErrorDialog(this, dispatcher.getMsg());
                    }
                } catch (ServiceException ex) {
                    testBtn.setText(testText);
                    SwingTools.showMessageErrorDialog(this, ex.getMessage());
                } finally {
                    testBtn.setEnabled(true);
                }
            });


        });
        return super.addBtnToBtnPanel(btnPanel);
    }

    private RedisConnectInfo validForm() {
        if (consumer == null) {
            throw new ServiceException("未绑定回调事件");
        }

//        private PlaceholderTextField nameField,hostField,portField,authField;
        final boolean selected = structurecheckBox.isSelected();
        String host = hostField.getText();
        String auth = new String(authField.getPassword());
        String name = nameField.getText();
        //校验
        if (StringUtils.isBlank(name)) {
            throw new ServiceException("请输入连接名");
        } else if (StringUtils.isBlank(host)) {
            throw new ServiceException("请输入连接地址");
        }
        if(StringUtils.isBlank(timeOutField.getText())) {
            timeOutField.setText(RedisURI.DEFAULT_TIMEOUT + "");
        }
        if(StringUtils.isBlank(dbAmountField.getText())) {
            dbAmountField.setText(RedisConnectInfo.DEFAULT_DBAMOUNT + "");
        }
        if(StringUtils.isBlank(nameSpaceSepartorField.getText())) {
            nameSpaceSepartorField.setText(PublicConstant.NAMESPACE_SPLIT);
        }
        int structure = selected ? 1 : 0;
        int port = -1;
        String id = null;
        if (connectInfo != null) {
            id = connectInfo.getId();
        }
        if (!selected) {
            try {
                port = Integer.valueOf(portField.getText());
            } catch (NumberFormatException ex) {
                throw new ServiceException("请输入正确的端口");
            }
        }
        return new RedisConnectInfo((short) 0, id, structure, name, host, port, auth, Long.parseLong(timeOutField.getText()), Integer.parseInt(dbAmountField.getText()), nameSpaceSepartorField.getText());
    }

    @Override
    protected void submit(ActionEvent e) {
        super.submit(okBtn, () -> {
            try {
                RedisConnectInfo item = validForm();
                ResultRes<RedisConnectInfo> resultRes;
                if (connectInfo == null) {
                    resultRes = BaseController.dispatcher(() -> redisConfigService.addRedisConfig(item));
                } else {
                    item.setId(connectInfo.getId());
                    resultRes = BaseController.dispatcher(() -> redisConfigService.updateRedisConfig(item));
                }
                if (resultRes.isRet()) {
                    consumer.accept(item);
                    this.dispose();
                    return false;
                } else {
                    SwingTools.showMessageErrorDialog(this, "未知错误：添加配置失败");
                }
            } catch (ServiceException ex) {
                SwingTools.showMessageErrorDialog(this, ex.getMessage());
            }
            return true;
        });
    }

    @Override
    public Component btnHorizontalStrut() {
        return Box.createHorizontalStrut(10);
    }

    @Override
    public void after() {
        super.after();
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

    }

    @Override
    public boolean isNeedBtn() {
        return true;
    }


}
