package cn.org.tpeach.nosql.view.component;

import cn.org.tpeach.nosql.tools.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * 版权声明：本文为CSDN博主「梦科」的原创文章，遵循CC 4.0 by-sa版权协议，转载请附上原文出处链接及本声明。
 * 原文链接：https://blog.csdn.net/wqjsir/article/details/7346924
 * <p>
 * Title: NonRectanglePopupFactory.java
 * </p>
 */
public class NonRectanglePopupFactory extends PopupFactory {
	private static final int BORDER_PAD = 20;
	private Color backColor = new Color(254, 254, 224);
	private Color borderColor = new Color(137,137,127);
	//是否横向滚动，缺省不
	private boolean horizontalExtending;
	//是否垂直滚动，缺省不
	private boolean verticalExtending;
	public NonRectanglePopupFactory() {
	}
	/**
	 * @param h 是否横向滚动
	 * @param v 是否垂直滚动
	 */
	public NonRectanglePopupFactory(boolean h, boolean v) {
		horizontalExtending=h;
		verticalExtending=v;
	}
	@Override
	public Popup getPopup(Component owner, Component contents, int x, int y) throws IllegalArgumentException {
		if (contents instanceof JToolTip) {
			JToolTip toolTip = (JToolTip) contents;
			String tipText = toolTip.getTipText();
			if(StringUtils.isBlank(tipText)) {
				return super.getPopup(owner, contents, x, y);
			}
			tipText = tipText.replaceAll("\\n", " ");
			char[] chars = tipText.toCharArray();
			StringBuffer sb = new StringBuffer("<html>");
			int i = 1;
			for (char aChar : chars) {
				sb.append(aChar);
				if(i%280 ==0){
					sb.append("<br>");
				}
				i++;
			}
			sb.append("</html>");
			toolTip.setTipText(sb.toString());
			toolTip.setBorder(null);
			toolTip.setBackground(backColor);
			toolTip.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, borderColor));
			Class<? extends NonRectanglePopupFactory> clazz = this.getClass();

			Popup popup = super.getPopup(owner, toolTip, x, y);

			String name=popup.getClass().getName();

			if(name.equals("javax.swing.PopupFactory$HeavyWeightPopup")){
				//重量级的弹出窗口，其顶层容器为JWindow
				return new PopupProxy(
						popup,
						SwingUtilities.getWindowAncestor(contents));
			}else{
				//轻量级的弹出窗口
				if(contents instanceof JToolTip) {
					//如果组件是JToolTip，则其父亲容器就是顶层容器
					return new PopupProxy(
							popup,
							contents.getParent());
				}else {
					//其他弹出式窗口则组件本身就是顶层容器
					return new PopupProxy(
							popup,
							contents);
				}
			}

		} else{
			return super.getPopup(owner, contents, x, y);
		}

	}

	class PopupProxy extends Popup implements ActionListener {
		//一些常量
		private static final int ANIMATION_FRAME_INTERVAL=10;
		private static final int ANIMATION_FRAMES=10;
		//被代理的弹出式窗口，这个弹出式窗口是从缺省工厂那儿获得的。
		private Popup popupProxy;
		//当前组件
		private Component topComponent;

		//弹出式窗口最终尺寸
		private Dimension fullSize;
		//动画时钟
		private Timer timer;
		//动画的当前帧
		private int frameIndex;

		public PopupProxy(Popup popup, Component component){
			popupProxy=popup;
			topComponent=component;
		}

		/**
		 * 覆盖show方法启动动画线程
		 */
		@Override
		public void show() {
			//代理窗口显示
			popupProxy.show();
			//获取显示后窗口的最终大小。
			fullSize=topComponent.getSize();
			//设置窗口的初始尺寸
			topComponent.setSize(
					horizontalExtending?0:fullSize.width,
					verticalExtending?0:fullSize.height);
			//初始化为第一帧
			frameIndex=1;
			//启动动画时钟
			timer=new Timer(ANIMATION_FRAME_INTERVAL, this);
			timer.start();
		}
		/**
		 * 重载hide，关闭可能的时钟
		 */
		@Override
		public void hide() {
			if(timer!=null&&timer.isRunning()){
				//关闭时钟
				timer.stop();
				timer=null;
			}
			//代理弹出窗口关闭
			popupProxy.hide();
		}
		//动画时钟事件的处理，其中一帧
		@Override
		public void actionPerformed(ActionEvent e) {
			//设置当前帧弹出窗口组件的尺寸
			topComponent.setSize(
					horizontalExtending?
							fullSize.width*frameIndex/ANIMATION_FRAMES:
							fullSize.width,
					verticalExtending?
							fullSize.height*frameIndex/ANIMATION_FRAMES:
							fullSize.height);

			if(frameIndex==ANIMATION_FRAMES){
				//最后一帧，关闭时钟
				timer.stop();
				timer=null;
			}else {
				//前进一帧
				frameIndex++;
			}
		}
	}

}
