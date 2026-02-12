package com.newland.sdk.module.pin;

/**
 * Created by youjf on 2019/7/25 17:09
 */
public class CipherExtParams {
    /**
     *  cbc initial value
     */
    private byte[] cbcInit;
    /**
     * the ciphertext data of working key
     */
    private byte[] workingKeyData;

    /**
     * get the cbc initial value
     * @return
     */
    public byte[] getCbcInit() {
        return cbcInit;
    }

    /**
     * set the cbc initial value
     * @param cbcInit
     */
    public void setCbcInit(byte[] cbcInit) {
        this.cbcInit = cbcInit;
    }

    /**
     * get the ciphertext of working key
     * @return
     */
    public byte[] getWorkingKeyData() {
        return workingKeyData;
    }

    /**
     * set the ciphertext of working key
     * @param workingKeyData
     */
    public void setWorkingKeyData(byte[] workingKeyData) {
        this.workingKeyData = workingKeyData;
    }
}
