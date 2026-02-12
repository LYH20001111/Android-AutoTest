package com.newland.nsdk.core.external.command.communication.usbhost;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.util.Log;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.external.communication.CommunicatorListener;
import com.newland.nsdk.core.api.external.communication.ExternalCommunicatorState;
import com.newland.nsdk.core.api.external.communication.NSDKCommunicator;
import com.newland.nsdk.core.api.common.exception.NSDKCommunicationException;
import com.newland.nsdk.core.external.command.communication.CommunicatorExtension;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.external.command.exception.ExternalErrorMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author hlh
 * @date 2020/7/23
 */
public class USBCommunicator implements NSDKCommunicator, CommunicatorExtension {
    private static final String TAG = "SerialPort";

    private static USBCommunicator serialPort;
    private final int MAX_SIZE = 1024 * 2;
    private NSDKUSB usb;
    private volatile boolean isConnected = false;
    private ExternalCommunicatorState state;
    private CommunicatorListener listener;
    private Context context;
    private Object sendObj = new Object(), receiveObj = new Object();

    private USBCommunicator(Context context, CommunicatorListener listener) {
        if (usb == null) {
            usb = new NSDKUSB(context);
        }
        this.listener = listener;
        this.context = context;
    }

    public static USBCommunicator getInstance(Context context, CommunicatorListener listener) {
        if (serialPort == null) {
            synchronized (USBCommunicator.class) {
                if (serialPort == null) {
                    serialPort = new USBCommunicator(context, listener);
                }
            }
        } else {
            if (listener != serialPort.listener || context != serialPort.context) {
                serialPort = new USBCommunicator(context, listener);
            }
        }
        return serialPort;
    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public void open(int timeout) throws NSDKException {
        if (isConnected) {
            return;
        }
        state = ExternalCommunicatorState.CONNECTTING;
        listener.onConnectedStateChange(ExternalCommunicatorState.CONNECTTING);

        if (openUsb()) {
            isConnected = true;
            state = ExternalCommunicatorState.CONNECTED;
            listener.onConnectedStateChange(ExternalCommunicatorState.CONNECTED);
            return;
        }
        throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_OPEN_ERROR, ExternalErrorMessage.FAILED_TO_OPEN_EXTERNAL_DEVICE);
    }

    private boolean openUsb() {
        int ret;
        HashMap<String, UsbDevice> deviceMap = usb.getDeviceMap();
        Log.d(TAG, "openUsb open deviceMap.size :" + deviceMap.size());
        if (deviceMap.size() == 0) {
            return false;
        }
        Set<Map.Entry<String, UsbDevice>> entrySet = deviceMap.entrySet();
        UsbDevice usbDevice;
        for (Map.Entry entry : entrySet) {
            usbDevice = (UsbDevice) entry.getValue();
            ret = usb.open(usbDevice);
            Log.d(TAG, entry.getKey() + ">>>" + usbDevice.getDeviceName() + "openUsb open ret:" + ret);
            if (ret >= 0) {
                return true;
            }
        }

        return false;
    }

    /**
     *
     */
    private void reconnect() {
        if (openUsb()) {
            isConnected = true;
            return;
        }

        isConnected = false;
        state = ExternalCommunicatorState.DISCONNECTED;
        listener.onConnectedStateChange(ExternalCommunicatorState.DISCONNECTED);
    }

    @Override
    public void close(int timeout) throws NSDKException {
        if (!isConnected) {
            return;
        }

        state = ExternalCommunicatorState.DISCONNECTTING;
        listener.onConnectedStateChange(ExternalCommunicatorState.DISCONNECTTING);
        int ret;
        ret = usb.close();

        if (ret < 0) {
            throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_CLOSE_ERROR, ExternalErrorMessage.FAILED_TO_CLOSE_EXTERNAL_DEVICE);
        }

        state = ExternalCommunicatorState.DISCONNECTED;
        listener.onConnectedStateChange(ExternalCommunicatorState.DISCONNECTED);
        isConnected = false;
    }

    @Override
    public synchronized void send(byte[] data, int timeout) throws NSDKException {
        if (state == ExternalCommunicatorState.DISCONNECTED || state == ExternalCommunicatorState.DISCONNECTED) {
            throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_SEND_ERROR, ExternalErrorMessage.FAILED_TO_SEND_DATA);
        }

        int ret;
        ret = usb.write(data, data.length, timeout);

        if (ret < 0) {
            isConnected = false;
            listener.onConnectedStateChange(ExternalCommunicatorState.DISCONNECTED);
            throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_SEND_ERROR, ExternalErrorMessage.FAILED_TO_SEND_DATA);
        }
    }

    @Override
    public synchronized byte[] receive(int timeout) throws NSDKException {
        if (state == ExternalCommunicatorState.DISCONNECTED || state == ExternalCommunicatorState.DISCONNECTED) {
            throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_SEND_ERROR, ExternalErrorMessage.FAILED_TO_SEND_DATA);
        }

        long startTime = System.currentTimeMillis();
        long remainTime;
        do {
            byte[] buf = new byte[MAX_SIZE];
            int ret;
            ret = usb.read(buf, MAX_SIZE, 0);
            if (ret > 0) {
                byte[] res = new byte[ret];
                System.arraycopy(buf, 0, res, 0, ret);
                Log.d(TAG, getClass().getName() + ">> read data buf:" + ISOUtils.hexString(res));
                return res;
            }
            remainTime = timeout - (System.currentTimeMillis() - startTime);
        } while (remainTime > 0);

        return null;
    }

    @Override
    public void clear() throws NSDKException {
        if (state == ExternalCommunicatorState.DISCONNECTED || state == ExternalCommunicatorState.DISCONNECTED) {
            throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_SEND_ERROR, ExternalErrorMessage.FAILED_TO_SEND_DATA);
        }
        boolean isSucc = usb.clearBuffer();
        if(!isSucc) {
            throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_ERROR, "Fail to clear Buffer.");
        }
    }

    @Override
    public void setCommunicationTimeout(int sendTimeout, int receiveTimeout) {
        ExternalCommunicationManager.getInstance().setSendTimeout(sendTimeout);
        ExternalCommunicationManager.getInstance().setReceiveTimeout(receiveTimeout);
    }

    @Override
    public void sendInterrupt(byte[] data, int timeout) throws NSDKException {
        if (state == ExternalCommunicatorState.DISCONNECTED || state == ExternalCommunicatorState.DISCONNECTED) {
            throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_SEND_ERROR, ExternalErrorMessage.FAILED_TO_SEND_DATA);
        }

        int ret;
        ret = usb.write(data, data.length, timeout);

        if (ret < 0) {
            isConnected = false;
            listener.onConnectedStateChange(ExternalCommunicatorState.DISCONNECTED);
            throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_SEND_ERROR, ExternalErrorMessage.FAILED_TO_SEND_DATA);
        }
    }
}
