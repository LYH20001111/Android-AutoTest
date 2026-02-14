package com.newland.nsdk.core.api.internal.printer;

public class PrnParams {
    private boolean enableHighQualityMode;
    private int highQualityGray = 1;

    public boolean isEnableHighQualityMode() {
        return enableHighQualityMode;
    }

    public void setEnableHighQualityMode(boolean enableHighQualityMode) {
        this.enableHighQualityMode = enableHighQualityMode;
    }

    public int getHighQualityGray() {
        return highQualityGray;
    }

    public void setHighQualityGray(int highQualityGray) {
        this.highQualityGray = highQualityGray;
    }
}
