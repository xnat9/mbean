package my.mbean.util;

import my.mbean.util.json.JsonUtil;
import org.springframework.aop.TargetClassAware;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.target.SingletonTargetSource;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.StringUtils;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class Utils {
    /**
     * 渲染html模板.
     *
     * @param pHtmlTemplate html 模板
     * @param pModel        渲染数据
     * @return 渲染后的html String.
     */
    public static String render(String pHtmlTemplate, final Map pModel) {
        // 自定义一个属性替代类是因为, 如果有个属性的值是个String 值为: "${", 那么其 originValue="${",
        // org.springframework.util.PropertyPlaceholderHelper 这个属性替代器会对这个originValue 做解析(它会一直匹配到后面的 "}"), 这里不应该对属性值做解析.
        SimplePropertyPlaceholderHelper placeholderHelper = new SimplePropertyPlaceholderHelper("${", "}");
        String result = placeholderHelper.replacePlaceholders(pHtmlTemplate, new PropertyPlaceholderHelper.PlaceholderResolver() {
            @Override
            public String resolvePlaceholder(String placeholderName) {
                return Objects.toString(pModel.get(placeholderName), "");
            }
        });
        return result;
    }

    /**
     * 得到一个class的所有Field, 包括父级.
     * @param cls
     * @return
     */
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
        return JsonUtil.get().toJson(pObj);
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
        if (pObj == null) return "null";
        Class<? extends Object> type = pObj.getClass();
        //常见类型toString.
        if (ClassUtils.isPrimitiveOrWrapper(type)
                || String.class.isAssignableFrom(type)
                || Class.class.isAssignableFrom(type)
                || Map.class.isAssignableFrom(type)
                || Collection.class.isAssignableFrom(type)) {
            return pObj.toString();
        } else if (type.isArray()) {
            if (double[].class.isAssignableFrom(type)) {
                return Arrays.toString((double[]) pObj);
            } else if (float[].class.isAssignableFrom(type)) {
                return Arrays.toString((float[]) pObj);
            } else if (boolean[].class.isAssignableFrom(type)) {
                return Arrays.toString((boolean[]) pObj);
            } else if (byte[].class.isAssignableFrom(type)) {
                return Arrays.toString((byte[]) pObj);
            } else if (char[].class.isAssignableFrom(type)) {
                return Arrays.toString((char[]) pObj);
            } else if (int[].class.isAssignableFrom(type)) {
                return Arrays.toString((int[]) pObj);
            } else if (long[].class.isAssignableFrom(type)) {
                return Arrays.toString((long[]) pObj);
            } else if (short[].class.isAssignableFrom(type)) {
                return Arrays.toString((short[]) pObj);
            } else {
                return Arrays.toString((Object[]) pObj);
            }
        }
        //extract java bean properties.
        try {
            StringBuilder sb = new StringBuilder("[");
            PropertyDescriptor[] pds = Introspector.getBeanInfo(type).getPropertyDescriptors();
            // 下面这种写法参考自Arrays.toString
            int iMax = pds.length - 1;
            if (iMax == -1) return "[]";
            for (int i = 0; ; i++) {
                Object v = null;
                try {
                    v = pds[i].getReadMethod().invoke(pObj);
                } catch (Exception e) {
                }
                if (i == iMax) return sb.append(']').toString();
                sb.append(pds[i].getName()).append("=").append(v).append(", ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return pObj.toString();
        }
    }

    public static int toInt(final String str, final int defaultValue) {
        if(str == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(str);
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
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
        return (array == null || Array.getLength(array) == 0);
    }


    public static boolean isNotEmpty(Object[] array) {
        return !isEmpty(array);
    }


    public static boolean isEmpty(final Map<?, ?> map) {
        return map == null || map.isEmpty();
    }


    /**
     * 判断集合里的元素的类型, 是否和给定的参数 cls匹配.
     * @param coll
     * @param cls
     * @return
     */
    public static boolean matchComponentType(Collection coll, Class cls) {
        if (isEmpty(coll)) return false;
        if (coll instanceof List) return ((List) coll).get(0).getClass().equals(cls);
        return coll.iterator().next().getClass().equals(cls);
    }
}
