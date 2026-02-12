package com.newland.sdk.mtypex.cmd.packager;

import com.newland.sdk.mtype.DeviceException;
import com.newland.sdk.mtype.common.ErrorCode;

public class ReadTimeout extends DeviceException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5681193366541171545L;

	public ReadTimeout(String msg) {
		super(ErrorCode.PROCESS_TIMEOUT,msg);
	}
	
	public ReadTimeout(String msg, Throwable e) {
		super(ErrorCode.PROCESS_TIMEOUT, msg, e);
	}


	
}
