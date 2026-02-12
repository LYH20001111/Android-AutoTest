package com.newland.sdk.mtype;

import com.newland.sdk.mtype.common.ErrorCode;

/**
 *  Device standard exception abstract class<p>
 * With respect to code definition, refer to<tt>{@link ErrorCode ErrorCode}</tt><p>
 * 
 * 
 *
 *
 * @since ver3.10.01
 */
public class DeviceException extends Exception{
	/**
	 *
	 */
	private static final long serialVersionUID = 3539744546589640702L;
	

	public int code;
	
	public DeviceException(int code , String msg, Throwable e){
		super(msg,e);
		this.code = code;
	}
	
	public DeviceException(int code, String msg){
		super(msg);
		this.code = code;
	}

	public int getCode() {
		return code;
	}
	
	@Override
	public String getLocalizedMessage(){
		return "("+code+")" + super.getLocalizedMessage();
	}
	
}
