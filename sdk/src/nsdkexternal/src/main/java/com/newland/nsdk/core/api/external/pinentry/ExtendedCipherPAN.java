package com.newland.nsdk.core.api.external.pinentry;

import com.newland.nsdk.core.api.common.crypto.CipherType;

public class ExtendedCipherPAN extends CipherPAN{
    private CipherType cipherType;
    private byte[] iv;
    private byte[] additionalData;

    public byte[] getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(byte[] additionalData) {
        this.additionalData = additionalData;
    }

    public CipherType getCipherType() {
        return cipherType;
    }

    public void setCipherType(CipherType cipherType) {
        this.cipherType = cipherType;
    }

    public byte[] getIv() {
        return iv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }
}
