package com.newland.ndk;

import com.newland.ndk.h.ENUM_MAG_DATA_TYPE;
import com.newland.ndk.h.ENUM_MAG_TRACK;

public class MagCard {
	protected MagCard(){
		super();
	}

	/**
	 * Open magnetic card reader.
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_MagOpen();

	/**
	 * Close magnetic card reader.
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_MagClose();

	/**
	 *Reset magnetic card reader.
	 *Reset magnetic head and clear buffer
	 *
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_MagReset();

	/**
	 * Check if card swiped.
	 * @param psSwiped 1: Swipei; 0: Not swiped
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_MagSwiped(byte[] psSwiped);

	/**
	 * Read data of track 1, 2, 3.
	 * @param pszTk1 Buffer to save track 1 data
	 * @param pszTk2 Buffer to save track 2 data
	 * @param pszTk3 Buffer to save track 3 data
	 * @param pnErrorCode Error code
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_MagReadNormal(byte[] pszTk1, byte[] pszTk2, byte[] pszTk3, int[] pnErrorCode);

	/**
	 * Read raw data of track 1, 2, 3.
	 * @param pszTk1 Buffer to save track 1 raw data
	 * @param pusTk1Len  Track 1 data length
	 * @param pszTk2 Buffer to save track 2 raw data
	 * @param pusTk2Len Track 2 data 2 length
	 * @param pszTk3 Buffer to save track 3 raw data
	 * @param pusTk3Len Track 3 data length
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_MagReadRaw(byte[] pszTk1, short[] pusTk1Len, byte[] pszTk2, short[] pusTk2Len,byte[] pszTk3, short[] pusTk3Len );

	/**
	 *
	 * @param type
	 * @param track
	 * @param off
	 * @param unLen
	 * @param tkdata
	 * @param pnReadlen
	 * @return
	 */
	public native int NDK_MagReadRawData(int type, int track, int off, int unLen, byte[] tkdata, int[] pnReadlen);

}
