package my.mbean.support;

public interface PropertyVOBuilder<T> {
    T build(String pPropName, String pBeanName, String pContextId);
}
