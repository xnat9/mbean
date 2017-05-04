package my.mbean.util.log;

import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Created by xnat on 17/5/3.
 */
public abstract class LogSystem {
    /**
     * A System property that can be used to indicate the {@link LogSystem} to use.
     */
    public static final String SYSTEM_PROPERTY = LogSystem.class.getName();

    /**
     * The value of the {@link #SYSTEM_PROPERTY} that can be used to indicate that no
     * {@link LogSystem} should be used.
     */
    public static final String NONE = "none";
    private ClassLoader classLoader;

    public LogSystem(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    public LogSystem() {}

    /**
     * The name used for the root logger. LogSystem implementations should ensure that
     * this is the name used to represent the root logger, regardless of the underlying
     * implementation.
     */
    public static final String ROOT_LOGGER_NAME = "ROOT";

    private static final Map<String, String> SYSTEMS;

    static {
        Map<String, String> systems = new LinkedHashMap<String, String>();
//        systems.put("ch.qos.logback.core.Appender",
//                "org.springframework.boot.logging.logback.LogbackLogSystem");
        systems.put("org.apache.log4j.LogManager",
                "my.mbean.util.log.Log4JLogSystem");
        systems.put("org.apache.logging.log4j.core.impl.Log4jContextFactory",
                "my.mbean.util.log.Log4J2LogSystem");
        systems.put("java.util.logging.LogManager",
                "org.springframework.boot.logging.java.JavaLogSystem");
        SYSTEMS = Collections.unmodifiableMap(systems);
    }


    /**
     * Sets the logging level for a given logger.
     * @param loggerName the name of the logger to set ({@code null} can be used for the
     * root logger).
     * @param level the log level
     */
    public void setLogLevel(String loggerName, LogLevel level) {
        throw new UnsupportedOperationException("Unable to set log level");
    }
    /**
     * Returns a set of the {@link LogLevel LogLevels} that are actually supported by the
     * logging system.
     * @return the supported levels
     */
    public Set<LogLevel> getSupportedLogLevels() {
        return EnumSet.allOf(LogLevel.class);
    }


    /**
     * Detect and return the logging system in use. Supports Logback and Java Logging.
     * @param classLoader the classloader
     * @return The logging system
     */
    public static LogSystem get(ClassLoader classLoader) {
        String LogSystem = System.getProperty(SYSTEM_PROPERTY);
        if (StringUtils.hasLength(LogSystem)) {
            if (NONE.equals(LogSystem)) {
                return new LogSystem.NoOpLogSystem();
            }
            return get(classLoader, LogSystem);
        }
        for (Map.Entry<String, String> entry : SYSTEMS.entrySet()) {
            if (ClassUtils.isPresent(entry.getKey(), classLoader)) {
                return get(classLoader, entry.getValue());
            }
        }
        throw new IllegalStateException("No suitable logging system located");
    }

    private static LogSystem get(ClassLoader classLoader, String LogSystemClass) {
        try {
            Class<?> systemClass = ClassUtils.forName(LogSystemClass, classLoader);
            return (LogSystem) systemClass.getConstructor(ClassLoader.class)
                    .newInstance(classLoader);
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * {@link LogSystem} that does nothing.
     */
    static class NoOpLogSystem extends LogSystem {
        public NoOpLogSystem(ClassLoader pClassLoader) {
            super(pClassLoader);
        }
        public NoOpLogSystem() {}
        @Override
        public void setLogLevel(String loggerName, LogLevel level) {

        }
    }
}
