package my.mbean;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import my.mbean.util.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;

import my.mbean.service.BeansService;
import my.mbean.spring.GenericService;
import my.mbean.util.Utils;
import my.mbean.view.SpelView;

@Controller
@RequestMapping(MBeanController.MBEAN_URL_PREFIX_EL)
public class MBeanController extends GenericService {
    public static final String CONTEXT_URL_PREFIX = "/mbean";
    public static final String MBEAN_URL_PREFIX_EL = "${mbean.url.prefix:" + CONTEXT_URL_PREFIX + "}";

    public static final String PARAME_ACTION = "action";
    public static final String PARAME_CONTEXTID = "contextId";
    public static final String PARAME_PROPERTY_NAME = "propertyName";
    public static final String PARAME_METHOD_NAME = "methodName";
    public static final String PARAME_BEAN_NAME = "beanName";
    public static final String PARAME_NEWVALUE = "newValue";
    public static final String PARAME_TYPE_NAME = "typeName";
    public static final String URL_BEAN_CHANGE = "/beans/change";
    public static final String URL_BEAN_INVOKE = "/beans/invoke";

    @Value(MBEAN_URL_PREFIX_EL)
    private String contextUrlPrefix = CONTEXT_URL_PREFIX;
    private String beansUrlPrefix;
    private String propertyChangeUrl;
    private String methodInvokeUrl;
    private String staticResourceUrlPrefix;


    /**
     * map<beanName, view>
     */
    private Map<String, String> beanViewMap;
    @Resource
    @Lazy
    BeansService beansService;


    @Override
    public void afterPropertiesSet() throws Exception {
        beansUrlPrefix = CONTEXT_URL_PREFIX + "/beans";
        propertyChangeUrl = CONTEXT_URL_PREFIX + URL_BEAN_CHANGE;
        methodInvokeUrl = CONTEXT_URL_PREFIX + URL_BEAN_INVOKE;
        staticResourceUrlPrefix = CONTEXT_URL_PREFIX + "/static/resource";
        customInit();
    }


    protected void customInit() {
        String[] beanNames = getApplicationContext().getBeanNamesForType(AbstractHandlerMethodMapping.class);
        if (Utils.isNotEmpty(beanNames)) {
            for (String beanName : beanNames) {
                getBeanViewMap().put(beanName, "bean/AbstractHandlerMethodMapping");
            }
        }
        beanNames = getApplicationContext().getBeanNamesForAnnotation(Configuration.class);
        if (Utils.isNotEmpty(beanNames)) {
            for (String beanName : beanNames) {
                getBeanViewMap().put(beanName, "bean/ConfigurationBean");
            }
        }
    }


    @GetMapping(path = "search/{beanName}", produces = MediaType.TEXT_HTML_VALUE)
    ModelAndView searchPage(@PathVariable("beanName") String pBeanName) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView(new SpelView(beansService.getHtmlTemplate("search")));
        modelAndView.addObject("beans", Utils.toJsonStr(beansService.searchBeans(pBeanName)));
        modelAndView.addObject("title", "Search Page: " + pBeanName);
        modelAndView.addObject("staticUrlPrefix", getStaticResourceUrlPrefix());
        return modelAndView;
    }

    @GetMapping(path = "beans/{beanName}", produces = MediaType.TEXT_HTML_VALUE)
    ModelAndView browse(@PathVariable("beanName") String pBeanName, @RequestParam Map<String, Object> pParams) {
        ModelAndView modelAndView = new ModelAndView();
        String contextId = (String) pParams.get(PARAME_CONTEXTID);
        String propertyName = (String) pParams.get(PARAME_PROPERTY_NAME);
        String methodName = (String) pParams.get(PARAME_METHOD_NAME);
        String templateName = null;
        if (StringUtils.isNotBlank(propertyName)) {
            templateName = "property";
            Object propertyVO = beansService.buildPropertyVO(propertyName, pBeanName, contextId);
            Utils.extractPropertiesToMapValueToJsonStr(propertyVO, modelAndView.getModel());
        } else if (StringUtils.isNotBlank(methodName)) {
            Map<String, Object> methodInfo = beansService.buildMethodInfo(methodName, pBeanName, contextId);
            modelAndView.addAllObjects(methodInfo);
            templateName = "method";
        } else {
            Object beanVO = beansService.buildBeanVO(pBeanName, contextId);
            Utils.extractPropertiesToMapValueToJsonStr(beanVO, modelAndView.getModel());
            templateName = getBeanViewMap().get(pBeanName);
            if (Utils.isBlank(templateName)) {
                templateName = "bean";
            }
        }
        modelAndView.addObject("staticUrlPrefix", getStaticResourceUrlPrefix());
        modelAndView.setView(new SpelView(beansService.getHtmlTemplate(templateName)));
        return modelAndView;
    }


    @ResponseBody
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


    @ResponseBody
    @PostMapping(path = {URL_BEAN_INVOKE})
    public Response invoke(@RequestParam Map<String, Object> pParams) {
        log.debug("invoke(): params: {0}", pParams);
        String methodName = (String) pParams.get(PARAME_METHOD_NAME);
        String beanName = (String) pParams.get(PARAME_BEAN_NAME);
        String contextId = (String) pParams.get(PARAME_CONTEXTID);
        Response result = beansService.invokeMethod(methodName, beanName, contextId);
        return result;
    }


    // @GetMapping(path = "bean/{beanName}", produces =
    // MediaType.TEXT_HTML_VALUE)
    // String beanPage() {
    // return "/mbean/static/resource/bean.html";
    // }


    // @ResponseBody
    // @PostMapping(path = "search/{beanName}")
    // Response search(@PathVariable("beanName") String pBeanName) {
    // List<Map<String, Object>> beans = beansService.searchBeans(pBeanName);
    // return Response.of("beans", beans);
    // }

    public String getStaticResourceUrlPrefix() {
        return staticResourceUrlPrefix;
    }


    /**
     * @return the propertyChangeUrl
     */
    public String getPropertyChangeUrl() {
        return propertyChangeUrl;
    }


    /**
     * @return the methodInvokeUrl
     */
    public String getMethodInvokeUrl() {
        return methodInvokeUrl;
    }


    /**
     * @return the beanViewMap
     */
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


    /**
     * @param pBeanViewMap the beanViewMap to set
     */
    public void setBeanViewMap(Map<String, String> pBeanViewMap) {
        beanViewMap = pBeanViewMap;
    }

    public String getContextUrlPrefix() {
        return this.contextUrlPrefix;
    }

    public String getBeansUrlPrefix() {
        return beansUrlPrefix;
    }
}
