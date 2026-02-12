package com.newland.sdk.mtype;

import com.newland.sdk.mtype.common.ErrorCode;

/**
 * Device offline exception
 * 
 *
 *
 * @since ver3.10.01
 */
public class DeviceOutofLineException  extends DeviceRTException{
	
	public DeviceOutofLineException(String msg,Throwable e){
		super(ErrorCode.DEVICE_DISCONNECTED, msg,e);
	}

	public DeviceOutofLineException(String msg) {
		super(ErrorCode.DEVICE_DISCONNECTED, msg);
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 6713752942107693804L;

}
