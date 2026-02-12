package com.newland.nsdk.core.internal.crypto;

public class ST_SEC_DUKPT_DERIVATE_DATA {
    private int derivateKeyType;
    private int derivateKeyUsage;
    private int derivateKeyLen;
    public void setDerivateKeyType(int derivateKeyType) {
        this.derivateKeyType = derivateKeyType;
    }

    public void setDerivateKeyUsage(int derivateKeyUsage) {
        this.derivateKeyUsage = derivateKeyUsage;
    }

    public void setDerivateKeyLen(int derivateKeyLen) {
        this.derivateKeyLen = derivateKeyLen;
    }
}
