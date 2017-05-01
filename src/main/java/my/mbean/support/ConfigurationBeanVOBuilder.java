package my.mbean.support;

import java.lang.reflect.Method;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.ReflectionUtils;

/**
 * 用于显示@Configuration bean.
 * @author Hubert
 */
public class ConfigurationBeanVOBuilder extends GenericBeanVOBuilder {
    @Override
    public Object buildPropertiesView(String pBeanName, String pContextId) {
        return null;
    }



    @Override
    protected Method[] findMethods(String pBeanName, ConfigurableApplicationContext pContext) {
        Class<?> beanClass = beansService.getBeanClass(pContext, pBeanName, true);
        if (beanClass == null) {
            log.warn("findMethods(): not retrieve class for bean: {0}", pBeanName);
            return null;
        }
        return ReflectionUtils.getUniqueDeclaredMethods(beanClass);
    }
}
