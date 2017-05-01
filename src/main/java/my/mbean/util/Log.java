package my.mbean.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * common log function.
 * @author hubert
 */
public class Log {
    /**
     * delegate logger.
     */
    private final Logger  logger;



    private Log(Logger logger) {
        this.logger = logger;
    }



    public static Log of(Class<?> clazz) {
        return new Log(LoggerFactory.getLogger(clazz));
    }
    public static Log of(String name) {
        return new Log(LoggerFactory.getLogger(name));
    }



    // ====================log method ==============

    public void error(String pMsg, Object... pArgs) {
        // first judge log error enabled, Msg.format will populate result msg by pArgs.
        if (isErrorEnabled()) {
            logger.error(Msg.format(pMsg, pArgs));
        }
    }



    public void error(Throwable pThrowable, String pMsg, Object... pArgs) {
        if (isErrorEnabled()) {
            logger.error(Msg.format(pMsg, pArgs), pThrowable);
        }
    }



    public void warn(Throwable pThrowable, String pMsg, Object... pArgs) {
        if (isWarnEnabled()) {
            logger.warn(Msg.format(pMsg, pArgs), pThrowable);
        }
    }



    public void warn(String pMsg, Object... pArgs) {
        if (isWarnEnabled()) {
            logger.warn(Msg.format(pMsg, pArgs));
        }
    }



    public void info(String pMsg, Object... pArgs) {
        if (isInfoEnabled()) {
            logger.info(Msg.format(pMsg, pArgs));
        }
    }



    public void debug(String pMsg, Object... pArgs) {
        if (isDebugEnabled()) {
            logger.debug(Msg.format(pMsg, pArgs));
        }
    }



    public void trace(String pMsg, Object... pArgs) {
        if (isTraceEnabled()) {
            logger.trace(Msg.format(pMsg, pArgs));
        }
    }



    // ====================log method ==============



    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }



    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }



    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }



    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }



    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

}
