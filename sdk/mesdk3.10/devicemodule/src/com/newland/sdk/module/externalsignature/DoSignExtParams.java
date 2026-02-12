package com.newland.sdk.module.externalsignature;

public class DoSignExtParams {

    private boolean isBypass = false;
    private boolean isContainCode = true;
    private boolean isButtonDisplay = true;
    /**
     * SP130 not supported
     */
    private boolean isJBigData = false;
    private int width;
    private int height;
    private byte retryTime;
    private int timeout;
    private String displayData;

    public boolean isBypass() {
        return isBypass;
    }

    public void setBypass(boolean bypass) {
        isBypass = bypass;
    }

    public boolean isContainCode() {
        return isContainCode;
    }

    public void setContainCode(boolean containCode) {
        isContainCode = containCode;
    }

    public boolean isButtonDisplay() {
        return isButtonDisplay;
    }

    public void setButtonDisplay(boolean buttonDisplay) {
        isButtonDisplay = buttonDisplay;
    }

    public boolean isJBigData() {
        return isJBigData;
    }

    public void setJBigData(boolean JBigData) {
        isJBigData = JBigData;
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

    public byte getRetryTime() {
        return retryTime;
    }

    public void setRetryTime(byte retryTime) {
        this.retryTime = retryTime;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getDisplayData() {
        return displayData;
    }

    public void setDisplayData(String displayData) {
        this.displayData = displayData;
    }
}
