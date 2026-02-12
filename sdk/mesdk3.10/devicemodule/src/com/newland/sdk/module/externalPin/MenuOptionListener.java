package com.newland.sdk.module.externalPin;

/**
 * @Description For {@link ExtPinpadModule#showMenuOption}
 * @Author linsi
 * @Date 2025/02/28
 */
public interface MenuOptionListener {
    /**
     * Return the menu option number
     *
     * @param option
     */
    void onKeyPress(int option);

    /**
     * Select menu option time out
     */
    void onTimeout();

    /**
     * Cancel the menu option selection
     */
    void onCancel();

    /**
     * Error
     * 1:Parameter Error
     * 2:Unsupported
     * 45:Command Length Error
     * 55:Unsupported
     */
    void onError(int errorCode,String errorMsg);
}
