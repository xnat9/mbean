package my.mbean.support;

import my.mbean.util.Utils;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.core.env.StandardEnvironment;

import javax.annotation.Resource;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * Created by xnat on 17/5/31.
 */
@Configuration
public class BeanVOBuildersConfiguration {
    @Resource
    private ApplicationContext context;

    @Bean
    @Lazy
    @Primary
    GenericBeanVOBuilder defaultBeanVOBuilder() {
        // default BeanVOBuilder.
        return new GenericBeanVOBuilder() {
        };
    }

    @Bean
    @Lazy
    GenericBeanVOBuilder beanFactoryBeanVOBuilder() {
        return new GenericBeanVOBuilder() {
            @Override
            public String[] getIds() {
                return context.getBeanNamesForType(DefaultListableBeanFactory.class);
            }
            @Override
            protected Object populatePropertyValue(String pBeanName, PropertyDescriptor pPropertyDescriptor, ConfigurableApplicationContext pContext) {
                return super.populatePropertyValue(pBeanName, pPropertyDescriptor, pContext);
            }

            @Override
            protected Object populatePropertyValue(String pBeanName, Field pField, ConfigurableApplicationContext pContext) {
                return super.populatePropertyValue(pBeanName, pField, pContext);
            }

            @Override
            protected boolean isIgnoreProperty(String pBeanName, PropertyDescriptor pPropDesc, List<PropertyInfo> pProperties) {
                return "singletonMutex".equals(pPropDesc.getName())
                        || super.isIgnoreProperty(pBeanName, pPropDesc, pProperties);
            }

            @Override
            protected boolean isIgnoreField(String pBeanName, Field pField, List<PropertyInfo> pProperties) {
                return "frozenBeanDefinitionNames".equals(pField.getName())
                        || "filteredPropertyDescriptorsCache".equals(pField.getName())
                        || super.isIgnoreField(pBeanName, pField, pProperties);
            }
        };
    }

    @Bean
    @Lazy
    GenericBeanVOBuilder environmentBeanVOBuilder() {
        return new GenericBeanVOBuilder() {
            @Override
            public String[] getIds() {
                return context.getBeanNamesForType(StandardEnvironment.class);
            }
        };
    }
    @Bean
    @Lazy
    GenericBeanVOBuilder genericConversionServiceBeanVOBuilder() {
        return new GenericBeanVOBuilder() {
            @Override
            public String[] getIds() {
                return context.getBeanNamesForType(GenericConversionService.class);
            }

            @Override
            protected Object populatePropertyValue(String pBeanName, Field pField, ConfigurableApplicationContext pContext) {
                if ("converters".equals(pField.getName())) {
                    final Object beanInstance = beansService.getBeanInstance(pBeanName, pContext);
                    final Object propInstance = Utils.getValue(pField, beanInstance);
                    String[] lines = propInstance.toString().replace("ConversionService converters =\n", "").replace("\t", "").split("\n");
                    List<ValueWrapper.BeanRef> beanRefs = beansService.detectBeanRefs(lines, 150);
//                    for (ValueWrapper.BeanRef ref : beanRefs) {
//                        ref.setToString(ref.getToString().replace(",", ",\n"));
//                    }
                    return new ValueWrapper().setValue(beanRefs);
                }
                return super.populatePropertyValue(pBeanName, pField, pContext);
            }
        };
    }



    @Bean
    @Lazy
    GenericBeanVOBuilder abstractAutoProxyCreatorBeanVOBuilder() {
        return new GenericBeanVOBuilder() {
            @Override
            public String[] getIds() {
                return context.getBeanNamesForType(AbstractAutoProxyCreator.class);
            }

            @Override
            @SuppressWarnings("unchecked")
            protected Object populatePropertyValue(String pBeanName, Field pField, ConfigurableApplicationContext pContext) {
                if ("advisedBeans".equals(pField.getName())) {
                    final Object beanInstance = beansService.getBeanInstance(pBeanName, pContext);
                    final Map<String, Boolean> propInstance = (Map<String, Boolean>) Utils.getValue(pField, beanInstance);
                    Map<String, Boolean> trueMap = new TreeMap<>();
                    Map<String, Boolean> falseMap = new TreeMap<>();
                    for (Map.Entry<String, Boolean> entry : propInstance.entrySet()) {
                        if (entry.getValue()) trueMap.put(entry.getKey(), entry.getValue());
                        else falseMap.put(entry.getKey(), entry.getValue());
                    }
                    trueMap.putAll(falseMap);
                    return populateValueWrapper(trueMap);
                }
                return super.populatePropertyValue(pBeanName, pField, pContext);
            }
        };
    }


//    @Bean
//    @Lazy
//    GenericBeanVOBuilder configurationBeanVOBuilder() {
//        return new GenericBeanVOBuilder() {
//            @Override
//            public String[] getIds() {
//                return context.getBeanNamesForAnnotation(Configuration.class);
//            }
//
//            @Override
//            protected Method[] findMethods(String pBeanName, ConfigurableApplicationContext pContext) {
//                Class<?> beanClass = beansService.getBeanClass(pContext, pBeanName, true);
//                if (beanClass == null) {
//                    log.warn("findMethods(): not retrieve class for bean: {0}", pBeanName);
//                    return null;
//                }
//                return ReflectionUtils.getUniqueDeclaredMethods(beanClass);
//            }
//        };
//    }


}
