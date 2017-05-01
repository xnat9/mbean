package my.mbean.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * base bean description.
 * @author hubert
 */
public class BaseBean implements ApplicationContextAware, BeanNameAware, BeanClassLoaderAware {
    private ApplicationContext _applicationContext;
    private String             _beanName;
    private ClassLoader        _classLoader;



    @Override
    public void setBeanName(String pBeanName) {
        this._beanName = pBeanName;
    }



    @Override
    public void setApplicationContext(ApplicationContext pApplicationContext) throws BeansException {
        this._applicationContext = pApplicationContext;
    }



    @Override
    public void setBeanClassLoader(ClassLoader pClassLoader) {
        this._classLoader = pClassLoader;

    }



    /**
     * get current bean's class loader.
     * @return
     */
    public ClassLoader getClassLoader() {
        return _classLoader;
    }



    /**
     * @return the applicationContext
     */
    public ApplicationContext getApplicationContext() {
        return _applicationContext;
    }



    /**
     * @return the beanName
     */
    public String getBeanName() {
        return _beanName;
    }

}