package my.mbean.web;

import my.mbean.MBeanConfiguration;
import my.mbean.service.BeansService;
import my.mbean.spring.GenericService;
import my.mbean.util.Response;
import my.mbean.util.Utils;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringValueResolver;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static my.mbean.MBeanConfiguration.URL_BEAN_CHANGE;
import static my.mbean.MBeanConfiguration.URL_BEAN_INVOKE;

/**
 * MBean Controller.
 * Created by xnat on 17/5/6.
 */
public class MBeanController extends GenericService {
    public static final String PARAME_ACTION        = "action";
    public static final String PARAME_CONTEXTID     = "contextId";
    public static final String PARAME_PROPERTY_NAME = "propertyName";
    public static final String PARAME_METHOD_NAME   = "methodName";
    public static final String PARAME_BEAN_NAME     = "beanName";
    public static final String PARAME_NEWVALUE      = "newValue";
    public static final String PARAME_TYPE_NAME     = "typeName";
    @Resource
    private BeansService       beansService;
    @Resource
    private MBeanConfiguration mBeanConfiguration;


    @GetMapping("/search/{beanName}")
    String searchPage(@PathVariable("beanName") String pBeanName) {
        String html = beansService.getHtmlTemplate("search");
        Map<String, Object> model = new HashMap<>(3);
        model.put("beans", Utils.toJsonStr(beansService.searchBeans(pBeanName)));
        model.put("kw", pBeanName);
        model.put("staticUrlPrefix", mBeanConfiguration.getStaticResourceUrlPrefix());
        html = Utils.render(html, model);
        return html;
    }

    @GetMapping("/beans/{beanName}")
    String browse(@PathVariable("beanName") String pBeanName, @RequestParam Map<String, Object> pParams) {
        String contextId = (String) pParams.get(PARAME_CONTEXTID);
        String propertyName = (String) pParams.get(PARAME_PROPERTY_NAME);
        String methodName = (String) pParams.get(PARAME_METHOD_NAME);
        String templateName = null;
        Map<String, Object> model = new HashMap<>(3);
        if (Utils.isNotBlank(propertyName)) {
            templateName = "property";
            Object propertyVO = beansService.buildPropertyVO(propertyName, pBeanName, contextId);
            Utils.extractPropertiesToMapValueToJsonStr(propertyVO, model);
        } else if (Utils.isNotBlank(methodName)) {
            model = beansService.buildMethodInfo(methodName, pBeanName, contextId);
            templateName = "method";
        } else {
            Object beanVO = beansService.buildBeanVO(pBeanName, contextId);
            Utils.extractPropertiesToMapValueToJsonStr(beanVO, model);
            templateName = mBeanConfiguration.getBeanViewMap().get(pBeanName);
            if (Utils.isBlank(templateName)) {
                templateName = "bean";
            }
        }
        model.put("staticUrlPrefix", mBeanConfiguration.getStaticResourceUrlPrefix());
        return Utils.render(beansService.getHtmlTemplate(templateName), model);
    }



    @PostMapping(path = {URL_BEAN_CHANGE})
    public Response change(@RequestParam Map<String, Object> pParams) {
        log.debug("change(): params: {0}", pParams);
        String contextId = (String) pParams.get(PARAME_CONTEXTID);
        String propertyName = (String) pParams.get(PARAME_PROPERTY_NAME);
        String beanName = (String) pParams.get(PARAME_BEAN_NAME);
        String newValue = (String) pParams.get(PARAME_NEWVALUE);
        Response result = beansService.changeProperty(propertyName, beanName, contextId, newValue);
        return result;
    }


    @PostMapping(path = {URL_BEAN_INVOKE})
    public Response invoke(@RequestParam Map<String, Object> pParams)  {
        log.debug("invoke(): params: {0}", pParams);
        String methodName = (String) pParams.get(PARAME_METHOD_NAME);
        String beanName = (String) pParams.get(PARAME_BEAN_NAME);
        String contextId = (String) pParams.get(PARAME_CONTEXTID);
        Response result = beansService.invokeMethod(methodName, beanName, contextId);
        return result;
    }


    /**
     * 处理静态资源.
     */
    @GetMapping("/static/**")
    void staticResource(HttpServletRequest pRequest, HttpServletResponse pResponse) {
        String resourcePath = (String) pRequest.getAttribute(RequestDispatcher.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        org.springframework.core.io.Resource resource = getApplicationContext().getResource(mBeanConfiguration.getInternalStaticResourcePathPrefix() + resourcePath);
//        InputStream in = MBeanConfiguration.class.getResourceAsStream("view/" + resourcePath);
        pResponse.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
        InputStream in = null;
        try {
            if (new ServletWebRequest(pRequest, pResponse).checkNotModified(resource.lastModified())) {
                // http: 1.1
                pResponse.setHeader("Cache-Control", CacheControl.maxAge(1, TimeUnit.MINUTES).getHeaderValue());
                log.trace("Resource not modified - returning 304");
                return;
            }
            in = resource.getInputStream();
            StreamUtils.copy(in, pResponse.getOutputStream());
        } catch (Exception e) {
            log.error(e, "get resource: {0} error", resourcePath);
        } finally {
            try {
//                resource.getInputStream().close();
                if (in != null) in.close();
            } catch (Throwable ex) {
                // ignore, see SPR-12999
            }
        }
    }
}
