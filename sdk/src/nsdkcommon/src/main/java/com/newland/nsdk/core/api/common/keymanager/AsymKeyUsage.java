package com.newland.nsdk.core.api.common.keymanager;

/**
 * Asymmetric key usage.
 */
public enum AsymKeyUsage {

    /**
     * Used for authentication.
     */
    AUTH((byte)0x20),

    /**
     * Used for data.
     */
    DATA((byte)0x21),

    /**
     * Used for both AUTH and DATA.
     */
    AUTH_DATA((byte)0x22),

    /**
     * Used for key distribution.
     */
    KEY_DISTRIBUTION((byte)0x23);

    byte code;

    AsymKeyUsage(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }
}
