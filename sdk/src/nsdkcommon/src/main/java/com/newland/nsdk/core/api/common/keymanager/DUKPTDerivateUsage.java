package com.newland.nsdk.core.api.common.keymanager;

public enum DUKPTDerivateUsage {
    /**
     * None
     */
    NONE,
    /**
     * Key encryption key
     */
    KEK,
    /**
     * PIN encryption
     */
    PIN,
    /**
     * Message Authentication, generation
     */
    MAC_GEN,
    /**
     * Message Authentication, verification
     */
    MAC_VERIFY,
    /**
     * Message Authentication, both ways (When the key type is DES, it is consistent with DUKPT_DERIVATE_MAC)
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
    /*
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
    DERIVATEKEY_INITIAL
}
