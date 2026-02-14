package com.newland.nsdk.core.api.common.crypto;

/**
 * KCV mode.
 */
public enum KCVMode {
    /**
     * No verification.
     */
    NONE,
    /**
     * Encrypt 8 bytes of 0x00 with DES/TDES.
     * <p>Take the first 3 bytes of ciphertext as KCV value.</p>
     */
    ZERO,
    /**
     * Verify plaintext of key with odd parity, then encrypt "\x12\x34x56\x78\x90\x12\x34\x56" with DES/TDES.
     * <p>Take the first 3 bytes of ciphertext as KCV value.</p>
     */
    VAL,
    /**
     * Calculate MAC of "DstKeyValue(ciphertext) + KcvData" by specified mode.
     * <p>Take the 8 bytes of MAC as KCV value.</p>
     */
    DATA,

    /**
     * Encrypt an all-zero block with CMAC(Cypher-Based Message Authentication Code).
     * <p>Take the first 5 bytes of ciphertext as KCV value.</p>
     */
    CMAC
}
