package com.newland.nsdk.core.api.common.exception;

import com.newland.nsdk.core.api.common.ErrorCode;

/**
 * NSDK exception thrown when error occurs.
 */
public class NSDKException extends Exception {
    protected int code = ErrorCode.ERROR;
    protected String message = "NSDK error";
    protected int innerError = ErrorCode.ERROR;

    public NSDKException() {
        super();
    }

    public NSDKException(int code, String message, Throwable throwable) {
        super(message, throwable);
        this.code = code;
        this.message = message;
    }

    public NSDKException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public NSDKException(int code, String message, int innerError, Throwable throwable) {
        super(message, throwable);
        this.code = code;
        this.message = message;
        this.innerError = innerError;
    }

    public NSDKException(int code, String message, int innerError) {
        super(message);
        this.code = code;
        this.message = message;
        this.innerError = innerError;
    }

    public NSDKException(String message) {
        super(message);
        this.message = message;
    }

    public NSDKException(Throwable throwable) {
        super(throwable);
    }

    /**
     * Gets error code.
     *
     * @return Error code.
     */
    public int getCode() {
        return code;
    }

    public String getMessage(){
        return this.message;
    }
}
