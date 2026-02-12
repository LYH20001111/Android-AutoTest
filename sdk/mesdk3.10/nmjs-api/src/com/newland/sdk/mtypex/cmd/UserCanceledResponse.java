package com.newland.sdk.mtypex.cmd;

public class UserCanceledResponse implements DeviceResponse{

	@Override
	public CommandInvokeRslt getProcessRslt() {
		return CommandInvokeRslt.USER_CANCELED;
	}

	@Override
	public boolean isUserCanceled() {
		return true;
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
