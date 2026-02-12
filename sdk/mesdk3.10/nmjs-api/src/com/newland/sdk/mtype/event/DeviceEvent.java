package com.newland.sdk.mtype.event;

import com.newland.sdk.mtype.Device;

/**
 * Device event<p>
 * 
 *
 *
 */
public interface DeviceEvent {
	
	/**
	 * Get the event name<p>
	 * @return Event name 
	 */
	public String getEventName();
	
	/**
	 * Get the time stamp of such event<p>
	 * 
	 * @return Time stamp
	 *
	 * @since ver3.10.01
	 */
	public long timestamp();
	
	/**
	 * Get the event-related device<p>
	 * Cannot ensure all events can be returned <p>
	 * @since ver1.1.6
	 */
	public Device getOwner();
	
}
