package com.newland.sdk.me.module.emv.structure;

/**
 * Supported transaction type setting<p>
 *
 *
 *
 */
public class TransferProperty extends AbstractBitSetting {
	/*
	 * Terminal transaction attribute (9F66)
	 */
	/** <  1: Support contactless magnetic stripe (MSD)*/
	public static final BitTag EMV_PROP_MSD = new BitTag(0x0080);
	/**   1: Support contactless PBOC */
	public static final BitTag EMV_PROP_PBOCCLSS = new BitTag(0x0040);
	/**   1: Support contactless qPBOC*/
	public static final BitTag EMV_PROP_QPBOC = new BitTag(0x0020);
	/** <  1 Support contact type PBOC */
	public static final BitTag EMV_PROP_PBOC = new BitTag(0x0010);
	/**
	 * <   1: Reader writer only supports offline 0: Reader writer has online capability
	 */
	public static final BitTag EMV_PROP_OFFLINE_ONLY = new BitTag(0x0008);
	/** <   1: Support online PIN*/
	public static final BitTag EMV_PROP_ONLINEPIN = new BitTag(0x0004);
	/** <   1: Support signature  */
	public static final BitTag EMV_PROP_SIGNATURE = new BitTag(0x0002);
	// < byte1 bit1 reserve

	/** <  The default setting for the following macros and reserve bits is 0 */

	/** <   Require online enciphered text  */
	public static final BitTag EMV_PROP_ONLINEAC = new BitTag(0x0180);
	/** <  Require CVM*/
	public static final BitTag EMV_PROP_CVM = new BitTag(0x0140);
	/** other bits reserve */
	public static final BitTag EMV_PROP_01VERSUPPORT = new BitTag(0x0380);


	public TransferProperty() {
		super(4);
	}

}
