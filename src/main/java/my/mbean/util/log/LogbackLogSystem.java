package my.mbean.util.log;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.impl.StaticLoggerBinder;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xnat on 17/5/12.
 */
public class LogbackLogSystem extends LogSystem {
    static final Map<LogLevel, Level> LEVELS = new HashMap<>(7);

    static {
        LEVELS.put(LogLevel.TRACE, Level.TRACE);
        LEVELS.put(LogLevel.DEBUG, Level.DEBUG);
        LEVELS.put(LogLevel.INFO, Level.INFO);
        LEVELS.put(LogLevel.WARN, Level.WARN);
        LEVELS.put(LogLevel.ERROR, Level.ERROR);
        LEVELS.put(LogLevel.OFF, Level.OFF);
    }

    public LogbackLogSystem(ClassLoader pClassLoader) {
        super(pClassLoader);
    }

    @Override
    public void setLogLevel(String loggerName, LogLevel level) {
        ch.qos.logback.classic.Logger logger = getLogger(loggerName);
        if (logger != null) {
            logger.setLevel(LEVELS.get(level));
        }
    }


    private ch.qos.logback.classic.Logger getLogger(String name) {
        LoggerContext factory = getLoggerContext();
        if (StringUtils.isEmpty(name) || ROOT_LOGGER_NAME.equals(name)) {
            name = Logger.ROOT_LOGGER_NAME;
        }
        return factory.getLogger(name);

    }


    private LoggerContext getLoggerContext() {
        ILoggerFactory factory = StaticLoggerBinder.getSingleton().getLoggerFactory();
        Assert.isInstanceOf(LoggerContext.class, factory,
                String.format(
                        "LoggerFactory is not a Logback LoggerContext but Logback is on "
                                + "the classpath. Either remove Logback or the competing "
                                + "implementation (%s loaded from %s). If you are using "
                                + "WebLogic you will need to add 'org.slf4j' to "
                                + "prefer-application-packages in WEB-INF/weblogic.xml",
                        factory.getClass(), getLocation(factory)));
        return (LoggerContext) factory;
    }

    private Object getLocation(ILoggerFactory factory) {
        try {
            ProtectionDomain protectionDomain = factory.getClass().getProtectionDomain();
            CodeSource codeSource = protectionDomain.getCodeSource();
            if (codeSource != null) {
                return codeSource.getLocation();
            }
        } catch (SecurityException ex) {
            // Unable to determine location
        }
        return "unknown location";
    }
}
