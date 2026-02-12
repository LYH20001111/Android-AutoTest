package com.newland.sdk.module.pin;

import com.newland.sdk.mtype.common.EventConst;
import com.newland.sdk.mtype.event.AbstractProcessDeviceEvent;

/**
 * Pin input event<p>
 * This event supports the return of multiple pin input states in pin input.<p>
 * Whether or not to support the return of multiple states depends on device implementation. <p>
 * For example: when a <tt>PinInputEvent</tt>is received, judge through {@link #isProcessing()} if the current is an un-ended state. <p>
 * Refer to the following implementations <p>
 * <pre><blockquote>
 * 	if(pinInputEvent.isFailed()){
 * 		throw pinInputEvent.getException();
 *    }else if(pinInputEvent.isSuccess()){
 * 		return getEncrypPin();
 *    }else if(pinInputEvent.isProcessing()){
 *        {@link NotifyStep} step = pinInputEvent.getNotifyStep();
 * 		if(step == NotifyStep.ENTER){
 * 			//...Add a '*' to the input pin displayed on android
 *        }else{
 * 			//....Reduce a '*' to the input pin displayed on android
 *        }
 *    }else if(pinInputEvent.isUserCanceled(){
 * 		//Process the user cancellation
 *    }
 * </blockquote></pre>
 *
 *
 * @since ver3.10.01
 */
public class PinInputEvent extends AbstractProcessDeviceEvent {

    private int inputLen;

    private byte[] encrypPin;

    private byte[] ksn;

    private NotifyStep notifyStep;

    public static enum NotifyStep {
        ENTER,
        BACKSPACE,
        CLEAR,
        SLID
    }


    public PinInputEvent(NotifyStep notifyStep) {
        super(EventConst.EVENT_PININPUT_FINISH, ProcessState.PROCESSING, null);
        this.notifyStep = notifyStep;
    }

    /**
     * Create an event of pin keyboard actively cancelled by user
     */
    public PinInputEvent() {
        super(EventConst.EVENT_PININPUT_FINISH, ProcessState.USER_CANCELED, null);
    }

    /**
     * Create a failed pin keyboard event
     *
     * @param e
     */
    public PinInputEvent(Throwable e) {
        super(EventConst.EVENT_PININPUT_FINISH, ProcessState.FAILED, e);
    }


    /**
     * Create a successful pin keyboard event
     *
     * @param inputLen   Pin length
     * @param encryptPin Pin cryptogram
     * @param ksn        ksn
     */
    public PinInputEvent(int inputLen, byte[] encryptPin, byte[] ksn) {
        super(EventConst.EVENT_PININPUT_FINISH, ProcessState.SUCCESS, null);
        this.inputLen = inputLen;
        this.encrypPin = encryptPin;
        this.ksn = ksn;
    }


    /**
     * Get a pin input crytogram<p>
     *
     * @return
     * @since ver3.10.01
     */
    public byte[] getEncrypPin() {
        return encrypPin;
    }

    /**
     * If <tt>DUKPT</tt> mode is used, return a <tt>KSN</tt>
     *
     * @return
     * @since ver3.10.01
     */
    public byte[] getKsn() {
        return ksn;
    }

    /**
     * Return the current input pin length<p>
     *
     * @return
     * @since ver3.10.01
     */
    public int getInputLen() {
        return inputLen;
    }

    /**
     * If the current step is a keyboard operation notice, return the notice type.<p>
     *
     * @return
     * @since ver3.10.01
     */
    public NotifyStep getNotifyStep() {
        return notifyStep;
    }


}
