package com.newland.sdk.me.module.rfcard;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import com.newland.ndk.NdkApiManager;
import com.newland.ndk.RfCard;
import com.newland.sdk.me.cmd.rfcard.CmdFelicaCardTransmit;
import com.newland.sdk.me.cmd.rfcard.CmdM0CardAuthenticate;
import com.newland.sdk.me.cmd.rfcard.CmdM0CardReadData;
import com.newland.sdk.me.cmd.rfcard.CmdM0CardWriteData;
import com.newland.sdk.me.cmd.rfcard.CmdM1CardAuthenticate;
import com.newland.sdk.me.cmd.rfcard.CmdM1CardDecrement;
import com.newland.sdk.me.cmd.rfcard.CmdM1CardIncrement;
import com.newland.sdk.me.cmd.rfcard.CmdM1CardReadData;
import com.newland.sdk.me.cmd.rfcard.CmdM1CardReadData.CmdM1CardReadDataResponse;
import com.newland.sdk.me.cmd.rfcard.CmdM1CardWriteData;
import com.newland.sdk.me.cmd.rfcard.CmdRFCardATS;
import com.newland.sdk.me.cmd.rfcard.CmdRFCardInduct;
import com.newland.sdk.me.cmd.rfcard.CmdRFCardPowerOff;
import com.newland.sdk.me.cmd.rfcard.CmdRFCardPowerOn;
import com.newland.sdk.me.cmd.rfcard.CmdRFCardPowerOn.CmdRFCardPowerOnResponse;
import com.newland.sdk.me.cmd.rfcard.CmdRFCardTransmit;
import com.newland.sdk.module.rfcard.RFCardMode;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.mtype.common.ErrorMsg;
import com.newland.sdk.mtype.common.ErrorMsgHelper;
import com.newland.sdk.module.rfcard.RFCardModule;
import com.newland.sdk.module.rfcard.RFCardPowerOnExtParams;
import com.newland.sdk.module.rfcard.RFCardType;
import com.newland.sdk.module.rfcard.RFKeyMode;
import com.newland.sdk.module.rfcard.RFResult;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.utils.ISOUtils;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.mtypex.AbstractModule;

import static com.newland.sdk.me.cmd.CmdCode.RFID_POWERON;

public class MERFCard extends AbstractModule implements RFCardModule {
    private DeviceLogger deviceLogger = DeviceLoggerFactory.getLogger("MERFCard");

    private RfCard mRfCard;
    private Object rfSync = new Object();

    public MERFCard(AbstractDevice owner) {
        super(owner);
        mRfCard = NdkApiManager.getNdkApiManager().getRfCard();
    }

    @Override
    public boolean isStandardModule() {
        return true;
    }

    @Override
    public ModuleType getStandardModuleType() {
        return ModuleType.RFCARDREADER;
    }

    @Override
    public String getExModuleType() {
        return null;
    }

    private byte[] fetchApduData(byte[] dataLength) {
        byte[] header = ISOUtils.hex2byte("00C00000");
        byte[] sendData = new byte[header.length + dataLength.length];
        System.arraycopy(header, 0, sendData, 0, header.length);
        System.arraycopy(dataLength, 0, sendData, header.length, dataLength.length);
        CmdRFCardTransmit.CmdRFCardTransmitResponse response = (CmdRFCardTransmit.CmdRFCardTransmitResponse) super.invoke(new CmdRFCardTransmit(sendData));
        if (null != response) {
            return response.getData();
        }
        throw new NullPointerException("response is null!");
    }

    @Override
    public RFResult powerOn(RFCardType[] rfCardType, int timeout, RFCardPowerOnExtParams powerOnExtParams) {
        deviceLogger.debug("[powerOn] rfCardType:" + Arrays.toString(rfCardType) + "; timeout" + timeout);
        int invokeTimeout = timeout + 3;
        CmdRFCardPowerOnResponse response = null;
        if (powerOnExtParams == null) {
            response = (CmdRFCardPowerOnResponse) super.invoke(new CmdRFCardPowerOn(rfCardType, timeout), invokeTimeout, TimeUnit.SECONDS);
        } else {
            if (powerOnExtParams.getFelicaParams() != null) {
                response = (CmdRFCardPowerOnResponse) super.invoke(new CmdRFCardPowerOn(rfCardType, timeout, powerOnExtParams.getFelicaParams()), invokeTimeout, TimeUnit.SECONDS);
            } else if ((byte) 0xFF != powerOnExtParams.getSak()) {
                response = (CmdRFCardPowerOnResponse) super.invoke(new CmdRFCardPowerOn(rfCardType, timeout, powerOnExtParams.getSak()), invokeTimeout, TimeUnit.SECONDS);
            } else {
                response = (CmdRFCardPowerOnResponse) super.invoke(new CmdRFCardPowerOn(rfCardType, timeout), invokeTimeout, TimeUnit.SECONDS);
            }
        }
        if (null != response) {
            return new RFResult(response.getRFCardType(), response.getCardSerialNo(), response.getATQA(), response.getSAK(), response.getIDmAndPMm(), response.getAts());
        }
        ErrorMsg msg = ErrorMsgHelper.getInstance().getErrorMsg(RFID_POWERON);
        throw new NullPointerException("response is null!"+" ErrCode:"+msg.getErrCode()+" ErrMsg:"+msg.getErrMsg()+" OtherMsg:"+msg.getOtherMsg());
    }

    @Override
    public boolean powerOff() {
        try {
            deviceLogger.debug("[powerOff]");
            super.invoke(new CmdRFCardPowerOff());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isCardExist() {
        try {
            deviceLogger.debug("[isCardExist]");
            invoke(new CmdRFCardInduct());
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    @Override
    public byte[] transmit(byte[] command, long timeout) {
        deviceLogger.debug("[transmit] timeout:"+timeout+";command:"+(command==null?null:ISOUtils.hexString(command)));
        CmdRFCardTransmit.CmdRFCardTransmitResponse response = (CmdRFCardTransmit.CmdRFCardTransmitResponse) super.invoke(new CmdRFCardTransmit(command), timeout, TimeUnit.MICROSECONDS);
        byte[] apduData = null;
        if (null != response) {
            apduData = response.getData();
            if (null != apduData) {
                String apduStr = ISOUtils.hexString(apduData);
                if (apduData.length == 2 && apduStr.startsWith("61")) {
                    byte[] dataleg = new byte[]{apduData[1]};
                    apduData = fetchApduData(dataleg);
                }
                return apduData;

            }
        }
        throw new NullPointerException("response is null!");
    }

    @Override
    public byte[] felicaTransmit(byte[] command, long timeout) {
        deviceLogger.debug("[felicaTransmit] timeout:"+timeout+"; ");
        CmdFelicaCardTransmit.CmdFelicaCardTransmitResponse response = (CmdFelicaCardTransmit.CmdFelicaCardTransmitResponse) super.invoke(new CmdFelicaCardTransmit(command), timeout, TimeUnit.MICROSECONDS);
        byte[] apduData = null;
        if (null != response) {
            apduData = response.getData();
            if (null != apduData) {
                String apduStr = ISOUtils.hexString(apduData);
                if (apduData.length == 2 && apduStr.startsWith("61")) {
                    byte[] dataleg = new byte[]{apduData[1]};
                    apduData = fetchApduData(dataleg);
                }
                return apduData;

            }
        }
        throw new NullPointerException("response is null!");
    }

    @Override
    public boolean m1Authenticate(RFKeyMode rfKeyMode, byte[] snr, int blockNo, byte[] key) {
        try {
            deviceLogger.debug("[m1Authenticate] rfKeyMode:"+rfKeyMode+";blockNo:"+blockNo+";key:"+(key==null?null:ISOUtils.hexString(key)));
            super.invoke(new CmdM1CardAuthenticate(rfKeyMode, snr, blockNo, key));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public byte[] m1ReadBlockData(int blockNo) {
        deviceLogger.debug("[m1ReadBlockData] blockNo:"+blockNo);
        CmdM1CardReadDataResponse response = (CmdM1CardReadDataResponse) super.invoke(new CmdM1CardReadData(blockNo));
        if (null != response) {
            return response.getData();
        }
        throw new NullPointerException("response is null!");
    }

    @Override
    public boolean m1WriteBlockData(int blockNo, byte[] data) {
        try {
            deviceLogger.debug("[m1WriteBlockData] blockNo:"+blockNo);
            super.invoke(new CmdM1CardWriteData(blockNo, data));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean m1Increment(int blockNo, byte[] data) {
        try {
            deviceLogger.debug("[m1Increment] blockNo:"+blockNo+";data:"+(data==null?null:ISOUtils.hexString(data)));
            super.invoke(new CmdM1CardIncrement(blockNo, data));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean m1Decrement(int blockNo, byte[] data) {
        try {
            deviceLogger.debug("[m1Decrement] blockNo:"+blockNo+";data:"+(data==null?null:ISOUtils.hexString(data)));
            super.invoke(new CmdM1CardDecrement(blockNo, data));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public byte[] getCardATS() {
        deviceLogger.debug("[getCardATS]");
        CmdRFCardATS.CmdRFCardATSResponse response = (CmdRFCardATS.CmdRFCardATSResponse)invoke(new CmdRFCardATS());
        if (null != response) {
            return response.getATS();
        }
        throw new NullPointerException("response is null!");
    }

    @Override
    public boolean m0Authenticate(byte[] command) {
        try {
            deviceLogger.debug("[m0Authenticate] command:"+(command==null?null:ISOUtils.hexString(command)));
            super.invoke(new CmdM0CardAuthenticate(command));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public byte[] m0ReadBlockData(int blockNo) {
        deviceLogger.debug("[m0ReadBlockData] blockNo:"+blockNo);
        CmdM0CardReadData.CmdM0CardReadDataResponse response = (CmdM0CardReadData.CmdM0CardReadDataResponse) super.invoke(new CmdM0CardReadData(blockNo));
        if (null != response) {
            return response.getData();
        }
        throw new NullPointerException("response is null!");
    }

    @Override
    public boolean m0WriteBlockData(int blockNo, byte[] data) {
        try {
            deviceLogger.debug("[m0WriteBlockData] blockNo:"+blockNo+";data:"+(data==null?null:ISOUtils.hexString(data)));
            super.invoke(new CmdM0CardWriteData(blockNo, data));
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    @Override
    public byte[] communication(byte[] sendData, int timeOut) {
        try {
            deviceLogger.debug("[Rfid communication] sendData="+ISOUtils.hexString(sendData)+" timeOut="+timeOut);
            if(sendData == null){
                return null;
            }
            byte[] receiveData = new byte[1024];
            int[] len = new int[1];
            int ret = mRfCard.NDK_RfidPiccApduInTransMode(sendData,sendData.length,receiveData,len,timeOut);
            deviceLogger.debug("[Rfid communication] ret="+ret);
            if(ret != 0){
                return null;
            }
            byte[] targetFb = new byte[len[0]];
            System.arraycopy(receiveData,0,targetFb,0,targetFb.length);
            deviceLogger.debug("[Rfid communication] receiveData="+ISOUtils.hexString(targetFb));
            return targetFb;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean setRFMode(RFCardMode rfCardMode) {
        deviceLogger.info("[setRFMode] rfCardMode:" + ((rfCardMode!=null) ? rfCardMode.getMode() : null));
        if (rfCardMode == null){
            return false;
        }
        try {
            /**
             * ret
             *  -1 机器配置文件不存在小卡参数切换失败使用默认参数
             *  -3 卡片激活中
             *  -6 参数错误或者不支持
             */
            int ret = NdkApiManager.getNdkApiManager().getRfCard().NDK_RfidConfig(rfCardMode.getMode());
            deviceLogger.info("[setRFMode] ret:" + ret);
            if (ret == 0){
                deviceLogger.info("[setRFMode] success.");
                return true;
            } else {
                deviceLogger.error("[setRFMode] failed.");
                return false;
            }
        }catch (Exception e){
            deviceLogger.error("[setRFMode] Exception.");
            e.printStackTrace();
            return false;
        }
    }
}
