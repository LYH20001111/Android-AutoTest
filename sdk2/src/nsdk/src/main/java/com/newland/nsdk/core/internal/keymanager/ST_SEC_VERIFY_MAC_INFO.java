package com.newland.nsdk.core.internal.keymanager;

public class ST_SEC_VERIFY_MAC_INFO {
    private int keyID;
    private int keyType;
    private int keyUsage;
    private int macMode;
    private byte[] iv;
    private int ivLen;
    private byte[] macData;
    private int macDataLen;


    public int getKeyID() {
        return keyID;
    }

    public void setKeyID(int keyID) {
        this.keyID = keyID;
    }

    public int getKeyType() {
        return keyType;
    }

    public void setKeyType(int keyType) {
        this.keyType = keyType;
    }

    public int getKeyUsage() {
        return keyUsage;
    }

    public void setKeyUsage(int keyUsage) {
        this.keyUsage = keyUsage;
    }

    public int getMacMode() {
        return macMode;
    }

    public void setMacMode(int macMode) {
        this.macMode = macMode;
    }

    public byte[] getIv() {
        return iv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }

    public int getIvLen() {
        return ivLen;
    }

    public void setIvLen(int ivLen) {
        this.ivLen = ivLen;
    }

    public byte[] getMacData() {
        return macData;
    }

    public void setMacData(byte[] macData) {
        this.macData = macData;
    }

    public int getMacDataLen() {
        return macDataLen;
    }

    public void setMacDataLen(int macDataLen) {
        this.macDataLen = macDataLen;
    }
}
