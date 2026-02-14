package com.newland.nsdk.core.api.common.keymanager;

/**
 * key injection method.
 */
public enum KeyGenerateMethod {
    /**
     * Load a clear key.
     */
    CLEAR(0),
    /**
     * Load a cipher key which is encrypted by the specified KEK.
     */
    CIPHER(1),
    /**
     * Key is generated under TR-31 rules.
     */
    TR31(2),
    /**
     * Generate a random key and store it in the specified index.
     */
    RANDOM(3),
    /**
     * Generate and return a random key which is encrypted by the specified master key.
     */
    RANDOM_OUT(4),
    /**
     * Derive a new DUKPT key overwriting the old one, the KSN will increase after derivation.
     */
    DUKPT_DERIVE(5),
    /**
     * Customized key derivation method, e.g. In accordance to the Spanish requirements.
     */
    DIVERSIFY_X(6),
    /**
     * Key is generated under GISKE rules.
     */
    GISKE(7),

    /**
     * Generate and return a random TR31 key which is encrypted by the specified master key.
     */
    RANDOM_OUT_TR31(8),

    /**
     *
     */
    CIPHER_VTB(9),

    /**
     *
     */
    AES_DUKPT_UPDATE_IK(10),

    /**
     * Generate a new key with HKDF.
     */
    HKDF(12),

    /**
     * Generate a new key with ANSI-X9143
     */
    ANSI_X9143(16),

    /**
     * A customized key generation method, which is based on RedSys's requirement and similar to the TR31 method.
     */
    PUP_2(17);

    int code;

    KeyGenerateMethod(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
