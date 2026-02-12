package com.newland.nsdk.core.api.external.display;

/**
 * Parameters for Chinese text display.
 */
public class DisplayCNTextParameters extends DisplayTextParameters {
    private int xCoordinate;
    private int yCoordinate;
    private int timeout;

    /**
     * Gets the X coordinate where the text to be displayed.
     *
     * @return X coordinate. Value range: [0-320].
     */
    public int getXCoordinate() {
        return xCoordinate;
    }

    /**
     * Sets the X coordinate where the text to be displayed.
     *
     * @param xCoordinate X coordinate. Value range: [0-320].
     */
    public void setXCoordinate(int xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    /**
     * Gets the Y coordinate where the text to be displayed.
     *
     * @return Y coordinate. Value range: [0-240].
     */
    public int getYCoordinate() {
        return yCoordinate;
    }

    /**
     * Sets the Y codrdinate where the text to be displayed.
     *
     * @param yCoordinate Y coordinate. Value range: [0-240].
     */
    public void setYCoordinate(int yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    /**
     * Gets text display timeout.
     *
     * @return Timeout. Unit: second. Value range: [0-0xFFFF].
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Sets text display timeout.
     *
     * @param timeout Timeout. Unit: second. Value range: [0-0xFFFF].
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
