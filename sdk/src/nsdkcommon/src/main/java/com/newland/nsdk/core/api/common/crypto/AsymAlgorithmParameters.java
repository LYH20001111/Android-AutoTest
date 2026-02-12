package com.newland.nsdk.core.api.common.crypto;

/**
 * Algorithm parameters used for asymmetric key generation or data encryption/decryption.
 */
public class AsymAlgorithmParameters {
    private AsymEncodingMode encodingMode;
    private AsymCryptoMode cryptoMode;
    private MessageDigestType messageDigestType;

    /**
     * Gets encoding mode.
     *
     * @return Encoding mode, see {@link AsymEncodingMode}.
     */
    public AsymEncodingMode getEncodingMode() {
        return encodingMode;
    }

    /**
     * Sets encoding mode.
     *
     * @param encodingMode Encoding mode, see {@link AsymEncodingMode}
     */
    public void setEncodingMode(AsymEncodingMode encodingMode) {
        this.encodingMode = encodingMode;
    }

    /**
     * Gets crypto mode.
     *
     * @return Crypto mode, see {@link AsymCryptoMode}
     */
    public AsymCryptoMode getCryptoMode() {
        return cryptoMode;
    }

    /**
     * Sets crypto mode.
     *
     * @param cryptoMode Crypto mode, see {@link AsymCryptoMode}
     */
    public void setCryptoMode(AsymCryptoMode cryptoMode) {
        this.cryptoMode = cryptoMode;
    }

    /**
     * Gets message digest type.
     *
     * @return Message digest type, see {@link MessageDigestType}.
     */
    public MessageDigestType getMessageDigestType() {
        return messageDigestType;
    }

    /**
     * Sets message digest type.
     *
     * @param messageDigestType Message digest type, see {@link MessageDigestType}.
     */
    public void setMessageDigestType(MessageDigestType messageDigestType) {
        this.messageDigestType = messageDigestType;
    }
}
