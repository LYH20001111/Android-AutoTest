package com.newland.sdk.me.module.usb;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import com.newland.sdk.me.module.usb.usbserial.CDCSerialDevice;
import com.newland.sdk.me.module.usb.usbserial.UsbSerialDevice;
import com.newland.sdk.me.module.usb.usbserial.UsbSerialInterface;
import com.newland.sdk.module.usb.SelectUsbDeviceListener;
import com.newland.sdk.module.usb.USBModule;
import com.newland.sdk.module.usb.UsbSerialPortConfig;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.utils.ISOUtils;

import java.util.Date;
import java.util.HashMap;

/**
 * Author by bxy, Date on 2019/11/21.
 */
public class MEUSB implements USBModule {
    private DeviceLogger deviceLogger = DeviceLoggerFactory.getLogger("MEUSB");
    private Context context;
    private UsbManager usbManager;
    private UsbDevice usbDevice;
    private UsbDeviceConnection usbDeviceConnection;
    private UsbSerialDevice usbSerialPort;
    private USBSafeBuffer safeBuffer;
    private Object openSync = new Object();
    private Object waitPermissionConfirm = new Object();
    private static final String ACTION_USB_PERMISSION = "com.newland.sdk.USB_PERMISSION";
    private boolean isWorkingSyncMode = false;
    private UsbSerialPortConfig usbSerialPortConfig = new UsbSerialPortConfig();
    private static MEUSB meusb;
    private boolean isOpen=false;

    public static MEUSB getInstance(Context context) {
        if (meusb == null) {
            meusb = new MEUSB(context);
        }
        return meusb;
    }
    private MEUSB(Context context) {
        this.context = context;
        usbSerialPort = null;
        safeBuffer = new USBSafeBuffer();
        usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
    }

    @Override
    public int open(SelectUsbDeviceListener listener) {
        try {
            synchronized (openSync) {
                if (usbSerialPort != null) {
                    deviceLogger.debug("[open] usbSerialPort already open. " + usbSerialPort);
                    if (isWorkingSyncMode) {
                        usbSerialPort.syncClose();
                    } else {
                        usbSerialPort.close();
                    }
                    usbSerialPort = null;
                    usbDevice = null;
                    usbDeviceConnection = null;
                }
                safeBuffer.clear();
                HashMap<String, UsbDevice> usbDeviceList = usbManager.getDeviceList();
                if (usbDeviceList.size() <= 0) {
                    deviceLogger.error("[open] usbDeviceList.size < 0");
                    isOpen=false;
                    return -1;
                }
                usbDevice = listener.onSelect(usbDeviceList);

                if (usbDevice == null) {
                    deviceLogger.error("[open] usbDevice == null");
                    isOpen=false;
                    return -1;
                }

                boolean isSupported = UsbSerialDevice.isSupported(usbDevice);
                if (!isSupported) {
                    deviceLogger.error("[open] UsbDevice is not Supported");
                    isOpen=false;
                    return -1;
                }
                deviceLogger.debug("[open] Permission=" + usbManager.hasPermission(usbDevice));
                if (!usbManager.hasPermission(usbDevice)) {
                    registerUsbReceiver();
                    deviceLogger.error("[open] requestPermission");
                    PendingIntent mPendingIntent = PendingIntent.getBroadcast(context, 0,
                            new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_UPDATE_CURRENT);
                    usbManager.requestPermission(usbDevice, mPendingIntent);
                    waitPermissionConfirm();
                    context.unregisterReceiver(usbReceiver);
                }

                deviceLogger.debug("[open] hasPermission=" + usbManager.hasPermission(usbDevice));
                if (!usbManager.hasPermission(usbDevice)) {
                    deviceLogger.error("[open] usbManager.has not Permission");
                    isOpen=false;
                    return -1;
                }
                deviceLogger.debug("[open] findUSBDevice succ.");

                usbDeviceConnection = usbManager.openDevice(usbDevice);
                usbSerialPort = UsbSerialDevice.createUsbSerialDevice(usbDevice, usbDeviceConnection);
                if (usbSerialPort == null) {
                    deviceLogger.error("[open] usbSerialPort==null return.");
                    isOpen=false;
                    return -1;
                }
                deviceLogger.debug("[open] isWorkingSyncMode=" + isWorkingSyncMode);
                boolean open;
                if (isWorkingSyncMode) {
                    open = usbSerialPort.syncOpen();
                } else {
                    open = usbSerialPort.open();
                }
                if (!open) {
                    usbSerialPort = null;
                    deviceLogger.error("[open] usbSerialPort open fail");
                    isOpen=false;
                    return -1;
                }
                if (this.usbSerialPortConfig == null) {
                    this.usbSerialPortConfig = new UsbSerialPortConfig();
                }
                usbSerialPort.setBaudRate(this.usbSerialPortConfig.getBaudRate());
                usbSerialPort.setDataBits(this.usbSerialPortConfig.getDataBits());
                usbSerialPort.setStopBits(this.usbSerialPortConfig.getStopBits());
                usbSerialPort.setParity(this.usbSerialPortConfig.getParity());
                usbSerialPort.setFlowControl(this.usbSerialPortConfig.getFlowControl());
                usbSerialPort.read(usbReadCallback);
                if (usbSerialPort instanceof CDCSerialDevice) {
                    ((CDCSerialDevice) usbSerialPort).setCallback(usbReadCallback);
                }
                deviceLogger.debug("[open] openUSBDevice succ...");
                isOpen=true;
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        isOpen=false;
        return -1;
    }

    @Override
    public int read(byte[] outputData, int lengthMax, int timeOut) {
        try {
            deviceLogger.debug("[read] read lengthMax=" + lengthMax + " timeOut=" + timeOut);
            if (usbSerialPort == null) {
                deviceLogger.error("[read] read usbSerialPort==null");
                return -1;
            }
            long dis = 0;
            Date start = new Date();

//            if(isWorkingSyncMode){
//                int offset = 0;
//                while ((offset < lengthMax) && (dis < timeOut)){
//                    int len = usbSerialPort.syncRead(outputData,offset,lengthMax-offset,timeOut);
//                    deviceLogger.debug("[read] syncRead len="+len+" outputData=" + (outputData == null ? "null" : ISOUtils.hexString(outputData)));
//                    if(len > 0){
//                        offset += len;
//                    }
//                    Date end = new Date();
//                    dis = end.getTime() - start.getTime();
//                }
//                return offset;
//            }

            int stepTime = 10;
//            deviceLogger.debug("[read] lengthMax:"+lengthMax+"; timeOut:"+timeOut);
            int bufferLen = safeBuffer.getLen();
//            deviceLogger.debug("[read] read bufferLen=" + bufferLen + " lengthMax=" + lengthMax);
            while ((bufferLen < lengthMax) && (dis < timeOut)) {
                safeBuffer.waitRead(stepTime);
                Date end = new Date();
                dis = end.getTime() - start.getTime();
                bufferLen = safeBuffer.getLen();
            }
//            deviceLogger.debug("[read] read bufferLen=" + bufferLen + " dis=" + dis);
            int len = safeBuffer.read(outputData, lengthMax);
            deviceLogger.debug("[read] read len=" + len + " outputData=" + (outputData == null ? "null" : ISOUtils.hexString(outputData)));
            return len;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int write(byte[] inputData, int lengthMax, int timeOut) {
        try {
            if (usbSerialPort == null) {
                deviceLogger.error("[write] write usbSerialPort==null");
                return -1;
            }
            if (isWorkingSyncMode) {
                int len = usbSerialPort.syncWrite(inputData, timeOut);
                deviceLogger.debug("[write] syncWrite lengthMax=" + lengthMax + " inputData=" + (inputData == null ? "null" : ISOUtils.hexString(inputData)) + " timeOut=" + timeOut);
                return len;
            }
            deviceLogger.debug("[write] lengthMax=" + lengthMax + " inputData=" + (inputData == null ? "null" : ISOUtils.hexString(inputData)) + " timeOut=" + timeOut);
            usbSerialPort.write(inputData);
            deviceLogger.debug("[write] end.");
            return lengthMax;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public boolean clearBuffer() {
        try {
            if (usbSerialPort == null) {
                deviceLogger.error("[clearBuffer] clearBuffer usbSerialPort==null");
                return false;
            }
            deviceLogger.debug("[clearBuffer] isWorkingSyncMode=" + isWorkingSyncMode);
            if (isWorkingSyncMode) {
//                return true;
            }
            deviceLogger.debug("[clearBuffer]");
            safeBuffer.clear();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int close() {
        try {
            isOpen=false;
            synchronized (openSync) {
                if (usbSerialPort == null) {
                    deviceLogger.debug("[close] usbSerialPort==null");
                    return -1;
                }
                if (isWorkingSyncMode) {
                    deviceLogger.debug("[syncClose]");
                    usbSerialPort.syncClose();
                } else {
                    deviceLogger.debug("[close]");
                    usbSerialPort.close();
                }
                usbSerialPort = null;
                usbDevice = null;
                usbDeviceConnection = null;
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void setConfig(UsbSerialPortConfig usbSerialPortConfig) {
        deviceLogger.debug("[setConfig] value:" + usbSerialPortConfig);
        if (usbSerialPortConfig != null) {
            this.usbSerialPortConfig = usbSerialPortConfig;
            deviceLogger.debug("[setConfig] baudRate:" + usbSerialPortConfig.getBaudRate() + ", dataBits:" + usbSerialPortConfig.getDataBits()
                    + ", stopBits:" + usbSerialPortConfig.getStopBits() + ", parity:" + usbSerialPortConfig.getParity()
                    + ", flowControl:" + usbSerialPortConfig.getFlowControl());
        }
    }

    public boolean isOTGOpen() {
        boolean isOpen = OTGUtils.isOTGOpen();
        deviceLogger.debug("isOTGOpen=" + isOpen);
        return isOpen;
    }

    public boolean openOTG() {
        try {
            deviceLogger.debug("openOTG");
            OTGUtils.openOTG();
            Thread.sleep(2000);
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean closeOTG() {
        try {
            deviceLogger.debug("closeOTG");
            OTGUtils.closeOTG();
            Thread.sleep(2000);
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setWorkingSyncMode(boolean mode) {
        isWorkingSyncMode = mode;
    }

    private UsbSerialInterface.UsbReadCallback usbReadCallback = new UsbSerialInterface.UsbReadCallback() {
        @Override
        public void onReceivedData(byte[] data) {
            //deviceLogger.debug("[usbReadCallback] onReceivedData data=" + (data == null ? "null" : ISOUtils.hexString(data)));
            safeBuffer.write(data);
            safeBuffer.notifyRead();
        }
    };

    private void registerUsbReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        context.registerReceiver(usbReceiver, filter);
    }

    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
                boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                deviceLogger.debug("[usbReceiver] ACTION_USB_PERMISSION=" + granted);
                notifyPermissionConfirm();
            }
        }
    };

    private void notifyPermissionConfirm() {
        synchronized (waitPermissionConfirm) {
            waitPermissionConfirm.notify();
        }
    }

    private void waitPermissionConfirm() {
        try {
            synchronized (waitPermissionConfirm) {
                waitPermissionConfirm.wait(120 * 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isOpen() {
        return isOpen;
    }
}
