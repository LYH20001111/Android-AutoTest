package com.newland.sdk.module.printerPro;

/**
 * <p>Barcode format</p>
 *
 * @author linsi
 */
public class NBarcodeFormat {
    private String content;
    private int width;
    private int height;
    private NAlignment alignment;
    private NBarcodeFormat.Type codeType;
    private boolean showCodeContent;

    public NBarcodeFormat(Builder builder){
        this.content = builder.content;
        this.width = builder.width;
        this.height = builder.height;
        this.alignment = builder.alignment;
        this.codeType = builder.codeType;
        this.showCodeContent = builder.showCodeContent;
    }

    public String getContent() {
        return content;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public NAlignment getAlignment() {
        return alignment;
    }

    public NBarcodeFormat.Type getCodeType() {
        return codeType;
    }

    public boolean isShowCodeContent() {
        return showCodeContent;
    }

    public static class Builder{
        private String content;
        private int width = 6;
        private int height = 64;
        private NAlignment alignment = NAlignment.CENTER;
        private NBarcodeFormat.Type codeType = NBarcodeFormat.Type.CODE128;
        private boolean showCodeContent = false;

        /**
         *  Barcode data
         * @param content
         * @return
         */
        public NBarcodeFormat.Builder content(String content){
            this.content = content;
            return this;
        }

        /**
         * <p>Set the print width of the barcode.</p>
         *
         * @param width Barcode width, the default value is 2 pixel.
         */
        public NBarcodeFormat.Builder width(int width){
            this.width = width;
            return this;
        }

        /**
         * <p>Set the print height of the barcode.</p>
         * <p>The max width is 384 pixel, but is 576 pixel in CPOS-X model.</p>
         *
         * @param height Barcode height, the default value is 64 pixel.
         */
        public NBarcodeFormat.Builder height(int height){
            this.height = height;
            return this;
        }
        /**
         * <p>Set a barcode alignment</p>
         *
         * @param alignment NAlignment, the default value is {@link NAlignment#LEFT}.
         */
        public NBarcodeFormat.Builder alignment(NAlignment alignment){
            this.alignment = alignment;
            return this;
        }
        /**
         * <p>Barcode Encode</p>
         *
         * @param codeType The barcode encode,the default value is code 128.
         */
        public NBarcodeFormat.Builder codeType(NBarcodeFormat.Type codeType){
            this.codeType = codeType;
            return this;
        }

        /**
         * <p>Set whether the bar code information at the bottom is displayed.</p>
         *
         * @param showCodeContent True is displayed, otherwise false is not. The default value is false.
         */
        public NBarcodeFormat.Builder showCodeContent(boolean showCodeContent){
            this.showCodeContent = showCodeContent;
            return this;
        }

        public NBarcodeFormat create() {
            return new NBarcodeFormat(this);
        }

    }

    public enum Type {
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

        private int code;

        Type(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }
}
