package com.newland.nsdk.core.external.command.keymanager;

/**
 * Types of External PIN key check value.
 */
public enum ExtPinKCVType {
    DES_KEK(0),
    DES_PIN(1),
    DES_MAC(2),
    DES_DATA(3),
    AES_KEK(4),
    AES_PIN(5),
    AES_MAC(6),
    AES_DATA(7),
    SM4_TMK(8),
    SM4_PIN(9),
    SM4_MAC(10),
    SM4_DATA(11);

    private int code;

    ExtPinKCVType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
