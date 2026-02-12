package com.newland.nsdk.core.external.command.emv.revocationlist;

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
 * Provides the ability to manage revocation list.
 */
public class ExternalRevocationListModule {
    /**
     * Update revocation list.
     *
     * @param rid   Update revocation list according to this RID.
     * @param index Update revocation list according to this index.
     * @param csn   Update revocation list according to this CSN.
     * @throws NSDKException
     */
    public void updateRevocationList(byte[] rid, byte index, byte[] csn) throws NSDKException {
        if (rid == null || rid.length != 5) {
            throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_RID);
        }

        if (csn == null || csn.length != 3) {
            throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_CSN);
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.EMV_REQUEST);

        // Request message data =  Function ID(1 byte) + RID(5 bytes) + Index(1 byte) + CSN(3 bytes)
        packRequestMessageData(EmvFunctionId.UPDATE_REVOCATION_LIST, rid, index, csn, requestMessage);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.EMV_RESPONSE, EmvFunctionId.UPDATE_REVOCATION_LIST);

        // Response message data = Function ID(1 byte) + Response code(2 bytes)
        ExternalEmvL3Utils.checkResponseCode(responseMessage);
    }

    /**
     * Check if the revocation list exist.
     *
     * @param rid   Check revocation list according to this RID.
     * @param index Check revocation list according to this index.
     * @param csn   Check revocation list according to this CSN.
     * @return The result of checking.
     * <ul>
     *     <li>result == true: Found.</li>
     *     <li>result == false: Not found.</li>
     * </ul>
     * @throws NSDKException
     */
    public boolean checkRevocationList(byte[] rid, byte index, byte[] csn) throws NSDKException {
        if (rid == null || rid.length != 5) {
            throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_RID);
        }

        if (csn == null || csn.length != 3) {
            throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_CSN);
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.EMV_REQUEST);

        // Request message data =  Function ID(1 byte) + RID(5 bytes) + Index(1 byte) + CSN(3 bytes)
        packRequestMessageData(EmvFunctionId.GET_REVOCATION_LIST, rid, index, csn, requestMessage);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.EMV_RESPONSE, EmvFunctionId.GET_REVOCATION_LIST);

        // Response message data = Function ID(1 byte) + Response code(2 bytes) + Result(1 byte)
        byte[] responseMessageData = responseMessage.getMessageData();
        ExternalEmvL3Utils.checkResponseCode(responseMessage);

        if (responseMessageData.length < 4) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }

        return responseMessageData[3] == 0;
    }

    /**
     * Remove a revocation list.
     *
     * @param rid   Remove revocation list according to this RID.
     * @param index Remove revocation list according to this index.
     * @param csn   Remove revocation list according to this CSN.
     * @throws NSDKException
     */
    public void removeRevocationList(byte[] rid, byte index, byte[] csn) throws NSDKException {
        if (rid == null || rid.length != 5) {
            throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_RID);
        }

        if (csn == null || csn.length != 3) {
            throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_CSN);
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.EMV_REQUEST);

        // Request message data =  Function ID(1 byte) + RID(5 bytes) + Index(1 byte) + CSN(3 bytes)
        packRequestMessageData(EmvFunctionId.REMOVE_A_REVOCATION_LIST, rid, index, csn, requestMessage);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.EMV_RESPONSE, EmvFunctionId.REMOVE_A_REVOCATION_LIST);

        // Response message data = Function ID(1 byte) + Response code(2 bytes)
        ExternalEmvL3Utils.checkResponseCode(responseMessage);
    }

    /**
     * Remove all revocation lists.
     *
     * @throws NSDKException
     */
    public void removeAllRevocationList() throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.EMV_REQUEST);

        // Request message data = Function ID(1 byte)
        byte[] requestMessageData = new byte[]{EmvFunctionId.REMOVE_ALL_REVOCATION_LIST};
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.EMV_RESPONSE, EmvFunctionId.REMOVE_ALL_REVOCATION_LIST);

        // Response message data = Function ID(1 byte) + Response code(2 bytes)
        ExternalEmvL3Utils.checkResponseCode(responseMessage);
    }

    private void packRequestMessageData(byte functionId, byte[] rid, byte index, byte[] csn, ExternalMessage requestMessage) {
        byte[] requestMessageData = new byte[10];
        int offset = 0;
        requestMessageData[offset] = functionId;
        offset++;
        System.arraycopy(rid, 0, requestMessageData, offset, rid.length);
        offset += rid.length;
        requestMessageData[offset] = index;
        offset++;
        System.arraycopy(csn, 0, requestMessageData, offset, csn.length);
        requestMessage.setMessageData(requestMessageData);
    }
}
