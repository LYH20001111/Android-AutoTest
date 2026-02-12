package com.newland.forth.spi.crypto.keystore;

import com.newland.forth.spi.crypto.cipher.AlgorithmParameters;

/**
 * The interface Key manager spi.
 */
public interface KeyManagerSpi {
    /**
     * Generate key int.
     *
     * @param method the method
     * @param SrcKey the src key
     * @param DstKey the dst key
     * @return the int
     */
    public int generateKey(KeyGenerateMethod method, Key SrcKey, Key DstKey);

    public int generateKey(KeyGenerateMethod method, AlgorithmParameters algorithmParameters, Key SrcKey, Key DstKey);

    public int generateKey(KeyGenerateMethod method, AlgorithmParameters algorithmParameters, Key SrcKey, Key DstKey, byte[] iv);

    /**
     * Delete key int.
     *
     * @param Key the key
     * @return the int
     */
    public int deleteKey(Key Key);

    /**
     * Get key info int.
     *
     * @param infoID the info id
     * @param Key    the key
     * @param data   the data
     * @return the int
     */
    public int getKeyInfo(KeyInfoID infoID, Key Key, byte[] data);

    /**
     * Set key owner int.
     *
     * @param keyOwner the key owner
     * @return the int
     */
    public int setKeyOwner(String keyOwner);

    /**
     * Get key owner int.
     *
     * @param keyOwner the key owner
     * @return the int
     */
    public int getKeyOwner(String keyOwner);
}