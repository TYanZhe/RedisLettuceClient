package cn.org.tpeach.nosql.service;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.swing.JOptionPane;

import cn.org.tpeach.nosql.framework.BeanContext;
import cn.org.tpeach.nosql.framework.LarkFrame;

public class ServiceProxy implements InvocationHandler {
    // 业务实现类对象，用来调用具体的业务方法
    private Object target;

    /**
     * 绑定业务对象并返回一个代理类
     */
    public Object bind(Object target) {
        this.target = target;
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), this);
    }


    /**
     *
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Object result = null;
        try {
            result = method.invoke(target, args);
        } catch (Exception e) {
//            logger.error("服务接口出错[" + proxy.getClass().getName() + "#" + method.getName() + "]:", e);
//            if(e instanceof ServiceException){
//                throw e;
//            }
//            else{
//                throw new ServiceException(getStackTrace(e.getCause()));
//            }
            throw e.getCause();

        }
        return result;

    }

    /**
     * 使用jdk代理模式beanId必须实现接口
     * @param beanId
     * @param clazz
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
	public static <T> T getBeanProxy(String beanId, Class<T> clazz) {
        Object bean = BeanContext.getBean(beanId);
        if (bean == null) {
            JOptionPane.showMessageDialog(LarkFrame.frame, beanId + "不存在,获取bean失败", "错误", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(beanId + "不存在,获取bean失败");
        }
        return (T) new ServiceProxy().bind(bean);
    }


    /**
     * 获取异常信息
     * @param t
     * @return
     */
    public static String getStackTrace(Throwable t) {
        try (StringWriter sw = new StringWriter();
             PrintWriter pw = new PrintWriter(sw, true)) {
            t.printStackTrace(pw);
            return sw.getBuffer().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
