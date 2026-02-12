package com.newland.nsdk.core.api.internal.pinentry;

/**
 * A listener used to monitor PIN entry events.
 */
public interface PINEntryListener {
    /**
     * Invoked when PIN input completed successfully.
     *
     * @param pinLen      PIN block length.
     * @param pinBlock    PIN block. When it is offline PIN, check if this PIN block is [0x90, 0x00].
     * @param ksn         KSN when using DUKPT key to encrypt PIN.
     */
    void onFinish(int pinLen, byte[] pinBlock, byte[] ksn);

    /**
     * Invoked when PIN entry timeout.
     */
    void onTimeout();

    /**
     * Invoked when number key pressed.
     */
    void onKeyPress();

    /**
     * Invoked when PIN entry is cancelled.
     */
    void onCancel();

    /**
     * Invoked when PIN is cleared.
     */
    void onClear();

    /**
     * Invoked when backspace.
     */
    void onBackspace();

    /**
     * Invoked when PIN entry error.
     *
     */
    void onError(int errorCode, String message);
}
