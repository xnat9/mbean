package my.mbean.service;

import my.mbean.spring.GenericService;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyEditorRegistrySupport;
import org.springframework.beans.propertyeditors.*;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.io.support.ResourceArrayPropertyEditor;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.xml.sax.InputSource;

import javax.annotation.Resource;
import java.beans.PropertyEditor;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 提供常用的用属性操作.比如: 从一个对象中拿一个属性,为一个对象set一个值,等等.
 * @author hubert
 */
@Service
public class PropertyAccessService extends GenericService {
    private static Class<?> zoneIdClass;

    static {
        ClassLoader cl = PropertyEditorRegistrySupport.class.getClassLoader();
        try {
            zoneIdClass = ClassUtils.forName("java.time.ZoneId", cl);
        } catch (ClassNotFoundException ex) {
            // Java 8 ZoneId class not available
            zoneIdClass = null;
        }
    }

    @Override
    protected void doStartService() {
        super.doStartService();
        setServiceInfo("instead PropertyAccessorFactory#forBeanPropertyAccess. can reuse default editors.");
    }



    /**
     * 为一个对象设置一个值.
     * @param pTarget
     *            被设置的对象.
     * @param pPropName
     *            需要设置的属性名.
     * @param pValue
     *            新的值.
     */
    public void setProperty(Object pTarget, String pPropName, Object pValue) {
        forBeanPropertyAccessor(pTarget).setPropertyValue(pPropName, pValue);
    }



    public boolean setPropertyQuiet(Object pTarget, String pPropName, Object pValue) {
        try {
            forBeanPropertyAccessor(pTarget).setPropertyValue(pPropName, pValue);
        } catch (Exception e) {
            log.warn(e, "setPropertyQuiet(): error!");
            return false;
        }
        return true;
    }



    public Object getProperty(String pPropName, Object pTarget) {
        Object value = null;
        try {
            value = forBeanPropertyAccessor(pTarget).getPropertyValue(pPropName);
        } catch (Exception e) {
            log.warn(e, "getPropertyValue(): error!");
        }
        return value;
    }



    @SuppressWarnings("unchecked")
    public <T> T getProperty(String pPropName, Object pTarget, Class<T> pExpectType) {
        return (T) getProperty(pPropName, pTarget);
    }



    /**
     * 机制的调用顺序是 CustomEditor,ConversionService,DefaultEditor. see:
     * TypeConverterDelegate.convertIfNecessary
     * @param pTarget
     *            Object.
     * @return PropertyAccessor
     */
    protected PropertyAccessor forBeanPropertyAccessor(Object pTarget) {
        BeanWrapperImpl beanWrapper = new BeanWrapperImpl(pTarget) {
            @Override
            public PropertyEditor getDefaultEditor(Class<?> pRequiredType) {
                //use BeanFactory's PropertyEditors.
//                ((ConfigurableBeanFactory) getApplicationContext().getAutowireCapableBeanFactory()).copyRegisteredEditorsTo(this);
//                return super.getDefaultEditor(pRequiredType);
                return createPropertyEditor(pRequiredType);
            }
        };
        ConversionService conversionService = ((ConfigurableApplicationContext) getApplicationContext()).getBeanFactory().getConversionService();
        beanWrapper.setConversionService(conversionService);
        return beanWrapper;
    }



    protected PropertyEditor createPropertyEditor(Class<?> pType) {
        log.trace("createPropertyEditor(): for type: {0.name}", pType);
        if (boolean.class.equals(pType)) {
            return new CustomBooleanEditor(false);
        } else if (Boolean.class.equals(pType)) {
            return new CustomBooleanEditor(true);
        } else if (int.class.equals(pType)) {
            return new CustomNumberEditor(Integer.class, false);
        } else if (Integer.class.equals(pType)) {
            return new CustomNumberEditor(Integer.class, true);
        } else if (Class.class.equals(pType)) {
            return new ClassEditor();
        } else if (HashMap.class.equals(pType)) {
            return new CustomMapEditor(HashMap.class);
        } else if (LinkedHashMap.class.equals(pType)) {
            return new CustomMapEditor(LinkedHashMap.class);
        } else if (Set.class.equals(pType)) {
            return new CustomCollectionEditor(Set.class);
        } else if (List.class.equals(pType)) {
            return new CustomCollectionEditor(List.class);
        } else if (long.class.equals(pType)) {
            return new CustomNumberEditor(Long.class, false);
        } else if (Long.class.equals(pType)) {
            return new CustomNumberEditor(Long.class, true);
        } else if (double.class.equals(pType)) {
            return new CustomNumberEditor(Double.class, false);
        } else if (Double.class.equals(pType)) {
            return new CustomNumberEditor(Double.class, true);
        } else if (String[].class.equals(pType)) {
            return new StringArrayPropertyEditor();
        } else if (Class[].class.equals(pType)) {
            return new ClassArrayEditor();
        } else if (SortedMap.class.equals(pType)) {
            return new CustomMapEditor(SortedMap.class);
        } else if (URI.class.equals(pType)) {
            return new URIEditor();
        } else if (URL.class.equals(pType)) {
            return new URLEditor();
        } else if (Pattern.class.equals(pType)) {
            return new PatternEditor();
        } else if (Properties.class.equals(pType)) {
            return new PropertiesEditor();
        } else if (SortedSet.class.equals(pType)) {
            return new CustomCollectionEditor(SortedSet.class);
        } else if (float.class.equals(pType)) {
            return new CustomNumberEditor(Float.class, false);
        } else if (Float.class.equals(pType)) {
            return new CustomNumberEditor(Float.class, true);
        } else if (Collection.class.equals(pType)) {
            return new CustomCollectionEditor(Collection.class);
        } else if (BigInteger.class.equals(pType)) {
            return new CustomNumberEditor(BigInteger.class, true);
        } else if (BigDecimal.class.equals(pType)) {
            return new CustomNumberEditor(BigDecimal.class, true);
        } else if (short.class.equals(pType)) {
            return new CustomNumberEditor(Short.class, false);
        } else if (Short.class.equals(pType)) {
            return new CustomNumberEditor(Short.class, true);
        } else if (byte[].class.equals(pType)) {
            return new ByteArrayPropertyEditor();
        } else if (char[].class.equals(pType)) {
            return new CharArrayPropertyEditor();
        } else if (char.class.equals(pType)) {
            return new CharacterEditor(false);
        } else if (Character.class.equals(pType)) {
            return new CharacterEditor(true);
        } else if (Byte.class.equals(pType)) {
            return new CustomNumberEditor(Byte.class, true);
        } else if (byte.class.equals(pType)) {
            return new CustomNumberEditor(Byte.class, false);
        } else if (Currency.class.equals(pType)) {
            return new CurrencyEditor();
        } else if (Charset.class.equals(pType)) {
            return new CharsetEditor();
        } else if (Locale.class.equals(pType)) {
            return new LocaleEditor();
        } else if (Resource[].class.equals(pType)) {
            return new ResourceArrayPropertyEditor();
        } else if (TimeZone.class.equals(pType)) {
            return new TimeZoneEditor();
        } else if (UUID.class.equals(pType)) {
            return new UUIDEditor();
        } else if (zoneIdClass != null && zoneIdClass.equals(pType)) {
            return new ZoneIdEditor();
        } else if (Path.class.equals(pType)) {
            return new PathEditor();
        } else if (int[].class.equals(pType)) {
            return new StringArrayPropertyEditor();
        } else if (long[].class.equals(pType)) {
            return new StringArrayPropertyEditor();
        } else if (short[].class.equals(pType)) {
            return new StringArrayPropertyEditor();
        } else if (File.class.equals(pType)) {
            return new FileEditor();
        } else if (InputSource.class.equals(pType)) {
            return new InputSourceEditor();
        } else if (InputStream.class.equals(pType)) {
            return new InputStreamEditor();
        } else if (Reader.class.equals(pType)) {
            return new ReaderEditor();
        } else {
            log.warn("createPropertyEditor(): not found PropertyEditor for type: {0.name}", pType);
        }
        return null;
    }
}
