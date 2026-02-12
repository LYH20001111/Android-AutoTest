package com.newland.sdk.module.printer;

/**
 * <p>Two-dimensional barcode encode</p>
 *
 * @author linsi
 * @since V3.10.01
 */
public enum TwoDimensionCodeEncode {
    /**
     * Data Matrix
     */
    DATAMATRIX(0),
    /**
     * Maxicode
     */
    MAXICODE(1),
    /**
     * PDF417
     */
    PDF417(2),
    /**
     * QR Code
     */
    QRCODE(3);

    private int size;

    TwoDimensionCodeEncode(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}
