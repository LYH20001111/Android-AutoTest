package com.newland.sdk.me.module.externalPininput;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.newland.ndk.NdkApiManager;
import com.newland.ndk.h.ST_SEC_RSA_KEY;
import com.newland.sdk.me.module.emvl3.impl.EmvL3Global;
import com.newland.sdk.me.module.emvl3.impl.EmvL3Step;
import com.newland.sdk.module.externalPin.MacExtParams;
import com.newland.sdk.module.externalPin.PinpadExtParams;
import com.newland.sdk.module.pin.AlgorithmMode;
import com.newland.sdk.module.pin.CipherExtParams;
import com.newland.sdk.module.pin.CipherMode;
import com.newland.sdk.module.pin.CipherResult;
import com.newland.sdk.module.pin.KeyManagement;
import com.newland.sdk.module.pin.KeyType;
import com.newland.sdk.module.pin.LoadDuktpExtParams;
import com.newland.sdk.module.pin.LoadKeyMode;
import com.newland.sdk.module.pin.LoadMKExtParams;
import com.newland.sdk.module.pin.MacAlgorithm;
import com.newland.sdk.module.pin.MacResult;
import com.newland.sdk.module.pin.PinInputListener;
import com.newland.sdk.module.pin.WorkingKeyType;
import com.newland.sdk.module.serialport.PinpadModel;
import com.newland.sdk.module.sm.SmModule;
import com.newland.sdk.mtype.DeviceInvokeException;
import com.newland.sdk.mtype.DeviceRTException;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.mtype.ProcessTimeoutException;
import com.newland.sdk.mtype.common.ErrorCode;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.utils.ISOUtils;

import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * 国内版sp100重构指令
 * @author youjf
 * @description
 * @date 2020/4/7
 * @since V3.10.13
 */
public class ReMEExternalPininput extends MEExternalPinInput{
    private final int timeout = 5000;
    private String version = null;
    private static final String ALGORITHM = "DES";//加密算法
    private DeviceLogger devicelogger = DeviceLoggerFactory.getLogger("ReMEExternalPininput");
    private SmModule smModule;
    private final int TRANSPORT_DATA_MAX_LENGTH = 4000;
    private final int FIRST_BLOCK = 0 ;//– First block. PIN pad will starts with IV=null.
    private final int NEXT_BLOCK = 1;//– Next Block.
    private final int LAST_BLOCK = 2 ;//– Last Block
    private final int ONLY_BLOCK = 3;// – Only Block
    private AlgorithmMode extAlgorithmMode;

    public ReMEExternalPininput(AbstractDevice device, Context context) {
        super(device,context);
        smModule = (SmModule)device.getStandardModule(ModuleType.SM);
    }

    @Override
    public boolean loadMasterKey(LoadKeyMode mkMode, AlgorithmMode algorithmMode, int masterIndex, byte[] inputData, byte[] checkValue, LoadMKExtParams loadMKExtParams) {
        try {
             if(getPinpadModel() == PinpadModel.SP && checkVersion() && isRestructureCommand()){
                devicelogger.debug("[SDK:loadMasterKey],mkMode:"+mkMode+";algorithmMode:"+algorithmMode+";masterIndex"+";inputData:"+(inputData==null?null:ISOUtils.hexString(inputData))+";checkValue:"+(checkValue==null?null:ISOUtils.hexString(checkValue)));
                boolean initRslt = initTLK();
                if(!initRslt){
                    devicelogger.error("-----initTLK failed----");
                    return false;
                }
                int aesKeyLen = 0;
                int algMode = 2;//0-tr31 block，1-aesk，2-des block,3-sm4
                switch (algorithmMode){
                    case SM4:
                        algMode = 3;
                        break;
                    case DES:
                        algMode = 2;
                        break;
                    case AES:
                        aesKeyLen = inputData.length;
                        algMode = 1;
                        break;
                    default:
                        devicelogger.error("---------unsupport this AlgorithmMode-----");
                        return false;
                }
                if(mkMode==LoadKeyMode.PLAIN){
                    if(algorithmMode == null){
                        algorithmMode = AlgorithmMode.DES;
                    }
                    switch (algorithmMode){
                        case SM4:
                            byte[] kekKey = ISOUtils.hex2byte("35353535353535353535353535353535");
                            inputData = SM4Utils.encodeSMS4(inputData,kekKey);
                            break;
                        case DES:
                            byte[] desKekKey = ISOUtils.hex2byte("383838383838383838383838383838383838383838383838");
                            inputData = encrype3Des(desKekKey,inputData);
                            break;
                        case AES:
                            byte[] aesKekKey = ISOUtils.hex2byte("3737373737373737373737373737373737373737373737373737373737373737");
                            inputData = aesEncry(aesKekKey,inputData);
                            break;
                        default:
                            devicelogger.error("---------unsupport this AlgorithmMode-----");
                            return false;
                    }
                    devicelogger.debug("--------encryedKeydata:"+(inputData==null?null:ISOUtils.hexString(inputData)));
                    boolean result =loadKey(1,0,algMode,aesKeyLen,03,masterIndex,inputData,checkValue,null);
                    devicelogger.debug("---loadmainkey---result:"+result);
                    return result;
                }else if(mkMode==LoadKeyMode.CUSTOM_ENCRYPT){
                    int kekIndex = -1;
                    if(loadMKExtParams!=null){
                        kekIndex = loadMKExtParams.getKekIndex();
                    }

                    devicelogger.debug("------kekIndex:"+kekIndex);
                    boolean result =loadKey(kekIndex,1,algMode,aesKeyLen,03,masterIndex,inputData,checkValue,null);
                    devicelogger.debug("------result:"+result);
                    return result;

                }else if(mkMode==LoadKeyMode.DEFAULT_ENCRYPT){
                    boolean result =loadKey(1,0,algMode,aesKeyLen,03,masterIndex,inputData,checkValue,null);
                    devicelogger.debug("---loadmainkey---result:"+result);
                    return result;
                }else{
                    devicelogger.error("unsupport this LoadKeyMode");
                    return false;
                }
            }else {
                return super.loadMasterKey(mkMode,algorithmMode,masterIndex,inputData,checkValue,loadMKExtParams);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean loadWorkingKey(WorkingKeyType wkType, AlgorithmMode algorithmMode, int masterKeyIndex, int workingKeyIndex, byte[] inputData, byte[] checkValue) {
        try {
            if (getPinpadModel() == PinpadModel.SP && checkVersion()&& isRestructureCommand()) {
                devicelogger.debug("[SDK:loadWorkingKey],wkType:"+wkType+";algorithmMode:"+algorithmMode+";masterKeyIndex:"+masterKeyIndex+";workingKeyIndex:"+workingKeyIndex+";inputData:"+(inputData==null?null:ISOUtils.hexString(inputData))+";checkValue:"+(checkValue==null?null:ISOUtils.hexString(checkValue)));
               // 0-pin，1-mac，2-data
                int keyType = 0;
                if(wkType == WorkingKeyType.MAC){
                    keyType = 1;
                }else if(wkType == WorkingKeyType.TRACK){
                    keyType = 2;
                }
                int algMode = 2;//0-tr31 block，1-aesk，2-des block,3-sm4
                int aesKeyLen = 0;
                switch (algorithmMode){
                    case SM4:
                        algMode = 3;
                        break;
                    case DES:
                        algMode = 2;
                        break;
                    case AES:
                        aesKeyLen = inputData.length;
                        algMode = 1;
                        break;
                    default:
                        devicelogger.error("---------unsupport this AlgorithmMode-----");
                        return false;
                }
                boolean result =loadKey(masterKeyIndex,1,algMode,aesKeyLen,keyType,workingKeyIndex,inputData,checkValue,null);
                devicelogger.debug("---------loadWorkingKey result:"+result);
                return result;
            }else{
                return super.loadWorkingKey(wkType,algorithmMode,masterKeyIndex,workingKeyIndex,inputData,checkValue);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public CipherResult encrypt(KeyManagement keyManagement, AlgorithmMode algorithms, CipherMode cipherMode, int masterKeyIndex, int workingKeyIndex, byte[] input, CipherExtParams params) {
        try {
            if (getPinpadModel() == PinpadModel.SP && checkVersion()&& isRestructureCommand()) {
                devicelogger.debug("[SDK:encrypt],keyManagement:"+keyManagement+";algorithms:"+algorithms+";cipherMode:"+cipherMode+";masterKeyIndex:"+masterKeyIndex+";workingKeyIndex:"+workingKeyIndex+";input:"+(input==null?null:ISOUtils.hexString(input)));
                if(input == null || input.length<=0){
                    devicelogger.error("----------input data shouldn't be null --------");
                    return null;
                }
                devicelogger.debug("-----input:"+(ISOUtils.hexString(input)));

                if(algorithms == null){
                    devicelogger.error("--------algorithms shouldn't be null-------");
                    return null;
                }
                if(cipherMode == null){
                    cipherMode = CipherMode.ECB;
                }
                //算法模式 0–DES , 1–SM4, 2–AES
                int algoMode = 0;
                if(algorithms == AlgorithmMode.SM4){
                    algoMode = 1;
                }else if(algorithms == AlgorithmMode.AES){
                    algoMode = 2;
                }

                if(keyManagement==KeyManagement.DUKPT){
                    algoMode = 0x04;
                }
                int encryMode = 2;//1 = CBC加密;  2 = ECB 加密

                if(cipherMode == CipherMode.CBC){
                    encryMode = 1;
                }
                byte[] cbcInit = null;
                if(params!=null ){
                    cbcInit = params.getCbcInit();
                }
                if(input.length<=TRANSPORT_DATA_MAX_LENGTH){
                    byte[] result = encryAndDecry(algoMode,workingKeyIndex,encryMode,input,cbcInit);
                    devicelogger.debug("------encry result:"+(result==null?null:ISOUtils.hexString(result)));
                    return new CipherResult(result,null);
                }else{
                    int elementLen = 16,maxLen = TRANSPORT_DATA_MAX_LENGTH;
                    if(algorithms == AlgorithmMode.DES){
                        elementLen = 8;
                    }
                    int destLen = (input.length + elementLen - 1) / elementLen * elementLen;
                    devicelogger.debug("-----destLen:"+destLen);
                    int count = destLen / maxLen,remainder = destLen % maxLen;
                    devicelogger.debug("-----count:"+count);
                    devicelogger.debug("-----remainder:"+remainder);
                    byte[] srcData = new byte[destLen];
                    Arrays.fill(srcData, (byte) 0x00);
                    System.arraycopy(input,0,srcData,0,input.length);
                    byte[] destData = new byte[destLen];
                    byte[] srcItem = new byte[maxLen];

                    if(cipherMode == CipherMode.CBC){
                        byte[] cbcIv = params.getCbcInit();
                        devicelogger.debug("-----cbcIv:"+(cbcIv==null?null:ISOUtils.hexString(cbcIv)));
                        for (int i = 0; i < count; i++) {
                            System.arraycopy(srcData,i*maxLen,srcItem,0,maxLen);
                            devicelogger.debug("----srcItem:"+(srcItem==null?null:ISOUtils.hexString(srcItem)));
                            byte[] tempResult = encryAndDecry(algoMode,workingKeyIndex,encryMode,srcItem,cbcIv);
                            System.arraycopy(tempResult,0,destData,i*maxLen,maxLen);

                            cbcIv = new byte[elementLen];
                            System.arraycopy(tempResult,tempResult.length-elementLen,cbcIv,0,elementLen);
                            devicelogger.debug("---i:"+i+"--cbcIv:"+(cbcIv==null?null:ISOUtils.hexString(cbcIv)));

                        }
                        if(remainder!=0){
                            byte[] srcRemData = new byte[remainder];
                            System.arraycopy(srcData,count*maxLen,srcRemData,0,remainder);
                            devicelogger.debug("----srcRemData:"+(srcItem==null?null:ISOUtils.hexString(srcRemData)));
                            byte[] tempResult = encryAndDecry(algoMode,workingKeyIndex,encryMode,srcRemData,cbcIv);
                            System.arraycopy(tempResult,0,destData,count*maxLen,remainder);
                        }
                    }else{//ECB
                        for (int i = 0; i < count; i++) {
                            System.arraycopy(srcData,i*maxLen,srcItem,0,maxLen);
                            devicelogger.debug("----srcItem:"+(srcItem==null?null:ISOUtils.hexString(srcItem)));

                            byte[] tempResult = encryAndDecry(algoMode,workingKeyIndex,encryMode,srcItem,null);
                            System.arraycopy(tempResult,0,destData,i*maxLen,maxLen);
                            devicelogger.debug("--i="+i+"---tempResult:"+(tempResult==null?null:ISOUtils.hexString(tempResult)));
                            devicelogger.debug("----destData:"+(destData==null?null:ISOUtils.hexString(destData)));
                        }
                        if(remainder!=0){
                            byte[] srcRemData = new byte[remainder];
                            System.arraycopy(srcData,count*maxLen,srcRemData,0,remainder);
                            devicelogger.debug("----srcRemData:"+(srcItem==null?null:ISOUtils.hexString(srcRemData)));
                            byte[] tempResult = encryAndDecry(algoMode,workingKeyIndex,encryMode,srcRemData,null);
                            System.arraycopy(tempResult,0,destData,count*maxLen,remainder);
                            devicelogger.debug("----tempResult:"+(tempResult==null?null:ISOUtils.hexString(tempResult)));
                            devicelogger.debug("----destData:"+(destData==null?null:ISOUtils.hexString(destData)));
                        }
                    }
                    devicelogger.debug("--encry--final result:"+(destData==null?null:ISOUtils.hexString(destData)));
                    return new CipherResult(destData,null);
                }

            }else{
                return super.encrypt(keyManagement, algorithms, cipherMode, masterKeyIndex, workingKeyIndex, input, params);
            }
        }catch (Exception e){
            e.printStackTrace();
            if(e.fillInStackTrace() instanceof DeviceRTException){
                throw new DeviceRTException( ((DeviceRTException)e).getCode(),e.getMessage());
            }
            throw new DeviceInvokeException(e.getMessage(),e);
        }
    }

    @Override
    public CipherResult decrypt(KeyManagement keyManagement, AlgorithmMode algorithms, CipherMode cipherMode, int masterKeyIndex, int workingKeyIndex, byte[] input, CipherExtParams params) {
        try {
            if (getPinpadModel() == PinpadModel.SP && checkVersion()&& isRestructureCommand()) {
                devicelogger.debug("[SDK:decrypt],keyManagement:"+keyManagement+";algorithms:"+algorithms+";cipherMode:"+cipherMode+";masterKeyIndex:"+masterKeyIndex+";workingKeyIndex:"+workingKeyIndex+";input:"+(input==null?null:ISOUtils.hexString(input)));
                if(input == null || input.length<=0){
                    devicelogger.error("----------input data shouldn't be null --------");
                    return null;
                }
                devicelogger.debug("-----input:"+(ISOUtils.hexString(input)));
                if(algorithms == null){
                    devicelogger.error("--------algorithms shouldn't be null-------");
                    return null;
                }
                if(cipherMode == null){
                    cipherMode = CipherMode.ECB;
                }
                //算法模式 0–DES , 1–SM4, 2–AES
                int algoMode = 0;
                if(algorithms == AlgorithmMode.SM4){
                    algoMode = 1;
                }else if(algorithms == AlgorithmMode.AES){
                    algoMode = 2;
                }
                if(keyManagement==KeyManagement.DUKPT){
                    algoMode = 0x04;
                }
                int encryMode = 4;//3 = CBC 解密; 4 = ECB 解密
                if(cipherMode == CipherMode.CBC){
                    encryMode = 3;
                }
                byte[] cbcInit = null;
                if(params!=null ){
                    cbcInit = params.getCbcInit();
                }
                if(input.length<=TRANSPORT_DATA_MAX_LENGTH){
                    byte[] result = encryAndDecry(algoMode,workingKeyIndex,encryMode,input,cbcInit);
                    devicelogger.debug("------decry result:"+(result==null?null:ISOUtils.hexString(result)));
                    return new CipherResult(result,null);
                }else{
                    int elementLen = 16,maxLen = TRANSPORT_DATA_MAX_LENGTH;
                    if(algorithms == AlgorithmMode.DES){
                        elementLen = 8;
                    }
                    int destLen = (input.length + elementLen - 1) / elementLen * elementLen;
                    devicelogger.debug("---decry--destLen:"+destLen);
                    int count = destLen / maxLen,remainder = destLen % maxLen;
                    devicelogger.debug("---decry--count:"+count);
                    devicelogger.debug("---decry--remainder:"+remainder);
                    byte[] srcData = new byte[destLen];
                    Arrays.fill(srcData, (byte) 0x00);
                    System.arraycopy(input,0,srcData,0,input.length);
                    byte[] destData = new byte[destLen];
                    byte[] srcItem = new byte[maxLen];

                    if(cipherMode == CipherMode.CBC){
                        byte[] cbcIv = params.getCbcInit();
                        devicelogger.debug("--decry---cbcIv:"+(cbcIv==null?null:ISOUtils.hexString(cbcIv)));
                        for (int i = 0; i < count; i++) {
                            System.arraycopy(srcData,i*maxLen,srcItem,0,maxLen);
                            devicelogger.debug("--decry--srcItem:"+(srcItem==null?null:ISOUtils.hexString(srcItem)));
                            byte[] tempResult = encryAndDecry(algoMode,workingKeyIndex,encryMode,srcItem,cbcIv);
                            System.arraycopy(tempResult,0,destData,i*maxLen,maxLen);

                            cbcIv = new byte[elementLen];
                            System.arraycopy(tempResult,tempResult.length-elementLen,cbcIv,0,elementLen);
                            devicelogger.debug("-decry--i:"+i+"--cbcIv:"+(cbcIv==null?null:ISOUtils.hexString(cbcIv)));

                        }
                        if(remainder!=0){
                            byte[] srcRemData = new byte[remainder];
                            System.arraycopy(srcData,count*maxLen,srcRemData,0,remainder);
                            devicelogger.debug("--decry--srcRemData:"+(srcItem==null?null:ISOUtils.hexString(srcRemData)));
                            byte[] tempResult = encryAndDecry(algoMode,workingKeyIndex,encryMode,srcRemData,cbcIv);
                            System.arraycopy(tempResult,0,destData,count*maxLen,remainder);
                        }
                    }else{//ECB
                        for (int i = 0; i < count; i++) {
                            System.arraycopy(srcData,i*maxLen,srcItem,0,maxLen);
                            devicelogger.debug("--decry--srcItem:"+(srcItem==null?null:ISOUtils.hexString(srcItem)));

                            byte[] tempResult = encryAndDecry(algoMode,workingKeyIndex,encryMode,srcItem,null);
                            System.arraycopy(tempResult,0,destData,i*maxLen,maxLen);
                            devicelogger.debug("-decry-i="+i+"---tempResult:"+(tempResult==null?null:ISOUtils.hexString(tempResult)));
                            devicelogger.debug("--decry--destData:"+(destData==null?null:ISOUtils.hexString(destData)));
                        }
                        if(remainder!=0){
                            byte[] srcRemData = new byte[remainder];
                            System.arraycopy(srcData,count*maxLen,srcRemData,0,remainder);
                            devicelogger.debug("--decry--srcRemData:"+(srcItem==null?null:ISOUtils.hexString(srcRemData)));
                            byte[] tempResult = encryAndDecry(algoMode,workingKeyIndex,encryMode,srcRemData,null);
                            System.arraycopy(tempResult,0,destData,count*maxLen,remainder);
                            devicelogger.debug("--decry--tempResult:"+(tempResult==null?null:ISOUtils.hexString(tempResult)));
                            devicelogger.debug("--decry--destData:"+(destData==null?null:ISOUtils.hexString(destData)));
                        }
                    }
                    devicelogger.debug("--decry--final result:"+(destData==null?null:ISOUtils.hexString(destData)));
                    return new CipherResult(destData,null);
                }

            }else{
                return super.decrypt(keyManagement, algorithms, cipherMode, masterKeyIndex, workingKeyIndex, input, params);
            }
        }catch (Exception e){
            e.printStackTrace();
            if(e.fillInStackTrace() instanceof DeviceRTException){
                throw new DeviceRTException( ((DeviceRTException)e).getCode(),e.getMessage());
            }
            throw new DeviceInvokeException(e.getMessage(),e);
        }
    }

    @Override
    public MacResult calcMac(KeyManagement keyManagement, int macAlgorithm, int masterKeyIndex, int workingKeyIndex, byte[] input, MacExtParams calMacExtParams) {
        try {
            if (getPinpadModel() == PinpadModel.SP && checkVersion()&& isRestructureCommand()) {
                devicelogger.debug("[SDK:calcMac],keyManagement:"+keyManagement+";macAlgorithm:"+macAlgorithm+";masterKeyIndex:"+masterKeyIndex+";workingKeyIndex:"+workingKeyIndex+";input:"+(input==null?null:ISOUtils.hexString(input)));
                int keyMag = 0;//0-MKSK, 1-DUKPT
                if(keyManagement==KeyManagement.DUKPT){
                    keyMag = 1;
                }
                int macAlg = 0;// – X99
                if(macAlgorithm == MacAlgorithm.DES.ECB){
                    macAlg = 2;
                }else if(macAlgorithm == MacAlgorithm.DES.X99){
                    macAlg = 0;
                }else if(macAlgorithm == MacAlgorithm.DES.X919){
                    macAlg = 1;
                }else if(macAlgorithm == MacAlgorithm.DES.M9606){
                    macAlg = 3;
                }else if(macAlgorithm == MacAlgorithm.SM4.X99){
                    macAlg = 4;
                }else if(macAlgorithm == MacAlgorithm.SM4.SM4_UNIONPAY){
                    macAlg = 6;
                }else if(macAlgorithm == MacAlgorithm.AES.X99){
                    macAlg = 5;
                }else {
                    devicelogger.error("---------unsupport this MacAlgorithm-------"+macAlgorithm);
                    return null;
                }
                if (null == input || input.length <= TRANSPORT_DATA_MAX_LENGTH) {
                    byte[] mac = calMac(keyMag,workingKeyIndex,input,ONLY_BLOCK,macAlg);
                    devicelogger.debug("--ONLY_BLOCK---mac result:"+(mac==null?null:ISOUtils.hexString(mac)));
                    return new MacResult(mac, null);
                }
                boolean first = true;
                byte[] remainBuffer = input;
                byte[] mac = null;
                do {
                    if (first) {
                        byte[] buffer = new byte[TRANSPORT_DATA_MAX_LENGTH];
                        System.arraycopy(remainBuffer, 0, buffer, 0, buffer.length);
                        byte[] tempRemainBuffer = new byte[remainBuffer.length - buffer.length];
                        System.arraycopy(remainBuffer, buffer.length, tempRemainBuffer, 0, tempRemainBuffer.length);
                        remainBuffer = tempRemainBuffer;
                        mac = calMac(keyMag,workingKeyIndex,buffer,FIRST_BLOCK,macAlg);
                        first = false;
                    } else {
                        if (remainBuffer.length <= TRANSPORT_DATA_MAX_LENGTH) {
                            mac = calMac(keyMag,workingKeyIndex,remainBuffer,LAST_BLOCK,macAlg);
                            remainBuffer = new byte[0];
                        } else {
                            byte[] buffer = new byte[TRANSPORT_DATA_MAX_LENGTH];
                            System.arraycopy(remainBuffer, 0, buffer, 0, buffer.length);
                            byte[] tempRemainBuffer = new byte[remainBuffer.length - buffer.length];
                            System.arraycopy(remainBuffer, buffer.length, tempRemainBuffer, 0, tempRemainBuffer.length);
                            remainBuffer = tempRemainBuffer;
                            mac = calMac(keyMag,workingKeyIndex,buffer,NEXT_BLOCK,macAlg);
                        }
                    }
                } while (remainBuffer.length > 0);
                devicelogger.debug("----mac = "+(mac==null?null:ISOUtils.hexString(mac)));
                return new MacResult(mac, null);
            }else {
                return super. calcMac(keyManagement, macAlgorithm, masterKeyIndex, workingKeyIndex, input, calMacExtParams);
            }
        }catch (Exception e){
            e.printStackTrace();
            if(e.fillInStackTrace() instanceof DeviceRTException){
                throw new DeviceRTException( ((DeviceRTException)e).getCode(),e.getMessage());
            }
            throw new DeviceInvokeException(e.getMessage(),e);
        }
    }

    private void onEmvL3PinProcess(boolean isOnline,KeyManagement keyManagement, AlgorithmMode algorithmMode, int masterKeyIndex, int workingKeyIndex, String pan,
                                   int timeOut, PinInputListener pinInputListener, PinpadExtParams pinpadExtParams){
        devicelogger.error("[onEmvL3PinProcess] onEmvL3GetPinProcess isOnline="+isOnline);
        devicelogger.debug("[onEmvL3PinProcess] keyManagement="+keyManagement+" algorithmMode="+algorithmMode+" masterKeyIndex="+masterKeyIndex+" workingKeyIndex="+workingKeyIndex+" pan="+pan+" timeOut="+timeOut+" PinpadModel="+getPinpadModel());
        EmvL3Global.setEmvL3GetPinProcess(false);

        byte targetTimeOut = (byte) (timeOut+PinpadPackage.EXTCMD_OFFSETTIME_MS/1000);
        byte[] pwdRangeFb2 = null;

        if(pinpadExtParams != null){
            byte[] pwdRangeFb = pinpadExtParams.getPwdRange();
            if(pwdRangeFb != null) {
                byte[] pwdRangeFb1 = new byte[pwdRangeFb.length];
                int count = 0;
                for(int i=0; i < pwdRangeFb.length; i++){
                    if( pwdRangeFb[i] >= pinpadExtParams.getInputMinLen() && pwdRangeFb[i] <= pinpadExtParams.getInputMaxLen()){
                        pwdRangeFb1[i] = pwdRangeFb[i];
                        count++;
                    }
                }
                pwdRangeFb2 = new byte[count];
                System.arraycopy(pwdRangeFb1,0,pwdRangeFb2,0,count);
            }
        }


        if(!isOnline){
            EmvL3Global.setPinParam((byte)-1,(byte)-1,targetTimeOut,pwdRangeFb2,pinInputListener,pinpadExtParams);
            return;
        }
        if(keyManagement == null || algorithmMode == null){
            return;
        }
        if(getPinpadModel() != PinpadModel.SP_OVERSEAS){
            //return;
        }
        //KeyType:0–Master/Session,1-DUKPT,2–AES,3-SM4
        byte keyType = -1,keyIndex = (byte) workingKeyIndex;
        if(keyManagement == KeyManagement.MKSK){
            if(algorithmMode == AlgorithmMode.DES){
                keyType = 0;
            }else if(algorithmMode == AlgorithmMode.SM4){
                keyType = 3;
            }else if(algorithmMode == AlgorithmMode.AES){
                keyType = 2;
            }
        }else if(keyManagement == KeyManagement.DUKPT){
            if(algorithmMode == AlgorithmMode.DES){
                keyType = 1;
            }
        }
        devicelogger.debug("[onEmvL3PinProcess] keyType(0–Master/Session,1-DUKPT,2–AES,3-SM4)="+keyType+" keyIndex="+keyIndex+" targetTimeOut="+targetTimeOut+" pwdRangeFb2="+(pwdRangeFb2==null?"null":ISOUtils.hexString(pwdRangeFb2)));
        EmvL3Global.setPinParam(keyType,keyIndex,targetTimeOut,pwdRangeFb2,pinInputListener,pinpadExtParams);
    }

    @Override
    public void startExternalPinInput(final KeyManagement keyManagement, final AlgorithmMode algorithmMode, final int masterKeyIndex, final int workingKeyIndex, final String pan,
                                     final int timeOut,final PinInputListener pinInputListener, final PinpadExtParams pinpadExtParams) {
        if(EmvL3Global.getIsEmvL3GetPinProcess()){
            onEmvL3PinProcess(true,keyManagement,algorithmMode,masterKeyIndex,workingKeyIndex,pan,timeOut,pinInputListener,pinpadExtParams);
            return;
        }

        try {
            if (getPinpadModel() == PinpadModel.SP && checkVersion()&& isRestructureCommand()) {
                this.extAlgorithmMode = algorithmMode;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        devicelogger.debug("[SDK:startExternalPinInput],keyManagement:"+keyManagement+";algorithmMode:"+algorithmMode+";masterKeyIndex:"+masterKeyIndex+";workingKeyIndex:"+workingKeyIndex+";pan:"+pan+";timeOut:"+timeOut);

                        if(extAlgorithmMode == null){
                            extAlgorithmMode = AlgorithmMode.DES;
                        }
                        int manageType = 0;
                        if(keyManagement == KeyManagement.DUKPT){
                            manageType = 1;
                        }
                        int panMode = 3;//使用主账号加密，密码不足位数补'F'
                        if(pinpadExtParams!=null && pinpadExtParams.getEncryMode() !=-1){
                            panMode =pinpadExtParams.getEncryMode();
                        }else{
                            switch (extAlgorithmMode){
                                case DES:
                                    if(pan!=null && !pan.equals("")){
                                        panMode = 3;
                                    }else{
                                        panMode = 5;
                                    }
                                    break;
                                case SM4:
                                    panMode = 8;
                                    break;
                                case AES:
                                    panMode = 12;
                                    break;
                            }
                        }
                        int tipMsg = 0x30;
                        if (pinpadExtParams != null && pinpadExtParams.getMsgType() != 0) {
                            tipMsg = pinpadExtParams.getMsgType();
                        }
                        int pinMaxLen = 12;
                        if(pinpadExtParams!=null){
                            pinMaxLen = pinpadExtParams.getInputMaxLen();
                        }
                        byte[] resultData = pinInput(manageType,tipMsg,pan,workingKeyIndex,panMode,timeOut,pinMaxLen);
                        if(resultData!=null && resultData.length>2){
                            int pinblockLen = resultData[2];
                            byte[] pinblock = new byte[pinblockLen];
                            System.arraycopy(resultData,3,pinblock,0,pinblockLen);
                            devicelogger.debug("--------pinblock:"+ISOUtils.hexString(pinblock));
                            if(Arrays.equals(pinblock,new byte[pinblockLen])){//直接按确认键
                                pinInputListener.onFinish(0,new byte[]{},null);
                            }else{
                                byte[] ksn = null;
                                if(keyManagement==KeyManagement.DUKPT){
                                    ksn = new byte[10];
                                    System.arraycopy(resultData,3+pinblockLen,ksn,0,10);
                                    devicelogger.debug("---------ksn:"+ISOUtils.hexString(ksn));
                                }
                                pinInputListener.onFinish(pinblockLen,pinblock,ksn);

                            }
                        }else if(resultData!=null && resultData.length==2){
                            byte[] rspCode = new byte[]{resultData[0],resultData[1]};
                            devicelogger.error("-------rspCode:"+ISOUtils.hexString(rspCode));
                            if(Arrays.equals(rspCode,new byte[]{0x30,0x31})){//无效参数
                                devicelogger.error("-----无效参数--------");
                                pinInputListener.onError(InnerUtils.bytesToInt(new byte[]{0x30,0x31},0,2,true),"无效参数");
                            }else if(Arrays.equals(rspCode,new byte[]{0x30,0x32})){//取消
                                devicelogger.error("-----取消--------");
                                pinInputListener.onCancel();
                            }else if(Arrays.equals(rspCode,new byte[]{0x30,0x33})){//超时
                                devicelogger.error("-----超时--------");
                                pinInputListener.onTimeout();
                            }else if(Arrays.equals(rspCode,new byte[]{0x30,0x34})) {//通常错误
                                devicelogger.error("-----通常错误--------");
                                pinInputListener.onError(InnerUtils.bytesToInt(new byte[]{0x30,0x34},0,2,true),"通常错误");
                            }
                        }else{
                            pinInputListener.onError(-1,"通讯异常");
                        }
                    }
                }).start();

            }else {
                super.startExternalPinInput(keyManagement,algorithmMode,masterKeyIndex,workingKeyIndex,pan,timeOut,pinInputListener,pinpadExtParams);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void startOfflinePinInput(final int keyIndex, final AlgorithmMode algorithmMode, final int timeout, final byte[] modulus, final byte[] exponent, final PinInputListener pinInputListener, @Nullable final PinpadExtParams pinpadExtParams) {
        try {
            if(EmvL3Global.getIsEmvL3GetPinProcess()){
                onEmvL3PinProcess(false,null,null,-1,-1,null,timeout,pinInputListener,pinpadExtParams);
                return;
            }
            if (getPinpadModel() == PinpadModel.SP && checkVersion()&& isRestructureCommand()) {
                this.extAlgorithmMode = algorithmMode;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        devicelogger.debug("[SDK:startOfflinePinInput],keyIndex:"+keyIndex+";algorithmMode:"+algorithmMode+";modulus:"+(modulus==null?null:ISOUtils.hexString(modulus))+";exponent:"+(exponent==null?null:ISOUtils.hexString(exponent)));
                        int pinBlockMode = 0x03;//3-DES, 8-SM4, 0x0C-AES
                        if(extAlgorithmMode == null){
                            extAlgorithmMode = AlgorithmMode.DES;
                        }
                        if(extAlgorithmMode == AlgorithmMode.SM4){
                            pinBlockMode = 0x08;
                        }else if(extAlgorithmMode == AlgorithmMode.AES){
                            pinBlockMode = 0x0C;
                        }
                        int msgType = 0x30;
                        if (pinpadExtParams != null && pinpadExtParams.getMsgType() != 0) {
                            msgType = pinpadExtParams.getMsgType();
                        }
                        offlinePinInput(keyIndex,pinBlockMode,timeout,msgType,modulus,exponent,pinpadExtParams,pinInputListener);
                    }
                }).start();

            }else {
                super.startOfflinePinInput(keyIndex,algorithmMode,timeout,modulus,exponent,pinInputListener,pinpadExtParams);
            }
        }catch (Exception e){
            e.printStackTrace();
            pinInputListener.onError(ErrorCode.INPUT_PIN_FAILED,"Exception:"+e);
        }
    }

    @Override
    public boolean checkKeyIsExist(KeyType keyType, AlgorithmMode algorithmMode, @IntRange(from = 1, to = 200) int keyIndex, @Nullable byte[] checkValue){
       try {
           if (getPinpadModel() == PinpadModel.SP && checkVersion()&& isRestructureCommand()) {
               devicelogger.debug("[SDK:checkKeyIsExist],keyType:"+keyType+";algorithmMode:"+algorithmMode+";keyIndex:"+keyIndex+";checkValue:"+(checkValue==null?null:ISOUtils.hexString(checkValue)));
               byte algo = 0x02;//0-tr31 block，1-aesk，2-des, 3-sm4
               if(algorithmMode == AlgorithmMode.SM4){
                   algo = 0x03;
               }else if(algorithmMode == AlgorithmMode.AES){
                   algo = 0x01;
               }
               byte type; //0-pin，1-mac，2-data，03-master key
               switch (keyType){
                   case PIN_KEY:
                       type = 0x00;
                       break;
                   case MAC_KEY:
                       type = 0x01;
                       break;
                   case TRACK_KEY:
                       type = 0x02;
                       break;
                   case MASTER_KEY:
                       type = 0x03;
                       break;
                       default:
                           throw new UnsupportedOperationException("Don't support this method");

               }

               byte[] reqData = new byte[7];
               reqData[0] = 0x1B;
               reqData[1] = 0x72;
               reqData[2] = algo;
               reqData[3] = type;
               reqData[4] = (byte) keyIndex;
               reqData[5] = 0x0D;
               reqData[6] = 0x0A;
               byte[] data = getPinpadPackage().sendPinpadCmd(null,reqData,PinpadPackage.EXTCMD_TIMEOUT_MS,true);
               devicelogger.debug("--------data:"+(data==null?null:ISOUtils.hexString(data)));
               if (data == null || !Arrays.equals(data,new byte[]{(byte)0xC0,0x01,0x00,0x00})) {
                   devicelogger.error("-------data:"+(data==null?null:ISOUtils.hexString(data)));
                   devicelogger.error("---checkKeyIsExist()---Serial port communication failure");
                   return false;
               }
               byte[] rspCheckKeyCode = getPinpadPackage().getPinpadRspCode(new byte[]{(byte) 0xC0, 0x02, 0x00, 0x0A, 0x00, (byte)0xFF}, PinpadPackage.EXTCMD_TIMEOUT_MS);
               if (Arrays.equals(new byte[]{rspCheckKeyCode[2],rspCheckKeyCode[3]}, new byte[]{0x00,0x00})) {
                   byte[] rspCode = new byte[]{rspCheckKeyCode[8],rspCheckKeyCode[9]};
                   if(Arrays.equals(rspCode,new byte[]{0x30,0x30})){
                       byte kcvLen = rspCheckKeyCode[10];
                       devicelogger.debug("------kcvLen:"+kcvLen);
                       byte[] kcv = new byte[kcvLen];
                       System.arraycopy(rspCheckKeyCode,11,kcv,0,kcvLen);
                       if (null != checkValue && null != kcv) {
                           int length = (checkValue.length > kcv.length ? kcv.length : checkValue.length);
                           byte[] cusKcv = new byte[length];
                           System.arraycopy(kcv, 0, cusKcv, 0, length);
                           boolean isKcvEquals = Arrays.equals(checkValue, cusKcv);
                           if(!isKcvEquals){
                               devicelogger.error("-----KCV ERROR------");
                           }
                           return isKcvEquals;
                       }
                       return true;
                   }else {
                       devicelogger.error("-----------resopnd code:"+ISOUtils.hexString(rspCode));
                   }
                   return false;
               }else{
                   devicelogger.error("-------ret data isn't 0x00 0x00---------");
                   return false;
               }
           }else {
              return super.checkKeyIsExist(keyType,algorithmMode,keyIndex,checkValue);
           }
       }catch (Exception e){
           e.printStackTrace();
           return false;
       }
    }

    @Override
    public boolean loadIPEK(LoadKeyMode loadKeyMode, int ipekIndex, @NonNull byte[] ksn, @NonNull byte[] IPEK, LoadDuktpExtParams loadDuktpExtParams) {
        if(getPinpadModel() == PinpadModel.SP && checkVersion()&& isRestructureCommand()){
            try {
                devicelogger.debug("[SDK:loadIPEK],loadKeyMode:"+loadKeyMode+";ipekIndex:"+ipekIndex+";IPEK:"+(IPEK==null?null:ISOUtils.hexString(IPEK))+";ksn:"+(ksn==null?null:ISOUtils.hexString(ksn)));

                boolean initRslt = initTLK();
                if(!initRslt){
                    devicelogger.error("------initTLK failed------");
                    return false;
                }
                int kekindex = 1;//默认传输密钥索引
                int kekType = 0; //0-TLK,  1-TMK
                if(loadDuktpExtParams!=null && loadDuktpExtParams.getKekIndex()!=-1){
                    kekindex = loadDuktpExtParams.getKekIndex();
                }
                if(loadKeyMode == LoadKeyMode.PLAIN){
                    byte[] desKekKey = ISOUtils.hex2byte("38383838383838383838383838383838");
                    IPEK = encrype3Des(desKekKey,IPEK);
                    devicelogger.debug("--------encry IPEK:"+(IPEK==null?null:ISOUtils.hexString(IPEK)));

                }else if(loadKeyMode == LoadKeyMode.CUSTOM_ENCRYPT){
                    kekType = 1;
                }
                boolean result = loadKey(kekindex,kekType,4,0,4,ipekIndex,IPEK,null,ksn);
                devicelogger.debug("-----load dukpt result:"+result);
                return result;
            }catch (Exception e){
                e.printStackTrace();
            }
            return false;
        }else{
            return super.loadIPEK(loadKeyMode,ipekIndex,ksn,IPEK,loadDuktpExtParams);
        }
    }

    @Override
    public boolean ksnIncrease(int dukptKeyIndex) {
        if(getPinpadModel() == PinpadModel.SP && checkVersion()&& isRestructureCommand()){
            devicelogger.debug("[SDK:ksnIncrease],dukptKeyIndex:"+dukptKeyIndex);

            byte[] result = operKSN(dukptKeyIndex, 0);
            if(result!=null){
                return true;
            }
            return false;
        }else {
            return super.ksnIncrease(dukptKeyIndex);
        }
    }

    @Override
    public byte[] getDukptKsn(int dukptKeyIndex) {
        if(getPinpadModel() == PinpadModel.SP && checkVersion()&& isRestructureCommand()){
            devicelogger.debug("[SDK:getDukptKsn],dukptKeyIndex:"+dukptKeyIndex);
            byte[] result = operKSN(dukptKeyIndex, 1);
            if(result!=null){
                return result;
            }
            return null;
        }else {
            return super.getDukptKsn(dukptKeyIndex);
        }
    }

    @Override
    public boolean deleteKey(KeyType keyType, AlgorithmMode algorithmMode, int keyIndex) {
        if(getPinpadModel() == PinpadModel.SP && checkVersion()&& isRestructureCommand()){
            devicelogger.debug("[SDK:deleteKey],keyType:"+keyType+";algorithmMode:"+algorithmMode+";keyIndex:"+keyIndex);
//            int algo = 1;//1=des，2=aes，3=sm4
//            if(algorithmMode==AlgorithmMode.AES){
//                algo = 2;
//            }else if(algorithmMode==AlgorithmMode.SM4){
//                algo = 3;
//            }
//            int type;//1=tmk,2=tpk,3=tak,4=tdk
//            if(keyType == KeyType.MASTER_KEY){
//                type = 1;
//            }else if(keyType == KeyType.PIN_KEY){
//                type = 2;
//            }else if(keyType == KeyType.MAC_KEY){
//                type = 3;
//            }else if(keyType == KeyType.TRACK_KEY){
//                type = 4;
//            }else {
//                devicelogger.error("---------unsupport this keytype:"+keyType);
//                return false;
//            }
            if(keyIndex<1||keyIndex>250){
                devicelogger.debug("unsupport this keyIndex");
                return false;
            }
            if(null==keyType||keyType.getSpIndex()==-1){
                devicelogger.debug("unsupport this keyType");
                return false;
            }
            if(null==algorithmMode||algorithmMode.getOverseaIndex()==-1){
                devicelogger.debug("unsupport this algorithmMode");
                return false;
            }

            boolean result = clearKey(algorithmMode.getSpIndex(),keyIndex,keyType.getSpIndex());
            if(result){
                return true;
            }
            return false;
        }else{
            return super.deleteKey(keyType,algorithmMode,keyIndex);
        }
    }

    @Override
    public boolean deleteAllKeys() {
        if(getPinpadModel() == PinpadModel.SP && checkVersion()&& isRestructureCommand()){
            devicelogger.debug("[SDK:deleteAllKeys]");

            boolean result = clearKey(-1,-1,-1);
            if(result){
               return true;
            }
            return false;
        }else{
            return super.deleteAllKeys();
        }
    }

    /**
     * 设置/获取ksn
     * @param ipekIndex IPEK索引
     * @param operMode 操作模式，0-ksn自增；1-获取ksn
     * @return
     */
    private byte[] operKSN(int ipekIndex, int operMode){
        try {
            byte[] reqData = new byte[6];
            reqData[0] = 0x1B;
            reqData[1] = 0x59;
            reqData[2] = (byte) ipekIndex;
            reqData[3] = (byte)operMode;
            reqData[4] = 0x0D;
            reqData[5] = 0x0A;
            byte[] data = getPinpadPackage().sendPinpadCmd(null,reqData,PinpadPackage.EXTCMD_TIMEOUT_MS,true);
            devicelogger.debug("--------data:"+(data==null?null:ISOUtils.hexString(data)));
            if (data == null || !Arrays.equals(data,new byte[]{(byte)0xC0,0x01,0x00,0x00})) {
                devicelogger.error("-------data:"+(data==null?null:ISOUtils.hexString(data)));
                devicelogger.error("---operKSN()---Serial port communication failure");
                return null;
            }
            byte[] rspGetSnCode = getPinpadPackage().getPinpadRspCode(new byte[]{(byte) 0xC0, 0x02, 0x00, 0x0A, 0x00, (byte)0xFF}, PinpadPackage.EXTCMD_TIMEOUT_MS);
            devicelogger.debug("-------rspOperKsnCode:"+(rspGetSnCode==null?null:ISOUtils.hexString(rspGetSnCode)));
            if (Arrays.equals(new byte[]{rspGetSnCode[2],rspGetSnCode[3]}, new byte[]{0x00,0x00})) {
                byte[] rspCode = new byte[]{rspGetSnCode[8],rspGetSnCode[9]};
                if(Arrays.equals(rspCode,new byte[]{0x30,0x30})){
                    if(operMode==0){
                        return rspCode;
                    }
                    byte[] ksn = new byte[10];
                    System.arraycopy(rspGetSnCode,10,ksn,0,10);
                    devicelogger.debug("--------ksn:"+ISOUtils.hexString(ksn));
                    return ksn;
                }else if(Arrays.equals(rspCode,new byte[]{0x30,0x31})){//数据长度错
                    devicelogger.error("--------data length error----------");
                }else if(Arrays.equals(rspCode,new byte[]{0x30,0x32})){//无效参数
                    devicelogger.error("--------params error----------");
                }else if(Arrays.equals(rspCode,new byte[]{0x30,0x33})){//密钥不存在
                    devicelogger.error("--------key doesn't exist----------");
                }else{//通用错误
                    devicelogger.error("--------general error----------");
                }
                return null;
            }else{
                devicelogger.error("------ret code error------");
                return null;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    /**
     * @param algo 1=des，2=aes，3=sm4 (该字段只有删除指定密钥时才生效)
     * @param keyIndex 密钥索引(该字段只有删除指定密钥时才生效，删除所有密钥时，不能设置)
     * @param keyType 1=tmk,2=tpk,3=tak,4=tdk (该字段只有删除指定密钥时才生效，删除所有密钥时，不能设置)
     * @return
     */
    public boolean clearKey(int algo,int keyIndex,int keyType){
        try {
            devicelogger.debug("------------clearKey----------keyIndex:"+keyIndex+";keyType");
            byte[] reqData = null;
            if(keyIndex==-1 && keyType==-1){
                reqData = new byte[]{0x1B,0x6E,0x0D, 0x0A};
            }else{
                reqData = new byte[]{0x1B,0x6E,(byte)keyIndex,(byte)keyType, (byte)algo, 0x0D, 0x0A};
            }
            byte[] data = getPinpadPackage().sendPinpadCmd(null,reqData,PinpadPackage.EXTCMD_TIMEOUT_MS,true);
            devicelogger.debug("--------data:"+(data==null?null:ISOUtils.hexString(data)));
            if (data == null || !Arrays.equals(data,new byte[]{(byte)0xC0,0x01,0x00,0x00})) {
                devicelogger.error("-------data:"+(data==null?null:ISOUtils.hexString(data)));
                devicelogger.error("---clearKey()---Serial port communication failure");
                return false;
            }
            byte[] rspGetSnCode = getPinpadPackage().getPinpadRspCode(new byte[]{(byte) 0xC0, 0x02, 0x00, 0x0A, 0x00, (byte)0xFF}, PinpadPackage.EXTCMD_TIMEOUT_MS);
            if (Arrays.equals(new byte[]{rspGetSnCode[rspGetSnCode.length - 1]}, new byte[]{(byte) 0xAA})) {
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 密钥安装
     * @param kekIndex kek索引
     * @param kekType kek类型，0-TLK,  1-TMK
     * @param AlgMode 0-tr31 block，1-aesk，2-des block,3-sm4,4-普通dukpt，5 - tr31格式dukpt
     * @param aesKeyLen 仅针对Aes时有效，16 or 24 or 32
     * @param keyType 0-pin，1-mac，2-data，03-master key, 04-DUKPT, 5-tr31格式dukpt
     * @param keyIndex 1-250
     * @param keyData key data
     * @param kcv des/sm4-3字节，aes-5字节
     * @param ksn 10字节，当keyType为04-DUKPT时有效
     * @return
     */
    private boolean loadKey(int kekIndex,int kekType,int AlgMode,int aesKeyLen,int keyType,int keyIndex,byte[] keyData, byte[] kcv,byte[] ksn){
        try {
            devicelogger.debug("[SDK:loadKey,kekIndex:]"+kekIndex+";kekType:"+kekType+";AlgMode:"+AlgMode+";keyType:"+keyType+";keyIndex"+keyIndex);
            //mode =0：不校验kcv；mode =1：校验kcv；
            byte[] reqData;
            int kcvMode = 0;
            int kcvLen = 0;
            int aesKeyLenFlag = 0;
            int kcvLenFlag = 0;
            int ksnLen = 0;
            if(kcv!=null && kcv.length>=5 && AlgMode==1){//AES-kcv只能5字节
                kcvMode = 1;
                kcvLen = 5;
                if(kcv.length>5){
                    kcv = new byte[]{kcv[0],kcv[1],kcv[2],kcv[3],kcv[4]};
                }
                kcvLenFlag = 1;
            }
            if(kcv!=null && kcv.length>=3 && (AlgMode==2 || AlgMode==3)){//DES/SM4-kcv只能3字节，AES-kcv只能5字节
                kcvMode = 1;
                kcvLen = 3;
                if(kcv.length>3){
                    kcv = new byte[]{kcv[0],kcv[1],kcv[2]};
                }
                kcvLenFlag = 1;
            }
            if(aesKeyLen!=0){
                aesKeyLenFlag = 1;
            }
            if(ksn!=null){
                ksnLen = ksn.length;
            }
            reqData = new byte[5+aesKeyLenFlag + 5 + kcvLenFlag + kcvLen +keyData.length + ksnLen + 2];

            devicelogger.debug("----kcvLen:"+kcvLen);
            reqData[0] = (byte)0x1B;
            reqData[1] = (byte)0x64;
            reqData[2] = (byte)kekIndex;
            reqData[3] = (byte)kekType;
            reqData[4] = (byte)AlgMode;
            if(aesKeyLenFlag!=0){
                reqData[5] = (byte)aesKeyLen;//不是AES，不能传AES的长度
            }
           // reqData[5] = (byte)aesKeyLen;
            reqData[5+aesKeyLenFlag] = (byte)keyType;
            reqData[6+aesKeyLenFlag] = (byte)keyIndex;
            reqData[7+aesKeyLenFlag] = (byte)kcvMode;
            byte[] keyDataLength = InnerUtils.intToBytes(keyData.length,2,true);
            devicelogger.debug("------keyData.length:"+keyData.length+";keyDataLength:"+(keyDataLength==null?null:ISOUtils.hexString(keyDataLength)));
            reqData[8+aesKeyLenFlag] = keyDataLength[0];
            reqData[9+aesKeyLenFlag] = keyDataLength[1];
            System.arraycopy(keyData,0,reqData,10+aesKeyLenFlag,keyData.length);
            devicelogger.debug("-----req:"+(reqData==null?null:ISOUtils.hexString(reqData)));
            //reqData[10+aesKeyLenFlag+keyData.length] = (byte)kcvLen;
            if(kcvLenFlag!=0){
                reqData[10+aesKeyLenFlag+keyData.length] = (byte)kcvLen;
            }
            if(kcv!=null && kcv.length>=3){
                System.arraycopy(kcv,0,reqData,10+kcvLenFlag+aesKeyLenFlag+keyData.length,kcvLen);
            }
            devicelogger.debug("-----req:"+(reqData==null?null:ISOUtils.hexString(reqData)));
            if(ksn!=null){
                System.arraycopy(ksn,0,reqData,10+kcvLenFlag+aesKeyLenFlag+keyData.length+kcvLen,ksnLen);
            }
            reqData[10+kcvLenFlag+aesKeyLenFlag+keyData.length+kcvLen+ksnLen] = 0x0D;
            devicelogger.debug("---reqData:"+(reqData==null?null:ISOUtils.hexString(reqData)));

            reqData[11+kcvLenFlag+aesKeyLenFlag+keyData.length+kcvLen+ksnLen] = 0x0A;
            devicelogger.debug("---reqData:"+(reqData==null?null:ISOUtils.hexString(reqData)));
            byte[] data = getPinpadPackage().sendPinpadCmd(null,reqData,PinpadPackage.EXTCMD_TIMEOUT_MS,true);
            if (data == null || !Arrays.equals(data,new byte[]{(byte)0xC0,0x01,0x00,0x00})) {
                devicelogger.error("-------data:"+(data==null?null:ISOUtils.hexString(data)));
                devicelogger.error("---loadKey()---Serial port communication failure");
                return false;
            }
            byte[] rspLoadKeyCode = getPinpadPackage().getPinpadRspCode(new byte[]{(byte) 0xC0, 0x02, 0x00, 0x0A, 0x00, (byte)0xFF}, PinpadPackage.EXTCMD_TIMEOUT_MS);
            devicelogger.debug("-----rspLoadKeyCode:"+(rspLoadKeyCode==null?null:ISOUtils.hexString(rspLoadKeyCode)));
            byte[] status = new byte[2];
            System.arraycopy(rspLoadKeyCode, 0, status, 0, 2);
            if (Arrays.equals(status, new byte[]{(byte) 0xc0, 0x02})) {
                byte[] resultCode = new byte[2];
                System.arraycopy(rspLoadKeyCode, 8, resultCode, 0, 2);
                if (Arrays.equals(resultCode, new byte[]{0x30, 0x30})) {
                    devicelogger.debug("----------load key sucess---------");
                    return true;
                }else{
                    devicelogger.error("-------respond result code is not 0x30 0x30,resultCode:"+(resultCode==null?null:ISOUtils.hexString(resultCode)));
                    if(Arrays.equals(new byte[]{0x30, 0x31}, resultCode)){
                        devicelogger.error("data length error");
                    }else if(Arrays.equals(new byte[]{0x30, 0x32}, resultCode)){
                        devicelogger.error("Invalid parameters");//无效参数
                    }else if(Arrays.equals(new byte[]{0x30, 0x33}, resultCode)){
                        devicelogger.error("key doesn't exist");//密钥不存在
                    }else if(Arrays.equals(new byte[]{0x30, 0x34}, resultCode)){
                        devicelogger.error("generally error");//通用错误
                    }else{
                        devicelogger.error("other error");//
                    }
                    return false;
                }
            }else{
                devicelogger.error("-------respond data not start with 0xc0 0x02");
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 重构指令，sp100不再有默认的TLK,需要初始化安装默认的tlk
     * 默认安装3组密钥类型为TLK，密钥Id 为1的密钥。
     * 其中Sm4算法安装的密钥明文为16字节的0x35；
     * Aes算法的密钥为32字节的0x37；
     * Des算法的密钥为24字节的0x38。
     * 指令：0x1B 0x6c 0x0d 0x0a
     * @return
     */
    private boolean initTLK(){
        try {
            devicelogger.debug("--------initTLK--------");
            byte[] reqData = new byte[]{0x1B, 0x6C, 0x0D, 0x0A};
            byte[] data = getPinpadPackage().sendPinpadCmd(null,reqData,4000,true);//清空所有秘钥后，首次初始化TLK键盘需要建表，初始化比较慢
            devicelogger.debug("--------data:"+(data==null?null:ISOUtils.hexString(data)));
            if (data == null || !Arrays.equals(data,new byte[]{(byte)0xC0,0x01,0x00,0x00})) {
                devicelogger.error("-------data:"+(data==null?null:ISOUtils.hexString(data)));
                devicelogger.error("---initTLK()---Serial port communication failure");
                return false;
            }
            byte[] rspGetSnCode = getPinpadPackage().getPinpadRspCode(new byte[]{(byte) 0xC0, 0x02, 0x00, 0x0A, 0x00, (byte)0xFF}, PinpadPackage.EXTCMD_TIMEOUT_MS);
            if (Arrays.equals(new byte[]{rspGetSnCode[rspGetSnCode.length - 1]}, new byte[]{(byte) 0xAA})) {
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param keyManagement 0-MKSK, 1-DUKPT
     * @param wkIndex mac密钥索引
     * @param inputData 待计算数据
     * @param blockFlag 0-第一块，1-下一块；2最后一块，3只有一块
     * @param macAlgo mac算法0–X99，1–X919（不支持分包）；2–ECB；3–9606；4–sm4 X99；5–AES; 6–sm4_Unionpay（整包、分包均不支持）
     * @return
     */
    private byte[] calMac(int keyManagement, int wkIndex,byte[] inputData,int blockFlag, int macAlgo){
        byte[] req = new byte[8+inputData.length+2];
        req[0] = 0x1B;
        req[1] = 0x63;
        req[2] = (byte) wkIndex;
        req[3] = (byte)keyManagement;//0-MKSK, 1-DUKPT
        req[4] = (byte)macAlgo;//0–X99，1–X919；2–ECB；3–9606；4–sm4 X99；5–AES; 6–sm4 X919
        req[5] = (byte)blockFlag;// 0-第一块，1-下一块；2最后一块，3只有一块 (新版键盘一次支持2500，去掉包头包尾，就算2000)
        byte[] inputDataLen = InnerUtils.intToBytes(inputData.length,2,true);
        devicelogger.debug("------inputDataLen:"+(inputDataLen==null?null:ISOUtils.hexString(inputDataLen)));
        System.arraycopy(inputDataLen,0,req,6,2);
        System.arraycopy(inputData,0,req,8,inputData.length);
        req[8+inputData.length] = 0x0D;
        req[9+inputData.length] = 0x0A;
        devicelogger.debug("-------req:"+(req==null?null:ISOUtils.hexString(req)));
        byte[] data = getPinpadPackage().sendPinpadCmd(null,req,PinpadPackage.EXTCMD_TIMEOUT_MS,true);
        if (data == null || !Arrays.equals(data,new byte[]{(byte)0xC0,0x01,0x00,0x00})) {
            devicelogger.error("-------data:"+(data==null?null:ISOUtils.hexString(data)));
            devicelogger.error("---calMac()---Serial port communication failure");
            return null;
        }
        byte[] calMacCode = getPinpadPackage().getPinpadRspCode(new byte[]{(byte) 0xC0, 0x02, 0x00, 0x0A, (byte)0xFF, (byte)0xFF}, PinpadPackage.EXTCMD_TIMEOUT_MS);
        devicelogger.debug("-----calMacCode:"+(calMacCode==null?null:ISOUtils.hexString(calMacCode)));
        byte[] status = new byte[2];
        System.arraycopy(calMacCode, 0, status, 0, 2);
        if (Arrays.equals(status, new byte[]{(byte) 0xc0, 0x02})) {
            byte[] rspCode = new byte[2];
            System.arraycopy(calMacCode, 8, rspCode, 0, 2);
            devicelogger.debug("--------calMac--rspCode:"+(ISOUtils.hexString(rspCode)));
            if (Arrays.equals(rspCode, new byte[]{(byte) 0x30, 0x30})) {
                byte[] dataLen = new byte[]{calMacCode[10]};
                int resultDataLen  = InnerUtils.bytesToInt(dataLen,0,1,true);
                devicelogger.debug("------calMacCode---dataLen:"+resultDataLen);
                byte[] rsultData = new byte[resultDataLen];

                System.arraycopy(calMacCode, 11, rsultData, 0, resultDataLen);
                devicelogger.debug("-----calMacCode---rsultData"+(rsultData==null?null:ISOUtils.hexString(rsultData)));
                return rsultData;
            }else {
                devicelogger.error("----calMac----respond code isn't 0x30 0x30:"+(status==null?null:ISOUtils.hexString(status)));
                if(Arrays.equals(new byte[]{0x30, 0x31}, rspCode)){
                    devicelogger.error("data length error");
                    throw new DeviceRTException(01,"data length error");
                }else if(Arrays.equals(new byte[]{0x30, 0x32}, rspCode)){
                    devicelogger.error("Invalid parameters");//无效参数
                    throw new DeviceRTException(02,"Invalid parameters");
                }else if(Arrays.equals(new byte[]{0x30, 0x33}, rspCode)){
                    devicelogger.error("key doesn't exist");//密钥不存在
                    throw new DeviceRTException(03,"key doesn't exist");
                }else if(Arrays.equals(new byte[]{0x30, 0x34}, rspCode)){
                    devicelogger.error("generally error");//通用错误
                    throw new DeviceRTException(04,"generally error");
                }else if(Arrays.equals(new byte[]{0x30, 0x35}, rspCode)){
                    devicelogger.error(" data divided failed");//分包错误
                    throw new DeviceRTException(05,"data divided failed");
                }else{
                    devicelogger.error("other error");//
                    throw new DeviceRTException(10,"other error");
                }
            }
        }else {
            devicelogger.error("-----calMac---Ret_status isn't 0xC0 0x02:"+(status==null?null:ISOUtils.hexString(status)));
            throw new DeviceRTException(11,"Ret_status isn't 0xC0 0x02");

        }
        //return null;
    }

    /**
     * @param keyManageType 0-mksk,1-dukpt
     * @param msgType <p>提示信息类型，0x30-显示"请输入密码"，有语音；</p>
     *                <p>0x31-显示"请再输入密码"，有语音；</p>
     *                <p>0x32-显示"请输入密码"，无语音；</p>
     *                <p>0x33-显示"请再输入密码"，无语音；</p>
     * @param pan 卡号
     * @param keyIndex pin 密钥索引
     * @param panMode 卡号以及pin补位模式 3-使用主账号，密码不足位补F
     * @param timeout 超时时间5-200 单位：s
     * @param maxPinLen 最大密码长度
     * @return
     */
    private byte[] pinInput(int keyManageType,int msgType, String pan, int keyIndex, int panMode,int timeout,int maxPinLen){
        try {

            if (pan == null || "".equals(pan)) {
                devicelogger.debug("Don't have pan.");
                pan = "00000000";

            }
//            else if (pan.length() < 16) {
//                int i = 16 - pan.length();
//                for (int j = 0; j < i; j++) {
//                    pan = pan + "0";
//                }
//            }
            int dukptFlag = 0;
            if(keyManageType == 1){
                dukptFlag = 1;
            }
            byte[] reqData = new byte[11+dukptFlag+pan.length()];
            reqData[0] = 0x1B;
            reqData[1] = 0x65;
            reqData[2] = (byte)keyManageType;
            reqData[3] = (byte)msgType;
            if(dukptFlag == 1){
                reqData[4] = (byte)0x00;//ksn模式，0-不自增ksn,1-输完密码，自增ksn
            }

            reqData[4+dukptFlag] = (byte)pan.length();

            System.arraycopy(pan.getBytes(),0,reqData,5+dukptFlag,pan.length());
            reqData[5+dukptFlag+pan.length()] = (byte)keyIndex;
            reqData[6+dukptFlag+pan.length()] = (byte)panMode;
            reqData[7+dukptFlag+pan.length()] = (byte)timeout;
            devicelogger.debug("----------timeout:"+timeout);
            reqData[8+dukptFlag+pan.length()] = (byte)maxPinLen;
            reqData[9+dukptFlag+pan.length()] = 0x0D;
            reqData[10+dukptFlag+pan.length()] = 0x0A;
            devicelogger.debug("-----pinInput--req:"+(reqData==null?null:ISOUtils.hexString(reqData)));
            byte[] data = getPinpadPackage().sendPinpadCmd(null,reqData,(timeout)*1000+PinpadPackage.EXTCMD_OFFSETTIME_MS,true);
            if (data == null || !Arrays.equals(data,new byte[]{(byte)0xC0,0x01,0x00,0x00})) {
                devicelogger.error("-----pinInput--data:"+(data==null?null:ISOUtils.hexString(data)));
                devicelogger.error("---pinInput()---Serial port communication failure");
                return null;
            }
            byte[] pinInputCode = getPinpadPackage().getPinpadRspCode(new byte[]{(byte) 0xC0, 0x02, 0x00, 0x0A, (byte)0x00, (byte)0xFF}, timeout*1000+PinpadPackage.EXTCMD_OFFSETTIME_MS);
            devicelogger.debug("-----pinInputCode:"+(pinInputCode==null?null:ISOUtils.hexString(pinInputCode)));
            byte[] status = new byte[2];
            System.arraycopy(pinInputCode, 0, status, 0, 2);
            if (Arrays.equals(status, new byte[]{(byte) 0xc0, 0x02})) {
                int dataLen = InnerUtils.bytesToInt(new byte[]{pinInputCode[7]},0,1,true);
                devicelogger.debug("-------dataLen:"+dataLen);

                byte[] rspData = new byte[dataLen];
                System.arraycopy(pinInputCode, 8, rspData, 0, dataLen);
                devicelogger.debug("-------rspData:"+ISOUtils.hexString(rspData));
                return rspData;
            }else{
                devicelogger.error("-----pinInputCode isn't start with  0xc0, 0x02------");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param mkIndex 主密钥索引
     * @param pinBlockMode pinblock模式， 3-DES, 8-SM4, 0x0C-AES
     * @param timeout 超时时间，单位秒
     * @param msgType 0x30-"请输入密码，有语音" 0x31-"请再输入密码，有语音"  0x32-"请输入密码，无语音" 0x33-"请再输入密码，无语音"
     * @param modulus
     * @param exponent
     * @param pinpadExtParams
     * @param pinInputListener
     */
    private void offlinePinInput(int mkIndex,int pinBlockMode, int timeout,int msgType, byte[] modulus, byte[] exponent,PinpadExtParams pinpadExtParams,PinInputListener pinInputListener){
        try {
            String pan = "1111111111111111111";//默认卡号
            byte[] cipherPan = pan.getBytes();
            byte[] reqData = new byte[14 + cipherPan.length];
            reqData[0] = 0x1B;
            reqData[1] = 0x71;
            reqData[2] = 0x00;//0-mk/sk
            if(pinpadExtParams!=null && pinpadExtParams.getUsePinKey()){
                reqData[3] = 0x00;//0-用现有的pin密钥，1-随机生成pin密钥
            }else{
                reqData[3] = 0x01;//0-用现有的pin密钥，1-随机生成pin密钥
            }
            reqData[4] = (byte)mkIndex;//主密钥索引
            reqData[5] = (byte)pinBlockMode;
            reqData[6] = 0x10;//默认用最小长度，不能超过主密钥长度
            reqData[7] = (byte) cipherPan.length;
            System.arraycopy(cipherPan, 0, reqData, 8, cipherPan.length);
            byte maxPinLen = 0x0C;
            if (pinpadExtParams != null && pinpadExtParams.getInputMaxLen() > 0) {
                maxPinLen = (byte) pinpadExtParams.getInputMaxLen();
            }
            reqData[8+cipherPan.length] = maxPinLen;
            reqData[9+cipherPan.length] = 0x00;//0-需要按确认件返回， 1-不需要按确认件返回
            reqData[10+cipherPan.length] = (byte) timeout;
            reqData[11+cipherPan.length] = (byte) msgType;
            reqData[12+cipherPan.length] = 0x0D;
            reqData[13+cipherPan.length] = 0x0A;
            devicelogger.debug("-----offlinePinInput--req:"+(reqData==null?null:ISOUtils.hexString(reqData)));
            byte[] data = getPinpadPackage().sendPinpadCmd(null,reqData,(timeout)*1000+PinpadPackage.EXTCMD_OFFSETTIME_MS,true);
            if (data == null || !Arrays.equals(data,new byte[]{(byte)0xC0,0x01,0x00,0x00})) {
                devicelogger.error("-----offlinePinInput--data:"+(data==null?null:ISOUtils.hexString(data)));
                devicelogger.error("---offlinePinInput()---Serial port communication failure");
                pinInputListener.onError(ErrorCode.INPUT_PIN_ERROR,"Serial port communication failure");
                return;
            }
            byte[] offlinePinInputCode = getPinpadPackage().getPinpadRspCode(new byte[]{(byte) 0xC0, 0x02, 0x00, 0x0A, (byte)0x00, (byte)0xFF}, timeout*1000+PinpadPackage.EXTCMD_OFFSETTIME_MS);
            devicelogger.debug("-----offlinePinInputCode:"+(offlinePinInputCode==null?null:ISOUtils.hexString(offlinePinInputCode)));
            byte[] status = new byte[2];
            System.arraycopy(offlinePinInputCode, 0, status, 0, 2);
            if (Arrays.equals(status, new byte[]{(byte) 0xc0, 0x02})) {
                byte[] rspCode = new byte[]{offlinePinInputCode[8],offlinePinInputCode[9]};
                if(Arrays.equals(rspCode,new byte[]{0x30,0x30})){
                    byte pinblockLen = offlinePinInputCode[10];
                    byte[] pinBlock = new byte[pinblockLen];
                    devicelogger.debug("------pinblockLen:"+pinblockLen);
                    System.arraycopy(offlinePinInputCode, 11, pinBlock, 0, pinblockLen);
                    devicelogger.debug("------pinBlock:"+ISOUtils.hexString(pinBlock));

                    byte encryptedPinKeyLen = offlinePinInputCode[11 + pinblockLen];
                    byte[] encryptedPinKey = new byte[encryptedPinKeyLen];
                    System.arraycopy(offlinePinInputCode, 12 + pinBlock.length, encryptedPinKey, 0, encryptedPinKeyLen);
                    devicelogger.debug("---encryptedPinKey:" + (encryptedPinKey == null ? null : ISOUtils.hexString(encryptedPinKey)));
                    ST_SEC_RSA_KEY rsaKey = null;
                    byte offlinePinType = 0x00;//0 - 脱机明文PIN， 1 - 脱机密文PIN
                    if (exponent != null) {
                        offlinePinType = 0x01;
                        rsaKey = new ST_SEC_RSA_KEY();
                        rsaKey.usBits = modulus.length * 8;
                        rsaKey.sExponent = exponent;
                        rsaKey.sModulus = modulus;
                        //rsaKey.reverse[0] = 0x04;
                    }

                    byte[] out = new byte[16];
                    cipherPan = "1111111111111111111\0".getBytes();
                    devicelogger.debug("-----cipherPan-----" + ISOUtils.hexString(cipherPan) + ";offlinePinType:" + offlinePinType);
                    int ret = NdkApiManager.getNdkApiManager().getSecN().NDK_SecVerifyPIN((byte) mkIndex, (byte)pinBlockMode, encryptedPinKeyLen, encryptedPinKey,
                            cipherPan, pinBlock.length, pinBlock, rsaKey, out, offlinePinType);
                    if (ret == 0) {
                        byte[] finalPinBlock = new byte[8];
                        System.arraycopy(out, 0, finalPinBlock, 0, 8);
                        devicelogger.debug("----finalPinBlock:" + (finalPinBlock == null ? null : ISOUtils.hexString(finalPinBlock)));

                        pinInputListener.onFinish(finalPinBlock.length, finalPinBlock, null);
                    } else {
                        devicelogger.error("-----------NDK_SecVerifyPIN failed,ret=" + ret);
                        pinInputListener.onError(ErrorCode.INPUT_PIN_ERROR, "NDK_SecVerifyPIN failed,ret=" + ret);
                        return;
                    }
                }else if(Arrays.equals(rspCode,new byte[]{0x30,0x31})){//参数错误
                    pinInputListener.onError(ErrorCode.INPUT_PIN_ERROR, "param error");
                }else if(Arrays.equals(rspCode,new byte[]{0x30,0x32})){//取不到密钥
                    pinInputListener.onError(ErrorCode.INPUT_PIN_ERROR, "failed to fetch encrypted pin key");
                }else if(Arrays.equals(rspCode,new byte[]{0x30,0x33})){//超时
                    pinInputListener.onTimeout();
                }else if(Arrays.equals(rspCode,new byte[]{0x30,0x34})){//通用错误
                    pinInputListener.onError(ErrorCode.INPUT_PIN_ERROR, "general error");
                }else if(Arrays.equals(rspCode,new byte[]{0x30,0x35})){//取消
                    pinInputListener.onCancel();
                }else{
                    devicelogger.error("errorcode:"+(rspCode==null?null:ISOUtils.hexString(rspCode)));
                    pinInputListener.onError(ErrorCode.INPUT_PIN_ERROR, "other error"+(rspCode==null?null:ISOUtils.hexString(rspCode)));
                    return ;
                }
            }else{
                devicelogger.error("-----offlinePinInputCode isn't start with  0xc0, 0x02------");
            }

        }catch (Exception e){
            e.printStackTrace();
            pinInputListener.onError(ErrorCode.INPUT_PIN_FAILED,"Exception:"+e);
        }
    }

    /**
     * V3.2.19版本的键盘才支持重构指令
     * @return
     */
    private boolean checkVersion(){
        try {
            devicelogger.debug("---checkVersion------"+version);
            version = getVersion();
            devicelogger.debug("---version------"+version);
            if("V3.2.19".compareTo(version) <= 0){
                return true;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param algoMode 算法模式
     *                 0–DES ,
     *                 1–SM4,
     *                 2–AES,
     *                 3–DUKPT_TAK,
     *                 4–DUKPT_TDK,
     *                 5–DUKPT_TPK
     *                 0x10-DUKPT
     * @param keyIndex 密钥索引
     * @param encryMode 加解密模式 1 = CBC加密; 2 = ECB加密; 3 = CBC解密; 4 = ECB解密
     * @param input 待加解密数据
     * @param cbcInit CBC初始化数据
     * @return 加解密结果
     */
    private byte[] encryAndDecry(int algoMode, int keyIndex, int encryMode, byte[] input, byte[] cbcInit){
        byte[] reqData;
        if(cbcInit!=null && cbcInit.length>0){
            reqData = new byte[7+input.length + 1 +cbcInit.length + 2];
        }else{
            reqData = new byte[7+input.length + 2];
        }
        reqData[0] = 0x1B;
        reqData[1] = 0x6A;
        reqData[2] = (byte) algoMode;
        reqData[3] = (byte) keyIndex;
        reqData[4] = (byte) encryMode;
        byte[] inputDataLen = InnerUtils.intToBytes(input.length,2,true);
        System.arraycopy(inputDataLen,0,reqData,5,2);

        System.arraycopy(input,0,reqData,7,input.length);
        if(cbcInit!=null && cbcInit.length>0){
            reqData[7+input.length] = (byte)cbcInit.length;
            System.arraycopy(cbcInit,0,reqData,8+input.length,cbcInit.length);
            devicelogger.debug("--------cbcInit--reqData:"+(ISOUtils.hexString(reqData)));
            reqData[8+input.length + cbcInit.length] = 0x0D;
            reqData[9+input.length + cbcInit.length] = 0x0A;
        }else{
            reqData[7+input.length] = 0x0D;
            reqData[8+input.length] = 0x0A;
        }
        devicelogger.debug("--------reqData:"+(reqData==null?null:ISOUtils.hexString(reqData)));
        byte[] data = getPinpadPackage().sendPinpadCmd(null,reqData,PinpadPackage.EXTCMD_TIMEOUT_MS,true);
        if (data == null || !Arrays.equals(data,new byte[]{(byte)0xC0,0x01,0x00,0x00})) {
            devicelogger.error("-------data:"+(data==null?null:ISOUtils.hexString(data)));
            devicelogger.error("---encryAndDecry()---Serial port communication failure");
            return null;
        }
        byte[] rspEncryAndDecryCode = getPinpadPackage().getPinpadRspCode(new byte[]{(byte) 0xC0, 0x02, 0x00, 0x0A, (byte)0xFF, (byte)0xFF}, PinpadPackage.EXTCMD_TIMEOUT_MS);
        devicelogger.debug("-----rspEncryAndDecryCode:"+(rspEncryAndDecryCode==null?null:ISOUtils.hexString(rspEncryAndDecryCode)));
        byte[] status = new byte[2];
        System.arraycopy(rspEncryAndDecryCode, 0, status, 0, 2);
        if (Arrays.equals(status, new byte[]{(byte) 0xc0, 0x02})) {
            byte[] rspCode = new byte[2];
            System.arraycopy(rspEncryAndDecryCode, 9, rspCode, 0, 2);
            devicelogger.debug("----------rspCode:"+(ISOUtils.hexString(rspCode)));
            if (Arrays.equals(rspCode, new byte[]{(byte) 0x30, 0x30})) {
                byte[] dataLen = new byte[2];
                System.arraycopy(rspEncryAndDecryCode, 11, dataLen, 0, 2);
                int resultDataLen  = InnerUtils.bytesToInt(dataLen,0,2,true);
                devicelogger.debug("---------dataLen:"+resultDataLen);
                byte[] rsultData = new byte[resultDataLen];

                System.arraycopy(rspEncryAndDecryCode, 13, rsultData, 0, resultDataLen);
                devicelogger.debug("---rsultData"+(rsultData==null?null:ISOUtils.hexString(rsultData)));
                return rsultData;
            }else {
                devicelogger.error("--------respond code isn't 0x30 0x30:"+(status==null?null:ISOUtils.hexString(status)));
                if(Arrays.equals(new byte[]{0x30, 0x31}, rspCode)){
                    devicelogger.error("data length error");
                    throw new DeviceRTException(01,"data length error");
                }else if(Arrays.equals(new byte[]{0x30, 0x32}, rspCode)){
                    devicelogger.error("Invalid parameters");//无效参数
                    throw new DeviceRTException(02,"Invalid parameters");
                }else if(Arrays.equals(new byte[]{0x30, 0x33}, rspCode)){
                    devicelogger.error("key doesn't exist");//密钥不存在
                    throw new DeviceRTException(03,"key doesn't exist");
                }else if(Arrays.equals(new byte[]{0x30, 0x34}, rspCode)){
                    devicelogger.error("generally error");//通用错误
                    throw new DeviceRTException(04,"generally error");
                }else{
                    devicelogger.error("other error");//
                    throw new DeviceRTException(10,"other error");
                }
            }
        }else {
            devicelogger.error("--------Ret_status isn't 0xC0 0x02:"+(status==null?null:ISOUtils.hexString(status)));
            throw new DeviceRTException(11,"Ret_status isn't 0xC0 0x02:"+(status==null?null:ISOUtils.hexString(status)));
        }
    }

    /**
     * 3DES加密
     * @param key 加密密钥(16字节长度)
     * @param source 明文
     * @return byte[] 密文
     */
    public static byte[] encrype3Des(byte[] key, byte[] source) {
        if (key==null || key.length<1 || source==null || source.length<1) {
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
            if(source.length>8) {//判断是否有多个8字节数据块
                //初始化下一个数据块
                byte[] tempSourceBytes = new byte[source.length-8];
                System.arraycopy(source, 8, tempSourceBytes, 0, source.length-8);
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
     * @param keybyte 加密密钥
     * @param src 明文数据
     * @return byte[] 密文
     */
    public static byte[] encryptDes(byte[] keybyte, byte[] src) {
        if (keybyte==null || keybyte.length<1 || src==null || src.length<1) {
            return null;
        }
        try {
            // 生成密钥
            SecretKey deskey = new SecretKeySpec(keybyte, ALGORITHM);
            // 加密
            Cipher cipher = Cipher.getInstance(ALGORITHM + "/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, deskey);
            return cipher.doFinal(src);
        } catch (Exception e) {
            //logger.error("DES加密异常", e);
        }
        return null;
    }

    /**
     * DES解密
     * @param keybyte 解密密钥
     * @param src 待解密的密文数据
     * @return byte[] 明文
     */
    public static byte[] decryptDes(byte[] keybyte, byte[] src) {
        if (keybyte==null || keybyte.length<1 || src==null || src.length<1) {
            return null;
        }
        try {
            // 生成密钥
            SecretKey deskey = new SecretKeySpec(keybyte, ALGORITHM);
            // 解密
            Cipher cipher = Cipher.getInstance(ALGORITHM + "/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, deskey);
            return cipher.doFinal(src);
        } catch (Exception e) {
            //	logger.error("DES解密异常", e);
        }
        return null;
    }


    /**
     * @param keyData 密钥
     * @param source
     * @return
     */
    private byte[] aesEncry(byte[] keyData,byte[] source){
        try{
//            //4.获得原始对称密钥的字节数组
//            byte [] raw=original_key.getEncoded();
            //5.根据字节数组生成AES密钥
            SecretKey key=new SecretKeySpec(keyData, "AES");
            //6.根据指定算法AES自成密码器
            Cipher cipher=Cipher.getInstance("AES");
            //7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密解密(Decrypt_mode)操作，第二个参数为使用的KEY
            cipher.init(Cipher.ENCRYPT_MODE, key);
            //9.根据密码器的初始化方式--加密：将数据加密
            byte [] byte_AES=cipher.doFinal(source);
            devicelogger.debug("---AES---encryResult:"+(byte_AES==null?null:ISOUtils.hexString(byte_AES)));
            return byte_AES;

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
