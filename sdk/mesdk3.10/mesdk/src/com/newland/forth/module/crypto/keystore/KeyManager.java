package com.newland.forth.module.crypto.keystore;

import com.newland.forth.spi.common.NDKErrorCode;
import com.newland.forth.spi.crypto.cipher.AlgorithmParameters;
import com.newland.forth.spi.crypto.cipher.KcvMode;
import com.newland.forth.spi.crypto.cipher.PaddingMode;
import com.newland.forth.module.jni.ForthJni;
import com.newland.forth.spi.crypto.keystore.CipherMode;
import com.newland.forth.spi.crypto.keystore.DUKPTKey;
import com.newland.forth.spi.crypto.keystore.Key;
import com.newland.forth.spi.crypto.keystore.KeyGenerateMethod;
import com.newland.forth.spi.crypto.keystore.KeyInfoID;
import com.newland.forth.spi.crypto.keystore.KeyManagerSpi;
import com.newland.forth.spi.crypto.keystore.SymmetricKey;

/**
 * The interface Key manager spi.
 */
public class KeyManager implements KeyManagerSpi {

    private static final String TAG = "KeySys";
    /**
     * Generate key int.
     *
     * @param method the method
     * @param SrcKey the src key
     * @param DstKey the dst key
     * @return the int
     */
    @Override
    public int generateKey(KeyGenerateMethod method, Key SrcKey, Key DstKey) {
        return generateKey(method,null,SrcKey,DstKey,null);
    }

    @Override
    public int generateKey(KeyGenerateMethod method, AlgorithmParameters algorithmParameters, Key SrcKey, Key DstKey) {
        return generateKey(method,algorithmParameters,SrcKey,DstKey,null);
    }

    @Override
    public int generateKey(KeyGenerateMethod method, AlgorithmParameters algorithmParameters, Key SrcKey, Key DstKey, byte[] iv) {
        if(method == null || SrcKey == null || DstKey == null){
            return NDKErrorCode.NDK_ERR_PARA;
        }
        ST_SEC_KEYIN_DATA keyData = new ST_SEC_KEYIN_DATA();
        keyData.setUcKEKIdx(SrcKey.getKeyId());
        if(SrcKey.getKeyType()!=null){
            keyData.setKEKType(SrcKey.getKeyType().ordinal());
        }
        if(SrcKey.getKeyUsage()!=null){
            keyData.setKEKUsage(SrcKey.getKeyUsage().getCode());
        }

        keyData.setUcKeyIdx(DstKey.getKeyId());
        if(DstKey.getKeyType()!=null){
            keyData.setKeyType(DstKey.getKeyType().ordinal());
        }
        if(DstKey.getKeyUsage()!=null){
            keyData.setKeyUsage(DstKey.getKeyUsage().getCode());
        }

        if(algorithmParameters != null && algorithmParameters.getCipherMode() != null){
            keyData.setCipherMode(algorithmParameters.getCipherMode().ordinal());
        }else {
            keyData.setCipherMode(CipherMode.SEC_CIPHER_MODE_ECB.ordinal());
        }

        if(algorithmParameters != null && algorithmParameters.getPaddingMode() != null){
            keyData.setPadingMode(algorithmParameters.getPaddingMode().ordinal());
        }else {
            keyData.setPadingMode(PaddingMode.SEC_PADDING_NONE.ordinal());
        }
        keyData.setnKeyLen(DstKey.getKeyLen());
        byte[] plainKeyData = ((SymmetricKey)DstKey).getPlaintextKeyVal();
        byte[] cipertextKeyVal = ((SymmetricKey)DstKey).getCipertextKeyVal();
        if(method == KeyGenerateMethod.SEC_KIM_CLEAR && plainKeyData != null){
            keyData.setnKeyDataLen(plainKeyData.length);
            keyData.setpKeyData(plainKeyData);
        }else if (cipertextKeyVal != null ){
            keyData.setnKeyDataLen(cipertextKeyVal.length);
            keyData.setpKeyData(cipertextKeyVal);
        }

        if(iv != null){
            keyData.setPsIV(iv);
        }

        if(DstKey instanceof DUKPTKey){
            DUKPTKey dukptKey = (DUKPTKey)DstKey;
            byte[] ksn = dukptKey.getKsn();
            if(ksn == null){
                return NDKErrorCode.NDK_ERR_PARA;
            }
            keyData.setnKsnLen(dukptKey.getKsn().length);
            keyData.setPsKsn(dukptKey.getKsn());
        }

        keyData.setnADSize(0);
        keyData.setpAD(null);

        ST_SEC_KCV_DATA kcvData = new ST_SEC_KCV_DATA();

        int kcvMode = (byte)((SymmetricKey) DstKey).getKcvMode().ordinal();
        kcvData.setnCheckMode(kcvMode);
        if (kcvMode != KcvMode.NAPI_SEC_KCV_NONE.ordinal()) {
            byte[] kcvValue = ((SymmetricKey) DstKey).getKcv();
            if(kcvValue == null){
                return NDKErrorCode.NDK_ERR_PARA;
            }
            kcvData.setnLen(kcvValue.length);
            kcvData.setsCheckBuf(kcvValue);
        }
        return ForthJni.getInstance().NAPI_SecGenerateKey(method.ordinal(),keyData,kcvData);
    }

    /**
     * Delete key int.
     *
     * @param Key the key
     * @return the int
     */
    @Override
    public int deleteKey(Key Key) {
        if(Key == null){
            return NDKErrorCode.NDK_ERR_PARA;
        }
        int keyType = -1;
        int keyUsage = -1;
        if(Key.getKeyType()!=null){
            keyType = Key.getKeyType().ordinal();
        }
        if(Key.getKeyUsage()!=null){
            keyUsage = Key.getKeyUsage().getCode();
        }
        return ForthJni.getInstance().NAPI_SecDeleteKey(Key.getKeyId(),keyType,keyUsage);
    }

    /**
     * Get key info int.
     *
     * @param infoID the info id
     * @param Key    the key
     * @param data   the data
     * @return the int
     */
    @Override
    public int getKeyInfo(KeyInfoID infoID, Key Key, byte[] data) {
        if(infoID == null || Key == null || data == null){
            return NDKErrorCode.NDK_ERR_PARA;
        }
        int keyType = -1;
        int keyUsage = -1;
        if(Key.getKeyType()!=null){
            keyType = Key.getKeyType().ordinal();
        }
        if(Key.getKeyUsage()!=null){
            keyUsage = Key.getKeyUsage().getCode();
        }
        byte[] outInfo = new byte[512];
        int[] outInfoLen = new int[1];
        int ret = ForthJni.getInstance().NAPI_SecGetKeyInfo(infoID.ordinal(),Key.getKeyId(),keyType,keyUsage,null,0,outInfo,outInfoLen);
        if(ret == NDKErrorCode.NDK_OK){
            if(data.length < outInfoLen[0]){
                return NDKErrorCode.NDK_ERR_PARA;
            }
            System.arraycopy(outInfo,0,data,0,outInfoLen[0]);
        }
        return ret;
    }

    /**
     * Set key owner int.
     *
     * @param keyOwner the key owner
     * @return the int
     */
    @Override
    public int setKeyOwner(String keyOwner) {
        if(keyOwner == null){
            return NDKErrorCode.NDK_ERR_PARA;
        }
        return ForthJni.getInstance().NDK_SecSetKeyOwner(keyOwner);
    }

    /**
     * Get key owner int.
     *
     * @param keyOwner the key owner
     * @return the int
     */
    @Override
    public int getKeyOwner(String keyOwner) {
        byte[] name = new byte[1024];
        int ret =  ForthJni.getInstance().NAPI_SecGetKeyOwner(name.length,name);
        if(ret != NDKErrorCode.NDK_OK){
            return ret;
        }
        keyOwner = new String(name).trim();
        return ret;
    }
}