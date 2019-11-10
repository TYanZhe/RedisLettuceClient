package cn.org.tpeach.nosql.view.dialog;

import cn.org.tpeach.nosql.bean.DicBean;
import cn.org.tpeach.nosql.constant.ConfigConstant;
import cn.org.tpeach.nosql.constant.I18nKey;
import cn.org.tpeach.nosql.constant.PublicConstant;
import cn.org.tpeach.nosql.framework.LarkFrame;
import cn.org.tpeach.nosql.tools.ConfigMapper;
import cn.org.tpeach.nosql.tools.ConfigParser;
import cn.org.tpeach.nosql.tools.StringUtils;
import cn.org.tpeach.nosql.tools.SwingTools;
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
import java.util.Map;


public class SettingDialog extends AbstractRowDialog<Object, Object>{
    private JPanel panel;
    private JComboBox<DicBean> languageComboBox;
    private JComboBox<String>  characterEncodingComboBox;
    private JCheckBox pageLoadingcheckBox;
    private PlaceholderTextField appendTextWaittime,appendTextNumber;
    private ConfigParser configParser = ConfigParser.getInstance();
    public SettingDialog() {
        super(LarkFrame.frame, null);
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
        appendTextWaittime = new PlaceholderTextField(20);
        appendTextNumber = new PlaceholderTextField(20);
        appendTextWaittime.setEnabled(false);
        appendTextNumber.setEnabled(false);
        pageLoadingcheckBox.addItemListener(e->{
            appendTextWaittime.setEnabled(pageLoadingcheckBox.isSelected());
            appendTextNumber.setEnabled(pageLoadingcheckBox.isSelected());
        });
        //实验特性
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
        TitledBorder experimentValueTitledBorder = BorderFactory.createTitledBorder("大文本（>150Kb）分页加载");
        experimentValueTitledBorder.setTitleColor(Color.lightGray);
        JLabel jLabel1 = new JLabel("分页加载:");
        jLabel1.setForeground(Color.lightGray);
        JLabel jLabel2 = new JLabel("单次渲染时间（ms）:");
        jLabel2.setForeground(Color.lightGray);
        JLabel jLabel3 = new JLabel("单次加载数量:");
        jLabel3.setForeground(Color.lightGray);
        experimentValuePanl.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0,20,0,20),experimentValueTitledBorder));
        experimentValuePanl.add(createRow(panel,jLabel1,pageLoadingcheckBox, 20, 0.32));
        experimentValuePanl.add(Box.createVerticalStrut(5));
        experimentValuePanl.add(createRow(panel,jLabel2, appendTextWaittime, 20, 0.32));
        experimentValuePanl.add(Box.createVerticalStrut(5));
        experimentValuePanl.add(createRow(panel, jLabel3, appendTextNumber, 20, 0.32));
        experimentValuePanl.add(Box.createVerticalStrut(5));
        experimentPanl.add(experimentValuePanl);
        experimentPanl.add(Box.createVerticalStrut(10));
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
        String append_text_waittime = configParser.getString(ConfigConstant.Section.EXPERIMENT, ConfigConstant.APPEND_TEXT_WAITTIME,"1000");
        if("1".equals(is_loading_text)){
            pageLoadingcheckBox.setSelected(true);
        }else{
            pageLoadingcheckBox.setSelected(false);
        }
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
        Map<String, ConfigMapper> mapper = (Map<String, ConfigMapper>) configParser.getEntries().get(ConfigConstant.Section.LOCAL);
        ConfigMapper languageMapper = mapper.get(ConfigConstant.LANGUAGE);
        ConfigMapper countryMapper = mapper.get(ConfigConstant.COUNTRY);
        if(I18nKey.RedisResource.SIM_CHINESE.getKey().equals(langItem.getCode())){
            languageMapper.setValue(ConfigConstant.Local.zh);
            countryMapper.setValue(ConfigConstant.Local.CN);
        }else if(I18nKey.RedisResource.ENGLISH.getKey().equals(langItem.getCode())){
            languageMapper.setValue(ConfigConstant.Local.en);
            countryMapper.setValue(ConfigConstant.Local.US);
        }else{
            SwingTools.showMessageErrorDialog(null,"未知语言");
        }
        configParser.getEntries().put(ConfigConstant.Section.LOCAL,mapper);


        String selectedItem = (String) characterEncodingComboBox.getSelectedItem();
        mapper = (Map<String, ConfigMapper>) configParser.getEntries().get(ConfigConstant.Section.CHARACTER_ENCODING);
        mapper.get(ConfigConstant.CHARACTER).setValue(selectedItem);
        configParser.getEntries().put(ConfigConstant.Section.CHARACTER_ENCODING,mapper);

        mapper = (Map<String, ConfigMapper>) configParser.getEntries().get(ConfigConstant.Section.EXPERIMENT);

        boolean selected = pageLoadingcheckBox.isSelected();
        if(selected){
            mapper.get(ConfigConstant.IS_LOADING_TEXT).setValue("1");
        }else{
            mapper.get(ConfigConstant.IS_LOADING_TEXT).setValue("0");
        }
        if (StringUtils.isNotBlank(appendTextWaittime.getText())) {
            mapper.get(ConfigConstant.APPEND_TEXT_WAITTIME).setValue(appendTextWaittime.getText());
        }
        if (StringUtils.isNotBlank(appendTextNumber.getText())) {
            mapper.get(ConfigConstant.APPEND_TEXT_NUMBER).setValue(appendTextNumber.getText());
        }
        configParser.getEntries().put(ConfigConstant.Section.EXPERIMENT,mapper);
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
