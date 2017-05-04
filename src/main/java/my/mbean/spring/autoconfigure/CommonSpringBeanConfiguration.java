package my.mbean.spring.autoconfigure;

import my.mbean.spring.CustomBeanNameGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

/**
 * for common bean define.
 * @author hubert
 */
@Configuration
public class CommonSpringBeanConfiguration {
    @Autowired
    Environment environment;


    @EventListener(ContextRefreshedEvent.class)
    void postApplicationReady(ContextRefreshedEvent pRefreshedEvent) {
        registerSpringCommonBean(pRefreshedEvent);
    }



    private void registerSpringCommonBean(ContextRefreshedEvent pRefreshedEvent) {
//        if (environment.getProperty("spring.registerApplication", boolean.class, true)) {
//            pReadyEvent.getApplicationContext().getBeanFactory()
//                .registerSingleton(CustomBeanNameGenerator.BEAN_NAME_SPRINGAPPLICATION, pReadyEvent.getSpringApplication());
//        }
        ConfigurableApplicationContext context = (ConfigurableApplicationContext) pRefreshedEvent.getApplicationContext();
        if (environment.getProperty("spring.registerApplicationContext", boolean.class, true)) {
            context.getBeanFactory().registerSingleton(CustomBeanNameGenerator.BEAN_NAME_APPLICATIONCONTEXT, context);
        }
        if (environment.getProperty("spring.registerBeanFactory", boolean.class, true)) {
            context.getBeanFactory().registerSingleton(CustomBeanNameGenerator.BEAN_NAME_BEANFACTORY, context.getBeanFactory());
        }
        if (environment.getProperty("spring.registerPropertySources", boolean.class, true)) {
            context.getBeanFactory().registerSingleton(CustomBeanNameGenerator.BEAN_NAME_PROPERTYSOURCES, context.getEnvironment().getPropertySources());
        }
    }

}
