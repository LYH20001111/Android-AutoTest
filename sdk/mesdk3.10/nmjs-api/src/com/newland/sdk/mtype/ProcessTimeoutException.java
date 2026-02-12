package com.newland.sdk.mtype;

import com.newland.sdk.mtype.common.ErrorCode;

/**
 * Device process timeout exception statement
 * 
 *
 *
 * @since ver3.10.01
 */
public class ProcessTimeoutException extends DeviceRTException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2489279709641553886L;
	
	public String msg;
	
	public ProcessTimeoutException(String msg){
		super(ErrorCode.PROCESS_TIMEOUT,msg);
	}
	
}
