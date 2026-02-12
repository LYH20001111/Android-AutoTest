package com.newland.sdk.me.module.emvl3.impl;

import com.newland.sdk.me.module.emvl3.external.Candidate;
import com.newland.sdk.me.module.emvl3.external.EmvL3Const;
import com.newland.sdk.me.module.emvl3.external.publickey;
import com.newland.sdk.me.module.emvl3.impl.EmvL3Step.EmvL3PauseStep;
import com.newland.sdk.me.module.emvl3.impl.EmvL3Step.EmvL3ListenerStep;
import com.newland.sdk.module.emv.AIDEntity;
import com.newland.sdk.module.emv.EMVControllerListener;
import com.newland.sdk.module.emv.EMVTransController;
import com.newland.sdk.module.emv.IDCardType;
import com.newland.sdk.module.emv.PINEntity;
import com.newland.sdk.module.emv.PinRequiredType;
import com.newland.sdk.me.module.emvl3.EMVL3Module;
import com.newland.sdk.me.module.emvl3.listener.MEEmvL3Listener;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtype.util.Dump;
import com.newland.sdk.utils.ISOUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description 只处理EMVL3 PerformTransaction的中断回调.
 * @Author wuhh
 * @Date 2020/4/14
 */
public abstract class EmvL3CallBack implements MEEmvL3Listener {

    protected DeviceLogger deviceLogger = DeviceLoggerFactory.getLogger("EmvL3CallBack");
    protected EMVControllerListener mEMVControllerListener;
    protected EMVTransController mEMVTransController;
    protected EMVL3Module mMEEmvL3;
    protected EmvL3Step mEmvL3Step;
    protected int mAppSelectIndex;
    protected int mCredentialsType;
    protected String mCredentialsIDNo;

    @Override
    public int uiEvent(int uiEventID, byte[] uiEventData) {
        deviceLogger.debug("[uiEvent] uiEventID="+uiEventID+" uiEventData="+(uiEventData==null?"null": ISOUtils.hexString(uiEventData)));
        EmvL3Global.setUiEventID(uiEventID);
        if(uiEventID == EmvL3Constant.UIEvent.UI_PRESENT_CARD){
            deviceLogger.debug("[uiEvent] UI_PRESENT_CARD");
        }else if(uiEventID == EmvL3Constant.UIEvent.UI_PROCESSING){
            deviceLogger.debug("[uiEvent] UI_PROCESSING");
        }else if(uiEventID == EmvL3Constant.UIEvent.UI_CAPK_LOAD_FAIL){
            deviceLogger.debug("[uiEvent] UI_CAPK_LOAD_FAIL");
        }else if(uiEventID == EmvL3Constant.UIEvent.UI_SEE_PHONE){
            deviceLogger.debug("[uiEvent] UI_SEE_PHONE");
        }else if(uiEventID == EmvL3Constant.UIEvent.UI_CARDNUM_CONFIRM){
            deviceLogger.debug("[uiEvent] UI_CARDNUM_CONFIRM");
        }else if(uiEventID == EmvL3Constant.UIEvent.UI_CHIP_ERR_RETRY){
            deviceLogger.debug("[uiEvent] UI_CHIP_ERR_RETRY");
            return fallback();
        }else if(uiEventID == EmvL3Constant.UIEvent.UI_PIN_STATUS){
            deviceLogger.debug("[uiEvent] UI_PIN_STATUS");
            return onPinInputListener(uiEventData);
        }else {
            deviceLogger.error("[uiEvent] unknow uiEventID");
        }
        return 0;
    }

    @Override
    public int selectCandidateList(final ArrayList<Candidate> candidateList, int[] select) {
        deviceLogger.debug("[selectCandidateList] candidateList="+candidateList);
        mEmvL3Step.pauseStep(new Runnable() {
            @Override
            public void run() {
                List<AIDEntity> aidEntityList = new ArrayList<AIDEntity>();
                for(int i=0;i<candidateList.size();i++){
                    Candidate candidate = candidateList.get(i);
                    String name = null;
                    if (candidate.preferName != null && candidate.preferName.length > 0) {
                        try {
                            name = new String(candidate.preferName);
                        } catch (Exception e) {
                            deviceLogger.debug("[selectCandidateList]  String(preferName) exception " + Dump.getHexDump(candidate.aid), e);
                        }
                    }
                    if (name == null) {
                        name = Dump.getHexDump(candidate.aid);
                    }
                    //int index, byte[] aid, String name, byte[] appLable, byte terminalPriority, byte enable, byte limitFlag, byte[] kernelId, byte apid
                    AIDEntity aidEntity = new AIDEntity(i,candidate.aid,name,
                            candidate.lable,candidate.terminalPriority,
                            (byte)-1,(byte)-1,candidate.kernelId,(byte)-1);
                    aidEntityList.add(aidEntity);
                }
                int times = 1;
                mEMVControllerListener.onRequestSelectApplication(mEMVTransController,aidEntityList,times);
            }
        }, EmvL3PauseStep.selectCandidateList,EmvL3ListenerStep.onRequestSelectApplication);
        select[0] = mAppSelectIndex;
        return 0;
    }

    @Override
    public int onFinalSelect(int cardInterface, byte[] aid, int aidLen) {
        deviceLogger.debug("[onFinalSelect] cardInterface="+cardInterface+" aid="+(aid==null?"null":ISOUtils.hexString(aid))+" aidLen="+aidLen);
        mEmvL3Step.pauseStep(new Runnable() {
            @Override
            public void run() {

                mEMVControllerListener.onRequestConfirmFinalAppSelection(mEMVTransController);
            }
        },EmvL3PauseStep.onFinalSelect,EmvL3ListenerStep.onRequestConfirmFinalAppSelection);
        //需要优化速度的可以在这里将设置的tag参数统一发送.否则调用setTlvData参数.
        return 0;
    }

    //内置EMVL3有UIEvmet回调,函数名称保持和EMV指令一致.
    //public abstract int cardnumConfirm();
    @Override
    public int cardnumConfirm() {
        mEmvL3Step.pauseStep(new Runnable() {
            @Override
            public void run() {
                mEMVControllerListener.onRequestConfirmCardInfo(mEMVTransController);
            }
        },EmvL3PauseStep.onConfirmCardInfo,EmvL3ListenerStep.onRequestConfirmCardInfo);
        return 0;
    }

    @Override
    public int getPIN(final int pinType, final int pinTryCnt, publickey pinPK, byte[] sw1sw2) {
        deviceLogger.debug("[getPIN] pinType="+pinType+" pinTryCnt="+pinTryCnt+" pinPK="+pinPK);
        mEmvL3Step.pauseStep(new Runnable() {
            @Override
            public void run() {
                boolean online = true;
                PinRequiredType pinRequiredType = PinRequiredType.ONLINE;
                if(pinType != EmvL3Const.PINType.PIN_ONLINE){
                    online = false;
                    pinRequiredType = PinRequiredType.OFFLINE;
                }
                PINEntity pinEntity = new PINEntity(pinRequiredType,pinTryCnt,null,null);
                EmvL3Global.setEmvL3GetPinProcess(true);
                mEMVControllerListener.onRequestInputPIN(mEMVTransController,online,pinEntity);
                mEmvL3Step.resumeStep(EmvL3ListenerStep.onRequestInputPIN,EmvL3PauseStep.getPIN);
            }
        },EmvL3PauseStep.getPIN,EmvL3ListenerStep.onRequestInputPIN);
        EmvL3Global.setEmvL3GetPinProcess(false);
        return 0;
    }

    @Override
    public int checkCredentials() {
        deviceLogger.debug("[checkCredentials]");
        mEmvL3Step.pauseStep(new Runnable() {
            @Override
            public void run() {
                IDCardType idCardType = null;
                if(mCredentialsType == EmvL3Constant.Credentials.ID_CARD){
                    idCardType = IDCardType.CITIZEN_IDCARD;
                } else if(mCredentialsType == EmvL3Constant.Credentials.MILITARY_ID_CARD){
                    idCardType = IDCardType.MILITARY_IDCARD;
                } else if(mCredentialsType == EmvL3Constant.Credentials.PASSPORT){
                    idCardType = IDCardType.PASSPORT;
                } else if(mCredentialsType == EmvL3Constant.Credentials.ENTRY_PERMIT){
                    idCardType = IDCardType.ENTRY_PERMIT;
                } else if(mCredentialsType == EmvL3Constant.Credentials.TEMPORARY_ID_CARD){
                    idCardType = IDCardType.TEMPORARY_CITIZEN_IDCARD;
                } else if(mCredentialsType == EmvL3Constant.Credentials.OTHER){
                    idCardType = IDCardType.OTHERS;
                }
                mEMVControllerListener.onRequestConfirmID(mEMVTransController,idCardType,mCredentialsIDNo);
            }
        },EmvL3PauseStep.checkCredentials,EmvL3ListenerStep.onRequestConfirmID);
        return 0;
    }

    @Override
    public int selectAccount(int[] select) {
        deviceLogger.debug("[selectAccount]");
        return 0;
    }

    @Override
    public int selectLanguage() {
        deviceLogger.debug("[selectLanguage]");
        return 0;
    }

    @Override
    public int voiceReferrals() {
        deviceLogger.debug("[voiceReferrals]");
        return 0;
    }

    @Override
    public int dek_det(int type, byte[] data, int[] dataLen) {
        deviceLogger.debug("[dek_det]");
        return 0;
    }

    public int getManualData() {
        deviceLogger.debug("[getManualData]");
        return 0;
    }

    public abstract int fallback();

    public abstract int onPinInputListener(byte[] uiEventData);

    public abstract int onlineProcess();

    public abstract int finished(boolean isSuccess);
}
