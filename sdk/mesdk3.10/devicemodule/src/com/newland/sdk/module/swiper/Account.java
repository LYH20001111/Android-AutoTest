package com.newland.sdk.module.swiper;

import com.newland.sdk.module.pin.PinpadModule;

/**
 * Account number object<p>
 * The return mode depends on specific implements.<p>
 * If {@link MagStripeCardModule#readPlainResult} is used, generally the returned {@link #acctNo} has the complete account number, similar to reading the plain text card number.<p>
 * But, if {@link MagStripeCardModule#readEncryptResult} is used, the returned card number generally carries mask<p>
 * Because the computation of pinblock by<tt>ansi x9.8</tt> may need the participation of account number. To ensure the consistence of the account number used in the computing with that of the swipe card, it is necessary to import the data obtained by{{@link #getAcctHashId()} to the pin encrypting interface<p>
 * @see PinpadModule#startPinInput
 *
 *
 * @since ver3.10.01
 */
public class Account {
	
	private String acctNo;
	
	private String acctHashId;
	
	public Account(String acctNo,String acctHashId){
		this.acctNo = acctNo;
		this.acctHashId = acctHashId;
	}
	
	/**
	 *  get an account number that may be mask-protected <p>
	 * <tt>ascii</tt> code 
	 * @return account number that may be protected by mask
	 * @since ver3.10.01
	 */
	public String getAcctNo() {
		return acctNo;
	}

	/**
	 *  mark an account number for confirming the consistence in key computing <p>
	 * SHA-1,128-bit,16-byte are changed into 32 HEX ASCII codes .
	 * 
	 * 
	 * @return account number marking
	 * @since ver3.10.01
	 * @see PinpadModule#startPinInput
	 */
	public String getAcctHashId() {
		return acctHashId;
	}
	
	public String toString(){
		return "acct(acctNo:"+acctNo+",acctHashId:"+acctHashId+")";
	}
	

}
