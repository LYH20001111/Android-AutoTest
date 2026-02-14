package com.newland.nsdk.core.api.common.exception;


import com.newland.nsdk.core.api.common.ErrorCode;

/**
 * This exception will be thrown when the parameter is invalid.
 *
 */
public class NSDKIllegalParameterException extends NSDKException {
    public NSDKIllegalParameterException() {
        super();
        this.code = ErrorCode.PARAM_ERROR;
        this.message = "Invalid parameter";
    }

    public NSDKIllegalParameterException(String message) {
        super(ErrorCode.PARAM_ERROR, message);
    }

    public NSDKIllegalParameterException(String message, int innerError) {
        super(ErrorCode.PARAM_ERROR, message, innerError);
    }

    public NSDKIllegalParameterException(int code, String message) {
        super(code, message);
    }

    public NSDKIllegalParameterException(int code, String message, int innerError) {
        super(code, message, innerError);
    }
}
