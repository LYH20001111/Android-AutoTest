package com.newland.sdk.module.externalPin;

public enum ScanErrorCode {
    /**
     * Device failed to open
     */
    OPEN_SCAN_FAILED(1, "Device failed to open"),
    /**
     * The device does not support scanning heads
     */
    NOT_SUPPORTED_SCAN_HEAD(2, "The device does not support scanning heads"),
    /**
     * stop scanning failed
     */
    STOP_SCAN_FAILED(4, "stop scanning failed"),
    /**
     * Parameter error
     */
    PARAMETER_ERROR(05, "Parameter error"),
    /**
     * Wrong command length
     */
    WRONG_COMMAND_LENGTH(45, "Wrong command length"),
    /**
     * Message type unmatched
     */
    MESSAGE_TYPE_UNMATCHED(-1, "Message type unmatched"),
    FUNCTION_ID_UNMATCHED(-3, "Function ID unmatched"),
    /**
     * Undefined
     */
    UNDEFINED(-2, "Undefined");

    private int code;
    private String msg;

    private ScanErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
