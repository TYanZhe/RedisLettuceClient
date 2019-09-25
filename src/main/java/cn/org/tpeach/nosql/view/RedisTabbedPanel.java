/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.org.tpeach.nosql.view;

import cn.org.tpeach.nosql.bean.DicBean;
import cn.org.tpeach.nosql.bean.PageBean;
import cn.org.tpeach.nosql.bean.TableColumnBean;
import cn.org.tpeach.nosql.constant.I18nKey;
import cn.org.tpeach.nosql.constant.PublicConstant;
import cn.org.tpeach.nosql.controller.BaseController;
import cn.org.tpeach.nosql.controller.ResultRes;
import cn.org.tpeach.nosql.enums.RedisType;
import cn.org.tpeach.nosql.framework.LarkFrame;
import cn.org.tpeach.nosql.redis.bean.RedisKeyInfo;
import cn.org.tpeach.nosql.redis.bean.RedisTreeItem;
import cn.org.tpeach.nosql.redis.service.IRedisConnectService;
import cn.org.tpeach.nosql.service.ServiceProxy;
import cn.org.tpeach.nosql.tools.*;
import cn.org.tpeach.nosql.view.component.*;
import cn.org.tpeach.nosql.view.dialog.AddRowDialog;
import cn.org.tpeach.nosql.view.dialog.MagnifyTextDialog;
import cn.org.tpeach.nosql.view.jtree.RTreeNode;
import cn.org.tpeach.nosql.view.menu.JRedisPopupMenu;
import cn.org.tpeach.nosql.view.menu.MenuManager;
import cn.org.tpeach.nosql.view.table.RTableModel;
import io.lettuce.core.ScanCursor;
import io.lettuce.core.ScoredValue;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;


@Getter
@Setter
@ToString
class ValueInfoPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;
    private int selectColume;
    private int selectRow;
    private TableColumnBean valueColumnBean;
    private TableColumnBean keyColumnBean;
    private int index;

}


/**
 *
 * @author smart
 */
public class RedisTabbedPanel extends javax.swing.JPanel {

    private final static String TYPEHTML = "<html><p style='width:65px;font-family:-apple-system,BlinkMacSystemFont,PingFang SC,Verdana,Helvetica Neue,Microsoft Yahei,Hiragino Sans GB,Microsoft Sans Serif,WenQuanYi Micro Hei,sans-serif'>%s</p></html>";
    private final int borderWidth = 20;
    private final int tableRowHeight = 24;
    private final int tableHeaderRowHeight = 30;
    private int actRow = 0;
    private Vector[] tableContext;
    @Getter
    private RTreeNode treeNode;
    private JTree tree;
    IRedisConnectService redisConnectService = ServiceProxy.getBeanProxy("redisConnectService", IRedisConnectService.class);
    protected RedisKeyInfo redisKeyInfo;
    private PageBean pageBean = new PageBean();
    private int resultTab = 0;
    private OnlyReadArea resultTextArea;

    /**
     *
     */
    private static final long serialVersionUID = -47351539002785269L;
    private javax.swing.JButton deleteBtn;
    private JTextField keyIdleTimeField;
    private JTextField keyNameField;
    private JTextField keySizeField;
    private javax.swing.JLabel keyTypeLabel;
    private JTextField keyTTLField;

//    private javax.swing.JButton reloadBtn;
//    private javax.swing.JButton renameBtn;
//    private javax.swing.JButton ttlBtn;
    private JLabel saveLabel, cancelLabel, addRowLabel, addRowLeftLabel, deleteRowLabel, deleteLabel, reloadLabel, firstPageLabel, previousPageLabel, nextPageLabel, lastPageLabel, totalPage;
    private PlaceholderTextField gotoPagefield, scoreField;

    private JLabel fieldInfoLabel, valueInfoLabel, scoreInfoLabel;
    private RComboBox<DicBean> selectKeyViewComn, selectValueViewComn;
    private RTextArea fieldArea, valueArea;
    private Box hBox1, hBox2, hBox3, hBox4, hBox5, hBox6;
    private Component createVerticalStrut1, createVerticalStrut2, createVerticalStrut3, createVerticalStrut4;
    private RButton searchButton;
    private PlaceholderTextField searchTextField;
    private RComboBox<Integer> selectPageComboBox;

    private DicBean plaintextDic= new DicBean("1", LarkFrame.getI18nText(I18nKey.RedisResource.PLAINTEXT));
    private DicBean jsonDic=new DicBean("2", "Json");
    private DicBean hexPlainDic=new DicBean("3", "Hex Plain");
    private DicBean hexDic=new DicBean("4", "Hex");
    private int valueInfoPanelWidth = 185;
    private int aroundPanelWidth = 24;
    private int initRow = 100;
    private Integer[] pageList = new Integer[]{initRow, 200, 500, 1000,10000};
    private List<Icon> headersIcon = new ArrayList<>(4);


    /**
     * Creates new form RedisTabbedPanel2
     */
    public RedisTabbedPanel(RTreeNode treeNode, JTree tree) {
        this.treeNode = treeNode;
        this.tree = tree;

        pageBean.setRows(initRow);
        this.getKeyInfo(resultRes -> SwingTools.showMessageErrorDialog(null, resultRes.getMsg()));
        if (redisKeyInfo == null) {
            this.redisKeyInfo = new RedisKeyInfo();
            this.redisKeyInfo.setType(RedisType.STRING);
            this.redisKeyInfo.setTtl(-1L);
            this.redisKeyInfo.setIdleTime(0L);
            this.redisKeyInfo.setCursor(ScanCursor.INITIAL);
        }
        this.initComponents();
        this.initValueInfoPanel();
        this.initPagePanel();
        this.initLeftPanel();
        this.initTable();
        this.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                jSplitPanel.setDividerLocation(RedisTabbedPanel.this.getWidth() - valueInfoPanelWidth - aroundPanelWidth * 2);
            }

        });
        rightTablePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0), BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(160, 160, 160))));
        redisBaseInfoBgPanel.setBackground(PublicConstant.RColor.themeColor);
    }
//---------------------------------------------------Baseinfo start---------------------------------------------------------------------

    /**
     * @return
     */
    private JPanel getBasePanel() {
        int rowHeight = 22;
        basePanel = new JPanel();
//        basePanel.setLayout(new javax.swing.BoxLayout(basePanel, javax.swing.BoxLayout.X_AXIS));
        keyTypeLabel = new javax.swing.JLabel();
        keyNameField = new PlaceholderTextField(20);
        keySizeField = new PlaceholderTextField(20);
        keyIdleTimeField = new PlaceholderTextField(20);
        keyTTLField = new PlaceholderTextField(20);
        JLabel sizeLabel = new javax.swing.JLabel("Size:");
        JLabel idleTimeLabel = new javax.swing.JLabel("IdleTime:");
        JLabel ttlLabel = new javax.swing.JLabel("TTL:");
        searchButton = new RButton();
        searchTextField = new PlaceholderTextField(20);
        searchTextField.setPlaceholder("输入筛选条件");
        searchTextField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
            @Override
            public void insertUpdate(DocumentEvent e) {
                searchTextChange(e);
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                searchTextChange(e);
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
            }


        });
//        keyTypeLabel.setFont(new java.awt.Font("宋体", 1, 15)); // NOI18N
        keyNameField.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                keyNameFieldMousePressed(evt);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    renameKey();
                }
            }

        });
        keyTTLField.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                ttlBtnMousePressed(evt);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editKeyTtlInfo();
                }
            }

        });
        updateBasePanel();

        setLabelSize(sizeLabel, 65, rowHeight);
        setLabelSize(idleTimeLabel, 65, rowHeight);
        setLabelSize(idleTimeLabel, 65, rowHeight);
        setLabelSize(ttlLabel, 65, rowHeight);
        setLabelSize(keyTypeLabel, 65, rowHeight);
        keySizeField.setBackground(PublicConstant.RColor.grapInputColor);
        keyIdleTimeField.setBackground(PublicConstant.RColor.grapInputColor);
        keyNameField.setEditable(false);
        keySizeField.setEditable(false);
        keyTTLField.setEditable(false);
        keyIdleTimeField.setEditable(false);

        JPanel redisKeyAttrPanel = new JPanel();
        JPanel emptyyAttrPanel = new JPanel();
        redisKeyAttrPanel.setBackground(PublicConstant.RColor.themeColor);
        emptyyAttrPanel.setBackground(PublicConstant.RColor.themeColor);
        redisKeyAttrPanel.setLayout(new BoxLayout(redisKeyAttrPanel, BoxLayout.Y_AXIS));
        redisKeyAttrPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10), BorderFactory.createEtchedBorder()));

        basePanel.add(redisKeyAttrPanel);
        basePanel.add(emptyyAttrPanel);

        Insets insets = new Insets(3, 10, 0, 5);
        redisKeyAttrPanel.add(SwingTools.createTextRow(keyTypeLabel, keyNameField, 0.1, 0.9, redisKeyAttrPanel.getWidth(), rowHeight, PublicConstant.RColor.themeColor, insets, insets));
        redisKeyAttrPanel.add(SwingTools.createTextRow(sizeLabel, keySizeField, 0.1, 0.9, redisKeyAttrPanel.getWidth(), rowHeight, PublicConstant.RColor.themeColor, insets, insets));
        redisKeyAttrPanel.add(SwingTools.createTextRow(ttlLabel, keyTTLField, 0.1, 0.9, redisKeyAttrPanel.getWidth(), rowHeight, PublicConstant.RColor.themeColor, insets, insets));
        redisKeyAttrPanel.add(SwingTools.createTextRow(idleTimeLabel, keyIdleTimeField, 0.1, 0.9, redisKeyAttrPanel.getWidth(), rowHeight, PublicConstant.RColor.themeColor, insets, insets));

        redisKeyAttrPanel.add(Box.createHorizontalGlue());

        searchButton.setOpaque(true);
        searchButton.setForeground(Color.WHITE);
        searchButton.setPreferredSize(new Dimension(80, aroundPanelWidth));
        searchButton.setMaximumSize(new Dimension(80, aroundPanelWidth));

        serachPanel.add(searchButton);
        serachPanel.add(searchTextField);
        serachPanel.setOpaque(false);
        serachPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, aroundPanelWidth, PublicConstant.RColor.themeColor));
        return basePanel;
    }

    /**
     * 过滤表格
     * @param e
     */
    private void searchTextChange(DocumentEvent e) {
        if(RedisType.LIST.equals(redisKeyInfo.getType())){
            refreshTable();
        }else{
            this.updateUI(this.treeNode, this.pageBean);
        }

    }

    private void updateBasePanel() {
//        keyTypeLabel.setText(redisKeyInfo.getType() + ":");
        keyTypeLabel.setText("Key:");
        keyNameField.setText(redisKeyInfo.getKey());
        keyTTLField.setText(redisKeyInfo.getTtl() + "");
        keySizeField.setText(redisKeyInfo.getSize() + "");
        keyIdleTimeField.setText("" + redisKeyInfo.getIdleTime());
        searchButton.setText( String.format(TYPEHTML, redisKeyInfo.getType()));
//        searchButton.setText(redisKeyInfo.getType().name());
        searchTextField.setVisible(true);
        switch (redisKeyInfo.getType()) {
            case LIST:
                searchButton.setBackground(new Color(2, 122, 180));
                break;
            case SET:
                searchButton.setBackground(new Color(245, 185, 15));
                break;
            case HASH:
                searchButton.setBackground(new Color(116, 39, 135));
                break;
            case ZSET:
                searchButton.setBackground(new Color(24, 170, 110));
                break;
            case STRING:
                searchTextField.setVisible(false);
            default:
                searchButton.setBackground(new Color(76,174,81));
                break;

        }

    }

    private void keyNameFieldMousePressed(MouseEvent evt) {
        if (evt.getButton() != MouseEvent.BUTTON3) {
            return;
        }
        JPopupMenu popMenu = new JRedisPopupMenu();// 菜单
        JMenuItem copyKeyItem = MenuManager.getInstance().getJMenuItem(I18nKey.RedisResource.COPY, PublicConstant.Image.copy);
        JMenuItem renameKeyItem = MenuManager.getInstance().getJMenuItem(I18nKey.RedisResource.REMAME, PublicConstant.Image.rename);
        copyKeyItem.setMnemonic('C');
        copyKeyItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        copyKeyItem.addActionListener(e -> {
            Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable tText = new StringSelection(keyNameField.getText());
            clip.setContents(tText, null);
        });
        renameKeyItem.addActionListener(e -> {
            renameKey();
        });
        popMenu.add(renameKeyItem);
        popMenu.add(copyKeyItem);
        popMenu.show(keyNameField, evt.getX(), evt.getY());
    }

    private void renameKey() {
        String name = SwingTools.showInputDialog(null, "NEW NAME:", "Rename key", redisKeyInfo.getKey());
        //取消
        if (name == null) {
            return;
        }
        if (StringUtils.isNotBlank(name)) {
            ResultRes<Boolean> resultRes = BaseController.dispatcher(() -> redisConnectService.remamenx(redisKeyInfo.getId(), redisKeyInfo.getDb(), redisKeyInfo.getKey(), name));

            if (!resultRes.isRet()) {
                SwingTools.showMessageErrorDialog(null, "重命名失败:" + resultRes.getMsg());
            } else if (!resultRes.getData()) {
                SwingTools.showMessageErrorDialog(null, "Cant't rename name: Key with new name already exist in database or original key was removed");
            } else {
                redisKeyInfo.setKey(name);
                keyNameField.setText(name);
                RedisTreeItem redisTreeItem = (RedisTreeItem) treeNode.getUserObject();
                redisTreeItem.updateKeyName(name);
                JTabbedPane parent = (JTabbedPane) this.getParent();
                RTabbedPane.ButtonClose buttonClose = (RTabbedPane.ButtonClose) parent.getTabComponentAt(parent.getSelectedIndex());
                if (buttonClose != null) {
                    buttonClose.setText(name);
                }
                tree.updateUI();
            }
        }else{
            SwingTools.showMessageErrorDialog(null, "重命名失败:名称不能为空" );
            this.renameKey();
        }


    }

    private void ttlBtnMousePressed(java.awt.event.MouseEvent evt) {
        if (evt.getButton() != MouseEvent.BUTTON3) {
            return;
        }
        JPopupMenu popMenu = new JRedisPopupMenu();// 菜单
        JMenuItem editKeyItem = MenuManager.getInstance().getJMenuItem(LarkFrame.getI18nText(I18nKey.RedisResource.MENU_EDIT) + " TTL", PublicConstant.Image.edit);
        popMenu.add(editKeyItem);
        popMenu.show(keyTTLField, evt.getX(), evt.getY());
        editKeyItem.addActionListener(e -> {
            editKeyTtlInfo();
        });

    }

    private void editKeyTtlInfo() {
        String ttl = SwingTools.showInputDialog(null, "NEW TTL:", "Set key TTL", redisKeyInfo.getTtl());
        //取消
        if (ttl == null) {
            return;
        }
        boolean valid = true;
        if (StringUtils.isNotBlank(ttl)) {
            try {
                Integer valueOf = Integer.valueOf(ttl);
                Integer newTTL = valueOf < 0 ? -1 : valueOf;
                if (newTTL.equals(redisKeyInfo.getTtl().intValue())) {
                    return;
                }
                ResultRes<Boolean> resultRes = BaseController.dispatcher(() -> redisConnectService.expireKey(redisKeyInfo.getId(), redisKeyInfo.getDb(), redisKeyInfo.getKey(), newTTL));
                if (resultRes.isRet() && resultRes.getData()) {
                    redisKeyInfo.setTtl(Long.valueOf(newTTL));
                    keyTTLField.setText(newTTL + "");
                } else if (!resultRes.isRet()) {
                    SwingTools.showMessageErrorDialog(null, "设置TTL失败:" + resultRes.getMsg());

                } else {
                    SwingTools.showMessageErrorDialog(null, "设置TTL失败");
                }
            } catch (NumberFormatException ex) {
                valid = false;
            }
        } else {
            valid = false;
        }
        if (!valid) {
            SwingTools.showMessageErrorDialog(null, "请输入数字");
            this.ttlBtnMousePressed(null);
            return;
        }
    }

    //---------------------------------------------------Baseinfo end---------------------------------------------------------------------
    //---------------------------------------------------table start---------------------------------------------------------------------
    private Vector[] getTableContext(RedisKeyInfo redisKeyInfo) {
        //表格
        // 表头（列名）
        Vector<String> columnNames = new Vector(4);
        Vector<Vector<TableColumnBean>> data = new Vector();
        headersIcon.clear();
        Vector<TableColumnBean> rowData = null;
        columnNames.add("");
        headersIcon.add(PublicConstant.Image.logo_16);
        String searchText = StringUtils.isBlank(searchTextField.getText()) || "*".equals(searchTextField.getText())? ".*":searchTextField.getText().trim();
        Pattern pattern ;
        if(StringUtils.isBlank(searchTextField.getText())){
            pattern = compile(".*");
        }else{
            pattern = compile(".*"+searchTextField.getText().trim().replaceAll("\\*",".*")+".*");
        }
        int index = 0;
        int startIndex = redisKeyInfo.getPageBean().getStartIndex();
        switch (redisKeyInfo.getType()) {
            case STRING:
                columnNames.add("VALUE");
                headersIcon.add(PublicConstant.Image.database);
                rowData = new Vector<>();
                rowData.add(new TableColumnBean(PublicConstant.StingType.INDEX, (startIndex+index)+"",startIndex+index));
                rowData.add(new TableColumnBean(PublicConstant.StingType.TEXT,redisKeyInfo.getValue(),startIndex+index));
                data.add(rowData);

                break;
            case LIST:
                columnNames.add("VALUE");
                headersIcon.add(PublicConstant.Image.database);
                java.util.List<String> list = redisKeyInfo.getValueList();
                for (String s : list) {
                    if(pattern.matcher(s).matches()){
                        rowData = new Vector<>();
                        rowData.add(new TableColumnBean(PublicConstant.StingType.INDEX,(startIndex+index)+"",startIndex+index));
                        rowData.add(new TableColumnBean(PublicConstant.StingType.TEXT,s,startIndex+index));
                        data.add(rowData);
                    }
                    index++;
                }
                break;
            case SET:
                columnNames.add("MEMBER");
                headersIcon.add(PublicConstant.Image.database);
                List<String> set = redisKeyInfo.getValueSet();
                for (String s : set) {
                    rowData = new Vector<>();
                    rowData.add(new TableColumnBean(PublicConstant.StingType.INDEX,(startIndex+index)+"",startIndex+index));
                    rowData.add(new TableColumnBean(PublicConstant.StingType.TEXT,s,startIndex+index));
                    data.add(rowData);
                    index++;
                }
                break;
            case HASH:
                columnNames.add("FIELD");
                columnNames.add("VALUE");
                headersIcon.add(PublicConstant.Image.database);
                headersIcon.add(PublicConstant.Image.database);
                Map<String, String> valueHash = redisKeyInfo.getValueHash();
                for (Map.Entry<String, String> entry : valueHash.entrySet()) {
                    rowData = new Vector<>();
                    rowData.add(new TableColumnBean(PublicConstant.StingType.INDEX,(startIndex+index)+"",startIndex+index));
                    rowData.add(new TableColumnBean(PublicConstant.StingType.TEXT,entry.getKey(),startIndex+index));
                    rowData.add(new TableColumnBean(PublicConstant.StingType.TEXT,entry.getValue(),startIndex+index));
                    data.add(rowData);
                    index++;
                }
                break;
            case ZSET:
                columnNames.add("SCORE");
                columnNames.add("MEMBER");
                headersIcon.add(PublicConstant.Image.database);
                headersIcon.add(PublicConstant.Image.database);
                List<ScoredValue<String>> valueZSet = redisKeyInfo.getValueZSet();
                Iterator<ScoredValue<String>> iterator = valueZSet.iterator();
                while (iterator.hasNext()) {
                    final ScoredValue<String> next = iterator.next();
                    rowData = new Vector<>();
                    rowData.add(new TableColumnBean(PublicConstant.StingType.INDEX,(startIndex+index)+"",startIndex+index));

                    DecimalFormat decimalFormat = new DecimalFormat("###################.###########");
                    rowData.add(new TableColumnBean(PublicConstant.StingType.TEXT,decimalFormat.format(next.getScore()),startIndex+index));
                    rowData.add(new TableColumnBean(PublicConstant.StingType.TEXT,next.getValue(),startIndex+index));

                    data.add(rowData);
                    index++;
                }
                break;
            default:
                break;
        }
        columnNames.add("");
        this.actRow = data.size();
        return new Vector[]{columnNames, data};

    }

    /**
     * @return
     */
    private TableModel getTableModel() {
        tableContext = getTableContext(redisKeyInfo);
        return new RTableModel(tableContext[1], tableContext[0]) {
            private static final long serialVersionUID = 1L;

            //表格不允许被编辑
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

    }

    private void initTable() {
        updateTableStyle();
        redisDataTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {

            }
        });
        redisDataTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int column = redisDataTable.getSelectedColumn();
                    int row = redisDataTable.getSelectedRow();
                    if (row >= actRow) {
                        return;
                    }
                    valueArea.setEditable(true);
                    ((ValueInfoPanel) valueInfoPanel).setValueColumnBean(null);
                    ((ValueInfoPanel) valueInfoPanel).setKeyColumnBean(null);
                    switch (redisKeyInfo.getType()) {
                        case STRING:
                        case LIST:
                        case SET:
                            if (column == 1) {
                                changeValueAreaText(row, column, (TableColumnBean)redisDataTable.getValueAt(row, column));
                            }
                            break;
                        case HASH:
                            if (column == 1 || column == 2) {
                                changeValueAreaText(row, column, (TableColumnBean)redisDataTable.getValueAt(row, 2));
                                TableColumnBean keyTextColumne = (TableColumnBean) redisDataTable.getValueAt(row, 1);
                                ((ValueInfoPanel) valueInfoPanel).setKeyColumnBean(keyTextColumne);
                                if(StringUtils.isText(keyTextColumne.getValue())){
                                    selectKeyViewComn.setSelectedItem(plaintextDic);
                                }else{
                                    selectKeyViewComn.setSelectedItem(hexPlainDic);
                                }
                                fieldArea.setText(keyTextColumne.toString());
                                setFieldInfoLabelText(keyTextColumne.toString().getBytes().length);
                                fieldArea.setEditable(true);
                            }
                            break;
                        case ZSET:
                            if (column == 1 || column == 2) {
                                changeValueAreaText(row, column, (TableColumnBean)redisDataTable.getValueAt(row, 2));
                                String scoreText = StringUtils.defaultEmptyToString(redisDataTable.getValueAt(row, 1));
                                scoreField.setText(scoreText);
                                scoreField.setEditable(true);
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        });
    }


    private void updateTableStyle() {

        // 设置表格内容颜色
        redisDataTable.setForeground(PublicConstant.RColor.tableForeground);                   // 字体颜色
//        redisDataTable.setFont(new Font(null, Font.PLAIN, 14));      // 字体样式
        redisDataTable.setSelectionBackground(PublicConstant.RColor.tableSelectBackground);     // 选中后字体背景
        redisDataTable.setShowGrid(true);
        // 网格颜色
        redisDataTable.setGridColor(PublicConstant.RColor.tableGridColor);
        // 设置表头
        JTableHeader tableHeader = redisDataTable.getTableHeader();
        ((DefaultTableCellRenderer) tableHeader.getDefaultRenderer())
                .setHorizontalAlignment(DefaultTableCellRenderer.CENTER);// 列头内容居中

        //   tableHeader.setFont(new Font(null, Font.BOLD, 14));  // 设置表头名称字体样式
        tableHeader.setForeground(PublicConstant.RColor.tableHeaderForeground);                // 设置表头名称字体颜色
        tableHeader.setBackground(PublicConstant.RColor.tableHeaderBackground);
//        tableHeader.setResizingAllowed(false);               // 设置不允许手动改变列宽
        tableHeader.setReorderingAllowed(false);             // 设置不允许拖动重新排序各列
        tableHeader.setPreferredSize(new Dimension(tableHeader.getWidth(), tableHeaderRowHeight));

        // 设置行高
        redisDataTable.setRowHeight(tableRowHeight);

        updateTableColumeWidth();
    }

    private void setTableColor() {
        makeFace(redisDataTable, PublicConstant.RColor.tableOddForeground, PublicConstant.RColor.tableEvenBackground);
        DefaultTableCellRenderer tableCellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {

                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

//                  setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(122, 138, 153)));
                setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(122, 138, 153)));
                setBackground(new Color(240,240,240));
                return component;

            }

        };


        redisDataTable.getColumn(redisDataTable.getColumnName(0)).setCellRenderer(tableCellRenderer);
    }

    /**
     * 設置奇數偶數行換行顯示
     *
     * @param table
     * @param oddBackground
     * @param evenBackground
     */
    @SuppressWarnings("serial")
    public void makeFace(JTable table, Color oddBackground, Color evenBackground) {
        RTableModel model = (RTableModel) redisDataTable.getModel();
        try {
            DefaultTableCellRenderer tcr = new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                               boolean hasFocus, int row, int column) {
                    if (row >= actRow) {
                        setBackground(Color.WHITE);
                    } else if (row % 2 == 0) {
                        // 设置奇数行底色
                        setBackground(oddBackground);
                    } else if (row % 2 == 1) {
                        // 设置偶数行底色
                        setBackground(evenBackground);
                    }
                    return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                }
            };

            for (int i = 1; i < table.getColumnCount(); i++) {
                table.getColumn(table.getColumnName(i)).setCellRenderer(tcr);
//                SwingTools.setTableHeaderColor(redisDataTable,i,PublicConstant.RColor.tableHeaderBackground);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void updateTableColumeWidth() {
        // 第一列列宽设置
        TableColumn tc = redisDataTable.getColumnModel().getColumn(0);
        int rowCount = redisDataTable.getRowCount();
        if(rowCount < 1000){
            tc.setMaxWidth(36);
            tc.setPreferredWidth(36);
        }else if(rowCount < 10000){
            tc.setMaxWidth(45);
            tc.setPreferredWidth(40);
        }else  {
            tc.setMaxWidth(55);
            tc.setPreferredWidth(45);
        }

        setTableColor();
        RTableModel model = (RTableModel) redisDataTable.getModel();
        //设置表头 添加图标
//        for (int i = 1; i < model.getColumnCount() - 1; i++) {
//            TableCellRenderer renderer = new JComponentTableCellRenderer();
//            TableColumn t1c = redisDataTable.getColumnModel().getColumn(i);
//            JLabel label = new JLabel(tableContext[0].get(i) + "", headersIcon.get(i), JLabel.CENTER);
//            t1c.setHeaderRenderer(renderer);
//            t1c.setHeaderValue(label);
////            t1c.setMinWidth(150);
////            t1c.setMaxWidth(300);
//        }

    }
    //---------------------------------------------------table end---------------------------------------------------------------------

    //---------------------------------------------------right value panl start---------------------------------------------------------------------
    private void initValueInfoPanel() {
        valueInfoPanel.setLayout(new BoxLayout(valueInfoPanel, BoxLayout.Y_AXIS));
        valueInfoPanel.setBackground(PublicConstant.RColor.themeColor);
        hBox1 = Box.createHorizontalBox();
        hBox2 = Box.createHorizontalBox();
        hBox3 = Box.createHorizontalBox();
        hBox4 = Box.createHorizontalBox();
        hBox5 = Box.createHorizontalBox();
        hBox6 = Box.createHorizontalBox();
        fieldInfoLabel = new JLabel();
        valueInfoLabel = new JLabel();
        setFieldInfoLabelText(0);
        setValueInfoLabelText(0);
        scoreInfoLabel = new JLabel("<html><p style='font-size:10px;color:black' >&nbsp;Score: </p></html>");
        fieldArea = new RTextArea(5, 20);
        fieldArea.setLineWrap(true);
        fieldArea.setEditable(false);
        valueArea = new RTextArea(5, 20);
        valueArea.setLineWrap(true);
        valueArea.setEditable(false);
        scoreField = new PlaceholderTextField(20);
        scoreField.setEditable(false);
        selectKeyViewComn = new RComboBox<>(20);
        selectKeyViewComn.setModel(new javax.swing.DefaultComboBoxModel<>(new DicBean[]{plaintextDic,jsonDic,hexPlainDic,hexDic,}));
        selectKeyViewComn.setMaximumSize(new java.awt.Dimension(32767, 25));
        selectKeyViewComn.setMinimumSize(new java.awt.Dimension(50, 25));
        selectKeyViewComn.setPreferredSize(new java.awt.Dimension(60, 25));
        Font font =  new Font(selectKeyViewComn.getFont().getName(), Font.PLAIN,12);
        selectKeyViewComn.setFont(font);
        selectKeyViewComn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if(evt.getActionCommand().equals("comboBoxChanged")){
                    DicBean dicBean = (DicBean) selectKeyViewComn.getSelectedItem();
                    TableColumnBean tableColumnBean = ((ValueInfoPanel) valueInfoPanel).getKeyColumnBean();
                    String keyAreaText =  getSelectDicText(dicBean, tableColumnBean,fieldArea);
                    fieldArea.setText(keyAreaText);
                    setValueInfoLabelText(keyAreaText.getBytes().length);
                }
            }
        });

        selectValueViewComn = new RComboBox<>(20);
        selectValueViewComn.setModel(new javax.swing.DefaultComboBoxModel<>(new DicBean[]{plaintextDic,jsonDic,hexPlainDic,hexDic,}));
        selectValueViewComn.setMaximumSize(new java.awt.Dimension(32767, 25));
        selectValueViewComn.setMinimumSize(new java.awt.Dimension(50, 25));
        selectValueViewComn.setPreferredSize(new java.awt.Dimension(60, 25));
        selectValueViewComn.setFont(font);
        selectValueViewComn.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED){
                    DicBean dicBean = (DicBean) selectValueViewComn.getSelectedItem();
                    TableColumnBean tableColumnBean = ((ValueInfoPanel) valueInfoPanel).getValueColumnBean();
                    String valueAreaText = getSelectDicText(dicBean, tableColumnBean, valueArea);
                    valueArea.setText(valueAreaText);
                    setValueInfoLabelText(valueAreaText.getBytes().length);
                }else if(e.getStateChange() == ItemEvent.DESELECTED){

                }

            }
        });
        ValueInfoPanel valueInfo = (ValueInfoPanel) valueInfoPanel;
        valueArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent e) {
                saveLabelEnabled();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                saveLabelEnabled();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                saveLabelEnabled();
            }
        });
        valueArea.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                SwingTools.copyMenuByValue(evt,valueArea);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    MagnifyTextDialog magnifyTextDialog = MagnifyTextDialog.getInstance();
                    magnifyTextDialog.setText(valueArea.getText());
                    magnifyTextDialog.open();
                }
            }
        });
        fieldArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent e) {
                saveLabelEnabled();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                saveLabelEnabled();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                saveLabelEnabled();
            }
        });
        fieldArea.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                SwingTools.copyMenuByValue(evt,fieldArea);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    MagnifyTextDialog magnifyTextDialog = MagnifyTextDialog.getInstance();
                    magnifyTextDialog.setText(fieldArea.getText());
                    magnifyTextDialog.open();
                }
            }
        });
        scoreField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent e) {
                saveLabelEnabled();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                saveLabelEnabled();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                //saveLabelEnabled(scoreField,1);
            }
        });
        SwingTools.addTextCopyMenu(scoreField);
        hBox1.add(fieldInfoLabel);
        hBox1.add(Box.createHorizontalGlue());
        hBox1.add(selectKeyViewComn);
        hBox1.setPreferredSize(new Dimension(hBox1.getPreferredSize().width, 28));

        hBox2.add(Box.createHorizontalStrut(5));
        hBox2.add(fieldArea.getJScrollPane());
        hBox2.setPreferredSize(new Dimension(hBox2.getPreferredSize().width, 28));
        hBox2.add(Box.createHorizontalStrut(5));

        hBox5.add(scoreInfoLabel);
        hBox5.add(Box.createHorizontalGlue());
        hBox5.setPreferredSize(new Dimension(hBox5.getPreferredSize().width, 28));

        hBox6.add(Box.createHorizontalStrut(5));
        scoreField.setPreferredSize(new Dimension(scoreField.getPreferredSize().width, 28));
        scoreField.setMaximumSize(new Dimension(32767, 28));
        hBox6.add(scoreField);
        hBox6.setPreferredSize(new Dimension(hBox6.getPreferredSize().width, 28));
        hBox6.setMaximumSize(new Dimension(32767, 28));
        hBox6.add(Box.createHorizontalStrut(5));

        hBox3.add(valueInfoLabel);
        hBox3.add(Box.createHorizontalGlue());
        hBox3.add(selectValueViewComn);
        hBox3.setPreferredSize(new Dimension(hBox3.getPreferredSize().width, 28));

        hBox4.add(Box.createHorizontalStrut(5));
        hBox4.add(valueArea.getJScrollPane());
        hBox4.setPreferredSize(new Dimension(hBox4.getPreferredSize().width, 28));
        hBox4.add(Box.createHorizontalStrut(5));
        createVerticalStrut1 = Box.createVerticalStrut(5);
        createVerticalStrut2 = Box.createVerticalStrut(5);
        createVerticalStrut3 = Box.createVerticalStrut(5);
        createVerticalStrut4 = Box.createVerticalStrut(5);
        valueInfoPanel.add(createVerticalStrut1);
        valueInfoPanel.add(hBox1);
        valueInfoPanel.add(createVerticalStrut2);
        valueInfoPanel.add(hBox2);
        valueInfoPanel.add(createVerticalStrut3);
        valueInfoPanel.add(hBox5);
        valueInfoPanel.add(createVerticalStrut4);
        valueInfoPanel.add(hBox6);

        valueInfoPanel.add(Box.createVerticalStrut(10));
        valueInfoPanel.add(hBox3);
        valueInfoPanel.add(Box.createVerticalStrut(5));
        valueInfoPanel.add(hBox4);
        valueInfoPanel.add(Box.createVerticalStrut(10));
        togglevalueInfo();

    }

    /**
     * 选择不同显示值
     * @param dicBean
     * @param tableColumnBean
     * @param textArea
     * @return
     */
    private String getSelectDicText(DicBean dicBean, TableColumnBean tableColumnBean, RTextArea textArea) {
        String text = "";
        textArea.setEditable(false);
        if ("1".equals(dicBean.getCode())) {
            text = tableColumnBean.getValue();
            textArea.setEditable(true);
        } else if ("2".equals(dicBean.getCode())) {
            text = GsonUtil.toPrettyFormat(tableColumnBean.getValue());
        } else if ("3".equals(dicBean.getCode())) {
            text = TableColumnBean.getHexStringValue(tableColumnBean.getValue());
        } else if ("4".equals(dicBean.getCode())) {
            text = StringUtils.bytesToHexString(tableColumnBean.getValue().getBytes(), true);
        }
        saveLabelEnabled();
        return text;
    }

    private void changeValueAreaText(int row, int column, TableColumnBean tableColumnBean) {
        String valueAreaText = tableColumnBean.toString();
        ((ValueInfoPanel) valueInfoPanel).setSelectColume(column);
        ((ValueInfoPanel) valueInfoPanel).setSelectRow(row);
        ((ValueInfoPanel) valueInfoPanel).setIndex(Integer.valueOf(StringUtils.defaultEmptyToString(redisDataTable.getValueAt(row, 0))));
        ((ValueInfoPanel) valueInfoPanel).setValueColumnBean(tableColumnBean);
        if(StringUtils.isText(tableColumnBean.getValue())){
            selectValueViewComn.setSelectedItem(plaintextDic);
        }else{
            selectValueViewComn.setSelectedItem(hexPlainDic);
        }
        valueArea.setText(valueAreaText);
        setValueInfoLabelText(valueAreaText.getBytes().length);
        if (!RedisType.STRING.equals(this.redisKeyInfo.getType())) {
            if (redisDataTable.getSelectedRowCount() > 0) {
                this.deleteRowLabel.setEnabled(true);
            } else {
                this.deleteRowLabel.setEnabled(false);
            }
        }
    }

    private void setValueInfoLabelText(int length) {
        valueInfoLabel.setText("<html><p style='font-size:8px;color:black' >&nbsp;Value: <span style='color:#A0A0B1'>Size: " + length + " bytes</span></p></html>");

    }

    private void setFieldInfoLabelText(int length) {
        fieldInfoLabel.setText("<html><p style='font-size:8px;color:black' >&nbsp;Field: <span style='color:#A0A0B1'>Size: " + length + " bytes</span></p></html>");

    }

    private void togglevalueInfo() {
        if (RedisType.HASH.equals(redisKeyInfo.getType())) {
            toggleKeyInfo(true);
            togglescoreFieldInfo(false);
        } else if (RedisType.ZSET.equals(redisKeyInfo.getType())) {
            toggleKeyInfo(false);
            togglescoreFieldInfo(true);
        } else {
            toggleKeyInfo(false);
            togglescoreFieldInfo(false);
        }
    }

    private void togglescoreFieldInfo(boolean flag) {
        hBox5.setVisible(flag);
        hBox6.setVisible(flag);
        createVerticalStrut3.setVisible(flag);
        createVerticalStrut4.setVisible(flag);
    }

    private void toggleKeyInfo(boolean flag) {
        hBox1.setVisible(flag);
        hBox2.setVisible(flag);
        createVerticalStrut1.setVisible(flag);
        createVerticalStrut2.setVisible(flag);
    }

    //---------------------------------------------------right value panl end---------------------------------------------------------------------
    //---------------------------------------------------bottom page  panl start---------------------------------------------------------------------
    private void initPagePanel() {

        saveLabel = new JLabel("Save");
        saveLabel.setToolTipText("保存");

        saveLabel.setIcon(PublicConstant.Image.overtime_done);
        cancelLabel = new JLabel("Cancel");
        cancelLabel.setToolTipText("取消");
        cancelLabel.setIcon(PublicConstant.Image.cancel);
        deleteLabel = new JLabel("Delete");
        deleteLabel.setToolTipText("删除");
        deleteLabel.setIcon(PublicConstant.Image.delete);
        addRowLeftLabel = new JLabel();
        addRowLeftLabel.setIcon(PublicConstant.Image.row_add_green);
        addRowLabel = new JLabel();
        addRowLabel.setIcon(PublicConstant.Image.row_add);

        deleteRowLabel = new JLabel();
        deleteRowLabel.setToolTipText("删除当前行");
        deleteRowLabel.setIcon(PublicConstant.Image.row_delete);
        reloadLabel = new JLabel();
        reloadLabel.setToolTipText("重新加载");
        reloadLabel.setIcon(PublicConstant.Image.page_reload);
        firstPageLabel = new JLabel();
        firstPageLabel.setToolTipText("移动至首页");
        firstPageLabel.setIcon(PublicConstant.Image.result_first);
        previousPageLabel = new JLabel();
        previousPageLabel.setToolTipText("移动到上一页");
        previousPageLabel.setIcon(PublicConstant.Image.result_previous);
        nextPageLabel = new JLabel();
        nextPageLabel.setToolTipText("移动到下一页");
        nextPageLabel.setIcon(PublicConstant.Image.result_next);
        lastPageLabel = new JLabel();
        lastPageLabel.setToolTipText("移动到尾页");
        lastPageLabel.setIcon(PublicConstant.Image.result_last);
        gotoPagefield = new PlaceholderTextField(3);
        gotoPagefield.setToolTipText("跳转页");
        gotoPagefield.setMaximumSize(new java.awt.Dimension(20, borderWidth - 1));
        gotoPagefield.setMinimumSize(new java.awt.Dimension(20, borderWidth - 1));
        gotoPagefield.setPreferredSize(new Dimension(20, borderWidth - 1));
        totalPage = new JLabel();

        addToPagePanel(saveLabel, true, borderWidth + 10, false, null);
        addToPagePanel(cancelLabel);
        addToPagePanel(deleteLabel);
        JLabel jSeparator = new JLabel();
        jSeparator.setIcon(PublicConstant.Image.separator_v_solid);
        addToPagePanel(jSeparator);
        addToPagePanel(addRowLeftLabel);
        addToPagePanel(addRowLabel);
        addToPagePanel(deleteRowLabel);
        addToPagePanel(reloadLabel);
        jSeparator = new JLabel();
        jSeparator.setIcon(PublicConstant.Image.separator_v);
        addToPagePanel(jSeparator);
        //分页
        selectPageComboBox = new RComboBox<>(20);

        selectPageComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(pageList));
        selectPageComboBox.setMaximumSize(new java.awt.Dimension(60, borderWidth));
        selectPageComboBox.setMinimumSize(new java.awt.Dimension(60, borderWidth));
        selectPageComboBox.setPreferredSize(new java.awt.Dimension(60, borderWidth));
        selectPageComboBox.addItemListener(e -> {
            pageBean.setRows((Integer) e.getItem());
            RedisTabbedPanel.this.updateUI(treeNode, pageBean);
        });
        addToPagePanel(selectPageComboBox);
        addToPagePanel(firstPageLabel);
        addToPagePanel(previousPageLabel);
        addToPagePanel(nextPageLabel);
        addToPagePanel(lastPageLabel);
        addToPagePanel(gotoPagefield);
        addToPagePanel(totalPage);
        pagePanel.add(Box.createHorizontalGlue());

        SwingTools.addMouseClickedListener(deleteLabel, e -> deleteBtnActionPerformed(e));
        SwingTools.addMouseClickedListener(reloadLabel, e -> reloadBtnMousePressed(e));
        SwingTools.addMouseClickedListener(firstPageLabel, e -> pageMousePPressed(1));
        SwingTools.addMouseClickedListener(previousPageLabel, e -> pageMousePPressed(redisKeyInfo.getPageBean().getPage() - 1));
        SwingTools.addMouseClickedListener(nextPageLabel, e -> pageMousePPressed(redisKeyInfo.getPageBean().getPage() + 1));
        SwingTools.addMouseClickedListener(lastPageLabel, e -> pageMousePPressed(redisKeyInfo.getPageBean().getTotalPage()));

        SwingTools.enterPressesWhenFocused(gotoPagefield, e -> {
            String text = gotoPagefield.getText();
            try {
                Integer gotoPage = Integer.valueOf(text);
                if (gotoPage > this.redisKeyInfo.getPageBean().getTotalPage()) {
                    gotoPage = this.redisKeyInfo.getPageBean().getTotalPage();
                }
                pageMousePPressed(gotoPage);
            } catch (NumberFormatException ex) {
                SwingTools.showMessageErrorDialog(null, "请输入正确的跳转页");
            }

        });
        SwingTools.addMouseClickedListener(cancelLabel, e -> {
            int selectRow = ((ValueInfoPanel) valueInfoPanel).getSelectRow();
            switch (redisKeyInfo.getType()) {
                case STRING:
                case LIST:
                case SET:
                    valueArea.setText(StringUtils.defaultEmptyToString(redisDataTable.getValueAt(selectRow, 1)));
                    break;
                case HASH:
                    fieldArea.setText(StringUtils.defaultEmptyToString(redisDataTable.getValueAt(selectRow, 1)));
                    valueArea.setText(StringUtils.defaultEmptyToString(redisDataTable.getValueAt(selectRow, 2)));
                    break;
                case ZSET:
                    scoreField.setText(StringUtils.defaultEmptyToString(redisDataTable.getValueAt(selectRow, 1)));
                    valueArea.setText(StringUtils.defaultEmptyToString(redisDataTable.getValueAt(selectRow, 2)));
                    break;

                default:

                    break;

            }

        });
        SwingTools.addMouseClickedListener(saveLabel, e -> saveKeyInfo(e));
        SwingTools.addMouseClickedListener(addRowLeftLabel, e -> {
            if (RedisType.LIST.equals(redisKeyInfo.getType())) {
                addRowToKey(e, true);
            }

        });
        SwingTools.addMouseClickedListener(addRowLabel, e -> {
            if (!RedisType.STRING.equals(redisKeyInfo.getType())) {
                addRowToKey(e, false);
            }
        });

        SwingTools.addMouseClickedListener(deleteRowLabel, e -> {
            if (RedisType.STRING.equals(RedisTabbedPanel.this.redisKeyInfo.getType())) {
                return;
            }
            int conform = SwingTools.showConfirmDialogYNC(null, "是否确认删除？", "删除确认");
            if (conform == JOptionPane.YES_OPTION) {
                TableColumnBean value = null;
                int selectRow = ((ValueInfoPanel) valueInfoPanel).getSelectRow();
                switch (redisKeyInfo.getType()) {
                    case STRING:
                    case LIST:
                    case SET:
                    case HASH:
                        value = (TableColumnBean) redisDataTable.getValueAt(selectRow, 1);
                        break;
                    case ZSET:
                        value = (TableColumnBean) redisDataTable.getValueAt(selectRow, 2);
                        break;

                    default:

                        break;

                }
                final String finalValue = StringUtils.defaultEmptyToString(value);
                String key = redisKeyInfo.getKey();
                String id = redisKeyInfo.getId();
                int db = redisKeyInfo.getDb();
//                int index = redisKeyInfo.getPageBean().getStartIndex() + selectRow;
                int  index= value.getIndex();
                ResultRes<?> resultRes = BaseController.dispatcher(() -> redisConnectService.deleteRowKeyInfo(id, db, key, finalValue,index, redisKeyInfo.getType()));
                if (resultRes.isRet()) {
//                         RTableModel tableModel = (RTableModel)redisDataTable.getModel();
//                         tableModel.removeRow(selectRow);
                    RedisTabbedPanel.this.updateUI(treeNode, pageBean);
                } else {
                    SwingTools.showMessageErrorDialog(null, "删除失败："+resultRes.getMsg());
                }
            }
        });

        updatePageInfo();
    }

    private void addRowToKey(MouseEvent evt, boolean isLeftList) {
        RedisKeyInfo newKeyInfo = new RedisKeyInfo();
        ReflectUtil.copyProperties(this.redisKeyInfo, newKeyInfo);
        AddRowDialog d = new AddRowDialog(LarkFrame.frame, newKeyInfo);
        d.setLeftList(isLeftList);
        d.getResult(item -> {
            RedisTabbedPanel.this.updateUI(RedisTabbedPanel.this.treeNode, RedisTabbedPanel.this.pageBean);
            d.close();
        });
        d.open();
    }

    private void saveKeyInfo(MouseEvent evt) {
        int selectRow = ((ValueInfoPanel) valueInfoPanel).getSelectRow();
        RedisKeyInfo newKeyInfo = new RedisKeyInfo();
        RedisKeyInfo oldKeyInfo = new RedisKeyInfo();
        ReflectUtil.copyProperties(this.redisKeyInfo, newKeyInfo);
        ReflectUtil.copyProperties(this.redisKeyInfo, oldKeyInfo);
        newKeyInfo.setPageBean(null);
        newKeyInfo.setValueHash(null);
        newKeyInfo.setValueList(null);
        newKeyInfo.setValueZSet(null);
        oldKeyInfo.setPageBean(null);
        oldKeyInfo.setValueHash(null);
        oldKeyInfo.setValueList(null);
        oldKeyInfo.setValueZSet(null);
        String oldValue = null;
        newKeyInfo.setValue(valueArea.getText());
        switch (redisKeyInfo.getType()) {
            case STRING:
            case LIST:
                newKeyInfo.setIndex(redisKeyInfo.getPageBean().getStartIndex() + selectRow);
            case SET:
                oldValue = StringUtils.defaultEmptyToString(redisDataTable.getValueAt(selectRow, 1));
                oldKeyInfo.setValue(oldValue);
                break;
            case HASH:
                oldValue = StringUtils.defaultEmptyToString(redisDataTable.getValueAt(selectRow, 2));
                String oldfield = StringUtils.defaultEmptyToString(redisDataTable.getValueAt(selectRow, 1));
                oldKeyInfo.setValue(oldValue);
                oldKeyInfo.setField(oldfield);
                newKeyInfo.setField(fieldArea.getText());
                break;
            case ZSET:
                String score = scoreField.getText();
                if (StringUtils.isBlank(score)) {
                    SwingTools.showMessageErrorDialog(this, "请输入分数");
                    return;
                }
                try {
                    newKeyInfo.setScore(Double.valueOf(score));
                } catch (NumberFormatException ez) {
                    SwingTools.showMessageErrorDialog(this, "请输入正确的分数");
                    return;
                }
                oldValue = StringUtils.defaultEmptyToString(redisDataTable.getValueAt(selectRow, 2));
                String oldScore = StringUtils.defaultEmptyToString(redisDataTable.getValueAt(selectRow, 1));
                oldKeyInfo.setValue(oldValue);
                oldKeyInfo.setScore(Double.valueOf(oldScore));
                break;

            default:

                break;

        }
        ResultRes<RedisKeyInfo> resultRes = BaseController.dispatcher(() -> redisConnectService.updateKeyInfo(newKeyInfo, oldKeyInfo));
        if (resultRes.isRet()) {
            //更新表格值 byte值
            switch (redisKeyInfo.getType()) {
                case STRING:
                case LIST:
                case SET:
                    redisDataTable.setValueAt(valueArea.getText(), selectRow, 1);
                    break;
                case HASH:
                    redisDataTable.setValueAt(valueArea.getText(), selectRow, 2);
                    redisDataTable.setValueAt(fieldArea.getText(), selectRow, 1);

                    setFieldInfoLabelText(fieldArea.getText().getBytes().length);
                    break;
                case ZSET:
                    redisDataTable.setValueAt(valueArea.getText(), selectRow, 2);
                    redisDataTable.setValueAt(scoreField.getText(), selectRow, 1);
                    break;

                default:

                    break;

            }
            setValueInfoLabelText(valueArea.getText().getBytes().length);
            updatePageInfo();
//            updateUI(treeNode, pageBean);
        } else {
            SwingTools.showMessageErrorDialog(null, resultRes.getMsg());
        }

    }

    private void pageMousePPressed(java.awt.event.MouseEvent evt, int page, int row) {
        this.pageBean.setPage(page);
        this.pageBean.setRows(row);
        this.updateUI(this.treeNode, this.pageBean);
    }

    private void pageMousePPressed(int page) {
        pageMousePPressed(null, page, this.redisKeyInfo.getPageBean().getRows());
    }

    private void updatePageInfo() {

        PageBean pageBean = redisKeyInfo.getPageBean();
        gotoPagefield.setText(pageBean.getPage() + "");
        gotoPagefield.setEnabled(true);
        totalPage.setText("共" + pageBean.getTotalPage() + "页");
        firstPageLabel.setEnabled(!pageBean.isFirstPage());
        previousPageLabel.setEnabled(pageBean.hasPreviousPage());
        nextPageLabel.setEnabled(pageBean.hasNextPage());
        lastPageLabel.setEnabled(!pageBean.isLastPage());
        addRowLeftLabel.setEnabled(false);
        addRowLabel.setToolTipText("新增一行");
        switch (redisKeyInfo.getType()) {

            case LIST:
                this.addRowLeftLabel.setEnabled(true);
                addRowLeftLabel.setToolTipText("从左边新增一行");
                addRowLabel.setToolTipText("从右边新增一行");
                this.addRowLabel.setEnabled(true);
                if (redisDataTable.getSelectedRowCount() > 0) {
                    this.deleteRowLabel.setEnabled(true);
                } else {
                    this.deleteRowLabel.setEnabled(false);
                }
                break;
            case SET:
            case HASH:
            case ZSET:
                this.addRowLabel.setEnabled(true);
                if (redisDataTable.getSelectedRowCount() > 0) {
                    this.deleteRowLabel.setEnabled(true);
                } else {
                    this.deleteRowLabel.setEnabled(false);
                }
                firstPageLabel.setEnabled(false);
                previousPageLabel.setEnabled(false);
                nextPageLabel.setEnabled(false);
                lastPageLabel.setEnabled(false);
                gotoPagefield.setEnabled(false);
                break;
            case STRING:
                gotoPagefield.setEnabled(false);
            default:
                this.addRowLabel.setEnabled(false);
                this.deleteRowLabel.setEnabled(false);

                break;

        }

        cancelLabel.setEnabled(false);
        saveLabel.setEnabled(false);
    }

    private void addToPagePanel(JComponent component, boolean firstStrut, Integer firstStrutWidth, boolean lastStrut, Integer lastStrutWidth) {
        addStrutHorizontalPanel(pagePanel, component, firstStrut, firstStrutWidth, lastStrut, lastStrutWidth);

    }

    private void addToPagePanel(JComponent component) {
        addToPagePanel(component, true, 10, false, null);
    }

    private void saveLabelEnabled() {
        boolean isEnabled = false;
        if (((ValueInfoPanel) valueInfoPanel).getSelectRow() < redisDataTable.getRowCount() && valueArea.isEditable()) {
            if(!((ValueInfoPanel) valueInfoPanel).getValueColumnBean().getValue().equals(valueArea.getText())){
                isEnabled = true;
            }

            switch (redisKeyInfo.getType()) {
                case STRING:
                case LIST:
                case SET:
                    break;
                case HASH:
                    if(!fieldArea.isEditable()){
                        isEnabled = false;
                    }else{
                        if(!isEnabled && !((ValueInfoPanel) valueInfoPanel).getKeyColumnBean().getValue().equals(fieldArea.getText())){
                            isEnabled = true;
                        }
                    }
                    break;
                case ZSET:
                    String s = StringUtils.defaultEmptyToString(redisDataTable.getValueAt(((ValueInfoPanel) valueInfoPanel).getSelectRow(), 1));
                    if(!isEnabled && !s.equals(scoreField.getText())){
                        isEnabled = true;
                    }
                    break;
                default:
                    break;
            }
        }
        saveLabel.setEnabled(isEnabled);
        cancelLabel.setEnabled(isEnabled);
    }

    private void reloadValueInfoPanel() {
        valueArea.setEditable(false);
        fieldArea.setEditable(false);
        scoreField.setEditable(false);
        valueArea.setText(null);
        fieldArea.setText(null);
        scoreField.setText(null);
        setFieldInfoLabelText(0);
        setValueInfoLabelText(0);
    }

    private void deleteBtnActionPerformed(MouseEvent evt) {
        RTabbedPane parent = (RTabbedPane) this.getParent();
        MenuManager.getInstance().removeKey(tree, treeNode, parent, null);
    }

    private void reloadBtnMousePressed(java.awt.event.MouseEvent evt) {
        this.updateUI(this.treeNode, this.pageBean);
    }

    //---------------------------------------------------bottom page  panl end---------------------------------------------------------------------
    //---------------------------------------------------left tab panl start---------------------------------------------------------------------
    private void initLeftPanel() {
        leftTablePanel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
        JLabel gridLabel = createVerticalLabel(LarkFrame.getI18nUpText(I18nKey.RedisResource.GRID), true);
        JLabel textLabel = createVerticalLabel(LarkFrame.getI18nUpText(I18nKey.RedisResource.TEXT), false);


        gridLabel.setToolTipText("show grid result");
        gridLabel.setIcon(PublicConstant.Image.grid);
        addStrutVerticalPanel(leftTablePanel, gridLabel, true, 10, false, null);
        textLabel.setToolTipText("show text result");
        textLabel.setIcon(PublicConstant.Image.text);
        addStrutVerticalPanel(leftTablePanel, textLabel, true, 2, false, null);


        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BorderLayout());
        textPanel.setBackground(Color.WHITE);

        SwingTools.addMouseClickedListener(textLabel,e ->{
            if(resultTab != 1){
                resultTab = 1;
                if(resultTextArea == null){
                    resultTextArea = new OnlyReadArea();
                    resultTextArea.setLimit(false);
                    resultTextArea.setCandy(Color.white);
                    resultTextArea.setForeground(Color.black);
                    textPanel.add(resultTextArea,BorderLayout.CENTER);
                }
                resultTextArea.setText(getTableText(redisKeyInfo));
                tableScrollPanel.setViewportView(textPanel);
            }
        });
        SwingTools.addMouseClickedListener(gridLabel,e ->{
            if(resultTab != 0){
                tableScrollPanel.setViewportView(redisDataTable);
                resultTab = 0;
            }

        });
    }

    private String getTableText(RedisKeyInfo redisKeyInfo){
        Vector[] tableContext = getTableContext(redisKeyInfo);
        Vector<String> titleVector = tableContext[0];
        String[] title = new String[titleVector.size()-2];
        for (int i = 1; i < titleVector.size()-1; i++) {
            title[i-1] = titleVector.get(i);
        }

        tableContext[0].toArray(title);
        TextForm.TextFormBulider bulider = TextForm.bulider().title(title);
        Vector<Vector<TableColumnBean>> vector = tableContext[1];
        for (Vector<TableColumnBean> columnBeans : vector) {
            String[] row = new String[columnBeans.size()-1];
            for (int i = 1; i < columnBeans.size(); i++) {
                row[i-1] = columnBeans.get(i).toString();
            }
            bulider.addRow(row);
        }

        return bulider.finish().formatTable();
    }

    private JLabel createVerticalLabel(String text, boolean isSelect) {
        JLabel label = new JLabel(text);
        label.setUI(new VerticalLabelUI(false));
        label.setPreferredSize(new Dimension(22, 65));
        label.setMinimumSize(new Dimension(22, 65));
        label.setMaximumSize(new Dimension(22, 65));
        if (isSelect) {
            label.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY), BorderFactory.createEmptyBorder(3, 5, 0, 0)));
        } else {
            label.setBorder(BorderFactory.createEmptyBorder(3, 5, 0, 0));
        }
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                label.setOpaque(false);
                label.setBackground(Color.GREEN);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                label.setOpaque(true);
                label.setBackground(new Color(195, 209, 225));
            }

        });

        return label;
    }
    //---------------------------------------------------left tab panl end---------------------------------------------------------
    //-------------------------------------------------------common----------------------------------------------------------------

    private void addStrutVerticalPanel(JComponent parentComponent, JComponent component, boolean firstStrut, Integer firstStrutWidth, boolean lastStrut, Integer lastStrutWidth) {
        if (firstStrut) {
            parentComponent.add(Box.createVerticalStrut(firstStrutWidth));
        }
        parentComponent.add(component);
        if (lastStrut) {
            parentComponent.add(Box.createVerticalStrut(lastStrutWidth));
        }

    }

    private void addStrutHorizontalPanel(JComponent parentComponent, JComponent component, boolean firstStrut, Integer firstStrutWidth, boolean lastStrut, Integer lastStrutWidth) {
        if (firstStrut) {
            parentComponent.add(Box.createHorizontalStrut(firstStrutWidth));
        }
        parentComponent.add(component);
        if (lastStrut) {
            parentComponent.add(Box.createHorizontalStrut(lastStrutWidth));
        }

    }

    private void getKeyInfo(Consumer<ResultRes<RedisKeyInfo>> error) {
        if (this.treeNode == null) {
            return;
        }
        RedisTreeItem item = (RedisTreeItem) treeNode.getUserObject();
        if (!RedisType.KEY.equals(item.getType())) {
            SwingTools.showMessageErrorDialog(null, item.getType() + "不是KEY");
            return;
        }
        ScanCursor cursor;
        if(redisKeyInfo == null){
            cursor = ScanCursor.INITIAL;
        }else{
            cursor = redisKeyInfo.getCursor();
        }
        String pattern =  searchTextField == null? null:searchTextField.getText();
        ResultRes<RedisKeyInfo> resultRes = BaseController.dispatcher(() -> redisConnectService.getRedisKeyInfo(item.getId(), item.getDb(), item.getKey(),cursor,pattern, pageBean));
        if (resultRes.isRet()) {
            redisKeyInfo = resultRes.getData();
        } else {
            error.accept(resultRes);
        }

    }

    private void updateUI(RTreeNode treeNode, PageBean pageBean) {
        this.updateUI(treeNode, this.tree, pageBean, false);
    }

    public void updateUI(RTreeNode treeNode, JTree tree, PageBean pageBean, final boolean isNewNode) {
        this.pageBean = pageBean;

        this.treeNode = treeNode;
        this.tree = tree;
        //重新获取key信息
        if (isNewNode) {
            this.pageBean.setRows(initRow);
            selectPageComboBox.setSelectedIndex(0);
            tableScrollPanel.setViewportView(redisDataTable);
            resultTab = 0;
            searchTextField.setText("");
        }
        this.getKeyInfo(resultRes -> {
            if (isNewNode) {
                SwingTools.showMessageErrorDialog(null, resultRes.getMsg());
            } else {
                JTabbedPane parent = (JTabbedPane) this.getParent();
                parent.remove(parent.getSelectedIndex());
                this.close();
            }
        });

        updateBasePanel();
        //更新值显示
        togglevalueInfo();
        //刷新表格
        refreshTable();
        JTabbedPane parent = (JTabbedPane) this.getParent();
        RTabbedPane.ButtonClose buttonClose = (RTabbedPane.ButtonClose) parent.getTabComponentAt(parent.getSelectedIndex());
        if (buttonClose != null) {
            buttonClose.setInit(false);
            buttonClose.setText(redisKeyInfo.getKey());
        }
        super.updateUI();
    }

    private void refreshTable(){
        RTableModel model = (RTableModel) redisDataTable.getModel();
        tableContext = getTableContext(redisKeyInfo);
        model.setDataVector(tableContext[1], tableContext[0]);
        updateTableStyle();
        updatePageInfo();
        redisDataTable.clearSelection();
        reloadValueInfoPanel();
        saveLabel.setEnabled(false);
        cancelLabel.setEnabled(false);
    }

    private void setLabelSize(JLabel label, int width, int height) {
        label.setMinimumSize(new java.awt.Dimension(width, height));
        label.setPreferredSize(new java.awt.Dimension(width, height));
    }

    public void close() {
        this.removeAll();
        this.setVisible(false);
    }

    //---------------------------------------------自动生成-----------------------------------------------------------------
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        redisBaseInfoBgPanel = new javax.swing.JPanel();
        serachPanel = new javax.swing.JPanel();
        basePanel = getBasePanel();
        tableBgPanel = new javax.swing.JPanel();
        leftTablePanel = new javax.swing.JPanel();
        jSplitPanel = new javax.swing.JSplitPane();
        tableScrollPanel = new EasyJSP();
        redisDataTable = new javax.swing.JTable();
        valueInfoPanel = new ValueInfoPanel();
        rightTablePanel = new javax.swing.JPanel();
        pagePanel = new javax.swing.JPanel();

        setBackground(new java.awt.Color(102, 255, 102));
        setMinimumSize(new java.awt.Dimension(462, 106));
        setPreferredSize(new java.awt.Dimension(987, 550));
        setLayout(new java.awt.BorderLayout());

        redisBaseInfoBgPanel.setBackground(new java.awt.Color(206, 221, 237));
        redisBaseInfoBgPanel.setMinimumSize(new java.awt.Dimension(0, 63));
        redisBaseInfoBgPanel.setName("基本按钮"); // NOI18N
        redisBaseInfoBgPanel.setPreferredSize(new java.awt.Dimension(0, 153));
        redisBaseInfoBgPanel.setLayout(new java.awt.BorderLayout());

        serachPanel.setBackground(new java.awt.Color(255, 255, 255));
        serachPanel.setMinimumSize(new java.awt.Dimension(1100, 24));
        serachPanel.setPreferredSize(new Dimension(0,aroundPanelWidth));
        serachPanel.setLayout(new javax.swing.BoxLayout(serachPanel, javax.swing.BoxLayout.X_AXIS));
        redisBaseInfoBgPanel.add(serachPanel, java.awt.BorderLayout.PAGE_END);

        basePanel.setLayout(new java.awt.GridLayout(1, 2));
        redisBaseInfoBgPanel.add(basePanel, java.awt.BorderLayout.CENTER);

        add(redisBaseInfoBgPanel, java.awt.BorderLayout.PAGE_START);

        tableBgPanel.setBackground(new java.awt.Color(255, 51, 51));
        tableBgPanel.setLayout(new java.awt.BorderLayout());

        leftTablePanel.setBackground(PublicConstant.RColor.themeColor);
        leftTablePanel.setMaximumSize(new Dimension(aroundPanelWidth,32767));
        leftTablePanel.setPreferredSize(new Dimension(aroundPanelWidth,0));
        leftTablePanel.setLayout(new javax.swing.BoxLayout(leftTablePanel, javax.swing.BoxLayout.Y_AXIS));
        tableBgPanel.add(leftTablePanel, java.awt.BorderLayout.WEST);

        jSplitPanel.setBackground(new java.awt.Color(255, 255, 255));
        jSplitPanel.setDividerSize(1);

        tableScrollPanel.setBackground(new java.awt.Color(255, 255, 255));
        tableScrollPanel.setEnabled(false);

        redisDataTable.setModel(getTableModel());
        redisDataTable.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        redisDataTable.setGridColor(new java.awt.Color(255, 255, 255));
        redisDataTable.setRowHeight(28);
        redisDataTable.setSelectionBackground(new java.awt.Color(109, 174, 228));
        tableScrollPanel.setViewportView(redisDataTable);

        jSplitPanel.setLeftComponent(tableScrollPanel);

        valueInfoPanel.setBackground(new java.awt.Color(255, 255, 255));
        valueInfoPanel.setMinimumSize(new Dimension(valueInfoPanelWidth,10));
        valueInfoPanel.setPreferredSize(new Dimension(valueInfoPanelWidth,10));
        jSplitPanel.setRightComponent(valueInfoPanel);

        tableBgPanel.add(jSplitPanel, java.awt.BorderLayout.CENTER);

        rightTablePanel.setBackground(PublicConstant.RColor.themeColor);
        rightTablePanel.setMaximumSize(new Dimension(aroundPanelWidth,32767));
        rightTablePanel.setPreferredSize(new Dimension(aroundPanelWidth,469));

        javax.swing.GroupLayout rightTablePanelLayout = new javax.swing.GroupLayout(rightTablePanel);
        rightTablePanel.setLayout(rightTablePanelLayout);
        rightTablePanelLayout.setHorizontalGroup(
            rightTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        rightTablePanelLayout.setVerticalGroup(
            rightTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 394, Short.MAX_VALUE)
        );

        tableBgPanel.add(rightTablePanel, java.awt.BorderLayout.EAST);

        pagePanel.setBackground(PublicConstant.RColor.themeColor);
        pagePanel.setMaximumSize(new Dimension(32767,aroundPanelWidth));
        pagePanel.setMinimumSize(new java.awt.Dimension(0, 20));
        pagePanel.setPreferredSize(new Dimension(0,aroundPanelWidth));
        pagePanel.setLayout(new javax.swing.BoxLayout(pagePanel, javax.swing.BoxLayout.X_AXIS));
        tableBgPanel.add(pagePanel, java.awt.BorderLayout.SOUTH);

        add(tableBgPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel basePanel;
    private javax.swing.JSplitPane jSplitPanel;
    private javax.swing.JPanel leftTablePanel;
    private javax.swing.JPanel pagePanel;
    private javax.swing.JPanel redisBaseInfoBgPanel;
    private javax.swing.JTable redisDataTable;
    private javax.swing.JPanel rightTablePanel;
    private javax.swing.JPanel serachPanel;
    private javax.swing.JPanel tableBgPanel;
    private javax.swing.JScrollPane tableScrollPanel;
    private javax.swing.JPanel valueInfoPanel;
    // End of variables declaration//GEN-END:variables
}
