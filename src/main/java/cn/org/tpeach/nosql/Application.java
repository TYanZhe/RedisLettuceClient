package cn.org.tpeach.nosql;

import cn.org.tpeach.nosql.annotation.ComponentScan;
import cn.org.tpeach.nosql.annotation.JFrameMain;
import cn.org.tpeach.nosql.framework.LarkFrame;

import javax.swing.*;

/**
 * @author tyz
 * @Title: Application
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-06-23 20:26
 * @since 1.0.0
 */
@ComponentScan(basePackages = { "cn.org.tpeach.nosql.redis.connection", "cn.org.tpeach.nosql.redis.service" })
@JFrameMain("cn.org.tpeach.nosql.view.RedisMainWindow")
public class Application {

	public static void main(String[] args) {

			//JFrame.setDefaultLookAndFeelDecorated(true);
			
			try {

				// Nimbus风格，
//				UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
				// 当前系统风格
//				 UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				// Motif风格，外观接近windows经典，但宽宽大大，而且不是黑灰主色，而是蓝黑
				// UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
				// 跨平台的Java界面风格
				 UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
				// windows风格
//				 UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
				// windows风格
				// UIManager.setLookAndFeel("javax.swing.plaf.windows.WindowsLookAndFeel");
				// Windows Classic风格
				// UIManager.setLookAndFee("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFee);
				// Metal风格 (默认)
//				 UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
				// Mac风格 (需要在相关的操作系统上方可实现)
				// UIManager.setLookAndFeel("com.apple.mrj.swing.MacLookAndFeel");


//				UIManager.LookAndFeelInfo looks[] = UIManager.getInstalledLookAndFeels();
//
//				for (UIManager.LookAndFeelInfo info : looks) {
//					UIManager.setLookAndFeel(info.getClassName());
//
//					UIDefaults defaults = UIManager.getDefaults();
//					Enumeration newKeys = defaults.keys();
//
//					while (newKeys.hasMoreElements()) {
//						Object obj = newKeys.nextElement();
//						System.out.printf("%50s : %s\n", obj, UIManager.get(obj));
//					}
//				}


			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
					| UnsupportedLookAndFeelException e) {
				// GTK风格 (需要在相关的操作系统上方可实现)
				try {
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InstantiationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (UnsupportedLookAndFeelException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
	

			
		
		LarkFrame.run(Application.class);
	}
}
