/**
 * 
 */
package cn.org.tpeach.nosql.view.component;

import javax.swing.*;
import java.awt.*;

/**
 * <p>
 * Title: RComboBoxRenderer.java
 * </p>
 * 
 * @author taoyz @date 2019年8月25日 @version 1.0
 */
public class RComboBoxRenderer<E> implements ListCellRenderer<E> {
	private DefaultListCellRenderer defaultCellRenderer = new DefaultListCellRenderer();

	public RComboBoxRenderer() {
		super();
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends E> list, E value, int index, boolean isSelected,
			boolean cellHasFocus) {
		JLabel renderer = (JLabel) defaultCellRenderer.getListCellRendererComponent(list, value, index, isSelected,
				cellHasFocus);
		if (isSelected) {
			renderer.setBackground(new Color(0,120,214));
			renderer.setForeground(Color.WHITE);
		} else {
			renderer.setBackground(new Color(254,254,254));
			
		}
		list.setSelectionBackground(Color.WHITE);
		list.setBorder(null);
//		renderer.setFont(XUtil.defaultComboBoxFont);
		renderer.setHorizontalAlignment(JLabel.LEFT);
		return renderer;
	}

}
