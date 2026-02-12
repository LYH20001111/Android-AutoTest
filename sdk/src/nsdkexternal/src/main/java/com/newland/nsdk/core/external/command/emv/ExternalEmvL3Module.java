package com.newland.nsdk.core.external.command.emv;

import static com.newland.nsdk.core.api.common.ErrorCode.EXT_MESSAGE_INVALID_MESSAGE_TYPE;

import android.util.Log;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.crypto.AlgorithmParameters;
import com.newland.nsdk.core.api.common.crypto.CipherType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.exception.NSDKTimeoutException;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.external.command.common.ExtToolUtils;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.external.command.emv.listener.ExtCandidateAID;
import com.newland.nsdk.core.external.command.emv.listener.ExtCompleteListener;
import com.newland.nsdk.core.external.command.emv.listener.ExtPerformListener;
import com.newland.nsdk.core.external.command.emv.listener.ExtTLVResult;
import com.newland.nsdk.core.external.command.emv.listener.TransactionResult;
import com.newland.nsdk.core.external.command.message.functionId.EmvFunctionId;
import com.newland.nsdk.core.api.external.exception.NSDKExternalDeviceException;
import com.newland.nsdk.core.external.command.exception.ExternalErrorMessage;
import com.newland.nsdk.core.external.command.exception.ExternalMessageException;
import com.newland.nsdk.core.external.command.message.ExternalMessage;
import com.newland.nsdk.core.external.command.message.ExternalMessageType;

import java.util.ArrayList;

/**
 * Provides EMV Level3 interface.
 */
public class ExternalEmvL3Module {
    private ExtPerformListener listener;
    private ExtCompleteListener cListener;
    private final static String TAG = "ExternalEmvL3Module";
    private boolean isWait = false;

    public ExternalEmvL3Module() {
    }

    /**
     * Initialize EMV Level3 module.
     *
     * @param configuration Configuration for initialization.
     * @throws NSDKException
     */
    public void initEMV(byte[] configuration) throws NSDKException {
        if (configuration == null || configuration.length != 8) {
            throw new NSDKIllegalParameterException("Invalid configuration. It shall be 8 bytes.");
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.EMV_REQUEST);

        // Request message data =  Function ID(1 byte) + Configuration(8 bytes)
        byte[] requestMessageData = new byte[9];
        requestMessageData[0] = EmvFunctionId.INIT_EMV;
        System.arraycopy(configuration, 0, requestMessageData, 1, configuration.length);
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.EMV_RESPONSE, EmvFunctionId.INIT_EMV);

        // Response message data = Function ID(1 byte) + Response code(2 bytes)
        ExternalEmvL3Utils.checkResponseCode(responseMessage);
    }

    /**
     * To set config
     *
     * @param opt
     * @param mode
     * @return
     */
    public void setConfig(int opt, byte mode) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.EMV_REQUEST);

        // Request message data =  Function ID(1 byte) + nMode(1 bytes) + Config(1 bytes)
        byte[] requestMessageData = new byte[3];
        requestMessageData[0] = EmvFunctionId.SET_CONFIG;
        requestMessageData[1] = mode;
        requestMessageData[2] = (byte) opt;
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.EMV_RESPONSE, EmvFunctionId.SET_CONFIG);

        // Response message data = Function ID(1 byte) + Response code(2 bytes)
        ExternalEmvL3Utils.checkResponseCode(responseMessage);
    }

    /**
     * To set config
     *
     * @param opt
     * @return
     */
    public boolean getConfig(int opt) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.EMV_REQUEST);

        // Request message data =  Function ID(1 byte) + Config(1 bytes)
        byte[] requestMessageData = new byte[2];
        requestMessageData[0] = EmvFunctionId.GET_CONFIG;
        requestMessageData[1] = (byte) opt;
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.EMV_RESPONSE, EmvFunctionId.GET_CONFIG);

        // Response message data = Function ID(1 byte) + Response code(2 bytes)
        ExternalEmvL3Utils.checkResponseCode(responseMessage);

        byte[] responseMessageData = responseMessage.getMessageData();
        System.out.println(ISOUtils.hexString(responseMessageData));
        if (responseMessageData[3] == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Set TLV value to the specified tag.
     *
     * @param tag   The tag to set.
     * @param value The tag's value.
     * @throws NSDKException
     */
    public void setData(int tag, byte[] value) throws NSDKException {
        if (value == null || value.length == 0) {
            throw new NSDKIllegalParameterException(ExternalErrorMessage.DATA_NULL_OR_EMPTY);
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.EMV_REQUEST);

        // Request message data =  Function ID(1 byte) + TLV data name(4 bytes) + TLV data value len(2 bytes) + TLV data value
        byte[] requestMessageData = new byte[1 + 4 + 2 + value.length];
        int offset = 0;
        requestMessageData[offset] = EmvFunctionId.SET_DATA;
        offset++;
        byte[] tagBuf = convertIntToHexBuffer(tag);
        System.arraycopy(tagBuf, 0, requestMessageData, offset, tagBuf.length);
        offset += 4;
        System.arraycopy(ExternalMessage.intToHexBuf(value.length), 0, requestMessageData, offset, 2);
        offset += 2;
        System.arraycopy(value, 0, requestMessageData, offset, value.length);
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.EMV_RESPONSE, EmvFunctionId.SET_DATA);

        // Response message data = Function ID(1 byte) + Response code(2 bytes)
        ExternalEmvL3Utils.checkResponseCode(responseMessage);
    }

    /**
     * Get TLV data.
     *
     * @param keyIndex   The key used to encrypt the command data. Only support Data Key Type and ID from 129~255.
     * @param l3DataType TLV data name or data type.
     * @param maxLen     The max tag values.
     * @return TLV data. See {@link ExtTLVResult}
     * @throws NSDKException
     */
    public ExtTLVResult getData(byte keyIndex, int l3DataType, int maxLen) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.EMV_REQUEST);

        // Request message data = Function ID(1 byte) + Key index(1 byte) + L3 data type(4 bytes) + Max len(2 bytes)
        byte[] requestMessageData = new byte[8];
        requestMessageData[0] = EmvFunctionId.GET_DATA;
        requestMessageData[1] = keyIndex;
        System.arraycopy(convertIntToHexBuffer(l3DataType), 0, requestMessageData, 2, 4);
        System.arraycopy(ExternalMessage.intToHexBuf(maxLen), 0, requestMessageData, 6, 2);
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.EMV_RESPONSE, EmvFunctionId.GET_DATA);

        // Response message data = Function ID(1 byte) + Response code(2 bytes) + Status(1 byte) + Actual data len(2 bytes) + TLV value len(2 bytes) + TLV data
        byte[] responseMessageData = responseMessage.getMessageData();
        ExternalEmvL3Utils.checkResponseCode(responseMessage);

        if (responseMessageData.length == 3) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }

        return extractTlvResult(keyIndex, responseMessageData);
    }

    public ExtTLVResult getData(SymmetricKey key, AlgorithmParameters params, int l3DataType, int maxLen) throws NSDKException {
        if(ExternalCommunicationManager.getInstance().isSupportCrypto()) {
            throw new NSDKExternalDeviceException(ErrorCode.NOT_SUPPORTED, ExternalErrorMessage.NOT_SUPPORTED);
        }

        byte keyID = 0;
        if(key != null) {
            keyID = key.getKeyID();
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.EMV_REQUEST);

        // Request message data = Function ID(1 byte) + Key index(1 byte) + L3 data type(4 bytes) + Max len(2 bytes)
        byte[] requestMessageData = new byte[8];
        requestMessageData[0] = EmvFunctionId.GET_DATA;
        requestMessageData[1] = keyID;
        System.arraycopy(convertIntToHexBuffer(l3DataType), 0, requestMessageData, 2, 4);
        System.arraycopy(ExternalMessage.intToHexBuf(maxLen), 0, requestMessageData, 6, 2);

        if(keyID != 0 && params != null) {
            ExtToolUtils.TLVPack tlvPack = ExtToolUtils.newTLVPack();
            CipherType cipherType = ExtToolUtils.combineCipherType(key, params);
            tlvPack.append(0xDF01, cipherType, key.getKeyUsage(), params.getPaddingMode(), params.getIV());
            byte[] tlvData = tlvPack.pack();
            byte[] requestPack = new byte[10 + tlvData.length];
            System.arraycopy(requestMessageData, 0, requestPack, 0, 8);
            System.arraycopy(ExternalMessage.intToHexBuf(tlvData.length), 0, requestPack, 8, 2);
            System.arraycopy(tlvData, 0, requestPack, 10, tlvData.length);
            requestMessage.setMessageData(requestPack);
        } else {
            requestMessage.setMessageData(requestMessageData);
        }

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.EMV_RESPONSE, EmvFunctionId.GET_DATA);

        // Response message data = Function ID(1 byte) + Response code(2 bytes) + Status(1 byte) + Actual data len(2 bytes) + TLV value len(2 bytes) + TLV data
        byte[] responseMessageData = responseMessage.getMessageData();
        ExternalEmvL3Utils.checkResponseCode(responseMessage);

        if (responseMessageData.length == 3) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }

        return extractTlvResult(keyID, responseMessageData);
    }

    /**
     * Set TLV list.
     *
     * @param tlvListData TLV list to set.
     * @throws NSDKException
     */
    public void setTlvListData(byte[] tlvListData) throws NSDKException {
        if (tlvListData == null || tlvListData.length == 0) {
            throw new NSDKIllegalParameterException(ExternalErrorMessage.DATA_NULL_OR_EMPTY);
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.EMV_REQUEST);

        // Request message data =  Function ID(1 byte) + TLV data value len(2 bytes) + TLV data value
        byte[] requestMessageData = new byte[1 + 2 + tlvListData.length];
        int offset = 0;
        requestMessageData[offset] = EmvFunctionId.SET_TLV_LIST_DATA;
        offset++;
        System.arraycopy(ExternalMessage.intToHexBuf(tlvListData.length), 0, requestMessageData, offset, 2);
        offset += 2;
        System.arraycopy(tlvListData, 0, requestMessageData, offset, tlvListData.length);
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.EMV_RESPONSE, EmvFunctionId.SET_TLV_LIST_DATA);

        // Response message data = Function ID(1 byte) + Response code(2 bytes)
        ExternalEmvL3Utils.checkResponseCode(responseMessage);
    }

    /**
     * Get TLV list data.
     *
     * @param keyIndex           The key used to encrypt the command data. Only support Data Key Type and ID from 129~255.
     * @param maxTlvListValueLen The length of TLV list data.
     * @param controlCode        When this parameter set to 1: Zero length of tag allowed.
     * @param tags               Tags.
     * @return TLV list data. See {@link ExtTLVResult}
     * @throws NSDKException
     */
    public ExtTLVResult getTlvListData(byte keyIndex, int maxTlvListValueLen, byte controlCode, int[] tags) throws NSDKException {
        if (tags == null || tags.length == 0) {
            throw new NSDKIllegalParameterException(ExternalErrorMessage.DATA_NULL_OR_EMPTY);
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.EMV_REQUEST);

        // Request message data = Function ID(1 byte) + Key index(1 byte) + Tag num(1 byte) + Max len(2 bytes)
        //                        + Control code(1 byte) + Tag list len(2 bytes) + Tag list
        byte[] tagsBuf = convertIntsToHexBuffer(tags);
        byte[] requestMessageData = new byte[8 + tagsBuf.length];
        int offset = 0;
        requestMessageData[offset] = EmvFunctionId.GET_TLV_LIST_DATA;
        offset++;
        requestMessageData[offset] = keyIndex;
        offset++;
        requestMessageData[offset] = (byte) tags.length;
        offset++;
        System.arraycopy(ExternalMessage.intToHexBuf(maxTlvListValueLen), 0, requestMessageData, offset, 2);
        offset += 2;
        requestMessageData[offset] = controlCode;
        offset++;
        System.arraycopy(ExternalMessage.intToHexBuf(tagsBuf.length), 0, requestMessageData, offset, 2);
        offset += 2;
        System.arraycopy(tagsBuf, 0, requestMessageData, offset, tagsBuf.length);

        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.EMV_RESPONSE, EmvFunctionId.GET_TLV_LIST_DATA);

        // Response message data = Function ID(1 byte) + Response code(2 bytes) + Status(1 byte) + Actual data len(2 bytes) + TLV value len(2 bytes) + TLV data
        byte[] responseMessageData = responseMessage.getMessageData();
        ExternalEmvL3Utils.checkResponseCode(responseMessage);

        if (responseMessageData.length == 3) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }

        return extractTlvResult(keyIndex, responseMessageData);
    }

    public ExtTLVResult getTlvListData(SymmetricKey key, AlgorithmParameters params, int maxTlvListValueLen, byte controlCode, int[] tags) throws NSDKException {
        if(ExternalCommunicationManager.getInstance().isSupportCrypto()) {
            throw new NSDKExternalDeviceException(ErrorCode.NOT_SUPPORTED, ExternalErrorMessage.NOT_SUPPORTED);
        }

        if (tags == null || tags.length == 0) {
            throw new NSDKIllegalParameterException(ExternalErrorMessage.DATA_NULL_OR_EMPTY);
        }

        byte keyID = 0;
        if(key != null) {
            keyID = key.getKeyID();
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.EMV_REQUEST);

        // Request message data = Function ID(1 byte) + Key index(1 byte) + Tag num(1 byte) + Max len(2 bytes)
        //                        + Control code(1 byte) + Tag list len(2 bytes) + Tag list
        byte[] tagsBuf = convertIntsToHexBuffer(tags);
        byte[] requestMessageData = new byte[8 + tagsBuf.length];
        int offset = 0;
        requestMessageData[offset] = EmvFunctionId.GET_TLV_LIST_DATA;
        offset++;
        requestMessageData[offset] = keyID;
        offset++;
        requestMessageData[offset] = (byte) tags.length;
        offset++;
        System.arraycopy(ExternalMessage.intToHexBuf(maxTlvListValueLen), 0, requestMessageData, offset, 2);
        offset += 2;
        requestMessageData[offset] = controlCode;
        offset++;
        System.arraycopy(ExternalMessage.intToHexBuf(tagsBuf.length), 0, requestMessageData, offset, 2);
        offset += 2;
        System.arraycopy(tagsBuf, 0, requestMessageData, offset, tagsBuf.length);

        if(keyID != 0 && params != null) {
            ExtToolUtils.TLVPack tlvPack = ExtToolUtils.newTLVPack();
            CipherType cipherType = ExtToolUtils.combineCipherType(key, params);
            tlvPack.append(0xDF01, cipherType, key.getKeyUsage(), params.getPaddingMode(), params.getIV());
            byte[] tlvData = tlvPack.pack();
            byte[] requestPack = new byte[10 + tagsBuf.length + tlvData.length];
            System.arraycopy(requestMessageData, 0, requestPack, 0, 8 + tagsBuf.length);
            System.arraycopy(ExternalMessage.intToHexBuf(tlvData.length), 0, requestPack, 8 + tagsBuf.length, 2);
            System.arraycopy(tlvData, 0, requestPack, 10 + tagsBuf.length, tlvData.length);
            requestMessage.setMessageData(requestPack);
        } else {
            requestMessage.setMessageData(requestMessageData);
        }

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.EMV_RESPONSE, EmvFunctionId.GET_TLV_LIST_DATA);

        // Response message data = Function ID(1 byte) + Response code(2 bytes) + Status(1 byte) + Actual data len(2 bytes) + TLV value len(2 bytes) + TLV data
        byte[] responseMessageData = responseMessage.getMessageData();
        ExternalEmvL3Utils.checkResponseCode(responseMessage);

        if (responseMessageData.length == 3) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }

        return extractTlvResult(keyID, responseMessageData);
    }

    /**
     * Set L2 kernel debug mode.
     *
     * @param mode L2 kernel debug mode.
     *             <ul>
     *             <li>0: CLOSE</li>
     *             <li>1: DEBUG</li>
     *             <li>3: ALL</li>
     *             </ul>
     * @throws NSDKException
     */
    public void setDebugMode(byte mode) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.EMV_REQUEST);

        // Request message data =  Function ID(1 byte) + Debug level(1 byte)
        byte[] requestMessageData = new byte[2];
        requestMessageData[0] = EmvFunctionId.SET_DEBUG_MODE;
        requestMessageData[1] = mode;
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.EMV_RESPONSE, EmvFunctionId.SET_DEBUG_MODE);

        // Response message data = Function ID(1 byte) + Response code(2 bytes)
        ExternalEmvL3Utils.checkResponseCode(responseMessage);
    }

    /**
     * Get the version of specified EMV L3 module.
     *
     * @param l3Module EMV L3 module.
     * @return The version of specified EMV L3 module.
     * @throws NSDKException
     */
    public String getVersion(int l3Module) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.EMV_REQUEST);

        // Request message data =  Function ID(1 byte) + L3 module(1 byte)
        byte[] requestMessageData = new byte[2];
        requestMessageData[0] = EmvFunctionId.GET_VERSION;
        requestMessageData[1] = (byte) l3Module;
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.EMV_RESPONSE, EmvFunctionId.GET_VERSION);

        // Response message data = Function ID(1 byte) + Response code(2 bytes) + Version len(1 byte) + Version
        byte[] responseMessageData = responseMessage.getMessageData();
        ExternalEmvL3Utils.checkResponseCode(responseMessage);

        if (responseMessageData.length == 3) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }

        int versionLen = responseMessageData[3] & 0xFF;
        String version = "";
        if (versionLen > 0) {
            if (versionLen > responseMessageData.length - 4) {
                throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LEN_FIELD_ERROR, ExternalErrorMessage.DATA_LEN_FIELD_ERROR);
            }

            byte[] versionBuf = new byte[versionLen];
            System.arraycopy(responseMessageData, 4, versionBuf, 0, versionLen);
            version = new String(versionBuf);
        }

        return version;
    }

    /**
     * Cancel a transaction.
     *
     * <p>Note: For now, only support to cancel a transaction before card is obtained.</p>
     *
     * @throws NSDKException
     */
    public void cancelTransaction() throws NSDKException {
        String commandString = "+++CANCEL";
        int commandStringLen = commandString.length();
        int dataLen = commandStringLen + ExternalMessage.ETX_FIELD_LEN + ExternalMessage.LRC_FIELD_LEN;
        byte[] data = new byte[dataLen];
        int offset = 0;
        System.arraycopy(commandString.getBytes(), 0, data, offset, commandStringLen);
        offset += commandStringLen;
        data[offset] = ExternalMessage.ETX;
        byte lrc = ExternalMessage.calculateLrc(data, 1, offset);
        offset++;
        data[offset] = lrc;

        ExternalCommunicationManager.getInstance().sendInterrupt(data);
    }

    /**
     * Perform a transaction.
     *
     * @param cardTypes   Card interfaces to search cards. Each bit of this parameter represents a card interface. If a bit is set, card reader will search for a card on that interface. Any of the bits can be set for a transaction.
     *                    <ul>
     *                         <li>Bit 1: Enable MSR Interface if set to 1.</li>
     *                         <li>Bit 2: Enable Contact Interface if set to 1.</li>
     *                         <li>Bit 3: Enable Contactless Interface if set to 1.</li>
     *                         <li>Bits 4-8: RFU. The RFU bits must be set to 0.</li>
     *                    </ul>*
     * @param timeout     Timeout in seconds.Card reader will search for a card on the specified interfaces within this timeout.
     * @param commandData Command data that used to perform a transaction.
     * @return Transaction result. See {@link TransactionResult}
     * @throws NSDKException
     */
    public TransactionResult performTransaction(int cardTypes, int timeout, byte[] commandData) throws NSDKException {
        if (this.listener == null) {
            throw new NSDKExternalDeviceException("Please set EMV callback first.");
        }

        if (timeout < 0) {
            throw new NSDKIllegalParameterException("Timeout shall be > 0.");
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.EMV_REQUEST);

        int commandDataLen = 0;
        if (commandData != null || commandData.length > 0) {
            commandDataLen = commandData.length;
        }

        // Request message data =  Function ID(1 byte) + Card interfaces(1 byte) + Timeout(4 bytes) + Command data len(2 bytes) + Command data
        byte[] requestMessageData = new byte[8 + commandDataLen];
        int offset = 0;
        requestMessageData[offset] = EmvFunctionId.PERFORM_TRANSACTION;
        offset++;
//        int type = 0;
//        for (CardType cardType : cardTypes) {
//            type |= cardType.ordinal();
//        }

        requestMessageData[offset] = (byte) cardTypes;
        offset++;
        System.arraycopy(convertIntToHexBuffer(timeout), 0, requestMessageData, offset, 4);
        offset += 4;
        System.arraycopy(ExternalMessage.intToHexBuf(commandDataLen), 0, requestMessageData, offset, 2);
        if (commandDataLen > 0) {
            offset += 2;
            System.arraycopy(commandData, 0, requestMessageData, offset, commandDataLen);
        }
        requestMessage.setMessageData(requestMessageData);

        synchronized (ExternalCommunicationManager.getInstance()) {
            ExternalCommunicationManager.getInstance().send(requestMessage);
            long startTime = System.currentTimeMillis();
            long totalTime = timeout * 1000;
            long remainTime;
            do {
                ArrayList<ExternalMessage> messages = ExternalCommunicationManager.getInstance().receiveMessages(100);
                if (messages == null || messages.size() == 0) {
                    remainTime = totalTime - (System.currentTimeMillis() - startTime);
                    if (remainTime > 0) {
                        continue;
                    }
                    continue;
                }
                for (ExternalMessage responseMessage : messages) {
                    // 先判断是否为不支持的指令，如果是不支持的指令，只会返回 FFFF 的消息类型，不会返回具体的错误码
                    if (responseMessage.getMessageType().equals(requestMessage.getMessageType()) && responseMessage.getResponseCode() == -99999) {
                        throw new NSDKExternalDeviceException(responseMessage.getResponseCode(), String.format("Unsupported command(%s).", responseMessage.getMessageType()));
                    }
                    try {
                        responseMessage.checkMessageType(ExternalMessageType.EMV_RESPONSE);
                    } catch (ExternalMessageException e) {
                        if(e.getCode() == ErrorCode.EXT_MESSAGE_INVALID_MESSAGE_TYPE){
                            continue;
                        } else {
                            throw e;
                        }
                    }
                    byte[] responseMessageData = responseMessage.getMessageData();
                    byte functionId = responseMessageData[0];

                    if (functionId == EmvFunctionId.PERFORM_TRANSACTION) {
                        ExternalEmvL3Utils.checkResponseCode(responseMessage);

                        if (responseMessageData.length == 3) {
                            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
                        }

                        int resultDataLen = responseMessageData.length - 3;
                        byte[] resultData = new byte[resultDataLen];
                        System.arraycopy(responseMessageData, 3, resultData, 0, resultDataLen);
                        ExternalCommunicationManager.getInstance().sendAck();
                        return unpack(resultData, true);
                    } else if (functionId == EmvFunctionId.CALLBACK) {
                        // Response message data = Function ID(1 byte) + Callback ID(1 byte) + Others
                        if (responseMessageData.length == 1) {
                            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
                        }

                        byte callbackId = responseMessageData[1];
                        int callbackDataLen = responseMessageData.length - 2;
                        if (callbackDataLen == 0) {
                            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
                        }

                        byte[] callbackData = new byte[callbackDataLen];
                        System.arraycopy(responseMessageData, 2, callbackData, 0, callbackDataLen);

                        extractCallbackRequestData(callbackId, callbackData);
                    } else if (functionId == EmvFunctionId.SEND_DEBUG_MESSAGE) {
                        ExternalEmvL3Utils.checkResponseCode(responseMessage);
                        if (responseMessageData.length == 3) {
                            continue;
                        }

                        int resultDataLen = responseMessageData.length - 3;
                        if (resultDataLen > 2) {
                            int logLen = ExternalMessage.hexBuffer2Int(new byte[]{responseMessageData[3], responseMessageData[4]});
                            if (logLen > responseMessageData.length - 5) {
                                logLen = responseMessageData.length - 5;
                            }
                            byte[] log = new byte[logLen];
                            System.arraycopy(responseMessageData, 5, log, 0, logLen);
                            printMessage(log);
                        } else {
                            byte[] resultData = new byte[resultDataLen];
                            System.arraycopy(responseMessageData, 3, resultData, 0, resultDataLen);
                            printMessage(resultData);
                        }
                    }
                }
                remainTime = totalTime - (System.currentTimeMillis() - startTime);
            } while (remainTime > 0);

            throw new NSDKTimeoutException();
        }
    }

    private void printMessage(byte[] msg) {
        LogUtils.d("EMVL3 Log", new String(msg));
    }

    public static TransactionResult unpack(byte[] resultData, boolean hasCmvStatus) throws NSDKException {
        if (resultData == null || resultData.length == 0) {
            throw new NSDKIllegalParameterException(ExternalErrorMessage.DATA_NULL_OR_EMPTY);
        }

        int resultDataLen = resultData.length;

        // Transaction result(1 byte) + Error code(4 byte) + Data status(1 byte) + Actual data len(2 bytes) + TLV list len(2 bytes) + TLV list
        TransactionResult result = new TransactionResult();
        int offset = 0;
        result.setResult(resultData[offset]);
        offset++;

        if (hasCmvStatus) {
            if (!ExternalMessage.isDataEnough(offset, resultDataLen, 1)) {
                return result;
            }
            result.setCVMStatus(resultData[offset]);
            offset++;
        }

        if (!ExternalMessage.isDataEnough(offset, resultDataLen, 4)) {
            return result;
        }

        byte[] errorCode = new byte[4];
        System.arraycopy(resultData, offset, errorCode, 0, 4);
        offset += 4;

        result.setErrorCode(ExternalMessage.hex2Int(errorCode));

        if (!ExternalMessage.isDataEnough(offset, resultDataLen, 1)) {
            return result;
        }
        result.setTLVDataStatus(resultData[offset]);
        offset++;

        if (!ExternalMessage.isDataEnough(offset, resultDataLen, 2)) {
            return result;
        }
        byte[] lenBuf = new byte[2];
        System.arraycopy(resultData, offset, lenBuf, 0, 2);
        result.setActualDataLen(ExternalMessage.hexBuffer2Int(lenBuf));
        offset += 2;

        if (!ExternalMessage.isDataEnough(offset, resultDataLen, 2)) {
            return result;
        }

        System.arraycopy(resultData, offset, lenBuf, 0, 2);
        int tlvListValueLen = ExternalMessage.hexBuffer2Int(lenBuf);
        offset += 2;
        if (tlvListValueLen > 0) {
            if (tlvListValueLen > resultDataLen - offset) {
                throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LEN_FIELD_ERROR, ExternalErrorMessage.DATA_LEN_FIELD_ERROR);
            }

            result.setTLVListData(new byte[tlvListValueLen]);
            System.arraycopy(resultData, offset, result.getTLVListData(), 0, tlvListValueLen);
        }

        return result;
    }

    /**
     * Complete a transaction when {@link #performTransaction} returns a status of Online Authorization Required.
     *
     * <p>The host device shall send the transaction online for authorization and respond the online result to external device to complete the transaction.</p>
     *
     * @param onlineResult Indicates whether the terminal was able to connect to the back-end host to complete the authorization or not.
     *                     <ul>
     *                     <li>0x00: Online Processing not completed. Could not connect to host or no response from host.</li>
     *                     <li>0x01: Online Processing with back-end host is completed.</li>
     *                     </ul>
     * @param commandData  The command data used for completing transaction.
     * @return Transaction result. See {@link TransactionResult}
     * @throws NSDKException
     */
    public TransactionResult completeTransaction(byte onlineResult, int timeout, byte[] commandData) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.EMV_REQUEST);

        // Request message data =  Function ID(1 byte) + Online result(1 byte) + Command data len(2 bytes) + Command data
        byte[] requestMessageData;
        int commandDataLen = 0;
        if (commandData != null && commandData.length > 0) {
            commandDataLen = commandData.length;
            requestMessageData = new byte[4 + commandDataLen];
        } else {
            requestMessageData = new byte[4];
        }
        requestMessageData[0] = EmvFunctionId.COMPLETE_TRANSACTION;
        requestMessageData[1] = onlineResult;
        System.arraycopy(ExternalMessage.intToHexBuf(commandDataLen), 0, requestMessageData, 2, 2);
        if (commandDataLen > 0) {
            System.arraycopy(commandData, 0, requestMessageData, 4, commandDataLen);
        }

        requestMessage.setMessageData(requestMessageData);

        synchronized (ExternalCommunicationManager.getInstance()) {
            ExternalCommunicationManager.getInstance().send(requestMessage);
            long startTime = System.currentTimeMillis();
            long totalTime = timeout * 1000;
            long remainTime;
            do {
                ArrayList<ExternalMessage> messages = ExternalCommunicationManager.getInstance().receiveMessages(100);
                if (messages == null || messages.size() == 0) {
                    remainTime = totalTime - (System.currentTimeMillis() - startTime);
                    if (remainTime > 0) {
                        continue;
                    }
                    continue;
                }
                for (ExternalMessage responseMessage : messages) {
                    // 先判断是否为不支持的指令，因为如果是不支持的指令，只会返回 FFFF 的消息类型，不会返回具体的错误码
                    if (responseMessage.getMessageType().equals(requestMessage.getMessageType()) && responseMessage.getResponseCode() == -99999) {
                        throw new NSDKExternalDeviceException(responseMessage.getResponseCode(), String.format("Unsupported command(%s).", responseMessage.getMessageType()));
                    }

                    responseMessage.checkMessageType(ExternalMessageType.EMV_RESPONSE);
                    byte[] responseMessageData = responseMessage.getMessageData();
                    byte functionId = responseMessageData[0];

                    if (functionId == EmvFunctionId.COMPLETE_TRANSACTION) {

                        ExternalEmvL3Utils.checkResponseCode(responseMessage);

                        if (responseMessageData.length == 3) {
                            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
                        }

                        int resultDataLen = responseMessageData.length - 3;
                        byte[] resultData = new byte[resultDataLen];
                        System.arraycopy(responseMessageData, 3, resultData, 0, resultDataLen);
                        ExternalCommunicationManager.getInstance().sendAck();
                        return unpack(resultData, true);
                    } else if (functionId == EmvFunctionId.CALLBACK) {
                        // Response message data = Function ID(1 byte) + Callback ID(1 byte) + Others
                        if (responseMessageData.length == 1) {
                            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
                        }

                        byte callbackId = responseMessageData[1];
                        int callbackDataLen = responseMessageData.length - 2;
                        if (callbackDataLen == 0) {
                            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
                        }

                        byte[] callbackData = new byte[callbackDataLen];
                        System.arraycopy(responseMessageData, 2, callbackData, 0, callbackDataLen);

                        extractCompleteCallbackData(callbackId, callbackData);
                    } else if (functionId == EmvFunctionId.SEND_DEBUG_MESSAGE) {
                        ExternalEmvL3Utils.checkResponseCode(responseMessage);
                        if (responseMessageData.length == 3) {
                            continue;
                        }

                        int resultDataLen = responseMessageData.length - 3;
                        if (resultDataLen > 2) {
                            int logLen = ExternalMessage.hexBuffer2Int(new byte[]{responseMessageData[3], responseMessageData[4]});
                            if (logLen > responseMessageData.length - 5) {
                                logLen = responseMessageData.length - 5;
                            }
                            byte[] log = new byte[logLen];
                            System.arraycopy(responseMessageData, 5, log, 0, logLen);
                            printMessage(log);
                        } else {
                            byte[] resultData = new byte[resultDataLen];
                            System.arraycopy(responseMessageData, 3, resultData, 0, resultDataLen);
                            printMessage(resultData);
                        }
                    }
                }
                remainTime = totalTime - (System.currentTimeMillis() - startTime);
            } while (remainTime > 0);

            throw new NSDKTimeoutException();
        }
    }

    private void extractCompleteCallbackData(byte callbackId, byte[] data) throws NSDKException {
        switch (callbackId) {
            case ExternalEmvCallbackID.UI_EVENT:
            case ExternalEmvCallbackID.HOST_UI_EVENT:
                byte uiEventId = data[0];

                byte[] uiEventData = null;
                int dataLen = ExternalMessage.hexBuffer2Int(new byte[]{data[1], data[2]});

                if (dataLen > 0) {
                    uiEventData = new byte[dataLen];
                    System.arraycopy(data, 3, uiEventData, 0, dataLen);
                }

                cListener.onUIEvent(uiEventId, uiEventData);
                if (callbackId == ExternalEmvCallbackID.HOST_UI_EVENT) {
                    responseEvent(0, new byte[4]);
                }
                break;
            default:
                throw new NSDKExternalDeviceException(String.format("Unsupported EMV callback(%d).", callbackId));
        }

    }

    /**
     * Terminate a transaction if it is not online after performing transaction.
     *
     * @param displayMessage Message that displayed when terminating transaction.
     * @param timeout        Time period to display message. Unit: second.
     * @throws NSDKException
     */
    public void terminateTransaction(String displayMessage, int timeout) throws NSDKException {
        if (timeout > 0xFFFF || timeout < 0) {
            throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_TIMEOUT_HEX);
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.EMV_REQUEST);

        // Request message data =  Function ID(1 byte) + Timeout(2 bytes) + Display data len(2 bytes) + Display data
        int displayMessageLen = 0;
        byte[] requestMessageData;
        if (displayMessage != null && displayMessage.length() > 0) {
            displayMessageLen = displayMessage.length();
            requestMessageData = new byte[5 + displayMessageLen];
        } else {
            requestMessageData = new byte[1];
        }
        requestMessageData[0] = EmvFunctionId.TERMINATE_TRANSACTION;
        if (displayMessageLen > 0) {
            System.arraycopy(ExternalMessage.intToHexBuf(timeout), 0, requestMessageData, 1, 2);
            System.arraycopy(ExternalMessage.intToHexBuf(displayMessageLen), 0, requestMessageData, 3, 2);
            System.arraycopy(displayMessage.getBytes(), 0, requestMessageData, 5, displayMessageLen);
        }
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.EMV_RESPONSE, EmvFunctionId.TERMINATE_TRANSACTION);

        // Response message data = Function ID(1 byte) + Response code(2 bytes)
        ExternalEmvL3Utils.checkResponseCode(responseMessage);
    }

    /**
     * Set callback to listen to EMV events.
     *
     * @param listener Listener which handles EMV events. See {@link ExtPerformListener}
     * @throws NSDKException
     */
    public synchronized void setCallback(ExtPerformListener listener) throws NSDKException {
        if (listener == null) {
            throw new NSDKIllegalParameterException("Listener shall not be null.");
        }
        this.listener = listener;
    }

    /**
     * Set callback to listen to EMV events.
     *
     * @param listener Listener which handles EMV events. See {@link ExtCompleteListener}
     * @throws NSDKException
     */
    public synchronized void setCallback(ExtCompleteListener listener) throws NSDKException {
        if (listener == null) {
            throw new NSDKIllegalParameterException("Listener shall not be null.");
        }
        this.cListener = listener;
    }

    /**
     * Pre process the transaction.
     *
     * @param commandData Command data in TLV format.
     * @return Error code of 4 bytes.
     * @throws NSDKException
     */
    public byte[] clssPreProcess(byte[] commandData) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.EMV_REQUEST);
        // Request message data = Function ID(1 byte) + Command data len(2 bytes) + Command data
        int dataLen = 3 + commandData.length;
        byte[] requestMessageData = new byte[dataLen];
        requestMessageData[0] = EmvFunctionId.TRANSACTION_PREPROCESS;
        System.arraycopy(ExternalMessage.intToHexBuf(commandData.length), 0, requestMessageData, 1, 2);
        System.arraycopy(commandData, 0, requestMessageData, 3, commandData.length);
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.EMV_RESPONSE, EmvFunctionId.TRANSACTION_PREPROCESS);

        // Response message data = Function ID(1 byte) + Response code(2 bytes) + Error code(4 bytes)
        byte[] responseMessageData = responseMessage.getMessageData();
        ExternalEmvL3Utils.checkResponseCode(responseMessage);

        if (responseMessageData.length < 7) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, ExternalErrorMessage.DATA_LENGTH_NOT_ENOUGH);
        }

        byte[] errorCode = new byte[4];
        System.arraycopy(responseMessageData, 3, errorCode, 0, errorCode.length);

        return errorCode;
    }

    public void responseEvent(int eventResult, byte[] data) throws NSDKException {
        LogUtils.d(TAG, "isWait=" + isWait);
        if (isWait) {
            isWait = false;
            ExternalMessage requestMessage = new ExternalMessage();
            requestMessage.setMessageType(ExternalMessageType.EMV_REQUEST);
            int dataLen = 0;
            if (data != null && data.length > 0) {
                dataLen = data.length;
            }

            // Request message data = Function ID(1 byte) +L3_Callback(1 byte) + result(4 byte)+ Command data len(4 bytes) + Command data
            byte[] requestMessageData = new byte[10 + dataLen];

            requestMessageData[0] = EmvFunctionId.CALLBACK;
            requestMessageData[1] = ExternalEmvCallbackID.CALLBACK_RESPONSE;

            byte[] resultBytes = convertIntToHexBuffer(eventResult);
            System.arraycopy(resultBytes, 0, requestMessageData, 2, resultBytes.length);

            byte[] dataLenBytes = convertIntToHexBuffer(dataLen);

            System.arraycopy(dataLenBytes, 0, requestMessageData, 6, dataLenBytes.length);

            if (dataLen > 0) {
                System.arraycopy(data, 0, requestMessageData, 10, data.length);
            }

            requestMessage.setMessageData(requestMessageData);

            ExternalCommunicationManager.getInstance().sendInterrupt(requestMessage.pack());
        }
    }

    private void extractCallbackRequestData(byte callbackId, byte[] data) throws NSDKException {
        switch (callbackId) {
            case ExternalEmvCallbackID.UI_EVENT:
            case ExternalEmvCallbackID.HOST_UI_EVENT:
                sendUIEvent(data);
                if (callbackId == ExternalEmvCallbackID.HOST_UI_EVENT) {
                    responseEvent(0, new byte[4]);
                }
                break;
            case ExternalEmvCallbackID.SELECT_CANDIDATE_LIST:
                isWait = true;
                sendCandidateAidListEvent(data);
                break;
            case ExternalEmvCallbackID.AFTER_FINAL_SELECT:
                isWait = true;
                sendFinalSelectEvent(data);
                break;
            case ExternalEmvCallbackID.CHECK_CREDENTIALS:
                isWait = true;
                sendCheckCredentialsEvent(data);
                break;
            case ExternalEmvCallbackID.PIN_ENTRY_DEAL:
                isWait = true;
                sendPinEntryDealEvent(data);
                break;
            case ExternalEmvCallbackID.CONFIRM_CARD_NUM:
                isWait = true;
                int len = data[0];
                byte[] pan = new byte[len];
                System.arraycopy(data, 1, pan, 0, len);
                String maskPAN = new String(pan);
                listener.onCardNumberConfirm(maskPAN);
                break;
            default:
                throw new NSDKExternalDeviceException(String.format("Unsupported EMV callback(%d).", callbackId));
        }

    }

    private void sendUIEvent(byte[] data) {
        byte uiEventId = data[0];

        byte[] uiEventData = null;
        int dataLen = ExternalMessage.hexBuffer2Int(new byte[]{data[1], data[2]});

        if (dataLen > 0) {
            uiEventData = new byte[dataLen];
            System.arraycopy(data, 3, uiEventData, 0, dataLen);
        }

        listener.onUIEvent(uiEventId, uiEventData);
    }

    private void sendCandidateAidListEvent(byte[] data) throws ExternalMessageException {
        ArrayList<ExtCandidateAID> aids = new ArrayList<>();
        byte aidCount = data[0];
        if (aidCount != 0) {
            if (data.length - 1 <= 2) {
                // No candidate AID data
                listener.onCandidateAIDList(aids);
            } else {
                int totalLen = data.length;
                int offset = 1;
                while (offset < totalLen) {
                    int aidBufLen = ExternalMessage.hexBuffer2Int(new byte[]{data[offset], data[offset + 1]});
                    offset += 2;
                    if (aidBufLen <= 0) {
                        continue;
                    }

                    if (!ExternalMessage.isDataEnough(offset, totalLen, aidBufLen)) {
                        throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LEN_FIELD_ERROR, ExternalErrorMessage.DATA_LEN_FIELD_ERROR);
                    }

                    byte[] aidBuf = new byte[aidBufLen];
                    System.arraycopy(data, offset, aidBuf, 0, aidBufLen);
                    aids.add(parseCandidateAID(aidBuf));
                    offset += aidBufLen;
                }


                listener.onCandidateAIDList(aids);
            }
        }

    }

    private ExtCandidateAID parseCandidateAID(byte[] aidBytes) {
        ExtCandidateAID candidateAID = new ExtCandidateAID();

        int current = 0;
        int tag = 0;
        int tagLen = 0;
        int lenValue = 0;

        while (current < aidBytes.length) {

            tagLen = getTagLen(aidBytes, current);
            byte[] tagBytes = new byte[tagLen];
            System.arraycopy(aidBytes, current, tagBytes, 0, tagLen);
            tag = bytesToInt(tagBytes);

            current += tagLen;
            if ((aidBytes[current] & 0x80) == 0x80) {
                int tmpLen = aidBytes[current] & 0x7F;
                switch (tmpLen) {
                    case 1:
                        lenValue = aidBytes[current + 1] & 0xFF;
                        break;
                    case 2:
                        lenValue = (aidBytes[current + 1] << 8) & 0xFF00 + (aidBytes[current + 2] & 0xFF);
                        break;
                    case 3:
                        lenValue = (aidBytes[current + 1] << 16) & 0xFF0000 + (aidBytes[current + 2] << 8) & 0xFF00 + (aidBytes[current + 3] & 0xFF);
                        break;
                }
                current += tmpLen + 1;
            } else {
                lenValue = aidBytes[current] & 0xFF;
                current += 1;
            }

            byte[] value = new byte[lenValue];
            System.arraycopy(aidBytes, current, value, 0, lenValue);
            current += lenValue;

            handleCandidateAID(candidateAID, value, tag);
        }

        return candidateAID;
    }

    private void handleCandidateAID(ExtCandidateAID candidateAID, byte[] value, int tag) {
        switch (tag) {
            case 0x9F40:
                candidateAID.setTerminalCodeTable(value);
                break;
            case 0x9F12:
                candidateAID.setPreferName(value);
                break;
            case 0x50:
                candidateAID.setLabel(value);
                break;
            case 0x87:
                candidateAID.setPriority(value[0]);
            case 0x4F:
                candidateAID.setAID(value);
            case 0x9F11:
                candidateAID.setIssuerCodeTableIndex(value[0]);
                break;
            case 0x5F2D:
                candidateAID.setLanguagePreference(value);
                break;
            case 0xDF37:
                candidateAID.setKernelID(value);
                break;
            case 0xDF65:
                candidateAID.setTerminalPriority(value[0]);
                break;
            case 0x1F811F:
                candidateAID.setCustomTagData(value);
                break;
        }
    }

    private int bytesToInt(byte[] bytes) {
        int result = 0;
        int end = bytes.length;

        for (int i = end - 1; i >= 0; --i) {
            byte b = bytes[i];
            int leftBit = (end - 1 - i) * 8;
            result |= (b & 255) << leftBit;
        }

        return result;
    }

    private int getTagLen(byte[] input, int offset) {
        int tagLen = 0;
        if((input[offset] & 0x1F) != 0x1F) {
            tagLen = 1;
        } else {
            int i = 1;
            for(i = 1; i < 4; i++) {
                if((input[offset + i] & 0x80) != 0x80) {
                    tagLen = i + 1;
                    break;
                }
            }
        }
        return tagLen;
    }

    private void sendFinalSelectEvent(byte[] data) throws ExternalMessageException {
        byte cardInterface = data[0];

        if (data.length < 3) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, ExternalErrorMessage.DATA_LENGTH_NOT_ENOUGH);
        }

        int aidLen = ExternalMessage.hexBuffer2Int(new byte[]{data[1], data[2]});
        byte[] aid = null;
        if (aidLen > 0) {
            aid = new byte[aidLen];
            if (aidLen > data.length - 3) {
                throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LEN_FIELD_ERROR, ExternalErrorMessage.DATA_LEN_FIELD_ERROR);
            }

            System.arraycopy(data, 3, aid, 0, aidLen);
        }

        listener.onFinalSelect(cardInterface, aid);
    }

    private void sendCheckCredentialsEvent(byte[] data) throws ExternalMessageException {
        if (data.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, ExternalErrorMessage.DATA_LENGTH_NOT_ENOUGH);
        }

        int offset = 0;

        // total len = type(4 byte) + number
        int totalLen = ExternalMessage.hexBuffer2Int(new byte[]{data[0], data[1]});
        offset += 2;

        if (totalLen > 0) {
            if (data.length < totalLen + 2) {
                throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_CORRECT, ExternalErrorMessage.DATA_LENGTH_NOT_CORRECT);
            }

            if (totalLen < 4) {
                throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, ExternalErrorMessage.DATA_LENGTH_NOT_ENOUGH);
            }

            // tag(2 bytes) + len(1 byte) + value(1 byte)
            byte[] tag9F62 = new byte[4];
            System.arraycopy(data, offset, tag9F62, 0, 4);
            offset += 4;
            byte type = tag9F62[3];

            int numberTlvLen = totalLen - 4;
            byte[] number = null;
            if (numberTlvLen > 0) {
                // tag(2 bytes) + len(1 byte) + value
                byte[] numberTlvBuf = new byte[numberTlvLen];
                System.arraycopy(data, offset, numberTlvBuf, 0, numberTlvLen);
                byte numberLen = numberTlvBuf[2];
                number = new byte[numberLen];
                System.arraycopy(numberTlvBuf, 3, number, 0, numberLen);
            }

            listener.onCredentialsCheck(type, number);
        }

    }

    private void sendPinEntryDealEvent(byte[] data) throws ExternalMessageException {
        byte pinType = data[0];
        byte[] tlv = null;
        if (data.length >= 3) {
            int tlvLen = ExternalMessage.hexBuffer2Int(new byte[]{data[1], data[2]});
            if (tlvLen > 0) {
                if (tlvLen > data.length - 3) {
                    throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LEN_FIELD_ERROR, ExternalErrorMessage.DATA_LEN_FIELD_ERROR);
                }

                tlv = new byte[tlvLen];
                System.arraycopy(data, 3, tlv, 0, tlvLen);
            }
        }

        listener.onPinEntry(pinType, tlv);
    }

    /**
     * Convert an int to a hex buffer of 4 bytes.
     *
     * <p>Example:</p>
     * <pre>
     *      int tag = 0x9F26;
     *      byte[] tagBuf = convertTagToHexBuffer(tag);
     *      // tagBuf is {0x00, 0x00, 0x9F, 0x26}.
     * </pre>
     *
     * @param data Int data.
     * @return
     */
    private byte[] convertIntToHexBuffer(int data) {
        return ISOUtils.hex2byte(String.format("%8s", Integer.toHexString(data)).replace(' ', '0'));
    }

    /**
     * Convert multiple ints to a hex buffer.
     *
     * <p>Example: </p>
     * <pre>
     *     int[] tags = new int[]{0x9F22, 0x9F8101, 0x9F33};
     *     byte[] tagsBuf = convertTagsToHexBuffer(tags);
     *     // tagsBuf is {0x9F, 0x22, 0x9F, 0x81, 0x01, 0x9F, 0x33}.
     * </pre>
     *
     * @param data Multiple ints.
     * @return
     * @throws NSDKIllegalParameterException
     */
    private byte[] convertIntsToHexBuffer(int[] data) throws NSDKIllegalParameterException {
        if (data == null || data.length == 0) {
            throw new NSDKIllegalParameterException(ExternalErrorMessage.DATA_NULL_OR_EMPTY);
        }

        StringBuilder sb = new StringBuilder();
        for (int t : data) {
            sb.append(Integer.toHexString(t));
        }
        return ISOUtils.hex2byte(sb.toString());
    }


    private ExtTLVResult extractTlvResult(byte keyIndex, byte[] responseMessageData) throws ExternalMessageException {
        ExtTLVResult tlvResult = new ExtTLVResult();
        tlvResult.setDataStatus(responseMessageData[3]);

        if (responseMessageData[3] == 0) {
            // Response message data = Function ID(1 byte) + Response code(2 bytes) + Status(1 byte) + Actual data len(2 bytes) + TLV value len(2 bytes) + TLV data
            if (keyIndex != 0) {
                if (responseMessageData.length < 8) {
                    throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, ExternalErrorMessage.DATA_LENGTH_NOT_ENOUGH);
                }

                byte[] dataLenBuf = new byte[2];
                dataLenBuf[0] = responseMessageData[4];
                dataLenBuf[1] = responseMessageData[5];
                int dataLen = ExternalMessage.hexBuffer2Int(dataLenBuf);

                dataLenBuf[0] = responseMessageData[6];
                dataLenBuf[1] = responseMessageData[7];
                int actualDataLen = ExternalMessage.hexBuffer2Int(dataLenBuf);
                tlvResult.setActualDataLen(actualDataLen);
                if (dataLen > 0) {
                    if (dataLen > responseMessageData.length - 8) {
                        throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LEN_FIELD_ERROR, ExternalErrorMessage.DATA_LEN_FIELD_ERROR);
                    }
                    byte[] tlvData = new byte[dataLen];
                    System.arraycopy(responseMessageData, 8, tlvData, 0, dataLen);
                    tlvResult.setData(tlvData);
                }
            } else {
                if (responseMessageData.length < 6) {
                    throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, ExternalErrorMessage.DATA_LENGTH_NOT_ENOUGH);
                }

                byte[] dataLenBuf = new byte[2];
                dataLenBuf[0] = responseMessageData[4];
                dataLenBuf[1] = responseMessageData[5];

                int actualDataLen = ExternalMessage.hexBuffer2Int(dataLenBuf);
                if (actualDataLen > 0) {
                    if (actualDataLen > responseMessageData.length - 6) {
                        throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LEN_FIELD_ERROR, ExternalErrorMessage.DATA_LEN_FIELD_ERROR);
                    }
                    byte[] tlvData = new byte[actualDataLen];
                    System.arraycopy(responseMessageData, 6, tlvData, 0, actualDataLen);
                    tlvResult.setData(tlvData);
                    tlvResult.setActualDataLen(actualDataLen);
                }
            }
        } else if(responseMessageData[3] == 3) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_ERROR, "Tlv result encryption error.");
        }
        return tlvResult;
    }
}
