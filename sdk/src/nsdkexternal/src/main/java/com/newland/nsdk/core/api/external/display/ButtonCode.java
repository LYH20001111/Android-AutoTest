package com.newland.nsdk.core.api.external.display;

public enum ButtonCode {
    NUMBER_0(0x30),
    NUMBER_1(0x31),
    NUMBER_2(0x32),
    NUMBER_3(0x33),
    NUMBER_4(0x34),
    NUMBER_5(0x35),
    NUMBER_6(0x36),
    NUMBER_7(0x37),
    NUMBER_8(0x38),
    NUMBER_9(0x39),
    F1(0x01),
    F2(0x02),
    F3(0x03),
    BACKSPACE(0x0A),
    ENTER(0x0D),
    CANCEL(0x1B),
    DOT(0x2E),
    HASH(0x1C);

    int code;
    ButtonCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
