package com.newland.forth.spi.crypto.cipher;

/**
 * The type Mac output.
 */
public class MacOutput {
    private int ret = 0;
    private byte[] data;
    private byte[] ksn;

    /**
     * Gets ret.
     *
     * @return the ret
     */
    public int getRet() {
        return ret;
    }

    /**
     * Get data byte [ ].
     *
     * @return the byte [ ]
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Get ksn byte [ ].
     *
     * @return the byte [ ]
     */
    public byte[] getKsn() {
        return ksn;
    }

    /**
     * Instantiates a new Mac output.
     *
     * @param ret  the ret
     * @param data the data
     * @param ksn  the ksn
     */
    public MacOutput(int ret, byte[] data, byte[] ksn) {
        this.ret = ret;
        this.data = data;
        this.ksn = ksn;
    }
}
