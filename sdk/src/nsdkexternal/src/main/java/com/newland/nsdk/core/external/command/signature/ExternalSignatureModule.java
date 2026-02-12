package com.newland.nsdk.core.external.command.signature;


import android.text.LoginFilter;
import android.text.TextUtils;
import android.util.Log;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.external.exception.NSDKExternalDeviceException;
import com.newland.nsdk.core.api.external.signature.ExtESignatureListener;
import com.newland.nsdk.core.api.external.signature.ExtESignatureParameters;
import com.newland.nsdk.core.api.external.signature.ImageFormat;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.external.command.exception.ExternalErrorMessage;
import com.newland.nsdk.core.external.command.message.ExternalMessage;
import com.newland.nsdk.core.external.command.message.ExternalMessageType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Provides the ability of electronic signature.
 */
public class ExternalSignatureModule {
    private static final String TAG = "ExternalSignatureModule";
    public void startSignature(ExtESignatureParameters parameters, int timeout, ExtESignatureListener listener) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.NEW_SIGNATURE_REQUEST);
        try (ByteArrayOutputStream messageStream = new ByteArrayOutputStream()){
            if (parameters.isSupportByPass()) {
                messageStream.write(0x00);
            } else {
                messageStream.write(0x01);
            }
            if (parameters.isSupportDisplayMessage()) {
                messageStream.write(0x00);
            } else {
                messageStream.write(0x01);
            }
            if (parameters.isShowButtons()) {
                messageStream.write(0x00);
            } else {
                messageStream.write(0x01);
            }
            if (parameters.getImageFormat() == ImageFormat.DEFAULT) {
                messageStream.write(0x00);
            } else {
                messageStream.write(0x01);
            }
            int areaWidth = parameters.getAreaWidth();
            if (areaWidth <= 0) {
                listener.onError(ErrorCode.PARAM_ERROR, "Area width shall be >0.");
                return;
            }
            messageStream.write(ExternalMessage.intToHexBuf(areaWidth));
            int areaHeight = parameters.getAreaHeight();
            if (areaHeight <= 0) {
                listener.onError(ErrorCode.PARAM_ERROR, "Area height shall be >0.");
                return;
            }
            messageStream.write(ExternalMessage.intToHexBuf(areaHeight));
            int retryTime = parameters.getRetryTime();
            if (retryTime < 0) {
                listener.onError(ErrorCode.PARAM_ERROR, "Retry time shall be >=0.");
                return;
            }
            messageStream.write(retryTime);
            if (timeout < 0) {
                listener.onError(ErrorCode.PARAM_ERROR, "Timeout shall be >=0.");
                return;
            }
            messageStream.write(ExternalMessage.intToHexBuf(timeout));
            if (!TextUtils.isEmpty(parameters.getDisplayMessage())) {
                byte[] displayMessageData = parameters.getDisplayMessage().getBytes();
                messageStream.write(ExternalMessage.intToHexBuf(displayMessageData.length));
                messageStream.write(displayMessageData);
            } else {
                messageStream.write(ExternalMessage.intToHexBuf(0));
            }
            requestMessage.setMessageData(messageStream.toByteArray());
        } catch (IOException e) {
            listener.onError(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR);
            return;
        }

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceive(requestMessage, ExternalMessageType.NEW_SIGNATURE_RESPONSE, null, timeout * 1000);
        ByteArrayOutputStream totalImageDataStream = new ByteArrayOutputStream();
        byte[] responseData = responseMessage.getMessageData();
        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseData, 2));
        if (responseCode != ErrorCode.OK) {
            if (1 == responseCode) {
                listener.onError(ErrorCode.PARAM_ERROR, ExternalErrorMessage.INVALID_PARAMETER);
                return;
            }
            if (2 == responseCode) {
                listener.onError(ErrorCode.PARAM_ERROR, "Area out of screen");
                return;
            }
            if (3 == responseCode) {
                listener.onError(ErrorCode.EXT_UNSUPPORTED, "The device does not support signature.");
                return;
            }
            if (4 == responseCode) {
                listener.onError(ErrorCode.EXT_ERROR, "Interrupted.");
                return;
            }
            if (5 == responseCode) {
                listener.onError(ErrorCode.EXT_ERROR, "Conversion failed.");
                return;
            }
            if (6 == responseCode) {
                listener.onError(ErrorCode.EXT_ERROR, "Sign failed.");
                return;
            }
            if (7 == responseCode) {
                listener.onError(ErrorCode.EXT_ERROR, "By Pass");
                return;
            }
            if (8 == responseCode) {
                listener.onError(ErrorCode.EXT_ERROR, "Complete failed.");
                return;
            }
            if (9 == responseCode) {
                listener.onError(ErrorCode.EXT_COMMUNICATION_RECEIVE_DATA_TIMEOUT, ExternalErrorMessage.TIMEOUT);
                return;
            }
            if (10 == responseCode) {
                listener.onCancel();
                return;
            }
            if (45 == responseCode) {
                listener.onError(ErrorCode.EXT_COMMAND_LENGTH_ERROR, "Command length error.");
                return;
            }
            if (55 == responseCode) {
                listener.onError(ErrorCode.EXT_UNSUPPORTED, ExternalErrorMessage.NOT_SUPPORTED);
                return;
            }
            listener.onError(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR);
            return;
        }
        if (responseData == null || responseData.length < 11) {
            listener.onError(ErrorCode.EXT_ERROR, ExternalErrorMessage.NO_RESPONSE_CODE);
            return;
        }
        LogUtils.d(TAG, "responseData:" + ISOUtils.hexString(responseData));
        byte[] sequencePackageNumber = new byte[2];
        System.arraycopy(responseData, 4, sequencePackageNumber, 0, 2);
        int totalPackageNumber = ExternalMessage.hexBuffer2Int(sequencePackageNumber);
        LogUtils.d(TAG, "totalPackageNumber:" + totalPackageNumber);
        byte[] currentPackageNumber = new byte[2];
        System.arraycopy(responseData, 2, currentPackageNumber, 0, 2);
        int currentPackageNo = ExternalMessage.hexBuffer2Int(currentPackageNumber);
        byte[] dataLen = new byte[2];
        System.arraycopy(responseData, 6, dataLen, 0, 2);
        int packageDataLen = ExternalMessage.hexBuffer2Int(dataLen);
        LogUtils.d(TAG, "packageLen:" + packageDataLen);
        byte[] data = new byte[packageDataLen];
        if (responseData.length - 8 < packageDataLen) {
            listener.onError(ErrorCode.EXT_COMMAND_LENGTH_ERROR, "Receive package data didn't match its length");
        }
        System.arraycopy(responseData, 8, data, 0, packageDataLen);
        try {
            totalImageDataStream.write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        while (totalPackageNumber > 0) {
            ExternalMessage requestSequenceMessage = new ExternalMessage();
            requestSequenceMessage.setMessageType(ExternalMessageType.NEW_SIGNATURE_REQUEST);
            try (ByteArrayOutputStream messageStream = new ByteArrayOutputStream()){
                messageStream.write(ExternalMessage.intToHexBuf(0));
                messageStream.write(ExternalMessage.intToHexBuf(currentPackageNo));
                requestSequenceMessage.setMessageData(messageStream.toByteArray());
            } catch (IOException e) {
                listener.onError(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR);
                break;
            }
            ExternalMessage responseSequenceMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestSequenceMessage, ExternalMessageType.NEW_SIGNATURE_RESPONSE, null);
            byte[] responseSequenceData = responseSequenceMessage.getMessageData();
            if (responseSequenceData == null || responseSequenceData.length < 2) {
                listener.onError(ErrorCode.EXT_ERROR, ExternalErrorMessage.NO_RESPONSE_CODE);
                break;
            }
            responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseSequenceData, 2));
            if (responseCode != ErrorCode.OK) {
                if (1 == responseCode) {
                    listener.onError(ErrorCode.PARAM_ERROR, ExternalErrorMessage.INVALID_PARAMETER);
                    return;
                }
                if (2 == responseCode) {
                    listener.onError(ErrorCode.PARAM_ERROR, "Area out of screen");
                    return;
                }
                if (3 == responseCode) {
                    listener.onError(ErrorCode.EXT_UNSUPPORTED, "The device does not support signature.");
                    return;
                }
                if (4 == responseCode) {
                    listener.onError(ErrorCode.EXT_ERROR, "Interrupted.");
                    return;
                }
                if (5 == responseCode) {
                    listener.onError(ErrorCode.EXT_ERROR, "Conversion failed.");
                    return;
                }
                if (6 == responseCode) {
                    listener.onError(ErrorCode.EXT_ERROR, "Sign failed.");
                    return;
                }
                if (7 == responseCode) {
                    listener.onError(ErrorCode.EXT_ERROR, "By Pass");
                    return;
                }
                if (8 == responseCode) {
                    listener.onError(ErrorCode.EXT_ERROR, "Complete failed.");
                    return;
                }
                if (9 == responseCode) {
                    listener.onError(ErrorCode.EXT_COMMUNICATION_RECEIVE_DATA_TIMEOUT, ExternalErrorMessage.TIMEOUT);
                    return;
                }
                if (10 == responseCode) {
                    listener.onCancel();
                    return;
                }
                if (45 == responseCode) {
                    listener.onError(ErrorCode.EXT_COMMAND_LENGTH_ERROR, "Command length error.");
                    return;
                }
                if (55 == responseCode) {
                    listener.onError(ErrorCode.EXT_UNSUPPORTED, ExternalErrorMessage.NOT_SUPPORTED);
                    return;
                }
                listener.onError(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR);
                return;
            }
            if (responseData == null || responseData.length < 11) {
                listener.onError(ErrorCode.EXT_ERROR, ExternalErrorMessage.NO_RESPONSE_CODE);
                return;
            }
            byte[] sequenceDataLen = new byte[2];
            System.arraycopy(responseSequenceData, 6, sequenceDataLen, 0, 2);
            int len = ExternalMessage.hexBuffer2Int(sequenceDataLen);
            LogUtils.d(TAG, "sequenceDataLen:" + len);
            byte[] leftDataPackageNum = new byte[2];
            System.arraycopy(responseSequenceData, 4, leftDataPackageNum, 0, 2);
            totalPackageNumber = ExternalMessage.hexBuffer2Int(leftDataPackageNum);
            byte[] sequenceData = new byte[len];
            System.arraycopy(responseSequenceData, 8, sequenceData, 0, len);
            try {
                totalImageDataStream.write(sequenceData);
            } catch (IOException e) {
                listener.onError(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR);
                break;
            }
            currentPackageNo++;
            Log.d(TAG, "totalPackageNumber: " + totalPackageNumber);
            if (totalPackageNumber == 0) {
                ExternalMessage finalMessage = new ExternalMessage();
                finalMessage.setMessageType(ExternalMessageType.NEW_SIGNATURE_REQUEST);
                byte[] finalMessageData = new byte[4];
                System.arraycopy(ExternalMessage.intToHexBuf(0), 0, finalMessageData, 0, 2);
                System.arraycopy(ExternalMessage.intToHexBuf(currentPackageNo), 0, finalMessageData, 2, 2);
                finalMessage.setMessageData(finalMessageData);
                ExternalMessage finalResponseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveACK(finalMessage);
                Log.d(TAG, "responseCode:" + finalResponseMessage.getResponseCode());
                if (finalResponseMessage.getResponseCode() != ErrorCode.OK) {
                    listener.onError(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR);
                    return;
                }
            }
        }
        if (totalPackageNumber == 0) {
            listener.onComplete(totalImageDataStream.toByteArray());
        }
    }

}