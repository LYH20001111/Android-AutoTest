package com.newland.forth.module.crypto.cipher;

/**
 * @Description
 * @Author wuhh
 * @Date 2023/4/14
 */
public class ST_SEC_DUKPT_DERIVATE_DATA {
    public int derivateKeyType;
    private int derivateKeyUsage;
    private int derivateKeyLen;

    public ST_SEC_DUKPT_DERIVATE_DATA() {
    }

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
