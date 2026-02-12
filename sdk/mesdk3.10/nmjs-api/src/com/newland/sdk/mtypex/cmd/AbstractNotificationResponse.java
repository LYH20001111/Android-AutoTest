package com.newland.sdk.mtypex.cmd;

public abstract class AbstractNotificationResponse implements DeviceResponse{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public CommandInvokeRslt getProcessRslt() {
		return CommandInvokeRslt.CONTINUED;
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
		return null;
	}

}
