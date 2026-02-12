package com.newland.sdkdemo.fragment.dock.emv;

/**
 * Author by wuhh, Date on 2020/3/19.
 */
public class TransResult {
    private int transResultCode;
    private boolean isSignature;
    private boolean onlineResult;
    private byte[] responseCode;//39域
    private byte[] isoField55;
    private byte[] pinBlock;

    public byte[] getPinBlock() {
        return pinBlock;
    }

    public void setPinBlock(byte[] pinBlock) {
        this.pinBlock = pinBlock;
    }

    public byte[] getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(byte[] responseCode) {
        this.responseCode = responseCode;
    }

    public byte[] getIsoField55() {
        return isoField55;
    }

    public void setIsoField55(byte[] isoField55) {
        this.isoField55 = isoField55;
    }

    public boolean getOnlineResult() {
        return onlineResult;
    }

    public void setOnlineResult(boolean onlineResult) {
        this.onlineResult = onlineResult;
    }

    public int getTransResultCode() {
        return transResultCode;
    }

    public void setTransResultCode(int transResultCode) {
        this.transResultCode = transResultCode;
    }

    public boolean isSignature() {
        return isSignature;
    }

    public void setSignature(boolean signature) {
        isSignature = signature;
    }
}
