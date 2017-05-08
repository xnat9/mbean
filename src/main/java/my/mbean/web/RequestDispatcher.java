package my.mbean.web;

import my.mbean.MBeanConfiguration;
import my.mbean.spring.GenericService;
import my.mbean.util.Response;
import my.mbean.util.Utils;
import my.mbean.util.json.JsonUtil;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.PathMatcher;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestParamMapMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.method.support.InvocableHandlerMethod;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.util.UrlPathHelper;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


/**
 * Created by xnat on 17/5/7.
 */
public class RequestDispatcher extends GenericService {
    public static final String URI_TEMPLATE_VARIABLES_ATTRIBUTE = "uriTemplateVariables";
    public static final String PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE = "pathWithinHandlerMapping";
    @Resource
    private MBeanConfiguration mBeanConfiguration;
    @Resource
    private MBeanController mBeanController;
    private HandlerMethodArgumentResolverComposite argumentResolvers;
    private Map<String, HandlerMethod> handlerMethodMap = new HashMap<>();


    @Override
    protected void doStartService() {
        super.doStartService();
        initHandlerMethods();
        initArgumentResolvers();
    }

    protected void initArgumentResolvers() {
        argumentResolvers = new HandlerMethodArgumentResolverComposite()
                .addResolver(new RequestParamMapMethodArgumentResolver())
                .addResolver(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        Class<?> paramType = parameter.getParameterType();
                        return (ServletRequest.class.isAssignableFrom(paramType) ||
                                OutputStream.class.isAssignableFrom(paramType) ||
                                ServletResponse.class.isAssignableFrom(paramType) ||
                                Writer.class.isAssignableFrom(paramType));
                    }

                    @Override
                    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
                        if (mavContainer != null) {
                            mavContainer.setRequestHandled(true);
                        }

                        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
                        Class<?> paramType = parameter.getParameterType();

                        if (ServletRequest.class.isAssignableFrom(paramType)) {
                            Object nativeRequest = webRequest.getNativeRequest(paramType);
                            if (nativeRequest == null) {
                                throw new IllegalStateException("Current request is not of type [" + paramType.getName() + "]: " + nativeRequest);
                            }
                            return nativeRequest;
                        } else if (ServletResponse.class.isAssignableFrom(paramType)) {
                            Object nativeResponse = webRequest.getNativeResponse(paramType);
                            if (nativeResponse == null) {
                                throw new IllegalStateException("Current response is not of type [" + paramType.getName() + "]: " + response);
                            }
                            return nativeResponse;
                        } else if (OutputStream.class.isAssignableFrom(paramType)) {
                            return response.getOutputStream();
                        } else if (Writer.class.isAssignableFrom(paramType)) {
                            return response.getWriter();
                        } else {
                            // should not happen
                            Method method = parameter.getMethod();
                            throw new UnsupportedOperationException("Unknown parameter type: " + paramType + " in method: " + method);
                        }
                    }
                })
                .addResolver(new AbstractNamedValueMethodArgumentResolver() {
                    @Override
                    protected NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
                        PathVariable annotation = parameter.getParameterAnnotation(PathVariable.class);
                        return new NamedValueInfo(annotation.name(), annotation.required(), ValueConstants.DEFAULT_NONE);
                    }

                    @Override
                    protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
                        Map<String, String> uriTemplateVars = (Map<String, String>) request.getAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
                        return (uriTemplateVars != null ? uriTemplateVars.get(name) : null);
                    }

                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        if (!parameter.hasParameterAnnotation(PathVariable.class)) {
                            return false;
                        }
                        if (Map.class.isAssignableFrom(parameter.nestedIfOptional().getNestedParameterType())) {
                            String paramName = parameter.getParameterAnnotation(PathVariable.class).value();
                            return StringUtils.hasText(paramName);
                        }
                        return true;
                    }
                });
    }


    protected void initHandlerMethods() {
        Class handlerSourceCls = mBeanController.getClass();
        Set<Method> handlerMethods = MethodIntrospector.selectMethods(handlerSourceCls, new ReflectionUtils.MethodFilter() {
            @Override
            public boolean matches(Method pMethod) {
                return AnnotatedElementUtils.hasAnnotation(pMethod, RequestMapping.class);
//                RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(pMethod, RequestMapping.class);
//                return requestMapping != null;
            }
        });
        for (Method method : handlerMethods) {
            HandlerMethod handlerMethod = new HandlerMethod(mBeanController, method);
            RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
            String[] pathArr = requestMapping.path();
            if (Utils.isNotEmpty(pathArr)) {
                for (String path : pathArr) {
                    handlerMethodMap.put(path, handlerMethod);
                }
            }
        }
    }

    public void dispatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UrlPathHelper urlPathHelper = mBeanConfiguration.getUrlPathHelper();
        String lookupPath = urlPathHelper.getLookupPathForRequest(request);
        PathMatcher pathMatcher = mBeanConfiguration.getPathMatcher();
        HandlerMethod handlerMethod = null;
        String pathMatch = null;
        // RequestMapping 中的Path变量解析, 参考AbstractUrlHandlerMapping.buildPathExposingHandler()
        Map<String, String> uriTemplateVariables = new LinkedHashMap<String, String>(5);
        for (Map.Entry<String, HandlerMethod> entry : handlerMethodMap.entrySet()) {
            // TODO 没有判断是GET或POST
            if (pathMatcher.match(entry.getKey(), lookupPath)) {
                handlerMethod = entry.getValue();
                pathMatch = entry.getKey();
                Map<String, String> vars = pathMatcher.extractUriTemplateVariables(entry.getKey(), lookupPath);
                uriTemplateVariables.putAll(vars);
                break;
            }
        }
        String pathWithinMapping = pathMatcher.extractPathWithinPattern(pathMatch, lookupPath);
        request.setAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, pathWithinMapping);
        request.setAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE, uriTemplateVariables);

        ServletWebRequest webRequest = new ServletWebRequest(request, response);
        ModelAndViewContainer mavContainer = new ModelAndViewContainer();
        try {
            InvocableHandlerMethod invocableHandlerMethod = new InvocableHandlerMethod(handlerMethod);
            invocableHandlerMethod.setHandlerMethodArgumentResolvers(argumentResolvers);
            Object returnValue = invocableHandlerMethod.invokeForRequest(webRequest, mavContainer);
            Class<?> returnType = handlerMethod.getReturnType().getParameterType();
            if (String.class.equals(returnType)) {
                StreamUtils.copy((String) returnValue, Charset.defaultCharset(), response.getOutputStream());
            } else if (Response.class.equals(returnType)) {
                JsonUtil.get(getClassLoader()).write(returnValue, response.getOutputStream());
            } else if (handlerMethod.isVoid()){
                // ignore.
            } else {
                log.info("not support return type: {0.class}", returnValue);
            }
        } catch (Exception e) {
            log.error(e, "dispatch error");
            //response.getWriter().write(e.getMessage());
        }
    }
}
