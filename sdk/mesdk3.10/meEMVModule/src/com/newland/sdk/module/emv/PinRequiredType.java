package com.newland.sdk.module.emv;

/**
 * Emv pin input type
 *
 *
 */
public enum PinRequiredType {

    /**
     * Offline
     */
    OFFLINE,
    /**
     * Last offline PIN
     */
    LAST_OFFLINE,
    /**
     * Online
     */
    ONLINE,
    /**
     * Electronic cash online<p>
     * Transfer to online transaction due to insurance electronic cash amount
     */
    EC_ONLINE

}
