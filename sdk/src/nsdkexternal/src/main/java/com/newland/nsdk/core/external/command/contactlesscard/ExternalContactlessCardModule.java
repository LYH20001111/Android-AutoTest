package com.newland.nsdk.core.external.command.contactlesscard;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.card.contactless.ActivationResult;
import com.newland.nsdk.core.api.common.card.contactless.ContactlessCardType;
import com.newland.nsdk.core.api.common.card.contactless.SubContactlessCardType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.exception.NSDKTimeoutException;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.api.external.card.ExtAPDUOutput;
import com.newland.nsdk.core.api.external.exception.NSDKExternalDeviceException;
import com.newland.nsdk.core.external.command.exception.ExternalErrorMessage;
import com.newland.nsdk.core.external.command.exception.ExternalMessageException;
import com.newland.nsdk.core.external.command.message.ExternalMessage;
import com.newland.nsdk.core.external.command.message.ExternalMessageType;
import com.newland.nsdk.core.external.command.message.functionId.ContactlessCardFunctionId;

/**
 * Provides the ability to operate contactless card.
 */
public class ExternalContactlessCardModule {

    /**
     * Check if the reader is connected and functional.
     *
     * @return Reader serial number, right justified and left padded with zeroes.
     * @throws NSDKException
     */
    public byte[] checkReader() throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.CONTACTLESS_CARD_REQUEST);
        requestMessage.setMessageData(new byte[]{ContactlessCardFunctionId.CHECK_READER});
        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.CONTACTLESS_CARD_RESPONSE, ContactlessCardFunctionId.CHECK_READER);

        // Response message data = Function ID(1 byte) + Response code(2 bytes) + Response data
        checkResponseCode(responseMessage);

        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData.length == 3) {
            return null;
        }

        int dataLen = responseMessageData.length - 3;
        byte[] data = new byte[dataLen];
        System.arraycopy(responseMessageData, 3, data, 0, dataLen);
        return data;
    }

    /**
     * Activate card.
     * @throws NSDKException
     */
    public ActivationResult activate(SubContactlessCardType type) throws NSDKException {
        if (type == null) {
            throw new NSDKIllegalParameterException("Contactless card type shall not be null.");
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.CONTACTLESS_CARD_REQUEST);
        byte[] requestMessageData = new byte[2];
        requestMessageData[0] = ContactlessCardFunctionId.ACTIVE_RF;
        if (type == SubContactlessCardType.CPU) {
            requestMessageData[1] = 0;
        } else if (type == SubContactlessCardType.M0 || type == SubContactlessCardType.M1) {
            requestMessageData[1] = 1;
        } else {
            throw new NSDKIllegalParameterException(String.format("Unsupported contactless card type: %s", type));
        }

        requestMessage.setMessageData(requestMessageData);
        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.CONTACTLESS_CARD_RESPONSE, ContactlessCardFunctionId.ACTIVE_RF);
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 3) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        byte[] responseCodeBuf = new byte[]{responseMessageData[1], responseMessageData[2]};
        int responseCode = ExternalMessage.convertResponseCodeBufToInt(responseCodeBuf);
        responseMessage.setResponseCode(responseCode);
        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 3);
            if (1 == responseCode) {
                throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_PARAMETER, innerErrorCode);
            }

            if (2 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_RF_CARD_NOT_PRESENT, ExternalErrorMessage.CONTACTLESS_CARD_NOT_PRESENT, innerErrorCode);
            }

            if (3 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_RF_CARD_ACTIVATE_FAIL, ExternalErrorMessage.CONTACTLESS_CARD_NOT_PRESENT, innerErrorCode);
            }

            if (4 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_RF_CARD_GET_INFO_ERROR, ExternalErrorMessage.CONTACTLESS_CARD_NOT_PRESENT, innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }
        int offset = 3;
        ActivationResult activationResult = new ActivationResult();
        try {
            int uidLen = responseMessageData[offset];
            offset ++;
            if (uidLen > 0) {
                byte[] uid = new byte[uidLen];
                System.arraycopy(responseMessageData, offset, uid, 0, uid.length);
                activationResult.setUID(uid);
                offset += uid.length;
            }

            int atqaLen = responseMessageData[offset];
            offset ++;
            if (atqaLen > 0) {
                byte[] atqa = new byte[atqaLen];
                System.arraycopy(responseMessageData, offset, atqa, 0, atqa.length);
                activationResult.setATQA(atqa);
                offset += atqa.length;
            }

            int atsLen = responseMessageData[offset];
            offset ++;
            if (atsLen > 0) {
                byte[] ats = new byte[atsLen];
                System.arraycopy(responseMessageData, offset, ats, 0, ats.length);
                activationResult.setATS(ats);
                offset += ats.length;
            }

            int atqbLen = responseMessageData[offset];
            offset ++;
            if (atqbLen > 0) {
                byte[] atqb = new byte[atqbLen];
                System.arraycopy(responseMessageData, offset, atqb, 0, atqb.length);
                activationResult.setATQB(atqb);
                offset += atqb.length;
            }

            int sakLen = responseMessageData[offset];
            offset ++;
            if (sakLen > 0) {
                byte[] sak = new byte[sakLen];
                System.arraycopy(responseMessageData, offset, sak, 0, sak.length);
                activationResult.setSAK(sak);
            }
            return activationResult;
        } catch (Exception e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_ERROR, "Failed to extract activation result.", e);
        }
    }

    /**
     * Check if there is a contactless card present.
     *
     * @param timeout Timeout for checking. Value range: [0-9999]. Unit: 10ms, e.g., if set this parameter to 100, means timeout is 1 second.
     * @return Whether there is a contactless card present or not.
     * <ul>
     *     <li>true - Card present.</li>
     *     <li>false - No card present.</li>
     * </ul>
     * @throws NSDKException
     */
    public boolean checkCard(int timeout) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.CONTACTLESS_CARD_REQUEST);
        // Request message data = Function ID(1 byte) + Timeout(2 bytes)
        byte[] requestMessageData = new byte[3];
        requestMessageData[0] = ContactlessCardFunctionId.CHECK_CARD_PRESENCE;
        System.arraycopy(ExternalMessage.intToBcdBuffer(timeout), 0, requestMessageData, 1, 2);
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceive(requestMessage, ExternalMessageType.CONTACTLESS_CARD_RESPONSE,
                ContactlessCardFunctionId.CHECK_CARD_PRESENCE, timeout * 10);
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 3) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        byte[] responseCodeBuf = new byte[]{responseMessageData[1], responseMessageData[2]};
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

            if (10 == responseCode) {
                return false;
            }

            if (11 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_RF_CARD_MULTI_CARDS, ExternalErrorMessage.CONTACTLESS_CARD_MULTI_CARD_ERROR, innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }

        return true;
    }

    /**
     * Execute APDU command. The command data is encrypted by the specified key.
     *
     * @param keyIndex             The key used to encrypt APDU command data.
     *                             <p>Only support Data Key with ID from 129 to 255.</p>
     * @param encryptedAPDUCommand APDU command data. It is encrypted by the specified Data Key.
     * @return APDU response data. It is encrypted by the specified Data Key.
     * @throws NSDKException
     */
    public ExtAPDUOutput exchangeAPDU(byte keyIndex, byte[] encryptedAPDUCommand) throws NSDKException {
        if (encryptedAPDUCommand == null) {
            throw new NSDKIllegalParameterException("APDU command is null.");
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.CONTACTLESS_CARD_REQUEST);
        // Request message data = Function ID(1 byte) + Key index(1 byte) + Command len(2 bytes) + Command
        int commandLen = encryptedAPDUCommand.length;
        int requestMessageDataLen = 4 + commandLen;
        byte[] requestMessageData = new byte[requestMessageDataLen];
        requestMessageData[0] = ContactlessCardFunctionId.EXCHANGE_APDU;
        requestMessageData[1] = keyIndex;
        System.arraycopy(ExternalMessage.intToHexBuf(commandLen), 0, requestMessageData, 2, 2);
        System.arraycopy(encryptedAPDUCommand, 0, requestMessageData, 4, commandLen);
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.CONTACTLESS_CARD_RESPONSE, ContactlessCardFunctionId.EXCHANGE_APDU);

        // Response message data = Function ID(1 byte) + Response code(2 bytes) + Response data
        byte[] responseMessageData = responseMessage.getMessageData();
        checkResponseCode(responseMessage);

        if (responseMessageData.length == 3) {
            return null;
        }

        if (responseMessageData.length < 5) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, ExternalErrorMessage.DATA_LENGTH_NOT_ENOUGH);
        }

        int dataLen = ExternalMessage.hexBuffer2Int(new byte[]{responseMessageData[3], responseMessageData[4]});

        if (dataLen <= 0) {
            return null;
        }

        if (dataLen > responseMessageData.length - 5) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LEN_FIELD_ERROR, ExternalErrorMessage.DATA_LEN_FIELD_ERROR);
        }

        ExtAPDUOutput result = new ExtAPDUOutput();
        result.setDataLen(dataLen);

        int encryptedDataLen = responseMessageData.length - 5;
        if (encryptedDataLen > 0) {
            byte[] data = new byte[encryptedDataLen];
            System.arraycopy(responseMessageData, 5, data, 0, data.length);
            result.setData(data);
        }

        return result;
    }

    public ExtAPDUOutput exchangeAPDU(byte keyIndex, int actualLen, byte[] encryptedAPDUCommand) throws NSDKException {
        if (encryptedAPDUCommand == null) {
            throw new NSDKIllegalParameterException("APDU command is null.");
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.CONTACTLESS_CARD_REQUEST);
        // Request message data = Function ID(1 byte) + Key index(1 byte) + Command len(2 bytes) + Command
        int commandLen = encryptedAPDUCommand.length;
        int requestMessageDataLen = 4 + commandLen;
        byte[] requestMessageData = new byte[requestMessageDataLen];
        requestMessageData[0] = ContactlessCardFunctionId.EXCHANGE_APDU;
        requestMessageData[1] = keyIndex;
        System.arraycopy(ExternalMessage.intToHexBuf(actualLen), 0, requestMessageData, 2, 2);
        System.arraycopy(encryptedAPDUCommand, 0, requestMessageData, 4, commandLen);
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.CONTACTLESS_CARD_RESPONSE, ContactlessCardFunctionId.EXCHANGE_APDU);

        // Response message data = Function ID(1 byte) + Response code(2 bytes) + Response data
        byte[] responseMessageData = responseMessage.getMessageData();
        checkResponseCode(responseMessage);

        if (responseMessageData.length == 3) {
            return null;
        }

        if (responseMessageData.length < 5) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, ExternalErrorMessage.DATA_LENGTH_NOT_ENOUGH);
        }

        int dataLen = ExternalMessage.hexBuffer2Int(new byte[]{responseMessageData[3], responseMessageData[4]});

        if (dataLen <= 0) {
            return null;
        }

        if (dataLen > responseMessageData.length - 5) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LEN_FIELD_ERROR, ExternalErrorMessage.DATA_LEN_FIELD_ERROR);
        }

        ExtAPDUOutput result = new ExtAPDUOutput();
        result.setDataLen(dataLen);

        int encryptedDataLen = responseMessageData.length - 5;
        if (encryptedDataLen > 0) {
            byte[] data = new byte[encryptedDataLen];
            System.arraycopy(responseMessageData, 5, data, 0, data.length);
            result.setData(data);
        }

        return result;
    }

    /**
     * Deactivate card.
     *
     * @throws NSDKException
     */
    public void deactivate() throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.CONTACTLESS_CARD_REQUEST);
        requestMessage.setMessageData(new byte[]{ContactlessCardFunctionId.DEACTIVATE});

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.CONTACTLESS_CARD_RESPONSE, ContactlessCardFunctionId.DEACTIVATE);

        checkResponseCode(responseMessage);
    }

    /**
     * Execute APDU command. The APDU command data is plain text.
     *
     * @param apduCommand APDU command data. It is plain text.
     * @return APDU response data. It is plain text.
     * @throws NSDKException
     */
    public byte[] exchangeClearAPDU(byte[] apduCommand) throws NSDKException {
        if (apduCommand == null) {
            throw new NSDKIllegalParameterException("APDU command is null.");
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.CONTACTLESS_CARD_REQUEST);
        // Request message data = Function ID(1 byte) + Command
        byte[] requestMessageData = new byte[1 + apduCommand.length];
        requestMessageData[0] = ContactlessCardFunctionId.EXCHANGE_PLAINTEXT_APDU;
        System.arraycopy(apduCommand, 0, requestMessageData, 1, apduCommand.length);
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.CONTACTLESS_CARD_RESPONSE, ContactlessCardFunctionId.EXCHANGE_PLAINTEXT_APDU);
        checkResponseCode(responseMessage);

        // Response message data = Function ID(1 byte) + Response code(2 bytes) + Data
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData.length == 3) {
            return null;
        }

        int dataLen = responseMessageData.length - 3;
        byte[] data = new byte[dataLen];
        System.arraycopy(responseMessageData, 3, data, 0, dataLen);

        return data;
    }

    /**
     * Check if felica card present.
     *
     * @param timeout Time for checking. Unit: 10ms, e.g., if this parameter is set to 100, means timeout is 1 second.
     * @return ID.
     * @throws NSDKException
     */
    public byte[] checkFelicaCard(int timeout) throws NSDKException {
        if (timeout < 0 || timeout > 0xFFFF) {
            throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_TIMEOUT_HEX);
        }
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.CONTACTLESS_CARD_REQUEST);
        // Request message data = Function ID(1 byte) + Timeout(2 bytes)
        byte[] requestMessageData = new byte[3];
        requestMessageData[0] = ContactlessCardFunctionId.CHECK_FELICA_CARD_PRESENCE;
        System.arraycopy(ExternalMessage.intToHexBuf(timeout), 0, requestMessageData, 1, 2);
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceive(requestMessage, ExternalMessageType.CONTACTLESS_CARD_RESPONSE,
                ContactlessCardFunctionId.CHECK_FELICA_CARD_PRESENCE, timeout * 10);
        checkResponseCode(responseMessage);

        // Response message data = Function ID(1 byte) + Response code(2 bytes) + ID len(1 byte) + ID
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData.length == 3) {
            return null;
        }

        int dataLen = responseMessageData[3];
        if (dataLen == 0) {
            return null;
        }

        if (dataLen > responseMessageData.length - 4) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LEN_FIELD_ERROR, ExternalErrorMessage.DATA_LEN_FIELD_ERROR);
        }

        byte[] data = new byte[dataLen];
        System.arraycopy(responseMessageData, 4, data, 0, dataLen);

        return data;
    }

    /**
     * Execute APDU command with felica card.
     *
     * @param apduCommand APDU command data.
     * @return APDU response data.
     * @throws NSDKException
     */
    public byte[] exchangeFelicaAPDU(byte[] apduCommand) throws NSDKException {
        if (apduCommand == null) {
            throw new NSDKIllegalParameterException("APDU command is null.");
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.CONTACTLESS_CARD_REQUEST);
        // Request message data = Function ID(1 byte) + Command
        byte[] requestMessageData = new byte[1 + apduCommand.length];
        requestMessageData[0] = ContactlessCardFunctionId.EXCHANGE_APDU_FELICA;
        System.arraycopy(apduCommand, 0, requestMessageData, 1, apduCommand.length);
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.CONTACTLESS_CARD_RESPONSE, ContactlessCardFunctionId.EXCHANGE_APDU_FELICA);
        checkResponseCode(responseMessage);

        // Response message data = Function ID(1 byte) + Response code(2 bytes) + Data len(2 bytes) + data
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData.length == 3) {
            return null;
        }

        if (responseMessageData.length < 5) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, ExternalErrorMessage.DATA_LENGTH_NOT_ENOUGH);
        }


        int dataLen = ExternalMessage.hexBuffer2Int(new byte[]{responseMessageData[3], responseMessageData[4]});

        if (dataLen <= 0) {
            return null;
        }

        if (dataLen > responseMessageData.length - 5) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LEN_FIELD_ERROR, ExternalErrorMessage.DATA_LEN_FIELD_ERROR);
        }

        byte[] data = new byte[dataLen];
        System.arraycopy(responseMessageData, 5, data, 0, dataLen);
        return data;
    }

    /**
     * Searches and activates contactless card.
     *
     * @param cardTypes Which types of card can be detected.
     * @param mode      Contactless card mode, see {@link ContactlessCardMode}.
     * @param timeout   Time for searching contactless card. Unit: second.
     * @return Detected contactless card information, see {@link ActivationResult}
     * @throws NSDKException
     */
    public ContactlessCardResult searchCard(ContactlessCardType[] cardTypes, ContactlessCardMode mode, int timeout) throws NSDKException {
        if (cardTypes == null || cardTypes.length == 0) {
            throw new NSDKIllegalParameterException("Please set card types.");
        }

        if (mode == null) {
            mode = ContactlessCardMode.WUPA;
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.CONTACTLESS_CARD_REQUEST);

        // Request message data = Function ID(1 byte) + Type(1 byte) + Mode(1 byte) + Timeout(2 bytes)
        byte[] requestMessageData = new byte[5];
        requestMessageData[0] = ContactlessCardFunctionId.PRESENCE;
        requestMessageData[1] = getPresenceType(cardTypes);
        requestMessageData[2] = (byte) mode.getCode();
        System.arraycopy(ExternalMessage.intToHexBuf(timeout), 0, requestMessageData, 3, 2);
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceive(requestMessage, ExternalMessageType.CONTACTLESS_CARD_RESPONSE,
                ContactlessCardFunctionId.PRESENCE, timeout * 1000);

        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 3) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        byte[] responseCodeBuf = new byte[]{responseMessageData[1], responseMessageData[2]};
        int responseCode = ExternalMessage.convertResponseCodeBufToInt(responseCodeBuf);
        responseMessage.setResponseCode(responseCode);
        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 3);
            if (1 == responseCode) {
                throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_PARAMETER, innerErrorCode);
            }

            if (2 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_RF_CARD_ANTI_COLLISION_FAILED, "Anti-conflict failed.", innerErrorCode);
            }

            if (3 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_RF_CARD_SELECT_CARD_FAILED, "Failed to select card.", innerErrorCode);
            }

            if (6 == responseCode) {
                throw new NSDKTimeoutException(ExternalErrorMessage.TIMEOUT);
            }

            if (9 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_RF_CARD_MULTI_CARDS, ExternalErrorMessage.CONTACTLESS_CARD_MULTI_CARD_ERROR, innerErrorCode);
            }

            if (10 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_DEVICE_INIT_ERROR, "Device initialization error.", innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }

        int dataLen = responseMessageData.length - 3;
        if (dataLen == 0) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }

        // Data = Card type(1 byte) + ATQ len(1 byte) + ATQ + UID len(1 byte) + UID + SAK len(1 byte) + SAK
        byte[] data = new byte[dataLen];
        System.arraycopy(responseMessageData, 3, data, 0, dataLen);
        int offset = 0;
        ContactlessCardType cardType;
        if (data[offset] == 0x0A) {
            cardType = ContactlessCardType.TYPE_A;
        } else if (data[offset] == 0x0B) {
            cardType = ContactlessCardType.TYPE_B;
        } else {
            // todo 现在非接卡类型没有 M1 了，指令如果返回 M1 卡类型，也设置成 TYPE_A，那其他卡类型呢？
            cardType = ContactlessCardType.TYPE_A;
        }
        offset++;
        byte[][] contactlessCardData = new byte[3][];
        for (int i = 0; i < 3; i++) {
            if (offset != dataLen) {
                byte resultLen = data[offset];
                offset++;
                if (resultLen > 0) {
                    if (resultLen > dataLen - offset) {
                        throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LEN_FIELD_ERROR, ExternalErrorMessage.DATA_LEN_FIELD_ERROR);
                    }

                    byte[] result = new byte[resultLen];
                    System.arraycopy(data, offset, result, 0, resultLen);
                    offset += resultLen;
                    contactlessCardData[i] = result;
                }
            }
        }

        ContactlessCardResult result = new ContactlessCardResult();
        result.setCardType(cardType);
        result.setATQA(contactlessCardData[0]);
        result.setUID(contactlessCardData[1]);
        result.setSAK(contactlessCardData[2]);
        return result;
    }

    /**
     * Authenticate with external key.
     *
     * <p>A block shall be authenticated before reading and writing.</p>
     *
     * @param keyMode    The mode of the key, Key_A :0x60 or 0x00, Key_B :0x61 or 0x01.
     * @param uid     Serial number returned by {@link #searchCard(ContactlessCardType[], ContactlessCardMode, int)}.
     * @param blockId The block to be authenticated.
     * @param key     The key used to do the authentication. 6 bytes.
     * @throws NSDKException
     */
    public void authenticateWithExternalKey(byte keyMode, byte[] uid, byte blockId, byte[] key) throws NSDKException {
        if (uid == null) {
            throw new NSDKIllegalParameterException("Please set UID.");
        }

        if (key == null) {
            throw new NSDKIllegalParameterException("Please set key.");
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.CONTACTLESS_CARD_REQUEST);
        // Request message data = Function ID(1 byte) + Key mode(1 byte) + UID + Block ID(1 byte) + Key
        byte[] requestMessageData = new byte[3 + uid.length + key.length];
        int offset = 0;
        requestMessageData[offset] = ContactlessCardFunctionId.AUTHENTICATION_WITH_EXTERNAL_KEY;
        offset++;
        requestMessageData[offset] = keyMode;
        offset++;
        System.arraycopy(uid, 0, requestMessageData, offset, uid.length);
        offset += uid.length;
        requestMessageData[offset] = blockId;
        offset++;
        System.arraycopy(key, 0, requestMessageData, offset, key.length);
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.CONTACTLESS_CARD_RESPONSE, ContactlessCardFunctionId.AUTHENTICATION_WITH_EXTERNAL_KEY);
        checkM1CardResponseCode(responseMessage);
    }

    /**
     * Read data from the specified block.
     *
     * <p>The block shall be authenticated before reading.</p>
     *
     * @param blockId The block to read, value range [0-255].
     * @return The data of the specified block.
     * @throws NSDKException
     */
    public byte[] readBlock(byte blockId) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.CONTACTLESS_CARD_REQUEST);
        requestMessage.setMessageData(new byte[]{ContactlessCardFunctionId.READ_BLOCK_DATA, blockId});
        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.CONTACTLESS_CARD_RESPONSE, ContactlessCardFunctionId.READ_BLOCK_DATA);

        // Response message data = Function ID(1 byte) + Response code(2 bytes) + Response data
        checkM1CardResponseCode(responseMessage);

        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData.length == 3) {
            return null;
        }

        int dataLen = responseMessageData.length - 3;
        byte[] data = new byte[dataLen];
        System.arraycopy(responseMessageData, 3, data, 0, dataLen);
        return data;
    }

    /**
     * Write data to the specified block.
     *
     * <p>The block shall be authenticated before writing.</p>
     *
     * @param blockId The block to write, value range [0-255].
     * @param data    The data written to the block. Max length: 16 bytes.
     * @throws NSDKException
     */
    public void writeBlock(byte blockId, byte[] data) throws NSDKException {
        if (data == null || data.length == 0) {
            throw new NSDKIllegalParameterException(ExternalErrorMessage.NO_DATA_TO_SEND);
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.CONTACTLESS_CARD_REQUEST);
        // Request message data = Function ID(1 byte) + Block ID(1 byte) + Data
        byte[] requestMessageData = new byte[2 + data.length];
        requestMessageData[0] = ContactlessCardFunctionId.WRITE_BLOCK_DATA;
        requestMessageData[1] = blockId;
        System.arraycopy(data, 0, requestMessageData, 2, data.length);
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.CONTACTLESS_CARD_RESPONSE, ContactlessCardFunctionId.WRITE_BLOCK_DATA);
        checkM1CardResponseCode(responseMessage);
    }

    /**
     * Add the value to the block.
     *
     * <p>After decrement: </p>
     * <ul>
     *     <li>Call {@link #transfer(byte)}: Update the result from register to the block.</li>
     *     <li>Call {@link #restore(byte)}: Restore the original value from the block to register. </li>
     * </ul>
     *
     * @param blockId The target block to increase.
     * @param value   The value added to the block's value.
     * @throws NSDKException
     */
    public void increment(byte blockId, byte[] value) throws NSDKException {
        if (value == null || value.length == 0) {
            throw new NSDKIllegalParameterException(ExternalErrorMessage.NO_DATA_TO_SEND);
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.CONTACTLESS_CARD_REQUEST);
        // Request message data = Function ID(1 byte) + Block ID(1 byte) + Data
        byte[] requestMessageData = new byte[2 + value.length];
        requestMessageData[0] = ContactlessCardFunctionId.INCREMENT;
        requestMessageData[1] = blockId;
        System.arraycopy(value, 0, requestMessageData, 2, value.length);
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.CONTACTLESS_CARD_RESPONSE, ContactlessCardFunctionId.INCREMENT);
        checkM1CardResponseCode(responseMessage);
    }

    /**
     * Subtract the value from the block.
     *
     * <p>After decrement: </p>
     * <ul>
     *     <li>Call {@link #transfer(byte)}: Update the result from register to the block.</li>
     *     <li>Call {@link #restore(byte)}: Restore the original value from the block to register. </li>
     * </ul>
     *
     * @param blockId The target block to subtract.
     * @param value   The value subtracted from the block.
     * @throws NSDKException
     */
    public void decrement(byte blockId, byte[] value) throws NSDKException {
        if (value == null || value.length == 0) {
            throw new NSDKIllegalParameterException(ExternalErrorMessage.NO_DATA_TO_SEND);
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.CONTACTLESS_CARD_REQUEST);
        // Request message data = Function ID(1 byte) + Block ID(1 byte) + Data
        byte[] requestMessageData = new byte[2 + value.length];
        requestMessageData[0] = ContactlessCardFunctionId.DECREMENT;
        requestMessageData[1] = blockId;
        System.arraycopy(value, 0, requestMessageData, 2, value.length);
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.CONTACTLESS_CARD_RESPONSE, ContactlessCardFunctionId.DECREMENT);
        checkM1CardResponseCode(responseMessage);
    }

    /**
     * Transfer data from register to the specified block.
     *
     * @param blockId The target block to update data from register. Value range [0-255].
     * @throws NSDKException
     */
    public void transfer(byte blockId) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.CONTACTLESS_CARD_REQUEST);
        // Request message data = Function ID(1 byte) + Block ID(1 byte)
        byte[] requestMessageData = new byte[2];
        requestMessageData[0] = ContactlessCardFunctionId.TRANSFER;
        requestMessageData[1] = blockId;
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.CONTACTLESS_CARD_RESPONSE, ContactlessCardFunctionId.TRANSFER);
        checkM1CardResponseCode(responseMessage);
    }

    /**
     * Restore the data of specified block to register.
     *
     * @param blockId Restore this block's data to register. Value range [0-255].
     * @throws NSDKException
     */
    public void restore(byte blockId) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.CONTACTLESS_CARD_REQUEST);
        // Request message data = Function ID(1 byte) + Block ID(1 byte)
        byte[] requestMessageData = new byte[2];
        requestMessageData[0] = ContactlessCardFunctionId.RESTORE;
        requestMessageData[1] = blockId;
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.CONTACTLESS_CARD_RESPONSE, ContactlessCardFunctionId.RESTORE);
        checkM1CardResponseCode(responseMessage);
    }

    /**
     * Get ATS.
     *
     * @param cid CID. Default value is 0x00.
     * @return ATS.
     * @throws NSDKException
     */
    public byte[] getATS(byte cid) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.CONTACTLESS_CARD_REQUEST);
        requestMessage.setMessageData(new byte[]{ContactlessCardFunctionId.GET_ATS, cid});
        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.CONTACTLESS_CARD_RESPONSE, ContactlessCardFunctionId.GET_ATS);

        // Response message data = Function ID(1 byte) + Response code(2 bytes) + Response data
        checkResponseCode(responseMessage);

        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData.length == 3) {
            return null;
        }

        int dataLen = responseMessageData.length - 3;
        byte[] data = new byte[dataLen];
        System.arraycopy(responseMessageData, 3, data, 0, dataLen);
        return data;
    }

    private void checkResponseCode(ExternalMessage responseMessage) throws NSDKException {
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 3) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        byte[] responseCodeBuf = new byte[]{responseMessageData[1], responseMessageData[2]};
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
                throw new NSDKExternalDeviceException(ErrorCode.EXT_RF_CARD_READER_NOT_CONFIGURED, "Reader not configured.", innerErrorCode);
            }

            if (10 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_RF_CARD_NOT_PRESENT, ExternalErrorMessage.CONTACTLESS_CARD_NOT_PRESENT, innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }
    }

    private void checkM1CardResponseCode(ExternalMessage responseMessage) throws ExternalMessageException, NSDKIllegalParameterException, NSDKExternalDeviceException {
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 3) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        byte[] responseCodeBuf = new byte[]{responseMessageData[1], responseMessageData[2]};
        int responseCode;
        try {
            responseCode = Integer.parseInt(new String(responseCodeBuf));
        } catch (Exception e) {
            if (responseCodeBuf[0] == (byte) 0x30 && responseCodeBuf[1] == (byte) 0xFF) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_RF_CARD_NOT_PRESENT, "No card.");
            } else {
                throw new NSDKExternalDeviceException(String.format("Unknown error code: %s", ISOUtils.hexString(responseCodeBuf)));
            }
        }
        responseMessage.setResponseCode(responseCode);
        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 3);
            if (1 == responseCode) {
                throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_PARAMETER, innerErrorCode);
            }

            if (2 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.CONTACTLESS_CARD_OTHER_ERROR, innerErrorCode);
            }

            if (3 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_RF_CARD_READER_AUTH_ERROR, "Failed to authenticate.", innerErrorCode);
            }

            if (4 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_RF_CARD_READER_NOT_AUTH, "Not authenticated.", innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }
    }

    private byte getPresenceType(ContactlessCardType[] cardTypes) throws NSDKIllegalParameterException {
        byte aCard = 0x01;
        byte bCard = 0x02;
        byte mCard = 0x04;
        byte result = 0;
        for (ContactlessCardType type : cardTypes) {
            switch (type) {
                case TYPE_A:
                    result = (byte) (aCard | result);
                    break;
                case TYPE_B:
                    result = (byte) (bCard | result);
                    break;
                case TYPE_F:
                case TYPE_V:
                default:
                    throw new NSDKIllegalParameterException("Only support these card types: type A, type B");
            }
        }

        return result;
    }
}
