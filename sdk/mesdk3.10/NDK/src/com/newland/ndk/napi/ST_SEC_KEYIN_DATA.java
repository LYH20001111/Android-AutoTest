package com.newland.ndk.napi;

/**
 * Author by wuhh, Date on 2020/2/17.
 */
public class ST_SEC_KEYIN_DATA {
    public int ucKEKIdx;
    public int KEKType;
    public int KEKUsage;

    public int ucKeyIdx;
    public int KeyType;
    public int KeyUsage;

    public int CipherMode;
    public int PadingMode;

    public int nKeyLen;
    public int nKeyDataLen;
    public byte[] pKeyData;

    public byte[] psIV;

    public int nKsnLen;
    public byte[] psKsn;

    public int nADSize;
    public byte[] pAD;
}
