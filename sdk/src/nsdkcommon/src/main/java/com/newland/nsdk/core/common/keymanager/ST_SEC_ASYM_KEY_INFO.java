package com.newland.nsdk.core.common.keymanager;

public class ST_SEC_ASYM_KEY_INFO {
    int keyType;
    int keyUsage;
    int keyIdx;

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

    public int getKeyIdx() {
        return keyIdx;
    }

    public void setKeyIdx(int keyIdx) {
        this.keyIdx = keyIdx;
    }
}
