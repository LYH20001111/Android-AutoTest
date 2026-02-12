package com.newland.nsdk.core.api.external.keyboard;

/**
 * Listener of keyboard entry.
 */
public interface KeyboardListener {
    /**
     * Invoked when error occurs.
     *
     * @param errorCode Error code, see {@link com.newland.nsdk.core.api.common.ErrorCode}
     * @param message   Error message.
     */
    void onError(int errorCode, String message);

    /**
     * Invoked when key input success.
     *
     * @param inputLen      How many characters entered by user.
     * @param encryptedData The encrypted data of input characters.
     */
    void onSuccess(int inputLen, byte[] encryptedData);

    /**
     * Invoked when timeout between two key presses.
     */
    void onTimeout();

    /**
     * Invoked when key entry is cancelled.
     */
    void onCancel();
}
