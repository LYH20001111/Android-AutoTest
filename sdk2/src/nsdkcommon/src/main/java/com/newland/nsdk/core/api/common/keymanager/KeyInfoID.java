package com.newland.nsdk.core.api.common.keymanager;

/**
 * Indicates what key info to get.
 */
public enum KeyInfoID {
    /**
     * To get key length.
     */
    KEY_LEN,
    /**
     * To get KCV.
     */
    KCV,
    /**
     * To get KSN.
     */
    KSN,

    /**
     * To get certificate.
     */
    CERTIFICATE,

    /**
     * To get PKEY certificate length.
     */
    CERTIFICATE_LEN,

    /**
     * To get public key.
     */
    PUBLIC_KEY,

    /**
     * To get CMAC KCV.
     */
    CMAC_KCV,

    /**
     * To get CA cert.
     */
    RKI_CA_CERT,

    /**
     * To get CA public key.
     */
    RKI_CA_PUBKEY
}
