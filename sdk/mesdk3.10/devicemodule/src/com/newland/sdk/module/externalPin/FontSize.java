package com.newland.sdk.module.externalPin;
/**
 * Font Size
 * @author linsi
 * @date 20250314
 */
public enum FontSize {
    /**
     * Normal Font
     */
    NORMAL(0x01),
    /**
     * Small Font
     */
    SMALL(0x02),
    /**
     * Large Font
     */
    LARGE(0x03);

    private int size;

    private FontSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

}
