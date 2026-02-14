package com.newland.nsdk.core.api.common.keymanager;

import com.newland.nsdk.core.api.common.crypto.KCVMode;

/**
 * Symmetric key.
 */
public class SymmetricKey extends Key {
    private KeyType keyType;
    private KeyUsage keyUsage;
    private byte[] kcv;
    private KCVMode kcvMode;

    /**
     * Gets key type.
     *
     * @return Key type, see {@link KeyType}
     */
    public KeyType getKeyType() {
        return keyType;
    }

    /**
     * Sets key type.
     *
     * @param keyType Key type, see {@link KeyType}
     */
    public void setKeyType(KeyType keyType) {
        this.keyType = keyType;
    }

    /**
     * Gets key usage.
     *
     * @return Key usage. See {@link KeyUsage}
     */
    public KeyUsage getKeyUsage() {
        return keyUsage;
    }

    /**
     * Sets key usage.
     *
     * @param keyUsage Key usage. See {@link KeyUsage}
     */
    public void setKeyUsage(KeyUsage keyUsage) {
        this.keyUsage = keyUsage;
    }

    /**
     * Gets KCV mode.
     *
     * @return KCV mode. See {@link KCVMode}
     */
    public KCVMode getKCVMode() {
        return kcvMode;
    }

    /**
     * Sets KCV mode.
     *
     * @param kcvMode KCV mode. See {@link KCVMode}
     */
    public void setKCVMode(KCVMode kcvMode) {
        this.kcvMode = kcvMode;
    }

    /**
     * Gets KCV.
     *
     * @return KCV.
     */
    public byte[] getKCV() {
        return kcv;
    }

    /**
     * Sets KCV.
     *
     * @param kcv KCV.
     */
    public void setKCV(byte[] kcv) {
        this.kcv = kcv;
    }
}
