package com.newland.sdk.module.bluetooth;

public interface BluetoothInterface {

    String getDeviceName();

    String getDeviceAddress();

    boolean isConnected();

    boolean startConnect(String name, String address);

    void disconnect();
}
