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


    public String getName() {
        return name;
    }

    public PropertyVO setName(String name) {
        this.name = name;
        return this;
    }

    public String getType() {
        return type;
    }

    public PropertyVO setType(String type) {
        this.type = type;
        return this;
    }

    public boolean isWritable() {
        return writable;
    }

    public PropertyVO setWritable(boolean writable) {
        this.writable = writable;
        return this;
    }

    public Object getValue() {
        return value;
    }

    public PropertyVO setValue(Object value) {
        this.value = value;
        return this;
    }

    public String getContextId() {
        return contextId;
    }

    public PropertyVO setContextId(String contextId) {
        this.contextId = contextId;
        return this;
    }

    public String getBeanName() {
        return beanName;
    }

    public PropertyVO setBeanName(String beanName) {
        this.beanName = beanName;
        return this;
    }

    public String getBeanClass() {
        return beanClass;
    }

    public PropertyVO setBeanClass(String beanClass) {
        this.beanClass = beanClass;
        return this;
    }

    public String getBeanUrl() {
        return beanUrl;
    }

    public PropertyVO setBeanUrl(String beanUrl) {
        this.beanUrl = beanUrl;
        return this;
    }

    public String getChangeUrl() {
        return changeUrl;
    }

    public PropertyVO setChangeUrl(String changeUrl) {
        this.changeUrl = changeUrl;
        return this;
    }

    public PropertySource getSource() {
        return source;
    }

    public PropertyVO setSource(PropertySource source) {
        this.source = source;
        return this;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public PropertyVO setExtra(Map<String, Object> extra) {
        this.extra = extra;
        return this;
    }
}
