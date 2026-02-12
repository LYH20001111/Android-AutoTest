package com.newland.sdk.me.module.externalmagiccard;

import android.content.Context;

import com.newland.sdk.me.module.externalCardreader.MEExtCardReader;
import com.newland.sdk.me.module.externalPininput.PinpadPackage;
import com.newland.sdk.module.externalPin.PinpadInitExtParams;
import com.newland.sdk.module.externalmagic.ExtMagicCardModule;
import com.newland.sdk.module.pin.AlgorithmMode;
import com.newland.sdk.module.pin.CipherMode;
import com.newland.sdk.module.pin.KeyManagement;
import com.newland.sdk.module.serialport.PinpadModel;
import com.newland.sdk.module.swiper.Account;
import com.newland.sdk.module.swiper.SwipExtParams;
import com.newland.sdk.module.swiper.SwipResult;
import com.newland.sdk.module.swiper.SwiperReadModel;
import com.newland.sdk.mtype.DeviceRTException;
import com.newland.sdk.mtype.ExModuleType;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.mtype.common.ErrorCode;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.mtypex.AbstractModule;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author youjf
 * @description
 * @date 2020/6/9
 * @since V3.10.20
 */
public class MEExtMagStripeCard  extends AbstractModule implements ExtMagicCardModule {
    private DeviceLogger devicelogger = DeviceLoggerFactory.getLogger("MEExtMagStripeCard");
    private PinpadPackage pinpadPackage;
    private Context context;
    private static final int TIMEOUT = 5000;
    private static final byte NAK = 0x15;

    private PinpadModel pinpadModel = PinpadModel.SP_OVERSEAS;

    public MEExtMagStripeCard(AbstractDevice device, Context context) {
        super(device);
        this.context = context;
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
    public SwipResult readPlainResult(SwiperReadModel[] readModel) {
        try {
            devicelogger.debug("[readPlainResult] readModel:"+readModel+";pinpadModel:"+pinpadModel+";isGlobalCheckUnionCard:" + MEExtCardReader.isGlobalCheckUnionCard());
            return readTrackData(0,readModel,AlgorithmMode.DES,CipherMode.ECB,null,MEExtCardReader.isGlobalCheckUnionCard());

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public SwipResult readPlainResultWithoutOpen(int timeout, SwiperReadModel[] readModel) {
        try {
            devicelogger.debug("[readPlainResultWithoutOpen]");
            return readTrackDataWithoutOpen(timeout,0,readModel,null,null,null,null);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public SwipResult readEncryptResult(KeyManagement keyManagement, int keyIndex,SwiperReadModel[] readModel,AlgorithmMode algorithmMode, CipherMode cipherMode, SwipExtParams swipExtParams) {
        try {
            devicelogger.debug("[readEncryptResult],keyManagement:"+keyManagement+";keyIndex:"+keyIndex+";algorithmMode:"+algorithmMode+";cipherMode:"+cipherMode+";pinpadModel:"+pinpadModel+";isGlobalCheckUnionCard:"+MEExtCardReader.isGlobalCheckUnionCard());
            byte[] cbcInit = null;
            if(swipExtParams!=null && swipExtParams.getCbcInitialVector()!=null){
                cbcInit = swipExtParams.getCbcInitialVector();
                devicelogger.debug(InnerUtils.hexString(cbcInit));
            }
            return readTrackData(keyIndex,readModel,algorithmMode,cipherMode,cbcInit,MEExtCardReader.isGlobalCheckUnionCard());
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * SP100 读取磁道数据
     * @param keyIndex 0：读取明文磁道
     * @param swiperReadModel
     * @param algorithmMode 0-DES, 1-AES
     * @param cipherMode 0-DES_CBC; 1-DES_ECB;2-AES
     * @return
     */
    private SwipResult readTrackData(int keyIndex,SwiperReadModel[] swiperReadModel,AlgorithmMode algorithmMode,CipherMode cipherMode,byte[] cbcInit,boolean checkUnionCard){
        try {

            byte[] messageType = new byte[]{0x42,0x44};
            byte[] reqData = null;
            int cbcInitLen = 0;
            if(cbcInit!=null){
                reqData = new byte[15];
                cbcInitLen = 8;
            }else{
                reqData = new byte[7];
            }
            reqData[0] = (byte) keyIndex;
            byte keyType = 0x00;//DES
            byte encryMode = 0x00;//DES_ECB
            if(algorithmMode!=null && algorithmMode == AlgorithmMode.AES){
                keyType = 0x01;
                encryMode = 0x00;
            }
            reqData[1] = (byte) keyType;

            if(cipherMode!=null && cipherMode==CipherMode.CBC){
//                encryMode = 0x01;//DES_CBC
                return null; // 指令集CBC模式不支持，阳灵杰说传下来的话，外接键盘必挂,所以直接返回null。 20230414 by linsi

            }
            reqData[2] = (byte) encryMode;
            int offset = 3;
            if(cbcInitLen>0){
                System.arraycopy(cbcInit,0,reqData,3,8);
                offset = offset +8;
            }
            byte track1 = 0x00;
            byte track2 = 0x00;
            byte track3 = 0x00;
            if(swiperReadModel!=null && swiperReadModel.length>0){
                for(SwiperReadModel model:swiperReadModel){
                    switch (model) {
                        case FIRST_TRACK:
                            track1 = 0x01;
                            break;
                        case SECOND_TRACK:
                            track2 = 0x01;
                            break;
                        case THIRD_TRACK:
                            track3 = 0x01;
                            break;
                    }
                }
            }
            reqData[offset] = track1;
            reqData[offset+1] = track2;
            reqData[offset+2] = track3;

            if (checkUnionCard){
                // 校验传0
                reqData[offset+3] = 0;
            } else {
                // 不校验传1
                reqData[offset+3] = 1;
            }

            devicelogger.debug("[readTrackData]reqData:"+(InnerUtils.hexString(reqData)));
            byte[] rspCode = pinpadPackage.sendPinpadCmd(messageType, reqData, PinpadPackage.EXTCMD_TIMEOUT_MS,true);
            if (rspCode == null || rspCode[0] == NAK) {
                throw new DeviceRTException(ErrorCode.DEVICE_INVOKE_FAILED, "read magic track failed" + ",res=" + (rspCode == null ? "null" : InnerUtils.hexString(rspCode)));

            } else {
                pinpadPackage.getPinpadRspCode();
                devicelogger.debug("[readTrackData]rspCode:"+InnerUtils.hexString(rspCode));
                if (Arrays.equals(new byte[]{rspCode[0], rspCode[1]}, new byte[]{0x42, 0x45})) {
                    if(Arrays.equals(new byte[]{rspCode[3], rspCode[4]}, new byte[]{0x30, 0x30})){
                        int panLen = (rspCode[5]&0xFF);
                        devicelogger.debug("[readTrackData]panLen："+panLen);
                        int rspOffset = 6;
                        if(keyIndex>0){//密文读取磁道数据，返回卡号长度+密文卡号数据+掩码卡号
                            int chipPanLen = 0;
                            if(panLen%16>0){
                                chipPanLen = (panLen/16 +1)*16;
                            }else{
                                chipPanLen = (panLen/16)*16;
                            }
                            devicelogger.debug("[read encry Track Data]chipPanLen:"+chipPanLen);
                            rspOffset = rspOffset + chipPanLen;
                        }
                        byte[] pan = new byte[panLen];
                        System.arraycopy(rspCode,rspOffset,pan,0,panLen);
                        devicelogger.debug("[readTrackData]pan:"+new String(pan));
                        if(keyIndex>0){
                            rspOffset = rspOffset + panLen;
                        }else {
                            rspOffset = rspOffset + (2*panLen);
                        }

                        Account account = new Account(new String(pan),"");

                        byte track1State = rspCode[rspOffset];
                        byte track2State = rspCode[rspOffset+1];
                        byte track3State = rspCode[rspOffset+2];
                        devicelogger.info("[readTrackData]track1State:"+track1State+";track2State:"+track2State+";track3State:"+track3State);
                        byte[] track1Data = null;
                        int track1Len = 0;int encryTrack1Len = 0;
                        track1Len = InnerUtils.bytesToInt(new byte[]{rspCode[rspOffset+3],rspCode[rspOffset+4]},0,2,true);
                        devicelogger.debug("[readTrackData]track1Len:"+track1Len);
                        rspOffset = rspOffset+4;
                        if(keyIndex>0){
                            encryTrack1Len = InnerUtils.bytesToInt(new byte[]{rspCode[rspOffset+1],rspCode[rspOffset+2]},0,2,true);
                            rspOffset = rspOffset+2;
                            devicelogger.debug("encryTrack1Len:"+encryTrack1Len);
                            track1Len = encryTrack1Len;
                        }
                        if(track1State==0x00){
                            track1Data = new byte[track1Len];
                            System.arraycopy(rspCode,rspOffset+1,track1Data,0,track1Len);
                            devicelogger.debug("[readTrackData]track1Data:"+InnerUtils.hexString(track1Data));
                            rspOffset = rspOffset+track1Len;
                            devicelogger.debug("[]rspOffset:"+rspOffset);
                        }


                        byte[] track2Data = null;
                        int track2Len = 0;int encryTrack2Len = 0;
                        String expiredDate ="";
                        String serviceCode = "";
                        track2Len = InnerUtils.bytesToInt(new byte[]{rspCode[rspOffset+1],rspCode[rspOffset+2]},0,2,true);
                        devicelogger.debug("[readTrackData]track2Len:"+track2Len);
                        rspOffset = rspOffset+2;
                        devicelogger.debug("[]rspOffset:"+rspOffset);
                        if(keyIndex>0){
                            encryTrack2Len = InnerUtils.bytesToInt(new byte[]{rspCode[rspOffset+1],rspCode[rspOffset+2]},0,2,true);
                            rspOffset = rspOffset+2;
                            track2Len = encryTrack2Len;
                            devicelogger.debug("encryTrack2Len:"+encryTrack2Len);
                        }
                        if(track2State==0x00){
                            track2Data = new byte[track2Len];
                            System.arraycopy(rspCode,rspOffset+1,track2Data,0,track2Len);
                            devicelogger.debug("[readTrackData]track2Data:"+InnerUtils.hexString(track2Data));
                            String trackData2 = new String(track2Data,"gbk");
                            devicelogger.debug("[readTrackData]trackData2:"+trackData2);
                            devicelogger.debug("[readTrackData]trackData2.indexOf('='):"+trackData2.indexOf('='));
                            expiredDate = trackData2.substring(trackData2.indexOf('=') + 1, trackData2.indexOf('=') + 5);
                            serviceCode = trackData2.substring(trackData2.indexOf('=') + 5, trackData2.indexOf('=') + 8);
                            rspOffset = rspOffset+track2Len;
                            devicelogger.debug("encryTrack2Len:"+encryTrack2Len);
                        }


                        int track3Len =0;int encryTrack3Len =0;
                        byte[] track3Data = null;
                        track3Len = InnerUtils.bytesToInt(new byte[]{rspCode[rspOffset+1],rspCode[rspOffset+2]},0,2,true);
                        devicelogger.debug("track3Len:"+track3Len);
                        rspOffset = rspOffset+2;
                        if(keyIndex>0){
                            encryTrack3Len = InnerUtils.bytesToInt(new byte[]{rspCode[rspOffset+1],rspCode[rspOffset+2]},0,2,true);
                            rspOffset = rspOffset+2;
                            track3Len = encryTrack3Len;
                            devicelogger.debug("encryTrack3Len:"+encryTrack3Len+";rspOffset:"+rspOffset);
                        }
                        if(track3State==0x00){
                            devicelogger.debug("[readTrackData]track3Len:"+track3Len);
                            track3Data = new byte[track3Len];
                            System.arraycopy(rspCode,rspOffset+1,track3Data,0,track3Len);
                            devicelogger.debug("[readTrackData]track3Data:"+InnerUtils.hexString(track3Data));
                        }

                        SwipResult swipResult = new SwipResult(account,swiperReadModel,track1Data,track2Data,track3Data,expiredDate,serviceCode,null);
                        return swipResult;
                    }else{
                        devicelogger.error("[readTrackData] repond code error:"+InnerUtils.hexString(new byte[]{rspCode[3], rspCode[4]}));
                        return null;
                    }
                }else{
                    devicelogger.error("[readTrackData]messageType error");
                    return null;
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
        return ExModuleType.MAGCARD;
    }

    /**
     * 不需要先打开读卡器，直接调用该指令获取磁道
     * @param timeOut
     * @param keyIndex
     * @param swiperReadModel
     * @param algorithmMode
     * @param cipherMode
     * @param tipMessage
     * @param cbcInit
     * @return
     */
    private SwipResult readTrackDataWithoutOpen(int timeOut,int keyIndex,SwiperReadModel[] swiperReadModel,AlgorithmMode algorithmMode,CipherMode cipherMode,List<byte[]> tipMessage,byte[] cbcInit){
        try {
            int tipMsgLen = 0;//提示信息
            Map<Integer,byte[]> msgMap = new HashMap<Integer, byte[]>();
            int msgIndex = 0;
            if(tipMessage!=null && tipMessage.size()>0){
                for(byte[] msg:tipMessage){
                    if(msg!=null){
                        tipMsgLen = tipMsgLen + msg.length;
                        msgMap.put(msgIndex,msg);
                    }
                    msgIndex = msgIndex +1;
                    devicelogger.debug("[readTrackDataWithoutOpen]tipMsgLen:"+tipMsgLen+";msgIndex:"+msgIndex);

                }
            }

            byte[] messageType = new byte[]{0x4A,0x30};
            byte[] reqData = null;
            int cbcInitLen = 0;
            if(cbcInit!=null){
                reqData = new byte[16+tipMsgLen];
                cbcInitLen = 8;
            }else{
                reqData = new byte[8+tipMsgLen];
            }
            reqData[0] = (byte) keyIndex;
            byte keyType = 0x00;//DES
            byte encryMode = 0x01;//DES_ECB
            if(algorithmMode!=null && algorithmMode == AlgorithmMode.AES){
                keyType = 0x01;
                encryMode = 0x02;
            }
            reqData[1] = (byte) keyType;

            if(cipherMode!=null && cipherMode==CipherMode.CBC){
                encryMode = 0x00;//DES_CBC
            }
            reqData[2] = (byte) encryMode;
            byte[] timeOutData = InnerUtils.intToBytes(timeOut,2,true);
            devicelogger.debug("[readTrackDataWithoutOpen]timeOutData:"+InnerUtils.hexString(timeOutData));
            reqData[3] = timeOutData[0];
            reqData[4] = timeOutData[1];
            byte track1 = 0x00;
            byte track2 = 0x00;
            byte track3 = 0x00;
            if(swiperReadModel!=null && swiperReadModel.length>0){
                for(SwiperReadModel model:swiperReadModel){
                    switch (model) {
                        case FIRST_TRACK:
                            track1 = 0x01;
                            break;
                        case SECOND_TRACK:
                            track2 = 0x01;
                            break;
                        case THIRD_TRACK:
                            track3 = 0x01;
                            break;
                    }
                }
            }
            reqData[5] = track1;
            reqData[6] = track2;
            reqData[7] = track3;
            if(cbcInitLen>0){
                System.arraycopy(cbcInit,0,reqData,8,8);
            }
//            int line1MsgLen = 0;
//            if(msgMap.size()>0 && msgMap.get(0)!=null){//第一行显示信息
//                devicelogger.debug("---line1 msg:"+(InnerUtils.hexString(msgMap.get(0))));
//                System.arraycopy(msgMap.get(0),0,reqData,8+cbcInitLen,(msgMap.get(0).length>16?16:msgMap.get(0).length));
//                line1MsgLen = (msgMap.get(0).length>16?16:msgMap.get(0).length);
//                devicelogger.debug("-------line1MsgLen:"+line1MsgLen);
//            }
//            reqData[8+cbcInitLen+line1MsgLen] = 0x1C;
//
//            int line2MsgLen = 0;
//            if(msgMap.size()>0 && msgMap.get(1)!=null){//第二行显示信息
//                devicelogger.debug("---line2 msg:"+(InnerUtils.hexString(msgMap.get(1))));
//                System.arraycopy(msgMap.get(1),0,reqData,9+cbcInitLen+line1MsgLen,(msgMap.get(1).length>16?16:msgMap.get(1).length));
//                line2MsgLen = (msgMap.get(1).length>16?16:msgMap.get(1).length);
//                devicelogger.debug("-------line2MsgLen:"+line1MsgLen);
//            }
//            reqData[9+cbcInitLen+line1MsgLen+line2MsgLen] = 0x1C;
//
//            int line3MsgLen = 0;
//            if(msgMap.size()>0 && msgMap.get(2)!=null){//第二行显示信息
//                devicelogger.debug("---line3 msg:"+(InnerUtils.hexString(msgMap.get(2))));
//                System.arraycopy(msgMap.get(2),0,reqData,10+cbcInitLen+line1MsgLen+line2MsgLen,(msgMap.get(2).length>16?16:msgMap.get(2).length));
//                line3MsgLen = (msgMap.get(2).length>16?16:msgMap.get(2).length);
//                devicelogger.debug("-------line3MsgLen:"+line1MsgLen);
//            }
//            reqData[10+cbcInitLen+line1MsgLen+line2MsgLen+line3MsgLen] = 0x1C;
//
//
//            int line4MsgLen = 0;
//            if(msgMap.size()>0 && msgMap.get(1)!=null){//第二行显示信息
//                devicelogger.debug("---line4 msg:"+(InnerUtils.hexString(msgMap.get(1))));
//                System.arraycopy(msgMap.get(3),0,reqData,11+cbcInitLen+line1MsgLen,(msgMap.get(3).length>16?16:msgMap.get(3).length));
//                line4MsgLen = (msgMap.get(3).length>16?16:msgMap.get(3).length);
//                devicelogger.debug("-------line4MsgLen:"+line1MsgLen);
//            }
//            reqData[11+cbcInitLen+line1MsgLen+line2MsgLen+line3MsgLen+line4MsgLen] = 0x1C;
            devicelogger.debug("[readTrackDataWithoutOpen]reqData:"+(InnerUtils.hexString(reqData)));
            byte[] rspCode = pinpadPackage.sendPinpadCmd(messageType, reqData, (timeOut)*1000+PinpadPackage.EXTCMD_OFFSETTIME_MS,true);
            if (rspCode == null || rspCode[0] == NAK) {
                throw new DeviceRTException(ErrorCode.DEVICE_INVOKE_FAILED, "read magic track failed" + ",res=" + (rspCode == null ? "null" : InnerUtils.hexString(rspCode)));

            } else {
                pinpadPackage.getPinpadRspCode();
                devicelogger.debug("[readTrackDataWithoutOpen]rspCode:"+InnerUtils.hexString(rspCode));
                if (Arrays.equals(new byte[]{rspCode[0], rspCode[1]}, new byte[]{0x4A, 0x31})) {
                    if(Arrays.equals(new byte[]{rspCode[3], rspCode[4]}, new byte[]{0x30, 0x30})){
                        int panLen = (rspCode[5]&0xFF);
                        devicelogger.debug("[readTrackDataWithoutOpen]panLen："+panLen);
                        byte[] pan = new byte[panLen];
                        System.arraycopy(rspCode,6,pan,0,panLen);
                        devicelogger.debug("pan:"+new String(pan));
                        Account account = new Account(new String(pan),"");
                        byte track1State = rspCode[6+(2*panLen)];
                        byte track2State = rspCode[7+(2*panLen)];
                        byte track3State = rspCode[8+(2*panLen)];
                        devicelogger.info("[readTrackDataWithoutOpen]track1State:"+track1State+";track2State:"+track2State+";track3State:"+track3State);
                        byte[] track1Data = null;
                        int track1Len = 0;
                        track1Len = InnerUtils.bytesToInt(new byte[]{rspCode[9+(2*panLen)],rspCode[10+(2*panLen)]},0,2,true);
                        devicelogger.debug("[readTrackDataWithoutOpen]track1Len:"+track1Len);
                        if(track1State==0x00){
                            track1Data = new byte[track1Len];
                            System.arraycopy(rspCode,11+(2*panLen),track1Data,0,track1Len);
                            devicelogger.debug("[readTrackDataWithoutOpen]track1Data:"+InnerUtils.hexString(track1Data));
                        }

                        byte[] track2Data = null;
                        int track2Len = 0;
                        String expiredDate = "";
                        String serviceCode = "";
                        track2Len = InnerUtils.bytesToInt(new byte[]{rspCode[11+(2*panLen)+track1Len],rspCode[12+(2*panLen)+track1Len]},0,2,true);
                        devicelogger.debug("[readTrackDataWithoutOpen]track2Len:"+track2Len);
                        if(track2State==0x00){
                            track2Data = new byte[track2Len];
                            System.arraycopy(rspCode,13+(2*panLen)+track1Len,track2Data,0,track2Len);
                            devicelogger.debug("[readTrackDataWithoutOpen]track2Data:"+InnerUtils.hexString(track2Data));
                            String trackData2 = new String(track2Data,"gbk");
                            expiredDate = trackData2.substring(trackData2.indexOf('=') + 1, trackData2.indexOf('=') + 5);
                            serviceCode = trackData2.substring(trackData2.indexOf('=') + 5, trackData2.indexOf('=') + 8);
                        }
                        int track3Len =0;
                        byte[] track3Data = null;
                        track3Len = InnerUtils.bytesToInt(new byte[]{rspCode[13+(2*panLen)+track1Len+track2Len],rspCode[14+(2*panLen)+track1Len+track2Len]},0,2,true);
                        devicelogger.debug("[readTrackDataWithoutOpen]track3Len:"+track3Len);
                        if(track3State==0x00 || track1State==0x01){
                            track3Data = new byte[track3Len];
                            System.arraycopy(rspCode,15+(2*panLen)+track1Len+track2Len,track3Data,0,track3Len);
                            devicelogger.debug("[readTrackDataWithoutOpen]track3Data:"+InnerUtils.hexString(track3Data));
                        }


                        SwipResult swipResult = new SwipResult(account,swiperReadModel,track1Data,track2Data,track3Data,expiredDate,serviceCode,null);
                        return swipResult;
                    }else{
                        devicelogger.error("[readTrackDataWithoutOpen] repond code error:"+InnerUtils.hexString(new byte[]{rspCode[3], rspCode[4]}));
                        return null;
                    }
                }else{
                    devicelogger.error("[readTrackDataWithoutOpen]messageType error");
                    return null;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
