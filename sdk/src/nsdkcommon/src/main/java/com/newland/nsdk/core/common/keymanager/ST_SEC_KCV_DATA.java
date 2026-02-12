package com.newland.nsdk.core.common.keymanager;

/**
 * Author by wuhh, Date on 2020/2/17.
 */
public class ST_SEC_KCV_DATA {
    private int nCheckMode;
    private int nLen;
    private byte[] sCheckBuf = new byte[8];

    public int getnCheckMode() {
        return nCheckMode;
    }

    public void setnCheckMode(int nCheckMode) {
        this.nCheckMode = nCheckMode;
    }

    public int getnLen() {
        return nLen;
    }

    public void setnLen(int nLen) {
        this.nLen = nLen;
    }

    public byte[] getsCheckBuf() {
        return sCheckBuf;
    }

    public void setsCheckBuf(byte[] sCheckBuf) {
        this.sCheckBuf = sCheckBuf;
    }
}
