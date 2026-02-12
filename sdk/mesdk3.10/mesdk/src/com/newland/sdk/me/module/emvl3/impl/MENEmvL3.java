package com.newland.sdk.me.module.emvl3.impl;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.newland.sdk.me.module.emv.EMVInnerUtils;
import com.newland.sdk.me.module.emvl3.external.EmvL3Const;
import com.newland.sdk.module.emv.AID;
import com.newland.sdk.module.emv.AIDEntity;
import com.newland.sdk.module.emv.AccountType;
import com.newland.sdk.module.emv.CAPK;
import com.newland.sdk.module.emv.CardInterface;
import com.newland.sdk.module.emv.ECTransLog;
import com.newland.sdk.module.emv.ECTransLogListener;
import com.newland.sdk.module.emv.EMVCardInfo;
import com.newland.sdk.module.emv.EMVControllerListener;
import com.newland.sdk.module.emv.EMVModule;
import com.newland.sdk.module.emv.EMVTransController;
import com.newland.sdk.module.emv.EMVTransInfo;
import com.newland.sdk.module.emv.EMVTransLog;
import com.newland.sdk.module.emv.EMVTransLogListener;
import com.newland.sdk.module.emv.EmvExtParams;
import com.newland.sdk.me.module.emvl3.CardContactMode;
import com.newland.sdk.me.module.emvl3.listener.MEEmvL3Listener;
import com.newland.sdk.module.emv.IDCardType;
import com.newland.sdk.module.emv.OnlineTransactionData;
import com.newland.sdk.module.emv.PINEntity;
import com.newland.sdk.module.externaliccard.ExtICCardModule;
import com.newland.sdk.module.externalrfcard.ExtRFCardModule;
import com.newland.sdk.mtype.Device;
import com.newland.sdk.mtype.ExModuleType;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.mtypex.AbstractModule;
import com.newland.sdk.utils.ISOUtils;
import com.newland.sdk.utils.TLVPackage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description 实现EMVL3接口,不包含SDK针对EMVL2的特殊处理.
 * @Author wuhh
 * @Date 2020/9/8
 */
public abstract class MENEmvL3 extends AbstractModule implements EMVModule {

    private DeviceLogger deviceLogger = DeviceLoggerFactory.getLogger("MENEmvL3");
    private MEEmvL3 mMEEmvL3;
    private volatile boolean mExtL3InitFlag = false;
    private EmvL3Usage mL3Usage;
    private EmvExtParams mEmvExtParams;

    protected MEEmvL3Listener mEmvL3CallBack;
    protected EMVTransInfo mEmvTransInfo;
    protected EMVTransController mEmvL3CtrlProcess;
    protected EmvL3Step mEmvL3Step;
    private AbstractDevice owner;
    enum LogType {
        PBOC_LOG,
        EC_LOG
    }

    protected MENEmvL3(Context context,AbstractDevice owner) {
        super(owner);
        this.owner = owner;
        mMEEmvL3 = MEEmvL3.getInstance(context,owner);
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
        return owner;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public boolean init(Context context, EmvExtParams emvExtParams) {
        deviceLogger.debug("[init] context="+context+" emvExtParams="+emvExtParams+" mExtL3InitFlag="+mExtL3InitFlag);
        this.mEmvExtParams = emvExtParams;
        mL3Usage = EmvL3Usage.INNER;
        if(mEmvExtParams != null && mEmvExtParams.isExternalReader()){
            mL3Usage = EmvL3Usage.EXTERNAL;
            boolean result = mMEEmvL3.extInit(emvExtParams);
            deviceLogger.info("[init] MEEmvL3.extInit result="+result);
            return result;
        }
        deviceLogger.info("[init] MEEmvL3 success."+mL3Usage);
        return true;
    }

    @Override
    public EMVTransController getEmvTransController(EMVControllerListener emvControllerListener) {
        deviceLogger.debug("[==================getEmvTransController]==================mExtL3InitFlag:"+mExtL3InitFlag);
        mEmvL3CallBack = new EmvL3CallBackHelper(emvControllerListener);
        mEmvTransInfo = new EMVTransInfo();
        mEmvL3CtrlProcess = new EmvL3CtrlProcess();
        mEmvL3Step = new EmvL3Step();
        EmvL3Global.setUiEventID(EmvL3Constant.UIEvent.UI_NONE);
        EmvL3Global.setEmvL3Step(mEmvL3Step);

        ((EmvL3CallBackHelper)mEmvL3CallBack).init(mMEEmvL3,mEmvL3CtrlProcess,mEmvL3Step);
        ((EmvL3CtrlProcess)mEmvL3CtrlProcess).init(mMEEmvL3,(EmvL3CallBackHelper)mEmvL3CallBack,this);
        deviceLogger.error("[getEmvTransController] mL3Usage="+mL3Usage+" mExtL3InitFlag="+mExtL3InitFlag);
        byte[] config = new byte[]{0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
        if(mL3Usage == EmvL3Usage.INNER){
            //内置的监听器和初始化接口是一起的,更新监听的时候必现重新Init.后续处理.
            boolean result = mMEEmvL3.l3init(mL3Usage,config,mEmvL3CallBack);
            deviceLogger.error("[getEmvTransController] init result="+result);
        }else {
            mMEEmvL3.setExtEmvL3Listener(mEmvL3CallBack);
            if(!mExtL3InitFlag){
                boolean result = mMEEmvL3.l3init(mL3Usage,config,mEmvL3CallBack);
                if(result){
                    mExtL3InitFlag = true;
                }
                deviceLogger.error("[getEmvTransController] init result="+result);
            }
        }
        return mEmvL3CtrlProcess;
    }

    @Override
    public boolean loadConfigurationFromXML(String fileName) {
        deviceLogger.debug("[loadConfigurationFromXML] fileName="+" fileName="+fileName);
        boolean result = mMEEmvL3.loadConfiguration(fileName,this);
        deviceLogger.debug("[loadConfigurationFromXML] result="+result);
        return result;
    }

    @Override
    public boolean addCAPublicKey(@NonNull byte[] inputData) {
        deviceLogger.debug("[addCAPublicKey] inputData="+(inputData==null?"null": ISOUtils.hexString(inputData)));
        boolean result = mMEEmvL3.addCAPublicKey(inputData);
        deviceLogger.debug("[addCAPublicKey] result="+result);
        return result;
    }

    @Override
    public boolean deleteCAPublicKey(byte[] rid, @Nullable Integer index) {
        deviceLogger.debug("[deleteCAPublicKey] rid="+(rid==null?"null": ISOUtils.hexString(rid))+" index="+index);
        boolean result = mMEEmvL3.deleteCAPublicKey(rid,index);
        deviceLogger.debug("[deleteCAPublicKey] result="+result);
        return result;
    }

    @Override
    public boolean deleteAllCAPublicKey() {
        deviceLogger.debug("[deleteAllCAPublicKey]");
        boolean result = mMEEmvL3.deleteAllCAPublicKey();
        deviceLogger.debug("[deleteCAPublicKey] result="+result);
        return result;
    }

    @Override
    public CAPK getSpecifiedCAPublicKey(byte[] rid, @NonNull int index) {
        deviceLogger.debug("[getSpecifiedCAPublicKey] rid="+(rid==null?"null": ISOUtils.hexString(rid))+" index="+index);
        CAPK result = mMEEmvL3.getCAPublicKey(rid,index);
        deviceLogger.debug("[deleteCAPublicKey] result="+result);
        return result;
    }

    @Override
    public List<CAPK> getAllCAPublicKey() {
        deviceLogger.debug("[getAllCAPublicKey]");
        byte[] numRidIndex = mMEEmvL3.getCAPublicKeyCount();
        if(numRidIndex == null){
            return null;
        }
        ArrayList<CAPK> list = new ArrayList<CAPK>();
        int num = InnerUtils.bytesToInt(numRidIndex,-1,2,true);
        deviceLogger.debug("[getAllCAPublicKey] count="+num);
        for(int i=0;i<num;i++){
            byte[] rid = new byte[5];
            System.arraycopy(numRidIndex,2+i*6,rid,0,rid.length);
            byte index = numRidIndex[2+i*6+5];
            deviceLogger.debug("[getAllCAPublicKey] rid="+(rid==null?"null":ISOUtils.hexString(rid))+" index="+String.format("%1x",index&0xFF));
            CAPK capk = mMEEmvL3.getCAPublicKey(rid,index);
            deviceLogger.debug("[getAllCAPublicKey] capk="+(capk==null?"null":capk.toString()));
            list.add(capk);
        }
        deviceLogger.debug("[getAllCAPublicKey] size="+list.size());
        return list;
    }

    @Override
    public boolean addAID(@NonNull byte[] inputData, CardInterface aidStorageMode) {
        deviceLogger.debug("[addAID] inputData="+(inputData==null?"null": ISOUtils.hexString(inputData))+" aidStorageMode="+aidStorageMode);
        boolean result = mMEEmvL3.addAID(getCardContactMode(aidStorageMode),inputData);
        deviceLogger.debug("[addAID] result="+result);
        return result;
    }

    @Override
    public boolean deleteAID(byte[] aid, CardInterface aidStorageMode) {
        deviceLogger.debug("[deleteAID] aid="+(aid==null?"null": ISOUtils.hexString(aid))+" aidStorageMode="+aidStorageMode);
        boolean result = mMEEmvL3.deleteAID(getCardContactMode(aidStorageMode),aid);
        deviceLogger.debug("[deleteAID] result="+result);
        return result;
    }

    @Override
    public List<AID> getAID(byte[] aid, CardInterface aidStorageMode) {
        deviceLogger.debug("[getAID] aid="+(aid==null?"null": ISOUtils.hexString(aid))+" aidStorageMode="+aidStorageMode);
        if(aid != null){
            ArrayList list = new ArrayList();
            AID AID = mMEEmvL3.getAID(getCardContactMode(aidStorageMode),aid);
            list.add(AID);
            deviceLogger.debug("[getAID] size0="+list.size());
            return list;
        }
        byte[] numLenTlv = mMEEmvL3.getAIDCount(getCardContactMode(aidStorageMode));
        if(numLenTlv == null){
            return null;
        }
        ArrayList<AID> list = new ArrayList<AID>();
        int num = InnerUtils.bytesToInt(numLenTlv,-1,2,true);
        deviceLogger.debug("[getAID] num="+num);
        int offset = 2;
        for(int i=0; i < num; i++){
            byte[] lenFb = new byte[2];
            System.arraycopy(numLenTlv,offset,lenFb,0,lenFb.length);offset+=2;
            int lenFi = InnerUtils.bytesToInt(lenFb,-1,2,true);
            byte[] tlvFb = new byte[lenFi];
            System.arraycopy(numLenTlv,offset,tlvFb,0,tlvFb.length);offset+=lenFi;
            deviceLogger.debug("[getAID] tlvFb="+(tlvFb==null?"null":ISOUtils.hexString(tlvFb)));
            TLVPackage tlvPackage = EMVInnerUtils.newTlvPackage();
            tlvPackage.unpack(tlvFb);
            byte[] aidFb = tlvPackage.getValue(0x9F06);
            String aidFS = ISOUtils.hexString(aidFb);
            if(aidFS.startsWith("0000000000")){
                continue;
            }
            AID AID = mMEEmvL3.getAID(getCardContactMode(aidStorageMode),aidFb);
            list.add(AID);
        }
        deviceLogger.debug("[getAID] size="+list.size());
        return list;
    }

    @Override
    public boolean setTerminalConfiguration(byte[] tlvData, CardInterface aidStorageMode) {
        deviceLogger.debug("[setTerminalConfiguration] tlvData="+(tlvData==null?"null": ISOUtils.hexString(tlvData))+" aidStorageMode="+aidStorageMode);
        boolean result = mMEEmvL3.updateTerminalConfig(getCardContactMode(aidStorageMode),tlvData);
        deviceLogger.debug("[setTerminalConfiguration] result="+result);
        return result;
    }

    @Override
    public EMVCardInfo getCardInformation() {
        deviceLogger.debug("[getCardInformation]");
        return null;
    }

    @Override
    public void getEMVTransLogs(EMVTransLogListener transLogListener) {
        deviceLogger.debug("[getEMVTransLogs]");
//        DefaultGetLogListener listener = new DefaultGetLogListener(LogType.PBOC_LOG, transLogListener, null);
//        EMVTransController emvTransController = getEmvTransController(listener);
//        int  processingCode = 0x37;//int RF_PBOC_LOGGER = 0x37;
//        TransactionExtParams emvExtraParam = new TransactionExtParams();
//
//        emvExtraParam.setCardInterfaces(0x04);//sp00只能设置非接卡
//
//        emvTransController.startEMV(processingCode, new BigDecimal(0), false, emvExtraParam);
        //L3暂时未实现，先返回空
        transLogListener.onResult(null);

    }

    @Override
    public void getECTransLogs(ECTransLogListener transLogListener) {
        deviceLogger.debug("[getECTransLogs]");
        //L3暂时未实现，先返回空
        transLogListener.onResult(null);
    }

    @Override
    public boolean setEmvData(int tag, byte[] value) {
        deviceLogger.debug("[setEmvData] value="+(value==null?"null": ISOUtils.hexString(value))+" tag="+tag);
        boolean result = mMEEmvL3.setData(tag,value);
        deviceLogger.debug("[setEmvData] result="+result);
        return result;
    }

    @Override
    public TLVPackage getEmvData(int[] emvTags) {
        deviceLogger.debug("----[getEmvData] emvTags="+(emvTags==null?null:emvTags.toString()));
        if(emvTags == null){
            return null;
        }
        ArrayList<Integer> tags = new ArrayList();
        for(int i=0;i<emvTags.length;i++){
            tags.add(emvTags[i]);
        }
        byte[] result = mMEEmvL3.getTlvData(tags,true);
        deviceLogger.debug("[getEmvData] result="+(result==null?"null":ISOUtils.hexString(result)));
        TLVPackage tlvPackage = EMVInnerUtils.newTlvPackage();
        if (null != result)
            tlvPackage.unpack(result);
        return tlvPackage;
    }

    @Override
    public byte[] getEmvData(int tag) {
        deviceLogger.debug("[getEmvData] tag="+tag);
        byte[] result = mMEEmvL3.getData(tag);
        deviceLogger.debug("[getEmvData] result="+(result==null?"null":ISOUtils.hexString(result)));
        return result;
    }

    @Override
    public byte[] getICCdata(int tag) {
        deviceLogger.debug("[getICCdata] tag="+tag);
        return getEmvData(tag);
    }

    @Override
    public String getEMVKernelVersion() {
        deviceLogger.debug("[getEMVKernelVersion]");
        String version = mMEEmvL3.getVersion(EmvL3Const.MODULE.L3_MODULE_EMV);
        deviceLogger.debug("[getEMVKernelVersion] version="+version);
        return version;
    }

    private CardContactMode getCardContactMode(CardInterface aidStorageMode){
        if(aidStorageMode == CardInterface.CONTACT){
            return CardContactMode.CONTACT;
        }else if(aidStorageMode == CardInterface.CONTACTLESS){
            return CardContactMode.CONTACTLESS;
        }
        deviceLogger.error("[getCardContactMode] return null.");
        return null;
    }

    public boolean setDebugMode(int level){
        return mMEEmvL3.setDebugMode(level);
    }

    public abstract EMVTransInfo getEMVTransInfo();

    public abstract int getExecuteRslt(int resultCode,boolean isComplete);

    public abstract int getEmvrsltCode(int resultCode,boolean isComplete);

    public MEEmvL3 getMEEmvL3(){
        return mMEEmvL3;
    }

    public EmvL3Step getEmvL3Step(){
        return mEmvL3Step;
    }

    private class DefaultGetLogListener implements EMVControllerListener {

        private Throwable e;

        private boolean isSuccess = false;

        private List<EMVTransLog> pbocLogs = new ArrayList<EMVTransLog>();
        private List<ECTransLog> ecLogs = new ArrayList<ECTransLog>();

        private LogType logType;
        private EMVTransLogListener transLogListener;
        private ECTransLogListener ecListener;

        public DefaultGetLogListener(LogType logType, EMVTransLogListener transLogListener, ECTransLogListener ecListener) {
            this.logType = logType;
            this.transLogListener = transLogListener;
            this.ecListener = ecListener;
        }

        @Override
        public void onRequestConfirmCardInfo(EMVTransController controller) {
            deviceLogger.info("------DefaultGetLogListener---[onRequestConfirmCardInfo]---logType:"+logType);
            if (logType == LogType.PBOC_LOG) {
                List<EMVTransLog> rslt = new ArrayList<EMVTransLog>();
//                int count = getPbocLogCount();
//                PbocTransFormat fmt = getPbocLogFmt();
//                if (fmt == null) {
//                   if (lastCardReadContainType(CardType.RFCARD)) {
                        // 通过APDU获取非接电子现金日志
//                        RFCardModule rfCarf = (RFCardModule) getOwner().getStandardModule(ModuleType.RFCARDREADER);
                        ExtRFCardModule extRFCardModule = (ExtRFCardModule) getOwner().getExModule(ExModuleType.RFCARD);
                        ExtICCardModule extICCardModule = (ExtICCardModule) getOwner().getExModule(ExModuleType.ICCARD);
                        List<EMVTransLog> logs = new ArrayList<EMVTransLog>();
                        String pbocCount = null;
                        for (int i = 1; i < 11; i++) {
                            if (i < 10) {
                                pbocCount = "0" + i;
                            } else {
                                pbocCount = "0A";
                            }
                            String apdu = "00B2" + pbocCount + "5C00";
                            byte req[] = EMVInnerUtils.hex2byte(apdu);
                            byte transLog[] = null;
 //                           if (null != emvExtParams && emvExtParams.isExternalReader()) {
//                                if (emvExtParams.getMediaType() == 0x00) {
//                                    extICCardModule.transmit(req, null);
//                                } else {
                                    transLog = extRFCardModule.transmit(req);
 //                               }
//                            } else {
//                                transLog = rfCarf.transmit(req, 3);
//                            }

                            // deviceLogger.info("read pboc
                            // log:"+ISOUtils.hexString(transLog));
                            if (transLog != null && transLog.length == 47) {
                                logs.add(new EMVTransLog(transLog));
                            }
                        }
                        pbocLogs = logs;
//                    } else {
//                        pbocLogs = rslt;
//                    }
//                } else {
//                    for (int i = 1; i <= count; i++) {
//                        EMVTransLog log = getPbocLog(i, fmt);
//                        // count 为交易日志容量，当取到当前数据为空时默认最后一条
//                        if (null == log) {
//                            break;
//                        }
//                        rslt.add(log);
//                    }
//                    pbocLogs = rslt;
//                }
            } else if (logType == LogType.EC_LOG) {

//                List<ECTransLog> rslt = new ArrayList<ECTransLog>();
//                int count = getEcLogCount();
//                ECTransFormat fmt = getEcLogFmt();
//                if (fmt == null) {
//                    ecLogs = rslt;
//                } else {
//                    for (int i = 1; i <= count; i++) {
//                        ECTransLog log = getEcLog(i, fmt);
//                        // count 为交易日志容量，当取到当前数据为空时默认最后一条
//                        if (null == log) {
//                            break;
//                        }
//                        rslt.add(log);
//                    }
//                    ecLogs = rslt;
//                }
            }
            controller.confirmInformation(true);
        }

        @Override
        public void onRequestSelectApplication(EMVTransController controller, List<AIDEntity> aidEntityList, int times) {
            deviceLogger.info("------DefaultGetLogListener---[onRequestSelectApplication]---logType:"+logType);

            if (logType.equals(LogType.PBOC_LOG))
                controller.setSelectedApplication(transLogListener.onRequestSelectApplication(aidEntityList));
            else
                controller.setSelectedApplication(ecListener.onRequestSelectApplication(aidEntityList));
        }

        @Override
        public void onRequestInputPIN(EMVTransController controller, boolean requireOnline, PINEntity pinEntity) {
            deviceLogger.info("------DefaultGetLogListener---[onRequestInputPIN]---logType:"+logType);

        }

        @Override
        public void onRequestOnlineProcess(EMVTransController controller) {
            deviceLogger.info("------DefaultGetLogListener---[onRequestOnlineProcess]---logType:"+logType);
            if (logType == LogType.PBOC_LOG) {
                List<EMVTransLog> rslt = new ArrayList<EMVTransLog>();
//                int count = getPbocLogCount();
//                PbocTransFormat fmt = getPbocLogFmt();
//                if (fmt == null) {
//                   if (lastCardReadContainType(CardType.RFCARD)) {
                // 通过APDU获取非接电子现金日志
//                        RFCardModule rfCarf = (RFCardModule) getOwner().getStandardModule(ModuleType.RFCARDREADER);
                ExtRFCardModule extRFCardModule = (ExtRFCardModule) getOwner().getExModule(ExModuleType.RFCARD);
                ExtICCardModule extICCardModule = (ExtICCardModule) getOwner().getExModule(ExModuleType.ICCARD);
                List<EMVTransLog> logs = new ArrayList<EMVTransLog>();
                String pbocCount = null;
                for (int i = 1; i < 11; i++) {
                    if (i < 10) {
                        pbocCount = "0" + i;
                    } else {
                        pbocCount = "0A";
                    }
                    String apdu = "00B2" + pbocCount + "5C00";
                    byte req[] = EMVInnerUtils.hex2byte(apdu);
                    byte transLog[] = null;
                    //                           if (null != emvExtParams && emvExtParams.isExternalReader()) {
//                                if (emvExtParams.getMediaType() == 0x00) {
//                                    extICCardModule.transmit(req, null);
//                                } else {
                    transLog = extRFCardModule.transmit(req);
                    //                               }
//                            } else {
//                                transLog = rfCarf.transmit(req, 3);
//                            }

                    // deviceLogger.info("read pboc
                    // log:"+ISOUtils.hexString(transLog));
                    if (transLog != null && transLog.length == 47) {
                        logs.add(new EMVTransLog(transLog));
                    }
                }
                pbocLogs = logs;
//                    } else {
//                        pbocLogs = rslt;
//                    }
//                } else {
//                    for (int i = 1; i <= count; i++) {
//                        EMVTransLog log = getPbocLog(i, fmt);
//                        // count 为交易日志容量，当取到当前数据为空时默认最后一条
//                        if (null == log) {
//                            break;
//                        }
//                        rslt.add(log);
//                    }
//                    pbocLogs = rslt;
//                }
            } else if (logType == LogType.EC_LOG) {

//                List<ECTransLog> rslt = new ArrayList<ECTransLog>();
//                int count = getEcLogCount();
//                ECTransFormat fmt = getEcLogFmt();
//                if (fmt == null) {
//                    ecLogs = rslt;
//                } else {
//                    for (int i = 1; i <= count; i++) {
//                        ECTransLog log = getEcLog(i, fmt);
//                        // count 为交易日志容量，当取到当前数据为空时默认最后一条
//                        if (null == log) {
//                            break;
//                        }
//                        rslt.add(log);
//                    }
//                    ecLogs = rslt;
//                }
            }
            OnlineTransactionData onlineTransactionData = new OnlineTransactionData();
            onlineTransactionData.setAuthorisationResponseCode("00");
            controller.completeEMVProcess(onlineTransactionData);

        }

        @Override
        public void onFallback(EMVTransController controller) {
            deviceLogger.info("------DefaultGetLogListener---[onFallback]---logType:"+logType);
            if (logType.equals(LogType.PBOC_LOG))
                transLogListener.onResult(null);
            else
                ecListener.onResult(null);
        }

        @Override
        public void onError(EMVTransController controller, Exception e) {
            deviceLogger.info("------DefaultGetLogListener---[onError]---logType:"+logType);
            if (logType.equals(LogType.PBOC_LOG))
                transLogListener.onResult(null);
            else
                ecListener.onResult(null);

        }

        @Override
        public void onRequestSelectAccountType(EMVTransController controller, AccountType[] accountType) {
            deviceLogger.info("------DefaultGetLogListener---[onRequestSelectAccountType]---logType:"+logType);
            controller.setSelectedAccountType(AccountType.DEFAULT);
        }


        @Override
        public void onRequestConfirmID(EMVTransController controller, IDCardType cardType, String IDNo) {
            deviceLogger.info("------DefaultGetLogListener---[onRequestConfirmID]---logType:"+logType);

            controller.confirmID(true);
        }

        @Override
        public void onRequestConfirmEC(EMVTransController controller) {
            deviceLogger.info("------DefaultGetLogListener---[onRequestConfirmEC]---logType:"+logType);
            controller.confirmEC(true);
        }

        @Override
        public void onRequestShowMessage(EMVTransController controller, String title, String msg, boolean isConfirm, int timeOut) {
            deviceLogger.info("------DefaultGetLogListener---[onRequestShowMessage]---logType:"+logType);

            controller.confirmMessage(isConfirm);
        }

        @Override
        public void onRequestSelectLanguage(EMVTransController controller, String[] language) {
            deviceLogger.info("------DefaultGetLogListener---[onRequestSelectLanguage]---logType:"+logType);

            if (language != null && language.length > 0) {
                controller.setSelectedLanguage(language[0]);
            } else {
                controller.cancelEMVProcess();
            }
        }

        @Override
        public void onRequestConfirmFinalAppSelection(EMVTransController controller) {
            deviceLogger.info("------DefaultGetLogListener---[onRequestConfirmFinalAppSelection]---logType:"+logType);

            controller.confirmInformation(true);
        }

        @Override
        public void onEmvFinished(boolean isSuccess, EMVTransController controller) {
            deviceLogger.info("------DefaultGetLogListener---[onEmvFinished]---logType:"+logType);
            if (logType.equals(LogType.PBOC_LOG)) {
                transLogListener.onResult(pbocLogs);
            } else
                ecListener.onResult(ecLogs);
        }

        @Override
        public void onRequestInputAmount(EMVTransController controller) {
            deviceLogger.info("------DefaultGetLogListener---[onRequestInputAmount]---logType:"+logType);
            controller.setTransactionAmount(new BigDecimal(0));
        }
    }

}
