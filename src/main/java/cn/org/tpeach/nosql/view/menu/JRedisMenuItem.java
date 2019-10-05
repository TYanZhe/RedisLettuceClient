/**
 * 
 */
package cn.org.tpeach.nosql.view.menu;

import javax.swing.*;
import java.awt.*;

/**
　 * <p>Title: JRedisMenuItem.java</p> 
　 * @author taoyz 
　 * @date 2019年8月22日 
　 * @version 1.0 
 */
public class JRedisMenuItem extends JMenuItem{
	private Color backgroundColor = new Color(242, 242, 242);
	public JRedisMenuItem() {
		super();
	}

	public JRedisMenuItem(Action a) {
		super(a);
		this.setBackground(backgroundColor);
	}

	public JRedisMenuItem(Icon icon) {
		super(icon);
		this.setBackground(backgroundColor);
	}

	public JRedisMenuItem(String text, Icon icon) {
		super(text, icon);
//		super(text);
		this.setIconTextGap(5);
		this.setBackground(backgroundColor);
//		this.setMargin(new Insets(0, 30, 0, 0));
//		Insets insets = this.getInsets();
//		insets.left = 30;
//		this.setMargin(insets);
//		this.setLayout( new FlowLayout(FlowLayout.LEFT, 0, 0) );
//		this.setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.X_AXIS));
//		JLabel label = new JLabel();
//		label.setIcon(icon);
//		this.add(label);
//		this.setBorder(BorderFactory.createCompoundBorder(this.getBorder(),BorderFactory.createEmptyBorder(0, 30, 0, 0)));

	
	}

	public JRedisMenuItem(String text, int mnemonic) {
		super(text, mnemonic);
		this.setBackground(backgroundColor);
	}

	public JRedisMenuItem(String text) {
		super(text);
		this.setBackground(backgroundColor);
		this.setIconTextGap(5);
	}


	
}
