package com.newland.nsdk.core.internal.serialportmanager;

import android.content.Context;
import android.newland.os.NlBuild;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.serialport.BaudRate;
import com.newland.nsdk.core.api.common.serialport.DataBits;
import com.newland.nsdk.core.api.common.serialport.ParityBit;
import com.newland.nsdk.core.api.common.serialport.SerialPortSettings;
import com.newland.nsdk.core.api.common.serialport.StopBits;
import com.newland.nsdk.core.api.internal.serialportmanager.SerialPort;
import com.newland.nsdk.core.api.internal.serialportmanager.SerialPortManager;
import com.newland.nsdk.core.api.internal.serialportmanager.SerialPortType;
import com.newland.nsdk.core.api.internal.serialportmanager.USBSerialPort;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class SerialPortManagerImpl implements SerialPortManager {
    private USBSerialPort usbSerialPort;
    private SerialPort serialPort;
    private Context mContext;
    private volatile static SerialPortManagerImpl instance;
    private boolean isSupportUSB;
    private boolean isSupportPinPad;
    private boolean isSupportRs232;
    private NlAccessControlManagerUtils nlAccessControlManagerUtils;
    public static SerialPortManagerImpl getInstance(Context mContext, boolean isSupportUSB, boolean isSupportPinPad, boolean isSupportRs232) {
        if (instance == null) {
            synchronized (SerialPortManagerImpl.class) {
                if (instance == null || instance.mContext != mContext || instance.isSupportUSB != isSupportUSB || instance.isSupportPinPad != isSupportPinPad || instance.isSupportRs232 != isSupportRs232) {
                    instance = new SerialPortManagerImpl(mContext, isSupportUSB, isSupportPinPad, isSupportRs232);
                }
            }
        } else {
            if (instance.mContext != mContext || instance.isSupportUSB != isSupportUSB || instance.isSupportPinPad != isSupportPinPad || instance.isSupportRs232 != isSupportRs232) {
                instance = new SerialPortManagerImpl(mContext, isSupportUSB, isSupportPinPad, isSupportRs232);
            }
        }
        return instance;
    }
    private SerialPortManagerImpl(Context mContext, boolean isSupportUSB, boolean isSupportPinPad, boolean isSupportRs232) {
        this.mContext = mContext;
        this.isSupportUSB = isSupportUSB;
        this.isSupportPinPad = isSupportPinPad;
        this.isSupportRs232 = isSupportRs232;
    }

    private boolean isSupportUSB() {
        return isSupportUSB;
    }
    private boolean isSupportPinPad() {
        return isSupportPinPad;
    }
    private boolean isSupportRs232() {
        return isSupportRs232;
    }
    @Override
    public SerialPort createInstance(SerialPortType type, SerialPortSettings serialPortSettings) throws NSDKException {
        if (type == null) {
            throw new NSDKIllegalParameterException("Serial port type shall not be null.");
        }
        SerialPortSettings settings = serialPortSettings;
        if (serialPortSettings == null) {
            settings = new SerialPortSettings(BaudRate.BPS115200, DataBits.DATA_BIT_8, ParityBit.NO_CHECK, StopBits.STOP_BIT_ONE, true);
        }
        checkSerialPortSupport(type);

        if (isExistsNlAccessControlManager()) {
            try {
                String portName = nlAccessControlManagerUtils.getPortName(type);
                if (portName != null && !portName.isEmpty()) {
                    return serialPort = new SerialPortImpl(portName, settings, type);
                }
                return getSerialPortByDevice(type, settings);
            } catch (Exception e) {
                //NlAccessControlManager.getPortName 不存在时使用旧逻辑进行兼容
                return getSerialPortByDevice(type, settings);
            }
        } else {
            return getSerialPortByDevice(type, settings);
        }
    }

    @Override
    public SerialPort createInstance(String nodeName, SerialPortSettings serialPortSettings) throws NSDKException {
        if (nodeName == null || nodeName.length() == 0) {
            throw new NSDKException(ErrorCode.PARAM_ERROR, "Node name shall not be null.");
        }
        SerialPortSettings settings = serialPortSettings;
        if (serialPortSettings == null) {
            settings = new SerialPortSettings(BaudRate.BPS115200, DataBits.DATA_BIT_8, ParityBit.NO_CHECK, StopBits.STOP_BIT_ONE, true, false);
        }
        serialPort = new SerialPortImpl(nodeName, settings, null);

        return serialPort;
    }

    private void checkSerialPortSupport(SerialPortType type) throws NSDKException{
        if (type == SerialPortType.PINPAD) {
            if (!isSupportPinPad()) {
                throw new NSDKException(ErrorCode.UNSUPPORTED, "Unsupported PINPAD Module.");
            }
        } else if (type == SerialPortType.RS232 || type == SerialPortType.RS232B) {
            if (!isSupportRs232()) {
                throw new NSDKException(ErrorCode.UNSUPPORTED, "Unsupported RS232 Module.");
            }
        } else {
            if (!isSupportUSB()) {
                throw new NSDKException(ErrorCode.UNSUPPORTED, "Unsupported USB Module.");
            }
        }
    }

    /**
     * 此方法主要用于获取 N750P 内 proc/tty/driver/usbserial 文件内 pl2303 驱动节点和外置串口线的对应关系，对应关系如下所示：
     * X: name:"pl2303" vendor:067b product:23d3 num_ports:1 port:0 path:usb-xhci-hcd.0.auto-1.3 -> RS232 interface, 其中 "X" 是 RS232 串口的对应节点后缀，节点全名为：dev/ttyUSBX.
     * Y: name:"pl2303" vendor:067b product:23d3 num_ports:1 port:0 path:usb-xhci-hcd.0.auto-1.4 -> USB interface, 其中 "Y" 是 USB Type-A 串口的对应节点后缀， 节点全名为: dev/ttyUSBY.
     */
    private String getNodeNameFromFile(SerialPortType type) throws NSDKException{
        try {
            String[] fileContent = new String[3];
            File file = new File("proc/tty/driver/usbserial");
            if (!file.exists()) {
                throw new NSDKException(ErrorCode.ERROR, "No available file.");
            }
            FileInputStream fio = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fio);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = "";
            int i = 0;
            while ((line = bufferedReader.readLine()) != null) {
                fileContent[i] = line;
                i++;
            }
            if (i < 2) {
                throw new NSDKException(ErrorCode.ERROR, "Please pull out USB OTG cable, and call this interface again.");
            }
            bufferedReader.close();
            inputStreamReader.close();
            fio.close();
            String nodePrefix = "dev/ttyUSB";
            String nodeSuffix = "";
            String deviceModel = Build.MODEL;
            if ("N750P".equalsIgnoreCase(deviceModel)) {
                if (type == SerialPortType.USB_HOST) {
                    nodeSuffix = getSuffixByKeyWord(fileContent, "path:usb-xhci-hcd.0.auto-1.4");
                    //兼容后续有可能出现 N750P 串口芯片也升级到 CH340 的情况，实际上当前不会走到该逻辑
                    if (TextUtils.isEmpty(nodeSuffix)) {
                        nodeSuffix = getSuffixByKeyWord(fileContent, "path:usb-xhci-hcd.0.auto-1.3");
                    }
                } else if (type == SerialPortType.RS232) {
                    //20260121:N750J->N750P 需要先对 N750J 1.2 的进行判断，没有则为 N750P
                    nodeSuffix = getSuffixByKeyWord(fileContent, "path:usb-xhci-hcd.0.auto-1.2");
                    //兼容后续有可能出现 N750P 串口芯片也升级到 CH340 的情况，实际上当前不会走到该逻辑
                    if (TextUtils.isEmpty(nodeSuffix)) {
                        nodeSuffix = getSuffixByKeyWord(fileContent, "path:usb-xhci-hcd.0.auto-1.3");
                    }
                }
            } else if ("N750J".equalsIgnoreCase(deviceModel)) {
                //均优先判断 CH34X 系列的逻辑
                if (type == SerialPortType.USB_HOST) {
                    nodeSuffix = getSuffixByKeyWord(fileContent, "path:usb-xhci-hcd.0.auto-1.3");
                    //兼容后续有可能出现 N750J 串口芯片使用 PL2303 的情况
                    if (TextUtils.isEmpty(nodeSuffix)) {
                        nodeSuffix = getSuffixByKeyWord(fileContent, "path:usb-xhci-hcd.0.auto-1.4");
                    }
                } else if (type == SerialPortType.RS232) {
                    nodeSuffix = getSuffixByKeyWord(fileContent, "path:usb-xhci-hcd.0.auto-1.2");
                    //兼容后续有可能出现 N750J 串口芯片使用 PL2303 的情况
                    if (TextUtils.isEmpty(nodeSuffix)) {
                        nodeSuffix = getSuffixByKeyWord(fileContent, "path:usb-xhci-hcd.0.auto-1.3");
                    }
                }
            }
            String portName = nodePrefix + nodeSuffix;
            Log.d("SerialPortManagerImpl", "nodeName:" + portName);
            return portName;
        } catch (Exception e) {
            return null;
        }
    }

    private String getSuffixByKeyWord(String[] content, String keyWord) {
        for (String ct : content) {
            if (ct.contains(keyWord)) {
                return String.valueOf(ct.charAt(0));
            }
        }
        return null;
    }

    private SerialPort getSerialPortByDevice(SerialPortType type, SerialPortSettings settings) throws NSDKException{
        if (type == SerialPortType.RS232 || type == SerialPortType.PINPAD || type == SerialPortType.RS232B) {
            if (Build.MODEL.contains("N750")) {
                String nodeName = getNodeNameFromFile(SerialPortType.RS232);
                serialPort = new SerialPortImpl(nodeName, settings, SerialPortType.RS232);
            } else {
                serialPort = new SerialPortImpl(mContext, type, settings);
            }
            return serialPort;
        } else if (type == SerialPortType.USB_HOST){
            if (Build.MODEL.equalsIgnoreCase("P300")) {
                usbSerialPort = new USBSerialPortImpl(mContext, type, settings);
                return usbSerialPort;
            }
            if (Build.MODEL.contains("N750")) {
                try {
                    String nodeName = getNodeNameFromFile(SerialPortType.USB_HOST);
                    if (Build.MODEL.equalsIgnoreCase("N750J") && nodeName.isEmpty()) {
                        return usbSerialPort = new USBSerialPortImpl(mContext, SerialPortType.USB, settings);
                    }
                    return serialPort = new SerialPortImpl(nodeName, settings, SerialPortType.USB_HOST);
                } catch (NSDKException e) {
                    if (Build.MODEL.equalsIgnoreCase("N750J")) {
                        return usbSerialPort = new USBSerialPortImpl(mContext, SerialPortType.USB, settings);
                    }
                }
            } else {
                throw new NSDKException(ErrorCode.UNSUPPORTED, "USB_HOST is not supported on the current device.");
            }
        } else if (type == SerialPortType.USB_DEVICE) {
           if (Build.MODEL.contains("N950S")) {
               return serialPort = new SerialPortImpl(mContext, SerialPortType.USB_DEVICE, settings);
           } else {
               throw new NSDKException(ErrorCode.UNSUPPORTED, "USB_DEVICE is not supported on the current device.");
           }
        } else {
            usbSerialPort = new USBSerialPortImpl(mContext, type, settings);
            return usbSerialPort;
        }
        return null;
    }


    private boolean isExistsNlAccessControlManager() {
        try {
            nlAccessControlManagerUtils = new NlAccessControlManagerUtils(mContext);
            return true;
        } catch (NSDKException e) {
            return false;
        }
    }
}
