/**
 * 
 */
package cn.org.tpeach.nosql.view.component;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.text.Document;

import cn.org.tpeach.nosql.constant.PublicConstant;

/**
 * <p>
 * Title: RTextArea.java
 * </p>
 * @author taoyz @date 2019年8月26日 @version 1.0
 */
public class RTextArea extends JTextArea {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3042974731575150249L;

	public RTextArea() {
		super();
		init();
	}

	public RTextArea(Document doc, String text, int rows, int columns) {
		super(doc, text, rows, columns);
		init();
	}

	public RTextArea(Document doc) {
		super(doc);
		init();
	}

	public RTextArea(int rows, int columns) {
		super(rows, columns);
		init();
	}

	public RTextArea(String text, int rows, int columns) {
		super(text, rows, columns);
		init();
	}

	public RTextArea(String text) {
		super(text);
		init();
	}

	private void init() {

	}
	
	public JScrollPane getJScrollPane() {
		EasyJSP easyJsp = new EasyJSP(this).hiddenHorizontalScrollBar();
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
		return  easyJsp;
	}
}
