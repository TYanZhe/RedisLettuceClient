package cn.org.tpeach.nosql.framework;

import java.awt.Font;
import java.awt.FontMetrics;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.swing.JFrame;

import cn.org.tpeach.nosql.tools.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.org.tpeach.nosql.annotation.Component;
import cn.org.tpeach.nosql.annotation.ComponentScan;
import cn.org.tpeach.nosql.annotation.JFrameMain;
import cn.org.tpeach.nosql.constant.ConfigConstant;
import cn.org.tpeach.nosql.constant.I18nKey;
import cn.org.tpeach.nosql.constant.PublicConstant;
import cn.org.tpeach.nosql.tools.AnnotationUtil;
import cn.org.tpeach.nosql.tools.CollectionUtils;
import cn.org.tpeach.nosql.tools.ConfigParser;

/**
 * @author tyz
 * @Title: JnFrameApplication
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-06-23 21:06
 * @since 1.0.0
 */
public class LarkFrame {
	final static Logger logger = LoggerFactory.getLogger(LarkFrame.class);
	
	public  static  ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);
	private  static String LANGUAGE = "en";
	private  static String COUNTRY = "US";
	//资源文件
	private static ResourceBundle platformResource;
	public static JFrame frame;
	public static FontMetrics fm = sun.font.FontDesignMetrics.getMetrics(new Font("Dialog", Font.PLAIN,16));
	public static  void run(Class<?> primarySource) {
		
		//注解扫描
		componentAnnotation(primarySource);
		
		ConfigParser.getInstance().parseReader();
		//加载国际化资源
		reloadPlatformResource(ConfigParser.getInstance().getString(ConfigConstant.Section.LOCAL, ConfigConstant.LANGUAGE, ConfigConstant.Local.zh), 
				ConfigParser.getInstance().getString(ConfigConstant.Section.LOCAL, ConfigConstant.COUNTRY, ConfigConstant.Local.CN));
		//启动主窗口
		final JFrameMain swingMain = (JFrameMain) AnnotationUtil.getClassAnnotation(primarySource,JFrameMain.class);
		if(swingMain != null) {
			try {
				frame = (JFrame) Thread.currentThread().getContextClassLoader().loadClass(swingMain.value()).newInstance();
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				logger.error("主窗口类不存在"+swingMain.value());
				System.exit(0);
			}
		}

	}

	private static void componentAnnotation(Class<?> primarySource) {
		// 获取需要扫描的包
		final ComponentScan annotation = (ComponentScan) AnnotationUtil.getClassAnnotation(primarySource,ComponentScan.class);
		final String[] packages = annotation.basePackages();
		if (packages != null && packages.length > 0) {
			for (String packageName : packages) {
				Set<String> classNameList = null;
				try {
					classNameList = AnnotationUtil.getClassName(packageName, true);
				} catch (IOException e1) {
					logger.error(packageName+"注解获取失败",e1);
					System.exit(0);
				}

				if (CollectionUtils.isNotEmpty(classNameList)) {
					Class<?> classes = null;
					Component componentAnnotation = null;
					ComponentBean beanInfo = null;
					for (String className : classNameList) {
						try {
							classes = Thread.currentThread().getContextClassLoader().loadClass(className);
							// 判断该注解类型是不是所需要的类型
							if (null != classes && null != (componentAnnotation = (Component) AnnotationUtil.getClassAnnotation(classes,Component.class))) {
								beanInfo = new ComponentBean();
								beanInfo.setBeanName(componentAnnotation.value());
								beanInfo.setScope(componentAnnotation.scope());
								beanInfo.setClassName(className);
								beanInfo.setLazy(componentAnnotation.lazy());
								BeanContext.setBean(beanInfo);
							}
						} catch (ClassNotFoundException e) {
							logger.error(className+" Not Found",e);
							System.exit(0);
						}
					}

				}
			}
		}
	}
	public static String getI18nText(I18nKey.RedisResource platformResourceEnum) {
		if(platformResource == null) {
			reloadPlatformResource(LANGUAGE, COUNTRY);
		}
		return platformResource.getString(platformResourceEnum.getKey());
		
	}

	public static String getI18nUpText(I18nKey.RedisResource platformResourceEnum){
		String text = getI18nText(platformResourceEnum);
		if(StringUtils.isNotBlank(text)){
			text = text.toUpperCase();
		}
		return text;
	}
	public static String getI18nLowText(I18nKey.RedisResource platformResourceEnum){
		String text = getI18nText(platformResourceEnum);
		if(StringUtils.isNotBlank(text)){
			text = text.toLowerCase();
		}
		return text;
	}
	public static String getI18nFirstUpText(I18nKey.RedisResource platformResourceEnum){
		String text = getI18nText(platformResourceEnum);
		if(StringUtils.isNotBlank(text)){
			text = text.substring(0,1).toUpperCase() + text.substring(1);
		}
		return text;
	}
	public static String getI18nFirstLowText(I18nKey.RedisResource platformResourceEnum){
		String text = getI18nText(platformResourceEnum);
		if(StringUtils.isNotBlank(text)){
			text = text.substring(0,1).toLowerCase() + text.substring(1);
		}
		return text;
	}

	//TODO 重新加载配置文件
	public static void reloadPlatformResource(String language, String country) {
		LANGUAGE = language;
		COUNTRY = country ;
		platformResource =  ResourceBundle.getBundle(PublicConstant.RESOURCE_CLASSNAME,new Locale(LANGUAGE, COUNTRY));
	}

}
