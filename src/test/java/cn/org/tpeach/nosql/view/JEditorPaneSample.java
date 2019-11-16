package cn.org.tpeach.nosql.view;

import cn.org.tpeach.nosql.tools.SwingTools;
import cn.org.tpeach.nosql.view.component.RButton;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

/**
 * JEditorPane支持text/html,text/plain,text/rtf内容显示
 * 此Demo主要展示了JEditor的用法和JFrame全屏的方法
 * @author hhzxj2008
 * */
public class JEditorPaneSample {

	/**
	 * 在JEditor中显示HTML
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		JFrame jf = new JFrame("JEditorPane示例");
		Container contentPane = jf.getContentPane();
		//text/html文本类型
		final JEditorPane jep = new JEditorPane( );
		JScrollPane jsp = new JScrollPane(jep);//添加滚动支持
		jep.setContentType("text/html");

		jep.setText("<!DOCTYPE html>\n<html lang=\"en\" xmlns:th=\"http://www.w3.org/1999/xhtml\">\n" +
				"\n" +
				"<head>\n" +
				"<meta charset=\"UTF-8\" />\n"+
				"<title>我的第一个 HTML 页面</title>\n" +
				"</head>\n" +
				"\n" +
				"<body>\n" +
				"<p>body 元素的内容会显示在浏览器中。</p>\n" +
				"<p>title 元素的内容会显示在浏览器的标题栏中。</p>\n" +
				"<textarea rows=\"3\" cols=\"20\" name=\"html\" id=\"feedlist_id\" style=\"resize: none;\" class=\"resizable processed\">都烦死了健康</br>fsdfds</br></textarea>\n"+
				"</body>\n" +
				"\n" +
				"</html>");
		jep.setText("");
/*		jep.addHyperlinkListener(new HyperlinkListener() {//添加链接点击监听者

			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
*//*				if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED){
					try {
						jep.setPage(e.getURL());//设置显示的URL资源
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}*//*
				System.out.println(e);

			}
		});
		jep.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				System.out.println("propertyChange");
			}
		});
		jep.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				System.out.println("keyTyped");
			}

			@Override
			public void keyPressed(KeyEvent e) {
				System.out.println("keyPressed");
			}

			@Override
			public void keyReleased(KeyEvent e) {
				System.out.println("keyReleased");
			}
		});
		*/
		jep.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				System.out.println("focusGained");
			}

			@Override
			public void focusLost(FocusEvent e) {
				Document document = Jsoup.parse(jep.getText());
				Element element = document.getElementById("feedlist_id");
				System.out.println(element.text());
			}
		});
		jep.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent e) {
				Document document = Jsoup.parse(jep.getText());
				Element element = document.getElementById("feedlist_id");
				System.err.println(element.text());
			}
		});
		jep.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println("mouseClicked");
			}

			@Override
			public void mousePressed(MouseEvent e) {
				System.out.println("mousePressed");
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				System.out.println("mouseReleased");
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				System.out.println("mouseEntered");
			}

			@Override
			public void mouseExited(MouseEvent e) {
				System.out.println("mouseExited");
			}
		});
/*		jep.addInputMethodListener(new InputMethodListener() {
			@Override
			public void inputMethodTextChanged(InputMethodEvent event) {
				System.out.println("inputMethodTextChanged");
			}

			@Override
			public void caretPositionChanged(InputMethodEvent event) {
				System.out.println("caretPositionChanged");
			}
		});*/

		contentPane.setLayout(new BorderLayout());
		contentPane.add(jep,BorderLayout.CENTER);
		RButton jButton = new RButton("提交");
		JPanel jPanel = new JPanel();
		jPanel.add(jButton);
		SwingTools.addMouseClickedListener(jButton,e->{
			System.out.println(jep.getText());
			Document document = Jsoup.parse(jep.getText());
			Element element = document.getElementById("feedlist_id");
			System.err.println(element.text());
			element.html("都烦死了健康");
//			jep.setText(document.html());


		});
		contentPane.add(jPanel,BorderLayout.SOUTH);
//		jf.setSize(Toolkit.getDefaultToolkit().getScreenSize().getSize());//窗口全屏
		jf.pack();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
	}



}
