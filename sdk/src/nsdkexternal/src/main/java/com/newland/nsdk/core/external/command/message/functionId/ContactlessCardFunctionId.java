package com.newland.nsdk.core.external.command.message.functionId;

public class ContactlessCardFunctionId {
    public static final byte CHECK_READER = 0x02;
    public static final byte ACTIVE_FIELD = 0x30;
    public static final byte ACTIVE_RF = 0x39;
    public static final byte CHECK_CARD_PRESENCE = 0x31;
    public static final byte EXCHANGE_APDU = 0x32;
    public static final byte DEACTIVATE = 0x33;
    public static final byte FLASH_LED = 0x34;
    public static final byte EXCHANGE_PLAINTEXT_APDU = 0x35;
    public static final byte CHECK_FELICA_CARD_PRESENCE = 0x36;
    public static final byte EXCHANGE_APDU_FELICA = 0x37;
    public static final byte PRESENCE = 0x40;
    public static final byte AUTHENTICATION_WITH_EXTERNAL_KEY = 0x44;
    public static final byte READ_BLOCK_DATA = 0x45;
    public static final byte WRITE_BLOCK_DATA = 0x46;
    public static final byte INCREMENT = 0x47;
    public static final byte DECREMENT = 0x48;
    public static final byte TRANSFER = 0x49;
    public static final byte RESTORE = 0x4A;
    public static final byte GET_ATS = 0x4B;
}
