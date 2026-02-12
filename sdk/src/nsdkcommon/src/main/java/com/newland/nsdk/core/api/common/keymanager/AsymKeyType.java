package com.newland.nsdk.core.api.common.keymanager;

/**
 * Asymmetric key type.
 */
public enum AsymKeyType {
    /**
     * RSA key type.
     */
    RSA((byte)0x20),
    /**
     * <b>[Not yet supported]</b> ECC key type.
     */
    ECC((byte)0x21);

    byte code;

    AsymKeyType(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }
}
