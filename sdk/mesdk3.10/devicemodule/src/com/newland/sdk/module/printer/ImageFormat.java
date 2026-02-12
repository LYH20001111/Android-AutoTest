package com.newland.sdk.module.printer;

import android.graphics.Bitmap;

/**
 * <p>Image format</p>
 * <p>It is used in {@link PrintScriptUtil#addImage(ImageFormat, Bitmap)}</p>
 *
 * @author linsi
 * @since V3.10.01
 */
public class ImageFormat {
    private int width;
    private int height;
    private int offset = 0;
    private Alignment alignment = Alignment.LEFT;

    /**
     * <p>Set the print width of the image.</p>
     * <p>The max width is 384 pixel, but is 576 pixel in CPOS-X model.</p>
     *
     * @param width Image width
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * <p>Set the print height of the image.</p>
     *
     * @param height Image height
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * <p>Set start position of the image to print.</p>
     * <p>The maximum width of the printing paper is 384 pixel, so the offset plus picture width cannot exceed 384 pixel,</p>
     * <p> but the maximum number is 576 in CPOS-X model.</p>
     *
     * @param offset Print the starting position offset, the default value is 0.
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * <p>Set a image alignment</p>
     *
     * @param alignment Alignment, the default value is {@link Alignment#LEFT}.
     */
    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getOffset() {
        return offset;
    }

    public Alignment getAlignment() {
        return alignment;
    }
}
