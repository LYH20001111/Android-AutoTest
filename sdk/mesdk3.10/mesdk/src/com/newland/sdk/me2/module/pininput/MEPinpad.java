package com.newland.sdk.me2.module.pininput;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.newland.os.NlBuild;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.newland.forth.module.jni.ForthJni;
import com.newland.ndk.NdkApiManager;
import com.newland.ndk.SecN;
import com.newland.ndk.h.EM_SEC_KCV;
import com.newland.ndk.h.EM_SEC_KEY_ALG;
import com.newland.ndk.h.EM_SEC_KEY_TYPE;
import com.newland.ndk.h.ST_SEC_KCV_INFO;
import com.newland.rkl.RKLListener;
import com.newland.sdk.me.module.cardreader.MECardReader;
import com.newland.sdk.me.module.pininput.K21PininutEvent;
import com.newland.sdk.me.module.pininput.K21PininutEvent.PinState;
import com.newland.sdk.me.module.pininput.KeyBoardParams;
import com.newland.sdk.me.module.pininput.PinConfirmType;
import com.newland.sdk.me2.cmd.pininput.CmdCalcMac;
import com.newland.sdk.me2.cmd.pininput.CmdCalcMac.CmdCalcMacResponse;
import com.newland.sdk.me2.cmd.pininput.CmdCalcMac.CmdState;
import com.newland.sdk.me2.cmd.pininput.CmdCheckKeyIsExist;
import com.newland.sdk.me2.cmd.pininput.CmdCheckKeyIsExist.CmdCheckKeyIsExistResponse;
import com.newland.sdk.me2.cmd.pininput.CmdDeleteKey;
import com.newland.sdk.me2.cmd.pininput.CmdEncryptDecrypt;
import com.newland.sdk.me2.cmd.pininput.CmdEncryptDecrypt.CmdEncryptDecryptResponse;
import com.newland.sdk.me2.cmd.pininput.CmdKSNLoad;
import com.newland.sdk.me2.cmd.pininput.CmdKSNLoad.CmdKSNLoadResponse;
import com.newland.sdk.me2.cmd.pininput.CmdLoadMainKeyAndVerify;
import com.newland.sdk.me2.cmd.pininput.CmdLoadMainKeyAndVerify.CmdLoadMainKeyAndVerifyResponse;
import com.newland.sdk.me2.cmd.pininput.CmdLoadWorkingKey;
import com.newland.sdk.me2.cmd.pininput.CmdLoadWorkingKey.CmdLoadWorkingKeyResponse;
import com.newland.sdk.me2.cmd.pininput.CmdRandomKeyboard;
import com.newland.sdk.me2.cmd.pininput.CmdRandomKeyboard.CmdRandomKeyboardResponse;
import com.newland.sdk.me2.cmd.pininput.CmdStartStandardPinInput;
import com.newland.sdk.me2.cmd.pininput.CmdStartStandardPinInput.CmdStartStandardPinInputNotificationResponse;
import com.newland.sdk.me2.cmd.pininput.CmdStartStandardPinInput.CmdStartStandardPinInputResponse;
import com.newland.sdk.me2.cmd.pininput.CmdStartStandardPinInputForSM4;
import com.newland.sdk.module.cardreader.CardType;
import com.newland.sdk.module.pin.AccountInputType;
import com.newland.sdk.module.pin.AlgorithmMode;
import com.newland.sdk.module.pin.CalMacExtParams;
import com.newland.sdk.module.pin.CipherExtParams;
import com.newland.sdk.module.pin.CipherResult;
import com.newland.sdk.module.pin.CipherMode;
import com.newland.sdk.module.pin.KeyManagement;
import com.newland.sdk.module.pin.KeyType;
import com.newland.sdk.module.pin.KeyboardRandom;
import com.newland.sdk.module.pin.LoadDuktpExtParams;
import com.newland.sdk.module.pin.LoadKeyMode;
import com.newland.sdk.module.pin.LoadMKExtParams;
import com.newland.sdk.module.pin.LoadWKExtParams;
import com.newland.sdk.module.pin.LoadWKMode;
import com.newland.sdk.module.pin.MacResult;
import com.newland.sdk.module.pin.PinInputEvent;
import com.newland.sdk.module.pin.PinInputEvent.NotifyStep;
import com.newland.sdk.module.pin.PinInputExtListener;
import com.newland.sdk.module.pin.PinInputExtParams;
import com.newland.sdk.module.pin.PinInputListener;
import com.newland.sdk.module.pin.PinPadButton;
import com.newland.sdk.module.pin.PinpadModule;
import com.newland.sdk.module.pin.RKLParams;
import com.newland.sdk.module.pin.TusnData;
import com.newland.sdk.module.pin.WorkingKeyType;
import com.newland.sdk.module.sm.SmModule;
import com.newland.sdk.mtype.Device;
import com.newland.sdk.mtype.DeviceInvokeException;
import com.newland.sdk.mtype.DeviceRTException;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.mtype.ProcessTimeoutException;
import com.newland.sdk.mtype.common.ErrorCode;
import com.newland.sdk.mtype.event.DeviceEventListener;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtype.util.Dump;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.mtypex.AbstractModule;
import com.newland.sdk.mtypex.cmd.DeviceResponse;
import com.newland.sdk.mtypex.conn.AbortableDeviceCommand;
import com.newland.sdk.utils.ISOUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author youjf
 * @description
 * @date 2019/8/28
 * @since V3.10.01
 */
public class MEPinpad extends AbstractModule implements PinpadModule {
    private DisplayMetrics displayMetrics;
    private DeviceLogger devicelogger = DeviceLoggerFactory.getLogger("MEPinpad");
    private static final int TRANSPORT_DATA_MAX_LENGTH = 1024;
    private Device device;
    private Context context;
    protected AbortableDeviceCommand lastCmd;
    private static final int CANCEL_EVENT_CODE = 0x02;
    private static final int KEYCODE_CANCEL = 0x06;
    private static final int KEYCODE_SWIPCARD = 0x0B;
    private static final int KEYCODE_ICCARD = 0x0C;
    private static final int CALCULATE_MAX_LEN = 1024;


    public MEPinpad(AbstractDevice device, Context context) {
        super(device);
        this.device = device;
        this.context = context;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getRealMetrics(displayMetrics);
    }

    @Override
    public void startRKL(RKLParams params, RKLListener listener) {
        devicelogger.error("me2 startRKL failed");
    }

    @Override
    public boolean loadMasterKey(LoadKeyMode loadKeyMode, AlgorithmMode algorithmMode, int masterKeyIndex, @NonNull byte[] masterKeyData, @Nullable byte[] checkValue, LoadMKExtParams loadMKExtParams) {
        devicelogger.debug("[loadMasterKey] loadKeyMode:"+loadKeyMode+"; algorithmMode:"+algorithmMode+"; masterKeyIndex:"+masterKeyIndex);
        int kekType = 0x02;// mksk ENCRYPT_TMK
        byte[] encryptedMainKey = null;

        switch (loadKeyMode) {
            case DEFAULT_ENCRYPT:
                if (algorithmMode == AlgorithmMode.DES) {
                    kekType = 0x02;
                } else if (algorithmMode == AlgorithmMode.SM4) {
                    kekType = 0x12;
                } else if (algorithmMode == AlgorithmMode.AES) {
                    kekType = 0x20;
                }
                break;
            case PLAIN:
                int keyLength = masterKeyData.length;
                byte[] tekKey = new byte[keyLength];
                Arrays.fill(tekKey, (byte) 0x31);

                if (algorithmMode == AlgorithmMode.DES) {
                    kekType = 0x02;
                    encryptedMainKey = encrype3Des(tekKey, masterKeyData);
                } else if (algorithmMode == AlgorithmMode.SM4) {
                    kekType = 0x12;
                    SmModule smModule = (SmModule) device.getStandardModule(ModuleType.SM);
                    encryptedMainKey = smModule.calcSM4(tekKey, new byte[keyLength], masterKeyData, (byte) 0x00);
                } else if (algorithmMode == AlgorithmMode.AES) {
                    kekType = 0x20;
                }
                break;
            case CUSTOM_ENCRYPT:
                if (algorithmMode == AlgorithmMode.DES) {
                    kekType = 0x04;
                } else if (algorithmMode == AlgorithmMode.SM4) {
                    kekType = 0x14;
                } else if (algorithmMode == AlgorithmMode.AES) {
                    kekType = 0x21;
                }
                break;
        }

        int kekIndex = -1;
        if (loadMKExtParams != null) {
            kekIndex = loadMKExtParams.getKekIndex();
        }
        byte[] finalKcv = null;
        if(checkValue!=null && checkValue.length>=4){
            finalKcv = new byte[4];
            System.arraycopy(checkValue,0,finalKcv,0,4);
        }
        CmdLoadMainKeyAndVerifyResponse cmdLoadMainKeyResponse;
        String answer;

        if (loadKeyMode == LoadKeyMode.PLAIN) {
            if (encryptedMainKey != null && encryptedMainKey.length > 0) {
                //2.0不支持明文装载主密钥,自己用默认传输密钥加密后，密文方式加载
                devicelogger.debug("[loadMasterKey] encryptedMainKey=" + (encryptedMainKey == null ? null : ISOUtils.hexString(encryptedMainKey)));
                cmdLoadMainKeyResponse = (CmdLoadMainKeyAndVerify.CmdLoadMainKeyAndVerifyResponse) invoke(new CmdLoadMainKeyAndVerify(kekType, masterKeyIndex, encryptedMainKey, finalKcv, kekIndex), 5, TimeUnit.SECONDS);
                answer = cmdLoadMainKeyResponse.getAnswerCode();
            } else {
                devicelogger.error("[loadMasterKey] unsupport this methord");
                throw new UnsupportedOperationException("not suppported this method yet!");
            }

        } else {
            cmdLoadMainKeyResponse = (CmdLoadMainKeyAndVerify.CmdLoadMainKeyAndVerifyResponse) invoke(new CmdLoadMainKeyAndVerify(kekType, masterKeyIndex, masterKeyData, finalKcv, kekIndex), 5, TimeUnit.SECONDS);
            answer = cmdLoadMainKeyResponse.getAnswerCode();
        }
        String errorInfo = "Error";
        if (!"00".equals(answer)) {
            if ("41".equals(answer)) {
                errorInfo = "Kcv error";
            } else if ("43".equals(answer)) {
                errorInfo = "Invalid index";
            } else if ("45".equals(answer)) {
                errorInfo = "Mainkey data length error";
            } else if ("46".equals(answer)) {
                errorInfo = "Invalid TR31 format";
            }
            devicelogger.error("[loadMasterKey] load master key failed: AnswerCode = " + answer + ",ERROR = " + errorInfo);
            return false;
        }
        if(finalKcv!=null){
            return true;
        }
        byte[] kcv = cmdLoadMainKeyResponse.getCheckValue();
        if (null != checkValue && null != kcv) {
            int length = (checkValue.length > kcv.length ? kcv.length : checkValue.length);
            byte[] cusKcv = new byte[length];
            System.arraycopy(kcv, 0, cusKcv, 0, length);
            if(length == kcv.length){//上层传的kcv长度大于底层返回的长度
                byte[] externalCheckValue = new byte[length];
                System.arraycopy(checkValue,0,externalCheckValue,0,length);
                return Arrays.equals(externalCheckValue, cusKcv);
            }else{
                return Arrays.equals(checkValue, cusKcv);
            }
        }
        devicelogger.debug("[loadMasterKey] load mk check:" + (kcv == null ? null : ISOUtils.hexString(kcv)));
        return true;
    }

    @Override
    public boolean loadWorkingKey(LoadWKMode loadWKMode, AlgorithmMode algorithmMode, WorkingKeyType workingKeyType, int masterKeyIndex, int workingKeyIndex, @NonNull byte[] data, @Nullable byte[] checkValue, LoadWKExtParams loadWKExtParams) {
        devicelogger.debug("[loadWorkingKey] loadWKMode:"+loadWKMode+"; algorithmMode:"+algorithmMode+"; workingKeyType:"+workingKeyType+"; masterKeyIndex:"+masterKeyIndex+"; workingKeyIndex:"+workingKeyIndex);
        int keyWorkingMode;
        int wkType = 0x01;
        if (loadWKMode == LoadWKMode.ENCRYPT) {
            keyWorkingMode = 0x00;
        } else {
            keyWorkingMode = 0x01;//plain text
        }
        switch (algorithmMode) {
            case DES:
                if (workingKeyType == WorkingKeyType.TRACK) {
                    wkType = 0x01;
                } else if (workingKeyType == WorkingKeyType.MAC) {
                    wkType = 0x03;
                } else if (workingKeyType == WorkingKeyType.PIN) {
                    wkType = 0x02;
                }
                break;
            case SM4:
                if (workingKeyType == WorkingKeyType.TRACK) {
                    wkType = 0x11;
                } else if (workingKeyType == WorkingKeyType.MAC) {
                    wkType = 0x13;
                } else if (workingKeyType == WorkingKeyType.PIN) {
                    wkType = 0x12;
                }
                break;
            case AES:
                if (workingKeyType == WorkingKeyType.TRACK) {
                    wkType = 0x21;
                } else if (workingKeyType == WorkingKeyType.MAC) {
                    wkType = 0x22;
                } else if (workingKeyType == WorkingKeyType.PIN) {
                    wkType = 0x23;
                }
                break;
        }
        if (workingKeyIndex > 255) {
            devicelogger.error("load working key failed: Index greater than 255");
            return false;
        }
        byte[] finalKcv = null;
        if(checkValue!=null && checkValue.length>=4){
            finalKcv = new byte[4];
            System.arraycopy(checkValue,0,finalKcv,0,4);
        }
        CmdLoadWorkingKeyResponse cmdLoadWorkingKeyResponse = (CmdLoadWorkingKeyResponse) invoke(new CmdLoadWorkingKey(keyWorkingMode, wkType, masterKeyIndex, workingKeyIndex, data, finalKcv));
        String answer = cmdLoadWorkingKeyResponse.getAnswerCode();
        String errorInfo = "Error";
        if (!"00".equals(answer)) {
            if ("41".equals(answer)) {
                errorInfo = "Kcv error";
            } else if ("43".equals(answer)) {
                errorInfo = "Invalid index";
            } else if ("45".equals(answer)) {
                errorInfo = "Mainkey data length error";
            } else if ("46".equals(answer)) {
                errorInfo = "Invalid TR31 format";
            } else if ("47".equals(answer)) {
                errorInfo = "Read key records error";
            }
            devicelogger.error("[loadWorkingKey] load working key failed: AnswerCode = " + answer + "，ERROR=" + errorInfo);
            return false;
        }
        if(finalKcv!=null){
            return true;
        }
        byte[] kcv = cmdLoadWorkingKeyResponse.getCheckvalue();
        if (null != checkValue && null != kcv) {
            int length = (checkValue.length > kcv.length ? kcv.length : checkValue.length);
            byte[] cusKcv = new byte[length];
            System.arraycopy(kcv, 0, cusKcv, 0, length);
            if(length == kcv.length){//上层传的kcv长度大于底层返回的长度
                byte[] externalCheckValue = new byte[length];
                System.arraycopy(checkValue,0,externalCheckValue,0,length);
                return Arrays.equals(externalCheckValue, cusKcv);
            }else{
                return Arrays.equals(checkValue, cusKcv);
            }
        }
        return true;
    }

    @Override
    public boolean loadIPEK(LoadKeyMode loadKeyMode, int ipekIndex, @NonNull byte[] ksn, @NonNull byte[] encryptedIPEK,LoadDuktpExtParams loadDuktpExtParams) {
        devicelogger.debug("[loadIPEK] loadKeyMode:"+loadKeyMode+";ipekIndex:"+ipekIndex);
        int kekIndex = -1;
        int keytype;
        if (loadDuktpExtParams != null) {
            kekIndex = loadDuktpExtParams.getKekIndex();
        }
        if (loadKeyMode == LoadKeyMode.DEFAULT_ENCRYPT) {
            keytype = 0x02;
        } else if (loadKeyMode == LoadKeyMode.CUSTOM_ENCRYPT) {
            keytype = 0x04;
        } else {
            keytype = 0x02;
        }
        CmdKSNLoadResponse cmdKSNLoadResponse = (CmdKSNLoadResponse) invoke(new CmdKSNLoad(keytype, ipekIndex, ksn, encryptedIPEK, kekIndex, null));
        if (cmdKSNLoadResponse != null) {
            String answerCode = cmdKSNLoadResponse.getResultCode();
            String errorInfo = "Error";
            if (!"00".equals(answerCode)) {
                if ("41".equals(answerCode)) {
                    errorInfo = "Kcv error";
                } else if ("43".equals(answerCode)) {
                    errorInfo = "Invalid index";
                } else if ("45".equals(answerCode)) {
                    errorInfo = "working key data length error";
                } else if ("46".equals(answerCode)) {
                    errorInfo = "Invalid TR31 format";
                }
                devicelogger.error("[loadIPEK] load loadIPEK failed: AnswerCode = " + answerCode + "，ERROR=" + errorInfo);
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public CipherResult encrypt(KeyManagement keyManagement, AlgorithmMode algorithmMode, CipherMode cipherMode, int keyIndex, @NonNull byte[] inputData, CipherExtParams params) {
        devicelogger.debug("[encrypt] keyManagement:"+keyManagement+"; algorithmMode:"+algorithmMode+";cipherMode:"+cipherMode+"; keyIndex:"+keyIndex);
        byte[] wkData = null;
        byte[] cbcInit = null;
        if (params != null) {
            wkData = params.getWorkingKeyData();
            cbcInit = params.getCbcInit();
        }
        byte modeValue = 0x01; // 0x01 cbc加密 0x02 ecb加密
        byte keyValue = 0x00;
        if (null != cipherMode && cipherMode == CipherMode.ECB) {
            modeValue = 0x02;
        }
        switch (algorithmMode) {
            case DES:
                keyValue = (byte) (modeValue | 0x00);
                if (keyManagement == KeyManagement.DUKPT) {
                    keyValue = (byte) (modeValue | 0x50);
                }
                break;
            case SM4:
                keyValue = (byte) (modeValue | 0x40);
                break;
            default:
                throw new IllegalArgumentException("not support this encryption algorithm!");
        }

        if (algorithmMode == AlgorithmMode.AES || algorithmMode == AlgorithmMode.SM4 && (cipherMode == CipherMode.CBC)) {
            //SM4或者AES算法模式,CBC初始值必须16字节
            if (params != null && params.getCbcInit() != null && params.getCbcInit().length == 16) {
                cbcInit = params.getCbcInit();
            } else if (params != null && params.getCbcInit() != null && params.getCbcInit().length != 16) {
                throw new IllegalArgumentException("[encrypt] SM4/AES CbcInit data length must be 16 !");
            } else {
                cbcInit = new byte[16];
            }
        }

        if (algorithmMode == AlgorithmMode.DES && (cipherMode == CipherMode.CBC)) {
            //DES算法模式,CBC初始值必须8字节
            if (params != null && params.getCbcInit() != null && params.getCbcInit().length == 8) {
                cbcInit = params.getCbcInit();
            } else if (params != null && params.getCbcInit() != null && params.getCbcInit().length != 8) {
                throw new IllegalArgumentException("[encrypt] DES cbcInit data length must be 8 !");
            } else {
                cbcInit = new byte[8];
            }
        }
        if(cipherMode == CipherMode.ECB){
            int elementLen = 16;
            if(algorithmMode == AlgorithmMode.DES){
                elementLen = 8;
            }
            int destLen = (inputData.length + elementLen - 1) / elementLen * elementLen;
             int maxLen = CALCULATE_MAX_LEN;
            CipherResult result=null;
            int count = destLen / maxLen,remainder = destLen % maxLen;
            if((count == 1 && remainder != 0) || (count > 1)){
                byte[] srcData = new byte[destLen];
                Arrays.fill(srcData, (byte) 0x00);
                System.arraycopy(inputData,0,srcData,0,inputData.length);
                byte[] destData = new byte[destLen];
                byte[] srcItem = new byte[maxLen];
                for (int i = 0; i < count; i++) {
                    System.arraycopy(srcData,i*maxLen,srcItem,0,maxLen);
                    result = encrypt(keyManagement,algorithmMode,cipherMode,keyIndex,srcItem,params);
                    System.arraycopy(result.getData(),0,destData,i*maxLen,maxLen);
                }
                if(remainder!=0){
                    byte[] srcRemData = new byte[remainder];
                    System.arraycopy(srcData,count*maxLen,srcRemData,0,remainder);
                    result = encrypt(keyManagement,algorithmMode,cipherMode,keyIndex,srcRemData,params);
                    System.arraycopy(result.getData(),0,destData,count*maxLen,remainder);
                }
                if(result==null){
                    return null;
                }
                return new CipherResult(destData,result.getKsn());
            }
        }else if(cipherMode == CipherMode.CBC){
            int elementLen = 16,maxLen = CALCULATE_MAX_LEN;
            if(algorithmMode == AlgorithmMode.DES){
                elementLen = 8;
            }
            int destLen = (inputData.length + elementLen - 1) / elementLen * elementLen;
            int count = destLen / maxLen,remainder = destLen % maxLen;
            if((count == 1 && remainder != 0) || (count > 1)){
                CipherExtParams tempExtParams = new CipherExtParams();
                if(params != null){
                    tempExtParams.setCbcInit(params.getCbcInit());
                    tempExtParams.setWorkingKeyData(params.getWorkingKeyData());
                }
                CipherResult result = null;
                byte[] srcData = new byte[destLen];
                Arrays.fill(srcData, (byte) 0x00);
                System.arraycopy(inputData,0,srcData,0,inputData.length);
                byte[] destData = new byte[destLen];
                byte[] srcItem = new byte[maxLen];
                for (int i = 0; i < count; i++) {
                    System.arraycopy(srcData,i*maxLen,srcItem,0,maxLen);
                    result = encrypt(keyManagement,algorithmMode,cipherMode,keyIndex,srcItem,tempExtParams);
                    System.arraycopy(result.getData(),0,destData,i*maxLen,maxLen);

                    byte[] cbcIv = new byte[elementLen];
                    System.arraycopy(result.getData(),result.getData().length-elementLen,cbcIv,0,elementLen);
                    tempExtParams.setCbcInit(cbcIv);
                }
                if(remainder!=0){
                    byte[] srcRemData = new byte[remainder];
                    System.arraycopy(srcData,count*maxLen,srcRemData,0,remainder);
                    result = encrypt(keyManagement,algorithmMode,cipherMode,keyIndex,srcRemData,tempExtParams);
                    System.arraycopy(result.getData(),0,destData,count*maxLen,remainder);
                }
                if(result==null){
                    return null;
                }
                return new CipherResult(destData,result.getKsn());
            }
        }
        CmdEncryptDecryptResponse cmdEncryptDecryptResponse = (CmdEncryptDecryptResponse) invoke(new CmdEncryptDecrypt(keyIndex, wkData, keyValue, inputData, cbcInit));
        String answer = cmdEncryptDecryptResponse.getAnswerCode();
        String errorInfo = "Error";
        if (!"00".equals(answer)) {
            if ("41".equals(answer)) {
                errorInfo = "Kcv error";
            } else if ("43".equals(answer)) {
                errorInfo = "Invalid index";
            } else if ("45".equals(answer)) {
                errorInfo = "Mainkey data length error";
            } else if ("46".equals(answer)) {
                errorInfo = "Invalid TR31 format";
            }
            throw new DeviceRTException(ErrorCode.DEVICE_INVOKE_FAILED, "encrypt failed: AnswerCode = " + answer + "ERROR = " + errorInfo);
        }
        return new CipherResult(cmdEncryptDecryptResponse.getEncryptedPassword(), "");
    }

    @Override
    public CipherResult decrypt(KeyManagement keyManagement, AlgorithmMode algorithmMode, CipherMode cipherMode, int keyIndex, @NonNull byte[] inputData, CipherExtParams params) {
        devicelogger.debug("[decrypt] keyManagement:"+keyManagement+"; algorithmMode:"+algorithmMode+"; cipherMode:"+cipherMode+"; keyIndex:"+keyIndex);
        byte[] wkData = null;
        byte[] cbcInit = null;
        if (params != null) {
            wkData = params.getWorkingKeyData();
            cbcInit = params.getCbcInit();
        }
        byte modeValue = 0x03; // 0x03 cbc解密 0x04 ecb解密
        byte keyValue = 0x00;
        if (null != cipherMode && cipherMode == CipherMode.ECB) {
            modeValue = 0x04;
        }
        switch (algorithmMode) {
            case DES:
                keyValue = (byte) (modeValue | 0x00);
                if (keyManagement == KeyManagement.DUKPT) {
                    keyValue = (byte) (modeValue | 0x50);
                }
                break;
            case SM4:
                keyValue = (byte) (modeValue | 0x40);
                break;
            default:
                throw new IllegalArgumentException("not support this encryption algorithm!");
        }
        if (algorithmMode == AlgorithmMode.AES || algorithmMode == AlgorithmMode.SM4 && (cipherMode == CipherMode.CBC)) {
            //SM4或者AES算法模式,CBC初始值必须16字节
            if (params != null && params.getCbcInit() != null && params.getCbcInit().length == 16) {
                cbcInit = params.getCbcInit();
            } else if (params != null && params.getCbcInit() != null && params.getCbcInit().length != 16) {
                throw new IllegalArgumentException("[decrypt] SM4/AES CbcInit data length must be 16 !");
            } else {
                cbcInit = new byte[16];
            }
        }

        if (algorithmMode == AlgorithmMode.DES && (cipherMode == CipherMode.CBC)) {
            //DES算法模式,CBC初始值必须8字节
            if (params != null && params.getCbcInit() != null && params.getCbcInit().length == 8) {
                cbcInit = params.getCbcInit();
            } else if (params != null && params.getCbcInit() != null && params.getCbcInit().length != 8) {
                throw new IllegalArgumentException("[decrypt] DES cbcInit data length must be 8 !");
            } else {
                cbcInit = new byte[8];
            }
        }
        if(cipherMode == CipherMode.ECB){
            int elementLen = 16,maxLen = CALCULATE_MAX_LEN;
            if(algorithmMode == AlgorithmMode.DES){
                elementLen = 8;
            }
            int destLen = inputData.length;
            int count = destLen / maxLen,remainder = destLen % maxLen;
            if(destLen % elementLen != 0){
                return null;
            }
            CipherResult result = null;
            if((count == 1 && remainder != 0) || (count > 1)){
                byte[] srcData = inputData;
                byte[] destData = new byte[destLen];
                byte[] srcItem = new byte[maxLen];
                for (int i = 0; i < count; i++) {
                    System.arraycopy(srcData,i*maxLen,srcItem,0,maxLen);
                    result = decrypt(keyManagement,algorithmMode,cipherMode,keyIndex,srcItem,params);
                    System.arraycopy(result.getData(),0,destData,i*maxLen,maxLen);
                }
                if(remainder!=0){
                    byte[] srcRemData = new byte[remainder];
                    System.arraycopy(srcData,count*maxLen,srcRemData,0,remainder);
                    result = decrypt(keyManagement,algorithmMode,cipherMode,keyIndex,srcRemData,params);
                    System.arraycopy(result.getData(),0,destData,count*maxLen,remainder);
                }
                if(result==null){
                    return null;
                }
                return new CipherResult(destData,result.getKsn());
            }
        }else if(cipherMode == CipherMode.CBC){
            int elementLen = 16,maxLen = CALCULATE_MAX_LEN;
            if(algorithmMode == AlgorithmMode.DES){
                elementLen = 8;
            }
            int destLen = inputData.length;
            int count = destLen / maxLen,remainder = destLen % maxLen;
            if(destLen % elementLen != 0){
                return null;
            }
            CipherResult result = null;
            if((count == 1 && remainder != 0) || (count > 1)){
                CipherExtParams tempExtParams = new CipherExtParams();
                if(params != null){
                    tempExtParams.setCbcInit(params.getCbcInit());
                    tempExtParams.setWorkingKeyData(params.getWorkingKeyData());
                }
                byte[] srcData = inputData;
                byte[] destData = new byte[destLen];
                byte[] srcItem = new byte[maxLen];
                for (int i = 0; i < count; i++) {
                    System.arraycopy(srcData,i*maxLen,srcItem,0,maxLen);
                    result = decrypt(keyManagement,algorithmMode,cipherMode,keyIndex,srcItem,tempExtParams);
                    System.arraycopy(result.getData(),0,destData,i*maxLen,maxLen);

                    byte[] cbcIv = new byte[elementLen];
                    System.arraycopy(srcData,(i+1)*maxLen-elementLen,cbcIv,0,elementLen);
                    tempExtParams.setCbcInit(cbcIv);
                }
                if(remainder!=0){
                    byte[] srcRemData = new byte[remainder];
                    System.arraycopy(srcData,count*maxLen,srcRemData,0,remainder);
                    result = decrypt(keyManagement,algorithmMode,cipherMode,keyIndex,srcRemData,tempExtParams);
                    System.arraycopy(result.getData(),0,destData,count*maxLen,remainder);
                }
                if(result==null){
                    return null;
                }
                return new CipherResult(destData,result.getKsn());
            }
        }

        CmdEncryptDecryptResponse cmdEncryptDecryptResponse = (CmdEncryptDecryptResponse) invoke(new CmdEncryptDecrypt(keyIndex, wkData, keyValue, inputData, cbcInit));
        String answer = cmdEncryptDecryptResponse.getAnswerCode();
        String errorInfo = "Error";
        if (!"00".equals(answer)) {
            if ("41".equals(answer)) {
                errorInfo = "Kcv error";
            } else if ("43".equals(answer)) {
                errorInfo = "Invalid index";
            } else if ("45".equals(answer)) {
                errorInfo = "Mainkey data length error";
            } else if ("46".equals(answer)) {
                errorInfo = "Invalid TR31 format";
            }
            throw new DeviceRTException(ErrorCode.DEVICE_INVOKE_FAILED, "encrypt failed: AnswerCode = " + answer + "ERROR = " + errorInfo);
        }
        return new CipherResult(cmdEncryptDecryptResponse.getEncryptedPassword(), "");
    }

    @Override
    public MacResult calcMac(KeyManagement keyManagement, int macAlgorithm, int keyIndex, @NonNull byte[] input, CalMacExtParams calMacExtParams) {
        devicelogger.debug("[keyManagement] keyManagement:"+keyManagement+";macAlgorithm:"+macAlgorithm+";keyIndex:"+keyIndex);
        byte[] randomIndex = null;
        byte[] wkData = null;
        if (calMacExtParams != null) {
            randomIndex = calMacExtParams.getRandomIndex();
            wkData = calMacExtParams.getWorkingKeyData();
        }
        int manageType = 0x00;//mksk
        if (keyManagement == KeyManagement.DUKPT) {
            manageType = 0x01;
        }
        if (null == input || input.length <= TRANSPORT_DATA_MAX_LENGTH) {
            CmdCalcMacResponse cmdCalcMacResponse = (CmdCalcMacResponse) invoke(new CmdCalcMac(CmdCalcMac.CmdState.ONLY_BLOCK, macAlgorithm, manageType, keyIndex, wkData, input, randomIndex));
            return cmdCalcMacResponse.getMacResult();
        }
        boolean first = true;
        byte[] remainBuffer = input;
        CmdCalcMacResponse response = null;
        do {
            CmdCalcMac cmd = null;
            if (first) {
                byte[] buffer = new byte[TRANSPORT_DATA_MAX_LENGTH];
                System.arraycopy(remainBuffer, 0, buffer, 0, buffer.length);
                byte[] tempRemainBuffer = new byte[remainBuffer.length - buffer.length];
                System.arraycopy(remainBuffer, buffer.length, tempRemainBuffer, 0, tempRemainBuffer.length);
                remainBuffer = tempRemainBuffer;
                cmd = new CmdCalcMac(CmdState.FIRST_BLOCK, macAlgorithm, manageType, keyIndex, wkData, buffer, randomIndex);
                first = false;
            } else {
                if (remainBuffer.length <= TRANSPORT_DATA_MAX_LENGTH) {
                    cmd = new CmdCalcMac(CmdState.LAST_BLOCK, macAlgorithm, manageType, keyIndex, wkData, remainBuffer, randomIndex);
                    remainBuffer = new byte[0];
                } else {
                    byte[] buffer = new byte[TRANSPORT_DATA_MAX_LENGTH];
                    System.arraycopy(remainBuffer, 0, buffer, 0, buffer.length);
                    byte[] tempRemainBuffer = new byte[remainBuffer.length - buffer.length];
                    System.arraycopy(remainBuffer, buffer.length, tempRemainBuffer, 0, tempRemainBuffer.length);
                    remainBuffer = tempRemainBuffer;
                    cmd = new CmdCalcMac(CmdState.NEXT_BLOCK, macAlgorithm, manageType, keyIndex, wkData, buffer, randomIndex);
                }
            }
            response = (CmdCalcMacResponse) invoke(cmd);
        } while (remainBuffer.length > 0);
        return response.getMacResult();

    }

    @Override
    public boolean ksnIncrease(int dukptKeyIndex) {
        return false;
    }

    @Override
    public boolean ksnAESIncrease(int dukptKeyIndex) {
        return false;
    }

    @Override
    public byte[] getDukptKsn(int dukptKeyIndex) {
        throw new UnsupportedOperationException("not supported this method!");
    }

    @Override
    public byte[] getDukptAESKsn(int dukptKeyIndex) {
        return null;
    }

    @Override
    public boolean checkKeyIsExist(KeyType keyType, AlgorithmMode algorithmMode, int keyIndex, @Nullable byte[] checkValue) {
        devicelogger.debug("[checkKeyIsExist] keyType:"+keyType+";algorithmMode:"+algorithmMode+";keyIndex:"+keyIndex);
        int type = 0x00;
        switch (keyType) {
            case TRANSPORT_KEY:
                if (algorithmMode == AlgorithmMode.DES) {
                    type = 0x00;
                } else if (algorithmMode == AlgorithmMode.SM4) {
                    type = 0x10;
                } else {
                    type = 0x20;
                }
                break;
            case MASTER_KEY:
                if (algorithmMode == AlgorithmMode.DES) {
                    type = 0x01;
                } else if (algorithmMode == AlgorithmMode.SM4) {
                    type = 0x11;
                } else {
                    type = 0x21;
                }
                break;
            case PIN_KEY:
                if (algorithmMode == AlgorithmMode.DES) {
                    type = 0x02;
                } else if (algorithmMode == AlgorithmMode.SM4) {
                    type = 0x12;
                } else {
                    type = 0x22;
                }
                break;
            case MAC_KEY:
                if (algorithmMode == AlgorithmMode.DES) {
                    type = 0x03;
                } else if (algorithmMode == AlgorithmMode.SM4) {
                    type = 0x13;
                } else {
                    type = 0x23;
                }
                break;
            case TRACK_KEY:
                if (algorithmMode == AlgorithmMode.DES) {
                    type = 0x04;
                } else if (algorithmMode == AlgorithmMode.SM4) {
                    type = 0x14;
                } else {
                    type = 0x24;
                }
                break;

        }
        byte[] finalKcv = null;
        if(checkValue!=null && checkValue.length>=4){
            finalKcv = new byte[4];
            System.arraycopy(checkValue,0,finalKcv,0,4);
        }
        CmdCheckKeyIsExistResponse response = (CmdCheckKeyIsExistResponse) invoke(new CmdCheckKeyIsExist(type, keyIndex, finalKcv));
        if (response.getIsExist() == 0) {
            if(finalKcv!=null){
                return true;
            }
            byte[] kcv = response.getCheckValue();
            if (null != checkValue && null != kcv) {
                int length = (checkValue.length > kcv.length ? kcv.length : checkValue.length);
                byte[] cusKcv = new byte[length];
                System.arraycopy(kcv, 0, cusKcv, 0, length);
                return Arrays.equals(checkValue, cusKcv);
            }
            return true;
        } else if (response.getIsExist() == 1) {
            return false;
        }
        return false;
    }

    @Override
    public boolean deleteKey(KeyType keyType, AlgorithmMode algorithmMode, int keyIndex) {
        devicelogger.debug("[deleteKey] keyType:"+keyType+";algorithmMode:"+algorithmMode+";keyIndex:"+keyIndex);
        int workingKeyType = 0x00;
        switch (keyType) {
            case MASTER_KEY:
                if (algorithmMode == AlgorithmMode.DES) {
                    try {
                        invoke(CmdDeleteKey.deleteMainKey(keyIndex));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        return false;
                    }
                    return true;
                } else if (algorithmMode == AlgorithmMode.SM4) {
                    devicelogger.error("[deleteKey] delete mainkey unsupport SM4 algorithmMode");
                    return false;
                } else {//AES
                    devicelogger.error("[deleteKey] delete mainkey unsupport AES algorithmMode");
                    return false;
                }
            case MAC_KEY:
                if (algorithmMode == AlgorithmMode.DES) {
                    workingKeyType = 0x03;
                } else if (algorithmMode == AlgorithmMode.SM4) {
                    workingKeyType = 0x13;
                } else {
                    devicelogger.error("[deleteKey] delete MAC key unsupport AES algorithmMode");
                    return false;
                }
                break;
            case TRACK_KEY:
                if (algorithmMode == AlgorithmMode.DES) {
                    workingKeyType = 0x01;
                } else if (algorithmMode == AlgorithmMode.SM4) {
                    workingKeyType = 0x11;
                } else {
                    devicelogger.error("[deleteKey] delete TRACK key unsupport AES algorithmMode");
                    return false;
                }

                break;

            case PIN_KEY:
                if (algorithmMode == AlgorithmMode.DES) {
                    workingKeyType = 0x02;
                } else if (algorithmMode == AlgorithmMode.DES) {
                    workingKeyType = 0x12;
                } else {
                    devicelogger.error("[deleteKey] delete PIN key unsupport AES algorithmMode");
                    return false;
                }
                break;
        }

        try {
            invoke(CmdDeleteKey.deleteWorkingKey(workingKeyType, keyIndex));
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteAllKeys() {
        try {
            devicelogger.debug("[deleteAllKeys]");
            invoke(CmdDeleteKey.deleteAllKey());
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public byte[] loadRandomKeyboard(KeyboardRandom keyboardRandom) {
        devicelogger.debug("[loadRandomKeyboard]");
        Point point = new Point();
        String size = getProperties("ro.boot.lcd_size");

        if (NlBuild.VERSION.MODEL.equals("N900")) {
            point = new Point(540, 960);
        } else {
            try {
                String TOUCHSCREEN_RESOLUTION = NlBuild.VERSION.TOUCHSCREEN_RESOLUTION; // 900设备不是所有固件支持此方法
                int height = Integer.valueOf(TOUCHSCREEN_RESOLUTION.split("x")[0]); // 获取K21端的触屏分辨率
                // 然后做等比例缩放
                int width = Integer.valueOf(TOUCHSCREEN_RESOLUTION.split("x")[1]);
                point = new Point(width, height);
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
        int[] coordinateInt = recover(keyboardRandom.getCoordinate());
        // 等比缩放后 下发给底层
        for (int i = 0; i < coordinateInt.length; i++) {
            if (i % 2 == 0) {
                coordinateInt[i] = coordinateInt[i] * point.x / displayMetrics.widthPixels;
            } else {
                coordinateInt[i] = coordinateInt[i] * point.y / displayMetrics.heightPixels;
            }
        }
        // 初始坐标集合
        byte[] initCoordinate = new byte[coordinateInt.length * 2];
        for (int i = 0, j = 0; i < coordinateInt.length; i++, j++) {
            initCoordinate[j] = (byte) ((coordinateInt[i] >> 8) & 0xff);
            j++;
            initCoordinate[j] = (byte) (coordinateInt[i] & 0xff);
        }
        keyboardRandom.setCoordinate(initCoordinate);
        CmdRandomKeyboardResponse response = (CmdRandomKeyboardResponse) invoke(new CmdRandomKeyboard(keyboardRandom));
        return response.getKeyCodes();
    }

    @Override
    public boolean loadRNIBKeyboard(int keyNum, Map<PinPadButton, int[]> pinPadButtons, int[] touchCoordinates, int[] KeyboradCoordinates){
        return false;
    }

    @Override
    public TusnData getTusnData(String random) {
        devicelogger.debug("[getTusnData] random:"+random);
        /** a)由[硬件序列号+加密随机因子]构成MAC ELEMEMENT BLOCK （MAB）。 硬件序列号20+加密随机因子6 */
        /** b)按每16个字节做异或，如果最后不满16个字节，则添加"0X00" */
        /** c)将异或运算后的最后16个字节（RESULT BLOCK）转换成32 个HEXDECIMAL */
        /** d)取前16 个字节用SM4加密 */
        /** e)将加密后的结果与后16个字节异或 */
        /** f)用异或的结果TEMP BLOCK 再进行一次SM4密钥算法运算 */
        /** g)将运算后的结果（ENC BLOCK2）转换成32 个HEXDECIMAL */
        /** h) 取前8个字节作为硬件序列号加密数据 */
        NdkApiManager ndkApiManager = NdkApiManager.getNdkApiManager();
        SecN secNDK = ndkApiManager.getSecN();
        TusnData tusnData = null;
        String deviceType = "04";// 01 ATM, 02 传统POS, 03 MPOS, 04 智能POS   09人脸设备
        if (isSupFaceRecognition()) {
            deviceType = "09";
        }
        String serialNo = "000003";// 新大陆厂商序号 000003
        // String tusn;
        // version>1.0.4
        // ndk, return tusn , -19 判定标志是否存在，存在则返回20，否则12 sn
        // -20 返回12sn

        // version<1.0.4
        // File file=new File("/newland/factory/flag_sn_20");
        // if(!file.exists()){
        // tusnData = new TusnData(true, deviceType, sn, null);
        // return tusnData;
        // }
        // exist:20, no :12

        String sn = device.getDeviceInfo().getSN();
        String tusn = serialNo + deviceType + sn;
        String finalSN = "";
        try {
            // 人行二次改造，判定文件存在则进行下一步验证，否则返回不支持
            File file = new File("/newland/factory/flag_sn_20");
            if (!file.exists()) {
                devicelogger.debug("[getTusnData] 文件不存在");
                tusnData = new TusnData(deviceType, sn, null);
                finalSN = sn;
                // return tusnData;
            } else {
                devicelogger.debug("[getTusnData] 文件存在");
                finalSN = tusn;
            }

            // 判断固件是否支持密钥分区以及是否有21号文密钥存在
            int setOwnerRslt = secNDK.NDK_SecSetKeyOwner("_NL_TERM_MGR"); // 人行21号表切换
            if (setOwnerRslt != 0) {
                devicelogger.debug("[getTusnData] 切换人行21号表失败，固件不支持:" + setOwnerRslt);
                tusnData = new TusnData(deviceType, finalSN, null);
                return tusnData;
            }

            int SEC_KEY_SM4 = (1 << 6);
            int SEC_KEY_TYPE_TDK = 4;
            ST_SEC_KCV_INFO kcv1 = new ST_SEC_KCV_INFO();
            kcv1.nCheckMode = 0;
            int ndkRslt = secNDK.NDK_SecGetKcv((byte) (SEC_KEY_TYPE_TDK | SEC_KEY_SM4), (byte) 255, kcv1);
            byte[] kcv = new byte[4];
            System.arraycopy(kcv1.sCheckBuf, 0, kcv, 0, 4);
            if (ndkRslt != 0) {
                devicelogger.error("[getTusnData] 不存在密钥，未灌装密钥:" + ndkRslt);
                tusnData = new TusnData(deviceType, finalSN, null);
                return tusnData;
            }
            byte[] mab = new byte[32];
            byte[] resultBlock = new byte[16];
            // a)
            String szNLSerialNo = finalSN + random;
            byte[] bData = szNLSerialNo.getBytes("GBK");
            System.arraycopy(bData, 0, mab, 0, bData.length);
            devicelogger.debug("[getTusnData] a)由[硬件序列号+加密随机因子]构成MAC ELEMEMENT BLOCK（MAB）硬件序列号20+加密随机因子6:" + ISOUtils.hexString(mab));
            // b)
            for (int i = 0; i < 16; i++) {
                resultBlock[i] = (byte) (mab[i] ^ mab[i + 16]);
            }
            devicelogger.debug("[getTusnData] b)按每16个字节做异或，如果最后不满16个字节，则添加0X00:" + ISOUtils.hexString(resultBlock));
            // c)
            byte[] szHexDecimal = ISOUtils.hexString(resultBlock).getBytes();
            devicelogger.debug("[getTusnData] c)将异或运算后的最后16个字节（RESULT BLOCK）转换成32 个HEXDECIMAL:" + ISOUtils.hexString(szHexDecimal));
            secNDK.NDK_SecSetKeyOwner("_NL_TERM_MGR"); // 人行21号独立密钥区
            byte ucKeyType = (byte) (4 | (1 << 6)); // track和sm4或运算
            byte index = (byte) 255; // 索引
            byte[] output = new byte[16];
            // d)
            byte[] firstPart = new byte[16];
            System.arraycopy(szHexDecimal, 0, firstPart, 0, firstPart.length);
            int resultCode = secNDK.NDK_SecCalcDes(ucKeyType, index, firstPart, 16, output, (byte) (1 << 4));
            if (resultCode == 0) {
                devicelogger.debug("[getTusnData] d)取前16 个字节用SM4加密:" + ISOUtils.hexString(output));
                Arrays.fill(resultBlock, (byte) 0x00);
                for (int i = 0; i < 16; i++) {
                    // e)
                    resultBlock[i] = (byte) (output[i] ^ szHexDecimal[i + 16]);
                }
                devicelogger.debug("[getTusnData] e)将加密后的结果与后16个字节异或:" + ISOUtils.hexString(resultBlock));
                Arrays.fill(output, (byte) 0x00);
                // f)
                int resultCode2 = secNDK.NDK_SecCalcDes(ucKeyType, index, resultBlock, 16, output, (byte) (1 << 4));
                if (resultCode2 == 0) {
                    devicelogger.debug("[getTusnData] f)用异或的结果TEMP BLOCK 再进行一次SM4密钥算法运算:" + ISOUtils.hexString(output));
                    // g)、h)
                    String result = ISOUtils.hexString(output).substring(0, 8);
                    tusnData = new TusnData(deviceType, finalSN, result);
                    return tusnData;
                }
            }
            devicelogger.error("[getTusnData] 人行密钥存在但加密失败!");
            tusnData = new TusnData(deviceType, finalSN, null);
            return tusnData;
        } catch (Exception ex) {
            ex.printStackTrace();
            tusnData = new TusnData(deviceType, finalSN, null);
            return tusnData;
        } finally {
            devicelogger.debug("[getTusnData] ####还原共享密钥区");
            secNDK.NDK_SecSetKeyOwner("*"); // 还原共享密钥区
        }
    }

    @Override
    public void startPinInput(KeyManagement keyManagement, AlgorithmMode algorithmMode, int keyIndex, String pan, int timeout, @NonNull final PinInputListener pinInputListener, PinInputExtParams pinInputExtParams) {
        try {
            devicelogger.debug("[startPinInput] keyManagement:"+keyManagement+";algorithmMode:"+algorithmMode+";keyIndex:"+keyIndex+";pan:"+pan+";timeout:"+timeout);
            int pinkeyManagement = 0x00;

            switch (algorithmMode) {
                case DES:
                    pinkeyManagement = 0x00;
                    break;
                case SM4:
                    pinkeyManagement = 0x07;
                    break;
                case AES:
                    pinkeyManagement = 0x08;
                    break;
            }
            if (keyManagement == KeyManagement.DUKPT) {
                pinkeyManagement = 0x01;
            }
            int invokeTimeout = timeout + 3;
            byte[] wkData = null;
            AccountInputType acctInputType = AccountInputType.USE_ACCOUNT;
            int inputMaxLen = 12;
            PinConfirmType pinConfirmType = PinConfirmType.ENABLE_ENTER;
            byte[] pwdLengthRange = null;
            if (pinInputExtParams != null) {
                if (pan == null || pan.length() <= 0) {
                    acctInputType = AccountInputType.UNUSE_ACCOUNT;
                } else if (pinInputExtParams.getAcctInputType() != null) {
                    acctInputType = pinInputExtParams.getAcctInputType();
                }

                if (pinInputExtParams.getInputMaxLen() > 0) {
                    inputMaxLen = pinInputExtParams.getInputMaxLen();
                }
                if (pinInputExtParams.getPwdLengthRange() != null) {
                    pwdLengthRange = pinInputExtParams.getPwdLengthRange();
                }

                if (pinInputExtParams.getDefaultLayout() != null) {
                    KeyBoardParams.setKeyManagement(keyManagement);
                    KeyBoardParams.setAlgorithmMode(algorithmMode);
                    KeyBoardParams.setKeyIndex(keyIndex);
                    KeyBoardParams.setPan(pan);
                    KeyBoardParams.setTimeout(timeout);
                    KeyBoardParams.setPinInputListener(pinInputListener);
                    KeyBoardParams.setPinInputExtParams(pinInputExtParams);
                    KeyBoardParams.setPinpadModule(this);
                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClassName(context, "com.newland.sdk.pininput.KeyBoardActivity");
                    context.startActivity(intent);
                    return;
                }
            }
            if (algorithmMode == AlgorithmMode.SM4) {
                CmdStartStandardPinInputForSM4 cmd = new CmdStartStandardPinInputForSM4(keyIndex, wkData, pinkeyManagement, acctInputType, pan, inputMaxLen, null, pinConfirmType, "", timeout, pwdLengthRange, -1);
                invoke(cmd, (long) invokeTimeout, TimeUnit.SECONDS, new DeviceEventListener<K21PininutEvent>() {
                    @Override
                    public void onEvent(K21PininutEvent event, Handler handler) {
                        if (event.isProcessing()) {
                            PinInputEvent.NotifyStep notifyStep = event.getNotifyStep();
                            if (notifyStep == PinInputEvent.NotifyStep.ENTER) {
                                pinInputListener.onKeyPress();
                            } else if (notifyStep == PinInputEvent.NotifyStep.BACKSPACE) {
                                pinInputListener.onBackspace();
                            }else if(notifyStep == NotifyStep.CLEAR){
                                if(pinInputListener instanceof PinInputExtListener){
                                    ((PinInputExtListener)pinInputListener).onNotifyStep((byte)0x0f);
                                }
                            }
                        } else if (event.isUserCanceled()) {
                            pinInputListener.onCancel();
                        } else if (event.isSuccess()) {
                            if (event.getInputLen() == 0) {
                                pinInputListener.onFinish(0, new byte[]{},null);
                            } else {
                                pinInputListener.onFinish(event.getEncrypPin().length, event.getEncrypPin(),event.getKsn());
                            }
                        } else {
                            if (event.getException().getCause() instanceof ProcessTimeoutException) {
                                pinInputListener.onTimeout();
                                return;
                            }
                            pinInputListener.onError(ErrorCode.INPUT_PIN_FAILED, event.getException().getMessage());
                        }
                    }

                    @Override
                    public Handler getUIHandler() {
                        return null;
                    }
                }, new EventMaker<K21PininutEvent>() {
                    @Override
                    public K21PininutEvent makeEvent(DeviceResponse deviceResponse) {
                        K21PininutEvent event = null;
                        try {
                            DeviceResponse response = dealDevResp(deviceResponse);
                            if (response == null) {
                                event = new K21PininutEvent();
                            } else if (response instanceof CmdStartStandardPinInputForSM4.CmdStartStandardPinInputForSM4NotificationResponse) {
                                CmdStartStandardPinInputForSM4.CmdStartStandardPinInputForSM4NotificationResponse notificationResponse = (CmdStartStandardPinInputForSM4.CmdStartStandardPinInputForSM4NotificationResponse) response;
                                if (notificationResponse.getReturnKey() == 0x0d) {
                                    event = new K21PininutEvent(NotifyStep.ENTER);
                                } else if (notificationResponse.getReturnKey() == 0x0a) {
                                    event = new K21PininutEvent(NotifyStep.BACKSPACE);
                                } else if (notificationResponse.getReturnKey() == 0x0f) {// 清空事件
                                    event = new K21PininutEvent(NotifyStep.CLEAR);
                                } else {
                                    Exception e = new DeviceInvokeException("unknown notification type!" + Dump.getHexDump(new byte[]{(byte) notificationResponse.getReturnKey()}));
                                    event = new K21PininutEvent(e);
                                }
                            } else {
                                CmdStartStandardPinInputForSM4.CmdStartStandardPinInputForSM4Response cmdResponse = (CmdStartStandardPinInputForSM4.CmdStartStandardPinInputForSM4Response) response;
                                if (KEYCODE_CANCEL == cmdResponse.getReturnKey()) {
                                    devicelogger.debug("[startPinInput] user cancel input:return code:" + cmdResponse.getReturnKey());
                                    event = new K21PininutEvent();
                                } else if (KEYCODE_SWIPCARD == cmdResponse.getReturnKey()) {
                                    event = new K21PininutEvent(PinState.SWIPCARD, -1, null, null);
                                } else if (KEYCODE_ICCARD == cmdResponse.getReturnKey()) {
                                    event = new K21PininutEvent(PinState.ICCARD, -1, null, null);
                                    MECardReader cardreader = (MECardReader) getOwner().getStandardModule(ModuleType.COMMON_CARDREADER);
                                    cardreader.setLastReaderTypes(new CardType[]{CardType.ICCARD});
                                } else {
                                    event = new K21PininutEvent(cmdResponse.getCyherLength(), cmdResponse.getEncryptPinBlock(), cmdResponse.getKsn());
                                }
                            }
                        } catch (Exception e) {
                            event = new K21PininutEvent(e);
                        }
                        return event;
                    }

                });
                lastCmd = cmd;
            } else {
                CmdStartStandardPinInput cmd = new CmdStartStandardPinInput(keyIndex, wkData, pinkeyManagement, acctInputType, pan, inputMaxLen, null, pinConfirmType, "", timeout, pwdLengthRange, -1);
                invoke(cmd, (long) invokeTimeout, TimeUnit.SECONDS, new DeviceEventListener<K21PininutEvent>() {
                    @Override
                    public void onEvent(K21PininutEvent event, Handler handler) {
                        if (event.isProcessing()) {
                            PinInputEvent.NotifyStep notifyStep = event.getNotifyStep();
                            if (notifyStep == PinInputEvent.NotifyStep.ENTER) {
                                pinInputListener.onKeyPress();
                            } else if (notifyStep == PinInputEvent.NotifyStep.BACKSPACE) {
                                pinInputListener.onBackspace();
                            }else if(notifyStep == NotifyStep.CLEAR){
                                if(pinInputListener instanceof PinInputExtListener){
                                    ((PinInputExtListener)pinInputListener).onNotifyStep((byte)0x0f);
                                }
                            }
                        } else if (event.isUserCanceled()) {
                            pinInputListener.onCancel();
                        } else if (event.isSuccess()) {
                            if (event.getInputLen() == 0) {
                                pinInputListener.onFinish(0, new byte[]{},null);
                            } else {
                                pinInputListener.onFinish(event.getEncrypPin().length, event.getEncrypPin(),event.getKsn());
                            }
                        } else {
                            if (event.getException().getCause() instanceof ProcessTimeoutException) {
                                pinInputListener.onTimeout();
                                return;
                            }
                            pinInputListener.onError(ErrorCode.INPUT_PIN_FAILED, event.getException().getMessage());
                        }
                    }

                    @Override
                    public Handler getUIHandler() {
                        return null;
                    }
                }, new EventMaker<K21PininutEvent>() {
                    @Override
                    public K21PininutEvent makeEvent(DeviceResponse deviceResponse) {
                        K21PininutEvent event = null;
                        try {
                            DeviceResponse response = dealDevResp(deviceResponse);
                            if (response == null) {
                                event = new K21PininutEvent();
                            } else if (response instanceof CmdStartStandardPinInput.CmdStartStandardPinInputNotificationResponse) {
                                CmdStartStandardPinInput.CmdStartStandardPinInputNotificationResponse notificationResponse = (CmdStartStandardPinInput.CmdStartStandardPinInputNotificationResponse) response;
                                if (notificationResponse.getReturnKey() == 0x0d) {
                                    event = new K21PininutEvent(PinInputEvent.NotifyStep.ENTER);
                                } else if (notificationResponse.getReturnKey() == 0x0a) {
                                    event = new K21PininutEvent(NotifyStep.BACKSPACE);
                                } else if (notificationResponse.getReturnKey() == 0x0f) {// 输密码清空事件
                                    event = new K21PininutEvent(PinInputEvent.NotifyStep.CLEAR);
                                } else {
                                    Exception e = new DeviceInvokeException("unknown notification type!" + Dump.getHexDump(new byte[]{(byte) notificationResponse.getReturnKey()}));
                                    event = new K21PininutEvent(e);
                                }
                            } else {
                                CmdStartStandardPinInput.CmdStartStandardPinInputResponse cmdResponse = (CmdStartStandardPinInput.CmdStartStandardPinInputResponse) response;
                                if (KEYCODE_CANCEL == cmdResponse.getReturnKey()) {
                                    devicelogger.debug("user cancel input:return code:" + cmdResponse.getReturnKey());
                                    event = new K21PininutEvent();
                                } else {
                                    event = new K21PininutEvent(cmdResponse.getCyherLength(), cmdResponse.getEncryptPinBlock(), cmdResponse.getKsn());
                                }
                            }
                        } catch (Exception e) {
                            event = new K21PininutEvent(e);
                        }
                        return event;
                    }

                });
                lastCmd = cmd;
            }

        } catch (Exception e) {
            e.printStackTrace();
            pinInputListener.onError(ErrorCode.INPUT_PIN_ERROR, "" + e);
        }
    }

    @Override
    public void startOfflinePinInput(int timeout, @NonNull byte[] modulus, @NonNull byte[] exponent, @NonNull final PinInputListener pinInputListener, PinInputExtParams pinInputExtParams) {
        try {
            if(pinInputListener==null){
                devicelogger.error("【startOfflinePinInput】参数错，pinInputListener==null");
                return;
            }
            devicelogger.info("[startOfflinePinInput] timeout:"+timeout+";modulus:"+(modulus==null?null:ISOUtils.hexString(modulus))+";exponent:"+(exponent==null?null:ISOUtils.hexString(exponent)));
            int invokeTimeout = timeout + 3;// pos超时上加个3秒
            int pinKeyIndex = 196;//输密索引有效范围是1-200
            int pinKeyType = 0x02;//默认DES的pin密钥
            int pinkeyManagement = 0x00;//mksk

            byte[] wkPlainKeyData = ISOUtils.hex2byte("11111111111111111111111111111111");
            byte[] kcv = ISOUtils.hex2byte("82E13665");
            CmdLoadWorkingKeyResponse cmdLoadWorkingKeyResponse = (CmdLoadWorkingKeyResponse) invoke(new CmdLoadWorkingKey(0x01, pinKeyType, 0x01, pinKeyIndex, wkPlainKeyData, null));
            String answer = cmdLoadWorkingKeyResponse.getAnswerCode();
            devicelogger.error("[startOfflinePinInput]answer:"+answer);
            if (!"00".equals(answer)) {
                pinInputListener.onError(ErrorCode.INPUT_PIN_ERROR, "pin key isn't exist");
                return;
            }


            int inputMaxLen = 12;
            PinConfirmType pinConfirmType = PinConfirmType.ENABLE_ENTER;
            byte[] pwdLengthRange = new byte[]{0x00,0x04,0x05,0x06,0x07,0x08,0x09,0x0A,0x0B,0x0C};
            if (pinInputExtParams != null) {
                if (pinInputExtParams.getInputMaxLen() > 0) {
                    inputMaxLen = pinInputExtParams.getInputMaxLen();
                }
                if (pinInputExtParams.getPwdLengthRange() != null) {
                    pwdLengthRange = pinInputExtParams.getPwdLengthRange();
                }
                if (pinInputExtParams.getDefaultLayout() != null) {
                    KeyBoardParams.setModulus(modulus);
                    KeyBoardParams.setExponent(exponent);
                    KeyBoardParams.setTimeout(timeout);
                    KeyBoardParams.setPinInputListener(pinInputListener);
                    KeyBoardParams.setPinInputExtParams(pinInputExtParams);
                    KeyBoardParams.setPinpadModule(this);
                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClassName(context, "com.newland.sdk.pininput.KeyBoardActivity");
                    context.startActivity(intent);
                    return;
                }
            }
            byte[] pinPadding = new byte[]{'F', 'F', 'F', 'F', 'F', 'F', 'F', 'F', 'F', 'F'};
            CmdStartStandardPinInput cmd = new CmdStartStandardPinInput(pinKeyIndex, null, pinkeyManagement, AccountInputType.UNUSE_ACCOUNT, null, inputMaxLen, pinPadding, pinConfirmType, null, timeout, pwdLengthRange, 1);
            invoke(cmd, (long) invokeTimeout, TimeUnit.SECONDS, new DeviceEventListener<K21PininutEvent>() {
                @Override
                public void onEvent(K21PininutEvent event, Handler handler) {
                    if (event.isProcessing()) {
                        PinInputEvent.NotifyStep notifyStep = event.getNotifyStep();
                        if (notifyStep == PinInputEvent.NotifyStep.ENTER) {
                            pinInputListener.onKeyPress();
                        } else if (notifyStep == PinInputEvent.NotifyStep.BACKSPACE) {
                            pinInputListener.onBackspace();
                        }else if(notifyStep == NotifyStep.CLEAR){
                            if(pinInputListener instanceof PinInputExtListener){
                                ((PinInputExtListener)pinInputListener).onNotifyStep((byte)0x0f);
                            }
                        }
                    } else if (event.isUserCanceled()) {
                        pinInputListener.onCancel();
                    } else if (event.isSuccess()) {
                        if (event.getInputLen() == 0) {
                            pinInputListener.onFinish(0, new byte[]{},null);
                        } else {
                            byte[] pin = event.getEncrypPin();
                            if (pin != null && pin.length >= 2) {
                                try {
                                    String pinblockS = ISOUtils.hexString(pin).substring(2).replace("F", "");
                                    pin = pinblockS.getBytes("gbk");
                                    pinInputListener.onFinish(pin.length, pin,event.getKsn());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    pinInputListener.onError(ErrorCode.INPUT_PIN_FAILED, e + "");
                                }

                            } else {
                                pinInputListener.onError(ErrorCode.INPUT_PIN_FAILED, "pinblock length error");
                            }

                        }
                    } else {
                        if (event.getException().getCause() instanceof ProcessTimeoutException) {
                            pinInputListener.onTimeout();
                            return;
                        }
                        pinInputListener.onError(ErrorCode.INPUT_PIN_FAILED, event.getException().getMessage());
                    }
                }

                @Override
                public Handler getUIHandler() {
                    return null;
                }
            }, new EventMaker<K21PininutEvent>() {
                @Override
                public K21PininutEvent makeEvent(DeviceResponse deviceResponse) {
                    K21PininutEvent event = null;
                    try {
                        DeviceResponse response = dealDevResp(deviceResponse);
                        if (response == null) {
                            event = new K21PininutEvent();
                        } else if (response instanceof CmdStartStandardPinInputNotificationResponse) {
                            CmdStartStandardPinInputNotificationResponse notificationResponse = (CmdStartStandardPinInputNotificationResponse) response;
                            if (notificationResponse.getReturnKey() == 0x0d) {
                                event = new K21PininutEvent(NotifyStep.ENTER);
                            } else if (notificationResponse.getReturnKey() == 0x0a) {
                                event = new K21PininutEvent(NotifyStep.BACKSPACE);
                            } else if (notificationResponse.getReturnKey() == 0x0f) {// 输密码清空事件
                                event = new K21PininutEvent(NotifyStep.CLEAR);
                            }else {
                                Exception e = new DeviceInvokeException("unknown notification type!" + Dump.getHexDump(new byte[]{(byte) notificationResponse.getReturnKey()}));
                                event = new K21PininutEvent(e);
                            }
                        } else {
                            CmdStartStandardPinInputResponse cmdResponse = (CmdStartStandardPinInputResponse) response;
                            if (KEYCODE_CANCEL == cmdResponse.getReturnKey()) {
                                devicelogger.debug("[startOfflinePinInput] user cancel input:return code:" + cmdResponse.getReturnKey());
                                event = new K21PininutEvent();
                            } else if (KEYCODE_SWIPCARD == cmdResponse.getReturnKey()) {
                                event = new K21PininutEvent(K21PininutEvent.PinState.SWIPCARD, -1, null, null);
                            } else if (KEYCODE_ICCARD == cmdResponse.getReturnKey()) {
                                event = new K21PininutEvent(PinState.ICCARD, -1, null, null);
                                MECardReader cardreader = (MECardReader) getOwner().getStandardModule(ModuleType.COMMON_CARDREADER);
                                cardreader.setLastReaderTypes(new CardType[]{CardType.ICCARD});
                            } else {
                                event = new K21PininutEvent(cmdResponse.getCyherLength(), cmdResponse.getEncryptPinBlock(), cmdResponse.getKsn());
                            }
                        }
                    } catch (Exception e) {
                        event = new K21PininutEvent(e);
                    }
                    return event;
                }

            });
            lastCmd = cmd;
        }catch (Exception e){
            e.printStackTrace();
            pinInputListener.onError(ErrorCode.INPUT_PIN_FAILED, ""+e);
        }

    }

    @Override
    public void cancelPinInput() {
        devicelogger.debug("[cancelPinInput]");
        if (lastCmd != null) {
            AbortableDeviceCommand tmp = lastCmd;
            lastCmd = null;
            tmp.abort(CANCEL_EVENT_CODE);
        }
    }

    @Override
    public byte[] getKeyKcv(KeyType keyType, AlgorithmMode algorithmMode, int keyIndex) {
        devicelogger.debug("[getKeyKcv] keyType:"+keyType+"; algorithmMode:"+algorithmMode+"; keyIndex:"+keyIndex);
        if(keyType == null || algorithmMode == null){
            devicelogger.error("[getKeyKcv] keyType == null || algorithmMode == null");
            return null;
        }
        byte secKeyType = 0;
        if(keyType == KeyType.TRANSPORT_KEY){
            secKeyType |= EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TLK.ordinal();
        }else if(keyType == KeyType.MASTER_KEY){
            secKeyType |= EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal();
        }else if(keyType == KeyType.PIN_KEY){
            secKeyType |= EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TPK.ordinal();
        }else if(keyType == KeyType.MAC_KEY){
            secKeyType |= EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TAK.ordinal();
        }else if(keyType == KeyType.TRACK_KEY){
            secKeyType |= EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TDK.ordinal();
        }else {
            return null;
        }

        if(algorithmMode == AlgorithmMode.DES){
            secKeyType |= EM_SEC_KEY_ALG.SEC_KEY_DES.getCode();
        }else if(algorithmMode == AlgorithmMode.SM4){
            secKeyType |= EM_SEC_KEY_ALG.SEC_KEY_SM4.getCode();
        }else if(algorithmMode == AlgorithmMode.AES){
            secKeyType |= EM_SEC_KEY_ALG.SEC_KEY_AES.getCode();
        }else {
            return null;
        }
        ST_SEC_KCV_INFO kcvInfo = new ST_SEC_KCV_INFO();
        kcvInfo.nCheckMode = EM_SEC_KCV.SEC_KCV_ZERO.ordinal();
        int ret = NdkApiManager.getNdkApiManager().getSecN().NDK_SecGetKcv(secKeyType, (byte) keyIndex, kcvInfo);
        byte[] kcv = null;
        if(ret == 0){
            kcv = new byte[3];
            System.arraycopy(kcvInfo.sCheckBuf, 0, kcv, 0, 3);
        }
        devicelogger.debug("[getKeyKcv] NDK_SecGetKcv ret="+ret);
        return kcv;
    }

    @Override
    public boolean isStandardModule() {
        return true;
    }

    @Override
    public ModuleType getStandardModuleType() {
        return ModuleType.PINPAD;
    }

    @Override
    public String getExModuleType() {
        return null;
    }

    /**
     * 获取系统属性值
     *
     * @param key
     * @return 返回值 unknown  表示属性值不存在。
     * 其他返回具体的属性值
     */

    private static String getProperties(String key) {
        String defaultValue = "unknown";
        String value = defaultValue;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            value = (String) (get.invoke(c, key, defaultValue));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    // 还原坐标
    private int[] recover(byte[] initCoordinate) {
        int[] orgCoordinate = new int[initCoordinate.length / 2];
        for (int i = 0; i < orgCoordinate.length; i++) {
            orgCoordinate[i] = initCoordinate[i * 2];
            orgCoordinate[i] = (orgCoordinate[i] << 8) | 0x00ff & initCoordinate[i * 2 + 1];
        }
        return orgCoordinate;
    }


    /**
     * 3DES加密
     *
     * @param key    加密密钥(16字节长度)
     * @param source 明文
     * @return byte[] 密文
     */
    private byte[] encrype3Des(byte[] key, byte[] source) {
        if (key == null || key.length < 1 || source == null || source.length < 1) {
            return null;
        }
        try {
            //初始化加密数据块
            byte[] cursorSourceBytes = new byte[8];
            System.arraycopy(source, 0, cursorSourceBytes, 0, 8);
            //初始化左半部分密钥
            byte[] keyLeft = new byte[8];
            System.arraycopy(key, 0, keyLeft, 0, 8);
            //初始化右半部分密钥
            byte[] keyRight = new byte[8];
            System.arraycopy(key, 8, keyRight, 0, 8);
            //第一步 : 用左半部分密钥对数据进行DES加密
            byte[] encryptResultBytes = encryptDes(keyLeft, cursorSourceBytes);
            //第二步 : 用右半部分密钥对第一步加密结果进行DES解密
            byte[] decryptResultbytes = decryptDes(keyRight, encryptResultBytes);
            //第三步 : 用左半部分密钥对第三步解密结果进行DES加密
            byte[] cursorResultBytes = encryptDes(keyLeft, decryptResultbytes);
            if (source.length > 8) {//判断是否有多个8字节数据块
                //初始化下一个数据块
                byte[] tempSourceBytes = new byte[source.length - 8];
                System.arraycopy(source, 8, tempSourceBytes, 0, source.length - 8);
                //下一个数据库加密结果
                byte[] subRelultBytes = encrype3Des(key, tempSourceBytes);
                byte[] resultBytes = new byte[cursorResultBytes.length + subRelultBytes.length];
                //合并加密结果
                System.arraycopy(cursorResultBytes, 0, resultBytes, 0, cursorResultBytes.length);
                System.arraycopy(subRelultBytes, 0, resultBytes, cursorResultBytes.length, subRelultBytes.length);
                return resultBytes;
            }
            return cursorResultBytes;
        } catch (Exception e) {
            //	logger.error("3DES加密异常", e);
        }
        return null;
    }

    /**
     * DES加密
     *
     * @param keybyte 加密密钥
     * @param src     明文
     * @return byte[] 密文
     */
    private byte[] encryptDes(byte[] keybyte, byte[] src) {
        if (keybyte == null || keybyte.length < 1 || src == null || src.length < 1) {
            return null;
        }
        try {
            // 生成密钥
            SecretKey deskey = new SecretKeySpec(keybyte, "DES");
            // 加密
            Cipher cipher = Cipher.getInstance("DES" + "/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, deskey);
            return cipher.doFinal(src);
        } catch (Exception e) {
            //logger.error("DES加密异常", e);
        }
        return null;
    }

    /**
     * DES解密
     *
     * @param keybyte 解密密钥
     * @param src     密文
     * @return byte[] 明文
     */
    private byte[] decryptDes(byte[] keybyte, byte[] src) {
        if (keybyte == null || keybyte.length < 1 || src == null || src.length < 1) {
            return null;
        }
        try {
            // 生成密钥
            SecretKey deskey = new SecretKeySpec(keybyte, "DES");
            // 解密
            Cipher cipher = Cipher.getInstance("DES" + "/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, deskey);
            return cipher.doFinal(src);
        } catch (Exception e) {
            //	logger.error("DES解密异常", e);
        }
        return null;
    }

    /**
     * 是否支持人脸识别
     * @return
     */
    private boolean isSupFaceRecognition(){
        try {
            String config = NlBuild.VERSION.NL_HARDWARE_CONFIG;// 硬件配置码
            devicelogger.debug("[isSupFaceRecognition] config:"+config);
            if (config != null && config.length() >= 46) {
                String faceRecognitionParam = config.substring(44, 46);
                devicelogger.debug("[isSupFaceRecognition] faceRecognitionParam:"+faceRecognitionParam);
                if ("01".equals(faceRecognitionParam) || "02".equals(faceRecognitionParam) || "03".equals(faceRecognitionParam)) {
                    return true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
