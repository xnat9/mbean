package my.mbean.support;

import my.mbean.service.BeansService;
import my.mbean.service.PropertyAccessService;
import my.mbean.spring.BaseBean;
import my.mbean.util.Utils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Generic BeanVOBuilder.
 *
 * @author hubert
 */
public class GenericBeanVOBuilder extends BeanVOBuilder<BeanVO> {
    final Comparator<PropertyInfo> PROPERTY_COMPARATOR = new Comparator<PropertyInfo>() {
        @Override
        public int compare(PropertyInfo pO1, PropertyInfo pO2) {
            return pO1.getName().compareTo(pO2.getName());
        }
    };
    final Comparator<String> ANNOTATION_COMPARATOR = new Comparator<String>() {
        @Override
        public int compare(String pO1, String pO2) {
            return pO1.length() - pO2.length();
        }
    };

    /**
     * bean view page, properties view 中的属性显示的大小限制,比如数组最多显示30个.
     */
    private Integer propertyValueLimitForArray = Integer.valueOf(30);
    private Integer propertyValueLimitForCollection = Integer.valueOf(30);
    private Integer propertyValueLimitForMap = Integer.valueOf(30);
    /**
     * String format: beanName.propertyName
     */
    private List<String> ignoreProperties;
    @Autowired
    protected BeansService beansService;
    @Autowired
    protected PropertyAccessService propertyAccessService;


    @Override
    public BeanVO build(String pBeanName, String pContextId) {
        BeanVO beanView = null;
        try {
            beanView = doBuild(pBeanName, pContextId);
        } catch (Exception e) {
            log.error(e, "beanViewBuilder: {0} error!, beanName: {1}, contextId: {2}", this, pBeanName, pContextId);
            beanView = new BeanVO();
        }
        return beanView;
    }


    protected BeanVO doBuild(String pBeanName, String pContextId) {
        BeanVO beanVO = new BeanVO();
        if (Utils.isBlank(pBeanName)) {
            log.warn("doBuild(): param beanName is empty", pBeanName);
            return beanVO;
        }

        Object commonVO = buildCommonView(pBeanName, pContextId);
        BeanUtils.copyProperties(commonVO, beanVO);

        Object propertyVO = buildPropertiesView(pBeanName, pContextId);
        beanVO.setProperties(propertyVO);
        log.trace("populate BeanVO.properties: {0}", propertyVO);

        Object methodVO = buildMethodsView(pBeanName, pContextId);
        beanVO.setMethods(methodVO);
        log.trace("populate BeanVO.methods: {0}", methodVO);

        Map<String, Object> extra = buildExtraAttrs(pBeanName, pContextId);
        beanVO.setExtra(extra);
        log.trace("populate BeanVO.extra: {0}", extra);
        return beanVO;
    }


    @Override
    public Object buildCommonView(String pBeanName, String pContextId) {
        BeanVO beanView = new BeanVO();
        String beanName = populateBeanName(pBeanName);
        beanView.setBeanName(beanName);
        log.trace("populate BeanVO.beanName: {0}", beanName);

        ConfigurableApplicationContext context = beansService.getContext(pContextId);
        String contextId = populateContextId(context);
        beanView.setContextId(contextId);
        log.trace("populate BeanVO.contextId: {0}", contextId);

        String url = populateUrl(pBeanName, context);
        beanView.setUrl(url);
        log.trace("populate BeanVO.url: {0}", url);

        String beanClassName = populateBeanClass(pBeanName, context);
        beanView.setBeanClass(beanClassName);
        log.trace("populate BeanVO.beanClass: {0}", beanClassName);

        String beanValueStr = populateBeanValue(pBeanName, context);
        beanView.setBeanValue(beanValueStr);
        log.trace("populate BeanVO.beanValue: {0}", beanValueStr);
        return beanView;
    }


    @Override
    public Object buildPropertiesView(String pBeanName, String pContextId) {
        if (Utils.isBlank(pBeanName)) {
            log.warn("buildPropertiesView(): parameter pBeanName is empty.");
            return null;
        }
        ConfigurableApplicationContext context = beansService.getContext(pContextId);
        //TODO get from cache.
        List<PropertyInfo> properties = null;
        properties = buildProperties(pBeanName, context);
        return properties;
    }


    @Override
    public Object buildMethodsView(String pBeanName, String pContextId) {
        if (Utils.isBlank(pBeanName)) {
            log.warn("buildMethodsView(): parameter pBeanName is empty.");
            return null;
        }
        ConfigurableApplicationContext context = beansService.getContext(pContextId);
        //get from cache.
        List<MethodInfo> methods = null;
        methods = buildMethods(pBeanName, context);
        return methods;
    }


    protected String populateBeanName(String pBeanName) {
        return pBeanName;
    }


    protected String populateContextId(ApplicationContext pContext) {
        return pContext.getId();
    }


    /**
     * populate bean url. can use browser browse.
     *
     * @param pBeanName bean name.
     * @param pContext  ApplicationContext
     * @return bean url.
     */
    protected String populateUrl(String pBeanName, ApplicationContext pContext) {
        return beansService.buildBeanUrl(pBeanName, pContext);
    }


    protected String populateBeanClass(String pBeanName, ConfigurableApplicationContext pContext) {
        Class<?> beanClass = beansService.getBeanClass(pContext, pBeanName);
        if (beanClass == null) {
            log.error("populateBeanClass(): can not retrive class for bean: {0}", pBeanName);
            return null;
        }
        return beanClass.getName();
    }


    protected String populateBeanValue(String pBeanName, ApplicationContext pContext) {
        Object beanInstance = beansService.getBeanInstance(pBeanName, pContext);
        log.trace("populateBeanValue(): beanName: {0} instance: {1}", pBeanName, beanInstance);
        return stringValueOfBean(pBeanName, beanInstance);
    }


    /**
     * @param pBeanName
     * @param pContext
     * @return
     * @Cacheable 这里不支持 非public 的method.
     * AbstractFallbackCacheOperationSource.computeCacheOperations(Method, Class<?>)
     */
    // @Cacheable(cacheNames="beanViewBuilder.default", key="#pBeanName.concat(#pContext.getId())")
    protected List<PropertyInfo> buildProperties(String pBeanName, ConfigurableApplicationContext pContext) {
        Class<?> beanClass = beansService.getBeanClass(pContext, pBeanName);
        if (beanClass == null) {
            log.warn("buildProperties(): not retrieve class for bean: {0}", pBeanName);
            return null;
        }
        List<PropertyInfo> properties = new ArrayList<>();
        // property from standard properties(javabean).
        PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(beanClass);
        for (PropertyDescriptor pd : pds) {
            if (isIgnoreProperty(pBeanName, pd, properties)) {
                continue;
            }
            String propName = pd.getName();
            PropertyInfo propertyInfo = new PropertyInfo();
            properties.add(propertyInfo);

            Object value = null;
            try {
                value = populatePropertyValue(pBeanName, pd, pContext);
            } catch (Exception e) {
                log.error(e, "populate standard property: {0} value error!", propName);
            }
            List<String> annotationMarks = popluateAnnotationMarks(pBeanName, pd, pContext);
            propertyInfo.setName(propName);
            propertyInfo.setSource(PropertySource.STANDARD);
            propertyInfo.setUrl(beansService.buildPropertyUrl(pBeanName, propName, pContext));
            propertyInfo.setType(pd.getPropertyType().getName());
            propertyInfo.setValue(value);
            propertyInfo.setAnnotationMarks(annotationMarks);
        }
        // property from field.
        List<Field> fields = Utils.getAllFieldsList(beanClass);
        for (Field field : fields) {
            // use field's name use for property name.
            String propName = field.getName();
            if (!isVisibleField(pBeanName, field) && isIgnoreField(pBeanName, field, properties)) {
                continue;
            }
            PropertyInfo propertyInfo = new PropertyInfo();
            properties.add(propertyInfo);

            Object value = null;
            try {
                value = populatePropertyValue(pBeanName, field, pContext);
            } catch (Exception e) {
                log.error(e, "populate field property: {0} value error!", propName);
            }
            List<String> annotationMarks = popluateAnnotationMarks(pBeanName, field, pContext);
            propertyInfo.setName(propName);
            propertyInfo.setSource(PropertySource.FIELD);
            propertyInfo.setUrl(beansService.buildPropertyUrl(pBeanName, propName, pContext));
            propertyInfo.setType(field.getType().getName());
            propertyInfo.setValue(value);
            propertyInfo.setAnnotationMarks(annotationMarks);
        }
        // TODO property from other. 
        // 比如自定义属性,可以用注解来做 PropertyInfoGeneratorMethod. 也可以用getter or setter.
        // sorted by property name.
        Collections.sort(properties, PROPERTY_COMPARATOR);
        return (CollectionUtils.isEmpty(properties) ? null : properties);
    }


    protected Map<String, Object> buildExtraAttrs(String pBeanName, String pContextId) {
        // TODO Auto-generated method stub
        return null;
    }


    protected List<MethodInfo> buildMethods(String pBeanName, ConfigurableApplicationContext pContext) {
        Method[] candidateMethods = findMethods(pBeanName, pContext);
        if (Utils.isEmpty(candidateMethods)) {
            log.debug("buildMethods(): no candidateMethods, beanName: {0}, context: {1.id}", pBeanName, pContext);
            return null;
        }
        List<MethodInfo> methods = new ArrayList<>(candidateMethods.length);
        for (Method method : candidateMethods) {
            if (isInvisibleMethod(method, methods)) {
                continue;
            }
            String name = method.getName();
            MethodInfo methodInfo = new MethodInfo();
            methods.add(methodInfo);
            methodInfo.setName(name);
            methodInfo.setUrl(beansService.buildMethodUrl(pBeanName, name, pContext));
            methodInfo.setReturnType(method.getReturnType().getName());
            methodInfo.setDeclaringClass(method.getDeclaringClass().getName());
            methodInfo.setCallable(beansService.isCallableMethod(method));
            methodInfo.setAnnotationMarks(populateAnnotationMarks(method));
        }
        return (CollectionUtils.isEmpty(methods) ? null : methods);
    }


    protected Method[] findMethods(String pBeanName, ConfigurableApplicationContext pContext) {
        Class<?> beanClass = beansService.getBeanClass(pContext, pBeanName, false);
        if (beanClass == null) {
            log.warn("findMethods(): not retrieve class for bean: {0}", pBeanName);
            return null;
        }
        return ReflectionUtils.getUniqueDeclaredMethods(beanClass);
    }


    protected List<String> popluateAnnotationMarks(String pBeanName, Field pField, ConfigurableApplicationContext pContext) {
        Annotation[] annotations = pField.getDeclaredAnnotations();
        List<String> annotationMarks = new ArrayList<>(annotations.length);
        for (Annotation anno : annotations) {
            if (isIgnoreAnnotation(anno)) continue;
            // AnnotationUtils.getAnnotationAttributes(anno)
            annotationMarks.add("@" + anno.annotationType().getSimpleName());
        }
        if (Utils.isEmpty(annotationMarks)) return null;
        Collections.sort(annotationMarks, ANNOTATION_COMPARATOR);
        return annotationMarks;
    }


    protected List<String> popluateAnnotationMarks(String pBeanName, PropertyDescriptor pPropertyDescriptor, ConfigurableApplicationContext pContext) {
        Annotation[] annotations = pPropertyDescriptor.getReadMethod().getAnnotations();
        List<String> annotationMarks = new ArrayList<>(annotations.length);
        for (Annotation anno : annotations) {
            if (isIgnoreAnnotation(anno)) continue;
            annotationMarks.add("@" + anno.annotationType().getSimpleName());
        }
        if (Utils.isEmpty(annotationMarks)) return null;
        Collections.sort(annotationMarks, ANNOTATION_COMPARATOR);
        return annotationMarks;
    }


    protected List<String> populateAnnotationMarks(Method pMethod) {
        Annotation[] annotations = pMethod.getAnnotations();
        List<String> annotationMarks = null;
        if (!ObjectUtils.isEmpty(annotations)) {
            annotationMarks = new ArrayList<>(annotations.length);
            for (Annotation anno : annotations) {
                if (isIgnoreAnnotation(anno)) continue;
                annotationMarks.add("@" + anno.annotationType().getSimpleName());
            }
            Collections.sort(annotationMarks, ANNOTATION_COMPARATOR);
        }
        return annotationMarks;
    }


    /**
     * format bean instance value toString.
     *
     * @param pBean bean instance.
     * @return bean value.
     */
    protected String stringValueOfBean(String pBeanName, Object pBean) {
        return toStringForBean(pBean, true);
    }


    /**
     * use Object.toString value of a beanInstance or self to String.
     *
     * @param pBean              bean instance.
     * @param pUseObjectToString is or not use Object.toString.
     * @return bean value of String.
     */
    protected String toStringForBean(Object pBean, boolean pUseObjectToString) {
        if (pUseObjectToString) {
            return useObjectToString(pBean);
        } else {
            return Objects.toString(pBean, null);
        }
    }


    private String useObjectToString(Object pBean) {
        if (pBean == null) return "null";
        return new StringBuilder().append(pBean.getClass().getName())
                .append("@")
                .append(Integer.toHexString(pBean.hashCode())).toString();
    }


    protected boolean isIgnoreAnnotation(Annotation pAnno) {
        return Override.class.equals(pAnno)
                || SuppressWarnings.class.equals(pAnno);
    }


    /**
     * 可以用PropertyValue 也可以用其它的对象表示 属性的值.比如 map.
     *
     * @param pBeanName           bean name.
     * @param pPropertyDescriptor PropertyDescriptor
     * @param pContext            ConfigurableApplicationContext.
     * @return property value.
     */
    protected Object populatePropertyValue(String pBeanName, PropertyDescriptor pPropertyDescriptor, ConfigurableApplicationContext pContext) {
        Object beanInstance = pContext.getBean(pBeanName);
        Object propInstance = propertyAccessService.getProperty(pPropertyDescriptor.getName(), beanInstance);
        if (propInstance == null) return null;
        ValueWrapper valueWrapper = populateValueWrapper(propInstance);
        return valueWrapper;
    }


    protected Object populatePropertyValue(String pBeanName, Field pField, ConfigurableApplicationContext pContext) {
        Object beanInstance = beansService.getBeanInstance(pBeanName, pContext);
        Object propInstance = Utils.getValue(pField, beanInstance);
        // 如果是代理对的话 这里是得不到field的值的. 所以要得到Origin Instance.
        if (propInstance == null && AopUtils.isAopProxy(beanInstance)) {
            propInstance = Utils.getValue(pField, Utils.extractOriginInstance(beanInstance));
        }
        if (propInstance == null) {
            return null;
        }
        ValueWrapper valueWrapper = populateValueWrapper(propInstance);
        return valueWrapper;
    }


    /**
     * 解析一个对象实例, 再包装成ValueWrapper.
     *
     * @param pPropInstance 属性的实例对象
     * @return ValueWrapper
     */
    private ValueWrapper populateValueWrapper(Object pPropInstance) {
        ValueWrapper valueWrapper = new ValueWrapper();
        Class<? extends Object> propType = pPropInstance.getClass();
        if (ClassUtils.isPrimitiveOrWrapper(propType) || String.class.isAssignableFrom(propType)) {
            valueWrapper.setOriginValue(pPropInstance);
            valueWrapper.setType(ValueWrapper.VALUE_TYPE_SIMPLE);
            return valueWrapper;
        }
        // Class也可能是个bean?.
        if ( Class.class.isAssignableFrom(propType)) {
            valueWrapper.setToString(Utils.toString(pPropInstance));
            return valueWrapper;
        }

        // 先探测对象是否为一个bean reference 对象(指向一个容器中的bean对象)
        ValueWrapper.BeanRef beanRef = beansService.detectBeanRef(pPropInstance);
        if (beanRef.isRef()) {
            valueWrapper.setBeanRef(beanRef);
            return valueWrapper; // 如果是一个bean reference 就直接返回.
        }

        // 如果对象是一个数组
        if (propType.isArray() && !propType.getComponentType().isPrimitive()) {
            List<?> beanRefs = beansService.detectBeanRefs((Object[]) pPropInstance, getPropertyValueLimitForArray());
            if (Utils.isNotEmpty(beanRefs)) {
                valueWrapper.setValue(beanRefs);
                valueWrapper.setType(ValueWrapper.VALUE_TYPE_ARRAY);
                return valueWrapper;
            }
        } else if (Collection.class.isAssignableFrom(propType)) { // 如果对象是一个集合类型
            List<?> beanRefs = beansService.detectBeanRefs((Collection<?>) pPropInstance, getPropertyValueLimitForCollection());
            if (Utils.isNotEmpty(beanRefs)) {
                valueWrapper.setValue(beanRefs);
                valueWrapper.setType(ValueWrapper.VALUE_TYPE_LIST);
                return valueWrapper;
            }
        } else if (Map.class.isAssignableFrom(propType)) { // 如果对象是一个Map类型.
            List<?> beanRefs = beansService.detectBeanRefs((Map<Object, Object>) pPropInstance, getPropertyValueLimitForMap());
            if (Utils.isNotEmpty(beanRefs)) {
                valueWrapper.setValue(beanRefs);
                valueWrapper.setType(ValueWrapper.VALUE_TYPE_MAP);
                return valueWrapper;
            }
        }
        valueWrapper.setToString(beanRef.getToString());
        return valueWrapper;
    }


    protected boolean isInvisibleMethod(Method pMethod, List<MethodInfo> pMethods) {
        Class<?> declaringClass = pMethod.getDeclaringClass();
        return (Object.class.equals(declaringClass)
                || Modifier.isStatic(pMethod.getModifiers())
                || pMethod.isBridge()
                || BaseBean.class.equals(declaringClass)
                || Collections.class.equals(declaringClass)
                || Set.class.equals(declaringClass)
                || List.class.equals(declaringClass)
                || Map.class.equals(declaringClass)
                || Modifier.isAbstract(pMethod.getModifiers())
                || BeanUtils.findPropertyForMethod(pMethod) != null // filter getter and setter method. inner class have some problem.
                || pMethod.getParameterCount() > 0
                || !Modifier.isPublic(pMethod.getModifiers())
        ) && (pMethod.getAnnotation(Bean.class) == null); // TODO 目前没什么用, @Bean的方法都是被代理了的 得到的是代理对象的方法 所以也就没有 @bean注解了;
    }


    protected boolean isVisibleField(String pBeanName, Field pField) {
        // TODO Auto-generated method stub
        return false;
    }


    protected boolean isIgnoreField(String pBeanName, Field pField, List<PropertyInfo> pProperties) {
        boolean ignore = Modifier.isStatic(pField.getModifiers())
                || Modifier.isAbstract(pField.getModifiers())
                || BaseBean.class.equals(pField.getDeclaringClass())
                || isCustomIgnoreProperty(pBeanName, pField.getName());
        if (!ignore) {
            for (PropertyInfo propInfo : pProperties) {
                if (Objects.equals(pField.getName(), propInfo.getName())) {
                    ignore = true;
                    break;
                }
            }
        }
        return ignore;
    }


    protected boolean isIgnoreProperty(String pBeanName, PropertyDescriptor pPropDesc, List<PropertyInfo> pProperties) {
        return pPropDesc.getReadMethod() == null
                || isCustomIgnoreProperty(pBeanName, pPropDesc.getName());
    }


    /**
     * 自定义 忽略 属性 格式: beanName.propName
     *
     * @param pBeanName bean name.
     * @param pPropName property name.
     * @return true if is custom ignore property.
     */
    private boolean isCustomIgnoreProperty(String pBeanName, String pPropName) {
        if (Utils.isEmpty(getIgnoreProperties())) {
            return false;
        }
        for (String el : getIgnoreProperties()) {
            // 可能参考文件后缀怎么取.
            int i = el.lastIndexOf(".");
            String beanName = el.substring(0, i);
            if (!Objects.equals(beanName, pBeanName)) continue;
            String propName = el.substring(i + 1);
            if (Objects.equals(propName, pPropName)) return true;
        }
        return false;
    }


    /**
     * @return the propertyValueLimitForArray
     */
    public Integer getPropertyValueLimitForArray() {
        return propertyValueLimitForArray;
    }


    /**
     * @return the propertyValueLimitForCollection
     */
    public Integer getPropertyValueLimitForCollection() {
        return propertyValueLimitForCollection;
    }


    /**
     * @return the propertyValueLimitForMap
     */
    public Integer getPropertyValueLimitForMap() {
        return propertyValueLimitForMap;
    }


    /**
     * @param pPropertyValueLimitForArray the propertyValueLimitForArray to set
     */
    public void setPropertyValueLimitForArray(Integer pPropertyValueLimitForArray) {
        propertyValueLimitForArray = pPropertyValueLimitForArray;
    }


    /**
     * @param pPropertyValueLimitForCollection the propertyValueLimitForCollection to set
     */
    public void setPropertyValueLimitForCollection(Integer pPropertyValueLimitForCollection) {
        propertyValueLimitForCollection = pPropertyValueLimitForCollection;
    }


    /**
     * @param pPropertyValueLimitForMap the propertyValueLimitForMap to set
     */
    public void setPropertyValueLimitForMap(Integer pPropertyValueLimitForMap) {
        propertyValueLimitForMap = pPropertyValueLimitForMap;
    }


    /**
     * @return the ignoreProperties
     */
    public List<String> getIgnoreProperties() {
        return ignoreProperties;
    }


    /**
     * @param pIgnoreProperties the ignoreProperties to set
     */
    public void setIgnoreProperties(List<String> pIgnoreProperties) {
        ignoreProperties = pIgnoreProperties;
    }

}
