package com.newland.sdk.module.bluetooth;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class CommonUtils {

    public class ErrorCode {
        public static final int BLUETOOTH_NOT_ENABLE = -1;  // 蓝牙未打开
        public static final int CONNECT_FAILED = -2;  // 连接失败
        public static final int CONNECT_EXCEPTION = -3;
    }

    private static String oldMsg;
    private static Toast toast = null;
    private static long oneTime = 0;
    private static long twoTime = 0;

    public static void showToast(final Context context, final String s) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (toast == null) {
                    toast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
                    toast.show();
                    oneTime = System.currentTimeMillis();
                } else {
                    twoTime = System.currentTimeMillis();
                    if (s.equals(oldMsg)) {
                        if (twoTime - oneTime > Toast.LENGTH_SHORT) {
                            toast.show();
                        }
                    } else {
                        oldMsg = s;
                        toast.setText(s);
                        toast.show();
                    }
                    oneTime = twoTime;
                }
            }
        });
    }

    private static final int MIN_CLICK_DELAY_TIME = 500;
    private static long lastClickTime;

    public static boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }
} 