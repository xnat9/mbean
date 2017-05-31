package my.mbean.support;

import java.io.Serializable;

import my.mbean.util.Utils;

/**
 * 属性值和bean值的包装表示.
 * @author hubert
 */
public class ValueWrapper implements Serializable {
    private static final long  serialVersionUID = 1L;
    public static final String VALUE_SHOW_TYPE_MAP   = "map";
    public static final String VALUE_SHOW_TYPE_LIST  = "list";
    /**
     * value tip.
     */
    private String             tip;
    /**
     * list/array/map or other custom value.
     * 对象被解析后, 可以表示为一个list, array, map 对象.
     */
    private Object             value;
    /**
     * list or map.
     */
    private String             showType;
    /**
     * origin value.
     */
    private Object             originValue;
    /**
     * 一般表示这个value 是一个bean 关联.
     */
    private BeanRef            beanRef;
    /**
     * Property Value's toString.
     */
    private String             toString;

    public static class Entry {
        private Object key;
        private Object value;

        public Entry(){};
        public Entry(Object key, Object value) {
            this.key = key;
            this.value = value;
        }

        public Object getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public void setKey(Object key) {
            this.key = key;
        }
    }



    public static class ValueDescriptor extends BeanRef {
        public ValueDescriptor(String pToString) {
            super(pToString);
        }

        public ValueDescriptor() {
        }
    }


    public static class BeanRef implements Serializable {
        private static final long serialVersionUID = 1L;
        /**
         * bean name.
         */
        private String            beanName;
        /**
         * bean url.
         */
        private String            beanUrl;
        /**
         * bean toString.
         */
        private String            toString;



        public BeanRef(String pToString) {
            this.toString = pToString;
        }



        public BeanRef() {
        }

        @Override
        public String toString() {
            return Utils.toString(this);
        }



        public boolean isRef() {
            return Utils.isNotBlank(getBeanName());
        }


        public String getBeanName() {
            return beanName;
        }

        public BeanRef setBeanName(String beanName) {
            this.beanName = beanName;
            return this;
        }

        public String getBeanUrl() {
            return beanUrl;
        }

        public BeanRef setBeanUrl(String beanUrl) {
            this.beanUrl = beanUrl;
            return this;
        }

        public String getToString() {
            return toString;
        }

        public BeanRef setToString(String toString) {
            this.toString = toString;
            return this;
        }
    }



    @Override
    public String toString() {
        return Utils.toString(this);
    }

    public String getTip() {
        return tip;
    }

    public ValueWrapper setTip(String tip) {
        this.tip = tip;
        return this;
    }

    public Object getValue() {
        return value;
    }

    public ValueWrapper setValue(Object value) {
        this.value = value;
        return this;
    }

    public String getShowType() {
        return showType;
    }

    public ValueWrapper setShowType(String showType) {
        this.showType = showType;
        return this;
    }

    public Object getOriginValue() {
        return originValue;
    }

    public ValueWrapper setOriginValue(Object originValue) {
        this.originValue = originValue;
        return this;
    }

    public BeanRef getBeanRef() {
        return beanRef;
    }

    public ValueWrapper setBeanRef(BeanRef beanRef) {
        this.beanRef = beanRef;
        return this;
    }

    public String getToString() {
        return toString;
    }

    public ValueWrapper setToString(String toString) {
        this.toString = toString;
        return this;
    }
}
