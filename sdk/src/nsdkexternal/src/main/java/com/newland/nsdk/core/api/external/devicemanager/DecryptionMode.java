package com.newland.nsdk.core.api.external.devicemanager;

/**
 * Working key decryption mode.
 */
public enum DecryptionMode {
    /**
     * Use CBC mode for TDEA working key decryption (Default).
     */
    TDEA_CBC,

    /**
     * Use ECB mode for TDEA working key decryption.
     */
    TDEA_ECB
}
