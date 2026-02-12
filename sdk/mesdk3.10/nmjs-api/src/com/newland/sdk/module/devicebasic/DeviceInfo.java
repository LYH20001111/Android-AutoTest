package com.newland.sdk.module.devicebasic;

/**
 *
 * Device information
 *
 * For devices satisfying the standard, the device information must be returned via given interface
 */
public interface DeviceInfo {

	/**
	 * Get the device SN number
	 *
	 * @return
	 */
	public String getSN();

	/**
	 * Get the current device firmware version
	 * <p>
	 *
	 * @return Firmware version
	 */
	public String getFirmwareVer();

	/**
	 * Get the device application version number
	 * <p>
	 *
	 * @return
	 */
	public String getAppVer();

	/**
	 * Get the CSN
	 * @return
	 */
	public byte[] getCSN();

	/**
	 * Get the device model.
	 * @return
	 */
	public String getModel();

	/**
	 * Get the manufacturer ID
	 *
	 * @return
	 */
	@Deprecated
	public String getVID();

	/**
	 *  Get the Boot version number
	 * @since 3.10.01
	 * @return
	 */
	public String getBootVersion();
	/**
	 * Check if the device supports audio
	 * @return
	 */
	public boolean isSupportAudio();

	/**
	 * Check if the device supports bluetooth
	 * @return
	 */
	public boolean isSupportBlueTooth();
	/**
	 * Check if the device supportsUSB
	 * @return
	 */
	public boolean isSupportUSB();
	/**
	 * Check if the device supports offline transaction
	 * @since 3.10.01
	 * @return
	 */
	public boolean isSupportOffLine();
	/**
	 * Check if the device supports magnetic stripe card
	 * @return
	 */
	public boolean isSupportMagCard();
	/**
	 * Check if the device supports contact type IC card
	 * @return
	 */
	public boolean isSupportICCard();
	/**
	 * Check if the device supports contactless IC card
	 * @return
	 */
	public boolean isSupportQuickPass();
	/**
	 *  Check if the device supports printing
	 * @return
	 */
	public boolean isSupportPrint();
	/**
	 * Check if the device supports screen display
	 * @return
	 */
	public boolean isSupportLCD();
	/**
	 *  Judge if the device supports GPS
	 * @return
	 * @since ver3.10.01
	 */
	public boolean isSupportGPS();
	/**
	 *  Judge if the device supports Ethernet
	 * @return
	 * @since ver3.10.01
	 */
	public boolean isSupportEthernet();
	/**
	 *  Judge if the device supports CashBox
	 * @return
	 * @since ver3.10.01
	 */
	public boolean isSupportCashBox();
	/**
	 *  Judge if the device supports sam card
	 * @return
	 * @since ver3.10.01
	 */
	public boolean isSupportSam();
	/**
	 *  Judge if the device supports Pinpad口
	 * @return
	 * @since ver3.10.01
	 */
	public boolean isSupportPinpadPort();
	/**
	 *  Judge if the device supports 232口
	 * @return
	 * @since ver3.10.01
	 */
	public boolean isSupport232Port();
	/**
	 *  Judge if the device supports Camera
	 * @return
	 * @since ver3.10.01
	 */
	public boolean isSupportCamera();
	/**
	 * get customer ID<p>
	 * @return
	 * @since ver3.10.01
	 */
	public String getCustomerID();
	/**
	 * Judge if the device supports GuestDisplay<p>
	 * @return
	 * @since ver3.10.01
	 */
	public boolean isSupportGuestDisplay();

	/**
	 *  Judge if the device supports SubScreen
	 * @return 0x01 support SubScreen and Touch screen  0x02 support SubScreen and do not support Touch screen  0xFF no subscreen
	 * @since ver3.10.01
	 */
	public int isSupportSubscreen();

	/**
	 * Get the device PN number
	 *
	 * @return
	 */
	public String getPN();

	/**
	 * get PCI version
	 * @return
	 */
	public String getPCIVersion();

	public String getKSN();

	/**
	 * SE FW Version
	 * @return
	 */
	public String getSeFwVersion();
}
