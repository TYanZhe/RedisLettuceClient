/**
 *
 */
package cn.org.tpeach.nosql.view.menu;

import cn.org.tpeach.nosql.bean.PageBean;
import cn.org.tpeach.nosql.constant.I18nKey;
import cn.org.tpeach.nosql.constant.PublicConstant;
import cn.org.tpeach.nosql.controller.BaseController;
import cn.org.tpeach.nosql.controller.ResultRes;
import cn.org.tpeach.nosql.enums.RedisStructure;
import cn.org.tpeach.nosql.framework.LarkFrame;
import cn.org.tpeach.nosql.redis.bean.RedisConnectInfo;
import cn.org.tpeach.nosql.redis.bean.RedisTreeItem;
import cn.org.tpeach.nosql.redis.connection.RedisLarkPool;
import cn.org.tpeach.nosql.redis.service.IRedisConfigService;
import cn.org.tpeach.nosql.redis.service.IRedisConnectService;
import cn.org.tpeach.nosql.service.ServiceProxy;
import cn.org.tpeach.nosql.tools.*;
import cn.org.tpeach.nosql.view.*;
import cn.org.tpeach.nosql.view.common.ServiceManager;
import cn.org.tpeach.nosql.view.component.RTabbedPane;
import cn.org.tpeach.nosql.view.dialog.AddRedisKeyRowDialog;
import cn.org.tpeach.nosql.view.dialog.AddRedisServerDialog;
import cn.org.tpeach.nosql.view.dialog.DeleteRedisKeyDialog;
import cn.org.tpeach.nosql.view.dialog.TextAreaDialog;
import cn.org.tpeach.nosql.view.jtree.RTreeNode;
import io.lettuce.core.KeyScanCursor;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.List;
import java.util.*;
import java.util.function.Consumer;

/**
 * <p>
 * Title: MenuManager.java
 * </p>
 *
 * @author taoyz
 * @date 2019年8月22日
 * @version 1.0
 */
@Slf4j
public enum MenuManager {
    /**实例*/
    INSTANCE;

    public static MenuManager getInstance() {
        return MenuManager.INSTANCE;
    }

    IRedisConfigService redisConfigService = ServiceProxy.getBeanProxy("redisConfigService", IRedisConfigService.class);
    IRedisConnectService redisConnectService = ServiceProxy.getBeanProxy("redisConnectService", IRedisConnectService.class);

    public void putPopMenuItem(JPopupMenu menu,JComponent... items){
        if(items != null && items.length > 0){
            for (JComponent item : items) {
                if(item !=null){
                    menu.add(item);
                }
            }
        }
    }
    public JPopupMenu getConnectPopMenu(JComponent componet) {
        JPopupMenu popMenu = new JRedisPopupMenu();// 菜单
        if (componet instanceof JTree) {
            JTree tree = (JTree) componet;
            JMenuItem addConnectItem = getJMenuItem(I18nKey.RedisResource.NEW, PublicConstant.Image.getImageIcon(PublicConstant.Image.redis_server));
            addConnectItem.addActionListener(e -> newConnectConfig(tree));
            putPopMenuItem(popMenu,addConnectItem);
        }

        return popMenu;
    }

    public void newConnectConfig(JTree tree) {
        AddRedisServerDialog d = new AddRedisServerDialog(LarkFrame.frame, null);
        d.getResult((item) -> {
            //刷新树
            RedisLarkPool.addOrUpdateConnectInfo(item);
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            SwingTools.addServerTreeNode((RTreeNode) model.getRoot(), item.getId(), item.getName(), item.getName());
            tree.expandPath(new TreePath(((RTreeNode) model.getRoot()).getPath()));
            tree.updateUI();
        });
        d.open();
    }
    public JMenuItem getJMenuItemByMenuName(JPopupMenu popMenu,String text){
        if(popMenu == null || text == null){
            return null;
        }
        int componentCount = popMenu.getComponentCount();
        for (int i = 0; i < componentCount; i++) {
            Component component = popMenu.getComponent(i);
            if(component instanceof JMenuItem){
                JMenuItem item = (JMenuItem) component;
                if(text.equals(item.getText())) {
                    return item;
                }
            }
        }
        return null;
    }

    public JPopupMenu getServerTreePopMenu(JComponent componet, RTabbedPane topTabbedPane, StatePanel statePanel,RTabbedPane logTabbedPane) {
        JPopupMenu popMenu = new JRedisPopupMenu();// 菜单
        if (componet instanceof JTree) {
            JTree tree = (JTree) componet;
            JMenuItem openItem = getJMenuItem(I18nKey.RedisResource.CONNECT, PublicConstant.Image.getImageIcon(PublicConstant.Image.connect));
            //编辑
            JMenuItem editItem = getJMenuItem(I18nKey.RedisResource.MENU_EDIT, PublicConstant.Image.getImageIcon(PublicConstant.Image.edit));
            //删除
            JMenuItem delItem = getJMenuItem(I18nKey.RedisResource.MENU_DEL, PublicConstant.Image.getImageIcon(PublicConstant.Image.delete));
            delItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, java.awt.event.InputEvent.CTRL_MASK));
            //控制台
            JMenuItem consoleItem = getJMenuItem(I18nKey.RedisResource.MENU_CONSOLE, PublicConstant.Image.getImageIcon(PublicConstant.Image.cmd_console,14,14));
            consoleItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_MASK));
            //关闭连接
            JMenuItem disConnectItem = getJMenuItem(I18nKey.RedisResource.MENU_DISCONNECT, PublicConstant.Image.getImageIcon(PublicConstant.Image.disconnect));
            disConnectItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
            //重新加载
            JMenuItem reloadItem = getJMenuItem(I18nKey.RedisResource.MENU_RELOAD, PublicConstant.Image.getImageIcon(PublicConstant.Image.menu_refresh));
            //服务信息
            JMenuItem serverInfoItem = getJMenuItem(I18nKey.RedisResource.SERVERINFO, PublicConstant.Image.getImageIcon(PublicConstant.Image.server16,16,16));
            reloadItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
            //编辑
            JMenuItem activeItem = getJMenuItem(I18nKey.RedisResource.ACTIVATE,PublicConstant.Image.getImageIcon(PublicConstant.Image.active_data));
            JMenuItem copyItem = getJMenuItem(I18nKey.RedisResource.COPY, PublicConstant.Image.getImageIcon(PublicConstant.Image.copy));
            JMenuItem pubsubItem = getJMenuItem(I18nKey.RedisResource.PUB_SUB, PublicConstant.Image.getImageIcon(PublicConstant.Image.subscribe));
            openItem.addActionListener(e -> {
                RTreeNode node = (RTreeNode) tree.getLastSelectedPathComponent(); // 获得右键选中的节点
                RedisTreeItem redisTreeItem = (RedisTreeItem) node.getUserObject();
                 ServiceManager.getInstance().openConnectRedisTree(statePanel, node, redisTreeItem, tree);
            });
            editItem.addActionListener(e-> editConnectInfo(tree, topTabbedPane, statePanel));
            delItem.addActionListener(e -> {
                int conform = SwingTools.showConfirmDialogYNC(null, "是否确认删除？", "删除确认");
                if (conform == JOptionPane.YES_OPTION) {
                    RTreeNode node = (RTreeNode) tree.getLastSelectedPathComponent(); // 获得右键选中的节点
                    if (node.isRoot()) {
                        return;
                    }
                    RedisTreeItem redisTreeItem = (RedisTreeItem) node.getUserObject();
                    final ResultRes<RedisConnectInfo> resultRes = BaseController.dispatcher(() -> redisConfigService.deleteRedisConfigById(redisTreeItem.getId()));
                    if (resultRes.isRet()) {
                        disConnect(tree, topTabbedPane, statePanel);
                        ((DefaultTreeModel) tree.getModel()).removeNodeFromParent(node);
                        Optional.ofNullable(ServiceManager.getInstance().findoOriginalRedisTreeNode(tree, node)).ifPresent(r -> r.removeFromParent());
                    } else {
                        SwingTools.showMessageErrorDialog(tree, "删除失败：" + resultRes.getMsg());
                    }

                }
            });

            disConnectItem.addActionListener(e -> disConnect(tree, topTabbedPane, statePanel));
            reloadItem.addActionListener(e -> {
                SwingTools.swingWorkerExec(() -> {
                    RTreeNode node = (RTreeNode) tree.getLastSelectedPathComponent(); // 获得右键选中的节点
                    RedisTreeItem redisTreeItem = (RedisTreeItem) node.getUserObject();
                    RedisLarkPool.destory(redisTreeItem.getId());
                    node.removeAllChildren();
                    ServiceManager.getInstance().openConnectRedisTree(statePanel, node, redisTreeItem, tree);
                });
            });
            serverInfoItem.addActionListener(e -> {
                RTreeNode node = (RTreeNode) tree.getLastSelectedPathComponent(); // 获得右键选中的节点
                RedisTreeItem redisTreeItem = (RedisTreeItem) node.getUserObject();
                statePanel.doUpdateStatus(redisTreeItem);
                addServerInfoToTab(topTabbedPane, statePanel);
            });
            activeItem.addActionListener(e -> {
                RTreeNode node = (RTreeNode) tree.getLastSelectedPathComponent(); // 获得右键选中的节点
                RedisTreeItem redisTreeItem = (RedisTreeItem) node.getUserObject();
                tree.setSelectionPath(new TreePath(node.getPath()));
                statePanel.doUpdateStatus(redisTreeItem);
            });
            copyItem.addActionListener(e -> {
                RTreeNode node = (RTreeNode) tree.getLastSelectedPathComponent(); // 获得右键选中的节点
                RedisTreeItem redisTreeItem = (RedisTreeItem) node.getUserObject();
                ResultRes<RedisConnectInfo> res = BaseController.dispatcher(() -> redisConfigService.getRedisConfigById(redisTreeItem.getId()));
                if (res.isRet()) {
                    RedisConnectInfo redisConnectInfo = res.getData();
                    redisConnectInfo.setId(null);
                    res = BaseController.dispatcher(() -> redisConfigService.addRedisConfig(redisConnectInfo));
                    if (res.isRet()) {
                        //刷新树
                        RedisConnectInfo item = res.getData();
                        RedisLarkPool.addOrUpdateConnectInfo(item);
                        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                        RTreeNode rTreeNode = SwingTools.addServerTreeNode((RTreeNode) model.getRoot(), item.getId(), item.getName(), item.getName());
                        if(!((RedisMainWindow) LarkFrame.frame).root.equals((RTreeNode) model.getRoot())){
                            ((RedisMainWindow) LarkFrame.frame).root.add(RTreeNode.copyNode(rTreeNode));
                         }
                        tree.expandPath(new TreePath(((RTreeNode) model.getRoot()).getPath()));
                        tree.updateUI();
                    }
                }
            });
            pubsubItem.addActionListener(e -> {
                RTreeNode node = (RTreeNode) tree.getLastSelectedPathComponent(); // 获得右键选中的节点
                RedisTreeItem item = (RedisTreeItem) node.getUserObject();

                for (int i = 0; i < logTabbedPane.getTabCount(); i++) {
                    Component componentAt = logTabbedPane.getComponentAt(i);
                    if(componentAt instanceof PubSubPanel){
                        PubSubPanel panel = (PubSubPanel) componentAt;
                        if(item.getId().equals(panel.getConnectId())){
                            topTabbedPane.setSelectedIndex(i);
                            return;
                        }
                    }
                }
                PubSubPanel pubSubPanel = new PubSubPanel( node);
//                if(pubSubPanel == null){
//                    System.out.println("pubSubPanel");
//                }
                topTabbedPane.addTab(item.getName(), PublicConstant.Image.getImageIcon(PublicConstant.Image.subscribe),pubSubPanel);
                topTabbedPane.setSelectedIndex(topTabbedPane.getTabCount()-1);

            });
            consoleItem.addActionListener(e -> openConsolePane(logTabbedPane, tree));
            //添加菜单需要修改cn.org.tpeach.nosql.view.RedisMainWindow.redisTreeMouseClicked 下标
            putPopMenuItem(popMenu,openItem,disConnectItem,pubsubItem,activeItem,serverInfoItem,editItem,reloadItem,consoleItem,delItem,copyItem);
        }


        return popMenu;
    }

    private void openConsolePane(RTabbedPane logTabbedPane, JTree tree) {
        int count = 0;
        for (int i = 0; i < logTabbedPane.getTabCount(); i++) {
            Component componentAt = logTabbedPane.getComponentAt(i);
            if(componentAt instanceof ConsolePanel){
                count++;
            }
        }
        if(count >= 8){
            SwingTools.showMessageErrorDialog(LarkFrame.frame,"Too many console(>8)" );
            return;
        }
        RTreeNode node = (RTreeNode) tree.getLastSelectedPathComponent();
        RedisTreeItem redisTreeItem = (RedisTreeItem) node.getUserObject();
        RedisConnectInfo connectInfo = redisConfigService.getRedisConfigById(redisTreeItem.getId());
        if(RedisStructure.getRedisStructure(connectInfo.getStructure()).equals(RedisStructure.SINGLE)){
            ConsolePanel consolePanel = new ConsolePanel(connectInfo,logTabbedPane);
            logTabbedPane.add(connectInfo.getName(), logTabbedPane.getTabCount(), PublicConstant.Image.getImageIcon(PublicConstant.Image.command), consolePanel);
            SwingTools.swingWorkerExec(()->consolePanel.start());
        }
    }

    private void editConnectInfo(JTree tree, RTabbedPane topTabbedPane, StatePanel statePanel) {
        //					tree.startEditingAtPath(tree.getSelectionPath());
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        RedisTreeItem redisTreeItem = (RedisTreeItem) node.getUserObject();
        //判断是否打开
        int childCount = node.getChildCount();
        boolean valid = true;
        if (childCount > 0) {
            valid = false;
            int conform = SwingTools.showConfirmDialogYNC(null, "连接已打开，编辑将会关闭连接，是否继续？", "编辑确认");
            if (conform == JOptionPane.YES_OPTION) {
                disConnect(tree, topTabbedPane, statePanel);
                //删除包含的标签
                int tabCount = topTabbedPane.getTabCount();
                for (int i = tabCount - 1; i >= 0; i--) {
                    Component component = topTabbedPane.getComponentAt(i);
                    if (component instanceof RedisTabbedPanel) {
                        RedisTabbedPanel tabPanel = (RedisTabbedPanel) component;

                        if (redisTreeItem.getId().equals(((RedisTreeItem) tabPanel.getTreeNode().getUserObject()).getId())) {
                            topTabbedPane.remove(i);
                        }
                    }
                }
                valid = true;
            }
        }
        if (valid) {
            final ResultRes<RedisConnectInfo> resultRes = BaseController.dispatcher(() -> redisConfigService.getRedisConfigById(redisTreeItem.getId()));
            if (resultRes.isRet()) {

                AddRedisServerDialog d = new AddRedisServerDialog(LarkFrame.frame, resultRes.getData());
                d.getResult((item) -> {
                    //销毁旧连接
                    RedisLarkPool.destory(redisTreeItem.getId());
                    redisTreeItem.setName(item.getName());
                    tree.updateUI();
                });
                d.open();
            } else {
                SwingTools.showMessageErrorDialog(tree, "获取配置失败");
            }
        }
    }

    public void addServerInfoToTab(RTabbedPane topTabbedPane, StatePanel statePanel) {
        boolean isExist = false;
        ServiceInfoPanel serviceInfoPanel = null;
        for (int i = 0; i < topTabbedPane.getTabCount(); i++) {
            Component componect = topTabbedPane.getComponentAt(i);
            if (componect instanceof ServiceInfoPanel) {
                serviceInfoPanel = (ServiceInfoPanel) componect;
                topTabbedPane.setSelectedIndex(i);
                isExist = true;
            }
        }
        if (isExist) {
            serviceInfoPanel.setRedisTreeItem(statePanel.getCurrentRedisItem());
            serviceInfoPanel.updateData(true);
        } else {
            topTabbedPane.addTab("SERVER " + statePanel.getCurrentRedisItem().getParentName(),
                    PublicConstant.Image.getImageIcon(PublicConstant.Image.server16,16,16),
                    new ServiceInfoPanel(statePanel.getCurrentRedisItem(), topTabbedPane, statePanel.getMonitorDialog()));

        }
    }

    /**
     * @param tree
     */
    private void disConnect(JTree tree, RTabbedPane jTabbedPane, StatePanel statePanel) {
        RTreeNode node = (RTreeNode) tree.getLastSelectedPathComponent(); // 获得右键选中的节点
        RedisTreeItem redisTreeItem = (RedisTreeItem) node.getUserObject();
        DefaultTreeModel defaultModel = (DefaultTreeModel) tree.getModel();
        String itemId = redisTreeItem.getId();
        RedisLarkPool.connectMap.remove(node);
        RedisLarkPool.destory(itemId);
        try {
            node.removeAllChildren();
            Optional.ofNullable(ServiceManager.getInstance().findoOriginalRedisTreeNode(tree, node)).ifPresent(r -> r.removeAllChildren());
            for (int i = jTabbedPane.getTabCount() - 1; i >= 0; i--) {
                Component componect = jTabbedPane.getComponentAt(i);
                if (componect instanceof ServiceInfoPanel) {
                    ServiceInfoPanel serviceInfoPanel = (ServiceInfoPanel) componect;
                    if (itemId.equals(serviceInfoPanel.getRedisTreeItem().getId())) {
                        jTabbedPane.remove(i);
                    }
                } else if (componect instanceof RedisTabbedPanel) {
                    RedisTabbedPanel redisTabbedPanel = (RedisTabbedPanel) componect;
                    RedisTreeItem treeItem = (RedisTreeItem) redisTabbedPanel.getTreeNode().getUserObject();
                    if (itemId.equals(treeItem.getId())) {
                        jTabbedPane.remove(i);
                    }
                }else if (componect instanceof PubSubPanel) {
                    PubSubPanel pubSubPanel = (PubSubPanel) componect;
                    if (itemId.equals(pubSubPanel.getConnectId())) {
                        jTabbedPane.remove(i);
                    }
                }
            }
            defaultModel.reload(node);
            statePanel.doUpdateStatus(null);
        } catch (Exception e) {
            log.error("断开连接异常", e);
        } finally {
            RedisLarkPool.destory(itemId);
        }
    }


    public JPopupMenu getDBTreePopMenu(JTree tree, RTabbedPane topTabbedPane,RTabbedPane logTabbedPane, JTextField keyFilterField) {
        JPopupMenu popMenu = new JRedisPopupMenu();// 菜单
        JMenuItem batchDelItem = getJMenuItem(I18nKey.RedisResource.DELETES, PublicConstant.Image.getImageIcon(PublicConstant.Image.delete));
        batchDelItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, java.awt.event.InputEvent.CTRL_MASK));
        //add new key
        JMenuItem addItem = getJMenuItem(I18nKey.RedisResource.MENU_ADDKEY, PublicConstant.Image.getImageIcon(PublicConstant.Image.object_add));
        addItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        //filter keys
//		JMenuItem filterKeyItem= getJMenuItem(I18nKey.RedisResource.MENU_FILTERKEY);
        //数据库属性
        JMenuItem dbAttrItem = getJMenuItem(I18nKey.RedisResource.MENU_ATTR, PublicConstant.Image.getImageIcon(PublicConstant.Image.attribute));
        //刷新数据库
        JMenuItem flushDbItem = getJMenuItem(I18nKey.RedisResource.MENU_FLUSH, PublicConstant.Image.getImageIcon(PublicConstant.Image.data_reset));
        flushDbItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        JMenuItem reloadItem = getJMenuItem(I18nKey.RedisResource.MENU_RELOAD, PublicConstant.Image.getImageIcon(PublicConstant.Image.menu_refresh));
        reloadItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        //控制台
        JMenuItem consoleItem = getJMenuItem(I18nKey.RedisResource.MENU_CONSOLE, PublicConstant.Image.getImageIcon(PublicConstant.Image.cmd_console,14,14));
        consoleItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_MASK));
        //批量删除指定键
        JMenuItem deleteByKeyItem = getJMenuItem(I18nKey.RedisResource.DELETEBYKEY, PublicConstant.Image.getImageIcon(PublicConstant.Image.cmd_console,14,14));
        //批量获取指定键
        JMenuItem getByKeyItem = getJMenuItem(I18nKey.RedisResource.GETBYKEY, PublicConstant.Image.getImageIcon(PublicConstant.Image.cmd_console,14,14));
        batchDelItem.addActionListener(e -> {
            String keyPattern = SwingTools.showInputDialog(null, "请输入删除键的表达式", LarkFrame.getI18nText(I18nKey.RedisResource.DELETES), null);
            if (StringUtils.isNotBlank(keyPattern)) {
                RTreeNode node = (RTreeNode) tree.getLastSelectedPathComponent();
                RedisTreeItem redisTreeItem = (RedisTreeItem) node.getUserObject();
                @SuppressWarnings("serial")
                DeleteRedisKeyDialog d = new DeleteRedisKeyDialog(LarkFrame.frame, node) {
                    @Override
                    public void initDialog(RTreeNode node) {
                        super.initDialog(node);
                        setKeyPattern(keyPattern);
                    }
                };
                d.getResult((number) -> {
                    d.setVisible(false);
                    String[] split = number.split(",");
                    int confirm = SwingTools.showConfirmDialogYNC(null, "删除 " + d.getKeyPattern() + " 数量: " + split[0] + ",耗时：" + split[1] + "毫秒, 是否重新加载db" + redisTreeItem.getDb(), "删除成功");
                    if (confirm == JOptionPane.YES_OPTION) {
                        ServiceManager.getInstance().openDbRedisTree(node, redisTreeItem, tree, keyFilterField, true);
                    }
                    d.close();
                });
                SwingTools.swingWorkerExec(() -> d.open());
            }

        });
        addItem.addActionListener(e -> {
            RTreeNode node = (RTreeNode) tree.getLastSelectedPathComponent();
            RedisTreeItem redisTreeItem = (RedisTreeItem) node.getUserObject();
            addNewKey(tree, node, redisTreeItem,keyFilterField);
        });
        reloadItem.addActionListener(e -> {
            SwingTools.swingWorkerExec(() -> {
                RTreeNode node = (RTreeNode) tree.getLastSelectedPathComponent();
                int childCount = node.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    RTreeNode childAt = (RTreeNode) node.getChildAt(i);
                    ServiceManager.getInstance().removeNodeForRedisTabbedPanel(childAt, topTabbedPane);
                }

                node.removeAllChildren();
                RedisTreeItem redisTreeItem = (RedisTreeItem) node.getUserObject();
                ServiceManager.getInstance().openDbRedisTree(node, redisTreeItem, tree, keyFilterField, true);
            });
        });
        flushDbItem.addActionListener(e -> {
            int conform = SwingTools.showConfirmDialogYNC(null, "Do you really want to remove all keys from this database", "Flush Database");
            if (conform == JOptionPane.YES_OPTION) {
                RTreeNode node = (RTreeNode) tree.getLastSelectedPathComponent();
                RedisTreeItem redisTreeItem = (RedisTreeItem) node.getUserObject();
                StatePanel.showLoading(()->{
                    final ResultRes<String> resultRes = BaseController.dispatcher(() -> redisConnectService.flushDb(redisTreeItem.getId(), redisTreeItem.getDb()));
                    if (resultRes.isRet()) {
                        Enumeration enumeration = node.children();
                        while (enumeration.hasMoreElements()) {
                            ServiceManager.getInstance().removeNodeForRedisTabbedPanel((RTreeNode) enumeration.nextElement(), topTabbedPane);
                        }
                        node.removeAllChildren();
                        Optional.ofNullable(ServiceManager.getInstance().findoOriginalRedisTreeNode(tree, node)).ifPresent(r -> r.removeAllChildren());
                        DefaultTreeModel defaultModel = (DefaultTreeModel) tree.getModel();
                        String name = redisTreeItem.getName();
                        redisTreeItem.setName(name.substring(0, name.lastIndexOf("(")) + "(" + redisConnectService.getDbKeySize(redisTreeItem.getId(), redisTreeItem.getDb()) + ")");
                        defaultModel.reload(node);
                    } else {
                        SwingTools.showMessageErrorDialog(null, resultRes.getMsg());
                    }
                });
            }
        });
        consoleItem.addActionListener(e -> openConsolePane(logTabbedPane, tree));
        deleteByKeyItem.addActionListener(e->{
            RTreeNode node = (RTreeNode) tree.getLastSelectedPathComponent();
            RedisTreeItem redisTreeItem = (RedisTreeItem) node.getUserObject();
            TextAreaDialog textAreaDialog = new TextAreaDialog(LarkFrame.frame, redisTreeItem);
            textAreaDialog.setPlaceholder("使用\\n或,分隔");
            textAreaDialog.setTitle("删除指定键");
            textAreaDialog.open(s-> {
                int conform = SwingTools.showConfirmDialogYNC(null, "是否确认删除？", "删除确认");
                if(conform == JOptionPane.YES_OPTION){
                    textAreaDialog.setNeedClose(true);
                    if(StringUtils.isNotBlank(s)){
                        s = s.replaceAll("\\n", ",");
                        String[] split = s.split(",");
                        if(!ArraysUtil.isEmpty(split)){
                            byte[][] bytes = Arrays.stream(split).map(item -> StringUtils.strToByte(item)).toArray(size -> new byte[size][]);
                            ResultRes<Long> dispatcher = BaseController.dispatcher(() ->redisConnectService.deleteKeys(redisTreeItem.getId(), redisTreeItem.getDb(), bytes),true,false);
                            if(dispatcher.isRet()) {
                                SwingTools.showMessageInfoDialog(textAreaDialog,"删除数量："+dispatcher.getData(),"删除完成");
                            }else{
                                SwingTools.showMessageErrorDialog(null,dispatcher.getMsg());
                            }
                        }
                    }
                }else{
                    textAreaDialog.setNeedClose(false);
                }

            });
        });
        getByKeyItem.addActionListener(e->{
            RTreeNode node = (RTreeNode) tree.getLastSelectedPathComponent();
            RedisTreeItem redisTreeItem = (RedisTreeItem) node.getUserObject();
            TextAreaDialog textAreaDialog = new TextAreaDialog(LarkFrame.frame, redisTreeItem);
            textAreaDialog.setPlaceholder("使用\\n或,分隔,仅支持获取STRING类型");
            textAreaDialog.open(s-> {
                if(StringUtils.isNotBlank(s)){
                    s = s.replaceAll("\\n", ",");
                    String[] split = s.split(",");
                    if(!ArraysUtil.isEmpty(split)) {
                        byte[][] bytes = Arrays.stream(split).map(item -> StringUtils.strToByte(item)).toArray(size -> new byte[size][]);
                        ResultRes<List<Map<String,String>>> dispatcher = BaseController.dispatcher(() ->redisConnectService.getStringByKeys(redisTreeItem.getId(), redisTreeItem.getDb(), bytes),true,false);
                        textAreaDialog.setNeedClose(false);
                        if(dispatcher.isRet()) {
                            textAreaDialog.clearAreaText();
                            StringBuilder stringBuilder = new StringBuilder();
                            for (Map<String, String> map : dispatcher.getData()) {
                                for (Map.Entry<String, String> entry : map.entrySet()) {
                                    stringBuilder.append(StringUtils.getCsvTypeStr(entry.getKey())+","+StringUtils.getCsvTypeStr(entry.getValue())+"\r\n");
                                }
                            }
                            textAreaDialog.setPlaceholder(null);
                            textAreaDialog.setText( stringBuilder.toString());
                            textAreaDialog.setEditable(false);
                            textAreaDialog.open();
                        }else{
                            SwingTools.showMessageErrorDialog(null,dispatcher.getMsg());
                        }
                    }

                }
            });
        });

        dbAttrItem.setEnabled(false);
        putPopMenuItem(popMenu,addItem,deleteByKeyItem,batchDelItem,getByKeyItem,reloadItem,consoleItem,dbAttrItem,flushDbItem);
        return popMenu;
    }

    public JPopupMenu getKeyTreePopMenu(JTree tree, RTabbedPane topTabbedPane) {
        JPopupMenu popMenu = new JRedisPopupMenu();// 菜单
        JMenuItem openKeyItem = getJMenuItem(I18nKey.RedisResource.OPENNEWTAB, PublicConstant.Image.getImageIcon(PublicConstant.Image.open));
        JMenuItem removeKeyItem = getJMenuItem(I18nKey.RedisResource.MENU_REMOVEKEY, PublicConstant.Image.getImageIcon(PublicConstant.Image.delete));
        JMenuItem remameKeyItem = getJMenuItem(I18nKey.RedisResource.REMAME, PublicConstant.Image.getImageIcon(PublicConstant.Image.rename));
        JMenuItem copyKeyItem = getJMenuItem(I18nKey.RedisResource.COPY, PublicConstant.Image.getImageIcon(PublicConstant.Image.copy));
        copyKeyItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        openKeyItem.addActionListener(e -> openTabbedPane(tree, topTabbedPane, (RTreeNode) tree.getLastSelectedPathComponent()));
        removeKeyItem.addActionListener(e -> removeKey(tree, (RTreeNode) tree.getLastSelectedPathComponent(), topTabbedPane, null));
        copyKeyItem.addActionListener(e -> {
            RTreeNode node = (RTreeNode) tree.getLastSelectedPathComponent();
            RedisTreeItem item = (RedisTreeItem) node.getUserObject();
            Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable tText = new StringSelection(StringUtils.byteToStr(item.getKey()));
            clip.setContents(tText, null);
        });
        remameKeyItem.addActionListener(e -> {
            RTreeNode node = (RTreeNode) tree.getLastSelectedPathComponent();
            ServiceManager.getInstance().renameKey(tree,node,name->{
                ServiceManager.getInstance().findNodeByRedisTabbedPanel(true,node,topTabbedPane,index->{
                    RedisTabbedPanel redisTabbedPanel = (RedisTabbedPanel) topTabbedPane.getComponentAt(index);
                    redisTabbedPanel.resetKeyName(name);
                });
            });
        });
        putPopMenuItem(popMenu,openKeyItem,remameKeyItem,copyKeyItem,removeKeyItem);
        return popMenu;
    }

    public JPopupMenu getKeyNameSpaceTreePopMenu(JTree tree) {
        JPopupMenu popMenu = new JRedisPopupMenu();// 菜单
        //add new key
        JMenuItem reloadItem = getJMenuItem(I18nKey.RedisResource.MENU_RELOAD, PublicConstant.Image.getImageIcon(PublicConstant.Image.menu_refresh));
        //filter keys
        JMenuItem removeKeyItem = getJMenuItem(I18nKey.RedisResource.MENU_REMOVEKEY, PublicConstant.Image.getImageIcon(PublicConstant.Image.delete));

        reloadItem.addActionListener(e -> {
            RTreeNode treeNode = (RTreeNode) tree.getLastSelectedPathComponent(); // 获得右键选中的节点
            RedisTreeItem redisTreeItem = (RedisTreeItem) treeNode.getUserObject();
            String keyPattern = redisTreeItem.getOriginName() + ":*";
            ResultRes<KeyScanCursor<byte[]>> dispatcher = BaseController.dispatcher(() -> redisConnectService.getKeys(redisTreeItem.getId(), redisTreeItem.getDb(), keyPattern, false));
            if (dispatcher.isRet()) {
                DefaultTreeModel defaultModel = (DefaultTreeModel) tree.getModel();
                List<byte[]> keys = dispatcher.getData().getKeys();
                if (CollectionUtils.isNotEmpty(keys)) {
                    ServiceManager.getInstance().drawRedisKeyTree(tree,treeNode, redisTreeItem, keys,true);
                }
                defaultModel.reload(treeNode);
            } else {
                SwingTools.showMessageErrorDialog(null, "获取" + keyPattern + "失败");
            }

        });
        removeKeyItem.addActionListener(e -> {
            RTreeNode node = (RTreeNode) tree.getLastSelectedPathComponent(); // 获得右键选中的节点
            DeleteRedisKeyDialog d = new DeleteRedisKeyDialog(LarkFrame.frame, node);
            d.getResult((number) -> {
                String[] split = number.split(",");
                d.close();
                SwingTools.showMessageInfoDialog(null, "删除" + d.getKeyPattern() + "数量：" + split[0], "删除成功");
                //置灰节点
                DefaultTreeModel defaultModel = (DefaultTreeModel) tree.getModel();
                node.setEnabled(false);
                node.removeAllChildren();
                defaultModel.reload(node);

            });
            SwingTools.swingWorkerExec(() -> d.open());
        });

        popMenu.add(reloadItem);
        popMenu.add(removeKeyItem);
        return popMenu;
    }

    public void openTabbedPane(JTree tree, RTabbedPane topTabbedPane, RTreeNode node) {
        StatePanel.showLoading(()-> {
            //获取数量
            RedisTreeItem item = (RedisTreeItem) node.getUserObject();
            topTabbedPane.addTab(item.getName(), PublicConstant.Image.getImageIcon(PublicConstant.Image.key_icon), new RedisTabbedPanel(node, tree));
            topTabbedPane.setSelectedIndex(topTabbedPane.getTabCount()-1);
        });
    }

    public void replaceTabbedPane(JTree tree, RTabbedPane topTabbedPane, RTreeNode node) {
        //获取数量
        StatePanel.showLoading(()-> {
            int count = topTabbedPane.getTabCount();
            RedisTreeItem item = (RedisTreeItem) node.getUserObject();
            if (count == 0) {
                topTabbedPane.addTab(StringUtils.showHexStringValue(item.getKey()), PublicConstant.Image.getImageIcon(PublicConstant.Image.key_icon), new RedisTabbedPanel(node, tree));
            } else {
                int selectedIndex = topTabbedPane.getSelectedIndex();
                if (selectedIndex > 0) {
                    replaceTabbedPane(tree, topTabbedPane, node, item, selectedIndex);
                } else {
                    if (count > 1) {
                        replaceTabbedPane(tree, topTabbedPane, node, item, count - 1);
                    } else {
                        topTabbedPane.addTab(StringUtils.showHexStringValue(item.getKey()), PublicConstant.Image.getImageIcon(PublicConstant.Image.key_icon), new RedisTabbedPanel(node, tree));
                    }

                }

            }
        });
    }

    private void replaceTabbedPane(JTree tree, RTabbedPane topTabbedPane, RTreeNode node, RedisTreeItem item, int selectedIndex) {
        Component componect = topTabbedPane.getComponentAt(selectedIndex);
        RedisTabbedPanel redisTabbedPanel = null;
        if (componect instanceof RedisTabbedPanel) {
            redisTabbedPanel  = (RedisTabbedPanel) componect;
        } else {
            for (int i = topTabbedPane.getTabCount() - 1; i > 0; i--) {
                componect = topTabbedPane.getComponentAt(i);
                if (topTabbedPane.getComponentAt(i) instanceof RedisTabbedPanel) {
                    redisTabbedPanel = (RedisTabbedPanel) componect;
                    selectedIndex = i;
                    break;
                }
            }
        }
        if(redisTabbedPanel != null){
            topTabbedPane.setSelectedIndex(selectedIndex);
            redisTabbedPanel.updateUI(node, tree, new PageBean(), true,true,true);
        }else{
            topTabbedPane.addTab(StringUtils.showHexStringValue(item.getKey()), PublicConstant.Image.getImageIcon(PublicConstant.Image.key_icon), new RedisTabbedPanel(node, tree));
            topTabbedPane.setSelectedIndex(selectedIndex + 1);
        }
    }


    private void addNewKey(JTree tree, RTreeNode node, RedisTreeItem redisTreeItem, JTextField keyFilterField) {
        AddRedisKeyRowDialog d = new AddRedisKeyRowDialog(LarkFrame.frame, redisTreeItem);
        d.getResult((item) -> {
            //重新加载
//				SwingTools.addTreeNode(node, redisTreeItem.getId(), db+"", item.getKey(), PublicConstant.Image.logo_20, RedisType.KEY);
            //TODO 与@link{cn.org.tpeach.nosql.view.RedisMainWindow#clickLeftTree} 合并
            String pattern = StringUtils.isNotBlank(keyFilterField.getText())?keyFilterField.getText()+"*":"*";
            ResultRes<KeyScanCursor<byte[]>> resDatabase = BaseController.dispatcher(() -> redisConnectService.getKeys(redisTreeItem.getId(), item.getDb(), pattern,false));
            if (resDatabase.isRet()) {
                d.close();
                int conform = SwingTools.showConfirmDialogYNC(null, "key was added.Do you want to reload keys in selected database", "key was added");
                if (conform == JOptionPane.YES_OPTION) {
                    final List<byte[]> keys = resDatabase.getData().getKeys();
                    if (CollectionUtils.isNotEmpty(keys)) {
                        ServiceManager.getInstance().drawRedisKeyTree(tree,node, redisTreeItem, keys,true);
                        tree.expandPath(new TreePath(node.getPath()));
                        String name = redisTreeItem.getName();
                        redisTreeItem.setName(name.substring(0, name.lastIndexOf("(")) + "(" + redisConnectService.getDbKeySize(redisTreeItem.getId(), redisTreeItem.getDb()) + ")");
                        tree.updateUI();
                    }

//					if (CollectionUtils.isNotEmpty(keys)) {
//						node.removeAllChildren();
//						keys.stream().forEach(key->SwingTools.addKeyTreeNode(node, node.getUserObject(), key, name, path, tipText));
//						tree.expandPath(new TreePath(node.getPath()));
//						tree.updateUI();
//					}
                }


            }

        });
        d.open();
    }

    public void removeKey(JTree tree, RTreeNode node, RTabbedPane topTabbedPane, Consumer<RTreeNode> confirmFun) {
        RedisTreeItem item = (RedisTreeItem) node.getUserObject();
        int confirm = SwingTools.showConfirmDialogYNC(null, "是否确认删除KEY:"+StringUtils.byteToStr(item.getKey())+"？", "删除确认");
        if (confirm == JOptionPane.YES_OPTION) {

            ResultRes<Long> res = BaseController.dispatcher(() -> redisConnectService.deleteKeys(item.getId(), item.getDb(), item.getKey()));
            if (res.isRet()) {
//				SwingTools.removeTreeNode(tree,node);
                item.setName(item.getName() + "(remove)");
                node.setEnabled(false);
                Optional.ofNullable(ServiceManager.getInstance().findoOriginalRedisTreeNode(tree, node)).ifPresent(r -> r.setEnabled(false));
                //删除包含的标签
                ServiceManager.getInstance().removeNodeForRedisTabbedPanel(node, topTabbedPane);
                tree.updateUI();
                if (confirmFun != null) {
                    confirmFun.accept(node);
                }
            } else {
                SwingTools.showMessageErrorDialog(null, "删除key失败:" + res.getMsg());
            }

        }
    }



    //--------------------common-----------------------------------

    /**
     * 获取菜单item
     * @param text
     * @param icon
     * @return
     */
    public JMenuItem getJMenuItem(String text, Icon icon) {
        return new JRedisMenuItem(text, icon);
    }

    public JMenuItem getJMenuItem(String text) {
        return getJMenuItem(text, null);
    }

    public JMenuItem getJMenuItem(I18nKey.RedisResource resourceKey) {
        return getJMenuItem(resourceKey, null);
    }

    public JMenuItem getJMenuItem(I18nKey.RedisResource resourceKey, Icon icon) {
        return getJMenuItem(LarkFrame.getI18nText(resourceKey), icon);
    }


}
