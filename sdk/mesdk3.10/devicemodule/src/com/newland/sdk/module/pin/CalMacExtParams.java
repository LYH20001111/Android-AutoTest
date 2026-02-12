package com.newland.sdk.module.pin;

/**
 * Created by youjf on 2019/7/25 17:21
 */
public class CalMacExtParams {
    /**
     * the position of random number
     */
    private byte[] randomIndex;
    /**
     * the ciphertext data of working key
     */
    private byte[] workingKeyData;

    /**
     * get the position of random number
     * @return
     */
    public byte[] getRandomIndex() {
        return randomIndex;
    }

    /**
     * set the position of random number
     * @param randomIndex the param length is 2 bytes
     */
    public void setRandomIndex(byte[] randomIndex) {
        this.randomIndex = randomIndex;
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
