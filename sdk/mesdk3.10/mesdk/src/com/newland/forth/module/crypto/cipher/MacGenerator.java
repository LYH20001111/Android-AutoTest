package com.newland.forth.module.crypto.cipher;

import android.util.Log;

import com.newland.forth.spi.common.NDKErrorCode;
import com.newland.forth.spi.crypto.cipher.AlgorithmParameters;
import com.newland.forth.spi.crypto.cipher.MacGeneratorSpi;
import com.newland.forth.spi.crypto.cipher.MacMode;
import com.newland.forth.spi.crypto.cipher.MacOutput;
import com.newland.forth.spi.crypto.cipher.MacType;
import com.newland.forth.spi.crypto.keystore.DUKPTKey;
import com.newland.forth.spi.crypto.keystore.DukptDerivedMode;
import com.newland.forth.spi.crypto.keystore.Key;
import com.newland.forth.spi.crypto.keystore.KeyType;
import com.newland.forth.module.jni.ForthJni;
import com.newland.sdk.module.pin.DukptDerivateUsage;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;

/**
 * The type Mac generator.
 */
public class MacGenerator implements MacGeneratorSpi {
    private static final String TAG = "MacGenerator";
    private DeviceLogger devicelogger = DeviceLoggerFactory.getLogger("MacGenerator");
    /**
     *
     * @param key
     * @param algorithmParameters
     * @param iv
     * @param datain
     * @return
     */
    @Override
    public MacOutput generateMac(Key key, AlgorithmParameters algorithmParameters, byte[] iv, byte[] datain){
        if(key == null || algorithmParameters == null || datain == null){
            return null;
        }
        KeySys keySys = KeySys.MKSK;
        if(key instanceof DUKPTKey){
            keySys = KeySys.DUKPT;
        }
        KeyType keyType = key.getKeyType();
        MacMode macMode = algorithmParameters.getMacMode();
        DukptDerivedMode  dukptDerivedMode = algorithmParameters.getDukptDerivedMode();

        DukptDerivateUsage dukptDerivateUsage = algorithmParameters.getDukptDerivateUsage();
        int derivateKeyLen = algorithmParameters.getDerivateKeyLen();

        if(keyType == null || macMode == null){
            devicelogger.debug("calculate keyType="+keyType+" macMode="+macMode);
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

        MacType macType = null;
        if(keySys == KeySys.MKSK && keyType == KeyType.DES && macMode == MacMode.SEC_MAC_MODE_LAST){
            macType = MacType.SEC_MAC_TDES_LAST;
        }else if(keySys == KeySys.MKSK && keyType == KeyType.DES && macMode == MacMode.SEC_MAC_MODE_X99){
            macType = MacType.SEC_MAC_TDES_X99;
        }else if(keySys == KeySys.MKSK && keyType == KeyType.DES && macMode == MacMode.SEC_MAC_MODE_X919){
            macType = MacType.SEC_MAC_TDES_X919;
        }else if(keySys == KeySys.MKSK && keyType == KeyType.DES && macMode == MacMode.SEC_MAC_MODE_UNIONPAY_ECB){
            macType = MacType.SEC_MAC_TDES_UNIONPAY_ECB;
        }else if(keySys == KeySys.DUKPT && keyType == KeyType.DES && macMode == MacMode.SEC_MAC_MODE_LAST && dukptDerivedMode == DukptDerivedMode.SEC_DUKPT_DERIVED_MODE_BOTH){
            macType = MacType.SEC_MAC_DUKPT_LAST;
        }else if(keySys == KeySys.DUKPT && keyType == KeyType.DES && macMode == MacMode.SEC_MAC_MODE_X99 && dukptDerivedMode == DukptDerivedMode.SEC_DUKPT_DERIVED_MODE_BOTH){
            macType = MacType.SEC_MAC_DUKPT_X99;
        }else if(keySys == KeySys.DUKPT && keyType == KeyType.DES && macMode == MacMode.SEC_MAC_MODE_X919 && dukptDerivedMode == DukptDerivedMode.SEC_DUKPT_DERIVED_MODE_BOTH){
            macType = MacType.SEC_MAC_DUKPT_X919;
        }else if(keySys == KeySys.DUKPT && keyType == KeyType.DES && macMode == MacMode.SEC_MAC_MODE_UNIONPAY_ECB && dukptDerivedMode == DukptDerivedMode.SEC_DUKPT_DERIVED_MODE_BOTH){
            macType = MacType.SEC_MAC_DUKPT_UNIONPAY_ECB;
        }else if(keySys == KeySys.DUKPT && keyType == KeyType.DES && macMode == MacMode.SEC_MAC_MODE_LAST && dukptDerivedMode == DukptDerivedMode.SEC_DUKPT_DERIVED_MODE_RESP){
            macType = MacType.SEC_MAC_DUKPT_RESP_LAST;
        }else if(keySys == KeySys.DUKPT && keyType == KeyType.DES && macMode == MacMode.SEC_MAC_MODE_X99 && dukptDerivedMode == DukptDerivedMode.SEC_DUKPT_DERIVED_MODE_RESP){
            macType = MacType.SEC_MAC_DUKPT_RESP_X99;
        }else if(keySys == KeySys.DUKPT && keyType == KeyType.DES && macMode == MacMode.SEC_MAC_MODE_X919 && dukptDerivedMode == DukptDerivedMode.SEC_DUKPT_DERIVED_MODE_RESP){
            macType = MacType.SEC_MAC_DUKPT_RESP_X919;
        }else if(keySys == KeySys.DUKPT && keyType == KeyType.DES && macMode == MacMode.SEC_MAC_MODE_UNIONPAY_ECB && dukptDerivedMode == DukptDerivedMode.SEC_DUKPT_DERIVED_MODE_RESP){
            macType = MacType.SEC_MAC_DUKPT_RESP_UNIONPAY_ECB;
        }else if(keySys == KeySys.MKSK && keyType == KeyType.AES && macMode == MacMode.SEC_MAC_MODE_LAST ){
            macType = MacType.SEC_MAC_AES_LAST;
        }else if(keySys == KeySys.MKSK && keyType == KeyType.AES && macMode == MacMode.SEC_MAC_MODE_X99 ){
            macType = MacType.SEC_MAC_AES_X99;
        }else if(keySys == KeySys.DUKPT && keyType == KeyType.AES && macMode == MacMode.SEC_MAC_MODE_LAST && dukptDerivedMode == DukptDerivedMode.SEC_DUKPT_DERIVED_MODE_BOTH){
            macType = MacType.SEC_MAC_AES_DUKPT_LAST;
        }else if(keySys == KeySys.DUKPT && keyType == KeyType.AES && macMode == MacMode.SEC_MAC_MODE_X99 && dukptDerivedMode == DukptDerivedMode.SEC_DUKPT_DERIVED_MODE_BOTH){
            macType = MacType.SEC_MAC_AES_DUKPT_X99;
        }else if(keySys == KeySys.DUKPT && keyType == KeyType.AES && macMode == MacMode.SEC_MAC_MODE_X919 && dukptDerivedMode == DukptDerivedMode.SEC_DUKPT_DERIVED_MODE_BOTH){
            macType = MacType.SEC_MAC_DUKPT_X919;
        }else if(keySys == KeySys.DUKPT && keyType == KeyType.AES && macMode == MacMode.SEC_MAC_MODE_X99 && dukptDerivedMode == DukptDerivedMode.SEC_DUKPT_DERIVED_MODE_BOTH){
            macType = MacType.SEC_MAC_DUKPT_UNIONPAY_ECB;
        }else if(keySys == KeySys.MKSK && keyType == KeyType.SM4 && macMode == MacMode.SEC_MAC_MODE_LAST ){
            macType = MacType.SEC_MAC_SM4_LAST;
        }else if(keySys == KeySys.MKSK && keyType == KeyType.SM4 && macMode == MacMode.SEC_MAC_MODE_X99 ){
            macType = MacType.SEC_MAC_SM4_X99;
        }else{
            Log.d(TAG,"generateMac mactype is null");
            return null;
        }
        int ivLen = ((iv == null) ? 0 : iv.length);

        byte[] outData = new byte[256];
        int[] outDataLen = new int[1];

        byte[] ksnData = new byte[32];
        int[] ksnDataLen = new int[1];

        int ret = -1;
        if(keySys == KeySys.DUKPT || keyType == KeyType.AES){
            ST_SEC_DUKPT_DERIVATE_DATA dukptDerivateData = new ST_SEC_DUKPT_DERIVATE_DATA();
            dukptDerivateData.setDerivateKeyType(KeyType.AES.ordinal());
            dukptDerivateData.setDerivateKeyUsage(dukptDerivateUsage.ordinal());
            dukptDerivateData.setDerivateKeyLen(derivateKeyLen);
            ret = ForthJni.getInstance().NAPI_SecGenerateMAC_DerivateKey(macType.ordinal(), key.getKeyId(),iv,ivLen,datain,datain.length,dukptDerivateData,outData,outDataLen,ksnData,ksnDataLen);
        }else {
            ret = ForthJni.getInstance().NAPI_SecGenerateMAC(macType.ordinal(), key.getKeyId(),iv,ivLen,datain,datain.length,null,0,outData,outDataLen,ksnData,ksnDataLen);
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
        return new MacOutput(ret,data,ksn);
    }
}
