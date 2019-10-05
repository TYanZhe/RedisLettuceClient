/**
 * 
 */
package cn.org.tpeach.nosql.view.component;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * <p>
 * Title: EasyButton.java
 * </p>
 * @author taoyz @date 2019年8月24日 @version 1.0
 */
public class RButton extends JButton implements MouseListener {
	private Color bgColor = new Color(224, 224, 224);
	private Color borderColor = new Color(179,179,179);
	private Color borderMouseColor = new Color(0,105,212);
	/**
	 * 
	 */
	private static final long serialVersionUID = 21913286175962804L;

	public RButton() {
		super();
		beforeStyle();
		init();
	}

	public RButton(Action a) {
		super(a);
		beforeStyle();
		init();
	}

	public RButton(Icon icon) {
		super(icon);
		beforeStyle();
		init();
	}

	public RButton(String text, Icon icon) {
		super(text, icon);
		beforeStyle();
		init();
	}

	public RButton(String text) {
		super(text);
		beforeStyle();
		init();
	}
	private void beforeStyle(){
		this.setUI(new BasicButtonUI());// 恢复基本视觉效果
		this.addMouseListener(this);
		// this.setContentAreaFilled(false);// 设置按钮透明
		this.setFont(new Font("宋体", Font.PLAIN, 15));// 按钮文本样式
		this.setBackground(bgColor);
		// this.setOpaque(false);
		this.setMargin(new Insets(0, 0, 0, 0));// 按钮内容与边框距离

//		this.setBorder(BorderFactory.createLineBorder(borderColor));
		this.setMinimumSize(new Dimension(90, 28));// 设置按钮大小
		this.setMaximumSize(new Dimension(180, 28));
		this.setPreferredSize(new Dimension(90, 28));

	}
	public void init() {
		this.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(borderColor, 1),
				getButtonEmptyBorder()));

	}


	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {
		init();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		this.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(borderMouseColor, 1),
				getButtonEmptyBorder() ));
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}
	private Border getButtonEmptyBorder() {
		return BorderFactory.createEmptyBorder(3, 15, 3, 15);
	}
}
