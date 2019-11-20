package cn.org.tpeach.nosql.view.dialog;

import cn.org.tpeach.nosql.bean.DicBean;
import cn.org.tpeach.nosql.constant.ConfigConstant;
import cn.org.tpeach.nosql.constant.I18nKey;
import cn.org.tpeach.nosql.constant.PublicConstant;
import cn.org.tpeach.nosql.framework.LarkFrame;
import cn.org.tpeach.nosql.tools.ConfigParser;
import cn.org.tpeach.nosql.tools.StringUtils;
import cn.org.tpeach.nosql.tools.SwingTools;
import cn.org.tpeach.nosql.view.RToolBar;
import cn.org.tpeach.nosql.view.RedisMainWindow;
import cn.org.tpeach.nosql.view.component.EasyGBC;
import cn.org.tpeach.nosql.view.component.PlaceholderTextField;
import cn.org.tpeach.nosql.view.component.RComboBox;
import cn.org.tpeach.nosql.view.ui.ServerTabbedPaneUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Locale;


public class SettingDialog extends AbstractRowDialog<Object, Object>{
    private JPanel panel;
    private JComboBox<DicBean> languageComboBox;
    private JComboBox<String>  characterEncodingComboBox;
    private JCheckBox pageLoadingcheckBox,magnifyTextDialogCheckBox,globalLoadingTimeOutCheckBox,testToolBarShowCheckBox;
    private PlaceholderTextField appendTextWaittime,appendTextNumber;
    private ConfigParser configParser = ConfigParser.getInstance();
    public SettingDialog() {
        super(LarkFrame.frame, null);
        this.setTitle(LarkFrame.getI18nFirstUpText(I18nKey.RedisResource.SETTING));
    }

    @Override
    public void initDialog(Object o) {

    }

    @Override
    protected void contextUiImpl(JPanel contextPanel, JPanel btnPanel) {
        final JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setUI(new ServerTabbedPaneUI("#FFFFFF","#000000"));
        panel = new JPanel();
        panel.setBackground(getPanelBgColor());
        tabbedPane.addTab("General",panel);
        contextPanel.setLayout(new BorderLayout());
        contextPanel.add(tabbedPane,BorderLayout.CENTER);

        JLabel languageLabel = new JLabel("语  言：");
        JLabel characterEncodingLabel = new JLabel("编  码：");
        languageComboBox = new RComboBox<>(new DicBean[]{
                new DicBean(I18nKey.RedisResource.SIM_CHINESE.getKey(),LarkFrame.getI18nFirstUpText(I18nKey.RedisResource.SIM_CHINESE)),
                new DicBean(I18nKey.RedisResource.ENGLISH.getKey(),LarkFrame.getI18nFirstUpText(I18nKey.RedisResource.ENGLISH)+"（部分实现）")
        });
        characterEncodingComboBox = new RComboBox<>(new String[]{
                PublicConstant.CharacterEncoding.ISO_8859_1,
                PublicConstant.CharacterEncoding.GBK,
                PublicConstant.CharacterEncoding.UTF_8,
                /*          PublicConstant.CharacterEncoding.UTF_16,
                          PublicConstant.CharacterEncoding.UTF_16BE,
                          PublicConstant.CharacterEncoding.UTF_16LE,*/
        });
        pageLoadingcheckBox = new JCheckBox();
        pageLoadingcheckBox.setBackground(getPanelBgColor());
        magnifyTextDialogCheckBox = new JCheckBox();
        globalLoadingTimeOutCheckBox = new JCheckBox();
        testToolBarShowCheckBox = new JCheckBox();
        testToolBarShowCheckBox.setBackground(getPanelBgColor());
        globalLoadingTimeOutCheckBox.setBackground(getPanelBgColor());
        magnifyTextDialogCheckBox.setBackground(getPanelBgColor());
        appendTextWaittime = new PlaceholderTextField(20);
        appendTextNumber = new PlaceholderTextField(20);
        appendTextWaittime.setEnabled(false);
        appendTextNumber.setEnabled(false);
        pageLoadingcheckBox.addItemListener(e->{
            appendTextWaittime.setEnabled(pageLoadingcheckBox.isSelected());
            appendTextNumber.setEnabled(pageLoadingcheckBox.isSelected());
        });
        //实验特性
        JPanel experimentPanl = getExperimentPanel();

        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));

        panel.add(Box.createVerticalStrut(15));
        panel.add(createRow(panel,languageLabel,languageComboBox,28,0.18));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createRow(panel,characterEncodingLabel,characterEncodingComboBox,28,0.18));
        panel.add(Box.createVerticalStrut(10));
        Box horizontalBox = Box.createHorizontalBox();
        horizontalBox.add(new JSeparator());
        SwingTools.fillWidthPanel(panel,horizontalBox);
        panel.add(horizontalBox);
        panel.add(Box.createVerticalStrut(10));

        panel.add(experimentPanl);
        panel.add(Box.createVerticalGlue());

    }

    private JPanel getExperimentPanel() {
        JPanel experimentPanl = new JPanel();
        experimentPanl.setBackground(getPanelBgColor());
        experimentPanl.setLayout(new BoxLayout(experimentPanl,BoxLayout.Y_AXIS));
        TitledBorder experimentTitledBorder = BorderFactory.createTitledBorder("实验特性");
        experimentTitledBorder.setTitleJustification(TitledBorder.LEFT);
        experimentPanl.setBorder(experimentTitledBorder);
        experimentPanl.add(Box.createVerticalStrut(10));

        JPanel experimentValuePanl = new JPanel();
        experimentValuePanl.setBackground(getPanelBgColor());
        experimentValuePanl.setLayout(new BoxLayout(experimentValuePanl,BoxLayout.Y_AXIS));
        TitledBorder experimentValueTitledBorder = BorderFactory.createTitledBorder("大文本（>150Kb）加载");
        experimentValueTitledBorder.setTitleColor(Color.lightGray);
        JLabel jLabel1 = new JLabel("Value加载Loading:");
        jLabel1.setForeground(Color.lightGray);
        JLabel jLabel2 = new JLabel("单次渲染时间（ms）:");
        jLabel2.setForeground(Color.lightGray);
        JLabel jLabel3 = new JLabel("单次加载数量:");
        jLabel3.setForeground(Color.lightGray);
        JLabel jLabel4 = new JLabel("双击打开大窗口编辑Key数据:");
        jLabel4.setForeground(Color.lightGray);

        experimentValuePanl.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0,20,0,20),experimentValueTitledBorder));

        JPanel  pageCheckBoxPanel = getPanel();
        pageCheckBoxPanel.setLayout(new BoxLayout(pageCheckBoxPanel,BoxLayout.X_AXIS));
        JPanel  magnifyTextCheckBoxPanel = getPanel();
        magnifyTextCheckBoxPanel.setLayout(new BoxLayout(magnifyTextCheckBoxPanel,BoxLayout.X_AXIS));
        experimentValuePanl.add(Box.createVerticalStrut(5));
        experimentValuePanl.add(createRow(panel, pageCheckBoxPanel, magnifyTextCheckBoxPanel, 20, 0.5,0,0,false));
        pageCheckBoxPanel.add(createRow(pageCheckBoxPanel, jLabel1, pageLoadingcheckBox, 20, 0.7,10,10,true));
        magnifyTextCheckBoxPanel.add(createRow(magnifyTextCheckBoxPanel, jLabel4, magnifyTextDialogCheckBox, 20, 0.8,10,10,true));
        experimentValuePanl.add(Box.createVerticalStrut(5));

        JPanel  globalLoadingTimeOutPanel = getPanel();
        globalLoadingTimeOutPanel.setLayout(new BoxLayout(globalLoadingTimeOutPanel,BoxLayout.X_AXIS));
        JPanel  testSettingPanel = getPanel();
        testSettingPanel.setLayout(new BoxLayout(testSettingPanel,BoxLayout.X_AXIS));
        experimentValuePanl.add(createRow(panel, globalLoadingTimeOutPanel, testSettingPanel, 20, 0.5,0,0,false));
        globalLoadingTimeOutPanel.add(createRow(globalLoadingTimeOutPanel, getLightGrayLabel("Loading超时失效:"), globalLoadingTimeOutCheckBox, 20, 0.7,10,10,true));
        if(!PublicConstant.ProjectEnvironment.DEV.equals(LarkFrame.getProjectEnv())){
            testSettingPanel.add(createRow(testSettingPanel, getLightGrayLabel("测试数据工具:"), testToolBarShowCheckBox, 20, 0.8,10,10,true));
        }



        // cn.org.tpeach.nosql.view.RedisTabbedPanel.setTextLoading使用新方式，分页配置无效 2019-11-17
//        experimentValuePanl.add(Box.createVerticalStrut(5));
//        experimentValuePanl.add(createRow(panel,jLabel2, appendTextWaittime, 20, 0.32));
//        experimentValuePanl.add(Box.createVerticalStrut(5));
//        experimentValuePanl.add(createRow(panel, jLabel3, appendTextNumber, 20, 0.32));
        experimentValuePanl.add(Box.createVerticalStrut(10));


        experimentPanl.add(experimentValuePanl);
        experimentPanl.add(Box.createVerticalStrut(10));
        return experimentPanl;
    }
    private JLabel getLightGrayLabel(String text){
        JLabel j = new JLabel(text);
        j.setForeground(Color.lightGray);
        return j;
    }
    private JPanel getPanel(){
        JPanel jPanel = new JPanel();
        jPanel.setBackground(this.getPanelBgColor());
        return jPanel;
    }
    @Override
    public void after() {
        super.after();
        String character = configParser.getString(ConfigConstant.Section.CHARACTER_ENCODING, ConfigConstant.CHARACTER, PublicConstant.CharacterEncoding.UTF_8);
        String language = configParser.getString(ConfigConstant.Section.LOCAL, ConfigConstant.LANGUAGE, Locale.getDefault().getLanguage());
        String country = configParser.getString(ConfigConstant.Section.LOCAL, ConfigConstant.COUNTRY,Locale.getDefault().getCountry());
        languageComboBox.setSelectedItem(findLanguage(language,country));
        characterEncodingComboBox.setSelectedItem(character);

        String is_loading_text = configParser.getString(ConfigConstant.Section.EXPERIMENT, ConfigConstant.IS_LOADING_TEXT, "0");
        String append_text_number = configParser.getString(ConfigConstant.Section.EXPERIMENT, ConfigConstant.APPEND_TEXT_NUMBER, "10000");
        String append_text_waittime = configParser.getString(ConfigConstant.Section.EXPERIMENT, ConfigConstant.APPEND_TEXT_WAITTIME, "1000");
        String magnifytext_dialog_swith = configParser.getString(ConfigConstant.Section.EXPERIMENT, ConfigConstant.MAGNIFYTEXT_DIALOG_SWITH, "0");
        String globalLoadingTimeOut = configParser.getString(ConfigConstant.Section.EXPERIMENT, ConfigConstant.LOADING_GLOBEL_TIMEOGT_ENABLED, "0");
        if ("1".equals(is_loading_text)) {
            pageLoadingcheckBox.setSelected(true);
        } else {
            pageLoadingcheckBox.setSelected(false);
        }
        if ("1".equals(magnifytext_dialog_swith)) {
            magnifyTextDialogCheckBox.setSelected(true);
        } else {
            magnifyTextDialogCheckBox.setSelected(false);
        }
        if ("1".equals(globalLoadingTimeOut)) {
            globalLoadingTimeOutCheckBox.setSelected(true);
        } else {
            globalLoadingTimeOutCheckBox.setSelected(false);
        }
        RToolBar toolBar = (RToolBar) ((RedisMainWindow) LarkFrame.frame).getToolBar();
        testToolBarShowCheckBox.setSelected(toolBar.getTestBatch().isVisible());
        appendTextWaittime.setText(append_text_waittime);
        appendTextNumber.setText(append_text_number);

    }






    @Override
    protected void setMiddlePanel(JPanel middlePanel){
        middlePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
    }
    @Override
    public boolean isNeedBtn() {
        return true;
    }

    @Override
    protected void submit(ActionEvent e) {
        DicBean langItem = (DicBean) languageComboBox.getSelectedItem();
        if(I18nKey.RedisResource.SIM_CHINESE.getKey().equals(langItem.getCode())){
            configParser.safePutMapperData(ConfigConstant.Section.LOCAL,ConfigConstant.LANGUAGE,ConfigConstant.Local.zh);
            configParser.safePutMapperData(ConfigConstant.Section.LOCAL,ConfigConstant.COUNTRY,ConfigConstant.Local.CN);
        }else if(I18nKey.RedisResource.ENGLISH.getKey().equals(langItem.getCode())){
            configParser.safePutMapperData(ConfigConstant.Section.LOCAL,ConfigConstant.LANGUAGE,ConfigConstant.Local.en);
            configParser.safePutMapperData(ConfigConstant.Section.LOCAL,ConfigConstant.COUNTRY,ConfigConstant.Local.US);
        }else{
            SwingTools.showMessageErrorDialog(null,"未知语言");
        }
        configParser.safePutMapperData(ConfigConstant.Section.CHARACTER_ENCODING,ConfigConstant.CHARACTER, (String) characterEncodingComboBox.getSelectedItem());

        configParser.safePutMapperData(ConfigConstant.Section.EXPERIMENT,ConfigConstant.IS_LOADING_TEXT,pageLoadingcheckBox.isSelected()?"1":"0");
        configParser.safePutMapperData(ConfigConstant.Section.EXPERIMENT,ConfigConstant.MAGNIFYTEXT_DIALOG_SWITH, magnifyTextDialogCheckBox.isSelected()?"1":"0");
        configParser.safePutMapperData(ConfigConstant.Section.EXPERIMENT,ConfigConstant.LOADING_GLOBEL_TIMEOGT_ENABLED, globalLoadingTimeOutCheckBox.isSelected()?"1":"0");
        RToolBar toolBar = (RToolBar) ((RedisMainWindow) LarkFrame.frame).getToolBar();
        toolBar.getTestBatch().setVisible(testToolBarShowCheckBox.isSelected());
        if (StringUtils.isNotBlank(appendTextWaittime.getText())) {
            configParser.safePutMapperData(ConfigConstant.Section.EXPERIMENT,ConfigConstant.APPEND_TEXT_WAITTIME,appendTextWaittime.getText());
        }
        if (StringUtils.isNotBlank(appendTextNumber.getText())) {
            configParser.safePutMapperData(ConfigConstant.Section.EXPERIMENT,ConfigConstant.APPEND_TEXT_NUMBER,appendTextNumber.getText());
        }
        configParser.safePutMapperData(ConfigConstant.Section.EXPERIMENT,ConfigConstant.MEMORY_FIXEDRATE_PERIOD,"5",false);
        try {
            configParser.writhConfigFile();
            SwingTools.showMessageInfoDialog(null,"修改成功,部分功能重启后生效",LarkFrame.getI18nText(I18nKey.RedisResource.SETTING));
            this.dispose();
        } catch (IOException ex) {
            ex.printStackTrace();
            SwingTools.showMessageErrorDialog(null,"修改失败");
        }

    }

    private DicBean findLanguage(String language,String country){
        String l = language + country;
        String key ;
        switch (l){
            case ConfigConstant.Local.zh+ConfigConstant.Local.CN:
                key = I18nKey.RedisResource.SIM_CHINESE.getKey();
                break;
            case ConfigConstant.Local.en+ConfigConstant.Local.US:
                key = I18nKey.RedisResource.ENGLISH.getKey();
                break;
            default:
                key = I18nKey.RedisResource.SIM_CHINESE.getKey();
                break;

        }
        return new DicBean(key,"");
    }
}
