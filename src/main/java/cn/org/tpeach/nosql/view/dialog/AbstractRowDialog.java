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

	public JComponent createCompoundRow(JComponent leftConponent,JComponent rightComponent,double leftPercent){
		JPanel panel = new JPanel();
		panel.setBackground(getPanelBgColor());
		panel.setLayout(new GridBagLayout());
		panel.add(leftConponent,
				EasyGBC.build(0, 0, 1, 1).setFill(EasyGBC.BOTH).setWeight(leftPercent, 1.0).resetInsets(0, 0, 0, 0).setAnchor(EasyGBC.WEST));
		panel.add(rightComponent,
				EasyGBC.build(1, 0, 1, 1).setFill(EasyGBC.BOTH).setWeight(1-leftPercent, 1.0).resetInsets(0, 10, 0, 0).setAnchor(EasyGBC.EAST));
		return panel;
	}
}
