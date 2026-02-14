package com.newland.nsdk.core.api.internal.devicemanager;

import android.content.Context;

import com.newland.nsdk.core.api.common.Module;
import com.newland.nsdk.core.api.common.exception.NSDKException;

import java.util.Date;
import java.util.List;

/**
 * Provides device info related operations.
 *
 * <p>How to get this module:</p>
 * <pre>
 *     DeviceManager deviceManager = (DeviceManager)NSDKModuleManagerImpl.getInstance().getModule(ModuleType.DEVICE_MANAGER);
 * </pre>
 */
public interface DeviceManager extends Module {

    /**
     * Gets device information.
     *
     * @return Current device information. See {@link DeviceInfo}
     */
    DeviceInfo getDeviceInfo() throws NSDKException;

    /**
     * Gets POS datetime.
     *
     * @return POS datetime.
     */
    Date getPOSDate() throws NSDKException;

    /**
     * Sets POS datetime.
     *
     * @param date <b>[Required]</b> POS datetime.
     */
    void setPOSDate(Date date) throws NSDKException;

    /**
     * Gets NSDK version.
     *
     * @return NSDK version.
     */
    String getSDKVersion();

    /**
     * Checks if the device has security module.
     *
     * @return "true" if yes, "false" if no.
     */
    boolean isExistSecurityModule();

    /**
     * Gets tamper status.
     *
     * @return Tamper status, see {@link TamperStatus}.
     * @throws NSDKException
     * @deprecated This method can be replaced by {@link DeviceManager#getTamperStatuses()}.
     */
    TamperStatus getTamperStatus() throws NSDKException;

    /**
     * Gets all the tamper status occurred.
     * @return All the tamper status occurred. See {@link TamperStatus}
     * @throws NSDKException
     */
    TamperStatus[] getTamperStatuses() throws NSDKException;

    /**
     * Gets Non-deletable application list.
     * @param context  <b>[Required]</b> The context for CertificationInfo and PackageManager to use..
     * @return The list of non-deletable applications.
     * @throws NSDKException
     */
    List<String> getNonDeletableAppList(Context context) throws NSDKException;

    /**
     * Sets the volume of physical keyboard supported by devices like P300.
     * @param isOpen   <b>[Required]</b> The volume state of physical keyboard. "true" is open ,"false" is close.
     * @throws NSDKException
     */
    void setKeyVolume(boolean isOpen) throws NSDKException;

    /**
     * To set the U2000 radar detection distance with the gain and delta parameters.
     * @param radarGain   <b>[Required]</b> The microwave radar gain whose default is 0x2B. The smaller gain has farther detection distance.
     * @param delta       <b>[Required]</b> The microwave radar distance parameter whose default configuration is 26, and range from 15 to 1022.
     * @throws NSDKException
     */
    void setRadarDetectionDistance(RadarGain radarGain, int delta) throws NSDKException;

    /**
     * Enables/Disables radar and heater function.
     * @param isRadarEnable   <b>[Required]</b> Whether to enable radar function or not.
     * @param isHeaterEnable  <b>[Required]</b> Whether to enable heater function or not.
     * @throws NSDKException
     */
    void enableRadarAndHeater(boolean isRadarEnable, boolean isHeaterEnable) throws NSDKException;

    /**
     * Gets the tamper reason.
     * @return The tamper reason, details see {@link TamperReason}. With no tamper triggered, this interface will return {@link TamperReason#NONE}.
     * @throws NSDKException
     */
    TamperReason[] getTamperReason() throws NSDKException;

    /**
     * Gets the anti-removal status of the current device.
     * @return The anti-removal status of the current device. See {@link AntiRemovalStatus}.
     * @throws NSDKException
     */
    AntiRemovalStatus getAntiRemovalStatus() throws NSDKException;

    /**
     * Sets the anti-removal status to the current device.
     * <p>Note: On the supported device, the initial anti-removal status is {@link AntiRemovalStatus#ARMED}.
     * And if was triggered to the {@link AntiRemovalStatus#LOCKED} by outer factors, you can set {@link AntiRemovalStatus#ARMED} to reset it.</p>
     * @param status <b>[Required]</b> The anti-removal status set to device. See {@link AntiRemovalStatus}.
     * @throws NSDKException
     */
    void setAntiRemovalStatus(AntiRemovalStatus status) throws NSDKException;

    /**
     * Sets ethernet mode.
     * <p>Note: If the current device is not support setting ethernet mode, it will throw an exception.</p>
     * <p>The changes will be only valid when the usb port is not inserted.</p>
     * @param mode  <b>[Required]</b> The ethernet mode to be set, see {@link EthernetMode}.
     * @throws NSDKException
     */
    void setEthernetMode(EthernetMode mode) throws NSDKException;

    /**
     * Gets ethernet mode.
     * @return The current ethernet mode, see {@link EthernetMode}. If device is not support setting ethernet mode, it will return {@link EthernetMode#NON_CONFIGURABLE}.
     * @throws NSDKException
     */
    EthernetMode getEthernetMode() throws NSDKException;

    /**
     * Gets the properties of the battery.
     * @return The battery properties, details see {@link BatteryProperty}.
     * @throws NSDKException
     */
    BatteryProperty getBatteryProperty() throws NSDKException;

    /**
     * Sets the mode and settings of the target device light.
     * @param deviceLight       <b>[Required]</b> The target device light, see {@link DeviceLight}.
     * @param lightMode         <b>[Required]</b> The light mode of the target device light, see {@link LightMode}.
     * @param flashingInterval  <b>[Optional]</b> The flashing interval, only valid when lightMode is {@link LightMode#BLINK}. Unit: ms. There are three recommended interval:
     *                          <ul>
     *                              <li>Fast: 200ms</li>
     *                              <li>Normal: 400ms</li>
     *                              <li>Slow: 600ms</li>
     *                          </ul>
     * @throws NSDKException
     */
    void setDeviceLightMode(DeviceLight deviceLight, LightMode lightMode, Integer flashingInterval) throws NSDKException;

    /**
     * Sets the long press function for keyboard buttons.
     * <p>For example:</p>
     * <pre>
     *
     *     DeviceManager mDeviceManager = (DeviceManager)NSDKModuleManagerImpl.getInstance().getModule(ModuleType.DEVICE_MANAGER);
     *     int keyMask = KeyboardButton.SYS_KEY_0 | KeyboardButton.SYS_KEY_ENTER;
     *     deviceManager.setLongPressButtons(keyMask, true);
     * </pre>
     * @param keyMask Key mask, combined using a bitwise OR (|) operation with constants in {@link KeyboardButton}
     * @param enableLongPress true: Enable long press, false: Disable long press
     * @throws NSDKException Throws an exception on failure
     */
    void setLongPressButtons(int keyMask, boolean enableLongPress) throws NSDKException;
}
