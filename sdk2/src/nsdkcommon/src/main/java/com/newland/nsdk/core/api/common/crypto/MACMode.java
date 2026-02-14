package com.newland.nsdk.core.api.common.crypto;

/**
 * MAC mode.
 */
public enum MACMode {
    /**
     * 9606 MAC algorithm
     */
    LAST,

    /**
     * ANSI X9.9 MAC algorithm
     */
    X99,

    /**
     * ANSI X9.19 MAC algorithm
     */
    X919,

    /**
     * MAC mode for the UnionPay requirement
     */
    UNIONPAY_ECB,

    /**
     * AES
     */
    AES
}
