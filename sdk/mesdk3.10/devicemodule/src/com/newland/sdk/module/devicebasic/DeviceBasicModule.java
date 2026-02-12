package com.newland.sdk.module.devicebasic;

import com.newland.sdk.mtype.Module;
import com.newland.sdk.utils.TLVPackage;

import java.util.Date;
import java.util.Locale;

public interface DeviceBasicModule extends Module {

    /**
     * Get the device information<p>
     *
     * @return Current device information
     */
    public DeviceInfo getDeviceInfo();

    /**
     * Get a TUSN number
     *
     * @return Device tusn
     * @since V3.10.01
     */
    public String getTusn();

    /**
     * Set CSN<p>
     *
     * @param csn Customer serial number
     * @since V3.10.01
     */
    public void setCSN(String csn);

    /**
     * Set the device inner time
     *
     * @param date
     */
    public void setDeviceDate(Date date);

    /**
     * Get the current device time
     *
     * @return Current device time
     */
    public Date getDeviceDate();

    /**
     * Set the terminal parameters
     *
     * @param tlvPackage
     * @since V3.10.01
     */
    public void setDeviceParams(TLVPackage tlvPackage);


    /**
     * Get a terminal parameter list
     *
     * @return Terminal parameter list
     * @since V3.10.01
     */
    public TLVPackage getDeviceParams(int... tags);

    /*
     * Get a line protection random number
     *
     * @param len The length of the needed random number.
     * @return A byte of the specified length
     * @since V3.10.01
     */
    public byte[] getRandom(int len);

    /**
     * Device soft reset <p>
     */
    public void reset();

    /**
     * get sdk version
     *
     * @return
     */
    public String getSDKVersion();

    /**
     * Whether the terminal has a security module.
     *
     * @return yes if success, false if no.
     */
    public boolean hasSecurityModule();

    /**
     * Obtain battery health status
     * @return
     * null, The device does not support
     * GREEN: Normal
     * YELLOW: There is an abnormality, it is recommended to replace it
     * RED: There is an abnormality, please replace it as soon as possible
     */
    public String getBatteryHealthStatus();
}
