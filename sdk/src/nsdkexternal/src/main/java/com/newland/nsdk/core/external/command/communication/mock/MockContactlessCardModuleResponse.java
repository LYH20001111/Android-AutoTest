package com.newland.nsdk.core.external.command.communication.mock;

import com.newland.nsdk.core.external.command.message.functionId.ContactlessCardFunctionId;
import com.newland.nsdk.core.external.command.message.ExternalMessage;
import com.newland.nsdk.core.external.command.message.ExternalMessageType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MockContactlessCardModuleResponse {
    public static ExternalMessage response(ExternalMessage requestMessage) {
        byte[] requestMessageData = requestMessage.getMessageData();
        byte functionId = requestMessageData[0];
        ExternalMessage responseMessage = null;
        switch (functionId) {
            case ContactlessCardFunctionId.CHECK_READER:
            case ContactlessCardFunctionId.EXCHANGE_PLAINTEXT_APDU:
            case ContactlessCardFunctionId.READ_BLOCK_DATA:
            case ContactlessCardFunctionId.GET_ATS:
                responseMessage = responseWithDataNoLen(functionId);
                break;
            case ContactlessCardFunctionId.EXCHANGE_APDU:
            case ContactlessCardFunctionId.EXCHANGE_APDU_FELICA:
                responseMessage = responseWithDataLen2(functionId);
                break;
            case ContactlessCardFunctionId.CHECK_FELICA_CARD_PRESENCE:
                responseMessage = responseWithDataLen1(functionId);
                break;
            case ContactlessCardFunctionId.PRESENCE:
                responseMessage = searchCard();
                break;
            case ContactlessCardFunctionId.ACTIVE_FIELD:
            case ContactlessCardFunctionId.CHECK_CARD_PRESENCE:
            case ContactlessCardFunctionId.DEACTIVATE:
            case ContactlessCardFunctionId.FLASH_LED:
            case ContactlessCardFunctionId.AUTHENTICATION_WITH_EXTERNAL_KEY:
            case ContactlessCardFunctionId.WRITE_BLOCK_DATA:
            case ContactlessCardFunctionId.INCREMENT:
            case ContactlessCardFunctionId.DECREMENT:
            case ContactlessCardFunctionId.TRANSFER:
            case ContactlessCardFunctionId.RESTORE:
                responseMessage = responseWithNoData(functionId);
                break;
            default:
                break;
        }
        return responseMessage;
    }

    public static ExternalMessage responseWithDataNoLen(byte functionId) {
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.CONTACTLESS_CARD_RESPONSE);

        String responseCode = "00";
//        String responseCode = "01";
//        String responseCode = "02";
//        String responseCode = "03";
//        String responseCode = "10";

        byte[] data = new byte[]{0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A};

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(functionId);
        try {
            outputStream.write(responseCode.getBytes());
//            outputStream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        responseMessage.setMessageData(outputStream.toByteArray());
        return responseMessage;
    }

    public static ExternalMessage activate() {
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.CONTACTLESS_CARD_RESPONSE);

        String responseCode = "00";
//        String responseCode = "01";
//        String responseCode = "02";

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(ContactlessCardFunctionId.ACTIVE_FIELD);
        try {
            outputStream.write(responseCode.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        responseMessage.setMessageData(outputStream.toByteArray());
        return responseMessage;
    }

    public static ExternalMessage checkCard() {
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.CONTACTLESS_CARD_RESPONSE);

        String responseCode = "00";
//        String responseCode = "01";
//        String responseCode = "02";
//        String responseCode = "10";
//        String responseCode = "11";

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(ContactlessCardFunctionId.CHECK_CARD_PRESENCE);
        try {
            outputStream.write(responseCode.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        responseMessage.setMessageData(outputStream.toByteArray());
        return responseMessage;
    }

    public static ExternalMessage responseWithDataLen2(byte functionId) {
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.CONTACTLESS_CARD_RESPONSE);

        String responseCode = "00";
//        String responseCode = "01";
//        String responseCode = "02";
//        String responseCode = "10";

        byte[] data = new byte[]{0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A};

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(functionId);
//        outputStream.write(ContactlessCardFunctionId.ACTIVE_FIELD);
        try {
            outputStream.write(responseCode.getBytes());
            outputStream.write(ExternalMessage.intToHexBuf(data.length));
//            outputStream.write(ExternalMessage.intToHexBuf(0));
//            outputStream.write(ExternalMessage.intToHexBuf(12));
//            outputStream.write(ExternalMessage.intToHexBuf(8));
            outputStream.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        responseMessage.setMessageData(outputStream.toByteArray());
        return responseMessage;
    }

    public static ExternalMessage responseWithNoData(byte functionId) {
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.CONTACTLESS_CARD_RESPONSE);

        String responseCode = "00";
//        String responseCode = "01";
//        String responseCode = "02";
//        String responseCode = "10";
//        String responseCode = "11";

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(functionId);
//        outputStream.write(ContactlessCardFunctionId.ACTIVE_FIELD);
        try {
            outputStream.write(responseCode.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        responseMessage.setMessageData(outputStream.toByteArray());
        return responseMessage;
    }

    public static ExternalMessage responseWithDataLen1(byte functionId) {
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.CONTACTLESS_CARD_RESPONSE);

        String responseCode = "00";
//        String responseCode = "01";
//        String responseCode = "02";
//        String responseCode = "10";

        byte[] data = new byte[]{0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A};

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(functionId);
//        outputStream.write(ContactlessCardFunctionId.ACTIVE_FIELD);
        try {
            outputStream.write(responseCode.getBytes());
            outputStream.write(data.length);
//            outputStream.write(0);
//            outputStream.write(5);
//            outputStream.write(12);
            outputStream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        responseMessage.setMessageData(outputStream.toByteArray());
        return responseMessage;
    }

    public static ExternalMessage searchCard() {
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.CONTACTLESS_CARD_RESPONSE);

        String responseCode = "00";
//        String responseCode = "01";
//        String responseCode = "02";
//        String responseCode = "03";
//        String responseCode = "06";
//        String responseCode = "09";
//        String responseCode = "10";

        byte[] atq = new byte[]{0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A};
        byte[] uid = new byte[]{0x21, 0x22, 0x23, 0x24, 0x25, 0x26};
        byte[] sak = new byte[]{0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38};

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(ContactlessCardFunctionId.PRESENCE);
//        outputStream.write(ContactlessCardFunctionId.ACTIVE_FIELD);
        try {
            outputStream.write(responseCode.getBytes());
            outputStream.write(0x0A);
            outputStream.write(atq.length);
//            outputStream.write(12);
            outputStream.write(atq);
//            outputStream.write(0);
            outputStream.write(uid.length);
            outputStream.write(uid);
//            outputStream.write(sak.length);
//            outputStream.write(sak);
        } catch (IOException e) {
            e.printStackTrace();
        }
        responseMessage.setMessageData(outputStream.toByteArray());
        return responseMessage;
    }
}
