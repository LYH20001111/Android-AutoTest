package com.newland.sdk.mtype;


import java.util.Date;
import java.util.Locale;

import com.newland.sdk.module.devicebasic.DeviceInfo;
import com.newland.sdk.utils.TLVPackage;


/**
 * Device description interface<p>
 */
public interface Device {

    /**
     * Get the device information<p>
     *
     * @return Current device information
     */
    public DeviceInfo getDeviceInfo();

    /**
     * Get the current device time
     *
     * @return Current device time
     * @throws UnsupportedOperationException Throw the exception when this method is not supported
     */
    public Date getDeviceDate();

    /**
     * Set the device inner time
     *
     * @param date
     * @throws UnsupportedOperationException Throw the exception when this method is not supported
     */
    public void setDeviceDate(Date date);

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

    /**
     * Get all the supported module types<p>
     *
     * @return Module type
     */
    public ModuleType[] getSupportStandardModule();

    /**
     * Get the module-corresponding operation object<p>
     *
     * @param moduleType Module type{@link ModuleType}
     * @return
     */
    public Module getStandardModule(ModuleType moduleType);

    /**
     * Get the supported extended device module<p>
     *
     * @return
     */
    public String[] getSupportExModule();


    /**
     * Get the operation object of extended module<p>
     *
     * @param moduleType Module type
     * @return
     */
    public Module getExModule(String moduleType);

    /**
     * Is the device connection still alive<p>
     *
     * @since V3.10.01
     */
    public boolean isAlive();

    /**
     * Turn off the device connection and recover all resources<p>
     *
     * @since V3.10.01
     */
    public void destroy();

    /**
     * Device soft reset <p>
     */
    public void reset();

    /**
     * Set CSN<p>
     *
     * @param csn Customer serial number
     * @since V3.10.01
     */
    public void setCSN(String csn);

    /**
     * Get a device transaction manager <p>
     *
     * @return
     * @since V3.10.01
     */
    public DeviceTransationManager getDeviceTransationManager();

    /**
     * get the system defalut locale.
     *
     * @return
     */
    public Locale getDefaultLocale();

    /**
     * Get a line protection random number
     *
     * @param len The length of the needed random number.
     * @return A byte of the specified length
     * @since V3.10.01
     */
    public byte[] getRandom(int len);

    /**
     * Get a TUSN number
     *
     * @return Device tusn
     * @since V3.10.01
     */
    public String getTusn();
}
