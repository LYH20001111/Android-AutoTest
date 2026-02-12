package com.newland.nsdk.core.external.command.emv;

public class ExternalEmvCallbackID {
    public static final byte UI_EVENT = 1;
    public static final byte SELECT_CANDIDATE_LIST = 4;
    public static final byte AFTER_FINAL_SELECT = 0x0A;
    public static final byte CHECK_CREDENTIALS = 7;
    public static final byte PIN_ENTRY_DEAL = 2;
    public static final byte CONFIRM_CARD_NUM = 0x0D;
    public static final byte HOST_UI_EVENT = (byte) 0x81;
    public static final byte CALLBACK_RESPONSE = (byte)0xFF;
}
