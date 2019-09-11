package cn.org.tpeach.nosql.framework;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cn.org.tpeach.nosql.constant.PublicConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.org.tpeach.nosql.annotation.Component;
import cn.org.tpeach.nosql.annotation.Component.BeanScope;
import lombok.Getter;
import lombok.Setter;

/**
 * @author tyz
 * @Title: AnnotationApplicationContext
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-06-23 20:35
 * @since 1.0.0
 */
public class BeanContext {
	final static Logger logger = LoggerFactory.getLogger(BeanContext.class);
	private static Map<String, ComponentBean> beanMap = new HashMap<>();

	private static Set<String> beanNameSet = new HashSet<>();
	private static Set<String> classNameSet = new HashSet<>();
	/**
	 * 获取Component注解的对象
	 * @param beanName
	 * @return
	 */
	public static Object getBean(String beanName) {
		ComponentBean componentBean = beanMap.get(beanName);
		Object obj = null;
		if (componentBean != null) {
			obj = componentBean.getObj();
			BeanScope scope = componentBean.getScope();
			if (scope == Component.BeanScope.SCOPE_PROTOTYPE || obj == null) {
				try {
					obj = Class.forName(componentBean.getClassName()).newInstance();
				} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			
			if (scope == Component.BeanScope.SCOPE_SINGLETON) {
				componentBean.setObj(obj);
			}else if (scope == Component.BeanScope.SCOPE_PROTOTYPE) {
				componentBean.setObj(null);
			}
			
		}
		return obj;
	}
	/**
	 * 获取Component注解的对象
	 * @param beanName
	 * @param bean
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public  static <T> T getBean(String beanName, Class<T> bean) {
		Object b = getBean(beanName);
		T t = null;
		if(b != null) {
			t = (T) b;
		}
		return t;
	}

	static void setBean(ComponentBean beanInfo) {
		// beanInfo 校验 TODO

		// 注解value重复校验
		if (beanNameSet.contains(beanInfo.getBeanName())) {
			logger.error(beanInfo.getBeanName() + "已经存在>>>>>>"+beanInfo.getClassName());
			System.exit(0);
		}
		if (classNameSet.contains(beanInfo.getClassName())) {
			logger.error(beanInfo.getClassName() + "已经存在");
			System.exit(0);
		}
		classNameSet.add(beanInfo.getClassName());
		beanNameSet.add(beanInfo.getBeanName());
		if(PublicConstant.ISDEBUG){
			logger.info("beanId:" + beanInfo.getBeanName() + " >>> " + beanInfo.getClassName());
		}
		beanInfo.setObj(null);
		if (Component.BeanScope.SCOPE_SINGLETON == beanInfo.getScope() && !beanInfo.isLazy()) {
			try {
				beanInfo.setObj(getBeanClass(beanInfo));
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				logger.info("加载失败:" + beanInfo.getBeanName() + " >>>" + beanInfo.getClassName());
				System.exit(0);
			}
		}
		beanMap.put(beanInfo.getBeanName(), beanInfo);
	}

	private static Object getBeanClass(ComponentBean beanInfo)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		return Class.forName(beanInfo.getClassName()).newInstance();

	}

}
@Getter
@Setter
class ComponentBean {

    private String beanName;
    private String className;
    private Component.BeanScope scope;
    private boolean lazy;
    private Object obj;
}