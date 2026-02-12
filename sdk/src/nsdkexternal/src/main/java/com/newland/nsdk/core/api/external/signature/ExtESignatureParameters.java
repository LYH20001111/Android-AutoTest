package com.newland.nsdk.core.api.external.signature;

public class ExtESignatureParameters {
    boolean supportByPass;
    boolean supportDisplayMessage;
    boolean showButtons;

    ImageFormat imageFormat = ImageFormat.DEFAULT;
    int areaWidth;
    int areaHeight;
    int retryTime;
    String displayMessage;


    public boolean isSupportByPass() {
        return supportByPass;
    }

    public void setSupportByPass(boolean supportByPass) {
        this.supportByPass = supportByPass;
    }

    public boolean isSupportDisplayMessage() {
        return supportDisplayMessage;
    }

    public void setSupportDisplayMessage(boolean supportDisplayMessage) {
        this.supportDisplayMessage = supportDisplayMessage;
    }

    public boolean isShowButtons() {
        return showButtons;
    }

    public void setShowButtons(boolean showButtons) {
        this.showButtons = showButtons;
    }

    public ImageFormat getImageFormat() {
        return imageFormat;
    }

    public void setImageFormat(ImageFormat imageFormat) {
        this.imageFormat = imageFormat;
    }

    public int getAreaWidth() {
        return areaWidth;
    }

    public void setAreaWidth(int areaWidth) {
        this.areaWidth = areaWidth;
    }

    public int getAreaHeight() {
        return areaHeight;
    }

    public void setAreaHeight(int areaHeight) {
        this.areaHeight = areaHeight;
    }

    public int getRetryTime() {
        return retryTime;
    }

    public void setRetryTime(int retryTime) {
        this.retryTime = retryTime;
    }

    public String getDisplayMessage() {
        return displayMessage;
    }

    public void setDisplayMessage(String displayMessage) {
        this.displayMessage = displayMessage;
    }
}
