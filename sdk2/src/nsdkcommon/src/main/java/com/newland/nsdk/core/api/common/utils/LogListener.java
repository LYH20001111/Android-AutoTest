package com.newland.nsdk.core.api.common.utils;

/**
 * This listener allows apps to handle logs by themselves.
 */
public interface LogListener {
    /**
     * This is triggered after NSDK writes the log to logcat.
     *
     * @param tag Log tag.
     * @param message Log content.
     */
    void onLog(String tag, String message);
}
