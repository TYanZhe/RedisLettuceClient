package cn.org.tpeach.nosql.view.dialog;

import cn.org.tpeach.nosql.constant.I18nKey;
import cn.org.tpeach.nosql.constant.PublicConstant;
import cn.org.tpeach.nosql.framework.LarkFrame;
import cn.org.tpeach.nosql.tools.SwingTools;
import cn.org.tpeach.nosql.view.StatePanel;
import cn.org.tpeach.nosql.view.component.RButton;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class BaseDialog<T,R> extends JDialog implements WindowListener {
	protected T t;
	@Getter
	@Setter
	protected int rowHeight = 35;
	@Getter
	@Setter
	protected int topMargin = 10;
	protected static final int GAP = 3;
	protected static final Insets LABEL_INSETS = new Insets(GAP, GAP, GAP, 3);
	protected static final Insets TEXTFIELD_INSETS = new Insets(GAP, GAP, GAP, GAP);

	private static final long serialVersionUID = -923835521999294714L;
	@Getter
	@Setter
	protected int minWidth = 476;
	@Getter
	@Setter
	protected int minHeight = 350;
	@Getter
	@Setter
	protected int marginx = 10;
	@Getter
	@Setter
	protected int marginy = 10;
	@Getter
	@Setter
	protected int btnPanelHeight = 56;
	@Getter
	protected JPanel contextPanel;
	protected JPanel middlePanel;
	@Getter
	protected JPanel btnPanel;
	protected Image icon = PublicConstant.Image.getImageIcon(PublicConstant.Image.logo).getImage();
	protected Box btnbox = Box.createHorizontalBox();
	protected Map<String,Object> containerMap = new HashMap<>();
	JButton okBtn = new RButton(LarkFrame.getI18nUpText(I18nKey.RedisResource.OK));
	JButton cancelBtn = new RButton(LarkFrame.getI18nUpText(I18nKey.RedisResource.CANCEL));

	protected boolean isError = false;
	// 改变后通知
	protected Consumer<R> consumer;

	private boolean init = false;
	public BaseDialog(JFrame parent, T t) {
		this(parent, true, null, t);
	}

	public BaseDialog(JFrame parent, Image icon, T t) {
		this(parent, true, icon, t);
	}

	/**
	 * @param parent
	 *            父Frame
	 * @param modal
	 *            是否模式窗体
	 */
	public BaseDialog(JFrame parent, boolean modal, T t) {
		this(parent, modal, null, t);
	}

	/**
	 * @param parent
	 *            父Frame
	 * @param modal
	 *            是否模式窗体
	 */
	public BaseDialog(JFrame parent, boolean modal, Image icon, T t) {
		super(parent, modal);
		if (icon != null) {
			this.icon = icon;
		}
		this.t = t;
		initDialog(t);
		this.cancelBtn.addActionListener(e -> cancel(e));
		this.okBtn.addActionListener(e -> submit(e));
		this.addWindowListener(this);
		middlePanel = new JPanel();
		btnPanel = new JPanel();
		contextPanel = new JPanel();

		Container container = this.getContentPane();
		// this.getRootPane().setBorder(BorderFactory.createLineBorder(new
		// Color(66,153,221),1));
		container.setLayout(new BorderLayout());
		container.add(middlePanel,BorderLayout.CENTER);
		container.add(btnPanel, BorderLayout.PAGE_END);
	}

	/**
	 * @param t
	 */
	public abstract void initDialog(T t);
	
	public void setMinimumSize() {
		Dimension screenSize = SwingTools.getScreenSize();
		int width = (int) (screenSize.width * 0.3);
		Dimension dimension = width < minWidth ? new Dimension(minWidth,minHeight):this.getAdaptDialogMinimumSize(minWidth,minHeight,0.3);
		this.setMinimumSize( dimension);
	}
	
	public Dimension getAdaptDialogMinimumSize(int minWidth,int minHeight) {
	    return getAdaptDialogMinimumSize(minWidth, minHeight, 0.25);
	}
	public Dimension getAdaptDialogMinimumSize(int minWidth,int minHeight,double percent) {
		Dimension screenSize = SwingTools.getScreenSize();
		int width = (int) (screenSize.width * percent);
	    int   height = minHeight * width / minWidth;
	    return new Dimension(width,height);
	}
	public abstract boolean isNeedBtn();

	protected void setMiddlePanel(JPanel middlePanel){
		middlePanel.setBorder(new EmptyBorder(10, 10, 35, 10));
	}
	/**
	 * 面板背景颜色
	 * @return
	 */
	protected Color getPanelBgColor() {
		return Color.WHITE;
	}

	public void open() {
		try {
			setMinimumSize();
			if (isError) {
				close();
				return;
			}
			if (!init) {
				// 设置网格包布局
				Container container = this.getContentPane();
				// this.getRootPane().setBorder(BorderFactory.createLineBorder(new
				// Color(66,153,221),1));
				containerStyle(container);
				middlePanel.setOpaque(false);
				middlePanel.setLayout(new BorderLayout());
				setMiddlePanel(middlePanel);

				btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(173, 173, 173)));
				btnPanel.setPreferredSize(new Dimension(this.getWidth(), btnPanelHeight));
				btnPanel.add(btnbox);
				addBtnToBtnPanel(btnPanel);
				middlePanel.add(contextPanel, BorderLayout.CENTER);
				contextUiImpl(contextPanel, btnPanel);


			}
			btnPanel.setVisible(isNeedBtn());

			// middlePanel.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(10,
			// 10, 10, 10), new EtchedBorder()));

		}finally {
			after();
		}
		CountDownLatch countDownLatch = new CountDownLatch(1);
		SwingTools.swingWorkerExec(()->{countDownLatch.countDown();visibleExec();});
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.init = true;

		if (isError) {
			close();
			return;
		}
	}
	public void visibleExec() {
		this.setVisible(true);
	}
	public void containerStyle(Container container) {
	}


	public void close() {
		this.setVisible(false);
		new Thread(() -> this.dispose()).start();
	}
	
	public Box addBtnToBtnPanel(JPanel btnPanel) {

		btnbox.add(Box.createVerticalStrut(btnPanelHeight - 10));
//        okBtn.setBackground(new Color(255,0,0));
        okBtn.setBackground(new Color(44,151,222));
        okBtn.setForeground(Color.WHITE);
//        cancelBtn.setBackground(new Color(44,151,222));
        cancelBtn.setBackground(new Color( 45,189,168));
       
        cancelBtn.setForeground(Color.WHITE);
		btnbox.add(okBtn);
		btnbox.add(btnHorizontalStrut());
		btnbox.add(cancelBtn);


		return btnbox;
	}

	public Component btnHorizontalStrut() {
		return Box.createHorizontalStrut(20);
	}

	protected void contextUiImpl(JPanel contextPanel, JPanel btnPanel) {
		contextPanel.setBounds(marginx, marginy, this.getWidth() - (2 * marginx) - 6,
				this.getHeight() - 2 * marginy - this.getBtnPanelHeight() - 25);
	}

	public void getResult(Consumer<R> consumer) {
		this.consumer = consumer;
	}

	public void after() {
		this.setResizable(false);
		// 居中
		this.setLocationRelativeTo(null);
		this.setIconImage(icon);
	}

	/**
	 * 创建一个面板，面板中心显示一个标签，用于表示某个选项卡需要显示的内容
	 */
	// protected JComponent createTextRow(JLabel lable,JComponent Field,int
	// rowHeight) {
	// JPanel pannel = getPannelPreferredSize(this.getWidth(),rowHeight);
	// pannel.setLayout(new GridBagLayout());
	// pannel.add(lable,EasyGBC.build(0,0,1,1).setFill(EasyGBC.BOTH).setWeight(1.0,1).setAnchor(GridBagConstraints.EAST)
	// .resetInsets(LABEL_INSETS));
	// pannel.add(Field,EasyGBC.build(1,0,1,1).setFill(EasyGBC.HORIZONTAL).setWeight(1.0,1).setAnchor(GridBagConstraints.WEST)
	// .resetInsets(TEXTFIELD_INSETS));
	// return pannel;
	// }





	public JPanel getPannelPreferredSize(int width, int height) {
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(width, height));
		return panel;
	}


	/**
	 * @param e
	 */
	protected void submit(ActionEvent e) {

	}
	public void submit(final JButton okBtn, Runnable request,boolean timeout,BiConsumer<String,Double>hiddenLister){
		StatePanel.showLoading(true,()->{
			if(okBtn != null){
				okBtn.setEnabled(false);
			}else{
				this.okBtn.setEnabled(false);
			}
			try{
				request.run();
			}finally {
				if(okBtn != null){
					okBtn.setEnabled(true);
				}else{
					this.okBtn.setEnabled(true);
				}
			}
		},true,true,timeout ? StatePanel.DEFAULTTIMEOUT : -1,hiddenLister);

//
//		Layer.showLoading_v3(false,timeout,()->{
//			if(okBtn != null){
//				okBtn.setEnabled(false);
//			}else{
//				this.okBtn.setEnabled(false);
//			}
//
//			try{
//				request.run();
//			}finally {
//				if(okBtn != null){
//					okBtn.setEnabled(true);
//				}else{
//					this.okBtn.setEnabled(true);
//				}
//			}
//		});
	}
	public void submit(final JButton okBtn, Runnable request ){
		submit(okBtn,request,true,null);
	}
	public void submit(Runnable request){
		submit(null,request);
	}
	public void submit(Runnable request,boolean timeout){
		submit(null,request,timeout,null);
	}
	/**
	 * @param e
	 */
	protected void cancel(ActionEvent e) {
		close();
	}

	@Override
	public void windowActivated(WindowEvent e) {

	}

	@Override
	public void windowClosed(WindowEvent e) {


	}

	@Override
	public void windowClosing(WindowEvent e) {
		close();
	}

	@Override
	public void windowDeactivated(WindowEvent e) {

	}

	@Override
	public void windowDeiconified(WindowEvent e) {

	}

	@Override
	public void windowIconified(WindowEvent e) {

	}

	@Override
	public void windowOpened(WindowEvent e) {

	}

}