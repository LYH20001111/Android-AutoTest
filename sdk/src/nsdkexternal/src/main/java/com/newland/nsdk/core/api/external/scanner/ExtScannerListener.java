package com.newland.nsdk.core.api.external.scanner;

/**
 * A listener used to monitor scanning result.
 */
public interface ExtScannerListener {

    /**
     * Invoked when scanning success.
     *
     * @param result Scanning result.
     */
    void onSuccess(String result);

    /**
     * Invoked when scanning failed.
     *
     * @param code    Error code.
     * @param message Error message.
     */
    void onError(int code, String message);

    /**
     * Invoked when scanning timeout.
     */
    void onTimeout();
}
