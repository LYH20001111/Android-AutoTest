package com.newland.nsdk.core.api.common.keymanager;

/**
 * Cipher mode.
 */
public enum CipherMode {
    /**
     * Electronic Codebook mode
     *
     */
    ECB,
    /**
     * Cipher Block Chaining mode
     */
    CBC,
    /**
     * Cipher Feedback mode
     */
    CFB,
    /**
     * Output Feedback mode
     */
    OFB,
    /**
     * Counter mode
     */
    CTR,
    /**
     * Galois/Counter mode
     */
    GCM,
    /**
     * Stream
     */
    STREAM,
    /**
     * Counter with CBC-MAC
     */
    CCM,
}
