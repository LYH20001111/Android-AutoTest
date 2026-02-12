package com.newland.sdk.mtype;

import com.newland.sdk.mtype.common.ErrorCode;

/**
 * Device operation exception abstract class <p>
 * 
 * About the code definition, refer to <tt>{@link ErrorCode ErrorCode}</tt><p>
 * 
 *
 *
 */
public class DeviceRTException extends RuntimeException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8115746699794950327L;

	public int code;
	
	public DeviceRTException(int code , String msg, Throwable e){
		super(msg,e);
		this.code = code;
	}
	
	public DeviceRTException(int code, String msg){
		super(msg);
		this.code = code;
	}
	
	@Override
	public String getLocalizedMessage(){
		return "("+code+")" + super.getLocalizedMessage();
	}

	public int getCode() {
		return code;
	}
	
}
