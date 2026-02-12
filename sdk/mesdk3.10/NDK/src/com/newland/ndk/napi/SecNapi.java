package com.newland.ndk.napi;

import java.security.KeyPairGenerator;

/**
 * @Description
 * @Author wuhh
 * @Date 2020/11/26
 */
public class SecNapi {

    public native int NAPI_SecGenerateKey(int method, ST_SEC_KEYIN_DATA keyData, ST_SEC_KCV_DATA kcvData);

    public native int NAPI_SecGetKeyInfo(int infoID,int keyId,int keyType,int keyUsage,byte[] pAD,int adSize,byte[] outInfo,int[] outInfoLen);

    public int NAPI_SecGenerateAsymKey(ST_SEC_ASYM_KEY_INFO pstKeyinfo, ST_SEC_ASYM_ALG_INFO asymAlgInfo) {
        int adSize = 0;
        byte[] adDdata = null;
        if (asymAlgInfo != null ) {
            adSize = asymAlgInfo.getSize();
            adDdata = asymAlgInfo.getData();
        }
        return NAPI_SecGenerateAsymKey(pstKeyinfo, adSize, adDdata);
    }

    private int NAPI_SecGenerateAsymKey(ST_SEC_ASYM_KEY_INFO pstKeyinfo, int nADSize, byte[] pAD) {
        return NAPI_SecGenerateAsymKey0(pstKeyinfo.KeytType.getCode(), pstKeyinfo.KeyUsage.getCode(), pstKeyinfo.KeyIdx, nADSize, pAD);
    }

    private native int NAPI_SecGenerateAsymKey0(int keyType, int KeyUsage, int KeyIdx, int nADSize, byte[] pAD);


    public int NAPI_SecAsymEncryption(ST_SEC_ASYM_KEY_INFO pstKeyinfo, EM_SEC_MD_TYPE MdAlg,
                                      EM_SEC_ASYM_ENCODING_MODE encodingMode, EM_SEC_ASYM_CRYPTO_MODE CryptoMode,
                                      int nDataInLen, byte[] psDataIn, int[] pnDataOutLen, byte[] psDataOut) {
        return NAPI_SecAsymEncryption0(pstKeyinfo.KeytType.getCode(), pstKeyinfo.KeyUsage.getCode(), pstKeyinfo.KeyIdx, MdAlg.ordinal(),
                encodingMode.ordinal(), CryptoMode.ordinal(),
                nDataInLen, psDataIn, pnDataOutLen, psDataOut);
    }

    private native int NAPI_SecAsymEncryption0(int keyType, int KeyUsage, int KeyIdx, int mdType,
                                               int encodingMode, int cryptoMode,
                                               int nDataInLen, byte[] psDataIn, int[] pnDataOutLen, byte[] psDataOut);

    public int NAPI_SecAsymDecryption(ST_SEC_ASYM_KEY_INFO pstKeyinfo, EM_SEC_MD_TYPE MdAlg,
                                      EM_SEC_ASYM_ENCODING_MODE encodingMode, EM_SEC_ASYM_CRYPTO_MODE CryptoMode,
                                      int nDataInLen, byte[] psDataIn, int[] pnDataOutLen, byte[] psDataOut) {
        return NAPI_SecAsymDecryption0(pstKeyinfo.KeytType.getCode(), pstKeyinfo.KeyUsage.getCode(), pstKeyinfo.KeyIdx, MdAlg.ordinal(),
                encodingMode.ordinal(), CryptoMode.ordinal(),
                nDataInLen, psDataIn, pnDataOutLen, psDataOut);
    }

    private native int NAPI_SecAsymDecryption0(int keyType, int KeyUsage, int KeyIdx, int mdType,
                                               int encodingMode, int cryptoMode,
                                               int nDataInLen, byte[] psDataIn, int[] pnDataOutLen, byte[] psDataOut);


    public int NAPI_SecAsymSign(ST_SEC_ASYM_KEY_INFO pstKeyinfo, EM_SEC_MD_TYPE MdAlg, EM_SEC_ASYM_ENCODING_MODE EncodingMode,
                                int nHashLen, byte[] psHash, int[] nSigLen, byte[] psSig) {
        return NAPI_SecAsymSign0(pstKeyinfo.KeytType.getCode(), pstKeyinfo.KeyUsage.getCode(), pstKeyinfo.KeyIdx, MdAlg.ordinal(), EncodingMode.ordinal(),
                nHashLen, psHash, nSigLen, psSig);
    }

    private native int NAPI_SecAsymSign0(int keyType, int KeyUsage, int KeyIdx, int MdAlg, int EncodingMode,
                                         int nHashLen, byte[] psHash, int[] nSigLen, byte[] psSig);


    public int NAPI_SecAsymVerify(ST_SEC_ASYM_KEY_INFO pstKeyinfo, EM_SEC_MD_TYPE MdAlg, EM_SEC_ASYM_ENCODING_MODE EncodingMode,
                                  int nHashLen, byte[] psHash, int nSigLen, byte[] psSig) {
        return NAPI_SecAsymVerify0(pstKeyinfo.KeytType.getCode(), pstKeyinfo.KeyUsage.getCode(), pstKeyinfo.KeyIdx, MdAlg.ordinal(), EncodingMode.ordinal(),
                nHashLen, psHash, nSigLen, psSig);
    }

    private native int NAPI_SecAsymVerify0(int keyType, int KeyUsage, int KeyIdx, int MdAlg, int EncodingMode,
                                           int nHashLen, byte[] psHash, int nSigLen, byte[] psSig);


    public int NAPI_SecAsymGenerateKey(EM_SEC_KEYIN_METHOD Method, ST_SEC_ASYM_KEYIN_DATA pstKGData, ST_SEC_KCV_DATA pstKcvData) {
        JNI_ST_SEC_ASYM_KEYIN_DATA jniStSecAsymKeyinData = new JNI_ST_SEC_ASYM_KEYIN_DATA();
        jniStSecAsymKeyinData.ucKEKIdx = pstKGData.ucKEKIdx;
        if (pstKGData.KEKType != null)
            jniStSecAsymKeyinData.KEKType = pstKGData.KEKType.getCode();
        if (pstKGData.KEKUsage != null)
            jniStSecAsymKeyinData.KEKUsage = pstKGData.KEKUsage.getCode();
        jniStSecAsymKeyinData.ucKeyIdx = pstKGData.ucKeyIdx;
        if (pstKGData.KeyType != null)
            jniStSecAsymKeyinData.KeyType = pstKGData.KeyType.getCode();
        if (pstKGData.KeyUsage != null)
            jniStSecAsymKeyinData.KeyUsage = pstKGData.KeyUsage.getCode();
        if (pstKGData.MdAlg != null)
            jniStSecAsymKeyinData.MdAlg = pstKGData.MdAlg.ordinal();
        if (pstKGData.EncodingMode != null)
            jniStSecAsymKeyinData.EncodingMode = pstKGData.EncodingMode.ordinal();
        jniStSecAsymKeyinData.nKeyLen = pstKGData.nKeyLen;
        jniStSecAsymKeyinData.pKeyData = pstKGData.pKeyData;
        jniStSecAsymKeyinData.nKsnLen = pstKGData.nKsnLen;
        jniStSecAsymKeyinData.psKsn = pstKGData.psKsn;
        jniStSecAsymKeyinData.nADSize = pstKGData.nADSize;
        jniStSecAsymKeyinData.pAD = pstKGData.pAD;
        return NAPI_SecAsymGenerateKey0(Method.ordinal(), jniStSecAsymKeyinData, pstKcvData);
    }

    private native int NAPI_SecAsymGenerateKey0(int Method, JNI_ST_SEC_ASYM_KEYIN_DATA pstKGData, ST_SEC_KCV_DATA pstKcvData);

    public native int NAPI_SecResetCertStatus();

    public native int NAPI_SecLoadTrustedCert(char isCA, byte[] cert, int certlen, byte[] pubkey, int[] pubkeylen);

    public native int NAPI_SecInitAtomic();

    public native int NAPI_SecCommitAtomic(char status);

    public native int NewlandV1SecAsymGenerateKey(int RDH_ENC_ID,int RKLA_SK_ID,byte[] sk_data, int sk_type,int sk_len, int[] adValue);

    public native int NAPI_SecVPPSetButtonFunc(int button,int fun);
}
