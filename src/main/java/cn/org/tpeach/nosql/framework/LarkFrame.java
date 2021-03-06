package cn.org.tpeach.nosql.framework;

import cn.org.tpeach.nosql.annotation.Component;
import cn.org.tpeach.nosql.annotation.ComponentScan;
import cn.org.tpeach.nosql.annotation.JFrameMain;
import cn.org.tpeach.nosql.constant.ConfigConstant;
import cn.org.tpeach.nosql.constant.I18nKey;
import cn.org.tpeach.nosql.constant.PublicConstant;
import cn.org.tpeach.nosql.tools.*;
import cn.org.tpeach.nosql.view.component.OnlyReadTextPane;
import cn.org.tpeach.nosql.view.menu.JRedisPopupMenu;
import cn.org.tpeach.nosql.view.menu.MenuManager;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import sun.font.FontDesignMetrics;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * @author tyz
 * @Title: JnFrameApplication
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-06-23 21:06
 * @since 1.0.0
 */
@Slf4j
public class LarkFrame {
	public static LarkLog larkLog = new LarkLog();
	public static OnlyReadTextPane logArea = new OnlyReadTextPane();

	public  static  ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);
	private  static String LANGUAGE = "en";
	private  static String COUNTRY = "US";
	//资源文件
	private static ResourceBundle platformResource;
	public static JFrame frame;
	public static FontMetrics fm = FontDesignMetrics.getMetrics(new Font("Dialog", Font.PLAIN,16));
	public static Properties APPLICATION_VALUE;
	private static AtomicBoolean startInit = new AtomicBoolean(false);
	public static boolean builtInJre = false;
	static {
		try {
			APPLICATION_VALUE = PropertiesUtils.getProperties("application.properties");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public static String getProjectEnv(){
		return   LarkFrame.APPLICATION_VALUE.getProperty("project.environment");
	}
	public static  void run(Class<?> primarySource) {
		try{
			String jreHome = System.getProperty("java.home");
			if(isWindows() && StringUtils.isNotBlank(jreHome)){
				//判断使用的jre
				String path = LarkFrame.class.getProtectionDomain().getCodeSource().getLocation().getPath().toLowerCase().replaceAll("/","").replaceAll("\\\\","");
				jreHome = jreHome.toLowerCase().replaceAll("/","").replaceAll("\\\\","");
				jreHome = jreHome.substring(0,jreHome.length()-3);
				if(path.startsWith(jreHome)){
					System.out.println("使用内置jre");
					builtInJre = true;
				}
			}
		}catch (Exception e){}

		logArea.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() != MouseEvent.BUTTON3) {
					return;
				}
				JPopupMenu popMenu = new JRedisPopupMenu();// 菜单
				JMenuItem clearItem = MenuManager.getInstance().getJMenuItem(I18nKey.RedisResource.MENU_FLUSH, PublicConstant.Image.getImageIcon(PublicConstant.Image.refresh));
				clearItem.addActionListener(evt->logArea.clear());
                JMenuItem copyKeyItem = MenuManager.getInstance().getJMenuItem(I18nKey.RedisResource.COPY, PublicConstant.Image.getImageIcon(PublicConstant.Image.copy));
                copyKeyItem.setMnemonic('C');
                copyKeyItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
                copyKeyItem.addActionListener(evt-> {
                    Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
                    String selectedText = logArea.getSelectedText();
                    Transferable tText;
                    if(StringUtils.isNotBlank(selectedText)){
                        tText = new StringSelection(selectedText);
                    }else{
                        tText = new StringSelection(logArea.getText());
                    }
                    clip.setContents(tText, null);
                });

				JMenuItem selectAllItem = MenuManager.getInstance().getJMenuItem("全选" );
				selectAllItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
				selectAllItem.addActionListener(evt->{logArea.requestFocus();logArea.selectAll();});
				popMenu.add(clearItem);
                popMenu.add(copyKeyItem);
				popMenu.add(selectAllItem);
				popMenu.show(logArea, e.getX(), e.getY());
			}
		});
		//日志文件解析
		loadindLarkLog();

		//注解扫描
		componentAnnotation(primarySource);
		
		ConfigParser.getInstance().parseReader();
		//加载国际化资源
		reloadPlatformResource(ConfigParser.getInstance().getString(ConfigConstant.Section.LOCAL, ConfigConstant.LANGUAGE, ConfigConstant.Local.zh), 
				ConfigParser.getInstance().getString(ConfigConstant.Section.LOCAL, ConfigConstant.COUNTRY, ConfigConstant.Local.CN));
		final JFrameMain swingMain = (JFrameMain) AnnotationUtil.getClassAnnotation(primarySource,JFrameMain.class);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if(swingMain != null) {
					try {
						frame = (JFrame) Thread.currentThread().getContextClassLoader().loadClass(swingMain.value()).newInstance();
					} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
						log.error("主窗口类不存在"+swingMain.value());
						System.exit(0);
					}
				}
			}
		});
	}




	public static boolean isWindows() {
		return System.getProperties().getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1;
	}
	/**
	 *
	 */
	private static void loadindLarkLog() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true); // never forget this!
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
			InputStream resourceAsStream = LarkFrame.class.getClassLoader().getResourceAsStream("larkLog.xml");
			if(resourceAsStream != null){
				Document doc = builder.parse(resourceAsStream);
				XPathFactory pathFactory = XPathFactory.newInstance();
				XPath xpath = pathFactory.newXPath();
				XPathExpression pathExpression = xpath.compile("//appender");
				Object result = pathExpression.evaluate(doc, XPathConstants.NODESET);
				NodeList nodes = (NodeList) result;
				for (int i = 0; i < nodes.getLength(); i++) {
					Node item = nodes.item(i);
					NamedNodeMap attributes = item.getAttributes();
					Node node = attributes.getNamedItem("name");
					Class<?> clazz = null;
					try {
						clazz = Class.forName(node.getNodeValue());
						larkLog.addObserver((Observer) clazz.newInstance());
					} catch (ClassNotFoundException | IllegalAccessException|InstantiationException|ClassCastException e) {
						log.error("LarkLog addObserver "+node.getNodeValue()+" Fail",e);
						System.exit(0);
					}

				}
			}else {
				throw new FileNotFoundException("larkLog.xml文件不存在");
			}

		} catch (ParserConfigurationException|SAXException|IOException|XPathExpressionException e) {
			log.error("larkLog.xml parse fail");
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
					log.error(packageName+"注解获取失败",e1);
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
							log.error(className+" Not Found",e);
							System.exit(0);
						}
					}

				}
			}
		}
	}
	public static String getI18nText(I18nKey.RedisResource platformResourceEnum) {
		return getI18nText(null,null,platformResourceEnum);
		
	}

	public static String getI18nText(Function<String,String> firstWordGet,Function<String,String> otherWordGet,I18nKey.RedisResource... platformResourceEnum) {
		if(platformResource == null) {
			reloadPlatformResource(LANGUAGE, COUNTRY);
		}
		String text ="";
		boolean flag = false;
		for (I18nKey.RedisResource redisResource : platformResourceEnum) {
			String s = platformResource.getString(redisResource.getKey());
			if(StringUtils.isNotBlank(s)){
				if(!COUNTRY.equals(ConfigConstant.Local.CN)){
					if(flag){
						if(otherWordGet == null){
							text+=" "+s;
						}else{
							text+=" "+otherWordGet.apply(s);
						}

					}else{
						flag = true;
						if(otherWordGet == null){
							text+= s;
						}else{
							text += firstWordGet.apply(s);
						}
					}
				}else{
					text+=s;
				}
			}
		}
		return text;

	}

	public static String getI18nUpText(I18nKey.RedisResource... platformResourceEnum){

		return getI18nText(s->s.toUpperCase(),s->s.toUpperCase(),platformResourceEnum);
	}
	public static String getI18nLowText(I18nKey.RedisResource platformResourceEnum){
		return getI18nText(s->s.toLowerCase(),s->s.toLowerCase(),platformResourceEnum);
	}


	public static String getI18nFirstUpText(I18nKey.RedisResource... platformResourceEnum){
		return getI18nText(s->s.substring(0,1).toUpperCase() + s.substring(1),s->s.substring(0,1).toLowerCase() + s.substring(1),platformResourceEnum);
	}
	public static String getI18nFirstUpAllText(I18nKey.RedisResource... platformResourceEnum){
		return getI18nText(s->s.substring(0,1).toUpperCase() + s.substring(1),s->s.substring(0,1).toUpperCase() + s.substring(1),platformResourceEnum);
	}



	//TODO 重新加载配置文件
	public static void reloadPlatformResource(String language, String country) {
		LANGUAGE = language;
		COUNTRY = country ;
		platformResource =  ResourceBundle.getBundle(PublicConstant.RESOURCE_CLASSNAME,new Locale(LANGUAGE, COUNTRY));
	}

	public static void hiddenStartLoading(){
		// nothing to do
		if(startInit.get()){
			return;
		}
		startInit.set(true);

	}

}
