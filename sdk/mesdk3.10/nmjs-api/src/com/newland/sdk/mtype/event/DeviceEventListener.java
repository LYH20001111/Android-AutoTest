package com.newland.sdk.mtype.event;

import android.os.Handler;

/**
 * Device event listener<p>
 * In a device registration event, enter a handler object. This object will be returned via interface in response and used for the main thread operation in special circumstances<p>
 * 
 *
 * 
 */
public interface DeviceEventListener<T extends DeviceEvent> {
	
	/**
	 *  Response event<p>
	 * 
	 * @param event Event type
	 * @param handler UIhandler 
	 */
	public void onEvent(T event,Handler handler);
	
	/**
	 *  If it is hoped to enter a given handler for processing in event response, it is expected that this listener return will not be empty<p>
	 * 
	 * @return
	 *
	 * @since ver3.10.01
	 */
	@Deprecated
	public Handler getUIHandler();

}
