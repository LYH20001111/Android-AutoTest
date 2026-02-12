package com.newland.nsdk.core.api.external.keyboard;

public interface InputListener {
    /**
     * Invoked when input finished.
     */
    void onComplete();

    /**
     * Invoked when error occurred.
     * @param code     The error code.
     * @param message  The error message.
     */
    void onError(int code, String message);
}
