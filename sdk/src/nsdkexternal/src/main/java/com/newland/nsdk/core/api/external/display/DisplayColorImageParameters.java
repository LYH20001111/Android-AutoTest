package com.newland.nsdk.core.api.external.display;

/**
 * Parameters for picture display.
 */
public class DisplayColorImageParameters {
    private int xCoordinate;
    private int yCoordinate;
    private int width;
    private int height;

    /**
     * Gets the X coordinate where the picture to be displayed.
     *
     * @return X coordinate. Value range: [0-320].
     */
    public int getXCoordinate() {
        return xCoordinate;
    }

    /**
     * Sets the X coordinate where the picture to be displayed.
     *
     * @param xCoordinate X coordinate. Value range: [0-320].
     */
    public void setXCoordinate(int xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    /**
     * Gets the Y coordinate where the picture to be displayed.
     *
     * @return Y coordinate. Value range: [0-240].
     */
    public int getYCoordinate() {
        return yCoordinate;
    }

    /**
     * Sets the Y coordinate where the picture to be displayed.
     *
     * @param yCoordinate Y coordinate. Value range: [0-240].
     */
    public void setYCoordinate(int yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    /**
     * Gets image width.
     *
     * @return Image width, shall be >0.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Sets image width.
     *
     * @param width Image width. Shall set the actual width of the image, otherwise the image will not be displayed.
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Gets image height.
     *
     * @return Image height, shall be >0.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets image height.
     *
     * @param height Image height. Shall set the actual height of the image, otherwise the image will not be displayed.
     */
    public void setHeight(int height) {
        this.height = height;
    }
}
