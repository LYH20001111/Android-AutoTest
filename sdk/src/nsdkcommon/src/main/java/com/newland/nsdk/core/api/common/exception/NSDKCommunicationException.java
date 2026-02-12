package com.newland.nsdk.core.api.common.exception;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;

/**
 * This exception will be thrown when error occurs during communication.
 */
public class NSDKCommunicationException extends NSDKException {

    public NSDKCommunicationException() {
        super();
        this.code = ErrorCode.EXT_COMMUNICATION_ERROR;
        this.message = "Communication error";
    }

    public NSDKCommunicationException(int code, String message, Throwable throwable) {
        super(code, message, throwable);
    }

    public NSDKCommunicationException(int code, String message) {
        super(code, message);
    }

    public NSDKCommunicationException(int code, String message, int innerError, Throwable throwable) {
        super(code, message, innerError, throwable);
    }

    public NSDKCommunicationException(int code, String message, int innerError) {
        super(code, message, innerError);
    }

    public NSDKCommunicationException(String message) {
        super(ErrorCode.EXT_COMMUNICATION_ERROR, message);
    }

    public NSDKCommunicationException(String message, int innerError) {
        super(ErrorCode.EXT_COMMUNICATION_ERROR, message, innerError);
    }

    public NSDKCommunicationException(Throwable throwable) {
        super(throwable);
        this.code = ErrorCode.EXT_COMMUNICATION_ERROR;
    }
}
