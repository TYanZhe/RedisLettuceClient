package cn.org.tpeach.nosql.view.component;

import cn.org.tpeach.nosql.constant.PublicConstant;
import cn.org.tpeach.nosql.tools.StringUtils;
import lombok.Getter;
import lombok.Setter;
import sun.font.FontDesignMetrics;

import javax.swing.*;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;


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
    @Getter
    @Setter
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
//        //绘制提示语

        final Graphics2D g = (Graphics2D) pG;
        Font font = this.getFont();
        g.setFont(font);
        g.setColor(getDisabledTextColor());
//        //消除文字锯齿
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//        //消除画图锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(getDisabledTextColor());
        int maxAscent = FontDesignMetrics.getMetrics(font).getHeight();
//        int y = ( this.getHeight() - maxAscent)/2 + maxAscent + getInsets().top;
        int y =   maxAscent + getInsets().top;
        g.drawString(placeholder, getInsets().left+2, y);
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