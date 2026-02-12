package com.newland.ndk.napi;

/**
 * @Description
 * @Author wuhh
 * @Date 2020/11/27
 */
public class JNI_ST_SEC_ASYM_KEYIN_DATA {
    public int ucKEKIdx;
    public int KEKType;
    public int KEKUsage;
    public int ucKeyIdx;
    public int KeyType;
    public int KeyUsage;
    public int MdAlg;
    public int EncodingMode;
    public int nKeyLen;      
    public byte[] pKeyData;
    public int nKsnLen;
    public byte[] psKsn;
    public int nADSize;
    public byte[] pAD;
}
