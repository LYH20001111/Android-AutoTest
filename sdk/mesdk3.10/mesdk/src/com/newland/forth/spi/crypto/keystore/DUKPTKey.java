package com.newland.forth.spi.crypto.keystore;

/**
 * The type Dukpt key.
 */
public class DUKPTKey extends SymmetricKey {
    private byte[] ksn;

    /**
     * Get ksn byte [ ].
     *
     * @return the byte [ ]
     */
    public byte[] getKsn() {
        return ksn;
    }

    /**
     * Sets ksn.
     *
     * @param ksn the ksn
     */
    public void setKsn(byte[] ksn) {
        this.ksn = ksn;
    }


    @Override
    public KEY_USE getKeyUsage() {
        return super.getKeyUsage();
    }

    @Override
    public void setKeyUsage(KEY_USE keyUsage) {
        super.setKeyUsage(keyUsage);
    }

    @Override
    public int getKeyLen() {
        return super.getKeyLen();
    }

    @Override
    public void setKeyLen(int keyLen) {
        super.setKeyLen(keyLen);
    }

    @Override
    public int getKeyId() {
        return super.getKeyId();
    }

    @Override
    public void setKeyId(int keyId) {
        super.setKeyId(keyId);
    }

    @Override
    public KeyType getKeyType() {
        return super.getKeyType();
    }

    @Override
    public void setKeyType(KeyType keyType) {
        super.setKeyType(keyType);
    }

    @Override
    public byte[] getCipertextKeyVal() {
        return super.getCipertextKeyVal();
    }

    @Override
    public void setCipertextKeyVal(byte[] cipertextKeyVal) {
        super.setCipertextKeyVal(cipertextKeyVal);
    }

    @Override
    public byte[] getPlaintextKeyVal() {
        return super.getPlaintextKeyVal();
    }

    @Override
    public void setPlaintextKeyVal(byte[] plaintextKeyVal) {
        super.setPlaintextKeyVal(plaintextKeyVal);
    }
}
