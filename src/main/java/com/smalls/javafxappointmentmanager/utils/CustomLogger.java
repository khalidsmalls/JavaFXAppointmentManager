package com.smalls.javafxappointmentmanager.utils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.*;

public class CustomLogger {

    private static final Logger logger = Logger.getLogger(
            CustomLogger.class.getName()
    );


    public static void initialize(String logFile) {
        try {
            FileHandler fh = new FileHandler(logFile, true);
            logger.addHandler(fh);
            fh.setFormatter(new LogFormatter());
        } catch (IOException e) {
            throw new RuntimeException(
                    "Error initializing logger: " +
                            e.getMessage()
            );
        }
    }

    public static void log(String className, Level level, String message) {
        String nowDateTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern(
                        "MM-dd-yy hh:mm:ss a"
                ));
        String msg = nowDateTime + " " + className + ": " + message;
        logger.log(level, msg);
    }

    public static void close() {
        for (Handler h : logger.getHandlers()) {
            h.close();
        }
    }

    private static class LogFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            String level = record.getLevel().getName();
            String msg = record.getMessage();
            return level + ": " + msg + "\n";
        }
    }
}
