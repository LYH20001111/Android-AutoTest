package com.newland.sdk.me.module.pininput;

import com.newland.sdk.module.pin.PinInputEvent;

/**
 * Pin keypad event
 *
 */
public class K21PininutEvent extends PinInputEvent {

	private int keyEvent;
	private PinState pinState;

	public static enum PinState {
		PIN_ENTER, SWIPCARD, ICCARD;
	}

	public PinState getPinState() {
		return pinState;
	}
	public K21PininutEvent(NotifyStep notifyStep){
		super(notifyStep);
	}
	public K21PininutEvent(NotifyStep notifyStep,int keyEvent){
		super(notifyStep);
		this.keyEvent = keyEvent;
	}
	/**
	 *
	 * Construct an event of active customer cancellation of pin keypad	
	 */
	public K21PininutEvent(){
		super();
	}
	
	/**
	 *
	 *  Construct a failed pin keypad event
	 * 
	 * @param e
	 */
	public K21PininutEvent(Throwable e) {
		super(e);
	}
	
	/**
	 *
	 * Construct a successful pin keypad event
	 * 
	 * @param encryptPin
	 */
	public K21PininutEvent(int inputLen,byte[] encryptPin,byte[] ksn) {
		super(inputLen,encryptPin,ksn);
	}
	public K21PininutEvent(PinState pinState, int inputLen, byte[] encryptPin, byte[] ksn) {
		super(inputLen,encryptPin,ksn);
		this.pinState = pinState;
	}

	public int getKeyEvent() {
		return keyEvent;
	}
}
