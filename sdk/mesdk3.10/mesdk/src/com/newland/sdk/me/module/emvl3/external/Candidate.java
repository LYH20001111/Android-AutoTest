package com.newland.sdk.me.module.emvl3.external;

/**
 * @Description
 * @Author wuhh
 * @Date 2021/3/30
 */
public class Candidate {
    public byte[] aid = new byte[16];
    public byte aidLen;
    public byte[] lable = new byte[17];
    public byte[] preferName = new byte[17];
    public byte issuerCodeTableIndex;
    public byte[] terminalCodeTable = new byte[2];
    public byte[] languagePreference = new byte[8];
    public byte priority;
    public byte[] kernelId = new byte[8];
    public byte[] extendAid = new byte[16];
    public byte extendAidLen;
    public byte terminalPriority;
    public byte[] tag9F0A = new byte[256];
    public int tag9F0ALen;
    public byte[] customTagData = new byte[256];
    public int customDataSize;
}