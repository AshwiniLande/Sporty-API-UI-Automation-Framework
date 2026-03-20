package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Provides a thread-safe Logger instance for the given class */
public class LogHelper {

    private LogHelper() {}

    public static Logger getLogger(Class<?> clazz) {
        return LogManager.getLogger(clazz);
    }
}