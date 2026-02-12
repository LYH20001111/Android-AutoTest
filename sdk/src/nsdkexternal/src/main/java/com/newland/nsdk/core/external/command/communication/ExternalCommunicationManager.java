package com.newland.nsdk.core.external.command.communication;

import android.util.Log;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.external.communication.DukptMacHandler;
import com.newland.nsdk.core.api.external.communication.ExternalCommunicator;
import com.newland.nsdk.core.api.common.exception.NSDKCommunicationException;
import com.newland.nsdk.core.api.external.exception.NSDKExternalDeviceException;
import com.newland.nsdk.core.common.NSDKExecutors;
import com.newland.nsdk.core.common.Version;
import com.newland.nsdk.core.external.command.ExternalCommandConfig;
import com.newland.nsdk.core.external.command.communication.usbhost.USBCommunicator;
import com.newland.nsdk.core.external.command.exception.ExternalErrorMessage;
import com.newland.nsdk.core.external.command.exception.ExternalMessageException;
import com.newland.nsdk.core.external.command.message.ExternalMessage;
import com.newland.nsdk.core.external.command.message.ExternalMessageType;
import com.newland.nsdk.core.external.command.message.functionId.EmvFunctionId;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ExternalCommunicationManager {
    private static String TAG = "ExternalCommunicationManager";

    /**
     * Timeout for async methods, in case that stuck in synchronized codes. Unit: ms.
     */
    public static final int ASYNC_TIMEOUT = 60000;
    /**
     * 发送默认超时时间. Unit: ms.
     */
    public static final int SEND_TIMEOUT = 10000;
    /**
     * 接收默认超时时间. Unit: ms.
     */
    public static final int RECEIVE_TIMEOUT = 10000;

    private ExternalCommunicator currentCommunicator;

//    private ExtPerformTransactionListener emvL3Listener;

    private ExternalCommandConfig config;

    private boolean enableDukptMac;
    private DukptMacHandler dukptMacHandler;

    /**
     * Unit: ms
     */
    private int sendTimeout;
    /**
     * Unit: ms
     */
    private int receiveTimeout;
    /**
     * Unit: ms
     */
    private int asyncTimeout;

    ByteArrayOutputStream receiveStream = new ByteArrayOutputStream();

    private ExternalCommunicationManager() {
        sendTimeout = SEND_TIMEOUT;
        receiveTimeout = RECEIVE_TIMEOUT;
        asyncTimeout = ASYNC_TIMEOUT;
    }

    public static ExternalCommunicationManager getInstance() {
        return ExternalCommunicatorHolder.singletonHolder;
    }

    /**
     * Set to the specified communicator.
     *
     * @param communicator Required communicator.
     */
    public synchronized void setCommunicator(ExternalCommunicator communicator) {
        this.currentCommunicator = communicator;
    }

    public ExternalCommunicator getCommunicator(){
        return this.currentCommunicator;
    }

    /**
     * Send message without receiving data.
     *
     * <p>This only can be called in order. The second calling is not allowed until the first calling is finished.</p>
     *
     * @param message Message to send.
     * @throws NSDKException
     */
    public synchronized void send(ExternalMessage message) throws NSDKException {
        if (this.currentCommunicator == null) {
            throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_NOT_INITIALIZED, ExternalErrorMessage.COMMUNICATOR_NOT_INITIALIZED);
        }

        if (message == null) {
            throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_NO_DATA_TO_SEND, ExternalErrorMessage.NO_DATA_TO_SEND);
        }

        byte[] sendData = message.pack();

        LogUtils.d(TAG, String.format("********** before send data: %s", sendData == null ? "null" : ISOUtils.hexString(sendData)));
        this.currentCommunicator.send(sendData, sendTimeout);
        LogUtils.d(TAG, "********** After send data.");
    }

    public synchronized ExternalMessage sendAndReceiveSync(ExternalMessage message, String expectedMessageType, Byte expectedFunctionId) throws NSDKException {
        return this.sendAndReceive(message, expectedMessageType, expectedFunctionId, 0);
    }

    public synchronized ExternalMessage sendAndReceiveAsync(ExternalMessage message, String expectedMessageType, Byte expectedFunctionId) throws NSDKException {
        return this.sendAndReceive(message, expectedMessageType, expectedFunctionId, asyncTimeout);
    }

    /**
     * Send and receive data.
     *
     * <p>This only can be called in order. The second calling is not allowed until the first calling is finished.</p>
     *
     * @param requestMessage      Message to send.
     * @param expectedMessageType Expected response message type.
     * @param expectedFunctionId  Expected response function id.
     * @param commandTimeout             Unit: ms
     * @return Response message.
     * @throws NSDKException
     */
    public synchronized ExternalMessage sendAndReceive(ExternalMessage requestMessage, String expectedMessageType, Byte expectedFunctionId, int commandTimeout) throws NSDKException {
        if (this.currentCommunicator == null) {
            throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_NOT_INITIALIZED, ExternalErrorMessage.COMMUNICATOR_NOT_INITIALIZED);
        }

        if (requestMessage == null) {
            throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_NO_DATA_TO_SEND, ExternalErrorMessage.NO_DATA_TO_SEND);
        }

        byte[] sendData = requestMessage.pack();
        LogUtils.d(TAG, String.format("**********###### Send data: %s", sendData == null ? "null" : ISOUtils.hexString(sendData)));
        this.currentCommunicator.send(sendData, sendTimeout);
        return receiveMessage(requestMessage, expectedMessageType, expectedFunctionId, commandTimeout + receiveTimeout);
    }

    /**
     * @param message
     * @param expectedMessageType
     * @param expectedFunctionId
     * @param timeout             Unit: ms
     * @return
     * @throws NSDKException
     */
    public synchronized ExternalMessage receiveMessage(ExternalMessage message, String expectedMessageType, Byte expectedFunctionId, int timeout) throws NSDKException {
        long startTime = System.currentTimeMillis();
        long remainTime;
        ArrayList<ExternalMessage> matchedMessages = new ArrayList<>();
        do {
            byte[] responseData = this.currentCommunicator.receive(50);
            ArrayList<ExternalMessage> messages = new ArrayList<>();
            if (responseData == null || responseData.length == 0) {
                remainTime = timeout - (System.currentTimeMillis() - startTime);
                if (remainTime > 0) {
                    continue;
                }

                throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
            }
            LogUtils.d(TAG, String.format("********** Receive data: %s", ISOUtils.hexString(responseData)));
            try {
                if (ExternalMessage.onlyOneByteResponse(message.getMessageType(), expectedFunctionId)) {
                    ExternalMessage responseMessage = new ExternalMessage();
                    if (responseData[0] == ExternalMessage.NAK || responseData[0] == ExternalMessage.ACK) {
                        responseMessage.setMessageData(responseData[0] == ExternalMessage.NAK ? "01".getBytes() : "00".getBytes());
                        if (responseData.length > 1) {
                            byte[] remainData = new byte[responseData.length - 1];
                            System.arraycopy(responseData, 1, remainData, 0, remainData.length);
                            receiveStream.write(remainData);
                        }
                        return responseMessage;
                    }
                }

                receiveStream.write(responseData);
                byte[] restData = ExternalMessage.extractResponseData(receiveStream.toByteArray(), messages);
                LogUtils.d(TAG, String.format("********** restData: %s", restData == null ? "null" : ISOUtils.hexString(restData)));
                receiveStream.reset();
                if (restData != null) {
                    receiveStream.write(restData);
                }
                Log.d(TAG, "message.size:" + messages.size());
                if (messages.size() > 0) {
                    for (ExternalMessage m : messages) {
                        if (m.getMessageType().equals(message.getMessageType())) {
                            if (m.getResponseCode() == -99999) {
                                // 不支持的指令，目前还不支持 function id 的判断，所以只能判断到消息类型是否匹配
                                matchedMessages.add(m);
                                continue;
                            }
                        }

                        byte[] responseMessageData = m.getMessageData();
                        boolean isEmvLog = ExternalMessageType.EMV_RESPONSE.equals(m.getMessageType()) && (m.getMessageData() != null && responseMessageData[0] == EmvFunctionId.SEND_DEBUG_MESSAGE);
                        if (isEmvLog) {
                            sendEmvLog(responseMessageData);
                        }

                        if (m.getMessageType().equals(expectedMessageType)) {
                            if (expectedFunctionId != null) {
                                if (m.getMessageData() != null && responseMessageData[0] == expectedFunctionId) {
                                    LogUtils.d(TAG, String.format("**********&&&&&&&& Matched message type: %s, function ID: %02X", expectedMessageType, expectedFunctionId));
                                    matchedMessages.add(m);
                                    continue;
                                }
                            } else {
                                LogUtils.d(TAG, "**********&&&&&&&& Matched message type: " + expectedMessageType);
                                matchedMessages.add(m);
                                continue;
                            }
                        }
                    }

                    if (matchedMessages.size() >= 1) {
                        // Return the last matched message
                        sendAck();

                        //USB配置后，会重新开启usb导致指令接收失败,因此nsdk也需要重启
                        if(ExternalMessageType.CONFIGURATION_LOAD_RESPONSE.equals(expectedMessageType)
                                && currentCommunicator instanceof USBCommunicator){
                            try {
                                Thread.sleep(1000);
                                ((USBCommunicator)currentCommunicator).close(100);
                                ((USBCommunicator)currentCommunicator).open(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        ExternalMessage msg = matchedMessages.get(matchedMessages.size() - 1);
                        if(msg.getResponseCode() == ErrorCode.EXT_MESSAGE_INVALID_LRC) {
                            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_INVALID_LRC, "LRC is not correct.");
                        }
                        if (msg.getResponseCode() == -99999) {
                            throw new NSDKExternalDeviceException(msg.getResponseCode(), String.format("Unsupported command(%s).", msg.getMessageType()));
                        }

                        return msg;
                    }
                }

                remainTime = timeout - (System.currentTimeMillis() - startTime);
            } catch (IOException e) {
                throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
            }
        } while (remainTime > 0);
        LogUtils.d(TAG, "**********&&&&&&&& Timeout ");
        throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_RECEIVE_DATA_TIMEOUT, "Receive data timeout.");
    }

    public synchronized ExternalMessage sendAndReceiveACK(ExternalMessage requestMessage) throws NSDKException{
        if (this.currentCommunicator == null) {
            throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_NOT_INITIALIZED, ExternalErrorMessage.COMMUNICATOR_NOT_INITIALIZED);
        }

        if (requestMessage == null) {
            throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_NO_DATA_TO_SEND, ExternalErrorMessage.NO_DATA_TO_SEND);
        }

        byte[] sendData = requestMessage.pack();
        this.currentCommunicator.send(sendData, sendTimeout);
        byte[] receiveData = this.currentCommunicator.receive(500);
        ExternalMessage responseMessage = new ExternalMessage();
        Log.d(TAG, "receiveData.length:" + receiveData.length + "receiveData[0]:" + receiveData[0]);
        if (receiveData.length == 1 && receiveData[0] == ExternalMessage.ACK) {
            Log.d(TAG, "enter here");
            responseMessage.setResponseCode(ErrorCode.OK);
        } else {
            responseMessage.setResponseCode(15);
        }
        return responseMessage;
    }

    /**
     * @param timeout Unit: ms
     * @return
     * @throws NSDKException
     */
    public synchronized ArrayList<ExternalMessage> receiveMessages(int timeout) throws NSDKException {
        long startTime = System.currentTimeMillis();
        long remainTime;
        ArrayList<ExternalMessage> messages = new ArrayList<>();
        do {
            byte[] responseData = this.currentCommunicator.receive(50);
            LogUtils.d(TAG, String.format("********** Receive data: %s", responseData == null ? "null" : ISOUtils.hexString(responseData)));
            if (responseData == null || responseData.length == 0) {
                remainTime = timeout - (System.currentTimeMillis() - startTime);
                if (remainTime > 0) {
                    continue;
                }

                return messages;
            }
            try {
                receiveStream.write(responseData);
                byte[] restData = ExternalMessage.extractResponseData(receiveStream.toByteArray(), messages);
                LogUtils.d(TAG, String.format("********** restData: %s", restData == null ? "null" : ISOUtils.hexString(responseData)));
                receiveStream.reset();
                if (restData != null) {
                    receiveStream.write(restData);
                }
                if (messages.size() > 0) {
                    break;
                }

                remainTime = timeout - (System.currentTimeMillis() - startTime);
            } catch (IOException e) {
                throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
            }
        } while (remainTime > 0);
        return messages;
    }

    public synchronized void sendAck() throws NSDKException {
        if (this.currentCommunicator == null) {
            throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_NOT_INITIALIZED, ExternalErrorMessage.COMMUNICATOR_NOT_INITIALIZED);
        }
        this.currentCommunicator.send(new byte[]{ExternalMessage.ACK}, sendTimeout);
        LogUtils.d(TAG, "********** ACK is sent.");
    }

    /**
     * Send interrupt request.
     *
     * <p>This can be called before previous command returned. E.g., cancel request, EMV callback response.</p>
     *
     * @param data Data to send.
     * @throws NSDKException
     */
    public void sendInterrupt(final byte[] data) throws NSDKException {
        if (this.currentCommunicator == null) {
            throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_NOT_INITIALIZED, ExternalErrorMessage.COMMUNICATOR_NOT_INITIALIZED);
        }

        if (data == null) {
            throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_NO_DATA_TO_SEND, ExternalErrorMessage.NO_DATA_TO_SEND);
        }

        NSDKExecutors.threadStart(new Runnable() {
            @Override
            public void run() {
                try {
                    LogUtils.d(TAG, String.format("********** Before send interrupt data: %s", data == null ? "null" : ISOUtils.hexString(data)));
                    if (currentCommunicator instanceof CommunicatorExtension) {
                        ((CommunicatorExtension) currentCommunicator).sendInterrupt(data, sendTimeout);
                    } else {
                        currentCommunicator.send(data, sendTimeout);
                    }

                    LogUtils.d(TAG, String.format("********** After send interrupt data: %s", data == null ? "null" : ISOUtils.hexString(data)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

//    public ExternalEmvL3Listener getEmvL3Listener() {
//        return emvL3Listener;
//    }

//    public void setEmvL3Listener(ExtPerformTransactionListener emvL3Listener) {
//        this.emvL3Listener = emvL3Listener;
//    }

    private void sendEmvLog(byte[] responseMessageData) {
//        if (emvL3Listener == null) {
//            return;
//        }

        int resultDataLen = responseMessageData.length - 3;
        if (resultDataLen > 2) {
            int logLen = ExternalMessage.hexBuffer2Int(new byte[]{responseMessageData[3], responseMessageData[4]});
            if (logLen > responseMessageData.length - 5) {
                logLen = responseMessageData.length - 5;
            }
            byte[] log = new byte[logLen];
            System.arraycopy(responseMessageData, 5, log, 0, logLen);
            // todo 内部处理
            LogUtils.d(TAG, new String(log));
//            emvL3Listener.debugMessage(log);
        } else {
            byte[] resultData = new byte[resultDataLen];
            System.arraycopy(responseMessageData, 3, resultData, 0, resultDataLen);
            // todo 内部处理
//            emvL3Listener.debugMessage(resultData);
            LogUtils.d(TAG, "EMVL3 log data: " + ISOUtils.hexString(resultData));
        }
    }

    public int getReceiveTimeout() {
        return receiveTimeout;
    }

    public void setReceiveTimeout(int receiveTimeout) {
        this.receiveTimeout = receiveTimeout;
    }

    public int getSendTimeout() {
        return sendTimeout;
    }

    public void setSendTimeout(int sendTimeout) {
        this.sendTimeout = sendTimeout;
    }

    public int getAsyncTimeout() {
        return asyncTimeout;
    }

    public void setAsyncTimeout(int asyncTimeout) {
        this.asyncTimeout = asyncTimeout;
    }

    private static class ExternalCommunicatorHolder {
        private static ExternalCommunicationManager singletonHolder = new ExternalCommunicationManager();
    }

    public ExternalCommandConfig getConfig() {
        return config;
    }

    public void setConfig(ExternalCommandConfig config) {
        this.config = config;
    }

    public boolean isEnableDukptMac() {
        return enableDukptMac;
    }

    public void setEnableDukptMac(boolean enableDukptMac) {
        this.enableDukptMac = enableDukptMac;
    }

    public DukptMacHandler getDukptMacHandler() {
        return dukptMacHandler;
    }

    public void setDukptMacHandler(DukptMacHandler dukptMacHandler) {
        this.dukptMacHandler = dukptMacHandler;
    }

    public boolean isSupportCrypto() {
        Version version = Version.getVersion(config.getVersion());
        return version.isLower(7, 0, 29);
    }
}
