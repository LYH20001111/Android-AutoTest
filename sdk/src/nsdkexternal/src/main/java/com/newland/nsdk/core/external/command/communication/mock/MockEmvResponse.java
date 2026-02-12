package com.newland.nsdk.core.external.command.communication.mock;

import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.external.command.emv.capk.ExternalCapkModule;
import com.newland.nsdk.core.external.command.message.functionId.EmvFunctionId;
import com.newland.nsdk.core.external.command.emv.ExternalEmvCallbackID;
import com.newland.nsdk.core.external.command.message.ExternalMessage;
import com.newland.nsdk.core.external.command.message.ExternalMessageType;
import com.newland.nsdk.core.external.command.emv.capk.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MockEmvResponse {
    private static volatile int currentEmvCallbackId = ExternalEmvCallbackID.UI_EVENT;
    private static volatile int lastEmvCallbackId = -1;

    public static void handleEmvRequestMessage(ExternalMessage requestMessage) {
        byte[] requestMessageData = requestMessage.getMessageData();
        if (requestMessageData != null && requestMessageData.length > 0) {
            byte functionId = requestMessageData[0];
            if (functionId == EmvFunctionId.PERFORM_TRANSACTION) {
                currentEmvCallbackId = ExternalEmvCallbackID.UI_EVENT;
                lastEmvCallbackId = -1;
                return;
            }

            if (functionId == EmvFunctionId.CALLBACK) {
                byte callbackId = requestMessageData[1];
                switch (callbackId) {
                    case ExternalEmvCallbackID.UI_EVENT:
                        currentEmvCallbackId = ExternalEmvCallbackID.SELECT_CANDIDATE_LIST;
                        break;
                    case ExternalEmvCallbackID.SELECT_CANDIDATE_LIST:
                        currentEmvCallbackId = ExternalEmvCallbackID.AFTER_FINAL_SELECT;
                        break;
                    case ExternalEmvCallbackID.AFTER_FINAL_SELECT:
                        currentEmvCallbackId = ExternalEmvCallbackID.PIN_ENTRY_DEAL;
                        break;
                    case ExternalEmvCallbackID.PIN_ENTRY_DEAL:
                        currentEmvCallbackId = ExternalEmvCallbackID.CHECK_CREDENTIALS;
                        break;
                    case ExternalEmvCallbackID.CHECK_CREDENTIALS:
                        currentEmvCallbackId = -1;
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public static ExternalMessage response(ExternalMessage requestMessage) throws NSDKException {
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.EMV_RESPONSE);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int requestFunctionId = requestMessage.getMessageData()[0];
        byte[] responseCode = new byte[]{0x30, 0x30};
        byte dataStatus;
        byte[] actualLen;
        byte[] tlvDataLen;
        byte[] tlvData;
        try {
            switch (requestFunctionId) {
                case EmvFunctionId.UPDATE_TERMINAL_CONFIG:
                case EmvFunctionId.UPDATE_AID_CONFIG:
                case EmvFunctionId.REMOVE_A_AID_CONFIG:
                case EmvFunctionId.REMOVE_ALL_AID_CONFIG:
                case EmvFunctionId.UPDATE_CAPK:
                case EmvFunctionId.REMOVE_A_CAPK:
                case EmvFunctionId.REMOVE_ALL_CAPK:
                case EmvFunctionId.UPDATE_REVOCATION_LIST:
                case EmvFunctionId.REMOVE_A_REVOCATION_LIST:
                case EmvFunctionId.REMOVE_ALL_REVOCATION_LIST:
                case EmvFunctionId.UPDATE_EXCEPTION_LIST:
                case EmvFunctionId.REMOVE_AN_EXCEPTION_LIST:
                case EmvFunctionId.REMOVE_ALL_EXCEPTION_LIST:
                case EmvFunctionId.INIT_EMV:
                case EmvFunctionId.SET_DATA:
                case EmvFunctionId.SET_TLV_LIST_DATA:
                case EmvFunctionId.SET_DEBUG_MODE:
                case EmvFunctionId.TERMINATE_TRANSACTION:
//                    requestFunctionId = 0;
//                    responseCode = new byte[] {0x30, 0x31};
//                    responseCode = new byte[] {0x30, 0x32};
//                    responseCode = new byte[] {0x30};
//                    responseMessage.setMessageType(ExternalMessageType.SIGNATURE_REQUEST);
                    outputStream.write(requestFunctionId);
//                    outputStream.write(EmvFunctionId.TERMINATE_TRANSACTION);
                    outputStream.write(responseCode);
                    responseMessage.setMessageData(outputStream.toByteArray());
                    break;
                case EmvFunctionId.GET_TERMINAL_CONFIG:
//                    requestFunctionId = EmvFunctionId.UPDATE_TERMINAL_CONFIG;
//                    responseCode = new byte[] {0x30, 0x31};
//                    responseCode = new byte[] {0x30, 0x32};
                    byte[] config = new byte[]{0x00, 0x09, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09};
//                    byte[] config = new byte[]{0x00, 0x09, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};
//                    byte[] config = new byte[]{0x00, 0x05, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};

                    outputStream.write(requestFunctionId);
                    outputStream.write(responseCode);
                    outputStream.write(config);
                    responseMessage.setMessageData(outputStream.toByteArray());
                    break;
                case EmvFunctionId.GET_AID_CONFIG:
//                    requestFunctionId = EmvFunctionId.UPDATE_TERMINAL_CONFIG;
                    tlvDataLen = new byte[]{0x00, 0x09};
                    tlvData = new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09};
//                    responseCode = new byte[]{0x30, 0x31};
//                  responseCode = new byte[] {0x30, 0x32};

                    outputStream.write(requestFunctionId);
                    outputStream.write(responseCode);
                    outputStream.write(tlvDataLen);
                    outputStream.write(tlvData);
                    responseMessage.setMessageData(outputStream.toByteArray());
                    break;
                case EmvFunctionId.GET_CAPK:
//                    requestFunctionId = EmvFunctionId.UPDATE_TERMINAL_CONFIG;
//                    responseCode = new byte[] {0x30, 0x31};
//                    responseCode = new byte[] {0x30, 0x32};

                    outputStream.write(requestFunctionId);
                    outputStream.write(responseCode);
                    outputStream.write(ExternalCapkModule.pack(getMockCapk(false)));
                    responseMessage.setMessageData(outputStream.toByteArray());
                    break;
                case EmvFunctionId.GET_REVOCATION_LIST:
                case EmvFunctionId.GET_EXCEPTION_LIST:
//                    requestFunctionId = EmvFunctionId.UPDATE_TERMINAL_CONFIG;
//                    responseCode = new byte[] {0x30, 0x31};
//                    responseCode = new byte[] {0x30, 0x32};
                    byte found = 0x00;
//                    byte found = 0x01;

                    outputStream.write(requestFunctionId);
                    outputStream.write(responseCode);
                    outputStream.write(found);
                    responseMessage.setMessageData(outputStream.toByteArray());
                    break;
                case EmvFunctionId.GET_DATA:
                case EmvFunctionId.GET_TLV_LIST_DATA:
//                    requestFunctionId = EmvFunctionId.UPDATE_TERMINAL_CONFIG;
//                    responseCode = new byte[] {0x30, 0x31};
//                    responseCode = new byte[] {0x30, 0x32};
                    dataStatus = 0;
//                    byte dataStatus = 1;
//                    byte dataStatus = 2;
                    actualLen = new byte[]{0x00, 0x03};
                    tlvDataLen = new byte[]{0x00, 0x05};
                    tlvData = new byte[]{0x11, 0x22, 0x33, 0x44, 0x55};
//                    byte[] tlvData = new byte[]{0x11, 0x22, 0x33, 0x00, 0x00};

                    outputStream.write(requestFunctionId);
                    outputStream.write(responseCode);
                    outputStream.write(dataStatus);
                    outputStream.write(actualLen);
                    outputStream.write(tlvDataLen);
                    outputStream.write(tlvData);
                    responseMessage.setMessageData(outputStream.toByteArray());
                    break;
                case EmvFunctionId.SET_CONFIG:
                    outputStream.write(requestFunctionId);
                    outputStream.write(responseCode);
                    responseMessage.setMessageData(outputStream.toByteArray());
                    break;
                case EmvFunctionId.GET_CONFIG:
                    outputStream.write(requestFunctionId);
                    outputStream.write(responseCode);
                    outputStream.write(0x00);
                    responseMessage.setMessageData(outputStream.toByteArray());
                    break;
                case EmvFunctionId.GET_VERSION:
//                    requestFunctionId = EmvFunctionId.UPDATE_TERMINAL_CONFIG;
//                    responseCode = new byte[] {0x30, 0x31};
//                    responseCode = new byte[] {0x30, 0x32};
                    byte versionLen = 0x05;
//                    byte[] version = new byte[]{0x31, 0x32, 0x33, 0x34, 0x35};
                    byte[] version = new byte[]{0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37};

                    outputStream.write(requestFunctionId);
                    outputStream.write(responseCode);
                    outputStream.write(versionLen);
                    outputStream.write(version);
                    responseMessage.setMessageData(outputStream.toByteArray());
                    break;
                case EmvFunctionId.COMPLETE_TRANSACTION:
//                    requestFunctionId = EmvFunctionId.UPDATE_TERMINAL_CONFIG;
//                byte[] responseCode = new byte[]{0x30, 0x31};
//                byte[] responseCode = new byte[]{0x30, 0x32};
                    byte transactionResult = 4;
                    byte[] errorCode = new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFE, (byte) 0x0A};
                    dataStatus = 0;
                    actualLen = new byte[]{0x00, 0x05};
                    tlvDataLen = new byte[]{0x00, 0x06};
//                byte[] tlvListValueLen = new byte[]{0x00, 0x07};
                    tlvData = new byte[]{0x11, 0x22, 0x33, 0x44, 0x55, 0x66};

                    outputStream.write(requestFunctionId);
                    outputStream.write(responseCode);
                    outputStream.write(transactionResult);
                    outputStream.write(errorCode);
                    outputStream.write(dataStatus);
                    outputStream.write(actualLen);
                    outputStream.write(tlvDataLen);
                    outputStream.write(tlvData);
                    responseMessage.setMessageData(outputStream.toByteArray());
                    break;
                case EmvFunctionId.GET_AID_NUM:
                case EmvFunctionId.GET_CAPK_NUM:
                    outputStream.write(requestFunctionId);
                    outputStream.write(responseCode);
                    outputStream.write(new byte[]{0x00, 0x0A});
                    responseMessage.setMessageData(outputStream.toByteArray());
                    break;
                case EmvFunctionId.TRANSACTION_PREPROCESS:
                    outputStream.write(requestFunctionId);
                    outputStream.write(responseCode);
                    outputStream.write(new byte[]{0x33, 0x0A, 0x11, 0x22});
                    responseMessage.setMessageData(outputStream.toByteArray());
                    break;
                case EmvFunctionId.PERFORM_TRANSACTION:
                case EmvFunctionId.CALLBACK:
                    responseMessage = mockEmvCallback();
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return responseMessage;
    }

    public static ExternalMessage mockEmvCallback() throws NSDKException {
        ExternalMessage responseMessage = null;
        if (lastEmvCallbackId == currentEmvCallbackId) {
            System.out.println("############## Call back ID is not changed.");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
        switch (currentEmvCallbackId) {
            case ExternalEmvCallbackID.UI_EVENT:
                responseMessage = generateUIEventData();
                break;
            case ExternalEmvCallbackID.SELECT_CANDIDATE_LIST:
                responseMessage = generateCandidateListSelectData();
                break;
            case ExternalEmvCallbackID.AFTER_FINAL_SELECT:
                responseMessage = generateFinalSelectData();
                break;
            case ExternalEmvCallbackID.PIN_ENTRY_DEAL:
                responseMessage = generatePinEntryDealData();
                break;
            case ExternalEmvCallbackID.CHECK_CREDENTIALS:
                responseMessage = generateCredentialsCheckData();
                break;
            case -1:
                responseMessage = generatePerformTransactionResponse();
                break;
            default:
                break;
        }

        return responseMessage;
    }

    private static ExternalMessage generatePinEntryDealData() {
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.EMV_RESPONSE);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(EmvFunctionId.CALLBACK);
        outputStream.write(ExternalEmvCallbackID.PIN_ENTRY_DEAL);
        outputStream.write(2);
        try {
            outputStream.write(new byte[]{0x00, 0x08});
            outputStream.write(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08});
        } catch (IOException e) {
            e.printStackTrace();
        }
        responseMessage.setMessageData(outputStream.toByteArray());
        lastEmvCallbackId = ExternalEmvCallbackID.PIN_ENTRY_DEAL;
        return responseMessage;
    }

    public static ExtCapkEntry getMockCapk(boolean isFullModulus) {
        ExtCapkEntry capk = new ExtCapkEntry();
        if (isFullModulus) {
            capk.setModulus(new byte[]{(byte) 0xF8, 0x02, (byte) 0xC3, 0x08, 0x54, 0x48, 0x73, (byte) 0xAD, 0x22, 0x25, (byte) 0xA8,
                    0x19, 0x43, 0x73, 0x2A, 0x4B, 0x7C, (byte) 0xFF, (byte) 0xA4, (byte) 0xE3, 0x15, 0x7D, 0x17, (byte) 0xCD, 0x5A,
                    0x77, 0x23, (byte) 0xF8, 0x58, (byte) 0xF0, (byte) 0xB1, 0x1E, 0x63, 0x6D, 0x29, 0x30, (byte) 0xFA, (byte) 0x93,
                    0x37, 0x78, (byte) 0xF2, 0x7C, 0x7C, 0x49, 0x12, 0x7E, 0x0C, (byte) 0xCA, 0x31, 0x70, 0x21, (byte) 0xCF, (byte) 0xE8,
                    (byte) 0xE0, (byte) 0xF7, 0x73, 0x78, 0x5E, (byte) 0xB3, (byte) 0xFF, 0x07, 0x58, 0x7E, (byte) 0x98, (byte) 0xCE,
                    (byte) 0x8E, (byte) 0xD4, (byte) 0xFE, (byte) 0x9E, 0x1C, (byte) 0xA1, (byte) 0x85, (byte) 0x9F, 0x41, (byte) 0xA9,
                    (byte) 0xCF, 0x25, 0x72, (byte) 0xD8, (byte) 0xA0, (byte) 0x93, (byte) 0xC5, 0x46, 0x5F, 0x5A, 0x29, 0x61, 0x2A, 0x45,
                    (byte) 0xB1, 0x70, 0x0F, 0x4D, (byte) 0xA1, 0x38, 0x14, (byte) 0xC3, (byte) 0xD4, (byte) 0xDF, 0x07, 0x5E, (byte) 0xAA,
                    (byte) 0xDE, (byte) 0x8D, (byte) 0xB4, (byte) 0xBE, 0x4D, 0x7B, 0x3A, (byte) 0xE0, 0x25, 0x6F, 0x7A, 0x0C, 0x12,
                    (byte) 0xE3, 0x4B, (byte) 0xD4, 0x16, (byte) 0xCA, (byte) 0xC4, (byte) 0xF9, 0x25, 0x0C, 0x38, (byte) 0xB7, (byte) 0xE1,
                    0x3B, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
        } else {
            capk.setModulus(new byte[]{(byte) 0xF8, 0x02, (byte) 0xC3, 0x08, 0x54, 0x48, 0x73, (byte) 0xAD, 0x22, 0x25, (byte) 0xA8,
                    0x19, 0x43, 0x73, 0x2A, 0x4B, 0x7C, (byte) 0xFF, (byte) 0xA4, (byte) 0xE3, 0x15, 0x7D, 0x17, (byte) 0xCD, 0x5A,
                    0x77, 0x23, (byte) 0xF8, 0x58, (byte) 0xF0, (byte) 0xB1, 0x1E, 0x63, 0x6D, 0x29, 0x30, (byte) 0xFA, (byte) 0x93,
                    0x37, 0x78, (byte) 0xF2, 0x7C, 0x7C, 0x49, 0x12, 0x7E, 0x0C, (byte) 0xCA, 0x31, 0x70, 0x21, (byte) 0xCF, (byte) 0xE8,
                    (byte) 0xE0, (byte) 0xF7, 0x73, 0x78, 0x5E, (byte) 0xB3, (byte) 0xFF, 0x07, 0x58, 0x7E, (byte) 0x98, (byte) 0xCE,
                    (byte) 0x8E, (byte) 0xD4, (byte) 0xFE, (byte) 0x9E, 0x1C, (byte) 0xA1, (byte) 0x85, (byte) 0x9F, 0x41, (byte) 0xA9,
                    (byte) 0xCF, 0x25, 0x72, (byte) 0xD8, (byte) 0xA0, (byte) 0x93, (byte) 0xC5, 0x46, 0x5F, 0x5A, 0x29, 0x61, 0x2A, 0x45,
                    (byte) 0xB1, 0x70, 0x0F, 0x4D, (byte) 0xA1, 0x38, 0x14, (byte) 0xC3, (byte) 0xD4, (byte) 0xDF, 0x07, 0x5E, (byte) 0xAA,
                    (byte) 0xDE, (byte) 0x8D, (byte) 0xB4, (byte) 0xBE, 0x4D, 0x7B, 0x3A, (byte) 0xE0, 0x25, 0x6F, 0x7A, 0x0C, 0x12,
                    (byte) 0xE3, 0x4B, (byte) 0xD4, 0x16, (byte) 0xCA, (byte) 0xC4, (byte) 0xF9, 0x25, 0x0C, 0x38, (byte) 0xB7, (byte) 0xE1, 0x3B});
        }

        capk.setExponent(new byte[]{0x01, 0x01, 0x01});
        capk.setHash(new byte[]{0x20, 0x15, 0x49, 0x7B, (byte) 0xE4, (byte) 0xB8, 0x6F, 0x10, 0x4B, (byte) 0xBF, 0x33, 0x76, (byte) 0x91, (byte) 0x82, 0x5E, (byte) 0xED, 0x64, (byte) 0xE1, 0x01, (byte) 0xCA});
        capk.setExpiredDate(new byte[]{0x20, 0x20, 0x07, 0x14});
        capk.setRid(new byte[]{0x02, 0x77, 0x40, 0x01, 0x01});
        capk.setIndex((byte) 0x23);
        capk.setAlgorithmIndicator((byte) 0x01);
        capk.setHashAlgorithm((byte) 0x02);

        return capk;
    }

    public static ExternalMessage generatePerformTransactionResponse() {
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.EMV_RESPONSE);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte requestFunctionId = EmvFunctionId.PERFORM_TRANSACTION;
        byte[] responseCode = new byte[]{0x30, 0x30};
//        byte[] responseCode = new byte[]{0x30, 0x31};
//        byte[] responseCode = new byte[]{0x30, 0x32};
        byte transactionResult = 4;
        byte cvmStatus = 0;
        byte[] errorCode = new byte[]{(byte) 0xE1, (byte) 0xE2, (byte) 0xE3, (byte) 0xE4};
        byte dataStatus = 0;
        byte[] actualLen = new byte[]{0x00, 0x05};
        byte[] tlvDataLen = new byte[]{0x00, 0x06};
//                byte[] tlvListValueLen = new byte[]{0x00, 0x07};
        byte[] tlvData = new byte[]{0x11, 0x22, 0x33, 0x44, 0x55, 0x66};

        try {
            outputStream.write(requestFunctionId);
            outputStream.write(responseCode);
            outputStream.write(transactionResult);
            outputStream.write(cvmStatus);
            outputStream.write(errorCode);
            outputStream.write(dataStatus);
            outputStream.write(actualLen);
            outputStream.write(tlvDataLen);
            outputStream.write(tlvData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        responseMessage.setMessageData(outputStream.toByteArray());
        return responseMessage;
    }

    public static ExternalMessage generateCredentialsCheckData() {
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.EMV_RESPONSE);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(EmvFunctionId.CALLBACK);
        outputStream.write(ExternalEmvCallbackID.CHECK_CREDENTIALS);
        byte[] typeTlv = new byte[]{(byte) 0x9F, 0x62, 0x01, 0x04};
        byte[] numberTlv = new byte[]{(byte) 0x9F, 0x61, 0x02, 0x11, 0x22};
        try {
            outputStream.write(ExternalMessage.intToHexBuf(typeTlv.length + numberTlv.length));
            outputStream.write(typeTlv);
            outputStream.write(numberTlv);
        } catch (Exception e) {
            e.printStackTrace();
        }
        responseMessage.setMessageData(outputStream.toByteArray());
        lastEmvCallbackId = ExternalEmvCallbackID.CHECK_CREDENTIALS;
        return responseMessage;
    }

    public static ExternalMessage generateFinalSelectData() {
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.EMV_RESPONSE);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(EmvFunctionId.CALLBACK);
        outputStream.write(ExternalEmvCallbackID.AFTER_FINAL_SELECT);
        outputStream.write(2);
        try {
            outputStream.write(new byte[]{0x00, 0x08});
            outputStream.write(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08});
        } catch (IOException e) {
            e.printStackTrace();
        }
        responseMessage.setMessageData(outputStream.toByteArray());
        lastEmvCallbackId = ExternalEmvCallbackID.AFTER_FINAL_SELECT;
        return responseMessage;
    }

    public static ExternalMessage generateCandidateListSelectData() throws NSDKException {
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.EMV_RESPONSE);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(EmvFunctionId.CALLBACK);
        outputStream.write(ExternalEmvCallbackID.SELECT_CANDIDATE_LIST);
        try {
            String aid1 = "9F3601939F3703112233";
            String aid2 = "9F3801639F3903778866";
            String aid3 = "9F4001569F4103345678";

            outputStream.write(3);
            outputStream.write(ExternalMessage.intToHexBuf(aid1.length() / 2));
            outputStream.write(ISOUtils.hex2byte(aid1));
            outputStream.write(ExternalMessage.intToHexBuf(aid2.length() / 2));
            outputStream.write(ISOUtils.hex2byte(aid2));
            outputStream.write(ExternalMessage.intToHexBuf(aid3.length() / 2));
            outputStream.write(ISOUtils.hex2byte(aid3));
        } catch (IOException e) {
            e.printStackTrace();
        }
        responseMessage.setMessageData(outputStream.toByteArray());
        lastEmvCallbackId = ExternalEmvCallbackID.SELECT_CANDIDATE_LIST;
        return responseMessage;
    }

    public static ExternalMessage generateUIEventData() {
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.EMV_RESPONSE);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(EmvFunctionId.CALLBACK);
        outputStream.write(ExternalEmvCallbackID.UI_EVENT);
        outputStream.write(0);
        try {
            outputStream.write("Swipe/Insert/Tap/Manual".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        responseMessage.setMessageData(outputStream.toByteArray());
        // No need to respond to UI event, go to next callback directly.
        currentEmvCallbackId = ExternalEmvCallbackID.SELECT_CANDIDATE_LIST;
        return responseMessage;
    }
}
