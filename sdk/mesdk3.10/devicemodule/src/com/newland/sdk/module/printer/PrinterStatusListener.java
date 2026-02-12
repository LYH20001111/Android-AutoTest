package com.newland.sdk.module.printer;

/**
 * Author by wuhh, Date on 2020/4/1.
 */
public interface PrinterStatusListener {
    /**
     * if printer status change,will call back this.
     */
    public void onStatus(PrinterStatus status);
}
