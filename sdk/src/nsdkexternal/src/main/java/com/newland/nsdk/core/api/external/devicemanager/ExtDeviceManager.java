package com.newland.nsdk.core.api.external.devicemanager;

import com.newland.nsdk.core.api.common.Module;
import com.newland.nsdk.core.api.common.exception.NSDKException;

import java.util.ArrayList;

/**
 * <b>[External Module]</b> Provides some device related functions of external device, such as getting attributes, updating FW/App and so on.
 *
 * <p>How to get this module:</p>
 * <pre>
 *     ExtDeviceManager extDeviceManager = (ExtDeviceManager)ExtNSDKModuleManagerImpl.getInstance().getModule(ModuleType.EXT_DEVICE_MANAGER);
 * </pre>
 */
public interface ExtDeviceManager extends Module {
    /**
     * Gets software version number of the external device.
     *
     * @return Software version number of the external device.
     * @throws NSDKException If error occurs.
     */
    String getVersionNumber() throws NSDKException;

    /**
     * Gets the external PIN pad serial number.
     *
     * @return External PIN pad serial number.
     * @throws NSDKException If error occurs.
     */
    String getSerialNumber() throws NSDKException;

    /**
     * Loads configuration to the external device.
     *
     * <p>Note: If the external device is connected via serial port, it will re-open the serial port after baud rate is set successfully.</p>
     *
     * <p>Example:</p>
     * <pre>
     *     // Load configuration with specified parameters.
     *     ExternalConfiguration configuration = new ExternalConfiguration();
     *
     *     // Only baud rate mode supported now.
     *     configuration.setBaudRateMode(BaudRateMode.MODE_115200_8_N_1);
     *
     *     try {
     *         extDeviceManager.setConfiguration(configuration);
     *     } catch (NSDKException e) {
     *         // Handle the exception according to different exception types.
     *     }
     * </pre>
     *
     * @param configuration <b>[Required]</b> Configuration to load. See {@link DeviceConfiguration}.
     * @throws NSDKException If error occurs.
     */
    void setDeviceConfiguration(DeviceConfiguration configuration) throws NSDKException;

    /**
     * Gets external configuration.
     *
     * @return External configuration. See {@link DeviceConfiguration}
     * @throws NSDKException If error occurs.
     */
    DeviceConfiguration getDeviceConfiguration() throws NSDKException;

    /**
     * Loads Firmware or app file to the external device.
     *
     * @param appName  <b>[Required]</b> FW/App file name. Its length shall be <=12.
     * @param data     <b>[Required]</b> FW/App file data.
     * @param listener <b>[Required]</b> Listener which will be called to give the result. See {@link UpdateListener}.
     * @deprecated Replaced by {@link #update(UpdateFiles, UpdateListener)}.
     */
    void loadFirmwareOrAppFile(String appName, byte[] data, UpdateListener listener) throws NSDKException;

    /**
     * Updates loaded firmware or app file.
     *
     * @param appName  <b>[Required]</b> File name of FW/App file that loaded in external device.
     * @param isReboot <b>[Required]</b> Whether restart device after updating or not.
     * @deprecated Replaced by {@link #update(UpdateFiles, UpdateListener)}.
     */
    void updateFirmwareOrApp(String appName, boolean isReboot) throws NSDKException;

    /**
     * Reboots external device.
     *
     * @throws NSDKException
     */
    void reboot() throws NSDKException;

    /**
     * Gets battery percentage.
     *
     * @return Battery percentage.
     * <ul>
     *     <li>0: Charging</li>
     *     <li>1 - 100: Battery percentage</li>
     * </ul>
     * @throws NSDKException
     */
    int getBatteryPercentage() throws NSDKException;

    /**
     * Gets device info.
     *
     * @return Device info, see {@link ExtDeviceInfo}.
     * @throws NSDKException
     */
    ExtDeviceInfo getDeviceInfo() throws NSDKException;

    /**
     * Sets device datetime.
     *
     * @param datetime <b>[Required]</b> Datetime. Format: YYYYMMDDHHMMSS.
     * @throws NSDKException
     */
    void setDatetime(String datetime) throws NSDKException;

    /**
     * Gets device datetime.
     *
     * @return Datetime. Format: YYYYMMDDHHMMSS.
     * @throws NSDKException
     */
    String getDatetime() throws NSDKException;

    /**
     * Sets bluetooth name.
     *
     * <p>Note: If bluetooth name is set successfully, bluetooth will be disconnected, so receiving timeout will occur.</p>
     *
     * <p>Example: </p>
     * <pre>
     *     try {
     *         String btName = "MyBluetooth";
     *         extDeviceManager.setBluetoothName(btName);
     *     } catch (NSDKException e) {
     *         e.printStackTrace();
     *         showMessage("Please check the external device if bluetooth name is set successfully.");
     *     }
     * </pre>
     *
     * @param name <b>[Required]</b> Bluetooth name to set. Max len: 21.
     * @throws NSDKException
     */
    void setBluetoothName(String name) throws NSDKException;

    /**
     * Gets bluetooth info.
     *
     * @return Bluetooth info, see {@link BluetoothInfo}.
     * @throws NSDKException
     */
    BluetoothInfo getBluetoothInfo() throws NSDKException;

    /**
     * Sets device connect mode.
     *
     * <p>Note: Device will be disconnected if mode is changed and set successfully, so receiving timeout will occur.</p>
     *
     * <p>Example: </p>
     * <pre>
     *     try {
     *         extDeviceManager.setConnectMode(DeviceConnectMode.USB);
     *     } catch (NSDKException e) {
     *         e.printStackTrace();
     *         showMessage("Please check the external device if connect mode is set successfully.");
     *     }
     * </pre>
     *
     * @param mode <b>[Required]</b> Device connect mode, see {@link DeviceConnectMode}.
     * @throws NSDKException
     */
    void setConnectMode(DeviceConnectMode mode) throws NSDKException;

    /**
     * Sets logo icon.
     *
     * <p>Example:</p>
     * <pre>
     *     AssetManager assetManager = context.getAssets();
     *     String iconPath = "iconfile";
     *     try {
     *         InputStream is = assetManager.open(iconPath);
     *         int length = is.available();
     *         byte[] buffer = new byte[length];
     *         is.read(buffer);
     *         is.close();
     *         extDeviceManager.setLogoIcon(LogoType.POWER_OFF_CHARGING, buffer, new UpdateListener() {
     *             {@code @Override}
     *             public void onError(int code, String message) {
     *                 // Handle error
     *             }
     *             {@code @Override}
     *             public void onFileTransferProgress(int percent) {
     *                 // Handle file transfer progress
     *             }
     *             {@code @Override}
     *             public void onComplete() {
     *                 // Setting is finished
     *             }
     *         });
     *     } catch (NSDKException e) {
     *         // Handle error
     *     } catch (IOException e) {
     *         // Handle error
     *     }
     * </pre>
     *
     * @param logoType <b>[Required]</b> Logo type. See {@link LogoType}.
     * @param data     <b>[Required]</b> Icon data.
     * @param listener <b>[Required]</b> Listener which will be called to give the result. See {@link UpdateListener}.
     * @throws NSDKException
     */
    void setLogoIcon(LogoType logoType, byte[] data, UpdateListener listener) throws NSDKException;

    /**
     * Sets language.
     *
     * <p>Example:</p>
     * <pre>
     *     AssetManager assetManager = context.getAssets();
     *     String filePath = "language-en.xml";
     *     try {
     *         InputStream is = assetManager.open(filePath);
     *         int length = is.available();
     *         byte[] buffer = new byte[length];
     *         is.read(buffer);
     *         is.close();
     *
     *         extDeviceManager.setLanguage(buffer, new UpdateListener() {
     *             {@code @Override}
     *             public void onError(int code, String message) {
     *                 // Handle error
     *             }
     *             {@code @Override}
     *             public void onFileTransferProgress(int percent) {
     *                 // Handle file transfer progress
     *             }
     *             {@code @Override}
     *             public void onComplete() {
     *                 // Setting is finished
     *             }
     *         });
     *     } catch (NSDKException e) {
     *         // Handle error
     *     } catch (IOException e) {
     *         // Handle error
     *     }
     *     </pre>
     *
     * @param language <b>[Required]</b> Language file data.
     * @param listener <b>[Required]</b> Listener which will be called to give the result. See {@link UpdateListener}.
     * @throws NSDKException
     */
    void setLanguage(byte[] language, UpdateListener listener) throws NSDKException;

    /**
     * Gets current language.
     *
     * @return Current language.
     * @throws NSDKException
     */
    String getLanguage() throws NSDKException;

    /**
     * Transfers application/firmware to external device and starts update process.
     *
     * <p>Example:</p>
     * <pre>
     *     AssetManager assetManager = context.getAssets();
     *     String appName = "mapp_ME30S_PinPad.NLP";
     *     // String firmwareName = "master_thm3682_me30su.NLP";
     *     try {
     *         InputStream is = assetManager.open(appName);
     *         int length = is.available();
     *         byte[] buffer = new byte[length];
     *         is.read(buffer);
     *
     *         // is = assetManager.open(firmwareName);
     *         // length = is.available();
     *         // byte[] firmwareBuffer = new byte[length];
     *         // is.read(firmwareBuffer);
     *
     *         is.close()
     *
     *         UpdateFiles updateFiles = new UpdateFiles();
     *         updateFiles.setApplicationFile(buffer);
     *         // updateFiles.setFirmwareFile(firmwareBuffer);
     *         extDeviceManager.update(updateFiles, new UpdateListener() {
     *             {@code @Override}
     *             public void onError(int code, String message) {
     *                 // Handle error
     *             }
     *
     *             {@code @Override}
     *             public void onFileTransferProgress(int percent) {
     *                 // Handle file transfer progress
     *             }
     *
     *             {@code @Override}
     *             public void onComplete() {
     *                 // Update is started. Device will reboot while updating. Check the device to see if it is updated successfully.
     *             }
     *         });
     *     } catch (NSDKException e) {
     *         // Handle error
     *     } catch (IOException e) {
     *         // Handle error
     *     }
     * </pre>
     *
     * @param files    <b>[Required]</b> Application and firmware files are supported.
     * @param listener <b>[Required]</b> Listener which will be called to give the result. See {@link UpdateListener}.
     * @throws NSDKException
     */
    void update(UpdateFiles files, UpdateListener listener) throws NSDKException;

    /**
     * Sets time configuration.
     *
     * <p>Example:</p>
     * <pre>
     *     try {
     *         TimeConfiguration configuration = new TimeConfiguration();
     *         configuration.setAutoBacklightOffTime(15);
     *         configuration.setAutoSleepTime(60);
     *         configuration.setAutoTurnOffTime(300);
     *
     *         extDeviceManager.setTimeConfiguration(configuration);
     *     } catch (NSDKException e) {
     *         // Handle error
     *     }
     * </pre>
     *
     * @param configuration <b>[Required]</b> Time configuration, see {@link TimeConfiguration}.
     * @throws NSDKException
     */
    void setTimeConfiguration(TimeConfiguration configuration) throws NSDKException;

    /**
     * Gets time configuration.
     *
     * @return Time configuration, see {@link TimeConfiguration}.
     * @throws NSDKException
     */
    TimeConfiguration getTimeConfiguration() throws NSDKException;

    /**
     * Search the file information by the keyword in the pinpad
     * @param keyword  <b>[Required]</b> The keyword for searching the file information.
     * @param tlvData  <b>[Optional]</b> This is reversed for the further use.
     * @return The file information searched by the keyword, details see {@link FileInfo}.
     * @throws NSDKException
     */
    ArrayList<FileInfo> getFileList(String keyword, byte[] tlvData) throws NSDKException;
}
