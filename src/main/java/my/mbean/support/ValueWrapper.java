package my.mbean.support;

import java.io.Serializable;

import my.mbean.util.Utils;

/**
 * 属性值和bean值的包装表示.
 * @author hubert
 */
public class ValueWrapper implements Serializable {
    private static final long  serialVersionUID = 1L;
    public static final String VALUE_TYPE_MAP   = "map";
    public static final String VALUE_TYPE_LIST  = "list";
    public static final String VALUE_TYPE_ARRAY = "array";
    public static final String VALUE_TYPE_SIMPLE = "simple";
    /**
     * value tip.
     */
    private String             tip = "";
    /**
     * list/array/map or other custom value.
     * 对象被解析后, 可以表示为一个list, array, map 对象.
     */
    private Object             value;
    /**
     * list, array or map.
     */
    private String             type;
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



        /**
         * @return the beanName
         */
        public String getBeanName() {
            return beanName;
        }



        /**
         * @return the beanUrl
         */
        public String getBeanUrl() {
            return beanUrl;
        }



        /**
         * @param pBeanName
         *            the beanName to set
         */
        public void setBeanName(String pBeanName) {
            beanName = pBeanName;
        }



        /**
         * @param pBeanUrl
         *            the beanUrl to set
         */
        public void setBeanUrl(String pBeanUrl) {
            beanUrl = pBeanUrl;
        }



        /**
         * @return the toString
         */
        public String getToString() {
            return toString;
        }



        /**
         * @param pToString
         *            the toString to set
         */
        public void setToString(String pToString) {
            toString = pToString;
        }
    }



    @Override
    public String toString() {
        return Utils.toString(this);
    }



    /**
     * @return the toString
     */
    public String getToString() {
        return toString;
    }



    /**
     * @param pToString
     *            the toString to set
     */
    public void setToString(String pToString) {
        toString = pToString;
    }



    /**
     * @return the originValue
     */
    public Object getOriginValue() {
        return originValue;
    }



    /**
     * @param pOriginValue
     *            the originValue to set
     */
    public void setOriginValue(Object pOriginValue) {
        originValue = pOriginValue;
    }



    /**
     * @return the beanRef
     */
    public BeanRef getBeanRef() {
        return beanRef;
    }



    /**
     * @param pBeanRef
     *            the beanRef to set
     */
    public void setBeanRef(BeanRef pBeanRef) {
        beanRef = pBeanRef;
    }



    /**
     * @return the tip
     */
    public String getTip() {
        return tip;
    }



    /**
     * @param pTip
     *            the tip to set
     */
    public void setTip(String pTip) {
        tip = pTip;
    }



    /**
     * @return the value
     */
    public Object getValue() {
        return value;
    }



    /**
     * @param pValue
     *            the value to set
     */
    public void setValue(Object pValue) {
        value = pValue;
    }



    /**
     * @return the type
     */
    public String getType() {
        return type;
    }



    /**
     * @param pType
     *            the type to set
     */
    public void setType(String pType) {
        type = pType;
    }

}
