package com.newland.sdk.module.printerPro;

import android.graphics.Typeface;


/**
 * @Description
 * @Author wuhh
 * @Date 2021/6/28
 */
public class NTextFormat {
    private String content;
    private int fontSize;
    private NAlignment alignment;
    private Typeface typeface;
    private int marginBottom;
    private int offset;
    private boolean isUnderline;
    private boolean isReverse;

    public NTextFormat(NTextFormat.Builder builder){
        this.content = builder.content;
        this.fontSize = builder.fontSize;
        this.alignment = builder.alignment;
        this.typeface = builder.typeface;
        this.marginBottom = builder.marginBottom;
        this.offset = builder.offset;
        this.isUnderline = builder.isUnderline;
        this.isReverse = builder.isReverse;
    }

    public String getContent() {
        return content;
    }

    public int getFontSize() {
        return fontSize;
    }

    public NAlignment getAlignment() {
        return alignment;
    }

    public Typeface getTypeface() {
        return typeface;
    }

    public int getMarginBottom() {
        return marginBottom;
    }

    public int getOffset() {
        return offset;
    }

    public boolean isUnderline() {
        return isUnderline;
    }

    public boolean isReverse() {
        return isReverse;
    }

    public static class Builder{
        private String content;
        private int fontSize = 24;
        private NAlignment alignment = NAlignment.LEFT;
        private Typeface typeface;// = Typeface.create("宋体",Typeface.NORMAL);
        private int marginBottom = 4;
        private int offset = 0;
        private boolean isUnderline = false;
        private boolean isReverse = false;

        /**
         *  the content
         *
         * @param content
         */
        public NTextFormat.Builder content(String content){
            this.content = content;
            return this;
        }

        /**
         * <p>Set font size</p>
         * <p>The font size of a line print style, the default is 24 pixel.
         *
         * @param fontSize Font size
         */
        public NTextFormat.Builder fontSize(int fontSize){
            this.fontSize = fontSize;
            return this;
        }

        /**
         * <p>Set a line alignment</p>
         * <p>The alignment of a line print style, the default is {@link NAlignment#LEFT}.</p>
         *
         * @param alignment Alignment
         */
        public NTextFormat.Builder alignment(NAlignment alignment){
            this.alignment = alignment;
            return this;
        }

        /**
         * The Typeface class specifies the typeface and intrinsic style of a font.
         * @param typeface
         */
        public NTextFormat.Builder typeface(Typeface typeface){
            this.typeface = typeface;
            return this;
        }

        /**
         * The space filled below the text.the default is 4 pixel.
         * @param marginBottom
         */
        public NTextFormat.Builder marginBottom(int marginBottom){
            this.marginBottom = marginBottom;
            return this;
        }

        /**
         * <p>Set start position of the text to print.</p>
         * <p>The maximum width of the printing paper is 384 pixel, so the offset plus picture width cannot exceed 384 pixel,</p>
         * <p> but the maximum number is 576 in CPOS-X model.</p>
         *
         * @param offset Print the starting position offset, the default value is 0.
         */
        public NTextFormat.Builder offset(int offset){
            this.offset = offset;
            return this;
        }

        /**
         * <p>Set whether text is underlined.</p>
         * @param isUnderline The default value is false.
         * @return
         */
        public NTextFormat.Builder isUnderline(boolean isUnderline){
            this.isUnderline = isUnderline;
            return this;
        }

        /**
         * Set white text on a black background
         * @param isReverse
         * @return
         */
        public NTextFormat.Builder isReverse(boolean isReverse){
            this.isReverse = isReverse;
            return this;
        }

        public NTextFormat create() {
            return new NTextFormat(this);
        }
    }

}
