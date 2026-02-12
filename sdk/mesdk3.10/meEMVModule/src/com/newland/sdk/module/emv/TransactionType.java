package com.newland.sdk.module.emv;

/**
 * Self define transaction processing codes
 */
public class TransactionType {
    /**
     * GOODS(defalut) <p>
     */
    public static final int STANDARD = 0x01;
    /**
     * Simple process(for getting account info)<p>
     */
    public static final int SIMPLE = 0x7F;
    /**
     * SERVICES
     */
    public static final int SERVICES = 0x02;
    /**
     * CASH
     */
    public static final int CASH = 0x03;
    /**
     * CASHBACK
     */
    public static final int CASHBACK = 0x04;
    /**
     * INQUIRY
     */
    public static final int INQUIRY = 0x05;
    /**
     * TRANFER
     */
    public static final int TRANFER = 0x06;
    /**
     * ADMIN
     */
    public static final int ADMIN = 0x07;
    /**
     * CASHDEPOSIT
     */
    public static final int CASHDEPOSIT = 0x08;
    /**
     * PAYMENT
     */
    public static final int PAYMENT = 0x09;

    /**
     * Electronic cash consumption<p>
     */
    public static final int EC_CONSUMPTION = 0x0B;
    /**
     * Pre-authentication<p>
     */
    public static final int PREAUTH = 0x0C;
    /**
     * Online account balance<p>
     */
    public static final int BALANCE = 0x0D;
    /**
     * refund<p>
     */
    public static final int REFUND = 0x20;
    /**
     * EC cash load reversal<p>
     */
    public static final int EC_CASH_LOAD_REVERSAL = 0x26;
    /**
     * EC appointed account load (contact)<p>
     */
    public static final int EC_APPOINTED_LOAD_CONTACT = 0x21;
    /**
     * EC appointed account load (contactless)<p>
     */
    public static final int EC_APPOINTED_LOAD_CTLS = 0x31;
    /**
     * EC non-appointed account load  (contact)<p>
     */
    public static final int EC_NOT_APPOINTED_LOAD_CONTACT = 0x22;
    /**
     * EC non-appointed account loa (contactless)<p>
     */
    public static final int EC_NOT_APPOINTED_LOAD_CTLS = 0x32;
    /**
     * EC cash load (contact)<p>
     */
    public static final int EC_CASH_LOAD_CONTACT = 0x23;
    /**
     * EC cash load (contactless)<p>
     */
    public static final int EC_CASH_LOAD_CTLS = 0x33;
    /**
     * EC cash balance inquiry (contact)<p>
     */
    public static final int EC_CASH_BALANCE_CONTACT = 0x25;
    /**
     * EC cash balance inquiry (contactless)<p>
     */
    public static final int EC_CASH_BALANCE_CTLS = 0x34;

    /**
     * Query account info(contactless)<p>
     * <p>
     */
    public static final int ACCOUNT_INFO_CTLS = 0x98;
//	/**
//	 * 电子现金圈提(暂未实现)
//	 */
//	public static final int EC_CASH_UNLOAD = 0x24;
//	/**
//	 * EC log (IC card inserted)<p>
//	 */
//	public static final int PBOC_LOGGER = 0x0A;
//	/**
//	 * EC log (contactless)<p>
//	 */
//	public static final int RF_PBOC_LOGGER = 0x37;
//	/**
//	 * EC load log (IC card inserted)<p>
//	 * @since 2.0.0
//	 */
//	public static final int EC_LOGGER = 0x0E;
//	/**
//	 * EC load log (contactless)<p>
//	 * @since 2.0.0
//	 */
//	public static final int RF_EC_LOGGER = 0x40;
}
