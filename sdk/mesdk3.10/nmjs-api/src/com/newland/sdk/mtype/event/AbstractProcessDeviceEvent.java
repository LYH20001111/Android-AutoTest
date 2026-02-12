package com.newland.sdk.mtype.event;

import com.newland.sdk.mtype.Device;

/**
 * Abstract transaction processing event<p>
 * 
 *
 *
 */
public class AbstractProcessDeviceEvent extends AbstractDeviceEvent{
	
	private ProcessState state;
	
	private Throwable e;
	
	protected static enum ProcessState{
		PROCESSING,
		USER_CANCELED,
		SUCCESS,
		FAILED
	}
	/**
	 *  Construct a transaction processing event
	 * @param eventName Event name
	 * @param state  Event state {@link ProcessState}
	 * @param e  Exception
	 */
	protected AbstractProcessDeviceEvent(String eventName,ProcessState state,Throwable e){
		this(null,eventName,state,e);
	}
	/**
	 * Construct a transaction processing event<p>
	 * @param device Device
	 * @param eventName Event name
	 * @param state Event state{@link ProcessState}
	 * @param e Exception
	 */
	protected AbstractProcessDeviceEvent(Device device,String eventName,ProcessState state,Throwable e) {
		super(device,eventName);
		this.state = state;
		this.e = e;
	}

	/**
	 * Get the result of transaction processing
	 * 
	 * @return true: Success  false: Failure
	 */
	public boolean isSuccess() {
		return (state == ProcessState.SUCCESS);
	}
	/**
	 * Is this transaction processing cancelled by customer?
	 * 
	 * @since ver3.10.01
	 * @return
	 */
	public boolean isUserCanceled(){
		return (state == ProcessState.USER_CANCELED);
	}
	/**
	 * Whether this transaction processing is in the process, i.e. continue to accept the event response	
	 * 
	 * @since ver3.10.01
	 * @return
	 */
	public boolean isProcessing(){
		return (state == ProcessState.PROCESSING);
	}
	/**
	 * Whether this transaction processing is failed<p>
	 * @since ver3.10.01
	 * @return
	 */
	public boolean isFailed(){
		return (state == ProcessState.FAILED);
	}

	/**
	 * If the processing is failed, return failure exception.
	 * 
	 * @return e Failure exception 
	 */
	public Throwable getException() {
		return e;
	}
	
	

}
