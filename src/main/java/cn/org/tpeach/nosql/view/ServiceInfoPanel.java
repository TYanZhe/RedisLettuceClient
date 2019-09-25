/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.org.tpeach.nosql.view;

import cn.org.tpeach.nosql.constant.PublicConstant;
import cn.org.tpeach.nosql.constant.RedisInfoKeyConstant;
import cn.org.tpeach.nosql.redis.bean.RedisClientBo;
import cn.org.tpeach.nosql.redis.bean.RedisTreeItem;
import cn.org.tpeach.nosql.redis.bean.SlowLogBo;
import cn.org.tpeach.nosql.redis.service.IRedisConnectService;
import cn.org.tpeach.nosql.service.ServiceProxy;
import cn.org.tpeach.nosql.tools.CollectionUtils;
import cn.org.tpeach.nosql.tools.GsonUtil;
import cn.org.tpeach.nosql.tools.MapUtils;
import cn.org.tpeach.nosql.tools.SwingTools;
import cn.org.tpeach.nosql.view.component.EasyGBC;
import cn.org.tpeach.nosql.view.component.EasyJSP;
import cn.org.tpeach.nosql.view.component.PrefixTextLabel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

@Getter
@Setter
class LiPanel extends  JPanel{
    private JPanel childPanel;
    private boolean isSelect;
}

/**
 *
 * @author smart
 */
@Slf4j
public class ServiceInfoPanel extends JPanel {
    private List<LiPanel> liPanlList = new ArrayList<>(5);
    private List<JPanel> contextPanelist = new ArrayList<>(5);
    IRedisConnectService redisConnectService = ServiceProxy.getBeanProxy("redisConnectService", IRedisConnectService.class);
    private RedisTreeItem redisTreeItem;
    @Getter
    @Setter
    private  Map<String, String> redinInfoMap;
    @Getter
    @Setter
    private List<RedisClientBo> redisClientList;
    @Getter
    @Setter
    List<SlowLogBo> slowLogList;
    private PrefixTextLabel redisVersionLabel,redisModeLabel,osLabel,processIdLabel,tcpPortLabel,uptimeInSecondsLabel;
    private PrefixTextLabel connectedClientsLabel,clientLongestOutputListLabel,clientBiggestInputBufLabel,blockedClientsLabel;
    private PrefixTextLabel usedMemoryLabel,usedMemoryRssLabel,usedMemoryPeakHumanLabel,memFragmentationRatioLabel,memAllocatorLabel;
    private PrefixTextLabel totalConnectionsReceivedLabel,totalCommandsProcessedLabel,instantaneousOpsOerSecLabel,totalNetInputBytesLabel,totalNetOutputBytesLabel,rejectedConnectionsLabel;
    private PrefixTextLabel usedCpuSysLabel,usedCpuUserLabel,usedCpuSysChildrenLabel,usedCpuUserChildrenLabel;
    /**
     * Creates new form ServiceInfoPanel
     */
    public ServiceInfoPanel(RedisTreeItem redisTreeItem) {
        this.redisTreeItem = redisTreeItem;
        //获取连接信息
        this.redinInfoMap = redisConnectService.getConnectInfo(redisTreeItem.getId(),false);
        if(MapUtils.isEmpty(redinInfoMap)){
            return;
        }
        redisClientList = redisConnectService.clientList(redisTreeItem.getId());
        slowLogList = redisConnectService.slowlogGet(redisTreeItem.getId());

        initComponents();
        initComponents2();


        updateData();
    }


    public void updateData(){
        //更新top表格
        //更新右边服务列表
        if(MapUtils.isNotEmpty(redinInfoMap)){
            DefaultTableModel model = (DefaultTableModel) baseInfoTable.getModel();
            model.getDataVector().clear();
            Vector<String> dataVector = new Vector<>();
            dataVector.add(redisTreeItem.getName());
            dataVector.add(redinInfoMap.get(RedisInfoKeyConstant.redisVersion));
            dataVector.add(redinInfoMap.get(RedisInfoKeyConstant.usedMemoryHuman));
            dataVector.add(redinInfoMap.get(RedisInfoKeyConstant.connectedClients));
            dataVector.add(redinInfoMap.get(RedisInfoKeyConstant.totalCommandsProcessed));
            dataVector.add(redinInfoMap.get(RedisInfoKeyConstant.uptimeInDays));
            model.addRow(dataVector);

            redisVersionLabel.setText(redinInfoMap.get(RedisInfoKeyConstant.redisVersion));
            redisModeLabel.setText(redinInfoMap.get(RedisInfoKeyConstant.redisMode));
            osLabel.setText(redinInfoMap.get(RedisInfoKeyConstant.OS));
            processIdLabel.setText(redinInfoMap.get(RedisInfoKeyConstant.processId));
            tcpPortLabel.setText(redinInfoMap.get(RedisInfoKeyConstant.tcpPort));
            uptimeInSecondsLabel.setText(redinInfoMap.get(RedisInfoKeyConstant.uptimeInSeconds));

            connectedClientsLabel.setText(redinInfoMap.get(RedisInfoKeyConstant.connectedClients));
            clientLongestOutputListLabel.setText(redinInfoMap.get(RedisInfoKeyConstant.clientLongestOutputList));
            clientBiggestInputBufLabel.setText(redinInfoMap.get(RedisInfoKeyConstant.clientBiggestInputBuf));
            blockedClientsLabel.setText(redinInfoMap.get(RedisInfoKeyConstant.blockedClients));


            usedMemoryLabel.setText(redinInfoMap.get(RedisInfoKeyConstant.usedMemory));
            usedMemoryRssLabel.setText(redinInfoMap.get(RedisInfoKeyConstant.usedMemoryRss));
            usedMemoryPeakHumanLabel.setText(redinInfoMap.get(RedisInfoKeyConstant.usedMemoryPeakHuman));
            memFragmentationRatioLabel.setText(redinInfoMap.get(RedisInfoKeyConstant.memFragmentationRatio));
            memAllocatorLabel.setText(redinInfoMap.get(RedisInfoKeyConstant.memAllocator));


            totalConnectionsReceivedLabel.setText(redinInfoMap.get(RedisInfoKeyConstant.totalConnectionsReceived));
            totalCommandsProcessedLabel.setText(redinInfoMap.get(RedisInfoKeyConstant.totalCommandsProcessed));
            instantaneousOpsOerSecLabel.setText(redinInfoMap.get(RedisInfoKeyConstant.instantaneousOpsOerSec));
            totalNetInputBytesLabel.setText(redinInfoMap.get(RedisInfoKeyConstant.totalNetInputBytes));
            totalNetOutputBytesLabel.setText(redinInfoMap.get(RedisInfoKeyConstant.totalNetOutputBytes));
            rejectedConnectionsLabel.setText(redinInfoMap.get(RedisInfoKeyConstant.totalNetOutputBytes));

            usedCpuSysLabel.setText(redinInfoMap.get(RedisInfoKeyConstant.usedCpuSys));
            usedCpuUserLabel.setText(redinInfoMap.get(RedisInfoKeyConstant.usedCpuUser));
            usedCpuSysChildrenLabel.setText(redinInfoMap.get(RedisInfoKeyConstant.usedCpuSysChildren));
            usedCpuUserChildrenLabel.setText(redinInfoMap.get(RedisInfoKeyConstant.usedCpuUserChildren));
        }
        if(CollectionUtils.isNotEmpty(redisClientList)){
            DefaultTableModel model = (DefaultTableModel) clientTable.getModel();
            model.getDataVector().clear();
            for (RedisClientBo redisClientBo : redisClientList) {
                Vector<String> dataVector = new Vector<>();
                dataVector.add(redisClientBo.getId());
                dataVector.add(redisClientBo.getAddr());
                dataVector.add(redisClientBo.getAge());
                dataVector.add(redisClientBo.getDb());
                dataVector.add(redisClientBo.getCmd());
                model.addRow(dataVector);
            }

        }

        if(CollectionUtils.isNotEmpty(slowLogList)){
            DefaultTableModel model = (DefaultTableModel) logListTable.getModel();
            model.getDataVector().clear();
            for (SlowLogBo slowLogBo : slowLogList) {
                Vector<Object> dataVector = new Vector<>();
                dataVector.add(slowLogBo.getId() );
                dataVector.add(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.ofInstant(Instant.ofEpochMilli(slowLogBo.getOperTime()*1000), ZoneId.systemDefault())));
                dataVector.add(slowLogBo.getExcuteTime());
                dataVector.add(slowLogBo.getCmd());
                model.addRow(dataVector);
            }
        }
    }

    private JPanel getChildPanel(int height,int row){
        JPanel j = new JPanel();
        j.setBackground(Color.WHITE);
        j.setPreferredSize(new Dimension(0,height));
        j.setMaximumSize(new Dimension(j.getPreferredSize().width,height));
        j.setLayout(new GridLayout(row,1));
        j.setBorder(BorderFactory.createEmptyBorder(5,20,5,0));
        return j;
    }

    private PrefixTextLabel getChildLabel(String labelName){
        PrefixTextLabel label = new PrefixTextLabel(labelName);
        label.setForeground(new Color(70,130,180));
        return label;
    }

    private void listView() {
        boolean first = true;
        for (LiPanel liPanel : liPanlList) {
            Box horizontalBox = Box.createHorizontalBox();
            horizontalBox.add(liPanel);
            rightPanel.add(horizontalBox);
            JPanel childPanel = liPanel.getChildPanel();
            if(childPanel != null){
                rightPanel.add(childPanel);
            }
            if(first){
                first = false;
                liPanel.setSelect(true);
                JLabel jLabel = (JLabel) liPanel.getComponent(1);
                jLabel.setIcon(PublicConstant.Image.arrow_circle_down);
            }else{
                if(childPanel != null){
                    childPanel.setVisible(false);
                }
            }

        }
        rightPanel.add(Box.createVerticalGlue());
        rightPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                liPanlList.forEach(item ->fillWidthPanel(rightPanel,item));
                contextPanelist.forEach(item ->fillWidthPanel(rightPanel,item));
            }
        });
    }

    /**
     * 使宽度与parentComponent保持一致
     * @param parentComponent
     * @param panel
     */
    private void fillWidthPanel(JComponent parentComponent,JPanel panel ){
        int width = parentComponent.getWidth();
        Dimension preferredSize = panel.getPreferredSize();
        panel.setPreferredSize(new Dimension(width,preferredSize.height));
        panel.setMinimumSize(new Dimension(width,preferredSize.height));
        panel.setMaximumSize(new Dimension(width,preferredSize.height));
        panel.updateUI();
    }

    private LiPanel getLiPanel(String liName){
        LiPanel panel = new LiPanel();
        int liHeight = 43;
        panel.setBackground(new Color(242,242,242));
        Dimension preferredSize = panel.getPreferredSize();
        panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
        panel.setPreferredSize(new Dimension(preferredSize.width,liHeight));
        panel.setMinimumSize(new Dimension(preferredSize.width,liHeight));
        panel.setMaximumSize(new Dimension(preferredSize.width,liHeight));
        JLabel label = new JLabel(liName);
        label.setIcon(PublicConstant.Image.arrow_circle_right);
        label.setFont(new Font("新宋体",Font.PLAIN,16));
        panel.add(Box.createHorizontalStrut(10));
        panel.add(label);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(Box.createHorizontalGlue());
        panel.setBorder(BorderFactory.createMatteBorder(0,0,1,0,new Color(232,232,232)));
        SwingTools.addMouseClickedListener(panel,e->{
            for (LiPanel liPanel : liPanlList) {
                JLabel l = (JLabel) liPanel.getComponent(1);
                JPanel childPanel = liPanel.getChildPanel();
                if(panel != liPanel){
                    liPanel.setSelect(false);
                    l.setIcon(PublicConstant.Image.arrow_circle_right);
                    if(childPanel != null){
                        childPanel.setVisible(false);
                    }
                }else{
                    if(liPanel.isSelect()){
                        //展开
                        l.setIcon(PublicConstant.Image.arrow_circle_right);
                    }else {
                        //关闭
                        l.setIcon(PublicConstant.Image.arrow_circle_down);
                    }
                    liPanel.setSelect(!liPanel.isSelect());
                    if(childPanel != null){
                        childPanel.setVisible(liPanel.isSelect());
                    }
                }
            }


        });
        return panel;
    }
    private void initComponents2(){
        DefaultTableModel model = (DefaultTableModel) baseInfoTable.getModel();
        baseInfoPanel.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));
        baseInfoTable.setShowGrid(false);
        baseInfoTable.setShowHorizontalLines(false);
        baseInfoTable.setShowVerticalLines(false);
        baseInfoScrollPane.setBorder(BorderFactory.createEmptyBorder());
        TableColumn tc = baseInfoTable.getColumnModel().getColumn(4);
        tc.setPreferredWidth(160);
        baseInfoTable.setColumnSelectionAllowed(false);
        baseInfoTable.setCellSelectionEnabled(false);
        baseInfoTable.setRowSelectionAllowed(false);

        //表头
        JTableHeader tableHeader = baseInfoTable.getTableHeader();
        ((DefaultTableCellRenderer) tableHeader.getDefaultRenderer())
                .setHorizontalAlignment(DefaultTableCellRenderer.LEFT);// 列头内容左对齐
        tableHeader.setBackground(Color.WHITE);
        tableHeader.setReorderingAllowed(false);             // 设置不允许拖动重新排序各列\
        tableHeader.setResizingAllowed(false);
        tableHeader.setPreferredSize(new Dimension(tableHeader.getWidth(), 35));
        for (int i = 0; i < model.getColumnCount() ; i++) {
            TableCellRenderer renderer = new DefaultTableCellRenderer();
            ((DefaultTableCellRenderer) renderer).setForeground(new Color(128,128,128));
            TableColumn t1c = baseInfoTable.getColumnModel().getColumn(i);
            TableCellRenderer rendererTable = new DefaultTableCellRenderer(){
                @Override
                public void setBorder(Border border){

                }
            };
            baseInfoTable.getColumn(baseInfoTable.getColumnName(i)).setCellRenderer(rendererTable);
            t1c.setHeaderRenderer(renderer);
        }

        //---------------------------------------------------
        infoPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(15,0,0,0),BorderFactory.createMatteBorder(1,0,0,0,new Color(192,192,192))));
        infoPanel.add(leftPanel, EasyGBC.build(0,0,1,1).setFill(EasyGBC.BOTH).setWeight(0.56, 1.0)
                .resetInsets(0,0,0,0).setAnchor(EasyGBC.EAST));
        infoPanel.add(rightPanel,EasyGBC.build(1,0,1,1).setFill(EasyGBC.BOTH).setWeight(0.44, 1.0)
                .resetInsets(0,0,0,0).setAnchor(EasyGBC.EAST));

        leftPanel.setBorder(BorderFactory.createMatteBorder(0,0,0,1,new Color(192,192,192)));
        clientScrollPane.setBorder(BorderFactory.createEmptyBorder());
        logjScrollPane.setBorder(BorderFactory.createEmptyBorder());
        logListPanel.setBorder(BorderFactory.createMatteBorder(1,0,0,0,new Color(192,192,192)));
        clientListPanel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
        JTableHeader clientTableTableHeader = clientTable.getTableHeader();
        clientTableTableHeader.setPreferredSize(new Dimension(clientTableTableHeader.getPreferredSize().width,43));
        clientTable.getColumnModel().getColumn(1).setPreferredWidth(145);
        clientTable.setGridColor(new Color(230,230,230));
        clientTable.setSelectionBackground(PublicConstant.RColor.tableSelectBackground2);
        DefaultTableModel clientTableModel = (DefaultTableModel) clientTable.getModel();
        for (int i = 0; i < clientTableModel.getColumnCount() ; i++) {
            TableCellRenderer renderer = new DefaultTableCellRenderer();
            ((DefaultTableCellRenderer) renderer).setBackground(new Color(242,242,242));
            TableColumn t1c = clientTable.getColumnModel().getColumn(i);
            t1c.setHeaderRenderer(renderer);
        }

        final JTableHeader logListTableTableHeader = logListTable.getTableHeader();
        logListTableTableHeader.setPreferredSize(new Dimension(logListTableTableHeader.getPreferredSize().width,43));
        logListTable.getColumnModel().getColumn(1).setPreferredWidth(145);
        logListTable.getColumnModel().getColumn(3).setPreferredWidth(160);
        logListTable.setShowGrid(false);
        logListTable.setSelectionBackground(PublicConstant.RColor.tableSelectBackground2);
        DefaultTableModel logListTableModel = (DefaultTableModel) logListTable.getModel();
        for (int i = 0; i < logListTableModel.getColumnCount() ; i++) {
            TableCellRenderer renderer = new DefaultTableCellRenderer();
            ((DefaultTableCellRenderer) renderer).setBackground(new Color(242,242,242));
            TableColumn t1c = logListTable.getColumnModel().getColumn(i);
            t1c.setHeaderRenderer(renderer);
        }
        //---------------------------------------------
        LiPanel serverInfoLi = getLiPanel(" 服务端信息");
        LiPanel clientInfoLi = getLiPanel(" 客户端信息");
        LiPanel memoryInfoLi = getLiPanel(" 内存信息");
        LiPanel connectInfoLi = getLiPanel(" 连接信息");
        LiPanel cpuInfoLi = getLiPanel(" 处理器信息");
        liPanlList.add(serverInfoLi);
        liPanlList.add(clientInfoLi);
        liPanlList.add(memoryInfoLi);
        liPanlList.add(connectInfoLi);
        liPanlList.add(cpuInfoLi);
        redisVersionLabel = getChildLabel("服务版本: ");
        redisModeLabel = getChildLabel("服务模式: ");
        osLabel = getChildLabel("系统版本: ");
        processIdLabel = getChildLabel("进程编号: ");
        tcpPortLabel = getChildLabel("服务端口: ");
        uptimeInSecondsLabel = getChildLabel("运行时间: ");

        connectedClientsLabel = getChildLabel("当前已连接客户端数: ");
        clientLongestOutputListLabel = getChildLabel("当前连接的客户端当中,最长的输出列表: ");
        clientBiggestInputBufLabel = getChildLabel("当前连接的客户端当中,最大输入缓存: ");
        blockedClientsLabel = getChildLabel("当前已阻塞的客户端数量: ");

        usedMemoryLabel = getChildLabel("已占用内存量: ");
        usedMemoryRssLabel = getChildLabel("分配内存总量: ");
        usedMemoryPeakHumanLabel = getChildLabel("内存高峰值: ");
        memFragmentationRatioLabel = getChildLabel("内存碎片率: ");
        memAllocatorLabel = getChildLabel("内存分配器: ");

        totalConnectionsReceivedLabel = getChildLabel("已连接的客户端总数: ");
        totalCommandsProcessedLabel = getChildLabel("已执行过的命令总数: ");
        instantaneousOpsOerSecLabel = getChildLabel("服务每秒执行数量: ");
        totalNetInputBytesLabel = getChildLabel("服务输入网络流量: ");
        totalNetOutputBytesLabel = getChildLabel("服务输出网络流量: ");
        rejectedConnectionsLabel = getChildLabel("拒绝连接客户端数: ");

        usedCpuSysLabel = getChildLabel("服务主进程在核心态累积CPU耗时: ");
        usedCpuUserLabel = getChildLabel("服务主进程在用户态累积CPU耗时: ");
        usedCpuSysChildrenLabel = getChildLabel("服务后台进程在核心态累积CPU耗时: ");
        usedCpuUserChildrenLabel = getChildLabel("服务后台进程在用户态累积CPU耗时: ");



        JPanel serverInfo = getChildPanel(198,6);
        serverInfo.add(redisVersionLabel);
        serverInfo.add(redisModeLabel);
        serverInfo.add(osLabel);
        serverInfo.add(processIdLabel);
        serverInfo.add(tcpPortLabel);
        serverInfo.add(uptimeInSecondsLabel);
        serverInfoLi.setChildPanel(serverInfo);

        JPanel clientInfo = getChildPanel(132,4);
        clientInfo.add(connectedClientsLabel);
        clientInfo.add(clientLongestOutputListLabel);
        clientInfo.add(clientBiggestInputBufLabel);
        clientInfo.add(blockedClientsLabel);
        clientInfoLi.setChildPanel(clientInfo);

        JPanel memoryInfo = getChildPanel(165,5);
        memoryInfo.add(usedMemoryLabel);
        memoryInfo.add(usedMemoryRssLabel);
        memoryInfo.add(usedMemoryPeakHumanLabel);
        memoryInfo.add(memFragmentationRatioLabel);
        memoryInfo.add(memAllocatorLabel);
        memoryInfoLi.setChildPanel(memoryInfo);

        JPanel connectInfo = getChildPanel(198,6);
        connectInfo.add(totalConnectionsReceivedLabel);
        connectInfo.add(totalCommandsProcessedLabel);
        connectInfo.add(instantaneousOpsOerSecLabel);
        connectInfo.add(totalNetInputBytesLabel);
        connectInfo.add(totalNetOutputBytesLabel);
        connectInfo.add(rejectedConnectionsLabel);
        connectInfoLi.setChildPanel(connectInfo);

        JPanel cpuInfo = getChildPanel(132,4);
        cpuInfo.add(usedCpuSysLabel);
        cpuInfo.add(usedCpuUserLabel);
        cpuInfo.add(usedCpuSysChildrenLabel);
        cpuInfo.add(usedCpuUserChildrenLabel);
        cpuInfoLi.setChildPanel(cpuInfo);

        contextPanelist.add(serverInfo);
        contextPanelist.add(memoryInfo);
        contextPanelist.add(clientInfo);
        contextPanelist.add(connectInfo);
        contextPanelist.add(cpuInfo);
        listView();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        baseInfoPanel = new javax.swing.JPanel();
        baseInfoScrollPane = new EasyJSP();
        baseInfoTable = new javax.swing.JTable();
        infoPanel = new javax.swing.JPanel();
        leftPanel = new javax.swing.JPanel();
        clientListPanel = new javax.swing.JPanel();
        clientScrollPane = new EasyJSP();
        clientTable = new javax.swing.JTable();
        logListPanel = new javax.swing.JPanel();
        logjScrollPane = new EasyJSP();
        logListTable = new javax.swing.JTable();
        rightPanel = new javax.swing.JPanel();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(987, 550));
        setRequestFocusEnabled(false);
        setLayout(new java.awt.BorderLayout());

        baseInfoPanel.setBackground(new java.awt.Color(255, 255, 255));
        baseInfoPanel.setMaximumSize(new java.awt.Dimension(32767, 63));
        baseInfoPanel.setPreferredSize(new java.awt.Dimension(744, 63));
        baseInfoPanel.setLayout(new java.awt.BorderLayout());

        baseInfoScrollPane.setMaximumSize(new java.awt.Dimension(32767, 63));
        baseInfoScrollPane.setName(""); // NOI18N

        baseInfoTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"127.0.0.1", "3.2.0", "334.9M", "79", "89595965", "48"}
            },
            new String [] {
                "Name", "Redis Version", "Used Memory", "Clients", "Commads Processed", "Uptime Days"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        baseInfoTable.setRowHeight(28);
        baseInfoTable.setSelectionBackground(new java.awt.Color(255, 255, 255));
        baseInfoTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        baseInfoTable.setShowHorizontalLines(false);
        baseInfoTable.setShowVerticalLines(false);
        baseInfoScrollPane.setViewportView(baseInfoTable);

        baseInfoPanel.add(baseInfoScrollPane, java.awt.BorderLayout.CENTER);

        add(baseInfoPanel, java.awt.BorderLayout.NORTH);

        infoPanel.setBackground(new java.awt.Color(255, 255, 255));
        infoPanel.setLayout(new java.awt.GridBagLayout());

        leftPanel.setBackground(new java.awt.Color(255, 255, 255));
        leftPanel.setPreferredSize(new java.awt.Dimension(300, 300));
        leftPanel.setRequestFocusEnabled(false);
        leftPanel.setLayout(new java.awt.GridLayout(2, 1));

        clientListPanel.setBackground(new java.awt.Color(255, 255, 255));
        clientListPanel.setLayout(new java.awt.BorderLayout());

        clientTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID", "主机", "连接时长", "连接数据库", "命令"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        clientTable.setRowHeight(28);
        clientScrollPane.setViewportView(clientTable);

        clientListPanel.add(clientScrollPane, java.awt.BorderLayout.CENTER);

        leftPanel.add(clientListPanel);

        logListPanel.setBackground(new java.awt.Color(255, 255, 255));
        logListPanel.setLayout(new java.awt.BorderLayout());

        logListTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "日志时间", "操作耗时", "操作命令"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        logListTable.setRowHeight(28);
        logjScrollPane.setViewportView(logListTable);

        logListPanel.add(logjScrollPane, java.awt.BorderLayout.CENTER);

        leftPanel.add(logListPanel);

        infoPanel.add(leftPanel, new java.awt.GridBagConstraints());

        rightPanel.setBackground(new java.awt.Color(255, 255, 255));
        rightPanel.setPreferredSize(new java.awt.Dimension(300, 300));
        rightPanel.setLayout(new javax.swing.BoxLayout(rightPanel, javax.swing.BoxLayout.Y_AXIS));
        infoPanel.add(rightPanel, new java.awt.GridBagConstraints());

        add(infoPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel baseInfoPanel;
    private javax.swing.JScrollPane baseInfoScrollPane;
    private javax.swing.JTable baseInfoTable;
    private javax.swing.JPanel clientListPanel;
    private javax.swing.JScrollPane clientScrollPane;
    private javax.swing.JTable clientTable;
    private javax.swing.JPanel infoPanel;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JPanel logListPanel;
    private javax.swing.JTable logListTable;
    private javax.swing.JScrollPane logjScrollPane;
    private javax.swing.JPanel rightPanel;
    // End of variables declaration//GEN-END:variables
}
