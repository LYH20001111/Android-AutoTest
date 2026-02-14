package com.newland.nsdk.core.api.internal.emvl2.type;

public class aidlist_emv {
    public byte[] aid = new byte[16];
    public int aidLen;
    public int aidOpt;
    public byte[] codeTable = new byte[2];
    public byte[] resv = new byte[2];
    public int tagDefCount;
    public emvTagAttr[] tagDefs = new emvTagAttr[100];

}
