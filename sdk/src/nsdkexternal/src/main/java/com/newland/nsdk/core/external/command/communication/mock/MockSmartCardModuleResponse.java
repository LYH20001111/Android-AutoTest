package com.newland.nsdk.core.external.command.communication.mock;

import com.newland.nsdk.core.external.command.message.functionId.IcCardFunctionId;
import com.newland.nsdk.core.external.command.message.ExternalMessage;
import com.newland.nsdk.core.external.command.message.ExternalMessageType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MockSmartCardModuleResponse {
    public static ExternalMessage response(ExternalMessage requestMessage) {
        byte[] requestMessageData = requestMessage.getMessageData();
        byte functionId = requestMessageData[0];
        ExternalMessage responseMessage = null;
        switch (functionId) {
            case IcCardFunctionId.READ_IC_CARD:
                responseMessage = MockSmartCardModuleResponse.readICCardResponse();
                break;
            case IcCardFunctionId.IC_CARD_POWER_UP:
                responseMessage = MockSmartCardModuleResponse.powerUpResponse();
                break;
            case IcCardFunctionId.IC_CARD_POWER_DOWN:
                responseMessage = MockSmartCardModuleResponse.powerDownResponse();
                break;
            case IcCardFunctionId.IC_CARD_RW:
                responseMessage = MockSmartCardModuleResponse.apduResponse(requestMessageData[1]);
                break;
            case IcCardFunctionId.IC_DETECT:
                responseMessage = MockSmartCardModuleResponse.detectResponse();
                break;
            default:
                break;
        }
        return responseMessage;
    }

    public static ExternalMessage readICCardResponse() {
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.IC_CARD_RESPONSE);

        String responseCode = "00";
//        String responseCode = "01";
//        String responseCode = "02";
//        String responseCode = "03";
//        String responseCode = "05";

        byte[] data = new byte[]{0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11};

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int len = data.length;
        try {
            byte[] dataLen = ExternalMessage.intToHexBuf(len);
            outputStream.write(IcCardFunctionId.READ_IC_CARD);
            outputStream.write(responseCode.getBytes());
            outputStream.write(dataLen);
            outputStream.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        responseMessage.setMessageData(outputStream.toByteArray());
        return responseMessage;
    }

    public static ExternalMessage powerUpResponse() {
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.IC_CARD_RESPONSE);

        String responseCode = "00";
//        String responseCode = "01";
//        String responseCode = "02";
//        String responseCode = "46";

        byte[] data = new byte[]{0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11};

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int len = data.length;
        try {
            byte[] dataLen = ExternalMessage.intToHexBuf(len);
            outputStream.write(IcCardFunctionId.IC_CARD_POWER_UP);
            outputStream.write(responseCode.getBytes());
            outputStream.write(dataLen);
            outputStream.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        responseMessage.setMessageData(outputStream.toByteArray());
        return responseMessage;
    }

    public static ExternalMessage powerDownResponse() {
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.IC_CARD_RESPONSE);

        String responseCode = "00";
//        String responseCode = "01";
//        String responseCode = "02";
//        String responseCode = "46";

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(IcCardFunctionId.IC_CARD_POWER_DOWN);
        try {
            outputStream.write(responseCode.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        responseMessage.setMessageData(outputStream.toByteArray());
        return responseMessage;
    }

    public static ExternalMessage apduResponse(byte keyId) {
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.IC_CARD_RESPONSE);

        String responseCode = "00";
//        String responseCode = "01";
//        String responseCode = "02";
//        String responseCode = "46";

        byte[] data = new byte[]{0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11};

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int len = data.length;
        try {
            byte[] dataLen = ExternalMessage.intToHexBuf(len);
            outputStream.write(IcCardFunctionId.IC_CARD_RW);
            outputStream.write(responseCode.getBytes());
            outputStream.write(keyId);
            outputStream.write(ExternalMessage.intToHexBuf(80));
            outputStream.write(dataLen);
            outputStream.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        responseMessage.setMessageData(outputStream.toByteArray());
        return responseMessage;
    }

    public static ExternalMessage detectResponse() {
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.IC_CARD_RESPONSE);

        String responseCode = "00";
//        String responseCode = "01";
//        String responseCode = "02";
//        String responseCode = "46";

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(IcCardFunctionId.IC_DETECT);
        try {
            outputStream.write(responseCode.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        responseMessage.setMessageData(outputStream.toByteArray());
        return responseMessage;
    }
}
