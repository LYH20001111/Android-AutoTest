package com.newland.sdk.mtype;

import android.content.Context;

import com.newland.sdk.mtype.conn.DeviceConnParams;
import com.newland.sdk.mtype.conn.DeviceConnType;
import com.newland.sdk.mtype.event.DeviceEventListener;

/**
 * Device driver entry<p>
 * A loading example:<p>
 * <pre><blockquote>
 *  Class&lt;DeviceDriver&gt; m31DriverClass = Class.forName("com.newland.m31.M31Driver");
 *  M31Driver diver = (M31Driver)m31DriverClass.newInstance();
 * </blockquote></pre>
 * 
 * 
 *
 *
 */
public interface DeviceDriver {
	
	/**
	 * Get the MESDK version.
	 * @return
	 */
	public String getSDKVersion();
	
	/**
	 * Get the supported connection type<p>
	 * 
	 * @return
	 */
	public DeviceConnType[] getSupportConnType();
	
	/**
	 * Judge if a certain connection mode is supported
	 * 
	 * @since ver3.10.01
	 * @param connType Tested connection mode{@link DeviceConnType}
	 * @return
	 */
	public boolean isSupportedConnType(DeviceConnType connType);
	
	/**
	 * Connect the device via a connection parameter. If the connection fails, throw exception <p>
	 * @param context Android Context
	 * @param connParams Connection parameter
	 * @return
	 * @throws Exception 
	 *
	 * @since ver3.10.01
	 */
	public Device connect(Context context, DeviceConnParams connParams, DeviceEventListener<ConnectionCloseEvent> closedListener) throws Exception ;
	
}
