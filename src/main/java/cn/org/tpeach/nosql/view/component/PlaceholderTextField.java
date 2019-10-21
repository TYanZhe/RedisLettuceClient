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
    PlaceholderCommon placeholderCommon = PlaceholderCommon.getInstance();
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
        placeholderCommon.init(this);
    }

    public PlaceholderTextField(final int pColumns) {
        super(pColumns);
        placeholderCommon.init(this);
    }

    public PlaceholderTextField(final String pText) {
        super(pText);
        placeholderCommon.init(this);
    }

    public PlaceholderTextField(final String pText, final int pColumns) {
        super(pText, pColumns);
        placeholderCommon.init(this);
    }

    
    public void resetHeightSize(int height) {
    	placeholderCommon.resetHeightSize(this,height);
    }


    @Override
    public Color getDisabledTextColor() {
        return Color.GRAY;
    }

    @Override
    protected void paintComponent(final Graphics pG) {
        super.paintComponent(pG);
        placeholderCommon.paintComponent(this,pG,placeholder);
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