package my.mbean.web;

import my.mbean.MBeanConfiguration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Created by xnat on 17/5/5.
 */
public class MBeanServlet extends HttpServlet {
    private MBeanConfiguration mBeanConfiguration;


    public MBeanServlet(MBeanConfiguration pMBeanConfiguration) {
        this.mBeanConfiguration = pMBeanConfiguration;
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
