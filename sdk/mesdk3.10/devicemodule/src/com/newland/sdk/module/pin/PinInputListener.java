package com.newland.sdk.module.pin;

/**
 * Created by YJF on 2019/7/23 14:52
 */
public interface PinInputListener {
    /**
     *  Press number key
     */
    public void onKeyPress();

    /**
     * Press "Backspace" key
     */
    public void onBackspace();

    /**
     * press "Cancel" key
     */
    public void onCancel();

    /**
     * press "Confirm" key.
     * @param pinblockLen the pinblock length. 0: pinblock is null
     * @param pinblock the pinblock is null,if user press the confirmation button directly
     * @param ksn ksn
     */
    public void onFinish(int pinblockLen,byte[] pinblock,byte[] ksn);

    /**
     * input pin timeout
     */
    public void onTimeout();

    /**
     * input pin failed
     * @param errorCode error code
     * @param message error message
     */
    public void onError(int errorCode,String message);
}
