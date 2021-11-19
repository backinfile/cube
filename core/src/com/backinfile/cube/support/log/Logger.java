package com.backinfile.cube.support.log;

public class Logger {
    private final String tag;

    public Logger(String tag) {
        this.tag = tag.toUpperCase();
    }

    public void debug(String message, Object... args) {
        if (LogManager.localLogLevel.ordinal() >= LogLevel.DEBUG.ordinal()) {
            LogManager.localAppender.append(LogLevel.DEBUG, tag, message, args, null);
        }
    }

    public void info(String message, Object... args) {
        if (LogManager.localLogLevel.ordinal() >= LogLevel.INFO.ordinal()) {
            LogManager.localAppender.append(LogLevel.INFO, tag, message, args, null);
        }
    }

    public void warn(String message, Object... args) {
        if (LogManager.localLogLevel.ordinal() >= LogLevel.WARN.ordinal()) {
            LogManager.localAppender.append(LogLevel.WARN, tag, message, args, null);
        }
    }

    public void error(String message, Object... args) {
        if (LogManager.localLogLevel.ordinal() >= LogLevel.ERROR.ordinal()) {
            LogManager.localAppender.append(LogLevel.ERROR, tag, message, args, null);
        }
    }

    public void error(Throwable throwable, String message, Object... args) {
        if (LogManager.localLogLevel.ordinal() >= LogLevel.ERROR.ordinal()) {
            LogManager.localAppender.append(LogLevel.ERROR, tag, message, args, throwable);
        }
    }

    public void error(Throwable throwable) {
        if (LogManager.localLogLevel.ordinal() >= LogLevel.ERROR.ordinal()) {
            LogManager.localAppender.append(LogLevel.ERROR, tag, "", null, throwable);
        }
    }

    public void error(String message, Throwable throwable) {
        if (LogManager.localLogLevel.ordinal() >= LogLevel.ERROR.ordinal()) {
            LogManager.localAppender.append(LogLevel.ERROR, tag, message, null, throwable);
        }
    }

}
