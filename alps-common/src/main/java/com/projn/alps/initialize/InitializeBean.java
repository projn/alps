package com.projn.alps.initialize;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.AbstractRefreshableWebApplicationContext;

/**
 * initialize bean
 *
 * @author : sunyuecheng
 */
@Component
public class InitializeBean implements ApplicationContextAware {

    private static volatile ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        InitializeBean.applicationContext = applicationContext;
    }

    /**
     * get application context
     *
     * @return ApplicationContext :
     */
    @SuppressWarnings("unchecked")
    public static ApplicationContext getApplicationContext() {
        checkApplicationContext();
        return applicationContext;
    }

    /**
     * get bean
     *
     * @param name :
     * @param <T>  :
     * @return T :
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        checkApplicationContext();
        return (T) applicationContext.getBean(name);
    }

    /**
     * get bean
     *
     * @param clazz :
     * @param <T>   :
     * @return T :
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> clazz) {
        checkApplicationContext();
        return (T) applicationContext.getBeansOfType(clazz);
    }

    private static void checkApplicationContext() {
        if (applicationContext == null) {
            throw new IllegalStateException("Load applicaitonContext error.");
        }
    }

    /**
     * refresh bean
     */
    @SuppressWarnings("unchecked")
    public static void refreshBean() {
        ((AbstractRefreshableWebApplicationContext) applicationContext).refresh();
    }
}
