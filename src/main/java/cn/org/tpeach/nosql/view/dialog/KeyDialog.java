/**
 * 
 */
package cn.org.tpeach.nosql.view.dialog;

import cn.org.tpeach.nosql.view.component.EasyGBC;

import javax.swing.*;
import java.awt.*;

/**
　 * <p>Title: KeyDialog.java</p> 
　 * @author taoyz 
　 * @date 2019年9月4日 
　 * @version 1.0 
 */
public abstract class KeyDialog<T,R> extends BaseDialog<T,R>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -582032971187449454L;

	public KeyDialog(JFrame parent, boolean modal, Image icon, T t) {
		super(parent, modal, icon, t);
	}

	public KeyDialog(JFrame parent, boolean modal, T t) {
		super(parent, modal, t);
	}

	public KeyDialog(JFrame parent, Image icon, T t) {
		super(parent, icon, t);
	}

	public KeyDialog(JFrame parent, T t) {
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
}
