package com.newland.nsdk.core.external.command.ecdhe;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.keymanager.ECCType;
import com.newland.nsdk.core.api.common.keymanager.KDFInfo;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.external.ecdhe.ExtECDHE;
import com.newland.nsdk.core.api.external.exception.NSDKExternalDeviceException;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.external.command.exception.ExternalErrorMessage;
import com.newland.nsdk.core.external.command.exception.ExternalMessageException;
import com.newland.nsdk.core.external.command.message.ExternalMessage;
import com.newland.nsdk.core.external.command.message.ExternalMessageType;
import com.newland.nsdk.core.external.command.message.functionId.ScannerFunctionId;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class ExternalECDHEModule {
    private static final String TAG = "ExternalECDHEModule";
    private byte[] handle = null;
    public void init() throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.ECDHE_INIT_REQUEST);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.ECDHE_INIT_RESPONSE, null);

        // Response message data = Response code(2 bytes) + Handle(2 bytes)
        byte[] responseMessageData = responseMessage.getMessageData();

        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));
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

        this.handle = new byte[4];
        System.arraycopy(responseMessageData, 2, this.handle, 0, 4);
        LogUtils.d(TAG, "ECDHE init handle: " + ISOUtils.hexString(this.handle));
    }

    public void release() throws NSDKException {
        if (this.handle == null) {
            return;
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.ECDHE_RELEASE_REQUEST);

        byte[] requestMessageData = this.handle;
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.ECDHE_RELEASE_RESPONSE, null);

        // Response data = Response Code
        byte[] responseMessageData = responseMessage.getMessageData();

        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));
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
        this.handle = null;
    }

    public byte[] generateKeyPair(ECCType curveType) throws NSDKException {
        if (curveType == null) {
            throw new NSDKIllegalParameterException("Curve type shall not be null.");
        }

        if (this.handle == null) {
            throw new NSDKException("Please init ECDHE first.");
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.ECDHE_GEN_KEY_PAIR_REQUEST);

        byte[] requestMessageData = new byte[5];
        System.arraycopy(this.handle, 0, requestMessageData, 0, 4);

        requestMessageData[4] = (byte) curveType.ordinal();

        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.ECDHE_GEN_KEY_PAIR_RESPONSE, null);

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

    public void generateSessionKey(SymmetricKey sessionKey, KDFInfo kdfInfo, byte[] publicKey) throws NSDKException {
        if (sessionKey == null || kdfInfo == null || publicKey == null) {
            throw new NSDKIllegalParameterException("Session key, HKDF info and public key shall not be null.");
        }

        if (sessionKey.getKeyType() == null || sessionKey.getKeyUsage() == null) {
            throw new NSDKIllegalParameterException("Session key type and usage shall not be null.");
        }

        if (kdfInfo.getKDFType() == null || kdfInfo.getMessageDigestType() == null) {
            throw new NSDKIllegalParameterException("HKDF type and message digest type shall not be null.");
        }

        if (this.handle == null) {
            throw new NSDKException("Please init ECDHE first.");
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.ECDHE_GEN_SK_REQUEST);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            outputStream.write(this.handle);
            outputStream.write(sessionKey.getKeyID());
            outputStream.write(sessionKey.getKeyType().getCode());
            outputStream.write(sessionKey.getKeyUsage().getCode());
            outputStream.write(ExternalMessage.intToHexBuf(sessionKey.getKeyLen()));
            outputStream.write(kdfInfo.getKDFType().ordinal());
            outputStream.write(kdfInfo.getMessageDigestType().ordinal());
            byte[] zeroLen = {0x00, 0x00};
            if (kdfInfo.getSalt() == null || kdfInfo.getSalt().length == 0) {
                outputStream.write(zeroLen);
            } else {
                outputStream.write(ExternalMessage.intToHexBuf(kdfInfo.getSalt().length));
                outputStream.write(kdfInfo.getSalt());
            }

            if (kdfInfo.getInfo() == null || kdfInfo.getInfo().length == 0) {
                outputStream.write(zeroLen);
            } else {
                outputStream.write(ExternalMessage.intToHexBuf(kdfInfo.getInfo().length));
                outputStream.write(kdfInfo.getInfo());
            }

            outputStream.write(ExternalMessage.intToHexBuf(publicKey.length));
            outputStream.write(publicKey);

        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }
        requestMessage.setMessageData(outputStream.toByteArray());

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.ECDHE_GEN_SK_RESPONSE, null);

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
