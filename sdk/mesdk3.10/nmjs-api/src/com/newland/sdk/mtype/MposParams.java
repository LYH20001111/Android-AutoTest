package com.newland.sdk.mtype;

import java.util.PropertyResourceBundle;

/**
 * @Description
 * @Author wuhh
 * @Date 2021/3/2
 */
public class MposParams {
    private ConnectType mConnectType;
    private String mName;
    private String mAddress;

    public MposParams(){
        mConnectType = ConnectType.BLUETOOTH;
    }

    public enum ConnectType{
        BLUETOOTH,USB
    }

    public ConnectType getConnectType() {
        return mConnectType;
    }

    public void setConnectType(ConnectType connectType) {
        mConnectType = connectType;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        this.mAddress = address;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }
}
