package com.platomix.lib.playerengine.util;

import android.util.Log;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class LogManager {
    public static final class Configuration {

        public static final boolean SWITCH_LOG_LEVEL_VERBOSE = true;
        public static final boolean SWITCH_LOG_LEVEL_DEBUG = true;
        public static final boolean SWITCH_LOG_LEVEL_INFO = true;
        public static final boolean SWITCH_LOG_LEVEL_WARN = true;
        public static final boolean SWITCH_LOG_LEVEL_ERROR = true;
        public static final boolean SWITCH_LOG_LEVEL_WTF = true;
    }

    private static LogManager sLogManager = new LogManager();

    private LogManager() {
    }

    public static LogManager getInstance() {
        if (sLogManager == null) {
            sLogManager = new LogManager();
        }

        return sLogManager;
    }

    private static String sTag;

    public LogManager tag(String object) {
        sTag = object;

        return sLogManager;
    }

    public LogManager debug(Object... objects) {
        if (Configuration.SWITCH_LOG_LEVEL_DEBUG) {
            log(Level.D, sTag, buildMessage(objects).toString().trim(), null);
        }

        return sLogManager;
    }

    public LogManager verbose(Object... objects) {
        if (Configuration.SWITCH_LOG_LEVEL_VERBOSE) {
            log(Level.V, sTag, buildMessage(objects).toString().trim(), null);
        }

        return sLogManager;
    }

    public LogManager info(Object... objects) {
        if (Configuration.SWITCH_LOG_LEVEL_INFO) {
            log(Level.I, sTag, buildMessage(objects).toString().trim(), null);
        }

        return sLogManager;
    }

    public LogManager error(Object... objects) {
        if (Configuration.SWITCH_LOG_LEVEL_ERROR) {
            log(Level.E, sTag, buildMessage(objects).toString().trim(), null);
        }

        return sLogManager;
    }

    public LogManager warn(Object... objects) {
        if (Configuration.SWITCH_LOG_LEVEL_WARN) {
            log(Level.W, sTag, buildMessage(objects).toString().trim(), null);
        }

        return sLogManager;
    }

    public LogManager wtf(Object... objects) {
        if (Configuration.SWITCH_LOG_LEVEL_WTF) {
            log(Level.WTF, sTag, buildMessage(objects).toString().trim(), null);
        }

        return sLogManager;
    }

    private StringBuffer buildMessage(Object... objects) {
        StringBuffer message = new StringBuffer();

        for (int i = 0; i < objects.length; i++) {
            if (objects[i] != null) {
                if (objects[i] instanceof boolean[]) {
                    boolean[] objecters = (boolean[]) objects[i];
                    message.append("{");

                    for (int j = 0; j < objecters.length; j++) {
                        message.append(objecters[j]);

                        if (j != (objecters.length - 1)) {
                            message.append(" ");
                        }
                    }

                    message.append("}");
                } else if (objects[i] instanceof char[]) {
                    char[] objecters = (char[]) objects[i];
                    message.append("{");

                    for (int j = 0; j < objecters.length; j++) {
                        message.append(objecters[j]);

                        if (j != (objecters.length - 1)) {
                            message.append(" ");
                        }
                    }

                    message.append("}");
                } else if (objects[i] instanceof byte[]) {
                    byte[] objecters = (byte[]) objects[i];
                    message.append("{");

                    for (int j = 0; j < objecters.length; j++) {
                        message.append(objecters[j]);

                        if (j != (objecters.length - 1)) {
                            message.append(" ");
                        }
                    }

                    message.append("}");
                } else if (objects[i] instanceof short[]) {
                    short[] objecters = (short[]) objects[i];
                    message.append("{");

                    for (int j = 0; j < objecters.length; j++) {
                        message.append(objecters[j]);

                        if (j != (objecters.length - 1)) {
                            message.append(" ");
                        }
                    }

                    message.append("}");
                } else if (objects[i] instanceof int[]) {
                    int[] objecters = (int[]) objects[i];
                    message.append("{");

                    for (int j = 0; j < objecters.length; j++) {
                        message.append(objecters[j]);

                        if (j != (objecters.length - 1)) {
                            message.append(" ");
                        }
                    }

                    message.append("}");
                } else if (objects[i] instanceof long[]) {
                    long[] objecters = (long[]) objects[i];
                    message.append("{");

                    for (int j = 0; j < objecters.length; j++) {
                        message.append(objecters[j]);

                        if (j != (objecters.length - 1)) {
                            message.append(" ");
                        }
                    }

                    message.append("}");
                } else if (objects[i] instanceof float[]) {
                    float[] objecters = (float[]) objects[i];
                    message.append("{");

                    for (int j = 0; j < objecters.length; j++) {
                        message.append(objecters[j]);

                        if (j != (objecters.length - 1)) {
                            message.append(" ");
                        }
                    }

                    message.append("}");
                } else if (objects[i] instanceof double[]) {
                    double[] objecters = (double[]) objects[i];
                    message.append("{");

                    for (int j = 0; j < objecters.length; j++) {
                        message.append(objecters[j]);

                        if (j != (objecters.length - 1)) {
                            message.append(" ");
                        }
                    }

                    message.append("}");
                } else if (objects[i] instanceof Object[]) {
                    Object[] objecters = (Object[]) objects[i];
                    message.append("{");

                    for (int j = 0; j < objecters.length; j++) {
                        message.append(objecters[j] != null ? objecters[j]
                                : "null");

                        if (j != (objecters.length - 1)) {
                            message.append(" ");
                        }
                    }

                    message.append("}");
                } else if (objects[i] instanceof Set<?>) {
                    Set<?> sets = ((Set<?>) objects[i]);
                    Iterator<?> iterator = sets.iterator();
                    int counter = 0;

                    message.append("{");
                    while (iterator.hasNext()) {
                        counter++;

                        Object objecter = iterator.next();

                        message.append(objecter != null ? objecter : "null");

                        if (counter != sets.size()) {
                            message.append(" ");
                        }
                    }

                    message.append("}");
                } else if (objects[i] instanceof List<?>) {
                    List<?> list = (List<?>) objects[i];

                    message.append("{");
                    for (int j = 0; j < list.size(); j++) {
                        message.append(list.get(j) != null ? list.get(j)
                                .toString() : "null");

                        if (j != (list.size() - 1)) {
                            message.append(" ");
                        }
                    }

                    message.append("}");
                } else {
                    message.append(objects[i].toString());
                }

            } else {
                message.append("null");
            }

            if (i != (objects.length - 1)) {
                message.append(" ");
            }
        }

        return message;
    }

    private static void resetTag() {
        sTag = null;
    }

    private static String buildDefaultTag(StackTraceElement[] stackTraceElements) {
        StringBuffer tag = new StringBuffer();

        String fileName = stackTraceElements[2].getFileName();
        String className = stackTraceElements[2].getClassName();
        String methodName = stackTraceElements[2].getMethodName();
        int lineNumber = stackTraceElements[2].getLineNumber();

        tag.append("[").append(fileName).append("]").append("[")
                .append(className).append("]").append("[").append(methodName)
                .append("]").append("[").append(lineNumber).append("]");

        return tag.toString();
    }

    public static void v(String message) {
        if (Configuration.SWITCH_LOG_LEVEL_VERBOSE) {
            log(Level.D, sTag, message, null);
        }
    }

    public static void v(String tag, String message) {
        if (Configuration.SWITCH_LOG_LEVEL_VERBOSE) {
            log(Level.V, tag, message, null);
        }
    }

    public static void v(String tag, String message, Throwable throwable) {
        if (Configuration.SWITCH_LOG_LEVEL_VERBOSE) {
            log(Level.V, tag, message, throwable);
        }
    }

    public static void d(String message) {
        if (Configuration.SWITCH_LOG_LEVEL_DEBUG) {
            log(Level.I, "kiwi_log", message, null);
        }
    }

    public static void d(String tag, String message) {
        if (Configuration.SWITCH_LOG_LEVEL_DEBUG) {
            log(Level.I, tag, message, null);
        }
    }



    public static void d(String tag, String message, Throwable throwable) {
        if (Configuration.SWITCH_LOG_LEVEL_DEBUG) {
            log(Level.D, tag, message, throwable);
        }
    }

    public static void i(String message) {
        if (Configuration.SWITCH_LOG_LEVEL_INFO) {
            log(Level.I, sTag, message, null);
        }
    }

//    public static void k(String message) {
//        if (Configuration.SWITCH_LOG_LEVEL_INFO) {
//            log(Level.I, "kiwi_log", message, null);
//        }
//    }

    public static void i(String tag, String message) {
        if (Configuration.SWITCH_LOG_LEVEL_INFO) {
            log(Level.I, tag, message, null);
        }
    }

    public static void i(String tag, String message, Throwable throwable) {
        if (Configuration.SWITCH_LOG_LEVEL_INFO) {
            log(Level.I, tag, message, throwable);
        }
    }

    public static void w(String message) {
        if (Configuration.SWITCH_LOG_LEVEL_WARN) {
            log(Level.W, sTag, message, null);
        }
    }

    public static void w(String tag, String message) {
        if (Configuration.SWITCH_LOG_LEVEL_WARN) {
            log(Level.W, tag, message, null);
        }
    }

    public static void w(String tag, String message, Throwable throwable) {
        if (Configuration.SWITCH_LOG_LEVEL_WARN) {
            log(Level.W, tag, message, throwable);
        }
    }

    public static void e(String message) {
        if (Configuration.SWITCH_LOG_LEVEL_ERROR) {
            log(Level.E, sTag, message, null);
        }
    }

    public static void e(String tag, String message) {
        if (Configuration.SWITCH_LOG_LEVEL_ERROR) {
            log(Level.E, tag, message, null);
        }
    }

    public static void e(String tag, String message, Throwable throwable) {
        if (Configuration.SWITCH_LOG_LEVEL_ERROR) {
            log(Level.E, tag, message, throwable);
        }
    }

    public static void wtf(String message) {
        if (Configuration.SWITCH_LOG_LEVEL_WTF) {
            log(Level.WTF, sTag, message, null);
        }
    }

    public static void wtf(String tag, String message) {
        if (Configuration.SWITCH_LOG_LEVEL_WTF) {
            log(Level.WTF, tag, message, null);
        }
    }

    public static void wtf(String tag, String message, Throwable throwable) {
        if (Configuration.SWITCH_LOG_LEVEL_WTF) {
            log(Level.WTF, tag, message, throwable);
        }
    }

    private static enum Level {
        V, D, I, E, W, WTF
    }

    private static void log(Level level, String tag, String message,
                            Throwable throwable) {
        switch (level) {
            case V: {
                if (throwable == null) {
                    Log.v(tag != null ? tag : buildDefaultTag(new Throwable().getStackTrace()),
                            message != null ? message : null);
                } else {
                    Log.v(tag != null ? tag : buildDefaultTag(new Throwable()
                                    .getStackTrace()),
                            message != null ? message : null, throwable);
                }
            }
            break;
            case D: {
                if (throwable == null) {
                    Log.d(tag != null ? tag : buildDefaultTag(new Throwable()
                            .getStackTrace()), message != null ? message : null);
                } else {
                    Log.d(tag != null ? tag : buildDefaultTag(new Throwable()
                                    .getStackTrace()),
                            message != null ? message : null, throwable);
                }
            }
            break;
            case I: {
                if (throwable == null) {
                    Log.i(tag != null ? tag : buildDefaultTag(new Throwable()
                            .getStackTrace()), message != null ? message : null);
                } else {
                    Log.i(tag != null ? tag : buildDefaultTag(new Throwable()
                                    .getStackTrace()),
                            message != null ? message : null, throwable);
                }
            }
            break;
            case E: {
                if (throwable == null) {
                    Log.i(tag != null ? tag : buildDefaultTag(new Throwable()
                            .getStackTrace()), message != null ? message : null);
                } else {
                    Log.i(tag != null ? tag : buildDefaultTag(new Throwable()
                                    .getStackTrace()),
                            message != null ? message : null, throwable);
                }
            }
            break;
            case W: {
                if (throwable == null) {
                    Log.w(tag != null ? tag : buildDefaultTag(new Throwable()
                            .getStackTrace()), message != null ? message : null);
                } else {
                    Log.w(tag != null ? tag : buildDefaultTag(new Throwable()
                                    .getStackTrace()),
                            message != null ? message : null, throwable);
                }
            }
            break;
            case WTF: {
                if (throwable == null) {
                    Log.wtf(tag != null ? tag : buildDefaultTag(new Throwable()
                            .getStackTrace()), message != null ? message : null);
                } else {
                    Log.wtf(tag != null ? tag : buildDefaultTag(new Throwable()
                                    .getStackTrace()),
                            message != null ? message : null, throwable);
                }
            }
            break;
            default:
                break;
        }

        resetTag();
    }
}