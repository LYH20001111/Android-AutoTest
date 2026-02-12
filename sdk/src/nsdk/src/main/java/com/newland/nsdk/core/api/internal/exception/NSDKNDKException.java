package com.newland.nsdk.core.api.internal.exception;

import com.newland.nsdk.core.api.common.exception.NSDKException;

/**
 * This exception will be thrown when error returned by NDK layer.
 */
public class NSDKNDKException extends NSDKException {

    public NSDKNDKException() {
        super();
        this.message = "NAPI/NDK error";
    }

    public NSDKNDKException(String message) {
        super(message);
    }

    public NSDKNDKException(int code, String message) {
        super(code, message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
