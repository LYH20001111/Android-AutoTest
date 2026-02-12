package com.newland.sdk.module.pin;

/**
 * @author youjf
 * @description
 * @date 2019/8/6
 * @since 3.10.01
 */
public class LoadDuktpExtParams {
    /**
     * Transmission key index (this field is should set when the loadMKMode
     *  is {@link LoadKeyMode#CUSTOM_ENCRYPT} and others is set -1.)
     */
    private int kekIndex = -1;

    public int getKekIndex() {
        return kekIndex;
    }

    public void setKekIndex(int kekIndex) {
        this.kekIndex = kekIndex;
    }
}
