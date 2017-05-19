package my.mbean.util;



import java.beans.*;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by xnat on 17/5/18.
 */
public class PropertyUtil {

    public static void main(String[] args) {
        Object result = getProperty(new PropertyUtil(), "class.declaredMethods[1].name");
        System.out.println("result: " + result);
    }

    public static Object getProperty(Object bean, String name) {
        if (bean == null || name == null) return null;
        while (Resolver.hasNested(name)) {
            final String next = Resolver.next(name);
            Object nestedBean = null;
            if (bean instanceof Map) {
                nestedBean = getPropertyOfMapBean((Map<?, ?>) bean, next);
            } else if (Resolver.isMapped(next)) {
                nestedBean = getMappedProperty(bean, next);
            } else if (Resolver.isIndexed(next)) {
                nestedBean = getIndexedProperty(bean, next);
            } else {
                nestedBean = getSimpleProperty(bean, next);
            }
            if (nestedBean == null) {
                return null;
            }
            bean = nestedBean;
            name = Resolver.remove(name);
        }
        if (bean instanceof Map) {
            bean = getPropertyOfMapBean((Map<?, ?>) bean, name);
        } else if (Resolver.isMapped(name)) {
            bean = getMappedProperty(bean, name);
        } else if (Resolver.isIndexed(name)) {
            bean = getIndexedProperty(bean, name);
        } else {
            bean = getSimpleProperty(bean, name);
        }
        return bean;
    }


    private static Object getMappedProperty(final Object bean, String name) {
        String key = Resolver.getKey(name);
        name = Resolver.getProperty(name);
        Object mapValue = getSimpleProperty(bean, name);
        if (mapValue instanceof Map) {
            return ((Map) mapValue).get(key);
        }
        return null;
    }

    private static Object getIndexedProperty(final Object bean, String name) {
        int index = Resolver.getIndex(name);
        name = Resolver.getProperty(name);
        final PropertyDescriptor descriptor = getPropertyDescriptor(bean, name);
        if (descriptor instanceof IndexedPropertyDescriptor) {
            Method readMethod = ((IndexedPropertyDescriptor) descriptor).getIndexedReadMethod();
            if (readMethod != null) {
                final Object[] subscript = new Object[1];
                subscript[0] = new Integer(index);
                try {
                    return readMethod.invoke(bean, subscript);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        }
        Object indexedValue = getSimpleProperty(bean, name);
        if (indexedValue != null) {
            Class<? extends Object> cls = indexedValue.getClass();
            if (cls.isArray()) {
                return Array.get(indexedValue, index);
            } else if (List.class.isAssignableFrom(cls)) {
                return ((List) indexedValue).get(index);
            }
        }
        return null;
    }

    private static Object getPropertyOfMapBean(final Map<?, ?> bean, String propertyName) {
        if (Resolver.isMapped(propertyName)) {
            final String name = Resolver.getProperty(propertyName);
            if (name == null || name.length() == 0) {
                propertyName = Resolver.getKey(propertyName);
            }
        }
        return bean.get(propertyName);
    }

    private static Object getSimpleProperty(final Object bean, final String name) {
        PropertyDescriptor descriptor = getPropertyDescriptor(bean, name);
        Method method = descriptor.getReadMethod();
        method.setAccessible(true);
        try {
            Object ret = method.invoke(bean);
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static PropertyDescriptor getPropertyDescriptor(Object bean, String name) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
            for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
                if (Objects.equals(name, pd.getName())) return pd;
            }
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
        return null;
    }

    static class Resolver {
        private static final char NESTED        = '.';
        private static final char MAPPED_START  = '(';
        private static final char MAPPED_END    = ')';
        private static final char INDEXED_START = '[';
        private static final char INDEXED_END   = ']';

        public Resolver() {
        }

        /**
         * Return the index value from the property expression or -1.
         *
         * @param expression The property expression
         * @return The index value or -1 if the property is not indexed
         * @throws IllegalArgumentException If the indexed property is illegally
         *                                  formed or has an invalid (non-numeric) value.
         */
        public static int getIndex(final String expression) {
            if (expression == null || expression.length() == 0) {
                return -1;
            }
            for (int i = 0; i < expression.length(); i++) {
                final char c = expression.charAt(i);
                if (c == NESTED || c == MAPPED_START) {
                    return -1;
                } else if (c == INDEXED_START) {
                    final int end = expression.indexOf(INDEXED_END, i);
                    if (end < 0) {
                        throw new IllegalArgumentException("Missing End Delimiter");
                    }
                    final String value = expression.substring(i + 1, end);
                    if (value.length() == 0) {
                        throw new IllegalArgumentException("No Index Value");
                    }
                    int index = 0;
                    try {
                        index = Integer.parseInt(value, 10);
                    } catch (final Exception e) {
                        throw new IllegalArgumentException("Invalid index value '"
                                + value + "'");
                    }
                    return index;
                }
            }
            return -1;
        }

        /**
         * Return the map key from the property expression or <code>null</code>.
         *
         * @param expression The property expression
         * @return The index value
         * @throws IllegalArgumentException If the mapped property is illegally formed.
         */
        public static String getKey(final String expression) {
            if (expression == null || expression.length() == 0) {
                return null;
            }
            for (int i = 0; i < expression.length(); i++) {
                final char c = expression.charAt(i);
                if (c == NESTED || c == INDEXED_START) {
                    return null;
                } else if (c == MAPPED_START) {
                    final int end = expression.indexOf(MAPPED_END, i);
                    if (end < 0) {
                        throw new IllegalArgumentException("Missing End Delimiter");
                    }
                    return expression.substring(i + 1, end);
                }
            }
            return null;
        }

        /**
         * Return the property name from the property expression.
         *
         * @param expression The property expression
         * @return The property name
         */
        public static String getProperty(final String expression) {
            if (expression == null || expression.length() == 0) {
                return expression;
            }
            for (int i = 0; i < expression.length(); i++) {
                final char c = expression.charAt(i);
                if (c == NESTED) {
                    return expression.substring(0, i);
                } else if (c == MAPPED_START || c == INDEXED_START) {
                    return expression.substring(0, i);
                }
            }
            return expression;
        }

        /**
         * Indicates whether or not the expression
         * contains nested property expressions or not.
         *
         * @param expression The property expression
         * @return The next property expression
         */
        public static boolean hasNested(final String expression) {
            if (expression == null || expression.length() == 0) {
                return false;
            } else {
                return (remove(expression) != null);
            }
        }

        /**
         * Indicate whether the expression is for an indexed property or not.
         *
         * @param expression The property expression
         * @return <code>true</code> if the expresion is indexed,
         * otherwise <code>false</code>
         */
        public static boolean isIndexed(final String expression) {
            if (expression == null || expression.length() == 0) {
                return false;
            }
            for (int i = 0; i < expression.length(); i++) {
                final char c = expression.charAt(i);
                if (c == NESTED || c == MAPPED_START) {
                    return false;
                } else if (c == INDEXED_START) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Indicate whether the expression is for a mapped property or not.
         *
         * @param expression The property expression
         * @return <code>true</code> if the expresion is mapped,
         * otherwise <code>false</code>
         */
        public static boolean isMapped(final String expression) {
            if (expression == null || expression.length() == 0) {
                return false;
            }
            for (int i = 0; i < expression.length(); i++) {
                final char c = expression.charAt(i);
                if (c == NESTED || c == INDEXED_START) {
                    return false;
                } else if (c == MAPPED_START) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Extract the next property expression from the
         * current expression.
         *
         * @param expression The property expression
         * @return The next property expression
         */
        public static String next(final String expression) {
            if (expression == null || expression.length() == 0) {
                return null;
            }
            boolean indexed = false;
            boolean mapped = false;
            for (int i = 0; i < expression.length(); i++) {
                final char c = expression.charAt(i);
                if (indexed) {
                    if (c == INDEXED_END) {
                        return expression.substring(0, i + 1);
                    }
                } else if (mapped) {
                    if (c == MAPPED_END) {
                        return expression.substring(0, i + 1);
                    }
                } else {
                    if (c == NESTED) {
                        return expression.substring(0, i);
                    } else if (c == MAPPED_START) {
                        mapped = true;
                    } else if (c == INDEXED_START) {
                        indexed = true;
                    }
                }
            }
            return expression;
        }

        /**
         * Remove the last property expresson from the
         * current expression.
         *
         * @param expression The property expression
         * @return The new expression value, with first property
         * expression removed - null if there are no more expressions
         */
        public static String remove(final String expression) {
            if (expression == null || expression.length() == 0) {
                return null;
            }
            final String property = next(expression);
            if (expression.length() == property.length()) {
                return null;
            }
            int start = property.length();
            if (expression.charAt(start) == NESTED) {
                start++;
            }
            return expression.substring(start);
        }
    }
}
