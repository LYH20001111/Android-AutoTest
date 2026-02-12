package com.newland.sdk.mtypex.conn;

/**
 * Internal object<p>
 * 
 * Used in passing interrupt requests between <tt>InnerMessage</tt> and <tt>DeviceRequest</tt><p>
 * 
 *
 *
 * @since ver3.10.01
 */
public interface Abortable {

	public void abort();

	/**
	 * Revoked event types
	 * 
	 * @param keyCode Key code of the revoked event
	 */
	public void abort(int keyCode);

}
