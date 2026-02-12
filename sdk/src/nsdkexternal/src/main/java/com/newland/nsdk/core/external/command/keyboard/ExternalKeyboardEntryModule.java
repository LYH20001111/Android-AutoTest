package com.newland.nsdk.core.external.command.keyboard;

import android.text.TextUtils;
import android.util.Log;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.crypto.AlgorithmParameters;
import com.newland.nsdk.core.api.common.crypto.CipherType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.exception.NSDKTimeoutException;
import com.newland.nsdk.core.api.common.keymanager.AsymmetricKey;
import com.newland.nsdk.core.api.common.keymanager.CipherMode;
import com.newland.nsdk.core.api.common.keymanager.DUKPTKey;
import com.newland.nsdk.core.api.common.keymanager.Key;
import com.newland.nsdk.core.api.common.keymanager.KeyType;
import com.newland.nsdk.core.api.common.keymanager.KeyUsage;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;
import com.newland.nsdk.core.api.common.pinentry.PINBlockMode;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.common.utils.TLVElement;
import com.newland.nsdk.core.api.common.utils.TLVUtils;
import com.newland.nsdk.core.api.external.keyboard.AmountListener;
import com.newland.nsdk.core.api.external.keyboard.AmountParameters;
import com.newland.nsdk.core.api.external.keyboard.AmountType;
import com.newland.nsdk.core.api.external.keyboard.InputButtonParameters;
import com.newland.nsdk.core.api.external.keyboard.InputItem;
import com.newland.nsdk.core.api.external.keyboard.InputListener;
import com.newland.nsdk.core.api.external.keyboard.InputParameters;
import com.newland.nsdk.core.api.external.pinentry.ExtOfflinePINParameters;
import com.newland.nsdk.core.api.external.pinentry.ExtPINEntryListener;
import com.newland.nsdk.core.api.external.pinentry.ExtendedCipherPAN;
import com.newland.nsdk.core.api.external.pinentry.ExtendedExtPINEntryParams;
import com.newland.nsdk.core.api.external.pinentry.RSAKey;
import com.newland.nsdk.core.external.command.common.ExtToolUtils;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.external.command.exception.ExternalErrorMessage;
import com.newland.nsdk.core.external.command.message.ExternalMessage;
import com.newland.nsdk.core.external.command.message.ExternalMessageType;
import com.newland.nsdk.core.api.external.exception.NSDKExternalDeviceException;
import com.newland.nsdk.core.api.external.keyboard.KeyboardParameters;
import com.newland.nsdk.core.api.external.pinentry.CipherPAN;
import com.newland.nsdk.core.api.external.pinentry.ExtOnlinePINParameters;
import com.newland.nsdk.core.api.external.pinentry.ExtPINEntryParameters;
import com.newland.nsdk.core.external.command.exception.ExternalMessageException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * Provides the ability of keyboard input.
 */
public class ExternalKeyboardEntryModule {
    /**
     * Set the line to display entered PIN.
     *
     * @param lineNumber The line to display entered PIN. Value range: [1-5]
     * @throws NSDKException If error occurs.
     */
    public void setPinLine(byte lineNumber) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.SET_PIN_LINE_REQUEST);
        requestMessage.setMessageData(new byte[]{lineNumber});

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.SET_PIN_LINE_RESPONSE, null);

        // Response message data = Response code(2 bytes)
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));
        if (responseCode != ErrorCode.OK) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (1 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_LINE_NUMBER_ERROR, "Line number error.", innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }
    }

    /**
     * Start PIN entry.
     *
     * <p>This method will be blocked until PIN input finished or any exception thrown, e.g., timeout exception.</p>
     *
     * @param pinInputParameter
     * @param timeout   Timeout for PIN input, unit: second.
     * @return Return the followings if success:
     * <ul>
     *     <li>Encrypted PIN block.</li>
     *     <li>KSN if it is DUKPT key type.</li>
     * </ul>
     * @throws NSDKException
     */
    public KeyboardEntryResult pinEntry(Key key, String plainPan, CipherPAN cipherPan, ExtPINEntryParameters pinInputParameter, int timeout) throws NSDKException{
        if (key == null || pinInputParameter == null) {
            throw new NSDKIllegalParameterException("Please set PIN key and parameters.");
        }

        if (!(key instanceof SymmetricKey)){
            throw new NSDKIllegalParameterException("Only support symmetric key for PIN entry now.");
        }

        SymmetricKey pinKey = (SymmetricKey) key;

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.PIN_ENTRY_REQUEST);
        requestMessage.setMessageData(packPinEntryRequestMessageData(pinKey, plainPan, cipherPan, pinInputParameter, timeout));

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceive(requestMessage, ExternalMessageType.PIN_ENTRY_RESPONSE,
                null, timeout * 1000);

        // Response message data = Response code(2 bytes) + Function key(1 byte) + PIN len(1 byte) + Encrypted PIN block + DUKPT KSN(10 bytes)
        byte[] responseMessageData = responseMessage.getMessageData();
        checkResponseCode(responseMessageData);

        // Skip response code and function key.
        int offset = 3;

        KeyboardEntryResult result = new KeyboardEntryResult();
        if (responseMessageData.length > 3) {
            int pinLen = responseMessageData[3] & 0xFF;
            offset++;
            result.setPinLen(pinLen);
            if (pinLen > 0) {
                int pinBlockLen = 8;

                if (pinKey.getKeyType() == KeyType.AES) {
                    pinBlockLen = 16;
                }

                if (pinBlockLen > responseMessageData.length - offset) {
                    throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LEN_FIELD_ERROR, ExternalErrorMessage.DATA_LEN_FIELD_ERROR);
                }

                byte[] pinBlock = new byte[pinBlockLen];
                System.arraycopy(responseMessageData, offset, pinBlock, 0, pinBlock.length);
                result.setEncryptedPinBlock(pinBlock);
                offset += pinBlockLen;
            }

            if (pinKey instanceof DUKPTKey) {
                if (responseMessageData.length - offset < 10) {
                    throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, ExternalErrorMessage.DATA_LENGTH_NOT_ENOUGH);
                }
                byte[] ksn = new byte[10];
                System.arraycopy(responseMessageData, offset, ksn, 0, 10);
                result.setKsn(ksn);
            }
        }

        return result;
    }

    public KeyboardEntryResult newPinEntry(Key key, String plainPan, CipherPAN cipherPAN, ExtPINEntryParameters pinInputParameter, int timeout) throws NSDKException {
        if (key == null) {
            throw new NSDKIllegalParameterException("PIN key shall not be null.");
        }
        if (pinInputParameter == null) {
            throw new NSDKIllegalParameterException("PIN entry parameters shall not be null.");
        }
        if (timeout < 5 || timeout > 200) {
            throw new NSDKIllegalParameterException("Timeout shall be between 5 and 200 seconds.");
        }
        if (!(key instanceof SymmetricKey)) {
            throw new NSDKIllegalParameterException("Only support Symmetric key now.");
        }

        SymmetricKey pinKey = (SymmetricKey) key;

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.NEW_PIN_ENTRY_REQUEST);
        requestMessage.setMessageData(packNewExtendedPinEntryRequestMessageData(pinKey, plainPan, cipherPAN, pinInputParameter, timeout));

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceive(requestMessage, ExternalMessageType.NEW_PIN_ENTRY_RESPONSE, null, timeout * 1000);
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new NSDKExternalDeviceException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }
        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));
        if (responseCode != ErrorCode.OK) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (1 == responseCode) {
                throw new NSDKIllegalParameterException();
            }
            if (3 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.SECVP_VPP_PIN_BYPASS, "ByPass", innerErrorCode);
            }
            if (4 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
            }
            if (5 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.CANCELLED, ExternalErrorMessage.CANCELLED, innerErrorCode);
            }
            if (6 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, "PAN decryption error.", innerErrorCode);
            }
            if (7 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, "Invalid PAN", innerErrorCode);
            }
            if (10 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.TIMEOUT, ExternalErrorMessage.TIMEOUT, innerErrorCode);
            }
            if (40 == responseCode) {
                throw new NSDKIllegalParameterException(ErrorCode.EXT_PINPAD_CARD_REMOVED, "Card Removed.", innerErrorCode);
            }
            if (43 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, "Missing PIN Key", innerErrorCode);
            }
            if (45 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_COMMAND_LENGTH_ERROR, "Bad Command Length.", innerErrorCode);
            }
            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }
        KeyboardEntryResult result = new KeyboardEntryResult();
        if (responseMessageData.length > 2) {
            int offset = 2;
            int pinLen = responseMessageData[2] & 0xFF;
            offset++;
            result.setPinLen(pinLen);
            if (pinLen > 0) {
                int pinBlockLen = 8;
                if (pinKey.getKeyType() == KeyType.AES) {
                    pinBlockLen = 16;
                }
                if (pinBlockLen > responseMessageData.length - 3) {
                    throw new NSDKExternalDeviceException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, ExternalErrorMessage.DATA_LENGTH_NOT_ENOUGH);
                }
                byte[] pinBlock = new byte[pinBlockLen];
                System.arraycopy(responseMessageData, 3, pinBlock, 0, pinBlockLen);
                result.setEncryptedPinBlock(pinBlock);
                offset += pinBlockLen;
            }
            if (pinKey instanceof DUKPTKey) {
                if (responseMessageData.length - offset < 10) {
                    throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, ExternalErrorMessage.DATA_LENGTH_NOT_ENOUGH);
                }
                byte[] ksn = new byte[10];
                System.arraycopy(responseMessageData, offset, ksn, 0, 10);
                result.setKsn(ksn);
                offset += 10;
            }
            byte[] bTlvDataLen = new byte[2];
            System.arraycopy(responseMessageData, offset, bTlvDataLen, 0, 2);
            offset +=2;
            int tlvDataLen = ExternalMessage.hex2Int(bTlvDataLen);
            if (tlvDataLen > 0) {
                byte[] tlvData = new byte[tlvDataLen];
                System.arraycopy(responseMessageData, offset, tlvData, 0, tlvDataLen);
                result.setTlvData(tlvData);
            }
        }
        return result;
    }

    /**
     * Cancel PIN entry. This will cause the Cancel callback of PIN entry.
     *
     * @throws NSDKException If error occurs.
     */
    public void cancelPinEntry() throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.CANCEL_PIN_ENTRY_REQUEST);
        ExternalCommunicationManager.getInstance().sendInterrupt(requestMessage.pack());
    }

    /**
     * Extended PIN entry.
     *
     * <p>Different to 'pinEntry' method, this method supports to generating a random PIN key which is under protection of a specified KEK for PIN block encryption.</p>
     * <p>This method will be blocked until PIN input finished or any exception thrown, e.g., timeout exception.</p>
     *
     * @param parameter
     * @param timeout   Timeout for PIN input. Unit: seconds. 0 is NOT ALLOWED.
     * @return Return the followings if success:
     * <ul>
     *     <li>Encrypted PIN block.</li>
     *     <li>Encrypted random PIN key if using random PIN key.</li>
     * </ul>
     * @throws NSDKException
     */
    public KeyboardEntryResult extendedPinEntry(Key key, String pan, ExtPINEntryParameters parameter, int timeout, boolean isRandomPinKey) throws NSDKException {
        if (parameter == null) {
            throw new NSDKIllegalParameterException("Please set PIN pad parameters.");
        }

        if (!(key instanceof SymmetricKey)){
            throw new NSDKIllegalParameterException("Only support symmetric key for PIN entry now.");
        }

        SymmetricKey pinKey = (SymmetricKey) key;

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.EXTENDED_PIN_ENTRY_REQUEST);
        requestMessage.setMessageData(packExtendedPinEntryRequestMessageData(pinKey, pan, parameter, timeout, isRandomPinKey));

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceive(requestMessage, ExternalMessageType.EXTENDED_PIN_ENTRY_RESPONSE,
                null, timeout * 1000);

        // Response message data = Response code(2 bytes) + Function key(1 byte) + PIN len(1 byte) + Encrypted PIN block + PIN key len(1 byte) + PIN key
        byte[] responseMessageData = responseMessage.getMessageData();
        int responseMessageDataLen = responseMessageData.length;
        checkResponseCode(responseMessageData);

        int offset = 3;
        // Skip response code and function key

        KeyboardEntryResult result = new KeyboardEntryResult();
        if (!ExternalMessage.isDataEnough(offset, responseMessageDataLen, 1)) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }

        int pinLen = responseMessageData[3] & 0xFF;
        result.setPinLen(pinLen);
        offset++;

        if (pinLen > 0) {
            int pinBlockLen = 8;
            if (pinKey.getKeyType() == KeyType.AES) {
                pinBlockLen = 16;
            }

            if (!ExternalMessage.isDataEnough(offset, responseMessageDataLen, pinBlockLen)) {
                throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, ExternalErrorMessage.DATA_LENGTH_NOT_ENOUGH);
            }

            byte[] pinBlock = new byte[pinBlockLen];
            System.arraycopy(responseMessageData, offset, pinBlock, 0, pinBlockLen);
            result.setEncryptedPinBlock(pinBlock);
            offset += pinBlockLen;
        }

        if (isRandomPinKey) {
            if (!ExternalMessage.isDataEnough(offset, responseMessageDataLen, 1)) {
                throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, ExternalErrorMessage.DATA_LENGTH_NOT_ENOUGH);
            }

            int pinKeyLen = responseMessageData[offset] & 0xFF;
            offset++;

            if (pinKeyLen > responseMessageData.length - offset) {
                throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LEN_FIELD_ERROR, ExternalErrorMessage.DATA_LEN_FIELD_ERROR);
            }

            byte[] pinKeyData = new byte[pinKeyLen];
            System.arraycopy(responseMessageData, offset, pinKeyData, 0, pinKeyLen);
            result.setEncryptedRandomPinKey(pinKeyData);
        }

        return result;
    }

    /**
     * Sensitive data entry.
     *
     * <p>This method will be blocked until PIN input finished or any exception thrown, e.g., timeout exception.</p>
     *
     * @return Return encrypted input data if success.
     * @throws NSDKException
     */
    public KeyboardEntryResult sensitiveDataEntry(Key dataKey, int timeout, KeyboardParameters parameter) throws NSDKException {
        if (dataKey == null || parameter == null) {
            throw new NSDKIllegalParameterException("Parameters and key shall not be null.");
        }
        if (parameter.getKeyboardMode() == null || parameter.getPromptID() == null) {
            throw new NSDKIllegalParameterException("Please set keyboard mode and prompt ID.");
        }

        byte keyTypeCode;
        if (dataKey instanceof SymmetricKey) {
            SymmetricKey tempSymmetricKey = (SymmetricKey)dataKey;
            keyTypeCode = tempSymmetricKey.getKeyType().getCode();
        } else if (dataKey instanceof AsymmetricKey) {
            AsymmetricKey tempAsymmetricKey = (AsymmetricKey)dataKey;
            keyTypeCode = tempAsymmetricKey.getKeyType().getCode();
        } else {
            throw new NSDKIllegalParameterException("Data key shall be symmetric or asymmetric key.");
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.SENSITIVE_DATA_ENTRY_REQUEST);
        // Request message data = Prompt ID(1 byte) + Data key index(1 byte) + Data key type(1 byte) + Input mode(1 byte) + Min(1 byte) + Max(1 byte) + Timeout(1 byte)
        byte[] requestMessageData = new byte[7];
        requestMessageData[0] = parameter.getPromptID().getCode();
        requestMessageData[1] = dataKey.getKeyID();
        requestMessageData[2] = keyTypeCode;
        requestMessageData[3] = (byte) parameter.getKeyboardMode().ordinal();
        requestMessageData[4] = parameter.getMinLen();
        requestMessageData[5] = parameter.getMaxLen();
        requestMessageData[6] = (byte)timeout;
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceive(requestMessage, ExternalMessageType.SENSITIVE_DATA_ENTRY_RESPONSE,
                null, timeout * 1000);

        // Response message data = Response code(2 bytes) +Data len(1 byte) + Encrypted data len(1 byte) + Encrypted data
        byte[] responseMessageData = responseMessage.getMessageData();
        checkResponseCode(responseMessageData);

        int offset = 2;
        KeyboardEntryResult result = new KeyboardEntryResult();
        if (requestMessageData.length == 2) {
            return result;
        }

        byte dataLen = responseMessageData[2];
        result.setDataLen(dataLen);
        offset++;
        if (dataLen <= 0) {
            return result;
        }

        if (offset >= responseMessageData.length) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, ExternalErrorMessage.DATA_LENGTH_NOT_ENOUGH);
        }

        int encryptedDataLen = responseMessageData[3] & 0xFF;
        offset++;
        if (encryptedDataLen > 0) {
            if (encryptedDataLen > responseMessageData.length - offset) {
                throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LEN_FIELD_ERROR, ExternalErrorMessage.DATA_LEN_FIELD_ERROR);
            }

            byte[] encryptedData = new byte[encryptedDataLen];
            System.arraycopy(responseMessageData, offset, encryptedData, 0, encryptedDataLen);
            result.setEncryptedData(encryptedData);
        }

        return result;
    }

    public KeyboardEntryResult sensitiveDataEntry(SymmetricKey dataKey, AlgorithmParameters params, int timeout, KeyboardParameters parameter) throws NSDKException {
        if(ExternalCommunicationManager.getInstance().isSupportCrypto()) {
            throw new NSDKExternalDeviceException(ErrorCode.NOT_SUPPORTED, ExternalErrorMessage.NOT_SUPPORTED);
        }
        if (dataKey == null || parameter == null) {
            throw new NSDKIllegalParameterException("Parameters and key shall not be null.");
        }
        if (parameter.getKeyboardMode() == null || parameter.getPromptID() == null) {
            throw new NSDKIllegalParameterException("Please set keyboard mode and prompt ID.");
        }
        byte keyTypeCode = dataKey.getKeyType().getCode();

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.SENSITIVE_DATA_ENTRY_REQUEST);

        byte[] requestMessageData = new byte[7];
        requestMessageData[0] = parameter.getPromptID().getCode();
        requestMessageData[1] = dataKey.getKeyID();
        requestMessageData[2] = keyTypeCode;
        requestMessageData[3] = (byte) parameter.getKeyboardMode().ordinal();
        requestMessageData[4] = parameter.getMinLen();
        requestMessageData[5] = parameter.getMaxLen();
        requestMessageData[6] = (byte)timeout;

        if(params != null) {
            ExtToolUtils.TLVPack tlvPack = ExtToolUtils.newTLVPack();

            CipherType cipherType = ExtToolUtils.combineCipherType(dataKey, params);
            tlvPack.append(0xDF01, cipherType, dataKey.getKeyUsage(), params.getPaddingMode(), params.getIV());
            byte[] tlvData = tlvPack.pack();
            byte[] requestPack = new byte[9 + tlvData.length];
            System.arraycopy(requestMessageData, 0, requestPack, 0, 7);
            System.arraycopy(ExternalMessage.intToHexBuf(tlvData.length), 0, requestPack, 7, 2);
            System.arraycopy(tlvData, 0, requestPack, 9, tlvData.length);
            requestMessage.setMessageData(requestPack);
        } else {
            requestMessage.setMessageData(requestMessageData);
        }

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceive(requestMessage, ExternalMessageType.SENSITIVE_DATA_ENTRY_RESPONSE,
                null, timeout * 1000);

        // Response message data = Response code(2 bytes) +Data len(1 byte) + Encrypted data len(1 byte) + Encrypted data
        byte[] responseMessageData = responseMessage.getMessageData();
        checkResponseCode(responseMessageData);

        int offset = 2;
        KeyboardEntryResult result = new KeyboardEntryResult();
        if (requestMessageData.length == 2) {
            return result;
        }

        byte dataLen = responseMessageData[2];
        result.setDataLen(dataLen);
        offset++;
        if (dataLen <= 0) {
            return result;
        }

        if (offset >= responseMessageData.length) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, ExternalErrorMessage.DATA_LENGTH_NOT_ENOUGH);
        }

        int encryptedDataLen = responseMessageData[3] & 0xFF;
        offset++;
        if (encryptedDataLen > 0) {
            if (encryptedDataLen > responseMessageData.length - offset) {
                throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LEN_FIELD_ERROR, ExternalErrorMessage.DATA_LEN_FIELD_ERROR);
            }

            byte[] encryptedData = new byte[encryptedDataLen];
            System.arraycopy(responseMessageData, offset, encryptedData, 0, encryptedDataLen);
            result.setEncryptedData(encryptedData);
        }

        return result;
    }

    private void checkResponseCode(byte[] responseMessageData) throws NSDKException, NSDKIllegalParameterException {
        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        byte[] responseCodeBuf = new byte[2];
        System.arraycopy(responseMessageData, 0, responseCodeBuf, 0, 2);
        int responseCode = ExternalMessage.convertResponseCodeBufToInt(responseCodeBuf);

        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (1 == responseCode) {
                throw new NSDKTimeoutException(ExternalErrorMessage.TIMEOUT, innerErrorCode);
            }

            if (2 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
            }

            if (4 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.CANCELLED, ExternalErrorMessage.CANCELLED, innerErrorCode);
            }

            if (5 == responseCode) {
                throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_PARAMETER, innerErrorCode);
            }

            if (42 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_KEY_TAG, ExternalErrorMessage.PINPAD_BAD_KEY_TAG, innerErrorCode);
            }

            if (43 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_KEY_INDEX, ExternalErrorMessage.PINPAD_BAD_KEY_INDEX, innerErrorCode);
            }

            if (55 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.NOT_SUPPORTED, ExternalErrorMessage.NOT_SUPPORTED, innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }
    }

    private byte[] packPinEntryRequestMessageData(SymmetricKey key, String plainPan, CipherPAN cipherPan, ExtPINEntryParameters parameter, int timeout) throws NSDKException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(key.getKeyID());
        KeyType keyType = key.getKeyType();
        KeyUsage keyUsage = key.getKeyUsage();
        if (keyType != KeyType.AES && keyType != KeyType.DES) {
            throw new ExternalMessageException("Only support DES and AES PIN key type now.");
        }
        if (keyUsage == KeyUsage.DUKPT) {
            outputStream.write(1);
        } else {
            if (keyType == KeyType.DES) {
                outputStream.write(0);
            } else if (keyType == KeyType.AES) {
                outputStream.write(2);
            }
        }

        byte fs = 0x1C;
        try {
            if (cipherPan != null) {
                if (!(cipherPan.getPANKey() instanceof  SymmetricKey)){
                    throw new NSDKIllegalParameterException("Only support symmetric PAN key now.");
                }

                SymmetricKey panKey = (SymmetricKey) cipherPan.getPANKey();
                outputStream.write(panKey.getKeyID());
                int panKeyIndex = panKey.getKeyID() & 0xFF;
                if (panKeyIndex >= 129 && panKeyIndex <= 255 && (panKey.getKeyType() != KeyType.DES && panKey.getKeyType() != KeyType.AES)) {
                    throw new ExternalMessageException("Please set PAN key type(DES or AES).");
                }
                outputStream.write(panKey.getKeyType().getCode());
                if (cipherPan.getCipherPAN() == null || cipherPan.getCipherPAN().length != 16) {
                    throw new ExternalMessageException("Please set cipher PAN(16 bytes).");
                }
                outputStream.write(cipherPan.getClearPANLen());
                outputStream.write(cipherPan.getCipherPAN());
            } else {
                outputStream.write(0);
                if (TextUtils.isEmpty(plainPan)) {
                    throw new ExternalMessageException("Please set plain text PAN.");
                }
                outputStream.write(plainPan.getBytes());
                outputStream.write(fs);
            }

            if (parameter instanceof ExtOnlinePINParameters) {
                ExtOnlinePINParameters onlinePINParameters = (ExtOnlinePINParameters) parameter;
                if (onlinePINParameters.getExtendedPINKeyData() != null && onlinePINParameters.getExtendedPINKeyData().length != 0) {
                    byte extendedKeyMode;
                    if (onlinePINParameters.getExtendedKeyMode() == CipherMode.CBC) {
                        extendedKeyMode = 0;
                    } else if (onlinePINParameters.getExtendedKeyMode() == CipherMode.ECB) {
                        extendedKeyMode = 1;
                    } else {
                        throw new ExternalMessageException(String.format("Unsupported key mode for extended key(%s)", onlinePINParameters.getExtendedKeyMode()));
                    }

                    outputStream.write(extendedKeyMode);
                    outputStream.write(onlinePINParameters.getExtendedPINKeyData().length);
                    outputStream.write(onlinePINParameters.getExtendedPINKeyData());
                } else {
                    outputStream.write(1);
                    outputStream.write(0);
                }
            } else {
                outputStream.write(1);
                outputStream.write(0);
            }
            outputStream.write(parameter.getMaxPINLen());
            outputStream.write(parameter.isAutoComplete() ? 1 : 0);
            outputStream.write(timeout);
            byte pinBlockMode = 0;
            if (parameter.getPINBlockMode() == PINBlockMode.ISO9564_0) {
                pinBlockMode = 0;
            } else if (parameter.getPINBlockMode() == PINBlockMode.ISO9564_1) {
                pinBlockMode = 1;
            } else if (parameter.getPINBlockMode() == PINBlockMode.ISO9564_3) {
                pinBlockMode = 3;
            } else if (parameter.getPINBlockMode() == PINBlockMode.ISO9564_4) {
                pinBlockMode = 4;
            } else {
                throw new ExternalMessageException(String.format("Unsupported PIN block mode(%s)", parameter.getPINBlockMode()));
            }

            outputStream.write(pinBlockMode);
            String[] displayMessages = parameter.getDisplayMessages();
            if (displayMessages != null && displayMessages.length > 0) {
                for (String m : displayMessages) {
                    if (m == null || m.length() == 0) {
                        outputStream.write(fs);
                        continue;
                    }
                    outputStream.write(m.getBytes());
                    outputStream.write(fs);
                }
            }
        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }

        return outputStream.toByteArray();
    }

    private byte[] packExtendedPinEntryRequestMessageData(SymmetricKey pinKey, String pan, ExtPINEntryParameters parameter, int timeout, boolean isRandomPinKey) throws NSDKException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte method = (byte) (isRandomPinKey ? 1 : 0);
        outputStream.write(method);

        // If method = 0, this is PIN key index
        // If method = 1(using random PIN key), this is KEK index.
        outputStream.write(pinKey.getKeyID());
        if (pinKey.getKeyType() == null) {
            throw new NSDKIllegalParameterException("Please set PIN key type.");
        }
        outputStream.write(pinKey.getKeyType().getCode());

        try {
            if (pan == null || pan.length() == 0) {
                outputStream.write(0);
            } else {
                outputStream.write(pan.length());
                outputStream.write(pan.getBytes());
            }
            outputStream.write(parameter.getMaxPINLen());
            outputStream.write(parameter.isAutoComplete() ? 1 : 0);
            outputStream.write(timeout);
            byte fs = 0x1C;
            String[] displayMessages = parameter.getDisplayMessages();
            if (displayMessages != null && displayMessages.length > 0) {
                for (String m : displayMessages) {
                    if (m == null || m.length() == 0) {
                        outputStream.write(fs);
                        continue;
                    }
                    outputStream.write(m.getBytes());
                    outputStream.write(fs);
                }
            }
        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }

        return outputStream.toByteArray();
    }


    private byte[] packNewExtendedPinEntryRequestMessageData(SymmetricKey pinKey, String clearPan, CipherPAN cipherPAN, ExtPINEntryParameters params, int timeout) throws NSDKException {
        try (ByteArrayOutputStream messageStream = new ByteArrayOutputStream()) {
            if (((ExtendedExtPINEntryParams) params).getSessionType() == null) {
                throw new NSDKIllegalParameterException("Session type shall not be null.");
            }
            int sessionType = ((ExtendedExtPINEntryParams)params).getSessionType().getCode();
            //Session type
            messageStream.write(sessionType);
            //Pin key id
            messageStream.write(pinKey.getKeyID());
            //Pin key type
            if (pinKey.getKeyType() != KeyType.AES && pinKey.getKeyType() != KeyType.DES) {
                throw new NSDKIllegalParameterException("Pin key type shall be AES or DES.");
            }
            messageStream.write(pinKey.getKeyType().getCode());
            //Clear PAN len
            if (cipherPAN != null) {
                Log.d("debug", "cipher pan");
                messageStream.write(cipherPAN.getClearPANLen());
                messageStream.write(cipherPAN.getCipherPAN().length);
                messageStream.write(cipherPAN.getCipherPAN());
            } else {
                Log.d("debug", "clear pan");
                messageStream.write(clearPan.length());
                messageStream.write(clearPan.length());
                messageStream.write(clearPan.getBytes());
            }
            //MAX digits
            int maxDigits = params.getMaxPINLen();
            if (maxDigits < 4 || maxDigits > 12) {
                throw new NSDKIllegalParameterException("MAX digits shall be between 4 and 12.");
            }
            messageStream.write(maxDigits);
            //Entry key required.
            messageStream.write(params.isAutoComplete() ? 1 : 0);
            //timeout
            if (timeout < 5 || timeout > 200) {
                throw new NSDKIllegalParameterException("Timeout shall be between 5 and 200.");
            }
            messageStream.write(timeout);
            //ISO Format
            messageStream.write(params.getPINBlockMode().getCode());
            //AD
            if (((ExtendedExtPINEntryParams) params).getAdditionalData() != null) {
                int adSize = ((ExtendedExtPINEntryParams) params).getAdditionalData().length;
                messageStream.write(ExternalMessage.intToHexBuf(adSize));
                messageStream.write(((ExtendedExtPINEntryParams) params).getAdditionalData());
            } else {
                messageStream.write(ExternalMessage.intToHexBuf(0));
            }
            //TLV
            //DF01
            ByteArrayOutputStream tlvDataStream = new ByteArrayOutputStream();
            int bytePass = ((ExtendedExtPINEntryParams) params).getMinLen() == 0? 0 : 1;
            if (bytePass == 0) {
                tlvDataStream.write(ISOUtils.hex2byte("DF01"));
                tlvDataStream.write(1);
                tlvDataStream.write(0);
            }
            //DF02
            if (params.getMaskLine() != null) {
                tlvDataStream.write(ISOUtils.hex2byte("DF02"));
                tlvDataStream.write(1);
                tlvDataStream.write(params.getMaskLine().getCode());
            }
            //DF03
            byte[] pinRange = params.getPinLengthRange();
            if (pinRange != null && pinRange.length != 0) {
                tlvDataStream.write(ISOUtils.hex2byte("DF03"));
                byte[] validPinRange = getValidPinRange(pinRange);
                tlvDataStream.write(validPinRange.length + 1);
                tlvDataStream.write(validPinRange.length);
                tlvDataStream.write(validPinRange);
            }
            //DF04
            if (cipherPAN instanceof ExtendedCipherPAN) {
                tlvDataStream.write(ISOUtils.hex2byte("DF04"));
                int adSize = ((ExtendedCipherPAN)cipherPAN).getAdditionalData() == null ? 0 : ((ExtendedCipherPAN)cipherPAN).getAdditionalData().length;
                int ivLen = ((ExtendedCipherPAN)cipherPAN).getIv() == null ? 0 : ((ExtendedCipherPAN)cipherPAN).getIv().length;
                int tlvDataLen = 6 + adSize + ivLen;
                tlvDataStream.write(tlvDataLen);
                //Key ID
                tlvDataStream.write(cipherPAN.getPANKey().getKeyID());
                //Key Usage
                if (((SymmetricKey)cipherPAN.getPANKey()).getKeyUsage() == null) {
                    throw new NSDKIllegalParameterException("Pan KeyUsage shall not be null.");
                }
                int keyUsage = ((SymmetricKey)cipherPAN.getPANKey()).getKeyUsage().getCode();
                if (keyUsage != KeyUsage.DATA.getCode() && keyUsage != KeyUsage.DUKPT.getCode()) {
                    throw new NSDKIllegalParameterException("Pan key usage shall be DATA or DUKPT.");
                }
                tlvDataStream.write(keyUsage);
                if (((ExtendedCipherPAN) cipherPAN).getCipherType() == null) {
                    throw new NSDKIllegalParameterException("Cipher type shall not be null.");
                }
                tlvDataStream.write(((ExtendedCipherPAN) cipherPAN).getCipherType().getCode());
                //IV
                byte[] iv = ((ExtendedCipherPAN) cipherPAN).getIv();
                if (iv == null || iv.length == 0) {
                    tlvDataStream.write(0);
                } else {
                    tlvDataStream.write(iv.length);
                    tlvDataStream.write(iv);
                }
                //AD
                if (cipherPAN.getPANKey() != null) {
                    SymmetricKey panKey = (SymmetricKey) cipherPAN.getPANKey();
                    if (panKey != null && panKey.getKeyType() == KeyType.AES && panKey.getKeyUsage() == KeyUsage.DUKPT) {
                        byte[] ad = ((ExtendedCipherPAN) cipherPAN).getAdditionalData();
                        if (ad == null || ad.length == 0) {
                            throw new NSDKIllegalParameterException("Additional data in cipherPan shall not be null when targeted at AES_DUKPT key.");
                        }
                        tlvDataStream.write(ExternalMessage.intToHexBuf(ad.length));
                        tlvDataStream.write(ad);
                    } else {
                        tlvDataStream.write(ExternalMessage.intToHexBuf(0));
                    }
                }
            }
            //DF05
            String[] displayMessages = params.getDisplayMessages();
            int pinMessageMode = ((ExtendedExtPINEntryParams) params).getPinMessageMode().ordinal();
            if (pinMessageMode == 1 && displayMessages.length != 3) {
                throw new NSDKIllegalParameterException("It should be 3 display messages when using the transaction type mode.");
            }
            tlvDataStream.write(ISOUtils.hex2byte("DF05"));
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            //Style
            stream.write(pinMessageMode);
            if (pinMessageMode == 1) {
                for (String displayMessage : displayMessages) {
                    if (!TextUtils.isEmpty(displayMessage)) {
                        byte[] temp = displayMessage.getBytes();
                        stream.write(temp.length);
                        stream.write(temp, 0, temp.length);
                    }
                }
            } else {
                if (displayMessages != null || displayMessages.length != 0) {
                    for (String displayMessage : displayMessages) {
                        if (!TextUtils.isEmpty(displayMessage)) {
                            byte[] temp = displayMessage.getBytes();
                            stream.write(temp, 0, temp.length);
                        }
                        stream.write(0x1C);
                    }
                }
            }
            byte[] temp = stream.toByteArray();
            tlvDataStream.write(temp.length);
            tlvDataStream.write(temp, 0, temp.length);
            //DF06
            tlvDataStream.write(ISOUtils.hex2byte("DF06"));
            tlvDataStream.write(1);
            tlvDataStream.write(params.getPinMaskAlignment().ordinal() + 1);
            //DF07
            tlvDataStream.write(ISOUtils.hex2byte("DF07"));
            tlvDataStream.write(1);
            tlvDataStream.write(((ExtendedExtPINEntryParams) params).isCheckIcPresent() ? 1 : 0);
            messageStream.write(ExternalMessage.intToHexBuf(tlvDataStream.size()));
            messageStream.write(tlvDataStream.toByteArray());
            return messageStream.toByteArray();
        } catch (IOException e) {
            throw new NSDKExternalDeviceException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }
    }
    public void setValidPINLengthRange(byte[] range) throws NSDKException {
        if (range == null || range.length == 0) {
            throw new NSDKIllegalParameterException("PIN length range shall not be null.");
        }
        if (range.length > 12) {
            throw new NSDKIllegalParameterException("PIN length range shall not be more than 12.");
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.SET_VALID_PIN_LENGTH_REQUEST);
        try (ByteArrayOutputStream messageStream = new ByteArrayOutputStream()){
            messageStream.write(range.length);
            messageStream.write(range);
            requestMessage.setMessageData(messageStream.toByteArray());
        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.SET_VALID_PIN_LENGTH_RESPONSE, null);
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new NSDKException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }
        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));
        if (responseCode != ErrorCode.OK) {
            throw new NSDKException(ErrorCode.PARAM_ERROR, ExternalErrorMessage.INVALID_PARAMETER);
        }
    }

    public void setPinLine(byte lineNumber, int alignment) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.SET_PIN_LINE_REQUEST);
        try (ByteArrayOutputStream messageStream = new ByteArrayOutputStream()) {
            //Line Number
            messageStream.write(lineNumber);
            //TLV Data
            ByteArrayOutputStream tlvDataStream = new ByteArrayOutputStream();
            //DF01
            tlvDataStream.write(ISOUtils.hex2byte("DF01"));
            tlvDataStream.write(1);
            tlvDataStream.write(alignment);
            messageStream.write(ExternalMessage.intToHexBuf(tlvDataStream.size()));
            messageStream.write(tlvDataStream.toByteArray());
            requestMessage.setMessageData(messageStream.toByteArray());
        } catch (IOException e) {
            throw new NSDKException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.SET_PIN_LINE_RESPONSE, null);

        // Response message data = Response code(2 bytes)
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));
        if (responseCode != ErrorCode.OK) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (1 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_LINE_NUMBER_ERROR, "Line number error.", innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }
    }

    public void inputData(InputItem[] inputItems, InputParameters parameters, InputListener listener) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.INPUT_DATA_REQUEST);
        try (ByteArrayOutputStream messageStream = new ByteArrayOutputStream()) {
            ByteArrayOutputStream tlvDataStream = new ByteArrayOutputStream();
            if (parameters != null) {
                Key encryptKey = parameters.getEncryptKey();
                if (!(encryptKey instanceof SymmetricKey)) {
                    throw new NSDKIllegalParameterException("Encrypt key shall be symmetric key.");
                }
                //DF01
                tlvDataStream.write(ISOUtils.hex2byte("DF01"));
                byte[] iv = parameters.getIv();
                int ivLen = iv == null ? 0 : iv.length;
                tlvDataStream.write(3 + ivLen);
                //Key ID
                tlvDataStream.write(encryptKey.getKeyID());
                KeyUsage keyUsage = ((SymmetricKey) encryptKey).getKeyUsage();
                if (keyUsage != KeyUsage.DATA && keyUsage != KeyUsage.DATA_ENC_ONLY && keyUsage != KeyUsage.DUKPT) {
                    throw new NSDKIllegalParameterException("Key usage shall be DATA, DATA_ENC_ONLY or DUKPT");
                }
                //Key Usage
                tlvDataStream.write(keyUsage.getCode());
                //Cipher Type
                CipherType cipherType = getCipherType(parameters);
                tlvDataStream.write(cipherType.getCode());
                //IV
                if (iv != null && ivLen != 0) {
                    tlvDataStream.write(iv);
                }
                //DF02
                tlvDataStream.write(ISOUtils.hex2byte("DF02"));
                tlvDataStream.write(2);
                int promptLine = parameters.getPromptLine();
                if (promptLine <1 || promptLine > 5) {
                    throw new NSDKIllegalParameterException("Prompt line shall range from 1 to 5.");
                }
                tlvDataStream.write(promptLine);
                int displayLine = parameters.getDisplayLine();
                if (displayLine < 1 || displayLine > 5) {
                    throw new NSDKIllegalParameterException("Display line shall range from 1 to 5.");
                }
                tlvDataStream.write(displayLine);
                //DF03
                tlvDataStream.write(ISOUtils.hex2byte("DF03"));
                int bytePassKey = parameters.getBytePassKey();
                if (bytePassKey != 0 && bytePassKey != 1) {
                    throw new NSDKIllegalParameterException("ByPass Key shall be 0 or 1.");
                }
                tlvDataStream.write(1);
                tlvDataStream.write(parameters.getBytePassKey());
                //DF8102
                tlvDataStream.write(ISOUtils.hex2byte("DF8102"));
                InputButtonParameters[] buttonParameters = parameters.getButtons();
                if (buttonParameters != null) {
                    if (buttonParameters.length > 8) {
                        throw new NSDKIllegalParameterException("Total button number shall not be more than 8.");
                    }
                    tlvDataStream.write(buttonParameters.length * 10 + 1);
                    tlvDataStream.write(buttonParameters.length);
                    for (InputButtonParameters button : buttonParameters) {
                        tlvDataStream.write(button.getId());
                        tlvDataStream.write(button.getButtonSettings());
                        tlvDataStream.write(ExternalMessage.intToHexBuf(button.getX()));
                        tlvDataStream.write(ExternalMessage.intToHexBuf(button.getY()));
                        tlvDataStream.write(ExternalMessage.intToHexBuf(button.getWidth()));
                        tlvDataStream.write(ExternalMessage.intToHexBuf(button.getHeight()));
                    }
                }
                messageStream.write(ExternalMessage.intToHexBuf(tlvDataStream.toByteArray().length));
                messageStream.write(tlvDataStream.toByteArray());
            } else {
                messageStream.write(ExternalMessage.intToHexBuf(0));
            }
            messageStream.write(inputItems.length);
            for (InputItem inputItem : inputItems) {
                if (inputItem.getType() == null) {
                    throw new NSDKIllegalParameterException("Input type of the input items shall not be null.");
                }
                messageStream.write(inputItem.getType().ordinal() + 1);
                messageStream.write(inputItem.getInputSettings());
                messageStream.write(inputItem.getFormatCode());
                int minDigits = inputItem.getMinDigits();
                if (minDigits < 0) {
                    throw new NSDKIllegalParameterException("Minimum digits shall be >=0.");
                }
                messageStream.write(minDigits);
                int maximumDigits = inputItem.getMaxDigits();
                if (maximumDigits < 0) {
                    throw new NSDKIllegalParameterException("Maximum digits shall be >=0.");
                }
                messageStream.write(inputItem.getMaxDigits());
                if (inputItem.getTimeout() <= 0) {
                    throw new NSDKIllegalParameterException("Timeout of the input items shall be >0.");
                }
                messageStream.write(ExternalMessage.intToHexBuf(inputItem.getTimeout()));
            }
            requestMessage.setMessageData(messageStream.toByteArray());
        } catch (IOException e) {
           listener.onError(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR);
        }

        int timeout = 0;
        for (InputItem item : inputItems) {
            timeout += item.getTimeout() * 1000;
        }
        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceive(requestMessage, ExternalMessageType.INPUT_DATA_RESPONSE, null, timeout);
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 2) {
            listener.onError(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }
        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));
        if (responseCode != ErrorCode.OK) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (1 == responseCode) {
                listener.onError(ErrorCode.PARAM_ERROR, ExternalErrorMessage.INVALID_PARAMETER);
                return;
            }
            if (2 == responseCode) {
                listener.onError(ErrorCode.EXT_ERROR, "Unknown input filed id exists.");
                return;
            }
            if (3 ==  responseCode) {
                listener.onError(ErrorCode.EXT_ERROR, "Too much input(one command can only support 10 times continuous input).");
                return;
            }
            if (4 == responseCode) {
                listener.onError(ErrorCode.EXT_UNKNOWN_ERROR, "Unknown error.");
                return;
            }
            if (5 == responseCode) {
                listener.onError(ErrorCode.CANCELLED, ExternalErrorMessage.CANCELLED);
                return;
            }
            if (6 == responseCode) {
                listener.onError(ErrorCode.TIMEOUT, ExternalErrorMessage.TIMEOUT);
                return;
            }
            if (43 == responseCode) {
                listener.onError(ErrorCode.EXT_PINPAD_BAD_KEY_INDEX, ExternalErrorMessage.PINPAD_BAD_KEY_INDEX);
                return;
            }
            if (45 == responseCode) {
                listener.onError(ErrorCode.EXT_COMMAND_LENGTH_ERROR, "Command Length Error.");
                return;
            }
            if (55 == responseCode) {
                listener.onError(ErrorCode.UNSUPPORTED, ExternalErrorMessage.NOT_SUPPORTED);
                return;
            }
            listener.onError(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR);
            return;
        }
        byte[] tlvPackageData = new byte[responseMessageData.length - 2];
        System.arraycopy(responseMessageData, 2, tlvPackageData, 0, tlvPackageData.length);
        int tlvLen = ExternalMessage.hexBuffer2Int(Arrays.copyOf(tlvPackageData, 2));
        int number = 0;
        if (tlvLen > 0) {
            byte[] tlvData = new byte[tlvLen];
            System.arraycopy(tlvPackageData, 2, tlvData, 0, tlvLen);
            List<TLVElement> tlvElementList = TLVUtils.getTLVElements(tlvData);
            for (TLVElement tlvElement : tlvElementList) {
                Log.d("debug", "tag:" + tlvElement.getTag() + "\ntagLen:" + tlvElement.getLen() + "\nvalue:" + ISOUtils.hexString(tlvElement.getValue()));
                int tag = tlvElement.getTag();
                switch (tag) {
                    case 0xDF01:
                    case 0xDF02:
                    case 0xDF03:
                    case 0xDF04:
                    case 0xDF05:
                    case 0xDF06:
                    case 0xDF07:
                        if (tlvElement.getLen() > 0) {
                            byte[] tlvValue = tlvElement.getValue();
                            int actualLen = tlvValue[0];
                            int dataLen = tlvValue[1];
                            Log.d("debug", "tlvValue:" + ISOUtils.hexString(tlvValue) + "dataLen:" + dataLen);
                            if (dataLen > 0) {
                                byte[] inputItemValue = new byte[dataLen];
                                System.arraycopy(tlvValue, 2, inputItemValue, 0, dataLen);
                                Log.d("debug", "InputItemValue:" + ISOUtils.hexString(inputItemValue));
                                inputItems[number].setButtonCode(0x00);
                                inputItems[number].setValue(inputItemValue);
                                inputItems[number].setActualLen(actualLen);
                            }
                            number++;
                        }
                        break;
                    case 0xDF8101:
                        for (int i = 0; i < inputItems.length; i++) {
                            inputItems[i].setButtonCode(tlvElement.getValue()[i]);
                        }
                        break;
                }
            }
            listener.onComplete();
        }
    }


    public void inputAmount(AmountType amountType, AmountParameters parameters, int timeout, AmountListener amountListener) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.INPUT_AMOUNT_REQUEST);
        try (ByteArrayOutputStream messageStream = new ByteArrayOutputStream()) {
            ByteArrayOutputStream tlvDataStream = new ByteArrayOutputStream();
            //DF01
            if (parameters.getTotalAmount() != null) {
                tlvDataStream.write(ISOUtils.hex2byte("DF01"));
                byte[] totalAmount = parameters.getTotalAmount().toString().replace(".", "").getBytes();
                tlvDataStream.write(totalAmount.length);
                tlvDataStream.write(totalAmount);
            }
            //DF02
            float[] tipPercentages = parameters.getTipPercentages();
            if (tipPercentages != null && tipPercentages.length != 0) {
                if (tipPercentages.length != 4) {
                    throw new NSDKIllegalParameterException("Tip percentages length shall be 4.");
                }
                tlvDataStream.write(ISOUtils.hex2byte("DF02"));
                tlvDataStream.write(8);
                for (float tipPercentage : tipPercentages) {
                    tlvDataStream.write(floatTo2ByteBCD(tipPercentage));
                }
            }
            //DF03
            BigDecimal[] tipSuggestions = parameters.getTipSuggestions();
            if (tipSuggestions != null && tipSuggestions.length != 0) {
                if (tipSuggestions.length != 4) {
                    throw new NSDKIllegalParameterException("Tip suggestions length shall be 4.");
                }
                tlvDataStream.write(ISOUtils.hex2byte("DF03"));
                tlvDataStream.write(8);
                for (BigDecimal tipSuggestion : tipSuggestions) {
                    tlvDataStream.write(ExternalMessage.intToBcdBuffer(tipSuggestion.intValue()));
                }
            }
            //DF04
            BigDecimal tipCalculation = parameters.getTipCalculationAmount();
            if (tipCalculation != null) {
                tlvDataStream.write(ISOUtils.hex2byte("DF04"));
                byte[] tipCalculationAmount = tipCalculation.toString().replace(".", "").getBytes();
                tlvDataStream.write(tipCalculationAmount.length);
                tlvDataStream.write(tipCalculationAmount);
            }
            Log.d("debug", "tlvData:" + ISOUtils.hexString(tlvDataStream.toByteArray()) + "\ntlvData len:" + tlvDataStream.size());
            messageStream.write(ExternalMessage.intToHexBuf(tlvDataStream.size()));
            messageStream.write(tlvDataStream.toByteArray());
            messageStream.write(amountType.ordinal());
            String title = parameters.getTitle();
            if (!TextUtils.isEmpty(title)) {
                byte[] titleData = title.getBytes();
                messageStream.write(titleData.length);
                messageStream.write(titleData);
            } else {
                messageStream.write(0);
            }
            String text = parameters.getText();
            if (!TextUtils.isEmpty(text)) {
                byte[] textData = text.getBytes();
                messageStream.write(textData.length);
                messageStream.write(textData);
            } else {
                messageStream.write(0);
            }
            int maxDigits = parameters.getMaxDigits();
            if (maxDigits < 4 || maxDigits > 12) {
                throw new NSDKIllegalParameterException("Max digits shall between 4 to 12.");
            }
            messageStream.write(parameters.getMaxDigits());
            messageStream.write(ExternalMessage.intToHexBuf(timeout));
            requestMessage.setMessageData(messageStream.toByteArray());
        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceive(requestMessage, ExternalMessageType.INPUT_AMOUNT_RESPONSE, null, timeout * 1000);
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new NSDKExternalDeviceException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }
        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));
        if (responseCode != ErrorCode.OK) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (1 == responseCode) {
                throw new NSDKIllegalParameterException();
            }
            if (4 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, "Other error.", innerErrorCode);
            }
            if (5 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.CANCELLED, ExternalErrorMessage.CANCELLED, innerErrorCode);
            }
            if (6 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.TIMEOUT, ExternalErrorMessage.TIMEOUT, innerErrorCode);
            }
            if (45 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_COMMAND_LENGTH_ERROR, "Command Length Error.", innerErrorCode);
            }
            if (55 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_UNSUPPORTED, ExternalErrorMessage.NOT_SUPPORTED, innerErrorCode);
            }
            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }
        byte[] len = new byte[2];
        System.arraycopy(responseMessageData, 2, len, 0, 2);
        int dataLen = ExternalMessage.hexBuffer2Int(len);
        Log.d("debug", "dataLen:" + dataLen);
        byte[] data = new byte[dataLen];
        System.arraycopy(responseMessageData, 4, data, 0, dataLen);
        byte[] intValue = Arrays.copyOf(data, dataLen - 2);
        byte[] fractionValue = new byte[2];
        System.arraycopy(data, dataLen - 2, fractionValue, 0, 2);
        Log.d("debug", "intValue:" + new String(intValue) + ", fractionValue:" + ISOUtils.hexString(fractionValue));
        BigDecimal amount = new BigDecimal(new String(intValue) + "." + new String(fractionValue));
        amountListener.onResult(amount);
    }

    public void verifyOfflinePIN(RSAKey rsaKey, int timeout, byte pinMaxDigits, String[] displayMessages, ExtPINEntryListener listener) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.OFFLINE_PIN_VERIFY_REQUEST);
        try (ByteArrayOutputStream messageStream = new ByteArrayOutputStream()) {
            //PIN max length
            if (pinMaxDigits < 4 || pinMaxDigits > 12) {
                throw new NSDKIllegalParameterException(ErrorCode.PARAM_ERROR, "PIN max digits shall range between 4 to 12.");
            }
            messageStream.write(pinMaxDigits);
            //Timeout
            messageStream.write(ExternalMessage.intToHexBuf(timeout));
            //Modulus
            if (rsaKey == null || rsaKey.getModulus() == null || rsaKey.getModulus().length == 0) {
                //plain text verification
                messageStream.write(0);
            } else {
                //cipher text verification
                byte[] modulus = rsaKey.getModulus();
                messageStream.write(modulus.length);
                messageStream.write(modulus);
            }
            //Exponent
            if (rsaKey != null && rsaKey.getExponent() != null) {
                byte[] exponent = rsaKey.getExponent();
                if (exponent.length != 3) {
                    throw new NSDKIllegalParameterException(ErrorCode.PARAM_ERROR, "Exponent shall not be null");
                }
                messageStream.write(3);
                messageStream.write(exponent);
            } else {
                messageStream.write(3);
                messageStream.write(new byte[3]);
            }
            if (displayMessages != null && displayMessages.length != 0) {
                int textLen = 0;
                for (String displayMessage : displayMessages) {
                    textLen += displayMessage.getBytes().length;
                    textLen ++;
                }
                messageStream.write(textLen);
                for (String displayMessage : displayMessages) {
                    messageStream.write(displayMessage.getBytes(StandardCharsets.UTF_8));
                    messageStream.write(0x1C);
                }
            } else {
                messageStream.write(0);
            }
            requestMessage.setMessageData(messageStream.toByteArray());
        } catch (IOException e) {
            listener.onError(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR);
        }
        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceive(requestMessage, ExternalMessageType.OFFLINE_PIN_VERIFY_RESPONSE, null, timeout * 1000);
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 2) {
            listener.onError(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }
        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));
        if (responseCode != ErrorCode.OK) {
            if (1 == responseCode) {
               throw new NSDKExternalDeviceException(ErrorCode.PARAM_ERROR, "Parameters error.");
            }
            if (3 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.TIMEOUT, "Timeout.");
            }
            if (4 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, "Other Error(Maybe pKey Parameters is invalid).");
            }
            if (5 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.CANCELLED, ExternalErrorMessage.CANCELLED);
            }
            if (45 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_COMMAND_LENGTH_ERROR, ExternalErrorMessage.DATA_LENGTH_NOT_CORRECT);
            }
            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR);
        }
        byte[] sw = new byte[2];
        System.arraycopy(responseMessageData, 2, sw, 0, 2);
        listener.onOfflineSuccess(2, sw, null);
    }

    private static CipherType getCipherType(InputParameters parameters) throws NSDKIllegalParameterException {
        CipherType cipherType = parameters.getCipherType();
        if (cipherType != CipherType.DES_ECB && cipherType != CipherType.DES_CBC && cipherType != CipherType.AES_ECB && cipherType != CipherType.AES_CBC
         && cipherType != CipherType.DUKPT_ECB_BOTH && cipherType != CipherType.DUKPT_ECB_RESP && cipherType != CipherType.DUKPT_CBC_BOTH && cipherType != CipherType.DUKPT_CBC_RESP) {
            throw new NSDKIllegalParameterException("Cipher type shall be DES_CBC, DES_ECB, AES_ECB, AES_CBC, DUKPT_ECB_BOTH, DUKPT_ECB_RESP, DUKPT_CBC_BOTH or DUKPT_CBC_RESP.");
        }
        return cipherType;
    }

    private boolean isInputTypeExists(String tag) {
        switch (tag) {
            case "DF01":
            case "DF02":
            case "DF03":
            case "DF04":
            case "DF05":
            case "DF06":
            case "DF07":
                return true;
            default:
                return false;
        }
    }

    private byte[] getValidPinRange(byte[] pinRange) {
        ByteArrayOutputStream validPinRangeStream = new ByteArrayOutputStream();
        for (byte range : pinRange) {
            Log.d("debug", "range:" + range);
            if (range >= 0x04 && range <= 0x0C) {
                validPinRangeStream.write(range);
            }
        }
        Log.d("debug", "valid size:" + validPinRangeStream.size());
        return validPinRangeStream.toByteArray();
    }

    private static byte[] floatTo2ByteBCD(float f) {
        byte[] result = new byte[2];
        String[] strs = String.valueOf(f).split("\\.");
        Log.d("debug", "strs[0]:" + strs[0] + "\n strs[1]:" + strs[1]);
        if (strs[1].length() == 1) {
            strs[1] = strs[1] + "0";
        }
        result[0] = ISOUtils.hex2byte(strs[0])[0];
        result[1] = ISOUtils.hex2byte(strs[1])[0];
        return result;
    }
}
