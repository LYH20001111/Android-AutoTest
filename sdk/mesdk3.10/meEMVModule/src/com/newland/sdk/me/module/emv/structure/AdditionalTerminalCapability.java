package com.newland.sdk.me.module.emv.structure;

/**
 * Description of expanded terminal capacity <p>
 *
 *
 */
public class AdditionalTerminalCapability extends AbstractBitSetting {

	/** <   Cash */
	public static final BitTag ATC_Cash = new BitTag(0x0080);
	/** <  Goods */
	public static final BitTag ATC_Goods = new BitTag(0x0040);
	/** <  Services*/
	public static final BitTag ATC_Services = new BitTag(0x0020);
	/** <  Cashback*/
	public static final BitTag ATC_Cashback = new BitTag(0x0010);
	/** <  Inquiry*/
	public static final BitTag ATC_Inquiry = new BitTag(0x0008);
	/** <  Transfer*/
	public static final BitTag ATC_Transfer = new BitTag(0x0004);
	/** <  Payment*/
	public static final BitTag ATC_Payment = new BitTag(0x0002);
	/** <  Administrative*/
	public static final BitTag ATC_Administrative = new BitTag(0x0001);
	/** <  Deposit */
	public static final BitTag ATC_Cash_Deposit = new BitTag(0x0180);
	/** <  Numeric keys */
	public static final BitTag ATC_Numeric_Keys = new BitTag(0x0280);
	/** <  Alphabetic Special keys */
	public static final BitTag ATC_Alphabetic_Special_Keys = new BitTag(0x0240);
	/** <  Command keys */
	public static final BitTag ATC_Command_Keys = new BitTag(0x0220);
	/** <  Function keys*/
	public static final BitTag ATC_Function_Keys = new BitTag(0x0210);
	/** <  Print to attendant */
	public static final BitTag ATC_Print_Attendant = new BitTag(0x0380);
	/** <  Print to cardholder */
	public static final BitTag ATC_Print_Cardholder = new BitTag(0x0340);
	/** <  Display to attendant*/
	public static final BitTag ATC_Display_Attendant = new BitTag(0x0320);
	/** <  Display to cardholder*/
	public static final BitTag ATC_Display_Cardholder = new BitTag(0x0310);
	/** <   Code Table 10*/
	public static final BitTag ATC_Code_Table_10 = new BitTag(0x0302);
	/** <   Code Table 9*/
	public static final BitTag ATC_Code_Table_9 = new BitTag(0x0301);
	/** <   Code Table 8*/
	public static final BitTag ATC_Code_Table_8 = new BitTag(0x0480);
	/** <   Code Table 7*/
	public static final BitTag ATC_Code_Table_7 = new BitTag(0x0440);
	/** <   Code Table 6*/
	public static final BitTag ATC_Code_Table_6 = new BitTag(0x0420);
	/** <  Code Table 5*/
	public static final BitTag ATC_Code_Table_5 = new BitTag(0x0410);
	/** <  Code Table 4*/
	public static final BitTag ATC_Code_Table_4 = new BitTag(0x0408);
	/** <   Code Table 3*/
	public static final BitTag ATC_Code_Table_3 = new BitTag(0x0404);
	/** <   Code Table 2*/
	public static final BitTag ATC_Code_Table_2 = new BitTag(0x0402);
	/** <   Code Table 1*/
	public static final BitTag ATC_Code_Table_1 = new BitTag(0x0401);

	public AdditionalTerminalCapability() {
		super(5);
	}

}
