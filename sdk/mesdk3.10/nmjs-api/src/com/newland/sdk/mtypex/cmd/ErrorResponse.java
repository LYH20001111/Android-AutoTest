package com.newland.sdk.mtypex.cmd;

public class ErrorResponse implements DeviceResponse{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2145917585985176155L;
	private Throwable e;
	
	public ErrorResponse(Throwable e){
		this.e = e;
	}
	
	@Override
	public boolean isUserCanceled() {
		return false;
	}

	@Override
	public boolean isSuccess() {
		return false;
	}

	@Override
	public Throwable getException() {
		return e;
	}

	@Override
	public CommandInvokeRslt getProcessRslt() {
		return CommandInvokeRslt.FAILED;
	}


	
}
