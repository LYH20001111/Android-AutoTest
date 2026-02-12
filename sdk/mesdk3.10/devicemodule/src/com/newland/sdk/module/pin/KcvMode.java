package com.newland.sdk.module.pin;

/**
 * Author by wuhh, Date on 2020/3/2.
 */
public enum KcvMode {
    /**
     * Encrypt 8 bytes 0x00 with DES/TDES or encrypt 16 bytes 0x00 with SM4. Take the first 3 bytes as KCV value.
     */
    ZERO,
    /**
     * Verify plaintext with parity check method, then encrypt "\x12\x34x56\x78\x90\x12\x34\x56" with DES/TDES. Take the first 3 bytes as KCV value.(NOT SUPPORTED)
     */
    VAL,
    /**
     * Caculate MAC of "aucDstKeyValue(ciphertext) + KcvData" in defined mode Take the 8 bytes MAC as KCV vlaue.(NOT SUPPORTED).
     */
    DATA
}
