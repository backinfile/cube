package com.backinfile.cube.support.log;

import com.backinfile.cube.support.Utils;

public interface ILogAppender {
    void append(LogLevel logLevel, String tag, String message, Object[] args, Throwable throwable);


    public static class ConsoleAppender implements ILogAppender {
        @Override
        public void append(LogLevel logLevel, String tag, String message, Object[] args, Throwable throwable) {
            if (args != null) {
                message = Utils.format(message, args);
            }
            System.out.println("[" + tag + "][" + logLevel.name() + "] " + message);
            if (throwable != null) {
                throwable.printStackTrace();
            }
        }
    }

    public static class EmptyAppender implements ILogAppender {
        @Override
        public void append(LogLevel logLevel, String tag, String message, Object[] args, Throwable throwable) {
        }
    }
}
