package com.newland.forth.spi.crypto.cipher;

/**
 * The type Cipher output.
 */
public class CipherOutput {
    private int ret = 0;
    private byte[] data = null;
    private byte[] ksn = null;

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
     * Instantiates a new Cipher output.
     *
     * @param ret  the ret
     * @param data the data
     */
    public CipherOutput(int ret, byte[] data) {
        this.ret = ret;
        this.data = data;
    }

    /**
     * Instantiates a new Cipher output.
     *
     * @param ret  the ret
     * @param data the data
     * @param ksn  the ksn
     */
    public CipherOutput(int ret, byte[] data, byte[] ksn) {
        this.ret = ret;
        this.data = data;
        this.ksn = ksn;
    }

}
