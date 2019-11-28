/**
 *
 */
package cn.org.tpeach.nosql.view.component;

import cn.org.tpeach.nosql.constant.PublicConstant;
import cn.org.tpeach.nosql.tools.SwingTools;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Vector;
import java.util.function.Consumer;

/**
 * <p>
 * Title: RTabbedPane.java
 * </p>
 * @author taoyz @date 2019年8月30日 @version 1.0
 */
public class RTabbedPane extends javax.swing.JTabbedPane {
	private final static int tabWidth =196;
	private List<Consumer<Object>> removeList = new Vector<>();
	public void addRemoveLister(Consumer<Object> consumer){
		removeList.add(consumer);
	}
	@Getter
	@Setter
	private boolean mouseWheelMoved = true;
	private static class RBasicTabbedPanelUI extends BasicTabbedPaneUI{
		private RTabbedPane tabbedPane;

		public RBasicTabbedPanelUI(RTabbedPane tabbedPane) {
			super();
			this.tabbedPane = tabbedPane;
		}

		// 计算tab页签的宽度
		@Override
		protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
			// 可根据placement来做不同的调整
			int width = super.calculateTabWidth(tabPlacement, tabIndex, metrics) ;
			if(width > 213){
				width = 213;
			}
//	    	Component tabComponentAt = this.tabbedPane.getTabComponentAt(tabIndex);
//	    	if(tabComponentAt != null && tabComponentAt instanceof ButtonClose) {
//	    		ButtonClose closeButton = (ButtonClose) tabComponentAt;
//				JLabel text = closeButton.getText();
//				int length = text.getText().getBytes().length;
//
//
//				if(!closeButton.isInit()) {
//	    			closeButton.setPreferredSize(new Dimension(width,24));
//	    			closeButton.setInit(true);
//
//	    		}
//	    	}

			return width;
		}

		// 计算tab页签的高度
		@Override
		protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
			// 可根据placement来做不同的调整
			return super.calculateTabHeight(tabPlacement, tabIndex, fontHeight);
		}
		// 绘制tab页签的边框
//	    @Override
//	    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
//	    	Graphics2D g2d = (Graphics2D) g;
//
//	    	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//	        switch (tabPlacement) {
//	            case LEFT:
////	                g.drawLine(x, y, x, y + h - 1);
////	                g.drawLine(x, y, x + w - 1, y);
////	                g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
//
//	        		
//	                break;
//	            case RIGHT:
////	                g.drawLine(x, y, x + w - 1, y);
////	                g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
////	                g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);
//	                break;
//	            case BOTTOM:
////	                g.drawLine(x, y, x, y + h - 1);
////	                g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);
////	                g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
//	                break;
//	            case TOP:
//	            default:
//
//	            	if(isSelected) {
//	            		g2d.setColor(Color.WHITE); 
//	            	}else {
//	            		g2d.setColor(new Color(206,222,238)); 
//	            	}
//	            	
////	            	 g2d.fillRect(x, y, x + w , y + h );
//	            	 g2d.setColor(new Color(223,225,232)); 
//	            	 // TODO四个圆角都有 不满足
//	            	 Shape shape = new RoundRectangle2D.Double(x, y, x + w - 2, y + h - 1, 15D, 15D);  
//	                 g2d.draw(shape);
////	            	 g2d.setColor( Color.RED); 
//////	            	 g2d.drawLine(x, y+5, x, y + h - 1);
//////	            	 g2d.drawLine(x+5, y, x + w - 1, y);
//////	            	 g2d.drawLine(x + w - 1, y, x + w - 1, y + h - 1);
////	            	 Rectangle bounds = new Rectangle(x, y, w, h);
////	            	 g2d.fillArc(x, y, x + w - 2, y + h - 1, 15, 15);
//	
//	        }
//	    }
	}
	public static class ButtonClose extends JPanel {
		@Getter
		@Setter
		boolean init = false;
		@Getter
		private JLabel ic;
		@Getter
		private JLabel text;

		/**
		 *
		 */
		private static final long serialVersionUID = -2901971173354068527L;

		public ButtonClose(final String title, Icon icon, boolean isCloseIcon, MouseListener e) {
			this.setLayout(new BorderLayout());
			Box hbox = Box.createHorizontalBox();
			ic = new JLabel(icon);
			ic.setSize(20, 20);
			text = new JLabel();
			this.setText(title);
			text.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
			this.setMaximumSize(new Dimension(tabWidth,24));
//			this.setPreferredSize(new Dimension(tabWidth+5,24));
			JLabel close = new JLabel(PublicConstant.Image.getImageIcon(PublicConstant.Image.close));
			close.addMouseListener(e);

			hbox.add(Box.createHorizontalStrut(5));
			hbox.add(ic);
			hbox.add(Box.createHorizontalStrut(10));
			hbox.add(text);
			hbox.add(Box.createHorizontalGlue());
			if(isCloseIcon) {
				hbox.add(close);
				hbox.add(Box.createHorizontalStrut(5));
			}
			this.add(hbox,BorderLayout.CENTER);
			this.setBackground(Color.green);
			this.setOpaque(false);
		}

		public void setText(String title){
//			text.setToolTipText(title); //导致不可点击切换
			int max = 10;
			if(title != null && title.length() > max){
				title = title.substring(0,max)+"...";
			}
			text.setText(title);

		}

		public void setIcon(final Icon icon){
			ic.setIcon(icon);
		}


	}

	private static final long serialVersionUID = 6242507161129020217L;

	public RTabbedPane() {
		super();
		this.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		this.setUI(new RBasicTabbedPanelUI(this));
//		this.setUI(new MetalTabbedPaneUI());
		this.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if(mouseWheelMoved){
					JTabbedPane pane = (JTabbedPane) e.getSource();
					int units = e.getWheelRotation();
					int oldIndex = pane.getSelectedIndex();
					int newIndex = oldIndex + units;
					if (newIndex < 0) {
						pane.setSelectedIndex(0);
					}else if (newIndex >= pane.getTabCount()) {
						pane.setSelectedIndex(pane.getTabCount() - 1);
					}else {
						pane.setSelectedIndex(newIndex);
					}
				}
			}
		});
	}


	public RTabbedPane(int tabPlacement, int tabLayoutPolicy) {
		super(tabPlacement, tabLayoutPolicy);
	}

	public RTabbedPane(int tabPlacement) {
		super(tabPlacement);
	}


	@Override
	public void addTab(String title, Icon icon, Component component) {
		this.addTab(title, icon, component,true);
	}
	public void addTab(String title, Icon icon, Component component, boolean isCloseIcon) {
		super.addTab(title, icon, component);
		int index = this.indexOfTab(title);
		addCloseTag(title, icon,index,isCloseIcon,component);
	}

	@Override
	public void addTab(String title, Component component) {
		super.addTab(title, component);
		int index = this.indexOfTab(title);
		addCloseTag(title,null,index,true,component);
	}

	public Component add(String title, int index, Icon icon ,Component component) {
		Component c = super.add(component,index);
		addCloseTag(title, icon,index,true,component);
		return c;

	}

	public Component add(String title, int index,Component component) {
		return add(title,index,null,component);
	}
	public void addCloseTag( String title, Icon icon,int index,boolean isCloseIcon,Component component) {

		MouseListener close = new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// your code to remove component
				// I use this way , because I use other methods of control than normal:
				// tab.remove(int index);
				int selectedIndex = RTabbedPane.this.getSelectedIndex();
				if(component == RTabbedPane.this.getComponentAt(selectedIndex)){
					RTabbedPane.this.remove(RTabbedPane.this.getSelectedIndex());
				}else{
					int tabCount = RTabbedPane.this.getTabCount();
					boolean isRemove = false;
					for (int i = selectedIndex+1; i < tabCount; i++) {
						Component componentAt = RTabbedPane.this.getComponentAt(i);
						if(component == componentAt){
							RTabbedPane.this.remove(i);
							isRemove = true;
							break;
						}
					}
					if(!isRemove){
						for (int i = selectedIndex-1; i >=0; i++) {
							Component componentAt = RTabbedPane.this.getComponentAt(i);
							if(component == componentAt){
								RTabbedPane.this.remove(i);
								break;
							}
						}
					}
				}
				System.gc();
				SwingTools.swingWorkerExec(()->{ removeList.forEach(o->{try{o.accept(component);}catch (Exception ex){}});});


			}

		};
		final ButtonClose buttonClose = new ButtonClose(title, icon, isCloseIcon,close);
		this.setTabComponentAt(index, buttonClose);
		this.validate();
		this.setSelectedIndex(index);
	}
}
