package com.newland.nsdk.core.api.common.utils;


import android.util.Log;

/**
 * Log helper class.
 */
public class LogUtils {

    private static LogLevel logLevel = LogLevel.OFF;

    private static LogListener mListener;

    /**
     * Sets log listener.
     *
     * @param listener Listens to logs.
     */
    public static void setLogListener(LogListener listener) {
        mListener = listener;
    }

    /**
     * Writes verbose logs.
     *
     * @param tag Log tag.
     * @param msg Message to log.
     */
    public static void v(String tag, String msg) {
        if (logLevel.ordinal() >= LogLevel.VERBOSE.ordinal()) {
            Log.v(tag, msg);
            if (mListener != null) {
                mListener.onLog(tag, msg);
            }
        }
    }

    /**
     * Writes debug logs.
     *
     * @param tag Log tag.
     * @param msg Message to log.
     */
    public static void d(String tag, String msg) {
        if (logLevel.ordinal() >= LogLevel.DEBUG.ordinal()) {
            Log.d(tag, msg);
            if (mListener != null) {
                mListener.onLog(tag, msg);
            }
        }
    }

    /**
     * Writes info logs.
     *
     * @param tag Log tag.
     * @param msg Message to log.
     */
    public static void i(String tag, String msg) {
        if (logLevel.ordinal() >= LogLevel.INFO.ordinal()) {
            Log.i(tag, msg);
            if (mListener != null) {
                mListener.onLog(tag, msg);
            }
        }
    }

    /**
     * Writes warn logs.
     *
     * @param tag Log tag.
     * @param msg Message to log.
     */
    public static void w(String tag, String msg) {
        if (logLevel.ordinal() >= LogLevel.WARN.ordinal()) {
            Log.w(tag, msg);
            if (mListener != null) {
                mListener.onLog(tag, msg);
            }
        }
    }

    /**
     * Writes warn logs.
     *
     * @param tag Log tag.
     * @param msg Message to log.
     * @param e An exception to log. This value may be null.
     */
    public static void w(String tag, String msg, Throwable e) {
        if (logLevel.ordinal() >= LogLevel.WARN.ordinal()) {
            Log.w(tag, msg, e);
            if (mListener != null) {
                mListener.onLog(tag, msg);
            }
        }
    }

    /**
     * Writes error logs.
     *
     * @param tag Log tag.
     * @param msg Message to log.
     */
    public static void e(String tag, String msg) {
        if (logLevel.ordinal() >= LogLevel.ERROR.ordinal()) {
            Log.e(tag, msg);
            if (mListener != null) {
                mListener.onLog(tag, msg);
            }
        }
    }

    /**
     * Sets debug level to control log output.
     *
     * @param level Debug level, see {@link LogLevel}.
     */
    public static void setLogLevel(LogLevel level) {
        logLevel = level;
    }
}