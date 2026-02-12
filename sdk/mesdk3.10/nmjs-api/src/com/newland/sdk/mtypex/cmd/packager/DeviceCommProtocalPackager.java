package com.newland.sdk.mtypex.cmd.packager;

import java.io.IOException;

import com.newland.sdk.mtypex.cmd.DeviceCommand;

public interface DeviceCommProtocalPackager {
	
	public byte[] pack(int serial,DeviceCommand cmd);
	
	public void readResponseFrom(ProtocalPackagerReader reader,ReadResponseListener listener) throws ReadTimeout, IOException, InterruptedException;

	public void unpack(DeviceCommand cmd,byte[] body,ResponseUnpackListener listener);
	
}
