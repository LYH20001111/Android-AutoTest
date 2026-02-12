package com.newland.sdk.module.printer;

import android.support.annotation.IntRange;

/**
 * <p>TwoDimensionCodeFormat format</p>
 * <p>It is used in {@link PrintScriptUtil#addTwoDimensionCode(TwoDimensionCodeFormat, String)} </p>
 *
 * @author linsi
 * @since V3.10.01
 */
public class TwoDimensionCodeFormat {
    private int offset = 0;
    private int height = 100;
    private Alignment alignment = Alignment.LEFT;
    private QRCodeErrorCorrectionLevel qrCodeErrorCorrectionLevel = QRCodeErrorCorrectionLevel.LEVEL_2ST;
    private TwoDimensionCodeEncode twoDimensionCodeEncode = TwoDimensionCodeEncode.QRCODE;

    /**
     * <p>Set the print height of the QR-Code.</p>
     *
     * @param height QR-Code height, the default value is 100.
     */
    public void setHeight(@IntRange(from = 1, to = 384) int height) {
        this.height = height;
    }

    /**
     * <p>Set a QR-Code alignment</p>
     *
     * @param alignment Alignment, the default value is {@link Alignment#LEFT}.
     */
    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }

    /**
     * <p>Set a error correction level of QR-Code.</p>
     * <p>it is valid in QR-Code Encode.<p/>
     *
     * @param qrCodeErrorCorrectionLevel The default value is {@link QRCodeErrorCorrectionLevel#LEVEL_2ST}
     */
    public void setQrCodeErrorCorrectionLevel(QRCodeErrorCorrectionLevel qrCodeErrorCorrectionLevel) {
        this.qrCodeErrorCorrectionLevel = qrCodeErrorCorrectionLevel;
    }

    /**
     * <p>Set two barcode encode.</p>
     *
     * @param twoDimensionCodeEncode The default value is {@link TwoDimensionCodeEncode#QRCODE}
     */
    public void setTwoDimensionCodeEncode(TwoDimensionCodeEncode twoDimensionCodeEncode) {
        this.twoDimensionCodeEncode = twoDimensionCodeEncode;
    }

    public int getHeight() {
        return height;
    }

    public Alignment getAlignment() {
        return alignment;
    }

    public QRCodeErrorCorrectionLevel getQrCodeErrorCorrectionLevel() {
        return qrCodeErrorCorrectionLevel;
    }

    public TwoDimensionCodeEncode getTwoDimensionCodeEncode() {
        return twoDimensionCodeEncode;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
