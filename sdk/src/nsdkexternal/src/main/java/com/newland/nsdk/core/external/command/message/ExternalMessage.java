package com.newland.nsdk.core.external.command.message;

import android.util.Log;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.api.external.exception.NSDKExternalDeviceException;
import com.newland.nsdk.core.external.command.exception.ExternalErrorMessage;
import com.newland.nsdk.core.external.command.exception.ExternalMessageException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Message that transferred between host device and external device.
 */
public class ExternalMessage {

    private static final String TAG = "ExternalMessage";
    public static final byte MINUS = 0x2D;
    /**
     * An ACK indicates the successful response to a request message.
     */
    public static final byte ACK = 0x06;

    /**
     * A NAK indicates that the response to the request message is in error.
     */
    public static final byte NAK = 0x15;

    /**
     * Length of STX field.
     */
    public static final int STX_FIELD_LEN = 1;

    /**
     * Length of length filed.
     */
    public static final int LENGTH_FIELD_LEN = 2;

    /**
     * Length of message type field.
     */
    public static final int MESSAGE_TYPE_FIELD_LEN = 2;

    /**
     * Length of separator field.
     */
    public static final int SEPARATOR_FIELD_LEN = 1;

    /**
     * Length of ETX field.
     */
    public static final int ETX_FIELD_LEN = 1;

    /**
     * Length of LRC field.
     */
    public static final int LRC_FIELD_LEN = 1;
    /**
     * Length of MAC field.
     */
    public static final int MAC_FIELD_LEN = 8;
    /**
     * Length of KSN field.
     */
    public static final int KSN_FIELD_LEN = 10;

    /**
     * Min length of an external message. These fields must present in an external message.
     */
    public static final int MIN_MESSAGE_LEN = STX_FIELD_LEN + LENGTH_FIELD_LEN + MESSAGE_TYPE_FIELD_LEN + SEPARATOR_FIELD_LEN + ETX_FIELD_LEN + LRC_FIELD_LEN;

    /**
     * Start of text.
     */
    public static final byte STX = 0x02;

    /**
     * End of text.
     */
    public static final byte ETX = 0x03;

    /**
     * Separator("/").
     */
    public static final byte SEPARATOR = 0x2F;

    /**
     * Max length of play load.
     */
    public static final int MAX_DATA_LEN = 9999;

    /**
     * Message type, see {@link ExternalMessageType}.
     */
    private String messageType;

    /**
     * Response code, only present in response message.
     */
    private int responseCode;

    /**
     * Message data. This is different according to different commands.
     */
    private byte[] messageData;

    /**
     * Extract response buffer to a response message.
     *
     * <p>An external package contains:</p>
     * <ul>
     *     <li>STX: 1 byte</li>
     *     <li>Length of the message type, separator and message data: 2 bytes</li>
     *     <li>Message type: 2 bytes</li>
     *     <li>Separator: 1 byte</li>
     *     <li>Message data</li>
     *     <li>ETX: 1 byte</li>
     *     <li>LRC: 1 byte</li>
     * </ul>
     *
     * <p>Example:</p>
     * <pre>
     *     // Clear screen, message type: 4e
     *     byte[] responseData = new byte[]{0x02, 0x00, 0x03, 0x34, 0x65, 0x2F, 0x03, 0x7E};
     *     try {
     *         ExternalMessage responseMessage = ExternalMessage.unpack(responseData);
     *     } catch(ExternalMessageException e) {
     *         // Handle the exception.
     *     }
     *
     * </pre>
     *
     * @param data
     * @return
     * @throws NSDKException
     */
    public static ExternalMessage unpack(byte[] data) throws NSDKException {
        if (data == null || data.length == 0) {
            throw new NSDKIllegalParameterException(ExternalErrorMessage.DATA_NULL_OR_EMPTY);
        }

        int offset = 0;
        byte stx = data[offset];
        offset += STX_FIELD_LEN;

        if (stx != STX) {
            throw new NSDKIllegalParameterException(String.format("Invalid STX(%02X).", stx));
        }

        if (data.length < MIN_MESSAGE_LEN) {
            throw new NSDKIllegalParameterException(ExternalErrorMessage.DATA_LENGTH_NOT_ENOUGH);
        }

        // Length of the message type, separator and message data(mac, ksn).
        byte[] lengthField = new byte[LENGTH_FIELD_LEN];
        System.arraycopy(data, offset, lengthField, 0, LENGTH_FIELD_LEN);
        int length = ExternalMessage.bcdBuffer2Int(lengthField);
        if (length != (data.length - STX_FIELD_LEN - LENGTH_FIELD_LEN - ETX_FIELD_LEN - LRC_FIELD_LEN)) {
            throw new NSDKIllegalParameterException("Length field is not matched with actual length.");
        }
        offset += LENGTH_FIELD_LEN;

        ExternalMessage responseMessage = new ExternalMessage();
        byte[] messageTypeBuf = new byte[2];
        System.arraycopy(data, offset, messageTypeBuf, 0, MESSAGE_TYPE_FIELD_LEN);
        offset += MESSAGE_TYPE_FIELD_LEN;
        offset += SEPARATOR_FIELD_LEN;
        if ("FFFF".equals(ISOUtils.hexString(messageTypeBuf))) {
            // 解析不支持的是哪个指令
            String messageType;
            try {
                messageType = new String(new byte[]{data[offset], data[offset + 1]});
                responseMessage.setMessageType(messageType);
                responseMessage.setResponseCode(-99999);
            } catch (Exception e) {
                e.printStackTrace();
                responseMessage.setMessageType("FF");
                responseMessage.setResponseCode(-99999);
            }
            offset += 2;
        } else {
            responseMessage.setMessageType(new String(messageTypeBuf));

            int messageDataLen;
            if (ExternalCommunicationManager.getInstance().isEnableDukptMac()) {
                messageDataLen = length - MESSAGE_TYPE_FIELD_LEN - SEPARATOR_FIELD_LEN - MAC_FIELD_LEN - KSN_FIELD_LEN;
            } else {
                messageDataLen = length - MESSAGE_TYPE_FIELD_LEN - SEPARATOR_FIELD_LEN;
            }
            if (messageDataLen > 0) {
                byte[] messageData = new byte[messageDataLen];
                System.arraycopy(data, offset, messageData, 0, messageDataLen);
                responseMessage.setMessageData(messageData);
                offset += messageDataLen;
            }

            if (ExternalCommunicationManager.getInstance().isEnableDukptMac()) {
                byte[] mac = new byte[MAC_FIELD_LEN];
                System.arraycopy(data, offset, mac, 0, MAC_FIELD_LEN);
                offset += MAC_FIELD_LEN;

                byte[] ksn = new byte[KSN_FIELD_LEN];
                System.arraycopy(data, offset, ksn, 0, KSN_FIELD_LEN);
                offset += KSN_FIELD_LEN;

                // message type, separator and message data
                byte[] tempMacData = new byte[length - MAC_FIELD_LEN - KSN_FIELD_LEN];
                System.arraycopy(data, 3, tempMacData, 0, tempMacData.length);
                ExternalCommunicationManager.getInstance().getDukptMacHandler().checkKsn(ksn, responseMessage.getMessageType());
                ExternalCommunicationManager.getInstance().getDukptMacHandler().checkMac(tempMacData, mac, responseMessage.getMessageType());
            }
        }

        byte lrc = data[data.length - 1];
        byte lrcTemp = calculateLrc(data, 1, offset);
        if (lrc != lrcTemp) {
            responseMessage.setResponseCode(ErrorCode.EXT_MESSAGE_INVALID_LRC);
            return responseMessage;
        }

        return responseMessage;
    }
    
    public static byte[] extractResponseData(byte[] data, ArrayList<ExternalMessage> messages) throws NSDKException {
        int offset = 0;
        byte[] restData = null;
//        LogUtils.d(TAG, "********** Extract data: " + ISOUtils.hexString(data));
        while (offset < data.length) {
            boolean isMessageBegin = false;
            for (int i = offset; i < data.length; i++) {
                boolean isSTXFound = (data[i] == ExternalMessage.STX);
                boolean isSeparatorFollowed = (i + 5 < data.length) && (data[i + 5] == SEPARATOR);
                if (isSTXFound && isSeparatorFollowed) {
//                    LogUtils.d(TAG, "********** Found STX and Separator, STX offset: " + i);
                    offset = i;
                    isMessageBegin = true;
                    break;
                }
            }

            if (isMessageBegin) {
                // STX is found in the data
                int lenFieldValue = ExternalMessage.bcdBuffer2Int(new byte[]{data[offset + 1], data[offset + 2]});
                int restDataLen = data.length - offset;
                int singleMessageDataLen = STX_FIELD_LEN + LENGTH_FIELD_LEN + lenFieldValue + ETX_FIELD_LEN + LRC_FIELD_LEN;
                if (singleMessageDataLen <= restDataLen) {
                    // The length from the STX is enough for a single message.
                    byte[] singleMessageData = new byte[singleMessageDataLen];
                    System.arraycopy(data, offset, singleMessageData, 0, singleMessageDataLen);
                    messages.add(ExternalMessage.unpack(singleMessageData));
                    offset += singleMessageDataLen;
                    // Update offset and it will continue to search for next STX from new offset.
//                    LogUtils.d(TAG, String.format("********** Got a single message: %s, offset: %d", ISOUtils.hexString(singleMessageData), offset));
                } else {
                    // The length from the STX is not enough for a single message, return these data for the next round of message extraction.
                    restData = new byte[restDataLen];
                    System.arraycopy(data, offset, restData, 0, restDataLen);
                    LogUtils.d(TAG, "********** Found STX and Separator, but data length not enough.");
                    LogUtils.d(TAG, "********** The remaining data1: " + ISOUtils.hexString(restData));
                    return restData;
                }
            } else {
                // There is no STX in the data from offset
                if (offset < data.length) {
                    // If offset is not the end of the data, return the rest data for the next round of message extraction.
                    int restDataLen = data.length - offset;
                    restData = new byte[restDataLen];
                    System.arraycopy(data, offset, restData, 0, restDataLen);
                    LogUtils.d(TAG, "********** The remaining data2: " + ISOUtils.hexString(restData));
                    return restData;
                }

                // offset is the end of the data, no data left
                return null;
            }
        }

        return restData;
    }

    /**
     * Calculate LRC from start index to end index by XOR'ing each byte.
     *
     * <p>Example:</p>
     * <pre>
     *     byte[] testBuf = new byte[]{0x11, 0x03, (byte) 0xF1, (byte)0x98, (byte) 0xE7, (byte) 0x99, 0x32};
     *
     *     try {
     *         // Calculate LRC from index 2 to index 5.
     *         byte result = ExternalMessage.calculateLrc(testBuf, 2, 5);
     *
     *         // Calculate LRC of whole buffer.
     *         result = ExternalMessage.calculateLrc(testBuf, 0, testBuf.length -1);
     *     } catch (NSDKException e) {
     *         // Handle the exception according to different exception types.
     *     }
     * </pre>
     *
     * @param data       The whole buffer.
     * @param startIndex Calculate LRC from this index of data.
     * @param endIndex   Calculate LRC up to this index of data.
     * @return LRC.
     * @throws NSDKException
     */
    public static byte calculateLrc(byte[] data, int startIndex, int endIndex) throws NSDKException {
        if (data == null || data.length == 0) {
            throw new NSDKIllegalParameterException("Not data to calculate LRC.");
        }

        if (startIndex > endIndex) {
            throw new NSDKIllegalParameterException("Start index should less than end index.");
        }

        if (endIndex >= data.length) {
            throw new NSDKIllegalParameterException("End index overflow.");
        }

        byte result = data[startIndex];
        for (int i = startIndex + 1; i <= endIndex; i++) {
            result = (byte) (result ^ data[i]);
        }

        return result;
    }

    /**
     * Check if data length is enough to get required field.
     *
     * @param offset        Start index of required field.
     * @param resultDataLen Total length of source data.
     * @param requiredLen   Length of required field.
     * @return
     */
    public static boolean isDataEnough(int offset, int resultDataLen, int requiredLen) {
        return offset + requiredLen <= resultDataLen;
    }

    public static boolean onlyOneByteResponse(String messageType, Byte expectedFunctionId) {
        switch (messageType) {
            case ExternalMessageType.BEEP_REQUEST:
            case ExternalMessageType.DISPLAY_TEXT_REQUEST:
            case ExternalMessageType.DISPLAY_IMAGE_REQUEST:
            case ExternalMessageType.UPDATE_APP_FW_REQUEST:
                return true;
            default:
                return false;
        }
    }

    /**
     * Convert an int value to a BCD buffer of 2 bytes. Only support int < 10000.
     *
     * <p>Example:</p>
     * <pre>
     *     int len = 205;
     *     byte[] result = toBcdBuffer(len);
     *     // The result will be {0x02, 0x05}.
     * </pre>
     *
     * @param data
     * @return
     */
    public static byte[] intToBcdBuffer(int data) throws NSDKIllegalParameterException {
        if (data < 0 || data > 9999) {
            throw new NSDKIllegalParameterException("Value shall be >=0 and <=9999.");
        }

        String dataStr = String.format("%4s", data).replace(' ', '0');
        return ISOUtils.hex2byte(dataStr);
    }

    /**
     * Convert an int value to a HEX buffer of 2 bytes. Only support int <= 0xFFFF, that is 65535.
     *
     * <p>Example:</p>
     * <pre>
     *   int data = 205;
     *   byte[] result = intToHexBuf(data);
     *   //The result will be { 0x00, 0xCD}.
     * </pre>
     *
     * @param data
     * @return
     */
    public static byte[] intToHexBuf(int data) throws NSDKIllegalParameterException {
        if (data < 0 || data > 0xFFFF) {
            throw new NSDKIllegalParameterException("Value shall be >=0 and <=65535(0xFFFF)");
        }

        byte[] targets = new byte[2];
        targets[0] = (byte) (data >> 8 & 0xFF);
        targets[1] = (byte) (data & 0xFF);
        return targets;
    }

    public static byte[] intTo4BytesHex(int i) throws NSDKIllegalParameterException {
        if (i < 0) {
            throw new NSDKIllegalParameterException("Value shall be >=0.");
        }
        byte[] arr = new byte[4] ;
        arr[0] = (byte)(i >> 24) ;
        arr[1] = (byte)(i >> 16) ;
        arr[2] = (byte)(i >> 8) ;
        arr[3] = (byte)i ;
        return arr ;
    }

    /**
     * Convert a buffer of 2 bytes to an int value.
     *
     * <p>Example:</p>
     * <pre>
     *     byte[] buf = new byte[]{0x02, 0x05};
     *     int result = bcdBuffer2Int(buf);
     *     // The result will be 205.
     *
     *    buf = null;
     *    result = bcdBuffer2Int(buf);
     *    //The result will be -1.
     *
     *    buf = new byte[0];
     *    result = bcdBuffer2Int(buf);
     *    //The result will be -1.
     *
     *    buf = new byte[]{0x02, 0x05, 0x12, 0x33};
     *    result = bcdBuffer2Int(buf);
     *    //The result will be 205, only take the first two bytes to calculate.
     * </pre>
     *
     * @param data
     * @return
     */
    public static int bcdBuffer2Int(byte[] data) {
        if (data == null || data.length < 2) {
            return -1;
        }
        String dataStr = ISOUtils.bcd2str(data, 0, 4, false);
        return Integer.parseInt(dataStr);
    }

    /**
     * Convert a buffer of 2 bytes to an int value.
     *
     * <p>Example:</p>
     * <pre>
     *    byte[] buf = new byte[]{0x00, 0xCD};
     *    int result = hexBuffer2Int(buf);
     *    //The result will be 205.
     *
     *    buf = null;
     *    result = hexBuffer2Int(buf);
     *    //The result will be -1.
     *
     *    buf = new byte[0];
     *    result = hexBuffer2Int(buf);
     *    //The result will be -1.
     *
     *    buf = new byte[]{0x00, 0xCD, 0x12, 0x33};
     *    result = hexBuffer2Int(buf);
     *    //The result will be 205, only take the first two bytes to calculate.
     * </pre>
     *
     * @param data
     * @return Int value of the hex buffer. Return -1 if the hex buffer is null or its length is less than 2.
     */
    public static int hexBuffer2Int(byte[] data) {
        if (data == null || data.length < 2) {
            return -1;
        }
        int high = data[0];
        int low = data[1];
        return (high << 8 & 0xFF00) | (low & 0xFF);
    }

    public static int hex2Int(byte[] data) {
        if (data == null || data.length != 4) {
            return -1;
        }

        int res = 0;

        res |= data[0] << 24 & 0xFF000000;
        res |= data[1] << 16 & 0xFF0000;
        res |= data[2] << 8 & 0xFF00;
        res |= data[3] & 0xFF;

        return res;
    }

    /**
     * Converts response code buffer to an int.
     *
     * @param responseCodeBuf 2 bytes of response code.
     * @return Int value of response code buffer.
     * @throws NSDKException
     */
    public static int convertResponseCodeBufToInt(byte[] responseCodeBuf) throws NSDKException {
        try {
            return Integer.parseInt(new String(responseCodeBuf));
        } catch (Exception e) {
            throw new NSDKExternalDeviceException(String.format("Unknown error code: %s", ISOUtils.hexString(responseCodeBuf)));
        }
    }

    public static int getInnerErrorCode(byte[] responseMessageData, int startIndex) {
        int innerErrorCode = ErrorCode.EXT_ERROR;
        if (responseMessageData == null || startIndex >= responseMessageData.length) {
            return innerErrorCode;
        }

        if (responseMessageData[startIndex] == ExternalMessage.MINUS) {
            try {
                byte[] innerErrorBuf = new byte[4];
                System.arraycopy(responseMessageData, startIndex + 1, innerErrorBuf, 0, innerErrorBuf.length);
                innerErrorCode = -(Integer.parseInt(ISOUtils.hexString(innerErrorBuf)));
                LogUtils.d(TAG, String.format("Inner error code is: %d", innerErrorCode));
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.d(TAG, String.format("Failed to extract inner error code, response message data is [%s]", ISOUtils.hexString(responseMessageData)));
            }
        }
        return innerErrorCode;
    }

    /**
     * Check if the message is correct.
     *
     * @param expectedType Expected message type.
     * @throws NSDKException
     */
    public void checkMessageType(String expectedType) throws NSDKException {
        if (expectedType == null || expectedType.length() == 0) {
            throw new NSDKIllegalParameterException(ExternalErrorMessage.DATA_NULL_OR_EMPTY);
        }

        if (!expectedType.equals(this.messageType)) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_INVALID_MESSAGE_TYPE,
                    String.format("Wrong response message type, it shall be %s, not %s ",
                            expectedType,
                            this.messageType));
        }
    }

    /**
     * Check if the function ID is correct.
     *
     * @param expectedFunctionId Expected function ID.
     * @throws NSDKException
     */
    public void checkFunctionId(byte expectedFunctionId) throws NSDKException {
        if (this.messageData == null || this.messageData.length == 0) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }

        if (expectedFunctionId != this.messageData[0]) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_INVALID_FUNCTION_ID,
                    String.format("Wrong response function ID, it shall be %02X, not %02X",
                            expectedFunctionId,
                            this.messageData[0]));
        }
    }

    /**
     * Pack an external request message to a buffer to send to external device.
     *
     * <p>An external message contains:</p>
     * <ul>
     *     <li>STX: 1 byte</li>
     *     <li>Length of the message type, separator and message data: 2 bytes</li>
     *     <li>Message type: 2 bytes</li>
     *     <li>Separator: 1 byte</li>
     *     <li>Message data</li>
     *     <li>ETX: 1 byte</li>
     *     <li>LRC: 1 byte</li>
     * </ul>
     *
     * <p>Example:</p>
     * <pre>
     *     ExternalMessage externalMessage = new ExternalMessage();
     *     externalMessage.setMessageType(ExternalMessageType.SCANNING_REQUEST);
     *     externalMessage.setMessageData(new byte[]{0x04, 0x00, 0x0A});
     *
     *     try {
     *         byte[] requestData = externalMessage.pack();
     *         // The result data is: 02 00 06 47 30 2F 04 00 0A 03 53
     *     } catch (NSDKException e) {
     *         // Handle the exception according to different exception types.
     *     }
     * </pre>
     *
     * @return
     * @throws NSDKException
     */
    public byte[] pack() throws NSDKException {
        if (this.messageType == null || this.messageType.isEmpty()) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_INVALID_MESSAGE_TYPE, "Invalid message type.");
        }

        int dataLen;
        if (this.messageData != null && this.messageData.length != 0) {
            dataLen = this.messageData.length;
        } else {
            dataLen = 0;
        }

        ByteArrayOutputStream messageStream = new ByteArrayOutputStream();

        try {
            // Set STX.
            messageStream.write(STX);

            // Set length of the message type, separator and message data.
            int lengthFieldValue;
            if (ExternalCommunicationManager.getInstance().isEnableDukptMac()) {
                lengthFieldValue = MESSAGE_TYPE_FIELD_LEN + SEPARATOR_FIELD_LEN + dataLen + MAC_FIELD_LEN + KSN_FIELD_LEN;
            } else {
                lengthFieldValue = MESSAGE_TYPE_FIELD_LEN + SEPARATOR_FIELD_LEN + dataLen;
            }

            if (lengthFieldValue > MAX_DATA_LEN) {
                throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_EXCEED_MAX_LENGTH, "Exceed max length.");
            }
            messageStream.write(ExternalMessage.intToBcdBuffer(lengthFieldValue));

            // Set message type.
            messageStream.write(this.messageType.getBytes());

            // Set separator.
            messageStream.write(SEPARATOR);

            // Set message data.
            if (dataLen > 0) {
                messageStream.write(this.messageData);
            }

            if (ExternalCommunicationManager.getInstance().isEnableDukptMac()) {
                byte[] tempData = messageStream.toByteArray();
                byte[] dataForMac = new byte[tempData.length - 3];
                System.arraycopy(tempData, 3, dataForMac, 0, dataForMac.length);
                byte[] mac = ExternalCommunicationManager.getInstance().getDukptMacHandler().generateMac(dataForMac);
                messageStream.write(mac);
                byte[] ksn = ExternalCommunicationManager.getInstance().getDukptMacHandler().getKsn();
                messageStream.write(ksn);
            }

            // Set ETX.
            messageStream.write(ETX);

            // The LRC is generated by XOR'ing all data bytes following the STX, up to and including the ETX.
            byte[] dataForLrc = messageStream.toByteArray();
            byte lrc = calculateLrc(dataForLrc, 1, dataForLrc.length - 1);

            // Set LRC.
            messageStream.write(lrc);

            return messageStream.toByteArray();
        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }
    }

    /**
     * Get message type.
     *
     * @return Message type. See {@link ExternalMessageType}
     */
    public String getMessageType() {
        return messageType;
    }

    /**
     * Set message type.
     *
     * @param messageType Message type. See {@link ExternalMessageType}.
     */
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    /**
     * Get response code.
     *
     * @return Response code. Only present in response message.
     */
    public int getResponseCode() {
        return responseCode;
    }

    /**
     * Set response code.
     *
     * @param responseCode Response code. Only present in response message.
     */
    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    /**
     * Get message data.
     *
     * @return Message data. This is different according to different commands.
     */
    public byte[] getMessageData() {
        return messageData;
    }

    /**
     * Set message data.
     *
     * @param messageData Message data. This is different according to different commands.
     */
    public void setMessageData(byte[] messageData) {
        this.messageData = messageData;
    }
}
