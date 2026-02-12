package com.newland.sdk.module.externalScan;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

public class ScanBoxParams {
    /** Set scan box backlight switch */
    private Boolean isBackLight;
    /** Set scan box color light state */
    private ScanLightStatus mScanLightStatus;
    /** Set the volume level */
    private Integer volume;
    /** Set the scan result prefix, {@code ""} clear the prefix */
    private String prefix;
    /** Set the scan result suffix, {@code ""} clear the suffix */
    private String suffix;
    /** Set the scan success voice prompt,no more than 100 bytes, using GBK code */
    private String successVoicePrompt;
    /** Set to add carriage return line */
    private Boolean isEnter;
    /** set scan mode once or continue*/
    private Boolean modeOnce;
//    /** productID end with "09",when modeonce true is working,do not set HID mode,it is a bug!*/
//    private Boolean cdcMode;

//    public Boolean isCdcMode() {
//        return cdcMode;
//    }
//
//    public void setCdcMode(Boolean cdcMode) {
//        this.cdcMode = cdcMode;
//    }

    public Boolean isBackLight() {
        return isBackLight;
    }

    public void setBackLight(boolean backLight) {
        isBackLight = backLight;
    }

    public ScanLightStatus getScanLightStatus() {
        return mScanLightStatus;
    }

    public void setScanLightStatus(ScanLightStatus scanLightStatus) {
        this.mScanLightStatus = scanLightStatus;
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(@IntRange(from = 0, to = 6) int volume) {
        this.volume = volume;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getSuccessVoicePrompt() {
        return successVoicePrompt;
    }

    public void setSuccessVoicePrompt(String successVoicePrompt) {
        this.successVoicePrompt = successVoicePrompt;
    }

    public Boolean isEnter() {
        return isEnter;
    }

    public void setEnter(boolean isEnter) {
        this.isEnter = isEnter;
    }

    public Boolean isModeOnce() {
        return modeOnce;
    }

    public void setModeOnce(boolean modeOnce) {
        this.modeOnce = modeOnce;
    }

    public static class ScanLightStatus {

        /** color light */
        private ScanBoxLight[] lightColor;
        /** set turn on or turn off */
        private boolean isTurnOn;

        @NonNull
        public ScanBoxLight[] getLightColor() {
            return lightColor;
        }

        public void setLightColor(@NonNull ScanBoxLight[] lightColor) {
            this.lightColor = lightColor;
        }

        public boolean isTurnOn() {
            return isTurnOn;
        }

        public void setTurnOn(boolean turnOn) {
            isTurnOn = turnOn;
        }
    }
}
