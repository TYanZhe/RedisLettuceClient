/**
 * 
 */
package cn.org.tpeach.nosql.view.component;

import cn.org.tpeach.nosql.constant.PublicConstant;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * <p>
 * Title: RTextArea.java
 * </p>
 * @author taoyz @date 2019年8月26日 @version 1.0
 */
public class RTextArea extends JTextArea {
	private EasyJSP easyJsp;
	PlaceholderCommon placeholderCommon = PlaceholderCommon.getInstance();
	//提示信息
	@Getter
	@Setter
	private String placeholder;
	/**
	 * 
	 */
	private static final long serialVersionUID = -3042974731575150249L;

	public RTextArea() {
		super();
		placeholderCommon.init(this);
	}

	public RTextArea(Document doc, String text, int rows, int columns) {
		super(doc, text, rows, columns);
		placeholderCommon.init(this);
	}

	public RTextArea(Document doc) {
		super(doc);
		placeholderCommon.init(this);
	}

	public RTextArea(int rows, int columns) {
		super(rows, columns);
		placeholderCommon.init(this);
	}

	public RTextArea(String text, int rows, int columns) {
		super(text, rows, columns);
		placeholderCommon.init(this);
	}

	public RTextArea(String text) {
		super(text);
		placeholderCommon.init(this);
	}

	@Override
	protected void paintComponent(final Graphics pG) {
		super.paintComponent(pG);
		placeholderCommon.paintComponent(this,pG,placeholder);
	}


	public synchronized JScrollPane getJScrollPane() {
		if(easyJsp == null){
			easyJsp = new EasyJSP(this).hiddenHorizontalScrollBar();
			Border border = BorderFactory.createLineBorder(PublicConstant.RColor.defalutInputColor);
			easyJsp.setBorder(border);
			this.addFocusListener(new FocusListener() {

				@Override
				public void focusLost(FocusEvent e) {
					easyJsp.setBorder(BorderFactory.createLineBorder(PublicConstant.RColor.defalutInputColor));
				}

				@Override
				public void focusGained(FocusEvent e) {
					easyJsp.setBorder(BorderFactory.createLineBorder(PublicConstant.RColor.selectInputColor));
				}
			});
		}
		return  easyJsp;
	}
}
