package my.mbean.support;

import my.mbean.spring.GenericService;

/**
 * admin/beans bean view page VO builder interface.
 * @author hubert
 * @param <T>
 */
public abstract class BeanVOBuilder<T> extends GenericService {
    /**
     * ids 代表多个beanName
     * 服务于特定的一些beanName.
     * 如果为null, 就是默认的builder 可以服务于所有的beanName.
     * @return beanName.
     */
    public String[] getIds() { return null; };
    /**
     * admin/beans bean view page. build 一个完整的bean view Object.
     * @param pBeanName
     *            bean name.
     * @param pContextId
     *            ApplicationContext's id.
     * @return BeanVO.
     */
    public abstract T build(String pBeanName, String pContextId);

    /**
     * 下面的方法是build bean view 的其中某一部分.
     * 一般在build方法中调下面的所有方法组成一个完整的beanView Object.
     * 也可以单独build bean view的某一部分(一般用于页面部分刷新ajax.)
     */

    /**
     * build bean view 界面的一般信息内容,一般显示在上边.
     * @param pBeanName
     *            bean name.
     * @param pContextId
     *            ApplicationContext's id.
     * @return bean view common info view object.
     */
    public Object buildCommonView(String pBeanName, String pContextId) { return null; };



    /**
     * build properties view 那部分.
     * @param pBeanName
     *            bean name.
     * @param pContextId
     *            ApplicationContext's id.
     * @return property view object.
     */
    public Object buildPropertiesView(String pBeanName, String pContextId) { return null; };



    /**
     * build methods view 那部分.
     * @param pBeanName
     *            bean name.
     * @param pContextId
     *            ApplicationContext's id.
     * @return method view object.
     */
    public Object buildMethodsView(String pBeanName, String pContextId) { return null; };
}
