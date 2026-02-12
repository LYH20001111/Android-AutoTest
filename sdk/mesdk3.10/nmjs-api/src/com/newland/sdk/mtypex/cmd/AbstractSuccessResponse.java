package com.newland.sdk.mtypex.cmd;


public abstract class AbstractSuccessResponse implements DeviceResponse{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7538547496642736070L;

	@Override
	public boolean isUserCanceled() {
		return false;
	}

	@Override
	public boolean isSuccess() {
		return true;
	}
	
	@Override
	public CommandInvokeRslt getProcessRslt(){
		return CommandInvokeRslt.SUCCESS;
	}

	@Override
	public Throwable getException() {
		return null;
	}

}
