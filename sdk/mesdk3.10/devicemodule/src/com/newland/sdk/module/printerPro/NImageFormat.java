package com.newland.sdk.module.printerPro;

import android.graphics.Bitmap;

/**
 * <p>Image format</p>
 *
 * @author linsi
 */
public class NImageFormat {
    private Bitmap bitmap;
    private int width;
    private int height;
    private NAlignment alignment;
    private int threshold;
    private int offset;

    public NImageFormat(Builder builder){
        this.bitmap = builder.bitmap;
        this.width = builder.width;
        this.height = builder.height;
        this.alignment = builder.alignment;
        this.threshold = builder.threshold;
        this.offset = builder.offset;
    }

    public Bitmap getBitmap() {
        return bitmap;
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

    public int getThreshold() {
        return threshold;
    }

    public int getOffset() {
        return offset;
    }

    public static class Builder{
        private Bitmap bitmap;
        private int width;
        private int height;
        private NAlignment alignment = NAlignment.LEFT;
        private int threshold = -1;
        private int offset = 0;
        /**
         * The image data.
         * @param bitmap
         */
        public NImageFormat.Builder bitmap(Bitmap bitmap){
            this.bitmap = bitmap;
            return this;
        }
        /**
         * <p>Set the print width of the image.</p>
         * <p>The max width is 384 pixel, but is 576 pixel in CPOS-X model.</p>
         *
         * @param width Image width
         */
        public NImageFormat.Builder width(int width){
            this.width = width;
            return this;
        }

        /**
         * <p>Set the print height of the image.</p>
         *
         * @param height Image height
         */
        public NImageFormat.Builder height(int height){
            this.height = height;
            return this;
        }

        /**
         * <p>Set a image alignment</p>
         *
         * @param alignment NAlignment, the default value is {@link NAlignment#LEFT}.
         */
        public NImageFormat.Builder alignment(NAlignment alignment){
            this.alignment = alignment;
            return this;
        }

        /**
         * Set the image threshold the rang is 1-254.
         * @param threshold the default value is automatic threshold.
         * @return
         */
        public NImageFormat.Builder threshold(int threshold){
            this.threshold = threshold;
            return this;
        }

        /**
         * <p>Set start position of the text to print.</p>
         * <p>The maximum width of the printing paper is 384 pixel, so the offset plus picture width cannot exceed 384 pixel,</p>
         * <p> but the maximum number is 576 in CPOS-X model.</p>
         *
         * @param offset Print the starting position offset, the default value is 0.
         */
        public NImageFormat.Builder offset(int offset){
            this.offset = offset;
            return this;
        }

        public NImageFormat create() {
            return new NImageFormat(this);
        }
    }

}
