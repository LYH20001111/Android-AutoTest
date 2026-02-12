package com.newland.sdk.module.externalKeyboard;

/**
 * @author Suyuming
 * @description External keyboard input.
 * @create 2019/7/30
 */
public interface ExtKeyboardModule {

    /**
     * External keyboard is available
     *
     * @return true if success,false if error.
     * @since 3.10.01
     */
    boolean isValid();

    /**
     * Start external keyboard input
     *
     * @param timeout  The timeout of search card
     * @param listener {@link KeyboardListener}
     * @return true if success,false if error.
     * @since 3.10.01
     */
    boolean startKeyInput(int timeout, KeyboardListener listener);

    /**
     * Stop external keyboard input.
     *
     * @return true if success,false if error.
     * @since 3.10.01
     */
    boolean stopInput();

    /**
     * Display external keyboard message
     *
     * @param message Maximum support for 10 characters, can contain Numbers and characters, using utf-8 encoding,
     *                including one decimal point.
     * @return true if success,false if error.
     * @since 3.10.01
     */
    boolean showMessage(String message);

    /**
     * Sets the key that needs to trigger the callback.
     * By default all keys will be called back in showDigitalLedByKeyboard fun.
     *
     * @param validKeys {@link KeyBoardCode}
     * @since 3.10.01
     */
    void setValidKeys(KeyBoardCode[] validKeys);

    /**
     * Set whether the click sound on the keyboard is turned on.
     *
     * @param clickSound {@code true} turn on the click sound
     *                   {@code false} turn off the click sound
     * @since 3.10.01
     */
    void setClickSound(boolean clickSound);
}
