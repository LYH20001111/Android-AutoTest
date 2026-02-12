package com.newland.nsdk.core.api.common.keymanager;

/**
 * KDF type
 */
public enum KDFType {
    /**
     * HKDF
     */
    HKDF,
    /**
     * HKDF algorithm used for google smart tap.
     */
    HKDF_GOOGLE_SMART_TAP,
    /**
     * KDF type used for mutual session key generation.
     */
    PEER,
    /**
     * KDF type specified that only the expansion procedure is used.
     */
    ONLY_EXPAND,
}
