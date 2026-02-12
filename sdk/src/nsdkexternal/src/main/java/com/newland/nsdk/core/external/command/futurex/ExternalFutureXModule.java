package com.newland.nsdk.core.external.command.futurex;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.keymanager.InstalledKeyInfo;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.external.exception.NSDKExternalDeviceException;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.external.command.exception.ExternalErrorMessage;
import com.newland.nsdk.core.external.command.exception.ExternalMessageException;
import com.newland.nsdk.core.external.command.message.ExternalMessage;
import com.newland.nsdk.core.external.command.message.ExternalMessageType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExternalFutureXModule {
    private static final String TAG = "ExternalFutureXModule";
    public byte[] get(FutureXCommandType commandType) throws NSDKException {
        if (commandType == null) {
            throw new NSDKIllegalParameterException("Command type shall not be null.");
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.FUTUREX_GET_REQUEST);
        requestMessage.setMessageData(new byte[]{commandType.getCode()});

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.FUTUREX_GET_RESPONSE, null);

        responseMessage.checkMessageType(ExternalMessageType.FUTUREX_GET_RESPONSE);

        byte[] responseMessageData = responseMessage.getMessageData();

        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        int offset = 0;
        byte[] responseCodeBuf = new byte[2];
        System.arraycopy(responseMessageData, offset, responseCodeBuf, 0, 2);
        offset += 2;
        int responseCode = ExternalMessage.convertResponseCodeBufToInt(responseCodeBuf);

        if (responseCode != ErrorCode.OK) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);

            if (4 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
            }

            if (45 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_COMMAND_LENGTH_ERROR, "Command length error.", innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }

        byte[] lenBuf = new byte[2];
        System.arraycopy(responseMessageData, offset, lenBuf, 0, lenBuf.length);
        offset += 2;
        int dataLenExpected = ExternalMessage.hexBuffer2Int(lenBuf);
        int dataLenActual = responseMessageData.length - offset;
        if (dataLenActual < dataLenExpected) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, ExternalErrorMessage.DATA_LENGTH_NOT_ENOUGH);
        }
        byte[] data = new byte[dataLenExpected];
        System.arraycopy(responseMessageData, offset, data, 0, data.length);

        return data;
    }

    public void set(FutureXCommandType commandType, byte[] commandData) throws NSDKException {
        if (commandType == null) {
            throw new NSDKIllegalParameterException("Command type shall not be null.");
        }

        if (commandData == null) {
            throw new NSDKIllegalParameterException("Command data shall not be null.");
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.FUTUREX_SET_REQUEST);

        ByteArrayOutputStream messageStream = new ByteArrayOutputStream();
        try {
            messageStream.write(commandType.getCode());
            messageStream.write(ExternalMessage.intToHexBuf(commandData.length));
            if (commandData.length > 0) {
                messageStream.write(commandData);
            }
        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }

        requestMessage.setMessageData(messageStream.toByteArray());

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.FUTUREX_SET_RESPONSE, null);

        responseMessage.checkMessageType(ExternalMessageType.FUTUREX_SET_RESPONSE);

        byte[] responseMessageData = responseMessage.getMessageData();

        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        byte[] responseCodeBuf = new byte[2];
        System.arraycopy(responseMessageData, 0, responseCodeBuf, 0, 2);
        int responseCode = ExternalMessage.convertResponseCodeBufToInt(responseCodeBuf);

        if (responseCode != ErrorCode.OK) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);

            if (1 == responseCode) {
                throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_PARAMETER, innerErrorCode);
            }

            if (4 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
            }

            if (45 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_COMMAND_LENGTH_ERROR, "Command length error.", innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }
    }

    public int getInstalledKeyNum() throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.GET_INSTALLED_KEY_NUM_REQUEST);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.GET_INSTALLED_KEY_NUM_RESPONSE, null);

        responseMessage.checkMessageType(ExternalMessageType.GET_INSTALLED_KEY_NUM_RESPONSE);

        byte[] responseMessageData = responseMessage.getMessageData();

        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        byte[] responseCodeBuf = new byte[2];
        System.arraycopy(responseMessageData, 0, responseCodeBuf, 0, 2);
        int responseCode = ExternalMessage.convertResponseCodeBufToInt(responseCodeBuf);

        if (responseCode != ErrorCode.OK) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);

            if (1 == responseCode) {
                throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_PARAMETER, innerErrorCode);
            }

            if (4 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
            }

            if (45 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_COMMAND_LENGTH_ERROR, "Command length error.", innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }

        return responseMessageData[2] & 0xFF;
    }

    public List<InstalledKeyInfo> getInstalledKeyInfo() throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.GET_INSTALLED_KEY_INFO_REQUEST);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.GET_INSTALLED_KEY_INFO_RESPONSE, null);

        responseMessage.checkMessageType(ExternalMessageType.GET_INSTALLED_KEY_INFO_RESPONSE);

        byte[] responseMessageData = responseMessage.getMessageData();

        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        byte[] responseCodeBuf = new byte[2];
        System.arraycopy(responseMessageData, 0, responseCodeBuf, 0, 2);
        int responseCode = ExternalMessage.convertResponseCodeBufToInt(responseCodeBuf);

        if (responseCode != ErrorCode.OK) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);

            if (2 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_INSTALLED_KEY_NUM_ERROR, "Failed to get installed key number.");
            }

            if (4 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
            }

            if (45 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_COMMAND_LENGTH_ERROR, "Command length error.", innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }

        List<InstalledKeyInfo> result = new ArrayList<>();
        byte[] keyInfoData = new byte[responseMessageData.length - 2];
        System.arraycopy(responseMessageData, 2, keyInfoData, 0, keyInfoData.length);
        try {
            LogUtils.d(TAG, "******** Key info data: " + ISOUtils.hexString(keyInfoData));
            int offset = 0;
            while (offset < keyInfoData.length) {
                int keyNumber = keyInfoData[offset++] & 0xFF;
                if (keyNumber == 0) {
                    break;
                }
                InstalledKeyInfo temp = new InstalledKeyInfo();
                temp.setIndex(keyInfoData[offset]);
                offset ++;
                temp.setType(keyInfoData[offset]);
                offset ++;
                temp.setUsage(keyInfoData[offset]);
                offset ++;
                int kcvLen = keyInfoData[offset] & 0xFF;
                offset ++;
                if (kcvLen > 0) {
                    byte[] kcv = new byte[kcvLen];
                    System.arraycopy(keyInfoData, offset, kcv, 0, kcvLen);
                    temp.setKCV(kcv);
                    offset += kcvLen;
                }
                result.add(temp);
            }
        } catch (Exception e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_ERROR, "Failed to extract message", e);
        }

        return result;
    }

    /**
     * @param infoType 0x01: sign cert index; 0x02: device group
     * @param data
     */
    public void setDeviceInfo(byte infoType, byte[] data) throws NSDKException {
        if (data == null || data.length == 0) {
            throw new NSDKIllegalParameterException("Data shall not be null or empty.");
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.SET_RKI_DEVICE_INFO_REQUEST);

        ByteArrayOutputStream messageStream = new ByteArrayOutputStream();
        try {
            messageStream.write(infoType);
            messageStream.write(data.length);
            messageStream.write(data);
        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }

        requestMessage.setMessageData(messageStream.toByteArray());

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.SET_RKI_DEVICE_INFO_RESPONSE, null);

        responseMessage.checkMessageType(ExternalMessageType.SET_RKI_DEVICE_INFO_RESPONSE);

        byte[] responseMessageData = responseMessage.getMessageData();

        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        byte[] responseCodeBuf = new byte[2];
        System.arraycopy(responseMessageData, 0, responseCodeBuf, 0, 2);
        int responseCode = ExternalMessage.convertResponseCodeBufToInt(responseCodeBuf);

        if (responseCode != ErrorCode.OK) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);

            if (1 == responseCode) {
                throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_PARAMETER, innerErrorCode);
            }

            if (4 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
            }

            if (45 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_COMMAND_LENGTH_ERROR, "Command length error.", innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }
    }
}
