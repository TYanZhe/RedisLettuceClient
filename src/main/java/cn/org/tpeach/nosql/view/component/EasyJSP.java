package cn.org.tpeach.nosql.view.component;

import cn.org.tpeach.nosql.view.ui.RScrollBarUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.plaf.ScrollBarUI;

/**
 * @author tyz
 * @Title: EasyJSP
 * @ProjectName RedisLark
 * @Description:
 * @date 2019-06-30 21:06
 * @since 1.0.0
 */
public class EasyJSP extends JScrollPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4891564512970590367L;

	public EasyJSP() {
		super();
		 init();
	}

	public EasyJSP(Component view, int vsbPolicy, int hsbPolicy) {
		super(view, vsbPolicy, hsbPolicy);
		 init();
	}

	public EasyJSP(Component view) {
		super(view);
		 init();
		
	}

	public EasyJSP(int vsbPolicy, int hsbPolicy) {
		super(vsbPolicy, hsbPolicy);
		 init();
	}

	private void init() {
		JScrollBar verticalScrollBar = this.getVerticalScrollBar();
		JScrollBar horizontalScrollBar = this.getHorizontalScrollBar();
		verticalScrollBar.setUI(getVerticalScrollBarUI());
		verticalScrollBar.setPreferredSize(new Dimension(20,verticalScrollBar.getPreferredSize().height));
		horizontalScrollBar.setUI(getHorizontalScrollBarUI());
		horizontalScrollBar.setPreferredSize(new Dimension(horizontalScrollBar.getPreferredSize().width,20));
		this.getViewport().setBackground(Color.WHITE);
	}
	private ScrollBarUI getVerticalScrollBarUI() {
		return new RScrollBarUI();
	}
	private ScrollBarUI getHorizontalScrollBarUI() {
		return new RScrollBarUI(false);
	}
	/**
	 * 设置水平和垂直滚动条自动出现
	 * 
	 * @return
	 */
	public EasyJSP autoScrollBar() {
		return this.autoVerticalScrollBar().autoHorizontalScrollBar();
	}

	/**
	 * 设置垂直滚动条自动出现
	 * 
	 * @return
	 */
	public EasyJSP autoVerticalScrollBar() {
		this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		return this;
	}

	/**
	 * 设置水平滚动条自动出现
	 * 
	 * @return
	 */
	public EasyJSP autoHorizontalScrollBar() {
		this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		return this;
	}

	/**
	 * 设置水平和垂直滚动条总是出现
	 * 
	 * @return
	 */
	public EasyJSP alwaysScrollBar() {
		return this.alwaysHorizontalScrollBar().alwaysVerticalScrollBar();
	}

	/**
	 * 设置水平滚动条总是出现
	 * 
	 * @return
	 */
	public EasyJSP alwaysHorizontalScrollBar() {
		this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		return this;
	}

	/**
	 * 设置垂直滚动条总是出现
	 * 
	 * @return
	 */
	public EasyJSP alwaysVerticalScrollBar() {
		this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		return this;
	}

	/**
	 * 设置水平和垂直滚动条总是总是隐藏
	 * 
	 * @return
	 */
	public EasyJSP hiddenScrollBar() {
		return this.hiddenHorizontalScrollBar().hiddenVerticalScrollBar();
	}

	/**
	 * 设置水平滚动条总是总是隐藏
	 * 
	 * @return
	 */
	public EasyJSP hiddenHorizontalScrollBar() {
		this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		return this;
	}

	/**
	 * 设置垂直滚动条总是总是隐藏
	 * 
	 * @return
	 */
	public EasyJSP hiddenVerticalScrollBar() {
		this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		return this;
	}
}
