package my.mbean;

import my.mbean.service.BeansService;
import my.mbean.support.ConfigurationBeanVOBuilder;
import my.mbean.support.GenericBeanVOBuilder;
import my.mbean.support.GenericPropertyVOBuilder;
import my.mbean.util.log.LogSystem;
import my.mbean.web.MBeanController;
import my.mbean.web.MBeanServlet;
import my.mbean.web.RequestDispatcher;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.*;
import javax.servlet.annotation.WebListener;

@Configuration
//@EnableWebMvc
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


//    @EventListener(ContextRefreshedEvent.class)
//    public void registerMbeanServlet(ContextRefreshedEvent pEvent) {
//        ServletContext servletContext = ((WebApplicationContext) pEvent.getApplicationContext()).getServletContext();
//        MBeanServlet mbeanServlet = new MBeanServlet();
//        MBeanConfiguration mBeanConfiguration = mBeanConfiguration();
//        ServletRegistration.Dynamic registration = servletContext.addServlet(MBEAN_SERVLET_NAME, mbeanServlet);
//        registration.addMapping(mBeanConfiguration.getMbeanUrlPrefix() + "/*");
//    }
//    @Bean
//    WebApplicationInitializer mbeanRegistrar() {
//        return new WebApplicationInitializer() {
//            @Override
//            public void onStartup(ServletContext pServletContext) throws ServletException {
//                MBeanServlet mbeanServlet = new MBeanServlet();
//                ServletRegistration.Dynamic registration = pServletContext.addServlet(MBEAN_SERVLET_NAME, mbeanServlet);
//                MBeanConfiguration mBeanConfiguration = mBeanConfiguration();
//                registration.addMapping(mBeanConfiguration.getMbeanUrlPrefix() + "/*");
//            }
//        };
//    }

//    @WebListener
//    class MbeanServletRegistion implements ServletContextListener {
//
//        @Override
//        public void contextInitialized(ServletContextEvent sce) {
//            MBeanServlet mbeanServlet = new MBeanServlet();
//            ServletRegistration.Dynamic registration = sce.getServletContext().addServlet(MBEAN_SERVLET_NAME, mbeanServlet);
//            MBeanConfiguration mBeanConfiguration = mBeanConfiguration();
//            registration.addMapping(mBeanConfiguration.getMbeanUrlPrefix() + "/*");
//        }
//
//        @Override
//        public void contextDestroyed(ServletContextEvent sce) {
//
//        }
//    }

    @Bean
    MBeanConfiguration mBeanConfiguration() {
        return new MBeanConfiguration();
    }

    public static final String MBEAN_SERVLET_NAME = "mbeanServlet";

    @Bean
    @Lazy
    MBeanController mBeanController() {
        return new MBeanController();
    }

    @Bean
    @Lazy
    RequestDispatcher requestDispatcher() {
        return new RequestDispatcher();
    }

    @Bean
    public ServletRegistrationBean mbeanServletRegistrationBean() {
        ServletRegistrationBean registrationBean = new ServletRegistrationBean();
        MBeanConfiguration mBeanConfiguration = mBeanConfiguration();
        MBeanServlet mbeanServlet = new MBeanServlet();
        registrationBean.setServlet(mbeanServlet);
        registrationBean.setName(MBEAN_SERVLET_NAME);
        registrationBean.addUrlMappings(mBeanConfiguration.getMbeanUrlPrefix() + "/*");
        return registrationBean;
    }


//    @Bean
//    WebMvcConfigurerAdapter mBeansWebMvcConfigurer() {
//        WebMvcConfigurerAdapter webMvcConfigurer = new WebMvcConfigurerAdapter() {
//            @Override
//            public void addResourceHandlers(ResourceHandlerRegistry pRegistry) {
//                pRegistry.addResourceHandler(MBeanController.CONTEXT_URL_PREFIX + "/static/**")
//                        .addResourceLocations(Utils.buildClassPath(MBeanController.class, "view/"));
//            }
//        };
//        return webMvcConfigurer;
//    }


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
