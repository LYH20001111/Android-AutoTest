package com.newland.sdk.me;

import android.content.Context;
import android.os.Bundle;

import com.newland.sdk.mtype.MposParams;
import com.newland.sdk.mtype.common.Const;
import com.newland.sdk.mtype.Device;

/**
 * device manager<p>
 * use this interface to keep the same device instance.<p>
 *
 * @since ver3.10.01
 */
public interface DeviceManager {

    public enum DeviceConnState {
        NOT_INIT,
        CONNECTING,
        CONNECTED,
        DISCONNECTING,
        DISCONNCECTED;
    }

    /**
     * Device Initialization
     *
     * @param context application context
     * @since ver3.10.01
     */
    public void init(Context context);

    /**
     * connect device<p>
     *
     * @throws Exception
     * @since ver3.10.01
     */
    public boolean connect() throws Exception;

    /**
     * get current device instance
     *
     * @return
     * @since ver3.10.01
     */
    public Device getDevice();

    /**
     * disconnect current device
     *
     * @since ver3.10.01
     */
    public void disconnect();

    /**
     * disconnect current device and destroy cached objects<p>
     *
     * @since ver3.10.01
     */
    public void destroy();

    /**
     * get device state
     *
     * @return
     * @since ver3.10.01
     */
    public DeviceConnState getDeviceConnState();

    /**
     * get the MESDK version.
     *
     * @return
     * @since ver3.10.01
     */
    public String getSDKVersion();

    /**
     * set mpos params
     * @param mposParams
     */
    public void setMposParams(MposParams mposParams);

    /**
     * Get mpos params
     */
    public MposParams getMposParams();



}
