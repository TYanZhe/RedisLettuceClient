package cn.org.tpeach.nosql.view.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

import cn.org.tpeach.nosql.constant.PublicConstant;

/**
 * <p>
 * Title: RComboBoxUI.java
 * </p>
 * 
 * @author taoyz @date 2019年8月25日 @version 1.0
 */
public class RComboBoxUI extends BasicComboBoxUI {

	private JButton arrow;
	private boolean boundsLight = false;
	private static final int ARCWIDTH = 15;
	private static final int ARCHEIGHT = 15;
	public RComboBoxUI() {
		super();
	}
	
	
	@Override
	protected JButton createArrowButton() {
		arrow = new JButton();
		arrow.setIcon(PublicConstant.Image.arrow_down_blue);
		arrow.setRolloverEnabled(true);
		arrow.setRolloverIcon(PublicConstant.Image.arrow_down_blue);
		arrow.setBorder(null);
		arrow.setBackground(Color.WHITE);
		arrow.setOpaque(true);
//		arrow.setContentAreaFilled(false);
		arrow.getModel().addChangeListener(new ChangeListener() {
		    @Override
		    public void stateChanged(ChangeEvent e) {
		        ButtonModel model = (ButtonModel) e.getSource();
		        if (model.isRollover()) {
		        	arrow.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(40,113,172)));
		        	arrow.setBackground(new Color(204, 228, 246));
		        } else {
		        	if(comboBox.isPopupVisible()) {
			        	arrow.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, PublicConstant.RColor.selectInputColor));
			        	arrow.setBackground(new Color(204, 228, 246));
		        	}else {
		        		arrow.setBorder(null);
			        	arrow.setBackground(Color.WHITE);	
		        	}
		        
		        }
		    }
		});
		comboBox.addPopupMenuListener(new PopupMenuListener() {
			
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			
			}
			
			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
	    		arrow.setBorder(null);
	        	arrow.setBackground(Color.WHITE);	
				
			}
			
			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
					
				
			}
		});
		return arrow;
	}
	@Override
	public void paint(Graphics g, JComponent c) {
		hasFocus = comboBox.hasFocus();
	
		Graphics2D g2 = (Graphics2D) g;
		if (!comboBox.isEditable()) {
			Rectangle r = rectangleForCurrentValue();
			// 重点:JComboBox的textfield 的绘制 并不是靠Renderer来控制 这点让我很吃惊.
			// 它会通过paintCurrentValueBackground来绘制背景
			// 然后通过paintCurrentValue();去绘制JComboBox里显示的值
			paintCurrentValueBackground(g2, r, hasFocus);
			paintCurrentValue(g2, r, hasFocus);
		}

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		int width = (int) this.getPreferredSize(c).getWidth() + arrow.getWidth() - 2;
		int height = 0;
		int heightOffset = 0;
		if (comboBox.isPopupVisible()) {
			heightOffset = 5;
			height = (int) this.getPreferredSize(c).getHeight();
//			arrow.setIcon(PublicConstant.Image.arrow_right_gray_20);
//			arrow.setBackground(new Color(204, 228, 246));
			arrow.setBackground(new Color(204, 228, 246));
			

		} else {
			heightOffset = 0;
			height = (int) this.getPreferredSize(c).getHeight() - 1;
//			arrow.setIcon(PublicConstant.Image.arrow_down_gray_20);
		}
		if (comboBox.isFocusable()) {
			g2.setColor(new Color(150, 207, 254));

		}else {
		}
//		g2.drawRoundRect(0, 0, width, height + heightOffset, ARCWIDTH, ARCHEIGHT);
	}
	@Override
	public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
		Font oldFont = comboBox.getFont();
		// comboBox.setFont(XUtil.defaultComboBoxFont);

		super.paintCurrentValue(g, bounds, hasFocus);
		comboBox.setFont(oldFont);
	}
	@Override
	public Dimension getPreferredSize(JComponent c) {
		return super.getPreferredSize(c);
	}

	public boolean isBoundsLight() {
		return boundsLight;
	}

	public void setBoundsLight(boolean boundsLight) {
		this.boundsLight = boundsLight;
	}
	@Override
	protected ComboPopup createPopup() {
		ComboPopup popup = new BasicComboPopup(comboBox) {
			protected JScrollPane createScroller() {
				JScrollPane sp = new JScrollPane(list, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
						ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
				sp.setHorizontalScrollBar(null);
				return sp;
			}

			// 重载paintBorder方法 来画出我们想要的边框..
			public void paintBorder(Graphics g) {
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(new Color(150, 207, 254));
				g2.drawRoundRect(0, -arrow.getHeight(), getWidth() - 1, getHeight() + arrow.getHeight() - 1, 0, 0);
			}
		};
		return popup;
	}
}
