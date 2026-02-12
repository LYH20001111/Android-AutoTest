package com.newland.sdk.module.externalsignature;

public class SignatureExtParams {

    /** Set the time out(s) */
    private int bordTimeout = 60;
    /** Set the reSignature times */
    private int reSignTimes = 1;
    /** Set the picture whit white background and black text(or black background and white text) */
    private boolean isWhiteBackground;
    /** Set the  Signing Board whether BackLight or not */
    private boolean isBackLight;
    /** Set the mode of Signing Board */
    private boolean isSaveSign;

    public int getBordTimeout() {
        return bordTimeout;
    }

    public void setBordTimeout(int bordTimeout) {
        this.bordTimeout = bordTimeout;
    }

    public int getReSignTimes() {
        return reSignTimes;
    }

    public void setReSignTimes(int reSignTimes) {
        this.reSignTimes = reSignTimes;
    }

    public boolean isWhiteBackground() {
        return isWhiteBackground;
    }

    public void setWhiteBackground(boolean whiteBackground) {
        isWhiteBackground = whiteBackground;
    }

    public boolean isBackLight() {
        return isBackLight;
    }

    public void setBackLight(boolean blackLight) {
        isBackLight = blackLight;
    }

    public boolean isSaveSign() {
        return isSaveSign;
    }

    public void setSaveSign(boolean saveSign) {
        isSaveSign = saveSign;
    }

    @Override
    public String toString() {
        return "SignatureExtParams{" +
                "bordTimeout=" + bordTimeout +
                ", reSignTimes=" + reSignTimes +
                ", isWhiteBackground=" + isWhiteBackground +
                ", isBackLight=" + isBackLight +
                ", isSaveSign=" + isSaveSign +
                '}';
    }
}
