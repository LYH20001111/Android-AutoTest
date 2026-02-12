package com.newland.nsdk.core.external.command.cardemulator;

import android.util.Log;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.external.cardemulator.EmulateCardType;
import com.newland.nsdk.core.api.external.cardemulator.EmulateConfig;
import com.newland.nsdk.core.api.external.exception.NSDKExternalDeviceException;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.external.command.exception.ExternalErrorMessage;
import com.newland.nsdk.core.external.command.message.ExternalMessage;
import com.newland.nsdk.core.external.command.message.ExternalMessageType;
import com.newland.nsdk.core.external.command.message.functionId.CardEmulatorFunctionId;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;

public class ExtCardEmulatorModule {
    private static final String TAG = "ExtCardEmulatorModule";

    public void init() throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.CARD_EMULATOR_REQUEST);
        byte[] requestData = new byte[1];
        requestData[0] = CardEmulatorFunctionId.CARD_EMULATOR_INIT;
        requestMessage.setMessageData(requestData);
        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.CARD_EMULATOR_RESPONSE, CardEmulatorFunctionId.CARD_EMULATOR_INIT);
        checkResponseCode(responseMessage);
    }

    public void start(int cardType) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.CARD_EMULATOR_REQUEST);
        byte[] requestData = new byte[2];
        requestData[0] = CardEmulatorFunctionId.CARD_EMULATOR_START;
        requestData[1] = (byte) cardType;
        requestMessage.setMessageData(requestData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.CARD_EMULATOR_RESPONSE, CardEmulatorFunctionId.CARD_EMULATOR_START);
        checkResponseCode(responseMessage);
    }

    public int getStatus(int cardType) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.CARD_EMULATOR_REQUEST);
        byte[] requestData = new byte[2];
        requestData[0] = CardEmulatorFunctionId.CARD_EMULATOR_GET_STATUS;
        requestData[1] = (byte) cardType;
        requestMessage.setMessageData(requestData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.CARD_EMULATOR_RESPONSE, CardEmulatorFunctionId.CARD_EMULATOR_GET_STATUS);
        checkResponseCode(responseMessage);
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData.length < 4) {
            throw new NSDKExternalDeviceException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }
        return responseMessageData[3];
    }

    public void writeConfig(int cardType, EmulateConfig config) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.CARD_EMULATOR_REQUEST);
        byte[] requestData = new byte[8];
        requestData[0] = CardEmulatorFunctionId.CARD_EMULATOR_WRITE_CONFIG;
        requestData[1] = (byte) cardType;
        byte[] uid = config.getUid();
        if (uid == null || uid.length != 4) {
           throw new NSDKIllegalParameterException("UID shall be 4 bytes.");
        }
        int memorySize = config.getMemorySize();
        if (cardType == 0) {
            if (memorySize % 8 != 0 || memorySize < 48 || memorySize > 992) {
                throw new NSDKIllegalParameterException("Memory size shall be multiply of 8, and it's value range is (48, 992).");
            }

        } else if (cardType == 1) {
            if (memorySize < 0 || memorySize > 2048) {
                throw new NSDKExternalDeviceException("Memory size shall range from 1 to 2047.");
            }
        }
        System.arraycopy(uid, 0, requestData, 2, 4);
        System.arraycopy(ExternalMessage.intToHexBuf(config.getMemorySize()), 0, requestData, 6, 2);
        requestMessage.setMessageData(requestData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.CARD_EMULATOR_RESPONSE, CardEmulatorFunctionId.CARD_EMULATOR_WRITE_CONFIG);
        checkResponseCode(responseMessage);
    }

    public void writeData(int fileType, byte[] data) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.CARD_EMULATOR_REQUEST);
        byte functionId = CardEmulatorFunctionId.CARD_EMULATOR_WRITE_DATA;
        try (ByteArrayOutputStream messageStream = new ByteArrayOutputStream()) {
            messageStream.write(functionId);
            messageStream.write(fileType);
            messageStream.write(ExternalMessage.intToHexBuf(data.length));
            messageStream.write(data);
            requestMessage.setMessageData(messageStream.toByteArray());
        } catch (IOException e) {
            throw new NSDKExternalDeviceException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.CARD_EMULATOR_RESPONSE, functionId);
        checkResponseCode(responseMessage);
    }

    public byte[] readData(int fileType, int readLength) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.CARD_EMULATOR_REQUEST);
        byte[] requestData = new byte[4];
        byte functionId = CardEmulatorFunctionId.CARD_EMULATOR_READ_DATA;
        requestData[0] = functionId;
        requestData[1] = (byte) fileType;
        System.arraycopy(ExternalMessage.intToHexBuf(readLength), 0, requestData, 2, 2);
        requestMessage.setMessageData(requestData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.CARD_EMULATOR_RESPONSE, functionId);
        checkResponseCode(responseMessage);
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData.length < 5) {
            throw new NSDKExternalDeviceException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        byte[] leftResponseData = new byte[responseMessageData.length - 3];
        System.arraycopy(responseMessageData, 3, leftResponseData, 0, leftResponseData.length);
        int length = ExternalMessage.hexBuffer2Int(Arrays.copyOf(leftResponseData, 2));
        Log.d(TAG, "length:" + length + ", leftResponse:" + leftResponseData.length);
        if (leftResponseData.length - 2 != length) {
            throw new NSDKExternalDeviceException(ErrorCode.EXT_COMMAND_LENGTH_ERROR, "Receive data error");
        }
        byte[] data = new byte[length];
        System.arraycopy(responseMessageData, 5, data, 0, data.length);
        return data;
    }

    public EmulateConfig getConfig(int cardType) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.CARD_EMULATOR_REQUEST);
        byte[] requestData = new byte[2];
        byte functionId = CardEmulatorFunctionId.CARD_EMULATOR_READ_CONFIG;
        requestData[0] = functionId;
        requestData[1] = (byte) cardType;
        requestMessage.setMessageData(requestData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.CARD_EMULATOR_RESPONSE, functionId);
        checkResponseCode(responseMessage);
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData.length < 8) {
            throw new NSDKExternalDeviceException(ErrorCode.EXT_COMMAND_LENGTH_ERROR, "Receive data error.");
        }
        EmulateConfig config = new EmulateConfig();
        byte sak = responseMessageData[3];
        config.setSak(sak);
        byte[] uid = new byte[4];
        System.arraycopy(responseMessageData, 4, uid, 0, 4);
        config.setUid(uid);
        int atsLen = responseMessageData[8] & 0xFF;
        if (atsLen > 0) {
            byte[] ats = new byte[atsLen];
            System.arraycopy(responseMessageData, 9, ats, 0, ats.length);
            config.setAts(ats);
        } else {
            config.setAts(null);
        }
        byte[] ndefSize = new byte[2];
        System.arraycopy(responseMessageData, 9 + atsLen, ndefSize, 0, 2);
        int memorySize = ExternalMessage.hexBuffer2Int(ndefSize);
        config.setMemorySize(memorySize);
        return config;
    }

    public void finish() throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.CARD_EMULATOR_REQUEST);
        byte functionId = CardEmulatorFunctionId.CARD_EMULATOR_END;
        requestMessage.setMessageData(new byte[] {functionId});

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.CARD_EMULATOR_RESPONSE, functionId);
        checkResponseCode(responseMessage);
    }

    public byte[] getEvent(int type) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.CARD_EMULATOR_REQUEST);
        byte[] requestMessageData = new byte[2];
        byte functionId = CardEmulatorFunctionId.CARD_EMULATOR_GET_EVENT;
        requestMessageData[0] = functionId;
        requestMessageData[1] = (byte) type;
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.CARD_EMULATOR_RESPONSE, functionId);
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new NSDKExternalDeviceException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }
        checkResponseCode(responseMessage);
        if (responseMessageData.length < 7) {
            throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.DATA_LENGTH_NOT_ENOUGH);
        }
        byte[] event = new byte[4];
        System.arraycopy(responseMessageData, 3, event, 0, 4);
        return event;
    }

    private void checkResponseCode(ExternalMessage responseMessage) throws NSDKException {
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 3) {
            throw new NSDKExternalDeviceException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }
        byte[] responseCodeBuf = new byte[2];
        System.arraycopy(responseMessageData, 1, responseCodeBuf, 0, 2);
        int responseCode = ExternalMessage.convertResponseCodeBufToInt(responseCodeBuf);
        if (responseCode != ErrorCode.OK) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 3);
            if (4 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
            }
            if (45 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_COMMAND_LENGTH_ERROR, "Bad command length.", innerErrorCode);
            }
            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }
    }
}
