package com.newland.sdk.module.rfcard;

/**
 * RFCard Mode
 *
 * @author linsi
 * @since V3.10.01
 */
public enum RFCardMode {

    /**
     * Default type
     */
    DEFAULT(0x07),
    /**
     * Ring M1 type
     */
    RING_M1(0x06);
    private int mode;

    RFCardMode(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return mode;
    }
}
