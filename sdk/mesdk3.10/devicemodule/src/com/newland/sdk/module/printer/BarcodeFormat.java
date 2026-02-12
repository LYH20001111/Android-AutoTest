package com.newland.sdk.module.printer;

import android.support.annotation.IntRange;

/**
 * <p>Barcode format</p>
 * <p>It is used in {@link PrintScriptUtil#addBarcode(BarcodeFormat, String)}</p>
 *
 * @author linsi
 * @since V3.10.01
 */
public class BarcodeFormat {
    private int width = 2;
    private int height = 64;
    private Alignment alignment = Alignment.LEFT;
    private BarcodeEncode barcodeEncode = BarcodeEncode.CODE128;
    private boolean isBelowShown = false;

    /**
     * <p>Set the print width of the barcode.</p>
     *
     * @param width Barcode width, the default value is 2 pixel.
     */
    public void setWidth(@IntRange(from = 1, to = 8) int width) {
        this.width = width;
    }

    /**
     * <p>Set the print height of the barcode.</p>
     * <p>The max width is 384 pixel, but is 576 pixel in CPOS-X model.</p>
     *
     * @param height Barcode height, the default value is 64 pixel.
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * <p>Set a barcode alignment</p>
     *
     * @param alignment Alignment, the default value is {@link Alignment#LEFT}.
     */
    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }

    /**
     * <p>Barcode Encode</p>
     *
     * @param barcodeEncode The barcode encode,the default value is code 128.
     */
    public void setBarcodeEncode(BarcodeEncode barcodeEncode) {
        this.barcodeEncode = barcodeEncode;
    }

    /**
     * <p>Set whether the bar code information at the bottom is displayed.</p>
     *
     * @param belowShown True is displayed, otherwise false is not. The default value is false.
     */
    public void setBelowShown(boolean belowShown) {
        isBelowShown = belowShown;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Alignment getAlignment() {
        return alignment;
    }

    public BarcodeEncode getBarcodeEncode() {
        return barcodeEncode;
    }

    public boolean isBelowShown() {
        return isBelowShown;
    }

}
