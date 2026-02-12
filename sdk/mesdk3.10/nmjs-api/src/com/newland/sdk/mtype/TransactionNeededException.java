package com.newland.sdk.mtype;

import com.newland.sdk.mtype.common.ErrorCode;

/**
 * Device exclusive use exception
 *
 */
public class TransactionNeededException extends DeviceRTException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5338445597290910164L;

	public TransactionNeededException(String msg) {
		super(ErrorCode.TRANSACTION_NEEDED, msg);
	}
	
	public TransactionNeededException(String msg, Throwable e){
		super(ErrorCode.TRANSACTION_NEEDED,msg,e);
	}

}
