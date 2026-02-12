package com.newland.sdk.module.pin;

/**
 * Created by youjf on 2019/7/26 11:15
 */
public class LoadWKExtParams {
    /**
     * cbc init data
     */
    private byte[] cbcInitData;

    /**
     *  get the cbc init data
     * @return
     */
    public byte[] getCbcInitData() {
        return cbcInitData;
    }

    /**
     * set the cbc init data
     * @param cbcInitData if cbcInitData is null,use ecb mode load;<p>
     *      		       if cbcInitData is 8 bytes, use cbc mode load<p>
     */
    public void setCbcInitData(byte[] cbcInitData) {
        this.cbcInitData = cbcInitData;
    }
}
