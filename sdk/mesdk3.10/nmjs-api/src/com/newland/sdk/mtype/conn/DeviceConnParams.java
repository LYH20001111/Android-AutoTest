package com.newland.sdk.mtype.conn;

import java.util.Set;

/**
 * Device connection parameters<p>
 * For a given connector that adopts <tt>Android Service</tt> for connection, either <tt>bluetooth</tt>,<tt>USB</tt>,<tt>audio-in</tt>or<tt>k21</tt>

 * They are expected to have the same communication and interaction mode.<p>
 * This makes it possible to describe the connection for a corresponding type of Bluetooth device by one type and one set of parameters.<p>
 * <p>
 * The interface definition also includes the implement of a parameter set used for the parameter setting for a given connection.<p>
 * 
 *
 *
 */
public interface DeviceConnParams {
	
	/**
	 *  Get a connection type<p>
	 * 
	 * @return Connection type
	 */
	public DeviceConnType getConnectType();
	
	/**
	 *  Get all the parameter key values in the statement of such connector
	 * 
	 * @return Obtained parameter key value list
	 */
	public Set<String> getParamKeys();
	
	/**
	 * Get the corresponding connection parameter
	 * 
	 * @param key Corresponding key value
	 * @return Corresponding parameter value
	 * 
	 */
	public String getParam(String key);

}
