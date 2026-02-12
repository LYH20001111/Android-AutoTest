package com.newland.sdk.mtypex.cmd;

import com.newland.sdk.mtypex.cmd.desc.CommandDescription;

/**
 * Instruction serialization/deserialization
 * @author chenliang
 *
 */
public interface CommandSerializer {

	/**
	 * Serialize a request
	 * @param deviceCmd
	 * @return
	 */
	public <T extends DeviceCommand> byte[] toRequestPayload(T deviceCmd);
	
	/**
	 * Deserialize the response type of a notification, which is the response type returned multiple times
	 * @param deviceCmd
	 * @param payload
	 * @return
	 */
	public <T extends DeviceCommand> DeviceResponse loadNotifiedDeviceResponse(T deviceCmd,byte[] payload);
	
	/**
	 * Dserialize a response
	 * @param deviceCmd
	 * @param payload
	 * @return
	 */
	public <T extends DeviceCommand> DeviceResponse loadDeviceResponse(T deviceCmd,byte[] payload);
	
	/**
	 * Get an instruction format description
	 * @param deviceCmd
	 * @return
	 */
	public <T extends DeviceCommand> CommandDescription getCmdDescription(T deviceCmd);
	
}
