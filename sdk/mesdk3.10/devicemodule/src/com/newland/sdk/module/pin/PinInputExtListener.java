package com.newland.sdk.module.pin;

/**
 * @author youjf
 * @description
 * @date 2020/9/24
 * @since V3.10.28
 */
public interface PinInputExtListener extends PinInputListener{
    /**
     * @param rspKeyCode 0x0F: clear passwords.
     */
    public void onNotifyStep(byte rspKeyCode);
}
