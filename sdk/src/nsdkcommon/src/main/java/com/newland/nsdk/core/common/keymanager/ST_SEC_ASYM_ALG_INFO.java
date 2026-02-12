package com.newland.nsdk.core.common.keymanager;

public class ST_SEC_ASYM_ALG_INFO {
    private int unBit;
    private byte[] ucRSAPubExp = new byte[5];

    public int getUnBit() {
        return unBit;
    }

    public void setUnBit(int unBit) {
        this.unBit = unBit;
    }

    public byte[] getUcRSAPubExp() {
        return ucRSAPubExp;
    }

    public void setUcRSAPubExp(byte[] ucRSAPubExp) {
        this.ucRSAPubExp = ucRSAPubExp;
    }
}
