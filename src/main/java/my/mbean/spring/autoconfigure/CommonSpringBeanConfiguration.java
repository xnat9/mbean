package my.mbean.spring.autoconfigure;

import my.mbean.spring.CustomBeanNameGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.web.context.support.ServletRequestHandledEvent;

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

    @EventListener(ServletRequestHandledEvent.class)
    void postProcessRequest(ServletRequestHandledEvent event) {
        if (environment.getProperty("spring.request.monitor", boolean.class, true)) {
            Log logger = LogFactory.getLog(event.getSource().getClass());
            if (event.getProcessingTimeMillis() > 1500) {
                logger.warn("long request: " + event.toString());
            } else {
                logger.debug(event.toString());
            }
        }
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
