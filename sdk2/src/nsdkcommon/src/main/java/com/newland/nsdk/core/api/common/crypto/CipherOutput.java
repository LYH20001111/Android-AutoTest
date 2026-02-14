package com.newland.nsdk.core.api.common.crypto;

/**
 * The result of data encryption/decryption.
 */
public class CipherOutput {
    private byte[] data = null;
    private byte[] ksn = null;

    /**
     * Instantiates a new Cipher output.
     *
     * @param data Encrypted/decrypted data.
     */
    public CipherOutput(byte[] data) {
        this.data = data;
    }

    /**
     * Instantiates a new Cipher output.
     *
     * @param data Encrypted/decrypted data.
     * @param ksn  KSN when it is DUKPT encryption/decryption.
     */
    public CipherOutput(byte[] data, byte[] ksn) {
        this.data = data;
        this.ksn = ksn;
    }

    /**
     * Gets encrypted/decrypted data.
     *
     * @return Encrypted/decrypted data.
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Gets KSN when it is DUKPT encryption/decryption.
     *
     * @return KSN.
     */
    public byte[] getKsn() {
        return ksn;
    }

}
