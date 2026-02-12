package com.newland.sdk.module.printer;

/**
 * <p>Error correction level of QRCode.</p>
 * <p>It is used in {@link TwoDimensionCodeFormat#setQrCodeErrorCorrectionLevel(QRCodeErrorCorrectionLevel)}</p>
 *
 * @author linsi
 * @since V3.10.01
 */
public enum QRCodeErrorCorrectionLevel {
    LEVEL_1ST(0),
    LEVEL_2ST(1),
    LEVEL_3ST(2);
    private int level;

    QRCodeErrorCorrectionLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
