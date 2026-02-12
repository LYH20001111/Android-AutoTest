package com.newland.sdk.me.module.emv;


/**
 * EMV transaction process exception<p>
 * 
 *
 * @since ver3.10.01
 */
public class ProcessEmvStepException extends EMVTransferException {

	/**
	 * 
	 */
	private final long serialVersionUID = -8435963120933288513L;

	private int processingCode = 0;
	
	public ProcessEmvStepException(int processingCode,String msg) {
		super(msg);
		this.processingCode = processingCode;
	}

	public int getProcessingCode() {
		return processingCode;
	}
	
}
