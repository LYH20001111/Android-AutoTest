package com.newland.nsdk.core.api.common.utils;

public enum NativeDebugLevel {
    /**
     * Open native NSDK and driver logs.
     */
    ALL_ON,
    /**
     * Close native NSDK and driver logs.
     */
    ALL_OFF,
    /**
     * Open native NSDK logs.
     */
    NSDK_ON,
    /**
     * Close native NSDK logs.
     */
    NSDK_OFF,
    /**
     * Open all driver logs.
     */
    DRIVER_ON,
    /**
     * Close all driver logs.
     */
    DRIVER_OFF,
    /**
     * Open driver logs except when detecting cards.
     */
    DRIVER_DETECT_CARD_OFF;
}
