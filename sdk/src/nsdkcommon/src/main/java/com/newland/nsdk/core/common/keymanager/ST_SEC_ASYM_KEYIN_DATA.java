package com.newland.nsdk.core.common.keymanager;

public class ST_SEC_ASYM_KEYIN_DATA {
    private int ucKEKIdx;
    private int KEKType;
    private int KEKUsage;

    private int ucKeyIdx;
    private int KeyType;
    private int KeyUsage;

    private int MdAlg;
    private int EncodingMode;

    private int nKeyLen;
    private byte[] pKeyData;

    private int nKsnLen;
    private byte[] psKsn;

    private int nADSize;
    private byte[] pAD;

    public int getUcKEKIdx() {
        return ucKEKIdx;
    }

    public void setUcKEKIdx(int ucKEKIdx) {
        this.ucKEKIdx = ucKEKIdx;
    }

    public int getKEKType() {
        return KEKType;
    }

    public void setKEKType(int KEKType) {
        this.KEKType = KEKType;
    }

    public int getKEKUsage() {
        return KEKUsage;
    }

    public void setKEKUsage(int KEKUsage) {
        this.KEKUsage = KEKUsage;
    }

    public int getUcKeyIdx() {
        return ucKeyIdx;
    }

    public void setUcKeyIdx(int ucKeyIdx) {
        this.ucKeyIdx = ucKeyIdx;
    }

    public int getKeyType() {
        return KeyType;
    }

    public void setKeyType(int keyType) {
        KeyType = keyType;
    }

    public int getKeyUsage() {
        return KeyUsage;
    }

    public void setKeyUsage(int keyUsage) {
        KeyUsage = keyUsage;
    }

    public int getnKeyLen() {
        return nKeyLen;
    }

    public void setnKeyLen(int nKeyLen) {
        this.nKeyLen = nKeyLen;
    }

    public byte[] getpKeyData() {
        return pKeyData;
    }

    public void setpKeyData(byte[] pKeyData) {
        this.pKeyData = pKeyData;
    }

    public int getnKsnLen() {
        return nKsnLen;
    }

    public void setnKsnLen(int nKsnLen) {
        this.nKsnLen = nKsnLen;
    }

    public byte[] getPsKsn() {
        return psKsn;
    }

    public void setPsKsn(byte[] psKsn) {
        this.psKsn = psKsn;
    }

    public int getnADSize() {
        return nADSize;
    }

    public void setnADSize(int nADSize) {
        this.nADSize = nADSize;
    }

    public byte[] getpAD() {
        return pAD;
    }

    public void setpAD(byte[] pAD) {
        this.pAD = pAD;
    }

    public int getMdAlg() {
        return MdAlg;
    }

    public void setMdAlg(int mdAlg) {
        MdAlg = mdAlg;
    }

    public int getEncodingMode() {
        return EncodingMode;
    }

    public void setEncodingMode(int encodingMode) {
        EncodingMode = encodingMode;
    }
}
