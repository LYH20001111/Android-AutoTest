package com.newland.nsdk.core.external.command.communication.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.external.communication.CommunicatorListener;
import com.newland.nsdk.core.api.external.communication.ExternalCommunicatorState;
import com.newland.nsdk.core.api.external.communication.NSDKCommunicator;
import com.newland.nsdk.core.api.common.exception.NSDKCommunicationException;
import com.newland.nsdk.core.external.command.communication.CommunicatorExtension;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class BluetoothCommunicator implements NSDKCommunicator, CommunicatorExtension {
    public static final String TAG = "BluetoothCommunicator";
    private Context context;
    private CommunicatorListener listener;
    private static BluetoothCommunicator instance;
    private BluetoothSocket socket;
    private OutputStream outStream;
    private InputStream inputStream;
    public static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private BluetoothCommunicator(Context context, CommunicatorListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public static BluetoothCommunicator getInstance(Context context, CommunicatorListener listener) {
        if (instance == null) {
            synchronized (BluetoothCommunicator.class) {
                if (instance == null) {
                    instance = new BluetoothCommunicator(context, listener);
                }
            }
        } else {
            if (context != instance.context || listener != instance.listener) {
                instance = new BluetoothCommunicator(context, listener);
            }
        }

        return instance;
    }

    @Override
    public synchronized void open(int timeout) throws NSDKException {
        LogUtils.d(TAG, ">>>> open");
        ArrayList<BluetoothDevice> devices = BluetoothUtils.getInstance().getBondedDevices();
        if (devices.size() == 0) {
            throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_BLUETHOOTH_NOT_PAIRED, "No device paired.");
        }

        BluetoothDevice device = listener.onBluetoothList(devices);
        if (device == null) {
            throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_BLUETHOOTH_DEVICE_NOT_CHOSEN, "No bluetooth device selected.");
        }

        try {
           socket = device.createRfcommSocketToServiceRecord(SPP_UUID);
        } catch (IOException e) {
            throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_OPEN_ERROR, "Failed to open bluetooth socket.", e);
        }

        listener.onConnectedStateChange(ExternalCommunicatorState.CONNECTTING);
        try {
            socket.connect();
            outStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_OPEN_ERROR, "Failed to open bluetooth socket.", e);
        }

        long startTime = System.currentTimeMillis();
        boolean isConnected = false;
        while (timeout - (System.currentTimeMillis() - startTime) > 0) {
            if (isConnected()) {
                isConnected = true;
                break;
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (!isConnected) {
            throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_OPEN_ERROR, "Failed to open.");
        }

        listener.onConnectedStateChange(ExternalCommunicatorState.CONNECTED);
        // 连接上以后延时 3s，让外设有时间进入准备好的状态。
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close(int timeout) throws NSDKException {
        if (socket != null) {
            listener.onConnectedStateChange(ExternalCommunicatorState.DISCONNECTTING);
            try {
                socket.close();
            } catch (Exception e) {
                throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_CLOSE_ERROR, "Failed to close bluetooth socket.", e);
            }

            long startTime = System.currentTimeMillis();
            boolean disconnected = false;
            while (timeout - (System.currentTimeMillis() - startTime) > 0) {
                if (!isConnected()) {
                    disconnected = true;
                    break;
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (!disconnected) {
                throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_CLOSE_ERROR, "Failed to close.");
            }

            listener.onConnectedStateChange(ExternalCommunicatorState.DISCONNECTED);
            release();
        }
    }

    private void release() {
        socket = null;
        outStream = null;
        inputStream = null;
    }

    @Override
    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    @Override
    public synchronized void send(byte[] data, int timeout) throws NSDKException {
        if (outStream == null) {
            throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_RECEIVE_ERROR, "Output stream is null.");
        }

        LogUtils.d(TAG, ">>>> Bluetooth socket send data:" + ISOUtils.hexString(data));
        try {
            outStream.write(data);
        } catch (IOException e) {
            throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_SEND_ERROR, "Failed to send data to bluetooth socket.", e);
        }
    }

    @Override
    public synchronized byte[] receive(int timeout) throws NSDKException {
        if (inputStream == null) {
            throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_RECEIVE_ERROR, "Input stream is null.");
        }

        long startTime = System.currentTimeMillis();
        long remainTime;
        do {
            byte[] buffer = new byte[1024];
            int len;
            try {
                len = inputStream.available();
                if (len > 0) {
                    len = inputStream.read(buffer);
                }
            } catch (IOException e) {
                throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_RECEIVE_ERROR, "Failed to read data from bluetooth socket.", e);
            }
            byte[] data = Arrays.copyOf(buffer, len);
            if (data != null && data.length > 0) {
                LogUtils.d(TAG, ">>>> Bluetooth socket receive data:" + ISOUtils.hexString(data));
                return data;
            }

            remainTime = timeout - (System.currentTimeMillis() - startTime);
        } while (remainTime > 0);

        return null;
    }

    @Override
    public void clear() throws NSDKException {

    }

    @Override
    public void setCommunicationTimeout(int sendTimeout, int receiveTimeout) {
        ExternalCommunicationManager.getInstance().setSendTimeout(sendTimeout);
        ExternalCommunicationManager.getInstance().setReceiveTimeout(receiveTimeout);
    }

    @Override
    public void sendInterrupt(byte[] data, int timeout) throws NSDKException {
        if (outStream == null) {
            throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_RECEIVE_ERROR, "Output stream is null.");
        }

        LogUtils.d(TAG, ">>>> Bluetooth socket send data:" + ISOUtils.hexString(data));
        try {
            outStream.write(data);
        } catch (IOException e) {
            throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_SEND_ERROR, "Failed to send data to bluetooth socket.", e);
        }
    }
}
