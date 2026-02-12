package com.newland.nsdk.core.api.internal.printer;

/**
 * Parameters for printing.
 */
public class PrintingParameters {
    private int startX;
    private int expectedImageWidth;
    private int expectedImageHeight;
    private int imageWidth;
    private int imageHeight;

    /**
     * Gets starting offset.
     *
     * @return Starting Offset from where to print, shall be >=0.
     */
    public int getStartX() {
        return startX;
    }

    /**
     * Sets starting offset.
     *
     * @param startX Starting Offset from where to print, shall be >=0.
     */
    public void setStartX(int startX) {
        this.startX = startX;
    }

    /**
     * Gets expected image width.
     *
     * @return Expected image width to print, shall be >0.
     */
    public int getExpectedImageWidth() {
        return expectedImageWidth;
    }

    /**
     * Sets expected image width.
     *
     * @param expectedImageWidth Expected image width to print, shall be >0.
     */
    public void setExpectedImageWidth(int expectedImageWidth) {
        this.expectedImageWidth = expectedImageWidth;
    }

    /**
     * Gets expected image height.
     *
     * @return Expected image height to print, shall be >0.
     */
    public int getExpectedImageHeight() {
        return expectedImageHeight;
    }

    /**
     * Sets expected image height.
     *
     * @param expectedImageHeight Expected image height to print, shall be >0.
     */
    public void setExpectedImageHeight(int expectedImageHeight) {
        this.expectedImageHeight = expectedImageHeight;
    }

    /**
     * Gets actual width of the image.
     *
     * @return Actual width of the image, shall be >0.
     */
    public int getImageWidth() {
        return imageWidth;
    }

    /**
     * Sets actual width of the image.
     *
     * @param imageWidth Actual width of the image, shall be >0.
     */
    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    /**
     * Gets actual height of the image.
     *
     * @return Actual height of the image, shall be >0.
     */
    public int getImageHeight() {
        return imageHeight;
    }

    /**
     * Sets actual height of the image.
     *
     * @param imageHeight Actual height of the image, shall be >0.
     */
    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }
}
