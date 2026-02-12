package com.newland.forth.spi.crypto.cipher;

import com.newland.forth.spi.crypto.keystore.Key;

/**
 * The interface Mac generator spi.
 */
public interface MacGeneratorSpi {
    /**
     * Generate mac mac output.
     *
     * @param key                 the key
     * @param algorithmParameters the algorithm parameters
     * @param iv                  the iv
     * @param datain              the datain
     * @return the mac output
     */
    public MacOutput generateMac(Key key, AlgorithmParameters algorithmParameters,byte[] iv, byte[] datain);
}
