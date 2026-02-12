package com.newland.nsdk.core.api.external.display;

public class PictureParameters {
    /**
     * The picture id stored in the device when calling "loadImage".
     */
    private int id = 0;
    /**
     * The start x of the picture to be displayed.
     */
    private int x = 0;
    /**
     * The start y of the picture to be displayed.
     */
    private int y = 0;
    /**
     * The width of the whole picture.
     */
    private int width = 0;
    /**
     * The height of the whole picture.
     */
    private int height = 0;

    /**
     * The picture format.
     */
    private PictureType pictureType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
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

    public PictureType getPictureType() {
        return pictureType;
    }

    public void setPictureType(PictureType pictureType) {
        this.pictureType = pictureType;
    }
}
