package com.newland.nsdk.core.external.command.scanner;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKTimeoutException;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.external.command.message.functionId.ScannerFunctionId;
import com.newland.nsdk.core.api.external.exception.NSDKExternalDeviceException;
import com.newland.nsdk.core.external.command.exception.ExternalErrorMessage;
import com.newland.nsdk.core.external.command.exception.ExternalMessageException;
import com.newland.nsdk.core.external.command.message.ExternalMessage;
import com.newland.nsdk.core.external.command.message.ExternalMessageType;

/**
 * Provides the ability to scan.
 */
public class ExternalScannerModule {
    /**
     * Start scanning and return scanned data if success.
     *
     * @param timeout Timeout for scanning. Unit: seconds
     * @return Scanning result.
     * @throws NSDKException
     */
    public byte[] scan(int timeout) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.SCANNING_REQUEST);
        // Request message data = Function ID(1 byte) + Timeout(2 bytes)
        byte functionId = ScannerFunctionId.SCANNING;
        byte[] requestMessageData = new byte[3];
        requestMessageData[0] = functionId;
        System.arraycopy(ExternalMessage.intToHexBuf(timeout), 0, requestMessageData, 1, 2);
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceive(requestMessage, ExternalMessageType.SCANNING_RESPONSE,
                functionId, timeout * 1000);

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

        byte[] data = new byte[dataLen];
        System.arraycopy(responseMessageData, 5, data, 0, dataLen);
        return data;
    }

    /**
     * Stop scanning.
     *
     * @param isScanning
     * @throws NSDKException
     */
    public void stopScan(boolean isScanning) throws NSDKException {
        if (!isScanning) {
            return;
        }
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.CANCEL_PIN_ENTRY_REQUEST);

        ExternalCommunicationManager.getInstance().sendInterrupt(requestMessage.pack());
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
                throw new NSDKExternalDeviceException(ErrorCode.EXT_DEVICE_OPEN_ERROR, "Failed to open device.", innerErrorCode);
            }

            if (2 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_SCANNER_SCANNING_HEAD_NOT_SUPPORTED, "Scanning head not supported.", innerErrorCode);
            }

            if (3 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_SCANNER_SCANNING_STOPPED, "Scanning is stopped.", innerErrorCode);
            }

            if (4 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_SCANNER_STOP_SCANNING_ERROR, "Failed to stop scanning.", innerErrorCode);
            }

            if (6 == responseCode) {
                throw new NSDKTimeoutException(ExternalErrorMessage.TIMEOUT, innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }
    }
}
