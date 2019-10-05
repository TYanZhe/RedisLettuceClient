package cn.org.tpeach.nosql.view.component;

import cn.org.tpeach.nosql.tools.StringUtils;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

/**
 * 
 * 版权声明：本文为CSDN博主「梦科」的原创文章，遵循CC 4.0 by-sa版权协议，转载请附上原文出处链接及本声明。
 * 原文链接：https://blog.csdn.net/wqjsir/article/details/7346924
 * <p>
 * Title: NonRectanglePopupFactory.java
 * </p>
 */
public class NonRectanglePopupFactory extends PopupFactory {
	private static final int BORDER_PAD = 20;
	private static Robot robot;
	static {
		try {
			robot = new Robot();
		} catch (Exception e) {
		}
	}

	public NonRectanglePopupFactory() {
	}

	@Override
	public Popup getPopup(Component owner, Component contents, int x, int y) throws IllegalArgumentException {
		if (contents instanceof JToolTip) {
			if(StringUtils.isBlank(((JToolTip) contents).getTipText())) {
				return super.getPopup(owner, contents, x, y);
			}
			((JToolTip) contents).setBorder(null);
			Dimension dim = contents.getPreferredSize();
			Rectangle bound = new Rectangle(x, y, dim.width + 2 * BORDER_PAD, dim.height + 2 * BORDER_PAD);
			/**
			 * 这是关键创建包含从屏幕中读取的像素的图像。该图像不包括鼠标光标。
			 */
			BufferedImage backgroundImage = robot.createScreenCapture(bound);
			NonRectangleFrame frame = new NonRectangleFrame(owner, contents, backgroundImage);
			return super.getPopup(owner, frame, x, y);
		} else
			return super.getPopup(owner, contents, x, y);
	}

	/**
	 * 
	 * @author mengke
	 * @email wqjsir@foxmail.com
	 * @version 1.0
	 */
	class NonRectangleFrame extends JComponent {
		private static final long serialVersionUID = -7139595600416626310L;
		private Color backColor = new Color(254, 254, 224);
		private Color borderColor = new Color(137,137,127);
		public NonRectangleFrame(Component owner, Component content, BufferedImage backgroundImage) {
			setLayout(new BorderLayout());
			setPreferredSize(new Dimension(content.getPreferredSize().width+10,content.getPreferredSize().height+5));
			add(content, BorderLayout.CENTER);
			setOpaque(true);
			content.setBackground(backColor);
			setBackground(backColor);
//			setBorder(new NonRectangleBorder(owner, content, backgroundImage));
			setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, borderColor));
		}
	}

	/**
	 * @author mengke
	 * @email wqjsir@foxmail.com
	 * @version 1.0
	 */
	class NonRectangleBorder implements Border {
		private BufferedImage leftImage;
		private BufferedImage rightImage;
		private BufferedImage topImage;
		private BufferedImage bottomImage;
		private Component content;
		private Color backColor = new Color(254, 254, 224);
		private Color borderColor = new Color(95, 145, 145);

		NonRectangleBorder(Component owner, Component content, BufferedImage backgroundImage) {
			this.content = content;
			// backColor = this.content.getBackground();
			// borderColor = backColor.darker();
			generateLeftImage(backgroundImage);
			generateTopImage(backgroundImage);
			generateRightImage(backgroundImage);
			generateBottomImage(backgroundImage);
		}

		@Override
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			// 绘制图形，这些图形是当前位置的截图图形
			g.drawImage(leftImage, x, y, c);
			g.drawImage(rightImage, x + width - BORDER_PAD, y, c);
			g.drawImage(topImage, x + BORDER_PAD, y, c);
			g.drawImage(bottomImage, x + BORDER_PAD, y + height - BORDER_PAD, c);
			Rectangle bounds = new Rectangle(x, y, width, height);
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setColor(backColor); // 背景颜色
			content.setBackground(backColor);// 使背景色与填充的多边形颜色一致
			g2d.fill(getArea(bounds.getSize()));
			g2d.setColor(borderColor);
			g2d.draw(getArea(bounds.getSize()));// 画边框

			g.setColor(Color.black);
		}

		/**
		 * 返回画图所需要的区域<br>
		 * 这里主要用到了图形合并共能。通过图形合并我们可以实现各种自定义的图形
		 * 
		 * @param dim
		 * @return
		 */
		private Area getArea(Dimension dim) {
			int roundX = BORDER_PAD - 2;
			int roundY = BORDER_PAD - 2;
			Shape r = new RoundRectangle2D.Float(roundX, roundY, dim.width - roundX * 2, dim.height - roundY * 2, 5, 5); // 圆角矩形
			Area area = new Area(r);
			Polygon polygon = new Polygon();// 多边形
			polygon.addPoint(22, roundY);
			polygon.addPoint(35, roundY);
			polygon.addPoint(22, 0);
			area.add(new Area(polygon)); // 合并图形
			return area;
		}

		@Override
		public Insets getBorderInsets(Component c) {
			return new Insets(BORDER_PAD, BORDER_PAD, BORDER_PAD, BORDER_PAD);
		}

		@Override
		public boolean isBorderOpaque() {
			return true;
		}

		private void generateLeftImage(BufferedImage backgroundImage) {
			leftImage = backgroundImage.getSubimage(0, 0, BORDER_PAD, backgroundImage.getHeight());
		}

		private void generateTopImage(BufferedImage backgroundImage) {
			int w = backgroundImage.getWidth() - 2 * BORDER_PAD;
			topImage = backgroundImage.getSubimage(BORDER_PAD, 0, w, BORDER_PAD);
		}

		private void generateRightImage(BufferedImage backgroundImage) {
			rightImage = backgroundImage.getSubimage(backgroundImage.getWidth() - BORDER_PAD, 0, BORDER_PAD,
					backgroundImage.getHeight());
		}

		private void generateBottomImage(BufferedImage backgroundImage) {
			int w = backgroundImage.getWidth() - 2 * BORDER_PAD;
			bottomImage = backgroundImage.getSubimage(BORDER_PAD, backgroundImage.getHeight() - BORDER_PAD, w,
					BORDER_PAD);
		}
	}
}
