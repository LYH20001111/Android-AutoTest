package com.newland.sdk.me.module.emv;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.WindowManager;

import com.newland.emv.jni.service.EmvJNIService;
import com.newland.emv.jni.type.EmvConst;
import com.newland.emv.jni.type.capk;
import com.newland.emv.jni.type.emv_opt;
import com.newland.emv.jni.type.emvparam;
import com.newland.emv.jni.type.ep_opt;
import com.newland.emv.jni.type.rf_transdata;
import com.newland.intelligent.jni.JniCmdInterface;
import com.newland.sdk.me.module.emv.structure.AbstractEMVPackage;
import com.newland.sdk.me.module.emv.structure.AdditionalTerminalCapability;
import com.newland.sdk.me.module.emv.structure.ECTransFormat;
import com.newland.sdk.me.module.emv.structure.ICS;
import com.newland.sdk.me.module.emv.structure.PbocTransFormat;
import com.newland.sdk.me.module.emv.structure.TerminalCapability;
import com.newland.sdk.me.module.emv.structure.TransferProperty;
import com.newland.sdk.module.cardreader.CardReaderModule;
import com.newland.sdk.module.cardreader.CardType;
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
import com.newland.sdk.module.emv.EMVTransInfo.EntryPointType;
import com.newland.sdk.module.emv.EMVTransLog;
import com.newland.sdk.module.emv.EMVTransLogListener;
import com.newland.sdk.module.emv.EmvExtParams;
import com.newland.sdk.module.emv.EmvPackager;
import com.newland.sdk.module.emv.IDCardType;
import com.newland.sdk.module.emv.OnlineTransactionData;
import com.newland.sdk.module.emv.PINEntity;
import com.newland.sdk.module.emv.TransactionType;
import com.newland.sdk.module.externaliccard.ExtICCardModule;
import com.newland.sdk.module.externalrfcard.ExtRFCardModule;
import com.newland.sdk.module.light.IndicatorLightModule;
import com.newland.sdk.module.light.LightColor;
import com.newland.sdk.module.light.LightState;
import com.newland.sdk.module.rfcard.RFCardModule;
import com.newland.sdk.mtype.DeviceRTException;
import com.newland.sdk.mtype.ExModuleType;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.mtype.common.Const.EmvSelfDefinedReference;
import com.newland.sdk.mtype.common.Const.EmvStandardReference;
import com.newland.sdk.mtype.common.ErrorCode;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtype.util.Dump;
import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.mtypex.AbstractModule;
import com.newland.sdk.mtypex.module.common.emv.SoundPoolImpl;
import com.newland.sdk.utils.TLVMsg;
import com.newland.sdk.utils.TLVPackage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @description: Emv leve2 implement
 * @author: Lindan
 * @create: 2019/08/01
 */
public class MEEMVLevel2 extends AbstractModule implements EMVModule {
    private final String NDKEMVVersion = "libNDK_EMVV103";
    private final String EMVSPVersion = "libemv_spV102";

    TLVPackage finanlSelData = InnerUtils.newTlvPackage();//NDK EMV 最终应用选择设置的数据

    EmvJNIService emvcore = new EmvJNIService();

    private EmvPackager packager = EMVInnerUtils.newEmvPackager();

    private DeviceLogger deviceLogger = DeviceLoggerFactory.getLogger("MEEMVLevel2");

    private EMVCoreOperator defaultEmvOperator;

    private final emvparam defaultEmvCoreParams = new emvparam();

    private String profilePath;

    private Context context;

    private volatile boolean isEpProcess = false;
    private ep_opt epOpt;
    private EmvExtParams emvExtParams = null;
    protected static final int EMV_INVOKE_DEFAULT_TIMEOUT = 48 * 1000;
    private static Set<Integer> defaultTags = new HashSet<Integer>();
    private static Map<Integer, Integer> matchMap = new HashMap<Integer, Integer>();
    //    private volatile boolean isSupportNDKEMV = false;
    private IndicatorLightModule indicatorLightModule;
    private volatile boolean isIndicatorsAndBeep = false;
    private Dialog singleDialog;
    private EMVLevel2TransferController controller;
    static {
        defaultTags.add(0x5F34);
        defaultTags.add(0x5A);
        defaultTags.add(0x57);
        defaultTags.add(0x5F24);
        defaultTags.add(0xDF37);
        defaultTags.add(0x9F5D);
        defaultTags.add(0x9F6C);
        defaultTags.add(0x5F20);
        matchMap.put(0xDF54, 0xDF8133);
        matchMap.put(0xDF55, 0xDF8132);
        matchMap.put(0xDF56, 0xDF8136);
        matchMap.put(0xDF57, 0xDF8137);
        matchMap.put(0xDF58, 0xDF8134);
        matchMap.put(0xDF59, 0xDF8135);
    }

    public MEEMVLevel2(AbstractDevice device, Context context) {
        super(device);
        this.context = context;
        this.indicatorLightModule = (IndicatorLightModule) device.getStandardModule(ModuleType.INDICATOR_LIGHT);

        EmvJNIService emvJNIService = new EmvJNIService();
        deviceLogger.debug("[MEEMVLevel2] EmvJNIService->VERSION:" + emvJNIService.jniemvgetVersion());
        if (Locale.getDefault().getLanguage().equalsIgnoreCase("zh")) {
            deviceLogger.info("zh disable k21 beep");
            //国内设备默认关闭蜂鸣器
            deviceLogger.debug("[MEEMVLevel2] EmvJNIService->jniemvsetextracontrol(1)");
            emvJNIService.jniemvsetextracontrol(0x01);//关闭A10非接下电emv自动蜂鸣,0x00开启,0x01关闭
        } else {
            deviceLogger.debug("[MEEMVLevel2] EmvJNIService->jniemvsetextracontrol(0)");
            emvJNIService.jniemvsetextracontrol(0x00);//非接下电emv自动蜂鸣,0x00开启,0x01关闭
        }
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open("aid_capk.app");
        } catch (Exception e) {
        }
        initEmv(inputStream);

    }

    @Override
    public boolean isStandardModule() {
        return true;
    }

    @Override
    public ModuleType getStandardModuleType() {
        return ModuleType.EMV;
    }

    @Override
    public String getExModuleType() {
        return null;
    }

    @Override
    public boolean init(Context context, EmvExtParams emvExtParams) {
        deviceLogger.debug("[init]");
        this.emvExtParams = emvExtParams;
        if (emvExtParams != null && emvExtParams.getEnableBeep() != null && emvExtParams.getEnableBeep() == true) {
            deviceLogger.debug("Enable k21 Beep");
            deviceLogger.debug("[init] EmvJNIService->jniemvsetextracontrol(0x00)");
            emvcore.jniemvsetextracontrol(0x00);//关闭A10非接下电emv自动蜂鸣,0x00开启,0x01关闭
        } else if (emvExtParams != null && emvExtParams.getEnableBeep() != null && emvExtParams.getEnableBeep() == false) {
            deviceLogger.debug("disenable k21 Beep");
            deviceLogger.debug("[init] EmvJNIService->jniemvsetextracontrol(0x01)");
            emvcore.jniemvsetextracontrol(0x01);//关闭A10非接下电emv自动蜂鸣,0x00开启,0x01关闭
        }
        boolean isOldEMVPath = isOldEMVPath();
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open("aid_capk.app");
        } catch (Exception e) {
        }catch (Error r){
        }
        if (isOldEMVPath) {
            deviceLogger.debug("[init] isOldEMVPath ,inputStream==null? "+(inputStream==null));
            initEmv(inputStream);
        }
        return true;
    }


    /**
     * 初始化emv
     * <p>
     * 确保该方法只执行一次
     */
    private synchronized void initEmv(InputStream inputStream) {
        deviceLogger.debug("[initEmv]inputStream："+inputStream);
        boolean isOldEMVPath = isOldEMVPath();
        initEmvPath(isOldEMVPath); // 初始化emv空间
        initEmvEnv();
        deviceLogger.debug("[initEmv] EmvJNIService->jniemvWriteNLTagData(0x0013, new byte[]{0x01}, 1)");
        emvcore.jniemvWriteNLTagData(0x0013, new byte[]{0x01}, 1);

        int aidCount = emvcore.jniGetAIDCount();
        int capkCount = emvcore.jniGetCAPKCount();
        int emvAidCount = emvcore.jniemvGetAIDCount();
        deviceLogger.debug("[initEmv] EmvJNIService->jniGetAIDCount=" + aidCount);
        deviceLogger.debug("[initEmv] EmvJNIService->jniGetCAPKCount=" + capkCount);
        deviceLogger.debug("[initEmv] EmvJNIService->jniemvGetAIDCount=" + emvAidCount);

        if (aidCount <= 0 && capkCount <= 0 && emvAidCount <= 0) {
            List<AID> aids = null;
            List<CAPK> capks = null;
            if (null != inputStream) {
                SimpleAIDCAPKParser parser = new SimpleAIDCAPKParser();
                parser.parser(inputStream);
                aids = parser.getAids();
                capks = parser.getCapks();
            }
            if (EMVInnerUtils.isSDK3() && !isOldEMVPath) {
                deviceLogger.info("[initEmv] initEmvCONTACT start]");
                initEmvParams(aids, null, CardInterface.CONTACT);//capk不需要重复装载
                deviceLogger.debug("[initEmv] initEmvCONTACT end]");
            }
            deviceLogger.debug("[initEmv] aids:"+aids);
            deviceLogger.debug("[initEmv] capks:"+capks);

            deviceLogger.info("[initEmv] initEmvCONTACTLESS start]");
            initEmvParams(aids, capks, CardInterface.CONTACTLESS);
            deviceLogger.debug("[initEmv] initEmvCONTACTLESS end]");
        }
        int emvAidCount2 = emvcore.jniemvGetAIDCount();
        boolean isSDK3 = EMVInnerUtils.isSDK3();
        deviceLogger.debug("[initEmv] emvAidCount2=" + emvAidCount2 + " isSDK3=" + isSDK3);
        if (emvAidCount2 <= 0 && isSDK3 && !isOldEMVPath) {
            List<AID> aids = null;
            if (null != inputStream) {
                SimpleAIDCAPKParser parser = new SimpleAIDCAPKParser();
                parser.parser(inputStream);
                aids = parser.getAids();
            }
            deviceLogger.info("[initEmv] initEmvCONTACT start]");
            initEmvParams(aids, null, CardInterface.CONTACT);//capk不需要重复装载
            deviceLogger.debug("[initEmv] initEmvCONTACT end]");
        }
        deviceLogger.debug("[initEmv] end");
    }

    public String getProfilePath(){
        return null;
    }

    private void initEmvPath(boolean isOldEMVPath) {
        deviceLogger.debug("[initEmvPath]");
        if (EMVInnerUtils.isSDK3()) {
            this.profilePath = context.getFilesDir() + File.separator + "emv" + File.separator + "doubleAidFiles" + File.separator;
            if (isOldEMVPath) {
                this.profilePath = context.getFilesDir() + File.separator + "emv" + File.separator;
            }
            String customEmvPofilePath = getProfilePath();
            deviceLogger.debug("[initEmvPath]Emv custom profile path:" + customEmvPofilePath);
            if(customEmvPofilePath != null){
                profilePath = customEmvPofilePath;
            }
            File propFile = new File(profilePath);
            if (!propFile.exists()) {
                boolean isSuccess = propFile.mkdirs();
                if (!isSuccess) {
                    throw new DeviceRTException(ErrorCode.CREATE_EMV_FOLDER_ERROR, "create emv file failed");
                }
            }
            deviceLogger.debug("[initEmvPath]Emv profile path:" + profilePath);
        } else {
            this.profilePath = context.getFilesDir() + File.separator + "emv" + File.separator + "aidFile" + File.separator;
            if (isOldEMVPath) {
                this.profilePath = context.getFilesDir() + File.separator + "emv" + File.separator;
            }
            File propFile = new File(profilePath);
            if (!propFile.exists()) {
                boolean isSuccess = propFile.mkdirs();
                if (!isSuccess) {
                    throw new DeviceRTException(ErrorCode.CREATE_EMV_FOLDER_ERROR, "create emv parama file failed");
                }
            }
            deviceLogger.debug("[initEmvPath]Emv profile path:" + profilePath);
        }

    }

    /**
     * 初始化emv参数默认终端
     */
    public void initEmvParams(List<AID> aids, List<CAPK> capks, CardInterface aidStorageMode) {
        deviceLogger.debug("[initEmvParams]  aidStorageMode:" + aidStorageMode);
        int readConfigResult = operAID(aidStorageMode, defaultEmvCoreParams, EMVLevel2Const.AIDOperatorModel.AID_CONFIG_R);
        deviceLogger.info("[initEmvParams]readConfigResult:" + readConfigResult);
        if (readConfigResult != 0) {
            /* transaction reference currency convert */
            Arrays.fill(defaultEmvCoreParams._trans_ref_conv, (byte) 0x00);
            /* script length limit */
            defaultEmvCoreParams._script_dev_limit = 0x00;
            ICS ics = new ICS();
            /** <ICS  DF7A*/
            /** <AS : Application Selection */
            ics.set(ICS.AS_Support_PSE);
            ics.set(ICS.AS_Support_CardHolder_Confirm);
            ics.set(ICS.AS_Support_Preferred_Order);
            ics.set(ICS.AS_Support_Partial_AID);
            ics.unset(ICS.AS_Support_Multi_Language);
            ics.set(ICS.AS_Support_Common_Charset);
            /** <DA : Data Authentication */
            ics.set(ICS.DA_Support_IPKC_Revoc_Check);
            ics.set(ICS.DA_Support_Default_DDOL);
            // ics.set(ICS.DA_Support_CAPKLoad_Fail_Action);
            // ics.set(ICS.DA_Support_CAPK_Checksum);
            /** <CV : Cardholder Verification */
            ics.set(ICS.CV_Support_Bypass_PIN);
            ics.set(ICS.CV_Support_PIN_Try_Counter);
            ics.set(ICS.CV_Support_Fail_CVM);
            ics.set(ICS.CV_Support_Amounts_before_CVM);
            ics.unset(ICS.CV_Support_Bypass_ALL_PIN);
            /** <TRM : Terminal Risk Management */
            ics.set(ICS.TRM_Support_FloorLimit);
            ics.set(ICS.TRM_Support_RandomSelect);
            ics.set(ICS.TRM_Support_VelocityCheck);
            ics.set(ICS.TRM_Support_TransLog);
            ics.set(ICS.TRM_Support_ExceptionFile);
            ics.unset(ICS.TRM_Support_AIPBased);
            ics.unset(ICS.TRM_Use_EMV_LogPolicy);
            /** <TAA : Terminal Action Analysis */
            ics.set(ICS.TAA_Support_TAC);
            ics.unset(ICS.TAA_Support_DAC_before_1GenAC);
            ics.set(ICS.TAA_Support_DAC_after_1GenAC);
            ics.unset(ICS.TAA_Support_Skip_DAC_OnlineFail);
            ics.set(ICS.TAA_Support_DAC_OnlineFail);
            ics.set(ICS.TAA_Support_CDAFail_Detected);
            ics.set(ICS.TAA_Support_CDA_Always_in_ARQC);
            ics.set(ICS.TAA_Support_CDA_Alawys_in_2TC);
            /** <CP : Completion Process */
            ics.set(ICS.CP_Support_Force_Online);
            ics.unset(ICS.CP_Support_Force_Accept);
            ics.unset(ICS.CP_Support_Advices);
            ics.unset(ICS.CP_Support_Issuer_VoiceRef);
            ics.set(ICS.CP_Support_Batch_Data_Capture);
            ics.set(ICS.CP_Support_Online_Data_capture);
            ics.set(ICS.CP_Support_Default_TDOL);
            /** <MISC : Miscellaneous */
            ics.unset(ICS.MISC_Support_Account_Select);
            ics.set(ICS.MISC_Support_ISDL_Greater_than_128);
            ics.set(ICS.MISC_Support_Internal_Date_Mana);
            ics.unset(ICS.DA_Support_CAPKLoad_Fail_Action);
            ics.unset(ICS.DA_Support_CAPK_Checksum);
            // paypass
            ics.set(ICS.MISC_PP_Support_Default_UDOL);
            ics.unset(ICS.MISC_MISC_PP_Support_MagAppVer);
            // 设置ics
            deviceLogger.debug("[initEmvParams]ics:" + Dump.getHexDump(ics.toByteArray()));
            System.arraycopy(ics.toByteArray(), 0, defaultEmvCoreParams._ics, 0, ics.getLength());

            /* Test type indicator */
            defaultEmvCoreParams._status = EmvConst.PBOC2_ENB;
            // defaultEmvCoreParams._status = (byte) EmvConst.MASTERCARD_ENB;
            /** 支持电子现金 */
            defaultEmvCoreParams._ec_indicator = 0x01;

            /** <TT : Terminal Type */
            defaultEmvCoreParams._type = 0x22;

            TerminalCapability tc = new TerminalCapability();

            /** < TC : Terminal Capabilities 定义终端性能  9F33*/
            tc.set(TerminalCapability.TC_Manual_Key_Entry);
            tc.set(TerminalCapability.TC_Magnetic_Stripe);
            tc.set(TerminalCapability.TC_IC_With_Contacts);
            tc.set(TerminalCapability.TC_Plaintext_PIN);
            tc.set(TerminalCapability.TC_Enciphered_PIN_Online);
            tc.set(TerminalCapability.TC_Signature_Paper);
            tc.set(TerminalCapability.TC_Enciphered_PIN_Offline);
            tc.set(TerminalCapability.TC_No_CVM_Required);
            tc.unset(TerminalCapability.TC_Cardholder_Cert);

            tc.set(TerminalCapability.TC_SDA);
            tc.set(TerminalCapability.TC_DDA);
            tc.unset(TerminalCapability.TC_Card_Capture);
            tc.set(TerminalCapability.TC_CDA);

            // 设置Terminal Capabilities
            deviceLogger.debug("[initEmvParams]tc:" + Dump.getHexDump(tc.toByteArray()));//9F33默认:E0F8C8
            System.arraycopy(tc.toByteArray(), 0, defaultEmvCoreParams._cap, 0, tc.getLength());

            AdditionalTerminalCapability atc = new AdditionalTerminalCapability();

            /** < ATC : Additional Terminal Capabilities 终端附加性能  0x9f40*/
            atc.set(AdditionalTerminalCapability.ATC_Cash);
            atc.set(AdditionalTerminalCapability.ATC_Goods);
            atc.set(AdditionalTerminalCapability.ATC_Services);
            atc.set(AdditionalTerminalCapability.ATC_Cashback);
            atc.set(AdditionalTerminalCapability.ATC_Inquiry);
            atc.set(AdditionalTerminalCapability.ATC_Transfer);
            atc.set(AdditionalTerminalCapability.ATC_Payment);
            atc.set(AdditionalTerminalCapability.ATC_Administrative);
            atc.set(AdditionalTerminalCapability.ATC_Cash_Deposit); // lld
            // 2013-4-17
            atc.set(AdditionalTerminalCapability.ATC_Numeric_Keys);
            atc.set(AdditionalTerminalCapability.ATC_Alphabetic_Special_Keys);
            atc.set(AdditionalTerminalCapability.ATC_Command_Keys);
            atc.set(AdditionalTerminalCapability.ATC_Function_Keys);
            atc.set(AdditionalTerminalCapability.ATC_Print_Attendant);
            atc.unset(AdditionalTerminalCapability.ATC_Print_Cardholder);
            atc.set(AdditionalTerminalCapability.ATC_Display_Attendant);
            atc.set(AdditionalTerminalCapability.ATC_Display_Cardholder); // lld
            atc.unset(AdditionalTerminalCapability.ATC_Code_Table_10);
            atc.unset(AdditionalTerminalCapability.ATC_Code_Table_9);
            atc.unset(AdditionalTerminalCapability.ATC_Code_Table_8);
            atc.unset(AdditionalTerminalCapability.ATC_Code_Table_7);
            atc.unset(AdditionalTerminalCapability.ATC_Code_Table_6);
            atc.unset(AdditionalTerminalCapability.ATC_Code_Table_5);
            atc.unset(AdditionalTerminalCapability.ATC_Code_Table_4);
            atc.unset(AdditionalTerminalCapability.ATC_Code_Table_3);
            atc.unset(AdditionalTerminalCapability.ATC_Code_Table_2);
            atc.set(AdditionalTerminalCapability.ATC_Code_Table_1);

            // Additional Terminal Capabilities 0x9f40
            deviceLogger.debug("[initEmvParams]atc:" + Dump.getHexDump(atc.toByteArray()));
            System.arraycopy(atc.toByteArray(), 0, defaultEmvCoreParams._add_cap, 0, atc.getLength());

            /* 9F39(Terminal), n2, 1 bytes */
            defaultEmvCoreParams._pos_entry = (byte) 0x80;
            /* 9F01(Terminal), n6-11, 6 bytes */
            System.arraycopy(new byte[]{0x12, 0x34, 0x56, 0x78, (byte) 0x90, 0x00}, 0, defaultEmvCoreParams._acq_id, 0, 6);
            /* 9F15(Terminal), n4, 2 bytes */
            System.arraycopy(new byte[]{0x12, 0x34}, 0, defaultEmvCoreParams._mer_category_code, 0, 2);
            /* 9F16(Terminal), ans15, 15 bytes */
            System.arraycopy("123456789012345".getBytes(), 0, defaultEmvCoreParams._merchant_id, 0, 15);
            /* 5F2A(Terminal), n3, 2 bytes */
            System.arraycopy(new byte[]{0x01, 0x56}, 0, defaultEmvCoreParams._trans_curr_code, 0, 2);
            /* 5F36(Terminal), n1, 1 bytes */
            defaultEmvCoreParams._trans_curr_exp = 0x00;
            /* 9F3C(Terminal), n3, 2 bytes */
            System.arraycopy(new byte[]{0x01, 0x56}, 0, defaultEmvCoreParams._trans_ref_curr_code, 0, 2);
            /* 9F3D(Terminal), n1, 1 bytes */
            defaultEmvCoreParams._trans_ref_curr_exp = 0x00;
            /* 9F1A(Terminal), n3, 2 bytes */
            System.arraycopy(new byte[]{0x01, 0x56}, 0, defaultEmvCoreParams._term_country_code, 0, 2);
            /* 9F1E(Terminal), an8, 8 bytes */
            System.arraycopy("00000001".getBytes(), 0, defaultEmvCoreParams._ifd_serial_num, 0, 8);
            /* 9F1C(Terminal), an8, 8 bytes */
            System.arraycopy("12345678".getBytes(), 0, defaultEmvCoreParams._terminal_id, 0, 8);
            /* fallback pos entry */
            defaultEmvCoreParams._fallback_posentry = 0x00;
            /*
             * limist exist?(判断以下限额是否存在的标识) bit 1 =1 EC limint exsit bit 2 =1
             * contactless limit exsit bit 3 =1 contactless offline limit exsit
             * bit 4 =1 cvm limit exsit
             */
            defaultEmvCoreParams._limit_exist = 0; /*
             * 1<<0 | 1<< 1 | 1<< 2 |
             * 1<<3;
             */

            /* 9F7B 电子现金终端限额n12 6bytes */
            System.arraycopy(new byte[]{0x00, 0x00, 0x00, 0x05, 0x00, 0x00}, 0, defaultEmvCoreParams._ec_limit, 0, 6);

            /* 非接触终端交易限额 n12 6bytes */
            System.arraycopy(new byte[]{0x00, 0x00, 0x00, 0x05, 0x00, 0x00}, 0, defaultEmvCoreParams._cl_limit, 0, 6);

            /* 非接触终端脱机最低限额n12 6bytes */
            System.arraycopy(new byte[]{0x00, 0x00, 0x00, 0x05, 0x00, 0x00}, 0, defaultEmvCoreParams._cl_offline_limit, 0, 6);

            /* 终端执行CVM限额 n12 6bytes */
            System.arraycopy(new byte[]{0x00, 0x00, 0x00, 0x05, 0x00, 0x00}, 0, defaultEmvCoreParams._cvm_limit, 0, 6);

            /** <默认终端交易属性  9F66  36 00 00 00*/
            Arrays.fill(defaultEmvCoreParams._trans_prop, (byte) 0x00);
            TransferProperty tp = new TransferProperty();

            tp.unset(TransferProperty.EMV_PROP_MSD);
            tp.unset(TransferProperty.EMV_PROP_PBOCCLSS);
            tp.set(TransferProperty.EMV_PROP_QPBOC);// update 20170713
            tp.set(TransferProperty.EMV_PROP_PBOC);// update 20170713
            tp.unset(TransferProperty.EMV_PROP_OFFLINE_ONLY);
            tp.set(TransferProperty.EMV_PROP_ONLINEPIN);// update 20170713
            tp.set(TransferProperty.EMV_PROP_SIGNATURE);// update 20170713

            // Additional Terminal Capabilities
            deviceLogger.debug("[initEmvParams]tp:" + Dump.getHexDump(tp.toByteArray()));
            System.arraycopy(tp.toByteArray(), 0, defaultEmvCoreParams._trans_prop, 0, tp.getLength());
            /* 非接触状态检查 */
            defaultEmvCoreParams._status_check = 0x00;
            defaultEmvCoreParams.StatusCheckSupport = 0x00;
            /** <默认为0 */
            defaultEmvCoreParams._aid_len = 16;

            defaultEmvCoreParams.ZeroAmountAllow = 0x01;// 0x01-允许0金额,0金额会请求联机；0x00-不允许0金额； 0x03-允许0金额，并且0金额不请求联机; 0x02-dpas在零金额存在但是不支持的情况下，如果有联机能力请求联机
            //ZeroAmountAllow QPBOC 其它值无效，只能0和1
            // ------------paypass-------default udol
            System.arraycopy(new byte[]{(byte) 0x9F, 0x6A, 0x04}, 0, defaultEmvCoreParams.DefaultUdol, 0, 3);
            defaultEmvCoreParams.DefaultUdolLen = 3;
            defaultEmvCoreParams.CapNoCvm = 0x08;
            defaultEmvCoreParams.VisaTtqPresent = 0x01;
            System.arraycopy(new byte[]{(byte) 0x9F, 0x37, 0x04}, 0, defaultEmvCoreParams._default_ddol, 0, 3);
            defaultEmvCoreParams._default_ddol_len = 3;
            defaultEmvCoreParams.MagStripeCvm = 0x10;
            System.arraycopy(new byte[]{(byte) 0x9F, 0x08, 0x02}, 0, defaultEmvCoreParams._default_tdol, 0, 3);
            defaultEmvCoreParams._default_tdol_len = 3;
            System.arraycopy(new byte[]{0x00, 0x01}, 0, defaultEmvCoreParams.MagAppVer, 0, 2);
            defaultEmvCoreParams.MagStripeIndicator = 0x01;
            defaultEmvCoreParams.KernelConfig = 0x20;
            deviceLogger.debug("[initEmvParams]AID_CONFIG_W[initEmvParams]");
            int nRet = operAID(aidStorageMode, defaultEmvCoreParams, EMVLevel2Const.AIDOperatorModel.AID_CONFIG_W);
            if (nRet != 0) {
                deviceLogger.error("[initEmvParams]failed to init" + aidStorageMode.toString() + " trmnl params!" + nRet);
            }
        }
        initAIDCAPK(aids, capks, aidStorageMode);
    }

    private void initAIDCAPK(List<AID> aids, List<CAPK> capks, CardInterface aidStorageMode) {
        deviceLogger.debug("[initAIDCAPK]");
        if (null != aids) {
            for (AID aid : aids) {
                addAID(aid, aidStorageMode);
            }
        }
        if (null != capks) {
            for (CAPK capk : capks) {
                addCAPublicKey(capk.getRid(), capk);
            }
        }
    }

    public boolean addCAPublicKey(byte[] rid, CAPK capkpayload) {
        TLVPackage tlvpackage = EMVInnerUtils.newTlvPackage();
        byte[] bs = packager.pack(capkpayload);

        tlvpackage.unpack(bs);
        capk capk = new capk();

        /**
         * typedef struct { publickey _key; < RSA公钥结构体 unsigned char
         * _hashvalue[20]; < 公钥HASH校验值 unsigned char _expired_date[4]; < 公钥过期时间
         * unsigned char _rid[5]; < 注册应用提供商标识RID unsigned char _index; < 公钥索引
         * unsigned char _pk_algorithm; < 公钥算法标识 unsigned char _hash_algorithm;
         * < HASH算法标识 unsigned char _disable; < =1公钥失效 unsigned char _resv[3]; <
         * 保留位 }capk;
         */
        if (rid != null) {
            System.arraycopy(rid, 0, capk._rid, 0, Math.min(rid.length, capk._rid.length));
        } else if ((rid = tlvpackage.getValue(EmvStandardReference.AID_TERMINAL)) != null) {
            System.arraycopy(rid, 0, capk._rid, 0, Math.min(rid.length, capk._rid.length));
        } else {
            throw new EMVTransferException("[addCAPublicKey]rid should not be null!");
        }

        byte[] value = tlvpackage.getValue(EmvSelfDefinedReference.CAPK_MODULUS);
        if (value != null) {
            System.arraycopy(value, 0, capk.pk_modulus, 0, Math.min(value.length, capk.pk_modulus.length));
            capk.pk_mod_len = (byte) (value.length);
        }
        value = tlvpackage.getValue(EmvSelfDefinedReference.CAPK_EXPONENT);
        if (value != null) {
            byte[] exponnent = EMVInnerUtils.padLeft(value, 3, (byte) 0x00);
            System.arraycopy(exponnent, 0, capk.pk_exponent, 0, 3);
        }
        value = tlvpackage.getValue(EmvSelfDefinedReference.CAPK_SHA1CHECKSUM);
        if (value != null) {
            System.arraycopy(value, 0, capk._hashvalue, 0, Math.min(value.length, capk._hashvalue.length));
        }
        value = tlvpackage.getValue(EmvSelfDefinedReference.CA_PK_EXPIRATION_DATE);
        if (value != null && value.length >= 3) {
            if (value.length == 8) {
                String expDate = new String(value);
                value = EMVInnerUtils.hex2byte(expDate);
            }
            byte[] expiredDate = EMVInnerUtils.padLeft(value, 4, (byte) 0x20);
            System.arraycopy(expiredDate, 0, capk._expired_date, 0, Math.min(value.length, capk._expired_date.length));
        }

        value = tlvpackage.getValue(EmvStandardReference.CA_PUBLIC_KEY_INDEX_TERMINAL);
        if (value != null && value.length > 0)
            capk._index = value[0];

        value = tlvpackage.getValue(EmvSelfDefinedReference.CA_PK_ALGORITHM_INDICATOR);
        if (value != null && value.length > 0)
            capk._pk_algorithm = value[0];

        value = tlvpackage.getValue(EmvSelfDefinedReference.CA_PK_HASH_ALGORITHM_INDICATOR);
        if (value != null && value.length > 0)
            capk._hash_algorithm = value[0];


        EMVInnerUtils.toString_capk(deviceLogger, capk);
        int nRet = emvcore.jniSDKEPOperCAPK(capk, EMVLevel2Const.CAPKOperatorModel.CAPK_UPT);
        deviceLogger.debug("[addCAPublicKey] EmvJNIService->jniSDKEPOperCAPK CAPK_UPT ret=" + nRet);
        if (nRet != 0) {
            if (deviceLogger.isDebugEnabled())
                deviceLogger.error("[addCAPublicKey]failed to update capk!" + Dump.getHexDump(rid) + "," + nRet);
        }
        // else {
        // initEmvEnv(); 公钥无需同步
        // }
        return nRet == 0;
    }

    public boolean addAID(AID aidConfig, CardInterface aidStorageMode) {
        deviceLogger.debug("[addAID]addAID aidConfig aidStorageMode:" + aidStorageMode);
        packager = EMVInnerUtils.newEmvPackager();
        TLVPackage tp = EMVInnerUtils.newTlvPackage();
        byte[] aidParam = packager.pack(aidConfig);
        tp.unpack(aidParam);
        deviceLogger.debug("[addAID]:" + (null == aidParam ? null : EMVInnerUtils.hexString(aidParam)));
        byte[] aid = tp.getValue(EmvStandardReference.AID_TERMINAL);
        if (aid == null) {
            throw new EMVTransferException("[addAID]aid should not be null!");
        }

        emvparam params = new emvparam();
        int rslt = operAID(aidStorageMode, params, EMVLevel2Const.AIDOperatorModel.AID_CONFIG_R);
        if (rslt != 0)
            deviceLogger.debug("[addAID]failed to read trmnl config!" + rslt);
        deviceLogger.debug("[addAID] AID_CONFIG_R params:");
        EMVInnerUtils.toString_emvparam(deviceLogger, params);

        // // 这里因为需要连续2次native拷贝数组，而native拷贝的方式是直接数据拷贝，所以长度必须满足。不能直接赋值
        System.arraycopy(aid, 0, params._aid, 0, aid.length);
        params._aid_len = (byte) aid.length;

        rslt = operAID(aidStorageMode, params, EMVLevel2Const.AIDOperatorModel.AID_GET);
        if (rslt != 0)
            deviceLogger.debug("failed to read aid params![" + Dump.getHexDump(aid) + "]" + rslt);

        deviceLogger.debug("[addAID]rslt:" + rslt + ",aid:" + EMVInnerUtils.hexString(params._aid));
        deviceLogger.debug("[addAID] AID_GET params:");
        EMVInnerUtils.toString_emvparam(deviceLogger, params);

        // kernel id
        byte[] ppTlv = null;
        int pptlvLength = 0;
        switch (getKernelID(aid)) {
            case EmvConst.KERNEL_ID_PAYWAVE:
                params.KernelId[0] = EmvConst.KERNEL_ID_PAYWAVE;// visa
                params._status = EmvConst.VISA_ENB;
                if (aidConfig.getDrlData() != null) {
                    System.arraycopy(aidConfig.getDrlData(), 0, params.DrlData, 0, aidConfig.getDrlData().length);
                }
                // params.DrlData = aidConfig.getDrlData();   // amex drldata标签：l2：df2b；l3：df53   paywave使用 drldata字段传入drl,美运使用DefaultUdol传入drl
                break;
            case EmvConst.KERNEL_ID_PAYPASS:
                params.KernelId[0] = EmvConst.KERNEL_ID_PAYPASS; // JCB，设置JCB特有参数
                params._status = EmvConst.MASTERCARD_ENB;
                ppTlv = new byte[]{(byte) 0xDF, (byte) 0x81, 0x33, 0x02, 0x00, 0x32, (byte) 0xDF, (byte) 0x81, 0x32, 0x02, 0x00, 0x14, (byte) 0xDF, (byte) 0x81, 0x36, 0x02, 0x01, 0x2c, (byte) 0xDF, (byte) 0x81, 0x37, 0x01, 0x32, (byte) 0xDF, (byte) 0x81, 0x34, 0x02, 0x00, 0x0B, (byte) 0xDF, (byte) 0x81, 0x35, 0x02, 0x00, 0x0D};
                pptlvLength = 0x23;
                break;
            case EmvConst.KERNEL_ID_JCB:
                params.KernelId[0] = EmvConst.KERNEL_ID_JCB; // JCB，设置JCB特有参数
                params._status = EmvConst.JCB_ENB;
                byte[] CombinationOP = tp.getValue(EmvSelfDefinedReference.COMBINATIONOPT);
                if (null == CombinationOP)
                    CombinationOP = new byte[]{0x7B, 0x00};
                System.arraycopy(CombinationOP, 0, params.CombinationOP, 0, Math.min(CombinationOP.length, params.CombinationOP.length));
                byte[] tip = tp.getValue(EmvStandardReference.TIP);
                if (null == tip)
                    tip = new byte[]{0x70, (byte) 0x80, 0x00};
                System.arraycopy(tip, 0, params.TIP, 0, Math.min(tip.length, params.TIP.length));
                break;
            case EmvConst.KERNEL_ID_EXPRESSPAY:
                deviceLogger.debug("[addAID] KERNEL_ID_EXPRESSPAY aidConfig.getDrlData()="+InnerUtils.hexString(aidConfig.getDrlData()));
                params._status = EmvConst.AMEX_ENB;
                if (null == aidConfig.getExpTerminalCap())
                    params.EXTerminalCap = (byte) 0xC0;
                params.KernelId[0] = EmvConst.KERNEL_ID_EXPRESSPAY; // expresspay
                if (null == aidConfig.getTerminalCapabilities()) {
                    params._cap = new byte[]{(byte) 0xE0, (byte) 0xF8, (byte) 0xC8};
                }
                if (null != aidConfig.getDrlDataExp()) {
                    TLVPackage tlvpackage = InnerUtils.newTlvPackage();
                    tlvpackage.append(EmvSelfDefinedReference.DRLDATA_EXP, aidConfig.getDrlDataExp());
                    byte[] drlData = tlvpackage.pack();
                    deviceLogger.debug("[addAID] DRLDATA_EXP(DF53) drlData="+InnerUtils.hexString(drlData));
                    System.arraycopy(drlData, 0, params.DefaultUdol, 0, drlData.length);
                    params.DefaultUdolLen = (byte) drlData.length;
                }
                break;
            case EmvConst.KERNEL_ID_UNIONPAY:
                params.KernelId[0] = EmvConst.KERNEL_ID_UNIONPAY; // 银联
                params._status = EmvConst.PBOC2_ENB;
                break;
            case EmvConst.KERNEL_ID_DISCOVER:
                params.KernelId[0] = EmvConst.KERNEL_ID_DISCOVER; // discover
                params._status = (byte) EmvConst.DISCOVER_ENB;
                break;
            case EmvConst.KERNEL_ID_MCCS:
                params.KernelId[0] = EmvConst.KERNEL_ID_MCCS; // mccs A000000615
                params._status = (byte) EmvConst.MCCS_ENB;
                pptlvLength = 8;
                ppTlv = new byte[]{(byte) 0xDF, 0x62, 0x05, 0x36, 0x00, 0x60, 0x43, (byte) 0xF9};
                break;
            case EmvConst.KERNEL_ID_INTERAC:
                params.KernelId[0] = EmvConst.KERNEL_ID_INTERAC; // INTERAC
                params._status = (byte) EmvConst.INTERAC_ENB;
                break;
            case EmvConst.KERNEL_ID_RUPAY:
                params.KernelId[0] = EmvConst.KERNEL_ID_RUPAY; // rupay
                params._status = (byte) EmvConst.RUPAY_ENB;
                break;
            case EmvConst.KERNEL_ID_MADA:
                params.KernelId[0] = EmvConst.KERNEL_ID_MADA;
                params._status = (byte) EmvConst.MCCS_ENB;
                pptlvLength = 8;
                ppTlv = new byte[]{(byte) 0xDF, 0x62, 0x05, 0x36, 0x00, 0x60, 0x43, (byte) 0xF9};
                break;
            case EMVLevel2Const.KERNEL_ID_IRAN_KAHROBA:
                System.arraycopy(new byte[]{(byte) 0x8A,0x06, (byte) 0x82,0x61,0x00,0x00,0x00,0x00},0,
                        params.KernelId,0,params.KernelId.length);
                break;
            case EMVLevel2Const.KERNEL_ID_GIRO://Girocard
                params.KernelId[0] = EMVLevel2Const.KERNEL_ID_GIRO;
                break;
            default:
                break;
        }
        //---pptlv---
        if (null != aidConfig.getKernelExtendedTLV()) {
            ppTlv = aidConfig.getKernelExtendedTLV();
            pptlvLength = ppTlv.length;
        }

        if (null != ppTlv) {
            params.PPTlvLen = (byte) pptlvLength;
            System.arraycopy(ppTlv, 0, params.PPTlv, 0, pptlvLength);
        }
        deviceLogger.debug("[addAID]params.KernelId[0]:" + params.KernelId[0] + ",aid:" + EMVInnerUtils.hexString(params._aid));

        byte[] value = tp.getValue(EmvSelfDefinedReference.APP_SELECT_INDICATOR);
        if (value != null && value.length > 0) {
            params._app_sel_indicator = value[0];
            //由于国内下发的国内外aid df01都为00部分匹配。因此强制转换
            if (!isOverseas()) {
                if (params._app_sel_indicator == 1) {
                    params._app_sel_indicator = 0;
                } else {
                    params._app_sel_indicator = 1;
                }
            }
            // params._ics = ics.toByteArray();
        }
        value = tp.getValue(EmvStandardReference.APP_VERSION_NUMBER_CARD);
        if (value == null) {
            value = tp.getValue(EmvStandardReference.APP_VERSION_NUMBER_TERMINAL);
        }
        if (value != null) {
            System.arraycopy(value, 0, params._app_ver, 0, Math.min(value.length, params._app_ver.length));
        }
        value = tp.getValue(EmvSelfDefinedReference.TAC_DEFAULT);
        if (value != null) {
            System.arraycopy(value, 0, params._tac_default, 0, Math.min(value.length, params._tac_default.length));
        }
        value = tp.getValue(EmvSelfDefinedReference.TAC_ONLINE);
        if (value != null) {
            System.arraycopy(value, 0, params._tac_online, 0, Math.min(value.length, params._tac_online.length));
        }
        value = tp.getValue(EmvSelfDefinedReference.TAC_DENIAL);
        if (value != null) {
            System.arraycopy(value, 0, params._tac_denial, 0, Math.min(value.length, params._tac_denial.length));
        }
        value = tp.getValue(EmvStandardReference.TERMINAL_FLOOR_LIMIT);
        if (value != null) {
            System.arraycopy(value, 0, params._floorlimit, 0, Math.min(value.length, params._floorlimit.length));
        }
        value = tp.getValue(EmvSelfDefinedReference.THRESHOLD_VALUE_FOR_BIASED_RANDOM_SELECTION);
        if (value != null) {
            System.arraycopy(value, 0, params._threshold_value, 0, Math.min(value.length, params._threshold_value.length));
        }

        value = tp.getValue(EmvSelfDefinedReference.MAX_TARGET_PERCENTAGE_FOR_BIASED_RANDOM_SELECTION);
        if (value != null && value.length > 0) {
            String str = EMVInnerUtils.bcd2str(value, 0, value.length * 2, true);// 期待长度为目标解析长度的2倍(定长)
            int randomValue = Integer.valueOf(EMVInnerUtils.unPadRight(str, 'F')).intValue();// 去掉右边的F
            params._max_target_percent = (byte) (randomValue);
        }
        value = tp.getValue(EmvSelfDefinedReference.TARGET_PERCENTAGE_FOR_RANDOM_SELECTION);
        if (value != null && value.length > 0) {
            String str = EMVInnerUtils.bcd2str(value, 0, value.length * 2, true);
            int randomValue = Integer.valueOf(EMVInnerUtils.unPadRight(str, 'F')).intValue();
            params._target_percent = (byte) (randomValue);
        }
//        params._limit_exist = 0x00;
        value = tp.getValue(EmvSelfDefinedReference.EC_TRANS_LIMIT);
        if (value != null) {
            params._limit_exist |= 0x01;
            System.arraycopy(value, 0, params._ec_limit, 0, Math.min(value.length, params._ec_limit.length));
        }
        value = tp.getValue(EmvSelfDefinedReference.NCICC_TRANS_LIMIT);
        if (value != null) {
            params._limit_exist |= 0x02;
            System.arraycopy(value, 0, params._cl_limit, 0, Math.min(value.length, params._cl_limit.length));
        }
        value = tp.getValue(EmvSelfDefinedReference.NCICC_OFFLINE_FLOOR_LIMIT);
        if (value != null) {
            params._limit_exist |= 0x04;
            System.arraycopy(value, 0, params._cl_offline_limit, 0, Math.min(value.length, params._cl_offline_limit.length));
        }
        value = tp.getValue(EmvSelfDefinedReference.NCICC_CVM_LIMIT);
        if (value != null) {
            params._limit_exist |= 0x08;
            System.arraycopy(value, 0, params._cvm_limit, 0, Math.min(value.length, params._cvm_limit.length));
        }
//        params._status = peekTypeByAid(params.KernelId[0], params._cap);
        // params._trans_prop = new byte[]{0x36,0x00,0x00,(byte)0x80};
        value = tp.getValue(EmvStandardReference.TERMINAL_TRANSACTION_QUALIFIERS);
        if (value != null && value.length > 0) {
            deviceLogger.debug("[addAID]value params._trans_prop=" + EMVInnerUtils.hexString(value));
            System.arraycopy(value, 0, params._trans_prop, 0, Math.min(value.length, params._trans_prop.length));
        } else {

            params._trans_prop = new byte[]{0x36, 0x00, 0x00, (byte) 0x80};
            if (aid.length >= 5) {
                byte[] adaptAid = new byte[5];
                System.arraycopy(aid, 0, adaptAid, 0, 5);
                if (Arrays.equals(adaptAid, new byte[]{(byte) 0xA0, 0x00, 0x00, 0x00, 0x25})) {
                    params._trans_prop = new byte[]{(byte) 0xD8, (byte) 0x80, 0x00, (byte) 0x00}; // expresspay
                }
            }
            deviceLogger.debug("[addAID] params._trans_prop=" + params._trans_prop);
        }
        // -------------add 2015-04-01-------------------
        value = tp.getValue(EmvStandardReference.MERCHANT_CATEGORY_CODE);
        if (value != null) {
            System.arraycopy(value, 0, params._mer_category_code, 0, Math.min(value.length, params._mer_category_code.length));
        }
        deviceLogger.debug("[addAID]5f2a " + (null == value ? "" : EMVInnerUtils.hexString(value)));
        deviceLogger.debug("[addAID]params._trans_curr_code" + (null == params._trans_curr_code ? "" : EMVInnerUtils.hexString(params._trans_curr_code)));
        value = tp.getValue(EmvStandardReference.TRANSACTION_CURRENCY_CODE);
        if (value != null) {
            System.arraycopy(value, 0, params._trans_curr_code, 0, Math.min(value.length, params._trans_curr_code.length));
        }
        value = tp.getValue(EmvStandardReference.TERMINAL_COUNTRY_CODE);
        if (value != null) {
            System.arraycopy(value, 0, params._term_country_code, 0, Math.min(value.length, params._term_country_code.length));
        }
        value = tp.getValue(EmvStandardReference.TRANSACTION_CURRENCY_EXP);
        if (value != null && value.length > 0) {
            params._trans_curr_exp = value[0];
        }
        value = tp.getValue(EmvStandardReference.TERMINAL_TYPE);
        if (value != null && value.length > 0) {
            params._type = value[0];
        }
        value = tp.getValue(EmvStandardReference.TERMINAL_CAPABILITIES);
        if (value != null) {
            System.arraycopy(value, 0, params._cap, 0, Math.min(value.length, params._cap.length));
        }
        value = tp.getValue(EmvStandardReference.ADDITIONAL_TERMINAL_CAPABILITIES);
        if (value != null) {

            System.arraycopy(value, 0, params._add_cap, 0, Math.min(value.length, params._add_cap.length));
        }
        value = tp.getValue(EmvSelfDefinedReference.STATUSCHECK);
        if (value != null) {
            params._status_check = value[0]; //df29  rfstart
            params.StatusCheckSupport = value[0]; //df39  ep
        }
        value = tp.getValue(EmvSelfDefinedReference.DEFAULT_DDOL);
        if (value != null) {
            params._default_ddol_len = (byte) value.length;
            System.arraycopy(value, 0, params._default_ddol, 0, value.length);
            params._default_ddol_len = (byte) Math.min(value.length, params._default_ddol.length);
        } else if (tp.hasTag(EmvSelfDefinedReference.DEFAULT_DDOL)) {
            params._default_ddol_len = 0;
            System.arraycopy(new byte[]{}, 0, params._default_ddol, 0, 0);
        }
        value = tp.getValue(EmvSelfDefinedReference.DEFAULT_TDOL);
        if (value != null) {
            params._default_tdol_len = (byte) value.length;
            System.arraycopy(value, 0, params._default_tdol, 0, value.length);
        } else if (tp.hasTag(EmvSelfDefinedReference.DEFAULT_TDOL)) {
            params._default_tdol_len = 0;
            System.arraycopy(new byte[]{}, 0, params._default_tdol, 0, 0);
        }
        value = tp.getValue(EmvStandardReference.ACQUIRER_IDENTIFIER);
        if (value != null) {
            System.arraycopy(value, 0, params._acq_id, 0, Math.min(value.length, params._acq_id.length));
        }
        value = tp.getValue(EmvStandardReference.TERMINAL_RISK_MANAGEMENT_DATA);
        if (value != null) {
            System.arraycopy(value, 0, params._riskmana_data, 0, Math.min(value.length, params._riskmana_data.length));
            params._riskmana_data_len = (byte) Math.min(value.length, params._riskmana_data.length);
        }

        value = tp.getValue(EmvSelfDefinedReference.KERNEL_CONFIGURATION);
        if (value != null) {
            params.KernelConfig = value[0];
        }
        value = tp.getValue(EmvSelfDefinedReference.LIMIT_EXIST);
        if (value != null) {
            params._limit_exist = value[0];
        }

        value = tp.getValue(EmvSelfDefinedReference.CAP_NO_CVM);
        if (value != null) {
            params.CapNoCvm = value[0];
        }

        value = tp.getValue(EmvSelfDefinedReference.MOBILE_SUPPORT_INDICATOR);
        if (value != null) {
            params.MobileSupportIndicator = value[0];
        }

        value = tp.getValue(EmvSelfDefinedReference.EX_Terminal_CAP);
        if (value != null) {
            params.EXTerminalCap = value[0];
        }
        value = tp.getValue(EmvSelfDefinedReference.PPTLV);
        if (value != null && value.length > 0) {
            System.arraycopy(value, 0, params.PPTlv, 0, value.length);
            params.PPTlvLen = (byte) value.length;
        }

        value = tp.getValue(EmvSelfDefinedReference.DEUDOL);
        if (value != null && value.length > 0) {
            System.arraycopy(value, 0, params.DefaultUdol, 0, value.length);
            params.DefaultUdolLen = (byte) value.length;
        }

        value = tp.getValue(EmvSelfDefinedReference.MAGAPPVER);
        if (value != null && value.length > 0) {
            System.arraycopy(value, 0, params.MagAppVer, 0, value.length);
        }

        value = tp.getValue(EmvSelfDefinedReference.PWCONFIG);
        if (value != null && value.length > 0) {
            System.arraycopy(value, 0, params.PwConfig, 0, value.length);
        }
        value = tp.getValue(EmvSelfDefinedReference.KERNELID);
        if (value != null && value.length > 0) {
            System.arraycopy(value, 0, params.KernelId, 0, value.length);
        }

        value = tp.getValue(EmvSelfDefinedReference.STATUSCHECK);
        if (value != null) {
            params.StatusCheckSupport = value[0];
        }

        value = tp.getValue(EmvSelfDefinedReference.ZEROALLOW);
        if (value != null) {
            params.ZeroAmountAllow = value[0];
        } else {
            params.ZeroAmountAllow = 0x01;
        }

        value = tp.getValue(EmvSelfDefinedReference.EXAIDSUPP);
        if (value != null) {
            params.ExtendAidSupport = value[0];
        }

//        value = tp.getValue(EmvSelfDefinedReference.CLSSCVA);
//        if (value != null) {
//            params.ClssCardholderVerifyAllow = value[0];
//        }

        value = tp.getValue(EmvSelfDefinedReference.DRLSTATUS);
        if (value != null) {
            params.DrlStatus = value[0];
        }

        value = tp.getValue(EmvSelfDefinedReference.MAGSCVM);
        if (value != null) {
            params.MagStripeCvm = value[0];
        }

        value = tp.getValue(EmvSelfDefinedReference.MEXLTTORN);
        if (value != null && value.length > 0) {
            System.arraycopy(value, 0, params.MaxLifetimeTornLog, 0, value.length);
        }

        value = tp.getValue(EmvSelfDefinedReference.MAGSNOCVM);
        if (value != null) {
            params.MagStripeNoCvm = value[0];
        }

        value = tp.getValue(EmvSelfDefinedReference.EXRANDOM);
        if (value != null) {
            params.EXRandomScope = value[0];
        }
        value = tp.getValue(EmvSelfDefinedReference.COMBINATIONOPT);
        if (value != null && value.length > 0) {
            System.arraycopy(value, 0, params.CombinationOP, 0, value.length);
        }

        value = tp.getValue(EmvStandardReference.TIP);
        if (value != null && value.length > 0) {
            System.arraycopy(value, 0, params.TIP, 0, value.length);
        }
        value = tp.getValue(EmvSelfDefinedReference.PP1F8101);
        if (value != null && value.length > 0) {
            params.TransTypeCheckFlag = value[0];
        }
        value = tp.getValue(EmvSelfDefinedReference.DF7D);
        if (value != null && value.length > 0) {
            params.TransType = value[0];
        }
        value = tp.getValue(EmvSelfDefinedReference.EMVSELECTKERNEL);
        if (value != null && value.length > 0) {
            params._status = value[0];
        }

        value = tp.getValue(EmvStandardReference.POINT_OF_SERVICE_ENTRY_MODE);
        if (value != null && value.length > 0) {
            params._pos_entry = value[0];
        }

        value = tp.getValue(EmvSelfDefinedReference.ICS);
        if (value != null) {
            System.arraycopy(value, 0, params._ics, 0, Math.min(value.length, params._ics.length));
        }

        int nRet = operAID(aidStorageMode, params, EMVLevel2Const.AIDOperatorModel.AID_UPT);
        if (nRet != 0) {
            if (deviceLogger.isDebugEnabled())
                deviceLogger.error("[addAID]failed to add aid![" + Dump.getHexDump(aid) + "]" + nRet);
        } else {
            // initEmvEnv();
            syncAidParam();
        }
        return nRet == 0;
    }

    protected void initEmvEnv() {
        defaultEmvOperator = new EMVCoreOperator(getOwner());
//        int result = emvcore.jniNLSDKEntryPointInitialize(profilePath, defaultEmvOperator);//非接 ndk emv
//        deviceLogger.debug("[initEmvEnv] EmvJNIService->jniNLSDKEntryPointInitialize profilePath=" + profilePath + " ret=" + result);
//        deviceLogger.info("NDK EMV初始化结果：" + result);//最后一次初始化的是NDKEMV，设备也支持NDKEMV,就会走NDKEMV
//        if (result != -10000) {//-10000表示不支持NDK EMV
//            isSupportNDKEMV = true;
//        }
        boolean isOldPath = isOldEMVPath();
        if(EMVInnerUtils.isSDK3() && isOldPath){
            deviceLogger.debug("[initEmvEnv] EmvJNIService->jniemvInitializeEx(2),isOldPath:"+isOldPath);
            emvcore.jniemvInitializeEx(profilePath, defaultEmvOperator, 1);//flag传1，插卡和非接aid参数存储在同个路径,并且脱机pin是密文方式
        }else if (EMVInnerUtils.isSDK3()) {
            deviceLogger.debug("[initEmvEnv] EmvJNIService->jniemvInitializeEx(2),isOldPath:"+isOldPath);
            emvcore.jniemvInitializeEx(profilePath, defaultEmvOperator, 2);//flag传2，支持插卡和非接aid参数存储在2个路径,并且脱机pin是密文方式
        } else{
            deviceLogger.debug("[initEmvEnv] EmvJNIService->jniemvInitializeEx(0)");
            emvcore.jniemvInitializeEx(profilePath, defaultEmvOperator, 0);//flag传0，插卡和非接aid参数存储在同个路径,并且脱机pin是明文方式
        }
        deviceLogger.debug("[initEmvEnv] EmvJNIService->jniSDKEntryPointInitialize");
        emvcore.jniSDKEntryPointInitialize(profilePath, defaultEmvOperator);// update


    }

    @Override
    public boolean addCAPublicKey(byte[] inputData) {
        if (null == inputData)
            return false;
        CAPK capk = packager.unpack(inputData, CAPK.class, null);
        return addCAPublicKey(capk.getRid(), capk);
    }

    @Override
    public boolean deleteCAPublicKey(byte[] rid, Integer index) {
        if (rid == null) {
            if (deviceLogger.isDebugEnabled()) {
                deviceLogger.debug("[deleteCAPublicKey]rid should not be null!");
            }
            return false;
        }

        if (index == null) {
            int nRet = 0;
            int capkCount = emvcore.jniGetCAPKCount();
            deviceLogger.debug("[deleteCAPublicKey] EmvJNIService->jniGetCAPKCount capkCount=" + capkCount);
            List<capk> delList = new ArrayList<capk>();
            for (int i = 0; i < capkCount; i++) {
                capk capk = new capk();
                emvcore.jniGetCAPK(i, capk);
                deviceLogger.debug("[deleteCAPublicKey] EmvJNIService->jniGetCAPK i=" + i + " capk._rid=" + InnerUtils.hexString(capk._rid) + " rid=" + InnerUtils.hexString(rid));
                if (Arrays.equals(capk._rid, rid)) {
                    delList.add(capk);
                }
            }
            for (int i = 0; i < delList.size(); i++) {
                capk capk = delList.get(i);
                nRet = emvcore.jniSDKEPOperCAPK(capk, EMVLevel2Const.CAPKOperatorModel.CAPK_RMV);
                deviceLogger.debug("[deleteCAPublicKey] EmvJNIService->jniSDKEPOperCAPK(CAPK_RMV) i=" + i + " capk._rid=" + InnerUtils.hexString(capk._rid) + " rid=" + InnerUtils.hexString(rid));
                if (nRet != 0) {
                    if (deviceLogger.isDebugEnabled()) {
                        deviceLogger.debug("[deleteCAPublicKey]failed to clear capks:" + Dump.getHexDump(rid) + "," + nRet);
                    }
                    break;
                }
            }
            return nRet == 0;

        }

        capk capk = new capk();
        System.arraycopy(rid, 0, capk._rid, 0, rid.length);
        capk._index = (byte) (index & 0xFF);

        int nRet = emvcore.jniSDKEPOperCAPK(capk, EMVLevel2Const.CAPKOperatorModel.CAPK_RMV);
        deviceLogger.debug("[deleteCAPublicKey] EmvJNIService->jniSDKEPOperCAPK(CAPK_RMV) capk._rid=" + InnerUtils.hexString(capk._rid) + " capk._index=" + capk._index + " ret=" + nRet);
        if (nRet != 0) {
            if (deviceLogger.isDebugEnabled()) {
                deviceLogger.error("[deleteCAPublicKey]failed to delete capk:" + Dump.getHexDump(rid) + "," + nRet);
            }
        }
        return nRet == 0;
    }

    @Override
    public boolean deleteAllCAPublicKey() {
        capk capk = new capk();
        int nRet = emvcore.jniSDKEPOperCAPK(capk, EMVLevel2Const.CAPKOperatorModel.CAPK_CLR);
        deviceLogger.debug("[deleteAllCAPublicKey] EmvJNIService->jniSDKEPOperCAPK(CAPK_CLR) ret=" + nRet);
        if (nRet != 0) {
            deviceLogger.error("[deleteAllCAPublicKey]failed to clear CAPublicKeys!" + nRet);
        }
        return nRet == 0;
    }

    @Override
    public CAPK getSpecifiedCAPublicKey(byte[] rid, @Nullable int index) {
        capk pstCAPK = new capk();
        pstCAPK._rid = rid;
        pstCAPK._index = (byte) (index & 0xff);
        int nRet = emvcore.jniSDKEPOperCAPK(pstCAPK, EMVLevel2Const.CAPKOperatorModel.CAPK_GET);
        deviceLogger.debug("[getSpecifiedCAPublicKey] EmvJNIService->jniSDKEPOperCAPK(CAPK_GET) capk._rid=" + InnerUtils.hexString(pstCAPK._rid) + " capk._index=" + pstCAPK._index + " ret=" + nRet);
        EMVInnerUtils.toString_capk(deviceLogger, pstCAPK);
        if (nRet != 0) {
            if (deviceLogger.isDebugEnabled()) {
                deviceLogger.debug("[getSpecifiedCAPublicKey]get capk failed!" + Dump.getHexDump(rid) + "," + index + "," + nRet);
            }
            return null;
        }
        /**
         * 构造一个CA公钥
         * <p>
         *
         * @param index
         *            认证中心公钥索引(0x9f22)
         * @param hashAlgorithmIndicator
         *            认证中心公钥哈什算法标识(0xDF06)
         * @param publicKeyAlgorithmIndicator
         *            认证中心公钥算法标识(0xDF07)
         * @param modulus
         *            认证中心公钥模(0xDF02)
         * @param exponent
         *            认证中心公钥指数(0xDF04)
         * @param sha1CheckSum
         *            认证中心公钥校验值(0xDF03)
         * @param expirationDate
         *            认证中心公钥有效期(格式yyyyMMdd)(0xDF05)
         */
        int moduleLen = (int) (pstCAPK.pk_mod_len & 0xff);
        byte[] module = new byte[moduleLen];
        String dateStr = EMVInnerUtils.bcd2str(pstCAPK._expired_date, 0, pstCAPK._expired_date.length * 2, true);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
        Date date = null;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            throw new DeviceRTException(ErrorCode.UNKNOWN, "[getSpecifiedCAPublicKey]could not parse dateStr to yyyyMMdd!" + dateStr);
        }
        System.arraycopy(pstCAPK.pk_modulus, 0, module, 0, moduleLen);
        deviceLogger.debug("[getSpecifiedCAPublicKey] _rid=" + InnerUtils.hexString(pstCAPK._rid) + " _index=" + pstCAPK._index + " _hash_algorithm=" + pstCAPK._hash_algorithm +
                " _pk_algorithm=" + pstCAPK._pk_algorithm + " module=" + InnerUtils.hexString(module) + " pk_exponent=" + InnerUtils.hexString(pstCAPK.pk_exponent) + " _hashvalue=" + InnerUtils.hexString(pstCAPK._hashvalue) + " date=" + dateStr);
        CAPK rslt = new CAPK(pstCAPK._index, pstCAPK._hash_algorithm, pstCAPK._pk_algorithm, module, pstCAPK.pk_exponent, pstCAPK._hashvalue, date);
        return rslt;
    }

    @Override
    public List<CAPK> getAllCAPublicKey() {
        List<CAPK> capkList = new ArrayList<CAPK>();
        int capkCount = emvcore.jniGetCAPKCount();
        deviceLogger.debug("[getAllCAPublicKey] EmvJNIService->jniGetCAPKCount capkCount=" + capkCount);
        for (int i = 0; i < capkCount; i++) {
            capk pstCAPK = new capk();
            emvcore.jniGetCAPK(i, pstCAPK);
            int moduleLen = (int) (pstCAPK.pk_mod_len & 0xff);
            byte[] module = new byte[moduleLen];
            System.arraycopy(pstCAPK.pk_modulus, 0, module, 0, moduleLen);
            String dateStr = EMVInnerUtils.bcd2str(pstCAPK._expired_date, 0, pstCAPK._expired_date.length * 2, true);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
            Date date = null;
            try {
                date = sdf.parse(dateStr);
            } catch (ParseException e) {
                throw new DeviceRTException(ErrorCode.UNKNOWN, "could not parse dateStr to yyyyMMdd!" + dateStr);
            }
            CAPK item = new CAPK((pstCAPK._index & 0xFF), pstCAPK._hash_algorithm, pstCAPK._pk_algorithm, module, pstCAPK.pk_exponent, pstCAPK._hashvalue, date);
            item.setRid(pstCAPK._rid);

            deviceLogger.debug("[getSpecifiedCAPublicKey] _rid=" + InnerUtils.hexString(pstCAPK._rid) + " _index=" + (pstCAPK._index & 0xFF) + " _hash_algorithm=" + pstCAPK._hash_algorithm +
                    " _pk_algorithm=" + pstCAPK._pk_algorithm + " module=" + InnerUtils.hexString(module) + " pk_exponent=" + InnerUtils.hexString(pstCAPK.pk_exponent) + " _hashvalue=" + InnerUtils.hexString(pstCAPK._hashvalue) + " date=" + dateStr);

            capkList.add(item);
        }
        return capkList;
    }

    private byte[] packPPTlv(byte[] inputData) {
        if (inputData == null || inputData.length <= 0) {
            return null;
        }
        deviceLogger.debug("[packPPTlv]inputData:" + (inputData == null ? null : InnerUtils.hexString(inputData)));
        TLVPackage tp = EMVInnerUtils.newTlvPackage();
        tp.unpack(inputData);
        TLVPackage pptlv = EMVInnerUtils.newTlvPackage();
        for (Integer key : matchMap.keySet()) {
            TLVMsg tlvMsg = tp.find(key);
            if (null != tlvMsg) {
                pptlv.append(matchMap.get(key), tlvMsg.getValue());
            }
        }
        return pptlv.pack();
    }

    @Override
    public boolean addAID(byte[] inputData, CardInterface aidStorageMode) {
        deviceLogger.debug("[addAID] byte[]---aidStorageMode:" + aidStorageMode);
        if (null == inputData)
            return false;
        byte[] data = inputData;
        data = dealRFcardParam(data);
        AID aidConfig = packager.unpack(data, AID.class, null);
        byte[] pptlv = packPPTlv(aidConfig.getKernelExtendedTLV());

        if (pptlv != null && pptlv.length != 0) {
            aidConfig.setKernelExtendedTLV(pptlv);
        }
        return addAID(aidConfig, aidStorageMode);
    }

    private byte[] dealRFcardParam(byte[] aidDatasource) {
        TLVPackage tlvPackage = EMVInnerUtils.newTlvPackage();
        tlvPackage.unpack(aidDatasource);
        byte[] cardAid = tlvPackage.getValue(EmvStandardReference.AID_TERMINAL);
        switch (getKernelID(cardAid)) {
            case EmvConst.KERNEL_ID_PAYPASS:
                //paypass kernel configuration
                String kernelConfig = tlvPackage.getString(0xDF811B);
                if (null != kernelConfig) {
                    tlvPackage.deleteByTag(0xDF811B);
                    tlvPackage.append(0xDF2F, kernelConfig);
                }
                // Reader Contactless Floor Limit DF19 ‘DF8123’
                String ctlsLimit = tlvPackage.getString(0xDF8123);
                if (null != ctlsLimit) {
                    tlvPackage.deleteByTag(0xDF8123);
                    tlvPackage.append(0xDF19, ctlsLimit);
                }

                // CLSS_TRANS_LIMIT_NO_ON_DEVICE_CVM(DF8124),我们参数对应的配置是0xDF20 (Paypass)
                String transLimit = tlvPackage.getString(0xDF8124);
                if (null != transLimit) {
                    tlvPackage.deleteByTag(0xDF8124);
                    tlvPackage.append(0xDF20, transLimit);
                }
                //Reader CVM Required Limit DF21 '0xDF8126'
                String CVMLimit = tlvPackage.getString(0xDF8126);
                if (null != CVMLimit) {
                    tlvPackage.deleteByTag(0xDF8126);
                    tlvPackage.append(0xDF21, CVMLimit);
                }
                // Mag-stripe CVM Capability – CVM Required DF42 'DF811E'
                String magCVMLimit = tlvPackage.getString(0xDF811E);
                if (null != magCVMLimit) {
                    tlvPackage.deleteByTag(0xDF811E);
                    tlvPackage.append(0xDF42, magCVMLimit);
                }
                //Mag-stripe CVM Capability – No CVM Required DF47 -'DF812C'
                String magNoCVMLimit = tlvPackage.getString(0xDF812C);
                if (null != magNoCVMLimit) {
                    tlvPackage.deleteByTag(0xDF812C);
                    tlvPackage.append(0xDF47, magNoCVMLimit);
                }
                // CLSS_TRANS_LIMIT_ON_DEVICE_CVM(DF8125) (Paypass)
                String df8125 = tlvPackage.getString(0xDF8125);
                if (null != df8125) {
                    tlvPackage.deleteByTag(0xDF8125);
                    tlvPackage.append(0x9F7B, df8125);
                }
                //Mobile Support Indicator  DF46-'9F7E’
                String msi = tlvPackage.getString(0x9F7E);
                if (null != msi) {
                    tlvPackage.deleteByTag(0x9F7E);
                    tlvPackage.append(0xDF46, msi);
                }
                String Tag9C = tlvPackage.getString(0x9C);
                if (null != Tag9C) {
                    tlvPackage.deleteByTag(0x9C);
                    tlvPackage.append(0xDF7D, Tag9C);
                }
                String Tag8119 = tlvPackage.getString(0xDF8119);
                if (null != Tag8119) {
                    tlvPackage.deleteByTag(0xDF8119);
                    tlvPackage.append(0xDF48, Tag8119);
                }
                break;
            case EmvConst.KERNEL_ID_PAYWAVE:
                break;
            case EmvConst.KERNEL_ID_JCB:
                break;
            case EmvConst.KERNEL_ID_EXPRESSPAY:
                String Tag9f6d = tlvPackage.getString(0x9F6D);
                if (null != Tag9f6d) {
                    tlvPackage.deleteByTag(0x9F6D);
                    tlvPackage.append(0xDF49, Tag9f6d);
                }
                // TTQ(9F6E / 9F66) (ExpressPay)
                String exp9F6E = tlvPackage.getString(0x9F6E);
                if (null != exp9F6E) {
                    tlvPackage.deleteByTag(0x9F6E);
                    tlvPackage.append(0x9F66, exp9F6E);
                }
                break;
            default:
                return aidDatasource;
        }
        return tlvPackage.pack();
    }

    private boolean deleteAllAID(CardInterface aidStorageMode) {
        deviceLogger.debug("[deleteAllAID]  aidStorageMode:" + aidStorageMode);
        emvparam params = new emvparam();
        int nRet = operAID(aidStorageMode, params, EMVLevel2Const.AIDOperatorModel.AID_CLR);
        if (nRet != 0) {
            deviceLogger.debug("[deleteAllAID]failed to clear aids!" + nRet);
        } else {
            // initEmvEnv();
            syncAidParam();
        }
        return nRet == 0;
    }

    @Override
    public boolean deleteAID(byte[] aid, CardInterface aidStorageMode) {
        deviceLogger.debug("[deleteAID]  aidStorageMode:" + aidStorageMode);
        emvparam params = new emvparam();
        if (aid == null)
            return deleteAllAID(aidStorageMode);
        // 直接拷贝aid
        System.arraycopy(aid, 0, params._aid, 0, aid.length);
        params._aid_len = (byte) aid.length;
        int nRet = operAID(aidStorageMode, params, EMVLevel2Const.AIDOperatorModel.AID_RMV);
        if (nRet != 0) {
            deviceLogger.error("[deleteAID]failed to delete aid![" + Dump.getHexDump(aid) + "]" + nRet);
        } else {
            // initEmvEnv();
            syncAidParam();
        }
        return nRet == 0;
    }

    private AID getSpecifiedAID(byte[] aid, CardInterface aidStorageMode) {
        deviceLogger.debug("[getSpecifiedAID] aidStorageMode:" + aidStorageMode);
        emvparam item = new emvparam();
        System.arraycopy(aid, 0, item._aid, 0, aid.length);
        item._aid_len = (byte) (aid.length & 0xFF);
        int nRet = operAID(aidStorageMode, item, EMVLevel2Const.AIDOperatorModel.AID_GET);
        if (nRet != 0) {
            if (deviceLogger.isDebugEnabled()) {
                deviceLogger.debug("[getSpecifiedAID]get aid failed!" + Dump.getHexDump(aid) + "," + "," + nRet);
            }
            return null;
        }
        return copyAid(item);
    }

    private AID copyAid(emvparam item) {
        AID aidConfig = new AID();
        aidConfig.setAcquirerIdentifier((null == item._acq_id ? null : EMVInnerUtils.hexString(item._acq_id)));
        aidConfig.setAdditionalTerminalCapabilities(item._add_cap);
        byte[] aid = new byte[item._aid_len & 0xFF];
        System.arraycopy(item._aid, 0, aid, 0, aid.length);
        aidConfig.setAid(aid);
        aidConfig.setAppSelectIndicator(item._app_sel_indicator & 0xff);//装载时银联aid取反，获取aid也需要取反
        if (!isOverseas()) {
            if (aidConfig.getAppSelectIndicator() == 1) {
                aidConfig.setAppSelectIndicator(0);
            } else {
                aidConfig.setAppSelectIndicator(1);
            }
        }
        aidConfig.setAppVersionNumber(item._app_ver);
        byte[] ddol = new byte[item._default_ddol_len & 0xFF];
        System.arraycopy(item._default_ddol, 0, ddol, 0, ddol.length);
        aidConfig.setDdol(ddol);
        aidConfig.setEcTransactionLimit(item._ec_limit);
        aidConfig.setMaxTargetPercentage(item._max_target_percent & 0xff);
        aidConfig.setMerchantCategoryCode(EMVInnerUtils.hexString(item._mer_category_code));
        aidConfig.setStatusCheckSupport(new byte[]{item.StatusCheckSupport});
        aidConfig.setTacDefault(item._tac_default);
        aidConfig.setTacOnLine(item._tac_online);
        aidConfig.setTacDenial(item._tac_denial);
        aidConfig.setTargetPercentage(item._target_percent & 0xff);
        byte[] tdol = new byte[item._default_tdol_len & 0xFF];
        System.arraycopy(item._default_tdol, 0, tdol, 0, tdol.length);
        aidConfig.setTdol(tdol);
        aidConfig.setTerminalCapabilities(item._cap);
        aidConfig.setTerminalCountryCode(item._term_country_code);
        aidConfig.setTerminalFloorLimit(item._floorlimit);
        aidConfig.setTerminalTransProp(item._trans_prop);
        aidConfig.setTerminalType(item._type & 0xff);
        aidConfig.setTransactionCurrencyCode(EMVInnerUtils.hexString(item._trans_curr_code));
        aidConfig.setTransactionCurrencyExp(EMVInnerUtils.hexString(new byte[]{item._trans_curr_exp}));
        aidConfig.setEcSupportIndicator(item._ec_indicator & 0xff);
        aidConfig.setCvmLimit(item._cvm_limit);
        aidConfig.setOfflineFloorLimit(item._cl_offline_limit);
        aidConfig.setTransactionLimit(item._cl_limit);
        aidConfig.setThresholdValue(item._threshold_value);
        aidConfig.setMaxTargetPercentage(item._max_target_percent & 0xff);
        aidConfig.setCombinationOP(item.CombinationOP);
        aidConfig.setTerminalInterchangeProfile(item.TIP);

        aidConfig.setDrlStatus(new byte[]{item._status});
        deviceLogger.debug("[copyAid] _status="+item._status+" DefaultUdolLen="+item.DefaultUdolLen);
        if(!Arrays.equals(item.DrlData,new byte[item.DrlData.length])){
            deviceLogger.debug("[copyAid] PAYWAVE DRL");
            aidConfig.setDrlData(item.DrlData);
        }
        if(InnerUtils.hexString(aidConfig.getAid()).startsWith("A000000025") && item.DefaultUdolLen > 0 ){
            try {
                deviceLogger.debug("[copyAid] AMEX DRL");
                byte[] expDrlDataTlv = new byte[item.DefaultUdolLen];
                System.arraycopy(item.DefaultUdol,0,expDrlDataTlv,0,expDrlDataTlv.length);
                deviceLogger.debug("[copyAid] expDrlDataTlv="+InnerUtils.hexString(expDrlDataTlv));
                TLVPackage tlvpackage = InnerUtils.newTlvPackage();
                tlvpackage.unpack(expDrlDataTlv);
                aidConfig.setDrlDataExp(tlvpackage.getValue(EmvSelfDefinedReference.DRLDATA_EXP));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        deviceLogger.debug("[copyAid] aidConfig.Aid="+InnerUtils.hexString(aidConfig.getAid())+" aidConfig.DrlData="+InnerUtils.hexString(aidConfig.getDrlData()));
        return aidConfig;
    }

    @Override
    public List<AID> getAID(byte[] aid, CardInterface aidStorageMode) {
        deviceLogger.debug("[getAID]aidStorageMode: aid=" + InnerUtils.hexString(aid) + " aidStorageMode=" + aidStorageMode);
        List<AID> aidList = new ArrayList<AID>();
        if (null != aid) {
            AID specifiedAID = this.getSpecifiedAID(aid, aidStorageMode);
            if (specifiedAID != null) {
                aidList.add(specifiedAID);
            }
            return aidList;
        }
        if (aidStorageMode == CardInterface.CONTACT && EMVInnerUtils.isSDK3()) {
            int aidCount = emvcore.jniemvGetAIDCount();
            deviceLogger.debug("[getAID] EmvJNIService->jniemvGetAIDCount aidCount=" + aidCount);
            for (int i = 0; i < aidCount; i++) {
                emvparam item = new emvparam();
                emvcore.jniemvGetAID(i, item);
                deviceLogger.debug("[getAID] EmvJNIService->jniemvGetAID i=" + i);
                aidList.add(copyAid(item));
            }
            return aidList;
        } else if (aidStorageMode == CardInterface.CONTACTLESS) {
            int aidCount = emvcore.jniGetAIDCount();
            deviceLogger.debug("[getAID] EmvJNIService->jniGetAIDCount aidCount=" + aidCount);
            for (int i = 0; i < aidCount; i++) {
                emvparam item = new emvparam();
                emvcore.jniGetAID(i, item);
                deviceLogger.debug("[getAID] EmvJNIService->jniGetAID i=" + i);
                aidList.add(copyAid(item));
            }
            return aidList;
        } else {
            int aidCtlsCount = emvcore.jniGetAIDCount();
            deviceLogger.debug("[getAID] EmvJNIService->jniGetAIDCount aidCtlsCount=" + aidCtlsCount);
            for (int i = 0; i < aidCtlsCount; i++) {
                emvparam item = new emvparam();
                emvcore.jniGetAID(i, item);
                deviceLogger.debug("[getAID] EmvJNIService->jniGetAID i=" + i);
                aidList.add(copyAid(item));
            }
            return aidList;
        }
    }

    @Override
    public EMVTransController getEmvTransController(EMVControllerListener emvControllerListener) {
        controller = new EMVLevel2TransferController(getOwner(), emvExtParams, emvControllerListener);
        controller.contextHelper.init(controller, context);
        controller.setDefaultEmvOperator(defaultEmvOperator);
        controller.setProfilePath(profilePath);
        defaultEmvOperator.setTransferController(controller);

        if (null == emvExtParams || !emvExtParams.isExternalReader()) {
            deviceLogger.debug("[getEmvTransController] EmvJNIService->jniemvUseOutCardReader(0)");
            emvcore.jniemvUseOutCardReader(0);
        } else {
            deviceLogger.debug("[getEmvTransController] EmvJNIService->jniemvUseOutCardReader(1)");
            emvcore.jniemvUseOutCardReader(1);
        }
        return controller;
    }

    @Override
    public void getEMVTransLogs(EMVTransLogListener transLogListener) {
        DefaultGetLogListener listener = new DefaultGetLogListener(LogType.PBOC_LOG, transLogListener, null);
        EMVLevel2TransferController controller = new EMVLevel2TransferController(this.getOwner(), emvExtParams, listener, EMVTransStep.defaultQuerySteps());
        controller.contextHelper.init(controller, context);
        int processingCode = 0x0A;//int PBOC_LOGGER = 0x0A;
        defaultEmvOperator.setTransferController(controller);
        if (lastCardReadContainType(CardType.RFCARD)) {
            controller.contextHelper.setDefaultModuleType(ModuleType.RFCARDREADER);
            processingCode = 0x37;//int RF_PBOC_LOGGER = 0x37;
        }
        controller.startEMV(processingCode, new BigDecimal(0), false, null);
    }

    enum LogType {
        PBOC_LOG,
        EC_LOG
    }

    protected boolean lastCardReadContainType(CardType expectedType) {
        if (emvExtParams != null && emvExtParams.isExternalReader()) {
            int mediaType = emvExtParams.getMediaType();
            if (expectedType == CardType.RFCARD) {
                return mediaType == 0x01;
            } else if (expectedType == CardType.ICCARD) {
                return mediaType == 0x00;
            } else {
                return false;
            }
        } else {
            CardReaderModule cardreader = (CardReaderModule) getOwner().getStandardModule(ModuleType.COMMON_CARDREADER);
            CardType[] types = cardreader.getLastReaderTypes();
            if (types == null)
                return false;

            for (CardType type : types) {
                if (expectedType == type) {
                    return true;
                }
            }
            return false;
        }
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
            if (logType == LogType.PBOC_LOG) {
                List<EMVTransLog> rslt = new ArrayList<EMVTransLog>();
                int count = getPbocLogCount();
                PbocTransFormat fmt = getPbocLogFmt();
                if (fmt == null) {
                    if (lastCardReadContainType(CardType.RFCARD)) {
                        // 通过APDU获取非接电子现金日志
                        RFCardModule rfCarf = (RFCardModule) getOwner().getStandardModule(ModuleType.RFCARDREADER);
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
                            if (null != emvExtParams && emvExtParams.isExternalReader()) {
                                if (emvExtParams.getMediaType() == 0x00) {
                                    extICCardModule.transmit(req, null);
                                } else {
                                    transLog = extRFCardModule.transmit(req);
                                }
                            } else {
                                transLog = rfCarf.transmit(req, 3);
                            }

                            // deviceLogger.info("read pboc
                            // log:"+ISOUtils.hexString(transLog));
                            if (transLog != null && transLog.length == 47) {
                                logs.add(new EMVTransLog(transLog));
                            }
                        }
                        pbocLogs = logs;
                    } else {
                        pbocLogs = rslt;
                    }
                } else {
                    for (int i = 1; i <= count; i++) {
                        EMVTransLog log = getPbocLog(i, fmt);
                        // count 为交易日志容量，当取到当前数据为空时默认最后一条
                        if (null == log) {
                            break;
                        }
                        rslt.add(log);
                    }
                    pbocLogs = rslt;
                }
            } else if (logType == LogType.EC_LOG) {

                List<ECTransLog> rslt = new ArrayList<ECTransLog>();
                int count = getEcLogCount();
                ECTransFormat fmt = getEcLogFmt();
                if (fmt == null) {
                    ecLogs = rslt;
                } else {
                    for (int i = 1; i <= count; i++) {
                        ECTransLog log = getEcLog(i, fmt);
                        // count 为交易日志容量，当取到当前数据为空时默认最后一条
                        if (null == log) {
                            break;
                        }
                        rslt.add(log);
                    }
                    ecLogs = rslt;
                }
            }
            controller.confirmInformation(true);
        }

        @Override
        public void onRequestSelectApplication(EMVTransController controller, List<AIDEntity> aidEntityList, int times) {
            if (logType.equals(LogType.PBOC_LOG))
                controller.setSelectedApplication(transLogListener.onRequestSelectApplication(aidEntityList));
            else
                controller.setSelectedApplication(ecListener.onRequestSelectApplication(aidEntityList));
        }

        @Override
        public void onRequestInputPIN(EMVTransController controller, boolean requireOnline, PINEntity pinEntity) {
        }

        @Override
        public void onRequestOnlineProcess(EMVTransController controller) {
        }

        @Override
        public void onFallback(EMVTransController controller) {
            if (logType.equals(LogType.PBOC_LOG))
                transLogListener.onResult(null);
            else
                ecListener.onResult(null);
        }

        @Override
        public void onError(EMVTransController controller, Exception e) {
            if (logType.equals(LogType.PBOC_LOG))
                transLogListener.onResult(null);
            else
                ecListener.onResult(null);

        }

        @Override
        public void onRequestSelectAccountType(EMVTransController controller, AccountType[] accountType) {
            controller.setSelectedAccountType(AccountType.DEFAULT);
        }


        @Override
        public void onRequestConfirmID(EMVTransController controller, IDCardType cardType, String IDNo) {
            controller.confirmID(true);
        }

        @Override
        public void onRequestConfirmEC(EMVTransController controller) {
            controller.confirmEC(true);
        }

        @Override
        public void onRequestShowMessage(EMVTransController controller, String title, String msg, boolean isConfirm, int timeOut) {
            controller.confirmMessage(isConfirm);
        }

        @Override
        public void onRequestSelectLanguage(EMVTransController controller, String[] language) {
            if (language != null && language.length > 0) {
                controller.setSelectedLanguage(language[0]);
            } else {
                controller.cancelEMVProcess();
            }
        }

        @Override
        public void onRequestConfirmFinalAppSelection(EMVTransController controller) {
            controller.confirmInformation(true);
        }

        @Override
        public void onEmvFinished(boolean isSuccess, EMVTransController controller) {
            if (logType.equals(LogType.PBOC_LOG)) {
                transLogListener.onResult(pbocLogs);
            } else
                ecListener.onResult(ecLogs);
        }

        @Override
        public void onRequestInputAmount(EMVTransController controller) {
            controller.setTransactionAmount(new BigDecimal(0));
        }
    }

    @Override
    public void getECTransLogs(ECTransLogListener transLogListener) {
        DefaultGetLogListener listener = new DefaultGetLogListener(LogType.EC_LOG, null, transLogListener);
        EMVLevel2TransferController controller = new EMVLevel2TransferController(this.getOwner(), emvExtParams, listener, EMVTransStep.defaultQuerySteps());
        controller.contextHelper.init(controller, context);
        // TODO
        controller.contextHelper.setDefaultModuleType(ModuleType.ICCARDREADER);
        int processingCode = 0x0E;// EC_LOGGER = 0x0E;
        defaultEmvOperator.setTransferController(controller);
        if (lastCardReadContainType(CardType.RFCARD)) {
            controller.contextHelper.setDefaultModuleType(ModuleType.RFCARDREADER);
            processingCode = 0x40;//RF_EC_LOGGER = 0x40;
        }
        controller.startEMV(processingCode, new BigDecimal(0), false, null);
    }

    int getEcLogCount() {
        byte[] buffer = new byte[256];
        int record = emvcore.jniemvGetecloadLog(EMVLevel2Const.PBOCLOG_OPERATOR.PBOCLOG_RECNUM, buffer, buffer.length);
        deviceLogger.debug("[getEcLogCount] EmvJNIService->jniemvGetecloadLog(PBOCLOG_RECNUM) result=" + record + " buffer=" + InnerUtils.hexString(buffer));
        if (record <= 0) {
            deviceLogger.error("[getEcLogCount]no ec record found!");
            return -1;
        }
        return record;
    }

    ECTransFormat getEcLogFmt() {
        byte[] fmtbuf = new byte[256];
        int key = emvcore.jniemvGetecloadLog(EMVLevel2Const.PBOCLOG_OPERATOR.PBOCLOG_FMT, fmtbuf, fmtbuf.length);
        deviceLogger.debug("[getEcLogFmt] EmvJNIService->jniemvGetecloadLog(PBOCLOG_FMT) result=" + key + " fmtbuf=" + InnerUtils.hexString(fmtbuf));
        if (key <= 0) {
            deviceLogger.error("[getEcLogFmt]read ec log fmt failed!");
            return null;
        }
        byte[] keys = new byte[key];
        System.arraycopy(fmtbuf, 0, keys, 0, key);
        return new ECTransFormat(keys);
    }

    ECTransLog getEcLog(int count, ECTransFormat fmt) {

        byte[] fmtbuf = new byte[256];
        int key = emvcore.jniemvGetecloadLog(count, fmtbuf, fmtbuf.length);
        deviceLogger.debug("[getEcLog] EmvJNIService->jniemvGetecloadLog count=" + count + " result=" + key + " fmtbuf=" + InnerUtils.hexString(fmtbuf));
        if (key <= 0) {
            deviceLogger.error("[getEcLog]read ec log failed!");
            return null;
        }
        byte[] logValue = new byte[key];
        System.arraycopy(fmtbuf, 0, logValue, 0, key);
        return new ECTransLog(logValue, fmt);
    }

    int getPbocLogCount() {
        byte[] buffer = new byte[256];
        int record = emvcore.jniemvGetPBOCLog(EMVLevel2Const.PBOCLOG_OPERATOR.PBOCLOG_RECNUM, buffer, buffer.length);
        deviceLogger.debug("[getPbocLogCount] EmvJNIService->jniemvGetPBOCLog(PBOCLOG_RECNUM) count=" + record + " buffer=" + InnerUtils.hexString(buffer));
        if (record <= 0) {
            deviceLogger.error("[getPbocLogCount]no pboc record found!");
            return -1;
        }
        return record;
    }

    PbocTransFormat getPbocLogFmt() {
        byte[] fmtbuf = new byte[256];
        int key = emvcore.jniemvGetPBOCLog(EMVLevel2Const.PBOCLOG_OPERATOR.PBOCLOG_FMT, fmtbuf, fmtbuf.length);
        deviceLogger.debug("[getPbocLogFmt] EmvJNIService->jniemvGetPBOCLog(PBOCLOG_FMT) count=" + key + " fmtbuf=" + InnerUtils.hexString(fmtbuf));
        if (key <= 0) {
            deviceLogger.error("[getPbocLogFmt]read pboc log fmt failed!");
            return null;
        }
        byte[] keys = new byte[key];
        System.arraycopy(fmtbuf, 0, keys, 0, key);
        return new PbocTransFormat(keys);
    }


    EMVTransLog getPbocLog(int count, PbocTransFormat fmt) {
        byte[] fmtbuf = new byte[256];
        int key = emvcore.jniemvGetPBOCLog(count, fmtbuf, fmtbuf.length);
        deviceLogger.debug("[getPbocLog] EmvJNIService->jniemvGetPBOCLog key=" + key + " count=" + count + " fmtbuf=" + InnerUtils.hexString(fmtbuf));
        if (key <= 0) {
            deviceLogger.error("[getPbocLog]read pboc log failed!");
            return null;
        }
        byte[] logValue = new byte[key];
        System.arraycopy(fmtbuf, 0, logValue, 0, key);
        return new EMVTransLog(logValue, fmt);
    }

    @Override
    public boolean setEmvData(int tag, byte[] value) {
        deviceLogger.debug("[setEmvData] tag:" + String.format("0x%x", tag) + " value:" + (value == null ? null : InnerUtils.hexString(value)));
        int result = -1;
        if (isEpProcess) {
            if (defaultEmvOperator.getTransferController() != null && defaultEmvOperator.getTransferController().hasDoneNDKEMV() &&
                    defaultEmvOperator.getTransferController().getCurrentEmvState() == AbstractEMVTransController.EMVState.FINAL_APPLICATION_SELECT) {
                deviceLogger.debug("[setEmvData] isNDKEmvProcess final select process");
                finanlSelData.append(tag, value);
                deviceLogger.debug("[setEmvData] complete");
                return true;
            }
            result = emvcore.jniSDKEPSetData(tag, value, value.length);
            deviceLogger.debug("[setEmvData] EmvJNIService->jniSDKEPSetData tag=" + String.format("0x%x", tag) + " value=" + InnerUtils.hexString(value) + " result=" + result);
        } else {
            result = emvcore.jniemvsetdata(tag, value, value.length);
            deviceLogger.debug("[setEmvData] EmvJNIService->jniemvsetdata tag=" + String.format("0x%x", tag) + " value=" + InnerUtils.hexString(value) + " result=" + result);
        }
        deviceLogger.debug("[setEmvData] result=" + result);
        if (result == 0) {
            return true;
        } else {
            deviceLogger.error("[setEmvData]failed to set EmvData!" + result + ",isEpProcess:" + isEpProcess);
            return false;
        }
    }

    @Override
    public TLVPackage getEmvData(int[] emvTags) {
        byte[] buffer = new byte[9216];
        int len = emvFetchData(emvTags, emvTags.length, buffer, buffer.length);
        byte[] payload = new byte[len];
        System.arraycopy(buffer, 0, payload, 0, len);

        TLVPackage tlvPackage = EMVInnerUtils.newTlvPackage();
        if (null != payload) {//有的卡5F24是三字节，从等效二磁道截取就2字节，先不处理
            tlvPackage.unpack(payload);
            if (isEpProcess && !tlvPackage.hasTag(0x5F24) && IntArrLookupInt(emvTags, 0x5F24)) {
                len = emvcore.jniSDKEPGetData(0x57, buffer, buffer.length);
                deviceLogger.debug("[setEmvData] EmvJNIService->jniSDKEPGetData tag=" + 0x57 + " buffer=" + InnerUtils.hexString(buffer) + " len=" + len);
                if (len > 0) {
                    byte[] track2 = new byte[len];
                    System.arraycopy(buffer, 0, track2, 0, len);
                    String track2Str = InnerUtils.hexString(track2);
                    String expiredDate = track2Str.substring(track2Str.indexOf('D') + 1, track2Str.indexOf('D') + 5);
                    tlvPackage.append(0x5F24, InnerUtils.hex2byte(expiredDate));
                }
            }
        }
        return tlvPackage;
    }

    @Override
    public byte[] getICCdata(int tag) {
        byte[] temp = new byte[15];
        int[] lentemp = new int[1];
        int rs = emvICCGetDataByTagName(tag, temp, lentemp);
        if (rs == 0) {
            int length = lentemp[0];
            byte[] sc = new byte[length];
            System.arraycopy(temp, 0, sc, 0, length);
            return sc;
        }
        return null;
    }

    @Override
    public String getEMVKernelVersion() {
        String version = emvcore.jniemvgetVersion();
        deviceLogger.debug("[getEMVKernelVersion] EmvJNIService->jniemvgetVersion emv kernel version:" + version);
        return version;
    }

    @Override
    public void setIndicatorsAndBeep(boolean isEnable) {
        deviceLogger.debug("[setIndicatorsAndBeep]:" + isEnable);
        this.isIndicatorsAndBeep = isEnable;
        EMVInnerUtils.setIndicatorsAndBeep(isEnable);
    }

    /**
     * 只比对指定长度byte
     *
     * @param a   字节数组a
     * @param b   字节数组b
     * @param len 长度
     * @return false a,b 指定长度不相等; ture a,b 指定长度相等
     */
    public static boolean compareBytes(byte[] a, byte[] b, int len) {
        if (a == null || a.length == 0 || b == null || b.length == 0 || a.length < len || b.length < len) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }
        return true;
    }

    private void icsOptSet(byte[] b, int offset) {
        int index = offset >> 8;
        byte val = (byte) (offset & 0x00FF);

        if (b == null) {
            return;
        }
        if (index < 0 || index >= b.length) {
            return;
        }
        b[index] |= val;
    }

    private void icsOptUnSet(byte[] b, int offset) {
        int index = offset >> 8;
        byte val = (byte) (offset & 0x00FF);

        if (b == null) {
            return;
        }
        if (index < 0 || index >= b.length) {
            return;
        }
        b[index] &= ~val;
    }

    private int emvICCGetDataByTagName(int unTagName, byte[] pusOut, int[] pnOutLen) {
        deviceLogger.debug("[emvICCGetDataByTagName]tags:" + Integer.toHexString(unTagName).toUpperCase());
        if (isEpProcess) {
            int ret = emvcore.jniSDKEPICCGetDataByTagName(unTagName, pusOut, pnOutLen);
            deviceLogger.debug("[emvICCGetDataByTagName] EmvJNIService->jniSDKEPICCGetDataByTagName tag=" + String.format("0x%x", unTagName)
                    + " len=" + ((pnOutLen != null && pnOutLen.length > 1) ? pnOutLen[0] : "null") + " value=" + InnerUtils.hexString(pusOut));
            return ret;
        } else {
            int ret = emvcore.jniemvICCGetDataByTagName(unTagName, pusOut, pnOutLen);
            deviceLogger.debug("[emvICCGetDataByTagName] EmvJNIService->jniemvICCGetDataByTagName tag=" + String.format("0x%x", unTagName)
                    + " len=" + ((pnOutLen != null && pnOutLen.length > 1) ? pnOutLen[0] : "null") + " value=" + InnerUtils.hexString(pusOut));
            return ret;
        }
    }

    public int emvFetchData(int[] punTagName, int nTagCnt, byte[] pusOutBuf, int nMaxOutLen) {
        if (isEpProcess) {
            int ret = emvcore.jniSDKEPFetchData(punTagName, nTagCnt, pusOutBuf, nMaxOutLen);
            deviceLogger.debug("[emvICCGetDataByTagName] EmvJNIService->jniSDKEPFetchData tags=" + EMVInnerUtils.toString_tags(punTagName)
                    + " nTagCnt=" + nTagCnt + " nMaxOutLen=" + nMaxOutLen + " value=" + InnerUtils.hexString(pusOutBuf));
            return ret;
        } else {
            int ret = emvcore.jniemvFetchData(punTagName, nTagCnt, pusOutBuf, nMaxOutLen);
            deviceLogger.debug("[emvICCGetDataByTagName] EmvJNIService->jniemvFetchData tags=" + EMVInnerUtils.toString_tags(punTagName)
                    + " nTagCnt=" + nTagCnt + " nMaxOutLen=" + nMaxOutLen + " value=" + InnerUtils.hexString(pusOutBuf));
            return ret;
        }
    }

    public boolean isSupportEP(int transType) {
        boolean isSupport = true;
        switch (transType) {
            case TransactionType.EC_APPOINTED_LOAD_CONTACT:
                isSupport = false;
                break;
            case TransactionType.EC_APPOINTED_LOAD_CTLS:
                isSupport = false;
                break;
            case TransactionType.EC_CASH_BALANCE_CONTACT:
                isSupport = false;
                break;
            case TransactionType.EC_CASH_BALANCE_CTLS:
                isSupport = false;
                break;
            case TransactionType.EC_CASH_LOAD_CONTACT:
                isSupport = false;
                break;
            case TransactionType.EC_CASH_LOAD_CTLS:
                isSupport = false;
                break;
            case TransactionType.EC_CASH_LOAD_REVERSAL:
                isSupport = false;
                break;
            case TransactionType.EC_NOT_APPOINTED_LOAD_CONTACT:
                isSupport = false;
                break;
            case TransactionType.EC_NOT_APPOINTED_LOAD_CTLS:
                isSupport = false;
                break;
            case EmvConst.EMV_TRANS_PBOCLOG:
                isSupport = false;
                break;
            case EmvConst.EMV_TRANS_RF_PBOCLOG:
                isSupport = false;
                break;
            case EmvConst.EMV_TRANS_ECLOADLOG:
                isSupport = false;
                break;
            case EmvConst.EMV_TRANS_RF_ECLOADLOG:
                isSupport = false;
                break;
            default:
                isSupport = true;
                break;
        }
        return isSupport;
    }

    //EMV_doEmvStep0
    /*********** EMV 核心交易处理 ***********/
    EMVTransInfo doEmvStep0(EMVTransContext context, EMVTransInfo emvTransInfo) {
        deviceLogger.debug("[doEmvStep0] create init start ");
        boolean isActiveKernel = false;
        rf_transdata rfTransData = null;
        emv_opt emvOpt = context.getEmvOpt();
        deviceLogger.debug("[doEmvStep0] context.getEmvOpt()=" + context.getEmvOpt()+" isEPStartB="+controller.isEPStartB);
        TLVPackage tp = EMVInnerUtils.newTlvPackage();
        if (emvTransInfo.getEmvOpt() != null) {
            context.setForceOnline(emvTransInfo.getEmvOpt()._force_online_enable == 1);
        }
        tp.unpack(packager.pack(context));
        byte[] value = tp.getValue(EmvSelfDefinedReference.PBOC_TRANS_STEP);
        if (value == null || value.length <= 0)
            throw new EMVTransferException("[doEmvStep0]pboc trans step should not be null!");
        if (null == emvOpt)
            emvOpt = getDefaultEmvOpt(context, tp);
        emvOpt._seq_to = value[0];
        deviceLogger.debug("[doEmvStep0] emvOpt._trans_type=" + emvOpt._trans_type);
        deviceLogger.debug("[doEmvStep0] emvOpt._seq_to=" + emvOpt._seq_to);
        deviceLogger.debug("[doEmvStep0] emvOpt._online_result=" + emvOpt._online_result);
        epOpt = context.getEpOpt();
        EMVInnerUtils.toString_ep_opt(deviceLogger, epOpt);
        EMVInnerUtils.toString_emv_opt(deviceLogger, emvOpt);
        if (context.getMediaType() == EMVTransContext._EMV_MEDIATYPE_RFCARD) {
            epOpt.emSeqTo = value[0];
            if (epOpt.emSeqTo == 6 && (context.getKernelID() == EmvConst.KERNEL_ID_EXPRESSPAY || context.getKernelID() == EmvConst.KERNEL_ID_MCCS ||
                    context.getKernelID() == EmvConst.KERNEL_ID_MADA || context.getKernelID() == EmvConst.KERNEL_ID_RUPAY ||
                    context.getKernelID() == EmvConst.KERNEL_ID_PAYWAVE || context.getKernelID() == EmvConst.KERNEL_ID_PAYPASS ))
                epOpt.emSeqTo = 8;
            rfTransData = context.getRfTransData();
            if (null == rfTransData) {
                rfTransData = getDefaultRfTransData(context, tp);
            }
            deviceLogger.debug("[doEmvStep0] epOpt.ucTransType2=" + epOpt.ucTransType);
            deviceLogger.debug("[doEmvStep0] ep_opt.emSeqTo2=" + epOpt.emSeqTo);
        }
        int emvrslt = emvTransInfo.getEmvrsltCode();
        if (EMVTransContext._EMV_MEDIATYPE_ICCARD == context.getMediaType()) {
            synchronized (deviceLogger) {
                isEpProcess = false;
            }
            deviceLogger.error("[doEmvStep0] EmvJNIService->jniemvStart EmvCoreStep=" + getEmvCoreStep(emvOpt._seq_to));
            EMVInnerUtils.toString_emv_opt(deviceLogger, emvOpt);
            emvrslt = emvcore.jniemvStart(emvOpt);
            deviceLogger.debug("[doEmvStep0] EmvJNIService->jniemvStart emvrslt=" + emvrslt);
            deviceLogger.debug("[doEmvStep0] EmvJNIService EmvrsltCode: Transaction accept(1) | Transaction denial(2) | Transaction go-online(3) | Second Generate AC return AAC(4) | Transaction terminate(-1) | Transaction fallback(-2) | Other (Failure) | Successfully obtain PBOC2 log(9) | Successfully obtain EC-load log(10) | Successfully obtain EC-balance(contact)(12)");
            emvTransInfo.setEmvOpt(emvOpt);
            EMVInnerUtils.toString_emv_opt(deviceLogger, emvOpt);
        } else if (EMVTransContext._EMV_MEDIATYPE_RFCARD == context.getMediaType()) {
            deviceLogger.error("[doEmvStep0] EmvJNIService->startEntryPointProcess EmvCoreStep=" + getEmvCoreStep(epOpt.emSeqTo));
            int transType = context.getInnerTransactionType();
            boolean isSupportEP = isSupportEP(transType);
            deviceLogger.debug("[doEmvStep0] jniIsINTLEMVVersion=" + emvcore.jniIsINTLEMVVersion() + " transType=" + transType + " isSupportEP=" + isSupportEP + " isEpProcess=" + context.isEpProcess());
            if (isSupportEP && context.isEpProcess()) {
                synchronized (deviceLogger) {
                    isEpProcess = true;
                }
                deviceLogger.debug("[doEmvStep0]1 isNDKEMVProcess=" + context.isNDKEMVProcess() + ",isDonePreProcess:" + context.isDonePreProcess() + " epOpt.ucRestart=" + epOpt.ucRestart);
                //if (epOpt.ucRestart != EmvConst.EntryPointSeq.START_B) {
                if (!controller.isEPStartB) {
                    if (!context.isNDKEMVProcess() && !context.isDonePreProcess()) {//START_B不需要调用first jniSDKEntryPointProcess
                        deviceLogger.debug("[doEmvStep0] EmvJNIService->jniSDKEntryPointProcess {preprocess} start");
                        EMVInnerUtils.toString_ep_opt(deviceLogger, epOpt);
                        EMVInnerUtils.toString_rf_transdata(deviceLogger, rfTransData);
                        emvrslt = emvcore.jniSDKEntryPointProcess(epOpt, rfTransData);//预处理
                        deviceLogger.debug("[doEmvStep0] EmvJNIService->jniSDKEntryPointProcess {preprocess} emvrslt=" + emvrslt);
                        EMVInnerUtils.toString_ep_opt(deviceLogger, epOpt);
                        EMVInnerUtils.toString_rf_transdata(deviceLogger, rfTransData);
                        context.setEpOpt(epOpt);
                        context.setRfTransData(rfTransData);
                        if (EmvConst.EMV_TRANS_RF_ACTIVE_CARD != emvrslt) {
                            doEntrypointSuspend(emvrslt);
                            // todo返回监听回调
                        }
                    }
                }
                deviceLogger.debug("[doEmvStep0]2 isDoneNDKEmv=" + context.isNDKEMVProcess());
                if (context.isNDKEMVProcess()) {//只有先做了预处理，并且设备支持ndk emv，才走ndk emv流程
                    epOpt.emSeqTo = 6;
                    byte[] ctrl = context.getPusCtrl();
                    byte[] processData = context.getProcessData();//最终应用选择时设置的数据
                    int processDataLen = 0;
                    if (processData != null) {
                        processDataLen = processData.length;
                    }
                    deviceLogger.debug("[doEmvStep0] processDataLen:" + processDataLen + "; processData:" + (processData == null ? null : InnerUtils.hexString(processData)));
                    if (ctrl == null) {
                        ctrl = new byte[]{0x03, 0x00, (byte) 0x00, (byte) 0x00, 0x00};//在APP寻卡激活，需要获取数据,指示灯标志，不开指示灯，不需要最终选择回调
                        if (isIndicatorsAndBeep) {
                            ctrl[2] = (byte) 0xC0;
                            ctrl[3] = 0x55;
                        }
                    }
                    if (!isIndicatorsAndBeep) {
                        ctrl[2] = 0x00;
                        ctrl[3] = 0x00;
                    }
                    EMVInnerUtils.toString_ep_opt(deviceLogger, epOpt);
                    EMVInnerUtils.toString_rf_transdata(deviceLogger, rfTransData);
                    deviceLogger.debug("[doEmvStep0] EmvJNIService->jniNLSDKCLL2PerformTransaction ctrl=" + InnerUtils.hexString(ctrl) + " processData=" + InnerUtils.hexString(processData) + " processDataLen=" + processDataLen);
                    emvrslt = doSpecificKernel(emvTransInfo, context, rfTransData, epOpt, ctrl, processData, processDataLen, true);
                    deviceLogger.debug("[doEmvStep0] EmvJNIService->jniNLSDKCLL2PerformTransaction emvrslt=" + emvrslt);
                    EMVInnerUtils.toString_ep_opt(deviceLogger, epOpt);
                    EMVInnerUtils.toString_rf_transdata(deviceLogger, rfTransData);
                } else {
                    deviceLogger.debug("[doEmvStep0]3 isDonePreProcess=" + context.isDonePreProcess() + " emvrslt=" + emvrslt);
                    if ((!context.isDonePreProcess() || emvrslt == EmvConst.EMV_TRANS_RF_SELECT_NEXT_AID)) {
                        EMVInnerUtils.toString_ep_opt(deviceLogger, epOpt);
                        EMVInnerUtils.toString_rf_transdata(deviceLogger, rfTransData);
                        deviceLogger.debug("[doEmvStep0] EmvJNIService->jniSDKEntryPointProcess {second} start");
                        emvrslt = emvcore.jniSDKEntryPointProcess(epOpt, rfTransData);//激活
                        deviceLogger.debug("[doEmvStep0] EmvJNIService->jniSDKEntryPointProcess {second} emvrslt=" + emvrslt);
                        EMVInnerUtils.toString_ep_opt(deviceLogger, epOpt);
                        EMVInnerUtils.toString_rf_transdata(deviceLogger, rfTransData);
                        context.setKernelID(rfTransData.usKernelId[0]);
                        context.setDonePreProcess(true);
                        context.setEpOpt(epOpt);
                        context.setRfTransData(rfTransData);
                        emvTransInfo.setKernelId(rfTransData.usKernelId[0]);
                        isActiveKernel = true;
                    }

                    deviceLogger.debug("[doEmvStep0]4 isActiveKernel=" + isActiveKernel + " emvrslt=" + emvrslt + " epOpt.ucRestart=" + epOpt.ucRestart);
                    if ( (emvrslt >= 0) && (EmvConst.EMV_TRANS_RF_ACTIVE_KERNEL == emvrslt || !isActiveKernel || controller.isEPStartB)) {//isActiveKernel其他步骤单独进来时isActiveKernel=false
                        EMVInnerUtils.toString_ep_opt(deviceLogger, epOpt);
                        EMVInnerUtils.toString_rf_transdata(deviceLogger, rfTransData);
                        emvrslt = doSpecificKernel(emvTransInfo, context, rfTransData, epOpt, null, null, -1, false);
                        EMVInnerUtils.toString_ep_opt(deviceLogger, epOpt);
                        EMVInnerUtils.toString_rf_transdata(deviceLogger, rfTransData);
                    }
                    deviceLogger.debug("[doEmvStep0]5 jniSDKxxxProcess kernelID=" + context.getKernelID() + " emvrslt=" + emvrslt);
                    if (isEpProcess && null != epOpt && emvTransInfo.getKernelId() == EmvConst.KERNEL_ID_RUPAY) {
                        byte[] sw = new byte[2];
                        sw[0] = epOpt._ER_SW1;
                        sw[1] = epOpt._ER_SW2;
                        byte[] sw2 = new byte[2];
                        if (null != rfTransData.usSW12 && rfTransData.usSW12.length >= 2) {
                            System.arraycopy(rfTransData.usSW12, 0, sw2, 0, 2);
                        }
                        deviceLogger.debug("[emv]rupay epOpt.sw:" + InnerUtils.hexString(sw));
                        deviceLogger.debug("[emv]rupay rfTransData.usSW12:" + InnerUtils.hexString(sw));
                        if (Arrays.equals(sw, new byte[]{0x62, (byte) 0x83}) || Arrays.equals(sw2, new byte[]{0x62, (byte) 0x83})) {
                            emvTransInfo.setErrorcode(-5);//APPLICATON LOCK
                            emvTransInfo.setExecuteRslt(2);
                            emvTransInfo.setEmvrsltCode(2);
                            return emvTransInfo;
                        }
                    }
                }
            } else {//RF start
                deviceLogger.debug("[doEmvStep0] jniemvrfstart process");
                synchronized (deviceLogger) {
                    isEpProcess = false;
                }
                emvOpt._request_amt = 3;//RF card:input amount when process
                long amt = 0;
                if (null != context.getAmountAuthorisedNumeric()) {
                    amt = Long.valueOf(context.getAmountAuthorisedNumeric());
                }
                if (!getHasSecModule()) {
                    emvcore.jniemvWriteNLTagData(0x0014, InnerUtils.hex2byte(formatDate(new Date())), 7); //设置pos时间，EP没做检查，所以不会报错，rfstart会去检查时间（由于cposx3 无安全模块的时候 无法通过ndk获取pos时间 所以由上层下发）lindan
                }

                if (!context.isDonePreProcess()) {
                    emvrslt = emvcore.jniemvrfstart(emvOpt, amt);
                    deviceLogger.debug("[doEmvStep0] EmvJNIService->jniemvrfstart emvrslt=" + emvrslt);
                    context.setDonePreProcess(true);
                    context.setEmvOpt(emvOpt);
                }
                if (EMVLevel2Const.EmvExecRslt.EMV_TRANS_RF_ACTIVECARD == emvrslt || epOpt.emSeqTo > 0) { // 第一次判断请求打开射频卡是否成功
                    emvrslt = emvcore.jniemvrfstart(emvOpt, amt);
                    deviceLogger.debug("[doEmvStep0] EmvJNIService->jniemvrfstart2 emvrslt=" + emvrslt);
                    context.setEmvOpt(emvOpt);
                } else {
                    deviceLogger.error("[doEmvStep0]jniemvrfstart failed,emvrslt:" + emvrslt + ",emSeqTo" + epOpt.emSeqTo);
                }
                if (null != emvOpt && (emvOpt._seq_to == 3 || emvOpt._seq_to == 6 || emvOpt._seq_to == 8) && emvrslt >= 0) {
                    // onlinepin 输联机PIN后能取到
                    //script res 发卡行脚本执行结束后能取到
                    //advice req 两次GAC之后都能取到
                    //force accept support 是应用传给内核的
                    //signature_req在持卡人认证步骤后能取到
                    // rf_start流程用的是接触的这套参数
                    emvTransInfo.setSignatureReq(emvOpt._signature_req);
                    emvTransInfo.setOnlinePin(emvOpt._online_pin);
                    emvTransInfo.setIssScriptRes(emvOpt._iss_script_res);
                    emvTransInfo.setAdviceReq(emvOpt._advice_req);
                }
            }
            emvTransInfo.setEpOpt(epOpt);
        } else {
            throw new EMVTransferException("unknown mediatype:" + context.getMediaType());
        }
        boolean isGetUnionSpecialTag = context.isGetUnionSpecialTag();
        deviceLogger.debug("[doEmvStep0]:isGetUnionSpecialTag..="+isGetUnionSpecialTag);
        if (emvrslt != EmvConst.EMV_TRANS_RF_SELECT_NEXT_AID) {
            if (null != emvOpt && emvOpt._seq_to == 0 && emvrslt >= 0 && isUnionCard() && isGetUnionSpecialTag) {
                deviceLogger.debug("[doEmvStep0]df71 9f51 9f77:iccget，emvsetstart");
                byte[] temp = new byte[15];
                int[] lentemp = new int[1];
                int rs = emvICCGetDataByTagName(EmvSelfDefinedReference.PBOC_TRANS_STEP, temp, lentemp);
                if (rs == 0) {
                    int length = lentemp[0];
                    byte[] sc = new byte[length];
                    System.arraycopy(temp, 0, sc, 0, length);
                    emvTransInfo.setSecondCurrencyCode(EMVInnerUtils.hexString(sc));
                    setEmvData(EmvSelfDefinedReference.PBOC_TRANS_STEP, sc);

                }
                byte[] temp2 = new byte[15];
                int[] lentemp2 = new int[1];
                int rs2 = emvICCGetDataByTagName(EmvStandardReference.APP_CURRENCY_CODE, temp2, lentemp2);
                if (rs2 == 0) {
                    int length = lentemp2[0];
                    byte[] sc = new byte[length];
                    System.arraycopy(temp2, 0, sc, 0, length);
                    String currencyCode = EMVInnerUtils.unPadLeft(EMVInnerUtils.hexString(sc), '0');
                    emvTransInfo.setAppCurrencyCode(currencyCode);
                    setEmvData(EmvStandardReference.APP_CURRENCY_CODE, sc);
                }
                byte[] temp3 = new byte[15];
                int[] lentemp3 = new int[1];
                int rs3 = emvICCGetDataByTagName(EmvStandardReference.EC_BALANCE_LIMIT, temp3, lentemp3);
                if (rs3 == 0) {
                    int length = lentemp3[0];
                    byte[] sc = new byte[length];
                    System.arraycopy(temp3, 0, sc, 0, length);
                    emvTransInfo.setEc_balance_limit(sc);
                    setEmvData(EmvStandardReference.EC_BALANCE_LIMIT, sc);
                }
                deviceLogger.debug("[doEmvStep0] df71 9f51 9f77:over");
            }
        }
        if (null != emvOpt && emvOpt._seq_to == 1 && emvrslt >= 0 && EMVTransContext._EMV_MEDIATYPE_ICCARD == context.getMediaType() && isUnionCard() && isGetUnionSpecialTag) {
            deviceLogger.debug("[doEmvStep0] 9f79:start");
            byte[] balance = getICCdata(EmvStandardReference.PBOC_CARD_FUNDS);
            if (null != balance) {
                emvTransInfo.setPbocCardFunds(EMVInnerUtils.hexString(balance));
                setEmvData(EmvStandardReference.PBOC_CARD_FUNDS, balance);
                deviceLogger.debug("[doEmvStep0] 9f79:over");
            }
        }

        if (null != emvOpt && (emvOpt._seq_to == 3 || emvOpt._seq_to == 6 || emvOpt._seq_to == 8) && emvrslt >= 0 && EMVTransContext._EMV_MEDIATYPE_ICCARD == context.getMediaType()) {
//            onlinepin 输联机PIN后能取到
//            script res 发卡行脚本执行结束后能取到
//            advice req 两次GAC之后都能取到
//            force accept support 是应用传给内核的
//            signature_req在持卡人认证步骤后能取到
            emvTransInfo.setSignatureReq(emvOpt._signature_req);
            emvTransInfo.setOnlinePin(emvOpt._online_pin);
            emvTransInfo.setIssScriptRes(emvOpt._iss_script_res);
            emvTransInfo.setAdviceReq(emvOpt._advice_req);
        }
        int df75Rslt = AbstractEMVTransController._EMV_RSLT_STEP_SUCCESS;
        if (isEpProcess) {
            deviceLogger.info("[doEmvStep0]epReslutcode:" + emvrslt);
            switch (emvrslt) {
                case 0:// 当前步骤执行成功
                case EMVLevel2Const.EmvExecRslt.EMV_TRANS_PBOC_CONTINUE:
                case EMVLevel2Const.EmvExecRslt.EMV_TRANS_QPBOC_CONTINUE:
                case EMVLevel2Const.EmvExecRslt.EMV_TRANS_MSD_CONTINUE:
                    df75Rslt = AbstractEMVTransController._EMV_RSLT_STEP_SUCCESS;
                    break;
                case EmvConst.EMV_TRANS_RF_MCHIP_ACCEPT:
                case EmvConst.EMV_TRANS_RF_MAG_ACCEPT:
                    df75Rslt = AbstractEMVTransController._EMV_RSLT_TC;
                    if (isIndicatorsAndBeep) {
                        deviceLogger.debug("[doEmvStep0]+ACCEPT led on ");
                        indicatorLightModule.operateLight(new LightColor[]{LightColor.BLUE, LightColor.YELLOW, LightColor.GREEN, LightColor.RED}, LightState.TURNON);
                        try {
                            Thread.sleep(100);//要保持读卡ok常量750ms,ndk emv常量500毫秒左右，所以要延迟下
                            deviceLogger.debug("[doEmvStep0]+online led off ");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case EmvConst.EMV_TRANS_RF_MCHIP_DENIAL:
                case EmvConst.EMV_TRANS_RF_MAG_DENIAL:
                    df75Rslt = AbstractEMVTransController._EMV_RSLT_AAC;
                    if (isIndicatorsAndBeep) {
                        deviceLogger.debug("[doEmvStep0]+_EMV_RSLT_AAC led on ");
                        indicatorLightModule.operateLight(new LightColor[]{LightColor.BLUE, LightColor.YELLOW, LightColor.GREEN, LightColor.RED}, LightState.TURNON);
                        try {
                            Thread.sleep(100);//要保持读卡ok常量750ms,ndk emv常量500毫秒左右，所以要延迟下
                            deviceLogger.debug("[doEmvStep0]+online led off ");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case EmvConst.EMV_TRANS_RF_MCHIP_GOONLINE:
                case EmvConst.EMV_TRANS_RF_MAG_GOONLINE:
                case 22:// rupay EmvConst.EMV_TRANS_RF_MCHIP_GOONLINE_LONGTAP
                case 23://rupay EMV_TRANS_RF_MCHIP_GOONLINE_ONLINETAP
                    df75Rslt = AbstractEMVTransController._EMV_RSLT_ARQC;
                    if (isIndicatorsAndBeep) {
                        try {
                            Thread.sleep(200);//要保持读卡ok常量750ms,ndk emv常量500毫秒左右，所以要延迟下
                            deviceLogger.debug("[doEmvStep0]+online led off ");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        indicatorLightModule.operateLight(new LightColor[]{LightColor.BLUE, LightColor.YELLOW, LightColor.GREEN, LightColor.RED}, LightState.TURNOFF);
                    }
                    break;
                case EMVLevel2Const.EmvExecRslt.EMV_TRANS_FALLBACK:
                case EmvConst.EMV_TRANS_RF_TRYOTHERINT:
                    df75Rslt = AbstractEMVTransController._EMV_RSLT_FALLBACK;
                    if (isIndicatorsAndBeep) {
                        try {
                            deviceLogger.debug("[doEmvStep0]+fallback led off ");
                            indicatorLightModule.operateLight(new LightColor[]{LightColor.BLUE, LightColor.YELLOW, LightColor.GREEN, LightColor.RED}, LightState.TURNOFF);
                            //降级要蜂鸣
                            deviceLogger.error("fallback ,beep");
                            SoundPoolImpl.getInstance(2).play(1, 200, 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case EMVLevel2Const.EmvExecRslt.EMV_TRANS_AMT_LIMITOVER:
                    df75Rslt = AbstractEMVTransController._EMV_TRANS_AMT_LIMITOVER;
                    if (isIndicatorsAndBeep) {
                        deviceLogger.debug("[doEmvStep0]+EMV_TRANS_AMT_LIMITOVER led off ");
                        indicatorLightModule.operateLight(new LightColor[]{LightColor.BLUE, LightColor.YELLOW, LightColor.GREEN, LightColor.RED}, LightState.TURNOFF);
                    }
                    break;
                case EmvConst.EMV_TRANS_RF_TERMINATE:
                default:// 其他返回都当交易失败处理
                    df75Rslt = AbstractEMVTransController._EMV_RSLT_STEP_FAILED;
                    if (isIndicatorsAndBeep) {
                        indicatorLightModule.operateLight(new LightColor[]{LightColor.BLUE, LightColor.YELLOW, LightColor.GREEN, LightColor.RED}, LightState.TURNOFF);
                        deviceLogger.error("[doEmvStep0] failed,beep,errorcode:" + emvcore.jniSDKEPErrorCode() + ";emvrslt:" + emvrslt);
                        SoundPoolImpl.getInstance(2).play(1, 200, 0);
                    }
                    break;
            }
            // ep码转为国内码
            emvrslt = epCodeAdapt(emvrslt);
        } else {
            switch (emvrslt) {
                case EMVLevel2Const.EmvExecRslt.EMV_TRANS_GOON_PBOC2LOG:
//                    if (EMVTransContext._EMV_MEDIATYPE_RFCARD == context.getMediaType()) {
//                        // 通过APDU获取非接电子现金日志
//                        RFCardModule rfCarf = (RFCardModule) getOwner().getStandardModule(ModuleType.RFCARDREADER);
//                        ExtRFCardModule extRFCardModule = (ExtRFCardModule) getOwner().getExModule(ExModuleType.RFCARD);
//                        List<EMVTransLog> logs = new ArrayList<EMVTransLog>();
//                        String count = null;
//                        for (int i = 1; i < 11; i++) {
//                            if (i < 10) {
//                                count = "0" + i;
//                            } else {
//                                count = "0A";
//                            }
//                            String apdu = "00B2" + count + "5C00";
//                            byte req[] = EMVInnerUtils.hex2byte(apdu);
//                            byte transLog[] = null;
//                            if (null != emvExtParams && emvExtParams.isExternalReader()) {
//                                transLog = extRFCardModule.transmit(req);
//                            } else {
//                                transLog = rfCarf.transmit(req, OPERATOR_WAITING_MILLS);
//                            }
//
//                            // deviceLogger.info("read pboc
//                            // log:"+ISOUtils.hexString(transLog));
//                            if (transLog != null && transLog.length == 47) {
//                                logs.add(new EMVTransLog(transLog));
//                            }
//
//                        }
//                        emvTransLogs = logs;
//                    }
                case 0:// 当前步骤执行成功
                case EMVLevel2Const.EmvExecRslt.EMV_TRANS_GOON_ECLOADLOG:
                case EMVLevel2Const.EmvExecRslt.EMV_TRANS_EC_GOON_AMOUNT:
                case EMVLevel2Const.EmvExecRslt.EMV_TRANS_RF_GOON_AMOUNT:
                case EMVLevel2Const.EmvExecRslt.EMV_TRANS_PBOC_CONTINUE:
                case EMVLevel2Const.EmvExecRslt.EMV_TRANS_QPBOC_CONTINUE:
                case EMVLevel2Const.EmvExecRslt.EMV_TRANS_MSD_CONTINUE:
                    df75Rslt = AbstractEMVTransController._EMV_RSLT_STEP_SUCCESS;
                    break;
                case EMVLevel2Const.EmvExecRslt.EMV_TRANS_ACCEPT:
                case EMVLevel2Const.EmvExecRslt.EMV_TRANS_QPBOC_ACCEPT:
                    df75Rslt = AbstractEMVTransController._EMV_RSLT_TC;
                    break;
                case EMVLevel2Const.EmvExecRslt.EMV_TRANS_DENIAL:
                case EMVLevel2Const.EmvExecRslt.EMV_TRANS_QPBOC_DENIAL:
                    df75Rslt = AbstractEMVTransController._EMV_RSLT_AAC;
                    break;
                case EMVLevel2Const.EmvExecRslt.EMV_TRANS_2GAC_AAC:
                    df75Rslt = AbstractEMVTransController._EMV_RSLT_SECOND_AAC;
                    break;
                case EMVLevel2Const.EmvExecRslt.EMV_TRANS_GOONLINE:
                case EMVLevel2Const.EmvExecRslt.EMV_TRANS_QPBOC_GOONLINE:
                case EMVLevel2Const.EmvExecRslt.EMV_TRANS_MSD_GOONLINE:
                    df75Rslt = AbstractEMVTransController._EMV_RSLT_ARQC;
                    break;
                case EMVLevel2Const.EmvExecRslt.EMV_TRANS_FALLBACK:
                    df75Rslt = AbstractEMVTransController._EMV_RSLT_FALLBACK;
                    break;
                case EMVLevel2Const.EmvExecRslt.EMV_TRANS_AMT_LIMITOVER:
                    df75Rslt = AbstractEMVTransController._EMV_TRANS_AMT_LIMITOVER;
                    break;
                case EMVLevel2Const.EmvExecRslt.EMV_TRANS_CANCEL:
                case EMVLevel2Const.EmvExecRslt.EMV_TRANS_TERMINATE:
                default:// 其他返回都当交易失败处理
                    df75Rslt = AbstractEMVTransController._EMV_RSLT_STEP_FAILED;
                    break;
            }
        }
        getEmvContext(EMVTransInfo.class, emvTransInfo, defaultTags);
        deviceLogger.info("[doEmvStep0] EmvJNIService df75Rslt:" + df75Rslt);
        emvTransInfo.setExecuteRslt(df75Rslt);
        deviceLogger.info("[doEmvStep0] EmvJNIService resultCode:" + emvrslt);
        emvTransInfo.setEmvrsltCode(emvrslt);
        if (isEpProcess) {
            int errorCode = emvcore.jniSDKEPErrorCode();
            deviceLogger.debug("[doEmvStep0] EmvJNIService->jniSDKEPErrorCode errorCode=" + errorCode+" ucRestart="+epOpt.ucRestart);
            emvTransInfo.setErrorcode(( df75Rslt == 3) ? 0 : errorCode);

			if(controller.isEPStartB){
                emvTransInfo.setErrorcode((df75Rslt == 0 || df75Rslt == 3) ? 0 : errorCode);
            }
        } else {
            int errorCode = emvcore.jniemvErrorCode();
            deviceLogger.debug("[doEmvStep0] EmvJNIService->jniemvErrorCode errorCode=" + errorCode);
            emvTransInfo.setErrorcode(errorCode);
        }
        deviceLogger.info("[doEmvStep0] EmvJNIService errorCode:" + emvTransInfo.getErrorcode());
        return emvTransInfo;
    }

    private int epCodeAdapt(int epCode) {
        if (epCode == EmvConst.EMV_TRANS_RF_MCHIP_ACCEPT || epCode == EmvConst.EMV_TRANS_RF_MAG_ACCEPT) {
            return EMVLevel2Const.EmvExecRslt.EMV_TRANS_QPBOC_ACCEPT;
        } else if (epCode == EmvConst.EMV_TRANS_RF_MCHIP_DENIAL || epCode == EmvConst.EMV_TRANS_RF_MAG_DENIAL) {
            return EMVLevel2Const.EmvExecRslt.EMV_TRANS_QPBOC_DENIAL;
        } else if (epCode == EmvConst.EMV_TRANS_RF_MCHIP_GOONLINE) {
            return EMVLevel2Const.EmvExecRslt.EMV_TRANS_QPBOC_GOONLINE;
        } else if (epCode == EmvConst.EMV_TRANS_RF_MAG_GOONLINE) {
            return EMVLevel2Const.EmvExecRslt.EMV_TRANS_MSD_GOONLINE;
        } else if (epCode == EmvConst.EMV_TRANS_RF_TRYOTHERINT) {
            return EMVLevel2Const.EmvExecRslt.EMV_TRANS_FALLBACK;
        }
        return epCode;
    }
    private String formatDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
        String dateString = formatter.format(date);
        return dateString;
    }
    private boolean isUnionCard() {
        byte[] cardAid = getEmvData(EmvStandardReference.AID_CARD);
        if (null != cardAid) {
            String aidStr = EMVInnerUtils.hexString(cardAid);
            if (aidStr.substring(0, 10).equalsIgnoreCase("A000000333")) {
                return true;
            }
        }
        return false;
    }

    private boolean isVisaCard() {
        byte[] cardAid = getEmvData(EmvStandardReference.AID_CARD);
        if (null != cardAid) {
            String aidStr = EMVInnerUtils.hexString(cardAid);
            if (aidStr.substring(0, 10).equalsIgnoreCase("A000000003")) {
                return true;
            }
        }
        return false;
    }

    private String getSysProperty(String key, String defaultValue) {
        String value = defaultValue;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method method = c.getMethod("get", String.class);
            value = (String) (method.invoke(c, key));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
    private boolean getHasSecModule(){
        boolean hasSecModule = true;
        if(getSysProperty("persist.sys.HasSecModule","yes").equals("no")){
            hasSecModule = false;
        }
        deviceLogger.debug(">>>hasSecModule="+hasSecModule);
        return hasSecModule;
    }

    @Override
    public byte[] getEmvData(int tag) {
        deviceLogger.debug("[getEmvData] isEpProcess:"+isEpProcess);
        if (tag == 0xDF8116) {
            try {
                deviceLogger.debug("[getEmvData]0xDF8116:from ep_opt");
                byte[] data = new byte[23];
                data[0] = epOpt._UI_message_id;
                data[1] = epOpt._UI_status;
                System.arraycopy(epOpt._UI_hold_time, 0, data, 2, 3);
                data[5] = epOpt._UI_language_len;
                System.arraycopy(epOpt._UI_language_preference, 0, data, 6, 8);
                data[14] = epOpt._UI_value_qualifier;
                System.arraycopy(epOpt._UI_value, 0, data, 15, 6);
                System.arraycopy(epOpt._UI_currency_code, 0, data, 21, 2);
                deviceLogger.debug("[getEmvData]0xDF8116 值：" + (EMVInnerUtils.hexString(data)));
                return data;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        byte[] buffer = new byte[1024];
        int len = -1;
        if (isEpProcess) {
            len = emvcore.jniSDKEPGetData(tag, buffer, buffer.length);
            deviceLogger.debug("[getEmvData] EmvJNIService->jniSDKEPGetData tag=" + String.format("0x%x", tag) + " len=" + len);
        } else {
            len = emvcore.jniemvgetdata(tag, buffer, buffer.length);
            deviceLogger.debug("[getEmvData] EmvJNIService->jniemvgetdata tag=" + String.format("0x%x", tag) + " len=" + len);
        }
        if (len > 0) {
            byte[] value = new byte[len];
            System.arraycopy(buffer, 0, value, 0, len);
            deviceLogger.debug("[getEmvData] tag=" + String.format("0x%x", tag) + " value=" + InnerUtils.hexString(value));
            return value;
        } else {
            if (isEpProcess && tag == 0x5F24) {
                len = emvcore.jniSDKEPGetData(0x57, buffer, buffer.length);
                deviceLogger.debug("[getEmvData] EmvJNIService->jniSDKEPGetData tag=" + 0x57 + " len=" + len);
                if (len > 0) {
                    byte[] track2 = new byte[len];
                    System.arraycopy(buffer, 0, track2, 0, len);
                    deviceLogger.debug("[getEmvData] track2=" + InnerUtils.hexString(track2));

                    String track2Str = InnerUtils.hexString(track2);
                    String expiredDate = track2Str.substring(track2Str.indexOf('D') + 1, track2Str.indexOf('D') + 5);
                    return InnerUtils.hex2byte(expiredDate);
                }
            }
            deviceLogger.error("[getEmvData]failed to get EmvchData!tag:" + tag + ",length:" + len + ",isEpProcess:" + isEpProcess);
            return null;
        }
    }

    /**
     * 刷新emv交易数据
     */
    void refreshEmvTransferInfo(EMVTransInfo emvTransInfo) {
        getEmvContext(EMVTransInfo.class, emvTransInfo, defaultTags);
    }

    <T extends AbstractEMVPackage> T getEmvContext(Class<T> packageContainerClz, T packageContainer, Set<Integer> expectedTags) {
        int[] tagsArr = new int[expectedTags.size()];
        int i = 0;
        for (Iterator<Integer> itor = expectedTags.iterator(); itor.hasNext(); i++) {
            tagsArr[i] = itor.next();
        }
        byte[] buffer = new byte[9216];
        int len = emvFetchData(tagsArr, tagsArr.length, buffer, buffer.length);
        byte[] payload = new byte[len];
        System.arraycopy(buffer, 0, payload, 0, len);
        deviceLogger.debug("[getEmvContext]get emv trans info:" + EMVInnerUtils.hexString(payload));
        return packager.unpack(payload, packageContainerClz, packageContainer);
    }

    private emv_opt getDefaultEmvOpt(EMVTransContext context, TLVPackage tp) {
        emv_opt emvOpt = null;
        if(controller != null && controller.getEMVTransInfo() != null && controller.getEMVTransInfo().getEmvParam() != null){
            emvOpt = controller.getEMVTransInfo().getEmvParam();
        }else {
            emvOpt = new emv_opt();
        }
        deviceLogger.debug("[getDefaultEmvOpt] _online_result="+emvOpt._online_result);
        EMVInnerUtils.toString_emv_opt(deviceLogger,emvOpt);
        byte[] value = tp.getValue(EmvSelfDefinedReference.PBOC_TRANS_STEP);
        if (value == null || value.length <= 0)
            throw new EMVTransferException("[getDefaultEmvOpt]pboc trans step should not be null!");
        emvOpt._seq_to = value[0];
        // TODO，强制联机这里，30应用里有单独针对ec和emv做判定
        value = tp.getValue(EmvSelfDefinedReference.FORCE_ONLINE);
        if (value == null || value.length <= 0) {
            value = new byte[]{EMVTransContext._EMV_PRCO_FORCEONLINE}; // 默认强制联机
        }
        emvOpt._force_online_enable = value[0];
        // setEmvData(EmvSelfDefinedReference.FORCE_ONLINE, value);

        // if (value[0] == EMVTransContext._EMV_PRCO_FORCEONLINE &&
        // EMVTransContext._EMV_MEDIATYPE_RFCARD == context.getMediaType()) {
        // emvcore.jniemvsetdata(EmvStandardReference.TERMINAL_TRANSACTION_QUALIFIERS,
        // new byte[] { 0x36, (byte) 0x80, 0x00, (byte) 0x80 }, 4);
        // } else if (value[0] == EMVTransContext._EMV_PROC_NOT_FORCEONLINE &&
        // EMVTransContext._EMV_MEDIATYPE_RFCARD == context.getMediaType()) {
        // emvcore.jniemvsetdata(EmvStandardReference.TERMINAL_TRANSACTION_QUALIFIERS,
        // new byte[] { 0x36, 0x00, 0x00, (byte) 0x80 }, 4);
        // }

        // 非接强制联机设置成false的时候，9f66默认值要修正为36/00/00/80
        value = tp.getValue(EmvSelfDefinedReference.INNER_TRANSACTION_TYPE);
        if (value == null || value.length <= 0)
            throw new EMVTransferException("[getDefaultEmvOpt]inner transaction type should not be null!");
        emvOpt._trans_type = value[0];// TODO
        // 内部交易类型设置后，有根据交易类型自动选择ec和emv的判断，参考代码实现chinaums,line 4377
        value = tp.getValue(EmvSelfDefinedReference.ACCTSELECTED_INDICATOR);
        if (value == null || value.length <= 0)
            throw new EMVTransferException("[getDefaultEmvOpt]acctselected indicator should not be null!");
        emvOpt._account_type_enable = value[0];
        if (context.getInnerTransactionType() == TransactionType.BALANCE)
            emvOpt._request_amt = 0;//not input amount
        emvOpt._request_amt = 1;//input amount when select application
        return emvOpt;
    }

    private rf_transdata getDefaultRfTransData(EMVTransContext context, TLVPackage tp) {
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
        rfData.usDate = EMVInnerUtils.hex2byte(time);
        return rfData;
    }


    void doEmvFinish0(EMVTransContext context, boolean isSuccess) {
        if (isSuccess) {
            deviceLogger.debug("[doEmvFinish0]doEmvFinish0 suspend");
            if (EMVTransContext._EMV_MEDIATYPE_ICCARD == context.getMediaType()) {
                deviceLogger.debug("[doEmvFinish0] EmvJNIService->jniemvSuspend(0)");
                emvcore.jniemvSuspend(0);
            } else if (EMVTransContext._EMV_MEDIATYPE_RFCARD == context.getMediaType()) {
                deviceLogger.debug("[doEmvFinish0] EmvJNIService->doEntrypointSuspend(0)");
                doEntrypointSuspend(0);
            } else
                throw new EMVTransferException("[doEmvFinish0]unknown mediatype:" + context.getMediaType());
        } else {
            deviceLogger.debug("[doEmvFinish0]doEmvFinish0 suspend");
            if (EMVTransContext._EMV_MEDIATYPE_ICCARD == context.getMediaType()) {
                deviceLogger.debug("[doEmvFinish0] EmvJNIService->jniemvSuspend(1)");
                emvcore.jniemvSuspend(1);
            } else if (EMVTransContext._EMV_MEDIATYPE_RFCARD == context.getMediaType()) {
                deviceLogger.debug("[doEmvFinish0] EmvJNIService->doEntrypointSuspend(1)");
                doEntrypointSuspend(1);
                if (!context.isUseExtCardReader()) {
                    RFCardModule rfCarf = (RFCardModule) getOwner().getStandardModule(ModuleType.RFCARDREADER);
                    rfCarf.powerOff();
                    deviceLogger.debug("[doEmvFinish0] EmvJNIService->powerOff()");
                }
            } else
                throw new EMVTransferException("unknown mediatype:" + context.getMediaType());
        }
    }

    // TODO 暂时不实现emvTransfer复制的功能
    EMVTransInfo doSecondIssurance0(EMVTransContext context, OnlineTransactionData request, EMVTransInfo emvTransInfo) {
        boolean isRupay = (emvTransInfo.getKernelId() == EmvConst.KERNEL_ID_RUPAY ? true : false); //rupay  22联机码可入
        emv_opt emvOpt = null;
        if(controller != null && controller.getEMVTransInfo().getEmvParam() !=null){
            emvOpt = controller.getEMVTransInfo().getEmvParam();
        }else {
            emvOpt = new emv_opt();
        }
        deviceLogger.debug("[doSecondIssurance0] emvOpt._online_result="+emvOpt._online_result);
        ep_opt entrypoint = new ep_opt();
        // public byte[] _auth_resp_code = new byte[2]; //in, 8A from the host
        // public byte[] _field55 = new byte[256]; //< in, field55 or tlv
        // decoded data from the host
        // public int _field55_len;
        // public int _online_result; /**< in, the online result */
        // 设置内部交易码
        TLVPackage tp0 = EMVInnerUtils.newTlvPackage();
        tp0.unpack(packager.pack(context));
        byte[] innerType = tp0.getValue(EmvSelfDefinedReference.INNER_TRANSACTION_TYPE);
        if (innerType == null || innerType.length <= 0)
            throw new EMVTransferException("inner transaction type should not be null!");
        emvOpt._trans_type = innerType[0];
        entrypoint.ucTransType = innerType[0];
        emvOpt._seq_to = AbstractEMVTransController._EMV_PROC_CONTINUE;

        byte[] reqpayload = packager.pack(request);
        TLVPackage tp = EMVInnerUtils.newTlvPackage();
        tp.unpack(packager.pack(request));
        byte[] value = tp.getValue(EmvStandardReference.AUTHORISATION_RESPONSE_CODE); // 联机响应码
        if (value == null) {
            byte[] authData = tp.getValue(EmvStandardReference.ISSUER_AUTHENTICATION_DATA);
            if (null == authData && null != request.getTlvData()) {
                TLVPackage field55Tlv = InnerUtils.newTlvPackage();
                field55Tlv.unpack(request.getTlvData());
                authData = field55Tlv.getValue(EmvStandardReference.ISSUER_AUTHENTICATION_DATA);
            }
            if (authData != null) {
                value = Arrays.copyOfRange(authData, authData.length - 2, authData.length);
            }
        }
        String respCode = null;
        if (value != null && value.length >= 2) {
            respCode = new String(value, 0, 2);
            emvOpt._auth_resp_code = respCode.getBytes();
            entrypoint.pusAuthRespCode = respCode.getBytes();
        }
        if(controller != null && controller.getEMVTransInfo().getEmvParam() == null){
            if (null == respCode) {
                emvOpt._online_result = EMVLevel2Const.EmvExecRslt.EMV_TRANS_ONLINEFAIL;
                entrypoint.nOnlineResult = EMVLevel2Const.EmvExecRslt.EMV_TRANS_ONLINEFAIL;
                deviceLogger.error("[doSecondIssurance0]import online Result,authcode is null!");
            } else if ("00".equals(respCode)) {
                emvOpt._online_result = EMVLevel2Const.EmvExecRslt.EMV_TRANS_ONLINESUCC_ACCEPT;
                entrypoint.nOnlineResult = EMVLevel2Const.EmvExecRslt.EMV_TRANS_ONLINESUCC_ACCEPT;
            } else if ("01".equals(respCode)) {
                emvOpt._online_result = EMVLevel2Const.EmvExecRslt.EMV_TRANS_ONLINESUCC_ISSREF;
                entrypoint.nOnlineResult = EMVLevel2Const.EmvExecRslt.EMV_TRANS_ONLINESUCC_ISSREF;
            } else {
                emvOpt._online_result = EMVLevel2Const.EmvExecRslt.EMV_TRANS_ONLINESUCC_DENIAL;
                entrypoint.nOnlineResult = EMVLevel2Const.EmvExecRslt.EMV_TRANS_ONLINESUCC_DENIAL;
            }
        }
        byte[] field55 = request.getTlvData();
        if (null != field55 && field55.length != 0) {
            byte[] authCode = tp.getValue(EmvStandardReference.AUTHORISATION_CODE);
            if (null != authCode && authCode.length > 0) {
                TLVPackage field55Tlv = EMVInnerUtils.newTlvPackage();
                field55Tlv.unpack(field55);
                field55Tlv.append(EmvStandardReference.AUTHORISATION_CODE, authCode);
                field55 = field55Tlv.pack();
            }
            reqpayload = field55;
        }
        deviceLogger.debug("[doSecondIssurance0]field55 data:" + EMVInnerUtils.hexString(reqpayload));
        emvOpt._field55 = reqpayload;
        emvOpt._field55_len = reqpayload.length;

        entrypoint.pusField55 = reqpayload;
        entrypoint.nField55Len = reqpayload.length;


        int emvrslt = -1;
        if (EMVTransContext._EMV_MEDIATYPE_ICCARD == context.getMediaType()) {
            deviceLogger.debug("[doSecondIssurance0] EmvJNIService->jniemvStart EmvCoreStep=" + getEmvCoreStep(emvOpt._seq_to));
            EMVInnerUtils.toString_emv_opt(deviceLogger, emvOpt);
            emvrslt = emvcore.jniemvStart(emvOpt);
            deviceLogger.debug("[doSecondIssurance0] EmvJNIService->jniemvStart emvrslt=" + emvrslt);
        } else if (EMVTransContext._EMV_MEDIATYPE_RFCARD == context.getMediaType()) {
            if (isRupay) {
                emvTransInfo.setEntryPointType(EntryPointType.RUPAY);
                deviceLogger.debug("[doSecondIssurance0] EmvJNIService->jniSDKRupayProcess");
                EMVInnerUtils.toString_ep_opt(deviceLogger, entrypoint);
                EMVInnerUtils.toString_rf_transdata(deviceLogger, context.getRfTransData());
                emvrslt = emvcore.jniSDKRupayProcess(entrypoint, context.getRfTransData());
                deviceLogger.debug("[doSecondIssurance0] EmvJNIService->jniSDKRupayProcess emvrslt=" + emvrslt);
                context.setEpOpt(entrypoint);
            } else {
                long amt = Long.valueOf(context.getAmountAuthorisedNumeric());
                deviceLogger.debug("[doSecondIssurance0] amt=" + amt);
                EMVInnerUtils.toString_emv_opt(deviceLogger, emvOpt);
                emvrslt = emvcore.jniemvrfstart(emvOpt, amt);
                deviceLogger.debug("[doSecondIssurance0] EmvJNIService->jniemvrfstart emvrslt=" + emvrslt);
            }
        } else
            throw new EMVTransferException("unknown mediatype:" + context.getMediaType());
        emvTransInfo.setEmvrsltCode(emvrslt);
        int errorCode = emvcore.jniemvErrorCode();
        if (isEpProcess)
            errorCode = emvcore.jniSDKEPErrorCode();
        int df75Rslt = AbstractEMVTransController._EMV_RSLT_STEP_SUCCESS;
        deviceLogger.debug("[doSecondIssurance0] errorCode:" + errorCode);
        deviceLogger.debug("[doSecondIssurance0] reslutcode:" + emvrslt);
        switch (emvrslt) {
            case 0:// 当前步骤执行成功
                df75Rslt = AbstractEMVTransController._EMV_RSLT_STEP_SUCCESS;
                break;
            case EMVLevel2Const.EmvExecRslt.EMV_TRANS_ACCEPT:
                df75Rslt = AbstractEMVTransController._EMV_RSLT_TC;
                break;
            case EMVLevel2Const.EmvExecRslt.EMV_TRANS_DENIAL:
                df75Rslt = AbstractEMVTransController._EMV_RSLT_AAC;
                break;
            case EMVLevel2Const.EmvExecRslt.EMV_TRANS_2GAC_AAC:
                df75Rslt = AbstractEMVTransController._EMV_RSLT_SECOND_AAC;
                break;
            case EMVLevel2Const.EmvExecRslt.EMV_TRANS_GOONLINE:
                getEmvContext(EMVTransInfo.class, emvTransInfo, defaultTags);
                df75Rslt = AbstractEMVTransController._EMV_RSLT_ARQC;
                break;
            case EMVLevel2Const.EmvExecRslt.EMV_TRANS_FALLBACK:
                df75Rslt = AbstractEMVTransController._EMV_RSLT_FALLBACK;
                break;
            case EMVLevel2Const.EmvExecRslt.EMV_TRANS_TERMINATE:
                if (errorCode == AbstractEMVTransController._EMV_TRANS_AMT_LIMITOVER) {
                    df75Rslt = AbstractEMVTransController._EMV_TRANS_AMT_LIMITOVER;
                    break;
                }
            case EMVLevel2Const.EmvExecRslt.EMV_TRANS_CANCEL:
            default:// 其他返回都当交易失败处理
                df75Rslt = AbstractEMVTransController._EMV_RSLT_STEP_FAILED;
                break;
        }
        getEmvContext(EMVTransInfo.class, emvTransInfo, defaultTags);
        deviceLogger.info("[doSecondIssurance0] df75Rslt2:" + df75Rslt);
        emvTransInfo.setExecuteRslt(df75Rslt);
        emvTransInfo.setErrorcode(errorCode);
        deviceLogger.debug("[doSecondIssurance0] EmvJNIService-> ExecuteRslt=" + emvTransInfo.getExecuteRslt());
        deviceLogger.debug("[doSecondIssurance0] EmvJNIService-> EmvrsltCode=" + emvTransInfo.getEmvrsltCode());
        deviceLogger.debug("[doSecondIssurance0] EmvJNIService-> Errorcode=" + emvTransInfo.getErrorcode());
        return emvTransInfo;
    }

    void doEntrypointSuspend(int flag) {
        deviceLogger.debug("[doEntrypointSuspend] flag:" + flag);
        deviceLogger.debug("[doEntrypointSuspend] EmvJNIService->xxxSuspend(" + flag + ")");
        emvcore.jniSDKEntryPointSuspend(flag);
        emvcore.jniSDKQpbocSuspend(flag);
        emvcore.jniSDKPayPassSuspend(flag);
        emvcore.jniSDKPayWaveSuspend(flag);
        emvcore.jniSDKExpressPaySuspend(flag);
        emvcore.jniSDKDiscoverPaySuspend(flag);
        emvcore.jniemvrfsuspend(flag);
        emvcore.jniSDKInteracSuspend(flag);
        emvcore.jniSDKPureSuspend(flag);
        emvcore.jniSDKRupaySuspend(flag);
        emvcore.jniSDKJCBSuspend(flag);
        //emvcore.jniSDKGirocardSuspend(flag);//Girocard
    }

    private void syncAidParam() {
        emvcore.jniemvbuildAidList();
        deviceLogger.debug("[syncAidParam] EmvJNIService->jniemvbuildAidList");
        emvcore.jniSDKEntryPointBuildAIDList();
        deviceLogger.debug("[syncAidParam] EmvJNIService->jniSDKEntryPointBuildAIDList");
    }

    @Override
    public EMVCardInfo getCardInformation() {
        GetAcctInfoListener listener = new GetAcctInfoListener();
        EMVTransController transController = getDefaultEmvTransferControllerForQueryAccount(listener);
        transController.startEMV(TransactionType.SIMPLE, new BigDecimal(0), false, null);
        try {
            listener.waitForRslt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return listener.cardInfo;
    }

    protected EMVTransController getDefaultEmvTransferControllerForQueryAccount(EMVControllerListener listener) {
        EMVLevel2TransferController controller = new EMVLevel2TransferController(this.getOwner(), emvExtParams, listener, EMVTransStep.defaultQuerySteps());
        controller.contextHelper.init(controller, context);
        // TODO
        controller.contextHelper.setDefaultModuleType(ModuleType.ICCARDREADER);
        defaultEmvOperator.setTransferController(controller);

        return controller;
    }

    private class GetAcctInfoListener implements EMVControllerListener {

        private Throwable e;

        private EMVCardInfo cardInfo;

        private boolean isSuccess = false;

        private Object invokeSync = new Object();
        private int yourChoice = 0;

        public GetAcctInfoListener() {
        }

        void waitForRslt() throws InterruptedException {
            synchronized (invokeSync) {
                invokeSync.wait(EMV_INVOKE_DEFAULT_TIMEOUT);
            }
        }

        @Override
        public void onRequestSelectApplication(final EMVTransController controller, List<AIDEntity> aidEntityList, int times) {
            deviceLogger.debug("[GetAcctInfoListener]onRequestSelectApplication");
            if (aidEntityList != null && aidEntityList.size() > 1) {
                String[] items = new String[aidEntityList.size()];
                int i = 0;
                for (AIDEntity entry : aidEntityList) {
                    items[i] = entry.getName();
                    i = i + 1;
                }
                Looper.prepare();
                AlertDialog.Builder singleChoiceDialog = new AlertDialog.Builder(context);
                singleChoiceDialog.setTitle("Select Applications");
                singleChoiceDialog.setSingleChoiceItems(items, 0,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                yourChoice = which;
                            }
                        });
                singleChoiceDialog.setPositiveButton("ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                singleDialog.dismiss();
                                try {
                                    deviceLogger.debug("onRequestSelectApplication yourChoice:" + yourChoice);
                                    controller.setSelectedApplication(yourChoice);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    controller.setSelectedApplication(-1);
                                }
                            }
                        });
                singleChoiceDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        singleDialog.dismiss();
                        try {
                            deviceLogger.error("cancel Select Application");
                            controller.setSelectedApplication(-1);
                        } catch (Exception e) {
                            e.printStackTrace();
                            controller.setSelectedApplication(-1);
                        }
                    }
                });

                singleDialog = singleChoiceDialog.create();
                singleDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                singleDialog.setCancelable(false);
                singleDialog.setCanceledOnTouchOutside(false);
                singleDialog.show();
                Looper.loop();
            } else {
                controller.setSelectedApplication(0);
            }

        }

        @Override
        public void onRequestConfirmCardInfo(EMVTransController controller) {
            deviceLogger.debug("[GetAcctInfoListener]onRequestConfirmCardInfo");
            OnlineTransactionData request = new OnlineTransactionData();
            request.setAuthorisationResponseCode("00");
            controller.completeEMVProcess(request);
        }

        @Override
        public void onRequestInputAmount(EMVTransController controller) {
            controller.setTransactionAmount(new BigDecimal(0));
        }

        @Override
        public void onRequestInputPIN(EMVTransController controller, boolean requireOnline, PINEntity pinEntity) {

        }

        @Override
        public void onRequestOnlineProcess(EMVTransController controller) {
            deviceLogger.debug("[GetAcctInfoListener]onRequestOnlineProcess");
            OnlineTransactionData request = new OnlineTransactionData();
            request.setAuthorisationResponseCode("00");
            controller.completeEMVProcess(request);
        }

        @Override
        public void onEmvFinished(boolean isSuccess, EMVTransController controller) {
            deviceLogger.debug("[GetAcctInfoListener]onEmvFinished,isSuccess:" + isSuccess);
            synchronized (invokeSync) {
                if (singleDialog != null && singleDialog.isShowing()) {
                    singleDialog.dismiss();
                }
                this.isSuccess = isSuccess;
                EMVTransInfo emvTransInfo = controller.getEMVTransInfo();
                String expiredDate = emvTransInfo.getCardExpirationDate();
                if (expiredDate != null && expiredDate.length() >= 4) {
                    expiredDate = expiredDate.substring(0, 4);
                }
                String cardSN = emvTransInfo.getCardSequenceNumber();
                if (null != cardSN) {
                    cardSN = EMVInnerUtils.padleft(cardSN, 3, '0');
                }
                byte[] track2 = emvTransInfo.getTrack_2_eqv_data();
                String track2Str = null;
                String serviceCode = null;
                if (null != track2) {
                    track2Str = EMVInnerUtils.hexString(track2);
                    serviceCode = track2Str.substring(track2Str.indexOf('D') + 5, track2Str.indexOf('D') + 8);
                    if (null == expiredDate) {
                        expiredDate = track2Str.substring(track2Str.indexOf('D') + 1, track2Str.indexOf('D') + 5);
                    }
                }
                byte[] interface_device_serial_number = getEmvData(0x9F1E);
                this.cardInfo = new EMVCardInfo(emvTransInfo.getCardNo(), (interface_device_serial_number == null ? null : EMVInnerUtils.hexString(interface_device_serial_number)), cardSN, expiredDate, emvTransInfo.getQpbocCardFunds(), emvTransInfo.getPbocCardFunds(), serviceCode, track2Str, emvTransInfo.getExecuteRslt(), emvTransInfo.getErrorcode());
                invokeSync.notify();
            }
        }

        @Override
        public void onFallback(EMVTransController controller) {
            synchronized (invokeSync) {
                this.e = new EMVTransferException("transfer to fallback");
                invokeSync.notify();
            }
        }

        @Override
        public void onError(EMVTransController controller, Exception e) {
            synchronized (invokeSync) {
                this.e = e;
                invokeSync.notify();
            }
        }

        @Override
        public void onRequestSelectAccountType(EMVTransController controller, AccountType[] accountTypes) {
            controller.setSelectedAccountType(AccountType.DEFAULT);
        }

        @Override
        public void onRequestConfirmID(EMVTransController controller, IDCardType cardType, String IDNo) {
            controller.confirmID(true);
        }

        @Override
        public void onRequestConfirmEC(EMVTransController controller) {
            controller.confirmEC(true);
        }

        @Override
        public void onRequestShowMessage(EMVTransController controller, String title, String msg, boolean isConfirm, int timeOut) {
            controller.confirmMessage(isConfirm);
        }

        @Override
        public void onRequestSelectLanguage(EMVTransController controller, String[] language) {
            if (language != null && language.length > 0) {
                controller.setSelectedLanguage(language[0]);
            } else {
                controller.cancelEMVProcess();
            }
        }

        @Override
        public void onRequestConfirmFinalAppSelection(EMVTransController controller) {
            controller.confirmInformation(true);
        }

    }

    @Override
    public boolean setTerminalConfiguration(byte[] tlvData, CardInterface aidStorageMode) {
        deviceLogger.debug("[setTerminalConfiguration]aidStorageMode:" + aidStorageMode);
        TLVPackage tp = EMVInnerUtils.newTlvPackage();
        tp.unpack(tlvData);
        emvparam params = new emvparam();
        int rslt = operAID(aidStorageMode, params, EMVLevel2Const.AIDOperatorModel.AID_CONFIG_R);
        if (rslt != 0)
            deviceLogger.debug("[setTerminalConfiguration]default terminal configuration is not exist!" + rslt);

        //9F06 默认aid 16字节0x00
        System.arraycopy(new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}, 0, params._aid, 0, 16);
        params._aid_len = 0x10;
        //DF24 ICS
        byte[] value = tp.getValue(EmvSelfDefinedReference.ICS);
        if (value != null) {
            System.arraycopy(value, 0, params._ics, 0, Math.min(value.length, params._ics.length));
        }
        //5f2a Transaction Currency Code
        value = tp.getValue(EmvStandardReference.TRANSACTION_CURRENCY_CODE);
        if (value != null) {
            System.arraycopy(value, 0, params._trans_curr_code, 0, Math.min(value.length, params._trans_curr_code.length));
        }
        //DF22 unuseful right now
        //	9f7a<!-- EC Terminal Support Indicator -->
        value = tp.getValue(EmvSelfDefinedReference.EC_SUPPORT_INDICATOR);
        if (value != null) {
            params._ec_indicator = value[0];
            // System.arraycopy(value, 0, params._ec_indicator, 0, 1);
        }
        //9F35<!-- Terminal Type -->
        value = tp.getValue(EmvStandardReference.TERMINAL_TYPE);
        if (value != null && value.length > 0)
            params._type = value[0];
        //DF01<!-- Application Selection Indicator -->
        value = tp.getValue(EmvSelfDefinedReference.APP_SELECT_INDICATOR);
        if (value != null && value.length > 0)
            params._app_sel_indicator = value[0];
        //9F33
        value = tp.getValue(EmvStandardReference.TERMINAL_CAPABILITIES);
        if (value != null) {
            System.arraycopy(value, 0, params._cap, 0, Math.min(value.length, params._cap.length));
        }
        //9F40
        value = tp.getValue(EmvStandardReference.ADDITIONAL_TERMINAL_CAPABILITIES);
        if (value != null) {
            System.arraycopy(value, 0, params._add_cap, 0, Math.min(value.length, params._add_cap.length));
        }
        //9F01
        value = tp.getValue(EmvStandardReference.ACQUIRER_IDENTIFIER);
        if (value != null) {
            System.arraycopy(value, 0, params._acq_id, 0, Math.min(value.length, params._acq_id.length));
        }
        //9F15
        value = tp.getValue(EmvStandardReference.MERCHANT_CATEGORY_CODE);
        if (value != null) {
            System.arraycopy(value, 0, params._mer_category_code, 0, Math.min(value.length, params._mer_category_code.length));
        }
        //9f16
        value = tp.getValue(EmvStandardReference.MERCHANT_IDENTIFIER);
        if (value != null) {
            System.arraycopy(value, 0, params._merchant_id, 0, Math.min(value.length, params._merchant_id.length));
        }
        //9F3C
        value = tp.getValue(EmvStandardReference.TRANSACTION_REFERENCE_CURRENCY_CODE);
        if (value != null) {
            System.arraycopy(value, 0, params._trans_ref_curr_code, 0, Math.min(value.length, params._trans_ref_curr_code.length));
        }
        //5f36
        value = tp.getValue(EmvStandardReference.TRANSACTION_CURRENCY_EXP);
        if (value != null && value.length > 0)
            params._trans_curr_exp = value[0];

        value = tp.getValue(EmvStandardReference.POINT_OF_SERVICE_ENTRY_MODE);
        if (value != null && value.length > 0)
            params._pos_entry = value[0];
        //9f3d
        value = tp.getValue(EmvStandardReference.TRANSACTION_REFERENCE_CURRENCY_EXP);
        if (value != null && value.length > 0)
            params._trans_ref_curr_exp = value[0];
        //0x9f1a
        value = tp.getValue(EmvStandardReference.TERMINAL_COUNTRY_CODE);
        if (value != null) {
            System.arraycopy(value, 0, params._term_country_code, 0, Math.min(value.length, params._term_country_code.length));
        }
        //IFD serial number (0x9f1e)
        value = tp.getValue(EmvStandardReference.INTERFACE_DEVICE_SERIAL_NUMBER);
        if (value != null) {
            System.arraycopy(value, 0, params._ifd_serial_num, 0, Math.min(value.length, params._ifd_serial_num.length));
        }
        // Terminal identification number (0x9f1c)
        value = tp.getValue(EmvStandardReference.TERMINAL_IDENTIFICATION);
        if (value != null) {
            System.arraycopy(value, 0, params._terminal_id, 0, Math.min(value.length, params._terminal_id.length));
        }
        //9F7B
        value = tp.getValue(EmvSelfDefinedReference.EC_TRANS_LIMIT);
        if (value != null) {
            System.arraycopy(value, 0, params._ec_limit, 0, Math.min(value.length, params._ec_limit.length));
        }
        //DF16
        value = tp.getValue(EmvSelfDefinedReference.MAX_TARGET_PERCENTAGE_FOR_BIASED_RANDOM_SELECTION);
        if (value != null && value.length > 0) {
            String str = EMVInnerUtils.bcd2str(value, 0, value.length * 2, true);// 期待长度为目标解析长度的2倍(定长)
            int randomValue = Integer.valueOf(EMVInnerUtils.unPadRight(str, 'F')).intValue();// 去掉右边的F
            params._max_target_percent = (byte) (randomValue);
        }
        //DF17
        value = tp.getValue(EmvSelfDefinedReference.TARGET_PERCENTAGE_FOR_RANDOM_SELECTION);
        if (value != null && value.length > 0) {
            String str = EMVInnerUtils.bcd2str(value, 0, value.length * 2, true);
            int randomValue = Integer.valueOf(EMVInnerUtils.unPadRight(str, 'F')).intValue();
            params._target_percent = (byte) (randomValue);
        }
        //DF15
        value = tp.getValue(EmvSelfDefinedReference.THRESHOLD_VALUE_FOR_BIASED_RANDOM_SELECTION);
        if (value != null) {
            System.arraycopy(value, 0, params._threshold_value, 0, Math.min(value.length, params._threshold_value.length));
        }
        //9F1B
        value = tp.getValue(EmvStandardReference.TERMINAL_FLOOR_LIMIT);
        if (value != null) {
            System.arraycopy(value, 0, params._floorlimit, 0, Math.min(value.length, params._floorlimit.length));
        }
        //DF44
        value = tp.getValue(EmvSelfDefinedReference.DEFAULT_DDOL);
        if (value != null) {
            System.arraycopy(value, 0, params._default_ddol, 0, Math.min(value.length, params._default_ddol.length));
            params._default_ddol_len = (byte) Math.min(value.length, params._default_ddol.length);
        }
        //DF45
        value = tp.getValue(EmvSelfDefinedReference.DEFAULT_TDOL);
        if (value != null) {
            System.arraycopy(value, 0, params._default_tdol, 0, Math.min(value.length, params._default_tdol.length));
            params._default_tdol_len = (byte) Math.min(value.length, params._default_tdol.length);
        }
        //9F09
        value = tp.getValue(EmvStandardReference.APP_VERSION_NUMBER_TERMINAL);
        if (value != null) {
            System.arraycopy(value, 0, params._app_ver, 0, Math.min(value.length, params._app_ver.length));
        }
        //DF27
        value = tp.getValue(EmvSelfDefinedReference.LIMIT_EXIST);
        if (value != null) {
            params._limit_exist = value[0];
            //System.arraycopy(value, 0, params._limit_exist, 0, 1);
        }
        //DF20
        value = tp.getValue(EmvSelfDefinedReference.NCICC_TRANS_LIMIT);
        if (value != null) {
            System.arraycopy(value, 0, params._cl_limit, 0, Math.min(value.length, params._cl_limit.length));
        }
        //DF19
        value = tp.getValue(EmvSelfDefinedReference.NCICC_OFFLINE_FLOOR_LIMIT);
        if (value != null) {
            System.arraycopy(value, 0, params._cl_offline_limit, 0, Math.min(value.length, params._cl_offline_limit.length));
        }
        //DF21
        value = tp.getValue(EmvSelfDefinedReference.NCICC_CVM_LIMIT);
        if (value != null) {
            System.arraycopy(value, 0, params._cvm_limit, 0, Math.min(value.length, params._cvm_limit.length));
        }
        //DF3A
        value = tp.getValue(EmvSelfDefinedReference.ZEROALLOW);
        if (value != null) {
            params.ZeroAmountAllow = value[0];
        } else {
            params.ZeroAmountAllow = 0x01;
        }
        //DF39
        value = tp.getValue(EmvSelfDefinedReference.STATUSCHECK);
        if (value != null) {
            params.StatusCheckSupport = value[0];
        }
        //1F8102<!-- Select by AID supported -->是否支持逐条列表选择法，在libemvjni中不支持该方法。

        // 9F4E
        value = tp.getValue(EmvStandardReference.MERCHANT_NAME_AND_LOCATION);
        params._merchant_name = new byte[20];
        if (value != null) {
            System.arraycopy(value, 0, params._merchant_name, 0, value.length);
        }
        //DF40
        value = tp.getValue(EmvSelfDefinedReference.FALLBACK_POSENTRY);
        if (value != null && value.length > 0)
            params._fallback_posentry = value[0];
        //9F66
        value = tp.getValue(EmvStandardReference.TERMINAL_TRANSACTION_QUALIFIERS);
        if (value != null && value.length > 0) {
            System.arraycopy(value, 0, params._trans_prop, 0, Math.min(value.length, params._trans_prop.length));
        } else {
            params._trans_prop = new byte[]{0x36, 0x00, 0x00, (byte) 0x80};
        }

        value = tp.getValue(EmvSelfDefinedReference.EMVSELECTKERNEL);
        if (value != null && value.length > 0) {
            params._status = value[0];
        }

        deviceLogger.debug("[setTerminalConfiguration] AID_CONFIG_W .--------params._limit_exist:" + params._limit_exist);
        int nRet = operAID(aidStorageMode, params, EMVLevel2Const.AIDOperatorModel.AID_CONFIG_W);
        if (nRet != 0) {
            deviceLogger.error("[setTerminalConfiguration]failed to update trmnl params!" + rslt);
        } else {
            syncAidParam();
        }

        return nRet == 0;
    }

    @Override
    public boolean loadConfigurationFromXML(String fileName) {
        EMVParseUtil emvParseUtil = new EMVParseUtil();
        return emvParseUtil.initializeEMVXml(context, fileName, this, false);
    }

    private int operAID(CardInterface aidStorageMode, emvparam params, int operType) {
        deviceLogger.debug("[operAID] EmvJNIService->AID operType{RMV(0x01)|UPT(0x02)|GET(0x10)|CONFIG_R(0x20)|CONFIG_W(0x40)|CLR(0x80)|RESET(0x04)}=" + String.format("0x%x", operType));
        int nRet;
        if (aidStorageMode == CardInterface.CONTACT && EMVInnerUtils.isSDK3()) {
            if (operType == EMVLevel2Const.AIDOperatorModel.AID_UPT ||
                    operType == EMVLevel2Const.AIDOperatorModel.AID_CONFIG_W) {
                EMVInnerUtils.toString_emvparam(deviceLogger, params);
            }
            nRet = emvcore.jniemvOperAID(params, operType);

            if (operType == EMVLevel2Const.AIDOperatorModel.AID_GET ||
                    operType == EMVLevel2Const.AIDOperatorModel.AID_CONFIG_R) {
                EMVInnerUtils.toString_emvparam(deviceLogger, params);
            }
            deviceLogger.debug("[operAID] EmvJNIService->jniemvOperAID nRet=" + nRet);
        } else {
            if (operType == EMVLevel2Const.AIDOperatorModel.AID_UPT ||
                    operType == EMVLevel2Const.AIDOperatorModel.AID_CONFIG_W) {
                EMVInnerUtils.toString_emvparam(deviceLogger, params);
            }
            nRet = emvcore.jniSDKEPOperAID(params, operType);
            if (operType == EMVLevel2Const.AIDOperatorModel.AID_GET ||
                    operType == EMVLevel2Const.AIDOperatorModel.AID_CONFIG_R) {
                EMVInnerUtils.toString_emvparam(deviceLogger, params);
            }
            deviceLogger.debug("[operAID] EmvJNIService->jniSDKEPOperAID nRet=" + nRet);
        }
        return nRet;
    }

    private int getKernelID(byte[] aid) {
        int kernelID = -1;
        if (null == aid || aid.length < 5)
            return kernelID;
        byte[] adaptAid = new byte[5];
        System.arraycopy(aid, 0, adaptAid, 0, 5);


        if (Arrays.equals(adaptAid, new byte[]{(byte) 0xA0, 0x00, 0x00, 0x00, 0x03})) {
            kernelID = EmvConst.KERNEL_ID_PAYWAVE;//visa
        } else if (Arrays.equals(adaptAid, new byte[]{(byte) 0xA0, 0x00, 0x00, 0x00, 0x04})||
                Arrays.equals(adaptAid, new byte[]{(byte) 0xA0, 0x00, 0x00, 0x08, (byte) 0x84})) {//A0 00 00 08 84 1010
            kernelID = EmvConst.KERNEL_ID_PAYPASS;//master
        } else if (Arrays.equals(adaptAid, new byte[]{(byte) 0xA0, 0x00, 0x00, 0x00, 0x65})) {
            kernelID = EmvConst.KERNEL_ID_JCB; // JCB
        } else if (Arrays.equals(adaptAid, new byte[]{(byte) 0xA0, 0x00, 0x00, 0x00, 0x25})) {
            System.out.println("-----aid:" + InnerUtils.hexString(aid));
            kernelID = EmvConst.KERNEL_ID_EXPRESSPAY; // expresspay
        } else if (Arrays.equals(adaptAid, new byte[]{(byte) 0xA0, 0x00, 0x00, 0x03, 0x33}) || Arrays.equals(adaptAid, new byte[]{(byte) 0xA0, 0x00, 0x00, 0x07, (byte) 0x90}) || Arrays.equals(adaptAid, new byte[]{(byte) 0xA0, 0x00, 0x00, 0x02, (byte) 0x41}) || Arrays.equals(adaptAid, new byte[]{(byte) 0xA0, 0x00, 0x00, 0x00, (byte) 0x10})) {
            System.out.println("-----aid:" + InnerUtils.hexString(aid));
            kernelID = EmvConst.KERNEL_ID_UNIONPAY; // 银联
        } else if (Arrays.equals(adaptAid, new byte[]{(byte) 0xA0, 0x00, 0x00, 0x01, 0x52}) || Arrays.equals(adaptAid, new byte[]{(byte) 0xA0, 0x00, 0x00, 0x03, 0x24}) || Arrays.equals(adaptAid, new byte[]{(byte) 0xA0, 0x00, 0x00, 0x06, 0x72})) {
            kernelID = EmvConst.KERNEL_ID_DISCOVER; // discover
        } else if (Arrays.equals(adaptAid, new byte[]{(byte) 0xA0, 0x00, 0x00, 0x06, 0x15})) {
            kernelID = EmvConst.KERNEL_ID_MCCS; // mccs A000000615
        } else if (Arrays.equals(adaptAid, new byte[]{(byte) 0xA0, 0x00, 0x00, 0x02, 0x77})) {
            kernelID = EmvConst.KERNEL_ID_INTERAC; // INTERAC
        } else if (Arrays.equals(adaptAid, new byte[]{(byte) 0xA0, 0x00, 0x00, 0x05, 0x24})) {
            kernelID = EmvConst.KERNEL_ID_RUPAY;
        } else if (Arrays.equals(adaptAid, new byte[]{(byte) 0xA0, 0x00, 0x00, 0x02, 0x28})) {
            if (aid.length >= 7) {
                byte[] aid_pix = new byte[2];
                System.arraycopy(aid, 5, aid_pix, 0, 2);
                if (Arrays.equals(aid_pix, new byte[]{0x10, 0x10})) {
                    kernelID = EmvConst.KERNEL_ID_MADA;//
                } else if (Arrays.equals(aid_pix, new byte[]{0x20, 0x10})) {
                    kernelID = EmvConst.KERNEL_ID_PAYWAVE;//
                }
            }
        } else if(Arrays.equals(adaptAid,new byte[]{(byte) 0xA0,0x00,0x00,0x08,0x03})){//A000000803 40001
            kernelID = EMVLevel2Const.KERNEL_ID_IRAN_KAHROBA;
        } else if(Arrays.equals(adaptAid,new byte[]{(byte) 0xA0,0x00,0x00,0x03,0x59})){//Girocard
            deviceLogger.debug("KERNEL_ID_GIRO");
            kernelID = EMVLevel2Const.KERNEL_ID_GIRO;
        } else if(Arrays.equals(adaptAid,new byte[]{(byte) 0xA0,0x00,0x00,0x08,0x33})) {//A000000833 2010 Mosolo
            deviceLogger.debug("KERNEL_ID_MADA");
            kernelID = EmvConst.KERNEL_ID_MADA;
        }else{
            kernelID = EmvConst.KERNEL_ID_UNIONPAY; // 默认银联，兼容国内石油卡等特殊卡片aid:B000000001504554524F4348494E41; B000000003504554524F4348494E4133
        }
        deviceLogger.debug("[getKernelID] kernelID="+kernelID);
        return kernelID;
    }

    /**
     * 判断是否海外设置
     *
     * @return true:海外版本， false：国内版本
     */
    private boolean isOverseas() {
        String version = "unknown";
        /**
         * ro.build.customer_id 后续固件版本增加的属性值
         *
         */
        version = getProperties("ro.build.customer_id");
        deviceLogger.debug("[isOverseas] customer_id:" + version);
        if (version.equalsIgnoreCase("Overseas")) {
            return true;
        } else if (version.equalsIgnoreCase("Brasil")) {
            return true;
        } else if (version.equalsIgnoreCase("Poynt")) {
            return true;
        }
        return false;
    }

    private String getProperties(String key) {
        String defaultValue = "unknown";
        String value = defaultValue;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            value = (String) (get.invoke(c, key, defaultValue));
        } catch (Exception e) {
            e.printStackTrace();
            deviceLogger.error("get property error, " + e.getMessage());
        }
        return value;
    }

    public TLVPackage getFinanlSelData() {
        return finanlSelData;
    }

    public void setFinanlSelData(TLVPackage finanlSelData) {
        this.finanlSelData = finanlSelData;
    }

    /**
     * 判断int数组中是否存在某个值的方法
     *
     * @param intArr 待查找int数组
     * @param intt   需要查找的值
     * @return :存在 true :不存在 false
     */
    public boolean IntArrLookupInt(int[] intArr, int intt) {
        String b = intt + ""; // 先转换为String类型
        for (int i : intArr) {
            if (b.equals(i + "")) {
                return true;
            }
        }
        return false;
    }

    private String getEmvCoreStep(int _seq_to) {
        if (_seq_to == AbstractEMVTransController._EMV_PROC_TO_APPSEL_INIT) {
            return "Initiate Application";
        } else if (_seq_to == AbstractEMVTransController._EMV_PROC_TO_READAPPDATA) {
            return "Read Application Data";
        } else if (_seq_to == AbstractEMVTransController._EMV_PROC_TO_OFFLINEAUTH) {
            return "Offline Data Authentication";
        } else if (_seq_to == AbstractEMVTransController._EMV_PROC_TO_RESTRITCT) {
            return "Processing Restrictions";
        } else if (_seq_to == AbstractEMVTransController._EMV_PROC_TO_CV) {
            return "Cardholder Verification";
        } else if (_seq_to == AbstractEMVTransController._EMV_PROC_TO_RISKMANA) {
            return "Terminal Risk Management";
        } else if (_seq_to == AbstractEMVTransController._EMV_PROC_TO_1GENAC) {
            return "Terminal Action Analysis(first gac)";
        } else if (_seq_to == AbstractEMVTransController._EMV_PROC_TO_2GENAC) {
            return "Issuer Authentication(second gac)";
        } else if (_seq_to == AbstractEMVTransController._EMV_PROC_CONTINUE) {
            return "Transaction Continue";
        } else {
            return "Unknow step";
        }
    }

    private int doSpecificKernel(EMVTransInfo emvTransInfo, EMVTransContext context, rf_transdata rfTransData, ep_opt epOpt, byte[] ctrl, byte[] processData, int processDataLen, boolean isNdkEmv) {
        int emvrslt = -1;
        if (isNdkEmv)
            emvrslt = emvcore.jniNLSDKCLL2PerformTransaction(epOpt, rfTransData, ctrl, processData, processDataLen);
        int kernelID = (rfTransData.usKernelId[0] & 0xFF);
        deviceLogger.debug("[doSpecificKernel] kernelID="+kernelID);
        if (kernelID == EmvConst.KERNEL_ID_PAYPASS) {
            emvTransInfo.setEntryPointType(EntryPointType.PAYPASS);
            if (!isNdkEmv)
                emvrslt = emvcore.jniSDKPayPassProcess(epOpt, rfTransData);
            deviceLogger.debug("[doEmvStep0] EmvJNIService->jniSDKPayPassProcess emvrslt=" + emvrslt);
        } else if (kernelID == EmvConst.KERNEL_ID_PAYWAVE) {
            emvTransInfo.setEntryPointType(EntryPointType.PAYWAVE);
            if (!isNdkEmv)
                emvrslt = emvcore.jniSDKPayWaveProcess(epOpt, rfTransData);
            deviceLogger.debug("[doEmvStep0] EmvJNIService->jniSDKPayWaveProcess emvrslt=" + emvrslt);
        } else if (kernelID == EmvConst.KERNEL_ID_UNIONPAY) {
            emvTransInfo.setEntryPointType(EntryPointType.QPBOC);
            if (!isNdkEmv)
                emvrslt = emvcore.jniSDKQpbocProcess(epOpt, rfTransData);
            deviceLogger.debug("[doEmvStep0] EmvJNIService->jniSDKQpbocProcess emvrslt=" + emvrslt);
        } else if (kernelID == EmvConst.KERNEL_ID_EXPRESSPAY) {
            emvTransInfo.setEntryPointType(EntryPointType.EXPRESSPAY);
            if (!isNdkEmv)
                emvrslt = emvcore.jniSDKExpressPayProcess(epOpt, rfTransData);
            deviceLogger.debug("[doEmvStep0] EmvJNIService->jniSDKExpressPayProcess emvrslt=" + emvrslt);
        } else if (kernelID == EmvConst.KERNEL_ID_DISCOVER) {
            emvTransInfo.setEntryPointType(EntryPointType.DISCOVERPAY);
            if (!isNdkEmv)
                emvrslt = emvcore.jniSDKDiscoverPayProcess(epOpt, rfTransData);
            deviceLogger.debug("[doEmvStep0] EmvJNIService->jniSDKDiscoverPayProcess emvrslt=" + emvrslt);
        } else if (kernelID == EmvConst.KERNEL_ID_JCB) {
            emvTransInfo.setEntryPointType(EntryPointType.JCB);
            if (!isNdkEmv)
                emvrslt = emvcore.jniSDKJCBProcess(epOpt, rfTransData);
            deviceLogger.debug("[doEmvStep0] EmvJNIService->jniSDKJCBProcess emvrslt=" + emvrslt);
        } else if (kernelID == EmvConst.KERNEL_ID_INTERAC) {
            emvTransInfo.setEntryPointType(EntryPointType.INTERAC);
            if (!isNdkEmv)
                emvrslt = emvcore.jniSDKInteracProcess(epOpt, rfTransData);
            deviceLogger.debug("[doEmvStep0] EmvJNIService->jniSDKInteracProcess emvrslt=" + emvrslt);
        } else if (kernelID == EmvConst.KERNEL_ID_MCCS) {
            emvTransInfo.setEntryPointType(EntryPointType.MCCS);
            if (!isNdkEmv)
                emvrslt = emvcore.jniSDKPureProcess(epOpt, rfTransData);
            deviceLogger.debug("[doEmvStep0] EmvJNIService->jniSDKPureProcess emvrslt=" + emvrslt);
        } else if (kernelID == EmvConst.KERNEL_ID_RUPAY) {
            emvTransInfo.setEntryPointType(EntryPointType.RUPAY);
            if (!isNdkEmv)
                emvrslt = emvcore.jniSDKRupayProcess(epOpt, rfTransData);
            deviceLogger.debug("[doEmvStep0] EmvJNIService->jniSDKRupayProcess emvrslt=" + emvrslt);
        } else if (kernelID == EmvConst.KERNEL_ID_MADA) {//pure
            emvTransInfo.setEntryPointType(EMVTransInfo.EntryPointType.PURE);
            if (!isNdkEmv)
                emvrslt = emvcore.jniSDKPureProcess(epOpt, rfTransData);
            deviceLogger.debug("[doEmvStep0] EmvJNIService->jniSDKPureProcess emvrslt=" + emvrslt);
        } else if(Arrays.equals(new byte[]{rfTransData.usKernelId[0],rfTransData.usKernelId[1],rfTransData.usKernelId[2]}, new byte[]{(byte) 0x8A, 0x06, (byte) 0x82})){//0x8A 06 82 61
            deviceLogger.debug("doEmvStep0 into [KERNEL_ID_IRAN_KAHROBA]");
            emvTransInfo.setEntryPointType(EMVTransInfo.EntryPointType.KAHROBA);
            emvrslt = emvcore.jniSDKPureProcess(epOpt, rfTransData);
            deviceLogger.debug("[doEmvStep0] EmvJNIService->jniSDKPureProcess emvrslt=" + emvrslt);
        }else if(kernelID == EMVLevel2Const.KERNEL_ID_GIRO){//Girocard
            deviceLogger.debug("doEmvStep0 into [KERNEL_ID_GIRO]");
            emvTransInfo.setEntryPointType(EMVTransInfo.EntryPointType.GIRO);
            if (!isNdkEmv) {
                //emvrslt = emvcore.jniSDKGirocardProcess(epOpt, rfTransData);
            }
            deviceLogger.debug("[doEmvStep0] EmvJNIService->jniSDKGirocardProcess emvrslt=" + emvrslt);
        }else {
            // 注意：未知KernelID可以默认走Qpboc流程，以适应国内环境。
            emvTransInfo.setEntryPointType(EMVTransInfo.EntryPointType.QPBOC);
            if (!isNdkEmv)
                emvrslt = emvcore.jniSDKQpbocProcess(epOpt, rfTransData);
            deviceLogger.debug("[doEmvStep0] EmvJNIService->jniSDKQpbocProcess emvrslt=" + emvrslt);
        }
        context.setEpOpt(epOpt);
        context.setRfTransData(rfTransData);
        if (emvrslt != EmvConst.EMV_TRANS_RF_SELECT_NEXT_AID || isNdkEmv) {
            deviceLogger.debug("[doEmvStep0] epOpt._OP_cvm:" + ((epOpt._OP_cvm) & 0xFF));
            emvTransInfo.setCvm(epOpt._OP_cvm);
            emvTransInfo.setSignatureReq(epOpt.nSignatureReq);
            emvTransInfo.setOnlinePin(epOpt.pusOnlinePin);
            emvTransInfo.setIssScriptRes(epOpt.pusIssScriptRes);
            emvTransInfo.setAdviceReq(epOpt.nAdviceReq);
            emvTransInfo.setEpOpt(epOpt);
            context.setKernelID(kernelID);
            emvTransInfo.setKernelId(rfTransData.usKernelId[0]);
        }
        return emvrslt;
    }

    /**
     * 判断是否是旧的aid文件路径
     *  @return
     */
    private boolean isOldEMVPath(){
        boolean isOldEMVPath = false;
        String fileName = "data" + File.separator + "share" + File.separator + "MESDKFile.txt";
        File file = new File(fileName);
        if (file.exists()) {
            isOldEMVPath = true;
        }
        deviceLogger.debug("[isOldEMVPath]file.exists():" + isOldEMVPath);
        if(emvExtParams!=null && emvExtParams.isEnableUsedEMVPath()){
            isOldEMVPath = true;
        }
        deviceLogger.debug("[isOldEMVPath]isOldEMVPath:" + isOldEMVPath);
        return isOldEMVPath;
    }

    public void setDiscoverTVROnlinePin() throws Exception{
        boolean pinBit = controller.getTransactionExtParams().isInternalTvrOnlinePinBit();
        deviceLogger.debug("setDiscoverTVROnlinePin,pinBit="+pinBit);
        if(pinBit){
            boolean isRfcard = controller.lastCardReadContainType(CardType.RFCARD);
            deviceLogger.debug("setDiscoverTVROnlinePin,isRfcard="+isRfcard);
            if(isRfcard){
                String aid = InnerUtils.hexString(getEmvData(0x4F));
                deviceLogger.debug("setDiscoverTVROnlinePin,aid="+aid+" "+controller.getEMVTransInfo().getCvm());
                if(!TextUtils.isEmpty(aid) && aid.startsWith("A000000152") && controller.getEMVTransInfo().getCvm() == EmvConst.OP_ONLINE_PIN){
                    byte[] valueTag95 =  getEmvData(0x95);
                    valueTag95[2] |= (0x1<<2);
                    setEmvData(0x95,valueTag95);
                }
            }
        }
    }
}
