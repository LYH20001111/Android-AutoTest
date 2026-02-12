package com.newland.sdk.module.externalScan;

public class StartScanExtParams {

    /** Scanning interval */
    private long interval = 1500;
    /** Whether to scan the code once */
    private boolean isOnce = true;
    /** Whether to turn off the amount display after scanning the code */
    private boolean isTurnOffAmountDisplay = true;
    /** Set the start scan voice prompt, no more than 100 bytes, using GBK code */
    private String scanVoicePrompt;

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public boolean isOnce() {
        return isOnce;
    }

    public void setOnce(boolean once) {
        isOnce = once;
    }

    public boolean isTurnOffAmountDisplay() {
        return isTurnOffAmountDisplay;
    }

    public void setTurnOffAmountDisplay(boolean turnOffAmountDisplay) {
        isTurnOffAmountDisplay = turnOffAmountDisplay;
    }

    public String getScanVoicePrompt() {
        return scanVoicePrompt;
    }

    public void setScanVoicePrompt(String scanVoicePrompt) {
        this.scanVoicePrompt = scanVoicePrompt;
    }
}
