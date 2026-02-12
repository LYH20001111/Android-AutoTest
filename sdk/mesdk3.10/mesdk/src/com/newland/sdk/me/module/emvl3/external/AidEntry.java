package com.newland.sdk.me.module.emvl3.external;

/**
 * @Description
 * @Author wuhh
 * @Date 2021/3/30
 */

public class AidEntry {
    public byte[] aid = new byte[16];
    public int aidLen;
    public byte[] kernelId = new byte[8];
    public byte externCheckFlag;
    public byte transactionType;
    public byte[] externString = new byte[50];
    public int externStrLen;
}