/**
 * 
 */
package cn.org.tpeach.nosql.view.menu;

import javax.swing.*;
import java.awt.*;

/**
 * <p>
 * Title: JRedisPopupMenu.java
 * </p>
 * 
 * @author taoyz @date 2019年8月22日 @version 1.0
 */
public class JRedisPopupMenu extends JPopupMenu {
	private Color backgroundColor = new Color(242, 242, 242);
	private static final long serialVersionUID = -5507226240512355991L;
	private final static int WIDTH = 272;

	private final static int ROW_HEIGHT = 28;
	private int menuItemCount = 0;;

	public JRedisPopupMenu() {
		super();
		this.setBackground(backgroundColor);
		this.setMinimumSize(new Dimension(WIDTH, 0));
		this.setBorder(BorderFactory.createLineBorder(new Color(216,216,216)));
	}

	public JRedisPopupMenu(String label) {
		super(label);
	}

	@Override
	public JMenuItem add(JMenuItem menuItem) {
		JMenuItem item = super.add(menuItem);
		menuItemCount++;
		this.setPopupSize(WIDTH, menuItemCount * ROW_HEIGHT);
		return item;
	}

	@Override
	public void addSeparator() {
		JRedisPopupMenu.Separator separator = new JRedisPopupMenu.Separator();
		// separator.getComponentAt(90, separator.getY());
		this.add(separator);
		separator.setBackground(new Color(242, 242, 242));
		separator.setForeground(new Color(220, 220, 220));
	}

	/**
	 * A popup menu-specific separator.
	 */
	@SuppressWarnings("serial")
	static public class Separator extends JSeparator {
		public Separator() {
			super(JSeparator.HORIZONTAL);
		}

		/**
		 * Returns the name of the L&amp;F class that renders this component.
		 *
		 * @return the string "PopupMenuSeparatorUI"
		 * @see JComponent#getUIClassID
		 * @see UIDefaults#getUI
		 */
		@Override
		public String getUIClassID() {
			return "PopupMenuSeparatorUI";

		}
		
		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			
			int x = 28;
			int y = 0;
			int width = getWidth() ;
			//图片背景为白色
//			g2d.fillRect(x, y, width, 2);
			// 设置线的型式
			Stroke stroke = new BasicStroke(2, // 线宽
					BasicStroke.CAP_SQUARE, // 端点样式
					BasicStroke.JOIN_BEVEL, // 接头样式
					15.0f, // 拼接限制
					null, // 虚线
					5.0f); // 虚线的设置
			  g2d.setStroke(stroke);
			  g2d.setColor(new Color(220, 220, 220));
			  g2d.drawLine(x, y, width, y);
			  g2d.dispose();
		}
	}


	

}
