package my.mbean.web;

import my.mbean.MBeanConfiguration;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Created by xnat on 17/5/5.
 */
//@WebServlet
public class MBeanServlet extends HttpServlet {
    private MBeanConfiguration mBeanConfiguration;


    @Override
    public void init() throws ServletException {
        super.init();
        WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        mBeanConfiguration = context.getBean(MBeanConfiguration.class);
        log("init....");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    void processRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        mBeanConfiguration.getRequestDispatcher().dispatch(req, resp);
    }

}
