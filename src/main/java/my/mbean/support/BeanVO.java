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


    public String getBeanName() {
        return beanName;
    }

    public BeanVO setBeanName(String beanName) {
        this.beanName = beanName;
        return this;
    }

    public String getBeanClass() {
        return beanClass;
    }

    public BeanVO setBeanClass(String beanClass) {
        this.beanClass = beanClass;
        return this;
    }

    public String getBeanValue() {
        return beanValue;
    }

    public BeanVO setBeanValue(String beanValue) {
        this.beanValue = beanValue;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public BeanVO setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getContextId() {
        return contextId;
    }

    public BeanVO setContextId(String contextId) {
        this.contextId = contextId;
        return this;
    }

    public Object getProperties() {
        return properties;
    }

    public BeanVO setProperties(Object properties) {
        this.properties = properties;
        return this;
    }

    public Object getMethods() {
        return methods;
    }

    public BeanVO setMethods(Object methods) {
        this.methods = methods;
        return this;
    }

    public Object getExtra() {
        return extra;
    }

    public BeanVO setExtra(Object extra) {
        this.extra = extra;
        return this;
    }
}
