package com.newland.nsdk.core.api.common.keymanager;

public class AsymAlgInfo {
    private int unBit = 0;
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
