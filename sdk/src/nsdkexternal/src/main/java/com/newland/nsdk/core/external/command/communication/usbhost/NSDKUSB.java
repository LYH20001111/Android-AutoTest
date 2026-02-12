package com.newland.nsdk.core.external.command.communication.usbhost;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.common.utils.LogUtils;

import java.util.Date;
import java.util.HashMap;

/**
 * Author by bxy, Date on 2019/11/21.
 */
public class NSDKUSB {
    private static final String TAG = "NSDKUSB";

    private Context context;
    private UsbManager usbManager;
    private UsbDevice usbDevice;
    private UsbDeviceConnection usbDeviceConnection;
    private UsbSerialDevice usbSerialPort;
    private USBSafeBuffer safeBuffer;
    private Object openSync = new Object();
    private Object waitPermissionConfirm = new Object();
    private static final String ACTION_USB_PERMISSION = "com.newland.sdk.USB_PERMISSION";

    public NSDKUSB(Context context) {
        this.context = context;
        usbSerialPort = null;
        safeBuffer = new USBSafeBuffer();
        usbManager = (UsbManager) this.context.getSystemService(Context.USB_SERVICE);
    }

    public HashMap<String, UsbDevice> getDeviceMap() {
        usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        return usbManager.getDeviceList();
    }

    public int open(UsbDevice mUsbDevice) {
        if (mUsbDevice == null) {
            LogUtils.d(TAG, ">>>mUsbDevice == null");
            return -1;
        }

        synchronized (openSync) {
            if (usbSerialPort != null) {
                LogUtils.d(TAG, ">>>usbSerialPort already open. " + usbSerialPort);
                usbSerialPort.close();
                usbSerialPort = null;
                usbDevice = null;
                usbDeviceConnection = null;
                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            safeBuffer.clear();
            usbDevice = mUsbDevice;

            boolean isSupported = UsbSerialDevice.isSupported(usbDevice);
            if (!isSupported) {
                LogUtils.d(TAG, ">>>UsbDevice is not Supported");
                return -1;
            }
            LogUtils.d(TAG, ">>>Permission=" + usbManager.hasPermission(usbDevice));
            if (!usbManager.hasPermission(usbDevice)) {
                registerUsbReceiver();
                LogUtils.e(TAG, ">>>requestPermission");
                PendingIntent mPendingIntent = PendingIntent.getBroadcast(context, 0,
                        new Intent(ACTION_USB_PERMISSION), 0);
                usbManager.requestPermission(usbDevice, mPendingIntent);
                waitPermissionConfirm();
                context.unregisterReceiver(usbReceiver);
            }

            LogUtils.d(TAG, ">>>hasPermission=" + usbManager.hasPermission(usbDevice));
            if (!usbManager.hasPermission(usbDevice)) {
                LogUtils.e(TAG, ">>>usbManager.has not Permission");
                return -1;
            }
            LogUtils.d(TAG, ">>>findUSBDevice succ.");

            usbDeviceConnection = usbManager.openDevice(usbDevice);
            usbSerialPort = UsbSerialDevice.createUsbSerialDevice(usbDevice, usbDeviceConnection);
            if (usbSerialPort == null) {
                LogUtils.d(TAG, ">>>usbSerialPort==null return.");
                return -1;
            }
            boolean open = usbSerialPort.open();
            if (!open) {
                usbSerialPort = null;
                LogUtils.d(TAG, ">>>usbSerialPort open fail");
                return -1;
            }
            usbSerialPort.setBaudRate(115200);
            usbSerialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
            usbSerialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
            usbSerialPort.setParity(UsbSerialInterface.PARITY_NONE);
            usbSerialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
            usbSerialPort.read(usbReadCallback);
            LogUtils.d(TAG, ">>>openUSBDevice succ.");
            return 0;
        }
    }

    public int read(byte[] outputData, int lengthMax, int timeOut) {
        if (usbSerialPort == null) {
            LogUtils.d(TAG, ">>>read usbSerialPort==null");
            return -1;
        }
        long dis = 0;
        int bufferLen = safeBuffer.getLen();
//        deviceLogger.debug(">>>read bufferLen=" + bufferLen + " lengthMax=" + lengthMax);
        Date start = new Date();
        while ((bufferLen < lengthMax) && (dis < timeOut)) {
            safeBuffer.waitRead(timeOut);
            Date end = new Date();
            dis = end.getTime() - start.getTime();
//            deviceLogger.debug(">>>read bufferLen=" + bufferLen + " dis=" + dis);
            bufferLen = safeBuffer.getLen();
        }
        int len = safeBuffer.read(outputData, lengthMax);
//        if(len > 0){
//            LogUtils.d(TAG, ">>>read len="+len+" outputData=" + (outputData == null ? "null" : ISOUtils.hexString(outputData)));
//        }
        return len;
    }

    public int write(byte[] inputData, int lengthMax, int timeOut) {
        if (usbSerialPort == null) {
            LogUtils.d(TAG, ">>>write usbSerialPort==null");
            return -1;
        }
        LogUtils.d(TAG, ">>>write lengthMax=" + lengthMax + " inputData=" + (inputData == null ? "null" : ISOUtils.hexString(inputData)));
        usbSerialPort.write(inputData);
        LogUtils.d(TAG, ">>>write end.");
        return lengthMax;
    }

    public boolean clearBuffer() {
        if (usbSerialPort == null) {
            LogUtils.d(TAG, ">>>clearBuffer usbSerialPort==null");
            return false;
        }
        LogUtils.d(TAG, ">>>clearBuffer.");
        safeBuffer.clear();
        return true;
    }

    public int close() {
        synchronized (openSync) {
            if (usbSerialPort == null) {
                LogUtils.d(TAG, ">>>close usbSerialPort==null");
                return -1;
            }
            usbSerialPort.close();
            usbSerialPort = null;
            usbDevice = null;
            usbDeviceConnection = null;
            return 0;
        }
    }

    private UsbSerialInterface.UsbReadCallback usbReadCallback = new UsbSerialInterface.UsbReadCallback() {
        @Override
        public void onReceivedData(byte[] data) {
            LogUtils.e(TAG, ">>>onReceivedData ::"+ISOUtils.hexString(data));
            safeBuffer.write(data);
            safeBuffer.notifyRead();
        }
    };

    private void registerUsbReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        context.registerReceiver(usbReceiver, filter);
        LogUtils.e(TAG, ">>>Register USB receiver.");
    }

    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            LogUtils.d(TAG, ">>>Receive broadcast, ACTION_USB_PERMISSION");
            if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
                boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                LogUtils.d(TAG, ">>>ACTION_USB_PERMISSION=" + granted);
                notifyPermissionConfirm();
            }
        }
    };

    private void notifyPermissionConfirm() {
        synchronized (waitPermissionConfirm) {
            waitPermissionConfirm.notify();
            LogUtils.d(TAG, ">>>Nofity permission confirm.");
        }
    }

    private void waitPermissionConfirm() {
        LogUtils.d(TAG, ">>>Wait for permission confirm.");
        try {
            synchronized (waitPermissionConfirm) {
                waitPermissionConfirm.wait(120 * 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
