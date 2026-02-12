package com.newland.nsdk.core.internal.jni;

import com.newland.nsdk.core.api.common.crypto.CryptogramInfo;
import com.newland.nsdk.core.api.common.keymanager.AsymmetricKey;
import com.newland.nsdk.core.api.internal.keymanager.MACVerifyParameters;
import com.newland.nsdk.core.api.internal.keymanager.SignVerifyParameters;
import com.newland.nsdk.core.api.internal.pinentry.RSAKey;
import com.newland.nsdk.core.api.internal.pinentry.KeyboardParameters;
import com.newland.nsdk.core.common.keymanager.ST_SEC_ASYM_ALG_INFO;
import com.newland.nsdk.core.common.keymanager.ST_SEC_ASYM_KEYIN_DATA;
import com.newland.nsdk.core.common.keymanager.ST_SEC_ASYM_KEY_INFO;
import com.newland.nsdk.core.common.keymanager.ST_SEC_KCV_DATA;
import com.newland.nsdk.core.common.keymanager.ST_SEC_KEYNUM_INFO;
import com.newland.nsdk.core.common.keymanager.ST_SEC_SYMM_KEYID_INFO;
import com.newland.nsdk.core.internal.card.contactless.JNIActivationResult;
import com.newland.nsdk.core.internal.cardreader.CardReaderResult;
import com.newland.nsdk.core.internal.crypto.ST_SEC_DUKPT_DERIVATE_DATA;
import com.newland.nsdk.core.internal.crypto.ST_SEC_ENCRYPTION_DATA;
import com.newland.nsdk.core.internal.ecdhe.ST_SEC_ECDHE_KDF_INFO;
import com.newland.nsdk.core.internal.keymanager.ST_SEC_INJECTKEY_INFO;
import com.newland.nsdk.core.internal.keymanager.ST_SEC_KEYIN_DATA;
import com.newland.nsdk.core.internal.keymanager.ST_SEC_VERIFY_MAC_INFO;
import com.newland.nsdk.core.internal.pinentry.ST_NAPI_RSA_KEY;
import com.newland.nsdk.core.internal.pinentry.SysEventCallBack;

/**
 * Author by wuhh, Date on 2020/2/10.
 */
public class NSDKJni {
    private static NSDKJni NSDKJni;

    static {
        System.loadLibrary("nsdk");
    }

    private NSDKJni() {
    }

    public static NSDKJni getInstance() {
        if (NSDKJni == null) {
            synchronized (NSDKJni.class) {
                if (NSDKJni == null) {
                    NSDKJni = new NSDKJni();
                }
            }
        }
        return NSDKJni;
    }

    public native int getErrorMsg(int errorCode, byte[] errorMsg);

    //CardReaderImpl
    public native int displayRfidLogo(boolean isDisplayed);

    public native int openCardReader(int readMode, int contactlessCardTypes, boolean isVerifyTrack, int timeout, byte[] paramTypeF, int lenParamTypeF, byte[] paramTypeV, int lenParamTypeV,  CardReaderResult result, boolean isLpcd);

    public native int openCardReader2(int readMode, int contactlessCardTypes, boolean isVerifyTrack, int timeout, byte[] paramTypeF, int lenParamTypeF, byte[] paramTypeV, int lenParamTypeV, CardReaderResult result);
    public native int openCardReaderWithCardEvent(int readMode, int contactlessCardTypes, boolean isVerifyTrack, int timeout, byte[] paramTypeF, int lenParamTypeF, byte[] paramTypeV, int lenParamTypeV, CardReaderResult result);
    public native int cancelCardReader();

    public native int closeCardReader();

    public native void resetCancelFlag();
    // Contact card
    public native int ICSetConfig(int ictype, int cfgtype, int value);
    public native int ICCheckSlotsState(int slot);

    public native int ICPowerUp(int slot, int cardType, byte[] atr, int[] atrLen);

    public native int ICPowerDown(int slot, int cardType);

    public native int ICPerformAPDU(int slot, int cardType, byte[] command, int commandLen,  byte[] recv, int[] len);
    // Contactless card

    public native int RFActivate(int cardType, JNIActivationResult jniActivationResult);

    public native int RFDeactivate();

    public native int RFIsCardPresent(int clType);

    public native int RFGetVersion(int nLen, byte[] versionBuf);

    public native int RFPerformAPDU(byte[] command, int commandLen, byte[] recv, int[] recvLen);

    public native int RFActivate2(JNIActivationResult jniActivationResult);

    public native int RFPerformRats(byte[] data, int[] dataLen);

    public native int RFOn();

    public native int RFClose();
    //DeviceBasicImpl

    public native int setDeviceDate(byte[] date);

    public native int getDeviceDate(byte[] date);

    public native int getDeviceSN(byte[] sn);

    public native int setSysKeyVol(boolean isOpen);

    public native int setSysBeep_Extern(int type, int volume);

    public native int NDK_LedFuncModeSet(int deviceType, int interval);

    public native int NDK_SysSetKeyLongPress(int keys, int status);

    public native int NDK_GetDeviceSN(byte[] sn);

    public native int NDK_SysGetPosInfo(int infoKey, byte[] sn);

    public native int NDK_SysGetCapability(int nSizeOfCap, byte[] szCaps);

    public native int NDK_RfidFuncisSupport(int type, int[] result);

    public native int NDK_SecGetDrySR(int[] value);

    public native int NAPI_SecGetDeviceStatus(int[] status);

    public native int NAPI_SecSetDeviceStatus(int status);
    //led

    public native int operateLight(int status, int color);

    public native int blinkLight(int count, int color, int interval);

    public native int blinkVirtualLight(int count, int color, int interval);

    public native int blinkVirtual(int x, int y, int horizontal, int alwaysDisplayBackground, int count, int color, int onDuration, int offDuration);

    public native int RFM0Authenticate(byte[] command);

    public native int RFM0ReadBlockData(int blockNo, byte[] recv, int[] len);

    public native int RFM0WriteBlockData(int blockNo, byte[] data);

    public native int RFM1Authenticate(int rfKeyMode, byte[] uid, int blockNo, byte[] key);

    public native int RFM1ReadBlockData(int blockNo, byte[] recv, int[] len);

    public native int RFM1WriteBlockData(int blockNo, byte[] data);

    public native int RFM1Increment(int blockNo, byte[] data);

    public native int RFM1Decrement(int blockNo, byte[] data);

    public native int RFM1Transfer(int blockNum);

    public native int RFM1Restore(int blockNum);

    public native int RFFelicaTransmit(byte[] send, byte[] recv, int[] len);

    public native int RFFelicaTransmit2(byte[] send, byte[] recv, int[] len, int times, int timeout);

    public native int RFFelicaSetTimeout(int timeout);

    public native int RFFelicaPolling(byte[] systemCode, byte requestCode, byte timeslot, byte[] receiveData, int[] receiveDataLen);

    public native int RFFelicaPollingWithTimeout(byte[] systemCode, byte requestCode, byte timeslot, byte[] receiveData, int[] receiveDataLen, int timeout);
    //crypto

    public native int NAPI_SecGenerateKey(int method, ST_SEC_KEYIN_DATA keyData, ST_SEC_KCV_DATA kcvData);

    public native int NAPI_SecGenerateAsymKey(long[] handle, ST_SEC_ASYM_KEYIN_DATA stSecAsymKeyinData, ST_SEC_ASYM_ALG_INFO asymAlgInfo);

    public native int NAPI_SecGenerateAsymKeyState(long handle);

    public native int NAPI_SecCancelGenerateAsymKey(long handle);

    public native int NAPI_SecDeleteKey(int keyId, int keyType, int keyUsage);

    public native int NAPI_SecSymmKeyErase();

    public native int NAPI_SecGetSymmKeyNum(int[] totalKeyNum, ST_SEC_KEYNUM_INFO[] stSecKeynumInfos, int[] keyNumCounts);

    public native int NAPI_SecGetSymmKeyInfoById(byte keyID, ST_SEC_SYMM_KEYID_INFO[] stSecSymmKeyidInfos, int[] keyNumCounts);

    public native int NAPI_SecGetKeyInfo(int infoID, int keyId, int keyType, int keyUsage, byte[] pAD, int adSize, byte[] outInfo, int[] outInfoLen);

    public native int NAPI_SecSetKeyOwner(String pszName);

    public native int NAPI_SecGetKeyOwner(int nLenOfOwnerBuffer, byte[] pszOwner);

    public native int NAPI_SecKeyExport(int mode, ST_SEC_KEYIN_DATA stSecKeyinData, byte[] outData, int[] outDataLen);


    public native int NAPI_SecInjectPubKeys(ST_SEC_INJECTKEY_INFO[] stSecInjectkeyInfos, int injectKeyInfoListCount, ST_SEC_VERIFY_MAC_INFO macInfo, SignVerifyParameters signVerifyParameters, byte[] data, int dataLen, byte[] additionalData, int additionalDataLen);

    public native int NAPI_SecGenerateMAC(int MacType, int ucKeyID, byte[] psIV, int unIVSize, byte[] psDataIn, int nDataInLen, byte[] pAD, int unADSize, byte[] psMacOut, int[] pnOutLen, byte[] psKsnOut, int[] nOutKsnLen);

    public native int NAPI_SecGenerateMAC_DerivateKey(int MacType, int ucKeyID, byte[] iv, int ivLen, byte[] dataIn, int length, ST_SEC_DUKPT_DERIVATE_DATA dukptDerivateData, byte[] outData, int[] outDataLen, byte[] ksnData, int[] ksnDataLen);

    public native int NAPI_SecEncryption(ST_SEC_ENCRYPTION_DATA DataIn, byte[] psDataOut, int[] pnOutLen, byte[] psKsnOut, int[] pnOutKsnLen);

    public native int NAPI_SecDecryption(ST_SEC_ENCRYPTION_DATA DataIn, byte[] psDataOut, int[] pnOutLen, byte[] psKsnOut, int[] pnOutKsnLen);

    public native int NAPI_SecEncryption_GCM(ST_SEC_ENCRYPTION_DATA DataIn, byte[] psDataOut, int[] pnOutLen, byte[] psKsnOut, int[] pnOutKsnLen, byte[] tagData, int tagDataLen, byte[] authData, int authDataLen);

    public native int NAPI_SecDecryption_GCM(ST_SEC_ENCRYPTION_DATA DataIn, byte[] psDataOut, int[] pnOutLen, byte[] psKsnOut, int[] pnOutKsnLen, byte[] tagData, int tagDataLen, byte[] authData, int authDataLen);

    public native int NAPI_SecGeneratePubKeyCert(ST_SEC_ASYM_KEY_INFO caKey, ST_SEC_ASYM_KEY_INFO dstKey, byte[] cipherCert, int[] cipherCertLen);

    public native int NAPI_SecVppTpInit(byte[] numBtn, byte[] funcBtn, byte[] keySeq, int keyboardType);

    public native int NAPI_SecVppRNIBTpInit(int[] coordination, int[] areaCoordination, int[] keyPadCoordination,int keyNumber);

    public native int NAPI_SecVPPInit(int SessionType, int KeyType, int ucKeyIdx, String pPAN, int PINBlockFmt, int unTimeOut, ST_NAPI_RSA_KEY pRSAKey, byte[] pAD, int unADSize);

    public native int NAPI_SecVPPInit_DerivateKey(int SessionType, int keyType, int keyID, String pan, int PINBlockFmt, int timeout, ST_SEC_DUKPT_DERIVATE_DATA dukptDerivateData);

    public native int NAPI_SecVPPGetEvent(int[] nEvent, byte[] psPinBlock, int[] pnOutPinLen, byte[] psKsn, int[] pnOutKsnLen);

    public native int NAPI_SecVPPSetEvent(int key);

    public native int NAPI_SecVPPSetExpPinLenIn(String pinLenIn);

    public native int NAPI_SecVppSetButtonFunc(int button, int funcType);

    public native int NAPI_SecGetRandom(int nRandLen, byte[] pvRandom);

    public native int NAPI_SecVPPAAInit(int[] keyValues, int[] buttonsCoordination, int buttonCount, int[] screenArea, int[] pinpadArea, KeyboardParameters keyboardParameters);

    public native int NAPI_SecVPPAASetMap(int[] eventValues, int[] actionValues, int count, int setMode);

    public native int NAPI_SecVPPAAGetPin(int[] nEvent, int[] eventType, int[] touchState, byte[] pinBlock, int[] pinLen, byte[] ksn, int[] ksnLen);

    public native int NDK_SecVerifyPIN(int keyId, int keyType, byte[] psTSK, byte[] psPan, byte[] pinBlock, ST_NAPI_RSA_KEY pRSAKey, byte[] psIccRespOut, int[] outLen);

    public native int NAPI_SecIncreaseKsn(int id);

    public native int NAPI_SecIncreaseAESKSN(int id);

    public native int NAPI_SecAsymGenerateKey(int keyGenerateMethod, ST_SEC_ASYM_KEYIN_DATA keyData, ST_SEC_KCV_DATA kcvData, int[] randomKeyLen, byte[] randomKey);

    public native int NAPI_SecGenerateTR34Random(int len, byte[] randomData);

    public native int NAPI_SecTR34ProcessKeyBlock(int encodingMode, ST_SEC_ASYM_KEYIN_DATA asymKeyinData, String tr34data);

    public native int NAPI_SecTR34ProcessKeyBlockRevolut(int encodingMode, ST_SEC_ASYM_KEYIN_DATA asymKeyinData, byte[] tr34data);

    public native int NAPI_SecTR34ProcessKeyBlockWithPad(int encodingMode, ST_SEC_ASYM_KEYIN_DATA asymKeyinData, byte[] pAD, int[] pADLen);

    public native int NAPI_SecCSRInit();

    public native int NAPI_SecCSRSetParameters(ST_SEC_ASYM_KEYIN_DATA asymKeyinData, byte certType, boolean isCA, String userName);

    public native int NAPI_SecCSRSetExtension(byte[] oid, int oidLen, byte[] value, int valueLen);

    public native int NAPI_SecCSRGen(int type, byte[] data, int[] dataLen);

    public native int NAPI_SecCSRRelease();

    public native int NAPI_SecVerifyCert(int caType, String caCertData, int caCertDataLen, byte[] certData, int certDataLen, byte[] publicKey, int[] publicKeyLen);

    public native int generateKeyWithSymmKey(int method, ST_SEC_KEYIN_DATA stSecKeyinData, ST_SEC_KCV_DATA stSecKcvData, byte[] outData, int[] outDataLen);

    public native int NAPI_SecPINBlockConvert(String pan, int pinConvertMode, int sessionPinBlockFormat, int convertPinBlockFormat, int sessionKeyID, ST_SEC_SYMM_KEYID_INFO sessionKeyInfo, int pinKeyID, ST_SEC_SYMM_KEYID_INFO pinKeyInfo, RSAKey rsaKey, byte[] pinBlock, int pinBlockLen, byte[] outPinBlock, int[] outPinBlockLen);
    //printer

    public native int NDK_PrnModuleInit();

    public native int NDK_PrnSetGreyScale(int unGrey);

    public native int TTF_PrnSetPaperSize(int size);

    public native int NDK_SYS_RegisterEvent(int event, int timeOutMs, SysEventCallBack callBack);

    public native int NDK_SYS_UnRegisterEvent(int event);

    public native int NDK_PrnGetStatus(int[] status);

    public native int NAPI_PrnOpenDev();

    public native int NAPI_PrnCloseDev();

    public native int NDK_PrnSetParam(int type, int value);

    public native int NDK_PrnFeedPaper();

    public native int NDK_PrnFeedByPixels(int pixels);

    public native int NDK_PrnGetStatusValue(int[] temperature);

    public native int NAPI_Beep(int frequency, int duration);

    public native int NDK_SysTimeBeep_Ex(int frequency, int duration, int volume);

    public native int NDK_SysSetBeepVol(int volume);

    public native int NDK_SysGetBatteryProperty(byte[] isSupportGetBatteryTemp, int[] supportGetBatteryTempLen, byte[] isSupportGetChargeCurrent, int[] supportGetChargeCurrentLen,
                                                byte[] batteryTemp, int[] batteryTempLen, byte[] adapterVoltage, int[] adapterVoltageLen, byte[] chargeCurrent, int[] chargeCurrentLen);

    public native int NAPI_SecLoadTrustedCert(boolean isCA, int length, byte[] cert, int[] pubKeyLen, byte[] pubKey);

    public native int NAPI_SecResetCertStatus();

    public native int NAPI_SecInitAtomic();

    public native int NAPI_SecCommitAtomic(boolean isSuccessful);

    public native int NAPI_SecAsymEncryption(byte keyId, byte keyType, byte keyUsage, int messageDigestType, int encodingMode, int cryptoMode, int dataInLen, byte[] dataIn, int[] outDataLen, byte[] outData);

    public native int NAPI_SecAsymDecryption(byte keyId, byte keyType, byte keyUsage, int messageDigestType, int encodingMode, int cryptoMode, int dataInLen, byte[] dataIn, int[] outDataLen, byte[] outData);

    public native int NAPI_SecAsymSign(byte keyId, byte keyType, byte keyUsage, int messageDigestType, int encodingMode, int hashLen, byte[] hash, int[] sigDataLen, byte[] sigData);

    public native int NAPI_SecAsymVerify(byte keyId, byte keyType, byte keyUsage, int messageDigestType, int encodingMode, int hashLength, byte[] hash, int signedDataLength, byte[] signedData);

    public native int NAPI_SecCreateCryptogram(AsymmetricKey cryptoKey, ST_SEC_KEYIN_DATA secKeyinData, CryptogramInfo cryptogramInfo, byte[] outData, int[] outDataLen);

    public native int NDK_PortOpen(int comNumber, String configStr);

    public native int NDK_PortClose(int comNumber);

    public native int NDK_PortRead(int comNumber, int maxLen, int timeout, byte[] outData, int[] outDataLen);

    public native int NDK_PortWrite(int comNumber, int length, byte[] data);

    public native int NDK_PortClrBuf(int comNumber);

    public native int verifyOfflinePIN(int pinSessionType, int keyTypeCode, int keyID, int keyUsageCode, String pan, int pinBlockFormat, byte[] pinBlock, ST_NAPI_RSA_KEY jniRSAKey, byte[] extKey, byte[] outData, int[] outDataLen);

    public native int getTamperStatus(int[] status);

    public native int NDK_KmlRkiGetPediRequest(byte[] info, int[] infoLen, byte[] errMsg, int[] errMsgLen);

    public native int NDK_KmlRkiSetPediResponse(byte[] data, int length, byte[] errMsg, int[] errMsgLen);

    public native int NDK_KmlRkiGetPedkInitialRequest(byte[] info, int[] infoLen, byte[] errMsg, int[] errMsgLen);

    public native int NDK_KmlRkiSetPedkResponse(byte[] data, int length, byte[] errMsg, int[] errMsgLen);

    public native int NDK_KmlRkiGetPedvRequest(byte[] info, int[] infoLen, byte[] errMsg, int[] errMsgLen);

    public native int NDK_KmlRkiSetPedvResponse(byte[] data, int length, byte[] errMsg, int[] errMsgLen);

    public native int NDK_KmlRkiGetInstallKeyNum(int[] out);

    public native int NDK_KmlRkiGetInstalledKeyInfo(int[] len, byte[] keyInfoData);

    public native int NDK_KmlRkiSetDeviceSignCertIndex(byte index);

    public native int NDK_KmlRkiSetDeviceGroup(String name);

    public native int NDK_KmlRkiSetWorkDirectory(String directory);

    public native int NAPI_SecECDHEInit(long[] handle);

    public native int NAPI_SecECDHERelease(long handle);

    public native int NAPI_SecECDHEGenerateKeyPair(long handle, int curveType, byte[] publicKey, int[] outDataLen);

    public native int NAPI_SecECDHEGenSK(long handle, ST_SEC_KEYIN_DATA keyInData, ST_SEC_ECDHE_KDF_INFO hkdfInfo, int publicKeyLen, byte[] publicKey);

    public native void enableNativeLog(boolean isEnable);

    public native int enableNDKLog(int ndkLevel, int sdtpLevel);

    public native int operateLightLT1118(int[] allParams, int length, int length1);

    //GuestDisplayManager
    public native int NDK_ScrBacklight(int status);

    public native int NDK_ScrDispString(int startX, int startY, String displayString, int characterSize);

    public native int NDK_ScrDrawBitmapV(int x, int y, int width, int height, byte[] bitmapData);

    public native int NDK_ScrClrs();

}