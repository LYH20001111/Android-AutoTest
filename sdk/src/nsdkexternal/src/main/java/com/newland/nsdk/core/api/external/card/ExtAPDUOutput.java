package com.newland.nsdk.core.api.external.card;

/**
 * The result of APDU command.
 */
public class ExtAPDUOutput {
    private int dataLen;
    private byte[] data;

    /**
     * Gets the length of data plaintext.
     *
     * @return Length of data plaintext.
     */
    public int getDataLen() {
        return dataLen;
    }

    /**
     * Sets the length of data plaintext.
     *
     * @param dataLen Length of data plaintext.
     */
    public void setDataLen(int dataLen) {
        this.dataLen = dataLen;
    }

    /**
     * Gets APDU result data.
     *
     * @return APDU result data. It could be plaintext or encrypted.
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Sets APDU result data.
     *
     * @param data APDU result data. It could be plaintext or encrypted.
     */
    public void setData(byte[] data) {
        this.data = data;
    }
}
