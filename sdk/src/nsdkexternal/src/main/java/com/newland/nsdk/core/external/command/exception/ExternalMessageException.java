package com.newland.nsdk.core.external.command.exception;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;

/**
 * This exception will be thrown when packing/unpacking messages.
 */
public class ExternalMessageException extends NSDKException {
    public ExternalMessageException() {
        super();
        this.code = ErrorCode.EXT_MESSAGE_ERROR;
        this.message = "External message error";
    }

    public ExternalMessageException(int code, String message, Throwable throwable) {
        super(code, message, throwable);
    }

    public ExternalMessageException(int code, String message) {
        super(code, message);
    }

    public ExternalMessageException(String message) {
        super(message);
    }

    public ExternalMessageException(Throwable throwable) {
        super(throwable);
    }
}
