package com.newland.sdk.module.externalPin;

/**
 * @description: Extra mac parameters
 * <p>RFU-Reserved for Future Use</p>
 * @author: Lindan
 * @create: 2019/07/29
 */
public class MacExtParams {

    private int algorithmID;

    /**
     * Get the index of the specified algorithm(RFU).
     *
     * @return
     */
    public int getAlgorithmID() {
        return algorithmID;
    }

    /**
     * <p>Set the index of the specified algorithm(RFU).</p>
     *
     * @param algorithmID
     */
    public void setAlgorithmID(int algorithmID) {
        this.algorithmID = algorithmID;
    }
}
