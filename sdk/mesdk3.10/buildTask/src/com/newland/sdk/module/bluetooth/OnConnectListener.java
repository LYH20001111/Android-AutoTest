package com.newland.sdk.module.bluetooth;

public interface OnConnectListener {

    void onConnected(String name, String address);

    void onDisconnected();

    void onFailed(int errorCode, String errorMessage);

    void onDataReceive(byte[] data);
}
