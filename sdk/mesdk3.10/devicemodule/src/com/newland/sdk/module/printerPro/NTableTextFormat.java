package com.newland.sdk.module.printerPro;

import android.graphics.Typeface;


/**
 * @Description
 * @Author wuhh
 * @Date 2021/6/28
 */
public class NTableTextFormat {
    private String content;
    private int fontSize;
    private NAlignment alignmentOfBorder;
    private Typeface typeface;
    private int marginLeft;
    private int borderSize;
    private int borderHeight;
    private float borderWeight;//percent
    private boolean isReverse;

    public NTableTextFormat(NTableTextFormat.Builder builder){
        this.content = builder.content;
        this.fontSize = builder.fontSize;
        this.alignmentOfBorder = builder.alignmentOfBorder;
        this.typeface = builder.typeface;
        this.marginLeft = builder.marginLeft;
        this.borderHeight = builder.borderHeight;
        this.borderWeight = builder.borderWeight;
        this.borderSize = builder.borderSize;
        this.isReverse = builder.isReverse;
    }

    public String getContent() {
        return content;
    }

    public int getFontSize() {
        return fontSize;
    }

    public NAlignment getAlignmentOfBorder() {
        return alignmentOfBorder;
    }

    public Typeface getTypeface() {
        return typeface;
    }

    public int getMarginLeft() {
        return marginLeft;
    }

    public int getBorderSize() {
        return borderSize;
    }

    public int getBorderHeight() {
        return borderHeight;
    }

    public float getBorderWeight() {
        return borderWeight;
    }

    public boolean isReverse() {
        return isReverse;
    }

    public static class Builder{
        private String content;
        private int fontSize = 24;
        private NAlignment alignmentOfBorder = NAlignment.CENTER;
        private Typeface typeface;
        private int marginLeft = 0;
        private int borderSize = 6;
        private int borderHeight = fontSize*2;
        private float borderWeight = 0.0f;
        private boolean isReverse = false;
        /**
         *  the content
         *
         * @param content
         */
        public NTableTextFormat.Builder content(String content){
            this.content = content;
            return this;
        }

        /**
         * <p>Set font size</p>
         * <p>The font size of a line print style, the default is 24 pixel.
         *
         * @param fontSize Font size
         */
        public NTableTextFormat.Builder fontSize(int fontSize){
            this.fontSize = fontSize;
            return this;
        }

        /**
         * <p>Set alignment of border</p>
         * <p>The alignment of a line print style, the default is {@link NAlignment#CENTER}.</p>
         *
         * @param alignment Alignment
         */
        public NTableTextFormat.Builder alignmentOfBorder(NAlignment alignment){
            this.alignmentOfBorder = alignment;
            return this;
        }

        /**
         * The Typeface class specifies the typeface and intrinsic style of a font.
         * @param typeface
         */
        public NTableTextFormat.Builder typeface(Typeface typeface){
            this.typeface = typeface;
            return this;
        }

        public NTableTextFormat.Builder borderSize(int borderSize){
            this.borderSize = borderSize;
            return this;
        }

        /**
         * Each item offset the pixel to the left. unit: pixel
         * @param marginLeft
         */
        public NTableTextFormat.Builder marginLeft(int marginLeft){
            this.marginLeft = marginLeft;
            return this;
        }

        /**
         * Sets the cell height. unit: pixel
         * @param borderHeight
         */
        public NTableTextFormat.Builder borderHeight(int borderHeight){
            this.borderHeight = borderHeight;
            return this;
        }

        /**
         * Sets the cell percentage.The default is an average score. The range of 0 to 1.
         * @param borderWeight
         */
        public NTableTextFormat.Builder borderWeight(float borderWeight){
            this.borderWeight = borderWeight;
            return this;
        }

        /**
         * Set white text on a black background
         * @param isReverse
         * @return
         */
        public NTableTextFormat.Builder isReverse(boolean isReverse){
            this.isReverse = isReverse;
            return this;
        }

        public NTableTextFormat create() {
            return new NTableTextFormat(this);
        }
    }

}
