package com.newland.sdk.module.pin;

/**
 * Copyright © 2023 Fujian Newland Payment Technology Co., Ltd
 * Author: wuhh
 * Date: 2024/5/24 17:23
 * Description:
 * History:
 * <author> <time> <version> <desc>
 */
public class NInjectKeyParams {
    byte[] ksn;
    CheckValue checkValue;
    CipherMode cipherMode;
    byte[] iv;
    PaddingMode paddingMode;
    DukptDerivedMode dukptDerivedMode;

    public byte[] getKsn() {
        return ksn;
    }

    public void setKsn(byte[] ksn) {
        this.ksn = ksn;
    }

    public CheckValue getCheckValue() {
        return checkValue;
    }

    public void setCheckValue(CheckValue checkValue) {
        this.checkValue = checkValue;
    }

    public CipherMode getCipherMode() {
        return cipherMode;
    }

    public void setCipherMode(CipherMode cipherMode) {
        this.cipherMode = cipherMode;
    }

    public byte[] getIv() {
        return iv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }

    public PaddingMode getPaddingMode() {
        return paddingMode;
    }

    public void setPaddingMode(PaddingMode paddingMode) {
        this.paddingMode = paddingMode;
    }

    public DukptDerivedMode getDukptDerivedMode() {
        return dukptDerivedMode;
    }

    public void setDukptDerivedMode(DukptDerivedMode dukptDerivedMode) {
        this.dukptDerivedMode = dukptDerivedMode;
    }
}
