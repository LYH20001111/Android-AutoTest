package com.newland.sdk.module.pin;

/**
 * Created by youjf on 2019/7/26 10:44
 */
public class LoadMKExtParams {
    /**
     * Transmission key index (this field is should set when the loadMKMode
     * is {@link LoadKeyMode#CUSTOM_ENCRYPT} and others is set -1.)
     */
    private int kekIndex = -1;
    /**
     * The block cipher modes.
     */
    private CipherMode cipherMode;
    /**
     * the cbc init data<p>
     * if cbcInit is null,use ecb mode load<p>
     * LoadKeyMode is ENCRYPT_TMK or MASTER_KEY and use cbc mode load, cbcInit is 8 bytes.
     */
    private byte[] cbcInit;


    /**
     * get the transmission key index
     *
     * @return
     */
    public int getKekIndex() {
        return kekIndex;
    }

    /**
     * set the transmission key index<p>
     * (this field is should set when the loadMKMode is {@link LoadKeyMode#CUSTOM_ENCRYPT} <p>
     * and others is set -1 )
     *
     * @param kekIndex
     */
    public void setKekIndex(int kekIndex) {
        this.kekIndex = kekIndex;
    }

    /**
     * get the cbc init data
     *
     * @return
     */
    public byte[] getCbcInit() {
        return cbcInit;
    }

    /**
     * set the cbc init data
     *
     * @param cbcInit if cbcInit is null,use ecb mode load<p>
     *                if LoadKeyMode is ENCRYPT_TMK or MASTER_KEY and use cbc mode load, cbcInit is 8 bytes.
     */
    public void setCbcInit(byte[] cbcInit) {
        this.cbcInit = cbcInit;
    }

    /**
     * get encrypt/decrypt type
     *
     * @return
     */
    public CipherMode getCipherMode() {
        return cipherMode;
    }

    /**
     * set encrypt/decrypt type
     *
     * @param cipherMode it's need to set cbcInit data when the cipherMode is{@link CipherMode#CBC}
     */
    public void setCipherMode(CipherMode cipherMode) {
        this.cipherMode = cipherMode;
    }
}
