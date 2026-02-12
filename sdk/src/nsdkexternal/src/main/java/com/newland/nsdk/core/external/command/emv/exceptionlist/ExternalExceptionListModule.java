package com.newland.nsdk.core.external.command.emv.exceptionlist;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.external.command.emv.ExternalEmvL3Utils;
import com.newland.nsdk.core.external.command.exception.ExternalErrorMessage;
import com.newland.nsdk.core.external.command.exception.ExternalMessageException;
import com.newland.nsdk.core.external.command.message.ExternalMessage;
import com.newland.nsdk.core.external.command.message.ExternalMessageType;
import com.newland.nsdk.core.external.command.message.functionId.EmvFunctionId;

/**
 * Provides the ability to manage exception list.
 */
public class ExternalExceptionListModule {
    /**
     * Update exception list.
     *
     * @param pan   Update exception list according to this PAN. The length of PAN shall be >0 and <=19.
     * @param panSn Update exception list according to this PAN SN.
     * @throws NSDKException
     */
    public void updateExceptionList(byte[] pan, byte panSn) throws NSDKException {
        if (pan == null || pan.length == 0 || pan.length > 19) {
            throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_PAN);
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.EMV_REQUEST);

        // Request message data =  Function ID(1 byte) + PAN(19 bytes) + PAN len(1 byte) + PAN SN(1 byte)
        packRequestMessageData(EmvFunctionId.UPDATE_EXCEPTION_LIST, pan, panSn, requestMessage);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.EMV_RESPONSE, EmvFunctionId.UPDATE_EXCEPTION_LIST);

        // Response message data = Function ID(1 byte) + Response code(2 bytes)
        ExternalEmvL3Utils.checkResponseCode(responseMessage);
    }

    /**
     * Check if the exception list exist.
     *
     * @param pan   Check exception list according to this PAN.
     * @param panSn Check exception list according to this PAN SN.
     * @return The result of checking.
     * <ul>
     *     <li>result == true: Found.</li>
     *     <li>result == false: Not found.</li>
     * </ul>
     * @throws NSDKException
     */
    public boolean checkExceptionList(byte[] pan, byte panSn) throws NSDKException {
        if (pan == null || pan.length == 0 || pan.length > 19) {
            throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_PAN);
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.EMV_REQUEST);

        // Request message data =  Function ID(1 byte) + PAN(19 bytes) + PAN len(1 byte) + PAN SN(1 byte)
        packRequestMessageData(EmvFunctionId.GET_EXCEPTION_LIST, pan, panSn, requestMessage);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.EMV_RESPONSE, EmvFunctionId.GET_EXCEPTION_LIST);

        // Response message data = Function ID(1 byte) + Response code(2 bytes) + Result(1 byte)
        byte[] responseMessageData = responseMessage.getMessageData();
        ExternalEmvL3Utils.checkResponseCode(responseMessage);

        if (responseMessageData.length < 4) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }

        return responseMessageData[3] == 0;
    }

    /**
     * Remove an exception list.
     *
     * @param pan   Remove exception list according to this PAN.
     * @param panSn Remove exception list according to this PAN SN.
     * @throws NSDKException
     */
    public void removeExceptionList(byte[] pan, byte panSn) throws NSDKException {
        if (pan == null || pan.length == 0 || pan.length > 19) {
            throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_PAN);
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.EMV_REQUEST);

        // Request message data =  Function ID(1 byte) + PAN(19 bytes) + PAN len(1 byte) + PAN SN(1 byte)
        packRequestMessageData(EmvFunctionId.REMOVE_AN_EXCEPTION_LIST, pan, panSn, requestMessage);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.EMV_RESPONSE, EmvFunctionId.REMOVE_AN_EXCEPTION_LIST);

        // Response message data = Function ID(1 byte) + Response code(2 bytes)
        ExternalEmvL3Utils.checkResponseCode(responseMessage);
    }

    /**
     * Remove all exception lists.
     *
     * @throws NSDKException
     */
    public void removeAllExceptionList() throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.EMV_REQUEST);

        // Request message data = Function ID(1 byte)
        byte[] requestMessageData = new byte[]{EmvFunctionId.REMOVE_ALL_EXCEPTION_LIST};
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.EMV_RESPONSE, EmvFunctionId.REMOVE_ALL_EXCEPTION_LIST);

        // Response message data = Function ID(1 byte) + Response code(2 bytes)
        ExternalEmvL3Utils.checkResponseCode(responseMessage);
    }

    private void packRequestMessageData(byte functionId, byte[] pan, byte panSn, ExternalMessage requestMessage) {
        byte[] requestMessageData = new byte[22];
        int offset = 0;
        requestMessageData[offset] = functionId;
        offset++;
        if (pan.length < 19) {
            byte[] panBuf = new byte[19];
            System.arraycopy(pan, 0, panBuf, 0, pan.length);
            System.arraycopy(panBuf, 0, requestMessageData, offset, panBuf.length);
        } else {
            System.arraycopy(pan, 0, requestMessageData, offset, pan.length);
        }
        offset += 19;
        requestMessageData[offset] = (byte) pan.length;
        offset++;
        requestMessageData[offset] = panSn;
        requestMessage.setMessageData(requestMessageData);
    }
}
