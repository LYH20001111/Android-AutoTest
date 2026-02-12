package com.newland.ndk;

public class IcCard {
	protected IcCard(){
		super();
	}

	/**
	 * Get driver verison.
	 * @param version Driver version string (No less than 16 bytes)
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_IccGetVersion(byte[] version);

	/**
	 * Power up.
	 * @param emIctype Card type (EM_ICTYPE)
	 * @param psAtrbuf ATR data
	 * @param pnAtrlen Length of ATR data
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_IccPowerUp (int emIctype, byte[] psAtrbuf,int[] pnAtrlen);

	/**
	 * Power down.
	 * @param emIctype Card type (EM_ICTYPE)
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_IccPowerDown(int emIctype);

	/**
	 *Detect card.
	 * @param pnSta Bit0(For smart card) 1: Inserted; 0: Not inserted
					Bit1(For smart card) 1: Powered up; 0: Not powered up
					Bit2: Reserved
					Bit3: Reserved
					Bit4(For SAM card 1) 1: Powered up; 0: Not powered up
					Bit5(For SAM card 2) 1: Powered up; 0: Not powered up
					Bit6(For SAM card 3) 1: Powered up; 0: Not powered up
					Bit7(For SAM card 4) 1: Powered up; 0: Not powered up
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_IccDetect(int[] pnSta);

	/**
	 * Send or Receive data.
	 * @param emIcType Card type (EM_ICTYPE)
	 * @param nSendLen Length of data sent
	 * @param psSendBuf Data sent
	 * @param pnRecvLen Length of data received
	 * @param psRecvBuf Data received
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_Iccrw(int emIcType, int nSendLen,  byte[] psSendBuf, int[] pnRecvLen,  byte[] psRecvBuf);

	/**
	 * PowerUp Mode
	 * @param mode
			  0x01 EMV
			  0x02 57600bps
			  0x03 38400bps
			  0x04 19200bps
			  0x05 social security cards
			  0x06 ISO,support PPS
			  0x07 ISO,9600bps,nonsupport PPS
	 * @param voltage
			  0x01 3V
			  0x02 5V
			  0x03 1.8V
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_IccSetPowerUpMode(int mode, int voltage);

	/**
	 * IC Card Config
	 * @param ictype Card type (EM_ICTYPE)
	 * @param cfgtype Config type(EM_CFGTYPE)
	 * @param value
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_IccSetConfig(int ictype,int cfgtype, int value);

	/**
	 * @param icType Card type (EM_ICTYPE)
	 * @param protocol
	 * @return  On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_IccGetProtocol(int icType, int[] protocol);
}
