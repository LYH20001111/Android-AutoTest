package com.newland.nsdk.plugin.card.api.common.contactless;

/**
 * Contactless card key mode.
 */
public enum ContactlessKeyMode {
    /**
     * Key A 0x60
     */
    KEYA_0X60(0x60),

    /**
     * Key A 0x00
     */
    KEYA_0X00(0x00),

    /**
     * Key B 0x61
     */
    KEYB_0X61(0x61),

    /**
     * Key B 0x01
     */
    KEYB_0X01(0x01);

    private int code;

    ContactlessKeyMode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
