package com.newland.nsdk.core.internal.devicestatisticsmanager;

import android.content.Context;

import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.internal.devicestatisticsmanager.DeviceStatisticsManager;

public class DeviceStatisticsManagerImpl implements DeviceStatisticsManager {
    private final static String TAG = "DeviceStatisticsManagerImpl";
    private Context mContext;
    private android.newland.os.DeviceStatisticsManager deviceStatisticsManager;
    private volatile static DeviceStatisticsManagerImpl instance;
    public static DeviceStatisticsManagerImpl getInstance(Context mContext) {
        if (instance == null) {
            synchronized (DeviceStatisticsManagerImpl.class) {
                if (instance == null || instance.mContext != mContext) {
                    instance = new DeviceStatisticsManagerImpl(mContext);
                }
            }
        } else {
            if (instance.mContext != mContext) {
                instance = new DeviceStatisticsManagerImpl(mContext);
            }
        }
        return instance;
    }

    private DeviceStatisticsManagerImpl(Context mContext) {
        this.mContext = mContext;
        deviceStatisticsManager = android.newland.os.DeviceStatisticsManager.getInstance(mContext);

    }
    @Override
    public String getDeviceStatisticsInfo() throws NSDKException {
        String info = deviceStatisticsManager.getDeviceStatisticsInfo();
        if (info == null) {
            throw new NSDKException("Failed to get device statistics Info.");
        }
        return info;
    }

    @Override
    public String getDeviceStatisticsInfoByTag(String tag) throws NSDKException {
        if (tag == null) {
            throw new NSDKIllegalParameterException("Tag shall not be null.");
        }
        String info = deviceStatisticsManager.getDeviceStatisticsInfo(tag);
        if (info == null) {
            throw new NSDKException("Failed to get device statistics info by tag.");
        }
        return info;
    }
}
