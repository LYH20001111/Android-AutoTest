package com.newland.nsdk.core.external.command.cipher;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.crypto.AsymAlgorithmParameters;
import com.newland.nsdk.core.api.common.crypto.AsymCryptoMode;
import com.newland.nsdk.core.api.common.crypto.AsymEncodingMode;
import com.newland.nsdk.core.api.common.crypto.CipherOutput;
import com.newland.nsdk.core.api.common.crypto.CipherType;
import com.newland.nsdk.core.api.common.crypto.MACOutput;
import com.newland.nsdk.core.api.common.crypto.MACType;
import com.newland.nsdk.core.api.common.crypto.MessageDigestType;
import com.newland.nsdk.core.api.common.crypto.PaddingMode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.keymanager.AsymmetricKey;
import com.newland.nsdk.core.api.common.keymanager.KeyUsage;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.api.external.exception.NSDKExternalDeviceException;
import com.newland.nsdk.core.external.command.exception.ExternalErrorMessage;
import com.newland.nsdk.core.external.command.exception.ExternalMessageException;
import com.newland.nsdk.core.external.command.message.ExternalMessage;
import com.newland.nsdk.core.external.command.message.ExternalMessageType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Provides the ability to encrypt/decrypt data and generate MAC.
 */
public class ExternalCipherModule {
    /**
     * Encrypt or decrypt data with specified key.
     *
     * @param keyIndex       The key used to encrypt or decrypt data. Value range [1-128].
     * @param dataKeyMode    Indicates whether to encrypt or decrypt data with CBC or ECB algorithm.
     *                       <ul>
     *                       <li>1: CBC Encryption</li>
     *                       <li>2: ECB Encryption</li>
     *                       <li>3: CBC Decryption</li>
     *                       <li>4: ECB Decryption</li>
     *                       </ul>
     * @param iv             This is required when it is CBC encryption/decryption, otherwise set it to null.
     * @param dataIn         The data for encryption or decryption.
     * @param protectKeyMode Indicates the algorithm used to encrypt the protect key.
     *                       <ul>
     *                       <li>0: CBC</li>
     *                       <li>1: ECB</li>
     *                       </ul>
     * @param protectKey     Temporary key used to encrypt or decrypt data. If this key is set, it will use this key to encrypt or decrypt data instead of the specified key index.
     *                       <p>Note: This is reserved, not supported now. Set it to null.</p>
     * @return Cipher data for encryption, clear data for decryption.
     * @throws NSDKException
     */
    public byte[] encryptOrDecryptNdk(byte keyIndex, byte dataKeyMode, byte[] iv, byte[] dataIn, byte protectKeyMode, byte[] protectKey) throws NSDKException {
        if (dataIn == null || dataIn.length == 0) {
            throw new NSDKIllegalParameterException(ExternalErrorMessage.DATA_NULL_OR_EMPTY);
        }

        if (dataKeyMode == 1 || dataKeyMode == 3) {
            if (iv == null || iv.length == 0) {
                throw new NSDKIllegalParameterException(ExternalErrorMessage.EMPTY_IV);
            }
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.DATA_ENCRYPTION_DECRYPTION_REQUEST);

        // Request message data = Key index(1 byte) + Mode(1 byte) + Input data len(2 bytes) + Input data
        //                        + Encrypting key mode(1 byte) + Encrypting key len(2 bytes) + Encrypting key data + CBC IV(8 bytes)
        int requestMessageDataLen;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(keyIndex);
        outputStream.write(dataKeyMode);
        try {
            outputStream.write(ExternalMessage.intToHexBuf(dataIn.length));
            outputStream.write(dataIn);
            outputStream.write(protectKeyMode);
            if (protectKey != null) {
                outputStream.write(protectKey.length);
                outputStream.write(protectKey);
            } else {
                outputStream.write(0);
            }
            if (iv != null) {
                outputStream.write(iv);
            }
        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }
        requestMessage.setMessageData(outputStream.toByteArray());

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.DATA_ENCRYPTION_DECRYPTION_RESPONSE, null);

        // Response message data = Key index(1 byte) + Response code(2 bytes) + Encrypted data len(2 bytes) + Encrypted data
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length == 0) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }

        byte responseKeyIndex = responseMessageData[0];
        if (responseKeyIndex != keyIndex) {
            throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_KEY_INDEX, String.format("Response key index(%d) is not equal with request key index(%d)", responseKeyIndex, keyIndex));
        }

        if (responseMessageData.length < 3) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(new byte[]{responseMessageData[1], responseMessageData[2]});
        responseMessage.setResponseCode(responseCode);
        if (responseCode != ErrorCode.OK) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 3);
            if (responseCode == 2) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
            }

            if (responseCode == 42) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_KEY_TAG, ExternalErrorMessage.PINPAD_BAD_KEY_TAG, innerErrorCode);
            }

            if (responseCode == 43) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_KEY_INDEX, ExternalErrorMessage.PINPAD_BAD_KEY_INDEX, innerErrorCode);
            }

            if (responseCode == 45) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_DATA_LENGTH, ExternalErrorMessage.PINPAD_BAD_DATA_LENGTH, innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }

        if (responseMessageData.length < 5) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }

        int encryptedDataLen = ExternalMessage.hexBuffer2Int(new byte[]{responseMessageData[3], responseMessageData[4]});
        if (encryptedDataLen <= 0) {
            return null;
        }

        if (encryptedDataLen > responseMessageData.length - 5) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LEN_FIELD_ERROR, ExternalErrorMessage.DATA_LEN_FIELD_ERROR);
        }

        byte[] result = new byte[encryptedDataLen];
        System.arraycopy(responseMessageData, 5, result, 0, encryptedDataLen);
        return result;
    }

    /**
     * AES data encryption or decryption.
     *
     * @param mode     Encrypt or decrypt:
     *                 <ul>
     *                 <li>1: AES encryption</li>
     *                 <li>2: AES decryption</li>
     *                 </ul>
     * @param keyIndex AES key index.
     * @param data     The data for AES encryption or decryption.
     * @return Cipher data for encryption, clear data for decryption.
     * @throws NSDKException
     */
    public byte[] aesEncryptOrDecryptNdk(byte mode, byte keyIndex, byte[] data) throws NSDKException {
        if (data == null || data.length == 0) {
            throw new NSDKIllegalParameterException(ExternalErrorMessage.DATA_NULL_OR_EMPTY);
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.AES_DATA_ENCRYPTION_DECRYPTION_REQUEST);
        // Request message data = Key index(1 byte) + Mode(1 byte) + Input data len(1 byte) + Data + Encrypting key len(Set it to 0)
        byte[] requestMessageData = new byte[5 + data.length];
        int offset = 0;
        requestMessageData[offset] = keyIndex;
        offset++;
        requestMessageData[offset] = mode;
        offset++;
        System.arraycopy(ExternalMessage.intToHexBuf(data.length), 0, requestMessageData, offset, 2);
        offset += 2;
        System.arraycopy(data, 0, requestMessageData, offset, data.length);
        offset += data.length;
        requestMessageData[offset] = 0;
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.AES_DATA_ENCRYPTION_DECRYPTION_RESPONSE, null);

        // Response message data = Key index(1 byte) + Response code(2 bytes) + Encrypted data len(2 bytes) + Encrypted data
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length == 0) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }

        byte responseKeyIndex = responseMessageData[0];
        if (responseKeyIndex != keyIndex) {
            throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_KEY_INDEX, String.format("Response key index(%d) is not equal with request key index(%d)", responseKeyIndex, keyIndex));
        }

        if (responseMessageData.length < 3) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(new byte[]{responseMessageData[1], responseMessageData[2]});
        responseMessage.setResponseCode(responseCode);
        if (responseCode != ErrorCode.OK) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 3);
            if (2 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
            }

            if (43 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_KEY_INDEX, ExternalErrorMessage.PINPAD_BAD_KEY_INDEX, innerErrorCode);
            }

            if (45 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_DATA_LENGTH, ExternalErrorMessage.PINPAD_BAD_DATA_LENGTH, innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }

        if (responseMessageData.length < 5) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }

        int encryptedDataLen = ExternalMessage.hexBuffer2Int(new byte[]{responseMessageData[3], responseMessageData[4]});
        if (encryptedDataLen <= 0) {
            return null;
        }

        if (encryptedDataLen > responseMessageData.length - 5) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LEN_FIELD_ERROR, ExternalErrorMessage.DATA_LEN_FIELD_ERROR);
        }

        byte[] result = new byte[encryptedDataLen];
        System.arraycopy(responseMessageData, 5, result, 0, encryptedDataLen);
        return result;
    }

    /**
     * DUKPT encryption or decryption.
     *
     * @param mode       Encrypt or decrypt:
     *                   <ul>
     *                   <li>1: CBC Encryption</li>
     *                   <li>2: ECB Decryption</li>
     *                   <li>3: CBC Decryption</li>
     *                   <li>4: ECB Decryption</li>
     *                   </ul>
     * @param groupIndex The key used to encrypt or decrypt.
     * @param keyType    Indicates which key type is used to encrypt/decrypt data.
     *                   <ul>
     *                   <li>2: TPK</li>
     *                   <li>3: TAK</li>
     *                   <li>4: TDK</li>
     *                   <li>16: DUKPT</li>
     *                   </ul>
     * @param data       The data for encryption or decryption.
     * @param iv         CBC initial value. Only present when CBC mode, otherwise set it to null.
     * @return DUKPT encryption/decryption result.
     * @throws NSDKException
     */
    public CipherOutput dukptEncryptOrDecryptNdk(byte mode, byte groupIndex, byte keyType, byte[] iv, byte[] data) throws NSDKException {
        if (data == null || data.length == 0) {
            throw new NSDKIllegalParameterException(ExternalErrorMessage.DATA_NULL_OR_EMPTY);
        }

        boolean isIvInvalid = (mode == 1 || mode == 3) && (iv == null || iv.length != 8);
        if (isIvInvalid) {
            throw new NSDKIllegalParameterException("IV(8 bytes) is required when CBC encryption/decryption.");
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.DUKPT_DATA_ENCRYPTION_DECRYPTION_REQUEST);
        // Request message data = Group index(1 byte) + Key type(1 byte) + Mode(1 byte) + Data len(2 bytes) + data + CBC iv(8 bytes)
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(groupIndex);
        outputStream.write(keyType);
        outputStream.write(mode);
        try {
            outputStream.write(ExternalMessage.intToHexBuf(data.length));
            outputStream.write(data);
            if (iv != null && iv.length > 0) {
                outputStream.write(iv);
            }
        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }

        requestMessage.setMessageData(outputStream.toByteArray());

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.DUKPT_DATA_ENCRYPTION_DECRYPTION_RESPONSE, null);

        // Response message data = Response code(2 bytes) + Encrypted data len(2 bytes) + Encrypted data
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length == 0) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }

        if (responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(new byte[]{responseMessageData[0], responseMessageData[1]});
        responseMessage.setResponseCode(responseCode);
        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (1 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_KEY_INDEX, ExternalErrorMessage.PINPAD_BAD_KEY_INDEX, innerErrorCode);
            }

            if (2 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
            }

            if (3 == responseCode) {
                throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_PARAMETER, innerErrorCode);
            }

            if (4 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_DATA_LENGTH, ExternalErrorMessage.PINPAD_BAD_DATA_LENGTH, innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }

        if (responseMessageData.length == 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }

        int resultDataLen = responseMessageData.length - 2;
        byte[] resultData = new byte[resultDataLen];
        System.arraycopy(responseMessageData, 2, resultData, 0, resultDataLen);
        if (resultDataLen < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, ExternalErrorMessage.DATA_LENGTH_NOT_ENOUGH);
        }

        int offset = 0;
        int encryptedDataLen = ExternalMessage.hexBuffer2Int(new byte[]{resultData[0], resultData[1]});
        if (encryptedDataLen > resultDataLen - 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LEN_FIELD_ERROR, ExternalErrorMessage.DATA_LEN_FIELD_ERROR);
        }
        offset += 2;

        byte[] encryptedData = new byte[encryptedDataLen];
        System.arraycopy(resultData, offset, encryptedData, 0, encryptedDataLen);
        offset += encryptedDataLen;

        if (resultDataLen - offset < 10) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, ExternalErrorMessage.DATA_LENGTH_NOT_ENOUGH);
        }

        byte[] ksn = new byte[10];
        System.arraycopy(resultData, offset, ksn, 0, 10);
        return new CipherOutput(encryptedData, ksn);
    }

    public MACOutput generateMacNdk(byte keyIndex, byte keyType, byte macMode, byte blockFlag, byte[] dataIn, byte keyMode, byte[] encryptKey) throws NSDKException {
        if (dataIn == null || dataIn.length == 0) {
            throw new NSDKIllegalParameterException(ExternalErrorMessage.DATA_NULL_OR_EMPTY);
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.MAC_GENERATION_REQUEST);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(keyIndex);
        outputStream.write(keyType);
        outputStream.write(macMode);
        outputStream.write(blockFlag);
        try {
            outputStream.write(ExternalMessage.intToHexBuf(dataIn.length));
            outputStream.write(dataIn);
            outputStream.write(keyMode);
            if (encryptKey != null) {
                outputStream.write(encryptKey.length);
                outputStream.write(encryptKey);
            } else {
                outputStream.write(0);
            }
        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }
        requestMessage.setMessageData(outputStream.toByteArray());

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.MAC_GENERATION_RESPONSE, null);

        // Response message data = Key index(1 byte) + Response code(2 bytes) + DES mac(8 bytes) + AES mac(16 bytes) + DUKPT KSN(10 bytes)
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length == 0) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }

        byte responseKeyIndex = responseMessageData[0];
        if (responseKeyIndex != keyIndex) {
            throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_KEY_INDEX, String.format("Response key index(%d) is not equal with request key index(%d)", responseKeyIndex & 0xff, keyIndex & 0xff));
        }

        if (responseMessageData.length < 3) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(new byte[]{responseMessageData[1], responseMessageData[2]});
        responseMessage.setResponseCode(responseCode);
        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 3);
            if (2 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
            }

            if (4 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_INVALID_COMMAND_SEQUENCE, ExternalErrorMessage.INVALID_COMMAND_SEQUENCE, innerErrorCode);
            }

            if (42 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_KEY_USAGE, ExternalErrorMessage.PINPAD_BAD_KEY_USAGE, innerErrorCode);
            }

            if (43 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_KEY_INDEX, ExternalErrorMessage.PINPAD_BAD_KEY_INDEX, innerErrorCode);
            }

            if (45 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_DATA_LENGTH, ExternalErrorMessage.PINPAD_BAD_DATA_LENGTH, innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }

        // MAC data only returned when block flag is "2-last block" or "3-only block".
        if (blockFlag == 0 || blockFlag == 1) {
            return null;
        }

        if (responseMessageData.length == 3) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }

        MACOutput macOutput;
        int resultDataLen = responseMessageData.length - 3;
        byte[] resultData = new byte[resultDataLen];
        System.arraycopy(responseMessageData, 3, resultData, 0, resultDataLen);
        if (keyType == 0 || keyType == 1) {
            // 0-MK/SK, 1-DUKPT
            if (resultDataLen < 8) {
                throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, ExternalErrorMessage.DATA_LENGTH_NOT_ENOUGH);
            }

            byte[] desMac = new byte[8];
            System.arraycopy(resultData, 0, desMac, 0, 8);

            byte[] ksn = null;
            if (keyType == 1) {
                if (resultDataLen < 18) {
                    throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, ExternalErrorMessage.DATA_LENGTH_NOT_ENOUGH);
                }

                ksn = new byte[10];
                System.arraycopy(resultData, 8, ksn, 0, 10);
            }
            macOutput = new MACOutput(desMac, ksn);
        } else {
            // AES
            if (resultDataLen < 16) {
                throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, ExternalErrorMessage.DATA_LENGTH_NOT_ENOUGH);
            }

            byte[] aesMac = new byte[16];
            System.arraycopy(resultData, 0, aesMac, 0, 16);
            macOutput = new MACOutput(aesMac, null);
        }

        return macOutput;
    }

    public MACOutput generateMacNapi(byte keyIndex, MACType macType, byte[] iv, byte[] data, ExtMacBlockFlag blockFlag) throws NSDKException {
        if (macType == null || data == null || blockFlag == null) {
            throw new NSDKIllegalParameterException("MAC type, data and block flag shall not be null.");
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.MAC_GENERATION_NAPI_REQUEST);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(macType.ordinal());
        outputStream.write(keyIndex);
        try {
            if (iv != null) {
                outputStream.write(iv.length);
                outputStream.write(iv);
            } else {
                outputStream.write(0);
            }

            outputStream.write(blockFlag.getCode());
            outputStream.write(ExternalMessage.intToHexBuf(data.length));
            outputStream.write(data);

            // todo AD is not supported now.
            outputStream.write(ExternalMessage.intToHexBuf(0));
        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }

        requestMessage.setMessageData(outputStream.toByteArray());

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.MAC_GENERATION_NAPI_RESPONSE, null);

        // Response message data = Key index(1 byte) + Response code(2 bytes) + MAC len(2 bytes) + MAC + KSN len(2 bytes) + DUKPT KSN
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length == 0) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }

        byte responseKeyIndex = responseMessageData[0];
        if (responseKeyIndex != keyIndex) {
            throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_KEY_INDEX, String.format("Response key index(%d) is not equal with request key index(%d)", responseKeyIndex & 0xff, keyIndex & 0xff));
        }

        if (responseMessageData.length < 3) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(new byte[]{responseMessageData[1], responseMessageData[2]});
        responseMessage.setResponseCode(responseCode);
        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 3);
            if (1 == responseCode) {
                throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_PARAMETER, innerErrorCode);
            }

            if (2 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_IV_LENGTH, ExternalErrorMessage.PINPAD_BAD_IV_LENGTH, innerErrorCode);
            }

            if (3 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_DATA_LENGTH, ExternalErrorMessage.PINPAD_BAD_DATA_LENGTH, innerErrorCode);
            }

            if (4 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_INVALID_COMMAND_SEQUENCE, ExternalErrorMessage.INVALID_COMMAND_SEQUENCE, innerErrorCode);
            }

            if (5 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
            }

            if (43 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_KEY_INDEX, ExternalErrorMessage.PINPAD_BAD_KEY_INDEX, innerErrorCode);
            }

            if (45 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_CMD_LENGTH, ExternalErrorMessage.PINPAD_BAD_CMD_LENGTH, innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }

        // MAC data only returned when block flag is "2-last block" or "3-only block".
        if (blockFlag == ExtMacBlockFlag.FIRST || blockFlag == ExtMacBlockFlag.NEXT) {
            return null;
        }

        if (responseMessageData.length == 3) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }

        MACOutput macOutput;
        int resultDataLen = responseMessageData.length - 3;
        byte[] resultData = new byte[resultDataLen];
        System.arraycopy(responseMessageData, 3, resultData, 0, resultDataLen);
        int offset = 0;
        int macLen = resultData[offset];
        offset++;
        byte[] mac = null;
        if (macLen > 0) {
            if (!ExternalMessage.isDataEnough(offset, resultDataLen, macLen)) {
                throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, ExternalErrorMessage.DATA_LENGTH_NOT_ENOUGH);
            }
            mac = new byte[macLen];
            System.arraycopy(resultData, offset, mac, 0, macLen);
            offset += macLen;
        }

        byte[] ksn = null;
        if (offset < resultDataLen && resultData[offset] > 0) {
            int ksnLen = resultData[offset];
            offset++;
            if (!ExternalMessage.isDataEnough(offset, resultDataLen, ksnLen)) {
                throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, ExternalErrorMessage.DATA_LENGTH_NOT_ENOUGH);
            }
            ksn = new byte[ksnLen];
            System.arraycopy(resultData, offset, ksn, 0, ksnLen);
        }
        macOutput = new MACOutput(mac, ksn);
        return macOutput;
    }

    public CipherOutput encryptOrDecryptNapi(byte mode, SymmetricKey key, CipherType cipherType, PaddingMode paddingMode, byte[] iv, byte[] data) throws NSDKException {
        if (key == null || cipherType == null || data == null) {
            throw new NSDKIllegalParameterException("Key, cipher type and data shall not be null.");
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.DATA_ENCRYPTION_DECRYPTION_NAPI_REQUEST);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(mode);
        outputStream.write(key.getKeyID());
        outputStream.write(cipherType.getCode());
        if (key.getKeyUsage() == null) {
            outputStream.write(KeyUsage.DATA.getCode());
        } else {
            outputStream.write(key.getKeyUsage().getCode());
        }

        if (paddingMode == null) {
            outputStream.write(PaddingMode.NONE.getCode());
        } else {
            outputStream.write(paddingMode.getCode());
        }

        try {
            if (iv != null) {
                outputStream.write(iv.length);
                outputStream.write(iv);
            } else {
                outputStream.write(0);
            }

            outputStream.write(ExternalMessage.intToHexBuf(data.length));
            outputStream.write(data);

            // todo AD is not supported now.
            outputStream.write(ExternalMessage.intToHexBuf(0));
        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }

        requestMessage.setMessageData(outputStream.toByteArray());

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.DATA_ENCRYPTION_DECRYPTION_NAPI_RESPONSE, null);

        // Response message data = Response code(2 bytes) + Data len(2 bytes) + Data + KSN len(2 bytes) + KSN
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length == 0) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }

        if (responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(new byte[]{responseMessageData[0], responseMessageData[1]});
        responseMessage.setResponseCode(responseCode);
        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (1 == responseCode) {
                throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_PARAMETER, innerErrorCode);
            }

            if (2 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_IV_LENGTH, ExternalErrorMessage.PINPAD_BAD_IV_LENGTH, innerErrorCode);
            }

            if (3 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_DATA_LENGTH, ExternalErrorMessage.PINPAD_BAD_DATA_LENGTH, innerErrorCode);
            }

            if (4 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
            }

            if (43 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_KEY_INDEX, ExternalErrorMessage.PINPAD_BAD_KEY_INDEX, innerErrorCode);
            }

            if (45 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_CMD_LENGTH, ExternalErrorMessage.PINPAD_BAD_CMD_LENGTH, innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }

        int offset = 2;
        if (!ExternalMessage.isDataEnough(offset, responseMessageData.length, 2)) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, ExternalErrorMessage.DATA_LENGTH_NOT_ENOUGH);
        }

        int dataLen = ExternalMessage.hexBuffer2Int(new byte[]{responseMessageData[2], responseMessageData[3]});
        offset += 2;

        if (dataLen <= 0) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LEN_FIELD_ERROR, "Invalid data length(<=0).");
        }


        if (!ExternalMessage.isDataEnough(offset, responseMessageData.length, dataLen)) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, ExternalErrorMessage.DATA_LENGTH_NOT_ENOUGH);
        }

        byte[] resultData = new byte[dataLen];
        System.arraycopy(responseMessageData, offset, resultData, 0, dataLen);
        offset += dataLen;

        byte[] ksn = null;
        if (offset < responseMessageData.length) {
            int ksnLen = responseMessageData[offset] & 0xFF;
            offset++;
            if (ksnLen > 0) {
                if (!ExternalMessage.isDataEnough(offset, responseMessageData.length, ksnLen)) {
                    throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, ExternalErrorMessage.DATA_LENGTH_NOT_ENOUGH);
                }

                ksn = new byte[ksnLen];
                System.arraycopy(responseMessageData, offset, ksn, 0, ksnLen);
            }
        }

        return new CipherOutput(resultData, ksn);
    }

    public byte[] asymEncryptOrDecryptNapi(byte mode, AsymmetricKey key, MessageDigestType messageDigestType, AsymEncodingMode encodingMode, AsymCryptoMode asymCryptoMode, byte[] data) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.RSA_ENCTRYP_DESCRYPT_REQUEST);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            baos.write(mode);
            baos.write((byte)key.getKeyType().getCode());
            baos.write((byte) key.getKeyUsage().getCode());
            baos.write((byte) key.getKeyID());
            baos.write((byte) messageDigestType.ordinal());
            baos.write((byte) encodingMode.ordinal());
            baos.write((byte) asymCryptoMode.ordinal());

            baos.write(ExternalMessage.intToHexBuf(data.length));
            baos.write(data);
        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }

        requestMessage.setMessageData(baos.toByteArray());

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.RSA_ENCTRYP_DESCRYPT_RESPONSE, null);

        // Response message data = Response code(2 bytes) + Encrypted data len(2 bytes) + Encrypted data
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length == 0) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(new byte[]{responseMessageData[0], responseMessageData[1]});

        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (1 == responseCode) {
                throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_PARAMETER, innerErrorCode);
            }

            if (4 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
            }

            if (43 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_KEY_INDEX, ExternalErrorMessage.PINPAD_BAD_KEY_INDEX, innerErrorCode);
            }

            if (45 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_CMD_LENGTH, ExternalErrorMessage.PINPAD_BAD_CMD_LENGTH, innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }

        int dataLen = ExternalMessage.hexBuffer2Int(new byte[]{responseMessageData[2], responseMessageData[3]});
        if (dataLen > responseMessageData.length - 4) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LEN_FIELD_ERROR, ExternalErrorMessage.DATA_LEN_FIELD_ERROR);
        }

        if (dataLen > 0) {
            byte[] result = new byte[dataLen];
            System.arraycopy(responseMessageData, 4, result, 0, dataLen);
            return result;
        }

        return null;
    }

    public byte[] asymEncryptOrDecryptNdk(byte mode, byte keyId, byte[] data) throws NSDKException {

        throw new NSDKException("Not yet supported.");
    }

    public byte[] signVerifyAsym(boolean isSign, AsymmetricKey key, AsymAlgorithmParameters algorithmParameters, byte[] hash, byte[] signedData) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.RSA_SIGN_VERIFY_REQUEST);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            if(isSign) {
                baos.write(0x00);
            }else {
                baos.write(0x01);
            }

            baos.write((byte)key.getKeyType().getCode());
            baos.write((byte) key.getKeyUsage().getCode());
            baos.write((byte) key.getKeyID());
            baos.write((byte) algorithmParameters.getMessageDigestType().ordinal());
            baos.write((byte) algorithmParameters.getEncodingMode().ordinal());

            baos.write(ExternalMessage.intToHexBuf(hash.length));
            baos.write(hash);

            if(signedData == null){
                baos.write(new byte[]{0x00,0x00});
            }else {
                baos.write(ExternalMessage.intToHexBuf(signedData.length));
                baos.write(signedData);
            }

        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }

        requestMessage.setMessageData(baos.toByteArray());

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.RSA_SIGN_VERIFY_RESPONSE, null);

        // Response message data = Response code(2 bytes) + Encrypted data len(2 bytes) + Encrypted data
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length == 0) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(new byte[]{responseMessageData[0], responseMessageData[1]});

        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (1 == responseCode) {
                throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_PARAMETER, innerErrorCode);
            }

            if (4 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
            }

            if (43 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_KEY_INDEX, ExternalErrorMessage.PINPAD_BAD_KEY_INDEX, innerErrorCode);
            }

            if (45 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_CMD_LENGTH, ExternalErrorMessage.PINPAD_BAD_CMD_LENGTH, innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }

        if(!isSign){
            return null;
        }

        int dataLen = ExternalMessage.hexBuffer2Int(new byte[]{responseMessageData[2], responseMessageData[3]});
        if (dataLen > responseMessageData.length - 4) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LEN_FIELD_ERROR, ExternalErrorMessage.DATA_LEN_FIELD_ERROR);
        }

        if (dataLen > 0) {
            byte[] result = new byte[dataLen];
            System.arraycopy(responseMessageData, 4, result, 0, dataLen);
            return result;
        }

        return null;
    }

    public byte[] getRandeom(int len) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.RANDOM_REQUEST);
        // Request message data = Key index(1 byte) + Mode(1 byte) + Input data len(1 byte) + Data + Encrypting key len(Set it to 0)
        byte[] requestMessageData = new byte[2];
        byte[] lenBytes = ExternalMessage.intToBcdBuffer(len);
        System.arraycopy(lenBytes, 0, requestMessageData, 0, 2);
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.RANDOM_RESPONSE, null);

        // Response message data = Response code(2 bytes) + Encrypted data len(2 bytes) + Encrypted data
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length == 0) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }

        byte[] resCodeBytes = new byte[2];
        System.arraycopy(responseMessageData, 0, resCodeBytes, 0, 2);
        int responseCode = ExternalMessage.convertResponseCodeBufToInt(resCodeBytes);
        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (45 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_COMMAND_LENGTH_ERROR, ExternalErrorMessage.PINPAD_BAD_CMD_LENGTH, innerErrorCode);
            }
            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }

        if (len != responseMessageData.length - 4) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LEN_FIELD_ERROR, ExternalErrorMessage.DATA_LEN_FIELD_ERROR);
        }

        byte[] result = new byte[len];
        System.arraycopy(responseMessageData, 4, result, 0, len);
        return result;
    }
}
