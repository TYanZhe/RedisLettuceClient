/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.org.tpeach.nosql.view;

import cn.org.tpeach.nosql.constant.ConfigConstant;
import cn.org.tpeach.nosql.constant.I18nKey;
import cn.org.tpeach.nosql.constant.PublicConstant;
import cn.org.tpeach.nosql.controller.BaseController;
import cn.org.tpeach.nosql.controller.ResultRes;
import cn.org.tpeach.nosql.enums.RedisType;
import cn.org.tpeach.nosql.framework.LarkFrame;
import cn.org.tpeach.nosql.redis.bean.RedisConnectInfo;
import cn.org.tpeach.nosql.redis.bean.RedisTreeItem;
import cn.org.tpeach.nosql.redis.service.IRedisConfigService;
import cn.org.tpeach.nosql.redis.service.IRedisConnectService;
import cn.org.tpeach.nosql.service.ServiceProxy;
import cn.org.tpeach.nosql.tools.CollectionUtils;
import cn.org.tpeach.nosql.tools.ConfigParser;
import cn.org.tpeach.nosql.tools.StringUtils;
import cn.org.tpeach.nosql.tools.SwingTools;
import cn.org.tpeach.nosql.view.common.ServiceManager;
import cn.org.tpeach.nosql.view.component.*;
import cn.org.tpeach.nosql.view.dialog.AboutDialog;
import cn.org.tpeach.nosql.view.jtree.RTreeNode;
import cn.org.tpeach.nosql.view.jtree.RedisTreeModel;
import cn.org.tpeach.nosql.view.jtree.RedisTreeRenderer;
import cn.org.tpeach.nosql.view.menu.MenuManager;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.util.Enumeration;
import java.util.List;

/**
 * https://docs.oracle.com/javase/7/docs/api/javax/swing/plaf/basic/package-summary.html
 *
 * @author smart
 */
public class RedisMainWindow extends javax.swing.JFrame {

    private static final long serialVersionUID = 4087854446377861533L;
    private final int INIT_WIDTH = 1425;
    private final int INIT_HEIGHT = 890;

    private RedisTreeRenderer redisTreeRenderer = new RedisTreeRenderer();
    //初始化宽度占总屏幕宽度百分比
    private float SIZE_PERCENT = 0.60F;
    //窗口宽度
    public int width;
    //窗口高度
    public int height;
    public int treePanelWidth = 309;
    int jToolBarHeight = 58;

    int dataPanelHeight;
    final int logPanelheight = 145;
    final int statePanelheight = 28;

//    final double dataBgDividerLocation = 0.8;
    private double treeDataDividerLocation = 0.2;

    private AboutDialog aboutDialog;
    //--------------------------服务相关开始--------------------
    IRedisConfigService redisConfigService = ServiceProxy.getBeanProxy("redisConfigService", IRedisConfigService.class);
    IRedisConnectService redisConnectService = ServiceProxy.getBeanProxy("redisConnectService", IRedisConnectService.class);
    MenuManager menuManager = MenuManager.getInstance();
    ServiceManager serviceManager = ServiceManager.getInstance();

    //--------------------------服务相关结束--------------------
    private void initUiManager() {
        /**
         * 设置用于获取 Popup 的 PopupFactory。
         */
        PopupFactory.setSharedInstance(new NonRectanglePopupFactory());
        //https://www.cnblogs.com/weisuoc/p/uimanager-properties-list.html
        UIManager.put("Tree.collapsedIcon", PublicConstant.Image.arrow_right_blue);
        UIManager.put("Tree.expandedIcon", PublicConstant.Image.arrow_down_blue);
        UIManager.put("MenuItem.acceleratorFont", new java.awt.Font("宋体", 0, 14));
        UIManager.put("MenuItem.acceleratorForeground", Color.black);
        UIManager.put("MenuItem.acceleratorDelimiter", "+");
//        UIManager.put("MenuItem.arrowIcon",PublicConstant.Image.arrow_right_gray_20);
        UIManager.put("Menu.arrowIcon", PublicConstant.Image.arrow_right_blue);
        UIManager.put("MenuItem.selectionBackground", PublicConstant.RColor.menuItemSelectionBackground);
//        UIManager.put("MenuItem.selectionForeground",new Color(156,206,248));
        UIManager.put("ScrollBarUI", RScrollBarUI.class.getName());
        UIManager.put("ScrollBar.width" ,10);
        UIManager.put("Menu.arrowIcon" ,"javax.swing.plaf.nimbus.NimbusIcon");
//        Insets insets = UIManager.getInsets("TabbedPane.contentBorderInsets");
//        insets.top = -1;
//        insets.bottom = -1;
//        insets.right = -1;
//        insets.left = -1;
//        UIManager.put("TabbedPane.contentBorderInsets", insets);
        //文本框不可编辑背景颜色
        UIManager.put("TextField.inactiveBackground", new ColorUIResource(Color.WHITE));
        UIManager.put("TabbedPane.tabAreaBackground", Color.RED);// Tab区域的背景色
//        UIManager.getDefaults().put("TabbedPane.contentBorderInsets", new Insets(0,0,0,0));
//        UIManager.getDefaults().put("TabbedPane.tabsOverlapBorder", true);
        //改变系统默认字体，需放在视图代码最前面
        Font font = new Font("Dialog", Font.PLAIN, ConfigParser.getInstance().getInt(ConfigConstant.Section.FONT, ConfigConstant.FONTSIZE, 12));
        Enumeration<Object> keys = UIManager.getDefaults().keys();

        LarkFrame.fm = sun.font.FontDesignMetrics.getMetrics(font);
        SwingTools.initGlobalFont(font);
        //UIManager.getDefaults().put("TabbedPane.tabRunOverlay", 0);
//        UIManager.put("TabbedPane.contentAreaColor", new ColorUIResource(Color.GREEN));
//        UIManager.put("TabbedPane.focus", new ColorUIResource(Color.ORANGE));
//        UIManager.put("TabbedPane.selected", new ColorUIResource(Color.YELLOW));
//        UIManager.put("TabbedPane.darkShadow", new ColorUIResource(Color.DARK_GRAY));
//        UIManager.put("TabbedPane.borderHightlightColor", new ColorUIResource(Color.LIGHT_GRAY));
//        UIManager.put("TabbedPane.light", new ColorUIResource(Color.WHITE));
//        UIManager.put("TabbedPane.tabAreaBackground", new ColorUIResource(Color.CYAN));
//        UIManager.put("TabbedPane.background",Color.CYAN);
//
//	      //Selected tab border color
//	      UIManager.put( "TabbedPane.selectHighlight", Color.DARK_GRAY );
//	
//	      //I don't know
//	      UIManager.put("TabbedPane.tabareabackground", Color.red );
//	      UIManager.put( "TabbedPane.tabForeground", new Color(255,0,0) );
//	      UIManager.put( "TabbedPane.tabBackground", Color.black );
        /*          UIManager.put("ToolTip.background", Color.WHITE);
            UIManager.put("ToolTip.border", new BorderUIResource(new LineBorder(
                Color.BLACK)));*/
    }

    /**
     * Creates new form RedisMainWindow
     */
    public RedisMainWindow() {
        //获取屏幕大小
        Dimension screenSize = SwingTools.getScreenSize();
        width = (int) (screenSize.width * SIZE_PERCENT);
        height = INIT_HEIGHT * width / INIT_WIDTH;
        treePanelWidth = width * treePanelWidth / INIT_WIDTH;
//        System.out.println("计算后的宽度：" + width + ",高度：" + height + ">>>>>treePanelWidth:" + treePanelWidth);
        initUiManager();
        this.setIconImage(PublicConstant.Image.logo.getImage());
        initComponents();
        ((PlaceholderTextField) keyFilterField).setPlaceholder("请输入检索键的表达式");
        keyFilterField.setText("*");
        redisTreeRenderer.setKeyFilterField(keyFilterField);
        //去掉树线条
        redisTree.putClientProperty("JTree.lineStyle", "None");

        ((RTabbedPane) redisDataTabbedPane).addTab(LarkFrame.getI18nUpText(I18nKey.RedisResource.HOME), PublicConstant.Image.home, new HomeTabbedPanel(), false);

        // 居中
        this.setLocationRelativeTo(null);
        this.addWindowStateListener(new WindowStateListener() {
            @Override
            public void windowStateChanged(WindowEvent state) {

                if (state.getNewState() == 1 || state.getNewState() == 7) {
//                	treeDataDividerLocation = MathUtils.getBigDecimal(MathUtils.divide(treePanel.getWidth(), contentPane.getWidth())).setScale(3,RoundingMode.UP).doubleValue();
//                	System.out.println("窗口最小化"+contentPane.getWidth()+">>>"+treePanel.getWidth()+">>>"+redisTree.getHeight());

                } else if (state.getNewState() == 0) {
//                	treeDataDividerLocation = MathUtils.getBigDecimal(MathUtils.divide(treePanel.getWidth(), contentPane.getWidth())).setScale(3,RoundingMode.UP).doubleValue();
//                    System.out.println("窗口恢复到初始状态"+contentPane.getWidth()+">>>"+treePanel.getWidth()+">>>"+redisTree.getHeight());
                    //   dataBgSplitPane1.setDividerLocation(centerSplitScale);
                } else if (state.getNewState() == 6) {
//                 	treeDataDividerLocation = MathUtils.getBigDecimal(MathUtils.divide(treePanel.getWidth(), contentPane.getWidth())).setScale(3,RoundingMode.UP).doubleValue();
//                    System.out.println("窗口最大化"+contentPane.getWidth()+">>>"+treePanel.getWidth()+">>>"+redisTree.getHeight());
                    //  dataBgSplitPane1.setDividerLocation(centerSplitScale);

                }
            }
        });
        //设置内容与日志间隔  最大化保持日志panel高度不变 
        dataBgSplitPane.setDividerLocation(height - logPanelheight);
        contentPane.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                int dataBgDividerLocation = contentPane.getHeight() - logPanelheight;
                dataBgSplitPane.setDividerLocation(dataBgDividerLocation);
//				  System.out.println(dataBgDividerLocation);
//				  jSplitPane.setDividerLocation(treeDataDividerLocation);
            }

        });
        JPanel jPanel = new JPanel();
        jPanel.setBackground(Color.WHITE);
        ((RTabbedPane) logTabbedPane).addTab(LarkFrame.getI18nText(I18nKey.RedisResource.LOG), PublicConstant.Image.logo_16, jPanel, false);
        initTree();
        intToolBar();
        jSplitPane.setDividerLocation(treePanelWidth);
        this.setVisible(true);

    }

    //------------------------------------------------------toolbar start-------------------------------------------
    private RButton getToolBarButton(String text, Icon icon) {
        RButton button = new RButton() {
            @Override
            public void init() {
                setBackground(Color.WHITE);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(new Color(229, 243, 255));

            }
        };
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
        return button;
    }

    private void intToolBar() {
        //是否需要绘制边框
        jToolBar.setBorderPainted(true);
        //// 设置工具栏边缘和其内部工具组件之间的边距（内边距）
        jToolBar.setMargin(new Insets(0, 0, 0, 0));
//        jToolBar.add(new JLabel(" Font: "));
        RButton newButton = getToolBarButton(LarkFrame.getI18nText(I18nKey.RedisResource.NEW), PublicConstant.Image.atom);
        RButton serverButton = getToolBarButton(LarkFrame.getI18nText(I18nKey.RedisResource.SERVER), PublicConstant.Image.server);
        RButton configButton = getToolBarButton(LarkFrame.getI18nText(I18nKey.RedisResource.CONFIG), PublicConstant.Image.config);
        RButton monitorButton = getToolBarButton(LarkFrame.getI18nText(I18nKey.RedisResource.MONITOR), PublicConstant.Image.monitor);
        RButton settingsButton = getToolBarButton(LarkFrame.getI18nText(I18nKey.RedisResource.SETTING), PublicConstant.Image.settings);
        RButton helpButton = getToolBarButton(LarkFrame.getI18nText(I18nKey.RedisResource.HELP), PublicConstant.Image.help);
        RButton aboutButton = getToolBarButton(LarkFrame.getI18nText(I18nKey.RedisResource.ABOUT), PublicConstant.Image.about);
        newButton.addActionListener(e -> menuManager.newConnectConfig(redisTree));
        aboutButton.addActionListener(e->{
            if(aboutDialog == null){
                aboutDialog = new AboutDialog(this, null);
            }
            aboutDialog.open();
        });
        jToolBar.add(newButton);
        jToolBar.add(serverButton);
        jToolBar.add(configButton);
        jToolBar.add(monitorButton);
        jToolBar.add(settingsButton);
//        jToolBar.add(helpButton);
        jToolBar.add(aboutButton);
        jToolBar.putClientProperty("JToolBar.isRollover", Boolean.TRUE);
    }

    private JToolBar getToolBar() {
        JToolBar bar = new JToolBar() {

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
        };

        return bar;
    }
    //------------------------------------------------------toolbar end-------------------------------------------

    //------------------------------------------------------tree start-------------------------------------------
    private void initTree() {
//               //鼠标点击事件
//        SwingTools.addMouseClickedListener(redisTree, (e) -> {
//
//        });
    }

    private TreeModel getTreeModel() {
        final RTreeNode root = new RTreeNode("Root");
        //加载配置
        ResultRes<List<RedisConnectInfo>> resultRes = BaseController.dispatcher(() -> redisConfigService.getRedisConfigAllList());
        if (resultRes.isRet()) {
            List<RedisConnectInfo> redisConfigAllList = resultRes.getData();
            if (CollectionUtils.isNotEmpty(redisConfigAllList)) {
                redisConfigAllList.stream().forEach(item -> SwingTools.addServerTreeNode(root, item.getId(), item.getName(), item.getName()));
            }
        } else {
            //TODO 国际化
            SwingTools.showMessageErrorDialog(null, resultRes.getMsg(), "获取配置失败");
        }
        return new RedisTreeModel(root);
    }
    private void redisTreeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_redisTreeMouseClicked
        int x = evt.getX();
        int y = evt.getY();
        // 获取点击所在树节点路径
        TreePath pathForLocation = redisTree.getPathForLocation(x, y);
//            Point p = evt.getPoint();
//            int row = redisTree.getRowForLocation(p.x, p.y);
//	    TreePath path = redisTree.getPathForRow(row);

        JPopupMenu connectPopMenu = menuManager.getConnectPopMenu(redisTree);
        JPopupMenu serverTreePopMenu = menuManager.getServerTreePopMenu(redisTree,(RTabbedPane) redisDataTabbedPane);
        JPopupMenu dbTreePopMenu = menuManager.getDBTreePopMenu(redisTree,keyFilterField);
        JPopupMenu keyTreePopMenu = menuManager.getKeyTreePopMenu(redisTree, (RTabbedPane) redisDataTabbedPane);
        JPopupMenu keyNameSpaceTreePopMenu = menuManager.getKeyNameSpaceTreePopMenu(redisTree);
        // JTree上没有任何项被选中
        if (pathForLocation == null) {
            if (evt.getButton() == MouseEvent.BUTTON3) {
                LarkFrame.executorService.execute(() -> connectPopMenu.show(treePanel, x, y));
            }
            return;
        }
        Object[] path = pathForLocation.getPath();
        redisTree.setSelectionPath(pathForLocation);
        RTreeNode treeNode = (RTreeNode) redisTree.getLastSelectedPathComponent();
        int childCount = treeNode.getChildCount();
        if (!treeNode.isEnabled()) {
            return;
        }
        //双击连接名称进行连接 如果已经有子节点 则展开 缩放
        if (evt.getModifiers() == InputEvent.BUTTON1_MASK && evt.getClickCount() == 2 && path.length == 2) {

            doubleClickTreeNode(evt);
        } else if (evt.getButton() == MouseEvent.BUTTON1) {//左键

            clickLeftTreeNode(evt);
        } else if (evt.getButton() == MouseEvent.BUTTON3) {//右键
            if (path.length == 2) {
                serverTreePopMenu.show(redisTree, x, y);
                if(childCount > 0 ){
                	if(childCount == 1 && RedisType.LOADING.equals(((RedisTreeItem)((RTreeNode) treeNode.getChildAt(0)).getUserObject()).getType())) {
                        serverTreePopMenu.getComponent(0).setEnabled(false);
                        serverTreePopMenu.getComponent(1).setEnabled(false);
                        serverTreePopMenu.getComponent(2).setEnabled(false);
                        serverTreePopMenu.getComponent(3).setEnabled(false);
                        serverTreePopMenu.getComponent(4).setEnabled(false);
                        serverTreePopMenu.getComponent(5).setEnabled(false);
                        serverTreePopMenu.getComponent(6).setEnabled(false);
                	}else {
                        //连接
                        serverTreePopMenu.getComponent(0).setEnabled(false);
                        //断开连接
                        serverTreePopMenu.getComponent(1).setEnabled(true);
                        //重新加载
                        serverTreePopMenu.getComponent(3).setEnabled(true);
                	}
        
                }else{
                    serverTreePopMenu.getComponent(0).setEnabled(true);
                    serverTreePopMenu.getComponent(1).setEnabled(false);
                    serverTreePopMenu.getComponent(3).setEnabled(false);
                }

            } else if (path.length == 3) {
                dbTreePopMenu.show(redisTree, x, y);
                if(childCount > 0){
                    //重新加载
                    dbTreePopMenu.getComponent(2).setEnabled(true);
                }else{
                    dbTreePopMenu.getComponent(2).setEnabled(false);
                }
            } else if (path.length >= 4) {
                RedisTreeItem item = (RedisTreeItem) treeNode.getUserObject();
                if (item.getType().equals(RedisType.KEY)) {
                    keyTreePopMenu.show(redisTree, x, y);
                } else if (item.getType().equals(RedisType.KEY_NAMESPACE)) {
                    keyNameSpaceTreePopMenu.show(redisTree, x, y);
                }

            }
        }
    }//GEN-LAST:event_redisTreeMouseClicked

    private void redisTreeValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_redisTreeValueChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_redisTreeValueChanged

    private void redisTreeMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_redisTreeMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_redisTreeMousePressed

    private void redisTreeMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_redisTreeMouseMoved
        int x = (int) evt.getPoint().getX();
        int y = (int) evt.getPoint().getY();
        redisTree.getComponentAt(x, y).repaint();
        RedisTreeRenderer.mouseRow = redisTree.getRowForLocation(x, y);
//                redisTree.repaint();

    }//GEN-LAST:event_redisTreeMouseMoved
    private void doubleClickTreeNode(MouseEvent evt) {
        RTreeNode treeNode = (RTreeNode) redisTree.getLastSelectedPathComponent();
        if (!treeNode.isEnabled()) {
            return;
        }
        final int childCount = treeNode.getChildCount();
        //判断是否已经加载过
        if (childCount > 0) {
            return;
        }
        final TreeNode[] path = treeNode.getPath();
        if (null != treeNode) {
            Object userObject = treeNode.getUserObject();

            if (null != userObject && userObject instanceof RedisTreeItem) {
                RedisTreeItem redisTreeItem = (RedisTreeItem) userObject;
                final String id = redisTreeItem.getId();
                switch (redisTreeItem.getType()) {
                    case SERVER:
                        // 连接 查询redis数据库
                        LarkFrame.executorService.execute(() -> serviceManager.openConnectRedisTree(treeNode, redisTreeItem, redisTree));
                        break;
                    case DATABASE:

                        break;
                    case KEY:

                        break;
                    default:
                }
            }
        }
    }

    /**
     * 鼠标左键点击树
     */
    private void clickLeftTreeNode(MouseEvent evt) {
        RTreeNode treeNode = (RTreeNode) redisTree.getLastSelectedPathComponent();
        final int childCount = treeNode.getChildCount();
        //判断是否已经加载过
        if (childCount > 0) {
            return;
        }
        final TreeNode[] path = treeNode.getPath();
        if (null != treeNode) {
            Object userObject = treeNode.getUserObject();
            if (null != userObject && userObject instanceof RedisTreeItem) {
                final RedisTreeItem redisTreeItem = (RedisTreeItem) userObject;
                switch (redisTreeItem.getType()) {
                    case SERVER:
                        LarkFrame.executorService.execute(() -> serviceManager.openConnectRedisTree(treeNode, redisTreeItem, redisTree));
                        break;
                    case DATABASE:
                        serviceManager.openDbRedisTree(treeNode, redisTreeItem, redisTree,keyFilterField);
                        break;
                    case KEY:
                        RTreeNode node = (RTreeNode) redisTree.getLastSelectedPathComponent(); // 获得右键选中的节点
//            			RedisTreeItem item = (RedisTreeItem) node.getUserObject();
//            			redisDataTabbedPane.add(item.getKey(),new RedisTabbedPanel(node));
//            			redisDataTabbedPane.setSelectedIndex(redisDataTabbedPane.getTabCount()-1);
                        menuManager.replaceTabbedPane(redisTree, (RTabbedPane) redisDataTabbedPane, node);
                        break;
                    default:
                }
            }
        }

    }

    //------------------------------------------------------tree end-------------------------------------------
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        contentPane = new javax.swing.JPanel();
        jSplitPane = new javax.swing.JSplitPane();
        treePanel = new javax.swing.JPanel();
        treeScroll = new EasyJSP();
        redisTree = new javax.swing.JTree();
        KeysFilterPannel = new javax.swing.JPanel();
        keyFilterField = new PlaceholderTextField(20);
        dataBgPanel = new javax.swing.JPanel();
        dataBgSplitPane = new javax.swing.JSplitPane();
        dataPanel = new javax.swing.JPanel();
        redisDataTabbedPane = new RTabbedPane();
        logPanel = new javax.swing.JPanel();
        logTabbedPane = new RTabbedPane();
        statePanel = new javax.swing.JPanel();
        connectStateLabel = new javax.swing.JLabel();
        jToolBar = getToolBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        contentPane.setBackground(new java.awt.Color(255, 255, 255));
        contentPane.setPreferredSize(new Dimension(width,height));
        contentPane.setLayout(new java.awt.BorderLayout());

        jSplitPane.setDividerSize(1);

        treePanel.setBackground(new java.awt.Color(23, 35, 55));
        treePanel.setMinimumSize(new java.awt.Dimension(150, 27));
        treePanel.setName(""); // NOI18N
        treePanel.setPreferredSize(new Dimension(treePanelWidth,height));
        treePanel.setLayout(new java.awt.BorderLayout());

        treeScroll.setBackground(new java.awt.Color(255, 255, 255));

        redisTree.setModel(getTreeModel());
        redisTree.setToolTipText("");
        redisTree.setAlignmentX(0.8F);
        redisTree.setAlignmentY(0.8F);
        redisTree.setCellRenderer(new RedisTreeRenderer());
        redisTree.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        redisTree.setRootVisible(false);
        redisTree.setRowHeight(28);
        redisTree.setShowsRootHandles(true);
        redisTree.setToggleClickCount(1);
        redisTree.setVisibleRowCount(24);
        redisTree.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                redisTreeMouseMoved(evt);
            }
        });
        redisTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                redisTreeMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                redisTreeMousePressed(evt);
            }
        });
        redisTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                redisTreeValueChanged(evt);
            }
        });
        treeScroll.setViewportView(redisTree);

        treePanel.add(treeScroll, java.awt.BorderLayout.CENTER);

        KeysFilterPannel.setBackground(new java.awt.Color(255, 51, 51));
        KeysFilterPannel.setMinimumSize(new java.awt.Dimension(6, 100));
        KeysFilterPannel.setLayout(new javax.swing.BoxLayout(KeysFilterPannel, javax.swing.BoxLayout.X_AXIS));

        keyFilterField.setMinimumSize(new java.awt.Dimension(6, 28));
        keyFilterField.setPreferredSize(new java.awt.Dimension(94, 28));
        keyFilterField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyFilterFieldActionPerformed(evt);
            }
        });
        KeysFilterPannel.add(keyFilterField);

        treePanel.add(KeysFilterPannel, java.awt.BorderLayout.PAGE_START);

        jSplitPane.setLeftComponent(treePanel);

        dataBgPanel.setBackground(new java.awt.Color(255, 255, 255));
        dataBgPanel.setLayout(new java.awt.BorderLayout());

        dataBgSplitPane.setDividerSize(1);
        dataBgSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        dataPanel.setBackground(new java.awt.Color(255, 255, 255));
        dataPanel.setMinimumSize(new java.awt.Dimension(0, 0));
        dataPanel.setName(""); // NOI18N
        dataPanel.setPreferredSize(new Dimension(width-treePanelWidth,height));
        dataPanel.setLayout(new java.awt.BorderLayout());

        redisDataTabbedPane.setBackground(new java.awt.Color(255, 255, 255));
        dataPanel.add(redisDataTabbedPane, java.awt.BorderLayout.CENTER);

        dataBgSplitPane.setLeftComponent(dataPanel);

        logPanel.setPreferredSize(new java.awt.Dimension(width-treePanelWidth,logPanelheight));
        logPanel.setRequestFocusEnabled(false);
        logPanel.setLayout(new java.awt.BorderLayout());

        logTabbedPane.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        logPanel.add(logTabbedPane, java.awt.BorderLayout.CENTER);

        dataBgSplitPane.setRightComponent(logPanel);

        dataBgPanel.add(dataBgSplitPane, java.awt.BorderLayout.CENTER);

        jSplitPane.setRightComponent(dataBgPanel);

        contentPane.add(jSplitPane, java.awt.BorderLayout.CENTER);

        getContentPane().add(contentPane, java.awt.BorderLayout.CENTER);

        statePanel.setBackground(PublicConstant.RColor.statePanelColor);
        statePanel.setPreferredSize(new java.awt.Dimension(width,statePanelheight));

        connectStateLabel.setForeground(new java.awt.Color(255, 0, 51));
        connectStateLabel.setText("   未连接到服务");

        javax.swing.GroupLayout statePanelLayout = new javax.swing.GroupLayout(statePanel);
        statePanel.setLayout(statePanelLayout);
        statePanelLayout.setHorizontalGroup(
            statePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statePanelLayout.createSequentialGroup()
                .addComponent(connectStateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 1055, Short.MAX_VALUE))
        );
        statePanelLayout.setVerticalGroup(
            statePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(connectStateLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        getContentPane().add(statePanel, java.awt.BorderLayout.SOUTH);

        jToolBar.setBackground(new java.awt.Color(255, 255, 255));
        jToolBar.setBorder(null);
        jToolBar.setFloatable(false);
        jToolBar.setForeground(new java.awt.Color(255, 255, 255));
        jToolBar.setRollover(true);
        jToolBar.setToolTipText("");
        jToolBar.setMinimumSize(new Dimension(0,jToolBarHeight));
        jToolBar.setName(""); // NOI18N
        jToolBar.setOpaque(false);
        jToolBar.setPreferredSize(new java.awt.Dimension(13, jToolBarHeight));
        jToolBar.setRequestFocusEnabled(false);
        getContentPane().add(jToolBar, java.awt.BorderLayout.PAGE_START);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void keyFilterFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_keyFilterFieldActionPerformed
        // TODO add your handling code here:
        String keyPattern = keyFilterField.getText();
//        if (StringUtils.isNotBlank(keyPattern)) {
//            redisTree.updateUI();
//        }
    }//GEN-LAST:event_keyFilterFieldActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel KeysFilterPannel;
    private javax.swing.JLabel connectStateLabel;
    public static javax.swing.JPanel contentPane;
    private javax.swing.JPanel dataBgPanel;
    private javax.swing.JSplitPane dataBgSplitPane;
    private javax.swing.JPanel dataPanel;
    private javax.swing.JSplitPane jSplitPane;
    private javax.swing.JToolBar jToolBar;
    private javax.swing.JTextField keyFilterField;
    private javax.swing.JPanel logPanel;
    private javax.swing.JTabbedPane logTabbedPane;
    private javax.swing.JTabbedPane redisDataTabbedPane;
    private javax.swing.JTree redisTree;
    private javax.swing.JPanel statePanel;
    public static javax.swing.JPanel treePanel;
    private javax.swing.JScrollPane treeScroll;
    // End of variables declaration//GEN-END:variables
}
