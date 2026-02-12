package com.newland.forth.spi.crypto.keystore;

/**
 * The enum Key generate method.
 */
public enum KeyGenerateMethod {
    /**
     * Sec kim clear key generate method.
     */
    SEC_KIM_CLEAR,
    /**
     * < Key is created from KeyData wich contains crear key data
     */
    SEC_KIM_CIPHER,
    /**
     * < Key is derived from ciphertext which is encrypted by the specified KEK.
     */
    SEC_KIM_TR31,
    /**
     * < Key is generated under TR-31 rules.
     */
    SEC_KIM_RANDOM,
    /**
     * < A random key is generated and stored in the specified index.
     */
    SEC_KIM_RANDOM_OUT,
    /*
     * Derive a new DUKPT key, the KSN will increase after derivation
     */
    SEC_KIM_DUKPT_DERIVE,
    /**
     * < Derive a new DUKPT key, the KSN will increase after derivation
     */
    SEC_KIM_DIVERSIFY_X,
    /**
     * < Customized key derivation method, e.g. In accordance to the Spanish requirements.?
     */
    SEC_KIM_GISKE,
    /**
     * Sec kim max key generate method.
     */
    SEC_KIM_MAX
}
