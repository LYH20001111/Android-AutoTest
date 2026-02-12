package com.newland.nsdk.core.api.external.cardreader;

import com.newland.nsdk.core.api.common.cardreader.CardReaderParameters;
import com.newland.nsdk.core.api.common.crypto.CipherType;

/**
 * Parameters used to searching cards.
 */
public class ExtCardReaderParameters extends CardReaderParameters {
    private byte panKeyIndex;
    private CipherType cipherType;
    private byte[] iv;
    private byte trackEncryptionType;
    private String[] displayMessages;
    private byte firstClearPANLen = 6;
    private byte lastClearPANLen = 4;

    /**
     * Gets PAN key index.
     *
     * @return PAN key index, indicates which key used to encrypt track data. The key shall already be exist in the external device.
     */
    public byte getPANKeyIndex() {
        return panKeyIndex;
    }

    /**
     * Sets PAN key index.
     *
     * @param panKeyIndex PAN key index, indicates which key used to encrypt track data. The key shall already be exist in the external device.
     */
    public void setPANKeyIndex(byte panKeyIndex) {
        this.panKeyIndex = panKeyIndex;
    }

    /**
     * Gets cipher type.
     *
     * @return Cipher type, indicates how to encrypt track data. See {@link CipherType}.
     */
    public CipherType getCipherType() {
        return cipherType;
    }

    /**
     * Sets cipher type.
     *
     * @param cipherType Cipher type, indicates how to encrypt track data. See {@link CipherType}.
     */
    public void setCipherType(CipherType cipherType) {
        this.cipherType = cipherType;
    }

    /**
     * Gets IV.
     *
     * @return Initial value used for CBC mode.
     */
    public byte[] getIV() {
        return iv;
    }

    /**
     * Sets IV.
     *
     * @param iv Initial value used for CBC mode.
     */
    public void setIV(byte[] iv) {
        this.iv = iv;
    }

    /**
     * Gets track encryption type.
     *
     * @return Track encryption type.
     * <ul>
     *     <li>0: All encrypted.</li>
     *     <li>1: Union Pay</li>
     * </ul>
     */
    public byte getTrackEncryptionType() {
        return trackEncryptionType;
    }

    /**
     * Sets track encryption type.
     *
     * @param trackEncryptionType Track encryption type.
     *                            <ul>
     *                                <li>0: All encrypted.</li>
     *                                <li>1: Union Pay</li>
     *                            </ul>
     */
    public void setTrackEncryptionType(byte trackEncryptionType) {
        this.trackEncryptionType = trackEncryptionType;
    }

    /**
     * Gets display messages.
     *
     * @return Display messages which will be displayed in order(one string for one line). If null or empty, nothing will be displayed.
     */
    public String[] getDisplayMessages() {
        return displayMessages;
    }

    /**
     * Sets display messages.
     *
     * @param displayMessages Display messages which will be displayed in order(one string for one line). If null or empty, nothing will be displayed.
     */
    public void setDisplayMessages(String[] displayMessages) {
        this.displayMessages = displayMessages;
    }

    /**
     * Gets the length of first clear part of masked PAN.
     *
     * @return The length of first clear part of masked PAN. Value range [0x06, 0x0A], default value is 0x06.
     */
    public byte getFirstClearPANLen() {
        return firstClearPANLen;
    }

    /**
     * Sets the length of first clear part of masked PAN
     *
     * @param firstClearPANLen The length of first clear part of masked PAN. Value range [0x06, 0x0A], default value is 0x06.
     */
    public void setFirstClearPANLen(byte firstClearPANLen) {
        this.firstClearPANLen = firstClearPANLen;
    }

    /**
     * Gets the length of last clear part of masked PAN.
     *
     * @return The length of last clear part of masked PAN. Value range [0x01, 0x04], default value is 0x04.
     */
    public byte getLastClearPANLen() {
        return lastClearPANLen;
    }

    /**
     * Sets the length of last clear part of masked PAN
     *
     * @param lastClearPANLen The length of last clear part of masked PAN. Value range [0x01, 0x04], default value is 0x04.
     */
    public void setLastClearPANLen(byte lastClearPANLen) {
        this.lastClearPANLen = lastClearPANLen;
    }

}
