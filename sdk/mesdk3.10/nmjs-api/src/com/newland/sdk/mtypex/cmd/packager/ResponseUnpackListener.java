package com.newland.sdk.mtypex.cmd.packager;

import com.newland.sdk.mtypex.cmd.DeviceResponse;

public interface ResponseUnpackListener {
	
	public void unpackFinished(boolean isNotifyResponse,DeviceResponse response);

}
