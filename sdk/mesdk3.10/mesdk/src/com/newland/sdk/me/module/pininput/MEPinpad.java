package com.newland.sdk.me.module.pininput;

import com.newland.forth.module.jni.ForthJni;
import com.newland.intelligent.jni.JniCmdInterface;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.newland.os.NlBuild;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
//import android.support.annotation.LongDef;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.newland.ndk.NdkApiManager;
import com.newland.ndk.SecN;
import com.newland.ndk.h.EM_SEC_KCV;
import com.newland.ndk.h.EM_SEC_KEY_ALG;
import com.newland.ndk.h.EM_SEC_KEY_TYPE;
import com.newland.ndk.h.ST_SEC_KCV_INFO;
import com.newland.rkl.RKLListener;
import com.newland.sdk.me.cmd.common.CmdGetTusn;
import com.newland.sdk.me.cmd.common.CmdGetTusn.CmdTusnResponse;
import com.newland.sdk.me.cmd.pininput.CmdCalcMac;
import com.newland.sdk.me.cmd.pininput.CmdCheckKeyIsExist;
import com.newland.sdk.me.cmd.pininput.CmdDeleteKey;
import com.newland.sdk.me.cmd.pininput.CmdEncryptDecrypt;
import com.newland.sdk.me.cmd.pininput.CmdGetDukptKSN;
import com.newland.sdk.me.cmd.pininput.CmdKSNIncrease;
import com.newland.sdk.me.cmd.pininput.CmdKSNLoad;
import com.newland.sdk.me.cmd.pininput.CmdLoadMainKeyAndVerify;
import com.newland.sdk.me.cmd.pininput.CmdLoadWorkingKeyAndVerify;
import com.newland.sdk.me.cmd.pininput.CmdRandomKeyboard;
import com.newland.sdk.me.cmd.pininput.CmdStartStandardPinInput;
import com.newland.sdk.me.module.cardreader.MECardReader;
import com.newland.sdk.me.module.emv.MEEMVL2;
import com.newland.sdk.module.cardreader.CardReaderModule;
import com.newland.sdk.module.cardreader.CardType;
import com.newland.sdk.module.emv.EMVModule;
import com.newland.sdk.module.pin.AccountInputType;
import com.newland.sdk.module.pin.AlgorithmMode;
import com.newland.sdk.module.pin.CalMacExtParams;
import com.newland.sdk.module.pin.CipherExtParams;
import com.newland.sdk.module.pin.CipherMode;
import com.newland.sdk.module.pin.CipherResult;
import com.newland.sdk.module.pin.DukptDerivateUsage;
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
import com.newland.sdk.module.pin.PinBlockMode;
import com.newland.sdk.module.pin.PinInputEvent.NotifyStep;
import com.newland.sdk.module.pin.PinInputExtListener;
import com.newland.sdk.module.pin.PinInputExtParams;
import com.newland.sdk.module.pin.PinInputListener;
import com.newland.sdk.module.pin.PinPadButton;
import com.newland.sdk.module.pin.PinpadModule;
import com.newland.sdk.module.pin.RKLParams;
import com.newland.sdk.module.pin.RNIBPinInputListener;
import com.newland.sdk.module.pin.TusnData;
import com.newland.sdk.module.pin.WorkingKeyType;
import com.newland.sdk.mtype.Device;
import com.newland.sdk.mtype.DeviceInvokeException;
import com.newland.sdk.mtype.DeviceRTException;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.mtype.ProcessTimeoutException;
import com.newland.sdk.mtype.common.Const;
import com.newland.sdk.mtype.common.ErrorCode;
import com.newland.sdk.mtype.common.ErrorMsg;
import com.newland.sdk.mtype.common.ErrorMsgHelper;
import com.newland.sdk.mtype.event.DeviceEventListener;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtype.util.Dump;
import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.mtypex.AbstractModule;
import com.newland.sdk.mtypex.cmd.CommandInvokeRslt;
import com.newland.sdk.mtypex.cmd.DeviceResponse;
import com.newland.sdk.mtypex.conn.AbortableDeviceCommand;
import com.newland.sdk.utils.ISOUtils;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.newland.sdk.me.cmd.CmdCode.PINPAD_LOADDUKPT;
import static com.newland.sdk.me.cmd.CmdCode.PINPAD_LOADMKEY;
import static com.newland.sdk.me.cmd.CmdCode.PINPAD_LOADWKEY;

public class MEPinpad extends AbstractModule implements PinpadModule {

    private DeviceLogger devicelogger = DeviceLoggerFactory.getLogger("MEPinpad");

    protected AbortableDeviceCommand lastCmd;
    private Context context;
    private static final int KEYCODE_CANCEL = 0x06;
    private static final int KEYCODE_SWIPCARD = 0x0B;
    private static final int KEYCODE_ICCARD = 0x0C;
    private static final int CALCULATE_MAX_LEN = 1024;
//    private DisplayMetrics displayMetrics;
    private Device device;
//    private int screenWidth;
//    private int screenHeight;
    private Handler handler = new Handler(Looper.getMainLooper());
    private EMVModule emvModule;

    public MEPinpad(AbstractDevice device, Context context) {
        super(device);
        this.device = device;
        this.context=context;
        this.emvModule = (EMVModule) device.getStandardModule(ModuleType.EMV);
//        displayMetrics = new DisplayMetrics();
//        Display display = null;//getPresentationDisplay(context);
//        if (display != null) {
//            display.getRealMetrics(displayMetrics);
//        }else{
//            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//            wm.getDefaultDisplay().getRealMetrics(displayMetrics);
//        }
//        screenWidth = displayMetrics.widthPixels;
//        screenHeight = displayMetrics.heightPixels;
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


    @Override
    public void cancelPinInput() {
        JniCmdInterface.getInstance().jniMposLibCmdCancel(0x02);
    }

    @Override
    public byte[] getKeyKcv(KeyType keyType, AlgorithmMode algorithmMode, int keyIndex) {
        if(keyType == null || algorithmMode == null){
            devicelogger.error("[getKeyKcv] keyType == null || algorithmMode == null");
            return null;
        }
        devicelogger.debug("[getKeyKcv] keyType:"+keyType+"; algorithmMode:"+algorithmMode+"; keyIndex:"+keyIndex);
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
    public MacResult calcMac(KeyManagement keyManagement, int macAlgorithm, int keyIndex, byte[] input, CalMacExtParams calMacExtParams) {
        devicelogger.debug("[keyManagement] keyManagement:"+keyManagement+";macAlgorithm:"+macAlgorithm+";keyIndex:"+keyIndex);
        byte[] wkData = null;
        byte[] randomIndex = null;
        if (calMacExtParams != null && calMacExtParams.getWorkingKeyData() != null) {
            wkData = calMacExtParams.getWorkingKeyData();
        }
        if (calMacExtParams != null && calMacExtParams.getRandomIndex() != null) {
            randomIndex = calMacExtParams.getRandomIndex();
        }
        if(macAlgorithm == MacAlgorithm.DES.CBC){
            int macType = -1;
            if(keyManagement == KeyManagement.MKSK){
                macType = 1;//SEC_MAC_TDES_X99;
            }else if(keyManagement == KeyManagement.DUKPT){
                macType = 5;//SEC_MAC_DUKPT_X99
            }
            SecN.MacOutput macOutput = NdkApiManager.getNdkApiManager().getSecN().NAPI_SecGenerateMAC(macType,keyIndex,input,null,null);
            if(macOutput != null){
                devicelogger.error("[keyManagement] NAPI_SecGenerateMAC MacOutput="+macOutput);
                return new MacResult(macOutput.getData(), macOutput.getKsn());
            }
        }
        CmdCalcMac.CmdCalcMacResponse cmdCalcMacResponse = (CmdCalcMac.CmdCalcMacResponse) invoke(new CmdCalcMac(keyManagement, macAlgorithm, keyIndex, input, CmdCalcMac.CmdState.ONLY_BLOCK, wkData, randomIndex));
        return new MacResult(cmdCalcMacResponse.getMAC(), cmdCalcMacResponse.getKSN());
    }

    @Override
    public void startRKL(RKLParams params, RKLListener listener) {
        devicelogger.error("startRKL......");
        if(params == null || listener == null){
            devicelogger.error("startRKL params="+params+" listener="+listener);
            return;
        }
        new FlyKey(context,device).startRKL(params,listener);
    }

    @Override
    public boolean loadMasterKey(LoadKeyMode loadKeyMode, AlgorithmMode algorithmMode, int masterKeyIndex, byte[] masterKeyData, byte[] checkValue, LoadMKExtParams loadMKExtParams) {
        devicelogger.debug("[loadMasterKey] loadKeyMode:"+loadKeyMode+"; algorithmMode:"+algorithmMode+"; masterKeyIndex:"+masterKeyIndex);
        int kekIndex = -1;
        byte[] cbcInitData = null;
        if (loadMKExtParams != null) {
            kekIndex = loadMKExtParams.getKekIndex();
            if (loadMKExtParams.getCbcInit() != null) {
                cbcInitData = loadMKExtParams.getCbcInit();
            }
        }
        byte[] finalKcv = null;
        if(checkValue!=null && checkValue.length>=4){
            finalKcv = new byte[4];
            System.arraycopy(checkValue,0,finalKcv,0,4);
        }
        CmdLoadMainKeyAndVerify.CmdLoadMainKeyAndVerifyResponse cmdLoadMainKeyResponse = (CmdLoadMainKeyAndVerify.CmdLoadMainKeyAndVerifyResponse) invoke(new CmdLoadMainKeyAndVerify(loadKeyMode, algorithmMode, masterKeyIndex, masterKeyData, finalKcv, kekIndex, cbcInitData), 5, TimeUnit.SECONDS);
        String answer = cmdLoadMainKeyResponse.getAnswerCode();
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
            ErrorMsg msg = ErrorMsgHelper.getInstance().getErrorMsg(PINPAD_LOADMKEY);
            devicelogger.error("[loadMasterKey] load master key failed: AnswerCode:" + answer + " " + errorInfo + "  ErrCode:" + msg.getErrCode() + " ErrMsg:" + msg.getErrMsg() + " OtherMsg:" + msg.getOtherMsg());
            return false;
        }
        if(finalKcv!=null){
            return true;
        }
        byte[] kcv = cmdLoadMainKeyResponse.getCheckValue();
        devicelogger.debug("[loadMasterKey] load mk check:" + ISOUtils.hexString(kcv));
        if (null != checkValue && null != kcv) {
            int length = (checkValue.length > kcv.length ? kcv.length : checkValue.length);

            byte[] srcKcv = new byte[length];
            System.arraycopy(checkValue, 0, srcKcv, 0, length);

            byte[] dstKcv = new byte[length];
            System.arraycopy(kcv, 0, dstKcv, 0, length);
            return Arrays.equals(srcKcv, dstKcv);
        }
        return true;
    }

    @Override
    public boolean loadWorkingKey(LoadWKMode loadWKMode, AlgorithmMode algorithmMode, WorkingKeyType workingKeyType, int masterKeyIndex, int workingKeyIndex, byte[] data, byte[] checkValue, LoadWKExtParams loadWKExtParams) {
        devicelogger.debug("[loadWorkingKey] loadWKMode:"+loadWKMode+"; algorithmMode:"+algorithmMode+"; workingKeyType:"+workingKeyType+"; masterKeyIndex:"+masterKeyIndex+"; workingKeyIndex:"+workingKeyIndex);
        byte[] cbcInitData = null;
        if (loadWKExtParams != null) {
            cbcInitData = loadWKExtParams.getCbcInitData();
        }
        byte[] finalKcv = null;
        if(checkValue!=null && checkValue.length>=4){
            finalKcv = new byte[4];
            System.arraycopy(checkValue,0,finalKcv,0,4);
        }
        CmdLoadWorkingKeyAndVerify.CmdLoadWorkingKeyAndVerifyResponse cmdLoadWorkingKeyResponse = (CmdLoadWorkingKeyAndVerify.CmdLoadWorkingKeyAndVerifyResponse) invoke(new CmdLoadWorkingKeyAndVerify(loadWKMode, algorithmMode, workingKeyType, masterKeyIndex,
                workingKeyIndex, data, finalKcv, cbcInitData));
        String answer = cmdLoadWorkingKeyResponse.getAnswerCode();
        String errorInfo = "Error";
        if (!"00".equals(answer)) {
            if ("41".equals(answer)) {
                errorInfo = "Kcv error";
            } else if ("43".equals(answer)) {
                errorInfo = "Invalid index";
            } else if ("45".equals(answer)) {
                errorInfo = "working key data length error";
            } else if ("46".equals(answer)) {
                errorInfo = "Invalid TR31 format";
            }
            ErrorMsg msg = ErrorMsgHelper.getInstance().getErrorMsg(PINPAD_LOADWKEY);
            devicelogger.error("[loadWorkingKey]load working key failed: AnswerCode:" + answer + " " + errorInfo + "  ErrCode:" + msg.getErrCode() + " ErrMsg:" + msg.getErrMsg() + " OtherMsg:" + msg.getOtherMsg());
            return false;
        }
        if(finalKcv!=null){
            return true;
        }
        byte[] kcv = cmdLoadWorkingKeyResponse.getCheckvalue();
        if (null != checkValue && null != kcv) {
            int length = (checkValue.length > kcv.length ? kcv.length : checkValue.length);

            byte[] srcKcv = new byte[length];
            System.arraycopy(checkValue, 0, srcKcv, 0, length);

            byte[] dstKcv = new byte[length];
            System.arraycopy(kcv, 0, dstKcv, 0, length);
            return Arrays.equals(srcKcv, dstKcv);
        }
        devicelogger.debug("[loadWorkingKey]load wk check:" + Dump.getHexDump(kcv));
        return true;
    }


    @Override
    public boolean loadIPEK(LoadKeyMode loadKeyMode, int KSNIndex, byte[] ksn, byte[] defaultKeyData, LoadDuktpExtParams loadDuktpExtParams) {
        devicelogger.debug("[loadIPEK] loadKeyMode:"+loadKeyMode+";KSNIndex:"+KSNIndex);
        int mainKeyIndex = -1;
        if (loadDuktpExtParams != null) {
            mainKeyIndex = loadDuktpExtParams.getKekIndex();
        }
        CmdKSNLoad.CmdKSNLoadResponse cmdKSNLoadResponse = (CmdKSNLoad.CmdKSNLoadResponse) invoke(new CmdKSNLoad(loadKeyMode, KSNIndex, ksn, defaultKeyData, mainKeyIndex, null));
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
                ErrorMsg msg = ErrorMsgHelper.getInstance().getErrorMsg(PINPAD_LOADDUKPT);
                devicelogger.error("[loadIPEK] load working key failed: AnswerCode:" + answerCode + " " + errorInfo + "  ErrCode:" + msg.getErrCode() + " ErrMsg:" + msg.getErrMsg() + " OtherMsg:" + msg.getOtherMsg());
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean ksnIncrease(int dukptKeyIndex) {
        try {
            devicelogger.debug("[ksnIncrease] dukptKeyIndex:"+dukptKeyIndex);
            invoke(new CmdKSNIncrease(dukptKeyIndex));
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean ksnAESIncrease(int dukptKeyIndex) {
        return false;
    }

    @Override
    public byte[] getDukptKsn(int dukptKeyIndex) {
        try {
            devicelogger.debug("[getDukptKsn] dukptKeyIndex:"+dukptKeyIndex);
            CmdGetDukptKSN.CmdGetDukptKSNResponse response = (CmdGetDukptKSN.CmdGetDukptKSNResponse) invoke(new CmdGetDukptKSN(dukptKeyIndex));
            if (null != response) {
                return response.getKSN();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public byte[] getDukptAESKsn(int dukptKeyIndex) {
        return null;
    }

    @Override
    public CipherResult encrypt(KeyManagement keyManagement, AlgorithmMode algorithmMode, CipherMode cipherMode, int keyIndex, byte[] inputData, CipherExtParams params) {
        devicelogger.debug("[encrypt] keyManagement:"+keyManagement+"; algorithmMode:"+algorithmMode+";cipherMode:"+cipherMode+"; keyIndex:"+keyIndex);
        if(keyManagement == null || algorithmMode == null || cipherMode == null || inputData == null){
           devicelogger.error("[encrypt] keyManagement == null || algorithmMode == null || cipherMode == null || inputData == null");
            return null;
        }
        byte[] cbcInit = null;
        byte[] workingKeyData = null;
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
        if (params != null && params.getWorkingKeyData() != null) {
            workingKeyData = params.getWorkingKeyData();
        }

        if(cipherMode == CipherMode.ECB){
            int elementLen = 16;
            if(algorithmMode == AlgorithmMode.DES){
                elementLen = 8;
            }
            int destLen = (inputData.length + elementLen - 1) / elementLen * elementLen;
            byte[] outData = new byte[destLen];
            int[] outLen = new int[1];
            byte[] outKsnData = new byte[10];
            int[] outKsnLen = new int[1];
            int ret = JniCmdInterface.getInstance().encrypt(keyManagement.ordinal(),algorithmMode.ordinal()+1,cipherMode.ordinal(),keyIndex,
                    inputData,inputData.length,null,0, outData,outLen,outKsnData,outKsnLen);
            if(ret != 0){
               return null;
            }
            return new CipherResult(outData,ISOUtils.hexString(outKsnData));
//            int elementLen = 16,maxLen = CALCULATE_MAX_LEN;
//            if(algorithmMode == AlgorithmMode.DES){
//                elementLen = 8;
//            }
//            CipherResult result=null;
//            int destLen = (inputData.length + elementLen - 1) / elementLen * elementLen;
//            int count = destLen / maxLen,remainder = destLen % maxLen;
//            if((count == 1 && remainder != 0) || (count > 1)){
//                byte[] srcData = new byte[destLen];
//                Arrays.fill(srcData, (byte) 0x00);
//                System.arraycopy(inputData,0,srcData,0,inputData.length);
//                byte[] destData = new byte[destLen];
//                byte[] srcItem = new byte[maxLen];
//                for (int i = 0; i < count; i++) {
//                    System.arraycopy(srcData,i*maxLen,srcItem,0,maxLen);
//                    result = encrypt(keyManagement,algorithmMode,cipherMode,keyIndex,srcItem,params);
//                    System.arraycopy(result.getData(),0,destData,i*maxLen,maxLen);
//                }
//                if(remainder!=0){
//                    byte[] srcRemData = new byte[remainder];
//                    System.arraycopy(srcData,count*maxLen,srcRemData,0,remainder);
//                    result = encrypt(keyManagement,algorithmMode,cipherMode,keyIndex,srcRemData,params);
//                    System.arraycopy(result.getData(),0,destData,count*maxLen,remainder);
//                }
//                if(result==null){
//                    return null;
//                }
//                return new CipherResult(destData,result.getKsn());
//            }
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
        CmdEncryptDecrypt.CmdEncryptDecryptResponse cmdEncryptDecryptResponse = (CmdEncryptDecrypt.CmdEncryptDecryptResponse) invoke(new CmdEncryptDecrypt(keyManagement, algorithmMode, cipherMode, keyIndex, inputData, workingKeyData, cbcInit, 0));
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
        return new CipherResult(cmdEncryptDecryptResponse.getEncDecData(), cmdEncryptDecryptResponse.getKsn());
    }

    @Override
    public CipherResult decrypt(KeyManagement keyManagement, AlgorithmMode algorithmMode, CipherMode cipherMode, int keyIndex, byte[] inputData, CipherExtParams params) {
        devicelogger.debug("[decrypt] keyManagement:"+keyManagement+"; algorithmMode:"+algorithmMode+"; cipherMode:"+cipherMode+"; keyIndex:"+keyIndex);
        if(keyManagement == null || algorithmMode == null || cipherMode == null || inputData == null){
            return null;
        }
        byte[] cbcInit = null;
        byte[] workingKeyData = null;
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
        if (params != null && params.getWorkingKeyData() != null) {
            workingKeyData = params.getWorkingKeyData();
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

        CmdEncryptDecrypt.CmdEncryptDecryptResponse cmdEncryptDecryptResponse = (CmdEncryptDecrypt.CmdEncryptDecryptResponse) invoke(new CmdEncryptDecrypt(keyManagement, algorithmMode, cipherMode, keyIndex, inputData, workingKeyData, cbcInit, 1));
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
        return new CipherResult(cmdEncryptDecryptResponse.getEncDecData(), cmdEncryptDecryptResponse.getKsn());
    }

    @Override
    public boolean checkKeyIsExist(KeyType keyType, AlgorithmMode algorithmMode, int keyIndex, byte[] checkValue) {
        devicelogger.debug("[checkKeyIsExist] keyType:"+keyType+";algorithmMode:"+algorithmMode+";keyIndex:"+keyIndex);
        CmdCheckKeyIsExist.CmdCheckKeyIsExistResponse response = (CmdCheckKeyIsExist.CmdCheckKeyIsExistResponse) invoke(new CmdCheckKeyIsExist(keyType, algorithmMode, keyIndex, null));
        if (response.getIsExist() == 0) {
            byte[] kcv = response.getCheckValue();
            devicelogger.debug("[checkKeyIsExist] checkValue="+(checkValue==null?"null":ISOUtils.hexString(checkValue))+ " respKcv="+(kcv==null?"null":ISOUtils.hexString(kcv)));

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
        } else if (response.getIsExist() == 1) {
            return false;
        }
        return false;
    }

    @Override
    public boolean deleteKey(KeyType keyType, AlgorithmMode algorithmMode, int keyIndex) {
        devicelogger.debug("[deleteKey] keyType:"+keyType+"; algorithmMode:"+algorithmMode+"; keyIndex:"+keyIndex);
        DeviceResponse rsp = invoke(new CmdDeleteKey(keyType, algorithmMode, keyIndex));
        if (rsp.getProcessRslt() == CommandInvokeRslt.SUCCESS) {
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteAllKeys() {
        devicelogger.debug("[deleteAllKeys]");
        DeviceResponse rsp = invoke(new CmdDeleteKey());
        if (rsp.getProcessRslt() == CommandInvokeRslt.SUCCESS) {
            return true;
        }
        return false;
    }

    @Override
    public byte[] loadRandomKeyboard(KeyboardRandom keyboardRandom) {
        devicelogger.debug("[loadRandomKeyboard]");
        if(NlBuild.VERSION.MODEL.equals("P300")||NlBuild.VERSION.MODEL.equals("N950K")){
            return null;
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Display display = null;//getPresentationDisplay(context);
        if (display != null) {
            display.getRealMetrics(displayMetrics);
        }else{
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            wm.getDefaultDisplay().getRealMetrics(displayMetrics);
        }
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        //java  int x0,y0 -> x01,x02,y01,y02 ->int x01|x02,y01|y02->缩放->byte x01,x02,y01,y02
        //C     byte x01,x02,y01,y02 -> byte x02,x01,y02,y01
        Point point = new Point();
        int width = screenWidth;
        int height = screenHeight;
        if (NlBuild.VERSION.MODEL.equals("N900")) {
            point = new Point(540, 960);
        } else {
            try {
                String TOUCHSCREEN_RESOLUTION = NlBuild.VERSION.TOUCHSCREEN_RESOLUTION;
                height = Integer.valueOf(TOUCHSCREEN_RESOLUTION.split("x")[0]); // 获取K21端的触屏分辨率
                // 然后做等比例缩放
                width = Integer.valueOf(TOUCHSCREEN_RESOLUTION.split("x")[1]);
                devicelogger.debug("k21分辨率height=" + height + ";width=" + width);
                point = new Point(width, height);
            } catch (Exception e) {
                e.getStackTrace();
            }
        }

        int[] coordinateInt = recover(keyboardRandom.getCoordinate());
        int[] coordinateIntFinal = null;
        if(false){//isX800() //
            double scaleW = (double)720/800,scaleH = (double)1280/480;
            int[] coordinateInt2 = new int[coordinateInt.length];
            for (int i = 0; i < coordinateInt.length/4; i++) {
                coordinateInt2[i*4+0] = (int)((screenWidth - coordinateInt[i*4+2])*scaleW);
                coordinateInt2[i*4+1] = (int)((screenHeight - coordinateInt[i*4+3])*scaleH);
                coordinateInt2[i*4+2] = (int)((screenWidth - coordinateInt[i*4+0])*scaleW);
                coordinateInt2[i*4+3] = (int)((screenHeight - coordinateInt[i*4+1])*scaleH);
            }
            coordinateIntFinal = coordinateInt2;
        }else {
            // 等比缩放后 下发给底层
            for (int i = 0; i < coordinateInt.length; i++) {
                if (i % 2 == 0) {
                    coordinateInt[i] = coordinateInt[i] * point.x / width;//displayMetrics.widthPixels;
                } else {
                    coordinateInt[i] = coordinateInt[i] * point.y / height;//displayMetrics.heightPixels;
                }
            }
            coordinateIntFinal = coordinateInt;
        }
        // 初始坐标集合
        byte[] initCoordinate = new byte[coordinateIntFinal.length * 2];
        for (int i = 0, j = 0; i < coordinateIntFinal.length; i++, j++) {
            initCoordinate[j] = (byte) ((coordinateIntFinal[i] >> 8) & 0xff);
            j++;
            initCoordinate[j] = (byte) (coordinateIntFinal[i] & 0xff);
        }
        keyboardRandom.setCoordinate(initCoordinate);
        CmdRandomKeyboard.CmdRandomKeyboardResponse response = (CmdRandomKeyboard.CmdRandomKeyboardResponse) invoke(new CmdRandomKeyboard(keyboardRandom));
        return response.getKeyCodes();
    }

    @Override
    public boolean loadRNIBKeyboard(int keyNum, Map<PinPadButton, int[]> pinPadButtons, int[] touchCoordinates, int[] KeyboradCoordinates){
        devicelogger.debug("loadRNIBKeyboard keyNum="+keyNum);
        int[] keyButtons = null;
        if(keyNum == 12){
            keyButtons = new int[48];
            System.arraycopy(pinPadButtons.get(PinPadButton.NUMBER_1), 0, keyButtons, 0, 4);//1
            System.arraycopy(pinPadButtons.get(PinPadButton.NUMBER_2), 0, keyButtons, 4, 4);//2
            System.arraycopy(pinPadButtons.get(PinPadButton.NUMBER_3), 0, keyButtons, 8, 4);//3
            System.arraycopy(pinPadButtons.get(PinPadButton.NUMBER_4), 0, keyButtons, 12, 4);//4
            System.arraycopy(pinPadButtons.get(PinPadButton.NUMBER_5), 0, keyButtons, 16, 4);//5
            System.arraycopy(pinPadButtons.get(PinPadButton.NUMBER_6), 0, keyButtons, 20, 4);//6
            System.arraycopy(pinPadButtons.get(PinPadButton.NUMBER_7), 0, keyButtons, 24, 4);//7
            System.arraycopy(pinPadButtons.get(PinPadButton.NUMBER_8), 0, keyButtons, 28, 4);//8
            System.arraycopy(pinPadButtons.get(PinPadButton.NUMBER_9), 0, keyButtons, 32, 4);//9
            System.arraycopy(pinPadButtons.get(PinPadButton.NUMBER_0), 0, keyButtons, 36, 4);//0
            System.arraycopy(pinPadButtons.get(PinPadButton.CANCEL), 0, keyButtons, 40, 4);//cancel
            System.arraycopy(pinPadButtons.get(PinPadButton.ENTER), 0, keyButtons, 44, 4);//enter
        }else if(keyNum == 13){
            keyButtons = new int[52];
            System.arraycopy(pinPadButtons.get(PinPadButton.NUMBER_1),0, keyButtons, 0, 4);//1
            System.arraycopy(pinPadButtons.get(PinPadButton.NUMBER_2),0, keyButtons, 4, 4);//2
            System.arraycopy(pinPadButtons.get(PinPadButton.NUMBER_3),0, keyButtons, 8, 4);//3
            System.arraycopy(pinPadButtons.get(PinPadButton.NUMBER_4),0, keyButtons, 12, 4);//4
            System.arraycopy(pinPadButtons.get(PinPadButton.NUMBER_5),0, keyButtons, 16, 4);//5
            System.arraycopy(pinPadButtons.get(PinPadButton.NUMBER_6),0, keyButtons, 20, 4);//6
            System.arraycopy(pinPadButtons.get(PinPadButton.NUMBER_7),0, keyButtons, 24, 4);//7
            System.arraycopy(pinPadButtons.get(PinPadButton.NUMBER_8),0, keyButtons, 28, 4);//8
            System.arraycopy(pinPadButtons.get(PinPadButton.NUMBER_9),0, keyButtons, 32, 4);//9
            System.arraycopy(pinPadButtons.get(PinPadButton.NUMBER_0),0, keyButtons, 36, 4);//0
            System.arraycopy(pinPadButtons.get(PinPadButton.CANCEL),0, keyButtons, 40, 4);//cancel
            System.arraycopy(pinPadButtons.get(PinPadButton.BACKSPACE),0, keyButtons, 44, 4);//backspace
            System.arraycopy(pinPadButtons.get(PinPadButton.ENTER),0, keyButtons, 48, 4);//enter
        }else if(keyNum == 15){
            keyButtons = new int[60];
            System.arraycopy(pinPadButtons.get(PinPadButton.NUMBER_1), 0, keyButtons, 0, 4);
            System.arraycopy(pinPadButtons.get(PinPadButton.NUMBER_2), 0, keyButtons, 4, 4);
            System.arraycopy(pinPadButtons.get(PinPadButton.NUMBER_3), 0, keyButtons, 8, 4);
            System.arraycopy(pinPadButtons.get(PinPadButton.CANCEL), 0,  keyButtons, 12, 4);
            System.arraycopy(pinPadButtons.get(PinPadButton.NUMBER_4), 0,keyButtons, 16, 4);
            System.arraycopy(pinPadButtons.get(PinPadButton.NUMBER_5), 0,keyButtons, 20, 4);
            System.arraycopy(pinPadButtons.get(PinPadButton.NUMBER_6), 0,keyButtons, 24, 4);
            System.arraycopy(pinPadButtons.get(PinPadButton.BACKSPACE), 0, keyButtons, 28, 4);
            System.arraycopy(pinPadButtons.get(PinPadButton.NUMBER_7), 0, keyButtons, 32, 4);
            System.arraycopy(pinPadButtons.get(PinPadButton.NUMBER_8), 0, keyButtons, 36, 4);
            System.arraycopy(pinPadButtons.get(PinPadButton.NUMBER_9), 0, keyButtons, 40, 4);
            System.arraycopy(pinPadButtons.get(PinPadButton.ENTER), 0, keyButtons, 44, 4);
            System.arraycopy(pinPadButtons.get(PinPadButton.BLANK1), 0, keyButtons, 48, 4);
            System.arraycopy(pinPadButtons.get(PinPadButton.NUMBER_0), 0, keyButtons, 52, 4);
            System.arraycopy(pinPadButtons.get(PinPadButton.BLANK2), 0, keyButtons, 56, 4);
        }
        if(keyButtons == null || touchCoordinates == null || KeyboradCoordinates == null){
            return false;
        }
        int ret = ForthJni.getInstance().NAPI_SecVppRNIBTpInit(keyButtons,touchCoordinates,KeyboradCoordinates,keyNum);
        devicelogger.debug("loadRNIBKeyboard ret="+ret);
        return ret == 0;
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
    private int[] recoverX800(byte[] initCoordinate,int screenWidth,int screenHeight) {
        int[] orgCoordinate = new int[initCoordinate.length / 2];
        for (int i = 0; i < orgCoordinate.length; i++) {
            orgCoordinate[i] = screenWidth - (initCoordinate[i * 2]);
            orgCoordinate[i] = screenHeight - ((orgCoordinate[i] << 8) | 0x00ff & initCoordinate[i * 2 + 1]);
        }
        return orgCoordinate;
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
        String finalSN = null;

        try {
            byte[] version = new byte[16];
            int rs = ndkApiManager.getSysN().NDK_Getlibver(version);
            if (rs == 0) {
                String ver = new String(version).toString().trim();
                devicelogger.debug("[getTusnData] 当前机器的ndk版本号为：" + ver);
                if (ver.startsWith("mNDK") || "NDK_V1.0.08".compareToIgnoreCase(ver) > 0) {
                    String serialNo = "000003";// 新大陆厂商序号 000003
                    String sn = device.getDeviceInfo().getSN();
                    String tusn = serialNo + deviceType + sn;
                    // 人行二次改造，判定文件存在则进行下一步验证，否则返回不支持
                    File file = new File("/newland/factory/flag_sn_20");
                    if (!file.exists()) {
                        devicelogger.debug("[getTusnData] 文件不存在");
                        tusnData = new TusnData(deviceType, sn, null);
                        finalSN = sn;
                    } else {
                        devicelogger.debug("[getTusnData] 文件存在");
                        finalSN = tusn;
                    }
                } else {
                    devicelogger.debug("[getTusnData] 使用新方式");
                    finalSN = getTusn();
                }
            } else {
                devicelogger.debug("[getTusnData] 获取ndk版本号失败" + rs);
                String serialNo = "000003";// 新大陆厂商序号 000003
                String sn = device.getDeviceInfo().getSN();
                String tusn = serialNo + deviceType + sn;
                // 人行二次改造，判定文件存在则进行下一步验证，否则返回不支持
                File file = new File("/newland/factory/flag_sn_20");
                if (!file.exists()) {
                    devicelogger.debug("[getTusnData] 文件不存在");
                    tusnData = new TusnData(deviceType, sn, null);
                    finalSN = sn;
                } else {
                    devicelogger.debug("[getTusnData] 文件存在");
                    finalSN = tusn;
                }
            }


            // 判断固件是否支持密钥分区以及是否有21号文密钥存在
            int setOwnerRslt = secNDK.NDK_SecSetKeyOwner("_NL_TERM_MGR"); // 人行21号表切换
            if (setOwnerRslt != 0) {
                devicelogger.error("[getTusnData] 切换人行21号表失败，固件不支持:" + setOwnerRslt);
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
                devicelogger.debug("[getTusnData] 不存在密钥，未灌装密钥:" + ndkRslt);
                tusnData = new TusnData(deviceType, finalSN, null);
                return tusnData;
            }
            byte[] mab = new byte[32];
            byte[] resultBlock = new byte[16];
            // a)
            String szNLSerialNo;
            if (random != null) {
                szNLSerialNo = finalSN + random;
            } else {
                szNLSerialNo = finalSN;
            }
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
            // secNDK.NDK_SecSetKeyOwner("_NL_TERM_MGR"); // 人行21号独立密钥区
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

    private String getTusn() {
        devicelogger.debug("[getTusn]");
        CmdTusnResponse response = (CmdTusnResponse) invoke(new CmdGetTusn()); //F1 05此指令在ndk1.0.08之后才支持

        String deviceType = "04";// 01 ATM, 02 传统POS, 03 MPOS, 04 智能POS
        if (isSupFaceRecognition()) {
            deviceType = "09";
        }
        String serialNo = "000003";// 新大陆厂商序号 000003
        String tusn = null;
        String sn = null;
        String answer = response.getAnswerCode();

        if ("00".equals(answer)) {
            tusn = response.getPosTusn();
        } else {
            devicelogger.debug("[getTusn] answer code:" + answer);
            sn = device.getDeviceInfo().getSN();
            tusn = serialNo + deviceType + sn;
            if ("01".equals(answer)) { // 不存在snk
                File file = new File(Const.TUSNFLAG_PATH);
                if (!file.exists()) { // 如果文件不存在
                    return sn;
                }
            } else if ("02".equals(answer)) {
                return sn;
            } else if ("03".equals(answer)) {
                devicelogger.debug("[getTusn] NDK版本低于1.0.08");
                File file = new File(Const.TUSNFLAG_PATH);
                if (!file.exists()) { // 如果文件不存在
                    return sn;
                }
            }
        }
        return tusn;
    }

    @Override
    public void startPinInput(KeyManagement keyManagement, final AlgorithmMode algorithmMode, int keyIndex, String pan, int timeout, final PinInputListener pinInputListener, PinInputExtParams pinInputExtParams) {
        try {
            devicelogger.debug("[startPinInput] keyManagement:"+keyManagement+";algorithmMode:"+algorithmMode+";keyIndex:"+keyIndex+";pan:"+pan+";timeout:"+timeout);
            int invokeTimeout = timeout + 3;
            byte[] wkData = null;
            AccountInputType acctInputType = AccountInputType.USE_ACCOUNT;
            int inputMaxLen = 12;
            PinConfirmType pinConfirmType = PinConfirmType.ENABLE_ENTER;
            PinBlockMode pinFormatMode=null;
            int derivateKeyLen = -1;
            int dukptDerivateUsage = -1;
            byte[] pwdLengthRange = null;
            if (pinInputExtParams != null) {
                if (pan == null || pan.length() <= 0) {
                    acctInputType = AccountInputType.UNUSE_ACCOUNT;
                } else if (pinInputExtParams.getAcctInputType() != null) {
                    acctInputType = pinInputExtParams.getAcctInputType();
                }
                if(pinInputExtParams.getPinBlockMode()!=null){
                    pinFormatMode = pinInputExtParams.getPinBlockMode();
                }
                if (pinInputExtParams.getInputMaxLen() > 0) {
                    inputMaxLen = pinInputExtParams.getInputMaxLen();
                }

                if (pinInputExtParams.getPwdLengthRange() != null) {
                    pwdLengthRange = pinInputExtParams.getPwdLengthRange();
                }
                if(pinInputExtParams!=null && pinInputExtParams.getPinConfirmType()== com.newland.sdk.module.pin.PinConfirmType.DISABLE_ENTER){
                    pinConfirmType = PinConfirmType.DISABLE_ENTER;
                }

                derivateKeyLen = pinInputExtParams.getDerivateKeyLen();

                if(pinInputExtParams.getDukptDerivateUsage() != null){
                    dukptDerivateUsage = pinInputExtParams.getDukptDerivateUsage().ordinal();
                }

                pinInputExtParams.getDukptDerivateUsage();

                if(!NlBuild.VERSION.MODEL.equals("P300") && !NlBuild.VERSION.MODEL.equals("N950K") && pinInputExtParams.getDefaultLayout()!=null){
                    KeyBoardParams.setKeyManagement(keyManagement);
                    KeyBoardParams.setAlgorithmMode(algorithmMode);
                    KeyBoardParams.setKeyIndex(keyIndex);
                    KeyBoardParams.setPan(pan);
                    KeyBoardParams.setTimeout(timeout);
                    KeyBoardParams.setPinInputListener(pinInputListener);
                    KeyBoardParams.setPinInputExtParams(pinInputExtParams);
                    KeyBoardParams.setPinpadModule(this);

                    if(isX800()){
                        handler.post(()->{
                            try {
                                Display display = getPresentationDisplay(context);
                                if(display != null ){
                                    //KeyBoardPresentation presentation = new KeyBoardPresentation(this,display);
                                    //presentation.show();
                                    Class<?> clazz = null;
                                    if(KeyBoardParams.getPinInputListener() instanceof RNIBPinInputListener) {
                                        clazz = Class.forName("com.newland.sdk.pininput.RNIBKeyBoardPresentation");
                                    }else {
                                        clazz = Class.forName("com.newland.sdk.pininput.KeyBoardPresentation");
                                    }
                                    Constructor<?> constructor = clazz.getConstructor(Context.class,Display.class);
                                    Object secondkeyBoard =  constructor.newInstance(context,display);
                                    Method method = secondkeyBoard.getClass().getMethod("show");
                                    method.invoke(secondkeyBoard);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        return;
                    }

                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if(KeyBoardParams.getPinInputListener() instanceof RNIBPinInputListener){
                        intent.setClassName(context,"com.newland.sdk.pininput.RNIBKeyBoardActivity");
                    }else {
                        intent.setClassName(context,"com.newland.sdk.pininput.KeyBoardActivity");
                    }
                    context.startActivity(intent);
                    return;
                }
            }
            int isRNIB = ((pinInputListener instanceof RNIBPinInputListener)?1:0);
            CmdStartStandardPinInput cmd = new CmdStartStandardPinInput(keyManagement, algorithmMode, keyIndex,
                    acctInputType, pan, wkData, inputMaxLen, pinConfirmType, timeout, pwdLengthRange, -1,pinFormatMode,
                    dukptDerivateUsage,derivateKeyLen,isRNIB);
            invoke(cmd, (long) invokeTimeout, TimeUnit.SECONDS, new DeviceEventListener<K21PininutEvent>() {
                @Override
                public void onEvent(K21PininutEvent event, Handler handler) {
                    if (event.isProcessing()) {
                        NotifyStep notifyStep = event.getNotifyStep();
                        if (notifyStep == NotifyStep.SLID) {
                            RNIBPinInputListener rnibPinInputListener = ((RNIBPinInputListener)pinInputListener);
                            switch (event.getKeyEvent()){
                                case 10:
                                    rnibPinInputListener.onSlidLeft();
                                    break;
                                case 11:
                                    rnibPinInputListener.onSlidRight();
                                    break;
                                case 12:
                                    rnibPinInputListener.onSlidUp();
                                    break;
                                case 13:
                                    rnibPinInputListener.onSlidDown();
                                    break;
                                case 14:
                                    rnibPinInputListener.onSlidNumberKey();
                                    break;
                                case 15:
                                    rnibPinInputListener.onSlidEnter();
                                    break;
                                case 16:
                                    rnibPinInputListener.onSlidCancel();
                                    break;
                                case 17:
                                    rnibPinInputListener.onSlidBackSpace();
                                    break;
                                case 18:
                                    rnibPinInputListener.onSlidNoDigitKey();
                                    break;
                            }
                        } else if (notifyStep == NotifyStep.ENTER) {
                            pinInputListener.onKeyPress();
                        } else if (notifyStep == NotifyStep.BACKSPACE) {
                            pinInputListener.onBackspace();
                        }else if(notifyStep == NotifyStep.CLEAR){
                            if(pinInputListener instanceof PinInputExtListener){
                                ((PinInputExtListener)pinInputListener).onNotifyStep((byte)0x0f);
                            }
                        }
                    } else if (event.isUserCanceled()) {
                        pinInputListener.onCancel();
                    } else if (event.isSuccess()) {
                        try {
                            ((MEEMVL2)emvModule).setDiscoverTVROnlinePin();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
                            if (notificationResponse.getReturnKey() == 0x10) {
                                event = new K21PininutEvent(NotifyStep.SLID,notificationResponse.getKeyEvent());
                            } else if (notificationResponse.getReturnKey() == 0x0d) {
                                event = new K21PininutEvent(NotifyStep.ENTER);
                            } else if (notificationResponse.getReturnKey() == 0x0a) {
                                event = new K21PininutEvent(NotifyStep.BACKSPACE);
                            } else if (notificationResponse.getReturnKey() == 0x0f) {// 输密码清空事件
                                event = new K21PininutEvent(NotifyStep.CLEAR);
                            } else {
                                Exception e = new DeviceInvokeException("unknown notification type!" + Dump.getHexDump(new byte[]{(byte) notificationResponse.getReturnKey()}));
                                event = new K21PininutEvent(e);
                            }
                        } else {
                            CmdStartStandardPinInput.CmdStartStandardPinInputResponse cmdResponse = (CmdStartStandardPinInput.CmdStartStandardPinInputResponse) response;
                            if (KEYCODE_CANCEL == cmdResponse.getReturnKey()) {
                                devicelogger.debug("[startPinInput] user cancel input:return code:" + cmdResponse.getReturnKey());
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
        } catch (Exception e) {
            e.printStackTrace();
            pinInputListener.onError(ErrorCode.INPUT_PIN_ERROR, "" + e);
        }

    }

    @Override
    public void startOfflinePinInput(int timeout, byte[] modulus, byte[] exponent, final PinInputListener pinInputListener, PinInputExtParams pinInputExtParams) {
        try {
            devicelogger.debug("[startOfflinePinInput] timeout:"+timeout+";modulus:"+(modulus==null?null:ISOUtils.hexString(modulus))+";exponent:"+(exponent==null?null:ISOUtils.hexString(exponent)));
            int inputMaxLen = 12;
            PinConfirmType pinConfirmType = PinConfirmType.ENABLE_ENTER;
            byte[] pwdLengthRange = null;
            if (pinInputExtParams != null) {
                if (pinInputExtParams.getInputMaxLen() > 0) {
                    inputMaxLen = pinInputExtParams.getInputMaxLen();
                }
                if (pinInputExtParams.getPwdLengthRange() != null) {
                    pwdLengthRange = pinInputExtParams.getPwdLengthRange();
                }

                if(!NlBuild.VERSION.MODEL.equals("P300") && !NlBuild.VERSION.MODEL.equals("N950K") && pinInputExtParams.getDefaultLayout()!=null){
                    KeyBoardParams.setModulus(modulus);
                    KeyBoardParams.setExponent(exponent);
                    KeyBoardParams.setTimeout(timeout);
                    KeyBoardParams.setPinInputListener(pinInputListener);
                    KeyBoardParams.setPinInputExtParams(pinInputExtParams);
                    KeyBoardParams.setPinpadModule(this);

                    if(isX800()){
                        handler.post(()->{
                            try {
                                Display display = getPresentationDisplay(context);
                                if(display != null ){
                                    //KeyBoardPresentation presentation = new KeyBoardPresentation(this,display);
                                    //presentation.show();
                                    Class<?> clazz = null;
                                    if(KeyBoardParams.getPinInputListener() instanceof RNIBPinInputListener) {
                                        clazz = Class.forName("com.newland.sdk.pininput.RNIBKeyBoardPresentation");
                                    }else {
                                        clazz = Class.forName("com.newland.sdk.pininput.KeyBoardPresentation");
                                    }
                                    Constructor<?> constructor = clazz.getConstructor(Context.class,Display.class);
                                    Object secondkeyBoard =  constructor.newInstance(context,display);
                                    Method method = secondkeyBoard.getClass().getMethod("show");
                                    method.invoke(secondkeyBoard);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        return;
                    }

                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if(KeyBoardParams.getPinInputListener() instanceof RNIBPinInputListener){
                        intent.setClassName(context,"com.newland.sdk.pininput.RNIBKeyBoardActivity");
                    }else {
                        intent.setClassName(context,"com.newland.sdk.pininput.KeyBoardActivity");
                    }
                    context.startActivity(intent);
                    return;
                }
            }
            int isRNIB = ((pinInputListener instanceof RNIBPinInputListener)?1:0);
            int invokeTimeout = timeout + 3;// pos超时上加个3秒
            CmdStartStandardPinInput cmd = new CmdStartStandardPinInput(inputMaxLen, pinConfirmType, timeout, pwdLengthRange, modulus, exponent,isRNIB);
            invoke(cmd, (long) invokeTimeout, TimeUnit.SECONDS, new DeviceEventListener<K21PininutEvent>() {
                        @Override
                        public void onEvent(K21PininutEvent event, Handler handler) {
                            if (event.isProcessing()) {
                                NotifyStep notifyStep = event.getNotifyStep();
                                if (notifyStep == NotifyStep.SLID) {
                                    RNIBPinInputListener rnibPinInputListener = ((RNIBPinInputListener)pinInputListener);
                                    switch (event.getKeyEvent()){
                                        case 10:
                                            rnibPinInputListener.onSlidLeft();
                                            break;
                                        case 11:
                                            rnibPinInputListener.onSlidRight();
                                            break;
                                        case 12:
                                            rnibPinInputListener.onSlidUp();
                                            break;
                                        case 13:
                                            rnibPinInputListener.onSlidDown();
                                            break;
                                        case 14:
                                            rnibPinInputListener.onSlidNumberKey();
                                            break;
                                        case 15:
                                            rnibPinInputListener.onSlidEnter();
                                            break;
                                        case 16:
                                            rnibPinInputListener.onSlidCancel();
                                            break;
                                        case 17:
                                            rnibPinInputListener.onSlidBackSpace();
                                            break;
                                        case 18:
                                            rnibPinInputListener.onSlidNoDigitKey();
                                            break;
                                    }
                                } else if (notifyStep == NotifyStep.ENTER) {
                                    pinInputListener.onKeyPress();
                                } else if (notifyStep == NotifyStep.BACKSPACE) {
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
                    }
                    , new EventMaker<K21PininutEvent>() {
                        @Override
                        public K21PininutEvent makeEvent(DeviceResponse deviceResponse) {
                            K21PininutEvent event = null;
                            try {
                                DeviceResponse response = dealDevResp(deviceResponse);
                                if (response == null) {
                                    event = new K21PininutEvent();
                                } else if (response instanceof CmdStartStandardPinInput.CmdStartStandardPinInputNotificationResponse) {
                                    CmdStartStandardPinInput.CmdStartStandardPinInputNotificationResponse notificationResponse = (CmdStartStandardPinInput.CmdStartStandardPinInputNotificationResponse) response;
                                    if (notificationResponse.getReturnKey() == 0x10) {
                                        event = new K21PininutEvent(NotifyStep.SLID,notificationResponse.getKeyEvent());
                                    } else if (notificationResponse.getReturnKey() == 0x0d) {
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
                                    CmdStartStandardPinInput.CmdStartStandardPinInputResponse cmdResponse = (CmdStartStandardPinInput.CmdStartStandardPinInputResponse) response;
                                    if (KEYCODE_CANCEL == cmdResponse.getReturnKey()) {
                                        devicelogger.debug("[startOfflinePinInput] user cancel input:return code:" + cmdResponse.getReturnKey());
                                        event = new K21PininutEvent();
                                    } else if (KEYCODE_SWIPCARD == cmdResponse.getReturnKey()) {
                                        event = new K21PininutEvent(K21PininutEvent.PinState.SWIPCARD, -1, null, null);
                                    } else if (KEYCODE_ICCARD == cmdResponse.getReturnKey()) {
                                        event = new K21PininutEvent(K21PininutEvent.PinState.ICCARD, -1, null, null);
                                        MECardReader cardreader = (MECardReader) getOwner().getStandardModule(ModuleType.COMMON_CARDREADER);
                                        cardreader.setLastReaderTypes(new CardType[]{CardType.ICCARD});
                                    } else {
                                        byte[] pinblock = cmdResponse.getEncryptPinBlock();
                                        if (null == pinblock) {
                                            pinblock = new byte[8];
                                        }
                                        event = new K21PininutEvent(cmdResponse.getCyherLength(), pinblock, cmdResponse.getKsn());
                                    }
                                }
                            } catch (Exception e) {
                                event = new K21PininutEvent(e);
                            }
                            return event;
                        }

                    });
            lastCmd = cmd;
        } catch (Exception e) {
            e.printStackTrace();
            pinInputListener.onError(ErrorCode.INPUT_PIN_ERROR, "" + e);
        }
    }

    /**
     * 是否支持人脸识别
     * @return
     */
    private boolean isSupFaceRecognition(){
        try {
            String config = NlBuild.VERSION.NL_HARDWARE_CONFIG;// 硬件配置码
            devicelogger.debug("[isSupFaceRecognition] config: "+config);
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
    //Gets X800 second display
    private Display getPresentationDisplay(Context context){
        if(!isX800()){
            return null;
        }
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return null;
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if(!Settings.canDrawOverlays(context)){
                return null;
            }
        }
        DisplayManager mDisplayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        Display[] displays = mDisplayManager.getDisplays();
        if(null == displays || displays.length <= 1){
            return null;
        }
        return displays[displays.length -1];
    }
    private static boolean isX800(){
        return "X800".equals(NlBuild.VERSION.MODEL);
    }
}
