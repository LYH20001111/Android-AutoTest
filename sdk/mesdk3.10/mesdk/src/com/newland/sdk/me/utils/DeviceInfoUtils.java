package com.newland.sdk.me.utils;

import android.util.Log;

import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;

import java.lang.reflect.Method;

/**
 * Author by bxy, Date on 2019/1/9 0009.
 */
public class DeviceInfoUtils {
    private static DeviceLogger logger = DeviceLoggerFactory.getLogger(DeviceInfoUtils.class);

    private static String getSysProperty(String key, String defaultValue) {
        String value = defaultValue;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method method = c.getMethod("get", String.class);
            value = (String) (method.invoke(c, key));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public static boolean getHasSecModule() {
        boolean hasSecModule = true;
        if (getSysProperty("persist.sys.HasSecModule", "yes").equals("no")) {
            hasSecModule = false;
        }
        logger.debug("SDK DeviceInfoUtils:" + ">>>hasSecModule=" + hasSecModule);
        return hasSecModule;
    }
}
