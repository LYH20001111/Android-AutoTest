package com.newland.ndk;

import com.newland.ndk.h.EM_PORT_NUM;

import android.util.Log;

public class SerialPort {
	protected SerialPort(){
		super();
	}

	/**
	 *Open serial port.
	 *Set baud rate,data bit, parity bit and stop bit and etc. Recommend to call this function every time before using serial port.
	 *Baud rate supported: 300,1200,2400,4800,9600,19200,38400,57600,115200
	 * 	 *Data bit supported: 8,7,6,5
	 * 	 *Parity Check mode supported: N(n):no parity; O(o):odd; E(e):even
	 * 	 *Stop bit supported: 1,2
	 * @param emPort Serial port number
	 * @param pszAttr Initialization string,e.g. "115200,8,N,1". When baud rate is missing, "8,N,1" is default.
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public int NDK_PortOpen(EM_PORT_NUM emPort, byte[] pszAttr) {
		if(pszAttr == null || emPort == null){
			return -1;
		}
		return NDK_PortOpen_m(emPort.ordinal(), pszAttr);
	}

	private native int NDK_PortOpen_m(int emPort, byte[] pszAttr);

	/**
	 * Close serial port
	 * @param emPort Serial port number
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public int NDK_PortClose(EM_PORT_NUM emPort) {
		if(emPort == null){
			return -1;
		}
		return NDK_PortClose_m(emPort.ordinal());
	}

	private native int NDK_PortClose_m(int emPort);

	/**
	 * Receive data.
	 * @param emPort Serial port number
	 * @param unLen Length of data to read
	 * @param pszOutBuf Buffer to save data
	 * @param nTimeoutMs Timeout in milliseconds
	 * @param pnReadLen Actual length read
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public int NDK_PortRead(EM_PORT_NUM emPort, int unLen, byte[] pszOutBuf, int nTimeoutMs, int[] pnReadLen) {
		if(emPort == null || unLen <=0 || pszOutBuf == null || nTimeoutMs < 0 || pnReadLen == null){
			return -1;
		}
		return NDK_PortRead_m(emPort.ordinal(), unLen, pszOutBuf, nTimeoutMs, pnReadLen);
	}

	private native int NDK_PortRead_m(int emPort, int unLen, byte[] pszOutBuf, int nTimeoutMs, int[] pnReadLen);

	/**
	 * Send data.
	 * @param emPort Serial port number
	 * @param unLen Length of data to write
	 * @param pszInbuf Data buffer to write
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public int NDK_PortWrite(EM_PORT_NUM emPort, int unLen, byte[] pszInbuf) {
		if(emPort == null || unLen < 0 || pszInbuf == null){
			return -1;
		}
		if(pszInbuf.length < unLen ){
			return -1;
		}
		return NDK_PortWrite_m(emPort.ordinal(), unLen, pszInbuf);
	}

	private native int NDK_PortWrite_m(int emPort, int unLen, byte[] pszInbuf);

	/**
	 * Check if send buffer is empty.
	 * @param emPort Serial port number
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public int NDK_PortTxSendOver(EM_PORT_NUM emPort) {
		if(emPort == null){
			return -1;
		}
		return NDK_PortTxSendOver_m(emPort.ordinal());
	}

	private native int NDK_PortTxSendOver_m(int emPort);

	/**
	 * Clear receive buffer.
	 * @param emPort Serial port number
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public int NDK_PortClrBuf(EM_PORT_NUM emPort) {
		if(emPort == null){
			return -1;
		}
		return NDK_PortClrBuf_m(emPort.ordinal());
	}

	private native int NDK_PortClrBuf_m(int emPort);


	/**
	 * Check if there is any data in receive buffer.
	 * @param emPort Serial port number
	 * @param pnReadLen Length of data in receiving buffer
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public int NDK_PortReadLen(EM_PORT_NUM emPort, int[] pnReadLen) {
		if(emPort == null || pnReadLen == null){
			return -1;
		}
		return NDK_PortReadLen(emPort.ordinal(), pnReadLen);
	}

	private native int NDK_PortReadLen(int emPort, int[] pnReadLen);
}
