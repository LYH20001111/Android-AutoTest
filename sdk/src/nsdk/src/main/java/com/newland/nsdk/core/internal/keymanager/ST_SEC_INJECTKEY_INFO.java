package com.newland.nsdk.core.internal.keymanager;

import com.newland.nsdk.core.api.common.keymanager.AsymmetricKey;

public class ST_SEC_INJECTKEY_INFO {
    private int keyID;
    private int keyType;
    private int keyUsage;
    private String tag;
    private boolean injectResult;

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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isInjectResult() {
        return injectResult;
    }

    public void setInjectResult(boolean injectResult) {
        this.injectResult = injectResult;
    }
}
