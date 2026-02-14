package com.newland.nsdk.core.api.common.crypto;

import com.newland.nsdk.core.api.common.keymanager.CipherMode;

/**
 * Algorithm parameters used for symmetric key generation or data encryption/decryption.
 */
public class AlgorithmParameters {
    private PaddingMode paddingMode;
    private CipherMode cipherMode;
    private byte[] iv;

    /**
     * Gets padding mode.
     *
     * @return Padding mode, see {@link PaddingMode}.
     */
    public PaddingMode getPaddingMode() {
        return paddingMode;
    }

    /**
     * Sets Padding mode.
     *
     * @param paddingMode Padding mode, see {@link PaddingMode}
     */
    public void setPaddingMode(PaddingMode paddingMode) {
        this.paddingMode = paddingMode;
    }

    /**
     * Gets cipher mode.
     *
     * @return Cipher mode, see {@link CipherMode}.
     */
    public CipherMode getCipherMode() {
        return cipherMode;
    }

    /**
     * Sets cipher mode.
     *
     * @param cipherMode Cipher mode, see {@link CipherMode}.
     */
    public void setCipherMode(CipherMode cipherMode) {
        this.cipherMode = cipherMode;
    }

    /**
     * Gets initial value.
     *
     * @return Initial value, required when cipher mode is {@link CipherMode#CBC}.
     */
    public byte[] getIV() {
        return iv;
    }

    /**
     * Sets initial value.
     *
     * @param iv Initial value, required when cipher mode is {@link CipherMode#CBC}.
     */
    public void setIV(byte[] iv) {
        this.iv = iv;
    }
}
