package my.mbean;

import my.mbean.service.BeansService;
import my.mbean.support.ConfigurationBeanVOBuilder;
import my.mbean.support.GenericBeanVOBuilder;
import my.mbean.support.GenericPropertyVOBuilder;
import my.mbean.util.Utils;
import my.mbean.util.log.LogSystem;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
public class AutoConfiguration {
    @Bean
    // @ConfigurationProperties(prefix = CONFIG_PREFIX_ACTUATOR_ADMIN_BEANS +
    // ".beansService")
    @Lazy
    BeansService beansService() {
        BeansService beansService = new BeansService();
        return beansService;
    }

    @Bean
    LogSystem logSystem(ApplicationContext pContext) {
        return LogSystem.get(pContext.getClassLoader());
    }

//    @Bean
//    @ConditionalOnMissingBean
//    CacheManager cacheManager() {
//        return new ConcurrentMapCacheManager(BeansService.CACHE_NAME) {
//            @Override
//            protected Cache createConcurrentMapCache(String pName) {
//                // TODO use LRUMap
//                return super.createConcurrentMapCache(pName);
//            }
//        };
//    }


    @Bean
    WebMvcConfigurerAdapter adminBeansWebMvcConfigurer() {
        WebMvcConfigurerAdapter webMvcConfigurer = new WebMvcConfigurerAdapter() {
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry pRegistry) {
                pRegistry.addResourceHandler(MBeanController.CONTEXT_URL_PREFIX + "/static/**")
                        .addResourceLocations(Utils.buildClassPath(MBeanController.class, "view/"));
            }
        };
        return webMvcConfigurer;
    }


    @Bean
    @Lazy
    @Primary
    // @ConfigurationProperties(prefix = CONFIG_PREFIX_ACTUATOR_ADMIN_BEANS +
    // ".beanview.builder.default")
        // @Scope(proxyMode=ScopedProxyMode.TARGET_CLASS)
    GenericBeanVOBuilder defaultBeanVOBuilder() {
        // default BeanVOBuilder.
        return new GenericBeanVOBuilder() {
        };
    }


    @Bean
    @Lazy
    // @ConfigurationProperties(prefix = CONFIG_PREFIX_ACTUATOR_ADMIN_BEANS +
    // ".beanview.builder.configuration")
    ConfigurationBeanVOBuilder configurationBeanVOBuilder(final ApplicationContext pContext) {
        return new ConfigurationBeanVOBuilder() {
            @Override
            public String[] getIds() {
                return pContext.getBeanNamesForAnnotation(Configuration.class);
            }
        };
    }


    @Bean
    @Lazy
    @Primary
    // @ConfigurationProperties(prefix = CONFIG_PREFIX_ACTUATOR_ADMIN_BEANS +
    // ".propertyview.builder")
    GenericPropertyVOBuilder defaultPropertyVOBuilder() {
        return new GenericPropertyVOBuilder() {

        };
    }
}
