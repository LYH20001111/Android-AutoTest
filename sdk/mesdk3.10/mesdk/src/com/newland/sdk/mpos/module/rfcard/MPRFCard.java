package com.newland.sdk.mpos.module.rfcard;

import android.content.Context;
import android.support.annotation.Nullable;

import com.newland.sdk.me.module.externalrfcard.MeExternalRFCard;
import com.newland.sdk.module.externalrfcard.ExtRFCardModule;
import com.newland.sdk.module.rfcard.RFCardMode;
import com.newland.sdk.module.rfcard.RFCardModule;
import com.newland.sdk.module.rfcard.RFCardPowerOnExtParams;
import com.newland.sdk.module.rfcard.RFCardType;
import com.newland.sdk.module.rfcard.RFKeyMode;
import com.newland.sdk.module.rfcard.RFResult;
import com.newland.sdk.mtype.Device;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.utils.ISOUtils;

/**
 * @Description
 * @Author wuhh
 * @Date 2021/3/16
 */
public class MPRFCard implements RFCardModule {
    private DeviceLogger devicelogger = DeviceLoggerFactory.getLogger("MPRFCard");
    ExtRFCardModule mExtRFCardModule;

    public MPRFCard(AbstractDevice device, Context context){
        mExtRFCardModule = new MeExternalRFCard(device,context);
    }
    @Override
    public RFResult powerOn(RFCardType[] rfCardType, int timeout, @Nullable RFCardPowerOnExtParams powerOnExtParams) {
        devicelogger.debug("[powerOn] "+rfCardType.toString()+" timeout="+timeout);
        RFResult result = mExtRFCardModule.powerOn(rfCardType,timeout);
        if(result!=null){
            devicelogger.debug("[powerOn] getRfcardType="+result.getRfcardType()+" getSNR="+ ISOUtils.hexString(result.getSNR())+" getATQA="+ISOUtils.hexString(result.getATQA()));
        }
        return result;
    }

    @Override
    public boolean powerOff() {
        devicelogger.debug("[powerOff]");
        boolean result = mExtRFCardModule.powerOff();
        devicelogger.debug("[powerOff] result="+result);
        return result;
    }

    @Override
    public boolean isCardExist() {
        devicelogger.debug("[isCardExist]");
        boolean isExist = mExtRFCardModule.isCardExist();
        devicelogger.debug("[isCardExist] isExist="+isExist);
        return isExist;
    }

    @Override
    public byte[] transmit(byte[] command, long timeout) {
        devicelogger.debug("[transmit] command="+ISOUtils.hexString(command)+" timeout="+timeout);
        byte[] respData = mExtRFCardModule.transmit(command);
        devicelogger.debug("[transmit] respData="+ISOUtils.hexString(respData));
        return respData;
    }

    @Override
    public byte[] felicaTransmit(byte[] command, long timeout) {
        devicelogger.debug("[felicaTransmit]");
        return null;
    }

    @Override
    public boolean m0Authenticate(byte[] command) {
        devicelogger.debug("[m0Authenticate]");
        return false;
    }

    @Override
    public byte[] m0ReadBlockData(int blockNo) {
        devicelogger.debug("[m0ReadBlockData] blockNo="+blockNo);
        return null;
    }

    @Override
    public boolean m0WriteBlockData(int blockNo, byte[] data) {
        devicelogger.debug("[m0WriteBlockData] blockNo="+blockNo);
        return false;
    }

    @Override
    public boolean m1Authenticate(RFKeyMode rfKeyMode, byte[] snr, int blockNo, byte[] key) {
        devicelogger.debug("[m1Authenticate] rfKeyMode="+rfKeyMode+" snr="+ISOUtils.hexString(snr)+" blockNo="+blockNo+" key="+ISOUtils.hexString(key));
        boolean result = mExtRFCardModule.m1Authenticate(rfKeyMode,snr,blockNo,key);
        devicelogger.debug("[m1Authenticate] result="+result);
        return result;
    }

    @Override
    public byte[] m1ReadBlockData(int blockNo) {
        devicelogger.debug("[m1ReadBlockData] blockNo="+blockNo);
        byte[] respData = mExtRFCardModule.readBlockData(blockNo);
        devicelogger.debug("[m1ReadBlockData] respData="+ISOUtils.hexString(respData));
        return respData;
    }

    @Override
    public boolean m1WriteBlockData(int blockNo, byte[] data) {
        devicelogger.debug("[m1WriteBlockData] blockNo="+blockNo+" data="+ISOUtils.hexString(data));
        boolean result = mExtRFCardModule.writeBlockData(blockNo,data);
        devicelogger.debug("[m1WriteBlockData] result="+result);
        return result;
    }

    @Override
    public boolean m1Increment(int blockNo, byte[] data) {
        devicelogger.debug("[m1Increment] blockNo="+blockNo+" data="+ISOUtils.hexString(data));
        boolean result = mExtRFCardModule.m1Increment(blockNo,data);
        devicelogger.debug("[m1Increment] result="+result);
        return result;
    }

    @Override
    public boolean m1Decrement(int blockNo, byte[] data) {
        devicelogger.debug("[m1Decrement] blockNo="+blockNo+" data="+ISOUtils.hexString(data));
        boolean result = mExtRFCardModule.m1Decrement(blockNo,data);
        devicelogger.debug("[m1Decrement] result="+result);
        return result;
    }

    @Override
    public byte[] getCardATS() {
        devicelogger.debug("[getCardATS]");
        byte[] ats = mExtRFCardModule.getCardATS();
        devicelogger.debug("[getCardATS] ats="+ISOUtils.hexString(ats));
        return null;
    }

    @Override
    public byte[] communication(byte[] sendData, int timeOut) {
        throw new UnsupportedOperationException("This method is not supported in MPOS.");
    }

    @Override
    public boolean setRFMode(RFCardMode rfCardMode) {
        return false;
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
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }
}
