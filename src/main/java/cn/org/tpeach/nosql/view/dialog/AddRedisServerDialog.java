package cn.org.tpeach.nosql.view.dialog;

import cn.org.tpeach.nosql.constant.I18nKey;
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
import cn.org.tpeach.nosql.view.component.EasyGBC;
import cn.org.tpeach.nosql.view.component.PlaceholderTextField;
import cn.org.tpeach.nosql.view.component.RButton;
import cn.org.tpeach.nosql.view.ui.ServerTabbedPaneUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.concurrent.TimeUnit;

/**
 * @author tyz
 * @Title: AddRedisServerDialog
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-07-05 21:11
 * @since 1.0.0
 */
public class AddRedisServerDialog extends BaseDialog<RedisConnectInfo,RedisConnectInfo>{
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
    JCheckBox structurecheckBox;
    private JLabel structureLable,nameLable,hostLable,authLable;
    private PlaceholderTextField nameField,hostField,portField,authField;
    private RedisConnectInfo connectInfo;

    IRedisConfigService redisConfigService = ServiceProxy.getBeanProxy("redisConfigService", IRedisConfigService.class);
    IRedisConnectService redisConnectService = ServiceProxy.getBeanProxy("redisConnectService", IRedisConnectService.class);


    public AddRedisServerDialog(JFrame parent, Image icon,RedisConnectInfo connectInfo){
    	super(parent, icon, connectInfo);
        initDialog( connectInfo);
    }
    public AddRedisServerDialog(JFrame parent,RedisConnectInfo connectInfo){
    	super(parent, connectInfo);
        initDialog( connectInfo);
    }
    
    


	@Override
    public void initDialog(RedisConnectInfo connectInfo){
        if(connectInfo != null){
            if(StringUtils.isBlank(connectInfo.getId())){
                this.isError = true;
               SwingTools.showMessageErrorDialog(this,"未知异常");
            }
            this.isCluster = connectInfo.getStructure() == 0 ? false : true;
        }
        this.connectInfo = connectInfo;
        this.isEdit = connectInfo == null ? false : true;
        this.setTitle(this.isEdit?"编辑连接":"新增连接");
    }
    /**
     * 面板背景颜色
     * @return
     */
    private Color getPanelBgColor() {
    	return Color.WHITE;
    }
    
    
    
    @Override
    protected void contextUiImpl(JPanel contextPanel,JPanel btnPanel) {
//    	this.rowHeight = LarkFrame.fm.getHeight() > 35 ? LarkFrame.fm.getHeight() : rowHeight;
//    	this.setMinimumSize(new Dimension(1000, 50));
    	super.contextUiImpl(contextPanel, btnPanel);
        //https://www.ibm.com/developerworks/cn/java/j-lo-boxlayout/
        // 创建选项卡面板
        final JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setUI(new ServerTabbedPaneUI("#FFFFFF","#000000"));
        panel = new JPanel();
        panel.setBackground(getPanelBgColor());
        structureLable = new JLabel("集群:",JLabel.RIGHT);
        nameLable = new JLabel("连接名称:",JLabel.RIGHT);
        hostLable = new JLabel("连接地址:",JLabel.RIGHT);
        authLable = new JLabel("连接密码:",JLabel.RIGHT);
        
        structurecheckBox=new JCheckBox("启用",isCluster);
        structurecheckBox.setBackground(getPanelBgColor());
        nameField = new PlaceholderTextField(20);
        nameField.setPlaceholder("连接名称");
        nameField.resetHeightSize(LarkFrame.fm.getHeight()-5);
        hostField = new PlaceholderTextField(null);
        hostField.resetHeightSize(LarkFrame.fm.getHeight()-5);
        portField = new PlaceholderTextField("6379",4);
        portField.resetHeightSize(LarkFrame.fm.getHeight()-5);
        portField.setPlaceholder("连接端口");
        authField = new PlaceholderTextField(20);
        authField.setPlaceholder("连接密码");
        authField.resetHeightSize(LarkFrame.fm.getHeight()-5);


        panel.add(SwingTools.createTextRow(structureLable,structurecheckBox,0.2,0.8,this.getWidth(),rowHeight,getPanelBgColor(),new Insets(10, 10, 0, 0),new Insets(10, 10, 0, 30)));
        panel.add(SwingTools.createTextRow(nameLable,nameField,this.getWidth(),rowHeight,getPanelBgColor()));
        panel.add(createHostTextRow(hostLable,hostField,portField,isCluster));
        panel.add(SwingTools.createTextRow(authLable,authField,this.getWidth(),rowHeight,getPanelBgColor()));
//

//        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1,2,2,2,Color.GRAY),BorderFactory.createEmptyBorder(5,5,5,5)));
//        tabbedPane.addTab("Server", null, panel, false);
        tabbedPane.addTab("Server",panel);
        contextPanel.setLayout(new BorderLayout());
        contextPanel.add(tabbedPane,BorderLayout.CENTER);
        if(connectInfo != null){
            nameField.setText(connectInfo.getName());
            hostField.setText(connectInfo.getHost());
            portField.setText(connectInfo.getPort()+"");
            authField.setText(connectInfo.getAuth());

        }
        //监听事件
        structurecheckBox.addItemListener(e->judgCluster((JCheckBox) e.getSource()));
    }
   

    /**
     * 是否是集群
     * @param chectBox
     */
    private void judgCluster(JCheckBox chectBox){
        final boolean selected =chectBox.isSelected();
        //集群
        if(selected){
            panel.remove(hostIndex);
            panel.add(createHostTextRow(hostLable,hostField,portField,true),hostIndex);
        }else{
            panel.remove(hostIndex);
            panel.add(createHostTextRow(hostLable,hostField,portField,false),hostIndex);
        }
        panel.updateUI();


    }

    private JComponent createHostTextRow(JLabel hostLable,PlaceholderTextField hostField,PlaceholderTextField portField,boolean isCluster){
        JComponent hostPannel = null;
        if(isCluster){
            hostField.setColumns(20);
            hostField.setPlaceholder("ip1:port1,ip2:port2,ip3:port3...");
            hostPannel = SwingTools.createTextRow(hostLable,hostField,this.getWidth(),rowHeight);
        }else{
            hostField.setColumns(14);
            hostField.setPlaceholder("连接地址");
            hostPannel = getPannelPreferredSize(this.getWidth(),rowHeight);
            hostPannel.setLayout(new GridBagLayout());
            hostPannel.add(hostLable,
    				EasyGBC.build(0, 0, 1, 1).setFill(EasyGBC.HORIZONTAL).setWeight(0.3, 1.0).resetInsets(topMargin, 10, 0,0).setAnchor(EasyGBC.EAST));

            hostPannel.add(hostField,
    				EasyGBC.build(1, 0, 3, 1).setFill(EasyGBC.HORIZONTAL).setWeight(0.55, 1.0).resetInsets(topMargin, 10, 0,0).setAnchor(EasyGBC.WEST));
            hostPannel.add(portField,
    				EasyGBC.build(4, 0, 1, 1).setFill(EasyGBC.HORIZONTAL).setWeight(0.15, 1.0).resetInsets(topMargin, 13, 0, 30).setAnchor(EasyGBC.WEST));
    		
        }
        hostPannel.setBackground(getPanelBgColor());
        return hostPannel;

    }

    @Override
    public Box addBtnToBtnPanel(JPanel btnPanel) {
        String testText = LarkFrame.getI18nUpText(I18nKey.RedisResource.TEST);
        JButton testBtn = new RButton(testText);
        testBtn.setBackground(new Color(108,117,125));
        testBtn.setForeground(Color.WHITE);
        btnbox.add(testBtn);
        btnbox.add(btnHorizontalStrut());
        testBtn.addActionListener(e->{
    		
    			testBtn.setEnabled(false);
    			testBtn.setText(LarkFrame.getI18nUpText(I18nKey.RedisResource.CONNECTING));
    			LarkFrame.executorService.execute(()->{
    				try {
    					RedisConnectInfo item = validForm();
    		                ResultRes<Boolean> dispatcher = BaseController.dispatcher(() -> redisConnectService.connectTest(item));
    		                if(dispatcher.isRet()) {
    		                	  testBtn.setBackground(new Color(132,181,71));
    		                      testBtn.setText(LarkFrame.getI18nUpText(I18nKey.RedisResource.SUCCESS));
    		                }else {
    		                	  testBtn.setBackground(Color.RED);
    		                      testBtn.setText(LarkFrame.getI18nUpText(I18nKey.RedisResource.FAIL));
    		                      LarkFrame.executorService.schedule(()->testBtn.setText(LarkFrame.getI18nUpText(I18nKey.RedisResource.RETRY)), 2, TimeUnit.SECONDS);
    		                      SwingTools.showMessageErrorDialog(this,dispatcher.getMsg());
    		                }
    				}catch (ServiceException ex) {
    					testBtn.setText(testText);
    	    			 SwingTools.showMessageErrorDialog(this,ex.getMessage());
    	    		}finally {
    	    			testBtn.setEnabled(true);
    	    		}
    			});
    					
    					
    					
           
              
    		
          
        });
        return super.addBtnToBtnPanel(btnPanel);
    }
    
    private RedisConnectInfo validForm() {
        if(consumer == null){
            throw new ServiceException("未绑定回调事件");
        }
        
//        private PlaceholderTextField nameField,hostField,portField,authField;
        final boolean selected = structurecheckBox.isSelected();
        String host = hostField.getText();
        String auth = authField.getText();
        String name = nameField.getText();
        //校验
        if(StringUtils.isBlank(name)){
            throw new ServiceException("请输入连接名");
        }else if(StringUtils.isBlank(host)){
            throw new ServiceException("请输入连接地址");
        }
        int structure = selected ? 1 : 0;
        int port = -1;
        String id = null;
        if(connectInfo != null){
            id = connectInfo.getId();
        }
        if(!selected){
            try{
                 port  = Integer.valueOf(portField.getText());
            }catch (NumberFormatException ex){
                throw new ServiceException("请输入正确的端口");
            }
        }
		return new RedisConnectInfo((short) 0,id,structure,name,host,port,auth);
    }
    
    @Override
    protected void submit(ActionEvent e) {
		try {
			this.okBtn.setEnabled(false);
            RedisConnectInfo item = validForm();
            ResultRes<RedisConnectInfo> resultRes ;
            if(connectInfo == null){
                resultRes = BaseController.dispatcher(() -> redisConfigService.addRedisConfig(item));
            }else{
                item.setId(connectInfo.getId());
                resultRes = BaseController.dispatcher(() -> redisConfigService.updateRedisConfig(item));
            }
            if(resultRes.isRet()){
                consumer.accept(item);
                this.dispose();
            }else{
                SwingTools.showMessageErrorDialog(this,"未知错误：添加配置失败");
            }	
		}catch (ServiceException ex) {
			 SwingTools.showMessageErrorDialog(this,ex.getMessage());
		}finally {
			this.okBtn.setEnabled(true);
		}
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
