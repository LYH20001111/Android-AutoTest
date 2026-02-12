package com.newland.nsdk.core.api.internal.devicemanager;

public enum AntiRemovalStatus {
    /**
     * Disable anti-removal detection.
     */
    DISARMED,
    /**
     * Triggered when device removal detection.
     */
    LOCKED,
    /**
     * Enable anti-removal detection
     */
    ARMED,
}
