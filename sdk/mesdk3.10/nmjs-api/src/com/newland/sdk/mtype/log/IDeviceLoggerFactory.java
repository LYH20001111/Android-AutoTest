package com.newland.sdk.mtype.log;
/**
 * Device log type interface
 *
 */
public interface IDeviceLoggerFactory {
	
	public DeviceLogger getLogger(String tagName);

}
