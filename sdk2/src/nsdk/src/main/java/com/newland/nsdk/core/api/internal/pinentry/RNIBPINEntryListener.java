package com.newland.nsdk.core.api.internal.pinentry;

public interface RNIBPINEntryListener extends PINEntryListener{
    /**
     * Invoked when slid to a number key button.
     */
    void onSlidNumberKey();

    /**
     * Invoked when slid to the key with no digit.
     */
    void onSlidNoDigitKey();

    /**
     * Invoked when slid to Backspace button.
     */
    void onSlidBackSpace();

    /**
     * Invoked when slid to Enter button.
     */
    void onSlidEnter();

    /**
     * Invoked when slid to Cancel button.
     */
    void onSlidCancel();

    /**
     * Invoked when slid above the keypad area.
     */
    void onSlidUp();

    /**
     * Invoked when slid below the keypad area.
     */
    void onSlidDown();

    /**
     * Invoked when slid to the left of the keypad area.
     */
    void onSlidLeft();

    /**
     * Invoked when slid to the right of the keypad area.
     */
    void onSlidRight();
}
