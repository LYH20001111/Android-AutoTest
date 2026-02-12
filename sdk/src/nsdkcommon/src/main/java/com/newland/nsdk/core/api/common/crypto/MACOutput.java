package com.newland.nsdk.core.api.common.crypto;


/**
 * The result of MAC generation.
 */
public class MACOutput {
    private byte[] data;
    private byte[] ksn;

    /**
     * Instantiates a new MAC output.
     *
     * @param data Generated MAC data.
     * @param ksn  KSN if the encryption key is DUKPT key.
     */
    public MACOutput(byte[] data, byte[] ksn) {
        this.data = data;
        this.ksn = ksn;
    }

    /**
     * Gets generated MAC data.
     *
     * @return Generated MAC data.
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Gets KSN if the encryption is using DUKPT key.
     *
     * @return KSN.
     */
    public byte[] getKsn() {
        return ksn;
    }
}
