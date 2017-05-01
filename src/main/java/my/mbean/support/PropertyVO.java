package my.mbean.support;

import java.util.Map;

public class PropertyVO {
    /**
     * property name.
     */
    private String              name;
    /**
     * propert tupe.
     */
    private String              type;
    /**
     * is or not writable.
     */
    private boolean             writable;
    /**
     * property value.
     */
    private Object              value;
    /**
     * this bean is belong to which ApplicationContext.
     */
    private String              contextId;

    /**
     * bean name.
     */
    private String              beanName;
    /**
     * full class name.
     */
    private String              beanClass;
    /**
     * bean url.
     */
    private String              beanUrl;
    /**
     * change value url.
     */
    private String              changeUrl;
    /**
     * property source.
     */
    private PropertySource      source;
    /**
     * 可扩展的额外属性.
     */
    private Map<String, Object> extra;



    /**
     * @return the name
     */
    public String getName() {
        return name;
    }



    /**
     * @return the type
     */
    public String getType() {
        return type;
    }



    /**
     * @return the writable
     */
    public boolean isWritable() {
        return writable;
    }



    /**
     * @return the value
     */
    public Object getValue() {
        return value;
    }



    /**
     * @return the contextId
     */
    public String getContextId() {
        return contextId;
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
     * @return the beanUrl
     */
    public String getBeanUrl() {
        return beanUrl;
    }



    /**
     * @return the changeUrl
     */
    public String getChangeUrl() {
        return changeUrl;
    }



    /**
     * @return the extra
     */
    public Map<String, Object> getExtra() {
        return extra;
    }



    /**
     * @param pName
     *            the name to set
     */
    public void setName(String pName) {
        name = pName;
    }



    /**
     * @param pType
     *            the type to set
     */
    public void setType(String pType) {
        type = pType;
    }



    /**
     * @param pWritable
     *            the writable to set
     */
    public void setWritable(boolean pWritable) {
        writable = pWritable;
    }



    /**
     * @param pValue
     *            the value to set
     */
    public void setValue(Object pValue) {
        value = pValue;
    }



    /**
     * @param pContextId
     *            the contextId to set
     */
    public void setContextId(String pContextId) {
        contextId = pContextId;
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
     * @param pBeanUrl
     *            the beanUrl to set
     */
    public void setBeanUrl(String pBeanUrl) {
        beanUrl = pBeanUrl;
    }



    /**
     * @param pChangeUrl
     *            the changeUrl to set
     */
    public void setChangeUrl(String pChangeUrl) {
        changeUrl = pChangeUrl;
    }



    /**
     * @param pExtra
     *            the extra to set
     */
    public void setExtra(Map<String, Object> pExtra) {
        extra = pExtra;
    }



    /**
     * @return the source
     */
    public PropertySource getSource() {
        return source;
    }



    /**
     * @param pSource
     *            the source to set
     */
    public void setSource(PropertySource pSource) {
        source = pSource;
    }

}
