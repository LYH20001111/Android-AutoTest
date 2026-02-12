package com.newland.nsdk.core.external.command.communication.mock;

import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.external.command.message.functionId.SignatureFunctionId;
import com.newland.nsdk.core.external.command.message.ExternalMessage;
import com.newland.nsdk.core.external.command.message.ExternalMessageType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MockSignatureResponse {
    public static ExternalMessage response(ExternalMessage requestMessage) throws NSDKException {
        byte[] requestMessageData = requestMessage.getMessageData();
        byte functionId = requestMessageData[0];
        ExternalMessage responseMessage = null;
        switch (functionId) {
            case SignatureFunctionId.HANDSHAKE:
                responseMessage = handshake();
                break;
            case SignatureFunctionId.CHECK_PREVIOUS_SIGNATURE:
                responseMessage = checkPreviousSignature();
                break;
            case SignatureFunctionId.INPUT_SIGNATURE:
                responseMessage = inputSignature();
                break;
            case SignatureFunctionId.REQUEST_FOR_COMPLETING_SIGNATURE:
                responseMessage = completeSignature();
                break;
            case SignatureFunctionId.BATCH_END_RESPONSE:
                responseMessage = batchEnd();
                break;
            case SignatureFunctionId.SEND_FAILURE_SIGNATURE:
            case SignatureFunctionId.BULK_TRANSFER_OF_FAILED_MESSAGES:
            case SignatureFunctionId.BULK_TRANSFER_OF_SUCCESSFUL_MESSAGES:
                responseMessage = sendSignature(SignatureFunctionId.SEND_FAILURE_SIGNATURE);
                break;
            default:
                break;
        }
        return responseMessage;
    }

    private static ExternalMessage sendSignature(byte functionId) {
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.SIGNATURE_RESPONSE);
        String responseCode = "00";
//        String responseCode = "02";
        byte[] data = new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06};
//        byte[] data = new byte[]{0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A,
//                0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2A,
//                0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3A,
//                0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0x4A,
//                0x51, 0x52, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58, 0x59, 0x5A};
        byte count = 1;
        byte[] number = new byte[]{0x11, 0x12, 0x13};
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(functionId);
        try {
            outputStream.write(responseCode.getBytes());
            outputStream.write(count);
            outputStream.write(number);
            outputStream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        responseMessage.setMessageData(outputStream.toByteArray());
        return responseMessage;
    }

    private static ExternalMessage batchEnd() {
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.SIGNATURE_RESPONSE);
        byte functionId = SignatureFunctionId.BATCH_END_RESPONSE;
//        String responseCode = "00";
        String responseCode = "02";

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(functionId);
        try {
            outputStream.write(responseCode.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        responseMessage.setMessageData(outputStream.toByteArray());
        return responseMessage;
    }

    private static ExternalMessage completeSignature() {
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.SIGNATURE_RESPONSE);
        byte functionId = SignatureFunctionId.REQUEST_FOR_COMPLETING_SIGNATURE;
        String responseCode = "00";
//        String responseCode = "02";

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(functionId);
        try {
            outputStream.write(responseCode.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        responseMessage.setMessageData(outputStream.toByteArray());
        return responseMessage;
    }

    private static ExternalMessage inputSignature() {
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.SIGNATURE_RESPONSE);
        byte functionId = SignatureFunctionId.INPUT_SIGNATURE;
        String responseCode = "00";
//        String responseCode = "02";
//        String responseCode = "05";
//        byte[] data = new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06};
        byte[] data = new byte[]{0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A,
                0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2A,
                0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3A,
                0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0x4A,
                0x51, 0x52, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58, 0x59, 0x5A};
        byte[] number = new byte[]{0x11, 0x12, 0x13};
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(functionId);
        try {
            outputStream.write(responseCode.getBytes());
            outputStream.write(number);
            outputStream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        responseMessage.setMessageData(outputStream.toByteArray());
        return responseMessage;
    }

    private static ExternalMessage checkPreviousSignature() {
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.SIGNATURE_RESPONSE);
        byte functionId = SignatureFunctionId.CHECK_PREVIOUS_SIGNATURE;
        String responseCode = "00";
//        String responseCode = "01";
//        String responseCode = "02";
//        byte state = 1;
        byte state = 2;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(functionId);
        try {
            outputStream.write(responseCode.getBytes());
            outputStream.write(state);
        } catch (IOException e) {
            e.printStackTrace();
        }
        responseMessage.setMessageData(outputStream.toByteArray());
        return responseMessage;
    }

    private static ExternalMessage handshake() {
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.SIGNATURE_RESPONSE);
        byte functionId = SignatureFunctionId.HANDSHAKE;
        String responseCode = "00";
//        String responseCode = "01";
//        String responseCode = "02";
        byte state = 1;
//        byte state = 0;
        byte[] softwareVersion = new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06};
//        byte[] serialNumber = new byte[]{0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A,
//                0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2A,
//                0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3A,
//                0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0x4A,
//                0x51, 0x52, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58, 0x59, 0x5A};
        byte[] serialNumber = new byte[]{0x30, 0x38, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A};
        byte flag = 1;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(functionId);
        try {
            outputStream.write(responseCode.getBytes());
            outputStream.write(state);
            outputStream.write(softwareVersion);
            outputStream.write(serialNumber);
            outputStream.write(flag);
        } catch (IOException e) {
            e.printStackTrace();
        }
        responseMessage.setMessageData(outputStream.toByteArray());
        return responseMessage;
    }
}
