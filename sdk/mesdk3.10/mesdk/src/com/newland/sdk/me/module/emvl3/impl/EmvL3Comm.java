package com.newland.sdk.me.module.emvl3.impl;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.os.SystemClock;
import android.util.Log;

import com.newland.sdk.me.module.emvl3.external.Candidate;
import com.newland.sdk.me.module.emvl3.external.EmvL3Const;
import com.newland.sdk.me.module.externalPininput.BleBasePackage;
import com.newland.sdk.me.module.externalPininput.BleBsaeDataRevListener;
import com.newland.sdk.me.module.serialport.MESerial;
import com.newland.sdk.me.module.usb.MEUSB;
import com.newland.sdk.me.module.emvl3.jni.CommListener;
import com.newland.sdk.me.module.emvl3.listener.MEEmvL3Listener;
import com.newland.sdk.me.module.usb.USBSafeBuffer;
import com.newland.sdk.me.utils.PropertiesUtils;
import com.newland.sdk.module.emv.EmvExtParams;
import com.newland.sdk.module.externalPin.PinpadInitExtParams;
import com.newland.sdk.module.serialport.Baudrate;
import com.newland.sdk.module.serialport.PortType;
import com.newland.sdk.module.serialport.SerialPortModule;
import com.newland.sdk.module.usb.SelectUsbDeviceListener;
import com.newland.sdk.module.usb.USBModule;
import com.newland.sdk.mtype.ProcessTimeoutException;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.utils.ISOUtils;
import com.newland.sdk.utils.TLVPackage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import static com.newland.sdk.me.module.emvl3.impl.EmvL3Constant.FunctionId.*;

/**
 * @Description EMVL3通讯实现.
 * @Author wuhh
 * @Date 2020/9/17
 */
public class EmvL3Comm implements CommListener {
    private static final String TAG = "EmvL3Comm";
    private DeviceLogger deviceLogger = DeviceLoggerFactory.getLogger(TAG);
    private Lock mSyncCmdLock = new ReentrantLock();
    private Lock mInterruptCmdLock = new ReentrantLock();
    private EmvL3Usage mEmvL3Usage;
    private MEEmvL3Listener mMEEmvL3Listener;
    private ChannelMode mChannelMode;
    private USBModule mUSBModule;
    private SerialPortModule mUARTModule;
    private PortType mPortType;
    private Baudrate mBaudrate;
    private volatile boolean isCancelOnInterrupt = false;
    private volatile boolean isCancelProcessing = false;
    private volatile boolean mCCSBusy = false;//cancelChannelState == CCS
    private EmvExtParams emvExtParams;
//    private BleBasePackage bleBasePackage;
    private Context context;
    private static EmvL3Comm emvL3Comm;
//    private USBSafeBuffer safeBuffer;


    public static EmvL3Comm getInstance (Context context, AbstractDevice owner){
        if(emvL3Comm==null){
            emvL3Comm = new  EmvL3Comm( context,  owner);
        }
        return emvL3Comm;
    }
    private EmvL3Comm(Context context, AbstractDevice owner) {
        mUSBModule = MEUSB.getInstance(context);
        this.context = context;
        ((MEUSB) mUSBModule).setWorkingSyncMode(true);
        mUARTModule = new MESerial(owner,context);
    }

    //判断外接设备具体通讯类型.
    public boolean init(EmvExtParams emvExtParams, CommChannel channel) {
        deviceLogger.debug("------init-------");
        this.mEmvL3Usage = EmvL3Usage.EXTERNAL;
        this.emvExtParams = emvExtParams;
        if(isUseBleBase(emvExtParams)){
            mChannelMode = ChannelMode.BLUETOOTH_BASE;
            boolean result = channel.getChannel();
            deviceLogger.error("ChannelMode0=" + mChannelMode + " result=" + result);
            return result;
        }
        if(isMicroUSB(emvExtParams)){
            mChannelMode = ChannelMode.MIN_USB;
            mPortType = emvExtParams.getPortType();
            mBaudrate = emvExtParams.getBaudrate();
            boolean result = channel.getChannel();
            deviceLogger.error("ChannelMode1=" + mChannelMode + " result=" + result);
            return result;
        }

        mChannelMode = ChannelMode.USB;
        mPortType = PortType.USB;
        boolean result = channel.getChannel();
        deviceLogger.error("ChannelMode2=" + mChannelMode + " result=" + result+" emvExtParams="+emvExtParams);
        if (result) {
            return true;
        }else {
            if(isExtUSB(emvExtParams)){
                return false;
            }
        }

        mChannelMode = ChannelMode.UART;
        List<Baudrate> baudrates = new ArrayList<Baudrate>();
        List<PortType> portTypes = new ArrayList<PortType>();
        List<Baudrate> allBaudrate = new ArrayList<Baudrate>(Arrays.asList(Baudrate.BPS115200, Baudrate.BPS57600));
        List<PortType> allPorttype = new ArrayList<PortType>(Arrays.asList(PortType.PINPAD, PortType.RS232));
        PropertiesUtils propertiesUtils = PropertiesUtils.getInstance("COMMParam", "/data/share/SDK_EXT_PINPAD/COMMParam.properties");
        String param = propertiesUtils.getValue("EXT_PARAM");
        if (null != param) {
            String[] extParam = param.split("\\|");
            if (null != extParam && extParam.length >= 2) {
                String baudrate = extParam[0];
                baudrates.add(Baudrate.valueOf(baudrate));
                allBaudrate.remove(Baudrate.valueOf(baudrate));
                baudrates.addAll(allBaudrate);
                String portType = extParam[1];
                portTypes.add(PortType.valueOf(portType));
                allPorttype.remove(PortType.valueOf(portType));
                portTypes.addAll(allPorttype);
            }
        } else {
            baudrates = allBaudrate;
            portTypes = allPorttype;
        }
        deviceLogger.debug("portTypes=" + portTypes + " baudrates=" + baudrates);
        for (PortType portType : portTypes) {
            for (Baudrate baudrate : baudrates) {
                mPortType = portType;
                mBaudrate = baudrate;
                result = channel.getChannel();
                deviceLogger.error(" portType=" + portType + " baudrate=" + baudrate + " result=" + result);
                if (result) {
                    deviceLogger.error("ChannelMode2=" + mChannelMode + " result=" + result);
                    return true;
                }
            }
        }
        deviceLogger.error("ChannelMode3=" + mChannelMode + " result=" + result);
        return false;
    }

    private enum ChannelMode {
        USB, UART, BLUETOOTH_BASE,MIN_USB
    }

    public interface CommChannel {
        boolean getChannel();
    }

    public void setMEEmvL3Listener(MEEmvL3Listener listener) {
        this.mMEEmvL3Listener = listener;
    }

    @Override
    public byte[] Communication(byte[] send) {
        return Communication0(send);
    }

    private byte[] Communication0(byte[] send) {
        if (send == null) {
            deviceLogger.error("send==null");
            return null;
        }
        boolean interruptMode = false;

        boolean findCardTime = (EmvL3Global.getUiEventID() == EmvL3Constant.UIEvent.UI_PRESENT_CARD)?true:false;
        boolean isInterruptTime = EmvL3Global.isInterruptTime();

        boolean isCancel = (findCardTime && new String(send).equals(EmvL3Constant.CMD_CANCEL))||
                (isInterruptTime && new String(send).equals(EmvL3Constant.CMD_CANCEL));
        try {
            int ret = -1;
            EmvL3Global.setChannelState(ChannelState.BUSY);

            deviceLogger.debug("Thread Info=" + Thread.currentThread().getName() + " " + Thread.currentThread().getId());
            String sendData = InnerUtils.hexString(send);
            if (mEmvL3Usage == EmvL3Usage.EXTERNAL) {
                deviceLogger.debug("Communication SEND:" + sendData);
            }

            deviceLogger.debug("findCardTime="+findCardTime+" isCancel="+isCancel);

            String functionID = sendData.substring(sendData.indexOf("2F") + 2, sendData.indexOf("2F") + 4);
            int funId = Integer.valueOf(functionID,16);

            printFunctionId(funId);
            boolean isTestEcho = funId == COMMAND_GET_VERSION ? true:false;
            boolean isAsyncId = isAsyncID(funId);
            deviceLogger.debug("isInterruptTime=" + isInterruptTime + " functionID=" + functionID+" isTestEcho="+isTestEcho);

            if(!isCancel){
                if (isInterruptTime && isAsyncId) {
                    deviceLogger.error("InterruptCmd Lock");
                    mInterruptCmdLock.lock();
                    interruptMode = true;
                } else {
                    deviceLogger.error("SyncCmd Lock");
                    mSyncCmdLock.lock();
                    interruptMode = false;
                }

                if(EmvL3Global.getChannelState() == ChannelState.INTERRUPT){
                    deviceLogger.debug("Cancel EmvProcess");
                    return null;
                }

                if (!interruptMode) {
                    ret = open(isTestEcho);
                    if (ret < 0) {
                        deviceLogger.debug("open fail....");
                        return null;
                    }
                }

                ret = write(send, 0);
                if (ret != send.length) {
                    deviceLogger.debug("write fail ret=" + ret);
                    return null;
                }
            }
            byte[] resp = null;
            while (true) {
                int emvTimeOut = getCommandTimeOut(funId);
                boolean isSupportMs = isSupportMs();
                int count = 0,result = 0,interval = 1000;
                if(isSupportMs){
                    interval = 100;
                }
                deviceLogger.debug(">>>>emvTimeOut="+emvTimeOut);
                int allCount = emvTimeOut / interval;
                if(isTestEcho){
                    allCount = EmvL3Constant.ECHO_TEST_COUNT;
                    interval = EmvL3Constant.ECHO_TEST_TIMEOUT_MS;
                }
                if(isUseBleBase(emvExtParams)){
                    interval = 2000;
                }
                deviceLogger.debug("allCount="+allCount+" interval="+interval+" isSupportMs="+isSupportMs);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                if (mEmvL3Usage == EmvL3Usage.EXTERNAL) {
                    long startTime = System.currentTimeMillis();
                    while (true) {
                        if(EmvL3Global.getChannelState() == ChannelState.INTERRUPT){
                            deviceLogger.debug("Interrupt EmvProcess");
                            return null;
                        }
//                        deviceLogger.debug("isCancelProcessing="+isCancelProcessing+" FunctionDesc="+getFunctionDesc(funId)+" Name="+Thread.currentThread().getName()+" Id="+Thread.currentThread().getId());
                        if(isCancelProcessing){
                            mCCSBusy = false;
                            if(funId == COMMAND_PERFORM_TRANSACTION){
                                Thread.sleep(50);
                                deviceLogger.debug("PerformTransaction waiting cancel ack......");
                                continue;
                            }
                        }
                        if(isCancel){
                            isCancelProcessing = true;
                            //必须先退出正在读串口的线程,
                            //当其他指令的状态为已经收到应答,则等指令全部接收完毕,才发送取消指令;
                            //当其他指令的状态为未收到应答,则直接中断,先发送取消指令;
                            while (mCCSBusy){
                                Thread.sleep(50);
                                if(EmvL3Global.isInterruptTime()){
                                    mCCSBusy = false;
                                }
                                deviceLogger.debug("Waiting channelState free......");
                                continue;
                            }
                            count += 1;
                            if (count > allCount) {
                                deviceLogger.debug("Cancel timeout!!!");
                                return null;
                            }
                            if((count-1) % 2 == 0){
                                ret = write(send,0);
                                if (ret != send.length) {
                                    deviceLogger.debug("write cancel fail ret=" + ret);
                                }
                                deviceLogger.error("EmvL3 Write Cancel!!!"+ret+" "+(count-1));
                                Thread.sleep(10);
                            }
                            if(isUseBleBase(emvExtParams) && emvExtParams.getPortType() == PortType.BLEBASE_USB2){
                                deviceLogger.debug("----BLEBASE_USB2---break");
                                break;
                            }
                            byte[] cancelAck = new byte[1];
                            ret = read(cancelAck,interval);
                            deviceLogger.debug("CANCEL ret="+ret+" ack="+cancelAck[0]);
                            if(!(ret == cancelAck.length && cancelAck[0] == 0x06)){
                                deviceLogger.debug("Wait cancel ack. ret="+ret+" cancelAckCode="+String.format("0x%x",cancelAck[0]));
                                continue;
                            }
                            deviceLogger.error("EmvL3 Cancel succ!!!");
                            //取消与perform、中断步骤是不同的线程,所以取消线程可直接返回,等待其他线程读取数据.
                            if(isInterruptTime){
                                isCancelOnInterrupt = true;
                                EmvL3Global.setIsInterruptTime(false);
                            }
                            return null;
                        }
//                        if(isUseBleBase(emvExtParams) && emvExtParams.getPortType() == PortType.BLEBASE_USB2){
//                            deviceLogger.info("----BLEBASE_USB2---break");
//                            break;
//                        }
                        mCCSBusy = true;
                        byte[] head = new byte[1];
                        count += 1;
                        if (count > allCount) {
                            deviceLogger.debug("Read timeout!!!");
                            return null;
                        }
                        deviceLogger.debug(getFunctionDesc(funId)+" Read response data......");
                        result = read(head, interval);
                        if(isTestEcho){
                            Thread.sleep(5);
                            long endTime = System.currentTimeMillis();
                            deviceLogger.debug("Echo count="+count+" disTimeMs="+(endTime-startTime)+" result="+result+" head="+head[0]);
                        }

                        if (result != head.length) {
                            continue;
                        }
                        count = 0;
                        if (Arrays.equals(head, new byte[]{0x15})) {
                            deviceLogger.error("head1=" + head[0]);
                            return null;
                        }
                        if (Arrays.equals(head, new byte[]{0x06}) || Arrays.equals(head, new byte[]{0x02})) {//06 02
                            try {
                                bos.write(head);
                            } catch (IOException e) {
                                e.printStackTrace();
                                return null;
                            }
                            if (head[0] != 0x02) {
                                continue;
                            }
                            break;
                        } else {
                            continue;
                        }
                    }
                }
                int timeOut = 2000;
                int len = 0;
//                if(isUseBleBase(emvExtParams) && emvExtParams.getPortType() == PortType.BLEBASE_USB2){
//                    deviceLogger.debug("-------read from Ble USB2----");
//                    resp = bleBasePackage.readUSB2ProtData(3000,PortType.BLEBASE_USB2);
//                    deviceLogger.info("-------read from Ble USB2----resp:"+(resp==null?null:ISOUtils.hexString(resp)));
//                    if(resp!=null && resp.length>=4 && resp[0] == 0x06){
//                        len =InnerUtils.bcdToInt(new byte[]{resp[2],resp[3]}, 0, 2 * 2, true);
//                        deviceLogger.info("-------read from Ble USB2----data len:"+len);
//                    }
//                  //
//                }else{
                    byte[] middle = new byte[2];//len
                    result = read(middle, timeOut);
                    if (result != middle.length) {
                        deviceLogger.debug("read middle lenth error.");
                        return null;
                    }
                    try {
                        bos.write(middle);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }

                    len = InnerUtils.bcdToInt(middle, 0, 2 * 2, true);

                    byte[] tail = new byte[len + 2];
                    int tailLen = tail.length;
                    for (int i = 0; i < tailLen; i += 2048) {
                        int needLen = 0;
                        if (tailLen - i >= 2048) {
                            needLen = 2048;
                        } else {
                            needLen = tailLen - i;
                        }
                        byte[] tmp = new byte[needLen];
                        result = read(tmp, timeOut);
                        if (result != needLen) {
                            deviceLogger.debug("read tail lenth error.");
                            return null;
                        }
                        try {
                            bos.write(tmp);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                    resp = bos.toByteArray();
//                }


                if (resp == null) {
                    deviceLogger.debug("resp byte is null.");
                    return null;
                }
                if (resp != null) {
                    deviceLogger.debug("Communication RECEIVE:" + InnerUtils.hexString(resp));
                }
                byte funID = resp[6];
                if (resp[0] == 0x06) {
                    funID = resp[7];
                }
                if (funID == EmvL3Constant.FUNCTIONID_LOG) {
                    try {
                        byte[] logLen = new byte[2];
                        int offset = 9;
                        if (resp[0] == 0x06) {
                            offset = 10;
                        }
                        System.arraycopy(resp, offset, logLen, 0, 2);
                        //int emvloglen = logLen[0] * 255 + logLen[1];
                        int emvloglen = InnerUtils.bytesToInt(logLen, 0, 2, true);
                        offset = offset + 2;//logLen
                        byte[] emvlog = new byte[emvloglen];
                        System.arraycopy(resp, offset, emvlog, 0, emvloglen);
//                                deviceLogger.debug("Communication EMVLOG(ASCII):" + new String(emvlog));
                        deviceLogger.debug("Communication EMVLOG(ASCII):" + new String(emvlog));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    bos.reset();
                    continue;
                } else if (funID == EmvL3Constant.FUNCTIONID_CALLBACK) {
                    try {
                        int offset = 7;
                        if (resp[0] == 0x06) {
                            offset++;
                        }
                        byte l3CallBackId = resp[offset];
                        offset++;

                        printCallBackStep(l3CallBackId);
                        if (l3CallBackId == EmvL3Constant.CallbackId.COMMAND_NOTIFICATION) {
                            byte uiEvent = resp[offset];
                            offset++;
                            int uiDataLen = len - 6;
                            byte[] uiData = null;
                            if (uiDataLen > 0) {
                                uiData = new byte[uiDataLen];
                                System.arraycopy(resp, offset, uiData, 0, uiDataLen);
                            }
                            deviceLogger.debug("uiEvent uiEvent=" + uiEvent + " uiData=" + (uiData == null ? "null" : ISOUtils.hexString(uiData)));
                            mMEEmvL3Listener.uiEvent(uiEvent, uiData);

                        } else if (l3CallBackId == EmvL3Constant.CallbackId.COMMAND_SELECT_CANDIDATE_LIST) {
                            byte listnum = resp[offset];
                            offset++;
                            ArrayList<Candidate> candidateList = new ArrayList<Candidate>();
                            for (int i = 0; i < listnum; i++) {
                                byte[] aidLenFb = new byte[2];
                                System.arraycopy(resp, offset, aidLenFb, 0, aidLenFb.length);
                                offset += 2;
                                int aidLenFi = InnerUtils.bytesToInt(aidLenFb, -1, 2, true);
                                byte[] aidTlv = new byte[aidLenFi];
                                System.arraycopy(resp, offset, aidTlv, 0, aidLenFi);
                                offset += aidLenFi;
                                TLVPackage tlvPackage = InnerUtils.newTlvPackage();
                                tlvPackage.unpack(aidTlv);
                                Candidate candidate = new Candidate();
                                candidateList.add(candidate);
                                byte[] terminalCodeTable = tlvPackage.getValue(0x9F40);
                                candidate.terminalCodeTable = terminalCodeTable;
                                deviceLogger.debug("terminalCodeTable=" + hexString(terminalCodeTable));

                                byte[] perferName = tlvPackage.getValue(0x9F12);
                                candidate.preferName = perferName;
                                deviceLogger.debug("perferName=" + hexString(perferName));

                                byte[] lable = tlvPackage.getValue(0x50);
                                candidate.lable = lable;
                                deviceLogger.debug("lable=" + hexString(lable));

                                byte[] priority = tlvPackage.getValue(0x87);
                                if (priority != null && priority.length >= 1) {
                                    candidate.priority = priority[0];
                                }
                                deviceLogger.debug("priority=" + hexString(priority));

                                byte[] aid = tlvPackage.getValue(0x4F);
                                candidate.aid = aid;
                                candidate.aidLen = (byte) aid.length;
                                deviceLogger.debug("aid=" + hexString(aid));

                                byte[] IssuerCodeTableIndex = tlvPackage.getValue(0x9F11);
                                if (IssuerCodeTableIndex != null && IssuerCodeTableIndex.length >= 1) {
                                    candidate.issuerCodeTableIndex = IssuerCodeTableIndex[0];
                                }
                                deviceLogger.debug("IssuerCodeTableIndex=" + hexString(IssuerCodeTableIndex));

                                byte[] languagePreference = tlvPackage.getValue(0x5F2D);
                                candidate.languagePreference = languagePreference;
                                deviceLogger.debug("languagePreference=" + hexString(languagePreference));

                                byte[] kernalId = tlvPackage.getValue(0xDF37);
                                candidate.kernelId = kernalId;
                                deviceLogger.debug("kernalId=" + hexString(kernalId));

                                byte[] terminalPriority = tlvPackage.getValue(0xDF65);
                                if (terminalPriority != null && terminalPriority.length >= 1) {
                                    candidate.terminalPriority = terminalPriority[0];
                                }

                                deviceLogger.debug("terminalPriority=" + hexString(terminalPriority));

                                byte[] customTag = tlvPackage.getValue(0x1F811F);
                                if(customTag != null){
                                    candidate.customTagData = customTag;
                                    candidate.customDataSize = customTag.length;
                                }
                                deviceLogger.debug("customTag=" + hexString(customTag));
                            }
                            int[] select = new int[1];
                            mMEEmvL3Listener.selectCandidateList(candidateList, select);
                            waitAsyncIDThread();

                            if (getCancelAck()){
                                bos.reset();
                                continue;
                            }

                            deviceLogger.debug("select index=" + select);
                            int selectIndex = select[0];
                            if (selectIndex < 0) {
                                deviceLogger.error("selectIndex < 0 ");
                                return null;
                            }
                            byte[] selectAid = candidateList.get(selectIndex).aid;
                            //ACK
                            deviceLogger.debug("ACK CALLBACK_ID_SELECT_CANDIDATE_LIST");
                            byte[] bodyData = new byte[1 + 1 + 2 + selectAid.length];//callbackid+index+len+aid
                            bodyData[0] = EmvL3Constant.CallbackId.COMMAND_SELECT_CANDIDATE_LIST;
                            bodyData[1] = (byte) selectIndex;
                            byte[] selectAidLen = InnerUtils.intToBytes(selectAid.length, 2, true);
                            System.arraycopy(bodyData, 2, selectAidLen, 0, selectAidLen.length);
                            System.arraycopy(bodyData, 4, selectAid, 0, selectAid.length);

                            if(!ackCallBack(bodyData,timeOut,interval)){
                                return null;
                            }
                            deviceLogger.debug("ackCallBack selectCandidateList succ");
                        } else if (l3CallBackId == EmvL3Constant.CallbackId.COMMAND_AFTER_FINAL_SELECT) {
                            byte cardIntf = resp[offset];
                            offset++;
                            byte[] aidLenTemp = new byte[2];
                            System.arraycopy(resp, offset, aidLenTemp, 0, aidLenTemp.length);
                            int aidLen = InnerUtils.bytesToInt(aidLenTemp, 0, 2, true);
                            offset += 2;
                            byte[] aid = new byte[aidLen];
                            System.arraycopy(resp, offset, aid, 0, aid.length);
                            offset += aidLen;
                            mMEEmvL3Listener.onFinalSelect(cardIntf, aid, aidLen);
                            waitAsyncIDThread();
                            if (getCancelAck()) {
                                bos.reset();
                                continue;
                            }

                            //ACK
                            deviceLogger.debug("ACK AFTER_FINAL_SELECT");
                            byte[] tlvData = new byte[]{};
                            byte[] tlvLen = InnerUtils.intToBytes(tlvData.length, 4, true);
                            byte[] bodyData = new byte[1 + 4 + tlvData.length];//callbackid+tlvlen+tlvdata
                            bodyData[0] = EmvL3Constant.CallbackId.COMMAND_AFTER_FINAL_SELECT;
                            System.arraycopy(tlvLen, 0, bodyData, 1, 4);
                            System.arraycopy(tlvData, 0, bodyData, 5, tlvData.length);

                            if(!ackCallBack(bodyData,timeOut,interval)){
                                return null;
                            }
                            deviceLogger.debug("ackCallBack onFinalSelect succ");

                        } else if (l3CallBackId == EmvL3Constant.CallbackId.COMMAND_CARDNUM_CONFIRM) {//cardnum confirm
                            mMEEmvL3Listener.cardnumConfirm();
                            waitAsyncIDThread();
                            if (getCancelAck()) {
                                bos.reset();
                                continue;
                            }

                            //ACK
                            deviceLogger.debug("ACK CALLBACK_ID_CARDNUM_CONFIRM");
                            byte[] bodyData = new byte[1 + 4];//callbackid+errorcode
                            bodyData[0] = EmvL3Constant.CallbackId.COMMAND_CARDNUM_CONFIRM;
                            int errCodeFi = 0;
                            byte[] errCodeFb = InnerUtils.intToBytes(errCodeFi, 4, true);
                            System.arraycopy(errCodeFb, 0, bodyData, 1, errCodeFb.length);

                            if(!ackCallBack(bodyData,timeOut,interval)){
                                return null;
                            }
                            deviceLogger.debug("ackCallBack cardnumconfirm succ");
                        } else if (l3CallBackId == EmvL3Constant.CallbackId.COMMAND_GET_PIN) {//pin callback
                            byte pinType = resp[offset];
                            offset++;
                            deviceLogger.debug("pinType=" + pinType);
                            String pan = null;int offlinePinCnt = -1;
                            try {
                                byte[] tlvLenTemp = new byte[2];
                                System.arraycopy(resp, offset, tlvLenTemp, 0, tlvLenTemp.length);
                                int tlvDataLen = InnerUtils.bytesToInt(tlvLenTemp, 0, 2, true);
                                offset += 2;
                                deviceLogger.debug("tlvDataLen=" + tlvDataLen);
                                if (tlvDataLen > 0) {
                                    byte[] tlvData = new byte[tlvDataLen];
                                    System.arraycopy(resp, offset, tlvData, 0, tlvDataLen);
                                    deviceLogger.debug("L3CallBack: offset=" + offset + " " + ISOUtils.hexString(tlvData));
                                    TLVPackage tlvPackage = InnerUtils.newTlvPackage();
                                    tlvPackage.unpack(tlvData);
                                    byte[] panFb = tlvPackage.getValue(0x5A);
                                    pan = new String(panFb);
                                    byte[] offlinePinCntFb = tlvPackage.getValue(0x1F8157);
                                    if(offlinePinCntFb != null && offlinePinCntFb.length >= 1){
                                        offlinePinCnt = offlinePinCntFb[0];
                                    }
                                    deviceLogger.debug("L3CallBack: pan=" + pan+" offlinePinCnt="+offlinePinCnt);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            byte[] keyType = new byte[1], keyIndex = new byte[1], pinTimeOut = new byte[1];
                            byte[] pwdRange = null;
                            if (pinType == EmvL3Const.PINType.PIN_ONLINE || pinType == EmvL3Const.PINType.PIN_OFFLINE || pinType == EmvL3Const.PINType.PIN_OFFLINE_ENCIPHERED) {//online pin
                                deviceLogger.error("PIN_XXX pinType{ONLINE(0), OFFLINE(1), OFFLINE_ENCIPHERED(2)}="+pinType);
                                EmvL3Global.pinblock = null;
                                mMEEmvL3Listener.getPIN(pinType, offlinePinCnt, null, null);

                                boolean isFree = mInterruptCmdLock.tryLock();
                                while (!isFree){
                                    Thread.sleep(10);
                                    isFree = mInterruptCmdLock.tryLock();
                                    deviceLogger.debug("InterruptCmdLock isFree="+isFree);
                                }
                                EmvL3Global.setIsInterruptTime(false);
                                mInterruptCmdLock.unlock();
                                //不能由步骤继续来解锁,因为输密码需要先应答才可以调起输密码,并且输密码过程中不能再取数据.
                                //waitAsyncIDThread();

                                keyType[0] = EmvL3Global.pinKeyType;
                                keyIndex[0] = EmvL3Global.pinKeyIndex;
                                pinTimeOut[0] = EmvL3Global.pinTimeOut;
                                pwdRange = EmvL3Global.pinKPwdRange;
                                deviceLogger.debug("ACK GET_PIN");

                                if(Arrays.equals(EmvL3Global.pinblock, new byte[]{})){
                                    byte[] bodyData = new byte[1 + 4];//callbackid+result
                                    bodyData[0] = (byte) 0xFF;
                                    int errCodeFi = -610;
                                    byte[] errCodeFb = InnerUtils.intToBytes(errCodeFi, 4, true);
                                    System.arraycopy(errCodeFb, 0, bodyData, 1, errCodeFb.length);
                                    if(!ackCallBack(bodyData,timeOut,interval)){
                                        return null;
                                    }
                                    deviceLogger.debug("ackCallBack getPIN bypass succ");
                                    bos.reset();
                                    continue;
                                }
                                if (((pinType==EmvL3Const.PINType.PIN_ONLINE)&&(keyType[0] < 0 || keyIndex[0] < 0)) || pinTimeOut[0] < 0) {
                                    deviceLogger.error("pin param error.. keyType=" + keyType[0] + " keyIndex=" + keyIndex[0] + " pinTimeOut=" + pinTimeOut[0]);
                                    return null;
                                }
                                String pwdRangeTlv = "";//"1F81350C0000000405060708090A0B0C";
                                if (pwdRange != null) {
                                    String lenFs = InnerUtils.hexString(new byte[]{(byte) pwdRange.length});
                                    pwdRangeTlv = "1F8135" + lenFs + ISOUtils.hexString(pwdRange);
                                }
                                deviceLogger.debug("keyType=" + keyType[0] + " keyIndex=" + keyIndex[0] + " timeOut=" + pinTimeOut[0] + " pwdRange=" + pwdRange + " pwdRangeTlv=" + pwdRangeTlv);                                    //ACK

                                String msg = "";
                                if(EmvL3Global.pinpadExtParams != null){
                                    String msg1 = EmvL3Global.pinpadExtParams.getFirstLineMessage();
                                    String msg2 = EmvL3Global.pinpadExtParams.getSecondLineMessage();
                                    String msg3 = EmvL3Global.pinpadExtParams.getThirdLineMessage();
                                    String msg4 = EmvL3Global.pinpadExtParams.getFourthLineMessage();
                                    if(msg1 != null || msg2 != null || msg3 != null || msg4 != null){
                                        if(msg1 != null){ msg += ISOUtils.hexString(msg1.getBytes())+"1C"; } else { msg += "1C";}
                                        if(msg2 != null){ msg += ISOUtils.hexString(msg2.getBytes())+"1C"; } else { msg += "1C";}
                                        if(msg3 != null){ msg += ISOUtils.hexString(msg3.getBytes())+"1C"; } else { msg += "1C";}
                                        if(msg4 != null){ msg += ISOUtils.hexString(msg4.getBytes())+"1C"; } else { msg += "1C";}
                                        msg += "FF021C";
                                    }
                                }
                                byte[] tlvData = null;
                                TLVPackage tlvPackage = ISOUtils.newTlvPackage();
                                if(pinType == EmvL3Const.PINType.PIN_ONLINE) {
                                    String value = "1F813601" + String.format("%02x", keyType[0]) + "1F813701" + String.format("%02x", keyIndex[0]) +
                                            "1F813801" + String.format("%02x", pinTimeOut[0]) + pwdRangeTlv;
                                    if(!msg.equals("")){
                                        tlvPackage.append(0x1F8150,msg);
                                        value += ISOUtils.hexString(tlvPackage.pack());
                                    }
                                    tlvData = ISOUtils.hex2byte(value);
                                }else {
                                    String value = "1F813801" + String.format("%02x", pinTimeOut[0]) + pwdRangeTlv;
                                    if(!msg.equals("")){
                                        tlvPackage.append(0x1F8150,msg);
                                        value += ISOUtils.hexString(tlvPackage.pack());
                                    }
                                    tlvData = ISOUtils.hex2byte(value);
                                }
                                byte[] bodyData = null;
                                if(!msg.equals("")){
                                    bodyData = new byte[1 + 4 + 4 + tlvData.length];//callbackid+result+tlvLen+tlvData
                                    bodyData[0] = (byte) 0xFF;
                                    bodyData[1] = 0x00;
                                    bodyData[2] = 0x00;
                                    bodyData[3] = 0x00;
                                    bodyData[4] = 0x00;
                                    byte[] tlvLen = InnerUtils.intToBytes(tlvData.length, 4, true);
                                    System.arraycopy(tlvLen, 0, bodyData, 5, 4);
                                    System.arraycopy(tlvData, 0, bodyData, 9, tlvData.length);
                                }else {
                                    byte[] tlvLen = InnerUtils.intToBytes(tlvData.length, 2, true);
                                    bodyData = new byte[1 + 2 + tlvData.length];//callbackid+tlvLen+tlvData
                                    bodyData[0] = EmvL3Constant.CallbackId.COMMAND_GET_PIN;
                                    System.arraycopy(tlvLen, 0, bodyData, 1, 2);
                                    System.arraycopy(tlvData, 0, bodyData, 3, tlvData.length);
                                }
                                if(!ackCallBack(bodyData,timeOut,interval)){
                                    return null;
                                }
                                deviceLogger.debug("ackCallBack getPIN succ");

                            }
                        } else if (l3CallBackId == EmvL3Constant.CallbackId.COMMAND_CHECK_CREDENTIALS) {
                            byte[] tlvLenFb = new byte[2];
                            System.arraycopy(resp, offset, tlvLenFb, 0, tlvLenFb.length);
                            offset += 2;
                            int tlvLenFi = InnerUtils.bytesToInt(tlvLenFb, -1, 2, true);
                            byte[] tlvData = new byte[tlvLenFi];
                            System.arraycopy(resp, offset, tlvData, 0, tlvLenFi);
                            offset += tlvLenFi;
                            TLVPackage tlvPackage = InnerUtils.newTlvPackage();
                            tlvPackage.unpack(tlvData);
                            byte[] cerType = tlvPackage.getValue(0x9F62);
                            byte[] number = tlvPackage.getValue(0x9F61);
                            deviceLogger.debug("cerType=" + cerType + " number=" + number);

                            ((EmvL3CallBackHelper) mMEEmvL3Listener).setCheckCredentialsParam(cerType[0], new String(number));
                            mMEEmvL3Listener.checkCredentials();
                            waitAsyncIDThread();
                            if (getCancelAck()) {
                                bos.reset();
                                continue;
                            }

                            //ACK
                            deviceLogger.debug("ACK CALLBACK_ID_CHECK_CREDENTIALS");
                            byte[] bodyData = new byte[1 + 4];//callbackid+errorcode
                            bodyData[0] = EmvL3Constant.CallbackId.COMMAND_CHECK_CREDENTIALS;
                            int errCodeFi = 0;
                            byte[] errCodeFb = InnerUtils.intToBytes(errCodeFi, 4, true);
                            System.arraycopy(errCodeFb, 0, bodyData, 1, errCodeFb.length);

                            if(!ackCallBack(bodyData,timeOut,interval)){
                                return null;
                            }
                            deviceLogger.debug("ackCallBack checkCredentials succ");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    bos.reset();
                    continue;
                } else {
                    break;
                }

            }
            byte[] resp2 = resp;
            if ((mEmvL3Usage == EmvL3Usage.EXTERNAL) && resp[0] != 0x06) {
                resp2 = new byte[resp.length + 1];
                resp2[0] = 0x06;
                System.arraycopy(resp, 0, resp2, 1, resp.length);
            }

            ret = write(new byte[]{0x06}, 0);
            if (ret != 1) {
                deviceLogger.debug("write ack fail ret=" + ret);
                return null;
            }
            return resp2;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            deviceLogger.debug("finally isCancel="+isCancel+" interruptMode="+interruptMode);
            isCancelProcessing = false;
            mCCSBusy = false;
            if(!isCancel){
                if (!interruptMode) {
                    close();
                    EmvL3Global.setChannelState(ChannelState.FREE);
                }
                if (interruptMode) {
                    deviceLogger.error("InterruptCmd unlock");
                    mInterruptCmdLock.unlock();
                } else {
                    deviceLogger.error("SyncCmd unlock");
                    mSyncCmdLock.unlock();
                }
            }
        }
        return null;

    }
    private boolean ackCallBack(byte[] bodyData,int timeOut,int interval){
        int ret = -1;
        for(int i = 0; i < 5 ;i++){
            ret = write(pack(bodyData), timeOut);
            if (ret <= 0) {
                deviceLogger.debug("ackCallBack write fail ret=" + ret);
                return false;
            }
            byte[] ack = new byte[1];
            ret = read(ack,interval);
            deviceLogger.debug("ackCallBack read ret="+ret+" ack="+ack[0]);
            if(ret == ack.length && ack[0] == 0x06){
                deviceLogger.debug("ackCallBack succ.");
                return true;
            }
        }
        deviceLogger.debug("ackCallBack fail.");
        return false;
    }
    private void waitAsyncIDThread() throws InterruptedException {
        boolean isInterruptTime = EmvL3Global.isInterruptTime();
        deviceLogger.debug("waitAsyncIDThread1 isInterruptTime="+isInterruptTime);
        while (isInterruptTime) {
            Thread.sleep(10);
            isInterruptTime = EmvL3Global.isInterruptTime();
            deviceLogger.debug("waitAsyncIDThread isInterruptTime="+isInterruptTime);
        }
        deviceLogger.debug("waitAsyncIDThread2 isInterruptTime="+isInterruptTime);
    }
    private boolean getCancelAck() throws InterruptedException {
        boolean waitAck = isCancelOnInterrupt;
        if(isCancelOnInterrupt){
            isCancelOnInterrupt = false;
//            Thread.sleep(1000);对于中断线程延迟1S,读取perform应答.
            deviceLogger.debug("CancelOnInterrupt Read ack.");
        }
        return waitAck;
    }
    private void printFunctionId(int funid){
        deviceLogger.debug(">>>EMVL3:"+getFunctionDesc(funid));
    }

    private int getCommandTimeOut(int funId){
        int timeOutMS = 2000;
        if(isUseBleBase(emvExtParams)){
            timeOutMS = 4000;
        }
        if(funId == COMMAND_PERFORM_TRANSACTION){
            timeOutMS = EmvL3Global.getEmvStepTimeOutMs();
        }
        return timeOutMS;
    }

    private String getFunctionDesc(int funId){
        String cmdDesc = "unknow functionId";
        switch (funId){
            case COMMAND_TERMINAL_CONFIG_UPDATE :cmdDesc = "COMMAND_TERMINAL_CONFIG_UPDATE";break;
            case COMMAND_TERMINAL_CONFIG_GET    :cmdDesc = "COMMAND_TERMINAL_CONFIG_GET";break;
            case COMMAND_AID_CONFIG_UPDATE      :cmdDesc = "COMMAND_AID_CONFIG_UPDATE";break;
            case COMMAND_AID_CONFIG_GET         :cmdDesc = "COMMAND_AID_CONFIG_GET";break;
            case COMMAND_AID_CONFIG_REMOVE_ONE  :cmdDesc = "COMMAND_AID_CONFIG_REMOVE_ONE";break;
            case COMMAND_AID_CONFIG_REMOVE_ALL  :cmdDesc = "COMMAND_AID_CONFIG_REMOVE_ALL";break;
            case COMMAND_CAPK_UPDATE            :cmdDesc = "COMMAND_CAPK_UPDATE";break;
            case COMMAND_CAPK_GET               :cmdDesc = "COMMAND_CAPK_GET";break;
            case COMMAND_CAPK_REMOVE_ONE        :cmdDesc = "COMMAND_CAPK_REMOVE_ONE";break;
            case COMMAND_CAPK_REMOVE_ALL        :cmdDesc = "COMMAND_CAPK_REMOVE_ALL";break;
            case COMMAND_CERT_BLACK_UPDATE      :cmdDesc = "COMMAND_CERT_BLACK_UPDATE";break;
            case COMMAND_CERT_BLACK_GET         :cmdDesc = "COMMAND_CERT_BLACK_GET";break;
            case COMMAND_CERT_BLACK_REMOVE_ONE  :cmdDesc = "COMMAND_CERT_BLACK_REMOVE_ONE";break;
            case COMMAND_CERT_BLACK_REMOVE_ALL  :cmdDesc = "COMMAND_CERT_BLACK_REMOVE_ALL";break;
            case COMMAND_CARD_BLACK_UPDATE      :cmdDesc = "COMMAND_CARD_BLACK_UPDATE";break;
            case COMMAND_CARD_BLACK_GET         :cmdDesc = "COMMAND_CARD_BLACK_GET";break;
            case COMMAND_CARD_BLACK_REMOVE_ONE  :cmdDesc = "COMMAND_CARD_BLACK_REMOVE_ONE";break;
            case COMMAND_CARD_BLACK_REMOVE_ALL  :cmdDesc = "COMMAND_CARD_BLACK_REMOVE_ALL";break;
            case COMMAND_AID_GET_COUNT          :cmdDesc = "COMMAND_AID_GET_COUNT";break;
            case COMMAND_CAPK_GET_COUNT         :cmdDesc = "COMMAND_CAPK_GET_COUNT";break;
            case COMMAND_DEBUG_MASSAGE          :cmdDesc = "COMMAND_DEBUG_MASSAGE";break;
            case COMMAND_INIT_EMV_KERNEL        :cmdDesc = "COMMAND_INIT_EMV_KERNEL";break;
            case COMMAND_SET_DATA               :cmdDesc = "COMMAND_SET_DATA";break;
            case COMMAND_GET_DATA               :cmdDesc = "COMMAND_GET_DATA";break;
            case COMMAND_SET_TLV_LIST           :cmdDesc = "COMMAND_SET_TLV_LIST";break;
            case COMMAND_GET_TLV_LIST           :cmdDesc = "COMMAND_GET_TLV_LIST";break;
            case COMMAND_SET_DEBUG_MODE         :cmdDesc = "COMMAND_SET_DEBUG_MODE";break;
            case COMMAND_GET_VERSION            :cmdDesc = "COMMAND_GET_VERSION";break;
            case COMMAND_PERFORM_TRANSACTION    :cmdDesc = "COMMAND_PERFORM_TRANSACTION";break;
            case COMMAND_COMPLETE_TRANSACTION   :cmdDesc = "COMMAND_COMPLETE_TRANSACTION";break;
            case COMMAND_TERMINATE_TRANSACTION  :cmdDesc = "COMMAND_TERMINATE_TRANSACTION";break;
            case COMMAND_PREPROCESS_TRANSACTION :cmdDesc = "COMMAND_PREPROCESS_TRANSACTION";break;
            case COMMAND_CANCEL                 :cmdDesc = "COMMAND_CANCEL";break;
        }
        return cmdDesc;
    }
    private boolean isAsyncID(int funId) {
        return (funId == EmvL3Constant.FunctionId.COMMAND_SET_DATA || funId == EmvL3Constant.FunctionId.COMMAND_GET_DATA||
                funId == EmvL3Constant.FunctionId.COMMAND_SET_TLV_LIST || funId == EmvL3Constant.FunctionId.COMMAND_GET_TLV_LIST);
    }

    private void printCallBackStep(int callBackId) {
        if (callBackId == EmvL3Constant.CallbackId.COMMAND_NOTIFICATION) {
            deviceLogger.error("CALLBACK_ID_NOTIFICATION");
        } else if (callBackId == EmvL3Constant.CallbackId.COMMAND_SELECT_CANDIDATE_LIST) {
            deviceLogger.error("CALLBACK_ID_SELECT_CANDIDATE_LIST");
        } else if (callBackId == EmvL3Constant.CallbackId.COMMAND_AFTER_FINAL_SELECT) {
            deviceLogger.error("CALLBACK_ID_AFTER_FINAL_SELECT");
        } else if (callBackId == EmvL3Constant.CallbackId.COMMAND_CARDNUM_CONFIRM) {
            deviceLogger.error("CALLBACK_ID_CARDNUM_CONFIRM");
        } else if (callBackId == EmvL3Constant.CallbackId.COMMAND_CHECK_CREDENTIALS) {
            deviceLogger.error("CALLBACK_ID_CHECK_CREDENTIALS");
        } else if (callBackId == EmvL3Constant.CallbackId.COMMAND_GET_PIN) {
            deviceLogger.error("CALLBACK_ID_GET_PIN");
        } else {
            deviceLogger.error("CALLBACK_ID_UNKNOW");
        }
    }

    private byte[] pack(byte[] bodyData) {
        try {
            int sendLen = 1 + 2 + 2 + 1 + 1 + bodyData.length + 1 + 1;
            byte[] sendData = new byte[sendLen];
            byte[] llll = InnerUtils.intToBCD(sendLen - 5, 4, true);
            sendData[0] = 0x02;
            sendData[1] = llll[0];
            sendData[2] = llll[1];
            sendData[3] = 'L';
            sendData[4] = '0';
            sendData[5] = 0x2F;
            sendData[6] = 0x36;
            System.arraycopy(bodyData, 0, sendData, 7, bodyData.length);
            sendData[sendLen - 2] = 0x03;
            sendData[sendLen - 1] = (byte) calcLrc(sendData, sendLen - 2);
            deviceLogger.debug("pack sendData=" + ISOUtils.hexString(sendData));
            return sendData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private int calcLrc(byte[] data, int len) {
        char lrc = 0;
        int offset = 1;
        for (int i = offset; i < (len + offset); i++) {
            lrc ^= data[i];
        }
        return lrc;
    }

    private boolean isOpen = false;

    public int open(boolean isTestEcho) {
        deviceLogger.debug("[open] isTestEcho:"+isTestEcho+" isOpen:"+isOpen+" mChannelMode="+mChannelMode);
        if (isOpen) {
            deviceLogger.error("Channel has open");
            return 0;
        }
        if(mChannelMode == ChannelMode.BLUETOOTH_BASE){
            deviceLogger.debug("PortType=" + emvExtParams.getPortType() + " Baudrate=" + emvExtParams.getBaudrate()+" isTestEcho="+isTestEcho);
            int ret = mUARTModule.open(emvExtParams.getPortType(),((emvExtParams.getBaudrate()==null)?Baudrate.BPS115200:emvExtParams.getBaudrate()),null);
            if (ret < 0) {
                return -1;
            }
        } else if (mChannelMode == ChannelMode.USB) {
            deviceLogger.debug("PortType=" + mPortType + " Baudrate=" + mBaudrate+" isTestEcho="+isTestEcho);
            if (!openUsb(isTestEcho)) {
                return -1;
            }
        } else if (mChannelMode == ChannelMode.UART || mChannelMode == ChannelMode.MIN_USB) {
            deviceLogger.debug("PortType=" + mPortType + " Baudrate=" + mBaudrate);
            int ret = mUARTModule.open(mPortType, mBaudrate, null);
            if (ret < 0) {
                return -1;
            }
        } else {
            return -1;
        }
        isOpen = true;
        return 0;
    }

    private boolean openUsb(boolean isTestEcho) {
        try {
            int count = 0,totalNum = 50;
            if(isTestEcho){
                totalNum = 1;
            }
            while (count++ < totalNum) {
                long startTime = System.currentTimeMillis();
                int ret = mUSBModule.open(new SelectUsbDeviceListener() {
                    @Override
                    public UsbDevice onSelect(HashMap<String, UsbDevice> usbDeviceList) {
                        Iterator<UsbDevice> iterator = usbDeviceList.values().iterator();
                        while (iterator.hasNext()) {
                            UsbDevice device = iterator.next();
                            deviceLogger.error("VID=" + device.getVendorId() + " PID=" + device.getProductId());
                            boolean device1 = (((device.getVendorId() == 1840) || (device.getVendorId() == 0x32C3)) && (device.getProductId() == 56506));
                            boolean device2 = (device.getVendorId() == 0x32C3);//P180键盘VID固定0x32C3
                            boolean device3 = (device.getVendorId() == 1659 && device.getProductId() == 9171);
                            if (( device1 || device2 ) || device3) { // == if ( device1 || device2  || device3)
                                deviceLogger.error("find usb device");
                                return device;
                            }
                        }
                        //VID 0x0730,0x32C3,
                        //PID 0xDCBA,0xDCBA,
                        deviceLogger.error("can not find usb device");
                        return null;
                    }
                });
                long endTime = System.currentTimeMillis();
                deviceLogger.error("openUsb disTime=" + (endTime - startTime));
                mUSBModule.clearBuffer();
                if (ret < 0) {
                    mUSBModule.close();
                    Thread.sleep(10);
                    deviceLogger.error("openUsb count=" + count);
                    continue;
                } else {
                    deviceLogger.error("openUsb succ " + count);
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        deviceLogger.error("openUsb fail");
        return false;
    }

    public int read(byte[] outputData, int timeOut) {
        deviceLogger.debug("[read] mChannelMode:"+mChannelMode+";timeOut:"+timeOut+"outputData:"+(outputData==null?null:outputData.length));
        if(mChannelMode == ChannelMode.BLUETOOTH_BASE){
            return mUARTModule.read(outputData, outputData.length, timeOut);
        }else if (mChannelMode == ChannelMode.USB) {
            return mUSBModule.read(outputData, outputData.length, timeOut);
        } else if (mChannelMode == ChannelMode.UART || mChannelMode == ChannelMode.MIN_USB) {
            return mUARTModule.read(outputData, outputData.length, timeOut);
        } else {
            deviceLogger.error("Read ChannelMode=" + mChannelMode + " error");
            return -1;
        }
    }

    public int write(byte[] inputData, int timeOut) {
        if(mChannelMode == ChannelMode.BLUETOOTH_BASE){
            return mUARTModule.write(inputData, inputData.length, timeOut);
        } else if (mChannelMode == ChannelMode.USB) {
            return mUSBModule.write(inputData, inputData.length, timeOut);
        } else if (mChannelMode == ChannelMode.UART || mChannelMode == ChannelMode.MIN_USB) {
            return mUARTModule.write(inputData, inputData.length, timeOut);
        } else {
            deviceLogger.error("Write ChannelMode=" + mChannelMode + " error");
            return -1;
        }
    }

    public int close() {
        deviceLogger.debug("-----[close]-------");
        isOpen = false;
        if(mChannelMode == ChannelMode.BLUETOOTH_BASE){
            return mUARTModule.close();
        }else if (mChannelMode == ChannelMode.USB) {
            return mUSBModule.close();
        } else if (mChannelMode == ChannelMode.UART || mChannelMode == ChannelMode.MIN_USB) {
            return mUARTModule.close();
        } else {
            deviceLogger.error("Close ChannelMode=" + mChannelMode + " error");
            return -1;
        }
    }

    private boolean isSupportMs(){
        if (mChannelMode == ChannelMode.UART) {
            return ((MESerial)mUARTModule).isSupportMs0();
        } else if(mChannelMode == ChannelMode.BLUETOOTH_BASE){
            return false;
        }else {
            return true;
        }
    }
    private String hexString(byte[] data){
        return data == null?"null":ISOUtils.hexString(data);
    }

    public static void setEmvL3Comm(EmvL3Comm emvL3Comm) {
        EmvL3Comm.emvL3Comm = emvL3Comm;
    }

    private boolean isUseBleBase(EmvExtParams params){
        if(params == null){
            return false;
        }
        if(params.getPortType() == PortType.BLEBASE_USB1 ||
                params.getPortType() == PortType.BLEBASE_USB2 ||
                params.getPortType() == PortType.BLEBASE_RS232){
            return true;
        }
        return false;
    }
    private boolean isMicroUSB(EmvExtParams params){
        if(params == null){
            return false;
        }
        if(params.getPortType() == PortType.MIN_USB){
            return true;
        }
        return false;
    }
    private boolean isExtUSB(EmvExtParams params){//是否在EmvExtParams参数指定USB
        if(params == null){
            return false;
        }
        if(params.getPortType() == PortType.USB){
            return true;
        }
        return false;
    }
}
