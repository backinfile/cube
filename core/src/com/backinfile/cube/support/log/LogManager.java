package com.backinfile.cube.support.log;

public class LogManager {
    static ILogAppender localAppender = new ILogAppender.ConsoleAppender();
    static LogLevel localLogLevel = LogLevel.INFO;

    public static Logger getLogger(String tag) {
        return new Logger(tag);
    }

    public static void setLocalAppender(ILogAppender appender) {
        LogManager.localAppender = appender;
    }

    public static void setLocalLogLevel(LogLevel localLogLevel) {
        LogManager.localLogLevel = localLogLevel;
    }
}

