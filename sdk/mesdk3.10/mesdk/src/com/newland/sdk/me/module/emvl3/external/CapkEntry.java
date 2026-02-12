package com.newland.sdk.me.module.emvl3.external;

/**
 * @Description
 * @Author wuhh
 * @Date 2021/3/30
 */
public class CapkEntry {
    public byte[] pkModulus = new byte[248];
    public int pkModulusLen;
    public byte[] pkExponent = new byte[3];
    public byte[] hashValue = new byte[20];
    public byte[] expiredDate = new byte[4];
    public byte[] rid = new byte[5];
    public int index;
    public byte pkAlgorithmIndicator;
    public byte hashAlgorithmIndicator;
    public byte[] rfu = new byte[4];
}
