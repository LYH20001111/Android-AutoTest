package com.newland.sdk.module.externaliccard;

import com.newland.sdk.module.pin.AlgorithmMode;
import com.newland.sdk.module.pin.CipherMode;

/**
 * @author youjf
 * @description
 * @date 2020/12/31
 * @since V3.10.33
 */
public class TransmitExtParams {
    private int keyIndex;
    private CipherMode cipherMode;
    private AlgorithmMode algorithmMode;
    private byte[] cbcInv;

    /**
     * @return 0, 129-255
     */
    public int getKeyIndex() {
        return keyIndex;
    }

    /**
     * @param keyIndex 0:plain text; 129-255: transmit data is encryed by the key.
     */
    public void setKeyIndex(int keyIndex) {
        this.keyIndex = keyIndex;
    }

    /**
     *
     * @return AlgorithmMode.ECB or AlgorithmMode.CBC
     */
    public CipherMode getCipherMode() {
        return cipherMode;
    }

    /**
     * set Cipher Mode
     * @param cipherMode just support ECB or CBC
     */
    public void setCipherMode(CipherMode cipherMode) {
        this.cipherMode = cipherMode;
    }

    /**
     * @return AlgorithmMode.DES or AlgorithmMode.AES
     */
    public AlgorithmMode getAlgorithmMode() {
        return algorithmMode;
    }

    /**
     * @param algorithmMode  just support AlgorithmMode.DES or AlgorithmMode.AES
     */
    public void setAlgorithmMode(AlgorithmMode algorithmMode) {
        this.algorithmMode = algorithmMode;
    }

    /**
     * get CBC Initial value
     * @return
     */
    public byte[] getCbcInv() {
        return cbcInv;
    }

    /**
     * set CBC Initial value
     * @param cbcInv
     */
    public void setCbcInv(byte[] cbcInv) {
        this.cbcInv = cbcInv;
    }
}
