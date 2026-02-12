package com.newland.sdk.me.module.emv.structure;

/**
 * Terminal capabilities<p>
 *
 *
 */
public class TerminalCapability extends AbstractBitSetting {

    /**
     * Manual keyboard input
     */
    public static final BitTag TC_Manual_Key_Entry = new BitTag(0x0080);
    /**
     * Magnetic stripe card
     */
    public static final BitTag TC_Magnetic_Stripe = new BitTag(0x0040);
    /**
     * Contact type IC card
     */
    public static final BitTag TC_IC_With_Contacts = new BitTag(0x0020);
    /**
     * Plain test PIN verification
     */
    public static final BitTag TC_Plaintext_PIN = new BitTag(0x0180);
    /**
     * Online enciphered PIN verification
     */
    public static final BitTag TC_Enciphered_PIN_Online = new BitTag(0x0140);
    /**
     * Signature (paper)
     */
    public static final BitTag TC_Signature_Paper = new BitTag(0x0120);
    /**
     * Offline enciphered PIN verification
     */
    public static final BitTag TC_Enciphered_PIN_Offline = new BitTag(0x0110);
    /**
     * No CVM required
     */
    public static final BitTag TC_No_CVM_Required = new BitTag(0x0108);
    /**
     * Card holder certificate show
     */
    public static final BitTag TC_Cardholder_Cert = new BitTag(0x0101);
    /**
     * SDAStatic data authentication SDA
     */
    public static final BitTag TC_SDA = new BitTag(0x0280);
    /**
     * Dynamic data authentication DDA
     */
    public static final BitTag TC_DDA = new BitTag(0x0240);
    /**
     * <   Card capture
     */
    public static final BitTag TC_Card_Capture = new BitTag(0x0220);
    /**
     * <  Compound dynamic data authentication/application enciphered text generation CDA
     */
    public static final BitTag TC_CDA = new BitTag(0x0208);

    public TerminalCapability() {
        super(3);
    }

}
