package com.newland.sdk.mtypex.conn;

import com.newland.sdk.mtype.event.AbstractDeviceEvent;
import com.newland.sdk.mtypex.cmd.DeviceResponse;

/**
 * Device response event<p>
 * If any exception is thrown during the execution of the device, it can be obtained by deviceResponse.
 * 
 * If the execution process is actively revoked by the client at the SDK side, the deviceResponse return is null.
 * 
 *
 *
 * @since ver3.10.01
 */
public class InvokeEvent extends AbstractDeviceEvent {

	private DeviceResponse deviceResponse;
	
	public InvokeEvent(String eventName,DeviceResponse deviceResponse){
		super(null,eventName);
		this.deviceResponse = deviceResponse;
	}

	public DeviceResponse getDeviceResponse() {
		return deviceResponse;
	}

}
