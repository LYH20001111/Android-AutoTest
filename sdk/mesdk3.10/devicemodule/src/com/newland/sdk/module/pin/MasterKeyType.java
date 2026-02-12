package com.newland.sdk.module.pin;

/**
 * Author by wuhh, Date on 2020/3/6.
 */
public enum MasterKeyType {
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
     *  Master key ONLY for data encryption and decryption key.
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
}
