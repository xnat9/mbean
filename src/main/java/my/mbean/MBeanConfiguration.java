package my.mbean;

import my.mbean.spring.GenericService;
import my.mbean.web.MBeanController;
import my.mbean.web.RequestDispatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringValueResolver;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UrlPathHelper;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xnat on 17/5/6.
 */
public class MBeanConfiguration extends GenericService {
    public static final String MBEAN_URL_PREFIX = "/mbean";
    public static final String MBEAN_URL_PREFIX_EXPR = "${mbean.url.prefix:" + MBEAN_URL_PREFIX + "}";
    public static final String URL_BEAN_CHANGE = "/beans/change";
    public static final String URL_BEAN_INVOKE = "/beans/invoke";

    @Value(MBEAN_URL_PREFIX_EXPR)
    private String mbeanUrlPrefix;
    private String beansUrlPrefix;
    private String propertyChangeUrl;
    private String methodInvokeUrl;
    private String staticResourceUrlPrefix;
    /**
     * map<beanName, view>
     */
    private Map<String, String> beanViewMap;

    private UrlPathHelper urlPathHelper;
    private PathMatcher pathMatcher;
//    @Resource
//    private StringValueResolver embeddedValueResolver;
    @Resource
    @Lazy
    private MBeanController mbeanController;
    @Resource
    @Lazy
    private RequestDispatcher requestDispatcher;


    @Override
    protected void doStartService() {
        ServletContext servletContext = ((WebApplicationContext) getApplicationContext()).getServletContext();
        String servletContextPath = servletContext.getContextPath();
        beansUrlPrefix = servletContextPath + mbeanUrlPrefix + "/beans";
        propertyChangeUrl = servletContextPath + mbeanUrlPrefix + URL_BEAN_CHANGE;
        methodInvokeUrl = servletContextPath + mbeanUrlPrefix + URL_BEAN_INVOKE;
        staticResourceUrlPrefix = servletContextPath + mbeanUrlPrefix + "/static/resource";

        urlPathHelper = new UrlPathHelper();
        pathMatcher = new AntPathMatcher();


    }

    public RequestDispatcher getRequestDispatcher() {
        return requestDispatcher;
    }

    public UrlPathHelper getUrlPathHelper() {
        return urlPathHelper;
    }

    public PathMatcher getPathMatcher() {
        return pathMatcher;
    }

    public Map<String, String> getBeanViewMap() {
        if (beanViewMap == null) {
            synchronized (this) {
                if (beanViewMap == null) {
                    beanViewMap = new HashMap<>(7);
                }
            }
        }
        return beanViewMap;
    }

    public MBeanController getMbeanController() {
        return mbeanController;
    }

    public String getMbeanUrlPrefix() {
        return mbeanUrlPrefix;
    }

    public String getBeansUrlPrefix() {
        return beansUrlPrefix;
    }

    public String getPropertyChangeUrl() {
        return propertyChangeUrl;
    }

    public String getMethodInvokeUrl() {
        return methodInvokeUrl;
    }

    public String getStaticResourceUrlPrefix() {
        return staticResourceUrlPrefix;
    }
}
