package com.newland.sdk.module.extpinpademv;

/**
 * Author by bxy, Date on 2019/12/30.
 */
public class CRLEntry {
    public byte[] rid = new byte[5];
    public byte	index;
    public byte[] csn = new byte[3];
    public byte[] rfu = new byte[3];
}
