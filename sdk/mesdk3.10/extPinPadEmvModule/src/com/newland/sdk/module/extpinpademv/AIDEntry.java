package com.newland.sdk.module.extpinpademv;

/**
 * Author by bxy, Date on 2019/12/30.
 */
public class AIDEntry {
    public byte[] aid = new byte[16];
    public byte	aidLen;
    public byte[] kernelId = new byte[8];
    public byte	externCheckFlag;
    public byte	transactionType;
    public String externString;
    public byte   externStrLen;
}
