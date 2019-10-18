package cn.org.tpeach.nosql.view.dialog;

import cn.org.tpeach.nosql.bean.DicBean;
import cn.org.tpeach.nosql.constant.ConfigConstant;
import cn.org.tpeach.nosql.constant.I18nKey;
import cn.org.tpeach.nosql.constant.PublicConstant;
import cn.org.tpeach.nosql.enums.RedisType;
import cn.org.tpeach.nosql.framework.LarkFrame;
import cn.org.tpeach.nosql.tools.ConfigMapper;
import cn.org.tpeach.nosql.tools.ConfigParser;
import cn.org.tpeach.nosql.tools.SwingTools;
import cn.org.tpeach.nosql.view.component.RComboBox;
import cn.org.tpeach.nosql.view.ui.ServerTabbedPaneUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;


public class SettingDialog extends BaseDialog<Object, Object>{
    private JPanel panel;
    private JComboBox<DicBean> languageComboBox;
    private JComboBox<String>  characterEncodingComboBox;
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



        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        panel.add(Box.createVerticalStrut(15));
        panel.add(createRow(panel,languageLabel,languageComboBox,28,0.18));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createRow(panel,characterEncodingLabel,characterEncodingComboBox,28,0.18));
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
    }


    public JPanel createRow(JComponent parentComponent,JLabel label,JComponent component,int rowHeight,double leftPercent){
        JPanel rowPanel = new JPanel();
        rowPanel.setPreferredSize(new Dimension(rowPanel.getPreferredSize().width,rowHeight));
        rowPanel.setMaximumSize(new Dimension(rowPanel.getPreferredSize().width,rowHeight));
        rowPanel.setMinimumSize(new Dimension(rowPanel.getPreferredSize().width,rowHeight));
        SwingTools.fillWidthPanel(parentComponent,rowPanel);
        rowPanel.setLayout(new BorderLayout());
        JPanel labelPanel = new JPanel();
        JPanel fieldPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel,BoxLayout.X_AXIS));
        fieldPanel.setLayout(new BoxLayout(fieldPanel,BoxLayout.X_AXIS));
        rowPanel.add(labelPanel,BorderLayout.WEST);
        rowPanel.add(fieldPanel,BorderLayout.CENTER);
        labelPanel.add(Box.createHorizontalGlue());
        labelPanel.add(label);
        labelPanel.add(Box.createHorizontalStrut(15));
        fieldPanel.add(component);
        fieldPanel.add(Box.createHorizontalStrut(15));
        rowPanel.setBackground(getPanelBgColor());
        labelPanel.setBackground(getPanelBgColor());
        fieldPanel.setBackground(getPanelBgColor());
        rowPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = (int) (rowPanel.getWidth()*leftPercent);
                Dimension preferredSize = labelPanel.getPreferredSize();
                labelPanel.setPreferredSize(new Dimension(width,preferredSize.height));
                labelPanel.setMinimumSize(new Dimension(width,preferredSize.height));
                labelPanel.setMaximumSize(new Dimension(width,preferredSize.height));
                labelPanel.updateUI();
            }
        });
        return rowPanel;
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
        try {
            configParser.writhConfigFile();
            SwingTools.showMessageInfoDialog(null,"修改成功,重启后生效",LarkFrame.getI18nText(I18nKey.RedisResource.SETTING));
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
