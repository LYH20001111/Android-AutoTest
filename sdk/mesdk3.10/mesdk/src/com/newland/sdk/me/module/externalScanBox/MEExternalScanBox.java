package com.newland.sdk.me.module.externalScanBox;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.newland.NlBluetooth.control.BluetoothController;
import com.newland.sdk.me.module.externalPininput.BleBasePackage;
import com.newland.sdk.me.module.externalPininput.BleBsaeDataRevListener;
import com.newland.sdk.me.module.serialport.MESerial;
import com.newland.sdk.me.module.usb.MEUSB;
import com.newland.sdk.me.module.usb.USBSafeBuffer;
import com.newland.sdk.module.externalPin.BleBaseParams;
import com.newland.sdk.module.externalPin.PinpadInitExtParams;
import com.newland.sdk.module.externalScan.ScanBoxDevParams;
import com.newland.sdk.module.externalScan.ScanBoxInitExtParams;
import com.newland.sdk.module.externalScan.ScanBoxLight;
import com.newland.sdk.module.serialport.CheckBit;
import com.newland.sdk.module.serialport.DataBit;
import com.newland.sdk.module.serialport.SerialPortModule;
import com.newland.sdk.module.serialport.StopBit;
import com.newland.sdk.module.usb.SelectUsbDeviceListener;
import com.newland.sdk.module.usb.USBModule;
import com.newland.sdk.mtype.ExModuleType;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.mtype.ProcessTimeoutException;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.module.externalScan.ExtScanBoxModule;
import com.newland.sdk.module.externalScan.ResultListener;
import com.newland.sdk.module.externalScan.ScanBoxParams;
import com.newland.sdk.module.externalScan.StartScanExtParams;
import com.newland.sdk.module.serialport.Baudrate;
import com.newland.sdk.module.serialport.PortType;
import com.newland.sdk.module.serialport.SerialExtParams;
import com.newland.sdk.utils.ISOUtils;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.mtypex.AbstractModule;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MEExternalScanBox extends AbstractModule implements ExtScanBoxModule {

    private DeviceLogger devicelogger = DeviceLoggerFactory.getLogger("MEExternalScanBox");
    private SerialPortModule serialOper;
    private USBModule usbModule;
    private static int serialNum = -1;
    private final int CMD_TIMEOUT = 3000;//ms
    private final String FIELD_PREFIX = "<", FIELD_SUFFIX = ">", STX = "STX", LEN = "LEN", CMD = "CMD", TAGNUM =
            "TAGNUM";
    private final String SERIAL = "SERIAL", DATA = "DATA", EXT = "ETX", LRC = "LRC", ACKCODE = "ACKCODE", SET = "SET"
            , GET = "GET", TAG_DATA = "DATA";
    private final int ERROR = 1, SUCCESS = 2, TIMEOUT = 3,CANCEL = 4;
    private final String ACK_OK = "00";
    private final String GBK = "GBK";
    private List<String> setTags = new ArrayList<String>();
    private boolean isScanning = false;
    private Thread processCodeThread = null;
    private HashMap<String, String> ackDataItems = new HashMap<String, String>();
    private int SCAN_ERROR_CODE = -1;

    //private String bitString = "8N1NN";// 数据位+校验位+停止位+红外通讯防止反射串扰+是否开启读写阻塞
    private ScanBoxInitExtParams.CommMode commMode;
    private PortType portType;
    private Baudrate baudRate;
    private SerialExtParams serialExtParams;
    private BleBasePackage bleBasePackage;
    private Context context;
    private volatile ScanBoxInitExtParams params;
    private volatile boolean isBleUSBOpened = false;
    private boolean isCompatibleVersion = false;//是否是兼容版盒子


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
        return ExModuleType.SCANNER;
    }

    public MEExternalScanBox(AbstractDevice device, Context context) {
        super(device);
        this.context = context;
        serialOper = new MESerial(device, context);
        usbModule = MEUSB.getInstance(context);
        ((MEUSB) usbModule).setWorkingSyncMode(true);
    }

    @Override
    public boolean init(@Nullable ScanBoxInitExtParams params) {
        devicelogger.debug("[init] params=" + params);
        this.params = params;
        if (params != null) {
            if (params.getPortType() == PortType.BLEBASE_USB2) {
                try {
                    bleBasePackage = BleBasePackage.getInstance();
                    PinpadInitExtParams pinpadInitExtParams = new PinpadInitExtParams(params.getPortType(), params.getBleName(),
                            params.getBleAddress(),null);
                    pinpadInitExtParams.setBleBaseParams(params.getBleBaseParams());
                    commMode = ScanBoxInitExtParams.CommMode.BLE_USB2;
                    return bleBasePackage.init(context, pinpadInitExtParams, false);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                } catch (Error r) {
                    r.printStackTrace();
                    return false;
                }
            }
            if (params.getPortType() == PortType.BLEBASE_USB1) {
                try {
                    bleBasePackage = BleBasePackage.getInstance();
                    PinpadInitExtParams pinpadInitExtParams = new PinpadInitExtParams(PortType.BLEBASE_RS232, params.getBleName(),
                            params.getBleAddress(),null);
                    pinpadInitExtParams.setBleBaseParams(params.getBleBaseParams());
                    commMode = ScanBoxInitExtParams.CommMode.BLE_USB1;
                    boolean initRslt = bleBasePackage.init(context, pinpadInitExtParams, false);
                    return initRslt;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                } catch (Error r) {
                    r.printStackTrace();
                    return false;
                }

            }
            if (params.getCommMode() == ScanBoxInitExtParams.CommMode.UART) {
                commMode = ScanBoxInitExtParams.CommMode.UART;
                this.portType = params.getPortType();
                this.baudRate = params.getBaudrate();
                serialExtParams = new SerialExtParams(DataBit.DATA_BIT_8, CheckBit.NO_CHECK, StopBit.STOP_BIT_ONE);
                return true;
            } else if (params.getCommMode() == ScanBoxInitExtParams.CommMode.USB) {
                commMode = ScanBoxInitExtParams.CommMode.USB;
                return initUSBDevice();
            } else {
                return false;
            }
        } else {
            if (initUSBDevice()) {
                commMode = ScanBoxInitExtParams.CommMode.USB;
                return true;
            }
            commMode = ScanBoxInitExtParams.CommMode.UART;
            this.portType = PortType.RS232;
            this.baudRate = Baudrate.BPS9600;
            serialExtParams = new SerialExtParams(DataBit.DATA_BIT_8, CheckBit.NO_CHECK, StopBit.STOP_BIT_ONE);
            return true;
        }
    }

    private boolean initUSBDevice() {
        int result = usbModule.open(new SelectUsbDeviceListener() {
            @Override
            public UsbDevice onSelect(HashMap<String, UsbDevice> usbDeviceList) {
                Iterator<UsbDevice> iterator = usbDeviceList.values().iterator();
                while (iterator.hasNext()) {
                    UsbDevice device = iterator.next();
                    String productName = device.getProductName();
                    String manufacturerName = device.getManufacturerName();
                    devicelogger.debug("[initUSBDevice] ScanBox initUSBDevice: getDeviceName=" + device.getDeviceName() + " getManufacturerName="
                            + device.getManufacturerName() + " getProductName=" + device.getProductName() +
                            " getSerialNumber=" + device.getSerialNumber() + " " +
                            " getDeviceId=" + device.getDeviceId() + " getVendorId=" + device.getVendorId() + " " +
                            "getProductId=" + device.getProductId()
                            + " getDeviceClass=" + device.getDeviceClass() + " getDeviceSubclass=" + device.getDeviceSubclass() + " " +
                            " getDeviceProtocol=" + device.getDeviceProtocol() + " getConfigurationCount=" + device.getConfigurationCount()
                            + " getInterfaceCount=" + device.getInterfaceCount());
                    if ((productName != null && productName.contains("Newland")) &&
                            (manufacturerName != null && manufacturerName.contains("Newland"))
                        /*&& (device.getVendorId() == ?) && (device.getProductId() ==?)*/  /*根据实际设备确定VendorId
                        ProductId*/) {
                        devicelogger.debug("[initUSBDevice] ScanBox open success.");
                        return device;
                    }
                }
                devicelogger.error("[initUSBDevice] ScanBox open fail.");
                return null;
            }
        });
        devicelogger.info("[initUSBDevice] result=" + result);
        if (result < 0)
            return false;
        return true;
    }

    @Override
    public boolean setParams(@NonNull ScanBoxDevParams param, @NonNull String value) {
        try {
            devicelogger.debug("[setParams] param:" + param + "; value:" + value);
            String target;
            byte[] bytes = value.getBytes("GBK");
            if (param == ScanBoxDevParams.SN) {
                if (bytes.length != 12) {
                    devicelogger.error("SN value length must be 12");
                    return false;
                }
                target = "SN=" + value;
            } else if (param == ScanBoxDevParams.PN) {
                if (bytes.length != 15) {
                    devicelogger.error("PN value length must be 15");
                    return false;
                }
                target = "PN=" + value;
            } else if (param == ScanBoxDevParams.CSN) {
                if (bytes.length <= 0 || value.length() > 24) {
                    devicelogger.error("CSN value length can't <= 0 or >24");
                    return false;
                }
                String len = String.valueOf(value.length());
                if (len.length() == 1) {
                    len = "000" + len;
                } else if (len.length() == 2) {
                    len = "00" + len;
                }
                target = "CSN=" + len + value;
            } else {
                return false;
            }

            return startSendCmd(SET, new String[]{target}, CMD_TIMEOUT);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public Map<String, String> getParams(@NonNull ScanBoxDevParams[] param) {
        String[] tags = new String[param.length];
        for (int i = 0; i < param.length; i++) {
            if (ScanBoxDevParams.SN == param[i]) {
                tags[i] = "SN";
            } else if (ScanBoxDevParams.PN == param[i]) {
                tags[i] = "PN";
            } else if (ScanBoxDevParams.CSN == param[i]) {
                tags[i] = "CSN";
            } else if (ScanBoxDevParams.PID == param[i]) {
                tags[i] = "PID";
            } else if (ScanBoxDevParams.VID == param[i]) {
                tags[i] = "VID";
            } else if (ScanBoxDevParams.APP == param[i]) {
                tags[i] = "APP";
            } else if (ScanBoxDevParams.MASTER == param[i]) {
                tags[i] = "MASTER";
            } else if (ScanBoxDevParams.BOOT == param[i]) {
                tags[i] = "BOOT";
            }
        }
        boolean result = startSendCmd(GET, tags, CMD_TIMEOUT);
        if (result) {
            return ackDataItems;
        } else {
            return null;
        }
    }

    @Override
    public boolean setScanParams(@NonNull ScanBoxParams params) {
        try {
            devicelogger.debug("[setScanParams] params:" + params);
            setTags = new ArrayList<String>();
            if(null!=params.isModeOnce()){
                if(params.isModeOnce())
                    setTags.add("MODE=ONCE");
                else
                    setTags.add("MODE=CONTINUE");
            }

            if(null!=params.isBackLight()){
                if (params.isBackLight()) {
                    setTags.add("LED=ON");
                } else {
                    setTags.add("LED=OFF");
                }
            }

//            if(null!=params.isCdcMode()){
//                if (params.isCdcMode()) {
//                    setTags.add("OUTPORT=USB");
//                } else {
//                    setTags.add("OUTPORT=HID");
//                }
//            }

//            setTags.add(setBackLight(params.isBackLight()));
            String scanLight = setScanLight(params.getScanLightStatus());
            if (!TextUtils.isEmpty(scanLight)) {
                setTags.add(scanLight);
            }

            if(null!=params.getVolume())
                setTags.add(setVolume(params.getVolume()));

            String prefix = setPrefix(params.getPrefix());
            if (!TextUtils.isEmpty(prefix)) {
                setTags.add(prefix);
            }
//            else {
//                return false;
//            }

            String suffix = setSuffix(params.getSuffix());
            if (!TextUtils.isEmpty(suffix)) {
                setTags.add(suffix);
            }
//            else {
//                return false;
//            }
            String successVoice = params.getSuccessVoicePrompt();
            if (successVoice != null && !successVoice.equals("")) {
                String temp = setSuccessVoicePrompt(successVoice);
                if (!TextUtils.isEmpty(temp)) {
                    setTags.add(temp);
                }
//                else {
//                    return false;
//                }
            }

            if(null!=params.isEnter()){
                if (params.isEnter()) {
                    setTags.add("ENTER=ON");
                } else {
                    setTags.add("ENTER=OFF");
                }
            }

//            setTags.add(setEnter(params.isEnter()));

            devicelogger.debug(Arrays.toString(setTags.toArray(new String[setTags.size()])));

            int size = 0;
            String[] tagTemp = new String[setTags.size()];
            for (int i = 0; i < setTags.size(); i++) {
                tagTemp[i] = setTags.get(i);
                size += tagTemp[i].getBytes(GBK).length;
            }

            if (size > 9000) {
                devicelogger.error("[setScanParams] sendParam data too large, size = " + size);
                return false;
            }
            return startSendCmd(SET, tagTemp, CMD_TIMEOUT);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean startScan(final String amount, final int timeOut, final ResultListener listener,
                             final @Nullable StartScanExtParams params) {
        try {
            devicelogger.debug("---------[startScan] isScanning:" + isScanning + ";timeOut:" + timeOut);
            if (isScanning) {
                devicelogger.error("[startScan] Scanning");
                return false;
            }
            cancel = false;
            List<String> list = new ArrayList<String>();
//            if (params!=null && params.isOnce()) {
//                list.add("MODE=ONCE");
//            } else {
//                list.add("MODE=CONTINUE");
//            }

            list.add("SWITCH=ON");
            list.add("PACK=ON");

            if (params != null) {
                String successVoice = params.getScanVoicePrompt();
                if (successVoice != null && !successVoice.equals("")) {
                    String temp = setScanVoicePrompt(successVoice);
                    if (temp != null) {
                        list.add(temp);
                    }
                }
            }

            long interval = 1500;
            if (params != null) {
                interval = params.getInterval();
                if (interval > 9999) {
                    interval = 9999;
                }
                if (interval <= 0) {
                    interval = 0;
                }
            }
            list.add("INTERVAL=" + interval);
            String amt = setAmount(amount);
            if (amt == null) {
                devicelogger.error("[startScan] Amount is null");
                return false;
            } else {
                list.add(amt);
            }

            String[] tags = list.toArray(new String[list.size()]);
            boolean ackCode = startSendCmd(SET, tags, CMD_TIMEOUT);
            if (ackCode) {
                isScanning = true;
                processCodeThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean isOnce = true;
                        if (params != null) {
                            isOnce = params.isOnce();
                        }
                        int code = processCode(timeOut, listener, isOnce);
                        devicelogger.debug("[startScan] 结果码 Scan response code: " + code);
                        if (code == ERROR && listener != null) {
                            devicelogger.error("---------扫码失败stopScan----");
                            if (commMode != ScanBoxInitExtParams.CommMode.BLE_USB2) {
                                stopScan();
                            }
                            listener.onError(SCAN_ERROR_CODE, "General error");
                        }else if(code == CANCEL){
                            devicelogger.error("---------取消扫码stopScan----");
//                            if (commMode != ScanBoxInitExtParams.CommMode.BLE_USB2) {
//                                stopScan();
//                            }
                            listener.onError(SCAN_ERROR_CODE, "cancel");
                        } else if (code == TIMEOUT) {
                            devicelogger.error("---------扫码失败TIMEOUT----");
                            if (commMode != ScanBoxInitExtParams.CommMode.BLE_USB2) {
                                stopScan();
                            }
                            listener.onTimeOut();
                        } else if (code == SUCCESS) {
                            devicelogger.debug("isOnce:" + isOnce + ";commMode:" + commMode);
                            if (isOnce && commMode != ScanBoxInitExtParams.CommMode.BLE_USB2 && commMode != ScanBoxInitExtParams.CommMode.BLE_USB1) {//蓝牙底座，扫完码，不关闭，不然第一次完全结束到第二次扫码会比较慢
                                devicelogger.debug("---------扫码成功stopScan-------");
//                                stopScan();
                            }
                        }
                        turnOffAmountDisplay(params);
                    }
                });
                processCodeThread.start();

                return true;
            } else {
                isScanning = false;
                if (listener != null) {
                    stopScan();
                    listener.onError(SCAN_ERROR_CODE, "General error");
                }

                turnOffAmountDisplay(params);
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();

            turnOffAmountDisplay(params);
            return false;
        }
    }

    private boolean cancel = false;
    @Override
    public boolean stopScan() {
        try {
            devicelogger.debug("-------[stopScan]---------isScanning:" + isScanning + ";commMode:" + commMode);
            List<String> list = new ArrayList<String>();
            list.add("SWITCH=OFF");
            String[] tags = list.toArray(new String[list.size()]);
            if (commMode == ScanBoxInitExtParams.CommMode.BLE_USB2 && bleBasePackage != null && isScanning) {
                bleBasePackage.setCance(true);
            }
            if(isScanning)
                cancel = true;
            isScanning = false;
            if (processCodeThread != null) {
                processCodeThread.join(3 * 1000);
            }
            processCodeThread = null;
            return startSendCmd(SET, tags, CMD_TIMEOUT);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean turnOffAmountDisplay(StartScanExtParams params) {
        boolean isTurnOff = true;
        if (params != null) {
            isTurnOff = params.isTurnOffAmountDisplay();
        }
        if (isTurnOff) {
            String[] tags = new String[]{"AMOUNT=OFF"};
            return startSendCmd(SET, tags, CMD_TIMEOUT);
        } else {
            return false;
        }
    }

    private String setAmount(String amount) {
        if (!TextUtils.isEmpty(amount)) {
            if (amount.length() > 7) {
                devicelogger.error("setAmount length can't > 7");
                return null;
            } else {
                try {
                    Float.valueOf(amount);
                } catch (Exception e) {
                    devicelogger.error("Float.valueOf(String) error. amount = " + amount);
                    return null;
                }
                return "AMOUNT=" + amount;
            }
        } else {
            return null;
        }
    }

    private String setEnter(boolean enter) {
        if (enter) {
            return "ENTER=ON";
        } else {
            return "ENTER=OFF";
        }
    }

    private String setSuccessVoicePrompt(String voicePrompt) {
        int length = 0;
        try {
            length = voicePrompt.getBytes("GBK").length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (length <= 100 && length > 0) {
            String len = String.valueOf(length);
            if (len.length() == 1) {
                len = "000" + len;
            } else if (len.length() == 2) {
                len = "00" + len;
            } else if (len.length() == 3) {
                len = "0" + len;
            }
            return "PROMPT=" + len + voicePrompt;
        } else {
            devicelogger.error("setSuccessVoicePrompt length can't > 100 or = 0");
            return null;
        }
    }

    private String setSuffix(String suffix) {
        if (suffix != null) {
            if (suffix.equals("")) {
                return "SUFFIX=";
            } else {
                int length = 0;
                try {
                    length = suffix.getBytes(GBK).length;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (length <= 40 && length > 0) {
                    String len = String.valueOf(length);
                    if (len.length() == 1) {
                        len = "000" + len;
                    } else if (len.length() == 2) {
                        len = "00" + len;
                    } else if (len.length() == 3) {
                        len = "0" + len;
                    }
                    return "SUFFIX=" + len + suffix;
                } else {
                    devicelogger.error("[setSuffix] Suffix length can't > 40");
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    private String setPrefix(String prefix) {
        if (prefix != null) {
            if (prefix.equals("")) {
                return "PREFIX=";
            } else {
                int length = 0;
                try {
                    length = prefix.getBytes(GBK).length;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (length <= 40 && length > 0) {
                    String len = String.valueOf(length);
                    if (len.length() == 1) {
                        len = "000" + len;
                    } else if (len.length() == 2) {
                        len = "00" + len;
                    } else if (len.length() == 3) {
                        len = "0" + len;
                    }
                    return "PREFIX=" + len + prefix;
                } else {
                    devicelogger.error("[setPrefix] Prefix length can't > 40 or = 0");
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    private String setScanVoicePrompt(String voicePrompt) {
        int length = 0;
        try {
            length = voicePrompt.getBytes(GBK).length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (length <= 100 && length > 0) {
            String len = String.valueOf(length);
            if (len.length() == 1) {
                len = "000" + len;
            } else if (len.length() == 2) {
                len = "00" + len;
            } else if (len.length() == 3) {
                len = "0" + len;
            }
            return "VOICE=" + len + voicePrompt;
        } else {
            devicelogger.error("[setScanVoicePrompt] length can't > 100 or = 0");
            return null;
        }
    }

    private String setVolume(int volume) {
        return "VOLUME=" + volume;
    }

    private String setScanLight(ScanBoxParams.ScanLightStatus lightStatus) {
        if (lightStatus != null) {
            StringBuilder builder = new StringBuilder();
            boolean turnOn = lightStatus.isTurnOn();
            if (turnOn) {
                builder.append("LED=ON_");
            } else {
                builder.append("LED=OFF_");
            }
            ScanBoxLight[] lightColor = lightStatus.getLightColor();
            for (int i = 0; i < lightColor.length; i++) {
                if (lightColor[i] == ScanBoxLight.RED) {
                    builder.append("R");
                } else if (lightColor[i] == ScanBoxLight.YELLOW) {
                    builder.append("Y");
                } else if (lightColor[i] == ScanBoxLight.BLUE) {
                    builder.append("B");
                } else if (lightColor[i] == ScanBoxLight.GREEN) {
                    builder.append("G");
                }
            }

            return builder.toString();
        }
        return null;
    }

    private String setBackLight(boolean backLight) {
        if (backLight) {
            return "LED=ON";
        } else {
            return "LED=OFF";
        }
    }

    private byte[] pack(String cmd, String[] data) {
        byte[] sendBuf = new byte[0];
        try {
            HashMap<String, String> items = new HashMap<String, String>();
            items.put(STX, FIELD_PREFIX + STX + FIELD_SUFFIX);
            items.put(CMD, FIELD_PREFIX + cmd + FIELD_SUFFIX);
            String tagsInd = "00";
            if(data!=null && data.length>0){
                tagsInd = String.format(Locale.CHINA, "%02d", data.length);
            }
            devicelogger.debug("[pack]data:"+(data==null?null:data.length)+"tagsInd:"+tagsInd);
            items.put(TAGNUM, FIELD_PREFIX + tagsInd + FIELD_SUFFIX);
            serialNum++;
            if (serialNum >= 100) {
                serialNum = 0;
            }
            String serial = String.valueOf(serialNum);
            if (serial.length() == 1) {
                serial = "0" + serial;
            }
            items.put(SERIAL, FIELD_PREFIX + serial + FIELD_SUFFIX);
            int lenth = 7;
            int dataNum = 0;
            String targer = "";
            if (data != null) {
                dataNum = data.length;
                for (int i = 0; i < data.length; i++) {
                    targer = FIELD_PREFIX + data[i] + FIELD_SUFFIX + targer;
                    lenth += data[i].getBytes(GBK).length;
                }
            }
            items.put(DATA, targer);
            items.put(EXT, FIELD_PREFIX + EXT + FIELD_SUFFIX);
            String lenthStr = String.valueOf(lenth);
            if (lenthStr.length() == 1) {
                lenthStr = "000" + lenthStr;
            } else if (lenthStr.length() == 2) {
                lenthStr = "00" + lenthStr;
            } else if (lenthStr.length() == 3) {
                lenthStr = "0" + lenthStr;
            }
            items.put(LEN, FIELD_PREFIX + lenthStr + FIELD_SUFFIX);
            sendBuf = new byte[lenth + STX.length() + lenthStr.length() + EXT.length() + 2 + (7 + dataNum) * 2];
            System.arraycopy(items.get(STX).getBytes(), 0, sendBuf, 0, items.get(STX).getBytes().length);
            System.arraycopy(items.get(LEN).getBytes(), 0, sendBuf, 5, items.get(LEN).getBytes().length);
            System.arraycopy(items.get(CMD).getBytes(), 0, sendBuf, 5 + 6, items.get(CMD).getBytes().length);
            System.arraycopy(items.get(TAGNUM).getBytes(), 0, sendBuf, 5 + 6 + 5, items.get(TAGNUM).getBytes().length);
            System.arraycopy(items.get(SERIAL).getBytes(), 0, sendBuf, 5 + 6 + 5 + 4,
                    items.get(SERIAL).getBytes().length);
            System.arraycopy(items.get(DATA).getBytes(GBK), 0, sendBuf, 5 + 6 + 5 + 4 + 4,
                    items.get(DATA).getBytes(GBK).length);
            System.arraycopy(items.get(EXT).getBytes(), 0, sendBuf,
                    5 + 6 + 5 + 4 + 4 + items.get(DATA).getBytes(GBK).length, items.get(EXT).getBytes().length);
            items.put(LRC, FIELD_PREFIX + getLrc(sendBuf) + FIELD_SUFFIX);
            System.arraycopy(items.get(LRC).getBytes(), 0, sendBuf,
                    5 + 6 + 5 + 4 + 4 + items.get(DATA).getBytes(GBK).length + 5, items.get(LRC).getBytes().length);
            devicelogger.debug("[pack] send:" + new String(sendBuf, GBK) + " len:" + sendBuf.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sendBuf;
    }

    private byte[] processCmd(byte[] sendBuf, int timeOut) {
        devicelogger.debug("-----[processCmd]timeOut:" + timeOut);
        String codeRslt = "";
        isCompatibleVersion = false;
        try {
            int result = this.open();
            if (result < 0) {
                devicelogger.error("[processCmd] open serial failed.");
                return null;
            }

            result = this.write(sendBuf, sendBuf.length, 0);
            if (result < 0) {
                devicelogger.debug("[processCmd] write serial failed.");
                return null;
            }
            if (commMode == ScanBoxInitExtParams.CommMode.BLE_USB2) {
                bleBasePackage.setCance(false);
                byte[] resp = bleBasePackage.readUSB2ProtData(timeOut, PortType.BLEBASE_USB2);
                if (resp == null) {
                    devicelogger.debug("[processCmd]resp byte is null.");
                    return null;
                }
                devicelogger.debug("[processCmd] Response data=" + new String(resp, GBK) + " response:" + ISOUtils.hexString(resp));
                return resp;
            }
            //清空读缓冲区
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int count = 0;
            while (true) {
                byte[] head = new byte[5];
                result = this.read(head, head.length, 1000);
                count += 1000;
                if (count > timeOut) {
                    devicelogger.debug("[processCmd] read timeout");
                    throw new ProcessTimeoutException("读串口数据超时");
                }
                if(result>0 && !Arrays.equals(head, new byte[]{0x3C, 0x53, 0x54, 0x58, 0x3E})){
                    //兼容版扫码盒子，扫码结果只有码值数据，没有包头包尾
                    isCompatibleVersion = true;
                    byte[] tempData = new byte[result];
                    System.arraycopy(head,0,tempData,0,result);
                    codeRslt = (tempData==null?"":ISOUtils.hexString(tempData));
                    devicelogger.debug("[processCmd]tempData:"+codeRslt);
                    break;
                }
                if (result != head.length) {
                    devicelogger.error("[processCmd]result != head.length,continue");
                    continue;
                }
                if (Arrays.equals(head, new byte[]{0x3C, 0x53, 0x54, 0x58, 0x3E})) {//3C 53 54 58 3E
                    try {
                        bos.write(head);
                    } catch (IOException e) {
                        devicelogger.error("[processCmd] bos write head excetion", e);
                        return null;
                    }
                    break;
                } else {
                    continue;
                }
            }

            if(isCompatibleVersion){
                devicelogger.debug("isCompatibleVersion:"+isCompatibleVersion);
                while (!(codeRslt.endsWith("0D0A") || codeRslt.endsWith("0A"))){
                    byte[] data = new byte[15];
                    result = this.read(data, data.length, 1000);
                    count += 1000;
                    if(result>0){
                        byte[] tempData = new byte[result];
                        System.arraycopy(data,0,tempData,0,result);
                        codeRslt = codeRslt+ISOUtils.hexString(tempData);
                    }
                    if (count > timeOut) {
                        devicelogger.debug("[processCmd] read timeout");
                        throw new ProcessTimeoutException("读串口数据超时");
                    }
                }
                if(!(codeRslt.endsWith("0D0A") || codeRslt.endsWith("0A"))){
                    devicelogger.error("[processCmd] read failed,data errror");
                    return null;
                }
                devicelogger.debug("[processCmd]sucess ,codeRslt:"+codeRslt);
                return ISOUtils.hex2byte(codeRslt);
            }
            byte[] middle = new byte[23];//13+5*2 数据长度4+指令号3+指示位2+序列号2+响应码2
            result = this.read(middle, middle.length, timeOut);
            if (result != middle.length) {
                devicelogger.debug("[processCmd] read middle lenth error.");
                return null;
            }
            try {
                bos.write(middle);
            } catch (IOException e) {
                devicelogger.error("[processCmd] bos write middle excetion", e);
                return null;
            }
            byte[] data = new byte[4];
            byte[] tag = new byte[2];
            System.arraycopy(middle, 1, data, 0, data.length);
            int len = Integer.valueOf(new String(data));
            devicelogger.debug("[processCmd] dataLenStr:" + new String(data) + " len:" + len);
            System.arraycopy(middle, 12, tag, 0, tag.length);
            int tagNum = Integer.valueOf(new String(tag));
            devicelogger.debug("[processCmd] tagLenStr:" + new String(tag) + " tagNum=" + tagNum);
            int needReadLen = tagNum * 2 + len - 9;//len - 9 = 数据长度4-(指令号3-指示位2-序列号2-响应码2)
            devicelogger.debug("[processCmd] needReadLen=" + needReadLen);

            byte[] tail = new byte[needReadLen + 9];//9=<ETX><XX>
            int tailLen = tail.length;
            for (int i = 0; i < tailLen; i += 1024) {
                int needLen = 0;
                if (tailLen - i >= 1024) {
                    needLen = 1024;
                } else {
                    needLen = tailLen - i;
                }
                byte[] tmp = new byte[needLen];
                result = this.read(tmp, needLen, timeOut);
                if (result != needLen) {
                    devicelogger.debug("[processCmd] read tail lenth error.");
                    return null;
                }
                try {
                    bos.write(tmp);
                } catch (IOException e) {
                    devicelogger.error("[processCmd] bos write excetion", e);
                    return null;
                }
            }
            byte[] resp = bos.toByteArray();
            if (resp == null) {
                devicelogger.debug("[processCmd]resp byte is null.");
                return null;
            }
            devicelogger.debug("[processCmd] Response data=" + new String(resp, GBK) + " response:" + ISOUtils.hexString(resp));
            return resp;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            devicelogger.debug("[processCmd] this close.");
            this.close();
        }
        return null;
    }

    private int processCode(int timeOut, ResultListener listener, boolean isOnce) {
        try {
            String codeRslt = "";
            isCompatibleVersion = false;
            devicelogger.debug("[processCode]isScanning:" + isScanning + "; timeOut:" + timeOut + "; isOnce:" + isOnce);
            if (timeOut <= 0 || listener == null) {
                devicelogger.debug("[processGetCode] timeOut=" + timeOut + " listener=" + listener);
//                listener.onError(SCAN_ERROR_CODE, "Params error.");
                return ERROR;
            }
            int readTimeOut = 3000;
            int result = this.open();
            if (result < 0) {
                devicelogger.debug("[processGetCode] open serial failed.");
//                listener.onError(SCAN_ERROR_CODE, "Open serial failed.");
                return ERROR;
            }
            if (commMode == ScanBoxInitExtParams.CommMode.BLE_USB2) {
                bleBasePackage.setCance(false);
                byte[] resp = bleBasePackage.readUSB2ProtData(timeOut, PortType.BLEBASE_USB2);
                if (resp == null) {
                    devicelogger.debug("[processCode]resp byte is null.");
                    return ERROR;
                }
                devicelogger.debug("[processCode] Response data=" + new String(resp, GBK) + " response:" + ISOUtils.hexString(resp));
                HashMap<String, String> map = unpack(resp);
                String content = ackDataItems.get(TAG_DATA);
                if (getAckCode(map, true) && content != null && !content.equals("")) {
                    devicelogger.debug("--------底座扫码盒子，扫描成功-----：" + content);
                    listener.onSuccess(content);
                } else {
//                    listener.onError(SCAN_ERROR_CODE, "General error");
                    return ERROR;
                }
                //               if (isOnce) {
                isScanning = false;
//                }
                return SUCCESS;
            }
            //清空读缓冲区
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int count = 0;
            readAgain:
            while (isScanning) {
                bos.reset();
                while (isScanning) {
                    byte[] head = new byte[5];
                    result = this.read(head, head.length, 1000);
                    count += 1000;
                    if (count > timeOut) {
                        return TIMEOUT;
                    }
                    if(cancel) {
                        return CANCEL;
                    }
                    if(result>0 && !Arrays.equals(head, new byte[]{0x3C, 0x53, 0x54, 0x58, 0x3E})){
                        //兼容版扫码盒子，扫码结果只有码值数据，没有包头包尾
                        isCompatibleVersion = true;
                        byte[] tempData = new byte[result];
                        System.arraycopy(head,0,tempData,0,result);
                        codeRslt = (tempData==null?"":ISOUtils.hexString(tempData));
                        devicelogger.debug("[processCode]tempData:"+codeRslt);
                        break;
                    }
                    if (result != head.length) {
                        continue;
                    }
                    if (Arrays.equals(head, new byte[]{0x3C, 0x53, 0x54, 0x58, 0x3E})) {//3C 53 54 58 3E
                        try {
                            bos.write(head);
                        } catch (IOException e) {
                            devicelogger.error("[processGetCode] bos write head excetion", e);
//                            listener.onError(SCAN_ERROR_CODE, "write head excetion.");
                            return ERROR;
                        }
                        break;
                    } else {
                        continue;
                    }
                }
                if (!isScanning) {
                    return SUCCESS;
                }
                if(isCompatibleVersion){
                    devicelogger.debug("[processCode]isCompatibleVersion:"+isCompatibleVersion);
                    while (!(codeRslt.endsWith("0D0A") || codeRslt.endsWith("0A"))){
                        byte[] data = new byte[15];
                        result = this.read(data, data.length, 1000);
                        count += 1000;
                        if(result>0){
                            byte[] tempData = new byte[result];
                            System.arraycopy(data,0,tempData,0,result);
                            codeRslt = codeRslt+ISOUtils.hexString(tempData);
                        }
                        if (count > timeOut) {
                            devicelogger.debug("[[processCode]] read timeout");
                            throw new ProcessTimeoutException("读串口数据超时");
                        }
                    }
                    if(!(codeRslt.endsWith("0D0A") || codeRslt.endsWith("0A"))){
                        devicelogger.error("[[processCode]] read failed,data errror");
                        return ERROR;
                    }
                    devicelogger.debug("[[processCode]]sucess ,codeRslt:"+codeRslt);
                    listener.onSuccess(new String(ISOUtils.hex2byte(codeRslt)));
                    if (isOnce) {
                        isScanning = false;
                    } else {
                        continue readAgain;
                    }
                    return SUCCESS;
                }

                byte[] middle = new byte[23];//13+5*2 数据长度4+指令号3+指示位2+序列号2+响应码2
                result = this.read(middle, middle.length, readTimeOut);
                if (result != middle.length) {
                    devicelogger.debug("[processGetCode] read middle lenth error.");
                    timeOut -= readTimeOut;
                    continue readAgain;
                }
                try {
                    bos.write(middle);
                } catch (IOException e) {
                    devicelogger.error("[processGetCode] bos write middle excetion", e);
//                    listener.onError(SCAN_ERROR_CODE, "write middle excetion.");
                    return ERROR;
                }
                byte[] data = new byte[4];
                byte[] tag = new byte[2];
                System.arraycopy(middle, 1, data, 0, data.length);
                int len = Integer.valueOf(new String(data));
                devicelogger.debug("[processGetCode] dataLenStr:" + new String(data) + " len:" + len);
                System.arraycopy(middle, 12, tag, 0, tag.length);
                int tagNum = Integer.valueOf(new String(tag));
                devicelogger.debug("[processGetCode] tagLenStr:" + new String(tag) + " tagNum=" + tagNum);
                int needReadLen = tagNum * 2 + len - 9;//len - 9 = 数据长度4-(指令号3-指示位2-序列号2-响应码2)
                devicelogger.debug("[processGetCode] needReadLen=" + needReadLen);

                byte[] tail = new byte[needReadLen + 9];//9=<ETX><XX>
                int tailLen = tail.length;
                for (int i = 0; i < tailLen; i += 1024) {
                    int needLen = 0;
                    if (tailLen - i >= 1024) {
                        needLen = 1024;
                    } else {
                        needLen = tailLen - i;
                    }
                    byte[] tmp = new byte[needLen];
                    result = this.read(tmp, needLen, readTimeOut);
                    if (result != needLen) {
                        devicelogger.debug("[processGetCode] read tail lenth error.");
                        timeOut -= readTimeOut;
                        continue readAgain;
                    }
                    try {
                        bos.write(tmp);
                    } catch (IOException e) {
                        devicelogger.error("[processGetCode] bos write excetion", e);
//                        listener.onError(SCAN_ERROR_CODE, "write excetion.");
                        return ERROR;
                    }
                }
                byte[] resp = bos.toByteArray();
                if (resp == null) {
                    devicelogger.debug("[processGetCode] resp byte is null.");
//                    listener.onError(SCAN_ERROR_CODE, "resp byte is null.");
                    return ERROR;
                }
                devicelogger.debug("[processGetCode] Response data=" + new String(resp, GBK) + " response:" + ISOUtils.hexString(resp));
                HashMap<String, String> map = unpack(resp);
                String content = ackDataItems.get(TAG_DATA);
                if (getAckCode(map, true) && content != null && !content.equals("")) {
                    listener.onSuccess(content);
                } else {
//                    listener.onError(SCAN_ERROR_CODE, "General error");
                    return ERROR;
                }
                if (isOnce) {
                    isScanning = false;
                } else {
                    continue readAgain;
                }
            }
            return SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            devicelogger.debug("[processGetCode] this close.");
            this.close();
        }
        return ERROR;
    }

    private HashMap<String, String> unpack(byte[] data) {
        try {
            devicelogger.debug("[unpack] data=" + (data != null ? ISOUtils.hexString(data) : "null"));
            byte[] recvLrc = new byte[2];
            System.arraycopy(data, data.length - 3, recvLrc, 0, recvLrc.length);
            String calcLrc = getLrc(data);
            int lrc1 = Integer.valueOf(new String(recvLrc), 16), lrc2 = 0;
            if (calcLrc != null) {
                lrc2 = Integer.valueOf(calcLrc, 16);
            }
            devicelogger.debug("lrc1:" + lrc1 + "; lrc2:" + lrc2);
            if (lrc1 == lrc2) {
                String[] items = new String(data, GBK).split(FIELD_SUFFIX + FIELD_PREFIX);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(STX, items[0].substring(1));
                map.put(LEN, items[1]);
                map.put(CMD, items[2]);
                map.put(TAGNUM, items[3]);
                map.put(SERIAL, items[4]);
                map.put(ACKCODE, items[5]);
                ackDataItems.clear();
                if (Integer.valueOf(items[1]) > 9) {
                    for (int i = 6; i < items.length - 2; i++) {
                        String body = items[i].trim();
                        String[] tmp = body.split("=");
                        ackDataItems.put(tmp[0], tmp[1]);
                    }
                }
                map.put(DATA, ackDataItems.size() + "");
                map.put(EXT, items[items.length - 2]);
                map.put(LRC, items[items.length - 1].substring(0, items[items.length - 1].length() - 1));
                //int serial = Integer.valueOf(map.get(SERIAL));
                //if((serial-1)!= serialNum){
                //devicelogger.debug("unpack serial num error. ack="+map.get(SERIAL)+ " serialNum="+serialNum);
                //return null;
                //}
                for (String key : map.keySet()) {
                    devicelogger.debug(key + "=" + map.get(key));
                }
                return map;
            } else {
                devicelogger.debug("[unpack] lrc check error.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getTag(HashMap<String, String> map, String tag) {
        String value = null;
        if (map == null) {
            return null;
        }
        value = map.get(tag);
        return value;
    }

    private boolean getAckCode(HashMap<String, String> map, boolean checkTagData) {
        try {
            if (checkTagData) {
                int bodySize = Integer.valueOf(map.get(DATA));
                String tag_data = ackDataItems.get(TAG_DATA);
                devicelogger.debug("[getAckCode] TAG_DATA=" + tag_data);
                if (bodySize > 0 && tag_data == null) {
                    return false;
                }
            }
            String ack = getTag(map, ACKCODE);
            devicelogger.debug("[getAckCode] ACKCODE = " + ack);
            if (ack != null && ack.equals(ACK_OK)) {
                return true;
            }else{
                devicelogger.error("[getAckCode] error ACKCODE = " + ack);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private String getLrc(byte[] src) {
        String lrc = null;
        try {
            lrc = "00";
            if (src == null) {
                return lrc;
            }
            byte temp = (byte) (src[0] ^ src[1]);
            for (int i = 2; i < src.length - 4; i++) {
                temp = (byte) (temp ^ src[i]);
            }

            lrc = ISOUtils.hexString(new byte[]{temp});
            if (lrc.length() == 1) {
                lrc = "0" + lrc;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        devicelogger.debug("[getLrc] lrc:" + lrc);
        return lrc;
    }

    private boolean startSendCmd(String cmd, String[] tag, int timeOut) {
        byte[] sendBuf = this.pack(cmd, tag);
        byte[] recvBuf = null;
        if (params.getPortType() == PortType.BLEBASE_USB2) {
            devicelogger.debug("[startSendCmd] 蓝牙底座发送数据：" + (sendBuf == null ? null : ISOUtils.hexString(sendBuf)));
            bleBasePackage.write(sendBuf, 1024, timeOut, PortType.BLEBASE_USB2);
            bleBasePackage.setCance(false);
            recvBuf = bleBasePackage.readUSB2ProtData(timeOut, PortType.BLEBASE_USB2);
            String str = (recvBuf == null ? "" : ISOUtils.hexString(recvBuf));
            if (recvBuf != null && Arrays.equals(ISOUtils.hex2byte("0D0A"), new byte[]{recvBuf[recvBuf.length - 2],
                    recvBuf[recvBuf.length - 1]})) {
                String fianlStr = str.substring(0, str.length() - 4);
                devicelogger.info("蓝牙底座USB2口最终数据：" + fianlStr);
                recvBuf = ISOUtils.hex2byte(fianlStr);
            }
        } else {
            recvBuf = processCmd(sendBuf, timeOut);
        }
        if (recvBuf == null) {
            devicelogger.error("----------recvBuf==null-----------");
            return false;
        }
        if((ISOUtils.hexString(recvBuf).endsWith("0D0A") || ISOUtils.hexString(recvBuf).endsWith("0A"))){
            return true;
        }
        HashMap<String, String> map = this.unpack(recvBuf);
        return getAckCode(map, false);
    }

    private int open() {
        devicelogger.debug("---[open]---commMode:" + commMode+";isBleUSBOpened:"+isBleUSBOpened);
        if (commMode == ScanBoxInitExtParams.CommMode.BLE_USB2) {
            return 0;
        } else if (commMode == ScanBoxInitExtParams.CommMode.BLE_USB1) {
            if(isBleUSBOpened){
                int clearRet = BluetoothController.getInstance().usbPortClrBuf(0x01,0);
                devicelogger.debug( "[open]clearRet: "+clearRet);
                if(clearRet<0){
                    int open =  BluetoothController.getInstance().usbOpenPort(0x01,0);
                    if(open>=0){
                        return 0;
                    }else{
                        isBleUSBOpened = false;
                    }
                }
                return clearRet;
            }else{
                int open =  BluetoothController.getInstance().usbOpenPort(0x01,0);
                devicelogger.debug("---[open]---commMode:" + commMode+";result:"+open);
                int clearRet = BluetoothController.getInstance().usbPortClrBuf(0x01,0);
                devicelogger.debug( "[open]clearRet: "+clearRet);
                if(open>=0){
                    isBleUSBOpened = true;
                }
                return open;
            }
        } else if (commMode == ScanBoxInitExtParams.CommMode.UART) {
            int result = serialOper.open(portType, baudRate, serialExtParams);
            if (result < 0) {
                devicelogger.debug("[open] serial failed.");
                return result;
            }
            boolean bool = serialOper.clearBuffer(0x00);
            if (!bool) {
                devicelogger.debug("[open] clearInputBuffer failed.");
            }
            return result;
        } else if (commMode == ScanBoxInitExtParams.CommMode.USB) {
//            if(usbModule.clearBuffer()){
            boolean result = init(new ScanBoxInitExtParams(ScanBoxInitExtParams.CommMode.USB, null, null));
            if (result) {
                usbModule.clearBuffer();
                return 1;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    private int read(byte[] outputData, int lengthMax, int timeOut) {
        devicelogger.debug("----[read]lengthMax:" + lengthMax + ";timeOut:" + timeOut);
        if (commMode == ScanBoxInitExtParams.CommMode.BLE_USB2) {
            bleBasePackage.setCance(false);
            byte[] resultData = bleBasePackage.readUSB2ProtData(timeOut, PortType.BLEBASE_USB2);
            if (outputData != null && outputData.length <= lengthMax) {
                System.arraycopy(resultData, 0, outputData, 0, outputData.length);
                return outputData.length;
            } else if (outputData != null && outputData.length > lengthMax) {
                System.arraycopy(resultData, 0, outputData, 0, lengthMax);
                return lengthMax;
            }
            return -1;
        } else if (commMode == ScanBoxInitExtParams.CommMode.BLE_USB1) {
            StringBuffer outLen = new StringBuffer();
            int readRet = BluetoothController.getInstance().usbPortRead(0x01,0,lengthMax,timeOut,outLen,outputData);
            if(readRet<0){
                devicelogger.error("BluetoothController.getInstance().usbPortRead failed:"+readRet);
                if(readRet==-4){
                    isBleUSBOpened = false;
                }
                return -1;
            }
            devicelogger.debug("[read] read len=" + readRet + " outputData=" + (outputData == null ? "null" :
                    ISOUtils.hexString(outputData)));
            return outputData.length;
        } else if (commMode == ScanBoxInitExtParams.CommMode.UART) {
            return serialOper.read(outputData, lengthMax, timeOut);
        } else if (commMode == ScanBoxInitExtParams.CommMode.USB) {
            return usbModule.read(outputData, lengthMax, timeOut);
        } else {
            return -1;
        }
    }

    private int write(byte[] inputData, int lengthMax, int timeOut) {
        devicelogger.debug("---------[write]lengthMax：" + lengthMax + ";timeOut:" + timeOut + ";commMode:" + commMode);
        if (commMode == ScanBoxInitExtParams.CommMode.UART) {
            return serialOper.write(inputData, lengthMax, timeOut);
        } else if (commMode == ScanBoxInitExtParams.CommMode.USB) {
            return usbModule.write(inputData, lengthMax, timeOut);
        } else if (commMode == ScanBoxInitExtParams.CommMode.BLE_USB1) {
            int ret =  BluetoothController.getInstance().usbPortWrite(0x01,0,inputData.length,inputData);
            devicelogger.debug("----------蓝牙底座写入结果：" + ret);
            return ret;
        } else {
            return -1;
        }
    }

    private int close() {
        devicelogger.debug("---[close]----");
        if (commMode == ScanBoxInitExtParams.CommMode.UART) {
            return serialOper.close();
        } else if (commMode == ScanBoxInitExtParams.CommMode.USB) {
            return usbModule.close();
        } else if (commMode == ScanBoxInitExtParams.CommMode.BLE_USB1 || commMode == ScanBoxInitExtParams.CommMode.BLE_USB2) {
            devicelogger.debug("-----[close]-------isUseBleBase");
            return 0;
        } else {
            return -1;
        }
    }
}
