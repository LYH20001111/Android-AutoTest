package com.newland.sdk.me.module.externaliccard;

import android.content.Context;

import com.newland.sdk.me.module.externalPininput.PinpadPackage;
import com.newland.sdk.module.externalPin.ExtPowerOnExtParams;
import com.newland.sdk.module.externalPin.PinpadInitExtParams;
import com.newland.sdk.module.externaliccard.ExtICCardModule;
import com.newland.sdk.module.externaliccard.TransmitExtParams;
import com.newland.sdk.module.iccard.ICCardType;
import com.newland.sdk.module.pin.AlgorithmMode;
import com.newland.sdk.module.pin.CipherMode;
import com.newland.sdk.module.serialport.PinpadModel;
import com.newland.sdk.mtype.DeviceRTException;
import com.newland.sdk.mtype.ExModuleType;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.mtype.common.ErrorCode;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.mtypex.AbstractModule;
import com.newland.sdk.utils.ISOUtils;

import java.util.Arrays;

/**
 * @author youjf
 * @description
 * @date 2020/6/10
 * @since V3.10.20
 */
public class MEExtICCard extends AbstractModule implements ExtICCardModule {
    private DeviceLogger devicelogger = DeviceLoggerFactory.getLogger("MEExtICCard");
    private PinpadPackage pinpadPackage;
    private Context context;
    private static final byte NAK = 0x15;
    private TransmitExtParams transmitExtParams;

    private PinpadModel pinpadModel = PinpadModel.SP_OVERSEAS;
    public MEExtICCard(AbstractDevice device, Context context){
        super(device);
        pinpadPackage=PinpadPackage.getInstance(device,context);
        pinpadModel = pinpadPackage.getModel();
    }
    @Override
    public boolean init(PinpadInitExtParams params) {
        try {
            devicelogger.debug("[init]params:"+params);
            boolean rs = pinpadPackage.init(params,false);
            pinpadModel=pinpadPackage.getModel();
            return rs;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public byte[] powerOn() {
        try {
            devicelogger.debug("[powerOn]");
            return iccardOperation(0x01, 5, null, null);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean setICCardType(ICCardType cardType, ExtPowerOnExtParams params) {
        try {
            devicelogger.debug("[setICCardType] " + cardType);
            byte[] data = iccardOperation(0x05, 5, null, cardType);
            devicelogger.debug("[setICCardType]data: " + (data == null ? null : ISOUtils.hexString(data)));
            if (data != null && Arrays.equals(data, new byte[]{0x30, 0x30})) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void powerOff() {
        try{
            devicelogger.debug("[powerOff]");
            iccardOperation(0x02, 0, null, null);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public byte[] transmit(byte[] reqApdu, TransmitExtParams transmitExtParams) {
        try{
            devicelogger.debug("[transmit]reqApdu:"+(reqApdu==null?null:ISOUtils.hexString(reqApdu)));
            this.transmitExtParams = transmitExtParams;
            return iccardOperation(0x03, 0, reqApdu, null);

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean isCardIn() {
        try{
            devicelogger.debug("[isCardIn]");
            byte[] data = iccardOperation(0x04, 0, null, null);
            devicelogger.debug("[isCardIn]data:"+(data==null?null:ISOUtils.hexString(data)));
            if(data!=null && Arrays.equals(data,new byte[]{0x30,0x30})){
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * IC 卡上电/下电/通讯
     * @param functionID 01-上电，02-下电，03-通讯
     * @param timeout
     * @param apduData
     * @return
     */
    private byte[] iccardOperation(int functionID,int timeout,byte[] apduData, ICCardType icCardType){
        try {
            byte[] messageType = new byte[]{0x42,0x30};
            byte[] reqData = null;
            if(functionID == 3){
                byte keyIndex = 0;
                byte keyType =0;//0-DES;1-AES
                byte encryMode = 0;//0-ECB; 1-CBC
                int cbcInvDataLen =0;
                if(transmitExtParams!=null){
                    keyIndex = (byte) transmitExtParams.getKeyIndex();
                    if(transmitExtParams.getAlgorithmMode()!=null && transmitExtParams.getAlgorithmMode()==AlgorithmMode.AES){
                        keyType = 0x01;
                    }
                    if(transmitExtParams.getCipherMode()!=null && transmitExtParams.getCipherMode()==CipherMode.CBC){
                        encryMode = 0x01;
                    }
                    if(transmitExtParams.getCbcInv()!=null){
                        cbcInvDataLen = transmitExtParams.getCbcInv().length;
                    }
                }
                devicelogger.debug("[transmit]keyIndex:"+keyIndex+";keyType:"+keyType+";encryMode:"+encryMode+";cbcInvDataLen:"+cbcInvDataLen);
//                if (keyIndex == 0) {
//                    reqData = new byte[1 + 1 + 2 + apduData.length];
//                } else {
                    reqData = new byte[4 + cbcInvDataLen + 2 + apduData.length];
//                }
                reqData[0] = (byte)functionID;
                reqData[1] = (byte)keyIndex;
                int offset = 2;
                if(keyIndex!=0){//明文通讯不需要传密钥类型和加解密模式
                    reqData[2] = (byte)keyType;
                    reqData[3] = (byte)encryMode;
                    offset = 4;
                    if(cbcInvDataLen>0){
                        System.arraycopy(transmitExtParams.getCbcInv(),0,reqData,4,cbcInvDataLen);
                        offset = offset+cbcInvDataLen;
                    }
                }

                byte[] len = InnerUtils.intToBytes(apduData.length,2,true);
                System.arraycopy(len,0,reqData,offset,2);
                System.arraycopy(apduData,0,reqData,offset+2,apduData.length);
                devicelogger.debug("[transmit]apduData:"+(ISOUtils.hexString(apduData)));
            }else if(functionID == 1 || functionID == 4){
                reqData = new byte[3];
                reqData[0] = (byte)functionID;
                byte[] timeOutData = InnerUtils.intToBytes(timeout,2,true);
                devicelogger.debug("[iccardOperation]timeOutData:"+(timeOutData==null?null:ISOUtils.hexString(timeOutData)));
                reqData[1] = timeOutData[0];
                reqData[2] = timeOutData[1];
//                reqData[3] = 0x1C;
//                reqData[4] = 0x1C;
//                reqData[5] = 0x1C;
//                reqData[6] = 0x1C;
            } else if (functionID == 5) {
                reqData = new byte[2];
                reqData[0] = (byte) functionID;
                int type = 0;
                switch (icCardType) {
                    case CPUCARD:
                        type = 0;
                        break;
                    case AT24C01:
                    case AT24C02:
                    case AT24C04:
                    case AT24C08:
                    case AT24C16:
                    case AT24C32:
                    case AT24C64:
                        type = 5;
                        break;
                    case SLE44X2:
                        type = 6;
                        break;
                    case SLE44X8:
                        type = 7;
                        break;
                    case AT88SC102:
                        type = 8;
                        break;
                    case AT88SC1604:
                        type = 9;
                        break;
                    case AT88SC1608:
                        type = 10;
                        break;
                    case ISO7816:
                        type = 11;
                        break;
                    case AT88SC153:
                        type = 12;
                        break;
                    case SAM1:
                        type = 1;
                        break;
                    case SAM2:
                        type = 2;
                        break;
                    case SAM3:
                        type = 3;
                        break;
                    case SAM4:
                        type = 4;
                        break;
                }
                reqData[1] = (byte) type;
            } else if (functionID == 2) {
                reqData = new byte[1];
                reqData[0] = (byte) functionID;
                timeout = 2;
            } else {
                reqData = new byte[5];
                reqData[0] = (byte)functionID;
                reqData[1] = 0x1C;
                reqData[2] = 0x1C;
                reqData[3] = 0x1C;
                reqData[4] = 0x1C;
            }
            devicelogger.debug("[iccardOperation]reqData:"+(ISOUtils.hexString(reqData)));
            if(timeout==0){
                if( functionID != 4){//检测卡槽，时间就设置1秒
                    timeout = PinpadPackage.EXTCMD_TIMEOUT_MS;
                }
            }
            byte[] rspCode = pinpadPackage.sendPinpadCmd(messageType, reqData, (timeout)*1000+PinpadPackage.EXTCMD_OFFSETTIME_MS,true);
            if (rspCode == null || rspCode[0] == NAK) {
                throw new DeviceRTException(ErrorCode.DEVICE_INVOKE_FAILED, "ICCard operation failed" + ",res=" + (rspCode == null ? "null" : InnerUtils.hexString(rspCode)));

            } else {
                pinpadPackage.getPinpadRspCode();
                devicelogger.debug("[iccardOperation]rspCode:"+InnerUtils.hexString(rspCode));
                if (Arrays.equals(new byte[]{rspCode[0], rspCode[1]}, new byte[]{0x42, 0x31})) {
                    byte functionId = rspCode[3];
                    devicelogger.debug("[iccardOperation]functionId:"+functionId);
                    byte[] respondCode = new byte[]{rspCode[4], rspCode[5]};
                    devicelogger.info("[iccardOperation]ICCard operation respond code:"+ISOUtils.hexString(respondCode));
                    if(Arrays.equals(respondCode,new byte[]{0x30,0x30})){
                        if(functionID == 0x03){
                            int keyIndex = rspCode[6]&0xFF;
                            devicelogger.debug("keyIndex:"+keyIndex);
                            int dataLen = InnerUtils.bytesToInt(new byte[]{rspCode[7],rspCode[8]},0,2,true);
                            devicelogger.debug("[iccardOperation] dataLen:"+dataLen);
                            byte[] apduRspData = new byte[dataLen];
                            if(keyIndex>0){
                                int encryDataLen = InnerUtils.bytesToInt(new byte[]{rspCode[9],rspCode[10]},0,2,true);
                                devicelogger.debug("[iccardOperation] encryDataLen:"+encryDataLen);
                                apduRspData = new byte[encryDataLen];
                                System.arraycopy(rspCode,11,apduRspData,0,encryDataLen);
                                devicelogger.debug("[iccardOperation] encry apdu RspData:"+ISOUtils.hexString(apduRspData));
                                return apduRspData;
                            }else{
                                System.arraycopy(rspCode,11,apduRspData,0,dataLen);
                                devicelogger.debug("[iccardOperation] apduRspData:"+ISOUtils.hexString(apduRspData));
                                return apduRspData;
                            }

                        }else if(functionID == 0x01){
                            int atrLen = InnerUtils.bytesToInt(new byte[]{rspCode[6],rspCode[7]},0,2,true);
                            devicelogger.debug("[iccardOperation] atrLen:"+atrLen);
                            byte[] atrData = new byte[atrLen];
                            System.arraycopy(rspCode,8,atrData,0,atrLen);
                            devicelogger.debug("[iccardOperation]atrData:"+ISOUtils.hexString(atrData));
                            return atrData;
                        }else if(functionID == 0x04 || functionID == 0x05){
                            return respondCode;
                        }
                    }else {
                        devicelogger.error("[iccardOperation]respondCode error");
                    }
                }else{
                    devicelogger.error("[iccardOperation]message Type erroe");
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
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
        return ExModuleType.ICCARD;
    }
}
