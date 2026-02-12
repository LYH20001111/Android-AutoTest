package com.newland.sdk.me.module.emvl3.impl;

import android.newland.os.NlBuild;
import android.provider.Settings;

import com.newland.ndk.NdkApiManager;
import com.newland.sdk.module.emv.EMVControllerListener;
import com.newland.sdk.module.emv.EMVTransController;
import com.newland.sdk.me.module.emvl3.EMVL3Module;
import com.newland.sdk.me.module.emvl3.impl.EmvL3Step.EmvL3PauseStep;
import com.newland.sdk.me.module.emvl3.impl.EmvL3Step.EmvL3ListenerStep;
import com.newland.sdk.mtype.common.ErrorCode;
import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.utils.ISOUtils;
import com.newland.sdk.utils.TLVPackage;

/**
 * @Description SDK特有的回调步骤.
 * @Author wuhh
 * @Date 2020/9/27
 */
public class EmvL3CallBackHelper extends EmvL3CallBack{

    public EmvL3CallBackHelper(EMVControllerListener listener){
        this.mEMVControllerListener = listener;
    }

    public void init(EMVL3Module emvl3Module, EMVTransController emvTransController, EmvL3Step emvL3Step){
        this.mMEEmvL3 = emvl3Module;
        this.mEMVTransController = emvTransController;
        this.mEmvL3Step = emvL3Step;
    }

    public void setSelectedApplication(int index){
        mAppSelectIndex = index;
        mEmvL3Step.resumeStep(EmvL3ListenerStep.onRequestSelectApplication,EmvL3PauseStep.selectCandidateList);
    }

    public void confirmInformation(){
        EmvL3ListenerStep emvStep = null;
        EmvL3PauseStep goStep = null;
        if(mEmvL3Step.getEmvStep().equals(EmvL3ListenerStep.onRequestConfirmFinalAppSelection)){
            emvStep = EmvL3ListenerStep.onRequestConfirmFinalAppSelection;
            goStep = EmvL3PauseStep.onFinalSelect;
        }else if(mEmvL3Step.getEmvStep().equals(EmvL3ListenerStep.onRequestConfirmCardInfo)){
            emvStep = EmvL3ListenerStep.onRequestConfirmCardInfo;
            goStep = EmvL3PauseStep.onConfirmCardInfo;
        }
        mEmvL3Step.resumeStep(emvStep,goStep);
    }

    public void setCheckCredentialsParam(int cerType,String number){
        this.mCredentialsType = cerType;
        this.mCredentialsIDNo = number;
    }

    @Override
    public int fallback() {
        mEmvL3Step.pauseStep(new Runnable() {
            @Override
            public void run() {
                mEmvL3Step.interruptStep(EmvL3PauseStep.fallback);
                closeRfidLight();
                mEMVControllerListener.onFallback(mEMVTransController);
            }
        }, EmvL3PauseStep.fallback,EmvL3ListenerStep.onFallback);
        return 0;
    }

    @Override
    public int onPinInputListener(byte[] uiEventData) {
        try {
            if(EmvL3Global.pinInputListener == null){
                deviceLogger.error("[onPinInputListener] PinInputListener==null");
                return -1;
            }
            int offset = 0;
            int dataLen = InnerUtils.bytesToInt(uiEventData, -1, 2, true);offset+=2;
            byte[] statusFb = new byte[4];
            System.arraycopy(uiEventData,offset,statusFb,0,statusFb.length);
            int status = InnerUtils.bytesToInt(statusFb, -1, 4, true);offset+=4;
            deviceLogger.debug("Pin status="+status);
            if(status == EmvL3Constant.PinEntryStatus.SUCC){
                if(dataLen > 4){
                    byte[] lenFb = new byte[2];
                    System.arraycopy(uiEventData,offset,lenFb,0,lenFb.length);
                    int lenFi = InnerUtils.bytesToInt(lenFb, -1, 2, true);
                    offset+=2;
                    byte[] tlv = new byte[lenFi];
                    System.arraycopy(uiEventData,offset,tlv,0,tlv.length);
                    TLVPackage tlvPackage = InnerUtils.newTlvPackage();
                    tlvPackage.unpack(tlv);
                    byte[] pinBlock = tlvPackage.getValue(0x1F8155);
                    byte[] ksn=tlvPackage.getValue(0x1F8153);
                    EmvL3Global.pinInputListener.onFinish(pinBlock.length,pinBlock,ksn);
                }
            }else if(status == EmvL3Constant.PinEntryStatus.FAIL){
                EmvL3Global.pinInputListener.onError(ErrorCode.INPUT_PIN_FAILED,"Input pin fail.");
            }else if(status == EmvL3Constant.PinEntryStatus.CANCLE){
                EmvL3Global.pinInputListener.onCancel();
            }else if(status == EmvL3Constant.PinEntryStatus.TIMEOUT){
                EmvL3Global.pinInputListener.onTimeout();
            }else if(status == EmvL3Constant.PinEntryStatus.BYPASS){
                if(dataLen > 4){
                    byte[] lenFb = new byte[2];
                    System.arraycopy(uiEventData,offset,lenFb,0,lenFb.length);
                    int lenFi = InnerUtils.bytesToInt(lenFb, -1, 2, true);
                    offset+=2;
                    byte[] tlv = new byte[lenFi];
                    System.arraycopy(uiEventData,offset,tlv,0,tlv.length);
                    TLVPackage tlvPackage = InnerUtils.newTlvPackage();
                    tlvPackage.unpack(tlv);
                    byte[] ksn=tlvPackage.getValue(0x1F8153);
                    EmvL3Global.pinInputListener.onFinish(0, new byte[]{},ksn);
                }else{
                    EmvL3Global.pinInputListener.onFinish(0, new byte[]{},null);
                }

            }else {
                EmvL3Global.pinInputListener.onError(ErrorCode.INPUT_PIN_FAILED,"Input pin status error.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            EmvL3Global.pinInputListener.onError(ErrorCode.INPUT_PIN_FAILED,"Input pin exception.");
        }
        EmvL3Global.setPinParam((byte)-1,(byte)-1,(byte)-1,null,null,null);
        return 0;
    }

    public int setPIN(byte[] pinblock){
        deviceLogger.debug("setPIN pinblock="+(pinblock==null?"null":ISOUtils.hexString(pinblock)));
        EmvL3Global.pinblock = pinblock;
        //需要区分内外置EMVL3,暂时未处理.
        //mEmvL3Step.resumeStep(EmvL3ListenerStep.onRequestInputPIN,EmvL3PauseStep.getPIN);
        return 0;
    }

    public int confirmID(){
        mEmvL3Step.resumeStep(EmvL3ListenerStep.onRequestConfirmID,EmvL3PauseStep.checkCredentials);
        return 0;
    }

    @Override
    public int onlineProcess() {
        mEmvL3Step.pauseStep(new Runnable() {
            @Override
            public void run() {
                closeRfidLight();
                mEMVControllerListener.onRequestOnlineProcess(mEMVTransController);
            }
        }, EmvL3PauseStep.onlineProcess,EmvL3ListenerStep.onRequestOnlineProcess);
        return 0;
    }

    public int completeEMVProcess(){
        mEmvL3Step.resumeStep(EmvL3ListenerStep.onRequestOnlineProcess,EmvL3PauseStep.onlineProcess+"->"+EmvL3Step.completeEMVProcess);
        return 0;
    }

    @Override
    public int finished(final boolean isSuccess) {
        deviceLogger.error("[EmvL3Finished]mEMVTransController:"+mEMVTransController);
        mEmvL3Step.interruptStep(EmvL3PauseStep.finished);
        mEmvL3Step.pauseStep(new Runnable() {
            @Override
            public void run() {
                try {
                    closeRfidLight();
                    mEMVControllerListener.onEmvFinished(isSuccess,mEMVTransController);
                    //mMEEmvL3.finishEMV();//下一笔交易前，再terminateTransaction。林礼达反馈，terminateTransaction结束后，不能再报错内核数据
                    //boolean result = mMEEmvL3.terminateTransaction();
                    //deviceLogger.error("[EmvL3Finished] EmvL3 TerminateTransaction result="+result);
                    mEmvL3Step.resumeStep(EmvL3ListenerStep.onEmvFinished,EmvL3PauseStep.finished);
                } finally {
                    boolean result = mMEEmvL3.terminateTransaction();
                    deviceLogger.error("[EmvL3Finished] EmvL3 TerminateTransaction result="+result);
                }
            }
        },EmvL3PauseStep.finished,EmvL3ListenerStep.onEmvFinished);
        return 0;
    }
    private void closeRfidLight(){
        if(!NlBuild.VERSION.MODEL.equals("P300")){
            return;
        }
        int ret = NdkApiManager.getNdkApiManager().getSysN().setLedLt1118Status(false);
        deviceLogger.debug("[closeRfidLed] ret="+ret);
    }
}
