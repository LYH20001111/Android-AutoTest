package com.newland.sdk.me.module.emvl3.jni;

/**
 * @Description
 * @Author wuhh
 * @Date 2019/12/30
 */
public class EntryDIA {
    public byte[] aid = new byte[16];
    public byte	aidLen;
    public byte[] kernelId = new byte[8];
    public byte	externCheckFlag;
    public byte	transactionType;
    public String externString;
    public byte   externStrLen;
}
