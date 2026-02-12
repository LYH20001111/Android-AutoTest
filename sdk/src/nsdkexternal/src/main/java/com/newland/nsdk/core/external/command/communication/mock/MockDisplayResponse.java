package com.newland.nsdk.core.external.command.communication.mock;

import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.external.command.message.ExternalMessage;
import com.newland.nsdk.core.external.command.message.ExternalMessageType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MockDisplayResponse {
    public static ExternalMessage response(ExternalMessage requestMessage) throws NSDKException {
        byte[] requestMessageData = requestMessage.getMessageData();
        ExternalMessage responseMessage = null;
        switch (requestMessage.getMessageType()) {
            case ExternalMessageType.DISPLAY_COLOR_IMAGE_REQUEST:
                responseMessage = displayColorImageResponse();
                break;
            default:
                break;
        }
        return responseMessage;
    }

    private static ExternalMessage displayColorImageResponse() {
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.DISPLAY_COLOR_IMAGE_RESPONSE);
        String responseCode = "00";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(responseCode.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        responseMessage.setMessageData(outputStream.toByteArray());
        return responseMessage;
    }
}
