package com.hudou.autotest.util;

import android.os.Build;

public class DeviceUtils {

    /**
     * 判断当前设备型号是否在不支持列表中
     *
     * @param unsupportedDevices 不支持的设备型号列表，支持填写 Build.MODEL（如 SM-G9880）
     *                           或 Build.MANUFACTURER + " " + Build.MODEL（如 samsung SM-G9880），忽略大小写匹配
     * @return true 表示当前设备不支持
     */
    public static boolean isDeviceUnsupported(String[] unsupportedDevices) {
        if (unsupportedDevices == null || unsupportedDevices.length == 0) {
            return false;
        }
        String model = Build.MODEL;
        String manufacturerModel = Build.MANUFACTURER + " " + Build.MODEL;
        for (String device : unsupportedDevices) {
            if (device != null && (device.equalsIgnoreCase(model) || device.equalsIgnoreCase(manufacturerModel))) {
                return true;
            }
        }
        return false;
    }
}
