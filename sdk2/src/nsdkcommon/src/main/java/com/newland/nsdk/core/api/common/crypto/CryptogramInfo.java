package com.newland.nsdk.core.api.common.crypto;

/**
 * This information for the cryptogram block to be encrypted.
 */
public class CryptogramInfo {
    private byte[] prefixInfo;
    private byte[] suffixInfo;
    private AsymEncodingMode encodingMode;
    private MessageDigestType messageDigestType;
    private byte[] additionalData;

    /**
     * Gets the prefix information of the cryptogram block.
     * @return The prefix information of the cryptogram block.
     */
    public byte[] getPrefixInfo() {
        return prefixInfo;
    }

    /**
     * Sets the prefix information of the cryptogram block.
     * @param prefixInfo The prefix information of the cryptogram block.
     */
    public void setPrefixInfo(byte[] prefixInfo) {
        this.prefixInfo = prefixInfo;
    }

    /**
     * Gets the suffix information of the cryptogram block.
     * @return The suffix information of the cryptogram block.
     */
    public byte[] getSuffixInfo() {
        return suffixInfo;
    }

    /**
     * Sets the suffix information of the cryptogram block.
     * @param suffixInfo The suffix information of the cryptogram block.
     */
    public void setSuffixInfo(byte[] suffixInfo) {
        this.suffixInfo = suffixInfo;
    }

    /**
     * Gets the asymmetrical encoding mode.
     * @return The asymmetrical encoding mode. See {@link AsymEncodingMode}.
     */
    public AsymEncodingMode getEncodingMode() {
        return encodingMode;
    }

    /**
     * Sets the asymmetrical encoding mode.
     * @param encodingMode The asymmetrical encoding mode. See {@link AsymEncodingMode}.
     */
    public void setEncodingMode(AsymEncodingMode encodingMode) {
        this.encodingMode = encodingMode;
    }

    /**
     * Gets the message digest type.
     * @return The message digest type. See {@link MessageDigestType}.
     */
    public MessageDigestType getMessageDigestType() {
        return messageDigestType;
    }

    /**
     * Sets the message digest type.
     * @param messageDigestType The message digest type. See {@link MessageDigestType}.
     */
    public void setMessageDigestType(MessageDigestType messageDigestType) {
        this.messageDigestType = messageDigestType;
    }

    /**
     * Gets the additional data.
     * @return The additional data.
     */
    public byte[] getAdditionalData() {
        return additionalData;
    }

    /**
     * Sets the additional data.
     * @param additionalData The additional data.
     */
    public void setAdditionalData(byte[] additionalData) {
        this.additionalData = additionalData;
    }
}
