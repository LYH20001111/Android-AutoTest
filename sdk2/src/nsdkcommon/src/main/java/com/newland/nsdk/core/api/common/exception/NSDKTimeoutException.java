package com.newland.nsdk.core.api.common.exception;

import com.newland.nsdk.core.api.common.ErrorCode;

/**
 * This exception will be thrown when the action dose not finished within timeout.
 */
public class NSDKTimeoutException extends NSDKException {
    public NSDKTimeoutException() {
        super();
        this.code = ErrorCode.TIMEOUT;
        this.message = "Timeout";
    }

    public NSDKTimeoutException(String message) {
        super(ErrorCode.TIMEOUT, message);
    }

    public NSDKTimeoutException(String message, int innerError) {
        super(ErrorCode.TIMEOUT, message, innerError);
    }

    public NSDKTimeoutException(int code, String message) {
        super(code, message);
    }

    public NSDKTimeoutException(int code, String message, int innerError) {
        super(code, message, innerError);
    }
}
