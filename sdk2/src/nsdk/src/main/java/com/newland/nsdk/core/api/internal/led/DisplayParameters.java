package com.newland.nsdk.core.api.internal.led;

public class DisplayParameters {
    int x = -1;
    int y = -1;
    boolean isHorizontal = true;
    boolean isBackgroundAlwaysDisplayed = false;

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

    public boolean isHorizontal() {
        return isHorizontal;
    }

    public void setHorizontal(boolean horizontal) {
        isHorizontal = horizontal;
    }

    public boolean isBackgroundAlwaysDisplayed() {
        return isBackgroundAlwaysDisplayed;
    }

    public void setBackgroundAlwaysDisplayed(boolean backgroundAlwaysDisplayed) {
        isBackgroundAlwaysDisplayed = backgroundAlwaysDisplayed;
    }
}
