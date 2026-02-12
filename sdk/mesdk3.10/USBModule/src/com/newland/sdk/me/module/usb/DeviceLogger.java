package com.newland.sdk.me.module.usb;

import android.util.Log;

import com.newland.sdk.ModuleManage;

public class DeviceLogger {
    private String TAG;

    public DeviceLogger(String tag){
        TAG = tag;
    }

    public void debug(String msg){
        if(ModuleManage.isDebug){
            Log.d(TAG,msg);
        }
    }

    public void error(String msg){
        Log.e(TAG,msg);
    }

}
