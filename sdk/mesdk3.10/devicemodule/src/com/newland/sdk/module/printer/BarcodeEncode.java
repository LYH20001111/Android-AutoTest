package com.newland.sdk.module.printer;

/**
 * <p>Barcode encode</p>
 *
 * @author linsi
 * @since V3.10.01
 */
public enum BarcodeEncode {
    /**
     * CODABAR
     */
    CODABAR(0),
    /**
     * CODE39
     */
    CODE39(1),
    /**
     * CODE93
     */
    CODE93(2),
    /**
     * CODE93
     */
    CODE128(3),
    /**
     * Ean-8 / jan-8 and ean-13 / jan-13 both use this code and are distinguished by data length.
     */
    EAN(4),
    /**
     * ITF
     */
    ITF(5),
    /**
     * UPC-A
     */
    UPCA(6),
    /**
     * UPC-E
     */
    UPCE(7);

    private int size;

    BarcodeEncode(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}
