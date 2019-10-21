/**
 * 
 */
package cn.org.tpeach.nosql.view.component;

import cn.org.tpeach.nosql.constant.PublicConstant;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.Document;
import javax.swing.undo.UndoManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * <p>
 * Title: RTextArea.java
 * </p>
 * @author taoyz @date 2019年8月26日 @version 1.0
 */
public class RTextArea extends JTextArea {
	private EasyJSP easyJsp;

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
		UndoManager undoManager = new UndoManager();
		this.getDocument().addUndoableEditListener(undoManager);
		this.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent arg0) {
			}

			@Override
			public void keyPressed(KeyEvent evt) {
				if (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_Z) {
					if (undoManager.canUndo()) {
						undoManager.undo();
					}
				}
				if (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_Y) {
					if (undoManager.canRedo()) {
						undoManager.redo();
					}
				}
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
			}
		});
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
