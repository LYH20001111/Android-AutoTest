package com.newland.sdk.me.module.emvl3.impl;

import android.support.annotation.NonNull;
import android.util.Log;

import com.newland.sdk.mtypex.module.common.emv.CommonUtils;
import com.newland.sdk.me.module.emv.AbstractEMVTransController;
import com.newland.sdk.me.module.emv.EMVInnerUtils;
import com.newland.sdk.me.module.emvl3.external.EmvL3Const;
import com.newland.sdk.module.cardreader.CardType;
import com.newland.sdk.module.emv.AccountType;
import com.newland.sdk.module.emv.EMVTransController;
import com.newland.sdk.module.emv.EMVTransInfo;
import com.newland.sdk.module.emv.EmvPackager;
import com.newland.sdk.module.emv.OnlineTransactionData;
import com.newland.sdk.module.emv.TransactionExtParams;
import com.newland.sdk.me.module.emvl3.EMVL3Module;
import com.newland.sdk.me.module.emvl3.TransactionResult;
import com.newland.sdk.me.module.emvl3.utils.METhreadExecutors;
import com.newland.sdk.module.emv.TransactionType;
import com.newland.sdk.mtype.common.Const;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.utils.ISOUtils;
import com.newland.sdk.utils.TLVPackage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

/**
 * @Description 实现EMVL3控制器
 * @Author wuhh
 * @Date 2020/9/8
 */
public class EmvL3CtrlProcess implements EMVTransController {

    private DeviceLogger deviceLogger = DeviceLoggerFactory.getLogger("EmvL3CtrlProcess");
    private EmvL3CallBackHelper mEmvL3CallBackHelper;
    private EMVL3Module mMEEmvL3;
    private MENEmvL3 mEMVModule;
    private volatile boolean isCancelPerform;

    public void init(EMVL3Module emvl3Module,EmvL3CallBackHelper emvL3CallBackHelper,MENEmvL3 emvModule){
        this.mMEEmvL3 = emvl3Module;
        this.mEmvL3CallBackHelper = emvL3CallBackHelper;
        this.mEMVModule = emvModule;
    }

    @Override
    public boolean preproccess(int transactionType, BigDecimal amount, boolean forceOnline, TransactionExtParams transactionExtParams) {
        deviceLogger.debug("preproccess");
        TLVPackage tlvpackage = EMVInnerUtils.newTlvPackage();
        long amountFL = amount.setScale(2, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).toBigInteger().longValue();
        String tmp = String.format("%012d",amountFL);
        tlvpackage.append(0x9F02, ISOUtils.hex2byte(tmp));

        if(transactionExtParams != null && transactionExtParams.getOtherAmount() != null){
            long otherAmountFL = transactionExtParams.getOtherAmount().setScale(2, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).toBigInteger().longValue();
            tmp = String.format("%012d", otherAmountFL);
            tlvpackage.append(0x9F03, ISOUtils.hex2byte(tmp));
        }else {
            tlvpackage.append(0x9F03, String.format("%012d", 0));
        }

        tlvpackage.append(0x9C, transactionType+"");
        byte[] data = tlvpackage.pack();
        int ret = mMEEmvL3.preProcessTransaction(data);
        deviceLogger.debug("[preproccess]->preProcessTransaction ret="+ret);
        return ret == 0;
    }

    @Override
    public void startEMV(final int transactionType, final BigDecimal amount, final boolean forceOnline, final TransactionExtParams transactionExtParams) {
        Log.d("SDKVersion", "startEMV3,SDKVersion:"+ CommonUtils.getInstance().getSDKVersion());
        METhreadExecutors.startThread(new Runnable() {
            @Override
            public void run() {
                deviceLogger.debug("[startEMV] transactionType="+transactionType+" amount="+amount+" forceOnline="+forceOnline+" transactionExtParams="+transactionExtParams);

                TLVPackage tlvpackage = EMVInnerUtils.newTlvPackage();
                if(amount!=null){
                    long amountFL = amount.setScale(2, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).toBigInteger().longValue();
                    String tmp = String.format("%012d",amountFL);
                    tlvpackage.append(0x9F02, ISOUtils.hex2byte(tmp));
                }else{
                    tlvpackage.append(0x9F02, ISOUtils.hex2byte("000000000000"));
                }


                if(transactionExtParams != null && transactionExtParams.getOtherAmount() != null){
                    long otherAmountFL = transactionExtParams.getOtherAmount().setScale(2, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).toBigInteger().longValue();
                    String tmp = String.format("%012d", otherAmountFL);
                    tlvpackage.append(0x9F03, ISOUtils.hex2byte(tmp));
                }

                tlvpackage.append(0x9C, transactionType+"");

                tlvpackage.append(0x9A, ISOUtils.hex2byte(new SimpleDateFormat("yyMMdd", Locale.ENGLISH).format(new Date())));
                tlvpackage.append(0x9F21, ISOUtils.hex2byte(new SimpleDateFormat("HHmmss", Locale.ENGLISH).format(new Date())));

                byte[] online = new byte[]{0};
                if(forceOnline){
                    online[0] = 1;
                }
                //Force Online Enable
                tlvpackage.append(0x1F8126,online);

                //App Selection Configuration
//                tlvpackage.append(0x1F8164,new byte[]{(byte) (EmvL3Constant.APPSELECTION_CONTACT|EmvL3Constant.APPSELECTION_CONTACTLESS)});



                //Retry Times before Fallback
                tlvpackage.append(0x1F8127,new byte[]{(byte) (0x02)});
                if(transactionType == TransactionType.SIMPLE){
                    tlvpackage.append(0x1F8128,new byte[]{(byte) (0x01)});
                    //This data item used to control callback to upper computer.
                    byte callBackFlag = (byte) (
                            EmvL3Constant.CALLBACK_ENABLE_NOTIFICATION
                                    /*|EmvL3Constant.CALLBACK_ENABLE_SELECT_CANDIDATE_LIST*/);
                    byte cardNumberConfirm = 0x01;
                    if(transactionExtParams != null && transactionExtParams.getExtEmvCallBack() != null && transactionExtParams.getExtEmvCallBack().length >= 2 && transactionExtParams.getExtEmvCallBack()[1] != null){
                        cardNumberConfirm = transactionExtParams.getExtEmvCallBack()[1];
                    }
                    tlvpackage.append(0x1F8139,new byte[]{callBackFlag,cardNumberConfirm,0x00});
                }else{
                    //This data item used to control callback to upper computer.
                    byte callBackFlag = (byte) (
                            EmvL3Constant.CALLBACK_ENABLE_NOTIFICATION|
                                    /*EmvL3Constant.CALLBACK_ENABLE_SELECT_CANDIDATE_LIST|*/
                                    EmvL3Constant.CALLBACK_ENABLE_CHECK_CREDENTIALS|
                                    EmvL3Constant.CALLBACK_ENABLE_AFTER_FINAL_SELECT|
                                    EmvL3Constant.CALLBACK_ENABLE_GET_PIN);
                    if(transactionExtParams != null && transactionExtParams.getExtEmvCallBack() != null && transactionExtParams.getExtEmvCallBack().length >= 1 && transactionExtParams.getExtEmvCallBack()[0] != null){
                        callBackFlag = transactionExtParams.getExtEmvCallBack()[0];
                    }

                    byte cardNumberConfirm = 0x01;
                    if(transactionExtParams != null && transactionExtParams.getExtEmvCallBack() != null && transactionExtParams.getExtEmvCallBack().length >= 2 && transactionExtParams.getExtEmvCallBack()[1] != null){
                        cardNumberConfirm = transactionExtParams.getExtEmvCallBack()[1];
                    }
                    tlvpackage.append(0x1F8139,new byte[]{callBackFlag,cardNumberConfirm,0x00});
                }
                if(transactionExtParams!=null && transactionExtParams.getCurrentCardInterfaces()!=null){
                    deviceLogger.debug("[startEMV] transactionExtParams.getCurrentCardInterfaces:"+transactionExtParams.getCurrentCardInterfaces());
                    //Current card interface,如果先调用上电，再调用startemv，需要设置当前卡类型
                    tlvpackage.append(0x1F8121,new byte[]{(byte) (transactionExtParams.getCurrentCardInterfaces().intValue())});
                    switch (transactionExtParams.getCurrentCardInterfaces()){
                        case EmvL3Constant.CardInterfaces.MSR:
                            getEMVTransInfo().setOpenCardType(CardType.MSGCARD);
                            break;
                        case EmvL3Constant.CardInterfaces.CONTACT:
                            getEMVTransInfo().setOpenCardType(CardType.ICCARD);
                            break;
                        case EmvL3Constant.CardInterfaces.CONTACTLESS:
                            getEMVTransInfo().setOpenCardType(CardType.RFCARD);
                            break;
                    }

                }


                byte[] data = tlvpackage.pack();
                byte[] transData = new byte[5 + data.length];

                transData[0] = EmvL3Constant.CardInterfaces.MSR|EmvL3Constant.CardInterfaces.CONTACT|EmvL3Constant.CardInterfaces.CONTACTLESS;
                if(transactionExtParams!=null && transactionExtParams.getCardInterfaces()!=null){
                    deviceLogger.debug("[[startEMV] transactionExtParams.getCardInterfaces():"+transactionExtParams.getCardInterfaces());
                    transData[0] = (byte)transactionExtParams.getCardInterfaces().intValue();
                }
                System.arraycopy(InnerUtils.intToBytes(EmvL3Constant.PERFORM_TIMEOUT, 4,true), 0, transData, 1, 4);
                System.arraycopy(data, 0, transData, 5, data.length);
                deviceLogger.debug("[startEMV]->performTransaction transData:" + (transData==null?"null":ISOUtils.hexString(transData)));
                isCancelPerform = false;
                TransactionResult result = mMEEmvL3.performTransaction(transData);
                if (result == null){
                    deviceLogger.error("[startEMV] result==null");
                    emvL3Finish(EmvL3Constant.EXCEPTION_COMM_FINISH);
                    return;
                }

                int resultCode = result.getResultCode();
                int errCode = result.getErrorCode();
                int cvmStatus = result.getCvmStatus();
                byte[] tlvData = result.getTlvData();
                getEMVTransInfo().setErrorcode(errCode);
                getEMVTransInfo().setEmvrsltCode(getEmvrsltCode(resultCode,false));
                if(isCancelPerform){
                    getEMVTransInfo().setExecuteRslt(getExecuteRslt(-1,false));
                }else {
                    getEMVTransInfo().setExecuteRslt(getExecuteRslt(resultCode,false));
                }
                deviceLogger.error("[startEMV] resultCode{OK(0) TERMINATE(1) TRY_ANOTHER(2) DECLINE(3) APPROVED(4) ONLINE(5)}="+resultCode+" errCode="+errCode+" cvmStatus="+cvmStatus+" isCancelPerform="+isCancelPerform+" tlvData="+(tlvData==null?"null":ISOUtils.hexString(tlvData)));
                if(!isCancelPerform && (resultCode == EmvL3Const.TransResult.L3_TXN_ONLINE)){
                    mEmvL3CallBackHelper.onlineProcess();
                }else {
                    mEmvL3CallBackHelper.finished(true);
                }

//                if(resultCode == EmvL3Const.TransResult.L3_TXN_DECLINE){
//                    deviceLogger.debug("[startEMV] OffLine DECLINE");
//                }else if(resultCode == EmvL3Const.TransResult.L3_TXN_APPROVED){
//                    deviceLogger.debug("[startEMV] OffLine APPROVED");
//                }else if(resultCode == EmvL3Const.TransResult.L3_TXN_ONLINE){
//                    deviceLogger.debug("[startEMV] ONLINE");
//                }else {
//                    if (errCode == ErrorCode.L3_ERR_SUCC) {
//
//                    }else if (errCode == ErrorCode.L3_ERR_TIMEOUT) {
//                        deviceLogger.debug("[startEMV] Timeout");
//                    } else if (errCode == ErrorCode.L3_ERR_COLLISION) {
//                        deviceLogger.debug("[startEMV] Present One Card Only");
//                    } else if (errCode == ErrorCode.L3_ERR_CANCEL) {
//                        deviceLogger.debug("[startEMV] Cancel");
//                    } else {
//                        deviceLogger.debug("[startEMV] Fail");
//                    }
//                }
            }
        });
    }

    @Override
    public void setTransactionAmount(BigDecimal amount) {
        deviceLogger.debug("[setTransactionAmount] amount="+amount);
    }

    @Override
    public void setSelectedApplication(int index) {
        deviceLogger.debug("[setSelectedApplication] index="+index);
        mEmvL3CallBackHelper.setSelectedApplication(index);
    }

    @Override
    public void confirmInformation(boolean confirm) {
        deviceLogger.debug("[confirmInformation] confirm="+confirm);
        if(confirm){
            mEmvL3CallBackHelper.confirmInformation();
        }else {
            emvL3Finish(EmvL3Constant.EXCEPTION_INTERRUPT_FINISH);
        }
    }

    @Override
    public void setPIN(byte[] pinblock) {
        deviceLogger.debug("[setPIN] pinblock="+(pinblock==null?"null": ISOUtils.hexString(pinblock)));
        mEmvL3CallBackHelper.setPIN(pinblock);
    }

    @Override
    public void confirmID(boolean confirm) {
        deviceLogger.debug("[confirmID] confirm="+confirm);
        if(confirm){
            mEmvL3CallBackHelper.confirmID();
        }else {
            emvL3Finish(EmvL3Constant.EXCEPTION_INTERRUPT_FINISH);
        }
    }


    @Override
    public void completeEMVProcess(@NonNull final OnlineTransactionData inputData) {
        deviceLogger.debug("[completeEMVProcess] inputData="+inputData);
        METhreadExecutors.startThread(new Runnable() {
            @Override
            public void run() {
                mEmvL3CallBackHelper.completeEMVProcess();
                if(inputData == null){
                    deviceLogger.error("[completeEMVProcess] inputData==null.");
                    emvL3Finish(EmvL3Constant.EXCEPTION_INTERRUPT_FINISH);
                    return;
                }
                EmvPackager packager = EMVInnerUtils.newEmvPackager();
                byte[] requestData = packager.pack(inputData);
                deviceLogger.debug("[completeEMVProcess] requestData="+(requestData==null?"null":ISOUtils.hexString(requestData)));
                TLVPackage tp = EMVInnerUtils.newTlvPackage();
                tp.unpack(requestData);

                byte[] transData = null;
                boolean onlineResult = true;
                if (onlineResult) {
                    TLVPackage tlvpackage = EMVInnerUtils.newTlvPackage();
                    byte[] value = tp.getValue(Const.EmvStandardReference.AUTHORISATION_RESPONSE_CODE);//联机响应码
                    if (value == null) {
                        byte[] authData = tp.getValue(Const.EmvStandardReference.ISSUER_AUTHENTICATION_DATA);
                        if (null == authData && null != inputData.getTlvData()) {
                            TLVPackage field55Tlv = InnerUtils.newTlvPackage();
                            field55Tlv.unpack(inputData.getTlvData());
                            authData = field55Tlv.getValue(Const.EmvStandardReference.ISSUER_AUTHENTICATION_DATA);
                        }
                        if (authData != null) {
                            value = Arrays.copyOfRange(authData, authData.length - 2, authData.length);
                        }
                    }
                    if(value != null){
                        tlvpackage.append(0x8A,value);
                    }

                    value = tp.getValue(Const.EmvStandardReference.ISSUER_SCRIPT_TEMPLATE_1);
                    if(value != null) {
                        tlvpackage.append(0x71, value);
                    }

                    value = tp.getValue(Const.EmvStandardReference.ISSUER_SCRIPT_TEMPLATE_2);
                    if(value != null) {
                        tlvpackage.append(0x72, value);
                    }

                    value = tp.getValue(Const.EmvStandardReference.ISSUER_AUTHENTICATION_DATA);
                    if(value != null) {
                        tlvpackage.append(0x91, value);
                    }
                    byte[] data = tlvpackage.pack();
                    transData = new byte[1 + data.length];
                    transData[0] = 1;
                    System.arraycopy(data, 0, transData, 1, data.length);

                } else { // unable go online
                    transData = new byte[1];
                    transData[0] = 0;
                }
                deviceLogger.debug("[completeEMVProcess] transData:" + (transData==null?"null":ISOUtils.hexString(transData)));
                TransactionResult result = mMEEmvL3.completeTransaction(transData);
                if(result == null){
                    emvL3Finish(EmvL3Constant.EXCEPTION_COMM_FINISH);
                    return;
                }
                int resultCode = result.getResultCode();
                int errCode = result.getErrorCode();
                int cvmStatus = result.getCvmStatus();
                byte[] tlvData = result.getTlvData();
                deviceLogger.error("[completeEMVProcess] resultCode{OK(0) TERMINATE(1) TRY_ANOTHER(2) DECLINE(3) APPROVED(4) ONLINE(5)}="+resultCode+" errCode="+errCode+" cvmStatus="+cvmStatus+" tlvData="+(tlvData==null?"null":ISOUtils.hexString(tlvData)));

                getEMVTransInfo().setErrorcode(errCode);
                getEMVTransInfo().setEmvrsltCode(getEmvrsltCode(resultCode,true));
                getEMVTransInfo().setExecuteRslt(getExecuteRslt(resultCode,true));
                mEmvL3CallBackHelper.finished(true);
            }
        });
    }

    @Override
    public void cancelEMVProcess() {
        try {
            deviceLogger.debug("[cancelEMVProcess]");
            isCancelPerform = true;
            boolean channelFreeTime =  (EmvL3Global.getChannelState() == ChannelState.FREE)?true:false;
            boolean findCardTime = (EmvL3Global.getUiEventID() == EmvL3Constant.UIEvent.UI_PRESENT_CARD)?true:false;
            boolean isInterruptTime = EmvL3Global.isInterruptTime();
            EmvL3Step.EmvL3ListenerStep emvL3ListenerStep = null;
            if(mEMVModule.getEmvL3Step() != null){
                emvL3ListenerStep = mEMVModule.getEmvL3Step().getEmvStep();
            }
            deviceLogger.error("[cancelEMVProcess] channelFreeTime="+channelFreeTime+" findCardTime="+findCardTime+" isInterruptTime="+isInterruptTime+" emvL3ListenerStep="+emvL3ListenerStep);

            if(emvL3ListenerStep == EmvL3Step.EmvL3ListenerStep.onRequestOnlineProcess){
                mEmvL3CallBackHelper.finished(false);
                deviceLogger.debug("[cancelEMVProcess] Channel -> finished");
                return;
            }

            if((!channelFreeTime && (findCardTime || isInterruptTime))){
                EmvL3Comm emvL3Comm = mEMVModule.getMEEmvL3().getEmvL3Comm();
                if(emvL3Comm !=null){
                    byte[] result = emvL3Comm.Communication(EmvL3Constant.CMD_CANCEL.getBytes());
                    deviceLogger.debug("[cancelEMVProcess] result="+(result==null?"null":ISOUtils.hexString(result)));
                }

                if(isInterruptTime){
                    EmvL3Step emvL3Step = mEMVModule.getEmvL3Step();
                    if(emvL3Step != null){
                        emvL3Step.interruptStep(EmvL3Step.EmvL3PauseStep.cancel);
                    }
                }
            }

            deviceLogger.debug("[cancelEMVProcess] Channel "+EmvL3Global.getChannelState());

            //如果正在执行EmvL3CallBack.uiEvent(),这时候通道是非空闲的,但是findCardTime和isInterruptTime都为false,
            //此时如果进入空闲等待会导致阻塞在等待uiEvent,perform没有继续读串口.

//            if(EmvL3Global.getChannelState() != ChannelState.FREE){
//                EmvL3Global.setChannelState(ChannelState.INTERRUPT);
//                while (EmvL3Global.getChannelState()!=ChannelState.FREE){
//                    deviceLogger.debug("[cancelEMVProcess] Waiting Channel free......");
//                    Thread.sleep(100);
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean setEmvData(int tag, byte[] value) {
        return mEMVModule.setEmvData(tag,value);
    }

    @Override
    public TLVPackage getEmvData(int[] emvTags) {
        return mEMVModule.getEmvData(emvTags);
    }

    @Override
    public byte[] getEmvData(int tag) {
        return mEMVModule.getEmvData(tag);
    }

    @Override
    public byte[] getICCdata(int tag) {
        return mEMVModule.getICCdata(tag);
    }

    @Override
    public EMVTransInfo getEMVTransInfo() {
        return mEMVModule.getEMVTransInfo();
    }

    @Override
    public void setSelectedAccountType(AccountType accountType) {
        deviceLogger.debug("[setSelectedAccountType] accountType="+accountType);
    }

    @Override
    public void confirmEC(boolean isEC) {
        deviceLogger.debug("[confirmEC] isEC="+isEC);
    }

    @Override
    public void confirmMessage(boolean confirm) {
        deviceLogger.debug("[confirmMessage] confirm="+confirm);
    }

    @Override
    public void setSelectedLanguage(String language) {
        deviceLogger.debug("[setSelectedLanguage] language="+language);
    }

    @Override
    public void setEMVTimeOut(int timeoutS) {
        deviceLogger.debug("[setEMVTimeOut] timeoutS="+timeoutS);
        EmvL3Global.setEmvStepTimeOutMs(timeoutS*1000);
    }

    private void emvL3Finish(int emvResult,int error){
        this.getEMVTransInfo().setEmvrsltCode(emvResult);
        this.getEMVTransInfo().setErrorcode(error);
        this.getEMVTransInfo().setExecuteRslt(AbstractEMVTransController._EMV_RSLT_STEP_FAILED);
        mEmvL3CallBackHelper.finished(false);
    }
    private void emvL3Finish(int errorCode){
        this.emvL3Finish(errorCode,errorCode);
    }

    private int getExecuteRslt(int resultCode,boolean isComplete){
        return mEMVModule.getExecuteRslt(resultCode,isComplete);
    }
    private int getEmvrsltCode(int resultCode,boolean isComplete){
        return mEMVModule.getEmvrsltCode(resultCode,isComplete);
    }
}
