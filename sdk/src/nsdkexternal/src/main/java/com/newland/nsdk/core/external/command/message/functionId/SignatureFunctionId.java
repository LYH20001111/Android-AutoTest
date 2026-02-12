package com.newland.nsdk.core.external.command.message.functionId;

public class SignatureFunctionId {
    public static final byte HANDSHAKE = (byte) 0xA0;
    public static final byte CHECK_PREVIOUS_SIGNATURE = (byte) 0xA1;
    public static final byte INPUT_SIGNATURE = (byte) 0xA2;
    public static final byte REQUEST_FOR_COMPLETING_SIGNATURE = (byte) 0xA3;
    public static final byte SEND_FAILURE_SIGNATURE = (byte) 0xA4;
    public static final byte BATCH_END_RESPONSE = (byte) 0xA5;
    public static final byte BULK_TRANSFER_OF_SUCCESSFUL_MESSAGES = (byte) 0xA8;
    public static final byte BULK_TRANSFER_OF_FAILED_MESSAGES = (byte) 0xA9;
}
