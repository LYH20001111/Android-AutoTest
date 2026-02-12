package com.newland.sdk.module.externalKeyboard;

public interface KeyboardListener {

    /**
     *  Start input
     */
    void onStart();

    /**
     * Input error
     */
    void onError();

    /**
     * Timeout
     */
    void onTimeOut();

    /**
     * key input
     * @param keyCode    Input the key value
     * @param currValue  Current input value (Support 6 Numbers plus 1 decimal point, maximum support 2 decimal places.)
     */
    void onKeyPress(KeyBoardCode keyCode, String currValue);
}
