package com.newland.sdk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.newland.sdk.ModuleManage;

public class AppReceiver extends BroadcastReceiver {

    private static final String TAG = "MESDKLOG";

    public static final String ACTION_INSTALL = "android.intent.action.INSTALL_APP_HIDE";

    public static final String ACTION_UNINSTALL = "android.intent.action.DELETE_APP_HIDE";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (ACTION_INSTALL.equalsIgnoreCase(intent.getAction())) {
                int errorCode = Integer.parseInt(intent.getStringExtra("respCode"));
                String packageName = intent.getStringExtra("packageName");
                Log.d(TAG, "AppReceiver Action=" + intent.getAction() + ", respCode=" + errorCode + ", packageName=" + packageName);
                if (errorCode == 0) {
                    if (CommonUtils.PACKAGE_NAME.equalsIgnoreCase(packageName)) {
                        if (CommonUtils.isOpenLog(context)) {
                            Log.i(TAG, "open log");
                            ModuleManage.getInstance().setDebugMode(true);
                        }
                    }
                }
            } else if (ACTION_UNINSTALL.equalsIgnoreCase(intent.getAction())) {
                int errorCode = Integer.parseInt(intent.getStringExtra("respCode"));
                String packageName = intent.getStringExtra("packageName");
                Log.d(TAG, "AppReceiver Action=" + intent.getAction() + ", respCode=" + errorCode + ", packageName=" + packageName);
                if (errorCode == 0) {
                    if (CommonUtils.PACKAGE_NAME.equalsIgnoreCase(packageName)) {
                        Log.i(TAG, "close log");
                        ModuleManage.getInstance().setDebugMode(false);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
