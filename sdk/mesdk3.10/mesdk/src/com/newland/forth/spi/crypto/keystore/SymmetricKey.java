package com.newland.forth.spi.crypto.keystore;

import com.newland.forth.spi.crypto.cipher.KcvMode;

/**
 * The type Symmetric key.
 */
public class SymmetricKey extends Key {
    private byte[] cipertextKeyVal;
    private byte[] plaintextKeyVal;
    private byte[] kcv;
    private KcvMode KcvMode;

    public KcvMode getKcvMode() {
        return KcvMode;
    }

    public void setKcvMode(KcvMode kcvMode) {
        KcvMode = kcvMode;
    }

    public byte[] getKcv() {
        return kcv;
    }

    public void setKcv(byte[] kcv) {
        this.kcv = kcv;
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

    /**
     * Get cipertext key val byte [ ].
     *
     * @return the byte [ ]
     */
    public byte[] getCipertextKeyVal() {
        return cipertextKeyVal;
    }

    /**
     * Sets cipertext key val.
     *
     * @param cipertextKeyVal the cipertext key val
     */
    public void setCipertextKeyVal(byte[] cipertextKeyVal) {
        this.cipertextKeyVal = cipertextKeyVal;
    }

    /**
     * Get plaintext key val byte [ ].
     *
     * @return the byte [ ]
     */
    public byte[] getPlaintextKeyVal() {
        return plaintextKeyVal;
    }

    /**
     * Sets plaintext key val.
     *
     * @param plaintextKeyVal the plaintext key val
     */
    public void setPlaintextKeyVal(byte[] plaintextKeyVal) {
        this.plaintextKeyVal = plaintextKeyVal;
    }


}
