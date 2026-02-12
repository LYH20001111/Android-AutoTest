package com.newland.forth.spi.crypto.cipher;

import com.newland.forth.spi.crypto.keystore.Key;

/**
 * The interface Cipher spi.
 */
public interface CipherSpi {
    /**
     * Encrypt int.
     *
     * @param key                 the key
     * @param algorithmParameters the algorithm parameters
     * @param iv                  the iv
     * @param datain              the datain
     * @return the int
     */
    public CipherOutput encrypt(Key key, AlgorithmParameters algorithmParameters, byte[] iv, byte[] datain);

    /**
     * Decrypt int.
     *
     * @param key                 the key
     * @param algorithmParameters the algorithm parameters
     * @param iv                  the iv
     * @param datain              the datain
     * @return the int
     */
    public CipherOutput decrypt(Key key, AlgorithmParameters algorithmParameters, byte[] iv, byte[] datain);

}
