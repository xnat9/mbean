package my.mbean.util.log;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 * Created by xxb on 2017/5/4.
 * Version：
 */
public class Log4JLogSystem extends LogSystem {
    static final Map<LogLevel, Level> LEVELS = new HashMap<>(7);

    static {
        LEVELS.put(LogLevel.TRACE, Level.TRACE);
        LEVELS.put(LogLevel.DEBUG, Level.DEBUG);
        LEVELS.put(LogLevel.INFO, Level.INFO);
        LEVELS.put(LogLevel.WARN, Level.WARN);
        LEVELS.put(LogLevel.ERROR, Level.ERROR);
        LEVELS.put(LogLevel.OFF, Level.OFF);
    }

    public Log4JLogSystem(ClassLoader pClassLoader) {
        super(pClassLoader);
    }

    @Override
    public void setLogLevel(String loggerName, LogLevel logLevel) {
        Level level = LEVELS.get(logLevel);
        Logger logger = LogManager.getLogger(loggerName);
        if (logger != null) {
            logger.setLevel(level);
        }
    }
}
