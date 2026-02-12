package com.newland.nsdk.core.external.command.emv;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.external.exception.NSDKExternalDeviceException;
import com.newland.nsdk.core.external.command.exception.ExternalErrorMessage;
import com.newland.nsdk.core.external.command.exception.ExternalMessageException;
import com.newland.nsdk.core.external.command.message.ExternalMessage;

public class ExternalEmvL3Utils {
    /**
     * Check response code.
     *
     * @param responseMessage Response message.
     * @throws NSDKException
     */
    public static void checkResponseCode(ExternalMessage responseMessage) throws NSDKException {
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 3) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        byte[] responseCodeBuf = new byte[2];
        System.arraycopy(responseMessageData, 1, responseCodeBuf, 0, 2);
        int responseCode = ExternalMessage.convertResponseCodeBufToInt(responseCodeBuf);
        responseMessage.setResponseCode(responseCode);

        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 3);
            if (1 == responseCode) {
                throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_PARAMETER, innerErrorCode);
            }

            if (2 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_COMMAND_FAILED, ExternalErrorMessage.COMMAND_FAILED, innerErrorCode);
            }

            if (3 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_EMV_CANCELLED_BY_HOST, ExternalErrorMessage.EMV_CANCELLED_BY_HOST, innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }
    }
}
