package com.newland.sdk.me.module.pininput;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.newland.forth.spi.common.NDKErrorCode;
import com.newland.forth.spi.crypto.cipher.CipherOutput;
import com.newland.forth.spi.crypto.cipher.MacOutput;
import com.newland.forth.spi.crypto.keystore.DUKPTKey;
import com.newland.forth.spi.crypto.keystore.KeyGenerateMethod;
import com.newland.forth.spi.crypto.keystore.KeyManagerSpi;
import com.newland.forth.spi.crypto.keystore.SymmetricKey;
import com.newland.forth.module.crypto.cipher.Cipher;
import com.newland.forth.module.crypto.cipher.MacGenerator;
import com.newland.forth.module.crypto.keystore.KeyManager;
import com.newland.forth.spi.crypto.cipher.AlgorithmParameters;
import com.newland.forth.spi.crypto.cipher.CipherSpi;
import com.newland.forth.spi.crypto.cipher.MacGeneratorSpi;
import com.newland.ndk.NdkApiManager;
import com.newland.ndk.napi.EM_SEC_ASYM_ENCODING_MODE;
import com.newland.ndk.napi.EM_SEC_CRYPTO_KEY_TYPE;
import com.newland.ndk.napi.EM_SEC_KEY_INFO_ID;
import com.newland.ndk.napi.EM_SEC_KEY_USAGE;
import com.newland.ndk.napi.EM_SEC_MD_TYPE;
import com.newland.ndk.napi.ST_SEC_ASYM_KEY_INFO;
import com.newland.ndk.napi.ST_SEC_KCV_DATA;
import com.newland.ndk.napi.ST_SEC_KEYIN_DATA;
import com.newland.sdk.module.pin.CheckValue;
import com.newland.sdk.module.pin.DukptDerivateUsage;
import com.newland.sdk.module.pin.DukptDerivedMode;
import com.newland.sdk.module.pin.InjectKeyType;
import com.newland.sdk.module.pin.KcvMode;
import com.newland.sdk.module.pin.MacType;
import com.newland.sdk.module.pin.MasterKeyType;
import com.newland.sdk.module.pin.NCalMacExtParams;
import com.newland.sdk.module.pin.NCipherExtParams;
import com.newland.sdk.module.pin.NCryptoModule;
import com.newland.sdk.module.pin.NInjectKeyParams;
import com.newland.sdk.module.pin.NLoadDuktpExtParams;
import com.newland.sdk.module.pin.NLoadMKExtParams;
import com.newland.sdk.module.pin.NLoadWKExtParams;
import com.newland.sdk.module.pin.NPinpadModule;
import com.newland.sdk.module.pin.PaddingMode;
import com.newland.sdk.module.pin.AlgorithmMode;
import com.newland.sdk.module.pin.CipherExtParams;
import com.newland.sdk.module.pin.CipherMode;
import com.newland.sdk.module.pin.CipherResult;
import com.newland.sdk.module.pin.KeyManagement;
import com.newland.sdk.module.pin.LoadDuktpExtParams;
import com.newland.sdk.module.pin.LoadKeyMode;
import com.newland.sdk.module.pin.LoadMKExtParams;
import com.newland.sdk.module.pin.LoadWKExtParams;
import com.newland.sdk.module.pin.LoadWKMode;
import com.newland.sdk.module.pin.MacResult;
import com.newland.sdk.module.pin.WorkingKeyType;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.forth.module.jni.ForthJni;
import com.newland.sdk.utils.ISOUtils;

/**
 * Author by wuhh, Date on 2020/8/10.
 */
public class MENPinpad extends MEPinpad implements NCryptoModule {
    private DeviceLogger devicelogger = DeviceLoggerFactory.getLogger("MENPinpad");

    private final boolean mIsForth = true;//是否启用NAPI
    private KeyManagerSpi mkeyManagerSpi;
    private CipherSpi mCipherSpi;
    private MacGeneratorSpi mMacGeneratorSpi;
    private CryptoMap mCryptoMap;
    private static final int NDK_ERR_OK = 0;
    private static final int CALC_ENCRYPT = 1;
    private static final int CALC_DECRYPT = 2;

    public MENPinpad(AbstractDevice device, Context context) {
        super(device, context);
        mCryptoMap = new CryptoMap();
        if(mIsForth){
            mkeyManagerSpi = new KeyManager();
            mCipherSpi = new Cipher();
            mMacGeneratorSpi = new MacGenerator();
            devicelogger.debug(">>>KeyManagerSpi="+mkeyManagerSpi+" CipherSpi="+mCipherSpi+" MacGeneratorSpi="+mMacGeneratorSpi);
        }
        devicelogger.debug(">>>IsForth="+mIsForth);

    }

    private boolean isSupportNapi(){
        if(!mIsForth){
            devicelogger.debug(">>>isSupportNapi mIsForth="+mIsForth);
            return false;
        }
        int ret = ForthJni.getInstance().isSupportNapi();
        devicelogger.debug(">>>isSupportNapi ret="+ret);
        if(ret == 1){
            return true;
        }else {
            return false;
        }
    }

    private boolean generateKey(LoadKeyMode loadKeyMode,
                                AlgorithmMode srcKeyAlg, int srcKeyIndex, KeyUsage srcKeyUsage,
                                AlgorithmMode destKeyAlg, int destKeyIndex, KeyUsage destKeyUsage,
                                byte[] keyData, byte[] ksn,
                                CheckValue checkValue, CipherMode cipherMode, byte[] iv, PaddingMode paddingMode, DukptDerivedMode dukptDerivedMode){
        if (mkeyManagerSpi == null || loadKeyMode == null) {
            return false;
        }

        if(loadKeyMode == LoadKeyMode.RANDOM || loadKeyMode == LoadKeyMode.RANDOM_OUT){
            keyData = new byte[]{};
        }

        KeyGenerateMethod method = mCryptoMap.getKeyGenerateMethod(loadKeyMode);
        AlgorithmParameters parameters = new AlgorithmParameters();

        parameters.setCipherMode(mCryptoMap.getCipherMode(cipherMode));

        int keyLen =  keyData.length;
        PaddingMode.Mode mode = null;
        if(paddingMode != null){
            mode = paddingMode.getMode();
            if(mode != null){
                keyLen = paddingMode.getKeyLen();
            }
        }
        parameters.setPaddingMode(mCryptoMap.getPaddingMode(mode));

        parameters.setDukptDerivedMode(mCryptoMap.getDukptDerivedMode(dukptDerivedMode));

        SymmetricKey srcKey = new SymmetricKey();
        SymmetricKey destKey = new SymmetricKey();
        if(destKeyUsage == KeyUsage.DUKPT){
            srcKey = new DUKPTKey();
            destKey = new DUKPTKey();
            ((DUKPTKey)destKey).setKsn(ksn);
        }
        srcKey.setKeyType(mCryptoMap.getAlgorithmMode(srcKeyAlg));
        srcKey.setKeyId(srcKeyIndex);
        srcKey.setKeyUsage(mCryptoMap.getKeyUsage(srcKeyUsage));

        destKey.setKeyType(mCryptoMap.getAlgorithmMode(destKeyAlg));
        destKey.setKeyId(destKeyIndex);
        destKey.setKeyLen(keyLen);
        destKey.setKeyUsage(mCryptoMap.getKeyUsage(destKeyUsage));
        destKey.setCipertextKeyVal(keyData);
        destKey.setPlaintextKeyVal(keyData);
        KcvMode kcvMode = null;
        byte[] kcvValue = null;
        if(checkValue !=null){
            kcvMode = checkValue.getKcvMode();
            kcvValue = checkValue.getKcvValue();
        }
        destKey.setKcv(kcvValue);
        destKey.setKcvMode(mCryptoMap.getKcvMode(kcvMode));

        int ret = mkeyManagerSpi.generateKey(method,parameters, srcKey, destKey,iv);
        if (ret != NDK_ERR_OK) {
            devicelogger.error(">>>generateKey ret="+ret);
            return false;
        }
        return true;
    }

    @Override
    public boolean loadMasterKey(LoadKeyMode loadKeyMode, AlgorithmMode algorithmMode, int masterKeyIndex,
                                 @NonNull byte[] masterKeyData, @Nullable byte[] checkValue, LoadMKExtParams loadMKExtParams) {
        devicelogger.debug("[loadMasterKey] loadKeyMode="+loadKeyMode+" algorithmMode="+algorithmMode+" masterKeyIndex="+masterKeyIndex+" loadMKExtParams="+loadMKExtParams);
        CipherMode cipherMode = null;
        if(loadMKExtParams!=null){
            cipherMode = loadMKExtParams.getCipherMode();
        }
        if(!(loadMKExtParams instanceof NLoadMKExtParams)){
            if(cipherMode!=null && cipherMode != CipherMode.ECB && cipherMode != CipherMode.CBC){
                devicelogger.debug("[loadMasterKey] cipherMode="+cipherMode+" not support.");
                return false;
            }
            if(loadKeyMode != LoadKeyMode.DEFAULT_ENCRYPT && loadKeyMode != LoadKeyMode.CUSTOM_ENCRYPT
                    && loadKeyMode != LoadKeyMode.PLAIN && loadKeyMode != LoadKeyMode.TR31){
                devicelogger.debug("[loadMasterKey] loadKeyMode="+loadKeyMode+" not support.");
                return false;
            }
            boolean result = super.loadMasterKey(loadKeyMode, algorithmMode, masterKeyIndex, masterKeyData, checkValue, loadMKExtParams);
            devicelogger.debug("[loadMasterKey] result="+result);
            return result;
        }
        NLoadMKExtParams nloadMKExtParams = ((NLoadMKExtParams)loadMKExtParams);
        byte[] iv = null;
        PaddingMode paddingMode = null;MasterKeyType masterKeyType = null;
        int kekIndex = nloadMKExtParams.getKekIndex();
        if(loadKeyMode == LoadKeyMode.PLAIN){
            kekIndex = 0;
        }
        if(nloadMKExtParams!=null){
            if(cipherMode!=null && cipherMode == CipherMode.CBC){
                iv = nloadMKExtParams.getCbcInit();
            }
            if(iv == null){
                iv = nloadMKExtParams.getInitialVector();
            }
            paddingMode = nloadMKExtParams.getPaddingMode();
            masterKeyType = nloadMKExtParams.getMasterKeyType();
        }
        if(masterKeyType == null){
            masterKeyType = MasterKeyType.MASTER;
        }
        CheckValue keyKcv = null;
        if(checkValue != null){
            int kcvLen = 4;
            if(algorithmMode == AlgorithmMode.AES){
                kcvLen = 5;
            }
            if(checkValue.length > kcvLen ){
                byte[] value = new byte[kcvLen];
                System.arraycopy(checkValue,0,value,0,value.length);
                checkValue = value;
            }
            keyKcv = new CheckValue(KcvMode.ZERO,checkValue);
        }
        if(loadKeyMode == LoadKeyMode.DEFAULT_ENCRYPT){
            devicelogger.debug("[loadMasterKey] NAPI FW not support "+loadKeyMode);
            return false;
        }
        if(isSupportNapi()){
            boolean result = generateKey(loadKeyMode,
                    algorithmMode,kekIndex,KeyUsage.MASTER,
                    algorithmMode,masterKeyIndex,mCryptoMap.getSDKKeyUsageByMasterKeyType(masterKeyType),
                    masterKeyData,null, keyKcv,cipherMode,iv, paddingMode,null);
            devicelogger.debug(">>>forth loadMasterKey result="+result+" IsForth="+mIsForth);
            return result;
        }
        devicelogger.debug("[loadMasterKey] fail.");
        return false;
    }

    @Override
    public boolean loadWorkingKey(LoadWKMode keyWorkingMode, AlgorithmMode algorithmMode, WorkingKeyType workingKeyType,
                                  int masterKeyIndex, int workingKeyIndex, @NonNull byte[] data, @Nullable byte[] checkValue, LoadWKExtParams loadWKExtParams) {

        if(!(loadWKExtParams instanceof NLoadWKExtParams)){
            if(keyWorkingMode != LoadWKMode.ENCRYPT && keyWorkingMode != LoadWKMode.PLAIN){
                devicelogger.debug("[loadWorkingKey] keyWorkingMode="+keyWorkingMode+" not support");
                return false;
            }
            if(workingKeyType != WorkingKeyType.PIN && workingKeyType != WorkingKeyType.MAC && workingKeyType != WorkingKeyType.TRACK){
                devicelogger.debug("[loadWorkingKey] workingKeyType="+workingKeyType+" not support");
                return false;
            }
            boolean result = super.loadWorkingKey(keyWorkingMode, algorithmMode, workingKeyType, masterKeyIndex, workingKeyIndex, data, checkValue, loadWKExtParams);
            devicelogger.debug("[loadWorkingKey] result="+result);
            return result;
        }

        NLoadWKExtParams nLoadWKExtParams = (NLoadWKExtParams)loadWKExtParams;
        KeyUsage srckeyUsage = KeyUsage.MASTER;
        if(nLoadWKExtParams !=null && nLoadWKExtParams.getMasterKeyType()!=null){
            srckeyUsage = mCryptoMap.getSDKKeyUsageByMasterKeyType(nLoadWKExtParams.getMasterKeyType());
        }
        KeyUsage destkeyUsage = mCryptoMap.getSDKKeyUsageByWorkingKeyType(workingKeyType);
        if(destkeyUsage==null){
            devicelogger.debug("[loadWorkingKey] destkeyUsage==null return.");
            return false;
        }
        CipherMode cipherMode = null; byte[] iv = null;PaddingMode paddingMode = null;
        if(nLoadWKExtParams!=null){
            if(cipherMode!=null && cipherMode == CipherMode.CBC){
                iv = nLoadWKExtParams.getCbcInitData();
            }
            if(iv == null){
                iv = nLoadWKExtParams.getInitialVector();
            }
            cipherMode = nLoadWKExtParams.getCipherMode();
            paddingMode = nLoadWKExtParams.getPaddingMode();
        }
        CheckValue keyKcv = null;
        if(checkValue != null){
            int kcvLen = 4;
            if(algorithmMode == AlgorithmMode.AES){
                kcvLen = 5;
            }
            if(checkValue.length > kcvLen ){
                byte[] value = new byte[kcvLen];
                System.arraycopy(checkValue,0,value,0,value.length);
                checkValue = value;
            }
            keyKcv = new CheckValue(KcvMode.ZERO,checkValue);
        }
        LoadKeyMode loadKeyMode = mCryptoMap.getLoadWKMode2LoadKeyMode(keyWorkingMode);
        if(isSupportNapi()){
            boolean result = generateKey(loadKeyMode,
                    algorithmMode,masterKeyIndex,srckeyUsage,
                    algorithmMode,workingKeyIndex,destkeyUsage,
                    data,null,keyKcv,cipherMode,iv, paddingMode,null);
            devicelogger.debug(">>>forth loadWorkingKey result="+result);
            return result;
        }
        devicelogger.debug("[loadWorkingKey] fail");
        return false;
    }

    @Override
    public boolean loadIPEK(LoadKeyMode loadKeyMode, int ipekIndex, @NonNull byte[] ksn, @NonNull
            byte[] keyData, LoadDuktpExtParams loadDuktpExtParams) {
        if(!(loadDuktpExtParams instanceof NLoadDuktpExtParams)){
            if(loadKeyMode != LoadKeyMode.DEFAULT_ENCRYPT && loadKeyMode != LoadKeyMode.CUSTOM_ENCRYPT && loadKeyMode != LoadKeyMode.PLAIN && loadKeyMode != LoadKeyMode.TR31){
                devicelogger.debug("[loadIPEK] loadKeyMode="+loadKeyMode+" not support.");
                return false;
            }
            boolean result = super.loadIPEK(loadKeyMode, ipekIndex, ksn, keyData, loadDuktpExtParams);
            devicelogger.debug("[loadIPEK] result="+result);
            return result;
        }
        NLoadDuktpExtParams nLoadDuktpExtParams = (NLoadDuktpExtParams)loadDuktpExtParams;
        DukptDerivedMode dukptDerivedMode = null;CipherMode cipherMode = null; byte[] iv = null;PaddingMode paddingMode = null;
        AlgorithmMode algorithmMode = AlgorithmMode.DES;
        int kekIndex = 0xFF;
        KeyUsage keyUsage = KeyUsage.MASTER;
        if(nLoadDuktpExtParams!=null){
            dukptDerivedMode = nLoadDuktpExtParams.getDukptDerivedMode();
            cipherMode = nLoadDuktpExtParams.getCipherMode();
            iv = nLoadDuktpExtParams.getInitialVector();
            paddingMode = nLoadDuktpExtParams.getPaddingMode();
            kekIndex = nLoadDuktpExtParams.getKekIndex();
            MasterKeyType srcMasterKeyType = nLoadDuktpExtParams.getSrcMasterKeyType();
            if(srcMasterKeyType != null){
                keyUsage = mCryptoMap.getSDKKeyUsageByMasterKeyType(srcMasterKeyType);
            }
            if(nLoadDuktpExtParams.getAlgorithmMode() != null){
                algorithmMode = nLoadDuktpExtParams.getAlgorithmMode();
            }
        }

        if(isSupportNapi()){
            boolean result = generateKey(loadKeyMode,
                    algorithmMode,kekIndex,keyUsage,
                    algorithmMode,ipekIndex,KeyUsage.DUKPT,
                    keyData,ksn,null,cipherMode,iv, paddingMode,dukptDerivedMode);
            devicelogger.debug("[loadIPEK] loadDukpt result="+result);
            return result;
        }
        devicelogger.debug("[loadIPEK] fail.");
        return false;
    }

    private CipherResult calculateDate(int mode, KeyManagement keyManagement,
                                       AlgorithmMode algorithmMode, int keyIndex, KeyUsage keyUsage,
                                       byte[] datain, NCipherExtParams cipherExtParams,CipherMode cipherMode){
        if(mCipherSpi == null){
            return null;
        }
        DukptDerivedMode dukptDerivedMode = null;byte[] iv = null;PaddingMode.Mode paddingModeMode = null;
        DukptDerivateUsage dukptDerivateUsage = null;int derivateKeyLen = -1;
        if(cipherExtParams!=null){
            dukptDerivedMode = cipherExtParams.getDukptDerivedMode();
            iv = cipherExtParams.getInitialVector();
            paddingModeMode = cipherExtParams.getPaddingMode();
            dukptDerivateUsage = cipherExtParams.getDukptDerivateUsage();
            derivateKeyLen = cipherExtParams.getDerivateKeyLen();
        }

        SymmetricKey key = new SymmetricKey();
        if(keyManagement == KeyManagement.DUKPT){
            key = new DUKPTKey();
        }
        key.setKeyType(mCryptoMap.getAlgorithmMode(algorithmMode));
        key.setKeyId(keyIndex);
        key.setKeyUsage(mCryptoMap.getKeyUsage(keyUsage));

        AlgorithmParameters parameters = new AlgorithmParameters();
        parameters.setCipherMode(mCryptoMap.getCipherMode(cipherMode));
        parameters.setPaddingMode(mCryptoMap.getPaddingMode(paddingModeMode));
        parameters.setDukptDerivedMode(mCryptoMap.getDukptDerivedMode(dukptDerivedMode));
        parameters.setDukptDerivateUsage(dukptDerivateUsage);
        parameters.setDerivateKeyLen(derivateKeyLen);
        CipherOutput cipherOutput;
        if(mode == CALC_ENCRYPT){
            cipherOutput = mCipherSpi.encrypt(key,parameters,iv,datain);
        }else {
            cipherOutput = mCipherSpi.decrypt(key,parameters,iv,datain);
        }
        if(cipherOutput == null){
            return null;
        }
        return new CipherResult(cipherOutput.getData(),(cipherOutput.getKsn()==null?null:ISOUtils.hexString(cipherOutput.getKsn())));
    }

    @Override
    public CipherResult encrypt(KeyManagement keyManagement, AlgorithmMode algorithmMode, CipherMode
            cipherMode, int keyIndex, @NonNull byte[] inputData, CipherExtParams params) {
        if(params instanceof NCipherExtParams){
            if(isSupportNapi()){
                KeyUsage keyUsage = KeyUsage.WORKINGKEY_DATA;
                if(keyManagement == KeyManagement.DUKPT){
                    keyUsage = KeyUsage.DUKPT;
                }
                CipherResult cipherResult = calculateDate(CALC_ENCRYPT,keyManagement,algorithmMode,keyIndex,
                        keyUsage,inputData,(NCipherExtParams) params,cipherMode);
                devicelogger.debug("[encrypt] calculateDate cipherResult="+cipherResult);
                return cipherResult;
            }
        }
        return super.encrypt(keyManagement, algorithmMode, cipherMode, keyIndex, inputData, params);
    }

    @Override
    public CipherResult decrypt(KeyManagement keyManagement, AlgorithmMode algorithmMode,
                                CipherMode cipherMode, int keyIndex, @NonNull byte[] inputData, CipherExtParams params) {
        if(params instanceof NCipherExtParams){
            if(isSupportNapi()){
                KeyUsage keyUsage = KeyUsage.WORKINGKEY_DATA;
                if(keyManagement == KeyManagement.DUKPT){
                    keyUsage = KeyUsage.DUKPT;
                }
                CipherResult cipherResult = calculateDate(CALC_DECRYPT,keyManagement,algorithmMode,keyIndex,
                        keyUsage,inputData,(NCipherExtParams)params,cipherMode);
                devicelogger.debug("[decrypt] calculateDate cipherResult="+cipherResult);
                return cipherResult;
            }
        }
        return super.decrypt(keyManagement, algorithmMode, cipherMode, keyIndex, inputData, params);
    }

    @Override
    public boolean ksnAESIncrease(int dukptKeyIndex) {
        ST_SEC_KEYIN_DATA keyData = new ST_SEC_KEYIN_DATA();
        ST_SEC_KCV_DATA kcvData = new ST_SEC_KCV_DATA();
        keyData.KeyType = EM_SEC_CRYPTO_KEY_TYPE.KEY_TYPE_AES.getCode();
        keyData.ucKeyIdx = dukptKeyIndex;
        keyData.KeyUsage = EM_SEC_KEY_USAGE.KEY_USE_DUKPT.getCode();
        int ret = NdkApiManager.getNdkApiManager().getSecNapi().NAPI_SecGenerateKey(
                KeyGenerateMethod.SEC_KIM_DUKPT_DERIVE.ordinal(),keyData,kcvData);
        devicelogger.debug("ksnAESIncrease index="+dukptKeyIndex+" ret="+ret);
        if(ret != 0){
            return false;
        }
        return true;
    }

    @Override
    public byte[] getDukptAESKsn(int dukptKeyIndex) {
        int[] len = new int[1];
        byte[] data = new byte[32];
        int ret = NdkApiManager.getNdkApiManager().getSecNapi().NAPI_SecGetKeyInfo(
                EM_SEC_KEY_INFO_ID.SEC_KEY_INFO_KSN.ordinal(),
                dukptKeyIndex,EM_SEC_CRYPTO_KEY_TYPE.KEY_TYPE_AES.getCode(),
                EM_SEC_KEY_USAGE.KEY_USE_DUKPT.getCode(),null,0,data,len);
        devicelogger.debug("getDukptAESKsn ret="+ret);
        if(ret != 0) {
            return null;
        }
        byte[] ksn = new byte[len[0]];
        System.arraycopy(data, 0, ksn, 0, ksn.length);
        devicelogger.debug("getDukptAESKsn ksn="+ISOUtils.hexString(ksn));
        return ksn;
    }


    @Override
    public MacResult calculateMac(MacType macType, int keyIndex, byte[] inputData, @Nullable NCalMacExtParams calMacExtParams) {
        if(macType == null){
            return null;
        }
        KeyManagement keyManagement = null;AlgorithmMode algorithmMode = null; CryptoMap.MacAlgMode macMode = null;
        if(macType == MacType.MKSK_DES_9606 || macType == MacType.MKSK_DES_X99 || macType == MacType.MKSK_DES_X919 || macType == MacType.MKSK_DES_UNIONPAY_ECB){
            keyManagement = KeyManagement.MKSK;
            algorithmMode = AlgorithmMode.DES;
            if(macType == MacType.MKSK_DES_9606){
                macMode = CryptoMap.MacAlgMode.LAST;
            } else if(macType == MacType.MKSK_DES_X99){
                macMode = CryptoMap.MacAlgMode.X99;
            } else if(macType == MacType.MKSK_DES_X919){
                macMode = CryptoMap.MacAlgMode.X919;
            } else if(macType == MacType.MKSK_DES_UNIONPAY_ECB){
                macMode = CryptoMap.MacAlgMode.UNIONPAY_ECB;
            }
        }else if(macType == MacType.MKSK_SM4_9606 || macType == MacType.MKSK_SM4_X99 || macType == MacType.MKSK_SM4_UNIONPAY){
            keyManagement = KeyManagement.MKSK;
            algorithmMode = AlgorithmMode.SM4;
            if(macType == MacType.MKSK_SM4_9606){
                macMode = CryptoMap.MacAlgMode.LAST;
            } else if(macType == MacType.MKSK_SM4_X99){
                macMode = CryptoMap.MacAlgMode.X99;
            }else if(macType == MacType.MKSK_SM4_UNIONPAY){
                macMode = CryptoMap.MacAlgMode.SM4_UNIONPAY;
            }
        }else if(macType == MacType.MKSK_AES_9606 || macType == MacType.MKSK_AES_X99){
            keyManagement = KeyManagement.MKSK;
            algorithmMode = AlgorithmMode.AES;
            if(macType == MacType.MKSK_AES_9606){
                macMode = CryptoMap.MacAlgMode.LAST;
            } else if(macType == MacType.MKSK_AES_X99){
                macMode = CryptoMap.MacAlgMode.X99;
            }
        }else if(macType == MacType.DUKPT_DES_9606 || macType == MacType.DUKPT_DES_X99 || macType == MacType.DUKPT_DES_X919 || macType == MacType.DUKPT_DES_UNIONPAY_ECB){
            keyManagement = KeyManagement.DUKPT;
            algorithmMode = AlgorithmMode.DES;
            if(macType == MacType.DUKPT_DES_9606){
                macMode = CryptoMap.MacAlgMode.LAST;
            } else if(macType == MacType.DUKPT_DES_X99){
                macMode = CryptoMap.MacAlgMode.X99;
            } else if(macType == MacType.DUKPT_DES_X919){
                macMode = CryptoMap.MacAlgMode.X919;
            } else if(macType == MacType.DUKPT_DES_UNIONPAY_ECB){
                macMode = CryptoMap.MacAlgMode.UNIONPAY_ECB;
            }
        }else if(macType == MacType.DUKPT_DES_RESP_9606| macType == MacType.DUKPT_DES_RESP_X99 || macType == MacType.DUKPT_DES_RESP_X919 || macType == MacType.DUKPT_DES_RESP_UNIONPAY_ECB){
            keyManagement = KeyManagement.DUKPT;
            algorithmMode = AlgorithmMode.DES;
            if(macType == MacType.DUKPT_DES_RESP_9606){
                macMode = CryptoMap.MacAlgMode.LAST;
            } else if(macType == MacType.DUKPT_DES_RESP_X99){
                macMode = CryptoMap.MacAlgMode.X99;
            } else if(macType == MacType.DUKPT_DES_RESP_X919){
                macMode = CryptoMap.MacAlgMode.X919;
            } else if(macType == MacType.DUKPT_DES_RESP_UNIONPAY_ECB){
                macMode = CryptoMap.MacAlgMode.UNIONPAY_ECB;
            }
        }else if(macType == MacType.DUKPT_AES_9606|| macType == MacType.DUKPT_AES_X99){
            keyManagement = KeyManagement.DUKPT;
            algorithmMode = AlgorithmMode.AES;
            if(macType == MacType.DUKPT_AES_9606){
                macMode = CryptoMap.MacAlgMode.LAST;
            } else if(macType == MacType.DUKPT_AES_X99){
                macMode = CryptoMap.MacAlgMode.X99;
            }
        }else if(macType == MacType.DUKPT_AES_X919|| macType == MacType.DUKPT_AES_UNIONPAY_ECB){
            keyManagement = KeyManagement.DUKPT;
            algorithmMode = AlgorithmMode.AES;
            if(macType == MacType.DUKPT_AES_X919){
                macMode = CryptoMap.MacAlgMode.X919;
            } else if(macType == MacType.DUKPT_AES_UNIONPAY_ECB){
                macMode = CryptoMap.MacAlgMode.UNIONPAY_ECB;
            }
        }else if(macType == MacType.HMAC_SHA1 || macType == MacType.HMAC_SHA256){
            byte[] outData = new byte[256];
            int[] outDataLen = new int[1];

            byte[] ksnData = new byte[32];
            int[] ksnDataLen = new int[1];
            int type = 0;
            if(macType == MacType.HMAC_SHA1){
                type = 21;
            }else {
                type = 22;
            }
            int ret = ForthJni.getInstance().NAPI_SecGenerateMAC(type,keyIndex, null,0,inputData,inputData.length,null,0,outData,outDataLen,ksnData,ksnDataLen);
            devicelogger.debug("calculateMac: ret="+ret);
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
                return new MacResult(data,null);
            }else {
                return null;
            }
        }else {
            devicelogger.debug(">>>macType="+macType);
            return null;
        }

        DukptDerivedMode dukptDerivedMode = null;byte[] iv = null;
        DukptDerivateUsage dukptDerivateUsage = null;
        int derivateKeyLen = 0;
        if(calMacExtParams!=null){
            dukptDerivedMode = calMacExtParams.getDukptDerivedMode();
            iv = calMacExtParams.getInitialVector();
            dukptDerivateUsage = calMacExtParams.getDukptDerivateUsage();
            derivateKeyLen = calMacExtParams.getDerivateKeyLen();
        }
        SymmetricKey key = new SymmetricKey();
        if(keyManagement == KeyManagement.DUKPT){
            key = new DUKPTKey();
        }
        key.setKeyType(mCryptoMap.getAlgorithmMode(algorithmMode));
        key.setKeyId(keyIndex);
        key.setKeyUsage(mCryptoMap.getKeyUsage(KeyUsage.WORKINGKEY_MAC));
        if(keyManagement == KeyManagement.DUKPT){
            key.setKeyUsage(mCryptoMap.getKeyUsage(KeyUsage.DUKPT));
        }

        AlgorithmParameters parameters = new AlgorithmParameters();
        parameters.setMacMode(mCryptoMap.getMacMode(macMode));
        parameters.setDukptDerivedMode(mCryptoMap.getDukptDerivedMode(dukptDerivedMode));
        parameters.setDukptDerivateUsage(dukptDerivateUsage);
        parameters.setDerivateKeyLen(derivateKeyLen);
        if(isSupportNapi()){
            MacOutput macOutput = null;
            if(mMacGeneratorSpi != null){
                macOutput = mMacGeneratorSpi.generateMac(key,parameters,iv,inputData);
            }
            devicelogger.debug("[calculateMac] MacOutput="+macOutput);
            MacResult macResult = null;
            if(macOutput != null){
                if(macOutput.getRet() == NDK_ERR_OK){
                    macResult = new MacResult(macOutput.getData(),macOutput.getKsn());
                }
            }
            return macResult;
        }
        devicelogger.debug("[calculateMac] MacOutput fail.");
        return null;
    }

    @Override
    public boolean injectKey(LoadKeyMode loadKeyMode, AlgorithmMode srcKeyAlg, int srcKeyIndex, InjectKeyType srcKeyType, AlgorithmMode destKeyAlg, int destKeyIndex, InjectKeyType destKeyType, byte[] keyData, NInjectKeyParams params) {
        if(params == null){
            params = new NInjectKeyParams();
        }
        return generateKey(loadKeyMode, srcKeyAlg,srcKeyIndex,mCryptoMap.getKeyUsage(srcKeyType), destKeyAlg,  destKeyIndex,mCryptoMap.getKeyUsage(destKeyType), keyData,
                params.getKsn(), params.getCheckValue(), params.getCipherMode(), params.getIv(),params.getPaddingMode(),params.getDukptDerivedMode());
    }

    private String hexString(byte[] data){
        return (data==null?"null": ISOUtils.hexString(data));
    }

    @Override
    public boolean getKeyInfo(int infoID, int keyId, int keyType, int keyUsage, byte[] pAD, int adSize, byte[] outInfo, int[] outInfoLen) {
        int ret = NdkApiManager.getNdkApiManager().getSecNapi().NAPI_SecGetKeyInfo(infoID, keyId, keyType, keyUsage, pAD, adSize, outInfo, outInfoLen);
        if(ret != 0){
            devicelogger.debug("getKeyInfo ret="+ret);
        }
        return (ret == 0);
    }

    @Override
    public boolean asymSign(ST_SEC_ASYM_KEY_INFO pstKeyinfo, EM_SEC_MD_TYPE MdAlg, EM_SEC_ASYM_ENCODING_MODE EncodingMode, int nHashLen, byte[] psHash, int[] nSigLen, byte[] psSig) {
        int ret = NdkApiManager.getNdkApiManager().getSecNapi().NAPI_SecAsymSign(pstKeyinfo, MdAlg, EncodingMode, nHashLen, psHash, nSigLen, psSig);
        if(ret != 0){
            devicelogger.debug("asymSign ret="+ret);
        }
        return (ret == 0);
    }

    @Override
    public boolean resetCertStatus() {
        int ret = NdkApiManager.getNdkApiManager().getSecNapi().NAPI_SecResetCertStatus();
        if(ret != 0){
            devicelogger.debug("resetCertStatus ret="+ret);
        }
        return (ret == 0);
    }

    @Override
    public boolean loadTrustedCert(char isCA, byte[] cert, int certlen, byte[] pubkey, int[] pubkeylen) {
        int ret = NdkApiManager.getNdkApiManager().getSecNapi().NAPI_SecLoadTrustedCert(isCA, cert, certlen, pubkey, pubkeylen);
        if(ret != 0){
            devicelogger.debug("loadTrustedCert ret="+ret);
        }
        return (ret == 0);
    }
}
