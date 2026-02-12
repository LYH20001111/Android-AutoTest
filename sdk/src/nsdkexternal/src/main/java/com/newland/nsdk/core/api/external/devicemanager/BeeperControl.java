package com.newland.nsdk.core.api.external.devicemanager;

/**
 * Control when to beep.
 */
public enum BeeperControl {
    /**
     * No limits for beeping.
     */
    NORMAL,

    /**
     * Only beep for key pad pressing.
     */
    KEY_PAD_ONLY,

    /**
     * Turn off beeper.
     */
    OFF
}
