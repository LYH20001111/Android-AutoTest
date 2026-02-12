package com.newland.nsdk.core.api.internal.devicestatisticsmanager;

import com.newland.nsdk.core.api.common.Module;
import com.newland.nsdk.core.api.common.exception.NSDKException;

public interface DeviceStatisticsManager extends Module {

    /**
     * Gets device statistics info.
     * @return Device statistics Info in xml format.
     * @throws NSDKException
     */
    String getDeviceStatisticsInfo() throws NSDKException;

    /**
     * Gets classified device statistics info by tag.
     * @param tag Classified Info tag.
     * @return Classified device statistics info.
     * @throws NSDKException
     */
    String getDeviceStatisticsInfoByTag(String tag) throws NSDKException;
}
