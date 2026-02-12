package com.newland.nsdk.core.api.external.display;

/**
 * Listener of display result.
 */
public interface DisplayListener {
    /**
     * Invoked on error.
     *
     * @param errorCode Error code.
     * @param message Error message.
     */
    void onError(int errorCode, String message);

    /**
     * Invoked on success.
     */
    void onSuccess();
}
