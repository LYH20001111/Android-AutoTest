package com.newland.nsdk.core.external.command.exception;

public class ExternalErrorMessage {
    public static final String GENERAL_ERROR = "General error.";
    public static final String COMMAND_FAILED = "Command failed.";
    public static final String CANCELLED = "Cancelled.";
    public static final String TIMEOUT = "Timeout.";
    public static final String INVALID_RESPONSE_DATA = "Response data not started with STX(0x02).";
    public static final String NO_RESPONSE = "No response from the external device.";
    public static final String NO_RESPONSE_CODE = "No response code.";
    public static final String NO_RESPONSE_DATA = "No response data.";
    public static final String INVALID_PARAMETER = "Invalid parameter error returned by external device.";
    public static final String DATA_LENGTH_NOT_ENOUGH = "Data length is not enough.";
    public static final String DATA_LENGTH_NOT_CORRECT = "Data length not correct, too long or too short.";
    public static final String INVALID_RID = "Invalid RID.";
    public static final String INVALID_CSN = "Invalid CSN.";
    public static final String INVALID_PAN = "Invalid PAN.";
    public static final String INVALID_TIMEOUT_HEX = "The value of timeout shall be >=0 and <=65535(0xFFFF).";
    public static final String INVALID_TIMEOUT_BCD = "The value of timeout shall be >=0 and <=9999.";
    public static final String UNKNOWN_ERROR = "Unknown error.";
    public static final String FUNCTION_ID_ERROR = "Function ID error.";

    /**
     * The value of length field is bigger than length of actual data.
     * <p>Example: {0x04, 0x01, 0x02, 0x03}, the first byte is the length of rest data, but there only 3 bytes followed it.
     * This error will be thrown in this case.</p>
     */
    public static final String DATA_LEN_FIELD_ERROR = "The value of length field is bigger than length of actual data.";
    public static final String DATA_NULL_OR_EMPTY = "Data is null or empty";

    public static final String NO_DATA_TO_SEND = "No data to send.";
    public static final String COMMUNICATOR_NOT_INITIALIZED = "Please init communicator first.";
    public static final String INVALID_COMMAND_SEQUENCE = "Invalid command sequence.";

    public static final String EMPTY_KEY_DATA = "Key data is null or empty.";
    public static final String EMPTY_KSN = "KSN is null or empty.";
    public static final String EMPTY_KCV = "Key check value is null or empty.";
    public static final String EMPTY_IV = "IV shall not be null or empty when CBC.";
    public static final String PINPAD_BAD_KEY_TAG = "Bad key tag.";
    public static final String PINPAD_BAD_KEY_USAGE = "Bad key usage.";
    public static final String PINPAD_BAD_KEY_INDEX = "Bad key index.";
    public static final String PINPAD_BAD_IV_LENGTH = "Bad IV length.";
    public static final String PINPAD_BAD_DATA_LENGTH = "Bad data length.";
    public static final String PINPAD_BAD_CMD_LENGTH = "Bad CMD length.";
    public static final String PINPAD_INVALID_BLOCK = "Invalid block.";
    public static final String BYTE_ARRAY_STREAM_IO_ERROR = "Failed to write data to byte array stream.";

    public static final String MAG_CARD_KEY_MODE_ERROR = "Mag card key mode error.";
    public static final String MAG_CARD_READ_ERROR = "Mag card reading error.";
    public static final String MAG_CARD_GET_TRACK_DATA_ERROR = "Getting track data error.";
    public static final String MAG_CARD_TRACK2_ERROR = "Track 2 error.";
    public static final String MAG_CARD_TRACK_ENCRYPT_ERROR = "Track encryption error.";
    public static final String MAG_CARD_TRACK3_ERROR = "Track 3 error.";
    public static final String KEY_INDEX_ERROR = "Key index error.";

    public static final String IC_CARD_READ_ERROR = "Contact card read error.";

    public static final String CONTACTLESS_CARD_MULTI_CARD_ERROR = "Multi card error.";
    public static final String CONTACTLESS_CARD_NOT_PRESENT = "Card not present.";
    public static final String CONTACTLESS_CARD_OTHER_ERROR = "Other error.";

    public static final String EMV_CANCELLED_BY_HOST = "Cancelled by host.";

    public static final String UPDATER_DELETE_ERROR = "File delete error.";
    public static final String UPDATER_CREATE_ERROR = "Failed to create file.";
    public static final String UPDATER_OPEN_FAILED = "Failed to open file.";
    public static final String UPDATER_WRITE_FAILED = "Failed to write file.";

    public static final String FAILED_TO_OPEN_EXTERNAL_DEVICE = "Failed to open external device.";
    public static final String FAILED_TO_CLOSE_EXTERNAL_DEVICE = "Failed to close external device";
    public static final String FAILED_TO_SEND_DATA = "Failed to send data.";
    public static final String BLUETHOOTH_DISCONNECTED = "Bluetooth disconnected.";
    public static final String BLUETHOOTH_DISABLED = "Bluetooth disabled.";
    public static final String NOT_SUPPORTED = "Not supported.";
    public static final String KEY_TYPE_ERROR = "Key type error.";
    public static final String KCV_ERROR = "KCV error.";
    public static final String KEY_EXIST = "Key already exist.";

    public static final String MESSAGE_EXTRACT_ERROR = "Failed to extract response data.";
}
