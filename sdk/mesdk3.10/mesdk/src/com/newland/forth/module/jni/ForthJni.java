package com.newland.forth.module.jni;
import com.newland.forth.module.crypto.cipher.ST_SEC_DUKPT_DERIVATE_DATA;
import com.newland.forth.module.crypto.cipher.ST_SEC_ENCRYPTION_DATA;
import com.newland.forth.module.crypto.keystore.ST_SEC_KCV_DATA;
import com.newland.forth.module.crypto.keystore.ST_SEC_KEYIN_DATA;

/**
 * Author by wuhh, Date on 2020/2/10.
 */
public class ForthJni {
    public native int NAPI_SecGenerateKey(int method, ST_SEC_KEYIN_DATA keyData, ST_SEC_KCV_DATA kcvData);
    public native int NAPI_SecDeleteKey(int keyId,int keyType,int keyUsage);
    public native int NAPI_SecGetKeyInfo(int infoID,int keyId,int keyType,int keyUsage,byte[] pAD,int adSize,byte[] outInfo,int[] outInfoLen);
    public native int NDK_SecSetKeyOwner(String pszName);
    public native int NAPI_SecGetKeyOwner(int nLenOfOwnerBuffer,byte[] pszOwner);

    public native int NAPI_SecGenerateMAC(int MacType, int ucKeyID, byte[] psIV, int unIVSize, byte[] psDataIn, int nDataInLen,
                                          byte[] pAD, int unADSize, byte[] psMacOut, int[] pnOutLen, byte[] psKsnOut, int[] nOutKsnLen);

    public native int NAPI_SecGenerateMAC_DerivateKey(int mac_type, int uc_key_id, byte[] iv, int iv_len, byte[] dataIn, int length,
                                                      ST_SEC_DUKPT_DERIVATE_DATA dukpt_derivate_data, byte[] out_data, int[] out_data_len,
                                                      byte[] ksn_data, int[] ksn_data_len);

    public native int NAPI_SecEncryption(ST_SEC_ENCRYPTION_DATA DataIn, byte[] psDataOut, int[] pnOutLen, byte[] psKsnOut, int[] pnOutKsnLen);
    public native int NAPI_SecDecryption(ST_SEC_ENCRYPTION_DATA DataIn, byte[] psDataOut, int[] pnOutLen, byte[] psKsnOut, int[] pnOutKsnLen);
//    public native int NDK_SecVppTpInit(byte[] numBtn, byte[] funcBtn, byte[] keySeq);
//    public native int NAPI_SecVPPInit(int SessionType, int KeyType, int ucKeyIdx, String pPAN, int PINBlockFmt, int unTimeOut, ST_NAPI_RSA_KEY pRSAKey, byte[] pAD, int unADSize);
//    public native int NAPI_SecVPPGetEvent(int[] nEvent,byte[] psPinBlock, int[] pnOutPinLen, byte[] psKsn, int[] pnOutKsnLen);
//    public native int NAPI_SecVPPSetEvent(int key);
//    public native int NAPI_SecVPPSetExpPinLenIn(String pinLenIn);

    public native int NAPI_SecResetCertStatus();
    public native int NAPI_SecLoadTrustedCert(char isCA, byte[] cert, int certlen, byte[] pubkey, int[] pubkeylen);

//    public static native int JNI_SecAsymGenerateKey(byte[] sk_data, int sk_type,int sk_len, int[] adValue);
//
//    public static native int JNI_SecInitAtomic();
//
//    public static native int JNI_SecCommitAtomic(int isTrue);

    public native int NAPI_SecVppRNIBTpInit(int[] coordination, int[] area_coordination, int[] key_pad_coordination, int key_number);
    public native int isSupportNapi();

    private static boolean libLoadSucc = true;
    private static ForthJni forthJni;
    private ForthJni(){}

    static {
        try {
            System.loadLibrary("intelligentLib");
        } catch (Throwable e) {
            libLoadSucc = false;
            e.printStackTrace();
        }
    }

    public static ForthJni getInstance(){
        if(forthJni == null){
            synchronized (ForthJni.class){
                if(forthJni == null){
                    forthJni = new ForthJni();
                }
            }
        }
        return forthJni;
    }

    private boolean isLibLoadSucc(){
        return libLoadSucc;
    }
}
