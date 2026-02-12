package com.newland.sdk.mtype.event;

import com.newland.sdk.mtype.Device;

/**
 * AbstractDeviceEvent implements<p>
 * 
 *
 *
 */
public abstract class AbstractDeviceEvent implements DeviceEvent{
	
	private String eventName;
	
	private long timestamp;
	
	private Device device;
	
	public AbstractDeviceEvent(Device device,String eventName){
		this.eventName = eventName;
		this.timestamp = System.currentTimeMillis();
		this.device = device;
	}
	public AbstractDeviceEvent(String eventName){
		this.eventName = eventName;
		this.timestamp = System.currentTimeMillis();
	}

	@Override
	public String getEventName() {
		return eventName;
	}

	@Override
	public long timestamp() {
		return timestamp;
	}

	@Override
	public Device getOwner() {
		return device;
	}
	
	
	
}
