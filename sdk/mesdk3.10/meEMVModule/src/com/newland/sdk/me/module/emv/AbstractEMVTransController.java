package com.newland.sdk.me.module.emv;

import android.newland.os.NlBuild;
import android.util.Log;

import com.newland.emv.jni.service.EmvJNIService;
import com.newland.emv.jni.type.EmvConst;
import com.newland.emv.jni.type.emv_opt;
import com.newland.emv.jni.type.ep_opt;
import com.newland.emv.jni.type.rf_transdata;
import com.newland.ndk.NdkApiManager;
import com.newland.sdk.common.RunningModel;
import com.newland.sdk.mtypex.module.common.emv.CommonUtils;
import com.newland.sdk.module.emv.EmvExtParams;
import com.newland.sdk.module.emv.TransactionExtParams;
import com.newland.sdk.module.emv.TransactionType;
import com.newland.sdk.module.emv.ProcessingCode;
import com.newland.sdk.module.externaliccard.ExtICCardModule;
import com.newland.sdk.module.light.IndicatorLightModule;
import com.newland.sdk.module.light.LightColor;
import com.newland.sdk.module.light.LightState;
import com.newland.sdk.mtype.Device;
import com.newland.sdk.mtype.DeviceException;
import com.newland.sdk.mtype.ExModuleType;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.mtype.common.Const.EmvSelfDefinedReference;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.module.cardreader.CardReaderModule;
import com.newland.sdk.module.cardreader.CardType;
import com.newland.sdk.module.emv.EMVControllerListener;
import com.newland.sdk.module.emv.EMVTransController;
import com.newland.sdk.module.emv.EMVTransInfo;
import com.newland.sdk.module.emv.OnlineTransactionData;
import com.newland.sdk.module.externalPin.PinpadInitExtParams;
import com.newland.sdk.module.externalrfcard.ExtRFCardModule;
import com.newland.sdk.module.rfcard.RFCardModule;
import com.newland.sdk.module.rfcard.RFCardType;
import com.newland.sdk.module.rfcard.RFResult;
import com.newland.sdk.module.serialport.Baudrate;
import com.newland.sdk.module.serialport.PortType;
import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.mtypex.module.common.emv.SoundPoolImpl;
import com.newland.sdk.utils.TLVPackage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class AbstractEMVTransController implements EMVTransController {

    private DeviceLogger logger = DeviceLoggerFactory.getLogger("AbstractEMVTransController");

    private void publishEventRunner(Runnable runnable) {
        new Thread(runnable).start();
    }

    /********** emv内核处理返回结果，tag:DF 75 *********/
    /**
     * 步骤执行成功
     */
    public static final int _EMV_RSLT_STEP_SUCCESS = 0x00;
    /**
     * 交易接受
     */
    public static final int _EMV_RSLT_TC = 0x01;
    /**
     * 第一次生成密文，交易拒绝
     */
    public static final int _EMV_RSLT_AAC = 0x02;
    /**
     * 联机交易请求
     */
    public static final int _EMV_RSLT_ARQC = 0x03;
    /**
     * 第二次生成密文，交易拒绝
     */
    public static final int _EMV_RSLT_SECOND_AAC = 0x04;
    /**
     * 步骤执行失败
     */
    public static final int _EMV_RSLT_STEP_FAILED = 0xFF;
    /**
     * fallback
     */
    public static final int _EMV_RSLT_FALLBACK = 0xFE;
    /**
     * 预处理输入金额超出限额
     */
    public static final int _EMV_TRANS_AMT_LIMITOVER = -2105;
    /**
     * 圈存后金额大于卡片余额上限
     */
    public static final int _EMV_BALANCE_LIMITOVER = 0xF7;

    /*************** emv内核处理步骤声明 ***********/
    /**
     * < 应用选择初始化
     */
    public static final int _EMV_PROC_TO_APPSEL_INIT = 0x00;
    /**
     * < 读应用数据
     */
    public static final int _EMV_PROC_TO_READAPPDATA = 0x01;
    /**
     * < 离线数据认证
     */
    public static final int _EMV_PROC_TO_OFFLINEAUTH = 0x02;
    /**
     * < 处理限制
     */
    public static final int _EMV_PROC_TO_RESTRITCT = 0x03;
    /**
     * < 持卡人验证
     */
    public static final int _EMV_PROC_TO_CV = 0x04;
    /**
     * < 终端风险管理
     */
    public static final int _EMV_PROC_TO_RISKMANA = 0x05;
    /**
     * < 第一次密文生成
     */
    public static final int _EMV_PROC_TO_1GENAC = 0x06;
    /**
     * < 第二次密文生成
     */
    public static final int _EMV_PROC_TO_2GENAC = 0x07;
    /**
     * < PBOC交易继续
     */
    public static final int _EMV_PROC_CONTINUE = 0x08;

//    public int innerProcessCode;

    /**
     * 期望遵循的执行步骤
     **/
    private List<EMVTransStep> expectedSteps = EMVTransStep.defaultTransSteps();
    /**
     * 当前的执行步骤
     **/
    public volatile EMVTransStep currentStep = EMVTransStep.PREPARED;

    /******** 步骤映射到具体发交易时，对应的startEmv送的交易码 ***/
    private static Map<EMVTransStep, Integer> stepToPbocStepIndicatorMapping = new HashMap<EMVTransStep, Integer>();

    static {
        stepToPbocStepIndicatorMapping.put(EMVTransStep.APPLICATION_SELECT, _EMV_PROC_TO_APPSEL_INIT);
        stepToPbocStepIndicatorMapping.put(EMVTransStep.TRANSINFO_READ, _EMV_PROC_TO_READAPPDATA);
        stepToPbocStepIndicatorMapping.put(EMVTransStep.PINENTRY_INPUT, _EMV_PROC_TO_CV);
        stepToPbocStepIndicatorMapping.put(EMVTransStep.WAITING_TRANSFER_FINISHED, _EMV_PROC_TO_1GENAC);
        stepToPbocStepIndicatorMapping.put(EMVTransStep.CONTINUE, _EMV_PROC_CONTINUE);
    }

    private boolean needToOnline = false;
    private boolean isSimpleProcess = false;
    private boolean isNeedInterrupt = false; //简易流程触发最终应用选择标志
    private boolean isRequestAmt = false;
    private int USER_OPERATOR_WAITING_MILLS = 120000;
    volatile EpRFCardRequestAmtRunnable epRFCardRequestAmtRunnable = null;
    /**
     * emv交易返回数据
     */
    private EMVTransInfo emvTransInfo;
    /**
     * emv交易上下文，用于执行emv start
     */
    private EMVTransContext context;
    /**
     * 二次授权交易上下文数据
     */
    private OnlineTransactionData secondIssuanceRequest;

    /**
     * emv回调监听器
     */
    private EMVControllerListener listener;

    /**
     * 持有的设备对象
     */
    private Device owner;

    /**
     * 读卡器模块
     */
    private CardReaderModule cardreader;

    public ExtRFCardModule extRFCardModule;

    public ExtICCardModule extICCardModule;

    /**
     * 联机pin参数设置,若需要在处理过程中，要求进行联机pin过程处理，则该项目不为空。
     */
    private int mediaType;
    private EmvExtParams emvExtParams;
    private EmvJNIService emvcore = new EmvJNIService();
    /**
     * emv回调超时时间
     */
    private int timeout;
    /**
     * 对于部分交易，可能需要在执行完成后，调用对应的回调，以便继续进行
     * <p>
     * 例如：需要在<tt>EMVTransStep.TRANSINFO_READ</tt>执行完成后，回调交易信息显示，用于确认交易的进行
     * <p>
     * 若交易过程<tt>{@link #expectedSteps}</tt>设置了对应步骤，则在执行到该步骤时，该回调过程会被激活
     */
    private final Map<EMVTransStep, EmvStepCompleteCallback> stepContext = new HashMap<EMVTransStep, EmvStepCompleteCallback>();
    protected volatile EMVState currentEmvState = EMVState.PREPARED;
    //    private boolean isNDKEmv = false;
    private boolean NDKProcess = false;
    private boolean isSearchCardInEMV = false;
    private TransactionExtParams transactionExtParams;
    //    private boolean hasPreProccess = false;
    private IndicatorLightModule indicatorLightModule;
    private EMVCoreOperator defaultEmvOperator;

    private String profilePath;

    public boolean isEPStartB = false;
    public boolean isResetEPStartB = false;

    private MessageConfirmRunnable messageConfirmRunnable = null;

    public enum EMVState {
        PREPARED, APPLICATION_SELECT, EC_SWITCH, FINAL_APPLICATION_SELECT, FINAL_APPLICATION_SELECT_COMPLETE, PIN_INPUT, CERT_CONFIRM, ACCOUNT_SELECT, LANGUAGE_SELECT, MESSAGE_CONFIRM, CARDINFO_CONFIRM, EP_AMOUNT_INPUT, AMOUNT_INPUT, TRADE_ONLINE, FINISHED, ICC_POWER_ON, ICC_COMM, ICC_POWER_OFF;
    }

    /**
     * 步骤结束回调
     **/
    private interface EmvStepCompleteCallback {
        public void onStepFinished();
    }

    private class DefaultEmvStepCompleteCallback implements EmvStepCompleteCallback {

        /**
         * 如果需要被一个事件中断，则该方法返回true。
         * <p>
         * 类似CVM的流程中，如果需要一个持卡人密码输入过程中断，则该方法返回true.
         * <p>
         *
         * @param processingCode 处理结果
         * @return
         */
        protected boolean requestEvent(int processingCode) {
            return false;
        }

        /**
         * 若被事件中断，则需要执行的事件处理.
         * <p>
         *
         * @param processingCode 处理码
         */
        protected void processingEvent(int processingCode) {
        }

        /**
         * 是否是一个fallback事件。
         * <p>
         *
         * @param processingCode 处理结果
         * @return
         */
        private boolean isFallBack(int processingCode) {
            return processingCode == _EMV_RSLT_FALLBACK;
        }

        //EMV_onStepFinished
        @Override
        public void onStepFinished() {
            logger.debug("[onStepFinished] currentStep:" + currentStep + " isNDKEmv():" + context.isNDKEMVProcess());
            if (emvTransInfo == null || emvTransInfo.getExecuteRslt() == null) {
                doEmvErrorHappens0(new ProcessEmvStepException(-1, "processing meet unknown rslt:" + emvTransInfo == null ? "transInfo == null" : "processCode == null"));
                return;
            }
            //quick pass  gpo返回6986 需要二次拍卡
            // Start: B
            // Online Response Data: N/A
            //CVM: N/A
            //UI Request on Outcome Present: Yes
            //            o Message Identifier: ‘20’ (“See your mobile device for instructions”)
            //            o Status: Processing Error
            //            o Hold Time: 132
            //            o Language Preference: ‘en’
            //UI Request on Restart Present: Yes
            //            o Status: Ready to Read
            final Integer processErrorCode = emvTransInfo.getErrorcode();
            /// #define INITERR_GPO_RETURN_6986					(INITERR_BASE - 1422)		/*应用初始化返回6986(Application initialization returns 6986)*/
            int emvResultCode = emvTransInfo.getEmvrsltCode();
            int kernelId = emvTransInfo.getKernelId();
            int _UI_message_id = (null == emvTransInfo.getEpOpt() ? 0x00 : emvTransInfo.getEpOpt()._UI_message_id);
            logger.debug("[onStepFinished] errorCode=" + processErrorCode + " emvResultCode=" + emvResultCode + " kernelId=" + kernelId + ",ui_message_id:" + _UI_message_id);
            if ((processErrorCode == -1422 && kernelId == EmvConst.KERNEL_ID_UNIONPAY) ||
                    (emvResultCode == -15 && kernelId == EmvConst.KERNEL_ID_PAYWAVE) ||
                    (kernelId == EmvConst.KERNEL_ID_PAYPASS) && _UI_message_id == 0x20 ||
                    (kernelId == EmvConst.KERNEL_ID_PAYPASS) && _UI_message_id == 0x11||
                    (kernelId == EmvConst.KERNEL_ID_EXPRESSPAY) && _UI_message_id == 0x20 ||
                    (kernelId == EmvConst.KERNEL_ID_DISCOVER && _UI_message_id == 0x20)) {
                logger.debug("[onStepFinished]secondTapProcess");
                emvTransInfo.getEpOpt()._UI_message_id=0x16;
                secondTapProcess("See your mobile device for instructions", 132, null, _UI_message_id);
                return;
            }
            final Integer processingCode = emvTransInfo.getExecuteRslt();
            logger.debug("[onStepFinished] context.isNDKEMVProcess()():" + context.isNDKEMVProcess());
            logger.debug("[onStepFinished] processingCode:" + processingCode + ";isSimpleProcess:" + isSimpleProcess + ";lastCardReadContainType(CardType.RFCARD):" + lastCardReadContainType(CardType.RFCARD));
            logger.debug("context.isEpProcess():" + context.isEpProcess() + ";innerProcessCode:" + context.getInnerTransactionType());
            if (context.isNDKEMVProcess() && getEmvModule().isSupportEP(context.getInnerTransactionType()) && lastCardReadContainType(CardType.RFCARD)) {
                logger.debug("[onStepFinished] isNDKEmv");//
                if (isSimpleProcess) {
                    isSimpleProcess = false;
                    if (processingCode == _EMV_RSLT_ARQC || processingCode == _EMV_RSLT_TC || processingCode == _EMV_RSLT_STEP_SUCCESS) {
                        logger.debug("[onStepFinished] isNDKEmv isSimpleProcess doEmvFinish0 true");
                        emvTransInfo.setExecuteRslt(_EMV_RSLT_STEP_SUCCESS);
                        doEmvFinish0(true);
                    } else {
                        if (isFallBack(processingCode)) {
                            doEmvFallback0();
                            if (EMVInnerUtils.getIndicatorsAndBeep()) {
                                logger.debug("emv finish ");
                                indicatorLightModule.operateLight(new LightColor[]{LightColor.BLUE, LightColor.YELLOW, LightColor.GREEN, LightColor.RED}, LightState.TURNOFF);
                            }
                            return;
                        }
                        emvTransInfo.setExecuteRslt(processingCode);
                        logger.warn("[onStepFinished] isNDKEmv isSimpleProcess doEmvFinish0 false");
                        doEmvFinish0(false);
                    }
                    //if (currentStep == EMVTransStep.WAITING_TRANSFER_FINISHED && processingCode == _EMV_RSLT_ARQC) {
                } else if (processingCode == _EMV_RSLT_ARQC) {   //不再限制需要gac才能返回联机请求，如paypass的mag-stripe mode在读应用数据后就结束。参考c-2kernel 3.4 Mag-Stripe Mode and EMV Mode
                    logger.debug("[onStepFinished] _EMV_RSLT_ARQC");
                    needToOnline = true;
                    currentStep = EMVTransStep.WAITING_TRANSFER_FINISHED;
                    logger.debug("[onStepFinished] _EMV_RSLT_ARQC" + ";currentStep:" + currentStep);
                    doStandardEmvStep0();
                }else if(!lastCardReadContainType(CardType.RFCARD) && currentStep.hashCode() == EMVTransStep.APPLICATION_SELECT.hashCode() && processErrorCode  == -18){
                    logger.debug("[onStepFinished] doEmvFinish0 processErrorCode == -18 NDKEMV");
                    doEmvFinish0(false);
                    if (lastCardReadContainType(CardType.RFCARD) && EMVInnerUtils.getIndicatorsAndBeep()) {
                        logger.debug("emv finish -18.");
                        indicatorLightModule.operateLight(new LightColor[]{LightColor.BLUE, LightColor.YELLOW, LightColor.GREEN, LightColor.RED}, LightState.TURNOFF);
                    }
                } else if(!lastCardReadContainType(CardType.RFCARD) && currentStep.hashCode() == EMVTransStep.APPLICATION_SELECT.hashCode() && emvResultCode == -2){
                    logger.debug("[onStepFinished] FallBack IC AppSelect Step emvStart -2");
                    doEmvFallback0();
                    if (EMVInnerUtils.getIndicatorsAndBeep()) {
                        logger.debug("Emv fallBack  ");
                        indicatorLightModule.operateLight(new LightColor[]{LightColor.BLUE, LightColor.YELLOW, LightColor.GREEN, LightColor.RED}, LightState.TURNOFF);
                    }
                } else if (processingCode == _EMV_RSLT_AAC || processingCode == _EMV_RSLT_STEP_FAILED || processingCode == _EMV_TRANS_AMT_LIMITOVER ||
                        processingCode == _EMV_BALANCE_LIMITOVER || processingCode == _EMV_RSLT_SECOND_AAC || processErrorCode==-6) {
                    //logger.debug("[onStepFinished] _EMV_RSLT_AAC");
                    //doEmvFinish0(false);
                    logger.debug("[onStepFinished] _EMV_RSLT_AAC./"+currentStep.hashCode()+" "+processingCode+" ");
                    if(currentStep.hashCode() == EMVTransStep.WAITING_TRANSFER_FINISHED.hashCode() && processingCode == _EMV_RSLT_SECOND_AAC){
                        currentStep = EMVTransStep.ONLINEREQUEST;
                        secondIssuanceRequest = new OnlineTransactionData();
                        emv_opt emvOpt = new emv_opt();
                        emvOpt._online_result = 4;
                        getEMVTransInfo().setEmvParam(emvOpt);
                        doStandardEmvStep0();
                    }else {
                        doEmvFinish0(false);
                    }
                } else if (processingCode == _EMV_RSLT_TC) {
                    logger.debug("[onStepFinished] _EMV_RSLT_TC");
                    doEmvFinish0(true);
                    if (EMVInnerUtils.getIndicatorsAndBeep()) {
                        logger.debug("emv finish ");
                        indicatorLightModule.operateLight(new LightColor[]{LightColor.BLUE, LightColor.YELLOW, LightColor.GREEN, LightColor.RED}, LightState.TURNOFF);
                    }
                } else if (processingCode == _EMV_RSLT_STEP_SUCCESS) {
                    doEmvFinish0(true);
                    if (EMVInnerUtils.getIndicatorsAndBeep()) {
                        logger.debug("emv finish ");
                        indicatorLightModule.operateLight(new LightColor[]{LightColor.BLUE, LightColor.YELLOW, LightColor.GREEN, LightColor.RED}, LightState.TURNOFF);
                    }
                } else if (isFallBack(processingCode)) {
                    doEmvFallback0();
                    if (EMVInnerUtils.getIndicatorsAndBeep()) {
                        logger.debug("emv finish ");
                        indicatorLightModule.operateLight(new LightColor[]{LightColor.BLUE, LightColor.YELLOW, LightColor.GREEN, LightColor.RED}, LightState.TURNOFF);
                    }
                } else { // TODO ,还缺少对pin输入相应请求的判定。其余都是错误的处理返回.
                    throw new ProcessEmvStepException(processingCode, "NDKEmv onStepFinished unknown processingCode:" + processingCode + ",currentStep:" + currentStep);
                }

                return;
            }
            //EMV_NO_NDKEMV
            logger.info("[onStepFinished] processingCode:"+processingCode+";processErrorCode:"+processErrorCode);
            if (( currentStep == EMVTransStep.TRANSINFO_READ) && isSimpleProcess) {//currentStep == EMVTransStep.APPLICATION_SELECT ||
                isSimpleProcess = false;
                if (processingCode == _EMV_RSLT_ARQC || processingCode == _EMV_RSLT_TC || processingCode == _EMV_RSLT_STEP_SUCCESS) {
                    logger.debug("[onStepFinished] doEmvFinish0 true");
                    emvTransInfo.setExecuteRslt(_EMV_RSLT_STEP_SUCCESS);
                    doEmvFinish0(true);
                } else {
                    if (isFallBack(processingCode)) {
                        doEmvFallback0();
                        if (EMVInnerUtils.getIndicatorsAndBeep()) {
                            logger.debug("emv finish ");
                            indicatorLightModule.operateLight(new LightColor[]{LightColor.BLUE, LightColor.YELLOW, LightColor.GREEN, LightColor.RED}, LightState.TURNOFF);
                        }
                        return;
                    }
                    emvTransInfo.setExecuteRslt(processingCode);
                    logger.debug("[onStepFinished] doEmvFinish0 false");
                    doEmvFinish0(false);
                }
                //if (currentStep == EMVTransStep.WAITING_TRANSFER_FINISHED && processingCode == _EMV_RSLT_ARQC) {
            } else if (processingCode == _EMV_RSLT_ARQC) {   //不再限制需要gac才能返回联机请求，如paypass的mag-stripe mode在读应用数据后就结束。参考c-2kernel 3.4 Mag-Stripe Mode and EMV Mode
                logger.debug("[onStepFinished] _EMV_RSLT_ARQC");
                needToOnline = true;
                doStandardEmvStep0();
            } else if(!lastCardReadContainType(CardType.RFCARD) && currentStep.hashCode() == EMVTransStep.APPLICATION_SELECT.hashCode() && processErrorCode  == -18){
                logger.debug("[onStepFinished] doEmvFinish0 processErrorCode == -18");
                doEmvFinish0(false);
                if (lastCardReadContainType(CardType.RFCARD) && EMVInnerUtils.getIndicatorsAndBeep()) {
                    logger.debug("emv finish -18");
                    indicatorLightModule.operateLight(new LightColor[]{LightColor.BLUE, LightColor.YELLOW, LightColor.GREEN, LightColor.RED}, LightState.TURNOFF);
                }
            }else if(!lastCardReadContainType(CardType.RFCARD) && currentStep.hashCode() == EMVTransStep.APPLICATION_SELECT.hashCode() && emvResultCode == -2){
                logger.debug("[onStepFinished] FallBack IC AppSelect Step emvStart -2");
                doEmvFallback0();
                if (EMVInnerUtils.getIndicatorsAndBeep()) {
                    logger.debug("Emv fallBack  ");
                    indicatorLightModule.operateLight(new LightColor[]{LightColor.BLUE, LightColor.YELLOW, LightColor.GREEN, LightColor.RED}, LightState.TURNOFF);
                }
            } else if (processingCode == _EMV_RSLT_AAC || processingCode == _EMV_RSLT_STEP_FAILED || processingCode == _EMV_TRANS_AMT_LIMITOVER ||
                    processingCode == _EMV_BALANCE_LIMITOVER || processingCode == _EMV_RSLT_SECOND_AAC || processErrorCode==-6)                     {//有些visa卡超限额后，找下一条aid，错误码-6，结果码17，sdk没有退出，继续调用PayWave_Process，会陷入死循环
                logger.debug("[onStepFinished] _EMV_RSLT_AAC |"+currentStep.hashCode()+" "+processingCode+" ");
                if(currentStep.hashCode() == EMVTransStep.WAITING_TRANSFER_FINISHED.hashCode() && processingCode == _EMV_RSLT_SECOND_AAC){
                    currentStep = EMVTransStep.ONLINEREQUEST;
                    secondIssuanceRequest = new OnlineTransactionData();
                    emv_opt emvOpt = new emv_opt();
                    emvOpt._online_result = 4;
                    getEMVTransInfo().setEmvParam(emvOpt);
                    doStandardEmvStep0();
                }else {
                    doEmvFinish0(false);
                }
//            } else if (currentStep == EMVTransStep.WAITING_TRANSFER_FINISHED && processingCode == _EMV_RSLT_TC) {
            } else if (processingCode == _EMV_RSLT_TC) {
                logger.debug("[onStepFinished] _EMV_RSLT_TC");
                doEmvFinish0(true);
                if (lastCardReadContainType(CardType.RFCARD) && EMVInnerUtils.getIndicatorsAndBeep()) {
                    logger.debug("emv finish ");
                    indicatorLightModule.operateLight(new LightColor[]{LightColor.BLUE, LightColor.YELLOW, LightColor.GREEN, LightColor.RED}, LightState.TURNOFF);
                }
            } else if (processingCode == _EMV_RSLT_STEP_SUCCESS) {// 需要追加如果返回是需要输入pin的情况，选择应用也是同样的处理
                logger.debug("[onStepFinished] _EMV_RSLT_STEP_SUCCESS");
                if (requestEvent(processingCode)) {
                    publishEventRunner(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                processingEvent(processingCode); // 将由客户手动来触发下一个步骤
                            } catch (Exception e) {
                                doEmvErrorHappens0(e);
                            }
                        }
                    });
                } else {
                    logger.debug("[onStepFinished] doStandardEmvStep0-");
                    doStandardEmvStep0();
                }
            } else if (isFallBack(processingCode)) {
                logger.debug("[onStepFinished] isFallBack");
                doEmvFallback0();
                if (lastCardReadContainType(CardType.RFCARD) && EMVInnerUtils.getIndicatorsAndBeep()) {
                    logger.debug("emv finish ");
                    indicatorLightModule.operateLight(new LightColor[]{LightColor.BLUE, LightColor.YELLOW, LightColor.GREEN, LightColor.RED}, LightState.TURNOFF);
                }
            } else { // TODO ,还缺少对pin输入相应请求的判定。其余都是错误的处理返回.
                throw new ProcessEmvStepException(processingCode, "onStepFinished unknown processingCode:" + processingCode + ",currentStep:" + currentStep);
            }
        }
    }

    protected AbstractEMVTransController(Device owner, EmvExtParams emvExtParams, EMVControllerListener listener, List<EMVTransStep> expectedSteps) {
        this.listener = listener;
        this.owner = owner;
        this.emvExtParams = emvExtParams;
        this.indicatorLightModule = (IndicatorLightModule) owner.getStandardModule(ModuleType.INDICATOR_LIGHT);
        this.cardreader = (CardReaderModule) owner.getStandardModule(ModuleType.COMMON_CARDREADER);
        this.expectedSteps = expectedSteps;
        if (null != emvExtParams && emvExtParams.isExternalReader())
            this.mediaType = 0x01;//外接键盘目前只支持非接触
        init();
    }

    protected AbstractEMVTransController(Device owner, EmvExtParams emvExtParams, EMVControllerListener listener) {
        this.listener = listener;
        this.owner = owner;
        this.emvExtParams = emvExtParams;
        this.indicatorLightModule = (IndicatorLightModule) owner.getStandardModule(ModuleType.INDICATOR_LIGHT);
        this.cardreader = (CardReaderModule) owner.getStandardModule(ModuleType.COMMON_CARDREADER);

        if (null != emvExtParams && emvExtParams.isExternalReader()) {
            this.mediaType = emvExtParams.getMediaType();
        }
        if (lastCardReadContainType(CardType.RFCARD))
            expectedSteps = EMVTransStep.transWithoutConfirmSteps();
        init();

    }

    private void init() {
        // 初始化步骤回调
        stepContext.put(EMVTransStep.TRANSINFO_READ, new DoWhenReadAppDataComplete());
        stepContext.put(EMVTransStep.APPLICATION_SELECT, new DoWhenAppSelectComplete());
        stepContext.put(EMVTransStep.CONTINUE, new DoWhenReadAppDataComplete());
    }

    protected boolean lastCardReadContainType(CardType expectedType) {
        if (null != emvExtParams && emvExtParams.isExternalReader()) {
            if (expectedType == CardType.ICCARD && mediaType == 0x00) {
                return true;
            }
            if (expectedType == CardType.RFCARD && mediaType == 0x01) {
                return true;
            }
            return false;

        }
        CardType[] types = cardreader.getLastReaderTypes();
        if (types != null) {
            for (CardType type : types) {
                if (expectedType == type) {
                    return true;
                }
            }
        } else {
//            logger.warn("[lastCardReadContainType] types == null");
////            if (context.isNDKEMVProcess() && expectedType == CardType.RFCARD) {
//            if (expectedType == CardType.RFCARD) {
//                //NDK EMV 由emv寻卡，不知道上次卡类型
//                return true;
//            }
            return false;
        }
        return false;
    }

    private boolean preStartEmv(int innerProcessingCode) {
        try {
            logger.debug("[preStartEmv]");
            if (null != emvExtParams && emvExtParams.isExternalReader())
                context.setUseExtCardReader(true);
            if (this instanceof EMVLevel2TransferController && lastCardReadContainType(CardType.RFCARD)) {
                if (null != emvExtParams && emvExtParams.isExternalReader()) {
                    extRFCardModule = (ExtRFCardModule) owner.getExModule(ExModuleType.RFCARD);
                    if (transactionExtParams.getCurrentCardInterfaces() == null
                            || (transactionExtParams.getCurrentCardInterfaces() != 0x02
                            && transactionExtParams.getCurrentCardInterfaces() != 0x04)) {
                        boolean isSucc = false;
                        PortType portType = emvExtParams.getPortType();
                        Baudrate baudrate = emvExtParams.getBaudrate();
                        if (portType == PortType.BLEBASE_USB1 || portType == PortType.BLEBASE_USB2 ||
                                portType ==  PortType.BLEBASE_RS232) {
                            PinpadInitExtParams pinpadInitExtParams = new PinpadInitExtParams(portType, null,null,null);
                            isSucc = extRFCardModule.init(pinpadInitExtParams);
                        } else {
                            if (portType != null && baudrate != null) {
                                PinpadInitExtParams pinpadInitExtParams = new PinpadInitExtParams(portType, baudrate);
                                isSucc = extRFCardModule.init(pinpadInitExtParams);
                            } else {
                                isSucc = extRFCardModule.init(new PinpadInitExtParams(true));
                            }
                        }

                        if (!isSucc) {
                            logger.error("[preStartEmv] RFCardModule.init fail.");
                            currentEmvState = EMVState.FINISHED;
                            logger.debug(">>>[onError]1");
                            closeRfidLight();
                            listener.onError(this, new DeviceException(-20, "extRFCardModule.init failed"));
                            return false;
                        }
                        if (mediaType == 0x00 && null != emvExtParams && emvExtParams.isRequiredPrePowerOn()) {
                            byte[] result = extICCardModule.powerOn();
                            if (result == null) {
                                logger.error("[preStartEmv] External ICCardModule.powerOn fail.");
                                currentEmvState = EMVState.FINISHED;
                                logger.debug(">>>[onError]2");
                                closeRfidLight();
                                listener.onError(this, new DeviceException(-20, "extICCardModule.powerOn failed"));
                                return false;
                            }
                            logger.error("[preStartEmv] External ICCardModule.powerOn succ.");
                        } else if(null != emvExtParams && emvExtParams.isRequiredPrePowerOn()){
                            RFResult rfResult = extRFCardModule.powerOn(new RFCardType[]{RFCardType.ACARD, RFCardType.BCARD}, 5);//有TYPE B卡，支持emv流程
                            if (rfResult == null) {
                                logger.error("[preStartEmv] RFCardModule.powerOn fail.");
                                currentEmvState = EMVState.FINISHED;
                                logger.debug(">>>[onError]3");
                                closeRfidLight();
                                listener.onError(this, new DeviceException(-20, "extRFCardModule.powerOn failed"));
                                return false;
                            }
                            logger.error("[preStartEmv] RFCardModule.powerOn succ.");
                        }
                    } else {
                        logger.debug("[preStartEmv] needn't ExtPowerOn");
                    }
                } else {
                    if (!EMVInnerUtils.isSDK3()) {// 2.0还是需要上电，3.0智能库打开读卡器做上电
                        RFCardModule rfModule = (RFCardModule) owner.getStandardModule(ModuleType.RFCARDREADER);
                        RFResult rfResult = rfModule.powerOn(new RFCardType[]{RFCardType.ACARD, RFCardType.BCARD}, 2, null);
                        if (logger.isDebugEnabled() && rfResult != null)
                            logger.debug("[prePowerOn] rfcard powerup:" + rfResult.getRfcardType());
                    }
                }
            }
            // 目前一次只支持一种刷卡方式,其中iccard优先级最高
            if (null != emvExtParams && emvExtParams.isExternalReader()) {
                if (mediaType == 0x00) {
                    context.setMediaType(EMVTransContext._EMV_MEDIATYPE_ICCARD);
                } else {
                    context.setMediaType(EMVTransContext._EMV_MEDIATYPE_RFCARD);
                }
            } else {
                if (lastCardReadContainType(CardType.ICCARD)) {
                    context.setMediaType(EMVTransContext._EMV_MEDIATYPE_ICCARD);
                } else if (lastCardReadContainType(CardType.RFCARD)) {
                    context.setMediaType(EMVTransContext._EMV_MEDIATYPE_RFCARD);
                    if (innerProcessingCode == TransactionType.EC_APPOINTED_LOAD_CTLS || innerProcessingCode == TransactionType.EC_NOT_APPOINTED_LOAD_CTLS || innerProcessingCode == TransactionType.EC_CASH_LOAD_CTLS || innerProcessingCode == TransactionType.EC_CASH_LOAD_REVERSAL) {
                        expectedSteps = EMVTransStep.defaultTransSteps();
                    }
                } else {
                    context.setMediaType(getSpecifyMediaType());
                }
            }
            return true;
        } catch (Exception e) {
            logger.error("[preStartEmv] powerOn failed:" + e.getMessage());
            e.printStackTrace();
            currentEmvState = EMVState.FINISHED;
            if (e.getMessage().equals("device invoke failed!20")) { // 非接多卡冲突判断
                if (EMVInnerUtils.getIndicatorsAndBeep()) {
                    indicatorLightModule.operateLight(new LightColor[]{LightColor.BLUE}, LightState.TURNON);
                    SoundPoolImpl.getInstance(2).play(1, 200, 0);
                }
                logger.debug(">>>[onError]4");
                closeRfidLight();
                listener.onError(this, new DeviceException(-20, e.getMessage()));
            } else {
                logger.debug(">>>[onError]5");
                closeRfidLight();
                listener.onError(this, new DeviceException(-7, e.getMessage()));
            }
        }
        return false;
    }

    /**
     * 为方便起见，终端交易属性9f66，在设置AID时设置，9f66的值默认为"\x76\xC0\x00\x00",
     * 接口保持不变，只是在交易的时候9f66不用传。by zhangj
     *
     * @param processingCode
     * @param amount
     * @param cashback
     */
    private void initContext(final int processingCode, int innerProcessingCode, BigDecimal amount, BigDecimal cashback, boolean forceOnline) {
        if (context == null) {
            context = new EMVTransContext();
        }
        context.setEpProcess(true);
        if (innerProcessingCode == TransactionType.SIMPLE) {
            // if (null == amount) {
            amount = new BigDecimal(0);
            // }
            // if (null == cashback) {
            cashback = new BigDecimal(0);
            // }
            // forceOnline = true;
            forceOnline = false;
            context.setEpProcess(true);
            expectedSteps = EMVTransStep.defaultQuerySteps();
            // 由于国外卡在0步骤无法获取卡片数据如57、5a，因此简易流程步骤统一执行到1步骤，并且确保金额为0及强制联机。
            // if (lastCardReadContainType(ModuleType.RFCARDREADER)) {
            // // expectedSteps = EMVTransStep.defaultRFsimpleSteps();
            // expectedSteps = EMVTransStep.defaultQuerySteps();
            // } else {
            // expectedSteps = EMVTransStep.defaultQuerySteps();
            // }
            isSimpleProcess = true;
//            isNDKEmv = false;
            if (context.isNDKEMVProcess()) {
                context.setNDKEMVProcess(false);
                getEmvModule().initEmvEnv();
            }

//            if (listener instanceof EMVInterceptListener) {
            isNeedInterrupt = true;
//            }
            if (null != emvExtParams && emvExtParams.isExternalReader() && lastCardReadContainType(CardType.RFCARD))
                isNeedInterrupt = false;
            context.setSimpleProcess(true);
        }
        if (innerProcessingCode == TransactionType.ACCOUNT_INFO_CTLS) {
            amount = new BigDecimal(0);
            cashback = new BigDecimal(0);
            forceOnline = false;
            expectedSteps = EMVTransStep.defaultQuerySteps();
            isSimpleProcess = true;
            if (context.getMediaType() == 0x01) {
                innerProcessingCode = TransactionType.BALANCE;
                context.setEpProcess(false);
            }
            context.setSimpleProcess(true);
        }
        context.setTransactionType(processingCode);
        context.setInnerTransactionType(innerProcessingCode);
        if (amount != null)
            context.setAmountAuthorisedNumeric(toAmt(amount));

        if (cashback != null)
            context.setAmountOtherNumeric(toAmt(cashback));

        context.setForceOnline(forceOnline);
        ep_opt defaultEpOpt = getDefalutEpOpt(context);
        if (context.getEpOpt() != null) {
            ep_opt epOpt = context.getEpOpt();
            if (epOpt.nRequestAmt == 0) {
                epOpt.nRequestAmt = defaultEpOpt.nRequestAmt;
            }
            if (epOpt.emSeqTo == 0) {
                epOpt.emSeqTo = defaultEpOpt.emSeqTo;
            }
            if (epOpt.ucCardNo == 0x00) {
                epOpt.ucCardNo = defaultEpOpt.ucCardNo;
            }
            if (epOpt.emSeqStart == 0) {
                epOpt.emSeqStart = defaultEpOpt.emSeqStart;
            }
            if (epOpt._UI_message_id == 0x00) {
                epOpt._UI_message_id = defaultEpOpt._UI_message_id;
            }
            if (epOpt._UI_status == 0x00) {
                epOpt._UI_status = defaultEpOpt._UI_status;
            }
            if (epOpt._OP_status == 0x00) {
                epOpt._OP_status = defaultEpOpt._OP_status;
            }
            if (epOpt._OP_start == 0x00) {
                epOpt._OP_start = defaultEpOpt._OP_start;
            }
            if (epOpt._OP_cvm == 0x00) {
                epOpt._OP_cvm = defaultEpOpt._OP_cvm;
            }
            if (epOpt._OP_alternate_interface_preference == 0x00) {
                epOpt._OP_alternate_interface_preference = defaultEpOpt._OP_alternate_interface_preference;
            }
            if (epOpt._OP_field_off_request == 0x00) {
                epOpt._OP_field_off_request = defaultEpOpt._OP_field_off_request;
            }
            if (epOpt._OP_online_response_data == 0x00) {
                epOpt._OP_online_response_data = defaultEpOpt._OP_online_response_data;
            }
            if (epOpt.nForceOnlineEnable == 0) {
                epOpt.nForceOnlineEnable = defaultEpOpt.nForceOnlineEnable;
            }
            if (epOpt.ucTransType == 0x00) {
                epOpt.ucTransType = defaultEpOpt.ucTransType;
            }
            context.setEpOpt(epOpt);
//        epOpt._OP_ui_request_on_outcome_present = 0;
//        epOpt._OP_data_record_present = 0;
//        epOpt._OP_discretionary_data_present = 0;
//        epOpt._OP_receipt = 0;// N/A
        } else {
            context.setEpOpt(defaultEpOpt);
        }
        innerContextInit(context);
    }

    /**
     * context设置完毕，对于context是否需要内部处理，则由该方法完成
     * <p>
     * 例如，强制联机标志的处理
     */
    protected abstract void innerContextInit(EMVTransContext context);

    protected int getSpecifyMediaType() {
        throw new EMVTransferException("cannot start emv transfer without a expected mediatype!");
    }

    private String toAmt(BigDecimal amount) {
        long amtInt = amount.setScale(2, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).toBigInteger().longValue();
        if (amtInt > 999999999999L) {
            throw new IllegalArgumentException("amt out of range:" + amtInt);
        }
        return Long.toString(amtInt);
    }

    /**
     * 执行一个标准的emv流程
     **/
    //EMV_doStandardEmvStep0
    protected void doStandardEmvStep0() {
        try {
            logger.debug("[doStandardEmvStep0] currentStep:" + currentStep + ";needToOnline:" + needToOnline+";isNeedInterrupt:"+isNeedInterrupt);
            if (currentStep == null) {
                throw new EMVTransferException("current step should not be null!");
            }
            EMVTransStep nextStep = null;
            synchronized (currentStep) {// 计算下个步骤
                if (currentStep == EMVTransStep.WAITING_TRANSFER_FINISHED) {
                    if (needToOnline) {// 该标识由emv库返回{@link
                        // #DefaultEmvStepCompleteCallback}
                        nextStep = EMVTransStep.ONLINEREQUEST;
                        needToOnline = false;
                    } else// 否则直接结束
                        nextStep = EMVTransStep.FINISHED;
                } else if (currentStep == EMVTransStep.ONLINEREQUEST) {
                    nextStep = EMVTransStep.SECONDISSUANCE;
                } else if ((currentStep == EMVTransStep.TRANSINFO_READ) && isNeedInterrupt) { //如果简易流程选择应用选择中断，则需要再次发起读应用数据步骤||currentStep == EMVTransStep.APPLICATION_SELECT
                    isNeedInterrupt = false;
                    nextStep = EMVTransStep.TRANSINFO_READ;
                }else if((currentStep == EMVTransStep.APPLICATION_SELECT) && isNeedInterrupt){
                    isNeedInterrupt = false;
                    nextStep = EMVTransStep.TRANSINFO_READ;
                } else if (currentStep == EMVTransStep.TRANSINFO_READ && needToOnline) { //mag-stripe mode模式下 只到读应用数据步骤就发起联机
                    needToOnline = false;
                    nextStep = EMVTransStep.ONLINEREQUEST;
                } else {
                    nextStep = currentStep.next(expectedSteps);
                }
            }
            logger.error("[doStandardEmvStep0] currentStep="+currentStep+"->> nextStep="+nextStep);
            if (nextStep == null) { // 如果不存在下一步
                logger.error("[doStandardEmvStep0] nextStep == null doEmvFinish0 ");
                doEmvFinish0(true);
            } else if (nextStep == EMVTransStep.ONLINEREQUEST) {
                doOnlineRequest(emvTransInfo);
            } else if (nextStep == EMVTransStep.SECONDISSUANCE) {
                doSecondIssuance();
            } else if (nextStep == EMVTransStep.FINISHED) {
                doEmvFinish0(true);
            } else {
                currentStep = nextStep;
                Integer pbocStep = stepToPbocStepIndicatorMapping.get(currentStep);
                logger.debug("[doStandardEmvStep0] EmvJNIService currentStep=" + currentStep + " pbocStep=" + pbocStep);

                if (pbocStep == null) // 无法获取到下个pboc步骤
                    throw new EMVTransferException("unknown step translate to pboc step:" + nextStep);

                context.setPbocTransStep(pbocStep);
                if (context.getEpOpt() != null) {
                    ep_opt epOpt = context.getEpOpt();
                    epOpt.emSeqTo = pbocStep;
                    context.setEpOpt(epOpt);
                }
                if (emvTransInfo == null) {
                    emvTransInfo = new EMVTransInfo();
                    if (null != emvExtParams && emvExtParams.isExternalReader()) {
                        if (mediaType == 0x00) {
                            emvTransInfo.setOpenCardType(CardType.ICCARD);
                        } else {
                            emvTransInfo.setOpenCardType(CardType.RFCARD);
                        }
                    } else {
                        if ((cardreader == null || cardreader.getLastReaderTypes() == null) && context.isNDKEMVProcess()) {
                            emvTransInfo.setOpenCardType(CardType.RFCARD);//待确定
                        } else {
                            emvTransInfo.setOpenCardType(cardreader.getLastReaderTypes()[0]);
                        }
                    }
                }
                int transtype = context.getInnerTransactionType();
                String cashStr = context.getAmountAuthorisedNumeric();//非接ep流程金额为null时，请求金额输入
                byte[] amtData = getEmvModule().getEmvData(0x9F02);
                logger.debug("[processingEvent]cashStr:" + cashStr + ";transtype:" + transtype + ";amtData:" + (amtData == null ? null : InnerUtils.hexString(amtData)));
                if (currentStep == EMVTransStep.WAITING_TRANSFER_FINISHED && cashStr == null && (amtData == null || Arrays.equals(amtData, new byte[6])) && (transtype == TransactionType.PREAUTH || transtype == TransactionType.STANDARD) && context.isEpProcess() && lastCardReadContainType(CardType.RFCARD)) {
                    logger.debug("[doStandardEmvStep0] onRequestInputAmount");
                    EpRFCardRequestAmtRunnable requestAmtRunnable = new EpRFCardRequestAmtRunnable();
                    epRFCardRequestAmtRunnable = requestAmtRunnable;
                    new Thread(requestAmtRunnable).start();
                    requestAmtRunnable.startWaiting();
                    logger.debug("[doStandardEmvStep0]requestAmtRunnable.respData:" + requestAmtRunnable.respData);
                    if (requestAmtRunnable.respData != null) {
                        try {
                            String amt = toAmt(requestAmtRunnable.respData);
                            String amtStr = EMVInnerUtils.padleft(amt, 12, '0');// 填充到12个字长
                            byte[] amtBs = EMVInnerUtils.str2bcd(amtStr, true);

                            getEmvModule().setEmvData(0x9F02, amtBs);
                            context.setAmountAuthorisedNumeric(amt);
                        } catch (Exception e) {
                            e.printStackTrace();
                            emvTransInfo.setExecuteRslt(_EMV_RSLT_STEP_FAILED);
                        }
                    } else {
                        currentEmvState = EMVState.FINAL_APPLICATION_SELECT;
                        cancelEmv();
                        return;
                    }
                }
                logger.debug("[doStandardEmvStep0] doEmvCoreStep STEP:" + context.getPbocTransStep());
                emvTransInfo = doEmvCoreStep(context, emvTransInfo);
                logger.debug("[doStandardEmvStep0] doEmvCoreStep getEmvrsltCode:" + emvTransInfo.getEmvrsltCode()+" getErrorcode:"+emvTransInfo.getErrorcode()+
                        " _ER_L2_indication:"+context.getEpOpt()._ER_L2_indication);
                if (emvTransInfo.getEmvrsltCode() == EmvConst.EMV_TRANS_RF_SELECT_NEXT_AID &&
                        (emvTransInfo.getErrorcode() == -2105 || context.getEpOpt()._ER_L2_indication == 0x05)) {
                    doEmvFallback0();
                    if (EMVInnerUtils.getIndicatorsAndBeep()) {
                        indicatorLightModule.operateLight(new LightColor[]{LightColor.BLUE, LightColor.YELLOW, LightColor.GREEN, LightColor.RED}, LightState.TURNOFF);
                    }
                    return;
                }
                if (emvTransInfo.getEmvrsltCode() == EmvConst.EMV_TRANS_RF_SELECT_NEXT_AID) {
                    currentStep = EMVTransStep.PREPARED;
                    context.getEpOpt().emSeqStart = 0x03;
                    doStandardEmvStep0();
                    return;
                }
                if (currentStep == EMVTransStep.APPLICATION_SELECT &&
                        (context.getInnerTransactionType() == 0x0A || context.getInnerTransactionType() == 0x0E)) {
                    currentStep = EMVTransStep.TRANSINFO_READ;
                }
                EmvStepCompleteCallback callback = stepContext.get(currentStep);
                logger.debug("[doStandardEmvStep0]callback:" + callback);
                if (callback == null) { // 如果指定结果判定步骤为空，则使用默认的判定
                    callback = new DefaultEmvStepCompleteCallback();
                }
                logger.debug("[doStandardEmvStep0] EmvrsltCode:" + emvTransInfo.getEmvrsltCode());
                logger.debug("[doStandardEmvStep0] simple double 1 step isSimpleProcess:" + isSimpleProcess + " callback:" + (callback instanceof DoWhenReadAppDataComplete) + " isNeedInterrupt:" + isNeedInterrupt+";callback instanceof DoWhenAppSelectComplete:"+(callback instanceof DoWhenAppSelectComplete));
                if (isSimpleProcess == true && emvTransInfo.getEmvrsltCode() == 0 && ((callback instanceof DoWhenReadAppDataComplete) || (callback instanceof DoWhenAppSelectComplete)) && isNeedInterrupt && !(context.isNDKEMVProcess() && lastCardReadContainType(CardType.RFCARD))) {
                    logger.debug("[doStandardEmvStep0] simple double 1 step--------------STEP:" + context.getPbocTransStep());
                    setEmvData(0x9C, InnerUtils.hex2byte("00"));//大莱卡，简易流程9C要设置00，否则会找不到交易类型导致emv失败
                    this.confirmInformation(true);
                    return;
                }
                callback.onStepFinished(); // 由默认的处理机制来确认结束后的判定处理
            }
        } catch (Exception e) {
            doEmvErrorHappens0(e);
        }
    }


    //EMV_doSecondIssuance
    private void doSecondIssuance() {
        if (currentStep == null)
            throw new EMVTransferException("not accepted step:" + null);

        synchronized (currentStep) {
            if (currentStep != EMVTransStep.ONLINEREQUEST)
                throw new EMVTransferException("not accepted step:" + currentStep);

            currentStep = EMVTransStep.SECONDISSUANCE;// 二次授权
        }

        if (null != secondIssuanceRequest) {
            try {
                emvTransInfo = doEmvCoreSecondIssuance(context, secondIssuanceRequest, emvTransInfo);
                Integer processingCode = emvTransInfo.getExecuteRslt();
                if (processingCode == null) {
                    throw new ProcessEmvStepException(-1, "processing meet unknown rslt: processCode == null");
                }
                switch (processingCode) {
                    case _EMV_RSLT_STEP_SUCCESS:
                    case _EMV_RSLT_SECOND_AAC:
                        doEmvFinish0(false);
                        return;
                    case _EMV_RSLT_TC:
                        doEmvFinish0(true);
                        return;
                    case _EMV_RSLT_AAC:
                        doEmvFinish0(false);
                        return;
                    case _EMV_RSLT_STEP_FAILED:
                        doEmvFinish0(false);
                        return;
                    default:
                        throw new ProcessEmvStepException(processingCode, "doSecondIssuance unknown processingCode:" + processingCode + ",currentStep:" + currentStep);
                }
            } catch (Exception e) {
                doEmvErrorHappens0(e);
            }
        }
        throw new EMVTransferException("second issuance request should not be null!");
    }

    private void doInnerEmvFinish(boolean isSuccess) {
        logger.debug("[doInnerEmvFinish] isSuccess:" + isSuccess + ";currentStep:" + currentStep);
        synchronized (currentStep) { // 默认只结束一次
            if (currentStep == EMVTransStep.FINISHED) {
                return;
            }
            currentStep = EMVTransStep.FINISHED;
        }
        doEmvCoreFinish(context, isSuccess);
    }

    private class DoWhenReadAppDataComplete extends DefaultEmvStepCompleteCallback implements EmvStepCompleteCallback {
        @Override
        protected boolean requestEvent(int processingCode) {
            return true;
        }

        @Override
        protected void processingEvent(int processingCode) {
            try {
                logger.debug("[DoWhenReadAppDataComplete] processingCode:" + processingCode);
                currentEmvState = EMVState.CARDINFO_CONFIRM;
                logger.debug(">>>[onRequestConfirmCardInfo]");
                listener.onRequestConfirmCardInfo(AbstractEMVTransController.this);
            } catch (Exception e) {
                doEmvErrorHappens0(e);
            }
        }
    }

    private class DoWhenAppSelectComplete extends DefaultEmvStepCompleteCallback implements EmvStepCompleteCallback {
        @Override
        protected boolean requestEvent(int processingCode) {
            return true;
        }

        @Override
        protected void processingEvent(int processingCode) {
            try {
                logger.debug("[DoWhenAppSelectComplete] processingCode:" + processingCode);
                currentEmvState = EMVState.FINAL_APPLICATION_SELECT;
                logger.debug(">>>[onRequestConfirmFinalAppSelection]");
                listener.onRequestConfirmFinalAppSelection(AbstractEMVTransController.this);
            } catch (Exception e) {
                doEmvErrorHappens0(e);
            }
        }
    }

    protected void doEmvErrorHappens0(Exception reason) {
        try {
            doInnerEmvFinish(false);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[doEmvErrorHappens0] invoke emv finish command failed!", e);
        } finally {
            publishEmvErrorEvent(reason);
        }
    }

    /**
     * 执行一个标准的emv fallback流程
     */
    protected void doEmvFallback0() {
        try {
            doInnerEmvFinish(false);
        } catch (Exception e) {
            doEmvErrorHappens0(e);
        } finally {
            publishFallbackEvent(emvTransInfo);
        }

    }

    /**
     * 执行一个标准的emv结束流程
     *
     * @param isSuccess 该emv执行过程是否成功
     */
    protected void doEmvFinish0(boolean isSuccess) {
        doEmvFinish0(isSuccess, true);
    }

    protected void doEmvFinish0(boolean isSuccess, boolean needCallback) {
        try {
            doInnerEmvFinish(isSuccess);
        } catch (Exception e) {
            doEmvErrorHappens0(e);
        } finally {
            if (needCallback) {
                publishEmvFinishedEvent(isSuccess, emvTransInfo);
            }
        }
    }

    private void publishFallbackEvent(final EMVTransInfo emvTransInfo) {
        publishEventRunner(new Runnable() {
            @Override
            public void run() {
                try {
                    if (null != emvExtParams && emvExtParams.isExternalReader()) {
                        if (mediaType == 0x01) {
                            extRFCardModule.powerOff();
                        } else {
                            extICCardModule.powerOff();
                        }
                    }
                    currentEmvState = EMVState.FINISHED;
                    logger.debug(">>>[onFallback]");
                    closeRfidLight();
                    listener.onFallback(AbstractEMVTransController.this);
                } catch (Exception e) {
                    doEmvErrorHappens0(e);
                }
            }
        });
    }

    private void publishEmvErrorEvent(final Exception e) {
        publishEventRunner(new Runnable() {
            @Override
            public void run() {
                try {
                    if (null != emvExtParams && emvExtParams.isExternalReader()) {
                        if (mediaType == 0x01) {
                            extRFCardModule.powerOff();
                        } else {
                            extICCardModule.powerOff();
                        }
                    }
                    logger.info("[publishEmvErrorEvent] publish emv error!", e);
                    currentEmvState = EMVState.FINISHED;
                    logger.debug(">>>[onError]6");
                    closeRfidLight();
                    listener.onError(AbstractEMVTransController.this, e);
                } catch (Exception e) {
                    logger.error("[publishEmvErrorEvent] do listener onEmvError meeting error!", e);
                }
            }
        });
    }

    private void publishOnlineRequestEvent(final EMVTransInfo emvTransInfo) {
        publishEventRunner(new Runnable() {
            @Override
            public void run() {
                try {
                    logger.debug("[publishOnlineRequestEvent] call onRequestOnlineProcess");
                    currentEmvState = EMVState.TRADE_ONLINE;
                    logger.debug(">>>[onRequestOnlineProcess]");
                    closeRfidLight();
                    listener.onRequestOnlineProcess(AbstractEMVTransController.this);
                } catch (Exception e) {
                    e.printStackTrace();
                    doEmvErrorHappens0(e);
                }
            }
        });
    }

    private void publishEmvFinishedEvent(final boolean isSuccess, final EMVTransInfo emvTransInfo) {
        publishEventRunner(new Runnable() {
            @Override
            public void run() {
                try {
                    if (null != emvExtParams && emvExtParams.isExternalReader() && !isSuccess) {
                        if (mediaType == 0x01) {
                            extRFCardModule.powerOff();
                        } else {
                            extICCardModule.powerOff();
                        }
                    }
                    logger.debug("[publishEmvFinishedEvent] call onEmvFinished");
                    currentEmvState = EMVState.FINISHED;
                    logger.debug(">>>[onEmvFinished]1");
                    closeRfidLight();
                    listener.onEmvFinished(isSuccess, AbstractEMVTransController.this);
                } catch (Exception e) {
                    e.printStackTrace();
                    doEmvErrorHappens0(e);
                }
            }
        });
    }

    private void doOnlineRequest(EMVTransInfo emvTransInfo) throws Exception {
        logger.debug("[doOnlineRequest]");
        emvTransInfo.setEmvDuration(EmvDurationUtil.getEmvDuration());
        synchronized (currentStep) {
//            if (currentStep != EMVTransStep.WAITING_TRANSFER_FINISHED)
//                throw new EMVTransferException("not accepted step:" + currentStep);
            currentStep = EMVTransStep.ONLINEREQUEST;
        }
        // update20180524,在联机请求时做suspend操作，避免由于非接交易（非圈存类）不做doemvfinished导致的非接未下电情况导致的耗电问题
        int innerTransType = context.getInnerTransactionType();
        if (emvTransInfo.getOpenCardType() == CardType.RFCARD && innerTransType != TransactionType.EC_APPOINTED_LOAD_CTLS && innerTransType != TransactionType.EC_NOT_APPOINTED_LOAD_CTLS && innerTransType != TransactionType.EC_CASH_LOAD_CTLS && innerTransType != TransactionType.EC_CASH_LOAD_REVERSAL && context.getKernelID() != EmvConst.KERNEL_ID_RUPAY)
            doInnerEmvFinish(true);
        publishOnlineRequestEvent(emvTransInfo);
    }

    /**
     * 调用emv内核的startEmv
     *
     * @param context      emv交易上下文
     * @param emvTransInfo 交易返回数据，新增返回信息必须填写入该数据内
     * @return emv交易处理结果
     */
    public abstract EMVTransInfo doEmvCoreStep(EMVTransContext context, EMVTransInfo emvTransInfo);

    /**
     * 获取emv内核数据
     *
     * @param tag 内核标签
     * @return 内核数据
     */
    public abstract byte[] getEmvICCData(int tag);

    /**
     * 设置emv内核数据
     *
     * @param tag 内核标签
     */
    public abstract void setInnerEmvData(int tag, byte[] data);

    /**
     * 设置NLTag中unTagName的数据值s
     *
     * @param tag        标签名
     * @param data       数据
     * @param dataLength 数据长度
     */
    public abstract void writeNLTagData(int tag, byte[] data, int dataLength);

    /**
     * 调用emv内核的二次密文生成
     *
     * @param secondIssuanceRequest 用于二次授权的数据
     * @param emvTransInfo          交易返回数据，新增返回信息必须填写入该数据内
     * @return emv交易处理结果
     */
    protected abstract EMVTransInfo doEmvCoreSecondIssuance(EMVTransContext context, OnlineTransactionData secondIssuanceRequest, EMVTransInfo emvTransInfo);

    protected abstract void doEmvCoreFinish(EMVTransContext context, boolean isSuccess);

    public void startEMV2(int innerProcessingCode, BigDecimal amount, boolean forceOnline, TransactionExtParams extParams) {
        this.isResetEPStartB = true;
        startEMV(innerProcessingCode, amount, forceOnline, extParams);
    }

    @Override
    public void startEMV(int innerProcessingCode, BigDecimal amount, boolean forceOnline, TransactionExtParams extParams) {
        Log.d("SDKVersion", "startEMV,SDKVersion:"+ CommonUtils.getInstance().getSDKVersion());
        logger.debug("[startEMV] innerProcessingCode=" + innerProcessingCode + " amount=" + amount + " forceOnline=" + forceOnline + " extParams=" + extParams+" isResetEPStartB="+isResetEPStartB);
        transactionExtParams = extParams;
        if(isResetEPStartB){
            isEPStartB = true;
        }else {
            isEPStartB = false;
        }
        this.isResetEPStartB = false;
        extICCardModule = (ExtICCardModule) owner.getExModule(ExModuleType.ICCARD);

        if (null == context || !context.isNDKEMVProcess()) {
            boolean isSuccess = dealParams(innerProcessingCode, amount, forceOnline, extParams);
            if (!isSuccess)
                return;
            initContext(transactionExtParams.getProcessingCode(), innerProcessingCode, amount, transactionExtParams.getOtherAmount(), forceOnline);
        }
        boolean isSucc = preStartEmv(innerProcessingCode);
        logger.debug("[preStartEmv] result=" + isSucc);
        if (!isSucc) {
            logger.error("[preStartEmv] error.");
            return;
        }
        if(extParams!=null && extParams.isSupportEmvDuration() && EMVTransContext._EMV_MEDIATYPE_RFCARD == getContext().getMediaType()){//非接支持获取PPSE到GPO耗时的话，APDU到sdk来做
            new EmvJNIService().jniemvUseOutCardReader(1);
            EmvDurationUtil.startRecordEMVTime();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                doStandardEmvStep0();
            }
        }).start();
    }

    @Override
    public boolean preproccess(int innerProcessingCode, BigDecimal amount, boolean forceOnline, TransactionExtParams extParams) {
//        hasPreProccess = false;
        logger.debug("[preproccess] EmvJNIService->jniNLSDKEntryPointInitialize profilePath=" + profilePath);
        int nret = emvcore.jniNLSDKEntryPointInitialize(profilePath, defaultEmvOperator);//非接 ndk emv，最后一次初始化必须调用NDKEMV才是走NDKEMV
        logger.debug("[preproccess] EmvJNIService->jniNLSDKEntryPointInitialize nret=" + nret);
        if (nret < 0) {
            context.setNDKEMVProcess(false);
            return false;
        }
        boolean isSuccess = dealParams(innerProcessingCode, amount, forceOnline, extParams);
        if (!isSuccess) {
            logger.debug("[preproccess] dealParams fail");
            return false;
        }
        initContext(transactionExtParams.getProcessingCode(), innerProcessingCode, amount, transactionExtParams.getOtherAmount(), forceOnline);
        if (RunningModel.isDebugEnabled) {
            emvcore.jniemvSetDebugMode(3);
            logger.debug("[preproccess] jniemvSetDebugMode(3)");
        }

        byte[] pusCtrl = context.getPusCtrl();
        if (null != pusCtrl && pusCtrl.length >= 5) {
            if ((pusCtrl[4] & 0x01) == 0x01) {
                logger.debug("[preproccess] EmvJNIService->jniSDKEPRunToFinalSel(1)");
                emvcore.jniSDKEPRunToFinalSel(1);
            }
        }
        rf_transdata rfTransData = getDefaultRfTransData(context);
        ep_opt ep_opt = context.getEpOpt();
        logger.debug("[preproccess]---preproccess------start:" + System.currentTimeMillis());
        logger.debug("[preproccess] ep_opt");
        logger.debug("[preproccess] EmvJNIService->jniSDKEntryPointProcess");
        EMVInnerUtils.toString_ep_opt(logger, ep_opt);
        EMVInnerUtils.toString_rf_transdata(logger, rfTransData);
        int emvrslt = emvcore.jniSDKEntryPointProcess(ep_opt, rfTransData); //预处理
        logger.debug("[preproccess] EmvJNIService->jniSDKEntryPointProcess ret=" + emvrslt);
        context.setEpOpt(ep_opt);
        context.setRfTransData(rfTransData);
        logger.debug("[preproccess]---preproccess------end:" + System.currentTimeMillis());
        logger.debug("[preProcess] jniSDKEntryPointProcess emvrslt:" + emvrslt);
        if (EmvConst.EMV_TRANS_RF_ACTIVE_CARD != emvrslt) {
            logger.debug("[preproccess] EmvJNIService->jniSDKEntryPointSuspend(0)");
            emvcore.jniSDKEntryPointSuspend(0);
            return false;
        }
        logger.debug("[SDK:EMV] preproccess succ.");
        context.setNDKEMVProcess(true);
        expectedSteps = EMVTransStep.transWithoutConfirmSteps();
        return true;
    }

    private boolean dealParams(int innerProcessingCode, BigDecimal amount, boolean forceOnline, TransactionExtParams extParams) {
        isSimpleProcess = false;
        transactionExtParams = extParams;
        context = new EMVTransContext();
        if (transactionExtParams == null) {
            transactionExtParams = new TransactionExtParams(ProcessingCode.GOODS_AND_SERVICE, false, new BigDecimal("0"), null, null);
        }
        if (innerProcessingCode == -1) {
            innerProcessingCode = ProcessingCodeAdaptor.convertToInnerProcessingCode(transactionExtParams.getProcessingCode());
        }
        context.setInnerTransactionType(innerProcessingCode);
        if (transactionExtParams.getEpOpt() != null) {
            context.setEpOpt(transactionExtParams.getEpOpt());
        }
        isSearchCardInEMV = false;
        if (transactionExtParams.getPusCtrl() != null) {
            byte[] ctrl = transactionExtParams.getPusCtrl();
            context.setPusCtrl(ctrl);
            if (ctrl != null && ctrl.length > 0 && (ctrl[0] == 0x02 || ctrl[0] == 0x03)) {
                isSearchCardInEMV = true;
            }
        }
        if (transactionExtParams.getProcessData() != null) {
            context.setProcessData(transactionExtParams.getProcessData());
        }

        context.setGetUnionSpecialTag(transactionExtParams.isGetUnionSpecialTag());

//        if (!isNDKEmv) {
        if (transactionExtParams.isSupportSM()) {
            writeNLTagData(0x0012, new byte[]{0x01}, 1);
            logger.debug("[startEmv] support SM");
        } else {
            writeNLTagData(0x0012, new byte[]{0x00}, 1);
            logger.debug("[startEmv] unsupport SM");
        }
//        }
        try {
            beforeEmvStart(lastCardReadContainType(CardType.RFCARD), extParams);
            boolean isPowerSuccess = runToFinalSel();
            if (!isPowerSuccess && (null != this.listener)) {
                currentEmvState = EMVState.FINISHED;
                logger.debug(">>>[onError]7");
                closeRfidLight();
                listener.onError(this, new DeviceException(-7, "contactless transaction aborted\n"));
                return false; // 修改非接上电失败后，通过onError回调，不在往下执行。
            }
            if (transactionExtParams.getCustomerTag() != null) {
                setCustomerTagList(transactionExtParams.getCustomerTag());
            }
            if (lastCardReadContainType(CardType.RFCARD)) {
                if (transactionExtParams.isPpseAppSel()) {
                    logger.debug("[dealParams] EmvJNIService->jniSDKEPSetData(0x1F8102, new byte[]{0x01}, 1)");
                    emvcore.jniSDKEPSetData(0x1F8102, new byte[]{0x01}, 1);
                } else {
                    logger.debug("[dealParams] EmvJNIService->jniSDKEPSetData(0x1F8102, new byte[]{0x00}, 1)");
                    emvcore.jniSDKEPSetData(0x1F8102, new byte[]{0x00}, 1);
                }
            }
        } catch (Exception e) {
            logger.error("[startEmv] powerOn failed:" + e.getMessage());
            e.printStackTrace();
            currentEmvState = EMVState.FINISHED;
            if (e.getMessage().equals("device invoke failed!20")) { // 非接多卡冲突判断
                if (EMVInnerUtils.getIndicatorsAndBeep()) {
                    indicatorLightModule.operateLight(new LightColor[]{LightColor.BLUE}, LightState.TURNON);
                    SoundPoolImpl.getInstance(2).play(1, 200, 0);
                }
                logger.debug(">>>[onError]8");
                closeRfidLight();
                listener.onError(this, new DeviceException(-20, e.getMessage()));
            } else {
                logger.debug(">>>[onError]9");
                closeRfidLight();
                listener.onError(this, new DeviceException(-7, e.getMessage()));
            }
            return false;
        }
        return true;
    }

    private rf_transdata getDefaultRfTransData(EMVTransContext context) {
        rf_transdata rfData = new rf_transdata();
        long amt = 0;
        if (null != context.getAmountAuthorisedNumeric()) {
            amt = Long.valueOf(context.getAmountAuthorisedNumeric());
        }
        long amtOther = 0;
        if (null != context.getAmountOtherNumeric()) {
            amtOther = Long.valueOf(context.getAmountOtherNumeric());
        }
        rfData.nAmount = amt;
        rfData.nAmountOther = amtOther;
        DateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
        String time = sf.format(new Date());
        logger.debug("[startEmv] time:" + time + ", hex:" + EMVInnerUtils.hexString(EMVInnerUtils.hex2byte(time)));
        rfData.usDate = EMVInnerUtils.hex2byte(time);
        return rfData;
    }

    /**
     * @param context
     * @return
     */
    private ep_opt getDefalutEpOpt(EMVTransContext context) {
        ep_opt epOpt = new ep_opt();
        TLVPackage tp = EMVInnerUtils.newTlvPackage();
        tp.unpack(EMVInnerUtils.newEmvPackager().pack(context));
        String amount = context.getAmountAuthorisedNumeric();
        if ((null != amount && Long.valueOf(amount) > 0) || context.getInnerTransactionType() == TransactionType.BALANCE) {
            epOpt.nRequestAmt = EmvConst.EMV_TRANS_REQAMT_NO;
        } else {
            epOpt.nRequestAmt = EmvConst.EMV_TRANS_REQAMT_RFPRECESS;
        }
        byte[] value = tp.getValue(EmvSelfDefinedReference.PBOC_TRANS_STEP);
        if (value == null || value.length <= 0)
            throw new EMVTransferException("rf trans step should not be null!");
        epOpt.emSeqTo = value[0];
        epOpt.ucCardNo = (byte) 0xa1;// -------默认0xa1
        epOpt.emSeqStart = EmvConst.EntryPointSeq.START_A; // start of EntryPoint
        epOpt._UI_message_id = EmvConst.UI_MSGID_PROCESSING;
        epOpt._UI_status = EmvConst.UI_STATUS_IDLE;
        epOpt._OP_status = (byte) EmvConst.OP_STATUS_NA;
        epOpt._OP_start = (byte) EmvConst.OP_START_NA;
        epOpt._OP_cvm = (byte) EmvConst.OP_CVM_NA;
        epOpt._OP_ui_request_on_outcome_present = 0;
        epOpt._OP_data_record_present = 0;
        epOpt._OP_discretionary_data_present = 0;
        epOpt._OP_receipt = 0;// N/A
        epOpt._OP_alternate_interface_preference = (byte) EmvConst.OP_ALTERNATE_INTERFACE_PREFERENCE;
        epOpt._OP_field_off_request = (byte) EmvConst.OP_FIELD_OFF_REQUEST;
        epOpt._OP_online_response_data = (byte) EmvConst.OP_ONLINE_RESPONSE_DATA_NA;
        value = tp.getValue(EmvSelfDefinedReference.FORCE_ONLINE);
        if (value == null || value.length <= 0) {
            value = new byte[]{EMVTransContext._EMV_PRCO_FORCEONLINE}; // 默认强制联机
        }
        epOpt.nForceOnlineEnable = value[0];
        logger.debug("[getDefalutEpOpt] epOpt.nForceOnlineEnable :" + epOpt.nForceOnlineEnable);
        // Purchase: 00 Goods: 00 Cash: 01 Cashback: 09 Inquiry: 31 Transfer: 40
        // Payment: 50 Administrative: 66 Cash Deposit: 21 refund 0x20
        if (context.getInnerTransactionType() == TransactionType.STANDARD || context.getInnerTransactionType() == TransactionType.EC_CONSUMPTION) {
            epOpt.ucTransType = 0x00;
        } else if (context.getInnerTransactionType() == TransactionType.BALANCE) {
            epOpt.ucTransType = 0x31;
        } else if (context.getInnerTransactionType() == TransactionType.PREAUTH) {
            epOpt.ucTransType = 0x03;
        } else if (context.getInnerTransactionType() == TransactionType.CASHBACK) {
            epOpt.ucTransType = 0x09;
        } else if (context.getInnerTransactionType() == TransactionType.CASH) {
            epOpt.ucTransType = 0x01;
        } else {
            epOpt.ucTransType = (byte) context.getInnerTransactionType();
        }

        return epOpt;
    }


    public boolean setCustomerTagList(int[] customerTag) { //需要在交易开始前设置，suspend会把这个列表清掉，这个注意一下.
        for (int i = 0; i < customerTag.length; i++) {
            logger.debug("[setCustomerTagList] tags---:" + Integer.toHexString(customerTag[i]).toUpperCase());
        }
        int result = -1;
        logger.debug("[setCustomerTagList] customerTag=" + EMVInnerUtils.toString_tags(customerTag));
        if(lastCardReadContainType(CardType.RFCARD)){
            result = emvcore.jniSDKEPSetCustomerTagList(customerTag, customerTag.length);
            logger.debug("[setCustomerTagList] EmvJNIService->jniSDKEPSetCustomerTagList ret=" + result);
        }else {
            result = emvcore.jniemvsetcustomertaglist(customerTag, customerTag.length);
            logger.debug("[setCustomerTagList] EmvJNIService->jniemvsetcustomertaglist ret=" + result);
        }
        if (result == 0) {
            return true;
        }
        return false;

    }

    public boolean runToFinalSel() {
        if (this instanceof EMVLevel2TransferController && lastCardReadContainType(CardType.RFCARD)) {
            if (null != emvExtParams && emvExtParams.isExternalReader()) {
            } else {
                logger.debug("[runToFinalSel] EmvJNIService->jniSDKEPRunToFinalSell(0x01)");
                emvcore.jniSDKEPRunToFinalSel(0x01);
            }
        } else if (lastCardReadContainType(CardType.ICCARD)) {
            logger.debug("[runToFinalSel] EmvJNIService->jniEMVrunToFinalSel(0x01)");
            emvcore.jniEMVrunToFinalSel(0x01);
        }
        return true;
    }

    protected abstract void beforeEmvStart(boolean isRfCard, TransactionExtParams extParams);


    @Override
    public void setSelectedApplication(int index) {
        // 默认做空实现
    }

    @Override
    public void setPIN(byte[] pinblock) {
        // 默认做空实现
    }

    @Override
    public void setTransactionAmount(BigDecimal amount) {

    }

    @Override
    public void confirmInformation(boolean confirmed) {
        if (confirmed) { // 用户若确认继续执行
            doStandardEmvStep0();
        } else {
            if (null != emvTransInfo) {
                emvTransInfo.setErrorcode(0);
                emvTransInfo.setEmvrsltCode(_EMV_RSLT_STEP_FAILED);
                emvTransInfo.setExecuteRslt(_EMV_RSLT_STEP_FAILED);
            }
            doEmvFinish0(false);
        }
    }

    public void cancelEmv() {
        OnlineTransactionData secondIssuanceRequest = new OnlineTransactionData();
        secondIssuanceRequest.setAuthorisationResponseCode("01");
        completeEMVProcess(secondIssuanceRequest);
    }

    //EMV_completeEMVProcess
    @Override
    public void completeEMVProcess(OnlineTransactionData inputData) {
        logger.debug("[completeEMVProcess]inputData:" + inputData + ";innerProcessCode:" + context.getInnerTransactionType());
        int innerProcessCode = context.getInnerTransactionType();
        String authorisationResponseCode = inputData.getAuthorisationResponseCode();
        boolean isRFCard = lastCardReadContainType(CardType.RFCARD);
        boolean isOnlineSucess = false;
        if (authorisationResponseCode != null && "00".equals(authorisationResponseCode)) {
            isOnlineSucess = true;
        }
        if (emvTransInfo.getKernelId() != EmvConst.KERNEL_ID_RUPAY && isRFCard && innerProcessCode != TransactionType.EC_APPOINTED_LOAD_CTLS && innerProcessCode != TransactionType.EC_NOT_APPOINTED_LOAD_CTLS && innerProcessCode != TransactionType.EC_CASH_LOAD_CTLS && innerProcessCode != TransactionType.EC_CASH_LOAD_REVERSAL) {//非接卡，并且交易类型不是圈存类型，结束流程
            endTransaction(isOnlineSucess, true);
        } else {//IC卡/非接圈存/rupay，做二次授权
            if (isRFCard && context.getKernelID() == EmvConst.KERNEL_ID_RUPAY && emvTransInfo.getEmvrsltCode() == 23) {
                TLVPackage tlvPackage = InnerUtils.newTlvPackage();
                tlvPackage.unpack(inputData.getTlvData());
                byte[] tag71 = tlvPackage.getValue(0x71);
                byte[] tag72 = tlvPackage.getValue(0x72);
                byte[] tag91 = tlvPackage.getValue(0x91);
//                内核返回值为：EMV_TRANS_RF_MCHIP_GOONLINE_ONLINETAP   23
//                如果没有脚本返回，处理过程与其他的非接一样，解析联机响应码，并结束交易流程。   //suspend
//                如果有脚本返回，并且gstEntryPointOpt._OP_start == OP_START_D，需要进行二次拍卡，重新寻卡上电进行脚本更新。
                if (null != emvTransInfo.getEpOpt() && emvTransInfo.getEpOpt()._OP_start == EmvConst.OP_START_D) {
                    boolean judgetag91 = false;
                    if (null != tag91 && tag91.length >= 8) {
                        byte[] compare = new byte[4];
                        System.arraycopy(tag91, 4, compare, 0, 4);
                        //csu byte 3 bit7-8
                        //00b – Do not update offline balance
                        //01b – Add to offline balance
                        //10b – Deduct from offline balance
                        //11b – RFU
                        boolean balanceUpate = (((compare[2] & 0x40) == 0x40) || ((compare[2] & 0x80) == 0x80));
                        if (Arrays.equals(compare, new byte[]{(byte) 0x80, (byte) 0x80, (byte) 0x80, 0x00}) || Arrays.equals(compare, new byte[]{(byte) 0x80, (byte) 0x80, (byte) 0x40, 0x00}) || Arrays.equals(compare, new byte[]{(byte) 0x83, (byte) 0x94, 0x40, 0x00}) || balanceUpate) {
                            judgetag91 = true;
                        }
                    }
                    if (null != tag71 || null != tag72 || judgetag91) {
                        String respCode = inputData.getAuthorisationResponseCode();
                        logger.debug("----respCode:" + respCode);
                        if (respCode != null) {
                            emvTransInfo.getEpOpt().pusAuthRespCode = respCode.getBytes();
                        }

                        int onlineResult;
                        if (null == respCode) {
                            onlineResult = EMVLevel2Const.EmvExecRslt.EMV_TRANS_ONLINEFAIL;
                        } else if ("00".equals(respCode)) {
                            onlineResult = EMVLevel2Const.EmvExecRslt.EMV_TRANS_ONLINESUCC_ACCEPT;
                        } else if ("01".equals(respCode)) {
                            onlineResult = EMVLevel2Const.EmvExecRslt.EMV_TRANS_ONLINESUCC_ISSREF;
                        } else {
                            onlineResult = EMVLevel2Const.EmvExecRslt.EMV_TRANS_ONLINESUCC_DENIAL;
                        }
                        emvTransInfo.getEpOpt().nOnlineResult = onlineResult;
                    }
                    try {
                        secondTapProcess("UI_RUPAY_2nd_TAP", 30, inputData.getTlvData(),0x00);
                    } catch (Exception e) {
                        e.printStackTrace();
                        doEmvErrorHappens0(e);
                    }
                    return;
                }
                endTransaction(isOnlineSucess, true);
            } else {
                if (isRFCard && context.getKernelID() == EmvConst.KERNEL_ID_RUPAY && emvTransInfo.getEmvrsltCode() != 22) {
                    logger.debug("[completeEMVProcess]rupay endTransaction");
                    endTransaction(isOnlineSucess, true);
                    return;
                }
                this.secondIssuanceRequest = inputData;
                doStandardEmvStep0();// 二次授权步骤中，就会根据返回的状态决定结果是接受还是拒绝，从而结束emv交易。因此步需要再次发起结束
            }
        }
    }

    void endTransaction(boolean isOnlineSucess, boolean needCallBack) {
        if (EMVInnerUtils.getIndicatorsAndBeep()) {
            logger.debug("online finish turn on light ");
            indicatorLightModule.operateLight(new LightColor[]{LightColor.BLUE, LightColor.YELLOW, LightColor.GREEN, LightColor.RED}, LightState.TURNON);
        }
        if (null != emvTransInfo) {
            emvTransInfo.setEmvrsltCode(0);
            emvTransInfo.setErrorcode(0);
            emvTransInfo.setExecuteRslt(_EMV_RSLT_STEP_SUCCESS);
            if (!isOnlineSucess) {
                emvTransInfo.setEmvrsltCode(_EMV_RSLT_STEP_FAILED);
                emvTransInfo.setExecuteRslt(_EMV_RSLT_STEP_FAILED);
            }
        }
        doEmvFinish0(isOnlineSucess, needCallBack);
        if (EMVInnerUtils.getIndicatorsAndBeep()) {
            logger.debug("emv finish ");
            indicatorLightModule.operateLight(new LightColor[]{LightColor.BLUE, LightColor.YELLOW, LightColor.GREEN, LightColor.RED}, LightState.TURNOFF);
        }
    }

    /******* 暴露给子类的方法 *********/
    public List<EMVTransStep> getExpectedSteps() {
        return expectedSteps;
    }

    public EMVTransStep getCurrentStep() {
        return currentStep;
    }

    public boolean isNeedToOnline() {
        return needToOnline;
    }

    public EMVTransInfo getEmvTransInfo() {
        return emvTransInfo;
    }

    public EMVTransContext getContext() {
        return context;
    }

    public OnlineTransactionData getSecondIssuanceRequest() {
        return secondIssuanceRequest;
    }

    public EMVControllerListener getListener() {
        return listener;
    }

    public Device getOwner() {
        return owner;
    }

    protected abstract MEEMVLevel2 getEmvModule();

    public boolean isRequestAmt() {
        return isRequestAmt;
    }

    public void setRequestAmt(boolean requestAmt) {
        isRequestAmt = requestAmt;
    }

    public class EpRFCardRequestAmtRunnable implements Runnable {

        private Object sync = new Object();
        private byte[] data;
        volatile BigDecimal respData = null;


        @Override
        public void run() {
            try {
                if (!owner.isAlive())
                    return;
                currentEmvState = EMVState.EP_AMOUNT_INPUT;
                isRequestAmt = true;
                logger.debug(">>>[onRequestInputAmount]");
                listener.onRequestInputAmount(AbstractEMVTransController.this);
            } catch (Exception e) {
                onError(e);
            }
        }

        void startWaiting() {
            synchronized (sync) {
                try {
                    if (timeout > 0) {
                        logger.debug("[startWaiting] timeout:" + timeout);
                        USER_OPERATOR_WAITING_MILLS = timeout * 1000;
                    }
                    sync.wait(USER_OPERATOR_WAITING_MILLS);
                } catch (InterruptedException e) {
                    respData = null;
                    isRequestAmt = false;
                }
            }
        }

        /**
         * 输入pin，若为空或者为
         */
        public void inputAmtResult(BigDecimal respData) {
            try {
                this.respData = respData;
            } finally {
                notifyWaiting();
            }
        }

        void onError(Exception e) {
            try {
                currentEmvState = EMVState.FINISHED;
                logger.debug(">>>[onError]10");
                closeRfidLight();
                listener.onError(null, e);
                e.printStackTrace();
            } finally {
                notifyWaiting();
            }
        }

        private void notifyWaiting() {
            synchronized (sync) {
                sync.notify();
                isRequestAmt = false;
            }
        }

    }

    public EpRFCardRequestAmtRunnable getEpRFCardRequestAmtRunnable() {
        return epRFCardRequestAmtRunnable;
    }

    public void setEpRFCardRequestAmtRunnable(EpRFCardRequestAmtRunnable epRFCardRequestAmtRunnable) {
        this.epRFCardRequestAmtRunnable = epRFCardRequestAmtRunnable;
    }

    public void setCurrentEmvState(EMVState currentEmvState) {

        this.currentEmvState = currentEmvState;
    }

    @Override
    public boolean setEmvData(int tag, byte[] value) {
        return getEmvModule().setEmvData(tag, value);
    }

    @Override
    public TLVPackage getEmvData(int[] emvTags) {
        return getEmvModule().getEmvData(emvTags);
    }

    @Override
    public byte[] getEmvData(int emvTag) {
        byte[] resp = getEmvModule().getEmvData(emvTag);
        return resp;
    }

    @Override
    public byte[] getICCdata(int tag) {
        return getEmvModule().getICCdata(tag);
    }

    @Override
    public EMVTransInfo getEMVTransInfo() {
        return emvTransInfo;
    }

//    public void setExtCardReader(boolean extCardReader) {
//        isExtCardReader = extCardReader;
//    }

    public ExtRFCardModule getExternalRFCardModule() {
        return extRFCardModule;
    }

    public ExtICCardModule getExtICCardModule() {
        return extICCardModule;
    }

    public EmvExtParams getEmvExtParams() {
        return emvExtParams;
    }

    @Override
    public void setEMVTimeOut(int timeout) {
        logger.debug("[setEMVTimeOut] timeout:" + timeout);
        this.timeout = timeout;
    }

    public int getEMVTimeOut() {
        logger.debug("[getEMVTimeOut] timeout:" + timeout);
        return timeout;
    }

//    public boolean isNDKEmv() {
//        return isNDKEmv;
//    }
//
//    public void setNDKEmv(boolean NDKEmv) {
//        isNDKEmv = NDKEmv;
//    }

    public EMVState getCurrentEmvState() {
        return currentEmvState;
    }

    public boolean hasDoneNDKEMV() {
        if (context != null && context.isNDKEMVProcess()) {
            return true;
        }
        return false;
    }

    public void setDefaultEmvOperator(EMVCoreOperator defaultEmvOperator) {
        this.defaultEmvOperator = defaultEmvOperator;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }

    public MessageConfirmRunnable getMessageConfirmRunnable() {
        return messageConfirmRunnable;
    }

    public void setMessageConfirmRunnable(MessageConfirmRunnable messageConfirmRunnable) {
        this.messageConfirmRunnable = messageConfirmRunnable;
    }

    class MessageConfirmRunnable implements Runnable {
        private Object sync = new Object();

        volatile boolean isConfirm = false;

        private String title;
        private String msg;
        private boolean needYesNo = true;
        private int waittime;

        public MessageConfirmRunnable(String title, String msg, boolean needYesNo, int waittime) {
            this.title = title;
            this.msg = msg;
            this.needYesNo = needYesNo;
            this.waittime = waittime;
        }

        @Override
        public void run() {
            logger.debug("[MessageConfirmRunnable] onRequestShowMessage");
            logger.debug(">>>[onRequestShowMessage]");
            listener.onRequestShowMessage(AbstractEMVTransController.this, title, msg, needYesNo, waittime);
        }

        public boolean startwaiting(int waitting) {
            synchronized (sync) {
                try {
                    sync.wait(waitting);
                } catch (InterruptedException e) {
                }
            }
            return isConfirm;
        }

        /**
         * 输入confirm result
         */
        void confirmMessage(boolean isConfirm) {
            try {
                logger.debug("[confirmMessage]isConfirm:" + isConfirm);
                this.isConfirm = isConfirm;
            } finally {
                notifyWaiting();
            }
        }

        private void notifyWaiting() {
            logger.debug("[confirmMessage]notifyWaiting");
            synchronized (sync) {
                sync.notify();
            }
        }
    }

    /**
     * 用于二次拍卡的数据填充及流程发起
     *
     * @param tipTitle
     * @param tipTimeout
     * @param field55
     */
    protected void secondTapProcess(String tipTitle, int tipTimeout, byte[] field55, int ui_message_id) {
        logger.debug("[secondTapProcess] tipTitle="+tipTitle+" tipTimeout="+tipTimeout+" field55="+InnerUtils.hexString(field55)+" ui_message_id="+ui_message_id);
        doEmvCoreFinish(context, false);
        currentStep = EMVTransStep.PREPARED;
        emvTransInfo.getEpOpt().ucRestart = 0x01;
        if (ui_message_id == 0x20 || ui_message_id == 0x11)
            emvTransInfo.getEpOpt().emSeqStart = EmvConst.EntryPointSeq.START_PPSE_SEL;
        else
            emvTransInfo.getEpOpt().emSeqStart = EmvConst.EntryPointSeq.START_FINAL_APP;

        if (field55 != null) {
            emvTransInfo.getEpOpt().nField55Len = field55.length;
            emvTransInfo.getEpOpt().pusField55 = field55;
        }
        emvTransInfo.getEpOpt().emSeqTo = 0x00;
        context.setPbocTransStep(0x00);
        messageConfirmRunnable = new MessageConfirmRunnable(tipTitle, "TAP CARD AGAIN", false, tipTimeout);
        new Thread(messageConfirmRunnable).start();
        boolean isConfirm = messageConfirmRunnable.startwaiting(USER_OPERATOR_WAITING_MILLS);
        logger.debug("isConfirm:" + isConfirm);
        if (!isConfirm) {
            endTransaction(false, true);
            return;
        }
        logger.debug("[secondTapProcess] isNDKEMVProcess:" + context.isNDKEMVProcess());
        
        if (!context.isNDKEMVProcess()) {
            if(emvExtParams != null){
                logger.debug("[secondTapProcess] isExternalReader:"+emvExtParams.isExternalReader()+" getMediaType:"+emvExtParams.getMediaType()+" isRequiredPrePowerOn:"+emvExtParams.isRequiredPrePowerOn());
            }
            if (null != emvExtParams && emvExtParams.isExternalReader()) {
                boolean isRfCard = lastCardReadContainType(CardType.RFCARD);
                logger.debug("[second tap on external reader] isRfCard:"+isRfCard);
                if(isRfCard){
                    extRFCardModule = (ExtRFCardModule) owner.getExModule(ExModuleType.RFCARD);
                    if (transactionExtParams.getCurrentCardInterfaces() == null
                            || (transactionExtParams.getCurrentCardInterfaces() != 0x02
                            && transactionExtParams.getCurrentCardInterfaces() != 0x04)) {
                        boolean isSucc = false;
                        PortType portType = emvExtParams.getPortType();
                        Baudrate baudrate = emvExtParams.getBaudrate();
                        if (portType == PortType.BLEBASE_USB1 || portType == PortType.BLEBASE_USB2 ||
                                portType ==  PortType.BLEBASE_RS232) {
                            PinpadInitExtParams pinpadInitExtParams = new PinpadInitExtParams(portType, null,null,null);
                            isSucc = extRFCardModule.init(pinpadInitExtParams);
                        } else {
                            if (portType != null && baudrate != null) {
                                PinpadInitExtParams pinpadInitExtParams = new PinpadInitExtParams(portType, baudrate);
                                isSucc = extRFCardModule.init(pinpadInitExtParams);
                            } else {
                                isSucc = extRFCardModule.init(new PinpadInitExtParams(true));
                            }
                        }

                        if (!isSucc) {
                            logger.error("[secondTapProcess] RFCardModule.init fail.");
                            currentEmvState = EMVState.FINISHED;
                            logger.debug(">>>[onError]1");
                            closeRfidLight();
                            listener.onError(this, new DeviceException(-20, "extRFCardModule.init failed"));
                            return;
                        }
                        if (mediaType == 0x00 && null != emvExtParams && emvExtParams.isRequiredPrePowerOn()) {
                            byte[] result = extICCardModule.powerOn();
                            if (result == null) {
                                logger.error("[secondTapProcess] External ICCardModule.powerOn fail.");
                                currentEmvState = EMVState.FINISHED;
                                logger.debug(">>>[onError]2");
                                closeRfidLight();
                                listener.onError(this, new DeviceException(-20, "extICCardModule.powerOn failed"));
                                return;
                            }
                            logger.error("[secondTapProcess] External ICCardModule.powerOn succ.");
                        } else if(null != emvExtParams && emvExtParams.isRequiredPrePowerOn()){
                            RFResult rfResult = extRFCardModule.powerOn(new RFCardType[]{RFCardType.ACARD, RFCardType.BCARD}, 30);//有TYPE B卡，支持emv流程
                            if (rfResult == null) {
                                logger.error("[secondTapProcess] RFCardModule.powerOn fail.");
                                currentEmvState = EMVState.FINISHED;
                                logger.debug(">>>[onError]3");
                                closeRfidLight();
                                listener.onError(this, new DeviceException(-20, "extRFCardModule.powerOn failed"));
                                return;
                            }
                            logger.error("[secondTapProcess] RFCardModule.powerOn succ.");
                        }
                    } else {
                        logger.debug("[secondTapProcess] needn't ExtPowerOn");
                    }
                }
            }else {
                RFCardModule rfModule = (RFCardModule) owner.getStandardModule(ModuleType.RFCARDREADER);
                RFResult rfResultI = rfModule.powerOn(new RFCardType[]{RFCardType.ACARD, RFCardType.BCARD}, 30, null);
                if (logger.isDebugEnabled() && rfResultI != null)
                    logger.debug("[second tap PowerOn] rfcard powerup:" + rfResultI.getRfcardType());
            }
        }
        TransactionExtParams extParams = new TransactionExtParams();
        BigDecimal tradeAmount = null;
        String amount = context.getAmountAuthorisedNumeric();
        tradeAmount = new BigDecimal(amount).divide(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);
        extParams.setEpOpt(emvTransInfo.getEpOpt());
        startEMV2(context.getInnerTransactionType(), tradeAmount, context.getForceOnline(), extParams);
    }

    public boolean isNDKProcess() {
        return NDKProcess;
    }

    public void setNDKProcess(boolean NDKProcess) {
        this.NDKProcess = NDKProcess;
    }

    private void closeRfidLight(){
        if(!NlBuild.VERSION.MODEL.equals("P300")){
            return;
        }
        int ret = NdkApiManager.getNdkApiManager().getSysN().setLedLt1118Status(false);
        logger.debug("[closeRfidLed] ret="+ret);
    }

    public TransactionExtParams getTransactionExtParams() {
        return transactionExtParams;
    }
}