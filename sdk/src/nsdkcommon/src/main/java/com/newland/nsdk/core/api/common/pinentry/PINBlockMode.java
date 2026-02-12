package com.newland.nsdk.core.api.common.pinentry;

/**
 * PIN block mode.
 */
public enum PINBlockMode {
    /**
     * PIN block mode - ISO 9564 0.
     */
    ISO9564_0(3),
    /**
     * PIN block mode - ISO 9564 1.
     */
    ISO9564_1(4),
    /**
     * PIN block mode - ISO 9564 2.
     */
    ISO9564_2(5),
    /**
     * PIN block mode - ISO 9564 3.
     */
    ISO9564_3(6),
    SM4_1(7),
    SM4_2(8),
    SM4_3(9),
    SM4_4(10),
    SM4_5(11),
    /**
     * PIN block mode - ISO 9564 4.
     */
    ISO9564_4(12);

    int code;

    PINBlockMode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
