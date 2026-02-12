package com.newland.nsdk.core.api.external.pinentry;

/**
 * A listener used to monitor the result of PIN entry.
 */
public interface ExtPINEntryListener {
    /**
     * Invoked when online PIN entry success.
     *
     * @param pinLen   Actual length of PIN.
     * @param pinBlock Encrypted PIN block.
     * @param dukptSN  If the PIN encryption is not DUKPT, this will be null.
     */
    void onOnlineSuccess(int pinLen, byte[] pinBlock, byte[] dukptSN);

    /**
     * Invoked when offline PIN entry success.
     *
     * @param pinLen   Actual length of PIN.
     * @param pinBlock Encrypted PIN block.
     * @param randomKey If {@link ExtOfflinePINParameters#isRandomProtectMode()} is set to "true" when starting offline PIN entry, random key will be returned.
     */
    void onOfflineSuccess(int pinLen, byte[] pinBlock, byte[] randomKey);

    /**
     * Invoked when PIN entry error.
     *
     * @param errorCode Error code.
     * @param message   Error message.
     */
    void onError(int errorCode, String message);

    /**
     * Invoked when timeout between two key presses.
     */
    void onTimeout();

    /**
     * Invoked when PIN entry cancelled.
     */
    void onCancel();
}
