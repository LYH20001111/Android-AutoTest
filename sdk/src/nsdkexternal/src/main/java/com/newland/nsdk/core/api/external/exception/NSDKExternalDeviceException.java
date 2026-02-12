package com.newland.nsdk.core.api.external.exception;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.external.command.exception.ExternalErrorMessage;

public class NSDKExternalDeviceException extends NSDKException {

    public NSDKExternalDeviceException() {
        super();
        this.code = ErrorCode.EXT_ERROR;
        this.message = "External device error";
    }

    public NSDKExternalDeviceException(int code, String message, Throwable throwable) {
        super(code, message, throwable);
    }

    public NSDKExternalDeviceException(int code, String message) {
        super(code, message);
    }

    public NSDKExternalDeviceException(String message) {
        super(ErrorCode.EXT_ERROR, message);
    }

    public NSDKExternalDeviceException(int code, String message, int innerError, Throwable throwable) {
        super(code, message, innerError, throwable);
    }

    public NSDKExternalDeviceException(int code, String message, int innerError) {
        super(code, message, innerError);
    }

    public NSDKExternalDeviceException(String message, int innerError) {
        super(ErrorCode.EXT_ERROR, message, innerError);
    }

    public NSDKExternalDeviceException(Throwable throwable) {
        super(throwable);
        this.code = ErrorCode.EXT_ERROR;
    }
}
