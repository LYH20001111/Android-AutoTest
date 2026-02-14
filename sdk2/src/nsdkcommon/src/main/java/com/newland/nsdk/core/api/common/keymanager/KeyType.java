package com.newland.nsdk.core.api.common.keymanager;

/**
 * Symmetric key type.
 */
public enum KeyType {
    /**
     * DES key type.
     */
    DES((byte)0),
    /**
     * AES key type.
     */
    AES((byte)1),
    /**
     * SM4
     */
    SM4((byte)2),
    /**
     * HMAC
     */
    HMAC((byte)3);

    byte code;

    KeyType(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }
}
