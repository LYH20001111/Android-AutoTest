package com.newland.nsdk.core.external.command.keyboard;

public class KeyboardEntryResult {
    private byte[] encryptedPinBlock;
    private byte[] ksn;
    private byte[] encryptedRandomPinKey;
    private byte[] encryptedData;
    private int dataLen;
    private int pinLen;
    private byte[] tlvData;

    public byte[] getEncryptedPinBlock() {
        return encryptedPinBlock;
    }

    public void setEncryptedPinBlock(byte[] encryptedPinBlock) {
        this.encryptedPinBlock = encryptedPinBlock;
    }

    public byte[] getKsn() {
        return ksn;
    }

    public void setKsn(byte[] ksn) {
        this.ksn = ksn;
    }

    public byte[] getEncryptedRandomPinKey() {
        return encryptedRandomPinKey;
    }

    public void setEncryptedRandomPinKey(byte[] encryptedRandomPinKey) {
        this.encryptedRandomPinKey = encryptedRandomPinKey;
    }

    public byte[] getEncryptedData() {
        return encryptedData;
    }

    public void setEncryptedData(byte[] encryptedData) {
        this.encryptedData = encryptedData;
    }

    public int getDataLen() {
        return dataLen;
    }

    public void setDataLen(int dataLen) {
        this.dataLen = dataLen;
    }

    public int getPinLen() {
        return pinLen;
    }

    public void setPinLen(int pinLen) {
        this.pinLen = pinLen;
    }

    public byte[] getTlvData() {
        return tlvData;
    }

    public void setTlvData(byte[] tlvData) {
        this.tlvData = tlvData;
    }
}
