package com.newland.nsdk.core.internal.ecdhe;

public class ST_SEC_ECDHE_KDF_INFO {
    private int kdfType;
    private int mdAlg;
    private int saltLen;
    private byte[] salt;
    private int infoLen;
    private byte[] info;

    public int getKdfType() {
        return kdfType;
    }

    public void setKdfType(int kdfType) {
        this.kdfType = kdfType;
    }

    public int getMdAlg() {
        return mdAlg;
    }

    public void setMdAlg(int mdAlg) {
        this.mdAlg = mdAlg;
    }

    public int getSaltLen() {
        return saltLen;
    }

    public void setSaltLen(int saltLen) {
        this.saltLen = saltLen;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public int getInfoLen() {
        return infoLen;
    }

    public void setInfoLen(int infoLen) {
        this.infoLen = infoLen;
    }

    public byte[] getInfo() {
        return info;
    }

    public void setInfo(byte[] info) {
        this.info = info;
    }
}
