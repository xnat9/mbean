package my.mbean;

import my.mbean.web.MBeanServlet;
import org.springframework.web.WebApplicationInitializer;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.util.EnumSet;

import static my.mbean.AutoConfiguration.MBEAN_SERVLET_NAME;

/**
 * Created by Administrator on 2017/5/18.
 */
public class MBeanServletRegistrar implements WebApplicationInitializer {

    private static final EnumSet<DispatcherType> ASYNC_DISPATCHER_TYPES = EnumSet.of(
            DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.REQUEST,
            DispatcherType.ASYNC);
    @Override
    public void onStartup(ServletContext pServletContext) throws ServletException {
        MBeanServlet mbeanServlet = new MBeanServlet();
        ServletRegistration.Dynamic registration = pServletContext.addServlet(MBEAN_SERVLET_NAME, mbeanServlet);
//        MBeanConfiguration mBeanConfiguration = mBeanConfiguration();
//        WebApplicationContext context = WebApplicationContextUtils.findWebApplicationContext(pServletContext);
//        MBeanConfiguration mBeanConfiguration = context.getBean(MBeanConfiguration.class);
//        if (mBeanConfiguration != null) {
//            registration.addMapping(mBeanConfiguration.getMbeanUrlPrefix() + "/*");
//        } else {
//
//        }
        registration.addMapping(MBeanConfiguration.MBEAN_URL_PREFIX + "/*");

//         add filter.
//        FilterRegistration.Dynamic dynamic = pServletContext.addFilter("webRequestLoggingFilter", new OncePerRequestFilter() {
//            Log log = Log.of("webRequestLoggingFilter");
//            @Override
//            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//                log.info("trace request: uri: {0.requestURI}, method: {0.method}, remoteAddr: {0.remoteAddr}", request);
//                filterChain.doFilter(request, response);
//            }
//        });
//        dynamic.addMappingForUrlPatterns(ASYNC_DISPATCHER_TYPES, false, "/*");
    }
}
