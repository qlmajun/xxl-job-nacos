package com.warrior.xxl.job.executor.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author majun
 * @description 自定义应用上下文操作
 * @date 2020/5/14
 */
@Component
public class ApplicationContextHolder implements ApplicationContextAware {
	
	private static ApplicationContext applicationContext;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ApplicationContextHolder.applicationContext = applicationContext;
	}
	
	
	public static HttpServletRequest getHttpServletRequest(){
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	}
	
	public static ApplicationContext getApplicationContext(){
		return applicationContext;
	}

	public static Object getBean(String name) {
		if (getApplicationContext() == null)
			return null;
		return getApplicationContext().getBean(name);
	}

	public static boolean containsBean(String name) {
		if (getApplicationContext() == null)
			return false;
		return getApplicationContext().containsBean(name);
	}

	/**
	 * 具有唯一性
	 * 
	 * @param clazz
	 * @return
	 */
	public static <T> T getBean(Class<T> clazz) {
		if (getApplicationContext() == null)
			return null;
		return getApplicationContext().getBean(clazz);
	}
	
	public static <T> T getProperties(String key,Class<T> clazz){
		if (getApplicationContext() == null)
			return null;
		return getApplicationContext().getEnvironment().getProperty(key, clazz);
	}
	
	public static <T> T getProperties(String key,Class<T> clazz,T defaultValue){
		if (getApplicationContext() == null)
			return null;
		return getApplicationContext().getEnvironment().getProperty(key, clazz, defaultValue);
	}
}
