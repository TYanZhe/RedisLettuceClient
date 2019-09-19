package cn.org.tpeach.nosql.view.component;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.text.Document;

import cn.org.tpeach.nosql.constant.PublicConstant;
import cn.org.tpeach.nosql.tools.StringUtils;


/**
 * 
　 * <p>Title: PlaceholderTextField.java</p> 
　 * @author taoyz 
　 * @date 2019年8月28日 
　 * @version 1.0
 */

public class PlaceholderTextField extends JTextField {
    /**
	 * 
	 */
	private static final long serialVersionUID = 2073112779882943540L;
	//提示信息
    private String placeholder;

    public PlaceholderTextField(final Document pDoc, final String pText, final int pColumns) {
        super(pDoc, pText, pColumns);
        init();
    }

    public PlaceholderTextField(final int pColumns) {
        super(pColumns);
        init();
    }

    public PlaceholderTextField(final String pText) {
        super(pText);
        init();
    }

    public PlaceholderTextField(final String pText, final int pColumns) {
        super(pText, pColumns);
        init();
    }
    
    public void init() {
    	this.setMinimumSize(new Dimension(0,25));
    	this.setPreferredSize(new Dimension(0,25));
        this.setSelectionColor(new Color(0,120,215));
        this.setSelectedTextColor(Color.WHITE);
        this.setBorder(PublicConstant.RColor.defalutInputColor);
		this.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
			    setBorder(PublicConstant.RColor.defalutInputColor);
			}
			
			@Override
			public void focusGained(FocusEvent e) {
                setBorder(PublicConstant.RColor.selectInputColor);
			}
		}); 

    }
    
    public void resetHeightSize(int height) {
    	if(height < 25) {
    		return ;
    	}
    	this.setMinimumSize(new Dimension(0,height));
    	this.setPreferredSize(new Dimension(0,height));
    }

    private void setBorder(Color color){
        this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(PublicConstant.RColor.defalutInputColor),BorderFactory.createEmptyBorder(0,5,0,5)));

    }
    @Override
    public Color getDisabledTextColor() {
        return Color.GRAY;
    }

    @Override
    protected void paintComponent(final Graphics pG) {
        super.paintComponent(pG);
        if (StringUtils.isBlank(placeholder) || StringUtils.isNotBlank(getText())) {
            return;
        }
        //绘制提示语
        final Graphics2D graphics = (Graphics2D) pG;
//        Font font = new Font("宋体",Font.PLAIN,14);
        Font font = this.getFont();
        graphics.setFont(font);
        //消除文字锯齿
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        //消除画图锯齿
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(getDisabledTextColor());
     // 获取到文字区域大小
        Rectangle2D rect = font.getStringBounds(placeholder, ((Graphics2D)graphics).getFontRenderContext());
        //向画板上写字
//        graphics.drawString(placeholder, getInsets().left, pG.getFontMetrics().getMaxAscent() + getInsets().top);
        int height = this.getHeight();
        int rectHeight = (int) rect.getHeight();
        int y =  getInsets().top + rectHeight  - (height - rectHeight)/2;

        graphics.drawString(placeholder, getInsets().left+2, y);
    }

    public void setPlaceholder(final String placeholder) {
        this.placeholder = placeholder;
    }

    public String getPlaceholder() {
        return placeholder;
    }

/*    private int state = 0; //1->修改 0->提示
    private String showText;
    public String getText(){
        if(this.state==1){
            return super.getText();
        }
        return "";
    }
    @Override
    public void focusGained(FocusEvent e) {
        if(state==0){
            state = 1;
            this.setText("");
            setForeground(Color.BLACK);
        }
    }
    @Override
    public void focusLost(FocusEvent e) {
        String temp = getText();
        if (temp.equals("")) {
            setForeground(Color.GRAY);
            this.setText(showText);
            state = 0;
        } else {
            this.setText(temp);
        }
    }*/
}