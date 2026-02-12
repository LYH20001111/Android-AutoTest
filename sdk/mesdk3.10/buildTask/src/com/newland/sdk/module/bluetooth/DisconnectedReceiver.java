package com.newland.sdk.module.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.newland.sdk.ModuleManage;
import com.newland.sdk.me.conn.SimpleDeviceManager;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class DisconnectedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(intent.getAction())) {
            Log.d("MPOS", "DisconnectedReceiver#ACTION_ACL_DISCONNECTED......");
        }
    }
}