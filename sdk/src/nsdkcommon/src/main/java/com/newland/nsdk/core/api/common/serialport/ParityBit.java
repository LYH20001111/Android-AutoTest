package com.newland.nsdk.core.api.common.serialport;

/**
 * The parity bit ensures that the total number of 1-bits in the data is even or odd.
 */
public enum ParityBit {
    /**
     * No check
     */
    NO_CHECK,

    /**
     * Odd verification
     */
    ODD_CHECK,

    /**
     * Even verification
     */
    EVEN_CHECK
}
