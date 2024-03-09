package com.smalls.javafxappointmentmanager.utils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.*;

public class CustomLogger {

    private static final Logger log = Logger.getLogger(
            CustomLogger.class.getName()
    );



    public static void initialize(String logFile) {
        try {
            FileHandler fh = new FileHandler(logFile, true);
            log.addHandler(fh);
            fh.setFormatter(new LogFormatter());
        } catch (IOException e) {
            throw new RuntimeException("Error initializing logger: ", e);
        }
    }

    public static void logMessage(Level level, String message) {
        String nowDateTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern(
                        "MM-dd-yy hh:mm:ss a"
                ));
        message = nowDateTime + " " + message;
        log.log(level, message);
    }

    private static class LogFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            return record.getLevel().getName() + ": " + record.getMessage() + "\n";
        }
    }
}
