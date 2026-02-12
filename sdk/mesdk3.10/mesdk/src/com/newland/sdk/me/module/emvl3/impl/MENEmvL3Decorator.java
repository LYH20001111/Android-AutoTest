package com.newland.sdk.me.module.emvl3.impl;

import android.content.Context;
import android.support.annotation.NonNull;
import com.newland.sdk.me.module.emv.AbstractEMVTransController;
import com.newland.sdk.me.module.emv.EMVInnerUtils;
import com.newland.sdk.me.module.emv.EMVLevel2Const;
import com.newland.sdk.module.emv.CardInterface;
import com.newland.sdk.module.emv.EMVTransInfo;
import com.newland.sdk.module.emv.EmvPackager;
import com.newland.sdk.mtype.common.Const;
import com.newland.sdk.mtype.common.Const.EmvStandardReference;
import com.newland.sdk.mtype.common.Const.EmvSelfDefinedReference;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.me.module.emvl3.impl.EmvL3Step.EmvL3ListenerStep;
import com.newland.sdk.utils.ISOUtils;
import com.newland.sdk.utils.TLVPackage;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description 负责SDK特殊处理的AID、终端参数等.
 *              将SDK特殊化的代码统一在该装饰类处理.
 * @Author wuhh
 * @Date 2020/11/10
 */
public class MENEmvL3Decorator extends MENEmvL3{
    private DeviceLogger deviceLogger = DeviceLoggerFactory.getLogger("MENEmvL3Decorator");
    private volatile EmvL3ListenerStep mLastEmvStep;
    public MENEmvL3Decorator(Context context, AbstractDevice owner) {
        super(context, owner);
    }
    private static List<Integer> L_55TAGS = new ArrayList<Integer>();

    static {
        L_55TAGS.add(0x9f26);
        L_55TAGS.add(0x9F10);
        L_55TAGS.add(0x9F27);
        L_55TAGS.add(0x9F37);
        L_55TAGS.add(0x9F36);
        L_55TAGS.add(0x95);
        L_55TAGS.add(0x9A);
        L_55TAGS.add(0x9C);
        L_55TAGS.add(0x9F02);
        L_55TAGS.add(0x5F2A);
        L_55TAGS.add(0x82);
        L_55TAGS.add(0x9F1A);
        L_55TAGS.add(0x9F03);
        L_55TAGS.add(0x9F33);
        L_55TAGS.add(0x9F34);
        L_55TAGS.add(0x9F35);
        L_55TAGS.add(0x9F1E);
        L_55TAGS.add(0x84);
        L_55TAGS.add(0x9F09);
        L_55TAGS.add(0x9F41);
        L_55TAGS.add(0x8a);
        L_55TAGS.add(0x9f63);
        // 为了优化EMV l3，增加以下两个tag
        L_55TAGS.add(0x9b);
        L_55TAGS.add(0x50);

        L_55TAGS.add(0x9F33);
        L_55TAGS.add(0x9F34);
        L_55TAGS.add(0x9F35);
        L_55TAGS.add(0x95);
        L_55TAGS.add(0x9F37);
        L_55TAGS.add(0x9F1E);
        L_55TAGS.add(0x9F10);
        L_55TAGS.add(0x9F26);
        L_55TAGS.add(0x9F27);
        L_55TAGS.add(0x9F36);
        L_55TAGS.add(0x82);
        L_55TAGS.add(0xDF31);
        L_55TAGS.add(0x9F1A);
        L_55TAGS.add(0x9A);
        L_55TAGS.add(0x9C);
        L_55TAGS.add(0x9F02);
        L_55TAGS.add(0x5F2A);
        L_55TAGS.add(0x84);
        L_55TAGS.add(0x9F09);
        L_55TAGS.add(0x9F41);
        L_55TAGS.add(0x9F63);

        L_55TAGS.add(0x95);
        L_55TAGS.add(0x9F1E);
        L_55TAGS.add(0x9F10);
        L_55TAGS.add(0x9F36);
        L_55TAGS.add(0xDF31);
        L_55TAGS.add(0x5A);
        L_55TAGS.add(0x5F34);
        L_55TAGS.add(0x57);
        L_55TAGS.add(0x5F24);
        L_55TAGS.add(0x9F5D);
        L_55TAGS.add(0x9F6C);
        L_55TAGS.add(0x5F20);
        L_55TAGS.add(0x4F);
        L_55TAGS.add(0x9F06);
        L_55TAGS.add(0x9F11);
        L_55TAGS.add(0x9F12);
        L_55TAGS.add(0x87);
        L_55TAGS.add(0x9F5A);
        L_55TAGS.add(0x9F6E);
    }

    private boolean isEnableSpecialParam(){
        //返回false表示禁用SDK特殊处理部分,排除问题可能用得到.
        return true;
    }

    @Override
    public EMVTransInfo getEMVTransInfo() {
        //TODO 除EmvErrorCode、EmvrsltCode和ExecuteRslt,其他TransInfo根据EMV步骤统一在这个函数获取.
        int errorCode = mEmvTransInfo.getErrorcode();
        int resultCode = mEmvTransInfo.getEmvrsltCode();
        EmvL3ListenerStep currEmvStep = mEmvL3Step.getEmvStep();
        deviceLogger.info("getEMVTransInfo "+mLastEmvStep+"->"+currEmvStep);
        if(mLastEmvStep != currEmvStep && currEmvStep!=null){
            //EMVTransInfo.java: 0x5A,0x5F34,0x57,0x5F24,0x9F5D,0x9F6c,0x5F20,0xDF75,0x9F79,0x9f77,0x9f51,0xdf71,
            //MEEMVLevel2.java:  0x5A,0x5F34,0x57,0x5F24,0x9F5D,0x9F6C,0x5F20,0xDF37(KERNELID),
            //EMVTransInfo.java里面的解析的tag和MEEMVLevel2.java里defaultTags获取的tag对不上.
            //这里取合集,EMVTransInfo.java其他方法有需要再处理.


            //外接键盘+底座，每次取很慢，getEMVTransInfo把大部分tag都取了，应用不需要再getemvData
            int[] temp = new int[L_55TAGS.size()];
            int i = 0;
            for (int tag : L_55TAGS) {
                temp[i] = tag;
                i++;
            }
         //   TLVPackage tlvPackage = getEmvData(new int[]{0x5A,0x5F34,0x57,0x5F24,0x9F5D,0x9F6C,0x5F20});
            TLVPackage tlvPackage = getEmvData(temp);

            byte[] tlvData = tlvPackage.pack();
            deviceLogger.debug("=========getEMVTransInfo tlvData="+(tlvData==null?"null": InnerUtils.hexString(tlvData)));
            EmvPackager packager = EMVInnerUtils.newEmvPackager();
            packager.unpack(tlvData, EMVTransInfo.class, mEmvTransInfo);
            mEmvTransInfo.setTlvData(tlvData);
        }
        mLastEmvStep = currEmvStep;
        return mEmvTransInfo;
    }

    @Override
    public int getEmvrsltCode(int resultCode,boolean isComplete){
        if(resultCode == EmvL3Constant.TransResult.L3_TXN_OK){
            return EMVLevel2Const.EmvExecRslt.EMV_TRANS_ACCEPT;
        } else if(resultCode == EmvL3Constant.TransResult.L3_TXN_TERMINATE){
            return EMVLevel2Const.EmvExecRslt.EMV_TRANS_TERMINATE;
        } else if(resultCode == EmvL3Constant.TransResult.L3_TXN_TRY_ANOTHER){
            return EMVLevel2Const.EmvExecRslt.EMV_TRANS_FALLBACK;
        } else if(resultCode == EmvL3Constant.TransResult.L3_TXN_DECLINE){
            if(isComplete){
                return EMVLevel2Const.EmvExecRslt.EMV_TRANS_2GAC_AAC;
            }else {
                return EMVLevel2Const.EmvExecRslt.EMV_TRANS_DENIAL;
            }
        } else if(resultCode == EmvL3Constant.TransResult.L3_TXN_APPROVED){
            return EMVLevel2Const.EmvExecRslt.EMV_TRANS_ACCEPT;
        } else if(resultCode == EmvL3Constant.TransResult.L3_TXN_ONLINE){
            return EMVLevel2Const.EmvExecRslt.EMV_TRANS_GOONLINE;
        }
        return EMVLevel2Const.EmvExecRslt.EMV_TRANS_DENIAL;
    }

    @Override
    public int getExecuteRslt(int resultCode,boolean isComplete){
        if(resultCode == EmvL3Constant.TransResult.L3_TXN_OK){
            return AbstractEMVTransController._EMV_RSLT_STEP_SUCCESS;
        } else if(resultCode == EmvL3Constant.TransResult.L3_TXN_TERMINATE){
            return AbstractEMVTransController._EMV_RSLT_STEP_FAILED;
        } else if(resultCode == EmvL3Constant.TransResult.L3_TXN_TRY_ANOTHER){
            return AbstractEMVTransController._EMV_RSLT_STEP_FAILED;
        } else if(resultCode == EmvL3Constant.TransResult.L3_TXN_DECLINE){
            if(isComplete){
                return AbstractEMVTransController._EMV_RSLT_SECOND_AAC;
            }else {
                return AbstractEMVTransController._EMV_RSLT_AAC;
            }
        } else if(resultCode == EmvL3Constant.TransResult.L3_TXN_APPROVED){
            return AbstractEMVTransController._EMV_RSLT_TC;
        } else if(resultCode == EmvL3Constant.TransResult.L3_TXN_ONLINE){
            return AbstractEMVTransController._EMV_RSLT_ARQC;
        }
        return AbstractEMVTransController._EMV_RSLT_STEP_FAILED;
    }
    @Override
    public boolean setTerminalConfiguration(byte[] tlvData, CardInterface aidStorageMode) {
        if(isEnableSpecialParam()){
            //TODO 针对终端参数特殊处理的代码写着这里.

        }
        return super.setTerminalConfiguration(tlvData,aidStorageMode);
    }

    @Override
    public void setIndicatorsAndBeep(boolean isEnable) {

    }

    @Override
    public boolean addAID(@NonNull byte[] inputData, CardInterface aidStorageMode) {
        byte[] inputData2 = inputData;
        if(isEnableSpecialParam()) {
            //TODO 针对AID特殊处理的代码写着这里.

            /*
            //1.外接EMVL3处理不了标准的DF3F的长度
            TLVPackage tlvPackage = InnerUtils.newTlvPackage();
            tlvPackage.unpack(inputData);
            int tagDRL = 0xDF3F;
            boolean hasDrl = tlvPackage.hasTag(tagDRL);
            if(hasDrl){
                String drlValue = tlvPackage.getString(tagDRL);
                tlvPackage.deleteByTag(tagDRL);
                inputData2 = ISOUtils.hex2byte(ISOUtils.hexString(tlvPackage.pack())+ "DF3F"+String.format("%04x",drlValue.length()/2)+drlValue);
                deviceLogger.debug("hasDrl inputData1="+hexString(inputData));
                deviceLogger.debug("hasDrl inputData2="+hexString(inputData2));
            }
            */
        }
        return super.addAID(inputData2,aidStorageMode);
    }

    @Override
    public boolean addCAPublicKey(@NonNull byte[] inputData) {
        if(isEnableSpecialParam()) {
            //TODO 针对CAPK特殊处理的代码写着这里.

        }
        return super.addCAPublicKey(inputData);
    }

    //TODO 其他需要特殊处理的函数也统一归纳到这里.

    private String hexString(byte[] data){
        return (data==null?"null":ISOUtils.hexString(data));
    }
}

