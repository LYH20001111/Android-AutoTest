package com.newland.sdk.mtype.log;

/**
 *  Standard device logger implementation class <p>
 * 
 *
 *
 * @since ver3.10.01
 */
public interface DeviceLogger {
	
	/**
	 *  Record an error log	 <p>
	 * 
	 * @param msg Error message
	 * @param e Error exception
	 */
	public void error(String msg,Throwable e);
	
	/**
	 * Record an alarm log<p>
	 * 
	 * @param msg Alarm message
	 * @param e Alarm exception
	 */
	public void warn(String msg,Throwable e);
	
	/**
	 * Record an error message<p>
	 * 
	 * @param msg Error message
	 */
	public void error(String msg);
	
	/**
	 * Record an alarm log<p>
	 * 
	 * @param msg Alarm message
	 */
	public void warn(String msg);
	
	/**
	 * Record an information message<p>
	 * 
	 * @param msg Information message
	 */
	public void info(String msg);
	
	/**
	 * Record an information message<p>
	 * 
	 * @param msg Information message
	 * @param e Message exception
	 */
	public void info(String msg,Throwable e);
	
	/**
	 * Record a debug message<p>
	 * 
	 * @param msg Debug mesage
	 */
	public void debug(String msg);
	
	/**
	 * Record a debug message<p>
	 * @param msg Debug mesage
	 * @param e Debug exception
	 */
	public void debug(String msg,Throwable e);
	
	/**
	 * Is the debugging mode enabled?<p>
	 * The extension interface implementation may be randomly returned and this method will be replaced by the default in-system implementation. 
	 * 
	 * @since 1.1.6
	 */
	public boolean isDebugEnabled();
}
