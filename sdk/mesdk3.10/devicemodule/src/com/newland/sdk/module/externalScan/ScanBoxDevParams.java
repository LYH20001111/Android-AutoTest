package com.newland.sdk.module.externalScan;

/**
 * Scan the device system parameters
 */
public enum ScanBoxDevParams {
    /**
     *The equipment SN is usually set by the factory and can only be set once.
     *The length is fixed at 12 bytes
     */
    SN,
    /**
     *The equipment PN is normally set by the factory and can only be set once.
     *The length is fixed at 15 bytes
     */
    PN,
    /**
     *The equipment CSN
     *The length cannot be greater than 24 bytes
     */
    CSN,
    /**
     *The equipment PID
     */
    PID,
    /**
     *The equipment VID
     */
    VID,
    /**
     *Application version
     */
    APP,
    /**
     *Firmware Version
     */
    MASTER,
    /**
     *BOOT Version
     */
    BOOT
}
