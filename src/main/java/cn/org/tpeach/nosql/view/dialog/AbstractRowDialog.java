/**
 * 
 */
package cn.org.tpeach.nosql.view.dialog;

import cn.org.tpeach.nosql.tools.SwingTools;
import cn.org.tpeach.nosql.view.component.EasyGBC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
　 * <p>Title: KeyDialog.java</p> 
　 * @author taoyz 
　 * @date 2019年9月4日 
　 * @version 1.0 
 */
public abstract class AbstractRowDialog<T,R> extends BaseDialog<T,R>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -582032971187449454L;

	public AbstractRowDialog(JFrame parent, boolean modal, Image icon, T t) {
		super(parent, modal, icon, t);
	}

	public AbstractRowDialog(JFrame parent, boolean modal, T t) {
		super(parent, modal, t);
	}

	public AbstractRowDialog(JFrame parent, Image icon, T t) {
		super(parent, icon, t);
	}

	public AbstractRowDialog(JFrame parent, T t) {
		super(parent, t);
	}

	protected JComponent createHashTextAreaRow(JLabel lable, JComponent Field, JComponent Field2) {
		JPanel pannel = getPannelPreferredSize(this.getWidth(), rowHeight * 5);
		pannel.setLayout(new GridBagLayout());
		pannel.add(lable,
				EasyGBC.build(0, 0, 1, 1).setFill(EasyGBC.HORIZONTAL).setWeight(0.3, 1.0).resetInsets(topMargin, 10, 0, 0).setAnchor(EasyGBC.EAST));
		pannel.add(Field,
				EasyGBC.build(1, 0, 4, 1).setFill(EasyGBC.HORIZONTAL).setWeight(0.7, 1.0).resetInsets(topMargin, 10, 0, 30).setAnchor(EasyGBC.WEST));
		pannel.add(Field2,
				EasyGBC.build(1, 1, 4, 4).setFill(EasyGBC.HORIZONTAL).setWeight(0.7, 1.0).resetInsets(topMargin, 10, 0, 30).setAnchor(EasyGBC.WEST));
		return pannel;
	}
	/**
	 * 创建label  component的行
	 * @param parentComponent
	 * @param label
	 * @param component
	 * @param rowHeight
	 * @param leftPercent
	 * @return
	 */
	public JPanel createRow(JComponent parentComponent, JComponent label, JComponent component, int rowHeight, double leftPercent){
		return  createRow(parentComponent,label,component,rowHeight,leftPercent,15,15,true);
	}
	/**
	 * 创建label  component的行
	 * @param parentComponent
	 * @param leftConmponet
	 * @param rightComponent
	 * @param rowHeight
	 * @param leftPercent
	 * @return
	 */
	public JPanel createRow(JComponent parentComponent, JComponent leftConmponet, JComponent rightComponent, int rowHeight, double leftPercent,int leftStrut,int rightStrut,boolean isLeftGlue){
		JPanel rowPanel = new JPanel();
		rowPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		rowPanel.setPreferredSize(new Dimension(rowPanel.getPreferredSize().width,rowHeight));
		rowPanel.setMaximumSize(new Dimension(rowPanel.getPreferredSize().width,rowHeight));
		rowPanel.setMinimumSize(new Dimension(rowPanel.getPreferredSize().width,rowHeight));
		//使宽度100%
		SwingTools.fillWidthPanel(parentComponent,rowPanel);
		rowPanel.setLayout(new BoxLayout(rowPanel,BoxLayout.X_AXIS));
		JPanel leftPanel = new JPanel();
		JPanel rightPanel = new JPanel();
		leftPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		rightPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		leftPanel.setLayout(new BoxLayout(leftPanel,BoxLayout.X_AXIS));
		rightPanel.setLayout(new BoxLayout(rightPanel,BoxLayout.X_AXIS));
		rowPanel.add(leftPanel);
		rowPanel.add(rightPanel);
		if(isLeftGlue){
			leftPanel.add(Box.createHorizontalGlue());
		}
		leftPanel.add(leftConmponet);
		leftPanel.add(Box.createHorizontalStrut(leftStrut));
		rightPanel.add(rightComponent);
		rightPanel.add(Box.createHorizontalStrut(rightStrut));
		rowPanel.setBackground(getPanelBgColor());
		leftPanel.setBackground(getPanelBgColor());
		rightPanel.setBackground(getPanelBgColor());
		rowPanel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int width = (int) (rowPanel.getWidth()*leftPercent);
				//保存左边占的比例
				Dimension preferredSize = leftPanel.getPreferredSize();
				Dimension dimension = new Dimension(width, preferredSize.height);
				leftPanel.setPreferredSize(dimension);
				leftPanel.setMinimumSize(dimension);
				leftPanel.setMaximumSize(dimension);
				leftConmponet.setMaximumSize(dimension);
				leftPanel.updateUI();
				preferredSize = rightPanel.getPreferredSize();
				dimension = new Dimension(rowPanel.getWidth() - width, preferredSize.height);
				rightPanel.setPreferredSize(dimension);
				rightPanel.setMinimumSize(dimension);
				rightPanel.setMaximumSize(dimension);
				rightComponent.setMaximumSize(dimension);
				rightPanel.updateUI();
			}
		});

		return rowPanel;
	}
	public JComponent createCompoundRow(JComponent leftConponent,JComponent rightComponent,double leftPercent){
		return  createCompoundRow(leftConponent,rightComponent,leftPercent,EasyGBC.EAST,EasyGBC.WEST,getZeroInsets(),new Insets(0, 10, 0, 0));
	}
	public Insets getZeroInsets(){
		return new Insets(0, 0, 0, 0);
	}

	public JComponent createCompoundRow(JComponent leftComponentt,JComponent rightComponent,double leftPercent,int leftAnchor,int rightAnchor,Insets leftInsets,Insets rightInsets){
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		panel.setBackground(getPanelBgColor());
		panel.setLayout(new GridBagLayout());
		JPanel leftConponentPanel = new JPanel();
		leftConponentPanel.setLayout(new BoxLayout(leftConponentPanel,BoxLayout.X_AXIS));
		leftConponentPanel.setBackground(getPanelBgColor());
		if(EasyGBC.WEST == leftAnchor){
			leftConponentPanel.add(leftComponentt);
			leftConponentPanel.add(Box.createHorizontalGlue());
		}else if(EasyGBC.EAST == leftAnchor){
			leftConponentPanel.add(Box.createHorizontalGlue());
			leftConponentPanel.add(leftComponentt);
		}else if(EasyGBC.CENTER == leftAnchor){
			leftConponentPanel.add(Box.createHorizontalGlue());
			leftConponentPanel.add(leftComponentt);
			leftConponentPanel.add(Box.createHorizontalGlue());
		}
//		JPanel rightConponentPanel = new JPanel();
//		rightConponentPanel.setBackground(getPanelBgColor());
//		rightConponentPanel.setLayout(new BoxLayout(rightConponentPanel,BoxLayout.X_AXIS));
//		if(EasyGBC.WEST == rightAnchor){
//			rightConponentPanel.add(rightComponent);
//			rightConponentPanel.add(Box.createHorizontalGlue());
//		}else if(EasyGBC.EAST == rightAnchor){
//			rightConponentPanel.add(Box.createHorizontalGlue());
//			rightConponentPanel.add(rightComponent);
//		}else if(EasyGBC.CENTER == rightAnchor){
//			rightConponentPanel.add(Box.createHorizontalGlue());
//			rightConponentPanel.add(rightComponent);
//			rightConponentPanel.add(Box.createHorizontalGlue());
//		}
		panel.add(leftConponentPanel,
				EasyGBC.build(0, 0, 1, 1).setFill(EasyGBC.BOTH).setWeight(leftPercent, 1.0).resetInsets(leftInsets).setAnchor(leftAnchor));
		panel.add(rightComponent,
				EasyGBC.build(1, 0, 1, 1).setFill(EasyGBC.BOTH).setWeight(1-leftPercent, 1.0).resetInsets(rightInsets).setAnchor(rightAnchor));
		return panel;
	}
}
