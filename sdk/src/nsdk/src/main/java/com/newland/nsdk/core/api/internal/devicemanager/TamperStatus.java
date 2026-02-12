package com.newland.nsdk.core.api.internal.devicemanager;

/**
 * Tamper status.
 */
public enum TamperStatus {
    /**
     * Device is OK.
     */
    NONE(0),
    /**
     * Hardware tamper.
     */
    HARDWARE(1),
    /**
     * Security register error.
     */
    SEC_CONFIG(2),
    /**
     * File check error.
     */
    CHECK_FILE(4),
    /**
     * FM module disconnected.
     */
    FM_DISCONNECT(8),
    /**
     * FM module tamper occurred.
     */
    FM_TAMPER(16),
    /**
     * Device is disabled.
     */
    DEVICE_DISABLED(0x100);

    int code;

    TamperStatus(int code) {
        this.code = code;
    }
    public int getCode() {
        return code;
    }
}
