package my.mbean;

import my.mbean.web.MBeanServlet;
import org.springframework.web.WebApplicationInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import static my.mbean.AutoConfiguration.MBEAN_SERVLET_NAME;

/**
 * Created by Administrator on 2017/5/18.
 */
public class MBeanServletRegistrar implements WebApplicationInitializer {
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
    }
}
