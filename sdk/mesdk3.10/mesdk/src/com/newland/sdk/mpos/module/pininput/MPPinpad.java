package com.newland.sdk.mpos.module.pininput;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.newland.rkl.RKLListener;
import com.newland.sdk.me.module.externalPininput.MposComm;
import com.newland.sdk.me.module.externalPininput.ReMEExternalPininput;
import com.newland.sdk.me.module.pininput.CryptoMap;
import com.newland.sdk.me.module.pininput.KeyUsage;
import com.newland.sdk.module.externalPin.ExtPinpadModule;
import com.newland.sdk.module.externalPin.PinpadExtParams;
import com.newland.sdk.module.pin.AccountInputType;
import com.newland.sdk.module.pin.AlgorithmMode;
import com.newland.sdk.module.pin.CalMacExtParams;
import com.newland.sdk.module.pin.CipherExtParams;
import com.newland.sdk.module.pin.CipherMode;
import com.newland.sdk.module.pin.CipherResult;
import com.newland.sdk.module.pin.DukptDerivedMode;
import com.newland.sdk.module.pin.InjectKeyType;
import com.newland.sdk.module.pin.KeyManagement;
import com.newland.sdk.module.pin.KeyType;
import com.newland.sdk.module.pin.KeyboardRandom;
import com.newland.sdk.module.pin.LoadDuktpExtParams;
import com.newland.sdk.module.pin.LoadKeyMode;
import com.newland.sdk.module.pin.LoadMKExtParams;
import com.newland.sdk.module.pin.LoadWKExtParams;
import com.newland.sdk.module.pin.LoadWKMode;
import com.newland.sdk.module.pin.MacAlgorithm;
import com.newland.sdk.module.pin.MacResult;
import com.newland.sdk.module.pin.MacType;
import com.newland.sdk.module.pin.NCalMacExtParams;
import com.newland.sdk.module.pin.NCipherExtParams;
import com.newland.sdk.module.pin.NInjectKeyParams;
import com.newland.sdk.module.pin.NLoadDuktpExtParams;
import com.newland.sdk.module.pin.NLoadMKExtParams;
import com.newland.sdk.module.pin.NLoadWKExtParams;
import com.newland.sdk.module.pin.NPinpadModule;
import com.newland.sdk.module.pin.PaddingMode;
import com.newland.sdk.module.pin.PinBlockMode;
import com.newland.sdk.module.pin.PinInputExtParams;
import com.newland.sdk.module.pin.PinInputListener;
import com.newland.sdk.module.pin.PinPadButton;
import com.newland.sdk.module.pin.RKLParams;
import com.newland.sdk.module.pin.TusnData;
import com.newland.sdk.module.pin.WorkingKeyType;
import com.newland.sdk.mtype.Device;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.mtype.common.ErrorCode;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.utils.ISOUtils;

import java.util.Arrays;
import java.util.Map;

/**
 * @Description
 * @Author wuhh
 * @Date 2021/3/8
 */
public class MPPinpad implements NPinpadModule{

    private DeviceLogger devicelogger = DeviceLoggerFactory.getLogger("MPPinpad");
    private MNAPIHelper mMNAPIHelper;
    private CryptoMap mCryptoMap;
    private MposComm mMposComm;
    private ExtPinpadModule mExternalPininput;
    private static final int OFFSET = 8;//数据部分的偏移值.

    public MPPinpad(AbstractDevice device, Context context) {
        mMNAPIHelper = new MNAPIHelper();
        mCryptoMap = new CryptoMap();
        mMposComm = new MposComm(device);
        mExternalPininput  = new ReMEExternalPininput(device,context);
    }

    @Override
    public void startRKL(RKLParams params, RKLListener listener) {
        devicelogger.error("mpos startRKL failed");
    }

    @Override
    public boolean loadMasterKey(LoadKeyMode loadKeyMode, AlgorithmMode algorithmMode, int masterKeyIndex,
                                 @NonNull byte[] masterKeyData, @Nullable byte[] checkValue, LoadMKExtParams loadMKExtParams) {
        try {
            devicelogger.debug("[loadMasterKey] loadKeyMode=" + loadKeyMode + " algorithmMode=" + algorithmMode + " masterKeyIndex=" + masterKeyIndex +
                    " masterKeyData=" + hexString(masterKeyData) + " checkValue=" + hexString(checkValue));
            NLoadMKExtParams nLoadMKExtParams = null;
            if (loadMKExtParams instanceof NLoadMKExtParams) {
                nLoadMKExtParams = (NLoadMKExtParams) loadMKExtParams;
            }
            if (loadMKExtParams != null) {
                devicelogger.debug("[loadMasterKey] loadMKExtParams getKekIndex=" + loadMKExtParams.getKekIndex() + " getCipherMode=" + loadMKExtParams.getCipherMode() + " getCbcInit=" + hexString(loadMKExtParams.getCbcInit()));
            }
            if (nLoadMKExtParams != null) {
                devicelogger.debug("[loadMasterKey] nLoadMKExtParams getPaddingMode=" + nLoadMKExtParams.getPaddingMode() + " getMasterKeyType=" + nLoadMKExtParams.getMasterKeyType() + " getInitialVector=" + hexString(nLoadMKExtParams.getInitialVector()));
            }
            int kekIndex = -1;
            if (loadMKExtParams != null) {
                kekIndex = loadMKExtParams.getKekIndex();
                if (loadKeyMode == LoadKeyMode.PLAIN) {
                    kekIndex = 0;
                }
            }
            AlgorithmMode kekType = algorithmMode;
            KeyUsage kekUsage = KeyUsage.MASTER;


            int keyIndex = masterKeyIndex;
            AlgorithmMode keyType = kekType;
            KeyUsage keyUsage = kekUsage;
            if (nLoadMKExtParams != null && nLoadMKExtParams.getMasterKeyType() != null) {
                keyUsage = mCryptoMap.getSDKKeyUsageByMasterKeyType(nLoadMKExtParams.getMasterKeyType());
            }

            CipherMode cipherMode = CipherMode.ECB;
            if (loadMKExtParams != null && loadMKExtParams.getCipherMode() != null) {
                cipherMode = loadMKExtParams.getCipherMode();
            }

            PaddingMode.Mode paddingMode = PaddingMode.Mode.NONE;
            if (nLoadMKExtParams != null && nLoadMKExtParams.getPaddingMode() != null && nLoadMKExtParams.getPaddingMode().getMode() != null) {
                paddingMode = nLoadMKExtParams.getPaddingMode().getMode();
            }

            int keyDataLen = masterKeyData.length;
            int keyLen = masterKeyData.length;
            if (nLoadMKExtParams != null && nLoadMKExtParams.getPaddingMode() != null) {
                keyLen = nLoadMKExtParams.getPaddingMode().getKeyLen();
            }
            byte[] keyData = masterKeyData;
            int ivLen = 0;
            byte[] ivData = null;
            if (cipherMode == CipherMode.CBC) {
                ivData = loadMKExtParams.getCbcInit();
            }
            if (ivData == null && nLoadMKExtParams != null) {
                ivData = nLoadMKExtParams.getInitialVector();
            }
            if (ivData != null) {
                ivLen = ivData.length;
            }

            int ksnLen = 0;
            byte[] ksnData = null;

            int kcvLen = 0;
            byte[] kcvData = null;
            if (checkValue != null) {
                int checkVlen = 3;
                if (algorithmMode == AlgorithmMode.AES) {
                    checkVlen = 5;
                }
                if (checkValue.length > checkVlen) {
                    byte[] value = new byte[checkVlen];
                    System.arraycopy(checkValue, 0, value, 0, value.length);
                    kcvLen = value.length;
                    kcvData = value;
                } else {
                    kcvLen = checkValue.length;
                    kcvData = checkValue;
                }
            }

            int adLen = 0;
            byte[] adData = null;

            devicelogger.debug("[loadMasterKey] kekIndex=" + kekIndex + " kekType=" + kekType + " kekUsage=" + kekUsage + " keyIndex=" + keyIndex + " keyType=" + keyType + " keyUsage=" + keyUsage
                    + " cipherMode=" + cipherMode + " paddingMode=" + paddingMode + " keyLen=" + keyLen + " keyDataLen=" + keyDataLen + " keyData=" + hexString(keyData)
                    + " ivLen=" + ivLen + " ivData=" + hexString(ivData) + " ksnLen=" + ksnLen + " ksnData=" + hexString(ksnData) + " kcvLen=" + kcvLen + " kcvData=" + hexString(kcvData)
                    + " adLen=" + adLen + " adData=" + hexString(adData));

            //Method  1  + KEK Index  1  + KEK Type  1 + KEK Usage  1 + Key Index  1 + Key Type  1  + Key Usage  1 +
            //Cipher Mode  1  + Padding Mode  1 + Key Length  2 + Key Data Length 2 + Key Data  var +
            //IV Length  1  + Initial value  var + KSN Length  1 + KSN  var + KCV length  1 + KCV Data  var +
            //AD size  2 + AD data  var

            String body = mMNAPIHelper.getMethod(loadKeyMode) + String.format("%02x", kekIndex) + mMNAPIHelper.getKeyType(kekType) + mMNAPIHelper.getKeyUsage(kekUsage) + String.format("%02x", keyIndex) + mMNAPIHelper.getKeyType(keyType) + mMNAPIHelper.getKeyUsage(keyUsage) +
                    mMNAPIHelper.getCipherMode(cipherMode) + mMNAPIHelper.getPaddingMode(paddingMode) + String.format("%04x", keyLen) + String.format("%04x", keyDataLen) + mMNAPIHelper.getData(keyData) +
                    String.format("%02x", ivLen) + mMNAPIHelper.getData(ivData) + String.format("%02x", ksnLen) + mMNAPIHelper.getData(ksnData) + String.format("%02x", kcvLen) + mMNAPIHelper.getData(kcvData) +
                    String.format("%04x", adLen) + mMNAPIHelper.getData(adData);

            boolean result = getResultCode(communication(mMNAPIHelper.pack("C0".getBytes(), body)));

            devicelogger.debug("[loadMasterKey] result=" + result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean loadWorkingKey(LoadWKMode keyWorkingMode, AlgorithmMode algorithmMode, WorkingKeyType workingKeyType, int masterKeyIndex, int workingKeyIndex, @NonNull byte[] data, @Nullable byte[] kcv, LoadWKExtParams loadWKExtParams) {
        try {
            devicelogger.debug("[loadWorkingKey] keyWorkingMode=" + keyWorkingMode + " algorithmMode=" + algorithmMode + " workingKeyType=" + workingKeyType +
                    " masterKeyIndex=" + masterKeyIndex + " workingKeyIndex=" + workingKeyIndex + " data=" + hexString(data) + " kcv=" + hexString(kcv));
            NLoadWKExtParams nLoadWKExtParams = null;
            if (nLoadWKExtParams instanceof NLoadWKExtParams) {
                nLoadWKExtParams = (NLoadWKExtParams) loadWKExtParams;
            }
            if (loadWKExtParams != null) {
                devicelogger.debug("[loadWorkingKey] loadWKExtParams getCbcInit=" + hexString(loadWKExtParams.getCbcInitData()));
            }
            if (nLoadWKExtParams != null) {
                devicelogger.debug("[loadWorkingKey] loadWKExtParams getPaddingMode=" + nLoadWKExtParams.getPaddingMode() + " getMasterKeyType=" + nLoadWKExtParams.getMasterKeyType() + " getInitialVector=" + hexString(nLoadWKExtParams.getInitialVector()) + " getCipherMode=" + nLoadWKExtParams.getCipherMode());
            }
            int kekIndex = masterKeyIndex;
            AlgorithmMode kekType = algorithmMode;
            KeyUsage kekUsage = KeyUsage.MASTER;
            if (nLoadWKExtParams != null && nLoadWKExtParams.getMasterKeyType() != null) {
                kekUsage = mCryptoMap.getSDKKeyUsageByMasterKeyType(nLoadWKExtParams.getMasterKeyType());
            }

            int keyIndex = workingKeyIndex;
            AlgorithmMode keyType = algorithmMode;
            KeyUsage keyUsage = mCryptoMap.getSDKKeyUsageByWorkingKeyType(workingKeyType);
            if (keyUsage == null) {
                devicelogger.debug("[loadWorkingKey] destkeyUsage==null return.");
                return false;
            }

            CipherMode cipherMode = CipherMode.ECB;
            if (nLoadWKExtParams != null && nLoadWKExtParams.getCipherMode() != null) {
                cipherMode = nLoadWKExtParams.getCipherMode();
            }

            PaddingMode.Mode paddingMode = PaddingMode.Mode.NONE;
            if (nLoadWKExtParams != null && nLoadWKExtParams.getPaddingMode() != null && nLoadWKExtParams.getPaddingMode().getMode() != null) {
                paddingMode = nLoadWKExtParams.getPaddingMode().getMode();
            }
            int keyDataLen = data.length;
            int keyLen = data.length;
            if (nLoadWKExtParams != null && nLoadWKExtParams.getPaddingMode() != null) {
                keyLen = nLoadWKExtParams.getPaddingMode().getKeyLen();
            }
            byte[] keyData = data;
            int ivLen = 0;
            byte[] ivData = null;
            if (cipherMode == CipherMode.CBC) {
                ivData = loadWKExtParams.getCbcInitData();
            }
            if (ivData == null && nLoadWKExtParams != null) {
                ivData = nLoadWKExtParams.getInitialVector();
            }
            if (ivData != null) {
                ivLen = ivData.length;
            }

            int ksnLen = 0;
            byte[] ksnData = null;

            int kcvLen = 0;
            byte[] kcvData = null;
            if (kcv != null) {
                int checkVlen = 3;
                if (algorithmMode == AlgorithmMode.AES) {
                    checkVlen = 5;
                }
                if (kcv.length > checkVlen) {
                    byte[] value = new byte[checkVlen];
                    System.arraycopy(kcv, 0, value, 0, value.length);
                    kcvLen = value.length;
                    kcvData = value;
                } else {
                    kcvLen = kcv.length;
                    kcvData = kcv;
                }
            }

            int adLen = 0;
            byte[] adData = null;
            devicelogger.debug("[loadWorkingKey] kekIndex=" + kekIndex + " kekType=" + kekType + " kekUsage=" + kekUsage + " keyIndex=" + keyIndex + " keyType=" + keyType + " keyUsage=" + keyUsage
                    + " cipherMode=" + cipherMode + " paddingMode=" + paddingMode + " keyLen=" + keyLen + " keyDataLen=" + keyDataLen + " keyData=" + hexString(keyData)
                    + " ivLen=" + ivLen + " ivData=" + hexString(ivData) + " ksnLen=" + ksnLen + " ksnData=" + hexString(ksnData) + " kcvLen=" + kcvLen + " kcvData=" + hexString(kcvData)
                    + " adLen=" + adLen + " adData=" + hexString(adData));

            //Method  1  + KEK Index  1  + KEK Type  1 + KEK Usage  1 + Key Index  1 + Key Type  1  + Key Usage  1 +
            //Cipher Mode  1  + Padding Mode  1 + Key Length  2 + Key Data Length 2 + Key Data  var +
            //IV Length  1  + Initial value  var + KSN Length  1 + KSN  var + KCV length  1 + KCV Data  var +
            //AD size  2 + AD data  var

            String body = mMNAPIHelper.getWKMethod(keyWorkingMode) + String.format("%02x", kekIndex) + mMNAPIHelper.getKeyType(kekType) + mMNAPIHelper.getKeyUsage(kekUsage) + String.format("%02x", keyIndex) + mMNAPIHelper.getKeyType(keyType) + mMNAPIHelper.getKeyUsage(keyUsage) +
                    mMNAPIHelper.getCipherMode(cipherMode) + mMNAPIHelper.getPaddingMode(paddingMode) + String.format("%04x", keyLen) + String.format("%04x", keyDataLen) + mMNAPIHelper.getData(keyData) +
                    String.format("%02x", ivLen) + mMNAPIHelper.getData(ivData) + String.format("%02x", ksnLen) + mMNAPIHelper.getData(ksnData) + String.format("%02x", kcvLen) + mMNAPIHelper.getData(kcvData) +
                    String.format("%04x", adLen) + mMNAPIHelper.getData(adData);

            boolean result = getResultCode(communication(mMNAPIHelper.pack("C0".getBytes(), body)));
            devicelogger.debug("[loadWorkingKey] result=" + result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean loadIPEK(LoadKeyMode loadKeyMode, int ipekIndex, @NonNull byte[] ksn, @NonNull byte[] encryptedIPEK, LoadDuktpExtParams loadDuktpExtParams) {
        try {
            devicelogger.debug("[loadIPEK] loadKeyMode=" + loadKeyMode + " ipekIndex=" + ipekIndex +
                    " ksn=" + hexString(ksn) + " encryptedIPEK=" + hexString(encryptedIPEK));
            NLoadDuktpExtParams nLoadDuktpExtParams = null;
            if (nLoadDuktpExtParams instanceof NLoadDuktpExtParams) {
                nLoadDuktpExtParams = (NLoadDuktpExtParams) loadDuktpExtParams;
            }
            if (loadDuktpExtParams != null) {
                devicelogger.debug("[loadIPEK] loadDuktpExtParams getKekIndex=" + loadDuktpExtParams.getKekIndex());
            }
            if (nLoadDuktpExtParams != null) {
                devicelogger.debug("[loadIPEK] nLoadDuktpExtParams getPaddingMode=" + nLoadDuktpExtParams.getPaddingMode() + " getDukptDerivedMode=" + nLoadDuktpExtParams.getDukptDerivedMode() + " getInitialVector=" + hexString(nLoadDuktpExtParams.getInitialVector()) + " getCipherMode=" + nLoadDuktpExtParams.getCipherMode());
            }
            int kekIndex = -1;
            if (loadDuktpExtParams == null) {
                kekIndex = loadDuktpExtParams.getKekIndex();
            }
            AlgorithmMode kekType = AlgorithmMode.DES;
            KeyUsage kekUsage = KeyUsage.MASTER;


            int keyIndex = ipekIndex;
            AlgorithmMode keyType = AlgorithmMode.DES;
            KeyUsage keyUsage = KeyUsage.DUKPT;

            CipherMode cipherMode = CipherMode.ECB;
            if (nLoadDuktpExtParams != null && nLoadDuktpExtParams.getCipherMode() != null) {
                cipherMode = nLoadDuktpExtParams.getCipherMode();
            }

            PaddingMode.Mode paddingMode = PaddingMode.Mode.NONE;
            if (nLoadDuktpExtParams != null && nLoadDuktpExtParams.getPaddingMode() != null && nLoadDuktpExtParams.getPaddingMode().getMode() != null) {
                paddingMode = nLoadDuktpExtParams.getPaddingMode().getMode();
            }
            int keyDataLen = encryptedIPEK.length;
            int keyLen = encryptedIPEK.length;
            if (nLoadDuktpExtParams != null && nLoadDuktpExtParams.getPaddingMode() != null ) {
                keyLen = nLoadDuktpExtParams.getPaddingMode().getKeyLen();
            }
            byte[] keyData = encryptedIPEK;
            int ivLen = 0;
            byte[] ivData = null;
            if (nLoadDuktpExtParams != null) {
                ivData = nLoadDuktpExtParams.getInitialVector();
            }
            if (ivData != null) {
                ivLen = ivData.length;
            }

            int ksnLen = 0;
            byte[] ksnData = null;
            if (ksn != null) {
                ksnData = ksn;
                ksnLen = ksnData.length;
            }

            int kcvLen = 0;
            byte[] kcvData = null;

            int adLen = 0;
            byte[] adData = null;
            devicelogger.debug("[loadWorkingKey] kekIndex=" + kekIndex + " kekType=" + kekType + " kekUsage=" + kekUsage + " keyIndex=" + keyIndex + " keyType=" + keyType + " keyUsage=" + keyUsage
                    + " cipherMode=" + cipherMode + " paddingMode=" + paddingMode + " keyLen=" + keyLen + " keyDataLen=" + keyDataLen + " keyData=" + hexString(keyData)
                    + " ivLen=" + ivLen + " ivData=" + hexString(ivData) + " ksnLen=" + ksnLen + " ksnData=" + hexString(ksnData) + " kcvLen=" + kcvLen + " kcvData=" + hexString(kcvData)
                    + " adLen=" + adLen + " adData=" + hexString(adData));

            //Method  1  + KEK Index  1  + KEK Type  1 + KEK Usage  1 + Key Index  1 + Key Type  1  + Key Usage  1 +
            //Cipher Mode  1  + Padding Mode  1 + Key Length  2 + Key Data Length 2 + Key Data  var +
            //IV Length  1  + Initial value  var + KSN Length  1 + KSN  var + KCV length  1 + KCV Data  var +
            //AD size  2 + AD data  var

            String body = mMNAPIHelper.getMethod(loadKeyMode) + String.format("%02x", kekIndex) + mMNAPIHelper.getKeyType(kekType) + mMNAPIHelper.getKeyUsage(kekUsage) + String.format("%02x", keyIndex) + mMNAPIHelper.getKeyType(keyType) + mMNAPIHelper.getKeyUsage(keyUsage) +
                    mMNAPIHelper.getCipherMode(cipherMode) + mMNAPIHelper.getPaddingMode(paddingMode) + String.format("%04x", keyLen) + String.format("%04x", keyDataLen) + mMNAPIHelper.getData(keyData) +
                    String.format("%02x", ivLen) + mMNAPIHelper.getData(ivData) + String.format("%02x", ksnLen) + mMNAPIHelper.getData(ksnData) + String.format("%02x", kcvLen) + mMNAPIHelper.getData(kcvData) +
                    String.format("%04x", adLen) + mMNAPIHelper.getData(adData);

            boolean result = getResultCode(communication(mMNAPIHelper.pack("C0".getBytes(), body)));
            devicelogger.debug("[loadWorkingKey] result=" + result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private CipherResult calculateDate(int mode, KeyManagement keyManagement, AlgorithmMode algorithmMode, CipherMode cipherMode, int keyIndex, byte[] inputData, CipherExtParams cipherExtParams) {
        try {
            devicelogger.debug("[calculateDate] mode=" + (mode == MNAPIHelper.CALC_ENCRYPT ? "encrypt" : "decrypt") + " keyManagement=" + keyManagement + " algorithmMode=" + algorithmMode + " cipherMode=" + cipherMode + " keyIndex=" + keyIndex + " inputData=" + hexString(inputData));
            NCipherExtParams nCipherExtParams = null;
            if (cipherExtParams instanceof NCipherExtParams) {
                nCipherExtParams = (NCipherExtParams) cipherExtParams;
            }
            if (cipherExtParams != null) {
                devicelogger.debug("[calculateDate] getCbcInit=" + hexString(cipherExtParams.getCbcInit()) + " getWorkingKeyData=" + hexString(cipherExtParams.getWorkingKeyData()));
            }
            if (nCipherExtParams != null) {
                devicelogger.debug("[calculateDate] getInitialVector=" + hexString(nCipherExtParams.getInitialVector()) + " getDukptDerivedMode=" + nCipherExtParams.getDukptDerivedMode() + " getPaddingMode=" + nCipherExtParams.getPaddingMode());
            }

            DukptDerivedMode dukptDerivedMode = DukptDerivedMode.BOTH;
            if (nCipherExtParams != null) {
                dukptDerivedMode = nCipherExtParams.getDukptDerivedMode();
            }
            PaddingMode.Mode paddingMode = PaddingMode.Mode.NONE;
            int ivLen = 0;
            byte[] ivData = null;
            if (cipherMode == CipherMode.CBC && cipherExtParams != null) {
                ivData = cipherExtParams.getCbcInit();
            }
            if (ivData == null && nCipherExtParams != null) {
                ivData = nCipherExtParams.getInitialVector();
            }
            if (ivData != null) {
                ivLen = ivData.length;
            }
            int dataLen = inputData.length;
            int adLen = 0;
            byte[] adData = null;

            //Mode 1 + Key Index 1 + Cipher Type 1 + Key Usage 1 + Padding Mode 1
            //IV len 1 + Initial value var(only present for CBC Cipher Type) + Data Length	2 + Data var + AD size 2 + AD data var

            String body = mMNAPIHelper.getMode(mode) + String.format("%02x", keyIndex) + mMNAPIHelper.getCipherType(keyManagement, algorithmMode, cipherMode, dukptDerivedMode) + mMNAPIHelper.getKeyUsage(keyManagement, KeyUsage.WORKINGKEY_DATA) + mMNAPIHelper.getPaddingMode(paddingMode) +
                    String.format("%02x", ivLen) + mMNAPIHelper.getData(ivData) + String.format("%04x", dataLen) + mMNAPIHelper.getData(inputData) + String.format("%04x", adLen) + mMNAPIHelper.getData(adData);
            byte[] respData = communication(mMNAPIHelper.pack("C4".getBytes(), body));
            boolean result = getResultCode(respData);
            if (result == false) {
                devicelogger.error("[calculateDate] mode=" + (mode == MNAPIHelper.CALC_ENCRYPT ? "encrypt" : "decrypt") + " result=" + result);
                return null;
            }
            int offset = OFFSET;
            //Encrypted/Decrypted Data Length	2 + Encrypted/Decrypted Data + KSN length 1 + KSN var
            byte[] dataLenFb = new byte[2];
            System.arraycopy(respData, offset, dataLenFb, 0, dataLenFb.length);
            offset += dataLenFb.length;
            int datalenFi = InnerUtils.bytesToInt(dataLenFb, -1, dataLenFb.length, true);

            byte[] dataFb = new byte[datalenFi];
            System.arraycopy(respData, offset, dataFb, 0, dataFb.length);
            offset += datalenFi;

            int ksnlenFi = 0;
            String ksn = null;
            if (keyManagement == KeyManagement.DUKPT && respData.length - 2 > offset) {
                ksnlenFi = respData[offset];
                offset += 1;
                if (ksnlenFi > 0) {
                    byte[] ksnFb = new byte[ksnlenFi];
                    System.arraycopy(respData, offset, ksnFb, 0, ksnFb.length);
                    offset += ksnFb.length;
                    ksn = ISOUtils.hexString(ksnFb);
                }
            }
            CipherResult cipherResult = new CipherResult(dataFb, ksn);
            devicelogger.debug("[calculateDate] cipherResult getData=" + cipherResult.getData() + " getKsn=" + cipherResult.getKsn());
            return cipherResult;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public CipherResult encrypt(KeyManagement keyManagement, AlgorithmMode algorithmMode, CipherMode cipherMode, int keyIndex, @NonNull byte[] inputData, CipherExtParams params) {
        return calculateDate(MNAPIHelper.CALC_ENCRYPT, keyManagement, algorithmMode, cipherMode, keyIndex, inputData, params);
    }

    @Override
    public CipherResult decrypt(KeyManagement keyManagement, AlgorithmMode algorithmMode, CipherMode cipherMode, int keyIndex, @NonNull byte[] inputData, CipherExtParams params) {
        return calculateDate(MNAPIHelper.CALC_DECRYPT, keyManagement, algorithmMode, cipherMode, keyIndex, inputData, params);
    }

    @Override
    public MacResult calcMac(KeyManagement keyManagement, int macAlgorithm, int keyIndex, @NonNull byte[] inputData, CalMacExtParams calMacExtParams) {
        devicelogger.debug("[calcMac] keyManagement=" + keyManagement + " macAlgorithm=" + macAlgorithm + " keyIndex=" + keyIndex + " inputData=" + hexString(inputData));
        NCalMacExtParams nCalMacExtParams = null;
        if (calMacExtParams instanceof NCalMacExtParams) {
            nCalMacExtParams = (NCalMacExtParams) calMacExtParams;
        }
        if (calMacExtParams != null) {
            devicelogger.debug("[calcMac] getWorkingKeyData=" + hexString(calMacExtParams.getWorkingKeyData()) + " getRandomIndex=" + hexString(calMacExtParams.getRandomIndex()));
        }
        if (nCalMacExtParams != null) {
            devicelogger.debug("[calcMac] getDukptDerivedMode=" + nCalMacExtParams.getDukptDerivedMode() + " getInitialVector=" + nCalMacExtParams.getInitialVector());
        }

        DukptDerivedMode dukptDerivedMode = DukptDerivedMode.BOTH;
        if (nCalMacExtParams != null) {
            dukptDerivedMode = nCalMacExtParams.getDukptDerivedMode();
        }
        MacType macType = null;
        if (keyManagement == KeyManagement.MKSK) {
            if (macAlgorithm == MacAlgorithm.DES.X99) {
                macType = MacType.MKSK_DES_X99;
            } else if (macAlgorithm == MacAlgorithm.DES.X919) {
                macType = MacType.MKSK_DES_X919;
            } else if (macAlgorithm == MacAlgorithm.DES.ECB) {
                macType = MacType.MKSK_DES_UNIONPAY_ECB;
            } else if (macAlgorithm == MacAlgorithm.DES.M9606) {
                macType = MacType.MKSK_DES_9606;
            } else if (macAlgorithm == MacAlgorithm.DES.CBC) {
                macType = MacType.MKSK_DES_X99;
            } else if (macAlgorithm == MacAlgorithm.DES.X919_3DES) {
                macType = null;
            } else if (macAlgorithm == MacAlgorithm.SM4.X99) {
                macType = MacType.MKSK_SM4_X99;
            } else if (macAlgorithm == MacAlgorithm.SM4.SM4_UNIONPAY) {
                macType = MacType.MKSK_SM4_UNIONPAY;
            } else if (macAlgorithm == MacAlgorithm.SM4.SM4_ECB) {
                macType = MacType.MKSK_SM4_UNIONPAY;
            } else if (macAlgorithm == MacAlgorithm.SM4.M9606) {
                macType = MacType.MKSK_SM4_9606;
            } else if (macAlgorithm == MacAlgorithm.AES.X99) {
                macType = MacType.MKSK_AES_X99;
            }
        } else if (keyManagement == KeyManagement.DUKPT) {
            if (macAlgorithm == MacAlgorithm.DES.X99) {
                if (dukptDerivedMode == DukptDerivedMode.BOTH) {
                    macType = MacType.DUKPT_DES_X99;
                } else if (dukptDerivedMode == DukptDerivedMode.RESP) {
                    macType = MacType.DUKPT_DES_RESP_X99;
                }
            } else if (macAlgorithm == MacAlgorithm.DES.X919) {
                if (dukptDerivedMode == DukptDerivedMode.BOTH) {
                    macType = MacType.DUKPT_DES_X919;
                } else if (dukptDerivedMode == DukptDerivedMode.RESP) {
                    macType = MacType.DUKPT_DES_RESP_X919;
                }
            } else if (macAlgorithm == MacAlgorithm.DES.ECB) {
                if (dukptDerivedMode == DukptDerivedMode.BOTH) {
                    macType = MacType.DUKPT_DES_UNIONPAY_ECB;
                } else if (dukptDerivedMode == DukptDerivedMode.RESP) {
                    macType = MacType.DUKPT_DES_RESP_UNIONPAY_ECB;
                }
            } else if (macAlgorithm == MacAlgorithm.DES.M9606) {
                if (dukptDerivedMode == DukptDerivedMode.BOTH) {
                    macType = MacType.DUKPT_DES_9606;
                } else if (dukptDerivedMode == DukptDerivedMode.RESP) {
                    macType = MacType.DUKPT_DES_RESP_9606;
                }
            } else if (macAlgorithm == MacAlgorithm.DES.CBC) {
                if (dukptDerivedMode == DukptDerivedMode.BOTH) {
                    macType = MacType.DUKPT_DES_X99;
                } else if (dukptDerivedMode == DukptDerivedMode.RESP) {
                    macType = MacType.DUKPT_DES_RESP_X99;
                }
            } else if (macAlgorithm == MacAlgorithm.DES.X919_3DES) {
                if (dukptDerivedMode == DukptDerivedMode.BOTH) {
                    macType = null;
                } else if (dukptDerivedMode == DukptDerivedMode.RESP) {
                    macType = null;
                }
            } else if (macAlgorithm == MacAlgorithm.AES.X99) {
                if (dukptDerivedMode == DukptDerivedMode.BOTH) {
                    macType = MacType.DUKPT_AES_X99;
                } else if (dukptDerivedMode == DukptDerivedMode.RESP) {
                    //macType = MacType.DUKPT_AES_RESP_X99;
                }
            }
        }
        devicelogger.debug("[calcMac]->[calculateMac] macType="+macType);
        return calculateMac(macType,keyIndex,inputData,nCalMacExtParams);
    }

    @Override
    public MacResult calculateMac(MacType macType, int keyIndex, byte[] inputData, @Nullable NCalMacExtParams calMacExtParams) {
        devicelogger.debug("[calculateMac] macType=" + macType + " keyIndex=" + keyIndex + " inputData=" + hexString(inputData));
        if (calMacExtParams != null) {
            devicelogger.debug("[calculateMac] getDukptDerivedMode=" + calMacExtParams.getDukptDerivedMode() + " getInitialVector=" + hexString(calMacExtParams.getInitialVector()));
        }
        int ivLen = 0;
        byte[] ivData = null;
        if (calMacExtParams != null) {
            ivData = calMacExtParams.getInitialVector();
        }

        if (ivData != null) {
            ivLen = ivData.length;
        }
        int adLen = 0;
        byte[] adData = null;
        int BlockFlag = MNAPIHelper.BLOCK_ONLY;
        //Mac Type 1+Key Index 1+IV Length	1+Initial value	var+
        //First/Next/Last Block Flag	1+Data Length 2+Data	var.+AD size 2+AD data var
        String body = mMNAPIHelper.getMacType(macType) + String.format("%02x", keyIndex) + String.format("%02x", ivLen) + mMNAPIHelper.getData(ivData) +
                String.format("%02x", BlockFlag) + String.format("%04x", inputData.length) + mMNAPIHelper.getData(inputData) + String.format("%04x", adLen) + mMNAPIHelper.getData(adData);
        byte[] respData = communication(mMNAPIHelper.pack("C6".getBytes(), body));
        boolean result = getResultCode(respData, 14);
        if (result == false) {
            devicelogger.error("[calculateMac] result=" + result);
            return null;
        }
        //02 00 15 43 37 2F 05 30 30 08 92 6D F9 CF B0 3C D8 A3 03 7E
        //key index	1 +Response Code 2 +MAC Length	1 +MAC data +Ksn len	1 +DUKPT KSN
        int offset = 9;
        byte[] dataLenFb = new byte[1];
        System.arraycopy(respData, offset, dataLenFb, 0, dataLenFb.length);
        offset += dataLenFb.length;
        int datalenFi = InnerUtils.bytesToInt(dataLenFb, -1, dataLenFb.length, true);

        byte[] dataFb = new byte[datalenFi];
        System.arraycopy(respData, offset, dataFb, 0, dataFb.length);
        offset += datalenFi;

        int ksnlenFi = 0;
        String ksn = null;
        byte[] ksnFb = null;
        if (respData.length - 2 > offset) {
            ksnlenFi = respData[offset];
            offset += 1;
            if (ksnlenFi > 0) {
                ksnFb = new byte[ksnlenFi];
                System.arraycopy(respData, offset, ksnFb, 0, ksnFb.length);
                offset += ksnFb.length;
            }
        }
        MacResult macResult = new MacResult(dataFb, ksnFb);
        devicelogger.debug("[calculateMac] getMac=" + hexString(macResult.getMac()) + " getKsn=" + hexString(macResult.getKsn()));
        return macResult;
    }

    @Override
    public boolean injectKey(LoadKeyMode loadKeyMode, AlgorithmMode srcKeyAlg, int srcKeyIndex, InjectKeyType srcKeyType, AlgorithmMode destKeyAlg, int destKeyIndex, InjectKeyType destKeyType, byte[] keyData, NInjectKeyParams params) {
        return false;
    }

    @Override
    public boolean ksnIncrease(int dukptKeyIndex) {
        try {
            devicelogger.debug("[ksnIncrease] dukptKeyIndex="+dukptKeyIndex);
            //group index	1
            String body = String.format("%02x",dukptKeyIndex);
            return getResultCode(communication(mMNAPIHelper.pack("7g".getBytes(),body)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean ksnAESIncrease(int dukptKeyIndex) {
        return false;
    }

    @Override
    public byte[] getDukptKsn(int dukptKeyIndex) {
        devicelogger.debug("[getDukptKsn] dukptKeyIndex="+dukptKeyIndex);
        byte[] ksn = mExternalPininput.getDukptKsn(dukptKeyIndex);
        devicelogger.debug("[getDukptKsn] ksn="+hexString(ksn));
        return ksn;
    }

    @Override
    public byte[] getDukptAESKsn(int dukptKeyIndex) {
        return null;
    }

    @Override
    public boolean checkKeyIsExist(KeyType keyType, AlgorithmMode algorithmMode, int keyIndex, @Nullable byte[] checkValue) {
        try {
            devicelogger.debug("[checkKeyIsExist] keyType="+keyType+" algorithmMode="+algorithmMode+" keyIndex="+keyIndex+" checkValue="+hexString(checkValue));
            int adLen = 0;
            byte[] adData = null;
            //Get Key Info ID	1 +Key Index	1 +
            //Key Type	1 +Key Usage	1 +AD size	2 +AD data	var
            String body = String.format("%02x",MNAPIHelper.KEYINFOID_KCV)+String.format("%02x",keyIndex)+
                    mMNAPIHelper.getKeyType(algorithmMode)+mMNAPIHelper.getKeyUsage(keyType)+String.format("%04x",adLen)+mMNAPIHelper.getData(adData);
            byte[] respData = null;
            boolean result = getResultCode(respData = communication(mMNAPIHelper.pack("C2".getBytes(),body)));
            if(!result){
                return false;
            }
            int offset = OFFSET;
            byte[] dataLenFb = new byte[2];
            System.arraycopy(respData, offset, dataLenFb, 0, dataLenFb.length);
            offset += dataLenFb.length;
            int datalenFi = InnerUtils.bytesToInt(dataLenFb, -1, dataLenFb.length, true);

            byte[] dataFb = new byte[datalenFi];
            System.arraycopy(respData, offset, dataFb, 0, dataFb.length);
            offset += datalenFi;

            byte[] kcv = dataFb;
            if (null != checkValue && null != kcv) {
                if(checkValue.length < 3){
                    return false;
                }
                int length = (checkValue.length > kcv.length ? kcv.length : checkValue.length);

                byte[] srcKcv = new byte[length];
                System.arraycopy(checkValue, 0, srcKcv, 0, length);

                byte[] dstKcv = new byte[length];
                System.arraycopy(kcv, 0, dstKcv, 0, length);
                return Arrays.equals(srcKcv, dstKcv);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteKey(KeyType keyType, AlgorithmMode algorithmMode, int keyIndex) {
        try {
            devicelogger.debug("[deleteKey] keyType="+keyType+" algorithmMode="+algorithmMode+" keyIndex="+keyIndex);
            //Key Index	1 +Key Type	1 +Block Format	1
            String body =String.format("%02x",keyIndex)+mMNAPIHelper.getKeyType(keyType)+mMNAPIHelper.getBlockFormat(algorithmMode);
            return getResultCode(communication(mMNAPIHelper.pack("BJ".getBytes(),body)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteAllKeys() {
        try {
            devicelogger.debug("[deleteAllKeys]");
            return getResultCode(communication(mMNAPIHelper.pack("60".getBytes(),null)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private PinpadExtParams getPinpadExtParams(PinInputExtParams pinInputExtParams){
        PinpadExtParams pinpadExtParams = null;
        if(pinInputExtParams != null){
            int inputMin = 0;
            int inputMax = pinInputExtParams.getInputMaxLen();
            byte[] pwdRangeFb = pinInputExtParams.getPwdLengthRange();
            byte[] pwdRangeFb2 = null;
            if(pwdRangeFb != null){
                if(pwdRangeFb != null) {
                    byte[] pwdRangeFb1 = new byte[pwdRangeFb.length];
                    int count = 0;
                    for(int i=0; i < pwdRangeFb.length; i++){
                        if( pwdRangeFb[i] >= inputMin && pwdRangeFb[i] <= inputMax){
                            pwdRangeFb1[i] = pwdRangeFb[i];
                            count++;
                        }
                    }
                    pwdRangeFb2 = new byte[count];
                    System.arraycopy(pwdRangeFb1,0,pwdRangeFb2,0,count);
                }
                pinpadExtParams = new PinpadExtParams(0,inputMax,pwdRangeFb2);
            }else {
                pinpadExtParams = new PinpadExtParams(0,inputMax);
            }

            /**
             * 海外指令暂不支持,先屏蔽代码
             */
            AccountInputType accountInputType = pinInputExtParams.getAcctInputType();
            if(accountInputType != null){
                //pinpadExtParams.setAcctInputType(accountInputType);
            }
            /**
             * 海外指令暂不支持,先屏蔽代码
             */
            PinBlockMode pinBlockMode = pinInputExtParams.getPinBlockMode();
            if(pinBlockMode != null){
                //pinpadExtParams.setEncryMode(pinBlockMode.getCode());
            }
            devicelogger.debug("[getPinpadExtParams] pinpadExtParams inputMin="+inputMin+" inputMax="+inputMax+" pwdRangeFb="+hexString(pwdRangeFb)+" pwdRangeFb2="+hexString(pwdRangeFb2)+" accountInputType="+accountInputType+" pinBlockMode="+pinBlockMode);
        }
        devicelogger.debug("[getPinpadExtParams] pinpadExtParams="+pinpadExtParams);
        return pinpadExtParams;
    }

    @Override
    public void startPinInput(KeyManagement keyManagement, AlgorithmMode algorithmMode, int keyIndex, String pan, int timeout, @NonNull PinInputListener pinInputListener, PinInputExtParams pinInputExtParams) {
        try {
            devicelogger.debug("[startPinInput] keyManagement="+keyManagement+" algorithmMode="+algorithmMode+" keyIndex="+keyIndex+" pan="+pan+" timeout="+timeout+" pinInputListener="+pinInputListener+" pinInputExtParams="+pinInputExtParams);
            mExternalPininput.startExternalPinInput(keyManagement,algorithmMode,-1,keyIndex,pan,timeout,pinInputListener,getPinpadExtParams(pinInputExtParams));
        } catch (Exception e) {
            pinInputListener.onError(ErrorCode.INPUT_PIN_ERROR, "PinInput exception:" + e);
            e.printStackTrace();
        }
    }

    @Override
    public void startOfflinePinInput(int timeout, byte[] modulus, byte[] exponent, @NonNull PinInputListener pinInputListener, PinInputExtParams pinInputExtParams) {
        try {
            devicelogger.debug("[startOfflinePinInput] timeout="+timeout+" modulus="+hexString(modulus)+" exponent="+hexString(exponent)+" pinInputListener="+pinInputListener+" pinInputExtParams="+pinInputExtParams);
            int mainKey = -1;
            AlgorithmMode algorithmMode = null;
            mExternalPininput.startOfflinePinInput(mainKey,algorithmMode,timeout,modulus,exponent,pinInputListener, getPinpadExtParams(pinInputExtParams));
        } catch (Exception e) {
            pinInputListener.onError(ErrorCode.INPUT_PIN_ERROR, "Offline PinInput exception:" + e);
            e.printStackTrace();
        }
    }

    @Override
    public void cancelPinInput() {
        devicelogger.debug("[cancelPinInput]");
        mExternalPininput.cancelPinInput();
    }

    @Override
    public byte[] getKeyKcv(KeyType keyType, AlgorithmMode algorithmMode, int keyIndex) {
        try {
            devicelogger.debug("[getKeyKcv] keyType="+keyType+" algorithmMode="+algorithmMode+" keyIndex="+keyIndex);
            int adLen = 0;
            byte[] adData = null;
            //Get Key Info ID	1 +Key Index	1 +
            //Key Type	1 +Key Usage	1 +AD size	2 +AD data	var
            String body = String.format("%02x",MNAPIHelper.KEYINFOID_KCV)+String.format("%02x",keyIndex)+
                    mMNAPIHelper.getKeyType(algorithmMode)+mMNAPIHelper.getKeyUsage(keyType)+String.format("%04x",adLen)+mMNAPIHelper.getData(adData);
            byte[] respData = null;
            boolean result = getResultCode(respData = communication(mMNAPIHelper.pack("C2".getBytes(),body)));
            if(!result){
                devicelogger.error("[getKeyKcv] error.");
                return null;
            }
            int offset = OFFSET;
            byte[] dataLenFb = new byte[2];
            System.arraycopy(respData, offset, dataLenFb, 0, dataLenFb.length);
            offset += dataLenFb.length;
            int datalenFi = InnerUtils.bytesToInt(dataLenFb, -1, dataLenFb.length, true);

            byte[] dataFb = new byte[datalenFi];
            System.arraycopy(respData, offset, dataFb, 0, dataFb.length);
            offset += datalenFi;
            return dataFb;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public byte[] loadRandomKeyboard(KeyboardRandom keyboardRandom) {
        devicelogger.debug("[loadRandomKeyboard] keyboardRandom="+keyboardRandom);
        return new byte[0];
    }

    @Override
    public boolean loadRNIBKeyboard(int keyNum, Map<PinPadButton, int[]> pinPadButtons, int[] touchCoordinates, int[] KeyboradCoordinates){
        return false;
    }

    @Override
    public TusnData getTusnData(String random) {
        devicelogger.debug("[getTusnData] random="+random);
        return null;
    }

    @Override
    public boolean isStandardModule() {
        return false;
    }

    @Override
    public ModuleType getStandardModuleType() {
        return null;
    }

    @Override
    public String getExModuleType() {
        return null;
    }

    @Override
    public Device getOwner() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    private byte[] communication(byte[] data) {
        devicelogger.debug("[communication] sendData=" + hexString(data));
        byte[] respData = mMposComm.communication(data);
        devicelogger.debug("[communication] receiveData=" + hexString(respData));
        return respData;
    }

    private boolean getResultCode(byte[] data, int index) {
        if (ISOUtils.hexString(data).substring(index, index + 4).equals("3030")) {
            return true;
        }
        return false;
    }

    private boolean getResultCode(byte[] data) {
        return getResultCode(data, 12);
    }

    private String hexString(byte[] data) {
        return (data == null ? "null" : ISOUtils.hexString(data));
    }

}
