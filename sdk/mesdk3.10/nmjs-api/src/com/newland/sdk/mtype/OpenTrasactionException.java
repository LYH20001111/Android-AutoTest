package com.newland.sdk.mtype;

import com.newland.sdk.mtype.common.ErrorCode;

/**
 * Open a transaction exception
 *
 */
public class OpenTrasactionException extends DeviceException{

	private static final long serialVersionUID = 2865916512858536738L;

	public OpenTrasactionException(String msg) {
		super(ErrorCode.OPEN_TRANSATION_FAILED, msg);
	}
	public OpenTrasactionException(String msg, Throwable e){
		super(ErrorCode.OPEN_TRANSATION_FAILED,msg,e);
	}


}
