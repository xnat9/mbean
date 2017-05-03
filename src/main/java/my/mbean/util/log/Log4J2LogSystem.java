package my.mbean.util.log;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xnat on 17/5/3.
 */
public class Log4J2LogSystem extends LogSystem {
    static final Map<LogLevel, Level> LEVELS = new HashMap<>(7);

    static {
        LEVELS.put(LogLevel.TRACE, Level.TRACE);
        LEVELS.put(LogLevel.DEBUG, Level.DEBUG);
        LEVELS.put(LogLevel.INFO, Level.INFO);
        LEVELS.put(LogLevel.WARN, Level.WARN);
        LEVELS.put(LogLevel.ERROR, Level.ERROR);
        LEVELS.put(LogLevel.OFF, Level.OFF);
    }

    @Override
    public void setLogLevel(String loggerName, LogLevel logLevel) {
        Level level = LEVELS.get(logLevel);
        LoggerConfig loggerConfig = getLoggerConfig(loggerName);
        if (loggerConfig == null) {
            loggerConfig = new LoggerConfig(loggerName, level, true);
            getLoggerContext().getConfiguration().addLogger(loggerName, loggerConfig);
        } else {
            loggerConfig.setLevel(level);
        }
        getLoggerContext().updateLoggers();
    }

    private LoggerConfig getLoggerConfig(String name) {
        if (!StringUtils.hasLength(name) || ROOT_LOGGER_NAME.equals(name)) {
            name = LogManager.ROOT_LOGGER_NAME;
        }
        return getLoggerContext().getConfiguration().getLoggers().get(name);
    }

    private LoggerContext getLoggerContext() {
        return (LoggerContext) LogManager.getContext(false);
    }
}
