package com.newland.nsdk.core.api.common.crypto;

/**
 * Encoding mode for asymmetric keys.
 */
public enum AsymEncodingMode {
    /**
     * None(Not yet supported)
     */
    NONE,
    /**
     * PKCS V15
     */
    PKCS_V15,
    /**
     * PKCS V21(OAEP, RSA-PSS)
     */
    PKCS_V21,
    /**
     * ECC ASN1 format
     */
    ECC_ASN1
}
