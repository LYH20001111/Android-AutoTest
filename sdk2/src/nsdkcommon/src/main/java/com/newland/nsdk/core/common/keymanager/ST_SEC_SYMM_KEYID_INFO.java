package com.newland.nsdk.core.common.keymanager;

public class ST_SEC_SYMM_KEYID_INFO {
    int keyUsage;
    int keyType;
    byte[] sCheckBuf = new byte[8];
    int checkLen;

    public int getKeyUsage() {
        return keyUsage;
    }

    public void setKeyUsage(int keyUsage) {
        this.keyUsage = keyUsage;
    }

    public int getKeyType() {
        return keyType;
    }

    public void setKeyType(int keyType) {
        this.keyType = keyType;
    }

    public byte[] getsCheckBuf() {
        return sCheckBuf;
    }

    public void setsCheckBuf(byte[] sCheckBuf) {
        this.sCheckBuf = sCheckBuf;
    }

    public int getCheckLen() {
        return checkLen;
    }

    public void setCheckLen(int checkLen) {
        this.checkLen = checkLen;
    }
}
