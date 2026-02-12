package com.newland.nsdk.core.api.common.keymanager;

/**
 * DUKPT key.
 */
public class DUKPTKey extends SymmetricKey {
    public DUKPTKey(){
        setKeyUsage(KeyUsage.DUKPT);
    }
    private byte[] ksn;

    /**
     * Gets KSN.
     *
     * @return KSN.
     */
    public byte[] getKSN() {
        return ksn;
    }

    /**
     * Sets KSN.
     *
     * @param ksn KSN.
     */
    public void setKSN(byte[] ksn) {
        this.ksn = ksn;
    }
}
