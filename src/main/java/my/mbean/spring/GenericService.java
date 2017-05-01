package my.mbean.spring;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;

import my.mbean.util.Log;

/**
 * provide generic service for bean.
 * @author hubert
 */
public class GenericService extends BaseBean implements InitializingBean {
    private final String  LOGGER_NAME = getClass().getName();
    protected final Log   log         = Log.of(LOGGER_NAME);
    private boolean       running;
    @Autowired
    private LoggingSystem loggingSystem;
    /**
     * service information.
     */
    private String        serviceInfo;



    @Override
    public void afterPropertiesSet() throws Exception {
        startService();
    }



    /**
     * 1. (自定义个BeanPostProcessor去调用) 被CommonBeansPostProcessor.postProcessAfterInitialization 调用
     * 2. afterPropertiesSet中调用.
     * 3. AbstractBeanDefinition.getInitMethodName() 设置startService 为init 方法.
     */
    public void startService() {
        log.debug("start service bean: {0.beanName}", this);
        if (running) {
            log.warn("service already running!!");
            return;
        }
        synchronized (this) {
            try {
                if (running) {
                    return;
                }
                doStartService();
                // TODO proxy object.
                this.running = true;
            } catch (Exception e) {
                log.error(e, "bean: {0.beanName} startService error!. class: {0.class.name}", this);
            }
        }
    }



    /**
     * @PreDestroy 被CommonAnnotationBeanPostProcessor处理.
     */
    @PreDestroy
    public void stopService() {
        log.info("stop service bean: {0.beanName}", this);
        if (!running) {
            log.warn("service already stopping!!");
            return;
        }
        synchronized (this) {
            if (!running) {
                return;
            }
            this.running = false;
            doStopService();
        }
    }



    public void restartService() {
        stopService();
        startService();
    }



    protected void doStartService() {
        // TODO
    }



    protected void doStopService() {
        // TODO
    }



    /**
     * stand for this service start success.
     * @return
     */
    public boolean isRunning() {
        return this.running;
    }



    /**
     * @return the loggingInfo
     */
    public boolean isLoggingInfo() {
        return log.isInfoEnabled();
    }



    /**
     * @param pLoggingInfo
     *            the loggingInfo to set
     */
    public void setLoggingInfo(boolean pLoggingInfo) {
        if (pLoggingInfo) {
            loggingSystem.setLogLevel(LOGGER_NAME, LogLevel.INFO);
        } else {
            loggingSystem.setLogLevel(LOGGER_NAME, LogLevel.DEBUG);
        }
    }



    /**
     * @return the loggingWarning
     */
    public boolean isLoggingWarning() {
        return log.isWarnEnabled();
    }



    /**
     * @param pLoggingWarning
     *            the loggingWarning to set
     */
    public void setLoggingWarning(boolean pLoggingWarning) {
        if (pLoggingWarning) {
            loggingSystem.setLogLevel(LOGGER_NAME, LogLevel.WARN);
        } else {
            loggingSystem.setLogLevel(LOGGER_NAME, LogLevel.ERROR);
        }
    }



    /**
     * @return the loggingError
     */
    public boolean isLoggingError() {
        return log.isErrorEnabled();
    }



    /**
     * @param pLoggingError
     *            the loggingError to set
     */
    public void setLoggingError(boolean pLoggingError) {
        if (pLoggingError) {
            loggingSystem.setLogLevel(LOGGER_NAME, LogLevel.ERROR);
        } else {
            loggingSystem.setLogLevel(LOGGER_NAME, LogLevel.OFF);
        }
    }



    /**
     * @return the loggingDebug
     */
    public boolean isLoggingDebug() {
        return log.isDebugEnabled();
    }



    /**
     * @param pLoggingDebug
     *            the loggingDebug to set
     */
    public void setLoggingDebug(boolean pLoggingDebug) {
        if (pLoggingDebug) {
            loggingSystem.setLogLevel(LOGGER_NAME, LogLevel.DEBUG);
        } else {
            loggingSystem.setLogLevel(LOGGER_NAME, LogLevel.INFO);
        }
    }



    /**
     * @return the loggingTrace
     */
    public boolean isLoggingTrace() {
        return log.isTraceEnabled();
    }



    /**
     * @param pLoggingTrace
     *            the loggingTrace to set
     */
    public void setLoggingTrace(boolean pLoggingTrace) {
        if (pLoggingTrace) {
            loggingSystem.setLogLevel(LOGGER_NAME, LogLevel.TRACE);
        } else {
            loggingSystem.setLogLevel(LOGGER_NAME, LogLevel.OFF);
        }
    }



    /**
     * @return the serviceInfo
     */
    public String getServiceInfo() {
        return serviceInfo;
    }



    /**
     * @param pServiceInfo
     *            the serviceInfo to set
     */
    public void setServiceInfo(String pServiceInfo) {
        serviceInfo = pServiceInfo;
    }

}