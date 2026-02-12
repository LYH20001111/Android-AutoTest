package com.newland.sdk.module.emv;

/**
 * @description: Card holder certificate type. Used for pboc indication.
 * @author: Lindan
 * @create: 2019/7/29
 */
public enum IDCardType {
	/**
	 *  Citizen ID card
	 */
	CITIZEN_IDCARD,
	/**
	 * Military ID card
	 */
	MILITARY_IDCARD,
	/**
	 * Passport
	 */
	PASSPORT,
	/**
	 * Entry permit
	 */
	ENTRY_PERMIT,
	/**
	 * Temporary citizen ID card
	 */
	TEMPORARY_CITIZEN_IDCARD,
	/**
	 * Others
	 */
	OTHERS
}
