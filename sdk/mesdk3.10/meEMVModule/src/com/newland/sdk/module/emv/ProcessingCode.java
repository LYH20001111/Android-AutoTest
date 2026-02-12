package com.newland.sdk.module.emv;

public class ProcessingCode {
	
	/**
	 * 00-19	Debits
	 */
	/** 00	Goods And Service */
	public static final int GOODS_AND_SERVICE = 0x00;
	/** 01	Cash */
	public static final int CASH = 0x01;
	/** 02	Adjustment */
	public static final int DEBITS_ADJUSTMENT = 0x02;
	/** 03	Cheque Guarantee(Funds Guaranteed) */
	public static final int CHEQUE_GUARANTEE = 0x03;
	/** 04	Cheque Verification(Funds Available But Not Guaranteed) */
	public static final int CHEQUE_VERIFICATION = 0x04;
	/** 05	Euro-cheque */
	public static final int EURO_CHEQUE = 0x05;
	/** 06	Traveller Cheque */
	public static final int TRAVELLER_CHEQUE = 0x06;
	/** 07	letter Of Credit*/
	public static final int LETTER_OF_CREDIT = 0x07;
	/** 08	Giro(Postal Banking) */
	public static final int GIRO_POSTAL_BANKING = 0x08;
	/** 09	Goods And Service With Cash Disbursement Transfer */
	public static final int GOODS_AND_SERVICE_WITH_CASH_DISBURSEMENT_TRANSFER = 0x09;
	
	/**
	 * 10-13	Reserved For ISO Use
	 * 14-16	Reserved For National Use
	 * 17-19	Reserved For Private Use
	 */
	
	/** 20-29	Credits */
	/** 20	Returns */
	public static final int RETURNS = 0x20;
	/** 21	Deposits */
	public static final int DEPOSITS = 0x21;
	/** 22	Adjustment */
	public static final int CREDITS_ADJUSTMENT = 0x22;
	/** 23	Cheque Deposit Guarantee */
	public static final int CHEQUE_DEPOSIT_GUARANTEE = 0x23;
	/** 24	Cheque Deposit */
	public static final int CHEQUE_DEPOSIT = 0x24;
	/**
	 * 25-26	Reserved For ISO Use
	 * 27	    Reserved For National Use
	 * 28-29	Reserved For Private Use
	 */
	
	/** 30-39	Inquiry services*/
	
	/**30	Available Funds Inquiry */
	public static final int AVAILABLE_FUNDS_INQUIRY = 0x30;
	/**31	Balance Inquiry */
	public static final int BALANCE_INQUIRY	= 0x31;
	/**32	Reserved For ISO Use */
	public static final int RESERVED_FOR_ISO_USE	= 0x32;
	/**33	Account Verification */
	public static final int ACCOUNT_VERIFICATION	= 0x33;
	
	/**
	 * 34-35	Reserved For ISO Use
	 * 36-37	Reserved For National Use
	 * 38-39	Reserved For Private Use
	 */
	/**40-49	Transfer services */
	/**40	Cardholder Accounts Transfer */
	public static final int CARDHOLDER_ACCOUNTS_TRANSFER	= 0x40;	
	
	/**
	 * 41-45	Reserved For ISO Use
	 * 46-47	Reserved For National Use
	 * 48-49	Reserved For Private Use
	 * 50-99	Reserved
	 */
	/**60	Load */
	public static final int LOAD	= 0x60;		
	/**62	Not Appointed Load */
	public static final int NOT_APPOINTED_LOAD	= 0x62;
	/**63	Cash Saving */
	public static final int CASH_SAVING	= 0x63;
}
