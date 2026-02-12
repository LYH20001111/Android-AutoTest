package com.newland.sdk.module.printer;

public class TextSize {
    /**
     * Text width
     */
    private int width;
    /**
     * Text height
     */
    private int height;

    public TextSize(int width,int height){
        this.width = width;
        this.height = height;
    }
    /**
     * Get text width
     * @return
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get text height
     * @return
     */
    public int getHeight() {
        return height;
    }
}
