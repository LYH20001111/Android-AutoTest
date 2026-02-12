package com.newland.nsdk.core.external.command.emv.aid;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.external.command.emv.ExternalEmvL3Utils;
import com.newland.nsdk.core.external.command.emv.aid.*;
import com.newland.nsdk.core.external.command.exception.ExternalErrorMessage;
import com.newland.nsdk.core.external.command.exception.ExternalMessageException;
import com.newland.nsdk.core.external.command.message.ExternalMessage;
import com.newland.nsdk.core.external.command.message.ExternalMessageType;
import com.newland.nsdk.core.external.command.message.functionId.EmvFunctionId;

import java.util.ArrayList;

/**
 * Provides the ability to manage AID configuration.
 */
public class ExternalAidModule {
    /**
     * Update AID configuration.
     *
     * @param cardType Indicates which card interface needs to update configuration.
     *                      <ul>
     *                          <li>0x01: Contact card.</li>
     *                          <li>0x02: Contactless card.</li>
     *                      </ul>
     * @param tlvListData   Terminal configuration in TLV list format.
     * @throws NSDKException
     */
    public void updateAIDConfiguration(int cardType, byte[] tlvListData) throws NSDKException {
        if (tlvListData == null || tlvListData.length == 0) {
            throw new NSDKIllegalParameterException("No configuration to update.");
        }

//        if (cardType == CardType.MAG_CARD) {
//            throw new NSDKIllegalParameterException("CardType error.");
//        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.EMV_REQUEST);

        // Request message data =  Function ID(1 byte) + Card interface(1 byte) + TLV len(2 bytes) + TLV data
        byte[] requestMessageData = new byte[1 + 1 + 2 + tlvListData.length];
        int offset = 0;
        requestMessageData[offset] = EmvFunctionId.UPDATE_AID_CONFIG;
        offset++;
        requestMessageData[offset] = (byte) cardType;
        offset++;
        System.arraycopy(ExternalMessage.intToHexBuf(tlvListData.length), 0, requestMessageData, offset, 2);
        offset += 2;
        System.arraycopy(tlvListData, 0, requestMessageData, offset, tlvListData.length);
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.EMV_RESPONSE, EmvFunctionId.UPDATE_AID_CONFIG);

        // Response message data = Function ID(1 byte) + Response code(2 bytes)
        ExternalEmvL3Utils.checkResponseCode(responseMessage);
    }

    /**
     * Get AID configuration.
     *
     * @param cardType     Indicates which card interface to get AID configuration.
     *                          <ul>
     *                              <li>0x01: Contact card.</li>
     *                              <li>0x02: Contactless card.</li>
     *                          </ul>
     * @return The AID configuration. Return null when error occurs(Invalid parameters or command failed).
     */
    public byte[] getAIDConfiguration(int cardType, ExtAIDEntry aidEntry) throws NSDKException {
        if (aidEntry.getAid() == null || aidEntry.getAid().length > 16 || aidEntry.getKernelId() == null || aidEntry.getKernelId().length > 8) {
            throw new NSDKIllegalParameterException("Please set correct AID(<= 16 bytes) and kernel ID(8 bytes).");
        }

//        if (cardType == CardType.MAG_CARD) {
//            throw new NSDKIllegalParameterException("CardType error.");
//        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.EMV_REQUEST);

        // Request message data = Function ID(1 byte) + Card interface(1 byte) + AID entry(27 bytes)
        byte[] requestMessageData = new byte[29];
        int offset = 0;
        requestMessageData[offset] = EmvFunctionId.GET_AID_CONFIG;
        offset++;
        requestMessageData[offset] = (byte) cardType;
        offset++;
        packAidEntry(aidEntry, requestMessageData, offset);

        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.EMV_RESPONSE, EmvFunctionId.GET_AID_CONFIG);

        // Response message data = Function ID(1 byte) + Response code(2 bytes) + TLV len(2 bytes) + TLV list data
        byte[] responseMessageData = responseMessage.getMessageData();
        ExternalEmvL3Utils.checkResponseCode(responseMessage);

        if (responseMessageData.length < 5) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }

        byte[] tlvDataLenBuf = new byte[2];
        tlvDataLenBuf[0] = responseMessageData[3];
        tlvDataLenBuf[1] = responseMessageData[4];
        int tlvListDataLen = ExternalMessage.hexBuffer2Int(tlvDataLenBuf);
        byte[] tlvListData = null;

        if (tlvListDataLen > 0) {
            if (tlvListDataLen > responseMessageData.length - 5) {
                throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LEN_FIELD_ERROR, ExternalErrorMessage.DATA_LEN_FIELD_ERROR);
            }

            tlvListData = new byte[tlvListDataLen];
            System.arraycopy(responseMessageData, 5, tlvListData, 0, tlvListDataLen);
        }

        return tlvListData;
    }

    /**
     * Remove AID configuration.
     *
     * @param cardType     Indicates which card interface to remove AID configuration.
     *                          <ul>
     *                              <li>0x01: Contact card.</li>
     *                              <li>0x02: Contactless card.</li>
     *                          </ul>
     * @throws NSDKException
     */
    public void removeAID(int cardType, ExtAIDEntry aidEntry) throws NSDKException {
        if (aidEntry.getAid() == null || aidEntry.getAid().length > 16 || aidEntry.getKernelId() == null || aidEntry.getKernelId().length > 8) {
            throw new NSDKIllegalParameterException("Please set correct AID(<= 16 bytes) and kernel ID(8 bytes).");
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.EMV_REQUEST);

        // Request message data = Function ID(1 byte) + Card interface(1 byte) + AID entry(27 bytes)
        byte[] requestMessageData = new byte[29];
        int offset = 0;
        requestMessageData[offset] = EmvFunctionId.REMOVE_A_AID_CONFIG;
        offset++;
        requestMessageData[offset] = (byte) cardType;
        offset++;
        // AID entry = AID(16 bytes) + AID len(1 byte) + Kernel ID(8 bytes) + External check flag(1 byte) + Transaction type(1 byte)
        packAidEntry(aidEntry, requestMessageData, offset);

        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.EMV_RESPONSE, EmvFunctionId.REMOVE_A_AID_CONFIG);

        // Response message data = Function ID(1 byte) + Response code(2 bytes)
        ExternalEmvL3Utils.checkResponseCode(responseMessage);
    }

    /**
     * Remove all AID configurations of the specified card interface.
     *
     * @param cardType Indicates which card interface to remove all configuration.
     *                      <ul>
     *                          <li>0x01: Contact card.</li>
     *                          <li>0x02: Contactless card.</li>
     *                      </ul>
     * @throws NSDKException
     */
    public void removeAllAID(int cardType) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.EMV_REQUEST);

        // Request message data = Function ID(1 byte) + Card interface(1 byte)
        byte[] requestMessageData = new byte[2];
        requestMessageData[0] = EmvFunctionId.REMOVE_ALL_AID_CONFIG;
        requestMessageData[1] = (byte) cardType;
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.EMV_RESPONSE, EmvFunctionId.REMOVE_ALL_AID_CONFIG);

        // Response message data = Function ID(1 byte) + Response code(2 bytes)
        ExternalEmvL3Utils.checkResponseCode(responseMessage);
    }

    /**
     * Get AID number of specified card interface.
     *
     * @param cardType Card interface:
     *                      <ul>
     *                      <li>0x01: Contact card</li>
     *                      <li>0x02: Contactless card</li>
     *                      </ul>
     * @return The number of AID.
     */
    public ArrayList<ExtAIDEntry> getAIDList(int cardType) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.EMV_REQUEST);
        byte type = (byte) cardType;
        requestMessage.setMessageData(new byte[]{EmvFunctionId.GET_AID_NUM, type});

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.EMV_RESPONSE, EmvFunctionId.GET_AID_NUM);

        // Response message data = Function ID(1 byte) + Response code(2 bytes) + AID num(2 bytes)
        byte[] responseMessageData = responseMessage.getMessageData();
        ExternalEmvL3Utils.checkResponseCode(responseMessage);

        if (responseMessageData.length < 7) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, ExternalErrorMessage.DATA_LENGTH_NOT_ENOUGH);
        }

        int aidNum = ExternalMessage.hexBuffer2Int(new byte[]{responseMessageData[3], responseMessageData[4]});
        if (aidNum <= 0) {
            return null;
        }

        int current = 0;
        int index = 5;
        ArrayList<ExtAIDEntry> aidEntries = new ArrayList<ExtAIDEntry>();

        while(current < aidNum){
            int entryLen = ExternalMessage.hexBuffer2Int(new byte[]{responseMessageData[index], responseMessageData[index+1]});
            index +=2;
            byte[] entryBytes = new byte[entryLen];
            System.arraycopy(responseMessageData,index,entryBytes,0,entryLen);
            aidEntries.add(parseAidEntry(entryBytes));
            index +=entryLen;

            current++;
        }

        return aidEntries;
    }

    private ExtAIDEntry parseAidEntry(byte[] tlvData) {
        int current = 0;
        int tag = 0;
        int tagLen = 0;
        int lenValue = 0;
        ExtAIDEntry aidEntry = new ExtAIDEntry();

        while (current < tlvData.length) {

            tagLen = getTagLen(tlvData, current);
            byte[] tagBytes = new byte[tagLen];
            System.arraycopy(tlvData,current,tagBytes,0,tagLen);
            tag = bytesToInt(tagBytes);

            current += tagLen;
            if ((tlvData[current] & 0x80) == 0x80) {
                int tmpLen = tlvData[current] & 0x7F;
                switch (tmpLen) {
                    case 1:
                        lenValue = tlvData[current + 1] & 0xFF;
                        break;
                    case 2:
                        lenValue = (tlvData[current + 1] << 8) & 0xFF00 + (tlvData[current + 2] & 0xFF);
                        break;
                    case 3:
                        lenValue = (tlvData[current + 1] << 16) & 0xFF0000 + (tlvData[current + 2] << 8) & 0xFF00 + (tlvData[current + 3] & 0xFF);
                        break;
                }
                current += tmpLen + 1;
            } else {
                lenValue = tlvData[current] & 0xFF;
                current += 1;
            }

            byte[] value = new byte[lenValue];
            System.arraycopy(tlvData, current, value, 0, lenValue);
            current += lenValue;

            if(tag == 0x9F06){
                aidEntry.setAid(value);
            }else if(tag == 0xDF37){
                aidEntry.setKernelId(value);
            }else if(tag == 0x1F8101){
                int checkFlag = bytesToInt(value);
                aidEntry.setExternCheckFlag(checkFlag);
            }else if(tag == 0x9C){
                aidEntry.setTransactionType(value[0]);
            }
        }
        return aidEntry;
    }

    public int bytesToInt(byte[] bytes) {
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
        int tagLen = 1;
        boolean isSubsequent = false;
        for (int i = 0; i < 3; i++) {
            byte b = input[i + offset];
            if ((b & 0x1F) == 0x1F) {
                isSubsequent = true;
                tagLen++;
            } else {
                if (isSubsequent) {
                    if ((b & 0x80) == 0x80) {
                        tagLen++;
                        continue;
                    }
                }
                break;
            }
        }
        return tagLen;
    }

    private void packAidEntry(ExtAIDEntry aidEntry, byte[] requestMessageData, int offset) {
        // AID entry = AID(16 bytes) + AID len(1 byte) + Kernel ID(8 bytes) + External check flag(1 byte) + Transaction type(1 byte)
        if (aidEntry.getAid().length < 16) {
            byte[] aidBuf = new byte[16];
            System.arraycopy(aidEntry.getAid(), 0, aidBuf, 0, aidEntry.getAid().length);
            System.arraycopy(aidBuf, 0, requestMessageData, offset, 16);
        } else {
            System.arraycopy(aidEntry.getAid(), 0, requestMessageData, offset, 16);
        }
        offset += 16;
        requestMessageData[offset] = (byte) aidEntry.getAid().length;
        offset++;
        System.arraycopy(aidEntry.getKernelId(), 0, requestMessageData, offset, 8);
        offset += 8;
        requestMessageData[offset] = (byte)aidEntry.getExternCheckFlag();
        offset++;
        requestMessageData[offset] = aidEntry.getTransactionType();
    }
}
