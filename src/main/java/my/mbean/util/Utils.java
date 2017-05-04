package my.mbean.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.aop.TargetClassAware;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.target.SingletonTargetSource;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.*;

public class Utils {
    public static List<Field> getAllFieldsList(Class<?> cls) {
        if (cls == null) return Collections.emptyList();
        ArrayList allFields = new ArrayList();

        for (Class currentClass = cls; currentClass != null; currentClass = currentClass.getSuperclass()) {
            Field[] declaredFields = currentClass.getDeclaredFields();
            Field[] var4 = declaredFields;
            int var5 = declaredFields.length;

            for (int var6 = 0; var6 < var5; ++var6) {
                Field field = var4[var6];
                allFields.add(field);
            }
        }

        return allFields;
    }

    public static String toJsonStr(Object pObj) {
        ObjectMapper mapper = new ObjectMapper().setSerializationInclusion(Include.NON_NULL);
        try {
            return mapper.writeValueAsString(pObj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String toString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }


    public static String buildClassPath(Class<?> pCls, String pSuffix) {
        StringBuilder sb = new StringBuilder("classpath:/");
        System.out.println(pCls.getPackage().getName());
        for (String name : pCls.getPackage().getName().split("\\.")) {
            sb.append(name).append("/");
        }
        return sb.append(pSuffix).toString();
    }


    public static Object extractOriginInstance(Object pWrapper) {
        if (pWrapper == null)
            return null;
        Object originInstance = pWrapper;
        if (AopUtils.isCglibProxy(pWrapper)) {
            Object current = pWrapper;
            while (current instanceof TargetClassAware) {
                if (current instanceof Advised) {
                    current = ((Advised) current).getTargetSource();
                    if (current instanceof SingletonTargetSource) {
                        current = ((SingletonTargetSource) current).getTarget();
                    }
                }
            }
            originInstance = (current == null ? originInstance : current);
        }
        return originInstance;
    }


    public static Object getValue(Field pField, Object pObject) {
        if (pField == null || pObject == null)
            return null;
        try {
            pField.setAccessible(true);
            return pField.get(pObject);
        } catch (Exception e) {
        }
        return null;
    }


    @SuppressWarnings("unchecked")
    public static <T> T getValue(Field pField, Object pObject, Class<T> pExpectType) {
        if (pField == null || pObject == null)
            return null;
        try {
            pField.setAccessible(true);
            return (T) pField.get(pObject);
        } catch (Exception e) {
        }
        return null;
    }


    public static String toString(Object pObj) {
        if (pObj == null)
            return "null";
        if (String.class.isAssignableFrom(pObj.getClass())) {
            return (String) pObj;
        }
        if (ClassUtils.isPrimitiveOrWrapper(pObj.getClass())) {
            return String.valueOf(pObj);
        }
        if (pObj instanceof Class) {
            return pObj.toString();
        }
        // if (pObj.getClass().isArray() &&
        // pObj.getClass().getComponentType().isPrimitive()) {
        // return
        // }
        return new ReflectionToStringBuilder(pObj) {
            @Override
            public String toString() {
                // if (this.getObject() == null) {
                // // return this.getStyle().getNullText();
                // return "null";
                // }
                Class<?> clazz = getObject().getClass();
                if (String.class.isAssignableFrom(clazz)) {
                    return (String) getObject();
                }
                if (Collection.class.isAssignableFrom(clazz)) {
                    append("size", ((Collection<?>) getObject()).size())
                            .append(getObject().toString());
                } else if (Map.class.isAssignableFrom(clazz)) {
                    append("size", ((Map<?, ?>) getObject()).size())
                            .append(getObject().toString());
                } else {
                    appendFieldsIn(clazz);
                    while (clazz.getSuperclass() != null && clazz != getUpToClass()) {
                        clazz = clazz.getSuperclass();
                        appendFieldsIn(clazz);
                    }
                }
                getStyle().appendEnd(getStringBuffer(), getObject());
                return getStringBuffer().toString();
            }
        }.toString();
    }


    public static boolean isBlank(final CharSequence cs) {
        return !StringUtils.hasText(cs);
    }


    public static boolean isNotBlank(final CharSequence cs) {
        return StringUtils.hasText(cs);
    }


    public static void extractPropertiesToMap(Object pObj, Map<String, Object> pMap) {
        if (pObj == null || pMap == null)
            return;
        PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(pObj.getClass());
        for (PropertyDescriptor pd : pds) {
            try {
                Object value = pd.getReadMethod().invoke(pObj);
                pMap.put(pd.getName(), value);
            } catch (Exception e) {
                continue;
            }
        }
    }


    public static void extractPropertiesToMapValueToJsonStr(Object pObj, Map<String, Object> pMap) {
        if (pObj == null || pMap == null)
            return;
        PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(pObj.getClass());
        for (PropertyDescriptor pd : pds) {
            try {
                Object value = pd.getReadMethod().invoke(pObj);
                if (ClassUtils.isPrimitiveOrWrapper(pd.getPropertyType()) || String.class.isAssignableFrom(pd.getPropertyType())) {
                    pMap.put(pd.getName(), value);
                } else {
                    pMap.put(pd.getName(), toJsonStr(value));
                }
            } catch (Exception e) {
                continue;
            }
        }
    }


    public static boolean isEmpty(Collection<?> pCollection) {
        return (pCollection == null || pCollection.isEmpty());
    }


    public static boolean isNotEmpty(Collection<?> pCollection) {
        return (!isEmpty(pCollection));
    }


    public static boolean isEmpty(Object[] array) {
        return (array == null || array.length == 0);
    }


    public static boolean isNotEmpty(Object[] array) {
        return !isEmpty(array);
    }


    public static boolean isEmpty(final Map<?, ?> map) {
        return map == null || map.isEmpty();
    }
}
