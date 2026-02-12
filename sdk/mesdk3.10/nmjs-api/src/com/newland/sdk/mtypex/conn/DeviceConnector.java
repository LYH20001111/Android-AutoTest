package com.newland.sdk.mtypex.conn;

import android.content.Context;

import com.newland.sdk.mtype.conn.DeviceConnParams;
import com.newland.sdk.mtype.conn.DeviceConnType;

/**
 * Device connector implementation<p>
 * 
 * 
 *
 */
public interface DeviceConnector {
	
	/**
	 * Get the device connector support
	 * @return
	 */
	public DeviceConnType[] getSupportConnType();
	
	/**
	 * Create a device connection<p>
	 * There is no guarantee that it will ensure a successful connection.<p>
	 * The upper layer needs to handle errors that may occur when created，which based on the different features of the connector.
	 * 
	 * @param context Android Context
	 * @param params Connection params
	 * @return
	 * 
	 * @throws Exception 
	 */
	public DeviceConnection create(Context context, DeviceConnParams params) throws  Exception;

	
}
