package com.newland.nsdk.core.external.command.keymanager;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.crypto.AlgorithmParameters;
import com.newland.nsdk.core.api.common.crypto.KCVMode;
import com.newland.nsdk.core.api.common.crypto.PaddingMode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.keymanager.AsymAlgInfo;
import com.newland.nsdk.core.api.common.keymanager.AsymKeyType;
import com.newland.nsdk.core.api.common.keymanager.AsymKeyUsage;
import com.newland.nsdk.core.api.common.keymanager.AsymmetricKey;
import com.newland.nsdk.core.api.common.keymanager.CipherMode;
import com.newland.nsdk.core.api.common.keymanager.DUKPTKey;
import com.newland.nsdk.core.api.common.keymanager.Key;
import com.newland.nsdk.core.api.common.keymanager.KeyGenerateMethod;
import com.newland.nsdk.core.api.common.keymanager.KeyInfoID;
import com.newland.nsdk.core.api.common.keymanager.KeyType;
import com.newland.nsdk.core.api.common.keymanager.KeyUsage;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.common.keymanager.ST_SEC_ASYM_KEYIN_DATA;
import com.newland.nsdk.core.common.keymanager.ST_SEC_KCV_DATA;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;
import com.newland.nsdk.core.api.external.exception.NSDKExternalDeviceException;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.external.command.exception.ExternalErrorMessage;
import com.newland.nsdk.core.external.command.exception.ExternalMessageException;
import com.newland.nsdk.core.external.command.message.ExternalMessage;
import com.newland.nsdk.core.external.command.message.ExternalMessageType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Provides the ability to manage keys.
 */
public class ExternalKeyManagerModule {
    /**
     * Load encrypted key under protection of KEK.
     *
     * @param kekIndex  The index of KEK which used to protect the key block. It shall already exist in PIN pad, value range [1-255].
     * @param format    Supports the following block formats:
     *                  <ul>
     *                  <li>0: TR31 block</li>
     *                  <li>1: AESK</li>
     *                  <li>2: DES block</li>
     *                  </ul>
     * @param usage     Key usage. Supports the following values:
     *                  <ul>
     *                  <li>0: PIN</li>
     *                  <li>1: MAC</li>
     *                  <li>2: Data</li>
     *                  <li>3: Master key</li>
     *                  </ul>
     * @param keyIndex  Key index. Value range [1-255]
     * @param keyData   Key block data: When it is AES key, this block contains other information besides key value. So AES key length is required to get AES key data from this block.
     * @param kcv       Key check value. 3 bytes.
     * @param aesKeyLen When it is AES key, this is required to get AES key data. Otherwise set it to null.
     *                  <ul>The following lengths supported:
     *                  <li>16</li>
     *                  <li>24</li>
     *                  <li>32</li>
     *                  </ul>
     * @throws NSDKException
     */
    public void loadKeyBlock(byte kekIndex, byte format, byte usage, byte keyIndex, byte[] keyData, byte[] kcv, int aesKeyLen) throws NSDKException {
        if (keyData == null || keyData.length == 0) {
            throw new NSDKIllegalParameterException(ExternalErrorMessage.EMPTY_KEY_DATA);
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.LOAD_KEY_BLOCK_REQUEST);

        // Request message data = KEK index(1 byte) + Block format(1 byte) + AES key len(1 byte) + Key type(1 byte) + Key index(1 byte) + Block len(2 bytes) + Block data + KCV(3 bytes)
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(kekIndex);
        outputStream.write(format);

        if (format == 1) {
            outputStream.write(aesKeyLen);
        }
        outputStream.write(usage);
        outputStream.write(keyIndex);
        try {
            outputStream.write(ExternalMessage.intToHexBuf(keyData.length));
            outputStream.write(keyData);
            if (kcv != null) {
                outputStream.write(Arrays.copyOf(kcv, 3));
            }
        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }
        requestMessage.setMessageData(outputStream.toByteArray());

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.LOAD_KEY_BLOCK_RESPONSE, null);

        // Response message data = Response code(2 bytes)
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));
        responseMessage.setResponseCode(responseCode);
        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (2 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
            }

            if (42 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_KEY_TAG, ExternalErrorMessage.PINPAD_BAD_KEY_TAG, innerErrorCode);
            }

            if (43 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_KEY_INDEX, ExternalErrorMessage.PINPAD_BAD_KEY_INDEX, innerErrorCode);
            }

            if (45 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_DATA_LENGTH, ExternalErrorMessage.PINPAD_BAD_DATA_LENGTH, innerErrorCode);
            }

            if (46 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_INVALID_BLOCK, ExternalErrorMessage.PINPAD_INVALID_BLOCK, innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }
    }

    /**
     * Load DUKPT key block.
     *
     * @param kekIndex    KEK index. Value range [0-250].
     * @param groupIndex  Group index which block data will be loaded to. Value range [1-250]
     * @param blockFormat Supports the following block formats:
     *                    <ul>
     *                    <li>0: TR31</li>
     *                    <li>1: DUKPT</li>
     *                    </ul>
     * @param block       Block data.
     * @param ksn         Key serial number, 10 bytes. This is required when block format is DUKPT, otherwise set it to null.
     * @throws NSDKException
     */
    public void loadDukptBlock(byte kekIndex, byte groupIndex, byte blockFormat, byte[] block, byte[] ksn) throws NSDKException {
        if (block == null || block.length == 0) {
            throw new NSDKIllegalParameterException(ExternalErrorMessage.EMPTY_KEY_DATA);
        }
        if (blockFormat == 1) {
            if (ksn == null || ksn.length == 0) {
                throw new NSDKIllegalParameterException(ExternalErrorMessage.EMPTY_KSN);
            }
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.LOAD_DUKPT_KEY_BLOCK_REQUEST);

        // Request message data = KEK index(1 byte) + Group index(1 byte) + Block format(1 byte) + Block len(2 bytes) + Block data + KSN(10 bytes)
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(kekIndex);
        outputStream.write(groupIndex);
        outputStream.write(blockFormat);

        try {
            outputStream.write(ExternalMessage.intToHexBuf(block.length));
            outputStream.write(block);
            if (blockFormat == 1) {
                outputStream.write(ksn);
            }
        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }
        requestMessage.setMessageData(outputStream.toByteArray());

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.LOAD_DUKPT_KEY_BLOCK_RESPONSE, null);

        // Response message data = Response code(2 bytes)
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));
        responseMessage.setResponseCode(responseCode);
        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (1 == responseCode) {
                throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_PARAMETER, innerErrorCode);
            }

            if (2 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_DATA_LENGTH, ExternalErrorMessage.PINPAD_BAD_DATA_LENGTH, innerErrorCode);
            }

            if (3 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
            }

            if (4 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_KEY_INDEX, ExternalErrorMessage.KEY_INDEX_ERROR, innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }
    }

    /**
     * Get key check value.
     *
     * @param keyIndex The target key to get KCV. Value range: [1-255]
     * @param keyType  Supports the following types:
     *                 <ul>
     *                 <li>0: DES KEK</li>
     *                 <li>1: DES PIN</li>
     *                 <li>2: DES MAC</li>
     *                 <li>3: DES DATA</li>
     *                 <li>4: AES KEK</li>
     *                 <li>5: AES PIN</li>
     *                 <li>6: AES MAC</li>
     *                 <li>7: AES DATA</li>
     *                 </ul>
     * @return
     * @throws NSDKException
     */
    public byte[] getKcv(byte keyIndex, byte keyType) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.GET_KEY_CHECK_VALUE_REQUEST);

        // Request message data = Key index(1 byte) + Key type(1 byte)
        requestMessage.setMessageData(new byte[]{keyIndex, keyType});

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.GET_KEY_CHECK_VALUE_RESPONSE, null);

        // Response message data = Key index(1 byte) + KCV(3 or 5 bytes)
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 4) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }
        byte responseKeyIndex = responseMessageData[0];
        if (responseKeyIndex != keyIndex) {
            throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_KEY_INDEX, String.format("Response key index(%d) is not equal with request key index(%d)", responseKeyIndex, keyIndex));
        }
        int kcvLen = responseMessageData.length - 1;
        if (kcvLen > 3) {
            kcvLen = 3;
        }
        byte[] kcv = new byte[kcvLen];
        System.arraycopy(responseMessageData, 1, kcv, 0, kcvLen);
        return kcv;
    }

    /**
     * Get DUKPT KSN.
     *
     * @param keyIndex Index of DUKPT key.
     * @return Current KSN for the specified key.
     * @throws NSDKException
     */
    public byte[] getKsn(byte keyIndex) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.GET_DUKPT_KSN_REQUEST);

        // Request message data = Key index(1 byte)
        requestMessage.setMessageData(new byte[]{keyIndex});

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.GET_DUKPT_KSN_RESPONSE, null);

        // Response message data = Response code(2 bytes) + KSN(10 bytes)
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(new byte[]{responseMessageData[0], responseMessageData[1]});
        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
        }

        if (responseMessageData.length > 2) {
            int ksnLen = responseMessageData.length - 2;
            byte[] ksn = new byte[ksnLen];
            System.arraycopy(responseMessageData, 2, ksn, 0, ksnLen);
            return ksn;
        }

        return null;
    }

    /**
     * Load Giske key.
     *
     * @param kekType  KEK type.
     *                 <ul>
     *                 <li>0: TLK</li>
     *                 <li>1: TMK</li>
     *                 </ul>
     * @param kekId    KEK index, value range [0-255].
     * @param keyId    Key index: value range [1-255].
     * @param giskeKey Giske format data.
     * @param kcv      Key check value, 3 bytes.
     * @throws NSDKException
     */
    public void loadGiskeKey(byte kekType, byte kekId, byte keyId, byte[] giskeKey, byte[] kcv) throws NSDKException {
        if (giskeKey == null || giskeKey.length == 0) {
            throw new NSDKIllegalParameterException("Giske key is null or empty.");
        }

        if (kcv == null || kcv.length == 0) {
            throw new NSDKIllegalParameterException(ExternalErrorMessage.EMPTY_KCV);
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.LOAD_GISKE_KEY_REQUEST);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(kekType);
        outputStream.write(kekId);
        outputStream.write(keyId);
        try {
            outputStream.write(giskeKey.length);
            outputStream.write(giskeKey);
            outputStream.write(kcv);
        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }
        requestMessage.setMessageData(outputStream.toByteArray());

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.LOAD_GISKE_KEY_RESPONSE, null);
        if (responseMessage.getMessageData() == null || responseMessage.getMessageData().length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }
        byte[] responseMessageData = responseMessage.getMessageData();
        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));
        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (1 == responseCode) {
                throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_PARAMETER, innerErrorCode);
            }

            if (2 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }
    }

    /**
     * Load Giske Tik.
     *
     * @param kekType  KEK type.
     *                 <ul>
     *                 <li>0: TLK</li>
     *                 <li>1: TMK</li>
     *                 </ul>
     * @param kekId    KEK index, value range [0-255].
     * @param groupId  Group id.
     * @param giskeKey The key to load. Giske format data.
     * @param ksn      KSN, 10 bytes.
     * @param kcv      KCV, 3 bytes.
     * @throws NSDKException
     */
    public void loadGiskeTik(byte kekType, byte kekId, byte groupId, byte[] giskeKey, byte[] ksn, byte[] kcv) throws NSDKException {
        if (giskeKey == null || giskeKey.length == 0) {
            throw new NSDKIllegalParameterException("Giske key is null or empty.");
        }

        if (ksn == null || ksn.length == 0) {
            throw new NSDKIllegalParameterException("KSN key is null or empty.");
        }

        if (kcv == null || kcv.length == 0) {
            throw new NSDKIllegalParameterException(ExternalErrorMessage.EMPTY_KCV);
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.LOAD_GISKE_TIK_REQUEST);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(kekType);
        outputStream.write(kekId);
        outputStream.write(groupId);
        try {
            outputStream.write(giskeKey.length);
            outputStream.write(giskeKey);
            outputStream.write(ksn);
            outputStream.write(kcv);
        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }
        requestMessage.setMessageData(outputStream.toByteArray());

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.LOAD_GISKE_TIK_RESPONSE, null);
        if (responseMessage.getMessageData() == null || responseMessage.getMessageData().length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }
        byte[] responseMessageData = responseMessage.getMessageData();
        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));
        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (1 == responseCode) {
                throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_PARAMETER, innerErrorCode);
            }

            if (3 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }
    }


    /**
     * Convert a common key to a Giske key of specified key usage.
     *
     * @param keyIndex  The key used to protect the block.
     * @param keyType   The usage of target Giske key.
     *                  <ul>
     *                  <li>1: master key</li>
     *                  <li>2: pin key</li>
     *                  <li>3: mac key</li>
     *                  <li>4: data key</li>
     *                  </ul>
     * @param blockData The key block data.
     * @throws NSDKException
     */
    public void convertAtmToGiske(byte keyType, byte keyIndex, byte[] blockData) throws NSDKException {
        if (blockData == null || blockData.length == 0) {
            throw new NSDKIllegalParameterException("Block data is null or empty.");
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.LOAD_CONVERT_ATM_TO_GISKE_REQUEST);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(keyType);
        outputStream.write(keyIndex);
        outputStream.write(blockData.length);
        try {
            outputStream.write(blockData);
        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }
        requestMessage.setMessageData(outputStream.toByteArray());

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.LOAD_CONVERT_ATM_TO_GISKE_RESPONSE, null);
        if (responseMessage.getMessageData() == null || responseMessage.getMessageData().length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }
        byte[] responseMessageData = responseMessage.getMessageData();
        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));
        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (1 == responseCode) {
                throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_PARAMETER, innerErrorCode);
            }

            if (2 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }
    }

    /**
     * Delete a specified key.
     *
     * @param keyIndex    The key to delete, value range: [1, 255]
     * @param keyType     The type of the key.
     *                    <ul>
     *                    <li>0: PIN key</li>
     *                    <li>1: MAC key</li>
     *                    <li>2: Data key</li>
     *                    <li>3: Master key</li>
     *                    <li>4: DUKPT key</li>
     *                    </ul>
     * @param blockFormat Block format.
     *                    <ul>
     *                    <li>0: DES</li>
     *                    <li>1: AES</li>
     *                    <li>2: RSA</li>
     *                    </ul>
     */
    public void deleteKey(byte keyIndex, byte keyType, byte blockFormat) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.DELETE_KEY_REQUEST);
        requestMessage.setMessageData(new byte[]{keyIndex, keyType, blockFormat});

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.DELETE_KEY_RESPONSE, null);

        if (responseMessage.getMessageData() == null || responseMessage.getMessageData().length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }
        byte[] responseMessageData = responseMessage.getMessageData();
        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));
        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (1 == responseCode) {
                throw new NSDKExternalDeviceException(responseCode, ExternalErrorMessage.PINPAD_BAD_KEY_INDEX, innerErrorCode);
            }

            if (2 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_KEY_TYPE_ERROR, "Key type error.", innerErrorCode);
            }

            if (3 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_COMMAND_LENGTH_ERROR, "CMD length error.", innerErrorCode);
            }

            if (4 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_FORMAT_ERROR, "Format error.", innerErrorCode);
            }

            if (5 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_DELETE_ERROR, "Failed to delete.", innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }
    }

    /**
     * Increase KSN of a specified group.
     *
     * @param groupId Group ID. Value range: [1, 250]
     */
    public void increaseKsn(byte groupId) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.DUKPT_KSN_INCREASE_REQUEST);
        requestMessage.setMessageData(new byte[]{groupId});

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.DUKPT_KSN_INCREASE_RESPONSE, null);

        if (responseMessage.getMessageData() == null || responseMessage.getMessageData().length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }
        byte[] responseMessageData = responseMessage.getMessageData();
        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));
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
    }

    public void generateKeyNapi(KeyGenerateMethod method, AlgorithmParameters algorithmParameters, SymmetricKey srcKey, Key dstKey, byte[] additionData) throws NSDKException {
        if (method == null || dstKey == null) {
            throw new NSDKIllegalParameterException("Key generation method and target key shall not be null.");
        }

        if (method != KeyGenerateMethod.CLEAR && (srcKey == null || srcKey.getKeyType() == null || srcKey.getKeyUsage() == null)) {
            throw new NSDKIllegalParameterException("Protection key and its usage shall not be null when target key is cipher text.");
        }

        if (dstKey.getKeyData() == null || (dstKey instanceof DUKPTKey && ((DUKPTKey) dstKey).getKSN() == null)) {
            throw new NSDKIllegalParameterException("Please check target key parameters(e.g., data, KSN)");
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.GENERATE_KEY_REQUEST);
        requestMessage.setMessageData(packGenerateKeyRequestMessageData(method, algorithmParameters, srcKey, dstKey, additionData));

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.GENERATE_KEY_RESPONSE, null);

        if (responseMessage.getMessageData() == null || responseMessage.getMessageData().length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        byte[] responseMessageData = responseMessage.getMessageData();
        int responseCode = ExternalMessage.convertResponseCodeBufToInt(new byte[]{responseMessageData[0], responseMessageData[1]});

        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (1 == responseCode) {
                throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_PARAMETER, innerErrorCode);
            }

            if (2 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_DATA_LENGTH, ExternalErrorMessage.PINPAD_BAD_DATA_LENGTH, innerErrorCode);
            }

            if (3 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.NOT_SUPPORTED, ExternalErrorMessage.NOT_SUPPORTED, innerErrorCode);
            }

            if (4 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
            }

            if (5 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_KCV_ERROR, ExternalErrorMessage.KCV_ERROR, innerErrorCode);
            }

            if (6 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_KEY_EXIST, ExternalErrorMessage.KEY_EXIST, innerErrorCode);
            }

            if (43 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_KEY_INDEX, ExternalErrorMessage.PINPAD_BAD_KEY_INDEX, innerErrorCode);
            }

            if (45 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_CMD_LENGTH, ExternalErrorMessage.PINPAD_BAD_CMD_LENGTH, innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }

        // todo AD is not supported now
    }

    public byte[] generateKeyWithAsymKey(int method, ST_SEC_ASYM_KEYIN_DATA keyinData, ST_SEC_KCV_DATA kcvData) throws NSDKException {

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.GENERATE_ASYM_KEY_REQUEST);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            baos.write((byte) method);
            baos.write((byte) keyinData.getUcKEKIdx());
            baos.write((byte) keyinData.getKEKType());
            baos.write((byte) keyinData.getKEKUsage());
            baos.write((byte) keyinData.getUcKeyIdx());
            baos.write((byte) keyinData.getKeyType());
            baos.write((byte) keyinData.getKeyUsage());
            baos.write((byte) keyinData.getMdAlg());
            baos.write((byte) keyinData.getEncodingMode());

            baos.write(ExternalMessage.intToHexBuf(keyinData.getnKeyLen()));
            if(method != KeyGenerateMethod.RANDOM_OUT.ordinal()) {
                baos.write(keyinData.getpKeyData());
            }

            baos.write((byte)keyinData.getnKsnLen());
            if(keyinData.getPsKsn() != null && keyinData.getnKsnLen() > 0) {
                baos.write(keyinData.getPsKsn());
            }

            baos.write((byte)kcvData.getnCheckMode());
            baos.write((byte)kcvData.getnLen());
            if(kcvData.getsCheckBuf() != null && kcvData.getnLen() > 0) {
                baos.write(kcvData.getsCheckBuf());
            }

            baos.write(new byte[]{0x00,0x00});

        }catch (IOException e){
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }

        requestMessage.setMessageData(baos.toByteArray());
        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.GENERATE_ASYM_KEY_RESPONSE, null);

        if (responseMessage.getMessageData() == null || responseMessage.getMessageData().length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        byte[] responseMessageData = responseMessage.getMessageData();
        int responseCode = ExternalMessage.convertResponseCodeBufToInt(new byte[]{responseMessageData[0], responseMessageData[1]});

        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (1 == responseCode) {
                throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_PARAMETER, innerErrorCode);
            }

            if (2 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_DATA_LENGTH, ExternalErrorMessage.PINPAD_BAD_DATA_LENGTH, innerErrorCode);
            }

            if (3 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.NOT_SUPPORTED, ExternalErrorMessage.NOT_SUPPORTED, innerErrorCode);
            }

            if (4 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
            }

            if (5 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_KCV_ERROR, ExternalErrorMessage.KCV_ERROR, innerErrorCode);
            }

            if (6 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_KEY_EXIST, ExternalErrorMessage.KEY_EXIST, innerErrorCode);
            }

            if (43 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_KEY_INDEX, ExternalErrorMessage.PINPAD_BAD_KEY_INDEX, innerErrorCode);
            }

            if (45 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_CMD_LENGTH, ExternalErrorMessage.PINPAD_BAD_CMD_LENGTH, innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }

        int adLen = ExternalMessage.hexBuffer2Int(new byte[]{responseMessageData[2], responseMessageData[3]});

        if(adLen == 0){
            return null;
        }else {
            byte[] adData = new byte[adLen];
            System.arraycopy(responseMessageData,4,adData,0,adLen);
            return adData;
        }
    }

    private byte[] packGenerateKeyRequestMessageData(KeyGenerateMethod method, AlgorithmParameters algorithmParameters, SymmetricKey srcKey, Key dstKey, byte[] additionalData) throws NSDKIllegalParameterException, ExternalMessageException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(method.getCode());
        if (method == KeyGenerateMethod.CLEAR) {
            outputStream.write(0);
            outputStream.write(KeyType.DES.getCode());
            outputStream.write(KeyUsage.KEK.getCode());
        } else {
            outputStream.write(srcKey.getKeyID());
            outputStream.write(srcKey.getKeyType().getCode());
            outputStream.write(srcKey.getKeyUsage().getCode());
        }

        outputStream.write(dstKey.getKeyID());
        if (dstKey instanceof SymmetricKey) {
            SymmetricKey tempSymmKey = (SymmetricKey) dstKey;

            if (tempSymmKey.getKeyType() == null || tempSymmKey.getKeyUsage() == null) {
                throw new NSDKIllegalParameterException("Target key type or key usage is null.");
            }
            outputStream.write(tempSymmKey.getKeyType().getCode());
            outputStream.write(tempSymmKey.getKeyUsage().getCode());
        } else if (dstKey instanceof AsymmetricKey) {
            AsymmetricKey tempAsymKey = (AsymmetricKey) dstKey;

            if (tempAsymKey.getKeyType() == null || tempAsymKey.getKeyUsage() == null) {
                throw new NSDKIllegalParameterException("Target key type or key usage is null.");
            }
            outputStream.write(tempAsymKey.getKeyType().getCode());
            outputStream.write(tempAsymKey.getKeyUsage().getCode());
        } else {
            throw new NSDKIllegalParameterException("Target key shall be a symmetric key or asymmetric key.");
        }
        byte[] iv = null;
        if (algorithmParameters != null && algorithmParameters.getCipherMode() != null) {
            iv = algorithmParameters.getIV();
            if (algorithmParameters.getCipherMode() == CipherMode.CBC && iv == null) {
                throw new NSDKIllegalParameterException("IV shall not be null when cipher mode is CBC.");
            }
            outputStream.write(algorithmParameters.getCipherMode().ordinal());
        } else {
            outputStream.write(CipherMode.ECB.ordinal());
        }

        if (algorithmParameters != null && algorithmParameters.getPaddingMode() != null) {
            outputStream.write(algorithmParameters.getPaddingMode().getCode());
        } else {
            outputStream.write(PaddingMode.NONE.getCode());
        }
        try {
            outputStream.write(ExternalMessage.intToHexBuf(dstKey.getKeyLen()));
            outputStream.write(ExternalMessage.intToHexBuf(dstKey.getKeyData().length));
            outputStream.write(dstKey.getKeyData());

            if (iv != null) {
                outputStream.write(iv.length);
                outputStream.write(iv);
            } else {
                outputStream.write(0);
            }

            if (dstKey instanceof DUKPTKey) {
                byte[] ksn = ((DUKPTKey) dstKey).getKSN();
                outputStream.write(ksn.length);
                outputStream.write(ksn);
            } else {
                outputStream.write(0);
            }

            if (dstKey instanceof SymmetricKey) {
                byte[] kcv = ((SymmetricKey) dstKey).getKCV();
                if(((SymmetricKey) dstKey).getKCVMode() != null) {
                    outputStream.write((byte) ((SymmetricKey) dstKey).getKCVMode().ordinal());
                }else {
                    outputStream.write(KCVMode.NONE.ordinal());
                }

                if (kcv != null) {

                    outputStream.write(kcv.length);
                    outputStream.write(kcv);
                } else {
                    outputStream.write(0);
                }
            } else {
                outputStream.write(KCVMode.NONE.ordinal());
                outputStream.write(0);
            }

            if (additionalData == null || additionalData.length == 0) {
                outputStream.write(ExternalMessage.intToHexBuf(0));
            } else {
                outputStream.write(ExternalMessage.intToHexBuf(additionalData.length));
                outputStream.write(additionalData);
            }
        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }
        return outputStream.toByteArray();
    }

    public byte[] getKeyInfoNapi(KeyInfoID keyInfoID, Key key) throws NSDKException {
        if (keyInfoID == null || key == null) {
            throw new NSDKIllegalParameterException("Key info ID and key shall not be null.");
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(keyInfoID.ordinal());
        outputStream.write(key.getKeyID());

        if (key instanceof SymmetricKey) {
            SymmetricKey tempSymmKey = (SymmetricKey) key;

            if (tempSymmKey.getKeyType() == null || tempSymmKey.getKeyUsage() == null) {
                throw new NSDKIllegalParameterException("Key type or key usage is null.");
            }
            outputStream.write(tempSymmKey.getKeyType().getCode());
            outputStream.write(tempSymmKey.getKeyUsage().getCode());
        } else if (key instanceof AsymmetricKey) {
            AsymmetricKey tempAsymKey = (AsymmetricKey) key;

            if (tempAsymKey.getKeyType() == null || tempAsymKey.getKeyUsage() == null) {
                throw new NSDKIllegalParameterException("Key type or key usage is null.");
            }
            outputStream.write(tempAsymKey.getKeyType().getCode());
            outputStream.write(tempAsymKey.getKeyUsage().getCode());
        } else {
            throw new NSDKIllegalParameterException("Target key shall be a symmetric key or asymmetric key.");
        }

        try {
            outputStream.write(ExternalMessage.intToHexBuf(0));
        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.GET_KEY_INFO_REQUEST);
        requestMessage.setMessageData(outputStream.toByteArray());

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.GET_KEY_INFO_RESPONSE, null);

        if (responseMessage.getMessageData() == null || responseMessage.getMessageData().length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        // response message data = response code(2 bytes) + data length(2 bytes) + data
        byte[] responseMessageData = responseMessage.getMessageData();
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

        if (responseMessageData.length < 4) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, ExternalErrorMessage.DATA_LENGTH_NOT_ENOUGH);
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

    public byte[] loadTrustedCert(boolean isCA, byte[] cert) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.LOAD_TRUSTED_CERT_REQUEST);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (isCA) {
            baos.write(0x01);
        } else {
            baos.write(0x00);
        }

        try {
            baos.write(ExternalMessage.intToHexBuf(cert.length));
            baos.write(cert);
        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }

        requestMessage.setMessageData(baos.toByteArray());

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.LOAD_TRUSTED_CERT_RESPONSE, null);

        if (responseMessage.getMessageData() == null || responseMessage.getMessageData().length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        byte[] responseMessageData = responseMessage.getMessageData();
        int responseCode = ExternalMessage.convertResponseCodeBufToInt(new byte[]{responseMessageData[0], responseMessageData[1]});

        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (1 == responseCode) {
                throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_PARAMETER, innerErrorCode);
            }

            if (4 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
            }

            if (45 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_CMD_LENGTH, ExternalErrorMessage.PINPAD_BAD_CMD_LENGTH, innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }

        int pubkeyLen = ExternalMessage.hexBuffer2Int(new byte[]{responseMessageData[2], responseMessageData[3]});

        if (pubkeyLen == 0) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, ExternalErrorMessage.DATA_LENGTH_NOT_ENOUGH);
        }

        byte[] pubkeyBytes = new byte[pubkeyLen];
        System.arraycopy(responseMessageData, 4, pubkeyBytes, 0, pubkeyLen);

        return pubkeyBytes;
    }

    public void resetCertStatus() throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.RESET_CERT_STATUS_REQUEST);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.RESET_CERT_STATUS_RESPONSE, null);

        if (responseMessage.getMessageData() == null || responseMessage.getMessageData().length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        byte[] responseMessageData = responseMessage.getMessageData();
        int responseCode = ExternalMessage.convertResponseCodeBufToInt(new byte[]{responseMessageData[0], responseMessageData[1]});

        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);

            if (4 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
            }

            if (45 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_CMD_LENGTH, ExternalErrorMessage.PINPAD_BAD_CMD_LENGTH, innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }

    }

    public void initAtomic() throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.INIT_ATOMIC_REQUEST);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.INIT_ATOMIC_RESPONSE, null);

        if (responseMessage.getMessageData() == null || responseMessage.getMessageData().length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        byte[] responseMessageData = responseMessage.getMessageData();
        int responseCode = ExternalMessage.convertResponseCodeBufToInt(new byte[]{responseMessageData[0], responseMessageData[1]});

        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);

            if (4 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
            }

            if (45 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_CMD_LENGTH, ExternalErrorMessage.PINPAD_BAD_CMD_LENGTH, innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }

    }

    public void commitAtomic(boolean isSuccessful) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.COMMIT_ATOMIC_REQUEST);

        if (isSuccessful) {
            requestMessage.setMessageData(new byte[]{0x01});
        } else {
            requestMessage.setMessageData(new byte[]{0x00});
        }

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.COMMIT_ATOMIC_RESPONSE, null);

        if (responseMessage.getMessageData() == null || responseMessage.getMessageData().length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        byte[] responseMessageData = responseMessage.getMessageData();
        int responseCode = ExternalMessage.convertResponseCodeBufToInt(new byte[]{responseMessageData[0], responseMessageData[1]});

        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);

            if (4 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
            }

            if (45 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_CMD_LENGTH, ExternalErrorMessage.PINPAD_BAD_CMD_LENGTH, innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }

    }

    public void clearSymmetricKeys() throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.CLEAR_SYMM_KEYS_REQUEST);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.CLEAR_SYMM_KEYS_RESPONSE, null);

        if (responseMessage.getMessageData() == null || responseMessage.getMessageData().length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        byte[] responseMessageData = responseMessage.getMessageData();
        int responseCode = ExternalMessage.convertResponseCodeBufToInt(new byte[]{responseMessageData[0], responseMessageData[1]});

        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);

            if (45 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_CMD_LENGTH, ExternalErrorMessage.PINPAD_BAD_CMD_LENGTH, innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }
    }

    public void generateAsymKey(AsymmetricKey asymmetricKey, AsymAlgInfo asymAlgInfo) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.GENERATE_RSA_KEY_REQUEST);
        try (ByteArrayOutputStream messageStream = new ByteArrayOutputStream()) {
            messageStream.write(asymmetricKey.getKeyID());
            if (asymmetricKey.getKeyType() != AsymKeyType.RSA) {
                throw new NSDKIllegalParameterException("KeyType shall be RSA.");
            }
            messageStream.write(AsymKeyType.RSA.getCode());
            if (asymmetricKey.getKeyUsage() != AsymKeyUsage.KEY_DISTRIBUTION) {
                throw new NSDKIllegalParameterException("KeyUsage shall be KEY_DISTRIBUTION.");
            }
            messageStream.write(AsymKeyUsage.KEY_DISTRIBUTION.getCode());
            messageStream.write(ExternalMessage.intToHexBuf(asymAlgInfo.getUnBit()));
            messageStream.write(asymAlgInfo.getUcRSAPubExp());
            requestMessage.setMessageData(messageStream.toByteArray());
        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.GENERATE_RSA_KEY_RESPONSE, null);
        byte[] responseMessageData =responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new NSDKException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }
        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));
        if (responseCode != ErrorCode.OK) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (1 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.PARAM_ERROR, ExternalErrorMessage.INVALID_PARAMETER, innerErrorCode);
            }
            if (3 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, "Failed to load.", innerErrorCode);
            }
            if (4 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, "Other error.", innerErrorCode);
            }
            if (45 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_COMMAND_LENGTH_ERROR, "Command Length Error.", innerErrorCode);
            }
            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }
    }
}
