package utility;

import java.util.logging.Level;

public class Logger {
    private final static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(java.util.logging.Logger.GLOBAL_LOGGER_NAME);

    public static void warnLog (String message) {
        logger.log(Level.WARNING, message);
    }

    public static void infoLog (String message) {
        logger.log(Level.INFO, message);
    }
}
