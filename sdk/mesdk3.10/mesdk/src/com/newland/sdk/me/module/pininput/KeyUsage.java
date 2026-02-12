package com.newland.sdk.me.module.pininput;

/**
 * Author by wuhh, Date on 2020/3/4.
 */
public enum KeyUsage {
    /**
     * Master key for all key.
     */
    MASTER,
    /**
     * Master key ONLY for PIN key.
     */
    MASTER_PIN,
    /**
     * Master key ONLY for MAC generation key.
     */
    MASTER_MAC,
    /**
     * Master key ONLY for data encryption and decryption key.
     */
    MASTER_DATA,
    /**
     * Master key ONLY for data encryption key.
     */
    MASTER_DATA_ENC,
    /**
     * Master key ONLY for TR31 key block.
     */
    MASTER_TR31,
    /**
     * Working key for pin.
     */
    WORKINGKEY_PIN,
    /**
     * Working key for mac.
     */
    WORKINGKEY_MAC,
    /**
     * Working key for encryp or decrypt data.
     */
    WORKINGKEY_DATA,
    /**
     * Working key for only encryp data.
     */
    WORKINGKEY_DATA_ENC_ONLY,
    /**
     * Dukpt Key.
     */
    DUKPT,
}
