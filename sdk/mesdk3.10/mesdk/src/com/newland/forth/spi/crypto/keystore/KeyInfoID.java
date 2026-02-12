package com.newland.forth.spi.crypto.keystore;

/**
 * The enum Key info id.
 */
public enum KeyInfoID {
    /**
     * Sec key info keylen key info id.
     */
    SEC_KEY_INFO_KEYLEN,
    /**
     * Sec key info kcv key info id.
     */
    SEC_KEY_INFO_KCV,
    /**
     * Sec key info ksn key info id.
     */
    SEC_KEY_INFO_KSN,

    SEC_KEY_INFO_CERT,
    SEC_KEY_INFO_PKEY_CERTLEN,
    SEC_KEY_INFO_PKEY_PUBKEY,
    SEC_KEY_INFO_KCV_CMAC,
    SEC_KEY_INFO_RKI_CA_CERT,
    SEC_KEY_INFO_RKI_CA_PUBKEY,
}
