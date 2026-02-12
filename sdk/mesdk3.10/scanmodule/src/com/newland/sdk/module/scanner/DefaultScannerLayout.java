package com.newland.sdk.module.scanner;

/**
 * @author youjf
 * @description
 * @date 2020/5/31
 * @since
 */
public class DefaultScannerLayout {
    private boolean enablePreview;
    private boolean enableSound;
    private boolean isOnce;

    public boolean isEnablePreview() {
        return enablePreview;
    }

    public void setEnablePreview(boolean enablePreview) {
        this.enablePreview = enablePreview;
    }

    public boolean isEnableSound() {
        return enableSound;
    }

    public void setEnableSound(boolean enableSound) {
        this.enableSound = enableSound;
    }

    public boolean isOnce() {
        return isOnce;
    }

    public void setOnce(boolean once) {
        isOnce = once;
    }
}
