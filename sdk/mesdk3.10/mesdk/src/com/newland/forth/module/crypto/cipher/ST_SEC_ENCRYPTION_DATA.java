package com.newland.forth.module.crypto.cipher;

/**
 * Author by wuhh, Date on 2020/2/18.
 */
public class ST_SEC_ENCRYPTION_DATA {
    private int ucKeyID;
    private int CipherType;
    private int KeyUsage;
    private int PaddingMode;
    private int unIVSize;
    private byte[] psIV;
    private int unDataInLen;
    private byte[] psDataIn;
    private int unADSize;
    private byte[] pAD;

    private ST_SEC_DUKPT_DERIVATE_DATA dukptDerivateData;
    public void setUcKeyID(int ucKeyID) {
        this.ucKeyID = ucKeyID;
    }

    public void setCipherType(int cipherType) {
        CipherType = cipherType;
    }

    public void setKeyUsage(int keyUsage) {
        KeyUsage = keyUsage;
    }

    public void setPaddingMode(int paddingMode) {
        PaddingMode = paddingMode;
    }

    public void setUnIVSize(int unIVSize) {
        this.unIVSize = unIVSize;
    }

    public void setPsIV(byte[] psIV) {
        this.psIV = psIV;
    }

    public void setUnDataInLen(int unDataInLen) {
        this.unDataInLen = unDataInLen;
    }

    public void setPsDataIn(byte[] psDataIn) {
        this.psDataIn = psDataIn;
    }

    public void setUnADSize(int unADSize) {
        this.unADSize = unADSize;
    }

    public void setpAD(byte[] pAD) {
        this.pAD = pAD;
    }

    public void setDukptDerivateData(ST_SEC_DUKPT_DERIVATE_DATA dukptDerivateData) {
        this.dukptDerivateData = dukptDerivateData;
    }
}
