package com.newland.sdk.mtype;

import com.newland.sdk.mtype.common.ErrorCode;

/**
 * Device invokes exception<p>
 * 
 *
 */
public class DeviceInvokeException extends DeviceRTException{
	
	/**
	 *
	 */
	private static final long serialVersionUID = 6506097048218911579L;
	
	private String nativeCode;

	public DeviceInvokeException(String msg, Throwable e) {
		super(ErrorCode.DEVICE_INVOKE_FAILED, msg, e);
	}
	public DeviceInvokeException(String msg) {
		super(ErrorCode.DEVICE_INVOKE_FAILED, msg);
	}
	public DeviceInvokeException(String nativeCode,String msg) {
		super(ErrorCode.DEVICE_INVOKE_FAILED, msg);
		this.nativeCode = nativeCode;
	}
	
	/**
	 *  Get the device native error code
	 * 
	 * @return
	 */
	public String getNativeCode() {
		return nativeCode;
	}

	@Override
	public String getLocalizedMessage(){
		return super.getLocalizedMessage()+",nativeCode:"+nativeCode;
	}

}
