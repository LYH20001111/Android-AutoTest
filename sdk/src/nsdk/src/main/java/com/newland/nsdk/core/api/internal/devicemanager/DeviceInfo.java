package com.newland.nsdk.core.api.internal.devicemanager;

import com.newland.nsdk.core.api.common.card.contact.ContactCardSlot;

import java.util.ArrayList;

/**
 * Device information.
 *
 * <p>As the standard required, the device information must be returned via given interface.</p>
 */
public interface DeviceInfo {

    /**
     * Gets device SN.
     *
     * @return Device SN.
     */
    String getSN();

    /**
     * Gets device PN.
     *
     * @return Device PN.
     */
    String getPN();

    /**
     * Gets firmware version of the device.
     *
     * @return Firmware version
     */
    String getFirmwareVer();

    /**
     * Gets contactless version.
     *
     * @return Contactless version
     */
    String getContactlessVer();

    /**
     * Checks if the device supports USB.
     *
     * @return "true": support, "false": not support.
     */
    boolean isSupportUSB();

    /**
     * Checks if the device supports offline transaction
     *
     * @return "true": support, "false": not support.
     */
    boolean isSupportOffline();

    /**
     * Checks if the device supports magnetic stripe card.
     *
     * @return "true": support, "false": not support.
     */
    boolean isSupportMagCard();

    /**
     * Checks if the device supports contact card.
     *
     * @return "true": support, "false": not support.
     */
    boolean isSupportICCard();

    /**
     * Checks if the device supports contactless card.
     * @deprecated This interface is replaced by {@link DeviceInfo#isSupportContactlessCard()}.
     * @return "true": support, "false": not support.
     */
    @Deprecated
    boolean isSupportQuickPass();

    /**
     * Checks if the device supports contactless card.
     * @return "true": support, "false": not support.
     */
    boolean isSupportContactlessCard();

    /**
     * Checks if the device supports HCE function.
     *
     * @return "true": support, "false": not support.
     */
    boolean isSupportHCE();

    /**
     * Checks if the device supports LPCD function.
     * @return "true": support, "false": not support.
     */
    boolean isSupportLPCD();

    /**
     * Checks if the device supports printing.
     *
     * @return "true": support, "false": not support.
     */
    boolean isSupportPrint();

    /**
     * Checks if the device supports GPS.
     *
     * @return "true": support, "false": not support.
     */
    boolean isSupportGPS();

    /**
     * Checks if the device supports ethernet.
     *
     * @return "true": support, "false": not support.
     */
    boolean isSupportEthernet();

    /**
     * Checks if the device supports cash box.
     *
     * @return "true": support, "false": not support.
     */
    boolean isSupportCashBox();

    /**
     * Checks if the device supports SAM card.
     *
     * @return "true": support, "false": not support.
     */
    boolean isSupportSam();

    /**
     * Checks if the device supports PIN pad.
     *
     * @return "true": support, "false": not support.
     */
    boolean isSupportPinpadPort();

    /**
     * Checks if the device supports 232.
     *
     * @return "true": support, "false": not support.
     */
    boolean isSupport232Port();

    /**
     * Checks if the device supports camera.
     *
     * @return "true": support, "false": not support.
     */
    boolean isSupportCamera();

    /**
     * Gets scanner config.
     *
     * @return Scanner config, see {@link ScannerConfig}.
     */
    ScannerConfig getScannerConfig();

    /**
     * Gets customer ID.
     *
     * @return Customer ID.
     */
    String getCustomerID();

    /**
     * Checks if the device supports guest display.
     *
     * @return "true": support, "false": not support.
     */
    boolean isSupportGuestDisplay();

    /**
     * Checks is the device supports beep.
     *
     * @return "true": support, "false": not support.
     */
    boolean isSupportBeep();

    /**
     * Checks if the device supports sub screen.
     *
     * @return See below:
     * <ul>
     * <li>0x01: Supports sub screen and touch screen.</li>
     * <li>0x02: Supports sub screen but not support touch screen.</li>
     * <li>0xFF: No sub screen.</li>
     * </ul>
     */
    int isSupportSubScreen();

    /**
     * Gets contact card slots.
     *
     * @return Contact card slots.
     */
    ArrayList<ContactCardSlot> getContactCardSlots();

    /**
     * Gets device model.
     *
     * @return Device model.
     */
    String getDeviceModel();

    /**
     * Gets android API level.
     *
     * @return Android API level.
     */
    int getAndroidVersion();

    /**
     * Gets LED config.
     *
     * @return See below:
     * <ul>
     * <li>0x00: Reserved.</li>
     * <li>0x01: Four color lights.</li>
     * <li>0x02: Four green lights.</li>
     * <li>0x11: Four color lights which are analogue.</li>
     * <li>0x12: Four green lights which are analogue.</li>
     * </ul>
     */
    int getLEDConfig();

    /**
     * Gets whether device has a physical keyboard.
     * @return Whether device has a physical keyboard.
     */
    boolean isPhysicalKeyboard();

    /**
     * Gets whether device owns two magnetic head.
     * @return Whether device owns two magnetic head.
     */
    boolean isDualMsr();
}
