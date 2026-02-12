package com.newland.nsdk.core.external.command.emv.terminalconfiguration;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.external.exception.NSDKExternalDeviceException;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.external.command.emv.ExternalEmvL3Utils;
import com.newland.nsdk.core.external.command.exception.ExternalErrorMessage;
import com.newland.nsdk.core.external.command.exception.ExternalMessageException;
import com.newland.nsdk.core.external.command.message.ExternalMessage;
import com.newland.nsdk.core.external.command.message.ExternalMessageType;
import com.newland.nsdk.core.external.command.message.functionId.EmvFunctionId;

/**
 * Provides the ability to manage terminal configuration.
 */
public class ExternalTerminalConfigModule {
    /**
     * Update terminal configuration.
     *
     * @param cardType Indicates which card interface needs to update configuration.
     *                      <ul>
     *                          <li>0x01: Contact card.</li>
     *                          <li>0x02: Contactless card.</li>
     *                      </ul>
     * @param tlvListData   Terminal configuration in TLV list format.
     * @throws NSDKException
     */
    public void updateTerminalConfiguration(int cardType, byte[] tlvListData) throws NSDKException {
        if (tlvListData == null || tlvListData.length == 0) {
            throw new NSDKIllegalParameterException("No configuration to update.");
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.EMV_REQUEST);

        // Request message data =  Function ID(1 byte) + Card interface(1 byte) + TLV len(2 bytes) + TLV data
        byte[] requestMessageData = new byte[1 + 1 + 2 + tlvListData.length];
        int offset = 0;
        requestMessageData[offset] = EmvFunctionId.UPDATE_TERMINAL_CONFIG;
        offset++;
        requestMessageData[offset] = (byte)cardType;
        offset++;
        System.arraycopy(ExternalMessage.intToHexBuf(tlvListData.length), 0, requestMessageData, offset, 2);
        offset += 2;
        System.arraycopy(tlvListData, 0, requestMessageData, offset, tlvListData.length);
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.EMV_RESPONSE, EmvFunctionId.UPDATE_TERMINAL_CONFIG);

        // Response message data = Function ID(1 byte) + Response code(2 bytes)
        ExternalEmvL3Utils.checkResponseCode(responseMessage);
    }

    /**
     * Get terminal configuration.
     *
     * @param cardType Indicates which card interface to get terminal configuration.
     *                      <ul>
     *                          <li>0x01: Contact card.</li>
     *                          <li>0x02: Contactless card.</li>
     *                      </ul>
     * @return The configuration data. Return null when error occurs(Invalid parameters or command failed).
     * @throws NSDKException
     */
    public byte[] getTerminalConfiguration(int cardType) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.EMV_REQUEST);

        // Request message data =  Function ID(1 byte) + Card interface(1 byte)
        byte[] requestMessageData = new byte[2];
        requestMessageData[0] = EmvFunctionId.GET_TERMINAL_CONFIG;
        requestMessageData[1] = (byte)cardType;
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.EMV_RESPONSE, EmvFunctionId.GET_TERMINAL_CONFIG);

        // Response message data = Function ID(1 byte) + Response code(2 bytes) + TLV len(2 bytes) + TLV list data
        byte[] responseMessageData = responseMessage.getMessageData();
        ExternalEmvL3Utils.checkResponseCode(responseMessage);

        if (responseMessageData.length < 5) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }

        byte[] tlvDataLenBuf = new byte[2];
        tlvDataLenBuf[0] = responseMessageData[3];
        tlvDataLenBuf[1] = responseMessageData[4];
        int tlvListDataLen = ExternalMessage.hexBuffer2Int(tlvDataLenBuf);
        byte[] tlvListData = null;

        if (tlvListDataLen > 0) {
            if (tlvListDataLen > responseMessageData.length - 5) {
                throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LEN_FIELD_ERROR, ExternalErrorMessage.DATA_LEN_FIELD_ERROR);
            }

            tlvListData = new byte[tlvListDataLen];
            System.arraycopy(responseMessageData, 5, tlvListData, 0, tlvListDataLen);
        }

        return tlvListData;
    }

    public void removeTerminalConfiguration(int cardType) throws NSDKException {
        if (cardType != 1 && cardType != 2) {
            throw new NSDKIllegalParameterException("Card type shall be 1(Contact Card) or 2(Contactless Card).");
        }
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.EMV_REQUEST);
        byte[] requestMessageData = new byte[2];
        byte functionID = EmvFunctionId.REMOVE_TERMINAL_CONFIGURATION;
        requestMessageData[0] = functionID;
        requestMessageData[1] = (byte) cardType;
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.EMV_RESPONSE, functionID);
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 3) {
            throw new NSDKExternalDeviceException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }
        ExternalEmvL3Utils.checkResponseCode(responseMessage);


    }
}
