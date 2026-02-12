package com.newland.sdk.me.module.externalPininput;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.newland.os.NlBuild;
import android.os.Build;

import com.newland.sdk.me.conn.SimpleDeviceManager;
import com.newland.sdk.me.module.emvl3.impl.EmvL3Global;
import com.newland.sdk.me.module.serialport.MESerial;
import com.newland.sdk.me.module.usb.MEUSB;
import com.newland.sdk.me.utils.PropertiesUtils;
import com.newland.sdk.module.externalPin.PinpadInitExtParams;
import com.newland.sdk.module.serialport.Baudrate;
import com.newland.sdk.module.serialport.PinpadModel;
import com.newland.sdk.module.serialport.PortType;
import com.newland.sdk.module.serialport.SerialExtParams;
import com.newland.sdk.module.usb.SelectUsbDeviceListener;
import com.newland.sdk.mtype.ProcessTimeoutException;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtype.util.Dump;
import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.utils.ISOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import com.newland.NlBluetooth.control.BluetoothController;
import com.newland.sdk.utils.TLVPackage;

/**
 * @description: Package of pinpad
 * @author: Lindan
 * @create: 2019/07/31
 */
public class PinpadPackage {

    private MposComm mMposComm;

    public static final int ECHO_TEST_COUNT = 5;
    public static final int ECHO_TEST_TIMEOUT_MS = 10;
    public static final int EXTCMD_TIMEOUT_MS = 2000;//接口没有指定超时时间情况下的默认时间;
    public static final int EXTCMD_OFFSETTIME_MS = 1000;//接口有指定超时时间情况下偏移时间;

    private static final byte STX = 0x02;
    private static final byte ETX = 0x03;

    private static final byte[] STX_OVERSEAS = new byte[]{0x02};
    private static final byte[] ETX_OVERSEAS = new byte[]{0x03};

    private static final byte[] SEPARATOR_SLASH = new byte[]{0x2F}; // "/"

    private static final byte[] SEPARATOR_POINT = new byte[]{0x2E};// "."
    private static final byte ACK = 0x06;

    private static final byte NAK = 0x15;

    private static final int LEN_STX = 1;

    private static final int LEN_SEPARATOR = 1;

    private static final int LEN_MESSAGETYPE = 2;

    private static final int LEN_LENGTH = 2; // 数据长度2字节的bcd

    private static final int LEN_LRC = 1;

    private static final int LEN_ETX = 1;

    private static final int OVERSEASE_PIN = 0x00;// 0 - PIN, 1- MAC, 2 - Data
    private static final int OVERSEASE_MAC = 0x01;
    private static final int OVERSEASE_TRACK = 0x02;

    private static final int MASTER = 0x00;
    private static final int DUKPT = 0x01;
    private static final int AES = 0x02;

    private static final int TIME_OUT = 5000;

    private DeviceLogger devicelogger = DeviceLoggerFactory.getLogger("PinpadPackage");

    private MESerial serialOper;
    private PortType portType;
    private Baudrate baudRate;
    private PinpadModel model = PinpadModel.SP_OVERSEAS;
    private MEUSB usbModule;
    private Context context;
    private AbstractDevice device;
    private static PinpadPackage pinpadPackage;
    private volatile boolean initialize = false;
    private volatile boolean isSignPad = false;
    private PinpadInitExtParams params;

    private static List<String> ackcmdList = new ArrayList<String>();
    private BleBasePackage bleBasePackage;

    private boolean isReadBreak = false;

    static {
        //返回报文只包含ack应答的指令
        ackcmdList.add("3343");
        ackcmdList.add("4138");
        ackcmdList.add("3339");
        ackcmdList.add("3336");
    }

    private PinpadPackage(AbstractDevice device, Context context) {
        this.context = context;
        this.device = device;
        this.initialize = false;
        this.isSignPad = false;
        mMposComm = new MposComm(device);
    }

    public static PinpadPackage getInstance(AbstractDevice device, Context context) {
        if (pinpadPackage == null) {
            pinpadPackage = new PinpadPackage(device, context);
        }
        return pinpadPackage;
    }


    public boolean init(PinpadInitExtParams params, boolean isSign) {
        devicelogger.debug("Start Init ExtPinpad.initialize:"+initialize+";isSignPad:"+isSignPad);
        this.isSignPad = isSign;
        this.params = params;
        boolean isUseBleBase = false;
        isReadBreak = true;
        if(isUseBleBase(params)){
            devicelogger.debug("[init]isUseBleBase true");
            try {
                bleBasePackage = BleBasePackage.getInstance();
                isUseBleBase = true;//蓝牙底座，还是每次调用init都重新初始化
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }catch (Error e){
                e.printStackTrace();
                return false;
            }
        }

        if (params != null && !params.isInit()) {
            if (initialize && this.isSignPad == isSign && !isUseBleBase) {//提前处理，避免应用在处理耗时操作(例如输密)，同时调用初始化接口，同步会导致应用卡死
                if(!(usbModule!=null&&!usbModule.isOpen())){ //emv那边会把口给关闭了导致pinpad模块无法正常读写
                    devicelogger.debug("[init]already init");
                    return true;
                }
            }
        }
        synchronized (SimpleDeviceManager.externalLock) {
            if (EmvL3Global.getIsEmvL3GetPinProcess()) {
                devicelogger.error("[Init] onEmvL3GetPinProcess");
                return true;
            }
            if(isUseBleBase(params)){
                devicelogger.debug("[init]go to bleBasePackage");
                this.isSignPad = isSign;
                this.params = params;
                boolean issucess = bleBasePackage.init(context,params,isSign);
                return issucess;
            }

            long startTime = System.currentTimeMillis();
            boolean result = this.init0(params, isSign);
            long endTime = System.currentTimeMillis();
            devicelogger.debug("ExtPinpadInit result=" + result + " disTime=" + (endTime - startTime));
            if (result) {
                initialize = true;
            }
            return result;
        }
    }

    private boolean init0(PinpadInitExtParams params, boolean isSign) {
        devicelogger.debug("[init] start pinpad init. params=" + params + " isSign=" + isSign);
        boolean isUSBEnbale = true;
        if(params!=null ){
            isUSBEnbale = params.isUSBPortEnable();
        }
        devicelogger.debug("[init] isUSBEnbale:" + isUSBEnbale);

        if (isUSBEnbale && openUsb(1)) {
            devicelogger.error("[init] start init usb mode");
            serialOper = null;

            this.model = PinpadModel.SP_OVERSEAS;
            byte[] messageType = new byte[]{0x36, 0x34};
            byte[] rspCode = sendPinpadCmd(messageType, null, 0, true);
            devicelogger.debug("[init] usb PinpadModel.SP_OVERSEAS rspCode=" + (rspCode == null ? null : ISOUtils.hexString(rspCode)));

            if (rspCode != null && rspCode.length >= 2) {
                getPinpadRspCode();
                if (Arrays.equals(new byte[]{rspCode[0], rspCode[1]}, new byte[]{0x36, 0x35})) {
                    // ME51R波特率都是115200
                    String storeParam = "BPS115200".concat("|").concat("USB");
                    PropertiesUtils propertiesUtils = PropertiesUtils.getInstance("COMMParam", "/data/share/SDK_EXT_PINPAD/COMMParam.properties");
                    propertiesUtils.setProp("EXT_PARAM", storeParam);
                    devicelogger.error("[init] usb find SP_OVERSEAS Pinpad.");
                    return true;
                }
            }

            byte[] reqData = new byte[]{0x1B, 0x78, 0x32, 0x0D, 0x0A};
            this.model = PinpadModel.SP;
            byte[] data = sendPinpadCmd(null, reqData, 0, true);
            devicelogger.debug("[init] usb PinpadModel.SP data=" + (data == null ? null : ISOUtils.hexString(data)));
            if (data != null && InnerUtils.hexString(data).startsWith("C001")) {
                // ME51R波特率都是115200，保存的文本为 eg.: BPS115200|RS232
                String storeParam = "BPS115200".concat("|").concat("USB");
                PropertiesUtils propertiesUtils = PropertiesUtils.getInstance("COMMParam", "/data/share/SDK_EXT_PINPAD/COMMParam.properties");
                propertiesUtils.setProp("EXT_PARAM", storeParam);
                devicelogger.error("[init] find usb SP Pinpad.");
                return true;
            }


            devicelogger.error("[init] usb Pinpad fail");
            return false;
        } else {
            devicelogger.error("[init] start uart mode");
            this.usbModule = null;
            serialOper = new MESerial(device, context);
        }

        if (params != null && !params.isAutoMatch()) {
            if (isSign) {
                devicelogger.error("[initSign] isSign=" + isSign + " PortType=" + params.getPortType() + " Baudrate=" + params.getBaudrate());
                return initSign(Arrays.asList(params.getBaudrate()), Arrays.asList(params.getPortType()));
            }
            devicelogger.error("[init] PortType=" + params.getPortType() + " Baudrate=" + params.getBaudrate());
            return init(Arrays.asList(params.getBaudrate()), Arrays.asList(params.getPortType()));
        } else {
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
            devicelogger.error("[init] portTypes=" + portTypes + " baudrates=" + baudrates + " isSign=" + isSign);
            if (isSign) {
                return initSign(baudrates, portTypes);
            }
            return init(baudrates, portTypes);
        }
    }

    private boolean openUsb(int totalNum) {
        try {
            if (usbModule == null) {
                usbModule = MEUSB.getInstance(context);
                ((MEUSB) usbModule).setWorkingSyncMode(true);
            }
            int count = 0;
            while (count++ < totalNum) {
                int ret = usbModule.open(new SelectUsbDeviceListener() {
                    @Override
                    public UsbDevice onSelect(HashMap<String, UsbDevice> usbDeviceList) {
                        devicelogger.debug("[onSelect] usbDeviceList="+usbDeviceList);
                        //device3 最早期是为了适配N750P当键盘使用，
                        // 后面发现 使用N910P+锁扣式底座  ；或者F10 或者 N950C + 有线扩展坞，POS会自动带有device3，导致识别成自动生成的device3外接键盘,导致异常
                        //所以，如果device3和其它device同时存在，优选其它device
                        UsbDevice preferredDevice = null; // 用于暂存device1或device2
                        UsbDevice fallbackDevice = null;  // 用于暂存device3

                        Iterator<UsbDevice> iterator = usbDeviceList.values().iterator();
                        while (iterator.hasNext()) {
                            UsbDevice device = iterator.next();
                            devicelogger.error("VID=" + device.getVendorId() + " PID=" + device.getProductId());
                            boolean device1 = (((device.getVendorId() == 1840) || (device.getVendorId() == 0x32C3)) && (device.getProductId() == 56506));
                            boolean device2 = (device.getVendorId() == 0x32C3);//P180键盘VID固定0x32C3
                            boolean device3 = (device.getVendorId() == 1659 && device.getProductId() == 9171);
                            if (device1 || device2) {
                                preferredDevice = device; // 暂存优先设备
                            }
                            if (device3) {
                                fallbackDevice = device;  // 暂存备选设备（仅当不是优先设备时）
                            }

                        }

                        // 优先返回device1/device2，若不存在则返回device3
                        if (preferredDevice != null) {
                            devicelogger.error("find usb device,"+"VID=" + preferredDevice.getVendorId() + " PID=" + preferredDevice.getProductId());
                            return preferredDevice;
                        } else if (fallbackDevice != null) {
                            devicelogger.error("find usb device3"+"VID=" + preferredDevice.getVendorId() + " PID=" + preferredDevice.getProductId());
                            return fallbackDevice;
                        } else {
                            devicelogger.error("can not find usb device");
                            return null;
                        }

                    }
                });
                usbModule.clearBuffer();
                if (ret < 0) {
                    usbModule.close();
                    Thread.sleep(10);
                    devicelogger.error("openUsb count=" + count);
                    continue;
                } else {
                    devicelogger.error("openUsb succ " + count);
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        devicelogger.error("openUsb fail");
        return false;
    }

    private boolean initSign(List<Baudrate> baudRates, List<PortType> portTypes) {
        devicelogger.debug("[initSign] portTypes=" + portTypes + " baudRates=" + baudRates);
        for (Baudrate baudrate : baudRates) {
            for (PortType portType : portTypes) {
                this.baudRate = baudrate;
                this.portType = portType;
                this.model = PinpadModel.SP;
                byte[] resp = boardTxn(null, (byte) 0xA0, new byte[0], 0);
                if (checkResp((byte) 0xB0, resp, 1)) {
                    String storeParam = baudrate.toString().concat("|").concat(portType.toString());
                    PropertiesUtils propertiesUtils = PropertiesUtils.getInstance("COMMParam", "/data/share/SDK_EXT_PINPAD/COMMParam.properties");
                    propertiesUtils.setProp("EXT_PARAM", storeParam);
                    devicelogger.error("[initSign] uart find SP Pinpad");
                    return true;
                }

                this.baudRate = baudrate;
                this.portType = portType;
                this.model = PinpadModel.SP_OVERSEAS;
                byte[] messageType = "S0".getBytes();
                resp = boardTxn(messageType, (byte) 0xA0, new byte[0], 0);
                devicelogger.debug("Oversea hand shake response: " + (resp == null ? "null" : InnerUtils.hexString(resp)));
                if (resp != null && resp.length >= 6) {
                    getPinpadRspCode();
                    if (Arrays.equals("S1".getBytes(), new byte[]{resp[0], resp[1]})) {
                        if (Arrays.equals(new byte[]{0x30, 0x30}, new byte[]{resp[4], resp[5]})) {
                            if (resp[6] == 0x01) {
                                this.baudRate = baudrate;
                                this.portType = portType;
                                this.model = PinpadModel.SP_OVERSEAS;
                                String storeParam = baudrate.toString().concat("|").concat(portType.toString());
                                PropertiesUtils propertiesUtils = PropertiesUtils.getInstance("COMMParam", "/data/share/SDK_EXT_PINPAD/COMMParam.properties");
                                propertiesUtils.setProp("EXT_PARAM", storeParam);
                                devicelogger.error("[initSign] uart find SP_OVERSEAS Pinpad");
                                return true;
                            }
                        }
                    }

                }
            }
        }
        devicelogger.error("[initSign] find Pinpad fail");
        return false;
    }

    public boolean checkResp(byte respCommand, byte[] resp, int minDataLen) {
        devicelogger.debug("-----checkResp------, " + Integer.toHexString(respCommand & 0xFF).toUpperCase());
        if (resp == null || resp.length == 0) {
            return false;
        }
        devicelogger.debug("-----resp------" + InnerUtils.hexString(resp) + ", minDataLen " + minDataLen);
        if (resp[0] != respCommand) {
            return false;
        }
        if (resp.length < minDataLen + 1) {
            return false;
        }
        return true;
    }

    private boolean init(List<Baudrate> baudRates, List<PortType> portTypes) {
        devicelogger.debug("[init] portTypes=" + portTypes + " baudRate=" + baudRates);
        for (PortType portType : portTypes) {
            for (Baudrate baudrate : baudRates) {
                byte[] reqData = new byte[]{0x1B, 0x78, 0x32, 0x0D, 0x0A};
                this.baudRate = baudrate;
                this.portType = portType;
                this.model = PinpadModel.SP;
                open(portType, baudRate, null);
                byte[] data = sendPinpadCmd(null, reqData, 0, true);
                if (data != null && InnerUtils.hexString(data).startsWith("C001")) {
                    String storeParam = baudrate.toString().concat("|").concat(portType.toString());
                    PropertiesUtils propertiesUtils = PropertiesUtils.getInstance("COMMParam", "/data/share/SDK_EXT_PINPAD/COMMParam.properties");
                    propertiesUtils.setProp("EXT_PARAM", storeParam);
                    devicelogger.error("[init] uart find SP Pinpad");
                    return true;
                }
                this.model = PinpadModel.SP_OVERSEAS;
                byte[] messageType = new byte[]{0x36, 0x34};
                byte[] rspCode = sendPinpadCmd(messageType, null, 0, true);
                if (rspCode != null && rspCode.length >= 2) {
                    getPinpadRspCode();
                    if (Arrays.equals(new byte[]{rspCode[0], rspCode[1]}, new byte[]{0x36, 0x35})) {
                        String storeParam = baudrate.toString().concat("|").concat(portType.toString());
                        PropertiesUtils propertiesUtils = PropertiesUtils.getInstance("COMMParam", "/data/share/SDK_EXT_PINPAD/COMMParam.properties");
                        propertiesUtils.setProp("EXT_PARAM", storeParam);
                        devicelogger.error("[init] uart find SP_OVERSEAS Pinpad");
                        return true;
                    }
                }
            }
        }
        devicelogger.error("[init] uart Pinpad fail");
        return false;
    }

    private boolean isNdkUartPort() {
        if (serialOper != null && NlBuild.VERSION.MODEL.equalsIgnoreCase("N850") && portType == PortType.PINPAD) {
            return true;
        }
        return false;
    }

    private boolean isSupportMs() {
        if (serialOper != null) {
            return ((MESerial) serialOper).isSupportMs0();
        }
        return true;
    }

    public int unblockSendCmd(byte[] messageType, byte[] data) {
        if(mMposComm.getMposConnect()){
            byte[] pack = makeupOverseas(messageType, data);
            mMposComm.unblockSendCmd(makeupOverseas(messageType, data),3);
            return pack.length;
        }
        if(isUseBleBase(params)){
            return bleBasePackage.unblockSendCmd(messageType,data);
        }
        devicelogger.debug("[unblockSendCmd] messageType=" + (messageType == null ? "null" : ISOUtils.hexString(messageType)) + " data=" + (data == null ? "null" : ISOUtils.hexString(data)));
        int result = 0;
//            open(portType, baudRate, null);
        if (serialOper != null) {
            serialOper.clearBuffer(0);
        }
        if (!Arrays.equals(messageType, new byte[]{0x33, 0x38})) {
            if (usbModule != null) {
                usbModule.clearBuffer();
            }
        }
        try {
            byte[] pack;
            if (model == PinpadModel.SP_OVERSEAS) {
                pack = makeupOverseas(messageType, data);
            } else {
                pack = makeup(data);
            }
            result = write(pack, pack.length, 0);
        } catch (Exception ex) {
            ex.printStackTrace();
            result = -1;
        }
        return result;
    }

    /**
     * 国内外发送外接密码键盘命令数据
     *
     * @param data pinpad的命令数据
     * @return
     */
    public byte[] sendPinpadCmd(byte[] messageType, byte[] data, int originalTime, boolean isRead) {
        synchronized (SimpleDeviceManager.externalLock) {
            isReadBreak = false;
            devicelogger.debug("[sendPinpadCmd]params:" + (params == null ? "null" : params.toString()));
            if(mMposComm.getMposConnect()){
                devicelogger.debug("[sendPinpadCmd] Mpos messageType=" + (messageType == null ? "null" : ISOUtils.hexString(messageType)) + " data=" + (data == null ? "null" : ISOUtils.hexString(data)) + " originalTime=" + originalTime + " isRead=" + isRead+" model="+model);
                if(Arrays.equals(messageType,new byte[]{0x33, 0x39})){//蜂鸣器指令
                    mMposComm.unblockSendCmd(makeupOverseas(messageType, data),originalTime);
                    return new byte[]{0x06};
                }
                byte[] respAllData = mMposComm.sendPinpadCmd(makeupOverseas(messageType, data),originalTime,isRead);
                if(respAllData != null && respAllData.length > 3){
                    byte[] respData = new byte[respAllData.length-3];
                    System.arraycopy(respAllData,3,respData,0,respData.length);
                    return respData;
                }
                return respAllData;
            }
            if(isUseBleBase(params)){
                return bleBasePackage.sendPinpadCmd(messageType,data,originalTime,isRead);
            }
            devicelogger.debug("[sendPinpadCmd] messageType=" + (messageType == null ? "null" : ISOUtils.hexString(messageType)) + " data=" + (data == null ? "null" : ISOUtils.hexString(data)) + " originalTime=" + originalTime + " isRead=" + isRead);
            int result = 0;
//            open(portType, baudRate, null);
            if (serialOper != null) {
                serialOper.clearBuffer(0);
            }
            if (usbModule != null) {
                usbModule.clearBuffer();
            }
            try {
                byte[] pack;
                if (model == PinpadModel.SP_OVERSEAS) {
                    pack = makeupOverseas(messageType, data);
                } else {
                    pack = makeup(data);
                }

                result = write(pack, pack.length, 0);

                ByteArrayOutputStream bos = new ByteArrayOutputStream();

                int count = 0;

                if (!isRead) {
                    devicelogger.debug("Don`t read data!");
                    return null;
                }
                boolean isSupportMs = isSupportMs();
                boolean isNdkUartPort = isNdkUartPort();
                int interval = 1000, sleepTimeMs = 100;
                if (isSupportMs) {
                    interval = 100;
                }
                int allCount0 = originalTime / interval;
                int allCount = allCount0;
                if (isNdkUartPort) {
                    int sleepCount = allCount0 * sleepTimeMs / interval;
                    if (interval != sleepTimeMs) {
                        allCount = allCount0 - sleepCount;
                    } else {
                        allCount = allCount / 2 + allCount % 2;
                    }
                    devicelogger.debug("[sendPinpadCmd] allCount0=" + allCount0 + " sleepCount=" + sleepCount);
                }

                devicelogger.debug("[sendPinpadCmd] srcTime=" + originalTime + " allCount=" + allCount + " isSupportMs=" + isSupportMs + " isNdkUartPort=" + isNdkUartPort);
                devicelogger.debug("[sendPinpadCmd] SendLen=" + pack.length + " writeLen=" + result + " Send data=" + (pack == null ? "null" : InnerUtils.hexString(pack)));
                devicelogger.error("[sendPinpadCmd] Begin to receive.");
                while (true) {
                    if (isReadBreak) {
                        devicelogger.debug("readBreak");
                        return null;
                    }
                    byte[] tmp = new byte[1];
                    if (allCount == 0) {//轮询使用
                        int time = 0;
                        long startTime = System.currentTimeMillis();
                        while (time < ECHO_TEST_COUNT) {
                            result = read(tmp, 1, ECHO_TEST_TIMEOUT_MS);
                            if (result < 0 || result != 1) {
                                time++;
                                Thread.sleep(5);
                            } else {
                                break;
                            }
                        }
                        long endTime = System.currentTimeMillis();
                        devicelogger.debug("Echo count=" + time + " disTimeMs=" + (endTime - startTime));
                    } else {
                        result = read(tmp, 1, interval);
                    }
                    count++;
                    if (allCount == 0) { //轮询使用
                        if (result < 0 || result != 1) {
//                            close();
                            devicelogger.error("Init TimeOut" + " result=" + result);
                            return null;
                        }
                    } else if (count > allCount) {
                        devicelogger.error("Read timeout!!!");
//                        close();
                        return null;
                    }

                    if (result < 0 || result != 1) {
                        if (isNdkUartPort) {
                            Thread.sleep(sleepTimeMs);//优化内外置一起寻卡，内置会比较慢的问题
                        }
                        continue;
                    }

                    if (onlyACKCommand(messageType) && tmp[0] == ACK) {
                        String type = InnerUtils.hexString(new byte[]{messageType[0], messageType[1]});
                        devicelogger.debug("--onlyACKCommand--/" + type);
                        devicelogger.debug("--onlyACKCommand--/" + tmp[0]);
                        if(type.equals("4138")){
                            tmp[0] = 0;
                            int time = 0,result2;
                            while (time < ECHO_TEST_COUNT) {
                                result2 = read(tmp, 1, 0);
                                devicelogger.debug("--onlyACKCommand--result2="+result2+ " value=" + tmp[0]);
                                if (result2 < 0 || result2 != 1) {
                                    time++;
                                    Thread.sleep(10);
                                } else {
                                    break;
                                }
                            }
                            if(tmp[0] != STX){
                                return new byte[]{0x06};
                            }
                        }else{
                            //close();
                            return tmp;
                        }
                    }
//                    if (null != messageType && Arrays.equals(new byte[]{messageType[0], messageType[1]}, new byte[]{0x33, 0x43}) && tmp[0] == ACK) {
//                        devicelogger.debug("----" + InnerUtils.hexString(new byte[]{messageType[0], messageType[1]}));
//                        devicelogger.debug("-----" + tmp[0]);
//                        if (Arrays.equals(new byte[]{messageType[0], messageType[1]}, new byte[]{0x33, 0x43}) && tmp[0] == ACK) { // 海外键盘LCD显示返回ACK
//                            devicelogger.debug("-----message type:33 43----");
////                            close();
//                            return tmp;
//                        }
//                    }
                    if (tmp[0] == NAK) { // SP100海外版专用
                        devicelogger.debug("----------NAK------");
//                        close();
                        return tmp;
                    }
                    if (tmp[0] == STX) {
                        try {
                            bos.write(tmp);
                        } catch (IOException e) {
                            devicelogger.error("bos write excetion", e);
//                            close();
                            return null;
                        }
                        break;
                    }
                }
                byte[] lenB = new byte[2];
                result = read(lenB, 2, originalTime);
                if (result != 2) {
                    devicelogger.error("---result != 2----");
//                    close();
                    return null;
                }
                try {
                    bos.write(lenB);
                } catch (IOException e) {
                    devicelogger.error("bos write excetion", e);
//                    close();
                    return null;
                }
                // 从Command ID到ETX的长度
                int len;

                if (model == PinpadModel.SP_OVERSEAS) {
                    len = InnerUtils.bcdToInt(lenB, 0, 4, true);  //海外版sp100返回的长度是十进制的。
                    len = len + 1;//再读一个字节lrc
                } else {
                    len = ((0xFF & lenB[0]) << 8) + (lenB[1] & 0xFF);
                }

                for (int i = 0; i < len + 1; i += 4096) {
                    int needLen = 0;
                    if (len + 1 - i >= 4096) {
                        needLen = 4096;
                    } else {
                        needLen = len + 1 - i;
                    }
                    byte[] tmp = new byte[needLen];
                    result = read(tmp, needLen, originalTime);
                    if (result != needLen) {
                        // error
                        devicelogger.error("----result != needLen------");
//                        close();
                        return null;
                    }
                    try {
                        bos.write(tmp);
                    } catch (IOException e) {
                        devicelogger.error("bos write excetion", e);
//                        close();
                        return null;
                    }
                }
                byte[] resp = bos.toByteArray();
                if (resp == null) {
                    devicelogger.error("----resp == null------");
//                    close();
                    return null;
                }
                devicelogger.debug("[sendPinpadCmd] Receive data=" + (resp == null ? "null" : InnerUtils.hexString(resp)) + " len=" + resp.length);
                // unpack data
                int position = 0;
                for (position = 0; position < resp.length; position++) {
                    if (resp[position] == STX) {
                        break;
                    }
                }
                if (position + 2 + 1 > resp.length) {
                    devicelogger.error("------position + 2 + 1 > resp.length----");
//                    close();
                    return null;
                }

                if (model == PinpadModel.SP_OVERSEAS) {
                    len = InnerUtils.bcdToInt(new byte[]{resp[position + 1], resp[position + 2]}, 0, 4, true);  //海外版sp100返回的长度是十进制的。
                } else {
                    len = ((0xFF & resp[position + 1]) << 8) + (resp[position + 2] & 0xFF);
                }
                // position += 2;
                if (position + 2 + len + 1 + 1 > resp.length) {
                    devicelogger.error("--------position + 2 + len + 1 + 1 > resp.length-------");
//                    close();
                    return null;
                }
                byte lrc = calcLRC(resp, position + 3, position + 2 + len);
                if (model == PinpadModel.SP_OVERSEAS) {
                    lrc = calcLRC(resp, 1, position + 3 + len);
                }
                if (model == PinpadModel.SP && lrc != resp[position + 2 + len + 1]) {
                    devicelogger.error("SP10 lrc not equal:" + lrc + " " + resp[position + 2 + len + 1]);
//                    close();
                    return null;
                } else if (model == PinpadModel.SP_OVERSEAS && lrc != resp[position + 2 + len + 2]) {
                    devicelogger.error("len:"+position + 2 + len + 1);

                    devicelogger.error(ISOUtils.hexString(new byte[]{resp[position + 2 + len + 1]}));
                    devicelogger.error(ISOUtils.hexString(new byte[]{lrc}));

                    devicelogger.error("SP100_OVERSEAS lrc not equal:" + lrc + " " + resp[position + 2 + len + 1]);
//                    close();
                    return null;
                }
                byte[] tmp = new byte[len];
                System.arraycopy(resp, position + 2 + 1, tmp, 0, len);
                resp = tmp;
                devicelogger.error("[sendPinpadCmd] end!!! resp="+hexString(resp));
//                close();
                return resp;
            } catch (Exception e1) {
                e1.printStackTrace();
                return null;
            }
        }
    }

    private boolean onlyACKCommand(byte[] messageType) {
        if (null != messageType && ackcmdList.contains(ISOUtils.hexString(messageType))) {
            return true;
        }
        return false;
    }

    /**
     * 国内外发送外接密码键盘命令数据
     *
     * @param data pinpad的命令数据
     * @return
     */
    public byte[] sendCmd(byte[] messageType, byte[] data, int originalTimeOut) {
        synchronized (SimpleDeviceManager.externalLock) {

            if(mMposComm.getMposConnect()){
                devicelogger.debug("[sendCmd] Mpos messageType=" + (messageType == null ? "null" : ISOUtils.hexString(messageType)) + " data=" + (data == null ? "null" : ISOUtils.hexString(data)) + " originalTimeOut=" + originalTimeOut +" model="+model);
                return mMposComm.sendCmd(data,originalTimeOut);
            }

            if(isUseBleBase(params)){
                return bleBasePackage.sendCmd(messageType, data, originalTimeOut);
            }
            devicelogger.debug("[sendCmd] messageType=" + (messageType == null ? "null" : ISOUtils.hexString(messageType)) + " data=" + (data == null ? "null" : ISOUtils.hexString(data)) + " originalTimeOut=" + originalTimeOut);
            int result = 0;
//        open(portType, baudRate, null);
            try {
                devicelogger.debug("send:" + (data == null ? "null" : InnerUtils.hexString(data)));

                result = write(data, data.length, 0);

                if (Arrays.equals(data, new byte[]{0x1B, 0x5A, 0x0D, 0x0A})) { //国内版如果是撤销指令 直接取消读取数据，以防冲突
                    devicelogger.debug("cancel command.");
                    return null;
                }

                ByteArrayOutputStream bos = new ByteArrayOutputStream();

                int count = 0;
                if (null != messageType) {
                    if (Arrays.equals(new byte[]{messageType[0], messageType[1]}, new byte[]{0x33, 0x38})) { // 海外版取消密码直接返回ACK
                        return null;
                    }
                }

                boolean isSupportMs = isSupportMs();
                boolean isNdkUartPort = isNdkUartPort();
                int interval = 1000, sleepTimeMs = 100;
                if (isSupportMs) {
                    interval = 100;
                }
                int allCount0 = originalTimeOut / interval;
                int allCount = allCount0;
                if (isNdkUartPort) {
                    int sleepCount = allCount0 * sleepTimeMs / interval;
                    if (interval != sleepTimeMs) {
                        allCount = allCount0 - sleepCount;
                    } else {
                        allCount = allCount / 2 + allCount % 2;
                    }
                    devicelogger.debug("[sendCmd] allCount0=" + allCount0 + " sleepCount=" + sleepCount);
                }

                devicelogger.debug("[sendCmd] srcTime=" + originalTimeOut + " allCount=" + allCount + " isSupportMs=" + isSupportMs + " isNdkUartPort=" + isNdkUartPort);
                devicelogger.debug("[sendCmd] SendLen=" + data.length + " writeLen=" + result + " Send data=" + (data == null ? "null" : InnerUtils.hexString(data)));
                devicelogger.error("[sendCmd] Begin to receive.");
                while (true) {
                    byte[] tmp = new byte[1];
                    result = read(tmp, 1, interval);
                    count++;
                    if (count > allCount) {
                        devicelogger.debug("read timeout");
//                    close();
                        return null;
                    }
                    if (result < 0 || result != 1) {
                        if (isNdkUartPort) {
                            Thread.sleep(sleepTimeMs);// 优化内外置一起寻卡，内置会比较慢的问题
                        }
                        continue;
                    }

                    if (onlyACKCommand(messageType) && tmp[0] == ACK) {
                        String type = InnerUtils.hexString(new byte[]{messageType[0], messageType[1]});
                        devicelogger.debug("sendCmd --onlyACKCommand--/" + type);
                        devicelogger.debug("sendCmd --onlyACKCommand---/" + tmp[0]);
                        if(type.equals("4138")){
                            tmp[0] = 0;
                            int time = 0,result2;
                            while (time < ECHO_TEST_COUNT) {
                                result2 = read(tmp, 1, 0);
                                devicelogger.debug("sendCmd --onlyACKCommand--result2="+result2+ " value=" + tmp[0]);
                                if (result2 < 0 || result2 != 1) {
                                    time++;
                                    Thread.sleep(10);
                                } else {
                                    break;
                                }
                            }
                            if(tmp[0] != STX){
                                return new byte[]{0x06};
                            }
                        }else{
                            //close();
                            return tmp;
                        }
                    }
//                if (null != messageType && Arrays.equals(new byte[]{messageType[0], messageType[1]}, new byte[]{0x33, 0x43}) && tmp[0] == ACK) {
//                    devicelogger.debug("----" + InnerUtils.hexString(new byte[]{messageType[0], messageType[1]}));
//                    devicelogger.debug("-----" + tmp[0]);
//                    if (Arrays.equals(new byte[]{messageType[0], messageType[1]}, new byte[]{0x33, 0x43}) && tmp[0] == ACK) { // 海外键盘LCD显示返回ACK
////                        close();
//                        return tmp;
//                    }
//                }
                    if (tmp[0] == NAK) { // SP100海外版专用
//                    close();
                        return tmp;
                    }
                    if (tmp[0] == STX) {
                        try {
                            bos.write(tmp);
                        } catch (IOException e) {
                            devicelogger.error("bos write excetion", e);
//                        close();
                            return null;
                        }
                        break;
                    }
                }
                byte[] lenB = new byte[2];
                int read = 0;
                result = read(lenB, 2, originalTimeOut);
                if (result != 2) {
//                close();
                    return null;
                }
                try {
                    bos.write(lenB);
                } catch (IOException e) {
                    devicelogger.error("bos write excetion", e);
//                close();
                    return null;
                }
                // 从Command ID到ETX的长度
                int len;

                if (model == PinpadModel.SP_OVERSEAS) {
                    len = InnerUtils.bcdToInt(lenB, 0, 4, true);  //海外版sp100返回的长度是十进制的。
                    len = len + 1;//再读一个字节lrc
                } else {
                    len = ((0xFF & lenB[0]) << 8) + (lenB[1] & 0xFF);
                }

//                int maxLen = 2 * 1024;
                int maxLen = 400;
                for (int i = 0; i < len + 1; i += maxLen) {
                    int needLen = 0;
                    if (len + 1 - i >= maxLen) {
                        needLen = maxLen;
                    } else {
                        needLen = len + 1 - i;
                    }
                    byte[] tmp = new byte[needLen];
                    result = read(tmp, needLen, originalTimeOut);
                    if (result != needLen) {
                        // error
//                    close();
                        return null;
                    }
                    try {
                        bos.write(tmp);
                    } catch (IOException e) {
                        devicelogger.error("bos write excetion", e);
//                    close();
                        return null;
                    }
                }

                byte[] resp = bos.toByteArray();
                if (resp == null) {
//                close();
                    return null;
                }
                devicelogger.debug("[sendCmd] Receive data=" + (resp == null ? "null" : InnerUtils.hexString(resp)) + " len=" + resp.length);
                devicelogger.debug("[sendCmd] end!!!");
//            close();
                return resp;
            } catch (Exception e1) {
                e1.printStackTrace();
                return null;
            }
        }

    }

    /**
     * 组装pinpad报文：STX(0x02) + (2字节长度，长度包含pinpad命令数据长度+3) + 0xC0 + 0x01 + 0x01 +
     * pinpad命令数据data + lrc + ETX(0x03)
     *
     * @param data
     * @return
     */
    private byte[] makeup(byte[] data) {
        byte[] pack = new byte[data.length + 3 + 1 + 2 + 1 + 1];
        pack[0] = STX;

        System.arraycopy(intToB2(data.length + 3), 0, pack, 1, 2);

        System.arraycopy(new byte[]{(byte) 0xC0, 0x01, 0x01}, 0, pack, 3, 3);

        System.arraycopy(data, 0, pack, 6, data.length);

        byte[] lrcData = new byte[data.length + 3];
        System.arraycopy(new byte[]{(byte) 0xC0, 0x01, 0x01}, 0, lrcData, 0, 3);
        System.arraycopy(data, 0, lrcData, 3, data.length);
        byte lrc = calcLRC(lrcData, 0, lrcData.length - 1);
        devicelogger.debug("lrc data：" + (lrcData == null ? "null" : InnerUtils.hexString(lrcData)));
        pack[data.length + 6] = lrc;

        pack[data.length + 7] = ETX;
        return pack;
    }


    /**
     * 组装海外版SP100报文：STX(0x02) + (2字节长度) + Message Type + Separator + Message
     * Data +ETX+LRC
     * *
     *
     * @return
     */
    private byte[] makeupOverseas(byte[] messageType, byte[] body) {

        int offset = 0;

        byte[] payload = new byte[LEN_STX + LEN_LENGTH + LEN_MESSAGETYPE + LEN_SEPARATOR + (body == null ? 0 : body.length) + LEN_ETX + LEN_LRC];

        devicelogger.debug("start make request payload...");
        devicelogger.debug("pack up stx[" + Dump.getHexDump(STX_OVERSEAS) + "]");
        System.arraycopy(STX_OVERSEAS, 0, payload, 0, LEN_STX);
        offset += LEN_STX;

        if (body != null) {
            int len = LEN_MESSAGETYPE + LEN_SEPARATOR + body.length;
            byte[] lenbs = InnerUtils.intToBCD(len, LEN_LENGTH * 2, true);
            System.arraycopy(lenbs, 0, payload, offset, LEN_LENGTH);
            devicelogger.debug("pack up len[" + Dump.getHexDump(lenbs) + "]");
            offset += LEN_LENGTH;
        } else {
            int len = LEN_MESSAGETYPE + LEN_SEPARATOR;
            byte[] lenbs = InnerUtils.intToBCD(len, LEN_LENGTH * 2, true);
            System.arraycopy(lenbs, 0, payload, offset, LEN_LENGTH);
            devicelogger.debug("pack up len[" + Dump.getHexDump(lenbs) + "]");
            offset += LEN_LENGTH;
        }

        devicelogger.debug("pack up cmd[" + Dump.getHexDump(messageType) + "]");
        System.arraycopy(messageType, 0, payload, offset, LEN_MESSAGETYPE);
        offset += LEN_MESSAGETYPE;

        devicelogger.debug("pack up signedSymbol[" + Dump.getHexDump(SEPARATOR_SLASH) + "]");
        System.arraycopy(SEPARATOR_SLASH, 0, payload, offset, LEN_SEPARATOR);
        offset += LEN_SEPARATOR;

        if (body != null) {
            devicelogger.debug("pack up body[" + Dump.getHexDump(body) + "]");
            System.arraycopy(body, 0, payload, offset, body.length);
            offset += body.length;
        }

        devicelogger.debug("pack up ETX[" + Dump.getHexDump(ETX_OVERSEAS) + "]");
        System.arraycopy(ETX_OVERSEAS, 0, payload, offset, LEN_ETX);
        offset += LEN_ETX;

        byte[] lrcData = new byte[payload.length - LEN_STX - LEN_LRC];
        System.arraycopy(payload, LEN_STX, lrcData, 0, lrcData.length);
        devicelogger.debug("pack up lrcData[" + Dump.getHexDump(lrcData) + "]");

        byte[] lrc = caculateLRC(lrcData);
        devicelogger.debug("pack up lrc[" + Dump.getHexDump(lrc) + "]");
        System.arraycopy(lrc, 0, payload, offset, LEN_LRC);

        devicelogger.debug("make payload finish...[" + Dump.getHexDump(payload) + "],total len:" + payload.length);
        return payload;
    }

    /**
     * 国内版 pinpad发送完命令后，需要再发送
     *
     * @param data 获取命令执行结果 的命令 0xC0 + 0x02 + 2字节超时时间 + 2字节的要获取的数据长度
     * @return 判断最后一个字节，0xAA 代表执行成功，0x55代表执行失败
     */
    public byte[] getPinpadRspCode(byte[] data, int timeOut) {
        synchronized (SimpleDeviceManager.externalLock){
            if(mMposComm.getMposConnect()){
                devicelogger.debug("[getPinpadRspCode] Mpos data=" + (data == null ? "null" : ISOUtils.hexString(data)) + " timeOut=" + timeOut);
                byte[] pack = new byte[data.length + 1 + 2 + 1 + 1];
                pack[0] = STX;
                System.arraycopy(intToB2(data.length), 0, pack, 1, 2);
                System.arraycopy(data, 0, pack, 3, data.length);
                byte lrc = calcLRC(data, 0, data.length - 1);
                pack[data.length + 3] = lrc;
                pack[data.length + 4] = ETX;

                byte[] respAllData = mMposComm.getPinpadRspCode(pack,timeOut);

                if(respAllData != null && respAllData.length > 3){
                    byte[] respData = new byte[respAllData.length-3];
                    System.arraycopy(respAllData,3,respData,0,respData.length);
                    return respData;
                }
                return respAllData;
            }
            devicelogger.debug("[getPinpadRspCode] data=" + (data == null ? "null" : ISOUtils.hexString(data)) + " timeOut=" + timeOut);
            try {
//            open(portType, baudRate, null);

                int result = 0;
                byte[] pack = new byte[data.length + 1 + 2 + 1 + 1];
                pack[0] = STX;

                System.arraycopy(intToB2(data.length), 0, pack, 1, 2);

                System.arraycopy(data, 0, pack, 3, data.length);

                byte lrc = calcLRC(data, 0, data.length - 1);
                devicelogger.debug("lrc data：" + (data == null ? "null" : InnerUtils.hexString(data)));
                pack[data.length + 3] = lrc;
                pack[data.length + 4] = ETX;

                devicelogger.debug("send:" + InnerUtils.hexString(pack));

                int ret = write(pack, pack.length, 0);

                if (Arrays.equals(data, new byte[]{0x1B, 0x5A, 0x0D, 0x0A})) { //国内版如果是撤销指令 直接取消读取数据，以防冲突
                    devicelogger.debug("cancel command.");
                    return null;
                }

                if (Arrays.equals(data, new byte[]{0x32, 0x25, (byte) 0x00, (byte) 0x00})) { //国内版如果是下电指令 直接取消读取数据，以防冲突
                    devicelogger.info("---poweroff command.---");
                    return null;
                }

                ByteArrayOutputStream bos = new ByteArrayOutputStream();

                int count = 0;
                boolean isSupportMs = isSupportMs();
                boolean isNdkUartPort = isNdkUartPort();
                int interval = 1000, sleepTimeMs = 100;
                if (isSupportMs) {
                    interval = 100;
                }
                int allCount0 = timeOut / interval;
                int allCount = allCount0;
                if (isNdkUartPort) {
                    int sleepCount = allCount0 * sleepTimeMs / interval;
                    if (interval != sleepTimeMs) {
                        allCount = allCount0 - sleepCount;
                    } else {
                        allCount = allCount / 2 + allCount % 2;
                    }
                    devicelogger.debug("[getPinpadRspCode] allCount0=" + allCount0 + " sleepCount=" + sleepCount);
                }
                devicelogger.debug("[getPinpadRspCode] srcTime=" + timeOut + " allCount=" + allCount + " isSupportMs=" + isSupportMs + " isNdkUartPort=" + isNdkUartPort);
                devicelogger.debug("[getPinpadRspCode] SendLen=" + pack.length + " writeLen=" + ret + " Send data=" + (pack == null ? "null" : InnerUtils.hexString(pack)));
                devicelogger.error("[getPinpadRspCode] Begin to receive.");

                while (true) {
                    byte[] tmp = new byte[1];
                    result = read(tmp, 1, interval);
                    count++;
                    if (count > allCount) {
                        devicelogger.debug("read timeout" + timeOut);
//                    close();
                        throw new ProcessTimeoutException("read timeout");
                    }
                    if (result < 0 || result != 1) {
                        if (isNdkUartPort) {
                            Thread.sleep(sleepTimeMs);// 优化内外置一起寻卡，内置会比较慢的问题
                        }
                        continue;
                    }

                    if (tmp[0] == STX) {
                        try {
                            bos.write(tmp);
                        } catch (IOException e) {
                            devicelogger.error("bos write excetion", e);
//                        close();
                            return null;
                        }
                        break;
                    }
                }
                //
                byte[] lenB = new byte[2];
                result = read(lenB, 2, timeOut);
                if (result != 2) {
//                close();
                    return null;
                }
                try {
                    bos.write(lenB);
                } catch (IOException e) {
                    devicelogger.error("bos write excetion", e);
//                close();
                    return null;
                }
                // 从Command ID到ETX的长度
                int len = ((0xFF & lenB[0]) << 8) + (lenB[1] & 0xFF);
                for (int i = 0; i < len + 1; i += 4096) {
                    int needLen = 0;
                    if (len + 1 - i >= 4096) {
                        needLen = 4096;
                    } else {
                        needLen = len + 1 - i;
                    }
                    byte[] tmp = new byte[needLen];
                    result = read(tmp, needLen, timeOut);
                    if (result != needLen) {
                        // error
//                    close();
                        return null;
                    }
                    try {
                        bos.write(tmp);
                    } catch (IOException e) {
                        devicelogger.error("bos write excetion", e);
//                    close();
                        return null;
                    }
                }
                byte[] resp = bos.toByteArray();
                if (resp == null) {
//                close();
                    return null;
                }
                devicelogger.debug("[getPinpadRspCode] Receive data=" + (resp == null ? "null" : InnerUtils.hexString(resp)) + " len=" + resp.length);

                // unpack data
                int position = 0;
                for (position = 0; position < resp.length; position++) {
                    if (resp[position] == STX) {
                        break;
                    }
                }
                if (position + 2 + 1 > resp.length) {
//                close();
                    return null;
                }
                len = ((0xFF & resp[position + 1]) << 8) + (resp[position + 2] & 0xFF);
                if (position + 2 + len + 1 + 1 > resp.length) {
//                close();
                    return null;
                }
                byte[] tmp = new byte[len];
                System.arraycopy(resp, position + 2 + 1, tmp, 0, len);
                resp = tmp;

//            close();
                devicelogger.debug("[getPinpadRspCode] end!!!");
                return resp;
            } catch (Exception e) {
                e.printStackTrace();
                if (e.fillInStackTrace() instanceof ProcessTimeoutException) {
                    throw new ProcessTimeoutException("read timeout");
                }
                return null;
            }
        }
    }


    //海外版sp100 需要收到0x06响应后才不会一直发送响应数据
    public void getPinpadRspCode() {
        synchronized (SimpleDeviceManager.externalLock) {

            if(mMposComm.getMposConnect()){
                devicelogger.debug("[getPinpadRspCode] Mpos");
                mMposComm.getPinpadRspCodeW(new byte[]{0x06},0);
                return;
            }

//        open(portType, baudRate, null);
            write(new byte[]{0x06}, 1, 0);
//        close();
            devicelogger.debug("Write ACK");
        }
    }


    /**
     * 将int转成2字节的bcd
     *
     * @param data
     * @return
     */
    public static final byte[] intToB2(int data) {
        byte[] p = new byte[2];
        p[0] = (byte) ((data >> 8) & 0xFF);
        p[1] = (byte) ((data) & 0xFF);
        return p;
    }

    /**
     * 计算lrc，
     *
     * @param data
     * @param start
     * @param end
     * @return
     */
    private static final byte calcLRC(byte[] data, int start, int end) {
        byte lrc = data[start];
        for (int i = start + 1; i <= end; i++) {
            lrc = (byte) ((lrc ^ data[i]) & 0xFF);
        }
        return lrc;
    }


    private byte[] caculateLRC(byte[] payload) {
        int offset = 0;
        byte lrc = payload[0];
        do {
            offset++;
            lrc ^= payload[offset];
        } while (offset < payload.length - 1);

        return new byte[]{lrc};
    }

    /**
     * 获取转换数据 格式转换规则：奇数字节 - 41H 作为一个字节的高位，偶数直接- 41H作为一个字节的低位
     *
     * @param dataSource
     * @param dataLen
     * @return
     */
    protected byte[] getData(byte[] dataSource, int dataLen) {
        if (dataLen > 256) {
            devicelogger.error("the length of data more than 256");
            return null;
        }
        byte pin;
        byte[] rsltData = new byte[dataLen / 2];
        for (int i = 0, j = 0; i < dataLen; i++) {
            pin = (byte) (dataSource[i] - 0x41);
            if (i % 2 == 0)/* 高位 */ {
                rsltData[j] = (byte) (pin << 4);
            } else/* 低位 */ {
                rsltData[j] |= pin & 0x0f;
                j++;
            }
        }
        return rsltData;
    }

    /**
     * 转换数据 格式转换规则: 字节的高四位 + 41H 为一个新的字节, 字节的低四位 + 41H 为一个新的字节.
     *
     * @param dataSource 需要转换的数据
     * @return
     */
    protected byte[] setData(byte[] dataSource, int dataLen) {
        byte[] outData = new byte[dataLen * 2];
        for (int i = 0, j = 0; i < dataLen; i++) {
            outData[j++] = (byte) (((dataSource[i] & 0xf0) >> 4) + 0x41);
            outData[j++] = (byte) ((dataSource[i] & 0x0f) + 0x41);
        }
        return outData;
    }


    /**
     * 字节补位
     *
     * @param sourceBytes 需要补充的byte数组
     * @return byte[] 补充完毕的byte数组
     */
    protected byte[] fillBytes(byte[] sourceBytes) {
        if (sourceBytes == null) {
            return null;
        }
        try {
            int mod = sourceBytes.length % 8;
            if (mod != 0) {
                byte[] sourceFilledBytes = new byte[sourceBytes.length + (8 - mod)];
                System.arraycopy(sourceBytes, 0, sourceFilledBytes, 0, sourceBytes.length);
                for (int i = 0; i < (8 - mod); i++) {
                    sourceFilledBytes[sourceBytes.length + i] = InnerUtils.hex2byte("00")[0];
                }
                return sourceFilledBytes;

            } else {
                return sourceBytes;
            }
        } catch (Exception e) {
            e.printStackTrace();
            devicelogger.error("fillBytes error" + e);
        }
        return null;
    }

    /**
     * 字节补位
     *
     * @param sourceBytes 需要补充的byte数组
     * @return byte[] 补充完毕的byte数组
     */
    protected byte[] fillBytesSM4(byte[] sourceBytes) {
        if (sourceBytes == null) {
            return null;
        }
        try {
            int mod = sourceBytes.length % 16;

            if (mod != 0) {
                byte[] sourceFilledBytes = new byte[sourceBytes.length + (16 - mod)];
                System.arraycopy(sourceBytes, 0, sourceFilledBytes, 0, sourceBytes.length);
                for (int i = 0; i < (16 - mod); i++) {
                    sourceFilledBytes[sourceBytes.length + i] = InnerUtils.hex2byte("00")[0];
                }
                return sourceFilledBytes;

            } else {
                return sourceBytes;
            }
        } catch (Exception e) {
            e.printStackTrace();
            devicelogger.error("fillBytesSM4 error" + e);
        }
        return null;
    }

    /**
     * 异或运算
     *
     * @param hexSource1 操作数1
     * @param hexSource2 操作数2
     * @return byte[] 异或结果(16进制数字符串)
     */
    protected byte[] xor(byte[] hexSource1, byte[] hexSource2) {
        if (hexSource1 == null || hexSource1.length < 1 || hexSource2 == null || hexSource2.length < 1) {
            return null;
        }
        try {
            int length = hexSource1.length;
            byte[] xor = new byte[length];
            for (int i = 0; i < length; i++) {
                xor[i] = (byte) (hexSource1[i] ^ hexSource2[i]);
            }
            return xor;
        } catch (Exception e) {
            e.printStackTrace();
            devicelogger.error("xor error:" + e);
        }
        return null;
    }

    public String getVersion() {
        try {
            if (model == PinpadModel.SP) {

                if (sendPinpadCmd(null, new byte[]{0x31, 0x10}, PinpadPackage.EXTCMD_TIMEOUT_MS, true) != null) {
                    byte[] rspClearCode = getPinpadRspCode(new byte[]{0x31, 0x10}, PinpadPackage.EXTCMD_TIMEOUT_MS);
                    if (Arrays.equals(new byte[]{0x00, 0x00}, new byte[]{rspClearCode[0], rspClearCode[1]})) {
                        byte[] version = new byte[rspClearCode.length - 2];
                        System.arraycopy(rspClearCode, 2, version, 0, version.length);

                        devicelogger.debug("--------------getVersion:" + (version == null ? "null" : new String(version)));
                        devicelogger.debug("-------------getVersion:" + (version == null ? "null" : InnerUtils.hexString(version)));
                        return (version == null ? "null" : new String(version));
                    }

                }

            } else if (model == PinpadModel.SP_OVERSEAS) {
                byte[] messageType = new byte[]{0x32, 0x30};
                byte[] resp = sendPinpadCmd(messageType, null, PinpadPackage.EXTCMD_TIMEOUT_MS, true);
                devicelogger.debug("-------------getVersion result:" + (resp == null ? "null" : new String(resp)));
                devicelogger.debug("-------------getVersion result:" + (resp == null ? "null" : InnerUtils.hexString(resp)));

                if (resp == null || resp[0] == NAK) {
                    return null;
                } else {
                    getPinpadRspCode();
                    if (Arrays.equals(new byte[]{0x32, 0x31}, new byte[]{resp[0], resp[1]})) {
                        if (Arrays.equals(new byte[]{0x30, 0x30}, new byte[]{resp[3], resp[4]})) { // 32312F3030 0B 5630312E30302E303100
                            byte[] len = new byte[1];
                            System.arraycopy(resp, 5, len, 0, 1);
                            devicelogger.debug("-----len=" + (len == null ? null : InnerUtils.hexString(len)));
//                            int length = InnerUtils.bcdToInt(len, 0, 1, true);
                            int length = Integer.parseInt(String.valueOf(len[0]), 10);
                            devicelogger.debug("-----length=" + length);
                            byte[] data = new byte[length];
                            System.arraycopy(resp, 6, data, 0, length);
                            devicelogger.debug("-----data=" + (data == null ? null : InnerUtils.hexString(data)));
                            return (data == null ? null : new String(data));
                        } else {
                            devicelogger.error("resopnd code error" + InnerUtils.hexString(new byte[]{resp[4], resp[5]}));
                        }
                    } else {
                        devicelogger.error("message type error");
                    }
                }
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    public String getDeviceInfo() {
        try {
            if (model == PinpadModel.SP) {
                devicelogger.error("[getPN] PinpadModel.SP, not supported now.");
                throw new UnsupportedOperationException("[getPN]Don't support this method");

            } else if (model == PinpadModel.SP_OVERSEAS) {
                byte[] messageType = new byte[]{0x33, 0x48};
                byte[] resp = sendPinpadCmd(messageType, null, PinpadPackage.EXTCMD_TIMEOUT_MS, true);
                devicelogger.debug("-------------getDeviceInfo result:" + (resp == null ? "null" : new String(resp)));
                devicelogger.debug("-------------getDeviceInfo result:" + (resp == null ? "null" : InnerUtils.hexString(resp)));

                if (resp == null || resp[0] == NAK) {
                    return null;
                } else {
                    getPinpadRspCode();
                    if (Arrays.equals(new byte[]{0x33, 0x49}, new byte[]{resp[0], resp[1]})) {
                        if (Arrays.equals(new byte[]{0x30, 0x30}, new byte[]{resp[3], resp[4]})) { // 32312F3030 0B 5630312E30302E303100
                            byte[] len = new byte[2];
                            System.arraycopy(resp, 5, len, 0, 2);
                            devicelogger.debug("-----len=" + (len == null ? null : InnerUtils.hexString(len)));
                            int length = InnerUtils.bytesToInt(len, 0, 2, true);
                            devicelogger.debug("-----length=" + length);
                            byte[] data = new byte[length];
                            System.arraycopy(resp, 7, data, 0, length);
                            devicelogger.debug("-----data=" + (data == null ? null : InnerUtils.hexString(data)));
                            return (data == null ? null : InnerUtils.hexString(data));
                        } else {
                            devicelogger.error("resopnd code error" + InnerUtils.hexString(new byte[]{resp[4], resp[5]}));
                        }
                    } else {
                        devicelogger.error("message type error");
                    }
                }
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    public byte[] boardTxn(byte[] messageType, byte functionId, byte[] data, int mTimeOut) {
        try {
            devicelogger.debug("[boardTxn] messageType=" + (messageType == null ? "null" : new String(messageType))
                    + " functionID=" + Integer.toHexString(functionId & 0xFF).toUpperCase()
                    + " data=" + (data == null ? "null" : InnerUtils.hexString(data)) + " mTimeOut=" + mTimeOut);
            if(isUseBleBase(params)){
                return bleBasePackage.boardTxn(messageType,functionId,data,mTimeOut);
            }
            int result = 0;
            byte[] pack;
            if (model == PinpadModel.SP_OVERSEAS) {
                if (functionId == 0x00) {
                    pack = boardPackOversea(messageType, data);
                } else {
                    pack = boardPackOversea(messageType, functionId, data);
                }
            } else {
                pack = boardPack(functionId, data);
            }
            if (usbModule != null) {
                boolean clrResult = usbModule.clearBuffer();
                devicelogger.debug("[ExtPinPad USB clearBuffer] result=" + clrResult);
            }
            if (serialOper != null) {//下发新指令前清空缓冲区，避免脏数据
                boolean clrResult = serialOper.clearBuffer(0);
                devicelogger.debug("[ExtPinPad clearBuffer] result=" + clrResult);
            }
            devicelogger.debug("send: " + InnerUtils.hexString(pack));
            result = write(pack, pack.length, 0);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int count = 0;
            boolean isSupportMs = isSupportMs();
            boolean isNdkUartPort = isNdkUartPort();
            int interval = 1000, sleepTimeMs = 100;
            if (isSupportMs) {
                interval = 100;
            }
            int allCount0 = mTimeOut / interval;
            int allCount = allCount0;
            if (isNdkUartPort) {
                int sleepCount = allCount0 * sleepTimeMs / interval;
                if (interval != sleepTimeMs) {
                    allCount = allCount0 - sleepCount;//0-999=0
                } else {
                    allCount = allCount / 2 + allCount % 2;
                }
                devicelogger.debug("[boardTxn] allCount0=" + allCount0 + " sleepCount=" + sleepCount);
            }
            devicelogger.debug("[boardTxn] srcTime=" + mTimeOut + " allCount=" + allCount + " isSupportMs=" + isSupportMs + " isNdkUartPort=" + isNdkUartPort);
            devicelogger.debug("[boardTxn] SendLen=" + pack.length + " writeLen=" + result + " Send data=" + (pack == null ? "null" : InnerUtils.hexString(pack)));
            devicelogger.error("[boardTxn] Begin to receive.");

            while (true) {
                byte[] tmp = new byte[1];
                if (allCount == 0) {  //轮询使用
                    int time = 0;
                    long startTime = System.currentTimeMillis();
                    while (time < ECHO_TEST_COUNT) {
                        result = read(tmp, 1, ECHO_TEST_TIMEOUT_MS);
                        if (result < 0 || result != 1) {
                            time++;
                            Thread.sleep(5);
                        } else {
                            break;
                        }
                    }
                    long endTime = System.currentTimeMillis();
                    devicelogger.debug("Echo count=" + time + " disTimeMs=" + (endTime - startTime));
                } else {
                    result = read(tmp, 1, interval);
                }

                count++;

                if (allCount == 0) { //轮询使用
                    if (result < 0 || result != 1) {
//                        close();
                        return null;
                    }
                } else if (count > allCount) {
                    devicelogger.debug("read timeout");
//                    close();
                    return null;
                }


                if (result < 0 || result != 1) {
                    if (isNdkUartPort) {
                        Thread.sleep(sleepTimeMs);// 优化内外置一起寻卡，内置会比较慢的问题
                    }
                    continue;
                }
                if (onlyACKCommand(messageType) && tmp[0] == ACK) {
                    devicelogger.debug("--onlyACKCommand--" + InnerUtils.hexString(new byte[]{messageType[0], messageType[1]}));
                    devicelogger.debug("--onlyACKCommand---" + tmp[0]);
//                    close();
                    return tmp;
                }
//                if (null != messageType && Arrays.equals(new byte[]{messageType[0], messageType[1]}, new byte[]{0x33, 0x43}) && tmp[0] == ACK) {
//                    devicelogger.debug("----" + InnerUtils.hexString(new byte[]{messageType[0], messageType[1]}));
//                    devicelogger.debug("-----" + tmp[0]);
//                    if (Arrays.equals(new byte[]{messageType[0], messageType[1]}, new byte[]{0x33, 0x43}) && tmp[0] == ACK) { // 海外键盘LCD显示返回ACK
////                        close();
//                        return tmp;
//                    }
//                }
                if (tmp[0] == NAK) { // SP100海外版专用
//                    close();
                    return tmp;
                }
                if (tmp[0] == STX) {
                    try {
                        bos.write(tmp);
                    } catch (IOException e) {
                        devicelogger.error("bos write excetion", e);
//                        close();
                        return null;
                    }
                    break;
                }
            }
            byte[] lenB = new byte[2];
            result = read(lenB, 2, TIME_OUT);
            if (result != 2) {
//                close();
                return null;
            }
            try {
                bos.write(lenB);
            } catch (IOException e) {
                devicelogger.error("bos write excetion", e);
//                close();
                return null;
            }
            // 从Command ID到ETX的长度
            int len;

            if (model == PinpadModel.SP_OVERSEAS) {
                len = InnerUtils.bcdToInt(lenB, 0, 4, true);  //海外版sp100返回的长度是十进制的。
                len = len + 1;//再读一个字节lrc
            } else {
                len = ((0xFF & lenB[0]) << 8) + (lenB[1] & 0xFF);
            }

            for (int i = 0; i < len + 1; i += 4096) {
                int needLen = 0;
                if (len + 1 - i >= 4096) {
                    needLen = 4096;
                } else {
                    needLen = len + 1 - i;
                }
                byte[] tmp = new byte[needLen];
                result = read(tmp, needLen, TIME_OUT);
                if (result != needLen) {
                    // error
//                    close();
                    return null;
                }
                try {
                    bos.write(tmp);
                } catch (IOException e) {
                    devicelogger.error("bos write excetion", e);
//                    close();
                    return null;
                }
            }
            byte[] resp = bos.toByteArray();
            if (resp == null) {
//                close();
                return null;
            }
            devicelogger.debug("[boardTxn] Receive data=" + (resp == null ? "null" : InnerUtils.hexString(resp)) + " len=" + resp.length);
            // unpack data
            int position = 0;
            for (position = 0; position < resp.length; position++) {
                if (resp[position] == STX) {
                    break;
                }
            }
            if (position + 2 + 1 > resp.length) {
//                close();
                return null;
            }

            if (model == PinpadModel.SP_OVERSEAS) {
                //海外版sp100返回的长度是十进制的。
                len = InnerUtils.bcdToInt(new byte[]{resp[position + 1], resp[position + 2]}, 0, 4, true);
            } else {
                len = ((0xFF & resp[position + 1]) << 8) + (resp[position + 2] & 0xFF);
            }
            // position += 2;
            if (position + 2 + len + 1 + 1 > resp.length) {
//                close();
                return null;
            }
            devicelogger.debug("position:" + position + ", len：" + len);
            byte lrc;
            if (model == PinpadModel.SP_OVERSEAS) {
                lrc = calcLRC(resp, 1, position + 3 + len);
                devicelogger.debug("SP_OVERSEAS resp lrc:" + InnerUtils.hexString(new byte[]{resp[position + 2 + len + 2]}));
            } else {
                lrc = calcLRC(resp, position + 1, position + 2 + len);  //校验
                devicelogger.debug(" sp resp lrc:" + InnerUtils.hexString(new byte[]{resp[position + 2 + len + 1]}));
            }

            devicelogger.debug("cal lrc:" + InnerUtils.hexString(new byte[]{lrc}));

            if (model == PinpadModel.SP && lrc != resp[position + 2 + len + 1]) {
                devicelogger.error("SP lrc not equal:" + lrc + " " + resp[position + 2 + len + 1]);
//                close();
                return null;
            } else if (model == PinpadModel.SP_OVERSEAS && lrc != resp[position + 2 + len + 2]) {
                devicelogger.error("SP_OVERSEAS lrc not equal:" + InnerUtils.hexString(new byte[]{lrc}) + ", " + InnerUtils.hexString(new byte[]{resp[position + 2 + len + 2]}));
//                close();
                return null;
            }

            byte[] tmp = new byte[len];
            System.arraycopy(resp, position + 2 + 1, tmp, 0, len);
            resp = tmp;

//            close();
            devicelogger.debug("[boardTxn] end!!!");
            return resp;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 组装海外版SP100报文：STX(0x02) + (2字节长度) + Message Type + Separator + Message Data + ETX+ LRC
     *
     * @return
     */
    private byte[] boardPackOversea(byte[] messageType, byte functionId, byte[] data) {
        byte[] pack = new byte[1 + 2 + 2 + 1 + 1 + (data == null ? 0 : data.length) + 1 + 1];
        pack[0] = STX;
        int len;
        if (data != null) {
            len = 2 + 1 + 1 + data.length;
        } else {
            len = 2 + 1 + 1;
        }
        byte[] lenbs = InnerUtils.intToBCD(len, 2 * 2, true);
        System.arraycopy(lenbs, 0, pack, 1, 2);
        devicelogger.debug("pack len = " + InnerUtils.hexString(lenbs));

        System.arraycopy(messageType, 0, pack, 3, messageType.length);
        pack[5] = 0x2F;
        pack[6] = functionId;
        System.arraycopy(data, 0, pack, 7, data.length);
        pack[data.length + 7] = ETX;
        byte lrc = calcLRC(pack, 1, data.length + 7);
        pack[data.length + 8] = lrc;
        return pack;
    }

    private byte[] boardPackOversea(byte[] messageType, byte[] data) {
        byte[] pack = new byte[1 + 2 + 2 + 1 + (data == null ? 0 : data.length) + 1 + 1];
        pack[0] = STX;
        int len;
        if (data != null) {
            len = 2 + 1 + data.length;
        } else {
            len = 2 + 1;
        }
        byte[] lenbs = InnerUtils.intToBCD(len, 2 * 2, true);
        System.arraycopy(lenbs, 0, pack, 1, 2);
        devicelogger.debug("pack len = " + InnerUtils.hexString(lenbs));

        System.arraycopy(messageType, 0, pack, 3, messageType.length);
        pack[5] = 0x2F;
        System.arraycopy(data, 0, pack, 6, data.length);
        pack[data.length + 6] = ETX;
        byte lrc = calcLRC(pack, 1, data.length + 6);
        pack[data.length + 7] = lrc;
        return pack;
    }

    private byte[] boardPack(byte command, byte[] data) {
        devicelogger.debug("-----boardPack------");
        byte[] pack = new byte[data.length + 1 + 2 + 1 + 1 + 1];
        pack[0] = STX;
        System.arraycopy(intToB2(data.length + 2), 0, pack, 1, 2);
        pack[3] = command;
        System.arraycopy(data, 0, pack, 4, data.length);
        pack[data.length + 4] = ETX;
        byte lrc = calcLRC(pack, 1, data.length + 4);
        pack[data.length + 5] = lrc;
        return pack;
    }

    private int open(PortType portType, Baudrate baudrate, SerialExtParams params) {
        devicelogger.debug("[ExtPinPad open] channel=" + ((serialOper != null) ? "UART" : "USB") + " portType=" + portType + " baudrate=" + baudrate);
        if (usbModule != null) {
            if (!openUsb(2)) {
                return -1;
            }
            usbModule.clearBuffer();
            return 1;
        }
        if (serialOper != null) {
            int ret = serialOper.open(portType, baudrate, params);
            serialOper.clearBuffer(0);
            devicelogger.debug("[ExtPinPad open] result=" + ret);
            return ret;
        }
        devicelogger.error("[ExtPinPad open] channel=" + ((serialOper != null) ? "UART" : "USB") + " fail");
        return -1;
    }

    public int read(byte[] outputData, int lengthMax, int timeOut) {
        devicelogger.debug("[ExtPinPad read] channel=" + ((serialOper != null) ? "UART" : "USB") + " lengthMax=" + lengthMax + " timeOut=" + timeOut);
        if (serialOper != null) {
            int readLen = serialOper.read(outputData, lengthMax, timeOut);
            if (readLen > 0) {
                devicelogger.debug("[ExtPinPad read] outputData=" + (outputData == null ? "null" : InnerUtils.hexString(outputData)) + " readLen=" + readLen);
            }
            return readLen;
        }
        if (usbModule != null) {
            if (timeOut == 0 || timeOut == PinpadPackage.ECHO_TEST_TIMEOUT_MS) {
                timeOut = ECHO_TEST_TIMEOUT_MS * 5;//USB 轮询时，不能传0，传0读不到
            }
            int readLen = usbModule.read(outputData, lengthMax, timeOut);
            if (readLen > 0) {
                devicelogger.debug("[ExtPinPad read] outputData=" + (outputData == null ? "null" : InnerUtils.hexString(outputData)) + " readLen=" + readLen);
            }
            return readLen;
        }
        return -1;
    }

    private int write(byte[] inputData, int lengthMax, int timeOut) {
        devicelogger.debug("[ExtPinPad write] channel=" + ((serialOper != null) ? "UART" : "USB") + " lengthMax=" + lengthMax + " timeOut=" + timeOut);
        int result = -1;
        int retryCount = 1;
        do {
            devicelogger.debug("[write] data:"+(inputData==null?null:ISOUtils.hexString(inputData)));
            devicelogger.debug("[ExtPinPad write] retryCount=" + retryCount);
            if(isUseBleBase(params)){
                if (!BluetoothController.getInstance().isConnectedA()) {
                    devicelogger.error("[write]  bleBase isConnectedA fasle");
                    return -1;
                }
                devicelogger.debug("[ExtPinPad write] singleSend  start ===");
                result = bleBasePackage.write(inputData,lengthMax,timeOut,params.getPortType());
                if(result<0){
                    devicelogger.error("[write] bleBase write failed");
                }
                devicelogger.debug("[write] bleBase write end---------------:");
                return inputData.length;
            }
            if (serialOper != null) {
                result = serialOper.write(inputData, lengthMax, timeOut);
                devicelogger.debug("[ExtPinPad write] channel=UART" + " inputData=" + (inputData == null ? "null" : ISOUtils.hexString(inputData)) + " result=" + result);
            }
            if (result < 0 && usbModule != null) {
                result = usbModule.write(inputData, lengthMax, timeOut);
                devicelogger.debug("[ExtPinPad write] channel=USB" + " inputData=" + (inputData == null ? "null" : ISOUtils.hexString(inputData)) + " result=" + result);
            }
            if (result > 0) {
                break;
            }
            if (result < 0 && retryCount > 0) {
                devicelogger.error("[ExtPinPad write] failed.result:" + result);
                this.initialize = false;
//                boolean isSuccess = this.init(null, isSignPad);
//                if (!isSuccess) {
//                    retryCount = 0;
//                }
            }
        } while (retryCount-- > 0);
        return result;
    }
//    private int write(byte[] inputData, int lengthMax, int timeOut) {
//        devicelogger.debug("[ExtPinPad write] channel=" + ((serialOper != null) ? "UART" : "USB") + " lengthMax=" + lengthMax + " timeOut=" + timeOut);
//
//        if (serialOper != null) {
//            int result = serialOper.write(inputData, lengthMax, timeOut);
//            devicelogger.debug("[ExtPinPad write] channel=UART" + " inputData=" + (inputData == null ? "null" : ISOUtils.hexString(inputData)) + " result=" + result);
//            return result;
//
//        }
//        if (usbModule != null) {
//            int result = usbModule.write(inputData, lengthMax, timeOut);
//            devicelogger.debug("[ExtPinPad write] channel=USB" + " inputData=" + (inputData == null ? "null" : ISOUtils.hexString(inputData)) + " result=" + result);
//            return result;
//        }
//        return -1;
//    }

    public int closePinpad() {
        devicelogger.debug("[ExtPinPad close] channel=" + ((serialOper != null) ? "UART" : "USB"));
        this.initialize = false;
        if(isUseBleBase(params) && BluetoothController.getInstance()!=null){
            try {
                devicelogger.error("-----------释放蓝牙底座-----------");
                if(BluetoothController.getInstance()!=null && BluetoothController.getInstance().isConnectedA()){
//                    boolean isSingleChannelThread = BluetoothController.getInstance().isSingleChannelThread();
//                    if(isSingleChannelThread){
//                        BluetoothController.getInstance().singleCancel();
//                    }
//                    devicelogger.debug("------BluetoothController.getInstance().disconnect()-----");
                    //BluetoothController.getInstance().disconnect();//纯蓝牙底座不能取消
                }
                BluetoothController.getInstance().release(context,bleBasePackage.getOnSearchListener(),false);

            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if(bleBasePackage!=null){
                    bleBasePackage.setOnSearchListener(null);
                }
                BleBasePackage.setBleBasePackage(null);
                context = null;
                pinpadPackage = null;
            }

        }
        if (serialOper != null) {
            return serialOper.close();
        }
        if (usbModule != null) {
            return usbModule.close();
        }
        return -1;
    }


    public PinpadModel getModel() {
        if(SimpleDeviceManager.getInstance().getMposParams()!=null){
            return PinpadModel.SP_OVERSEAS;
        }
        if(isUseBleBase(params)){
            return bleBasePackage.getModel();
        }
        return model;
    }

    private String hexString(byte[] data){
        return (data==null?"null":ISOUtils.hexString(data));
    }

    private boolean isUseBleBase(PinpadInitExtParams params){
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

    private boolean isLockBase() {
        String result = "";
        ProcessBuilder cmd;
        try {
            String[] args = {"/system/bin/cat", "sys/class/otg_ctrl/base_type"};
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[24];
            while (in.read(re) != -1) {
                result = result + new String(re);
            }
            in.close();
            devicelogger.info("isLockBase=" + ("0".equals(result.trim())));
            return "0".equals(result.trim());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "0".equals(result.trim());
    }
}
