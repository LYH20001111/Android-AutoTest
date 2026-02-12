package com.newland.nsdk.core.external.command.cardreader;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.card.contactless.ContactlessCardInfo;
import com.newland.nsdk.core.api.common.card.contactless.ContactlessCardType;
import com.newland.nsdk.core.api.common.card.magcard.MagCardInfo;
import com.newland.nsdk.core.api.common.cardreader.CardReaderParameters;
import com.newland.nsdk.core.api.common.cardreader.CardType;
import com.newland.nsdk.core.api.common.crypto.CipherType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.exception.NSDKTimeoutException;
import com.newland.nsdk.core.api.common.keymanager.CipherMode;
import com.newland.nsdk.core.api.common.keymanager.KeyType;
import com.newland.nsdk.core.api.external.cardreader.ExtCardReaderParameters;
import com.newland.nsdk.core.api.external.exception.NSDKExternalDeviceException;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.external.command.exception.ExternalErrorMessage;
import com.newland.nsdk.core.external.command.exception.ExternalMessageException;
import com.newland.nsdk.core.external.command.message.ExternalMessage;
import com.newland.nsdk.core.external.command.message.ExternalMessageType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Provides the ability to search for mag card/smart card/contactless card at the same time.
 */
public class ExternalCardReaderModule {

    /**
     * Type A card.
     */
    public final static int CONTACTLESS_CARD_TYPE_A = 0x01;
    /**
     * Type B card.
     */
    public final static int CONTACTLESS_CARD_TYPE_B = 0x02;
    /**
     * Type F card
     */
    public final static int CONTACTLESS_CARD_TYPE_F = 0x04;
    /**
     * Type V card
     */
    public final static int CONTACTLESS_CARD_TYPE_V = 0x08;

    /**
     * This supports multiple card interfaces to detect card.
     *
     * @return The information of detected card, see {@link DetectedCardInfo}
     * @throws NSDKException
     */
    public DetectedCardInfo searchCard(CardType[] cardTypes, int timeout, CardReaderParameters parameter) throws NSDKException {
        if (parameter == null) {
            throw new NSDKIllegalParameterException("Card reader parameter is null.");
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.CARD_READ_REQUEST);
        requestMessage.setMessageData(packCardReaderRequestMessageData(cardTypes, timeout, parameter));

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceive(requestMessage, ExternalMessageType.CARD_READ_RESPONSE,
                null, timeout * 1000);

        // Response message data = Response code(2 bytes) + Card type(1 byte) + atq len(2 bytes) + atq + Atr len(2 bytes) + Atr
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(new byte[]{responseMessageData[0], responseMessageData[1]});
        if (responseCode != ErrorCode.OK) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (1 == responseCode) {
                throw new NSDKIllegalParameterException(ErrorCode.PARAM_ERROR, ExternalErrorMessage.INVALID_PARAMETER, innerErrorCode);
            }

            if (3 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_MAG_READ_ERROR, ExternalErrorMessage.MAG_CARD_READ_ERROR, innerErrorCode);
            }

            if (4 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_CARD_READER_PAN_GETTING_ERROR, "Getting PAN error.", innerErrorCode);
            }

            if (5 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_CARD_READER_PAN_ENCRYPTION_ERROR, "PAN encryption error.", innerErrorCode);
            }

            if (6 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_MAG_TRACK_ENCRYPTION_ERROR, ExternalErrorMessage.MAG_CARD_TRACK_ENCRYPT_ERROR, innerErrorCode);
            }

            if (10 == responseCode) {
                throw new NSDKTimeoutException(ExternalErrorMessage.TIMEOUT, innerErrorCode);
            }

            if (45 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_BAD_CMD_LENGTH, ExternalErrorMessage.PINPAD_BAD_CMD_LENGTH, innerErrorCode);
            }

            if (46 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.CANCELLED, ExternalErrorMessage.CANCELLED, innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }

        if (responseMessageData.length == 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }

        return extractCardReaderResponseMessageData(parameter, responseMessageData);
    }

    private DetectedCardInfo extractCardReaderResponseMessageData(CardReaderParameters parameter, byte[] responseMessageData) throws ExternalMessageException {
        DetectedCardInfo cardInfo = new DetectedCardInfo();
        int offset = 2;
        byte cardType = responseMessageData[offset];
        switch (cardType) {
            case 0:
                cardInfo.setCardType(CardType.CONTACTLESS_CARD);
                break;
            case 1:
                cardInfo.setCardType(CardType.CONTACT_CARD);
                break;
            case 2:
                cardInfo.setCardType(CardType.MAG_CARD);
                break;
            default:
                break;
        }
        offset++;

        // Contactless card
        if (cardType == 0) {
            byte contactlessCardType = responseMessageData[offset];
            ContactlessCardInfo contactlessCardInfo = new ContactlessCardInfo();
            offset++;
            switch (contactlessCardType) {
                case CONTACTLESS_CARD_TYPE_A:
                    cardInfo.setContactlessCardType(ContactlessCardType.TYPE_A);
                    // Type A 在寻卡时没有可返回的信息，card info len 为 0，跳过 card info len 字节
                    offset++;
                    break;
                case CONTACTLESS_CARD_TYPE_B:
                    // Type B 在寻卡时没有可返回的信息，card info len 为 0，跳过 card info len 字节
                    offset++;
                    cardInfo.setContactlessCardType(ContactlessCardType.TYPE_B);
                    break;
                case CONTACTLESS_CARD_TYPE_F:
                    cardInfo.setContactlessCardType(ContactlessCardType.TYPE_F);
                    // Felica 在寻卡是会返回 idmpmm
                    byte idmpmmLen = responseMessageData[offset];
                    offset++;
                    if (idmpmmLen > 0) {
                        byte[] idmpmmBuf = new byte[idmpmmLen];
                        System.arraycopy(responseMessageData, offset, idmpmmBuf, 0, idmpmmBuf.length);
                        offset += idmpmmBuf.length;
                        contactlessCardInfo.setIDmPMm(idmpmmBuf);
                    }
                    break;
                case CONTACTLESS_CARD_TYPE_V:
                    // Type V 目前还不确认寻卡时是否有信息返回，暂时跳过 card info len 字节
                    offset++;
                    cardInfo.setContactlessCardType(ContactlessCardType.TYPE_V);
                    break;
                default:
                    throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_ERROR, String.format("Unsupported contactless card type: %d", contactlessCardType));
            }

            cardInfo.setContactlessCardInfo(contactlessCardInfo);
        }

        if (cardType == 2) {
            MagCardInfo magCardInfo = new MagCardInfo();
            try {
                byte track1Status = responseMessageData[offset];
                offset++;
                byte track2Status = responseMessageData[offset];
                offset++;
                byte track3Status = responseMessageData[offset];
                offset++;
                magCardInfo.setTrackStatus(new byte[]{track1Status, track2Status, track3Status});

                // 解析磁道 1 数据
                int plainTrack1DataLen = responseMessageData[offset];
                magCardInfo.setPlainTrack1DataLen(plainTrack1DataLen);
                offset++; // 明文长度字节

                int encryptTrack1DataLen = responseMessageData[offset];
                offset++; // 密文长度字节

                //磁道1有数据才拷贝
                if (track1Status == 0) {
                    int track1DataLen = encryptTrack1DataLen == 0 ? plainTrack1DataLen : encryptTrack1DataLen;

                    byte[] track1Data = new byte[track1DataLen];
                    System.arraycopy(responseMessageData, offset, track1Data, 0, track1Data.length);
                    magCardInfo.setTrack1Data(track1Data);
                    offset += track1Data.length;
                }

                // 解析磁道 2 数据
                int plainTrack2DataLen = responseMessageData[offset];
                magCardInfo.setPlainTrack2DataLen(plainTrack2DataLen);
                offset++; // 明文长度字节

                int encryptTrack2DataLen = responseMessageData[offset];
                offset++; // 密文长度字节

                //磁道2有数据才拷贝
                if (track2Status == 0) {
                    int track2DataLen = encryptTrack2DataLen == 0 ? plainTrack2DataLen : encryptTrack2DataLen;

                    byte[] track2Data = new byte[track2DataLen];
                    System.arraycopy(responseMessageData, offset, track2Data, 0, track2Data.length);
                    magCardInfo.setTrack2Data(track2Data);
                    offset += track2Data.length;
                }

                // 解析磁道 3 数据
                int plainTrack3DataLen = responseMessageData[offset];
                magCardInfo.setPlainTrack3DataLen(plainTrack3DataLen);
                offset++; // 明文长度字节

                int encryptTrack3DataLen = responseMessageData[offset];
                offset++; // 密文长度字节

                //磁道3有数据才拷贝
                if (track3Status == 0) {
                    int track3DataLen = encryptTrack3DataLen == 0 ? plainTrack3DataLen : encryptTrack3DataLen;

                    byte[] track3Data = new byte[track3DataLen];
                    System.arraycopy(responseMessageData, offset, track3Data, 0, track3Data.length);
                    magCardInfo.setTrack3Data(track3Data);
                    offset += track3Data.length;
                }

                if(track2Status == 0) {
                    int plainPanLen = responseMessageData[offset];
                    magCardInfo.setPlainPANLen(plainPanLen);
                    offset++;

                    boolean isEncryptTrack = false;
                    if (parameter instanceof ExtCardReaderParameters) {
                        ExtCardReaderParameters tempParameter = (ExtCardReaderParameters) parameter;
                        isEncryptTrack = tempParameter.getPANKeyIndex() != 0;
                    }

                    int panDataLen = plainPanLen;
                    if (isEncryptTrack) {
                        // 密文情况下 pan data 固定 32 个字节
                        panDataLen = 32;
                    }
                    byte[] panData = new byte[panDataLen];
                    System.arraycopy(responseMessageData, offset, panData, 0, panDataLen);
                    offset += panDataLen;
                    magCardInfo.setPanData(panData);

                    // 带掩码的卡号的前面明文部分
                    byte firstClearPANLen = responseMessageData[offset];
                    offset++;
                    if (firstClearPANLen > 0) {
                        byte[] firstClearPAN = new byte[firstClearPANLen];
                        System.arraycopy(responseMessageData, offset, firstClearPAN, 0, firstClearPAN.length);
                        magCardInfo.setFirstClearPAN(new String(firstClearPAN));
                        offset += firstClearPAN.length;
                    }

                    // 带掩码的卡号的后面明文部分
                    byte lastClearPANLen = responseMessageData[offset];
                    offset++;
                    if (lastClearPANLen > 0) {
                        byte[] lastClearPAN = new byte[lastClearPANLen];
                        System.arraycopy(responseMessageData, offset, lastClearPAN, 0, lastClearPAN.length);
                        magCardInfo.setLastClearPAN(new String(lastClearPAN));
                        offset += lastClearPAN.length;
                    }

                    byte expiredDateLen = responseMessageData[offset];
                    offset++;
                    if (expiredDateLen > 0) {
                        byte[] expiredDate = new byte[expiredDateLen];
                        System.arraycopy(responseMessageData, offset, expiredDate, 0, expiredDate.length);
                        magCardInfo.setValidDate(new String(expiredDate));
                        offset += expiredDate.length;
                    }

                    byte serviceCodeLen = responseMessageData[offset];
                    offset++;

                    if (serviceCodeLen > 0) {
                        byte[] serviceCode = new byte[serviceCodeLen];
                        System.arraycopy(responseMessageData, offset, serviceCode, 0, serviceCode.length);
                        magCardInfo.setServiceCode(new String(serviceCode));
                        offset += serviceCode.length;
                    }
                }

                cardInfo.setMagCardInfo(magCardInfo);
            } catch (Exception e) {
                throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_ERROR, "Failed to extract mag card info.", e);
            }
        }

        return cardInfo;
    }

    private void extractContactlessCardInfo(byte[] contactlessCardInfoBuf, DetectedCardInfo cardInfo) {
        if (contactlessCardInfoBuf != null && contactlessCardInfoBuf.length > 0) {
            if (cardInfo.getContactlessCardType() == ContactlessCardType.TYPE_F) {
                int offset = 0;
                int len = contactlessCardInfoBuf[0];
                offset++;
                if (len > 0) {
                    byte[] idmpmm = new byte[len];
                    System.arraycopy(contactlessCardInfoBuf, offset, idmpmm, 0, idmpmm.length);
                }
            }
        }
    }

    /**
     * Cancel to read card.
     *
     * @throws NSDKException <ul>
     *                       <li>Throws <b><i>NSDKCommunicationException</i></b> when send or receive data. Get detailed error code by "e.getCode()".</li>
     *                       </ul>
     */
    public void cancelSearch() throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.CANCEL_CARD_READ_REQUEST);
        ExternalCommunicationManager.getInstance().sendInterrupt(requestMessage.pack());
    }

    /**
     * Get mag card information after mag card is swiped.
     *
     * @param keyType      The key used to encrypt track and PAN data. When it is set to null, track and PAN data will not be encrypted.
     *                     <ul>The following required:
     *                     <li>Key index: [129-255]</li>
     *                     <li>Key Type: {@link KeyType#DES} and {@link KeyType#AES} supported</li>
     *                     </ul>
     * @param cipherMode   The algorithm for track and PAN data encryption.
     *                     <ul>The following supported:
     *                     <li>{@link CipherType#DES_CBC}</li>
     *                     <li>{@link CipherType#DES_ECB}</li>
     *                     <li>{@link CipherType#AES_ECB}</li>
     *                     </ul>
     * @param iv           CBC Initial value. This is required when the algorithm is CBC, otherwise set it to null.
     * @param isReadTracks Indicates which tracks to get.
     *                     <ul>
     *                     <li>isReadTracks[0]: true - read track 1, false - not to read track 1</li>
     *                     <li>isReadTracks[1]: true - read track 2, false - not to read track 2</li>
     *                     <li>isReadTracks[2]: true - read track 3, false - not to read track 3</li>
     *                     </ul>
     * @return Mag card information, see {@link MagCardInfo}
     * @throws NSDKException
     */
    public MagCardInfo getMagCardInfo(byte keyIndex, KeyType keyType, CipherMode cipherMode, byte[] iv, boolean[] isReadTracks) throws NSDKException {
        // If CBC, iv shall not be null or empty.
        if (cipherMode == CipherMode.CBC && (iv == null || iv.length == 0)) {
            throw new NSDKIllegalParameterException(ExternalErrorMessage.EMPTY_IV);
        }
        if (isReadTracks == null || isReadTracks.length < 3) {
            throw new NSDKIllegalParameterException("Please set whether to read track 1, track 2, track 3.");
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.GET_MAG_TRACK_DATA_REQUEST);

        // Request message data = Key index(1 byte) + Key type(1 byte) + Key mode(1 byte) + Read track1(1 byte) + Read track2(1 byte) + Read track3(1 byte) + IV
        int requestMessageDataLen = 6;
        if (cipherMode == CipherMode.CBC) {
            requestMessageDataLen += iv.length;
        }

        byte panKeyType = 0;
        if (keyType == KeyType.AES) {
            panKeyType = 1;
        }

        byte keyMode = 0;
        if (cipherMode == CipherMode.CBC) {
            keyMode = 1;
        }

        byte[] requestMessageData = new byte[requestMessageDataLen];
        requestMessageData[0] = keyIndex;
        requestMessageData[1] = panKeyType;
        requestMessageData[2] = keyMode;
        requestMessageData[3] = (byte) (isReadTracks[0] ? 1 : 0);
        requestMessageData[4] = (byte) (isReadTracks[1] ? 1 : 0);
        requestMessageData[5] = (byte) (isReadTracks[2] ? 1 : 0);
        if (cipherMode == CipherMode.CBC) {
            System.arraycopy(iv, 0, requestMessageData, 6, iv.length);
        }
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.GET_MAG_TRACK_DATA_RESPONSE, null);

        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(new byte[]{responseMessageData[0], responseMessageData[1]});
        if (responseCode != ErrorCode.OK) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (responseCode == 1) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_MAG_GET_TRACK_DATA_ERROR, ExternalErrorMessage.MAG_CARD_GET_TRACK_DATA_ERROR, innerErrorCode);
            }

            if (responseCode == 2) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_KEY_MODE_ERROR, ExternalErrorMessage.MAG_CARD_KEY_MODE_ERROR, innerErrorCode);
            }

            if (responseCode == 3) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_MAG_READ_ERROR, ExternalErrorMessage.MAG_CARD_READ_ERROR, innerErrorCode);
            }

            if (responseCode == 4) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_MAG_TRACK2_ERROR, ExternalErrorMessage.MAG_CARD_TRACK2_ERROR, innerErrorCode);
            }

            if (responseCode == 5) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_MAG_TRACK_ENCRYPTION_ERROR, ExternalErrorMessage.MAG_CARD_TRACK_ENCRYPT_ERROR, innerErrorCode);
            }

            if (responseCode == 6) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_MAG_TRACK3_ERROR, ExternalErrorMessage.MAG_CARD_TRACK3_ERROR, innerErrorCode);
            }

            if (responseCode == 46) {
                throw new NSDKExternalDeviceException(ErrorCode.CANCELLED, ExternalErrorMessage.CANCELLED, innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }

        int magCardResultDataLen = responseMessageData.length - 2;
        byte[] magCardResultData = new byte[magCardResultDataLen];
        System.arraycopy(responseMessageData, 2, magCardResultData, 0, magCardResultDataLen);
        return unpackMagCardResult(magCardResultData, keyIndex);
    }

    private byte[] packCardReaderRequestMessageData(CardType[] cardTypes, int timeout, CardReaderParameters parameter) throws NSDKException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        boolean isSearchContactlessCard = false;
        boolean isSearchMagCard = false;
        boolean isSearchTypeF = false;
        boolean isSearchTypeV = false;
        byte targetCardTypes = 0x00;
        for (CardType ct : cardTypes) {
            if (ct == CardType.CONTACTLESS_CARD) {
                targetCardTypes |= 0x01;
                isSearchContactlessCard = true;
            } else if (ct == CardType.CONTACT_CARD) {
                targetCardTypes |= 0x02;
            } else if (ct == CardType.MAG_CARD) {
                targetCardTypes |= 0x04;
                isSearchMagCard = true;
            }
        }

        outputStream.write(targetCardTypes);

        byte contactlessCardTypes = 0x00;
        try {
            if (isSearchContactlessCard) {
                if (parameter.getContactlessCardTypes() == null) {
                    // 默认寻 AB 卡
                    contactlessCardTypes = 0x03;
                } else {
                    for (ContactlessCardType cardType : parameter.getContactlessCardTypes()) {
                        if (cardType == ContactlessCardType.TYPE_A) {
                            contactlessCardTypes |= CONTACTLESS_CARD_TYPE_A;
                        } else if (cardType == ContactlessCardType.TYPE_B) {
                            contactlessCardTypes |= CONTACTLESS_CARD_TYPE_B;
                        } else if (cardType == ContactlessCardType.TYPE_F) {
                            isSearchTypeF = true;
                            contactlessCardTypes |= CONTACTLESS_CARD_TYPE_F;
                        } else if (cardType == ContactlessCardType.TYPE_V) {
                            isSearchTypeV = true;
                            contactlessCardTypes |= CONTACTLESS_CARD_TYPE_V;
                        }
                    }
                }

                outputStream.write(contactlessCardTypes);

                if (isSearchTypeF) {
                    if (parameter.getTypeFParameters() != null && parameter.getTypeFParameters().length > 0) {
                        outputStream.write(parameter.getTypeFParameters().length);
                        outputStream.write(parameter.getTypeFParameters());
                    } else {
                        outputStream.write(0);
                    }
                } else {
                    outputStream.write(0);
                }

                if (isSearchTypeV) {
                    if (parameter.getTypeVParameters() != null && parameter.getTypeVParameters().length > 0) {
                        outputStream.write(parameter.getTypeVParameters().length);
                        outputStream.write(parameter.getTypeVParameters());
                    } else {
                        outputStream.write(0);
                    }
                } else {
                    outputStream.write(0);
                }
            }

            if (isSearchMagCard) {
                packMagCardParameters(outputStream, parameter);
            }

            outputStream.write(ExternalMessage.intToHexBuf(timeout));
            packDisplayMessages(parameter, outputStream);
        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }
        return outputStream.toByteArray();
    }

    private void packDisplayMessages(CardReaderParameters parameter, ByteArrayOutputStream outputStream) throws IOException {
        byte fs = 0x1C;
        String[] displayMessages = null;
        if (parameter instanceof ExtCardReaderParameters) {
            displayMessages = ((ExtCardReaderParameters) parameter).getDisplayMessages();
        }
        if (displayMessages != null && displayMessages.length > 0) {
            for (String m : displayMessages) {
                if (m == null || m.length() == 0) {
                    outputStream.write(fs);
                    continue;
                }
                outputStream.write(m.getBytes());
                outputStream.write(fs);
            }
        }
    }

    private void packMagCardParameters(ByteArrayOutputStream outputStream, CardReaderParameters parameter) throws IOException, NSDKException {
        outputStream.write(parameter.isVerifyTrack() ? 1 : 0);
        if (parameter instanceof ExtCardReaderParameters) {
            ExtCardReaderParameters tempParam = (ExtCardReaderParameters) parameter;
            CipherType cipherType = CipherType.DES_ECB;
            if (tempParam.getCipherType() != null) {
                cipherType = tempParam.getCipherType();
            }
            CipherMode cipherMode = CipherType.getCipherMode(cipherType);
            if (cipherMode == CipherMode.CBC && (tempParam.getIV() == null || tempParam.getIV().length == 0)) {
                throw new NSDKIllegalParameterException("IV is required when cipher mode is CBC.");
            }
            outputStream.write(tempParam.getPANKeyIndex());
            outputStream.write(cipherType.getCode());
            if (tempParam.getIV() != null && tempParam.getIV().length > 0) {
                outputStream.write(tempParam.getIV().length);
                outputStream.write(tempParam.getIV());
            } else {
                outputStream.write(0);
            }
            outputStream.write(tempParam.getTrackEncryptionType());
            outputStream.write(tempParam.getFirstClearPANLen());
            outputStream.write(tempParam.getLastClearPANLen());
        } else {
            // 没有设置磁道加密相关参数，则认为不加密磁道信息
            outputStream.write(0); // key index
            outputStream.write(0); // cipher type
            outputStream.write(0); // iv len
            outputStream.write(0); // encryption algorithm
            outputStream.write(6); // 卡号掩码前面明文部分长度
            outputStream.write(4); // 卡号掩码后面明文部分长度
        }
    }

    public static MagCardInfo unpackMagCardResult(byte[] data, byte keyIndex) throws NSDKException {
        if (data == null || data.length == 0) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, "Mag card result data is null or empty.");
        }

        int offset = 0;
        int totalLen = data.length;

        MagCardInfo result = new MagCardInfo();
        // 明文 PAN 长度
        int panLen = data[offset];
        result.setPlainPANLen(panLen);

        offset++;

        int panDataLen = 0;
        if (panLen > 0) {
            if (keyIndex != 0) {
                panDataLen = 32;
            } else {
                panDataLen = panLen;
            }
        }

        if (panDataLen > 0) {
            if (!ExternalMessage.isDataEnough(offset, totalLen, panDataLen)) {
                throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, "Mag card result data length is not enough to extract.");
            }

            byte[] pan = new byte[panDataLen];
            System.arraycopy(data, offset, pan, 0, panDataLen);
            offset += panDataLen;
            result.setPanData(pan);
        }

        if (panLen > 0) {
            if (!ExternalMessage.isDataEnough(offset, totalLen, panLen)) {
                throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, "Mag card result data length is not enough to extract.");
            }

            byte[] maskedPan = new byte[panLen];
            System.arraycopy(data, offset, maskedPan, 0, panLen);
            offset += panLen;
            String maskedPAN = new String(maskedPan);
            result.setFirstClearPAN(maskedPAN.substring(0, maskedPAN.indexOf('*')));
            result.setLastClearPAN(maskedPAN.substring(maskedPAN.lastIndexOf('*') + 1));
        }

        byte[] trackStatus = new byte[3];
        for (int i = 0; i < 3; i++) {
            if (offset >= data.length) {
                return result;
            }

            trackStatus[i] = data[offset];
            offset++;
        }

        if (offset <= data.length - 2) {
            int track1Len = ExternalMessage.hexBuffer2Int(new byte[]{data[offset], data[offset + 1]});
            offset += 2;
            result.setPlainTrack1DataLen(track1Len);
            if (keyIndex != 0) {
                if (!ExternalMessage.isDataEnough(offset, data.length, 2)) {
                    throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, "Mag card result data is not enough to extract.");
                }

                track1Len = ExternalMessage.hexBuffer2Int(new byte[]{data[offset], data[offset + 1]});
                offset += 2;
            }

            if (track1Len > data.length - offset) {
                throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LEN_FIELD_ERROR, "Track1 len error.");
            }
            byte[] track1 = new byte[track1Len];
            System.arraycopy(data, offset, track1, 0, track1Len);
            offset += track1Len;
            if (trackStatus[0] == 0) {
                result.setTrack1Data(track1);
            }
        }

        if (offset <= data.length - 2) {
            int track2Len = ExternalMessage.hexBuffer2Int(new byte[]{data[offset], data[offset + 1]});
            offset += 2;

            result.setPlainTrack2DataLen(track2Len);
            if (keyIndex != 0) {
                if (!ExternalMessage.isDataEnough(offset, data.length, 2)) {
                    throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, "Mag card result data is not enough to extract.");
                }

                track2Len = ExternalMessage.hexBuffer2Int(new byte[]{data[offset], data[offset + 1]});
                offset += 2;
            }

            if (track2Len > data.length - offset) {
                throw new NSDKException("Track2 len error.");
            }
            byte[] track2 = new byte[track2Len];
            System.arraycopy(data, offset, track2, 0, track2Len);
            offset += track2Len;
            if (trackStatus[1] == 0) {
                result.setTrack2Data(track2);
            }
        }

        if (offset <= data.length - 2) {
            int track3Len = ExternalMessage.hexBuffer2Int(new byte[]{data[offset], data[offset + 1]});
            offset += 2;

            result.setPlainTrack3DataLen(track3Len);
            if (keyIndex != 0) {
                if (!ExternalMessage.isDataEnough(offset, data.length, 2)) {
                    throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, "Mag card result data is not enough to extract.");
                }

                track3Len = ExternalMessage.hexBuffer2Int(new byte[]{data[offset], data[offset + 1]});
                offset += 2;
            }

            if (track3Len > data.length - offset) {
                throw new NSDKException("Track3 len error.");
            }
            byte[] track3 = new byte[track3Len];
            System.arraycopy(data, offset, track3, 0, track3Len);
            if (trackStatus[2] == 0) {
                result.setTrack3Data(track3);
            }
        }

        return result;
    }
}
