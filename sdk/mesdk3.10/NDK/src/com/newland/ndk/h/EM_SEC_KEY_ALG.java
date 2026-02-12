package com.newland.ndk.h;

/**
 * Author by wuhh, Date on 2020/4/16.
 */
public enum  EM_SEC_KEY_ALG {
    SEC_KEY_DES(0),
    SEC_KEY_SM4(64),
    SEC_KEY_AES(128);

    private int code;

    private EM_SEC_KEY_ALG(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}
