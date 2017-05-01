package my.mbean.support;

import my.mbean.util.Utils;

/**
 * bean view page Object.
 * @author hubert
 */
public class BeanVO {
    /**
     * bean name.
     */
    private String beanName;
    /**
     * full class name.
     */
    private String beanClass;
    /**
     * bean value.common is toString. show memory address.
     */
    private String beanValue;
    /**
     * bean url.
     */
    private String url;
    /**
     * this bean is belong to which ApplicationContext.
     */
    private String contextId;
    private Object properties;
    private Object methods;
    /**
     * 可扩展的额外属性.
     */
    private Object extra;



    @Override
    public String toString() {
        return Utils.toString(this);
    }



    /**
     * @return the beanName
     */
    public String getBeanName() {
        return beanName;
    }



    /**
     * @return the beanClass
     */
    public String getBeanClass() {
        return beanClass;
    }



    /**
     * @return the beanValue
     */
    public String getBeanValue() {
        return beanValue;
    }



    /**
     * @param pBeanName
     *            the beanName to set
     */
    public void setBeanName(String pBeanName) {
        beanName = pBeanName;
    }



    /**
     * @param pBeanClass
     *            the beanClass to set
     */
    public void setBeanClass(String pBeanClass) {
        beanClass = pBeanClass;
    }



    /**
     * @param pBeanValue
     *            the beanValue to set
     */
    public void setBeanValue(String pBeanValue) {
        beanValue = pBeanValue;
    }



    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }



    /**
     * @param pUrl
     *            the url to set
     */
    public void setUrl(String pUrl) {
        url = pUrl;
    }



    /**
     * @return the contextId
     */
    public String getContextId() {
        return contextId;
    }



    /**
     * @param pContextId
     *            the contextId to set
     */
    public void setContextId(String pContextId) {
        contextId = pContextId;
    }



    /**
     * @return the properties
     */
    public Object getProperties() {
        return properties;
    }



    /**
     * @return the methods
     */
    public Object getMethods() {
        return methods;
    }



    /**
     * @param pProperties
     *            the properties to set
     */
    public void setProperties(Object pProperties) {
        properties = pProperties;
    }



    /**
     * @param pMethods
     *            the methods to set
     */
    public void setMethods(Object pMethods) {
        methods = pMethods;
    }



    /**
     * @return the extra
     */
    public Object getExtra() {
        return extra;
    }



    /**
     * @param pExtra
     *            the extra to set
     */
    public void setExtra(Object pExtra) {
        extra = pExtra;
    }

}
