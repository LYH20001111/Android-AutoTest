package com.newland.nsdk.core.api.common.keymanager;

/**
 * Asymmetric key.
 */
public class AsymmetricKey extends Key {
    private AsymKeyType keyType;
    private AsymKeyUsage keyUsage;

    /**
     * Gets key type.
     *
     * @return Key type, see {@link AsymKeyType}
     */
    public AsymKeyType getKeyType() {
        return keyType;
    }

    /**
     * Sets key type.
     *
     * @param keyType Key type, see {@link AsymKeyType}
     */
    public void setKeyType(AsymKeyType keyType) {
        this.keyType = keyType;
    }

    /**
     * Gets key usage.
     *
     * @return Key usage. See {@link AsymKeyUsage}
     */
    public AsymKeyUsage getKeyUsage() {
        return keyUsage;
    }

    /**
     * Sets key usage.
     *
     * @param keyUsage Key usage. See {@link AsymKeyUsage}
     */
    public void setKeyUsage(AsymKeyUsage keyUsage) {
        this.keyUsage = keyUsage;
    }
}
