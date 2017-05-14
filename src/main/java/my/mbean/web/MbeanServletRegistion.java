package my.mbean.web;

import my.mbean.MBeanConfiguration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebListener;

import static my.mbean.AutoConfiguration.MBEAN_SERVLET_NAME;

/**
 * Created by xnat on 17/5/9.
 */
@WebListener
public class MbeanServletRegistion implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
//        MBeanServlet mbeanServlet = new MBeanServlet();
//        ServletRegistration.Dynamic registration = sce.getServletContext().addServlet(MBEAN_SERVLET_NAME, mbeanServlet);
//        MBeanConfiguration mBeanConfiguration = mBeanConfiguration();
//        registration.addMapping(mBeanConfiguration.getMbeanUrlPrefix() + "/*");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
