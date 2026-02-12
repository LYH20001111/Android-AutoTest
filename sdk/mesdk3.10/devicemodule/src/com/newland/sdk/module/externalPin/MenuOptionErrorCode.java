package com.newland.sdk.module.externalPin;

public enum MenuOptionErrorCode {
    /**
     * Parameter Error
     */
    PAAMETER_ERROR(1,"Parameter Error"),
    /**
     * Unsupported
     */
    UNSUPPORTED_02(2,"Unsupported"),
    /**
     * Command Length Error
     */
    COMMAND_LENGTH_ERROR(45,"Command Length Error"),
    /**
     * Unsupported
     */
    UNSUPPORTED_55(55,"Unsupported"),
    /**
     * Message type unmatched
     */
    MESSAGE_TYPE_UNMATCHED(-1,"Message type unmatched"),
    /**
     * Undefined
     */
    UNDEFINED(-2,"Undefined");

    private int code;
    private String msg;

    private MenuOptionErrorCode(int code,String msg) {
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
