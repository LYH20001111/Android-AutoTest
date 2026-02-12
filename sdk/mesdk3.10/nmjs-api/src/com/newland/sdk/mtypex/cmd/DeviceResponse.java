package com.newland.sdk.mtypex.cmd;

import java.io.Serializable;

/**
 * Device response interface
 * 
 *
 *
 * @since ver3.10.01
 */
public interface DeviceResponse extends Serializable{
	
	/**
	 * Get the result of the current instruction processing
	 * 
	 * @return
	 */
	public CommandInvokeRslt getProcessRslt();
	
	/**
	 * Is it revoked<p>
	 * @return
	 *
	 * @since ver3.10.01
	 * @deprecated
	 * @see #getProcessRslt();
	 */
	public boolean isUserCanceled();
	
	/**
	 * Is it success<p>
	 * @return
	 * @since ver3.10.01
	 * @deprecated
	 * @see #getProcessRslt();
	 */
	public boolean isSuccess();
	
	/**
	 * If {@link #isSuccess()} return is <tt>false</tt>, expected to return an error exception by this call
	 * 
	 * @return Exception
	 * 		
	 * @since ver3.10.01
	 */
	public Throwable getException();

}
