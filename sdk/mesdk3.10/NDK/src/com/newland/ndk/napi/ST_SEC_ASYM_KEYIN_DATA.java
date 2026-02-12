package com.newland.ndk.napi;

/**
 * @Description
 * @Author wuhh
 * @Date 2020/11/27
 */
public class ST_SEC_ASYM_KEYIN_DATA {
    public int ucKEKIdx;
    public EM_SEC_CRYPTO_KEY_TYPE KEKType;
    public EM_SEC_KEY_USAGE KEKUsage;
    public int ucKeyIdx;
    public EM_SEC_CRYPTO_KEY_TYPE KeyType;
    public EM_SEC_KEY_USAGE KeyUsage;
    public EM_SEC_MD_TYPE MdAlg;
    public EM_SEC_ASYM_ENCODING_MODE EncodingMode;
    public int nKeyLen;
    public byte[] pKeyData;
    public int nKsnLen;
    public byte[] psKsn;
    public int nADSize;
    public byte[] pAD;
}
