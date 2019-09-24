package cn.org.tpeach.nosql.view.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import cn.org.tpeach.nosql.constant.I18nKey;
import cn.org.tpeach.nosql.constant.PublicConstant;
import cn.org.tpeach.nosql.framework.LarkFrame;
import cn.org.tpeach.nosql.tools.SwingTools;
import cn.org.tpeach.nosql.view.component.EasyGBC;
import cn.org.tpeach.nosql.view.component.RButton;
import lombok.Getter;
import lombok.Setter;

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
	private int btnPanelHeight = 56;
	@Getter
	private JPanel contextPanel;
	private JPanel middlePanel;
	@Getter
	private JPanel btnPanel;
	protected Image icon = PublicConstant.Image.logo.getImage();
	protected Box btnbox = Box.createHorizontalBox();
	JButton okBtn = new RButton(LarkFrame.getI18nUpText(I18nKey.RedisResource.OK));
	JButton cancelBtn = new RButton(LarkFrame.getI18nUpText(I18nKey.RedisResource.CANCEL));
	// 错误关闭 单开一个线程调用dispose()才可以关闭 原因未知
	protected boolean isError = false;
	// 改变后通知
	protected Consumer<R> consumer;

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
		
	}

	/**
	 * @param t
	 */
	public abstract void initDialog(T t);
	
	public void setMinimumSize() {
		this.setMinimumSize(new Dimension(minWidth, minHeight));
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
	public void open() {
		
		setMinimumSize();
		if (isError) {
//			new Thread(() -> this.dispose()).start();
			close();
			return;
		}
		
		// 设置网格包布局
		Container container = this.getContentPane();
		// this.getRootPane().setBorder(BorderFactory.createLineBorder(new
		// Color(66,153,221),1));
		container.setLayout(new BorderLayout());
		containerStyle(container);


		if(middlePanel == null){
			middlePanel = new JPanel();
			middlePanel.setOpaque(false);
			middlePanel.setLayout(new BorderLayout());
			setMiddlePanel(middlePanel);
			container.add(middlePanel);
		}

		if(btnPanel == null) {
			btnPanel = new JPanel();
			container.add(btnPanel, BorderLayout.PAGE_END);
			if (isNeedBtn()) {
				btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(173, 173, 173)));
				btnPanel.setPreferredSize(new Dimension(this.getWidth(), btnPanelHeight));
				btnPanel.add(btnbox);
				addBtnToBtnPanel(btnPanel);
			}
		}
		if(contextPanel == null){
			contextPanel = new JPanel();
			middlePanel.add(contextPanel, BorderLayout.CENTER);
			contextUiImpl(contextPanel, btnPanel);
		}
		// middlePanel.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(10,
		// 10, 10, 10), new EtchedBorder()));
		after();
		this.setVisible(true);
		if (isError) {
			close();
			return;
		}
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

	// ------get set
	public int getBtnPanelHeight() {
		return btnPanelHeight;
	}

	public void setBtnPanelHeight(int btnPanelHeight) {
		this.btnPanelHeight = btnPanelHeight;
	}

	/**
	 * @param e
	 */
	protected void submit(ActionEvent e) {

	}

	/**
	 * @param e
	 */
	protected void cancel(ActionEvent e) {
		this.dispose();
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