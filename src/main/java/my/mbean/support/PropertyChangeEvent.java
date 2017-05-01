package my.mbean.support;

import org.springframework.context.ApplicationEvent;

public class PropertyChangeEvent extends ApplicationEvent {
    private static final long serialVersionUID = 1L;
    private String            propertyName;
    private String            beanName;
    private String            contextId;



    public PropertyChangeEvent(Object pSource, String pPropertyName, String pBeanName, String pContextId) {
        super(pSource);
        this.beanName = pBeanName;
        this.contextId = pContextId;
        this.propertyName = pPropertyName;
    }



    public String getBeanName() {
        return beanName;
    }



    public String getContextId() {
        return contextId;
    }



    public String getPropertyName() {
        return propertyName;
    }
}
