package com.newland.nsdk.core.external.command.communication.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.external.communication.CommunicatorListener;
import com.newland.nsdk.core.api.external.communication.ExternalCommunicatorState;
import com.newland.nsdk.core.api.external.communication.NSDKCommunicator;
import com.newland.nsdk.core.api.common.exception.NSDKCommunicationException;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.external.command.exception.ExternalErrorMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author hlh
 * @date 2020/8/12
 */
public class BLECommunicator extends BluetoothGattCallback implements NSDKCommunicator {
    private static BLECommunicator instance;
    private static final String TAG = "BLECommunicator";
    private Context context;
    private BluetoothDevice device;
    private final String SERVICEUUID = "49535343-fe7d-4ae5-8fa9-9fafd205e455";
    private final String READUUID = "49535343-1E4D-4BD9-BA61-23C647249616";
    private final String SEDNUUID = "49535343-8841-43F4-A8D4-ECBE34729BB3";
    private CommunicatorListener listener;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCharacteristic sendCharacteristic;
    private BluetoothGattCharacteristic readCharacteristic;
    private BluetoothGattService service;
    private Object sendDataLock = new Object();
    private Object receiveDataLock = new Object();
    private Object openLock = new Object();
    private ByteArrayOutputStream receiveDataStream = new ByteArrayOutputStream();
    private volatile int openTimeout;
    private final int DEFAULT_MTU = 512;
    private int mMtu = DEFAULT_MTU;
    private boolean isMtuSucc = false;

    private BLECommunicator(Context context, CommunicatorListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public static BLECommunicator getInstance(Context context, CommunicatorListener listener) {
        if (instance == null) {
            synchronized (BLECommunicator.class) {
                if (instance == null) {
                    instance = new BLECommunicator(context, listener);
                }
            }
        } else {
            if (context != instance.context || listener != instance.listener) {
                instance = new BLECommunicator(context, listener);
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

        device = listener.onBluetoothList(devices);
        if (device == null) {
            throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_BLUETHOOTH_DEVICE_NOT_CHOSEN, "No bluetooth device selected.");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mBluetoothGatt = device.connectGatt(context, false, this, BluetoothDevice.TRANSPORT_LE);
        } else {
            mBluetoothGatt = device.connectGatt(context, false, this);
        }

        this.openTimeout = timeout;
        waitConnectionNotify();
        if (!isConnected()) {
            if (mBluetoothGatt != null) {
                mBluetoothGatt.close();
            }
            
            release();
            throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_OPEN_ERROR, "Failed to open.");
        }

        // 连接上以后延时 3s，让外设有时间进入准备好的状态。
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close(int timeout) throws NSDKException {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
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

        release();
    }

    private void release() {
        device = null;
        mBluetoothGatt = null;
        service = null;
        sendCharacteristic = null;
        readCharacteristic = null;
        receiveDataStream.reset();
        isMtuSucc = false;
    }

    @Override
    public boolean isConnected() {
        if (device == null || readCharacteristic == null || sendCharacteristic == null) {
            return false;
        }

        return BluetoothUtils.isConnected(device);
    }

    /**
     * @param data
     * @param timeout Unit: ms
     * @throws NSDKException
     */
    @Override
    public synchronized void send(byte[] data, int timeout) throws NSDKException {
        if (sendCharacteristic == null) {
            throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_BLUETHOOTH_DISCONNECTED, ExternalErrorMessage.BLUETHOOTH_DISCONNECTED);
        }
        LogUtils.d(TAG, ">>>> BLE send data:" + ISOUtils.hexString(data));
        int singlePackLen = 512;
        if (data.length <= singlePackLen) {
            sendCharacteristic.setValue(data);
            mBluetoothGatt.writeCharacteristic(sendCharacteristic);
            try {
                synchronized (sendDataLock) {
                    sendDataLock.wait(timeout);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            List<byte[]> dataList = splitData(data, singlePackLen);
            for (byte[] d : dataList) {
                sendCharacteristic.setValue(d);
                mBluetoothGatt.writeCharacteristic(sendCharacteristic);
                try {
                    synchronized (sendDataLock) {
                        sendDataLock.wait(timeout);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private List<byte[]> splitData(byte[] data, int singlePackLen) {
        List<byte[]> dataList = new ArrayList<>();
        int totalLen = data.length;
        int offset = 0;
        byte[] tempBuf;
        int tempBufLen;
        while (offset < totalLen) {
            if (totalLen - offset >= singlePackLen) {
                tempBufLen = singlePackLen;
            } else {
                tempBufLen = totalLen - offset;
            }
            tempBuf = new byte[tempBufLen];
            System.arraycopy(data, offset, tempBuf, 0, tempBufLen);
            dataList.add(tempBuf);
            offset += tempBufLen;
        }
        return dataList;
    }

    /**
     * @param timeout Unit: ms
     * @return
     * @throws NSDKException
     */
    @Override
    public synchronized byte[] receive(int timeout) throws NSDKException {
        if (readCharacteristic == null) {
            throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_BLUETHOOTH_DISCONNECTED, ExternalErrorMessage.BLUETHOOTH_DISCONNECTED);
        }

        long startTime = System.currentTimeMillis();
        long remainTime;
        do {
            byte[] buf;
            synchronized (receiveDataLock) {
                buf = receiveDataStream.toByteArray();
                receiveDataStream.reset();
            }
            if (buf != null && buf.length > 0) {
                LogUtils.d(TAG, ">>>> BLE receive data:" + ISOUtils.hexString(buf));
                return buf;
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
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        LogUtils.d(TAG, ">>>> onConnectionStateChange  status:" + status + ", newState:" + newState);

        if (status == BluetoothGatt.GATT_SUCCESS) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !isMtuSucc) {
                    gatt.requestMtu(mMtu);
                }
                boolean ret;
                int i = 0;

                // 连接上以后延时 600ms 再启动服务发现
                try {
                    Thread.sleep(600);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                do {
                    ret = mBluetoothGatt.discoverServices();
                    if (ret) {
                        LogUtils.d(TAG, ">>>> onConnectionStateChange start to discover services successfully.");
                        break;
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i ++;
                } while (i < 3);

                if (!ret) {
                    LogUtils.d(TAG, ">>>> onConnectionStateChange failed to discover services.");
                    if (mBluetoothGatt != null) {
                        mBluetoothGatt.close();
                    }
                    return;
                }

                listener.onConnectedStateChange(ExternalCommunicatorState.CONNECTED);
            } else if (newState == BluetoothProfile.STATE_CONNECTING) {
                listener.onConnectedStateChange(ExternalCommunicatorState.CONNECTTING);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                listener.onConnectedStateChange(ExternalCommunicatorState.DISCONNECTED);
                if (mBluetoothGatt != null) {
                    mBluetoothGatt.close();
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTING) {
                listener.onConnectedStateChange(ExternalCommunicatorState.DISCONNECTTING);
            }
        } else {
            if (mBluetoothGatt != null) {
                mBluetoothGatt.close();
            }
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);
        LogUtils.d(TAG, ">>>> onServicesDiscovered, status: " + status);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            service = gatt.getService(UUID.fromString(SERVICEUUID));
            sendCharacteristic = service.getCharacteristic(UUID.fromString(SEDNUUID));
            readCharacteristic = service.getCharacteristic(UUID.fromString(READUUID));
            gatt.setCharacteristicNotification(readCharacteristic, true);
            notifyConnected();
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
        synchronized (sendDataLock) {
            sendDataLock.notify();
        }
        LogUtils.d(TAG, ">>>> onCharacteristicWrite ");
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicRead(gatt, characteristic, status);

        LogUtils.d(TAG, ">>>> onCharacteristicRead ");
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
        byte[] data = characteristic.getValue();
        LogUtils.d(TAG, ">>>> onCharacteristicChanged:" + ISOUtils.hexString(data));
        synchronized (receiveDataLock) {
            try {
                receiveDataStream.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
        super.onMtuChanged(gatt, mtu, status);
        //当设置的mtu和默认的mtu不一致时，将蓝牙返回的mtu设置到底层，解决不同的手机版本直接mtu不同的问题
        if(mMtu != mtu){
            mMtu = mtu;
            gatt.requestMtu(mMtu);
        }else {
            isMtuSucc = true;
        }
    }

    private void waitConnectionNotify() {
        LogUtils.d(TAG, ">>>Wait for connected...");
        try {
            synchronized (openLock) {
                openLock.wait(this.openTimeout);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void notifyConnected() {
        synchronized (openLock) {
            openLock.notify();
            LogUtils.d(TAG, ">>>Notify connected.");
        }
    }
}
