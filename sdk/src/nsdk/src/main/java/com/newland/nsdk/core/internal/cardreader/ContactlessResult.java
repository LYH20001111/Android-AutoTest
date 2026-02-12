package com.newland.nsdk.core.internal.cardreader;

public class ContactlessResult {
    private byte[] idmpmm = new byte[512];
    private int idmpmmLen;

    public byte[] getIdmpmm() {
        return idmpmm;
    }

    public void setIdmpmm(byte[] idmpmm) {
        this.idmpmm = idmpmm;
    }

    public int getIdmpmmLen() {
        return idmpmmLen;
    }

    public void setIdmpmmLen(int idmpmmLen) {
        this.idmpmmLen = idmpmmLen;
    }
}
