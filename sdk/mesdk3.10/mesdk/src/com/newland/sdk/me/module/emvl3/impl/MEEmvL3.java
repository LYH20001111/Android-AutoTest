package com.newland.sdk.me.module.emvl3.impl;

import android.content.Context;

import com.newland.emv.jni.type.EmvConst;
import com.newland.sdk.me.module.emvl3.external.Aid;
import com.newland.sdk.me.module.emvl3.external.AidEntry;
import com.newland.sdk.me.module.emvl3.external.CapkEntry;
import com.newland.sdk.me.module.emvl3.external.EmvL3Const;
import com.newland.sdk.me.module.emvl3.external.EmvL3Module;
import com.newland.sdk.me.module.emvl3.external.Capk;
import com.newland.sdk.me.module.emvl3.impl.EmvL3Constant.MODULE;
import com.newland.sdk.me.module.emv.EMVInnerUtils;
import com.newland.sdk.me.module.emv.EMVParseUtil;
import com.newland.sdk.module.emv.AID;
import com.newland.sdk.module.emv.CAPK;
import com.newland.sdk.module.emv.EMVModule;
import com.newland.sdk.module.emv.EmvExtParams;
import com.newland.sdk.module.emv.EmvPackager;
import com.newland.sdk.me.module.emvl3.CardContactMode;
import com.newland.sdk.me.module.emvl3.EMVL3Module;
import com.newland.sdk.me.module.emvl3.TransactionResult;
import com.newland.sdk.me.module.emvl3.jni.ConfigMode;
import com.newland.sdk.me.module.emvl3.jni.EntryDIA;
import com.newland.sdk.me.module.emvl3.jni.EntryKPAC;
import com.newland.sdk.me.module.emvl3.jni.EntryLRC;
import com.newland.sdk.me.module.emvl3.jni.EntryNoitpecxe;
import com.newland.sdk.me.module.emvl3.jni.NapiEmvL3;
import com.newland.sdk.me.module.emvl3.jni.TXNResult;
import com.newland.sdk.me.module.emvl3.listener.MEEmvL3Listener;
import com.newland.sdk.module.externalLight.ExtIndicatorLightModule;
import com.newland.sdk.module.externalPin.ExtPinpadModule;
import com.newland.sdk.module.externaliccard.ExtICCardModule;
import com.newland.sdk.module.externalrfcard.ExtRFCardModule;
import com.newland.sdk.module.light.LightColor;
import com.newland.sdk.module.light.LightState;
import com.newland.sdk.mtype.DeviceRTException;
import com.newland.sdk.mtype.ExModuleType;
import com.newland.sdk.mtype.common.Const;
import com.newland.sdk.mtype.common.ErrorCode;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.mtypex.tlv.SimpleTLVPackage;
import com.newland.sdk.utils.ISOUtils;
import com.newland.sdk.utils.TLVMsg;
import com.newland.sdk.utils.TLVPackage;

import java.io.File;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;

/**
 * @Description 外接EMVL3指令接口封装.
 * @Author wuhh
 * @Date 2019/10/30
 */
public class MEEmvL3 implements EMVL3Module {
    private DeviceLogger deviceLogger = DeviceLoggerFactory.getLogger("MEEmvL3");
    private static final int BUFFER_SIZE_BIG = 1536;
    private Context mContext;
    private NapiEmvL3 mNapiEmvL3;
    private EmvL3Usage mEmvL3Usage;
    private int cvmStatus;
    private EmvL3Comm mEmvL3Comm;
    private EmvL3Module  mSPEmvL3Module;
    private static MEEmvL3 meEmvL3;
    private volatile boolean hasLoadTrmConfig;
    private EmvExtParams emvExtParams;
    private AbstractDevice owner;

    private MEEmvL3(Context context, AbstractDevice owner) {
        this.owner = owner;
        this.mContext = context;
        mNapiEmvL3 = new NapiEmvL3();
        mEmvL3Comm = EmvL3Comm.getInstance(context,owner);
    }

     public static MEEmvL3 getInstance(Context context, AbstractDevice owner){
        if(meEmvL3==null){
            meEmvL3 =  new MEEmvL3(context,owner);
        }
        return meEmvL3;
     }

    @Override
    public boolean extInit(EmvExtParams emvExtParams) {
        deviceLogger.debug("----------[extInit]-----------");
        this.emvExtParams = emvExtParams;
        int ret = mNapiEmvL3.NAPI_Init(mEmvL3Comm);
        long startTime = System.currentTimeMillis();
        deviceLogger.debug("[NAPI_Init] ret="+ret);
        boolean result = mEmvL3Comm.init(emvExtParams,new EmvL3Comm.CommChannel() {
            @Override
            public boolean getChannel() {
                String version = getVersion(MODULE.L3_MODULE_API);
                deviceLogger.debug("[extInit] version="+version);

                if(version == null){
                    return false;
                }
                return true;
            }
        });//初始化通讯,确定通讯模式
        long endTime = System.currentTimeMillis();
        deviceLogger.debug("[NAPI_Init] result="+result+" disTime="+(endTime-startTime));
        return result;
    }

    public void setExtEmvL3Listener(MEEmvL3Listener listener){
        mEmvL3Comm.setMEEmvL3Listener(listener);
    }

    @Override
    public boolean l3init(final EmvL3Usage l3Usage, byte[] configuration,final MEEmvL3Listener listener) {
        if (l3Usage == null || configuration == null || listener == null) {
            return false;
        }
        if (configuration.length != 8) {
            return false;
        }
        mEmvL3Usage = l3Usage;
        deviceLogger.debug( ">>>EmvL3Usage=" + l3Usage);
        if (mEmvL3Usage == EmvL3Usage.INNER) {
            mSPEmvL3Module = new EmvL3Module();
            String filePath = mContext.getFilesDir() + File.separator + "emvl3" + File.separator;
            deviceLogger.debug( ">>>Smart Pos Emv filePath=" + filePath);
            int ret = mSPEmvL3Module.init(filePath, configuration,listener);
            if (ret != 0) {
                return false;
            }
            return true;
        }else {
            int ret = mNapiEmvL3.NAPI_L3Init(configuration);
            deviceLogger.debug( "init: ret=" + ret);
            if (ret < 0) {
                return false;
            }
            return true;
        }
    }

    @Override
    public boolean loadConfiguration(String fileName,EMVModule emvModule) {
        deviceLogger.debug("-------[loadConfiguration]-------");
        boolean loadConfigurationResult = false;
        loadConfigurationResult = new EMVParseUtil().initializeEMVXml(mContext, fileName, emvModule,true);
        deviceLogger.debug("-------[loadConfiguration]-------loadConfigurationResult:"+loadConfigurationResult);

        if(loadConfigurationResult){
           hasLoadTrmConfig = true;
       }
        return loadConfigurationResult;
    }

    @Override
    public boolean updateTerminalConfig(CardContactMode cardIntf, byte[] tlvList) {
        if (cardIntf == null || tlvList == null) {
            return false;
        }
        if (getSPEmvL3()) {
            Aid aid = new Aid(getSPAidInterface(cardIntf));
            boolean result = (aid.loadTerminalConfig(tlvList) != 0 ? false : true);
            deviceLogger.debug("[updateTerminalConfig] SPEmvL3 result="+result);
            return result;
        }
        int[] tlvLen = new int[1];
        tlvLen[0] = tlvList.length;
        int ret = mNapiEmvL3.NAPI_L3LoadTerminalConfig(cardIntf.ordinal(), tlvList, tlvLen, ConfigMode.UPDATE.ordinal());
        deviceLogger.info( "loadTerminalConfig: ret=" + ret);
        if (ret < 0) {
            return false;
        }
        hasLoadTrmConfig = true;
        return true;
    }

    @Override
    public byte[] getTerminalConfig(CardContactMode cardIntf) {
        if (cardIntf == null) {
            return null;
        }
        if (getSPEmvL3()) {
            Aid aid = new Aid(getSPAidInterface(cardIntf));
            return aid.getTerminalConfig();
        }
        byte[] tlvList = new byte[2048];
        int[] tlvLen = new int[1];
        int ret = mNapiEmvL3.NAPI_L3LoadTerminalConfig(cardIntf.ordinal(), tlvList, tlvLen, ConfigMode.GET.ordinal());
        deviceLogger.debug( "loadTerminalConfig: ret=" + ret);
        if (ret < 0) {
            return null;
        }
        byte[] tlvData = new byte[tlvLen[0]];
        if(tlvLen[0]==0){
            deviceLogger.error("--------------term config null--------");
            return null;
        }
        System.arraycopy(tlvList, 0, tlvData, 0, tlvData.length);
        return tlvData;
    }

    @Override
    public boolean addAID(CardContactMode cardIntf, byte[] tlvList) {
        deviceLogger.debug("---------[addAID],hasLoadTrmConfig:"+hasLoadTrmConfig+";cardIntf:"+cardIntf);
        if (cardIntf == null || tlvList == null) {
            return false;
        }
        //非接默认终端参数
        //9F061000000000000000000000000000000000DF2407F4C0F0F8EF0E629F3501229F3303E0F8C89F4005FF80F0A0019F01060001234567899F150212349F160F3132333435363738393031323334355F3601029F3C0208409F3D01029F1A0208409F1E083835313049434300DF27011FDF2006999999999999DF1906000000000000DF2106000000010000DF3A0101DF390100DF1504000013889F09020002DF440B9F37049F47018F019F3201DF45039F0802DF0101015F2A0208409F1B04000000001F81020100
        if(!hasLoadTrmConfig){
            String rfTermConfig = "9F061000000000000000000000000000000000DF2407F4C0F0F8EF0E629F3501229F3303E0F8C89F4005FF80F0A0019F01060001234567899F150212349F160F3132333435363738393031323334355F3601029F3C0208409F3D01029F1A0201569F1E083835313049434300DF27011FDF2006999999999999DF1906000000000000DF2106000000010000DF3A0101DF390100DF1504000013889F09020002DF440B9F37049F47018F019F3201DF45039F0802DF0101015F2A0201569F1B04000000001F81020100";
            byte[] termConfig= getTerminalConfig(CardContactMode.CONTACTLESS);
            deviceLogger.debug("------termConfig:"+(termConfig==null?null:ISOUtils.hexString(termConfig)));
            updateTerminalConfig(CardContactMode.CONTACTLESS,ISOUtils.hex2byte(rfTermConfig));
        }

        /*
        TLVPackage tlvPackage = InnerUtils.newTlvPackage();
        tlvPackage.unpack(tlvList);
        byte[] aid_terminal = tlvPackage.getValue(Const.EmvStandardReference.AID_TERMINAL);
        byte kernelid = (byte) getKernelID(aid_terminal);
        byte[] kernelID = tlvPackage.getValue(Const.EmvSelfDefinedReference.KERNELID);
        if (null == kernelID) {
            kernelID = new byte[8];
            kernelID[0] = kernelid;
            tlvPackage.append(0xDF37, kernelID);
        }
        tlvPackage = dealRFcardParam(tlvPackage, kernelid);
        switch (kernelid) {
            case EmvConst.KERNEL_ID_PAYWAVE:
                break;
            case EmvConst.KERNEL_ID_PAYPASS:
                byte[] rrp = tlvPackage.getValue(0xDF55);//DF8132
                if (null == rrp) {
                    tlvPackage.append(0xDF55, new byte[]{0x00, 0x14});
                }
                rrp = tlvPackage.getValue(0xDF54);//DF8133
                if (null == rrp) {
                    tlvPackage.append(0xDF54, new byte[]{0x00, 0x32});
                }
                rrp = tlvPackage.getValue(0xDF8136);//DF8136
                if (null == rrp) {
                    tlvPackage.append(0xDF56, new byte[]{0x01, 0x2c});
                }
                rrp = tlvPackage.getValue(0xDF8137);//DF8137
                if (null == rrp) {
                    tlvPackage.append(0xDF57, new byte[]{0x32});
                }
                rrp = tlvPackage.getValue(0xDF8134);//DF8134
                if (null == rrp) {
                    tlvPackage.append(0xDF58, new byte[]{0x00, 0x0B});
                }
                rrp = tlvPackage.getValue(0xDF8135);//DF8135
                if (null == rrp) {
                    tlvPackage.append(0xDF59, new byte[]{0x00, 0x0D});
                }
                break;
            case EmvConst.KERNEL_ID_JCB:
                byte[] CombinationOP = tlvPackage.getValue(Const.EmvSelfDefinedReference.COMBINATIONOPT);
                if (null == CombinationOP)
                    tlvPackage.append(0xDF60, CombinationOP);
                byte[] tip = tlvPackage.getValue(Const.EmvStandardReference.TIP);
                if (null == tip)
                    tlvPackage.append(0x9F53, new byte[]{0x70, (byte) 0x80, 0x00});
                break;
            case EmvConst.KERNEL_ID_EXPRESSPAY:
                byte[] exTerminalCap = tlvPackage.getValue(Const.EmvSelfDefinedReference.EX_Terminal_CAP);
                if (null == exTerminalCap)
                    tlvPackage.append(0xDF49, new byte[]{(byte) 0xC0});
                byte[] terminalCap = tlvPackage.getValue(Const.EmvStandardReference.TERMINAL_CAPABILITIES);
                if (null == terminalCap) {
                    tlvPackage.append(0x9F33, new byte[]{(byte) 0xC0, (byte) 0xF8, (byte) 0xC8});
                }
                break;
            case EmvConst.KERNEL_ID_UNIONPAY:
                byte[] value = tlvPackage.getValue(Const.EmvSelfDefinedReference.APP_SELECT_INDICATOR);
                if (value != null && value.length > 0) {
                    byte appSelectIndicator = value[0];
                    if (appSelectIndicator == 0x01) {
                        tlvPackage.append(0xDF01, new byte[]{(byte) 0x00});
                    } else {
                        tlvPackage.append(0xDF01, new byte[]{(byte) 0x01});
                    }
                }
                break;
            case EmvConst.KERNEL_ID_DISCOVER:
                break;
            case EmvConst.KERNEL_ID_MCCS:
                byte[] cac = tlvPackage.getValue(0xDF62);
                if (null == cac) {
                    tlvPackage.append(0xDF62, new byte[]{(byte) 0xDF, 0x62, 0x05, 0x36, 0x00, 0x60, 0x43, (byte) 0xF9});
                }
                break;
            case EmvConst.KERNEL_ID_INTERAC:
                break;
            case EmvConst.KERNEL_ID_RUPAY:
                break;
            case EmvConst.KERNEL_ID_MADA:
                cac = tlvPackage.getValue(0xDF62);
                if (null == cac) {
                    tlvPackage.append(0xDF62, new byte[]{(byte) 0xDF, 0x62, 0x05, 0x36, 0x00, 0x60, 0x43, (byte) 0xF9});
                }
                break;
            default:
                break;
        }
        byte[] ttq = tlvPackage.getValue(Const.EmvStandardReference.TERMINAL_TRANSACTION_QUALIFIERS);
        if (null == ttq) {
            if (kernelid == EmvConst.KERNEL_ID_EXPRESSPAY)
                tlvPackage.append(0x9F66, new byte[]{(byte) 0xD8, (byte) 0x80, 0x00, (byte) 0x00});// expresspay
            else
                tlvPackage.append(0x9F66, new byte[]{0x36, 0x00, 0x00, (byte) 0x80});
        }
        byte[] requestData = tlvPackage.pack();
        */

        byte[] requestData = tlvList;
        deviceLogger.debug("[addAID] target tlvList="+(requestData==null?"null":ISOUtils.hexString(requestData)));
        deviceLogger.debug("--------mEmvL3Usage:"+mEmvL3Usage);
        if (getSPEmvL3()) {
            Aid aid = new Aid(getSPAidInterface(cardIntf));
            boolean result = (aid.loadAID(requestData) != 0 ? false : true);
            deviceLogger.debug("[addAID] result="+result);
            return result;
        }
        deviceLogger.debug("------NAPI_L3LoadAIDConfig-------");
        try {
            TLVPackage tp = EMVInnerUtils.newTlvPackage();
            tp.unpack(requestData);
            byte[] aid = tp.getValue(Const.EmvStandardReference.AID_TERMINAL);
            int kernelID = getKernelID(aid);
            deviceLogger.debug("======kernelID:"+kernelID);
            byte[] berferKernelID = tp.getValue(Const.EmvSelfDefinedReference.KERNELID);
            deviceLogger.debug("==========berferKernelID:"+(berferKernelID==null?null:ISOUtils.hexString(berferKernelID)));
            byte[] ttq = tp.getValue(Const.EmvStandardReference.TERMINAL_TRANSACTION_QUALIFIERS);
            deviceLogger.debug("----------ttq:"+(ttq==null?null:ISOUtils.hexString(ttq)));
            if(kernelID!=-1 && berferKernelID==null){
                byte[] fianlKernelID = new byte[8];
                fianlKernelID[0] = (byte)kernelID;
                tp.append(0xDF37,fianlKernelID);
                requestData = tp.pack();
            }
            if(ttq==null && (kernelID== EmvConst.KERNEL_ID_UNIONPAY || kernelID== EmvConst.KERNEL_ID_PAYWAVE|| kernelID== EmvConst.KERNEL_ID_EXPRESSPAY || kernelID== EmvConst.KERNEL_ID_DISCOVER)){//pboc   paywave   Amex    dpas这几个内核一定要有9F66
                tp.append(0x9F66,"36000080");
                requestData = tp.pack();
            }
            byte[] appSelIndicator =  tp.getValue(Const.EmvSelfDefinedReference.APP_SELECT_INDICATOR);
            if(appSelIndicator==null){
                tp.append(0xDF01,ISOUtils.hex2byte("01"));
                requestData = tp.pack();
            }
            //由于国内下发的国内外aid df01都为00部分匹配。因此强制转换
            if (appSelIndicator!=null && !isOverseas()) {
                if (appSelIndicator[0] == 0x01) {
                    appSelIndicator[0] = 0x00;
                } else {
                    appSelIndicator[0] = 0x01;
                }
                tp.deleteByTag(0xDF01);
                tp.append(0xDF01,ISOUtils.hex2byte("01"));
                requestData = tp.pack();
            }
            byte[] limitExist = null;
            limitExist = tp.getValue(Const.EmvSelfDefinedReference.LIMIT_EXIST);
            deviceLogger.debug("-----limitExist:"+(limitExist==null?null:ISOUtils.hexString(limitExist)));
            if(limitExist == null){
                byte _limit_exist = 0x00;
                byte[] value = tp.getValue(Const.EmvSelfDefinedReference.EC_TRANS_LIMIT);
                if (value != null) {
                    _limit_exist |= 0x01;
                }
                value = tp.getValue(Const.EmvSelfDefinedReference.NCICC_TRANS_LIMIT);
                if (value != null) {
                    _limit_exist |= 0x02;
                }
                value = tp.getValue(Const.EmvSelfDefinedReference.NCICC_OFFLINE_FLOOR_LIMIT);
                if (value != null) {
                    _limit_exist |= 0x04;
                }
                value = tp.getValue(Const.EmvSelfDefinedReference.NCICC_CVM_LIMIT);
                if (value != null) {
                    _limit_exist |= 0x08;
                }
                deviceLogger.debug("--------_limit_exist:"+(_limit_exist & 0xFF));
                tp.append(0xDF27,new byte[]{_limit_exist});
               // tp.append(0xDF27,new byte[]{0x1F});
                requestData = tp.pack();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        deviceLogger.debug("-----------final requestData:"+(requestData==null?null:ISOUtils.hexString(requestData)));

        int ret = mNapiEmvL3.NAPI_L3LoadAIDConfig(cardIntf.ordinal(), null, requestData, new int[]{requestData.length}, ConfigMode.UPDATE.ordinal());
        deviceLogger.debug( "[addAID] ret=" + ret);
        if (ret < 0) {
            return false;
        }
        return true;
    }

    private TLVPackage dealRFcardParam(TLVPackage tlvPackage, int kernelID) {
        switch (kernelID) {
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
                //----paypass RRP
                //<!-- Maximum Relay Resistance Grace Period- 'DF8133’ -->
                String reqData = tlvPackage.getString(0xDF8133);
                if (null != msi) {
                    tlvPackage.deleteByTag(0xDF8133);
                    tlvPackage.append(0xDF54, msi);
                }
                //	<!-- Minimum Relay Resistance Grace Period- 'DF8132' -->
                reqData = tlvPackage.getString(0xDF8132);
                if (null != msi) {
                    tlvPackage.deleteByTag(0xDF8132);
                    tlvPackage.append(0xDF55, msi);
                }
                //<!-- Relay Resistance Accuracy Threshold- 'DF8136' -->
                reqData = tlvPackage.getString(0xDF8136);
                if (null != msi) {
                    tlvPackage.deleteByTag(0xDF8136);
                    tlvPackage.append(0xDF56, msi);
                }
                //<!-- Relay Resistance Transmission Time Mismatch Threshold- 'DF8137’ -->
                reqData = tlvPackage.getString(0xDF8137);
                if (null != msi) {
                    tlvPackage.deleteByTag(0xDF8137);
                    tlvPackage.append(0xDF57, msi);
                }
                //<!-- Terminal Expected Transmission Time For Relay Resistance C-APDU- ‘DF8134’ -->
                reqData = tlvPackage.getString(0xDF8134);
                if (null != msi) {
                    tlvPackage.deleteByTag(0xDF8134);
                    tlvPackage.append(0xDF58, msi);
                }
                //	<!-- Terminal Expected Transmission Time For Relay Resistance R-APDU- 'DF8135’ -->
                reqData = tlvPackage.getString(0xDF8135);
                if (null != msi) {
                    tlvPackage.deleteByTag(0xDF8135);
                    tlvPackage.append(0xDF59, msi);
                }
                //<!-- Default UDOL- 'DF811A' -->
                reqData = tlvPackage.getString(0xDF811A);
                if (null != msi) {
                    tlvPackage.deleteByTag(0xDF811A);
                    tlvPackage.append(0xDF2B, msi);
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
                //<!-- Reader Contactless Transaction Limit(No On-device CVM)- 'DF8124' -->
                String noCVM = tlvPackage.getString(0xDF8124);
                if (null != noCVM) {
                    tlvPackage.deleteByTag(0xDF8124);
                    tlvPackage.append(0xDF20, noCVM);
                }
                break;
            default:
                return tlvPackage;
        }
        return tlvPackage;
    }

    @Override
    public AID getAID(CardContactMode cardIntf, byte[] aid) {
        if (cardIntf == null || aid == null) {
            return null;
        }
        if (getSPEmvL3()) {
            Aid spaid = new Aid(getSPAidInterface(cardIntf));
            AidEntry aidEntry = new AidEntry();
            int aidLen = Math.min(aid.length, aidEntry.aid.length);
            System.arraycopy(aid, 0, aidEntry.aid, 0, aidLen);
            aidEntry.aidLen = (byte) aidLen;
            byte[] aidTlv = spaid.getAID(aidEntry);
            EmvPackager emvPackager = EMVInnerUtils.newEmvPackager();
            AID aidConfig = new AID();
            emvPackager.unpack(aidTlv, AID.class, aidConfig);
            return aidConfig;
        }
        EntryDIA entryDIA = new EntryDIA();
        int aidLen = Math.min(aid.length, entryDIA.aid.length);
        System.arraycopy(aid, 0, entryDIA.aid, 0, aidLen);
        entryDIA.aidLen = (byte) aidLen;
        byte[] tlv = new byte[2048];
        int[] len = new int[1];
        int ret = mNapiEmvL3.NAPI_L3LoadAIDConfig(cardIntf.ordinal(), entryDIA, tlv, len, ConfigMode.GET.ordinal());
        deviceLogger.debug( "getAID: ret=" + ret + " len=" + len[0]);
        if (ret < 0 || len[0] <= 0) {
            return null;
        }
        byte[] aidTlv = new byte[len[0]];
        System.arraycopy(tlv, 0, aidTlv, 0, len[0]);
        deviceLogger.debug("getAID aidTlv="+(aidTlv==null?"null":ISOUtils.hexString(aidTlv)));
        EmvPackager emvPackager = EMVInnerUtils.newEmvPackager();
        AID aidConfig = new AID();
        emvPackager.unpack(aidTlv, AID.class, aidConfig);
        return aidConfig;
    }

    @Override
    public boolean deleteAID(CardContactMode cardIntf, byte[] aid) {
        if (cardIntf == null) {
            return false;
        }
        if (aid == null) {
            return deleteAllAID(cardIntf);
        }
        if (getSPEmvL3()) {
            Aid spaid = new Aid(getSPAidInterface(cardIntf));
            AidEntry aidEntry = new AidEntry();
            int aidLen = Math.min(aid.length, aidEntry.aid.length);
            System.arraycopy(aid, 0, aidEntry.aid, 0, aidLen);
            aidEntry.aidLen = (byte) aidLen;
            return spaid.remove(aidEntry);
        }
        EntryDIA entryDIA = new EntryDIA();
        int aidLen = Math.min(aid.length, entryDIA.aid.length);
        System.arraycopy(aid, 0, entryDIA.aid, 0, aidLen);
        entryDIA.aidLen = (byte) aidLen;
        int ret = mNapiEmvL3.NAPI_L3LoadAIDConfig(cardIntf.ordinal(), entryDIA, null, null, ConfigMode.REMOVE.ordinal());
        deviceLogger.debug( "deleteAID: ret=" + ret);
        if (ret < 0) {
            return false;
        }
        return true;
    }

    @Override
    public byte[] getAIDCount(CardContactMode cardIntf) {
        if (cardIntf == null || getSPEmvL3()) {
            return null;
        }
        int[] len = new int[1];
        byte[] data = new byte[BUFFER_SIZE_BIG];
        int ret = mNapiEmvL3.NAPI_L3GetAIDCount(cardIntf.ordinal(),len,data);
        if (ret < 0) {
            return null;
        }
        byte[] numLenTlv = new byte[len[0]];
        System.arraycopy(data,0,numLenTlv,0,numLenTlv.length);
        deviceLogger.debug("getAIDCount len="+len[0]+" numLenTlv="+(numLenTlv==null?"null":ISOUtils.hexString(numLenTlv)));
        return numLenTlv;
    }

    public boolean deleteAllAID(CardContactMode cardIntf) {
        if (cardIntf == null) {
            return false;
        }
        if (getSPEmvL3()) {
            Aid aid = new Aid(getSPAidInterface(cardIntf));
            boolean result = aid.flush();
            deviceLogger.debug("deleteAllAID result="+result);
            return result;
        }
        int ret = mNapiEmvL3.NAPI_L3LoadAIDConfig(cardIntf.ordinal(), null, null, null, ConfigMode.FLUSH.ordinal());
        deviceLogger.debug( "deleteAllAID: ret=" + ret);
        if (ret < 0) {
            return false;
        }
        return true;
    }

    @Override
    public boolean addCAPublicKey(final byte[] inputData) {
        if (inputData == null) {
            return false;
        }
        TLVPackage tlvpackage = EMVInnerUtils.newTlvPackage();
        tlvpackage.unpack(inputData);

        EntryKPAC capk = new EntryKPAC();
        byte[] value = tlvpackage.getValue(Const.EmvSelfDefinedReference.CAPK_MODULUS);
        if (value != null) {
            System.arraycopy(value, 0, capk.pkModulus, 0, Math.min(value.length, capk.pkModulus.length));
            capk.pkModulusLen = (byte) (value.length);
        }
        value = tlvpackage.getValue(Const.EmvSelfDefinedReference.CAPK_EXPONENT);
        if (value != null) {
            byte[] exponnent = EMVInnerUtils.padLeft(value, 3, (byte) 0x00);
            System.arraycopy(exponnent, 0, capk.pkExponent, 0, 3);
        }
        value = tlvpackage.getValue(Const.EmvSelfDefinedReference.CAPK_SHA1CHECKSUM);
        if (value != null) {
            System.arraycopy(value, 0, capk.hashValue, 0, Math.min(value.length, capk.hashValue.length));
        }
        value = tlvpackage.getValue(Const.EmvSelfDefinedReference.CA_PK_EXPIRATION_DATE);
        if (value != null && value.length >= 3) {
            if (value.length == 8) {
                String expDate = new String(value);
                value = EMVInnerUtils.hex2byte(expDate);
            }
            byte[] expiredDate = EMVInnerUtils.padLeft(value, 4, (byte) 0x20);
            System.arraycopy(expiredDate, 0, capk.expiredDate, 0, Math.min(value.length, capk.expiredDate.length));
        }

        value = tlvpackage.getValue(Const.EmvStandardReference.AID_TERMINAL);
        if (value != null) {
            System.arraycopy(value, 0, capk.rid, 0, Math.min(value.length, capk.rid.length));
        } else {
            throw new DeviceRTException(ErrorCode.EMV_TRANSFER_FAILED, "rid should not be null!");
        }

        value = tlvpackage.getValue(Const.EmvStandardReference.CA_PUBLIC_KEY_INDEX_TERMINAL);
        if (value != null && value.length > 0)
            capk.index = value[0];

        value = tlvpackage.getValue(Const.EmvSelfDefinedReference.CA_PK_ALGORITHM_INDICATOR);
        if (value != null && value.length > 0)
            capk.pkAlgorithmIndicator = value[0];

        value = tlvpackage.getValue(Const.EmvSelfDefinedReference.CA_PK_HASH_ALGORITHM_INDICATOR);
        if (value != null && value.length > 0)
            capk.hashAlgorithmIndicator = value[0];

        if (getSPEmvL3()) {
            Capk capkl3 = new Capk();
            CapkEntry capkEntryl3 = new CapkEntry();
            capkEntryl3.pkModulusLen = capk.pkModulusLen & 0xFF;
            System.arraycopy(capk.pkModulus, 0, capkEntryl3.pkModulus, 0, capkEntryl3.pkModulusLen);
            System.arraycopy(capk.pkExponent, 0, capkEntryl3.pkExponent, 0, capk.pkExponent.length);
            System.arraycopy(capk.hashValue, 0, capkEntryl3.hashValue, 0, capk.hashValue.length);
            System.arraycopy(capk.expiredDate, 0, capkEntryl3.expiredDate, 0, capk.expiredDate.length);
            System.arraycopy(capk.rid, 0, capkEntryl3.rid, 0, capk.rid.length);
            capkEntryl3.index = capk.index;
            capkEntryl3.pkAlgorithmIndicator = capk.pkAlgorithmIndicator;
            capkEntryl3.hashAlgorithmIndicator = capk.hashAlgorithmIndicator;
            System.arraycopy(capk.rfu, 0, capkEntryl3.rfu, 0, capk.rfu.length);
            boolean result = (capkl3.load(capkEntryl3) == 0 ? true : false);
            deviceLogger.debug( "addCAPublicKey: result=" + result);
            return result;
        }
        int ret = mNapiEmvL3.NAPI_L3LoadCAPK(capk, ConfigMode.UPDATE.ordinal());
        deviceLogger.debug( "addCAPublicKey: ret=" + ret);
        if (ret < 0) {
            return false;
        }
        return true;
    }

    @Override
    public CAPK getCAPublicKey(byte[] rid, int index) {
        if (rid == null) {
            return null;
        }
        if (rid.length != 5) {
            return null;
        }

        if (getSPEmvL3()) {
            Capk capkl3 = new Capk();
            CapkEntry capkEntryl3 = capkl3.get(rid, index);
            deviceLogger.debug( "getCAPublicKey capkEntryl3=" + capkEntryl3);
            if (capkEntryl3 == null) {
                return null;
            }
            int moduleLen = (capkEntryl3.pkModulusLen & 0xff);
            byte[] module = new byte[moduleLen];
            String dateStr = EMVInnerUtils.bcd2str(capkEntryl3.expiredDate, 0, capkEntryl3.expiredDate.length * 2, true);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
            Date date = null;
            try {
                date = sdf.parse(dateStr);
            } catch (ParseException e) {
                throw new DeviceRTException(ErrorCode.UNKNOWN, "could not parse dateStr to yyyyMMdd!" + dateStr);
            }
            System.arraycopy(capkEntryl3.pkModulus, 0, module, 0, moduleLen);
            CAPK rslt = new CAPK(capkEntryl3.index, capkEntryl3.hashAlgorithmIndicator,
                    capkEntryl3.pkAlgorithmIndicator, module, capkEntryl3.pkExponent, capkEntryl3.hashValue, date);
            return rslt;
        }
        EntryKPAC capk = new EntryKPAC();
        System.arraycopy(rid, 0, capk.rid, 0, rid.length);
        capk.index = (byte) index;
        int ret = mNapiEmvL3.NAPI_L3LoadCAPK(capk, ConfigMode.GET.ordinal());
        deviceLogger.debug( "getCAPublicKey: ret=" + ret);
        if (ret < 0) {
            return null;
        }
        int moduleLen = (capk.pkModulusLen & 0xff);
        byte[] module = new byte[moduleLen];
        String dateStr = EMVInnerUtils.bcd2str(capk.expiredDate, 0, capk.expiredDate.length * 2, true);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
        Date date = null;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            throw new DeviceRTException(ErrorCode.UNKNOWN, "could not parse dateStr to yyyyMMdd!" + dateStr);
        }
        System.arraycopy(capk.pkModulus, 0, module, 0, moduleLen);
        CAPK rslt = new CAPK(capk.index, capk.hashAlgorithmIndicator, capk.pkAlgorithmIndicator, module, capk.pkExponent, capk.hashValue, date);
        rslt.setRid(rid);
        rslt.setIndex(index);
        return rslt;

    }

    @Override
    public boolean deleteCAPublicKey(byte[] rid, int index) {
        if (rid == null) {
            return false;
        }
        if (rid.length != 5) {
            return false;
        }
        if (getSPEmvL3()) {
            Capk capkl3 = new Capk();
            boolean result = capkl3.remove(rid, index);
            deviceLogger.debug( "deleteCAPublicKey: result=" + result);
            return result;
        }
        EntryKPAC capk = new EntryKPAC();
        System.arraycopy(rid, 0, capk.rid, 0, rid.length);
        capk.index = (byte) index;
        int ret = mNapiEmvL3.NAPI_L3LoadCAPK(capk, ConfigMode.REMOVE.ordinal());
        deviceLogger.debug( "deleteCAPublicKey: ret=" + ret);
        if (ret < 0) {
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteAllCAPublicKey() {
        if (getSPEmvL3()) {
            Capk capkl3 = new Capk();
            boolean result = capkl3.flush();
            deviceLogger.debug(" deleteAllCAPublicKey result="+result);
            return result;
        }
        EntryKPAC capk = new EntryKPAC();
        int ret = mNapiEmvL3.NAPI_L3LoadCAPK(capk, ConfigMode.FLUSH.ordinal());
        deviceLogger.debug( "deleteAllCAPublicKey: ret=" + ret);
        if (ret < 0) {
            return false;
        }
        return true;
    }

    @Override
    public byte[] getCAPublicKeyCount() {
        if (getSPEmvL3()) {
            return null;
        }
        int[] len = new int[1];
        byte[] data = new byte[BUFFER_SIZE_BIG];
        int ret = mNapiEmvL3.NAPI_L3GetCAPKCount(len,data);
        if (ret < 0) {
            return null;
        }
        byte[] numRidIndex = new byte[len[0]];
        System.arraycopy(data,0,numRidIndex,0,numRidIndex.length);
        deviceLogger.debug("getCAPublicKeyCount len="+len[0]+" numRidIndexData="+(numRidIndex==null?"null":ISOUtils.hexString(numRidIndex)));
        return numRidIndex;
    }

    //    @Override
    public boolean loadRevocationList(EntryLRC crl, ConfigMode mode) {
        if (crl == null || mode == null) {
            return false;
        }
        int ret = mNapiEmvL3.NAPI_L3LoadRevocationList(crl, mode.ordinal());
        deviceLogger.debug( "loadRevocationList: ret=" + ret);
        if (ret < 0) {
            return false;
        }
        return true;
    }

    //    @Override
    public boolean loadExceptionList(EntryNoitpecxe exceptionList, ConfigMode mode) {
        if (exceptionList == null || mode == null) {
            return false;
        }
        int ret = mNapiEmvL3.NAPI_L3LoadExceptionList(exceptionList, mode.ordinal());
        deviceLogger.debug( "loadExceptionList: ret=" + ret);
        if (ret < 0) {
            return false;
        }
        return true;
    }

    @Override
    public int preProcessTransaction(byte[] data) {
        if (getSPEmvL3()) {
            return -1;
        }
        int[] errorCode = new int[1];
        errorCode[0] = -1;
        int ret = mNapiEmvL3.NAPI_L3PreProcessTransaction(data, data.length, errorCode);
        if (ret < 0) {
            return -1;
        }
        return errorCode[0];
    }

    @Override
    public TransactionResult performTransaction(byte[] data) {
        if (data == null) {
            return null;
        }
        if (getSPEmvL3()) {
            int errorCode = mSPEmvL3Module.performTransaction(data);
            int resultCode = mSPEmvL3Module.getTransResult();
            return new TransactionResult(errorCode, resultCode);
        }
//        terminateTransaction();//交易前清空上一笔的内核数据
        TXNResult result = new TXNResult();
        result.flag1F8131 = 0;
        TLVPackage tlvPackage =  new SimpleTLVPackage();
        byte[] sendtlvData = new byte[data.length-5];
        System.arraycopy(data,5,sendtlvData,0,sendtlvData.length);
        tlvPackage.unpack(sendtlvData);
        byte[] tag1F8131 = tlvPackage.getValue(0x1F8131);
        if(tag1F8131 != null){
            result.flag1F8131 = 1;
            result.keyIndex = tag1F8131[0];
        }
        int ret = mNapiEmvL3.NAPI_L3PerformTransaction(data, data.length, result);
        deviceLogger.debug( "performTransaction: ret=" + (ret==-13?"cancel":ret));
        if (ret < 0) {
            return null;
        }
        cvmStatus = result.cvmStatus;
        deviceLogger.debug(">>>cvmStatus="+cvmStatus);
        deviceLogger.debug(">>>result.cardSchemeId="+result.cardSchemeId);
        byte[] tlvAllData = null;
        if(result.tlvLen != 0 || result.l3TlvLen != 0){
            tlvAllData = new byte[result.tlvLen+result.l3TlvLen];

            if(result.tlvLen != 0){
                System.arraycopy(result.tlvData,0,tlvAllData,0,result.tlvLen);
            }
            if(result.l3TlvLen != 0){
                System.arraycopy(result.l3TlvData,0,tlvAllData,result.tlvLen,result.l3TlvLen);
            }
        }
        deviceLogger.debug(">>>tlvAllLen="+(result.tlvLen+result.l3TlvLen)+" tlvAllData="+(tlvAllData==null?"null":InnerUtils.hexString(tlvAllData)));
        return new TransactionResult(result.errorCode, result.resultCode,cvmStatus,tlvAllData);
    }

    @Override
    public TransactionResult completeTransaction(byte[] data) {
        if (data == null) {
            return null;
        }
        if (getSPEmvL3()) {
            int errorCode = mSPEmvL3Module.completeTransaction(data);
            int resultCode = mSPEmvL3Module.getTransResult();
            return new TransactionResult(errorCode, resultCode);
        }
        TXNResult result = new TXNResult();
        int ret = mNapiEmvL3.NAPI_L3CompleteTransaction(data, data.length, result);
        deviceLogger.debug( "completeTransaction: ret=" + ret);
        if (ret < 0) {
            return null;
        }
        return new TransactionResult(result.errorCode, result.resultCode);
    }

    @Override
    public boolean terminateTransaction() {
//        mListener = null;
//        mEmvL3Usage = null;
        if (getSPEmvL3()) {
//            return (mSPEmvL3Module.terminateTransacion() == 0 ? true : false);
        }
        TXNResult result = new TXNResult();
        int ret = mNapiEmvL3.NAPI_L3TerminateTransaction(result);
        deviceLogger.debug( "terminateTransaction: ret=" + ret);
        if (ret < 0) {
            return false;
        }
        return true;
    }

    //    @Override
    public boolean cancelTransaction() {
        if (getSPEmvL3()) {
            return false;
        }
        int ret = mNapiEmvL3.NAPI_L3CancelTransaction();
        deviceLogger.debug( "cancelTransaction: ret=" + ret);
        if (ret < 0) {
            return false;
        }
        return true;
    }

    @Override
    public boolean setData(int tag, byte[] data) {
        if (data == null || (data != null && data.length <= 0)) {
            return false;
        }
        if (getSPEmvL3()) {
            boolean result = (mSPEmvL3Module.setData(tag, data) == 0 ? true : false);
            deviceLogger.debug("setData retult="+result);
            return result;
        }

        int ret = mNapiEmvL3.NAPI_L3SetData(tag, data, data.length);
        deviceLogger.debug( "setData: ret=" + ret);
        if (ret < 0) {
            return false;
        }
        return true;
    }

    @Override
    public byte[] getData(int tag) {
        if (tag < 0) {
            return null;
        }
        if (getSPEmvL3()) {
            return mSPEmvL3Module.getData(tag);
        }
        byte[] value = new byte[152];
        int[] realLen = new int[1];
        int ret = mNapiEmvL3.NAPI_L3GetData(tag, (byte) 0, value, value.length, realLen);
        deviceLogger.debug( "getData: ret=" + ret);
        if (ret < 0) {
            return null;
        }
        if(realLen[0]==0){
            deviceLogger.error("-----realLen==0-----");
            return null;
        }
        byte[] realValue = new byte[realLen[0]];
        System.arraycopy(value, 0, realValue, 0, realLen[0]);
        return realValue;
    }

    @Override
    public boolean setTLVData(byte[] tlvList) {
        if (tlvList == null) {
            return false;
        }
        if (getSPEmvL3()) {
            TLVPackage tlvPackage = EMVInnerUtils.newTlvPackage();
            tlvPackage.unpack(tlvList);
            Enumeration elements = tlvPackage.elements();
            while (elements.hasMoreElements()) {
                TLVMsg tlvmsg = (TLVMsg) elements.nextElement();
                int tag = tlvmsg.getTag();
                byte[] value = tlvmsg.getValue();
                int ret = mSPEmvL3Module.setData(tag, value);
                if (ret != 0) {
                    return false;
                }
            }
            return true;
        }
        int ret = mNapiEmvL3.NAPI_L3SetTLVData(tlvList, tlvList.length);
        deviceLogger.debug( "setTLVData: ret=" + ret);
        if (ret < 0) {
            return false;
        }
        return true;
    }

    @Override
    public byte[] getTlvData(ArrayList<Integer> tagList, boolean isPackZeroLen) {
        if (tagList == null || (tagList != null && tagList.size() <= 0)) {
            return null;
        }
        if (getSPEmvL3()) {
            return mSPEmvL3Module.getListData(tagList, isPackZeroLen);
        }
        String tags = "";
        for (int i = 0; i < tagList.size(); i++) {
            tags += String.format("%2x", tagList.get(i));
        }
        deviceLogger.debug( "getTlvData: tags=" + tags);
        int isPackZeroFlag = 0;
        if (isPackZeroLen) {
            isPackZeroFlag = 1;
        }
        byte[] tlvValue = new byte[1024];
        int[] tlvLen = new int[1];
        int ret = mNapiEmvL3.NAPI_L3GetTlvData(InnerUtils.hex2byte(tags), tagList.size(), (byte) 0, tlvValue, tlvValue.length, isPackZeroFlag, tlvLen);
        deviceLogger.debug( "getTlvData: ret=" + ret);
        if (ret < 0) {
            return null;
        }
        byte[] tlvData = new byte[tlvLen[0]];
        System.arraycopy(tlvValue, 0, tlvData, 0, tlvLen[0]);
        return tlvData;
    }

    @Override
    public boolean setDebugMode(int level) {
        if (getSPEmvL3()) {
            mSPEmvL3Module.setDebugMode(level);
            return true;
        }
        int ret = mNapiEmvL3.NAPI_L3SetDebugMode(level);
        deviceLogger.debug( "setDebugMode: ret=" + ret);
        if (ret < 0) {
            return false;
        }
        return true;
    }

    @Override
    public void finishEMV() {
        try {
            deviceLogger.debug("----[finishEMV]]------owner:"+owner);
            if (getSPEmvL3()) {//内置不用处理
                return;
            }
            if(owner==null){
                return;
            }
            ExtPinpadModule extPinpadModule = (ExtPinpadModule)owner.getExModule(ExModuleType.PINPAD);
            boolean initResult = extPinpadModule.init(null);
            if(!initResult){
                deviceLogger.error("[finishEMV]]init failed");
                return;
            }
            extPinpadModule.backToMainScreen();//回退键盘主页面


            ExtIndicatorLightModule extIndicatorLightModule = (ExtIndicatorLightModule)owner.getExModule(ExModuleType.LIGHT);
            boolean ledResult = extIndicatorLightModule.operateLight(new LightColor[]{LightColor.BLUE,LightColor.GREEN,LightColor.RED,LightColor.YELLOW}, LightState.TURNOFF,500);
            deviceLogger.debug("[finishEMV]] turn off led result:"+ledResult);

            if(emvExtParams!=null && emvExtParams.getMediaType() == 0x00){
                ExtICCardModule extICCardModule = (ExtICCardModule)owner.getExModule(ExModuleType.ICCARD);
                deviceLogger.debug("----[finishEMV]]--iccard poweroff:");
                extICCardModule.powerOff();
            }else{
                deviceLogger.debug("----[finishEMV]]--rfcard poweroff:");
                ExtRFCardModule extRFCardModule = (ExtRFCardModule)owner.getExModule(ExModuleType.RFCARD);
                extRFCardModule.powerOff();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //    @Override
    public String getVersion(int module) {
        if (getSPEmvL3()) {
            return mSPEmvL3Module.getVersion(module);
        }
        byte[] version = new byte[64];
        int ret = mNapiEmvL3.NAPI_L3GetVersion(module, version);
        deviceLogger.debug( "getVersion: ret=" + ret);
        if (ret < 0) {
            return null;
        }
        return new String(version).trim();
    }

    @Override
    public boolean isSignature() {
        if (mEmvL3Usage == EmvL3Usage.INNER) {
            byte[] isSignature = getData(EmvL3Const.L3_DATA.SIGNATURE);
            if (isSignature == null || isSignature.length < 1) {
                return false;
            }
            deviceLogger.debug( ">>>EmvL3Usage=" + mEmvL3Usage + " isSignature=" + isSignature[0]);
            if (isSignature[0] == 0x01) {
                return true;
            }
        } else {
            deviceLogger.debug( ">>>EmvL3Usage=" + mEmvL3Usage + " cvmStatus=" + cvmStatus);
            if (cvmStatus == 0x10) {
                return true;
            }
        }
        return false;
    }

    private boolean getSPEmvL3() {
        return (mEmvL3Usage == EmvL3Usage.INNER ? true : false);
    }

    private int getSPAidInterface(CardContactMode cardIntf) {
        if (cardIntf == CardContactMode.CONTACT) {
            return Aid.CONTACT;
        } else {
            return Aid.CONTACTLESS;
        }
    }


    private int getKernelID(byte[] aid) {
        int kernelID = -1;
        if (null == aid || aid.length < 5)
            return kernelID;
        byte[] adaptAid = new byte[5];
        System.arraycopy(aid, 0, adaptAid, 0, 5);

        if (Arrays.equals(adaptAid, new byte[]{(byte) 0xA0, 0x00, 0x00, 0x00, 0x03})) {
            kernelID = EmvConst.KERNEL_ID_PAYWAVE;//visa
        } else if (Arrays.equals(adaptAid, new byte[]{(byte) 0xA0, 0x00, 0x00, 0x00, 0x04})) {
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
        }
        return kernelID;
    }
    public EmvL3Comm getEmvL3Comm(){
        return mEmvL3Comm;
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

    public static void setMeEmvL3(MEEmvL3 meEmvL3) {
        MEEmvL3.meEmvL3 = meEmvL3;
    }
}
