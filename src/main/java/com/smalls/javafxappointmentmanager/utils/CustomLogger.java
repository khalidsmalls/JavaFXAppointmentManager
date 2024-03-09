package com.smalls.javafxappointmentmanager.utils;

import java.io.IOException;
import java.util.logging.*;

public class CustomLogger {

    private static final Logger log = Logger.getLogger(CustomLogger.class.getName());

    public static void initialize(String logFile) {
        try {
            FileHandler fh = new FileHandler(logFile, true);
            log.addHandler(fh);
            fh.setFormatter(new LogFormatter());
        } catch (IOException e) {
            throw new RuntimeException("Error initializing logger: " + e, e);
        }
    }

    public static void logMessage(Level level, String message) {
        log.log(level, message);
    }

    private static class LogFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            return record.getLevel().getName() + ": " + record.getMessage();
        }
    }
}
