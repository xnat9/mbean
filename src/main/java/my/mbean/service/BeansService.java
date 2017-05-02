package my.mbean.service;

import my.mbean.spring.BaseBean;
import my.mbean.MBeanController;
import my.mbean.spring.GenericService;
import my.mbean.support.BeanVOBuilder;
import my.mbean.support.PropertyChangeEvent;
import my.mbean.support.PropertyVOBuilder;
import my.mbean.support.ValueWrapper;
import my.mbean.support.ValueWrapper.BeanRef;
import my.mbean.util.Msg;
import my.mbean.util.Response;
import my.mbean.util.Utils;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.util.*;
import org.springframework.web.servlet.HandlerMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 尽量用Spring 相关的工具和已存在的类, 本身就是专为Spring 写的.
 * 判断为null 用 Objects.isNull 非null Objects.nonNull
 * service for MBeanController.
 *
 * @author hubert
 */
public class BeansService extends GenericService {
    /**
     * search limit bean count.
     */
    private Integer searchBeanLimitCount = Integer.valueOf(30);
    private List<Class<?>> typeList;
    // TODO not need?
    private List<Class<?>> additionalTypeList;

    /**
     * 找定某个bean 用某个builder. map(beanName, BeanVOBuilder).
     */
    private Map<String, BeanVOBuilder<?>> beanVOBuilders;
    private PropertyVOBuilder<?> propertyVOBuilder;
    @Autowired
    PropertyAccessService propertyAccessService;
    @Autowired
    MBeanController mbeanController;


    @Override
    protected void doStartService() {
        super.doStartService();
        setServiceInfo("for MBeanController(admin/beans) provide logic service.");
        customInit();
    }


    @Override
    protected void doStopService() {
        super.doStopService();
        clearCache();
    }


    protected void customInit() {
        initBeanVOBuidlers();
        initPropertyVOBuidler();
    }


    @SuppressWarnings("rawtypes")
    protected void initBeanVOBuidlers() {
        // initialize BeanVOBuilders.
        // 默认的 BeanVOBuilder getId 为null, 有注解@Primary, 只应该存在一个.
        ApplicationContext context = getApplicationContext();
        // 不能用TreeMap,因为TreeMap的key 不能为null.
        Map<String, BeanVOBuilder<?>> viewBuilders = new HashMap<>();
        Map<String, BeanVOBuilder> candidateBuilders = context.getBeansOfType(BeanVOBuilder.class);
        for (BeanVOBuilder<?> builder : candidateBuilders.values()) {
            if (Utils.isEmpty(builder.getIds()) && viewBuilders.get(null) == null) {
                log.debug("current BeanVOBuilder: {0} should be add Annotation @Primary, set this builder default BeanVOBuilder", builder);
                viewBuilders.put(null, builder);
            } else if (Utils.isEmpty(builder.getIds()) && viewBuilders.get(null) != null) {
                log.warn("exist many default BeanVOBuilder, drop builder: {0}", builder);
            } else {
                for (String id : builder.getIds()) {
                    viewBuilders.put(id, builder);
                }
            }
        }
        setBeanVOBuilders(viewBuilders);
    }


    protected void initPropertyVOBuidler() {
        PropertyVOBuilder<?> lPropertyVOBuidler = getApplicationContext().getBean(PropertyVOBuilder.class);
        setPropertyVOBuilder(lPropertyVOBuidler);
    }


    public Object buildBeanVO(String pBeanName, String pContextId) {
        if (Utils.isBlank(pBeanName)) {
            log.warn("buildBeanVO(): params not complete. pBeanName: {0}", pBeanName);
            return Collections.emptyMap();
        }
        BeanVOBuilder<?> BeanVOBuilder = findBeanVOBuilder(pBeanName);
        log.trace("buildBeanVO(): bean: {0} use builder: {1}", pBeanName, BeanVOBuilder);
        Object beanVO = BeanVOBuilder.build(pBeanName, pContextId);
        log.debug("buildBeanVO(): beanName: {0}, beanVO: {1} ", beanVO);
        return beanVO;
    }


    public Object buildPropertyVO(String pPropName, String pBeanName, String pContextId) {
        if (Utils.isBlank(pPropName) || Utils.isBlank(pBeanName)) {
            log.warn("buildPropertyVO(): params not complete. pPropName: {0}, pBeanName: {1}", pPropName, pBeanName);
            return Collections.emptyMap();
        }
        Object propertyVO = getPropertyVOBuilder().build(pPropName, pBeanName, pContextId);
        log.debug("buildPropertyVO(): pPropName: {0}, pBeanName: {1}, propertyVO: {1} ", pPropName, pBeanName, propertyVO);
        return propertyVO;
    }


    public Map<String, Object> buildMethodInfo(String pMethodName, String pBeanName, String pContextId) {
        if (!StringUtils.hasText(pMethodName) || !StringUtils.hasText(pBeanName)) {
            log.warn("buildMethodInfo(): params not complete. methodName: {0}, beanName: {1}", pMethodName, pBeanName);
            return Collections.emptyMap();
        }
        Map<String, Object> methodInfo = new HashMap<>();
        methodInfo.put("name", pMethodName);
        methodInfo.put("beanName", pBeanName);
        methodInfo.put("invokeUrl", getMethodInvokeUrl());

        ConfigurableApplicationContext context = getContext(pContextId);
        methodInfo.put("contextId", context.getId());
        methodInfo.put("beanUrl", buildBeanUrl(pBeanName, context));

        Class<?> beanClass = getBeanClass(context, pBeanName);
        if (beanClass == null) {
            log.error("buildMethodInfo(): not found Class for beanName: {0}", pBeanName);
            return methodInfo;
        }
        methodInfo.put("beanClassName", beanClass.getName());
        return methodInfo;
    }


    public Response invokeMethod(String pMethodName, String pBeanName, String pContextId) {
        Response resp = new Response(Boolean.FALSE);
        ConfigurableApplicationContext context = getContext(pContextId);
        Object beanInstance = context.getBean(pBeanName);
        if (beanInstance == null) {
            resp.setErrorMsg(Msg.format("not found instance for bean: {0}", pBeanName));
            return resp;
        }
        try {
            Method method = ReflectionUtils.findMethod(beanInstance.getClass(), pMethodName);
            if (isCallableMethod(method)) {
                Object ret = ReflectionUtils.invokeMethod(method, beanInstance);
                resp.setSuccess(Boolean.TRUE);
                resp.setResult(ret);
            } else {
                resp.setErrorMsg(Msg.format("method: {0.name} of class: {1.class.name} is not callable", method, beanInstance));
            }
        } catch (Exception e) {
            resp.setErrorMsg(e.getMessage());
        }
        return resp;
    }


    public Response changeProperty(String pPropName, String pBeanName, String pContextId, String pNewValue) {
        Response result = new Response(Boolean.FALSE);
        ConfigurableApplicationContext context = getContext(pContextId);
        Object beanInstance = context.getBean(pBeanName);
        if (beanInstance == null) {
            result.setErrorMsg(Msg.format("not found instance for bean: {0}", pBeanName));
            return result;
        }
        try {
            propertyAccessService.setProperty(beanInstance, pPropName, pNewValue);
            result.setSuccess(Boolean.TRUE);
            //clear cache. 用事件来做.
            getApplicationContext().publishEvent(new PropertyChangeEvent(this, pPropName, pBeanName, context.getId()));
        } catch (Exception e) {
            result.setErrorMsg(e.getMessage());
        }
        return result;
    }


    List<Map<String, Object>> searchSubtype(String pClassName) {
        if (!StringUtils.hasText(pClassName)) {
            return Collections.emptyList();
        }
        Class<?> type = null;
        try {
            type = ClassUtils.forName(pClassName, getApplicationContext().getClassLoader());
        } catch (ClassNotFoundException | LinkageError e) {
            log.error(e, "searchSubtype():: {0}", pClassName);
            return Collections.emptyList();
        }
        return searchSubtype(type);
    }


    @SuppressWarnings("unchecked")
    List<Map<String, Object>> searchSubtype(Class<?> pType) {
        if (pType == null) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> subtypesInfo = null;
        String cacheKey = generateSubtypeCacheKey(pType);
        for (Iterator<ConfigurableApplicationContext> it = getContextHierarchy().iterator(); it.hasNext(); ) {
            ConfigurableApplicationContext context = it.next();
            String[] beanNames = null;
            if (pType.isAnnotation()) {
                beanNames = context.getBeanNamesForAnnotation((Class<? extends Annotation>) pType);
            } else {
                beanNames = context.getBeanNamesForType(pType);
            }
            if (ObjectUtils.isEmpty(beanNames)) {
                log.debug("not found subtype for type: {0.name} in context: {0.id}", pType, context);
                continue;
            }
            for (String beanName : beanNames) {
                Map<String, Object> beanInfo = new HashMap<>(2);
                subtypesInfo.add(beanInfo);
                beanInfo.put("beanName", beanName);
                beanInfo.put("url", buildBeanUrl(beanName, context));
            }
            // sort by execute order.
            Collections.sort(subtypesInfo, AnnotationAwareOrderComparator.INSTANCE);
        }
        return subtypesInfo;
    }


    private String generateSubtypeCacheKey(Class<?> pType) {
        return new StringBuilder().append(getClass().getSimpleName()).append("searchSubtype").append(pType.getName()).toString();
    }


    /***
     * search matched name bean.
     * @param pBeanName
     *            search name.
     * @return matched beans.
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> searchBeans(String pBeanName) {
        if (!StringUtils.hasText(pBeanName)) {
            return Collections.emptyList();
        }
        Pattern pattern = Pattern.compile(new StringBuilder().append(".*").append(pBeanName).append(".*").toString(), Pattern.CASE_INSENSITIVE);
        int count = 0, limit = getSearchBeanLimitCount();
        List<Map<String, Object>> beans = new ArrayList<>(limit);
        for (Iterator<ConfigurableApplicationContext> it = getContextHierarchy().iterator(); count < limit && it.hasNext(); ) {
            ConfigurableApplicationContext context = it.next();
            ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
            Iterator<String> beanNamesIt = beanFactory.getBeanNamesIterator();
            for (; count < limit && beanNamesIt.hasNext(); ) {
                String beanName = beanNamesIt.next();
                Matcher matcher = pattern.matcher(beanName);
                if (matcher.matches()) {
                    Map<String, Object> beanInfo = new HashMap<>(2);
                    beanInfo.put("url", buildBeanUrl(beanName, context));
                    beanInfo.put("name", beanName);
                    beans.add(beanInfo);
                    count++;
                }
            }
        }
        return beans;
    }


    public void clearCache() {
        setTypeList(null);
    }


    /**
     * find BeanVOBuilder for pBeanName.
     * if not special BeanVOBuilder for pBeanName, return default BeanVOBuilder.
     *
     * @param pBeanName bean name.
     * @return BeanVOBuilder
     */
    protected BeanVOBuilder<?> findBeanVOBuilder(String pBeanName) {
        BeanVOBuilder<?> builder = getBeanVOBuilders().get(pBeanName);
        if (builder == null) {
            // get default builder.
            builder = getBeanVOBuilders().get(null);
            log.debug("bean: {0} use default builder: {1}", pBeanName, builder);
        }
        if (builder == null) {
            throw new NullPointerException("not found any BeanVOBuilder.");
        }
        return builder;
    }


    public Class<?> getBeanClass(ConfigurableApplicationContext pContext, String pBeanName) {
        return getBeanClass(pContext, pBeanName, false);
    }


    public Class<?> getBeanClass(ConfigurableApplicationContext pContext, String pBeanName, boolean pOriginClass) {
        Class<?> beanClass = null;
        // TODO if get from BeanFactory some have problem. e.g: org.springframework.boot.autoconfigure.internalCachingMetadataReaderFactory
        Object beanInstance = getBeanInstance(pBeanName, pContext);
        if (beanInstance != null) {
            if (pOriginClass) {
                if (ClassUtils.isCglibProxy(beanInstance)) {
                    beanClass = beanInstance.getClass().getSuperclass();
                } else {
                    beanClass = AopProxyUtils.ultimateTargetClass(beanInstance);
                }
            } else {
                beanClass = beanInstance.getClass();
            }
        }
        try {
            if (beanClass == null) {
                log.debug("getBeanClass(): get from beanInstance fail. beanName: {0}", pBeanName);
                beanClass = pContext.getType(pBeanName);
                if (beanClass == null) {
                    log.warn("getBeanClass(): get from BeanFactory fail. beanName: {0}", pBeanName);
                    ConfigurableListableBeanFactory beanFactory = pContext.getBeanFactory();
                    BeanDefinition bd = beanFactory.getBeanDefinition(pBeanName);
                    // TODO BeanDefinition中的beanClass可能会为空.
                    // beanClass = ((AbstractBeanDefinition) bd).getBeanClass();
                    try {
                        beanClass = org.springframework.util.ClassUtils.forName(bd.getBeanClassName(), beanFactory.getBeanClassLoader());
                    } catch (ClassNotFoundException | LinkageError e) {
                        log.error(e, "getBeanClass(): forClassName: {0.beanClassName} error", bd);
                    }
                    // log.debug("getBeanClass(): get from BeanClassLoader fail. beanName: {0}", pBeanName);
                }
            }
        } catch (NoSuchBeanDefinitionException e) {
            log.warn("NoSuchBeanDefinitionException, beanName: {0}", pBeanName);
        } catch (Exception e) {
            log.error(e, "getBeanClass(): error!!");
        }
        return beanClass;
    }


    public List<?> detectBeanRefs(Collection<?> pCollection, int pLimit) {
        if (Utils.isEmpty(pCollection)) return null;
        if (pLimit < 0) {
            log.debug("detectBeanRefInfo(): parameter pLimit < 0, so not dectect.");
            return null;
        }
        int limit = Math.min(pLimit, pCollection.size());
        List<Object> result = new ArrayList<>(limit);
        Iterator<?> it = pCollection.iterator();
        for (int i = 0; it.hasNext() && i < limit; i++) {
            BeanRef beanRef;
            Object v = it.next();
            if (v == null) {
                beanRef = new BeanRef("null");
            } else {
                beanRef = detectBeanRef(v);
            }
            result.add(beanRef);
        }
        if (pCollection.size() > pLimit) {
            BeanRef beanRef = new BeanRef("Size: " + pCollection.size() + " ......");
            result.add(beanRef);
        }
        return result;
    }


    public List<?> detectBeanRefs(Object[] pArrayValue, int pLimit) {
        if (Utils.isEmpty(pArrayValue)) return null;
        if (pLimit < 0) {
            log.debug("detectBeanRefInfo(): parameter pLimit < 0, so not dectect.");
            return null;
        }
        int limit = Math.min(pLimit, pArrayValue.length);
        List<Object> result = new ArrayList<>(limit);
        for (int i = 0; i < limit; i++) {
            BeanRef beanRef = null;
            if (pArrayValue[i] == null) {
                beanRef = new BeanRef("null");
            } else {
                beanRef = detectBeanRef(pArrayValue[i]);
            }
            result.add(beanRef);
        }
        if (pArrayValue.length > pLimit) {
            BeanRef beanRef = new BeanRef("Length: " + pArrayValue.length + " ......");
            result.add(beanRef);
        }
        return result;
    }


    public List<?> detectBeanRefs(Map<Object, Object> pMapValue, int pLimit) {
        if (Utils.isEmpty(pMapValue)) return null;
        if (pLimit < 0) {
            log.debug("detectBeanRefInfo(): parameter pLimit < 0, so not dectect.");
            return null;
        }
        int limit = Math.min(pLimit, pMapValue.size());
        List<Object> result = new ArrayList<>(limit + 1);
        // map 的key和value 都可能为null.
        String nullStr = "null";
        Iterator<Entry<Object, Object>> it = pMapValue.entrySet().iterator();
        for (int i = 0; it.hasNext() && i < limit; i++) {
            Entry<Object, Object> entry = it.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            BeanRef valueBeanRef = null;
            BeanRef keyBeanRef = null;
            if (key == null && value == null) {
                valueBeanRef = new BeanRef(nullStr);
                keyBeanRef = new BeanRef(nullStr);
            } else if (key == null && value != null) {
                keyBeanRef = new BeanRef(nullStr);
                valueBeanRef = detectBeanRef(value);
            } else if (key != null && value == null) {
                keyBeanRef = detectBeanRef(key);
                valueBeanRef = new BeanRef(nullStr);
            } else if (key != null && value != null) {
                keyBeanRef = detectBeanRef(key);
                valueBeanRef = detectBeanRef(value);
            }
            result.add(new ValueWrapper.Entry(keyBeanRef, valueBeanRef));
        }
        if (pMapValue.size() > pLimit) {
            BeanRef keyBeanRef = new BeanRef("Size: " + pMapValue.size());
            BeanRef valueBeanRef = new BeanRef("......");
            result.add(new ValueWrapper.Entry(keyBeanRef, valueBeanRef));
        }
        return result;
    }


    public BeanRef detectBeanRef(Object pInstance) {
        if (pInstance == null) return null;
        BeanRef beanRef = new BeanRef();
        if (!isNeedDetectBeanInfo(pInstance.getClass())) {
            beanRef.setToString(Utils.toString(pInstance));
            return beanRef;
        }
        // TODO get from cache.
        Class<?> type = pInstance.getClass();
        boolean found = false;
        for (Iterator<ConfigurableApplicationContext> it = getContextHierarchy().iterator(); !found && it.hasNext(); ) {
            ConfigurableApplicationContext context = it.next();
            Map<String, ?> beans = context.getBeansOfType(type);
            if (Utils.isEmpty(beans)) continue;
            for (Entry<String, ?> entry : beans.entrySet()) {
                if (Objects.equals(pInstance, entry.getValue())) {
                    String beanName = entry.getKey();
                    beanRef.setBeanName(beanName);
                    beanRef.setBeanUrl(buildBeanUrl(beanName, context));
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            beanRef.setToString(pInstance.toString());
        }
        return beanRef;
    }


    protected boolean isNeedDetectBeanInfo(Class<?> pType) {
        if (pType == null
                || ClassUtils.isPrimitiveOrWrapper(pType)
                || String.class.isAssignableFrom(pType)
                || (pType.isArray() && pType.getComponentType().isPrimitive())) {
            return false;
        }
        return true;
    }


    public String getHtmlTemplate(String pTemplateName) {
        String htmlTemplate = Utils.toString(MBeanController.class.getResourceAsStream("view/resource/" + pTemplateName + ".html"));
        return htmlTemplate;
    }


    public String buildPropertyUrl(String pBeanName, String pPropName, ApplicationContext pContext) {
        StringBuilder sb = new StringBuilder()
                .append(mbeanController.getBeansUrlPrefix())
                .append("/")
                .append(pBeanName)
                .append("?").append(MBeanController.PARAME_PROPERTY_NAME).append("=").append(pPropName);
        if (null != pContext) {
            sb.append("&").append(MBeanController.PARAME_CONTEXTID).append("=").append(pContext.getId());
        }
        return sb.toString();
    }


    public String buildMethodUrl(String pBeanName, String pMethodName, ApplicationContext pContext) {
        StringBuilder sb = new StringBuilder()
                .append(mbeanController.getBeansUrlPrefix())
                .append("/")
                .append(pBeanName)
                .append("?").append(MBeanController.PARAME_METHOD_NAME).append("=").append(pMethodName);
        if (null != pContext) {
            sb.append("&").append(MBeanController.PARAME_CONTEXTID).append("=").append(pContext.getId());
        }
        return sb.toString();
    }


    public String buildBeanUrl(String pBeanName, ApplicationContext pContext) {
        StringBuilder sb = new StringBuilder()
                .append(mbeanController.getBeansUrlPrefix())
                .append("/")
                .append(pBeanName);
        if (null != pContext) {
            sb.append("?").append(MBeanController.PARAME_CONTEXTID).append("=").append(pContext.getId());
        }
        return sb.toString();
    }


    /**
     * 判断一个方法是否能在admin/beans 界面可调用.
     *
     * @param pMethod Method
     * @return true if is callable method.
     */
    public boolean isCallableMethod(Method pMethod) {
        Class<?> declaringClass = pMethod.getDeclaringClass();
        return (null != pMethod
                && pMethod.getParameterTypes().length < 1
                && Modifier.isPublic(pMethod.getModifiers())
                && !Modifier.isStatic(pMethod.getModifiers())
                && !pMethod.isBridge()
                && !Collections.class.equals(declaringClass)
                && !Set.class.equals(declaringClass)
                && !List.class.equals(declaringClass)
                && !Map.class.equals(declaringClass)
                && !Modifier.isAbstract(pMethod.getModifiers())
                && BeanUtils.findPropertyForMethod(pMethod) == null // filter getter and setter method. inner class have some problem.
                && pMethod.getAnnotation(Bean.class) == null
                && !BaseBean.class.equals(declaringClass));
    }


    /**
     * 查找 ConfigurableApplicationContext. 如果没找到就直接抛错.
     *
     * @param pContextId ApplicationContext id
     * @return ConfigurableApplicationContext
     */
    public ConfigurableApplicationContext getContext(String pContextId) {
        ConfigurableApplicationContext lContext = null;
        for (Iterator<ConfigurableApplicationContext> it = getContextHierarchy().iterator(); it.hasNext(); ) {
            ConfigurableApplicationContext tContext = it.next();
            if (Objects.equals(tContext.getId(), pContextId)) {
                lContext = tContext;
                break;
            }
        }
        if (StringUtils.hasText(pContextId) && null == lContext) {
            log.warn("context: {} is not exist.", pContextId);
        }
        if (null == lContext) {
            lContext = asConfigurableContext(getApplicationContext());
        }
        if (null == lContext) {
            NullPointerException exception = new NullPointerException("not found ApplicationContext. input cntextId: " + pContextId);
            log.error(exception.getMessage());
            throw exception;
        }
        return lContext;
    }


    public Object getBeanInstance(String pBeanName, ApplicationContext pContext) {
        try {
            return pContext.getBean(pBeanName);
        } catch (NoSuchBeanDefinitionException e) {
            log.warn("NoSuchBeanDefinitionException, beanName: {0}", pBeanName);
        } catch (Exception e) {
            log.error(e, "getBeanInstance(): error!");
        }
        return null;
    }


    public Object getBeanInstance(String pBeanName) {
        return getBeanInstance(pBeanName, getApplicationContext());
    }


    public Object getOriginBeanInstance(String pBeanName, ApplicationContext pContext) {
        Object instance = getBeanInstance(pBeanName, pContext);
        return Utils.extractOriginInstance(instance);
    }


    public Object getOriginBeanInstance(String pBeanName) {
        return getOriginBeanInstance(pBeanName, getApplicationContext());
    }


    /**
     * 得到当前 Context 的所有 关联的Context.
     *
     * @return set of ConfigurableApplicationContext
     */
    private Set<ConfigurableApplicationContext> getContextHierarchy() {
        Set<ConfigurableApplicationContext> contexts = new LinkedHashSet<ConfigurableApplicationContext>(3);
        ApplicationContext context = getApplicationContext();
        while (context != null) {
            contexts.add(asConfigurableContext(context));
            context = context.getParent();
        }
        return contexts;
    }


    /**
     * cast ApplicationContext to ConfigurableApplicationContext.
     *
     * @param applicationContext ApplicationContext
     * @return ConfigurableApplicationContext
     */
    private ConfigurableApplicationContext asConfigurableContext(ApplicationContext applicationContext) {
        Assert.isTrue(applicationContext instanceof ConfigurableApplicationContext, "'" + applicationContext + "' does not implement ConfigurableApplicationContext");
        return (ConfigurableApplicationContext) applicationContext;
    }


    /**
     * show type list.
     *
     * @return
     */
    public List<Class<?>> getTypeList() {
        if (typeList != null) {
            return typeList;
        }
        typeList = new ArrayList<Class<?>>();
        typeList.add(Controller.class);
        typeList.add(Service.class);
//        typeList.add(ApplicationContextInitializer.class);
        typeList.add(ApplicationListener.class);
        typeList.add(BeanPostProcessor.class);
        typeList.add(BeanFactoryPostProcessor.class);
        typeList.add(Configuration.class);
        typeList.add(HandlerMapping.class);
        if (!CollectionUtils.isEmpty(getAdditionalTypeList())) {
            typeList.addAll(getAdditionalTypeList());
        }
        return typeList;
    }


    /**
     * @return the searchBeanLimitCount
     */
    public Integer getSearchBeanLimitCount() {
        return searchBeanLimitCount;
    }


    /**
     * @param pAdditionalTypeList the additionalTypeList to set
     */
    public void setAdditionalTypeList(List<Class<?>> pAdditionalTypeList) {
        additionalTypeList = pAdditionalTypeList;
        setTypeList(null);
    }


    /**
     * @param pSearchBeanLimitCount the searchBeanLimitCount to set
     */
    public void setSearchBeanLimitCount(Integer pSearchBeanLimitCount) {
        searchBeanLimitCount = pSearchBeanLimitCount;
    }


    /**
     * @return the additionalTypeList
     */
    public List<Class<?>> getAdditionalTypeList() {
        return additionalTypeList;
    }


    /**
     * @param pTypeList the typeList to set
     */
    public void setTypeList(List<Class<?>> pTypeList) {
        typeList = pTypeList;
    }


    /**
     * @return the BeanVOBuilders
     */
    public Map<String, BeanVOBuilder<?>> getBeanVOBuilders() {
        if (beanVOBuilders == null) {
            synchronized (this) {
                if (beanVOBuilders == null) {
                    beanVOBuilders = new HashMap<>();
                }
            }
        }
        return beanVOBuilders;
    }


    /**
     * @param pBeanVOBuilders the BeanVOBuilders to set
     */
    public void setBeanVOBuilders(Map<String, BeanVOBuilder<?>> pBeanVOBuilders) {
        if (Utils.isEmpty(pBeanVOBuilders)) {
            beanVOBuilders = Collections.emptyMap();
        } else {
            beanVOBuilders = new HashMap<>(pBeanVOBuilders);
        }
    }


    /**
     * @return the propertyChangeUrl
     */
    public String getPropertyChangeUrl() {
        return mbeanController.getPropertyChangeUrl();
    }


    /**
     * @return the propertyChangeUrl
     */
    public String getMethodInvokeUrl() {
        return mbeanController.getMethodInvokeUrl();
    }


    /**
     * @return the PropertyVOBuilder
     */
    public PropertyVOBuilder<?> getPropertyVOBuilder() {
        return propertyVOBuilder;
    }


    /**
     * @param pPropertyVOBuilder the PropertyVOBuilder to set
     */
    public void setPropertyVOBuilder(PropertyVOBuilder<?> pPropertyVOBuilder) {
        propertyVOBuilder = pPropertyVOBuilder;
    }
}
