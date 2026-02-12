package com.newland.nsdk.core.external.command.magcard;

import android.util.Log;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.card.magcard.MagCardInfo;
import com.newland.nsdk.core.api.common.crypto.CipherType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.exception.NSDKTimeoutException;
import com.newland.nsdk.core.api.common.keymanager.CipherMode;
import com.newland.nsdk.core.api.common.keymanager.KeyType;
import com.newland.nsdk.core.external.command.cardreader.ExternalCardReaderModule;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.api.external.cardreader.ExtCardReaderParameters;
import com.newland.nsdk.core.api.external.exception.NSDKExternalDeviceException;
import com.newland.nsdk.core.external.command.exception.ExternalErrorMessage;
import com.newland.nsdk.core.external.command.exception.ExternalMessageException;
import com.newland.nsdk.core.external.command.message.ExternalMessage;
import com.newland.nsdk.core.external.command.message.ExternalMessageType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Provides the ability to operate magnetic card.
 */
public class ExternalMagCardModule {
    /**
     * Start to search mag card and return mag card information after user swiped card.
     *
     * <p>It will not return until:</p>
     * <ul>
     *     <li>Mag card swiped</li>
     *     <li>Timeout</li>
     *     <li>Cancelled by pressing "Cancel" button or {@link ExternalCardReaderModule#cancelSearch()}</li>
     *     <li>Error happened</li>
     * </ul>
     *
     * @return Mag card information, see {@link MagCardInfo}
     * @throws NSDKException
     */
    public MagCardInfo searchCard(int readModels, int timeout, ExtCardReaderParameters parameter) throws NSDKException {
        if (parameter == null) {
            throw new NSDKIllegalParameterException("External mag card parameter is null.");
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.READ_MAG_CARD_REQUEST);
        requestMessage.setMessageData(packRequestMessageData(readModels, timeout, parameter));

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceive(requestMessage, ExternalMessageType.READ_MAG_CARD_RESPONSE,
                null, timeout * 10);

        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(new byte[]{responseMessageData[0], responseMessageData[1]});
        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (1 == responseCode) {
                throw new NSDKTimeoutException(ExternalErrorMessage.TIMEOUT, innerErrorCode);
            }

            if (2 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_KEY_MODE_ERROR, ExternalErrorMessage.MAG_CARD_KEY_MODE_ERROR, innerErrorCode);
            }

            if (3 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_MAG_READ_ERROR, ExternalErrorMessage.MAG_CARD_READ_ERROR, innerErrorCode);
            }

            if (4 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_MAG_TRACK2_ERROR, ExternalErrorMessage.MAG_CARD_TRACK2_ERROR, innerErrorCode);
            }

            if (5 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_MAG_TRACK_ENCRYPTION_ERROR, ExternalErrorMessage.MAG_CARD_TRACK_ENCRYPT_ERROR, innerErrorCode);
            }

            if (6 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_MAG_TRACK3_ERROR, ExternalErrorMessage.MAG_CARD_TRACK3_ERROR, innerErrorCode);
            }

            if (7 == responseCode) {
                throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_PARAMETER, innerErrorCode);
            }

            if (8 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_KEY_INDEX, ExternalErrorMessage.KEY_INDEX_ERROR, innerErrorCode);
            }

            if (6 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.CANCELLED, ExternalErrorMessage.CANCELLED, innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }

        int magCardResultDataLen = responseMessageData.length - 2;
        byte[] magCardResultData = new byte[magCardResultDataLen];
        System.arraycopy(responseMessageData, 2, magCardResultData, 0, magCardResultDataLen);
        return unpackMagCardResult(magCardResultData, parameter.getPANKeyIndex());
    }

    public static MagCardInfo unpackMagCardResult(byte[] data, byte keyIndex) throws NSDKException {
        if (data == null || data.length == 0) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, "Mag card result data is null or empty.");
        }

        int offset = 0;
        int totalLen = data.length;

        MagCardInfo result = new MagCardInfo();
        int panLen = data[offset];
        result.setPlainPANLen(panLen);
        offset++;

        int panDataLen = 0;
        if (panLen > 0) {
            if (keyIndex != 0) {
                panDataLen = 32;
            } else {
                panDataLen = panLen;
            }
        }


        if (panDataLen > 0) {
            if (!ExternalMessage.isDataEnough(offset, totalLen, panDataLen)) {
                throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, "Mag card result data length is not enough to extract.");
            }

            byte[] pan = new byte[panDataLen];
            System.arraycopy(data, offset, pan, 0, panDataLen);
            offset += panDataLen;
            result.setPanData(pan);
        }

        if (panLen > 0) {
            if (!ExternalMessage.isDataEnough(offset, totalLen, panLen)) {
                throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, "Mag card result data length is not enough to extract.");
            }

            byte[] maskedPan = new byte[panLen];
            System.arraycopy(data, offset, maskedPan, 0, panLen);
            offset += panLen;
            String maskedPAN = new String(maskedPan);
            result.setFirstClearPAN(maskedPAN.substring(0, maskedPAN.indexOf('*')));
            result.setLastClearPAN(maskedPAN.substring(maskedPAN.lastIndexOf('*') + 1));
        }

        byte[] trackStatus = new byte[3];
        for (int i = 0; i < 3; i++) {
            if (offset >= data.length) {
                return result;
            }

            trackStatus[i] = data[offset];
            offset++;
        }

        if (offset <= data.length - 2) {
            int plainTrack1Len = ExternalMessage.hexBuffer2Int(new byte[]{data[offset], data[offset + 1]});
            offset += 2;
            result.setPlainTrack1DataLen(plainTrack1Len);

            int track1Len;
            if (keyIndex != 0 && plainTrack1Len % 16 != 0) {
                track1Len = plainTrack1Len + 16 - plainTrack1Len % 16;
            } else {
                track1Len = plainTrack1Len;
            }
            Log.d("MAGDebug", "track1Len="+track1Len+";data.length="+data.length+";offset="+offset);
            if (track1Len > data.length - offset) {
                throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, ExternalErrorMessage.DATA_LENGTH_NOT_ENOUGH);
            }
            byte[] track1 = new byte[track1Len];
            System.arraycopy(data, offset, track1, 0, track1Len);
            offset += track1Len;
            if (trackStatus[0] == 0) {
                result.setTrack1Data(track1);
            }
        }

        if (offset <= data.length - 2) {
            int plainTrack2Len = ExternalMessage.hexBuffer2Int(new byte[]{data[offset], data[offset + 1]});
            offset += 2;
            result.setPlainTrack2DataLen(plainTrack2Len);

            int track2Len;
            if (keyIndex != 0 && plainTrack2Len % 16 != 0) {
                track2Len = plainTrack2Len + 16 - plainTrack2Len % 16;
            } else {
                track2Len = plainTrack2Len;
            }

            if (track2Len > data.length - offset) {
                throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, ExternalErrorMessage.DATA_LENGTH_NOT_ENOUGH);
            }
            byte[] track2 = new byte[track2Len];
            System.arraycopy(data, offset, track2, 0, track2Len);
            offset += track2Len;
            if (trackStatus[1] == 0) {
                result.setTrack2Data(track2);
            }
        }

        if (offset <= data.length - 2) {
            int plainTrack3Len = ExternalMessage.hexBuffer2Int(new byte[]{data[offset], data[offset + 1]});
            offset += 2;
            result.setPlainTrack3DataLen(plainTrack3Len);

            int track3Len;
            if (keyIndex != 0 && plainTrack3Len % 16 != 0) {
                track3Len = plainTrack3Len + 16 - plainTrack3Len % 16;
            } else {
                track3Len = plainTrack3Len;
            }

            if (track3Len > data.length - offset) {
                throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, ExternalErrorMessage.DATA_LENGTH_NOT_ENOUGH);
            }
            byte[] track3 = new byte[track3Len];
            System.arraycopy(data, offset, track3, 0, track3Len);
            offset += track3Len;
            if (trackStatus[2] == 0) {
                result.setTrack3Data(track3);
            }
        }

        return result;
    }

    private byte[] packRequestMessageData(int readModels, int timeout, ExtCardReaderParameters parameter) throws NSDKIllegalParameterException, ExternalMessageException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(parameter.getPANKeyIndex());
        KeyType keyType = CipherType.getKeyType(parameter.getCipherType());

        if (keyType != KeyType.AES && keyType != KeyType.DES) {
            if (keyType == null) {
                keyType = KeyType.DES;
            } else {
                throw new NSDKIllegalParameterException(String.format("Invalid key type(%s), only support DES and AES now.", keyType));
            }
        }

        outputStream.write(keyType.getCode());

        CipherMode cipherMode = CipherType.getCipherMode(parameter.getCipherType());
        if (cipherMode != CipherMode.ECB && cipherMode != CipherMode.CBC) {
            if (cipherMode == null) {
                cipherMode = CipherMode.ECB;
            } else {
                throw new NSDKIllegalParameterException(String.format("Invalid key mode(%s), only support CBC and ECB now.", cipherMode));
            }
        }
        byte keyMode = 1;
        if (cipherMode == CipherMode.CBC) {
            if (parameter.getIV() == null || parameter.getIV().length == 0) {
                throw new NSDKIllegalParameterException(ExternalErrorMessage.EMPTY_IV);
            }
            keyMode = 0;
        }
        outputStream.write(keyMode);
        boolean isReadTrack1 = false;
        boolean isReadTrack2 = false;
        boolean isReadTrack3 = false;
        try {
            outputStream.write(ExternalMessage.intToHexBuf(timeout));
            outputStream.write(isReadTrack1 ? 1 : 0);
            outputStream.write(isReadTrack2 ? 1 : 0);
            outputStream.write(isReadTrack3 ? 1 : 0);
            if (keyMode == 0) {
                outputStream.write(parameter.getIV());
            }

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
}
