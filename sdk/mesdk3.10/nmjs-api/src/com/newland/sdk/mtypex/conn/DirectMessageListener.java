package com.newland.sdk.mtypex.conn;

import com.newland.sdk.mtype.util.SimIdGenerator;

/**
 * Device message listener<p>
 * This listener will return directly the device info package whitout message parsing.<p>
 * And the implements of listener's queue like a simple message sending mechanism, which does not care about queue execution length and performance consumption.<p>
 * It is recommended that a connection should not set up too much this listener to avoid performance bottlenecks.<p>
 * Referring to the decision of the instruction message in the newland instruction specification, the indicator bit is (3F)
 * 
 *
 * @since 1.1.2
 */
public abstract class DirectMessageListener {
	
	private static final SimIdGenerator idgen = new SimIdGenerator(0xFF);
	
	private final String listenerId;
	
	public DirectMessageListener(){
		listenerId = "DM-Listener-" + idgen.getId(DirectMessageListener.class);
	}
	
	public String getListenerId(){
		return listenerId;
	}

	public abstract void notify(byte[] cmdCode,byte[] payload);
	
}
