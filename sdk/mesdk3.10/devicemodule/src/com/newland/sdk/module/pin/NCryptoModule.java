package com.newland.sdk.module.pin;

import com.newland.ndk.napi.EM_SEC_ASYM_ENCODING_MODE;
import com.newland.ndk.napi.EM_SEC_MD_TYPE;
import com.newland.ndk.napi.ST_SEC_ASYM_KEY_INFO;

public interface NCryptoModule extends NPinpadModule{
    public boolean getKeyInfo(int infoID,int keyId,int keyType,int keyUsage,byte[] pAD,int adSize,byte[] outInfo,int[] outInfoLen);
    public boolean asymSign(ST_SEC_ASYM_KEY_INFO pstKeyinfo, EM_SEC_MD_TYPE MdAlg, EM_SEC_ASYM_ENCODING_MODE EncodingMode, int nHashLen, byte[] psHash, int[] nSigLen, byte[] psSig);

    public boolean resetCertStatus();
    public boolean loadTrustedCert(char isCA, byte[] cert, int certlen, byte[] pubkey, int[] pubkeylen);
}
