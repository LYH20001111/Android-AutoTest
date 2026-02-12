package com.newland.forth.module.crypto.cipher;

import android.util.Log;

import com.newland.forth.spi.common.NDKErrorCode;
import com.newland.forth.spi.crypto.cipher.AlgorithmParameters;
import com.newland.forth.spi.crypto.cipher.CipherOutput;
import com.newland.forth.spi.crypto.cipher.CipherSpi;
import com.newland.forth.spi.crypto.cipher.CipherType;
import com.newland.forth.spi.crypto.cipher.PaddingMode;
import com.newland.forth.spi.crypto.keystore.CipherMode;
import com.newland.forth.spi.crypto.keystore.DUKPTKey;
import com.newland.forth.spi.crypto.keystore.DukptDerivedMode;
import com.newland.forth.spi.crypto.keystore.Key;
import com.newland.forth.spi.crypto.keystore.KeyType;
import com.newland.forth.module.jni.ForthJni;
import com.newland.sdk.module.pin.DukptDerivateUsage;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;

/**
 * The interface Cipher spi.
 */
public class Cipher implements CipherSpi {

    private DeviceLogger devicelogger = DeviceLoggerFactory.getLogger("Cipher");
    private static final String TAG = "Cipher";
    private static final int MODE_ENCRYPT = 1;
    private static final int MODE_DECRYPT = 2;
    /**
     * Encrypt int.
     *
     * @param key                 the key
     * @param algorithmParameters the algorithm parameters
     * @param iv                  the iv
     * @param datain              the datain
     * @return the int
     */
    @Override
    public CipherOutput encrypt(Key key, AlgorithmParameters algorithmParameters, byte[] iv, byte[] datain){
        return calculate(MODE_ENCRYPT,key,algorithmParameters,iv,datain);
    }

    /**
     * Decrypt int.
     *
     * @param key                 the key
     * @param algorithmParameters the algorithm parameters
     * @param iv                  the iv
     * @param datain              the datain
     * @return the int
     */
    @Override
    public CipherOutput decrypt(Key key, AlgorithmParameters algorithmParameters, byte[] iv, byte[] datain){
        return calculate(MODE_DECRYPT,key,algorithmParameters,iv,datain);
    }


    private CipherOutput calculate(int mode,Key key, AlgorithmParameters algorithmParameters, byte[] iv, byte[] datain){
        if(key == null || algorithmParameters == null || datain == null ){
            devicelogger.debug("calculate key="+key+" algorithmParameters="+algorithmParameters+" datain="+datain);
            return null;
        }
        ST_SEC_ENCRYPTION_DATA encryptionData = new ST_SEC_ENCRYPTION_DATA();
        encryptionData.setUcKeyID(key.getKeyId());

        KeySys keySys = KeySys.MKSK;
        if(key instanceof DUKPTKey){
            keySys = KeySys.DUKPT;
        }
        KeyType keyType = key.getKeyType();
        CipherMode cipherMode = algorithmParameters.getCipherMode();
        DukptDerivedMode dukptDerivedMode = algorithmParameters.getDukptDerivedMode();
        DukptDerivateUsage dukptDerivateUsage = algorithmParameters.getDukptDerivateUsage();
        int derivateKeyLen = algorithmParameters.getDerivateKeyLen();
        if(keyType == null || cipherMode == null){
            devicelogger.debug("calculate keyType="+keyType+" cipherMode="+cipherMode);
            return null;
        }
        if((key instanceof DUKPTKey) && dukptDerivedMode == null){
            devicelogger.debug("calculate key="+key+" dukptDerivedMode="+dukptDerivedMode);
            return null;
        }
        if(((key instanceof DUKPTKey) && keyType == KeyType.AES) && (dukptDerivateUsage == null || derivateKeyLen <= 0 )){
            devicelogger.debug("calculate key="+key+" keyType="+keyType+" dukptDerivateUsage="+dukptDerivateUsage+" derivateKeyLen="+derivateKeyLen);
            return null;
        }
        CipherType cipherType = null;
        if(keySys == KeySys.MKSK && keyType == KeyType.DES && cipherMode ==  CipherMode.SEC_CIPHER_MODE_ECB){
            cipherType = CipherType.SEC_CIPHER_DES_ECB;
        }else if(keySys == KeySys.MKSK && keyType == KeyType.DES && cipherMode ==  CipherMode.SEC_CIPHER_MODE_CBC){
            cipherType = CipherType.SEC_CIPHER_DES_CBC;
        }else if(keySys == KeySys.MKSK && keyType == KeyType.DES && cipherMode ==  CipherMode.SEC_CIPHER_MODE_CFB){
            cipherType = CipherType.SEC_CIPHER_DES_CFB;
        }else if(keySys == KeySys.MKSK && keyType == KeyType.DES && cipherMode ==  CipherMode.SEC_CIPHER_MODE_OFB){
            cipherType = CipherType.SEC_CIPHER_DES_OFB;
        }else if(keySys == KeySys.MKSK && keyType == KeyType.AES && cipherMode ==  CipherMode.SEC_CIPHER_MODE_ECB){
            cipherType = CipherType.SEC_CIPHER_AES_ECB;
        }else if(keySys == KeySys.MKSK && keyType == KeyType.AES && cipherMode ==  CipherMode.SEC_CIPHER_MODE_CBC){
            cipherType = CipherType.SEC_CIPHER_AES_CBC;
        }else if(keySys == KeySys.MKSK && keyType == KeyType.AES && cipherMode ==  CipherMode.SEC_CIPHER_MODE_CFB){
            cipherType = CipherType.SEC_CIPHER_AES_CFB;
        }else if(keySys == KeySys.MKSK && keyType == KeyType.AES && cipherMode ==  CipherMode.SEC_CIPHER_MODE_OFB){
            cipherType = CipherType.SEC_CIPHER_AES_OFB;
        }else if(keySys == KeySys.DUKPT && keyType == KeyType.DES && cipherMode ==  CipherMode.SEC_CIPHER_MODE_ECB && dukptDerivedMode == DukptDerivedMode.SEC_DUKPT_DERIVED_MODE_RESP){
            cipherType = CipherType.SEC_CIPHER_DUKPT_ECB_RESP;
        }else if(keySys == KeySys.DUKPT && keyType == KeyType.DES && cipherMode ==  CipherMode.SEC_CIPHER_MODE_ECB && dukptDerivedMode == DukptDerivedMode.SEC_DUKPT_DERIVED_MODE_BOTH){
            cipherType = CipherType.SEC_CIPHER_DUKPT_ECB_BOTH;
        }else if(keySys == KeySys.DUKPT && keyType == KeyType.DES && cipherMode ==  CipherMode.SEC_CIPHER_MODE_CBC && dukptDerivedMode == DukptDerivedMode.SEC_DUKPT_DERIVED_MODE_RESP){
            cipherType = CipherType.SEC_CIPHER_DUKPT_CBC_RESP;
        }else if(keySys == KeySys.DUKPT && keyType == KeyType.DES && cipherMode ==  CipherMode.SEC_CIPHER_MODE_CBC && dukptDerivedMode == DukptDerivedMode.SEC_DUKPT_DERIVED_MODE_BOTH){
            cipherType = CipherType.SEC_CIPHER_DUKPT_CBC_BOTH;
        }else if(keySys == KeySys.DUKPT && keyType == KeyType.DES && cipherMode ==  CipherMode.SEC_CIPHER_MODE_CFB && dukptDerivedMode == DukptDerivedMode.SEC_DUKPT_DERIVED_MODE_RESP){
            cipherType = CipherType.SEC_CIPHER_DUKPT_CFB_RESP;
        }else if(keySys == KeySys.DUKPT && keyType == KeyType.DES && cipherMode ==  CipherMode.SEC_CIPHER_MODE_CFB && dukptDerivedMode == DukptDerivedMode.SEC_DUKPT_DERIVED_MODE_BOTH){
            cipherType = CipherType.SEC_CIPHER_DUKPT_CFB_BOTH;
        }else if(keySys == KeySys.DUKPT && keyType == KeyType.DES && cipherMode ==  CipherMode.SEC_CIPHER_MODE_OFB && dukptDerivedMode == DukptDerivedMode.SEC_DUKPT_DERIVED_MODE_RESP){
            cipherType = CipherType.SEC_CIPHER_DUKPT_OFB_RESP;
        }else if(keySys == KeySys.DUKPT && keyType == KeyType.DES && cipherMode ==  CipherMode.SEC_CIPHER_MODE_OFB && dukptDerivedMode == DukptDerivedMode.SEC_DUKPT_DERIVED_MODE_BOTH){
            cipherType = CipherType.SEC_CIPHER_DUKPT_OFB_BOTH;
        }
        //同步NAPI修改
        else if(keySys == KeySys.DUKPT && keyType == KeyType.AES && cipherMode ==  CipherMode.SEC_CIPHER_MODE_ECB){
            cipherType = CipherType.SEC_CIPHER_AES_DUKPT_ECB;
        }else if(keySys == KeySys.DUKPT && keyType == KeyType.AES && cipherMode ==  CipherMode.SEC_CIPHER_MODE_CBC){
            cipherType = CipherType.SEC_CIPHER_AES_DUKPT_CBC;
        }
        /* 同步NAPI删除,NAPI后面实现,代码保留.
        else if(keySys == KeySys.DUKPT && keyType == KeyType.AES && cipherMode ==  CipherMode.SEC_CIPHER_MODE_ECB && dukptDerivedMode == DukptDerivedMode.SEC_DUKPT_DERIVED_MODE_RESP){
            cipherType = CipherType.SEC_CIPHER_AES_DUKPT_ECB_RESP;
        }else if(keySys == KeySys.DUKPT && keyType == KeyType.AES && cipherMode ==  CipherMode.SEC_CIPHER_MODE_ECB && dukptDerivedMode == DukptDerivedMode.SEC_DUKPT_DERIVED_MODE_BOTH){
            cipherType = CipherType.SEC_CIPHER_AES_DUKPT_ECB_BOTH;
        }

        else if(keySys == KeySys.DUKPT && keyType == KeyType.AES && cipherMode ==  CipherMode.SEC_CIPHER_MODE_CBC && dukptDerivedMode == DukptDerivedMode.SEC_DUKPT_DERIVED_MODE_RESP){
            cipherType = CipherType.SEC_CIPHER_AES_DUKPT_CBC_RESP;
        }else if(keySys == KeySys.DUKPT && keyType == KeyType.AES && cipherMode ==  CipherMode.SEC_CIPHER_MODE_CBC && dukptDerivedMode == DukptDerivedMode.SEC_DUKPT_DERIVED_MODE_BOTH){
            cipherType = CipherType.SEC_CIPHER_AES_DUKPT_CBC_BOTH;
        }

        else if(keySys == KeySys.DUKPT && keyType == KeyType.AES && cipherMode ==  CipherMode.SEC_CIPHER_MODE_CFB && dukptDerivedMode == DukptDerivedMode.SEC_DUKPT_DERIVED_MODE_RESP){
            cipherType = CipherType.SEC_CIPHER_AES_DUKPT_CFB_RESP;
        }else if(keySys == KeySys.DUKPT && keyType == KeyType.AES && cipherMode ==  CipherMode.SEC_CIPHER_MODE_CFB && dukptDerivedMode == DukptDerivedMode.SEC_DUKPT_DERIVED_MODE_BOTH){
            cipherType = CipherType.SEC_CIPHER_AES_DUKPT_CFB_BOTH;
        }

        else if(keySys == KeySys.DUKPT && keyType == KeyType.AES && cipherMode ==  CipherMode.SEC_CIPHER_MODE_OFB && dukptDerivedMode == DukptDerivedMode.SEC_DUKPT_DERIVED_MODE_RESP){
            cipherType = CipherType.SEC_CIPHER_AES_DUKPT_OFB_RESP;
        }else if(keySys == KeySys.DUKPT && keyType == KeyType.AES && cipherMode ==  CipherMode.SEC_CIPHER_MODE_OFB && dukptDerivedMode == DukptDerivedMode.SEC_DUKPT_DERIVED_MODE_BOTH){
            cipherType = CipherType.SEC_CIPHER_AES_DUKPT_OFB_BOTH;
        }
        */
        else if(keySys == KeySys.MKSK && keyType == KeyType.SM4 && cipherMode ==  CipherMode.SEC_CIPHER_MODE_ECB ){
            cipherType = CipherType.SEC_CIPHER_SM4_ECB;
        }else if(keySys == KeySys.MKSK && keyType == KeyType.SM4 && cipherMode ==  CipherMode.SEC_CIPHER_MODE_CBC ){
            cipherType = CipherType.SEC_CIPHER_SM4_CBC;
        }else {
            Log.d(TAG,">>>encrypt cipherType is null.");
            return null;

        }
        encryptionData.setCipherType(cipherType.getCode());
        encryptionData.setKeyUsage(key.getKeyUsage().getCode());
        if(algorithmParameters.getPaddingMode() != null){
            encryptionData.setPaddingMode(algorithmParameters.getPaddingMode().ordinal());
        }else {
            encryptionData.setPaddingMode(PaddingMode.SEC_PADDING_NONE.ordinal());
        }
        if(iv != null){
            encryptionData.setUnIVSize(iv.length);
            encryptionData.setPsIV(iv);
        }else {
            encryptionData.setUnIVSize(0);
            encryptionData.setPsIV(null);
        }
        encryptionData.setUnDataInLen(datain.length);
        encryptionData.setPsDataIn(datain);
        encryptionData.setUnADSize(0);
        encryptionData.setpAD(null);

        byte[] outData = new byte[4096];
        int[] outDataLen = new int[1];

        byte[] ksnData = new byte[32];
        int[] ksnDataLen = new int[1];
        int ret = NDKErrorCode.NDK_ERR;

        if((key instanceof DUKPTKey) && keyType == KeyType.AES){
            ST_SEC_DUKPT_DERIVATE_DATA dukptDerivateData = new ST_SEC_DUKPT_DERIVATE_DATA();
            dukptDerivateData.setDerivateKeyType((KeyType.AES.ordinal()));
            Log.d(TAG, "calculate: dukptDerivateData.derivateKeyType="+dukptDerivateData.derivateKeyType);
            dukptDerivateData.setDerivateKeyUsage(dukptDerivateUsage.ordinal());
            dukptDerivateData.setDerivateKeyLen(derivateKeyLen);
            encryptionData.setDukptDerivateData(dukptDerivateData);
        }
        if(mode == MODE_ENCRYPT){
            ret = ForthJni.getInstance().NAPI_SecEncryption(encryptionData,outData,outDataLen,ksnData,ksnDataLen);
        }else if(mode == MODE_DECRYPT){
            ret = ForthJni.getInstance().NAPI_SecDecryption(encryptionData,outData,outDataLen,ksnData,ksnDataLen);
        }
        byte[] data = null; byte[] ksn = null;
        if(ret == NDKErrorCode.NDK_OK){
            if(outDataLen[0] > 0){
                data = new byte[outDataLen[0]];
                System.arraycopy(outData,0,data,0,data.length);
            }
            if(ksnDataLen[0] > 0){
                ksn = new byte[ksnDataLen[0]];
                System.arraycopy(ksnData,0,ksn,0,ksn.length);
            }
        }
        return new CipherOutput(ret,data,ksn);
    }

}
