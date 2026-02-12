package com.newland.nsdk.core.api.external.cardemulator;

public enum EmulateCardStatus {
    IDLE((byte) 0x00),
    INIT((byte) 0x01),
    ACTIVE((byte) 0x03);

    byte code;
    EmulateCardStatus(byte code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
