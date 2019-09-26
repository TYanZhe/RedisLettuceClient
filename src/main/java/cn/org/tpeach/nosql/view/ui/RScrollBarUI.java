package cn.org.tpeach.nosql.view.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.plaf.basic.BasicArrowButton;

import cn.org.tpeach.nosql.constant.PublicConstant;
import cn.org.tpeach.nosql.view.component.EasyJSP;
import cn.org.tpeach.nosql.view.component.OnlyReadArea;

class LogFrame extends  JFrame{

    OnlyReadArea textArea = new OnlyReadArea(50,100,1000);


    public LogFrame() throws HeadlessException {
        JScrollPane scrollPane = new EasyJSP(textArea);
        
//        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(scrollPane);
        this.pack();
        this.setSize(this.getWidth(), this.getHeight()-50);
        this.setVisible(true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }


    public  void appendText(String text){
        textArea.println(text);
    }
}
// BasicScrollBarUI
public class RScrollBarUI extends javax.swing.plaf.metal.MetalScrollBarUI {
	private boolean isVerticalScrollBar = true;
    public static LogFrame logFrame ;
    public static void main(String[] args) {
    	logFrame = new LogFrame();
	}
    
    
    public RScrollBarUI() {
		super();
		// TODO Auto-generated constructor stub
	}


	public RScrollBarUI(boolean isVerticalScrollBar) {
		super();
		this.isVerticalScrollBar = isVerticalScrollBar;
	}


	/**
     * 绘制滑动块
     */
    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        int width = thumbBounds.width;
        int height = thumbBounds.height;
        Graphics2D g2 = (Graphics2D)g;
        // 消除锯齿
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.translate(thumbBounds.x, thumbBounds.y);
        g2.setColor(new Color(156,190,200));
        g2.drawRoundRect(1,1,width-2, height-2,3,3);
        g2.setColor(new Color(205,205,205));
        g2.fillRoundRect(1,1,width-2,height-2,3,3);
//        if(height > 25){
//            g2.setColor(new Color(0,143,207));
//            g2.drawLine(3,height/2,width-4,height/2);
//            g2.drawLine(3,height/2+3,width-4,height/2+3);
//        }
        // 半透明
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f));
     // 设置填充颜色，这里设置了渐变，由下往上
      g2.setPaint(new GradientPaint(c.getWidth() / 2, 1, Color.GRAY,c.getWidth() / 2, c.getHeight(), Color.GRAY));
        g2.translate(-thumbBounds.x, -thumbBounds.y);
    }
    /**
     * 重绘滑块的滑动区域背景
     */
    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        g.setColor(new Color(240,240,240));
//        g.setColor(Color.RED);
        int x = trackBounds.x;
        int y = trackBounds.y;
        int width = trackBounds.width;
        int height = trackBounds.height;
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));

        g2.fill3DRect(x, y, width, height, true);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g2.setColor(new Color(141,166,186));
        g2.fill3DRect(x, y, 1, height+1, true);
        if(trackHighlight == DECREASE_HIGHLIGHT) {
            paintDecreaseHighlight(g);
        } else if(trackHighlight == INCREASE_HIGHLIGHT)  {
            paintIncreaseHighlight(g);
        }
    }
    @Override
    protected JButton createIncreaseButton(int orientation) {
    	JButton button;
//    	button = super.createIncreaseButton(orientation);
        button = new BasicArrowButton(orientation) {
            @Override
            public void paint(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(122,138,153));
                g2.drawLine(0,0,0,getHeight());
                g2.drawLine(0,0,getWidth(),0-1);
                if(isVerticalScrollBar) {
                	g2.drawImage(PublicConstant.Image.arrow_up_blue.getImage(),4,2,null);
                }else {
                	g2.drawImage(PublicConstant.Image.arrow_right_blue.getImage(),4,4,null);
                }
                
            }
        };
        button.setOpaque(false);
        button.setBorder(BorderFactory.createEmptyBorder());
//        button.setPreferredSize(new Dimension(button.getPreferredSize().width,button.getPreferredSize().width));

        return button;
    }
    @Override
    protected JButton createDecreaseButton(int orientation) {

    	JButton button;
//    	button = super.createIncreaseButton(orientation);
        button = new BasicArrowButton(orientation){
            @Override
            public void paint(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(122,138,153));
          
                if(isVerticalScrollBar) {
                    g2.drawLine(0,0,0,getHeight());
                    g2.drawLine(0,getHeight()-1,getWidth(),getHeight());
                	g2.drawImage(PublicConstant.Image.arrow_down_blue.getImage(),4,3,null);
                }else {
                    g2.drawLine(0,getHeight(),getWidth(),getHeight());
                    g2.drawLine(getWidth(),0,getWidth(),getHeight()-1);
                	g2.drawImage(PublicConstant.Image.arrow_left_blue.getImage(),3,4,null);
                }
            }
        };
        button.setOpaque(false);
        button.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
        return button;
    }

}