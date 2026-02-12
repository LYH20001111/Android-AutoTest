package com.newland.nsdk.core.external.command.communication.mock;

import com.newland.nsdk.core.external.command.message.ExternalMessage;
import com.newland.nsdk.core.external.command.message.ExternalMessageType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MockPinPadResponse {
    public static ExternalMessage dataEncryptDecrypt(ExternalMessage requestMessage) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.DATA_ENCRYPTION_DECRYPTION_RESPONSE);
        byte keyIndex = requestMessage.getMessageData()[0];
//                byte keyIndex = -1;
        byte[] responseCodeBuf = new byte[]{0x30, 0x30};
//                byte[] responseCodeBuf = new byte[]{0x30, 0x32};
//                byte[] responseCodeBuf = new byte[]{0x30, 0x32};
//                byte[] responseCodeBuf = new byte[]{0x34, 0x32};
//                byte[] responseCodeBuf = new byte[]{0x34, 0x33};
//                byte[] responseCodeBuf = new byte[]{0x34, 0x35};
        byte[] encryptedData = new byte[]{(byte) 0xA1, (byte) 0xA2, (byte) 0xA3, (byte) 0xA4};
        try {
            outputStream.write(keyIndex);
            outputStream.write(responseCodeBuf);
            outputStream.write(ExternalMessage.intToHexBuf(encryptedData.length));
//                    outputStream.write(new byte[]{0x00, 0x00});
            outputStream.write(encryptedData);
        } catch (Exception e) {
            e.printStackTrace();
        }

        responseMessage.setMessageData(outputStream.toByteArray());
        return responseMessage;
    }

    public static ExternalMessage dataEncryptDecryptNapi(ExternalMessage requestMessage) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.DATA_ENCRYPTION_DECRYPTION_NAPI_RESPONSE);
        byte keyIndex = requestMessage.getMessageData()[0];
//                byte keyIndex = -1;
        byte[] responseCodeBuf = new byte[]{0x30, 0x30};
//                byte[] responseCodeBuf = new byte[]{0x30, 0x32};
//                byte[] responseCodeBuf = new byte[]{0x30, 0x32};
//                byte[] responseCodeBuf = new byte[]{0x34, 0x32};
//                byte[] responseCodeBuf = new byte[]{0x34, 0x33};
//                byte[] responseCodeBuf = new byte[]{0x34, 0x35};
        byte[] encryptedData = new byte[]{(byte) 0xA1, (byte) 0xA2, (byte) 0xA3, (byte) 0xA4};
        byte[] ksn = new byte[]{0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19};
        try {
//            outputStream.write(keyIndex);
            outputStream.write(responseCodeBuf);
            outputStream.write(ExternalMessage.intToHexBuf(encryptedData.length));
//                    outputStream.write(new byte[]{0x00, 0x00});
            outputStream.write(encryptedData);
//            outputStream.write(ksn.length);
            outputStream.write(0);
//            outputStream.write(ksn);
        } catch (Exception e) {
            e.printStackTrace();
        }

        responseMessage.setMessageData(outputStream.toByteArray());
        return responseMessage;
    }

    public static ExternalMessage pinEntry(ExternalMessage requestMessage) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.PIN_ENTRY_RESPONSE);
        byte keyType = requestMessage.getMessageData()[1];
        String responseCode = "00";
//        String responseCode = "02";
//        String responseCode = "04";
//        String responseCode = "42";
//        String responseCode = "43";

        byte functionKey = 0;
        byte[] encryptedPinBlock = new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};
        byte[] ksn = new byte[]{0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A};

        try {
            outputStream.write(responseCode.getBytes());
            outputStream.write(functionKey);
            outputStream.write(encryptedPinBlock.length);
//                    outputStream.write(new byte[]{0x00, 0x00});
            outputStream.write(encryptedPinBlock);
            if (keyType == 1) {
                outputStream.write(ksn);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (responseCode.equals("00")) {
            for (int i = 0; i < 6; i++) {
                System.out.println("Press key " + i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        responseMessage.setMessageData(outputStream.toByteArray());
        return responseMessage;
    }

    public static ExternalMessage extendedPinEntry(ExternalMessage requestMessage) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.EXTENDED_PIN_ENTRY_RESPONSE);
        byte method = requestMessage.getMessageData()[0];
        String responseCode = "00";
//        String responseCode = "02";
//        String responseCode = "04";
//        String responseCode = "42";
//        String responseCode = "43";

        byte functionKey = 0;
        byte[] encryptedPinBlock = new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};
        byte[] randomPinKey = new byte[]{0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A};

        try {
            outputStream.write(responseCode.getBytes());
            outputStream.write(functionKey);
            outputStream.write(encryptedPinBlock.length);
//                    outputStream.write(new byte[]{0x00, 0x00});
            outputStream.write(encryptedPinBlock);
            if (method == 1) {
                outputStream.write(randomPinKey.length);
                outputStream.write(randomPinKey);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (responseCode.equals("00")) {
            for (int i = 0; i < 6; i++) {
                System.out.println("Press key " + i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        responseMessage.setMessageData(outputStream.toByteArray());
        return responseMessage;
    }

    public static ExternalMessage sensitivePinEntry(ExternalMessage requestMessage) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.SENSITIVE_DATA_ENTRY_RESPONSE);
        String responseCode = "00";
//        String responseCode = "02";
//        String responseCode = "04";
//        String responseCode = "05";
//        String responseCode = "43";

        byte dataLen = 10;
        byte[] encryptedData = new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};

        try {
            outputStream.write(responseCode.getBytes());
            outputStream.write(dataLen);
            if (dataLen > 0) {
                outputStream.write(encryptedData.length);
                outputStream.write(encryptedData);
            } else {
                outputStream.write(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (responseCode.equals("00")) {
            for (int i = 0; i < 6; i++) {
                System.out.println("Press key " + i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        responseMessage.setMessageData(outputStream.toByteArray());
        return responseMessage;
    }

    public static ExternalMessage aesDataEncryptDecrypt(ExternalMessage requestMessage) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.AES_DATA_ENCRYPTION_DECRYPTION_RESPONSE);
        byte keyIndex = requestMessage.getMessageData()[0];
//                byte keyIndex = -1;
        byte[] responseCodeBuf = new byte[]{0x30, 0x30};
//                byte[] responseCodeBuf = new byte[]{0x30, 0x32};
//                byte[] responseCodeBuf = new byte[]{0x34, 0x33};
//                byte[] responseCodeBuf = new byte[]{0x34, 0x35};
        byte[] encryptedData = new byte[]{(byte) 0xA1, (byte) 0xA2, (byte) 0xA3, (byte) 0xA4};
        try {
            outputStream.write(keyIndex);
            outputStream.write(responseCodeBuf);
            outputStream.write(ExternalMessage.intToHexBuf(encryptedData.length));
//                    outputStream.write(new byte[]{0x00, 0x00});
            outputStream.write(encryptedData);
        } catch (Exception e) {
            e.printStackTrace();
        }

        responseMessage.setMessageData(outputStream.toByteArray());
        return responseMessage;
    }

    public static ExternalMessage dukptEncryptDecrypt(ExternalMessage requestMessage) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.DUKPT_DATA_ENCRYPTION_DECRYPTION_RESPONSE);
        byte keyIndex = requestMessage.getMessageData()[0];
//                byte keyIndex = -1;
        byte[] responseCodeBuf = new byte[]{0x30, 0x30};
//                byte[] responseCodeBuf = new byte[]{0x30, 0x31};
//                byte[] responseCodeBuf = new byte[]{0x30, 0x32};
//                byte[] responseCodeBuf = new byte[]{0x30, 0x33};
//                byte[] responseCodeBuf = new byte[]{0x30, 0x34};
        byte[] encryptedData = new byte[]{(byte) 0xA1, (byte) 0xA2, (byte) 0xA3, (byte) 0xA4};
        byte[] ksn = new byte[]{(byte) 0xB1, (byte) 0xB2, (byte) 0xB3, (byte) 0xB4, (byte) 0xB5, (byte) 0xB6, (byte) 0xB7, (byte) 0xB8, (byte) 0xB9, (byte) 0xB0};
        outputStream.write(keyIndex);
        try {
            outputStream.write(responseCodeBuf);
            outputStream.write(ExternalMessage.intToHexBuf(encryptedData.length));
//                    outputStream.write(new byte[]{0x00, 0x00});
            outputStream.write(encryptedData);
            outputStream.write(ksn);
        } catch (Exception e) {
            e.printStackTrace();
        }

        responseMessage.setMessageData(outputStream.toByteArray());
        return responseMessage;
    }

    public static ExternalMessage macGeneration(ExternalMessage requestMessage) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.MAC_GENERATION_RESPONSE);
        byte keyId = requestMessage.getMessageData()[0];
        byte keyType = requestMessage.getMessageData()[1];
        byte flag = requestMessage.getMessageData()[3];
        byte[] responseCodeBuf = new byte[]{0x30, 0x30};
//                byte[] responseCodeBuf = new byte[]{0x30, 0x32};
//                byte[] responseCodeBuf = new byte[]{0x30, 0x34};
//                byte[] responseCodeBuf = new byte[]{0x34, 0x32};
//                byte[] responseCodeBuf = new byte[]{0x34, 0x33};
//                byte[] responseCodeBuf = new byte[]{0x34, 0x35};
        byte[] desMac = new byte[]{0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18};
        byte[] aesMac = new byte[]{0x20, 0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2A, 0x2B, 0x2C, 0x2D, 0x2E, 0x2F};
        byte[] ksn = new byte[]{0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3A};
        outputStream.write(keyId);
        try {
            outputStream.write(responseCodeBuf);
            if (flag == 2 || flag == 3) {
                if (keyType == 0) {
                    outputStream.write(desMac);
                } else if (keyType == 1) {
                    outputStream.write(desMac);
                    outputStream.write(ksn);
                } else {
//                    outputStream.write(desMac);
                    outputStream.write(aesMac);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        responseMessage.setMessageData(outputStream.toByteArray());
        return responseMessage;
    }

    public static ExternalMessage macGenerationNapi(ExternalMessage requestMessage) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.MAC_GENERATION_NAPI_RESPONSE);
        byte keyId = requestMessage.getMessageData()[1];
        byte ivLen = requestMessage.getMessageData()[2];
        int offset = 3 + ivLen;
        byte flag = requestMessage.getMessageData()[offset];
        byte[] responseCodeBuf = new byte[]{0x30, 0x30};
//                byte[] responseCodeBuf = new byte[]{0x30, 0x32};
//                byte[] responseCodeBuf = new byte[]{0x30, 0x34};
//                byte[] responseCodeBuf = new byte[]{0x34, 0x32};
//                byte[] responseCodeBuf = new byte[]{0x34, 0x33};
//                byte[] responseCodeBuf = new byte[]{0x34, 0x35};
        byte[] desMac = new byte[]{0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18};
//        byte[] aesMac = new byte[]{0x20, 0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2A, 0x2B, 0x2C, 0x2D, 0x2E, 0x2F};
        byte[] ksn = new byte[]{0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3A};
        outputStream.write(keyId);
        try {
            outputStream.write(responseCodeBuf);
            if (flag == 2 || flag == 3) {
                outputStream.write(desMac.length);
                outputStream.write(desMac);
                outputStream.write(0);
//                outputStream.write(ksn.length);
                outputStream.write(ksn);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        responseMessage.setMessageData(outputStream.toByteArray());
        return responseMessage;
    }

    public static ExternalMessage loadGiskeKey() {
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.LOAD_GISKE_KEY_RESPONSE);
        byte[] responseCodeBuf = new byte[]{0x30, 0x30};
//                byte[] responseCodeBuf = new byte[]{0x30, 0x31};
//                byte[] responseCodeBuf = new byte[]{0x30, 0x32};

        responseMessage.setMessageData(responseCodeBuf);
        return responseMessage;
    }

    public static ExternalMessage loadGiskeTikKey() {
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.LOAD_GISKE_TIK_RESPONSE);
        byte[] responseCodeBuf = new byte[]{0x30, 0x30};
//                byte[] responseCodeBuf = new byte[]{0x30, 0x31};
//                byte[] responseCodeBuf = new byte[]{0x30, 0x33};

        responseMessage.setMessageData(responseCodeBuf);
        return responseMessage;
    }

    public static ExternalMessage convertAtmToGiske() {
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.LOAD_CONVERT_ATM_TO_GISKE_RESPONSE);
        byte[] responseCodeBuf = new byte[]{0x30, 0x30};
//                byte[] responseCodeBuf = new byte[]{0x30, 0x31};
//                byte[] responseCodeBuf = new byte[]{0x30, 0x32};

        responseMessage.setMessageData(responseCodeBuf);
        return responseMessage;
    }

    public static ExternalMessage deleteKey() {
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.DELETE_KEY_RESPONSE);
        byte[] responseCodeBuf = new byte[]{0x30, 0x30};
//                byte[] responseCodeBuf = new byte[]{0x30, 0x31};
//                byte[] responseCodeBuf = new byte[]{0x30, 0x32};

        responseMessage.setMessageData(responseCodeBuf);
        return responseMessage;
    }

    public static ExternalMessage increaseKsn() {
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.DUKPT_KSN_INCREASE_RESPONSE);
        byte[] responseCodeBuf = new byte[]{0x30, 0x30};
//                byte[] responseCodeBuf = new byte[]{0x30, 0x31};
//                byte[] responseCodeBuf = new byte[]{0x30, 0x34};

        responseMessage.setMessageData(responseCodeBuf);
        return responseMessage;
    }
}
