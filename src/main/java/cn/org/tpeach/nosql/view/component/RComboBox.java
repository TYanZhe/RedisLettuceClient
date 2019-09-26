/**
 * 
 */
package cn.org.tpeach.nosql.view.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

import cn.org.tpeach.nosql.constant.PublicConstant;
import cn.org.tpeach.nosql.view.ui.RComboBoxUI;

/**
 * <p>
 * Title: RComboBox.java
 * </p>
 * 
 * @author taoyz @date 2019年8月25日 @version 1.0
 */
public class RComboBox<E> extends JComboBox<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5972296716514445836L;

	private int colums = 20;

	public RComboBox() {
		super();
		init();
	}

	public RComboBox(int colums) {
		super();
		this.colums = colums;
		init();
	}

	public RComboBox(ComboBoxModel<E> aModel) {
		super(aModel);
		init();
	}

	public RComboBox(E[] items) {
		super(items);
		init();
	}

	public RComboBox(E[] items, int colums) {
		super(items);
		this.colums = colums;
		init();
	}

	public RComboBox(Vector<E> items) {
		super(items);
		init();
	}

	public RComboBox(Vector<E> items, int colums) {
		super(items);
		this.colums = colums;
		init();
	}

	private void init() {
		this.setOpaque(false);
		this.setUI(new RComboBoxUI());
		this.setRenderer(new RComboBoxRenderer());
		this.setBackground(Color.WHITE);
		this.setMinimumSize(new Dimension(0, 25));
		this.setPreferredSize(new Dimension((int) (colums * 11.3), 25));
		this.setBorder(BorderFactory.createLineBorder(PublicConstant.RColor.defalutInputColor));
		this.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				RComboBox.this.setBorder(BorderFactory.createLineBorder(PublicConstant.RColor.defalutInputColor));
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				RComboBox.this.setBorder(BorderFactory.createLineBorder(PublicConstant.RColor.selectInputColor));
			}
		});  

	}

	public Dimension getPreferredSize() {
		return super.getPreferredSize();
	}
}
