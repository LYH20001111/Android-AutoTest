package com.newland.nsdk.core.api.common.crypto;

/**
 * Configuration class for cryptographic parameters.
 * <p>
 * This class encapsulates the necessary parameters required for encryption and decryption operations,
 * including the algorithm type, padding mode, iv, and auth data, auth tag for AES-GCM.
 * </p>
 */
public class CipherParameters {

    /**
     * The encryption/decryption algorithm type. See {@link CipherType}
     */
    private CipherType cipherType;

    /**
     * Padding mode, see {@link PaddingMode},
     * default value is {@link PaddingMode#NONE} if this is set to null.
     */
    private PaddingMode paddingMode = PaddingMode.NONE;

    /**
     * Initial vector required for CBC decryption. 8 bytes for TDES, 16 bytes for AES.
     */
    private byte[] iv = null;

    /**
     * The Authentication Tag.
     * <p><strong>Usage: Decryption only.</strong></p>
     * <p>In AES-GCM, this tag is passed during decryption to verify
     * the integrity and authenticity of the ciphertext.</p>
     */
    private byte[] authTag;

    /**
     * The length of the Authentication Tag (in bytes).
     * <p><strong>Usage: Encryption only.</strong></p>
     * <p>In AES-GCM, this specifies the desired length of the
     * generated authentication tag.</p>
     */
    private int authTagLen;

    /**
     * Authenticated Data.
     * <p><strong>Usage: Encryption and Decryption.</strong></p>
     * <p>In AES-GCM, this data is authenticated (integrity checked)
     * but <em>not</em> encrypted. It remains in plaintext.</p>
     */
    private byte[] authData;

    /**
     * Gets the Authenticated Data.
     *
     * @return the Auth Data byte array.
     */
    public byte[] getAuthData() {
        return authData;
    }

    /**
     * Sets the Authenticated Data.
     *
     * @param authData the Auth Data byte array.
     */
    public void setAuthData(byte[] authData) {
        this.authData = authData;
    }

    /**
     * Gets the Authentication Tag.
     *
     * @return the authentication tag byte array.
     */
    public byte[] getAuthTag() {
        return authTag;
    }

    /**
     * Sets the Authentication Tag.
     * <p><strong>Required for GCM Decryption.</strong></p>
     * <p>Provide the tag that was generated during encryption to verify the data.</p>
     *
     * @param authTag the authentication tag byte array.
     */
    public void setAuthTag(byte[] authTag) {
        this.authTag = authTag;
    }

    /**
     * Gets the cipher algorithm type.
     *
     * @return the {@link CipherType}.
     */
    public CipherType getCipherType() {
        return cipherType;
    }

    /**
     * Sets the cipher algorithm type.
     *
     * @param cipherType the {@link CipherType} to use.
     */
    public void setCipherType(CipherType cipherType) {
        this.cipherType = cipherType;
    }

    /**
     * Gets the Initialization Vector (IV).
     *
     * @return the IV byte array.
     */
    public byte[] getIv() {
        return iv;
    }

    /**
     * Sets the Initialization Vector (IV).
     * <p>For secure encryption (CBC, GCM, etc.), the IV must be random and unique
     * for every encryption operation using the same key.</p>
     *
     * @param iv the IV byte array.
     */
    public void setIv(byte[] iv) {
        this.iv = iv;
    }

    /**
     * Gets the padding mode.
     *
     * @return the {@link PaddingMode}.
     */
    public PaddingMode getPaddingMode() {
        return paddingMode;
    }

    /**
     * Sets the padding mode.
     * <p>If the data length is not a multiple of the block size, padding is required.
     * For stream ciphers or GCM mode, this is usually set to NONE.</p>
     *
     * @param paddingMode the {@link PaddingMode} to set.
     */
    public void setPaddingMode(PaddingMode paddingMode) {
        this.paddingMode = paddingMode;
    }

    /**
     * Gets the requested Authentication Tag length.
     *
     * @return the tag length in bytes.
     */
    public int getAuthTagLen() {
        return authTagLen;
    }

    /**
     * Sets the requested Authentication Tag length.
     * <p><strong>Required for GCM Encryption.</strong></p>
     * <p>Specifies how long the generated tag should be.</p>
     *
     * @param authTagLen the tag length in bytes.
     */
    public void setAuthTagLen(int authTagLen) {
        this.authTagLen = authTagLen;
    }
}