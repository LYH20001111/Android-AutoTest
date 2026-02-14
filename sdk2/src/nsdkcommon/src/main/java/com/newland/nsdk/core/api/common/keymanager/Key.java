package com.newland.nsdk.core.api.common.keymanager;

/**
 * Key.
 */
public abstract class Key {
    private int keyLen;
    private byte keyID;
    private byte[] keyData;

    /**
     * Gets key length.
     *
     * @return Actual length of the key.
     */
    public int getKeyLen() {
        return keyLen;
    }

    /**
     * Sets key length.
     *
     * @param keyLen Actual length of the key.
     */
    public void setKeyLen(int keyLen) {
        this.keyLen = keyLen;
    }

    /**
     * Gets key ID.
     *
     * @return Key ID. Value range:
     * <ul>
     *     <li>For KEK: [1-251]</li>
     *     <li>For others: [1-250]</li>
     * </ul>
     */
    public byte getKeyID() {
        return keyID;
    }

    /**
     * Sets key ID.
     *
     * @param keyID Key ID. Value range:
     * <ul>
     *     <li>For KEK: [1-251]</li>
     *     <li>For others: [1-250]</li>
     * </ul>
     */
    public void setKeyID(byte keyID) {
        this.keyID = keyID;
    }

    /**
     * Gets key data.
     *
     * @return Key data.
     */
    public byte[] getKeyData() {
        return keyData;
    }

    /**
     * Sets key data.
     *
     * @param keyData Key data.
     */
    public void setKeyData(byte[] keyData) {
        this.keyData = keyData;
    }
}
