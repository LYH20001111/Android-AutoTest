package com.newland.sdk.module.pin;

/**
 * @Description
 * @Author wuhh
 * @Date 2023/4/14
 */
public enum DukptDerivateUsage {
    /**
     * NONE
     */
    NONE,
    /**
     * key Encryption Key
     */
    KEK,
    /**
     * PIN Encryption
     */
    PIN,
    /**
     *  Message Authentication,generation
     */
    MAC_GEN,
    /**
     * Message Authentication,verification
     */
    MAC_VERIFY,
    /**
     * Message Authentication, both ways(When the key type is DES, it is consistent with DUKPT_DERIVATE_MAC_GEN)
     */
    MAC_BOTH,
    /**
     * Data Encryption, encrypt
     */
    DATA_ENC,
    /**
     * Data Encryption, decrypt
     */
    DATA_DEC,
    /**
     * Data Encryption, both ways (When the key type is DES, it is consistent with DUKPT_DERIVATE_DATA_ENC)
     */
    DATA_BOTH,
    /**
     * Key Derivation
     */
    DERIVATEKEY,
    /**
     * Initial Key Derivation
     */
    DERIVATEKEY_INITIAL,
}
