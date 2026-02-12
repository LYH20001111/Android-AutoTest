package com.newland.sdk.me.module.emv;

import com.newland.sdk.mtype.DeviceRTException;
import com.newland.sdk.mtype.common.ErrorCode;

/**
 * EMV transaction exception
 *
 *
 * @since ver3.10.01
 */
public class EMVTransferException extends DeviceRTException {

    public EMVTransferException(String msg) {
        super(ErrorCode.EMV_TRANSFER_FAILED, msg);

    }

    public EMVTransferException(String msg, Throwable e) {
        super(ErrorCode.EMV_TRANSFER_FAILED, msg, e);

    }

    private static final long serialVersionUID = -8492655873479448851L;


}
