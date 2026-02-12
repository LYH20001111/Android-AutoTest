package com.newland.sdk.module.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.newland.buildtask.R;

public class BluetoothManager {

    private Context mContext;

    private OnConnectListener mOnConnectListener;

    private static BluetoothManager instance;

    private BluetoothInterface mBluetoothInterface;

    private BluetoothManager(Context context) {
        this.mContext = context;
    }

    public static BluetoothManager getInstance(Context context) {
        if (instance == null) {
            synchronized (BluetoothManager.class) {
                instance = new BluetoothManager(context);
            }
        }
        return instance;
    }

    public void setBluetoothInterface(BluetoothInterface bluetoothInterface) {
        this.mBluetoothInterface = bluetoothInterface;
    }

//    public OnConnectListener getOnConnectListener() {
//        return mOnConnectListener;
//    }

    /**
     * 启动蓝牙界面
     */
    public void startBluetooth() {
        if (mContext == null) {
            throw new NullPointerException("Context is null");
        }
        BluetoothActivity.start(mContext, mBluetoothInterface);
    }


    /**
     * 连接蓝牙底座
     * @param name  底座名称
     * @param address  底座MAC地址
     * @param reconnect 是否跳转到蓝牙界面
     * @param listener  监听
     */
     /*
    public void connect(final String name, final String address, boolean reconnect, final OnConnectListener listener) {
        if (listener == null) {
            throw new NullPointerException("OnConnectListener not be null");
        }
        if (!TextUtils.isEmpty(address) && !BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            listener.onFailed(CommonUtils.ErrorCode.CONNECT_FAILED, mContext.getString(R.string.bt_not_enable));
            return;
        }
        mOnConnectListener = listener;

        if (reconnect || !BluetoothAdapter.getDefaultAdapter().isEnabled()) {  // 重新连接
            startBluetooth();
        } else {
            try {
                if (TextUtils.isEmpty(address)) { // 未连接，并且没传MAC地址
                    startBluetooth();
                } else {  // 未连接，传了MAC地址
                    String deviceName = mBluetoothInterface.getDeviceName();
                    String deviceAddress = mBluetoothInterface.getDeviceAddress();
                    // 已经连接
                    if (mBluetoothInterface.isConnected() && !TextUtils.isEmpty(deviceName)) {
                        if(deviceAddress.equals(address)){
                            listener.onConnected(deviceName, deviceAddress);
                            return;
                        }
                    }

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final boolean ret = mBluetoothInterface.startConnect(name, address);
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    if (ret) {
                                        listener.onConnected(name, address);
                                    } else {
                                        listener.onFailed(CommonUtils.ErrorCode.CONNECT_FAILED, mContext.getString(R.string.connect_failed));
                                    }
                                }
                            });
                        }
                    }).start();
                }
            } catch (Exception e) {
                e.printStackTrace();
                listener.onFailed(CommonUtils.ErrorCode.CONNECT_EXCEPTION, mContext.getString(R.string.connect_error));
            }
        }
    }

    public void disconnect() {
        if (mBluetoothInterface!=null && mBluetoothInterface.isConnected()) {
            mBluetoothInterface.disconnect();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (mOnConnectListener != null) {
                        mOnConnectListener.onDisconnected();
                    }
                }
            });
        }
    }
    */
}
