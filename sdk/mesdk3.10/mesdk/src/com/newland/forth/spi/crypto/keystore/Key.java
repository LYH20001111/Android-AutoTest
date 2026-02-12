package com.newland.forth.spi.crypto.keystore;

/**
 * The type Key.
 */
public abstract class Key {
    private KeyType keyType;
    private int keyLen;
    private int keyId;
    private KEY_USE keyUsage;

    /**
     * Gets key usage.
     *
     * @return the key usage
     */
    public KEY_USE getKeyUsage() {
        return keyUsage;
    }

    /**
     * Sets key usage.
     *
     * @param keyUsage the key usage
     */
    public void setKeyUsage(KEY_USE keyUsage) {
        this.keyUsage = keyUsage;
    }

    /**
     * Gets key len.
     *
     * @return the key len
     */
    public int getKeyLen() {
        return keyLen;
    }

    /**
     * Sets key len.
     *
     * @param keyLen the key len
     */
    public void setKeyLen(int keyLen) {
        this.keyLen = keyLen;
    }

    /**
     * Gets key id.
     *
     * @return the key id
     */
    public int getKeyId() {
        return keyId;
    }

    /**
     * Sets key id.
     *
     * @param keyId the key id
     */
    public void setKeyId(int keyId) {
        this.keyId = keyId;
    }

    /**
     * Gets key type.
     *
     * @return the key type
     */
    public KeyType getKeyType() {
        return keyType;
    }

    /**
     * Sets key type.
     *
     * @param keyType the key type
     */
    public void setKeyType(KeyType keyType) {
        this.keyType = keyType;
    }
}
