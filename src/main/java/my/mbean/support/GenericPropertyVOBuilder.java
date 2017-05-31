package my.mbean.support;

import my.mbean.MBeanConfiguration;
import my.mbean.service.BeansService;
import my.mbean.service.PropertyAccessService;
import my.mbean.spring.GenericService;
import my.mbean.util.Utils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.ClassUtils;

import javax.annotation.Resource;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GenericPropertyVOBuilder extends GenericService implements PropertyVOBuilder<PropertyVO> {
    /**
     * bean view page, properties view 中的属性显示的大小限制,比如数组最多显示30个.
     */
    private Integer propertyValueLimitForArray      = Integer.valueOf(100);
    private Integer propertyValueLimitForCollection = Integer.valueOf(100);
    private Integer propertyValueLimitForMap        = Integer.valueOf(70);
    @Resource
    private   BeansService          beansService;
    @Autowired
    protected PropertyAccessService propertyAccessService;
    @Resource
    private   MBeanConfiguration    mBeanConfiguration;



    @Override
    public PropertyVO build(String pPropName, String pBeanName, String pContextId) {
        PropertyVO propertyVO = null;
        try {
            propertyVO = doBuild(pPropName, pBeanName, pContextId);
        } catch (Exception e) {
            log.error(e, "propertyVOBuilder error!, propertyName: {0}, beanName: {1}, contextId: {1}", pPropName, pBeanName, pContextId);
            propertyVO = new PropertyVO();
        }
        return propertyVO;
    }


    protected PropertyVO doBuild(String pPropName, String pBeanName, String pContextId) {
        PropertyVO propertyVO = new PropertyVO();
        if (Utils.isBlank(pPropName) || Utils.isBlank(pBeanName)) {
            log.error("doBuild(): params not complete. propName: {0}, beanName: {1}", pPropName, pBeanName);
            return propertyVO;
        }
        ConfigurableApplicationContext context = beansService.getContext(pContextId);

        String propName = populateName(pPropName);
        propertyVO.setName(propName);
        log.trace("populate PropertyVO.name: {0}", propName);

        String beanName = populateBeanName(pBeanName);
        propertyVO.setBeanName(beanName);
        log.trace("populate PropertyVO.beanName: {0}", beanName);

        String changeUrl = mBeanConfiguration.getPropertyChangeUrl();
        propertyVO.setChangeUrl(changeUrl);
        log.trace("populate PropertyVO.changeUrl: {0}", changeUrl);

        String contextId = populateContextId(context);
        propertyVO.setContextId(contextId);
        log.trace("populate PropertyVO.contextId: {0}", contextId);

        String beanUrl = populateBeanUrl(pBeanName, context);
        propertyVO.setBeanUrl(beanUrl);
        log.trace("populate PropertyVO.beanUrl: {0}", beanUrl);

        String beanClassName = populateBeanClass(pBeanName, context);
        propertyVO.setBeanClass(beanClassName);
        log.trace("populate PropertyVO.beanClass: {0}", beanClassName);

        String type = populateType(pPropName, pBeanName, context);
        propertyVO.setType(type);
        log.trace("populate PropertyVO.type: {0}", type);

        boolean writable = populateWritable(pPropName, pBeanName, context);
        propertyVO.setWritable(writable);
        log.trace("populate PropertyVO.writable: {0}", writable);

        Object value = populateValue(pPropName, pBeanName, context);
        propertyVO.setValue(value);
        log.trace("populate PropertyVO.value: {0}", value);

        PropertySource source = populatePropertySource(pPropName, pBeanName, context);
        propertyVO.setSource(source);
        log.trace("populate PropertyVO.source: {0}", source);

        Map<String, Object> extra = buildExtraAttrs(pBeanName, context);
        propertyVO.setExtra(extra);
        log.trace("populate PropertyVO.extra: {0}", extra);
        return propertyVO;
    }


    protected PropertySource populatePropertySource(String pPropName, String pBeanName, ConfigurableApplicationContext pContext) {
        Class<?> beanClass = beansService.getBeanClass(pContext, pBeanName);
        if (beanClass == null) {
            log.error("populatePropertySource(): not retrieve out class for beanName: {0}", pPropName);
            return null;
        }
        PropertySource propertySource = null;
        PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(beanClass, pPropName);
        if (pd == null) {
            List<Field> fields = Utils.getAllFieldsList(beanClass);
            for (Field field : fields) {
                // use field's name use for property name.
                String propName = field.getName();
                if (Objects.equals(propName, pPropName)) {
                    propertySource = PropertySource.FIELD;
                    break;
                }
            }
        } else {
            propertySource = PropertySource.STANDARD;
        }
        return propertySource;
    }


    protected Object populateValue(String pPropName, String pBeanName, ConfigurableApplicationContext pContext) {
        Class<?> beanClass = beansService.getBeanClass(pContext, pBeanName);
        if (beanClass == null) {
            log.error("populateValue(): not retrieve out class for beanName: {0}", pPropName);
            return null;
        }
        Object beanInstance = beansService.getBeanInstance(pBeanName);
        if (beanInstance == null) {
            log.warn("populateValue(): pBeanName: {0} not found instance", pBeanName);
            return null;
        }
        Object propInstance = null;
        PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(beanClass, pPropName);
        if (pd == null || pd.getReadMethod() == null) {
            List<Field> fields = Utils.getAllFieldsList(beanClass);
            for (Field field : fields) {
                // use field's name use for property name.
                String propName = field.getName();
                if (Objects.equals(propName, pPropName)) {
                    propInstance = Utils.getValue(field, beanInstance);
                    // 如果是代理对的话 这里是得不到field的值的. 所以要得到Origin Instance.
                    if (propInstance == null && AopUtils.isAopProxy(beanInstance)) {
                        propInstance = Utils.getValue(field, Utils.extractOriginInstance(beanInstance));
                    }
                    break;
                }
            }
        } else {
            propInstance = propertyAccessService.getProperty(pPropName, beanInstance);
        }
        if (propInstance == null) {
            return null;
        }
        return populateValueWrapper(propInstance);
    }


    @SuppressWarnings("unchecked")
    private ValueWrapper populateValueWrapper(Object pPropInstance) {
        ValueWrapper valueWrapper = new ValueWrapper();
        Class<? extends Object> propType = pPropInstance.getClass();
        if (ClassUtils.isPrimitiveOrWrapper(propType) || String.class.isAssignableFrom(propType)) {
            valueWrapper.setOriginValue(pPropInstance);
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

        if (propType.isArray()) {
            List<?> beanRefs = beansService.detectBeanRefs((Object[]) pPropInstance, getPropertyValueLimitForArray());
            if (Utils.isNotEmpty(beanRefs)) {
                valueWrapper.setValue(beanRefs);
                valueWrapper.setShowType(ValueWrapper.VALUE_SHOW_TYPE_LIST);
                return valueWrapper;
            }
        }
        if (Collection.class.isAssignableFrom(propType)) {
            Collection<?> beanRefs = beansService.detectBeanRefs((Collection<?>) pPropInstance, getPropertyValueLimitForCollection());
            if (Utils.isNotEmpty(beanRefs)) {
                valueWrapper.setValue(beanRefs);
                valueWrapper.setShowType(ValueWrapper.VALUE_SHOW_TYPE_LIST);
                return valueWrapper;
            }
        }
        if (Map.class.isAssignableFrom(propType)) {
            List<?> beanRefs = beansService.detectBeanRefs((Map<Object, Object>) pPropInstance, getPropertyValueLimitForMap());
            if (Utils.isNotEmpty(beanRefs)) {
                valueWrapper.setValue(beanRefs);
                valueWrapper.setShowType(ValueWrapper.VALUE_SHOW_TYPE_MAP);
                return valueWrapper;
            }
        }
        valueWrapper.setToString(beanRef.getToString());
        return valueWrapper;
    }


    protected String populateBeanUrl(String pBeanName, ConfigurableApplicationContext pContext) {
        return beansService.buildBeanUrl(pBeanName, pContext);
    }


    protected boolean populateWritable(String pPropName, String pBeanName, ConfigurableApplicationContext pContext) {
        Class<?> beanClass = beansService.getBeanClass(pContext, pBeanName);
        if (beanClass == null) {
            log.error("populateWritable(): not retrieve out class for beanName: {0}", pPropName);
            return false;
        }
        PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(beanClass, pPropName);
        if (pd == null) {
            log.debug("populateWritable(): not found PropertyDescriptor, propName: {0} in class: {1.name}", pPropName, beanClass);
            return false;
        }
        return (pd.getWriteMethod() != null);
    }


    protected String populateType(String pPropName, String pBeanName, ConfigurableApplicationContext pContext) {
        Class<?> beanClass = beansService.getBeanClass(pContext, pBeanName);
        if (beanClass == null) {
            log.error("populateType(): not retrieve out class for beanName: {0}", pPropName);
            return null;
        }
        PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(beanClass, pPropName);
        Class<?> propType = null;
        if (pd == null) {
            log.debug("populateType(): not found PropertyDescriptor, propName: {0} in class: {1.name}", pPropName, beanClass);
            if (pd == null) {
                List<Field> fields = Utils.getAllFieldsList(beanClass);
                for (Field field : fields) {
                    // use field's name use for property name.
                    String propName = field.getName();
                    if (Objects.equals(propName, pPropName)) {
                        propType = field.getType();
                        break;
                    }
                }
            }
        } else {
            propType = pd.getPropertyType();
        }
        return propType.getName();
    }


    protected String populateName(String pPropName) {
        return pPropName;
    }


    protected String populateBeanName(String pBeanName) {
        return pBeanName;
    }


    protected String populateContextId(ApplicationContext pContext) {
        return pContext.getId();
    }


    protected String populateBeanClass(String pBeanName, ConfigurableApplicationContext pContext) {
        Class<?> beanClass = beansService.getBeanClass(pContext, pBeanName);
        if (beanClass == null) {
            log.error("populateBeanClass(): can not retrive class for bean: {0}", pBeanName);
            return null;
        }
        return beanClass.getName();
    }


    protected Map<String, Object> buildExtraAttrs(String pBeanName, ConfigurableApplicationContext pContext) {
        // TODO Auto-generated method stub
        return null;
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
}
