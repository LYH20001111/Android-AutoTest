package com.newland.sdk.module.externalPin;

public enum InputMode {
    /**
     * Only for digit
     */
    ONLY_DIGIT(0),
    /**
     * Hexadecimal characters
     */
    HEX(1),
    /**
     * All characters
     */
    ALL_CHARACTER(2),
    /**
     * submit PinInput when pin length is max length
     */
    SUBMIT_PININPUT(0x10);//自动结束pin输入，当密码长度达到最大时

    private int code;

    private InputMode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
