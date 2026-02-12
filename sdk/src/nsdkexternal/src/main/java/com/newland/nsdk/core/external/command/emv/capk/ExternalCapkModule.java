package com.newland.nsdk.core.external.command.emv.capk;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.external.command.emv.capk.*;
import com.newland.nsdk.core.external.command.emv.ExternalEmvL3Utils;
import com.newland.nsdk.core.external.command.exception.ExternalErrorMessage;
import com.newland.nsdk.core.external.command.exception.ExternalMessageException;
import com.newland.nsdk.core.external.command.message.ExternalMessage;
import com.newland.nsdk.core.external.command.message.ExternalMessageType;
import com.newland.nsdk.core.external.command.message.functionId.EmvFunctionId;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Provides the ability to manage CAPK.
 */
public class ExternalCapkModule {
    /**
     * Load CAPK.
     *
     * @param capk The CAPK to update.
     * @throws NSDKException
     */
    public void loadCAPK(ExtCapkEntry capk) throws NSDKException {
        if (capk == null) {
            throw new NSDKIllegalParameterException("CAPK shall not be null");
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.EMV_REQUEST);

        byte[] capkBuf = pack(capk);
        // Request message data = Function ID(1 byte) + CAPK(284 bytes)
        byte[] requestMessageData = new byte[1 + capkBuf.length];
        requestMessageData[0] = EmvFunctionId.UPDATE_CAPK;
        System.arraycopy(capkBuf, 0, requestMessageData, 1, capkBuf.length);
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.EMV_RESPONSE, EmvFunctionId.UPDATE_CAPK);

        // Response message data = Function ID(1 byte) + Response code(2 bytes)
        ExternalEmvL3Utils.checkResponseCode(responseMessage);
    }

    /**
     * Get a CAPKEntry. A CAPKEntry can be identified by RID and index.
     *
     * @param rid   Get CAPKEntry according to this RID.
     * @param index Get CAPKEntry according to this index.
     * @return The CAPKEntry. Return null when error occurs(Invalid parameters or command failed).
     * @throws NSDKException
     */
    public ExtCapkEntry getCAPK(byte[] rid, int index) throws NSDKException {
        if (rid == null || rid.length != 5) {
            throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_RID);
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.EMV_REQUEST);

        // Request message data = Function ID(1 byte) + RID(5 bytes) + Index(1 byte)
        byte[] requestMessageData = new byte[7];
        requestMessageData[0] = EmvFunctionId.GET_CAPK;
        System.arraycopy(rid, 0, requestMessageData, 1, rid.length);
        requestMessageData[6] = (byte)index;

        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.EMV_RESPONSE, EmvFunctionId.GET_CAPK);

        // Response message data = Function ID(1 byte) + Response code(2 bytes) + CAPK data
        int expectedResponseDataLen = 1 + 2 + ExtCapkEntry.CAPK_LEN;
        byte[] responseMessageData = responseMessage.getMessageData();
        ExternalEmvL3Utils.checkResponseCode(responseMessage);

        if (responseMessageData.length != expectedResponseDataLen) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_CORRECT, ExternalErrorMessage.DATA_LENGTH_NOT_CORRECT);
        }

        byte[] capkData = new byte[ExtCapkEntry.CAPK_LEN];
        System.arraycopy(responseMessageData, 3, capkData, 0, ExtCapkEntry.CAPK_LEN);

        return unpack(capkData);
    }

    public static ExtCapkEntry unpack(byte[] data) throws NSDKException {
        if (data == null || data.length == 0) {
            throw new NSDKIllegalParameterException(ExternalErrorMessage.DATA_NULL_OR_EMPTY);
        }

        if (data.length != ExtCapkEntry.CAPK_LEN) {
            throw new NSDKIllegalParameterException(ExternalErrorMessage.DATA_LENGTH_NOT_CORRECT);
        }

        ExtCapkEntry capk = new ExtCapkEntry();
        int offset = 0;
        int modulusLen = data[248] & 0xFF;
        capk.setModulus(new byte[modulusLen]);
        System.arraycopy(data, offset, capk.getModulus(), 0, modulusLen);
        // modulus
        offset += 248;
        // modulus len
        offset++;

        capk.setExponent(new byte[3]);
        System.arraycopy(data, offset, capk.getExponent(), 0, capk.getExponent().length);
        offset += capk.getExponent().length;

        capk.setHash(new byte[20]);
        System.arraycopy(data, offset, capk.getHash(), 0, capk.getHash().length);
        offset += capk.getHash().length;

        capk.setExpiredDate(new byte[4]);
        System.arraycopy(data, offset, capk.getExpiredDate(), 0, capk.getExpiredDate().length);
        offset += capk.getExpiredDate().length;

        capk.setRid(new byte[5]);
        System.arraycopy(data, offset, capk.getRid(), 0, capk.getRid().length);
        offset += capk.getRid().length;

        capk.setIndex(data[offset]);
        offset++;
        capk.setAlgorithmIndicator(data[offset]);
        offset++;
        capk.setHashAlgorithm(data[offset]);

        return capk;
    }

    public static byte[] pack(ExtCapkEntry capk) throws NSDKException {
        if (capk.getModulus() == null || capk.getModulus().length > 248) {
            throw new NSDKIllegalParameterException("Please set correct modulus(<= 248 bytes).");
        }
        if (capk.getExponent() == null || capk.getExponent().length != 3) {
            throw new NSDKIllegalParameterException("Please set correct exponent(3 bytes).");
        }
        if (capk.getHash() == null || capk.getHash().length != 20) {
            throw new NSDKIllegalParameterException("Please set correct hash(20 bytes).");
        }

        //允许为空，不为空时，必须是4字节
        if (capk.getExpiredDate() != null && capk.getExpiredDate().length != 4) {
            throw new NSDKIllegalParameterException("Please set correct expired date(4 bytes).");
        }
        if (capk.getRid() == null || capk.getRid().length != 5) {
            throw new NSDKIllegalParameterException("Please set correct modulus(5 bytes).");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            if (capk.getModulus().length < 248) {
                byte[] modulusBuf = new byte[248];
                System.arraycopy(capk.getModulus(), 0, modulusBuf, 0, capk.getModulus().length);
                baos.write(modulusBuf,0,modulusBuf.length);
            } else {
                baos.write(capk.getModulus(),0,capk.getModulus().length);
            }

            baos.write((byte) capk.getModulus().length);
            baos.write(capk.getExponent(),0,capk.getExponent().length);
            baos.write(capk.getHash(),0,capk.getHash().length);

            if(capk.getExpiredDate() != null) {
                baos.write(capk.getExpiredDate(),0,capk.getExpiredDate().length);
            }else {
                baos.write(new byte[]{(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00});
            }

            baos.write(capk.getRid(),0,capk.getRid().length);
            baos.write((byte)capk.getIndex());
            baos.write(capk.getAlgorithmIndicator());
            baos.write(capk.getHashAlgorithm());
        }catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }

        return baos.toByteArray();
    }


    /**
     * Remove a CAPK.
     *
     * @param rid   Remove CAPK according to this RID.
     * @param index Remove CAPK according to this index.
     * @throws NSDKException
     */
    public void removeCAPK(byte[] rid, int index) throws NSDKException {
        if (rid == null || rid.length != 5) {
            throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_RID);
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.EMV_REQUEST);

        // Request message data = Function ID(1 byte) + RID(5 bytes) + Index(1 byte)
        byte[] requestMessageData = new byte[7];
        requestMessageData[0] = EmvFunctionId.REMOVE_A_CAPK;
        System.arraycopy(rid, 0, requestMessageData, 1, rid.length);
        requestMessageData[6] = (byte)index;

        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.EMV_RESPONSE, EmvFunctionId.REMOVE_A_CAPK);

        // Response message data = Function ID(1 byte) + Response code(2 bytes)
        ExternalEmvL3Utils.checkResponseCode(responseMessage);
    }

    /**
     * Remove all CAPKs.
     *
     * @throws NSDKException
     */
    public void removeAllCAPK() throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.EMV_REQUEST);

        // Request message data = Function ID(1 byte)
        byte[] requestMessageData = new byte[]{EmvFunctionId.REMOVE_ALL_CAPK};
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.EMV_RESPONSE, EmvFunctionId.REMOVE_ALL_CAPK);

        // Response message data = Function ID(1 byte) + Response code(2 bytes)
        ExternalEmvL3Utils.checkResponseCode(responseMessage);
    }

    /**
     * Get CAPKEntry List.
     *
     * @return The number of CAPK.
     */
    public ArrayList<ExtCapkEntry> getCAPKList() throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.EMV_REQUEST);
        requestMessage.setMessageData(new byte[]{EmvFunctionId.GET_CAPK_NUM});

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.EMV_RESPONSE, EmvFunctionId.GET_CAPK_NUM);

        // Response message data = Function ID(1 byte) + Response code(2 bytes) + CAPK num(2 bytes)
        byte[] responseMessageData = responseMessage.getMessageData();
        ExternalEmvL3Utils.checkResponseCode(responseMessage);

        if (responseMessageData.length < 5) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, ExternalErrorMessage.DATA_LENGTH_NOT_ENOUGH);
        }

        int capkNum = ExternalMessage.hexBuffer2Int(new byte[]{responseMessageData[3], responseMessageData[4]});
        if (capkNum <= 0) {
            return null;
        }

        if (((responseMessageData.length - 5) % 6) != 0) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, ExternalErrorMessage.DATA_LENGTH_NOT_ENOUGH);
        }

        byte[] capkBytes = new byte[responseMessageData.length - 5];
        System.arraycopy(responseMessageData,5,capkBytes,0,capkBytes.length);
        return parseCAPKList(capkBytes);
    }

    private ArrayList<ExtCapkEntry> parseCAPKList(byte[] capkBytes){
        int currentIndex = 0;
        ArrayList<ExtCapkEntry> capkList= new ArrayList<ExtCapkEntry>();

        while(currentIndex < capkBytes.length){
            ExtCapkEntry capk = new ExtCapkEntry();
            byte[] rid = new byte[5];
            System.arraycopy(capkBytes,currentIndex,rid,0,5);
            currentIndex += 5;
            capk.setRid(rid);

            capk.setIndex(capkBytes[currentIndex] & 0xFF);
            currentIndex += 1;

            capkList.add(capk);
        }

        return capkList;
    }
}
