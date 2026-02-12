package com.newland.sdk.mtypex.conn;


/**
 * Message listener manager interface<p>
 * 
 * @since 1.1.2
 * @author chenliang
 *
 */
public interface DirectMessageListenerManager {

	/**
	 * Register the direct message listener
	 * 
	 * @param listener
	 */
	public void registerDirectMessageListener(DirectMessageListener listener);
	
	/**
	 * Remove the direct message listener
	 * 
	 * @param listener
	 */
	public void removeDirectMessageListener(DirectMessageListener listener);
	
	/**
	 * Remove all listeners
	 */
	public void removeAllListeners();
	
}
