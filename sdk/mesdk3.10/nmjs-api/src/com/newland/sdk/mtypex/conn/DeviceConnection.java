package com.newland.sdk.mtypex.conn;

import java.io.Closeable;
import java.io.IOException;

import com.newland.sdk.mtypex.cmd.DeviceCommand;
import com.newland.sdk.mtypex.cmd.DeviceResponse;


/** 
 * 
 * Describe a device connection<p> 
 * 
 * Connection can not be used repeatedly switch.<p> 
 * The alive of the connection is from the DeviceConnection object create until  the object close method been called.<p>
 * if isClosed() return  true all the objects held should  are discarded 
 *  
 *
 *
 */
public interface DeviceConnection extends DirectMessageListenerManager,Closeable{
	
	public interface InvokeStateNotifyListener {
		
		public void notify(DeviceResponse deviceResponse);
		
	}
	
	/**
	 * Get the connection ID<p> 
	 * The connection id is unique.
	 * @return
	 */
	public String getId();
	
	/**
	 * Send a transaction request<p>  
	 * This method must ensure blocking before respond<p>
	 * 
	 * For a general error, this method should not throw  exception.If transaction is abnormal, then pass <tt>DeviceResponse</tt>inside <tt>isSuccess</tt>statement returned.<p>
	 * If this method throws  exception, it will be considered that the connection can not be used again. The connection manager <tt>close()</tt> method  will be called
	 * @param request
	 * @param listener
	 * @param timeout
	 * @return
	 * @throws IOException    
	 * @throws InterruptedException  
	 */
	public DeviceResponse send(DeviceCommand request,InvokeStateNotifyListener listener,long timeout) throws IOException ,InterruptedException;
	
	/**
	 * Send a transaction request<p>  
	 * This method must ensure blocking before respond<p>
	 * 
	 * For a general error, this method should not throw  exception.If transaction is abnormal, then pass <tt>DeviceResponse</tt>inside <tt>isSuccess</tt>statement returned.<p>
	 * If this method throws  exception, it will be considered that the connection can not be used again. The connection manager <tt>close()</tt> method  will be called
	 
	 * @param request
	 * @param timeout
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */	
	public DeviceResponse send(DeviceCommand request,long timeout) throws IOException ,InterruptedException;
	

	

	/**
	 * Determine whether the connection is closed
	 * @return  true or false
	 */
	public boolean isClosed();

}
