package com.newland.nsdk.core.api.internal.emvl2.type;

public class candidate_emv {
    public aidlist_emv aidList = new aidlist_emv();
    public byte[] adfName = new byte[16];
    public int adfLen;
    /**
     * < length of AID
     */
    public byte[] label = new byte[16 + 1];
    public byte[] apn = new byte[16 + 1];
    public byte api;
    public byte codeTable;
    public byte[] language = new byte[8 + 1];
    public int tagDataSize;
    public byte[] tagData = new byte[256];
}
