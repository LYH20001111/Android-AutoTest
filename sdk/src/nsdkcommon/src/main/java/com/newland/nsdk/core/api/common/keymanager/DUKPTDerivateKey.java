package com.newland.nsdk.core.api.common.keymanager;

public class DUKPTDerivateKey extends SymmetricKey {
    private KeyType derivateKeyType;
    private DUKPTDerivateUsage derivateUsage;
    private int derivateKeyLen;

    public DUKPTDerivateKey(){
        setKeyUsage(KeyUsage.DUKPT);
    }

    public KeyType getDerivateKeyType() {
        return derivateKeyType;
    }

    public void setDerivateKeyType(KeyType derivateKeyType) {
        this.derivateKeyType = derivateKeyType;
    }

    public DUKPTDerivateUsage getDerivateUsage() {
        return derivateUsage;
    }

    public void setDerivateUsage(DUKPTDerivateUsage derivateUsage) {
        this.derivateUsage = derivateUsage;
    }

    public int getDerivateKeyLen() {
        return derivateKeyLen;
    }

    public void setDerivateKeyLen(int derivateKeyLen) {
        this.derivateKeyLen = derivateKeyLen;
    }
}
