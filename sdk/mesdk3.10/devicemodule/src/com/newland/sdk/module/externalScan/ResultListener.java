package com.newland.sdk.module.externalScan;

public interface ResultListener {
    /**
     * Scan and listen for a successful callback
     * @param data Sweep code results
     */
    void onSuccess(String data);

    /**
     * Scan the timeout callback
     */
    void onTimeOut();

    /**
     * Scan for error callbacks
     * @param errorCode  Error code
     * @param message       description
     */
    void onError(int errorCode, String message);
}
