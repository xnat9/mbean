package my.mbean.spring.autoconfigure;

import my.mbean.spring.CustomBeanNameGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
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


    @EventListener(ApplicationReadyEvent.class)
    void postApplicationReady(ApplicationReadyEvent pReadyEvent) {
        registerSpringCommonBean(pReadyEvent);
    }



    private void registerSpringCommonBean(ApplicationReadyEvent pReadyEvent) {
        if (environment.getProperty("spring.registerApplication", boolean.class, true)) {
            pReadyEvent.getApplicationContext().getBeanFactory()
                .registerSingleton(CustomBeanNameGenerator.BEAN_NAME_SPRINGAPPLICATION, pReadyEvent.getSpringApplication());
        }
        if (environment.getProperty("spring.registerApplicationContext", boolean.class, true)) {
            pReadyEvent.getApplicationContext().getBeanFactory()
                .registerSingleton(CustomBeanNameGenerator.BEAN_NAME_APPLICATIONCONTEXT, pReadyEvent.getApplicationContext());
        }
        if (environment.getProperty("spring.registerBeanFactory", boolean.class, true)) {
            pReadyEvent.getApplicationContext().getBeanFactory()
                .registerSingleton(CustomBeanNameGenerator.BEAN_NAME_BEANFACTORY, pReadyEvent.getApplicationContext().getBeanFactory());
        }
        if (environment.getProperty("spring.registerPropertySources", boolean.class, true)) {
            pReadyEvent.getApplicationContext().getBeanFactory()
                .registerSingleton(CustomBeanNameGenerator.BEAN_NAME_PROPERTYSOURCES, pReadyEvent.getApplicationContext().getEnvironment().getPropertySources());
        }
    }

}
