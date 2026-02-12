package com.newland.nsdk.core.external.command.smartcard;

import android.text.TextUtils;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.exception.NSDKTimeoutException;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.external.command.exception.ExternalErrorMessage;
import com.newland.nsdk.core.external.command.message.ExternalMessage;
import com.newland.nsdk.core.external.command.message.ExternalMessageType;
import com.newland.nsdk.core.external.command.message.functionId.IcCardFunctionId;
import com.newland.nsdk.core.api.external.card.ExtAPDUOutput;
import com.newland.nsdk.core.api.external.exception.NSDKExternalDeviceException;
import com.newland.nsdk.core.external.command.exception.ExternalMessageException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Provides the ability to operate contact smart card.
 */
public class ExternalSmartCardModule {
    /**
     * This contains the following operations:
     * <ol>
     *     <li>Start to wait for smart card.</li>
     *     <li>Power up the card after it is inserted.</li>
     *     <li>Execute APDU command with the card.</li>
     *     <li>Power down the card.</li>
     * </ol>
     *
     * @param timeout                                             Timeout for reading smart card. Unit: second.
     * @param apduCommand                                         APDU command sent to the inserted smart card.
     * @param displayLine1,displayLine2,displayLine3,displayLine4 Text to be displayed for line1,line2,line3,line4
     * @return The response data of APDU command.
     * @throws NSDKException
     */
    public byte[] searchCard(int timeout, byte[] apduCommand, String displayLine1, String displayLine2, String displayLine3, String displayLine4) throws NSDKException {

        if (apduCommand == null) {
            throw new NSDKIllegalParameterException("Invalid APDU command.");
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.IC_CARD_REQUEST);

        int len = apduCommand.length;
        byte[] timeBuf = ExternalMessage.intToHexBuf(timeout);
        byte[] lenBuf = ExternalMessage.intToHexBuf(len);

        ByteArrayOutputStream messageStream = new ByteArrayOutputStream();
        try {
            messageStream.write(IcCardFunctionId.READ_IC_CARD);
            messageStream.write(timeBuf);
            messageStream.write(lenBuf);
            messageStream.write(apduCommand);
            if (displayLine1 != null) {
                messageStream.write(displayLine1.getBytes());
                messageStream.write(0x1c);
            }
            if (displayLine2 != null) {
                messageStream.write(displayLine2.getBytes());
                messageStream.write(0x1c);
            }
            if (displayLine3 != null) {
                messageStream.write(displayLine3.getBytes());
                messageStream.write(0x1c);
            }
            if (displayLine4 != null) {
                messageStream.write(displayLine4.getBytes());
                messageStream.write(0x1c);
            }
        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }

        byte[] searchBuf = messageStream.toByteArray();

        requestMessage.setMessageData(searchBuf);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceive(requestMessage, ExternalMessageType.IC_CARD_RESPONSE,
                IcCardFunctionId.READ_IC_CARD, timeout * 1000);

        byte[] responseMessageData = responseMessage.getMessageData();

        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(new byte[]{responseMessageData[1], responseMessageData[2]});
        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 3);
            if (1 == responseCode) {
                throw new NSDKTimeoutException(ExternalErrorMessage.TIMEOUT, innerErrorCode);
            }
            if (2 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_IC_CARD_READ_ERROR, ExternalErrorMessage.IC_CARD_READ_ERROR, innerErrorCode);
            }
            if (3 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_MESSAGE_INVALID_FUNCTION_ID, ExternalErrorMessage.FUNCTION_ID_ERROR, innerErrorCode);
            }
            if (5 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.CANCELLED, ExternalErrorMessage.CANCELLED, innerErrorCode);
            }
            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }

        byte[] dataLen = new byte[2];
        System.arraycopy(responseMessageData, 3, dataLen, 0, 2);
        int ICCardResultDataLen = ExternalMessage.hexBuffer2Int(dataLen);

        byte[] ICCardResultData = new byte[ICCardResultDataLen];
        System.arraycopy(responseMessageData, 5, ICCardResultData, 0, ICCardResultDataLen);
        return ICCardResultData;
    }

    /**
     * Power up the card.
     *
     * @param timeout  Timeout for powering up. Unit: ms.
     * @param messages Text to be displayed for line1,line2,line3,line4
     * @return ATR data.
     * @throws NSDKException
     */
    public byte[] powerUp(int timeout, String[] messages) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.IC_CARD_REQUEST);

        byte[] timeBuf = ExternalMessage.intToHexBuf(timeout);

        ByteArrayOutputStream messageStream = new ByteArrayOutputStream();
        try {
            messageStream.write(IcCardFunctionId.IC_CARD_POWER_UP);
            messageStream.write(timeBuf);
            if (messages != null && messages.length > 0) {
                for (String m : messages) {
                    if (!TextUtils.isEmpty(m)) {
                        messageStream.write(m.getBytes());
                        messageStream.write(0x1c);
                    }
                }
            }
        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }

        byte[] powerUpBuf = messageStream.toByteArray();
        requestMessage.setMessageData(powerUpBuf);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.IC_CARD_RESPONSE, IcCardFunctionId.IC_CARD_POWER_UP);

        byte[] responseMessageData = responseMessage.getMessageData();

        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(new byte[]{responseMessageData[1], responseMessageData[2]});
        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 3);
            if (1 == responseCode) {
                // 如果是 timeout，则转成无卡错误
                throw new NSDKExternalDeviceException(ErrorCode.EXT_IC_CARD_NO_CARD, "No card inserted.", innerErrorCode);
            }
            if (2 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_IC_CARD_READ_ERROR, ExternalErrorMessage.IC_CARD_READ_ERROR, innerErrorCode);
            }
            if (46 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.CANCELLED, ExternalErrorMessage.CANCELLED, innerErrorCode);
            }
            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }

        byte[] dataLen = new byte[2];
        System.arraycopy(responseMessageData, 3, dataLen, 0, 2);
        int atrLen = ExternalMessage.hexBuffer2Int(dataLen);

        byte[] atrBuf = new byte[atrLen];
        System.arraycopy(responseMessageData, 5, atrBuf, 0, atrLen);
        return atrBuf;
    }

    /**
     * Power down the card.
     *
     * @throws NSDKException
     */
    public void powerDown() throws NSDKException {

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.IC_CARD_REQUEST);


        ByteArrayOutputStream messageStream = new ByteArrayOutputStream();
        messageStream.write(IcCardFunctionId.IC_CARD_POWER_DOWN);

        byte[] powerDownBuf = messageStream.toByteArray();
        requestMessage.setMessageData(powerDownBuf);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.IC_CARD_RESPONSE, IcCardFunctionId.IC_CARD_POWER_DOWN);

        byte[] responseMessageData = responseMessage.getMessageData();

        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(new byte[]{responseMessageData[1], responseMessageData[2]});
        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 3);
            if (1 == responseCode) {
                throw new NSDKTimeoutException(ExternalErrorMessage.TIMEOUT, innerErrorCode);
            }
            if (2 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_IC_CARD_READ_ERROR, ExternalErrorMessage.IC_CARD_READ_ERROR, innerErrorCode);
            }
            if (46 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.CANCELLED, ExternalErrorMessage.CANCELLED, innerErrorCode);
            }
            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }

    }

    /**
     * Execute APDU command with the card.
     *
     * @param keyId       Key used to encrypt the command data. Only support data key.
     *                    <ul>
     *                    <li>When key id is 0: APDU command data is plain text.</li>
     *                    <li>When key id is in range [129-255]: APDU command data is encrypted by the data key.</li>
     *                    </ul>
     * @param keyType     Key Type.
     *                    <ul>
     *                    <li>0 - DES</li>
     *                    <li>1 = AES</li>
     *                    </ul>
     * @param keyMode     Key mode.
     *                    <ul>
     *                    <li>0 - ECB</li>
     *                    <li>1 - CBC</li>
     *                    </ul>
     * @param iv          CBC initial value.
     * @param apduCommand APDU command data.
     * @return APDU response data.
     * @throws NSDKException
     */
    public ExtAPDUOutput exchangeAPDU(byte keyId, byte keyType, byte keyMode, byte[] iv, byte[] apduCommand) throws NSDKException {
        if (apduCommand == null || apduCommand.length == 0) {
            throw new NSDKIllegalParameterException("Invalid APDU command data.");
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.IC_CARD_REQUEST);

        int len = apduCommand.length;
        byte[] lenBuf = ExternalMessage.intToHexBuf(len);

        ByteArrayOutputStream messageStream = new ByteArrayOutputStream();
        try {
            messageStream.write(IcCardFunctionId.IC_CARD_RW);
            messageStream.write(keyId);
            if (keyId != 0) {
                messageStream.write(keyType);
                messageStream.write(keyMode);
                if (keyMode == 1 && (iv != null && iv.length > 0)) {
                    messageStream.write(iv);
                }
            }

            messageStream.write(lenBuf);
            messageStream.write(apduCommand);
        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }

        byte[] apduBuf = messageStream.toByteArray();


        requestMessage.setMessageData(apduBuf);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.IC_CARD_RESPONSE, IcCardFunctionId.IC_CARD_RW);

        byte[] responseMessageData = responseMessage.getMessageData();

        if (responseMessageData == null || responseMessageData.length < 3) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(new byte[]{responseMessageData[1], responseMessageData[2]});
        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 3);
            if (1 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
            }
            if (2 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_IC_CARD_READ_ERROR, ExternalErrorMessage.IC_CARD_READ_ERROR, innerErrorCode);
            }
            if (3 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_MESSAGE_INVALID_FUNCTION_ID, ExternalErrorMessage.FUNCTION_ID_ERROR, innerErrorCode);
            }
            if (4 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_KEY_INDEX, ExternalErrorMessage.KEY_INDEX_ERROR, innerErrorCode);
            }
            if (5 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.CANCELLED, ExternalErrorMessage.CANCELLED, innerErrorCode);
            }
            if (6 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_KEY_TYPE_ERROR, ExternalErrorMessage.KEY_TYPE_ERROR, innerErrorCode);
            }
            if (7 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_KEY_MODE_ERROR, "Encrypting key mode error.", innerErrorCode);
            }
            if (8 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_IC_CARD_DECRYPTION_ERROR, "Decrypt error.", innerErrorCode);
            }
            if (9 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_IC_CARD_ENCRYPTION_ERROR, "Encrypt error.", innerErrorCode);
            }
            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }

        int offset = 4;
        ExtAPDUOutput apduOutput = new ExtAPDUOutput();
        if (ExternalMessage.isDataEnough(offset, responseMessageData.length, 2)) {
            apduOutput.setDataLen(ExternalMessage.hexBuffer2Int(new byte[]{responseMessageData[offset], responseMessageData[offset + 1]}));
            offset += 2;

            if (ExternalMessage.isDataEnough(offset, responseMessageData.length, 2)) {
                int encryptedDataLen = ExternalMessage.hexBuffer2Int(new byte[]{responseMessageData[offset], responseMessageData[offset + 1]});
                offset += 2;
                if (encryptedDataLen > 0 && responseMessageData.length - offset >= encryptedDataLen) {
                    byte[] apduResultData = new byte[encryptedDataLen];
                    System.arraycopy(responseMessageData, offset, apduResultData, 0, encryptedDataLen);
                    apduOutput.setData(apduResultData);
                } else {
                    throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LEN_FIELD_ERROR, ExternalErrorMessage.DATA_LEN_FIELD_ERROR);
                }
            }
        } else {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, ExternalErrorMessage.DATA_LENGTH_NOT_ENOUGH);
        }

        return apduOutput;
    }

    public ExtAPDUOutput exchangeAPDU(byte keyId, byte keyType, byte keyMode, byte[] iv, int actualLen, byte[] apduCommand) throws NSDKException {
        if (apduCommand == null || apduCommand.length == 0) {
            throw new NSDKIllegalParameterException("Invalid APDU command data.");
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.IC_CARD_REQUEST);

        byte[] lenBuf = ExternalMessage.intToHexBuf(actualLen);

        ByteArrayOutputStream messageStream = new ByteArrayOutputStream();
        try {
            messageStream.write(IcCardFunctionId.IC_CARD_RW);
            messageStream.write(keyId);
            if (keyId != 0) {
                messageStream.write(keyType);
                messageStream.write(keyMode);
                if (keyMode == 1 && (iv != null && iv.length > 0)) {
                    messageStream.write(iv);
                }
            }

            messageStream.write(lenBuf);
            messageStream.write(apduCommand);
        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }

        byte[] apduBuf = messageStream.toByteArray();


        requestMessage.setMessageData(apduBuf);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.IC_CARD_RESPONSE, IcCardFunctionId.IC_CARD_RW);

        byte[] responseMessageData = responseMessage.getMessageData();

        if (responseMessageData == null || responseMessageData.length < 3) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(new byte[]{responseMessageData[1], responseMessageData[2]});
        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 3);
            if (1 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
            }
            if (2 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_IC_CARD_READ_ERROR, ExternalErrorMessage.IC_CARD_READ_ERROR, innerErrorCode);
            }
            if (3 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_MESSAGE_INVALID_FUNCTION_ID, ExternalErrorMessage.FUNCTION_ID_ERROR, innerErrorCode);
            }
            if (4 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_KEY_INDEX, ExternalErrorMessage.KEY_INDEX_ERROR, innerErrorCode);
            }
            if (5 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.CANCELLED, ExternalErrorMessage.CANCELLED, innerErrorCode);
            }
            if (6 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_KEY_TYPE_ERROR, ExternalErrorMessage.KEY_TYPE_ERROR, innerErrorCode);
            }
            if (7 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_KEY_MODE_ERROR, "Encrypting key mode error.", innerErrorCode);
            }
            if (8 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_IC_CARD_DECRYPTION_ERROR, "Decrypt error.", innerErrorCode);
            }
            if (9 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_IC_CARD_ENCRYPTION_ERROR, "Encrypt error.", innerErrorCode);
            }
            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }

        int offset = 4;
        ExtAPDUOutput apduOutput = new ExtAPDUOutput();
        if (ExternalMessage.isDataEnough(offset, responseMessageData.length, 2)) {
            apduOutput.setDataLen(ExternalMessage.hexBuffer2Int(new byte[]{responseMessageData[offset], responseMessageData[offset + 1]}));
            offset += 2;

            if (ExternalMessage.isDataEnough(offset, responseMessageData.length, 2)) {
                int encryptedDataLen = ExternalMessage.hexBuffer2Int(new byte[]{responseMessageData[offset], responseMessageData[offset + 1]});
                offset += 2;
                if (encryptedDataLen > 0 && responseMessageData.length - offset >= encryptedDataLen) {
                    byte[] apduResultData = new byte[encryptedDataLen];
                    System.arraycopy(responseMessageData, offset, apduResultData, 0, encryptedDataLen);
                    apduOutput.setData(apduResultData);
                } else {
                    throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LEN_FIELD_ERROR, ExternalErrorMessage.DATA_LEN_FIELD_ERROR);
                }
            }
        } else {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, ExternalErrorMessage.DATA_LENGTH_NOT_ENOUGH);
        }

        return apduOutput;
    }

    /**
     * Check if there is smart card inserted in the slot.
     *
     * <p>This will not return until:</p>
     * <ul>
     *     <li>Card detected</li>
     *     <li>Timeout, means there is no card.</li>
     *     <li>Error happened.</li>
     * </ul>
     *
     * @param timeout Timeout for detecting. Unit: ms.
     * @return Whether the card is inserted in the slot or not.
     * <ul>
     *     <li>true - The card is inserted in the slot.</li>
     *     <li>false - No card is inserted in the slot.</li>
     * </ul>
     * @throws NSDKException
     */
    public boolean checkCard(int timeout) throws NSDKException {

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.IC_CARD_REQUEST);

        boolean res = true;

        byte[] timeBuf = ExternalMessage.intToHexBuf(timeout);

        ByteArrayOutputStream messageStream = new ByteArrayOutputStream();
        try {
            messageStream.write(IcCardFunctionId.IC_DETECT);
            messageStream.write(timeBuf);
        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }

        byte[] checkBuf = messageStream.toByteArray();


        requestMessage.setMessageData(checkBuf);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceive(requestMessage, ExternalMessageType.IC_CARD_RESPONSE,
                IcCardFunctionId.IC_DETECT, timeout);

        byte[] responseMessageData = responseMessage.getMessageData();

        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(new byte[]{responseMessageData[1], responseMessageData[2]});
        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 3);
            if (1 == responseCode) {
                throw new NSDKTimeoutException(ExternalErrorMessage.TIMEOUT, innerErrorCode);
            }
            if (2 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_IC_CARD_READ_ERROR, ExternalErrorMessage.IC_CARD_READ_ERROR, innerErrorCode);
            }
            if (3 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_MESSAGE_INVALID_FUNCTION_ID, ExternalErrorMessage.FUNCTION_ID_ERROR, innerErrorCode);
            }
            if (5 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.CANCELLED, ExternalErrorMessage.CANCELLED, innerErrorCode);
            }
            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }
        return res;
    }
}
