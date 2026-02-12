package com.newland.sdk.module.pin;

/**
 * The block cipher modes
 *
 * @since 1.0
 */
public enum CipherMode {
    /**
     * Electronic Code Book
     */
    ECB,
    /**
     * Cipher Block Chaining
     */
    CBC,
    /**
     * Cipher feedback
     * Not support
     */
    CFB,
    /**
     * Output feedback
     * Not support
     */
    OFB,
    /**
     *
     *Not support
     */
    CTR,
    /**
     *
     *Not support
     */
    GCM,
    /**
     *
     *Not support
     */
    STREAM,
    /**
     *
     *Not support
     */
    CCM

}
