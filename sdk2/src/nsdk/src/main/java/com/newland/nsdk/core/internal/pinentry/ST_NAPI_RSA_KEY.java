package com.newland.nsdk.core.internal.pinentry;

/**
 * Author by wuhh, Date on 2020/2/19.
 */
public class ST_NAPI_RSA_KEY {
    private int usBits;
    private byte[] sModulus;
    private byte[] sExponent;

    public ST_NAPI_RSA_KEY(int usBits, byte[] sModulus, byte[] sExponent) {
        this.usBits = usBits;
        this.sModulus = sModulus;
        this.sExponent = sExponent;
    }

    public int getUsBits() {
        return usBits;
    }

    public byte[] getsModulus() {
        return sModulus;
    }

    public byte[] getsExponent() {
        return sExponent;
    }
}
