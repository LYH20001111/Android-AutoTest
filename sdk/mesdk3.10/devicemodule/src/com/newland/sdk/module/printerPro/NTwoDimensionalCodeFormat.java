package com.newland.sdk.module.printerPro;

/**
 * <p>TwoDimensionCodeFormat format</p>
 *
 * @author linsi
 */
public class NTwoDimensionalCodeFormat {
    private String content;
    private int height;
    private NAlignment alignment;
    private NTwoDimensionalCodeFormat.Type codeType;

    public NTwoDimensionalCodeFormat(Builder builder){
         this.content= builder.content;
         this.height= builder.height;
         this.alignment= builder.alignment;
         this.codeType= builder.codeType;
    }

    public String getContent() {
        return content;
    }

    public int getHeight() {
        return height;
    }

    public NAlignment getAlignment() {
        return alignment;
    }

    public NTwoDimensionalCodeFormat.Type getCodeType() {
        return codeType;
    }


    public static class Builder{
        private String content;
        private int height = 300;
        private NAlignment alignment = NAlignment.CENTER;
        private NTwoDimensionalCodeFormat.Type codeType = NTwoDimensionalCodeFormat.Type.QRCODE;

        /**
         * TwoDimensionalCode data
         * @param content
         * @return
         */
        public NTwoDimensionalCodeFormat.Builder content(String content){
            this.content = content;
            return this;
        }
        /**
         * <p>Set the print height of the QR-Code.</p>
         *
         * @param height QR-Code height, the default value is 300.
         */
        public NTwoDimensionalCodeFormat.Builder height(int height){
            this.height = height;
            return this;
        }

        /**
         * <p>Set a QR-Code alignment</p>
         *
         * @param alignment NAlignment, the default value is {@link NAlignment#LEFT}.
         */
        public NTwoDimensionalCodeFormat.Builder alignment(NAlignment alignment){
            this.alignment = alignment;
            return this;
        }

        /**
         * <p>Set two barcode encode.</p>
         *
         * @param codeType The default value is {@link Type#QRCODE}
         */
        public NTwoDimensionalCodeFormat.Builder codeType(NTwoDimensionalCodeFormat.Type codeType){
            this.codeType = codeType;
            return this;
        }

        public NTwoDimensionalCodeFormat create() {
            return new NTwoDimensionalCodeFormat(this);
        }

    }

    public enum Type {

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

        private int code;

        Type(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

}
