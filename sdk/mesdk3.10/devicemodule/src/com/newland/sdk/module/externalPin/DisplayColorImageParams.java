package com.newland.sdk.module.externalPin;

/**
 * @Description
 * @Author Denise
 * @Date 2024/03/29 17:08
 */
public class DisplayColorImageParams {

    private int xCoordinate=0;
    private int yCoordinate=0;
    private int width=320;
    private int height=480;

    public DisplayColorImageParams() {
    }

    public int getxCoordinate() {
        return xCoordinate;
    }

    public void setxCoordinate(int xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public int getyCoordinate() {
        return yCoordinate;
    }

    public void setyCoordinate(int yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
